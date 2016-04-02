/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveely.computing.supervisor;

import com.iveely.computing.common.Message;
import com.iveely.framework.net.Packet;
import com.iveely.framework.net.SyncClient;

/**
 *
 * @author Administrator
 */
public class WorkerChecker {

	public static boolean isOnline(int port) {
		SyncClient client = new SyncClient("127.0.0.1", port);
		Packet packet = new Packet();
		packet.setExecutType(Message.ExecuteType.ISONLINE.ordinal());
		packet.setData(Message.getBytes("Nice to meet you!"));
		packet.setMimeType(1);
		packet = client.send(packet);
		if (packet.getExecutType() != Message.ExecuteType.RESPISONLINE.ordinal()) {
			return false;
		}
		return true;
	}

}
