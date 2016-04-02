package com.iveely.computing.api;

import com.iveely.computing.zookeeper.ZookeeperClient;
import java.util.HashMap;
import java.util.UUID;
import org.apache.log4j.Logger;

/**
 * Data Input.
 *
 * @author sea11510@mail.ustc.edu.cn
 * @date 2015-3-4 19:43:01
 */
public abstract class IInput {

    /**
     * Name of the data-input.
     */
    private final String name = this.getClass().getSimpleName() + "(" + UUID.randomUUID().toString() + ")";

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(IInput.class.getName());

    /**
     * Initialize.
     *
     * @param conf
     */
    public void start(HashMap<String, Object> conf) {
    }

    /**
     * Next data.
     *
     * @param channel
     */
    public abstract void nextTuple(StreamChannel channel);

    /**
     * Data to which output.
     *
     * @param channel
     */
    public abstract void toOutput(StreamChannel channel);

    /**
     * Get name of the task.
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * Prepare before execute.
     *
     * @param conf
     */
    public void end(HashMap<String, Object> conf) {

    }

    /**
     * Set public cache. Everyone can access it.
     *
     * @param key
     * @param value
     */
    public void setPublicCache(String key, String value) {
        ZookeeperClient.getInstance().setNodeValue("/cache/" + key, value);
    }

    /**
     * Get public cache. Everyone can get it.
     *
     * @param key
     * @return
     */
    public String getPublicCache(String key) {
        String value = ZookeeperClient.getInstance().getNodeValue("/cache/" + key);
        return value;
    }
}
