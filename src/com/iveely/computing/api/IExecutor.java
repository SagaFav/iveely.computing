package com.iveely.computing.api;

import java.util.HashMap;

/**
 * Executor.
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class IExecutor {

    /**
     * Config.
     */
    protected HashMap<String, Object> _conf;

    /**
     * Output of the collector.
     */
    protected StreamChannel _collector;

    /**
     * Name of the topology.
     */
    protected String _name;

    /**
     * Get name of the toplogy.
     *
     * @return
     */
    public String getName() {
        return this._name;
    }
}
