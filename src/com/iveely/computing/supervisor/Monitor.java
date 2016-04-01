package com.iveely.computing.supervisor;

import com.iveely.computing.common.Computer;
import com.iveely.computing.common.ProcessBuilder;
import com.iveely.computing.common.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Administrator
 */
public class Monitor implements Runnable {

    private class WorkerStatus implements WorkerChecker.Feedback {

        private Integer key;

        /**
        * 
        */
        public WorkerStatus(Integer key) {
            this.key = key;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.iveely.computing.supervisor.WorkerChecker.Feedback#success()
         */
        @Override
        public void success() {
            logger.info("Port:" + key + " is online...");

        }

        /*
         * (non-Javadoc)
         * 
         * @see com.iveely.computing.supervisor.WorkerChecker.Feedback#failur()
         */
        @Override
        public void failur() {
            logger.info("Port:" + key + " is offline...");

        }

    }

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(Monitor.class.getName());

    private HashMap<Integer, Process> list;

    private int basePort;

    private String masterIP;

    private int masterPort;

    public Monitor(String mip, int port) {
        list = new HashMap<>();
        basePort = 4000;
        this.masterIP = mip;
        this.masterPort = port;
    }

    @Override
    public void run() {
        // 1. Check size to make sure how many workers.
        int memSize = Computer.getFreeMemory();
        int count = (memSize - 1024) / 1024;
        if (count < 1) {
            count = 1;
        }
        String jarPath = "Iveely.Computing.jar"; // Computer.getCurrentJar();
        logger.info("Assigned amount of slave:" + count);
        logger.info("Jar path:" + jarPath);
        for (int i = 0; i < count; i++) {
            int port = basePort + i;
            int slotPort = basePort + 100 * (i + 1) + i;
            String pars = this.masterIP + " " + this.masterPort + " " + port + " " + slotPort;
            logger.info("Slave with port:" + port + ", arguments:" + pars + " is prepare to start.");
            Process p = ProcessBuilder.start(jarPath, pars);
            if (p != null) {
                list.put(basePort + i, p);
                logger.info("Port:" + port + " is starting...");
            } else {
                logger.error("Port:" + port + ",worker not started yet.");
            }
            Utils.sleep(2);
        }
        Utils.sleep(5);

        // 2. Check is ready setup.
        Iterator iter = list.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            int key = (int) entry.getKey();
            logger.info("Port:" + key + " is in checking...");

            new WorkerChecker(new WorkerStatus(key)).isOnline(key);

        }

        // 3. Monitor all slaves.
        while (true) {
            Iterator iter2 = list.entrySet().iterator();
            int i = 0;
            while (iter2.hasNext()) {
                Map.Entry entry = (Map.Entry) iter2.next();
                int key = (int) entry.getKey();
                Process val = (Process) entry.getValue();
                if (!val.isAlive()) {
                    logger.error("Port:" + key + ", has died.");
                    int slotPort = key + 100 * (i + 1);
                    val = ProcessBuilder.start(jarPath,
                            " slave " + this.masterIP + " " + this.masterPort + " " + key + " " + slotPort);
                    if (val != null) {
                        entry.setValue(val);
                        logger.error("Port:" + key + ", has restarted.");
                    } else {
                        logger.error("Port:" + key + ", can not restarted.");
                    }
                }
            }
            Utils.sleep(5);
        }

    }
}
