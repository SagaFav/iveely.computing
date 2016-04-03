package com.iveely.computing.host;

import com.iveely.framework.net.websocket.WSHandler;
import com.iveely.framework.net.websocket.SocketServer;

/**
 * Event processor for web to master.
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class EventProcessor implements SocketServer.IHandler {

    @Override
    public String invoke(Integer sessionId, WSHandler handler, String data) {
        return data;
    }

    @Override
    public void close(Integer sessionId) {

    }

}
