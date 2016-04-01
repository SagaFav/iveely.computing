/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveely.computing.supervisor;

import com.iveely.computing.common.Message;
import com.iveely.framework.net.AsynClient;
import com.iveely.framework.net.Packet;

/**
 *
 * @author Administrator
 */
public class WorkerChecker {

    public interface Feedback {

        public void success();

        public void failur();

    }

    private class Handler implements AsynClient.IHandler {

        /*
         * (non-Javadoc)
         * 
         * @see com.iveely.framework.net.AsynClient.IHandler#receive(java.lang.
         * Object)
         */
        @Override
        public void receive(Object obj) {
            Packet packet = (Packet) obj;
            if (packet.getExecutType() == Message.ExecuteType.RESPISONLINE.ordinal()) {
                if (feedback != null) {
                    feedback.success();
                }
            } else {
                if (feedback != null) {
                    feedback.failur();
                }
            }

        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.iveely.framework.net.AsynClient.IHandler#caught(java.lang.String)
         */
        @Override
        public void caught(String exception) {
            // TODO Auto-generated method stub

        }

    }

    private Feedback feedback;

    public WorkerChecker(Feedback feedback) {
        this.feedback = feedback;
    }

    public void isOnline(int port) {
        AsynClient client = new AsynClient("127.0.0.1", port, new Handler());
        Packet packet = new Packet();
        packet.setExecutType(Message.ExecuteType.ISONLINE.ordinal());
        packet.setData(Message.getBytes("Nice to meet you!"));
        packet.setMimeType(1);
        client.send(packet);
    }

}
