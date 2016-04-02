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
//        testMaster();
        // testSlave();
//        SystemConfig.zkServer = "127.0.0.1";
//        SystemConfig.zkPort = 2181;
//        System.out.println(ZookeeperClient.getInstance().getNodeValue("/app/WordCount/finished"));
        //testWebSocket();
        // testConsole();
        // testSubmit();
        //testJarExecutor();
        //  args = "master 127.0.0.1 2181".split(" ");
        processArgs(args);
        //  onlinetest();
    }

    private static void testMaster() {
        String[] argsStrings = new String[]{"master", "127.0.0.1", "2181", "123456"};
        processArgs(argsStrings);
    }

    private static void testSlave() {
        String[] argsStrings = new String[]{"slave", "127.0.0.1", "2181", "4000", "4100"};
        processArgs(argsStrings);
    }

    private static void onlinetest() {
        if (WorkerChecker.isOnline(4000)) {
            System.out.println("online");
        } else {
            System.out.println("offline");
        }
    }

    public static void processArgs(String[] args) {
        if (args.length > 2) {
            String ip = args[1].trim();
            Integer port = Integer.parseInt(args[2].trim());
            if (port > 0 && port < 65535) {

                // Update confige information
                if (null != args[0].toLowerCase()) {
                    switch (args[0].toLowerCase()) {
                        case "master":
                            // Cmd for master
                            try {
                                String password = args[3];
                                Master master = new Master(ip, port, password);
                                master.run();
                            } catch (IOException | KeeperException | InterruptedException e) {
                                logger.error(e);
                            }

                            logger.info("Master started on port:" + Configurator.getMasterPort());
                            break;
                        case "slave":
                            try {
                                // Cmd for slave
                                //Attribute.getInstance().setFolder(SystemConfig.appFoler);
                                Integer slavePort = Integer.parseInt(args[3].trim());
                                SystemConfig.crSlavePort = slavePort;
                                Integer slotPort = Integer.parseInt(args[4].trim());
                                SystemConfig.slotBasePort = slotPort;
                                Slave slave = new Slave(ip, port);
                                slave.run();
                                logger.info("Slave started, master is " + SystemConfig.masterServer + ":" + SystemConfig.masterPort);
                            } catch (Exception e) {
                                logger.error(e);
                            }
                            break;
                        case "supervisor":
                            try {
                                // Cmd for supervisor
                                Monitor monitor = new Monitor(ip, port);
                                monitor.run();
                                logger.info("monitor started, master is " + SystemConfig.masterServer + ":" + SystemConfig.masterPort);
                            } catch (Exception e) {
                                logger.error(e);
                            }
                            break;
                        default:
                            // Cmd for console
                            try {
                                Console console = new Console(ip, port);
                                console.run();
                            } catch (Exception e) {
                                e.printStackTrace();
                                logger.error(e);
                            }

                            break;
                    }
                }

            } else {
                argsInvaid();
            }

        } else {
            argsError();
        }
    }

    /**
     * Arguments error.
     */
    private static void argsError() {
        logger.error("arguments error,example [master zkServer zkPort] or [slave zkServer zkPort]");
    }

    /**
     * Arguments invaid.
     */
    private static void argsInvaid() {
        logger.error("arguments invaid, 0<port<65536.");
    }
}
