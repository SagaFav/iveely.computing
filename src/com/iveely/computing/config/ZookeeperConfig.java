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
public class ZookeeperConfig {

    private String address;

    private Integer port;

    public ZookeeperConfig(String address, Integer port) {
        this.address = address;
        this.port = port;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the port
     */
    public Integer getPort() {
        return port;
    }

}
