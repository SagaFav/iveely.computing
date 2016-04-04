package com.iveely.computing.ui;

import com.iveely.computing.config.Configurator;
import com.iveely.computing.host.Luggage;
import com.iveely.computing.status.SystemConfig;
import com.iveely.computing.zookeeper.ZookeeperClient;
import java.util.Iterator;
import org.apache.log4j.Logger;

/**
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class ClusterSummary {

    /**
     * Logger
     */
    private final Logger logger = Logger.getLogger(ClusterSummary.class.getName());

    /**
     * Response type.
     */
    private String resType;

    public String getResType() {
        return this.resType;
    }

    /**
     * Current version of computing.
     */
    private String version;

    /**
     * @return the version
     */
    public String getVersion() {

        return version;
    }

    private String setupTime;

    /**
     * @return the setupTime
     */
    public String getSetupTime() {

        return this.setupTime;
    }

    /**
     * Count of slaves.
     */
    private int slaveCount;

    /**
     * @return the slaveCount
     */
    public int getSlaveCount() {
        return slaveCount;
    }

    /**
     * Total slotsCount.
     */
    private int totalSlotCount;

    public int getTotalSlotCount() {
        return this.totalSlotCount;
    }

    /**
     * Count of used slots.
     */
    private int usedSlotCount;

    public int getUsedSlotCount() {
        return this.usedSlotCount;
    }

    /**
     * Count of free slots.
     */
    private int freeSlotCount;

    public int getFreeSlotCount() {
        return this.freeSlotCount;
    }

    public void init() {

        // 0. Type
        this.resType = "cluster summary";

        // 1. Version
        if (this.version == null || this.version.isEmpty()) {
            this.version = ClusterSummary.class.getPackage().getSpecificationVersion();
        }

        // 2. Master uptime
        if (this.setupTime == null || this.setupTime.isEmpty()) {
            String time = ZookeeperClient.getInstance().getNodeValue(Configurator.get().getMaster().getRoot() + "/setup");
            this.setupTime = time;
        }

        // 3. Count of slaves.
        this.slaveCount = ZookeeperClient.getInstance().getChildren(Configurator.get().getSlave().getRoot()).size();

        // 4. Total slots.
        this.totalSlotCount = this.slaveCount * Configurator.get().getSlave().getSlotCount();

        // 5. Used slots.
        this.usedSlotCount = 0;
        Iterator it = Luggage.slaves.keySet().iterator();
        while (it.hasNext()) {
            this.usedSlotCount += Luggage.slaves.get(it.next());
        }

        // 6. Free slots.
        this.freeSlotCount = this.totalSlotCount - this.usedSlotCount;
    }
}
