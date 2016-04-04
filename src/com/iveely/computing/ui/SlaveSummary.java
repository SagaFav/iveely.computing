package com.iveely.computing.ui;

import com.iveely.computing.config.ConfigWrapper;
import com.iveely.computing.config.Configurator;
import com.iveely.computing.host.Luggage;
import com.iveely.computing.status.SystemConfig;
import com.iveely.computing.zookeeper.ZookeeperClient;
import com.iveely.framework.text.JSONUtil;

import java.util.List;

/**
 * Slave information summary.
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class SlaveSummary {

    /**
     * Response type.
     */
    private String resType;

    public String getResType() {
        return this.resType;
    }

    /**
     * All slave simple information.
     */
    private String zBuffer;

    public String getZBuffer() {
        return this.zBuffer;
    }

    public static class SlaveSimple {

        public SlaveSimple(String host) {
            this.host = host;
            this.id = host.hashCode();
        }

        /**
         * Id of the slave.
         */
        private final int id;

        public int getId() {
            return this.id;
        }

        /**
         * Host of the slave.
         */
        private final String host;

        public String getHost() {
            return this.host;
        }

        /**
         * Setup time.
         */
        private String setupTime;

        public String getSetupTime() {
            return this.setupTime;
        }

        /**
         * Count of slots.
         */
        private int slotsCount;

        public int getSlotsCount() {
            return this.slotsCount;
        }

        /**
         * How many app on this slave.
         */
        private int runningApp;

        public int getRunningApp() {
            return this.runningApp;
        }

        public void init() {
            // 1. Running app.
            this.runningApp = Luggage.slaves.get(this.host);

            // 2. slots count.
            this.slotsCount = ConfigWrapper.get().getSlave().getSlotCount();

            // 3. setup time.
            this.setupTime = ZookeeperClient.getInstance().getNodeValue(ConfigWrapper.get().getSlave().getRoot() + "/" + this.host);
        }

        public String toJson() {
            return JSONUtil.toString(this);
        }
    }

    public void init() {
        // 1. Response type.
        this.resType = "slave summary";

        // 2. Build topologys.
        List<String> names = ZookeeperClient.getInstance().getChildren("/iveely/slave");
        this.zBuffer = "[";
        if (names.size() > 0) {
            names.stream().map((name) -> new SlaveSimple(name)).map((simple) -> {
                simple.init();
                return simple;
            }).forEach((simple) -> {
                this.zBuffer += simple.toJson() + ",";
            });
        }
        this.zBuffer = this.zBuffer.substring(0, this.zBuffer.length() - 1);
        this.zBuffer += "]";
    }

    /**
     * Current summary to json.
     *
     * @return
     */
    public String toJson() {
        return JSONUtil.toString(this);
    }
}
