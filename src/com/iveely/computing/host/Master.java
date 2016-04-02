package com.iveely.computing.host;

import com.iveely.computing.status.SystemConfig;
import com.iveely.computing.ui.HostProvider;
import com.iveely.computing.zookeeper.ZookeeperClient;
import com.iveely.framework.net.SyncServer;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;

/**
 * Master.
 *
 * @author liufanping@iveely.com
 * @date 2014-10-18 14:06:47
 */
public class Master implements Runnable {

    /**
     * Event server from slave to master.
     */
	private final SyncServer server;

    /**
     * Event processor from slave to master.
     */
    private final MasterProcessor masterProcessor;

    /**
     * WebSocket provider for UI.
     */
    private final HostProvider uiProvider;

    /**
     * Check node is online.
     */
    private final NodeValidator validator;

    /**
     * Thread for UI
     */
    private Thread uiThread;

    private Thread validatorThread;

    /**
     * Logger.
     */
    private final Logger logger = Logger.getLogger(Master.class.getName());

    public Master(String zookeeperServer, int zookeeperPort, String uiPwd) throws IOException, KeeperException, InterruptedException {
        this.validator = new NodeValidator();
        this.masterProcessor = new MasterProcessor(this.validator);
        this.uiProvider = new HostProvider(masterProcessor, uiPwd);
        this.server = new SyncServer(masterProcessor, SystemConfig.masterPort);
        SystemConfig.zkServer = zookeeperServer;
        SystemConfig.zkPort = zookeeperPort;
        SystemConfig.masterServer = com.iveely.framework.net.Internet.getLocalIpAddress();
        initZookeeper(zookeeperServer, zookeeperPort, SystemConfig.masterPort);
        initFolder();
        initUIService();
        initValidatorService();
    }

    /**
     * Start master.
     */
    @Override
    public void run() {
        try {
            server.start();
        } catch (Exception ex) {
            logger.error(ex);
        }
    }

    /**
     * Init zookeeper.
     *
     * @param server
     * @param port
     * @param masterPort
     * @throws IOException
     * @throws KeeperException
     * @throws InterruptedException
     */
    private void initZookeeper(String server, int port, int masterPort)
            throws IOException, KeeperException, InterruptedException {

        // 1. Create zookeeper.
        SystemConfig.zkServer = server;
        SystemConfig.zkPort = port;

        // 2. Create root\master
        String master = com.iveely.framework.net.Internet.getLocalIpAddress()
                + "," + masterPort;
        ZookeeperClient.getInstance().deleteNode("/iveely");
        ZookeeperClient.getInstance().setNodeValue(SystemConfig.masterRoot, master);
        ZookeeperClient.getInstance().setNodeValue(SystemConfig.masterRoot + "/setup", new Date().toString());
        logger.info("master information:" + master);
    }

    private void initFolder() {
        File file = new File(SystemConfig.appFoler);
        if (!file.exists()) {
            if (!file.mkdir()) {
                logger.error(SystemConfig.appFoler + " not created on master.");
            }
        }
    }

    /**
     * UI Service.
     */
    private void initUIService() {
        uiThread = new Thread(this.uiProvider);
        uiThread.start();
    }

    /**
     * Validator Service.
     */
    private void initValidatorService() {
        validatorThread = new Thread(this.validator);
        validatorThread.start();
    }
}
