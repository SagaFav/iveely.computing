/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveely.computing.config;

/**
 *
 * @author Administrator
 */
public class SlaveConfig {

    private String root;

    private Integer port;

    private Integer slot;

    private Integer slotCount;
    
    public SlaveConfig(){
        
    }

    public SlaveConfig(Integer port, Integer slot, Integer slotCount, String root) {
        this.slot = slot;
        this.port = port;
        this.root = root;
        this.slotCount = slotCount;
    }

    /**
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

    /**
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * @return the slot
     */
    public Integer getSlot() {
        return slot;
    }

    /**
     * @return the root
     */
    public String getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(String root) {
        this.root = root;
    }

    /**
     * @param slot the slot to set
     */
    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    /**
     * @return the slotCount
     */
    public Integer getSlotCount() {
        return slotCount;
    }

    /**
     * @param slotCount the slotCount to set
     */
    public void setSlotCount(Integer slotCount) {
        this.slotCount = slotCount;
    }

}
