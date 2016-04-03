package com.iveely.computing.ui;

/**
 * Request.
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class Request {

    /**
     * User command.
     */
    private String command;

    /**
     * @return the command
     */
    public String getCommand() {
        return command;
    }

    /**
     * @param command the command to set
     */
    public void setCommand(String command) {
        this.command = command;
    }

    /**
     * Name of the topology.
     */
    private String topology;

    /**
     * @return the topology
     */
    public String getTopology() {
        return topology;
    }

    /**
     * @param topology the topology to set
     */
    public void setTopology(String topology) {
        this.topology = topology;
    }
}
