package com.iveely.computing.config;

import com.iveely.framework.text.JSONUtil;
import java.io.File;

/**
 * Configurator.
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class Configurator {

    private MasterConfig master;

    private SlaveConfig slave;

    private ZookeeperConfig zookeeper;

    public Configurator() {
     
    }

    /**
     * @return the master
     */
    public MasterConfig getMaster() {
        return master;
    }

    /**
     * @param master the master to set
     */
    public void setMaster(MasterConfig master) {
        this.master = master;
    }

    /**
     * @return the slave
     */
    public SlaveConfig getSlave() {
        return slave;
    }

    /**
     * @param slave the slave to set
     */
    public void setSlave(SlaveConfig slave) {
        this.slave = slave;
    }

    /**
     * @return the zookeeper
     */
    public ZookeeperConfig getZookeeper() {
        return zookeeper;
    }

    /**
     * @param zookeeper the zookeeper to set
     */
    public void setZookeeper(ZookeeperConfig zookeeper) {
        this.zookeeper = zookeeper;
    }
}
