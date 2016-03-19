package com.iveely.computing.api;

import java.util.HashMap;
import java.util.UUID;
import org.apache.log4j.Logger;

/**
 * Data ouput.
 *
 * @author liufanping@iveely.com
 * @date 2015-3-4 19:43:16
 */
public abstract class IOutput implements Cloneable {

	/**
	 * Unique instance name.
	 */
	private String name = this.getClass().getSimpleName() + "(" + UUID.randomUUID().toString() + ")";

	/**
	 * Logger.
	 */
	private static final Logger logger = Logger.getLogger(IOutput.class.getName());

	/**
	 * Prepare before execute.
	 *
	 * @param conf
	 * @param collector
	 */
	public void start(HashMap<String, Object> conf) {

	}

	/**
	 * Declare field to output.
	 *
	 * @param declarer
	 */
	public abstract void declareOutputFields(FieldsDeclarer declarer);

	/**
	 * Process recived tuple.
	 *
	 * @param tuple
	 */
	public abstract void execute(Tuple tuple, StreamChannel channel);

	/**
	 * Data to which output.
	 *
	 */
	public void toOutput(StreamChannel channel) {

	}

	/**
	 * Prepare before execute.
	 *
	 * @param conf
	 */
	public void end(HashMap<String, Object> conf) {

	}

	/**
	 * Get name of the task.
	 *
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Clone self.
	 *
	 * @return
	 */
	public IOutput cloneSelf() {
		IOutput o = null;
		try {
			o = (IOutput) super.clone();
			o.name = this.getClass().getName() + "(" + UUID.randomUUID().toString() + ")";
		} catch (CloneNotSupportedException e) {
			logger.error(e);
		}
		return o;
	}
}
