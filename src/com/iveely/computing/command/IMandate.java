package com.iveely.computing.command;

/**
 * The interface for command process.
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public interface IMandate {

    /**
     * process command.
     *
     * @param cmd cmd
     * @return process result.
     */
    public String processCmd(String cmd);

}
