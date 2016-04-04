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
public class MasterConfig {

    private String address;

    private Integer port;

    private String password;

    private Integer ui_port;

    private String root;

    public MasterConfig() {

    }

    public MasterConfig(String address, Integer port, Integer uiport, String password, String root) {
        this.address = address;
        this.port = port;
        this.password = password;
        this.ui_port = uiport;
        this.root = root;
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

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return the ui_port
     */
    public Integer getUi_port() {
        return ui_port;
    }

    /**
     * @param ui_port the ui_port to set
     */
    public void setUi_port(Integer ui_port) {
        this.ui_port = ui_port;
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
     * @param port the port to set
     */
    public void setPort(Integer port) {
        this.port = port;
    }

}
