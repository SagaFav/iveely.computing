/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveely.computing.host;

import com.iveely.computing.common.Message;
import com.iveely.computing.common.Utils;
import com.iveely.computing.zookeeper.ZookeeperClient;
import com.iveely.framework.net.Packet;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 *
 * @author Administrator
 */
public class NodeValidator implements Runnable {

	private static HashMap<String, Long> heartbeat;

	private MasterProcessor masterProcessor;

	public void setMasterProcessor(MasterProcessor processor) {
		this.masterProcessor = processor;
	}

	/**
	 * Logger
	 */
	private final Logger logger = Logger.getLogger(NodeValidator.class.getName());

	public NodeValidator() {
		if (heartbeat == null) {
			heartbeat = new HashMap<>();
		}
	}

	public void arrive(String ipaddress) {
		heartbeat.put(ipaddress, new Date().getTime());
	}

	@Override
	public void run() {
		while (true) {
			try {
				Iterator iter = heartbeat.keySet().iterator();
				while (iter.hasNext()) {
					String key = (String) iter.next();
					Long val = heartbeat.get(key);
					long diff = new Date().getTime() - val;
					if (diff / 1000 > 60) {
						ZookeeperClient.getInstance().deleteNode("/iveely/slave/" + key);
						Luggage.slaves.remove(key);
						Luggage.performanceSlaves.remove(key);
						logger.error(key + " is crashed.");
						logger.info("Check topologies on " + key);
						String[] list = NodeTopology.getInstance().get(key);
						if (list != null) {
							for (String tpName : list) {
								Packet packet = new Packet();
								packet.setExecutType(Message.ExecuteType.REBALANCE.ordinal());
								packet.setData(Message.getBytes(tpName));
								packet.setMimeType(Message.MIMEType.MESSAGE.ordinal());
								this.masterProcessor.invoke(packet);
							}
						} else {
							logger.info("No topologies on this node.");
						}
						iter.remove();
					}
				}
				Utils.sleep(5);
			} catch (Exception e) {
			}
		}

	}

}
