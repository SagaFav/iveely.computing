package com.iveely.computing.common;

/**
 *
 * @author sea11510@mail.ustc.edu.cn
 * @date 2015-3-6 23:13:19
 */
public class IStreamCallback implements Runnable {

	/**
	 * Stream packet.
	 */
	private StreamPacket packet;

	/**
	 * call back method.
	 *
	 * @param packet
	 *            InternetPacket
	 */
	public void invoke(StreamPacket packet) {
		this.packet = packet;
	}

	@Override
	public void run() {

	}
}
