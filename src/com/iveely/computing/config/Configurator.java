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

    private Configurator() {
        Configurator instance = JSONUtil.fromFile(new File("conf/system.json"));
        if (instance != null) {
            this.master = instance.getMaster();
            this.slave = instance.getSlave();
            this.zookeeper = instance.getZookeeper();
        } else {
            this.master = new MasterConfig("127.0.0.1", 8000, 9000, "", "/iveely.computing/master");
            this.slave = new SlaveConfig(4000, 6000, 6, "/iveely.computing/slave");
            this.zookeeper = new ZookeeperConfig("127.0.0.1", 2181);
            JSONUtil.toFile(this, new File("conf/system.json"));
        }
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

    public static Configurator get() {
        return new Configurator();
    }
}
