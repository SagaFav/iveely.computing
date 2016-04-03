package com.iveely.computing.node;

import com.iveely.computing.common.Message;
import com.iveely.computing.common.Utils;
import com.iveely.computing.status.SystemConfig;
import com.iveely.framework.net.Packet;
import com.iveely.framework.net.SyncClient;
import com.iveely.framework.text.JsonUtil;

import org.apache.log4j.Logger;

/**
 * Heartbeat
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class Heartbeat implements Runnable {

    public class HearbeatInfo {

        private String ipaddress;

        private int port;

        private int usedSlot;

        public HearbeatInfo() {
            this.ipaddress = SystemConfig.crSlaveAddress;
            this.port = SystemConfig.crSlavePort;
            this.usedSlot = Communicator.getInstance().getUsedSlotCount();
        }

        /**
         * @return the ipaddress
         */
        public String getIpaddress() {
            return ipaddress;
        }

        /**
         * @param ipaddress the ipaddress to set
         */
        public void setIpaddress(String ipaddress) {
            this.ipaddress = ipaddress;
        }

        /**
         * @return the port
         */
        public int getPort() {
            return port;
        }

        /**
         * @param port the port to set
         */
        public void setPort(int port) {
            this.port = port;
        }

        /**
         * @return the freeSlot
         */
        public int getUsedSlot() {
            return usedSlot;
        }

        /**
         * @param usedSlot the freeSlot to set
         */
        public void setUsedSlot(int usedSlot) {
            this.usedSlot = usedSlot;
        }

        @Override
        public String toString() {
            return JsonUtil.beanToJson(this);
        }
    }

    /**
     * Message client.
     */
    private final SyncClient client;

    /**
     * Logger.
     */
    private final Logger logger = Logger.getLogger(Heartbeat.class.getName());

    public Heartbeat() {
        SystemConfig.crSlaveAddress = com.iveely.framework.net.Internet.getLocalIpAddress();
        client = new SyncClient(SystemConfig.masterServer, SystemConfig.masterPort);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Packet packet = new Packet();
                packet.setExecutType(Message.ExecuteType.HEARTBEAT.ordinal());
                packet.setMimeType(Message.MIMEType.MESSAGE.ordinal());
                packet.setData(Message.getBytes(beatInfo()));
                Packet response = client.send(packet);
                if (response != null) {
                    String allClients = Message.getString(response.getData());
                    logger.info("All client:" + allClients);
                }
                Utils.sleep(SystemConfig.heartbeatSeconds);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("Send heartbeat stoped:" + ex);
                return;
            }
        }
    }

    /**
     * Heartbeat information.
     *
     * @return
     */
    private String beatInfo() {
        return new HearbeatInfo().toString();
    }
}
