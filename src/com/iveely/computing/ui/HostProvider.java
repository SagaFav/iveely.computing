package com.iveely.computing.ui;

import com.iveely.computing.status.SystemConfig;
import com.iveely.framework.net.SyncServer;
import com.iveely.framework.net.websocket.SocketServer;

import org.apache.log4j.Logger;

/**
 *
 * @author sea11510@mail.ustc.edu.cn
 * @date 2015-3-7 23:04:49
 */
public class HostProvider implements Runnable {

	/**
	 * Websocket server.
	 */
	private SocketServer socket;

	/**
	 * Response callback.
	 */
	private Response response;

	/**
	 * Logger.
	 */
	private final Logger logger = Logger.getLogger(HostProvider.class.getName());

	public HostProvider(SyncServer.ICallback masterEvent, String uiPwd) {
		try {
			this.response = new Response(masterEvent, uiPwd);
			this.socket = new SocketServer(this.response, SystemConfig.uiPort);
		} catch (Exception e) {
			logger.error(e);
		}

	}

	@Override
	public void run() {
		logger.info("UI service is starting...");
		this.socket.start();
	}
}
