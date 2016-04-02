package com.iveely.computing.api;

import java.util.HashMap;
import java.util.UUID;
import org.apache.log4j.Logger;

/**
 * Data ouput.
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public abstract class IOutput implements Cloneable {

    /**
     * Unique instance name.
     */
    private String name = this.getClass().getSimpleName() + "(" + UUID.randomUUID().toString() + ")";

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(IOutput.class.getName());

    /**
     * Prepare before execute.
     *
     * @param conf
     */
    public void start(HashMap<String, Object> conf) {

    }

    /**
     * Process recived tuple.
     *
     * @param tuple
     * @param channel
     */
    public abstract void execute(Tuple tuple, StreamChannel channel);

    /**
     * Data to which output.
     *
     * @param channel
     */
    public void toOutput(StreamChannel channel) {

    }

    /**
     * Prepare before execute.
     *
     * @param conf
     */
    public void end(HashMap<String, Object> conf) {

    }

    /**
     * Get name of the task.
     *
     * @return
     */
    public String getName() {
        return name;
    }
}
