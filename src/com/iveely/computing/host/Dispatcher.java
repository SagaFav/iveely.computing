package com.iveely.computing.host;

import com.iveely.computing.common.Message;
import com.iveely.framework.net.Packet;
import com.iveely.framework.net.SyncClient;

import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 * Dispatcher for master to slave.
 *
 * @author sea11510@mail.ustc.edu.cn
 * @date 2014-10-19 14:13:44
 */
public class Dispatcher {

	/**
	 * Logger
	 */
	private final Logger logger = Logger.getLogger(Dispatcher.class.getName());

	public Dispatcher() {

	}

	/**
	 * Send message to slaves.
	 *
	 * @param packet
	 * @return
	 */
	public Packet callSlaves(Packet packet) {
		Message.ExecuteType executeType = Message.getExecuteType(packet.getExecutType());
		logger.info("callSlaves:" + executeType.name());

		// 1. Upload application
		if (executeType == Message.ExecuteType.UPLOAD) {
			return processTask(packet, Message.ExecuteType.RESPUPLOADAPP);
		}

		// 2. Execute application.
		if (executeType == Message.ExecuteType.RUN) {
			return processRunApp(packet);
		}

		// 3. Show all tasks.
		if (executeType == Message.ExecuteType.LIST) {
			return processTask(packet, Message.ExecuteType.RESPLISTTASK);
		}

		// 4. Kill task.
		if (executeType == Message.ExecuteType.KILLTASK) {
			return processTask(packet, Message.ExecuteType.RESPKILLTASK);
		}

		// 5. Rebalance task.
		if (executeType == Message.ExecuteType.REBALANCE) {
			return processTask(packet, Message.ExecuteType.RESPREBALANCE);
		}

		return Packet.getUnknowPacket();
	}

	/**
	 * Process run application task.
	 *
	 * @param slaves
	 * @param packet
	 * @return
	 */
	private Packet processRunApp(Packet packet) {

		// 1. Prepare packet.
		Packet respPacket = new Packet();
		respPacket.setExecutType(Message.ExecuteType.RESPRUNAPP.ordinal());
		respPacket.setMimeType(Message.MIMEType.TEXT.ordinal());

		// 2. Process.
		StringBuilder responseText = new StringBuilder();
		String[] allcate = Message.getString(packet.getData()).split(" ");

		// 2.1 Is specify slaves count to run.
		String innerData = allcate[0];
		int sum = Luggage.performanceSlaves.size();
		if (allcate.length == 2) {
			sum = Integer.parseInt(allcate[1]);
		}

		// 2.2 Is specify slaves by dependency.
		String dependencyAppString = "";
		if (allcate.length == 3 && "on".equals(allcate[1].toLowerCase())) {
			dependencyAppString = allcate[2];
		}

		// 2.3 Is specify slave by address.
		String specifyAddress = "";
		if (allcate.length == 3 && "at".equals(allcate[1].toLowerCase())) {
			if (Luggage.performanceSlaves.contains(allcate[2])) {
				specifyAddress = allcate[2];
			} else {
				respPacket.setData(Message.getBytes("Can not find your slave."));
				return respPacket;
			}
		}

		// 2.4 Call slaves to run.
		Iterator salve = Luggage.performanceSlaves.iterator();
		int currentId = 0;
		int count = sum;
		do {
			String[] info;
			if (specifyAddress.equals("")) {
				info = salve.next().toString().split(":");
			} else {
				info = specifyAddress.split(":");
			}
			String ip = info[0];
			int port = Integer.parseInt(info[1]);
			SyncClient client = new SyncClient(ip, port);

			// Add flag that 0-2 or 1-2,means has two slaves would be run. first
			// number is id.
			packet.setData(Message.getBytes(innerData + ":" + dependencyAppString + ":" + currentId + "-" + sum));
			Packet slaveRespPacket = client.send(packet);
			responseText.append(info[0]).append(",").append(info[1]).append(":")
					.append(Message.getString(slaveRespPacket.getData())).append("\n");
			count--;
			currentId++;
		} while (salve.hasNext() && count > 0 && specifyAddress.equals(""));

		// 3. Finish packet.
		respPacket.setData(Message.getBytes(responseText.toString()));
		return respPacket;
	}

	/**
	 * Process other task.
	 *
	 * @param slaves
	 * @param packet
	 * @param respExecutType
	 * @return
	 */
	private Packet processTask(Packet packet, Message.ExecuteType respExecutType) {

		// 1. Prepare packet.
		Packet respPacket = new Packet();
		respPacket.setExecutType(respExecutType.ordinal());
		respPacket.setMimeType(Message.MIMEType.TEXT.ordinal());
		StringBuilder responseText = new StringBuilder();

		// 2. Send to slaves.
		Iterator salve = Luggage.performanceSlaves.iterator();
		while (salve.hasNext()) {
			String abs = salve.next().toString();
			logger.info("call slave begin->" + abs);
			String[] info = abs.split(":");
			if (info.length == 2) {
				String ip = info[0];
				int port = Integer.parseInt(info[1]);
				SyncClient client = new SyncClient(ip, port);
				Packet slaveRespPacket = client.send(packet);
				responseText.append(info[0]).append(",").append(info[1]).append(":")
						.append(Message.getString(slaveRespPacket.getData())).append("\n");
			}
			logger.info("call slave end->" + abs);
		}

		// 3. Finish packet.
		respPacket.setData(Message.getBytes(responseText.toString()));
		return respPacket;
	}
}
