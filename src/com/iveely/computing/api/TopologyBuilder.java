package com.iveely.computing.api;

import com.iveely.computing.status.SystemConfig;
import com.iveely.computing.zookeeper.ZookeeperClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Topology Builder.
 *
 * @author sea11510@mail.ustc.edu.cn
 * @date 2015-3-4 20:03:59
 */
public class TopologyBuilder {

    public enum ExecuteType {

        LOCAL, CLUSTER
    }

    /**
     * Local(false) or cluster.
     */
    public boolean isLocalMode;

    /**
     * Input as assigned to a node as possible.
     */
    private boolean inputFocusOn;

    /**
     * Total count of worker.
     */
    private int workerCount;

    /**
     * Total count of slave.
     */
    private int slaveCount;

    /**
     * User config.
     */
    private final HashMap<String, Object> userConfig;

    /**
     * Output & Input counter.
     */
    private final HashMap<String, Integer> counter;

    /**
     * Name of the topology.
     */
    private final String name;

    /**
     * Input.
     */
    private final List<String> inputs;

    /**
     * Output.
     */
    private final List<String> outputs;

    public TopologyBuilder(ExecuteType type, String topologyClass, String name) {
        this.userConfig = new HashMap<>();
        this.workerCount = 0;
        this.counter = new HashMap<>();
        this.name = name;
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
        if (type == ExecuteType.CLUSTER) {
            ZookeeperClient.getInstance().setNodeValue("/iveely/app/mapper/" + name, topologyClass.replace("$", "."));
        }
        this.isLocalMode = (type == ExecuteType.LOCAL);
        this.inputFocusOn = false;
    }

    public void setInputFocusOn() {
        this.inputFocusOn = true;
    }

    /**
     * Get topology name.
     *
     * @return
     */
    public String getName() {
        return this.name;
    }

    /**
     * Set count of slave if need.
     *
     * @param count
     */
    public void setSlave(int count) {
        this.slaveCount = count;
    }

    /**
     * Return count of slave.
     *
     * @return
     */
    public int getSlaveCount() {
        int allSlaveCount = ZookeeperClient.getInstance().getChildren(SystemConfig.slaveRoot).size();
        if (this.slaveCount < 1 || this.slaveCount > allSlaveCount) {
            this.slaveCount = allSlaveCount / 2 + 1;
        }
        return this.slaveCount;
    }

    /**
     * Get config of user.
     *
     * @return
     */
    public HashMap<String, Object> getUserConfig() {
        return userConfig;
    }

    /**
     * Set input workers.
     *
     * @param input
     * @param workerCount
     */
    public void setInput(Class<? extends IInput> input, int workerCount) throws InstantiationException, IllegalAccessException {
        List<IInput> list = new ArrayList<>();
        for (int i = 0; i < workerCount; i++) {
            list.add(input.newInstance());
            this.inputs.add(input.getName());
        }
        this.workerCount += workerCount;
        if (SystemConfig.inputs.containsKey(this.name)) {
            List<IInput> temp = SystemConfig.inputs.get(this.name);
            temp.addAll(list);
            SystemConfig.inputs.put(this.name, temp);
        } else {
            SystemConfig.inputs.put(this.name, list);
        }
    }

    /**
     * Set output workers.
     *
     * @param output
     * @param workerCount
     */
    public void setOutput(Class<? extends IOutput> output, int workerCount) throws InstantiationException, IllegalAccessException {
        List<IOutput> list = new ArrayList<>();
        for (int i = 0; i < workerCount; i++) {
            list.add(output.newInstance());
            this.outputs.add(output.getName());
        }
        this.workerCount += workerCount;
        if (SystemConfig.outputs.containsKey(this.name)) {
            List<IOutput> temp = SystemConfig.outputs.get(this.name);
            temp.addAll(list);
            SystemConfig.outputs.put(this.name, temp);
        } else {
            SystemConfig.outputs.put(this.name, list);
        }
    }

    /**
     * Config information.
     *
     * @param key
     * @param val
     */
    public void put(String key, Object val) {
        userConfig.put(key, val);
    }

    /**
     * Get total count of workers.
     *
     * @return
     */
    public int getTotalWorkerCount() {
        return this.workerCount;
    }

    /**
     * Get all puts (input & output)
     *
     * @return
     */
    public List<String> getAllputs() {
        List<String> allputs = new ArrayList<>();
        if (!inputFocusOn) {
            int inputSize = inputs.size();
            int outputSize = outputs.size();
            if (inputSize <= outputSize) {
                int k = outputSize / inputSize;
                int j = 0;
                for (int i = 0; i < outputSize; i++) {
                    if (i % k == 0 && i != 0 && j < inputSize) {
                        allputs.add(inputs.get(j));
                        j++;
                    }
                    allputs.add(outputs.get(i));
                }
                for (int i = j; i < inputSize; i++) {
                    allputs.add(inputs.get(i));
                }
            } else {
                int k = inputSize / outputSize;
                int j = 0;
                for (int i = 0; i < inputSize; i++) {
                    if (i % k == 0 && i != 0 && j < outputSize) {
                        allputs.add(outputs.get(j));
                        j++;
                    }
                    allputs.add(inputs.get(i));
                }
                for (int i = j; j < outputSize; j++) {
                    allputs.add(outputs.get(i));
                }
            }
            return allputs;
        } else {
            for (String ip : inputs) {
                allputs.add(ip);
            }
            for (String op : outputs) {
                allputs.add(op);
            }
            return allputs;
        }
    }
}
