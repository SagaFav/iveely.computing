package com.iveely.computing.api;

import com.iveely.computing.common.Message;
import com.iveely.computing.host.Luggage;
import com.iveely.computing.host.NodeTopology;
import com.iveely.computing.status.SystemConfig;
import com.iveely.computing.zookeeper.ZookeeperClient;
import com.iveely.framework.net.Packet;
import com.iveely.framework.net.SyncClient;
import com.iveely.framework.text.Convertor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.log4j.Logger;

/**
 * Topology Submitter.
 *
 * @author sea11510@mail.ustc.edu.cn
 * @date 2015-3-4 20:17:50
 */
public class TopologySubmitter {

    /**
     * Thread pool.
     */
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(SystemConfig.maxWorkerCount);

    /**
     * All topology.
     */
    private static final TreeMap<String, TopologyBuilder> topologies = new TreeMap<>();

    /**
     * Input executor.
     */
    private static final TreeMap<String, List<InputExecutor>> inputExecutors = new TreeMap<>();

    /**
     * Logger
     */
    private final static Logger logger = Logger.getLogger(TopologySubmitter.class.getName());

    /**
     * Submit topology to Iveely Computing.
     *
     * @param builder
     * @param args
     */
    public static void submit(TopologyBuilder builder, String[] args) {
        // 1.  If local mode.
        if (builder.isLocalMode) {
            LocalCluster local = new LocalCluster();
            local.submit(builder);
            return;
        }

        // 2. If master run to check.
        if (args.length == 2) {

            logger.info("Master start to running toplogy:" + builder.getName());
            if (topologies.containsKey(builder.getName())) {
                ZookeeperClient.getInstance().deleteNode("/app/" + builder.getName());
                logger.warn("Topology is already exist.");
            }

            // 2.1 Make sure config.
            String jarPath = args[0];
            String topology = args[1];
            int specifySlaveCount = builder.getSlaveCount();
            int workerCount = builder.getTotalWorkerCount();
            int total = workerCount;

            // 2.2 Get best slaves.
            logger.info("Master start to get best slaves:" + builder.getName());
            logger.info("1. Specify slave count:" + specifySlaveCount);
            logger.info("2. Specify worker count:" + workerCount);
            List<String> slaves = new ArrayList<>();
            List<Integer> runningApps = new ArrayList<>();
            for (int i = 0; i < specifySlaveCount && i < Luggage.performanceSlaves.size(); i++) {
                String slave = Luggage.performanceSlaves.get(i);
                int cnt = Luggage.slaves.get(slave);
                slaves.add(slave);
                workerCount += cnt;
                runningApps.add(cnt);
            }

            // 2.3 worker distribution.
            int left = total % slaves.size();
            int avg = total / slaves.size();
            logger.info("3. Avg workers' count on each slave:" + avg);
            logger.info("4. left workers' count:" + left);
            List<Integer> distri = new ArrayList<>();
            slaves.stream().forEach((_item) -> {
                distri.add(avg);
            });
            for (int i = 0; i < left; i++) {
                distri.set(i, distri.get(i) + 1);
            }

            // 2.4 Configuration parameters.
            List<String> parameters = new ArrayList<>();
            List<String> allputs = builder.getAllputs();
            int lastIndex = 0;
            int hasDistr = 0;
            for (int cnt : distri) {
                StringBuffer par = new StringBuffer();
                for (int i = lastIndex; i < hasDistr + cnt; i++) {
                    par.append(allputs.get(i));
                    par.append(" ");
                }
                lastIndex += cnt;
                hasDistr += cnt;
                parameters.add(par.toString());
            }

            // 2.5 Send to slaves.
            byte[] content = null;
            try {
                ByteArrayOutputStream out;
                try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(jarPath))) {
                    out = new ByteArrayOutputStream(inputStream.available());
                    byte[] temp = new byte[inputStream.available()];
                    int size = 0;
                    while ((size = inputStream.read(temp)) != -1) {
                        out.write(temp, 0, size);
                    }
                }
                content = out.toByteArray();
            } catch (Exception e) {
                logger.error(e);
            }
            if (content != null) {
                for (int i = 0; i < slaves.size(); i++) {
                    try {
                        String[] infor = slaves.get(i).split(",");
                        String header = topology + ":" + parameters.get(i);
                        logger.info("5." + i + "(" + slaves.get(i) + "):" + parameters.get(i));
                        NodeTopology.getInstance().set(slaves.get(i), builder.getName());

                        // 3. Build packet.
                        Packet packet = new Packet();
                        packet.setExecutType(Message.ExecuteType.UPLOAD.ordinal());
                        packet.setMimeType(Message.MIMEType.APP.ordinal());
                        byte[] appName = Message.getBytes(header);
						byte[] appNameSize = Convertor.int2byte(appName.length, 4);
                        byte[] data = new byte[4 + appName.length + content.length];
                        System.arraycopy(appNameSize, 0, data, 0, 4);
                        System.arraycopy(appName, 0, data, 4, appName.length);
                        System.arraycopy(content, 0, data, 4 + appName.length, content.length);
                        packet.setData(data);
						SyncClient client = new SyncClient(infor[0], Integer.parseInt(infor[1]));
                        client.send(packet);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }
                ZookeeperClient.getInstance().setNodeValue("/app/" + builder.getName() + "/finished", builder.getAllputs().size() + "");
                ZookeeperClient.getInstance().setNodeValue("/app/" + builder.getName() + "/slavecount", builder.getSlaveCount() + "");
                topologies.put(builder.getName(), builder);

            } else {
                logger.error("Content cannot be null.");
            }
            return;
        }

        // 3. If slave run.
        if (args.length == 1) {
            List<String> puts = Arrays.asList(args[0].split(" "));
            HashMap<String, Object> userConfig = builder.getUserConfig();
            List<IInput> inputConfig = SystemConfig.inputs.get(builder.getName());
            List<IOutput> outputConfig = SystemConfig.outputs.get(builder.getName());

            // 3. Try to run output.
            for (int i = outputConfig.size() - 1; i > -1; i--) {
                IOutput output = outputConfig.get(i);
                String name = output.getClass().getName();
                for (int j = 0; j < puts.size(); j++) {
                    if (puts.get(j).equals(name)) {
                        puts.set(j, "empty");
                        OutputExecutor executor = new OutputExecutor(builder.getName(), output, userConfig);
                        Thread thread = new Thread(executor);
                        thread.start();
                        break;
                    }
                }
            }

            // 4. Try to run input.
            List<InputExecutor> exs = new ArrayList<>();
            for (int i = inputConfig.size() - 1; i > -1; i--) {
                IInput input = inputConfig.get(i);
                String name = input.getClass().getName();
                for (int j = 0; j < puts.size(); j++) {
                    if (puts.get(j).equals(name)) {
                        puts.set(j, "empty");
                        InputExecutor executor = new InputExecutor(builder.getName(), input, userConfig);
                        exs.add(executor);
                        Thread thread = new Thread(executor);
                        thread.start();
                        break;
                    }
                }
            }
            inputExecutors.put(builder.getName(), exs);
            topologies.put(builder.getName(), builder);
        }
    }

    public static String stop(String tpName) {
        if (inputExecutors.containsKey(tpName)) {
            List<InputExecutor> list = inputExecutors.get(tpName);
            list.stream().forEach((executor) -> {
                executor.stop();
            });
            return "In stopping.";
        } else {
            return "Not found topology:" + tpName;
        }
    }
}
