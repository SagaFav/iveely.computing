/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveely.computing;

import com.iveely.computing.config.Configurator;
import com.iveely.computing.host.Master;
import com.iveely.computing.node.Slave;
import com.iveely.computing.status.SystemConfig;
import com.iveely.computing.user.Console;
import com.iveely.computing.supervisor.Monitor;
import com.iveely.computing.supervisor.WorkerChecker;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.zookeeper.KeeperException;

/**
 * Iveely computing entrance.
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class Program {

    /**
     * Logger.
     */
    private static final Logger logger = Logger.getLogger(Program.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (args != null && args.length == 1) {
            Configurator configurator = Configurator.get();
            String type = args[0].trim();
            switch (args[0].toLowerCase()) {
                case "master":
                    try {
                        Master master = new Master(configurator.getZookeeper().getAddress(), configurator.getZookeeper().getPort(), configurator.getMaster().getPassword());
                        master.run();
                    } catch (IOException | KeeperException | InterruptedException e) {
                        logger.error(e);
                    }
                    logger.info(String.format("Master started on port: %d", Configurator.get().getMaster().getPort()));
                    break;
                case "slave":
                    try {
                        Slave slave = new Slave(configurator.getZookeeper().getAddress(), configurator.getZookeeper().getPort());
                        slave.run();
                        //logger.info("Slave started, master is " + SystemConfig.masterServer + ":" + SystemConfig.masterPort);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                    break;
                case "supervisor":
                    try {
                        Monitor monitor = new Monitor(configurator.getMaster().getAddress(), configurator.getMaster().getPort());
                        monitor.run();
                        //logger.info("monitor started, master is " + SystemConfig.masterServer + ":" + SystemConfig.masterPort);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                    break;
                case "console":
                    try {
                        Console console = new Console();
                        console.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error(e);
                    }
                    break;

            }
        }
        logger.error("arguments error,example [master | slave | slave | console]");
    }
}
