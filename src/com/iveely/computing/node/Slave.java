package com.iveely.computing.node;

import com.iveely.computing.status.SystemConfig;
import com.iveely.computing.zookeeper.ZookeeperClient;
import com.iveely.framework.net.SyncServer;

import java.io.IOException;
import java.util.Date;
import java.util.logging.Level;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;

/**
 * Slave machine.
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class Slave implements Runnable {

    /**
     * Event server.
     */
    private final SyncServer server;

    /**
     * Heartbeat, send each minitue.
     */
    private final Heartbeat heartbeat;

    /**
     * Logger.
     */
    private final Logger logger = Logger.getLogger(Slave.class.getName());

    /**
     * Event processor.
     */
    private final SlaveProcessor processor;

    public Slave(String zkServer, int zkPort) throws IOException, KeeperException, InterruptedException, Exception {
        this.processor = new SlaveProcessor();
        this.server = new SyncServer(processor, SystemConfig.crSlavePort);
        initZookeeper(zkServer, zkPort, SystemConfig.crSlavePort);
        getMasterInfor();
        this.heartbeat = new Heartbeat();
        Communicator.getInstance();
    }

    @Override
    public void run() {

        // 1.Startup heartbeat.
        Thread heartThread = new Thread(heartbeat);
        heartThread.start();
        logger.info("heartbeat is started.");

        // 2.Startup event listen.
        logger.info("Startup event listen.");
        try {
            //Utils.sleep(20);
            server.start();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(Slave.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Init information to zookeeper.
     *
     * @param server
     * @param port
     * @param slavePort
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void initZookeeper(String server, int port, int slavePort)
            throws IOException, KeeperException, InterruptedException {

        // 1. Create zookeeper.
        SystemConfig.zkServer = server;
        SystemConfig.zkPort = port;

        // 2. Create root\master
        String slave = com.iveely.framework.net.Internet.getLocalIpAddress()
                + "," + slavePort;
        ZookeeperClient.getInstance().setNodeValue(SystemConfig.slaveRoot + "/" + com.iveely.framework.net.Internet.getLocalIpAddress() + "," + slavePort, new Date().toString());
        logger.info("slave information:" + slave);
    }

    /**
     * Get master information.
     */
    private void getMasterInfor() throws Exception {
        String connectPath = ZookeeperClient.getInstance().getNodeValue(SystemConfig.masterRoot);
        if (connectPath == null || connectPath.isEmpty()) {
            throw new Exception("When get master information, connection string can not null or empty.");
        }
        String[] infor = connectPath.split(",");
        SystemConfig.masterServer = infor[0];
    }
}
