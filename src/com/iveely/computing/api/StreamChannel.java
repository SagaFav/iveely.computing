package com.iveely.computing.api;

import com.iveely.computing.common.IStreamCallback;
import com.iveely.computing.common.Message;
import com.iveely.computing.common.StreamPacket;
import com.iveely.computing.common.StreamType;
import com.iveely.computing.node.Communicator;
import com.iveely.computing.zookeeper.ZookeeperClient;
import com.iveely.framework.net.Packet;
import com.iveely.framework.net.SyncClient;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Stream Channel.
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class StreamChannel extends IStreamCallback {

    /**
     * Has finish end of output data.
     */
    private boolean hasEnd;

    /**
     * Flag of data spliter.
     */
    private final String dataSpliter = "\t";

    /**
     * Output executor.
     */
    private final IExecutor executor;

    /**
     * For next output executor.
     */
    private final List<IOutput> outputs;

    /**
     * For next output clients.
     */
    private final TreeMap<String, List<SyncClient>> outputClients;

    /**
     * For next output output.
     */
    private final TreeMap<String, List<String>> outputGuids;

    /**
     * Receiver which work on.
     */
    private Communicator.Slot slot;

    /**
     * Emit count.
     */
    private int emitCount;

    /**
     * Time of start.
     */
    private final long start;

    /**
     * Name of the input.
     */
    private final String inputName;

    /**
     * Name of the topology.
     */
    private final String name;

    public StreamChannel(String inputName, String name, IExecutor executor) {
        this.hasEnd = false;
        this.executor = executor;
        this.outputs = new ArrayList<>();
        this.outputClients = new TreeMap<>();
        this.outputGuids = new TreeMap<>();
        this.name = name;
        this.emitCount = 0;
        this.inputName = inputName;
        this.start = new Date().getTime();
    }

    /**
     * Emit data to output.
     *
     * @param objs
     */
    public void emit(Object key, Object value) {
        if (emitCount % 100 == 0) {
            long diff = new Date().getTime() - start;
            double hour = diff * 1.0 / (1000 * 60 * 60);
            ZookeeperClient.getInstance()
                    .setNodeValue("/app/" + this.name + "/statistic/input/" + this.inputName + "/emit", emitCount + "");
            ZookeeperClient.getInstance().setNodeValue(
                    "/app/" + this.name + "/statistic/input/" + this.inputName + "/emit_avg",
                    emitCount / hour + "(emit)/h");
        }
        emitCount++;
        for (Iterator<Entry<String, List<SyncClient>>> it = this.outputClients.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = it.next();
            List<SyncClient> list = (List<SyncClient>) entry.getValue();
            String buffer = key + dataSpliter + value;
            int code = key.hashCode() % list.size();
            if (code < 0) {
                code = code * -1;
            }
            Packet packet = new Packet();
            StreamPacket streamPacket = new StreamPacket();
            streamPacket.setData(Message.getBytes(buffer));
            streamPacket.setType(StreamType.DATASENDING);
            streamPacket.setGuid(this.outputGuids.get(entry.getKey()).get(code));
            streamPacket.setName(this.name);
            packet.setData(streamPacket.toBytes());
            list.get(code).send(packet);
        }
    }

    /**
     * Told IOutput, IInput emit end.
     */
    public void emitEnd() {
        hasEnd = true;
        ZookeeperClient.getInstance().setNodeValue("/app/" + this.name + "/statistic/input/" + this.inputName + "/emit",
                emitCount + "");
        for (Iterator<Entry<String, List<SyncClient>>> it = this.outputClients.entrySet().iterator(); it.hasNext();) {
            Map.Entry entry = it.next();
            String key = entry.getKey().toString();
            List<SyncClient> list = (List<SyncClient>) entry.getValue();
            for (int i = 0; i < list.size(); i++) {
                Packet packet = new Packet();
                StreamPacket streamPacket = new StreamPacket();
                streamPacket.setType(StreamType.END);
                streamPacket.setGuid(this.outputGuids.get(key).get(i));
                streamPacket.setName(this.name);
                packet.setData(streamPacket.toBytes());
                list.get(i).send(packet);
                // list.get(i).close();
            }
        }

        ZookeeperClient.getInstance().setNodeValue("/app/" + this.name + "/finished/" + this.inputName, "Emit end.");
    }

    public boolean hasEnd() {
        return hasEnd;
    }

    public void addOutputTo(IOutput output) {
        String rootPath = "/app/" + executor.getName() + "/output/" + output.getClass().getName();
        int retryCount = 5;
        List<String> children;
        List<SyncClient> clients = new ArrayList<>();
        List<String> guids = new ArrayList<>();
        do {
            children = ZookeeperClient.getInstance().getChildren(rootPath);
            for (String cd : children) {
                String path = rootPath + "/" + cd;
                String guid = cd;
                String value = ZookeeperClient.getInstance().getNodeValue(path);
                if (value != null && !value.isEmpty()) {
                    String[] infor = value.split(",");
                    SyncClient client = new SyncClient(infor[0], Integer.parseInt(infor[1]));
                    clients.add(client);
                    guids.add(guid);
                }
            }
            if (children.isEmpty()) {
                try {
                    retryCount--;
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(StreamChannel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } while (children.isEmpty() && retryCount > -1);
        String key = output.getClass().getName();
        if (!this.outputClients.containsKey(key)) {
            this.outputClients.put(key, clients);
            this.outputGuids.put(key, guids);
        }
    }

    @Override
    public void invoke(StreamPacket packet) {

        OutputExecutor outputExecutor = (OutputExecutor) executor;
        int streamType = packet.getType().ordinal();
        if (streamType == StreamType.DATASENDING.ordinal()) {
            byte[] data = packet.getData();
            String fdata = Message.getString(data);
            String[] array = fdata.split(dataSpliter);
            List<Object> list = new ArrayList<>();
            for (String ele : array) {
                list.add(ele);
            }
            Tuple tuple = new Tuple(list);
            outputExecutor.invoke(tuple);
        } else if (streamType == StreamType.END.ordinal()) {
            outputExecutor.end();
        }
    }
}
