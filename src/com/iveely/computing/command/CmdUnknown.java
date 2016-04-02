package com.iveely.computing.command;

/**
 * Unknown command.
 *
 * @author sea11510@mail.ustc.edu.cn
 * @date 2014-10-18 14:15:38
 */
public class CmdUnknown implements IMandate {

    @Override
    public String processCmd(String cmd) {
        return "Unknow mandate:" + cmd;
    }
}
