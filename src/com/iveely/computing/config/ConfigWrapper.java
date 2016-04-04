/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveely.computing.config;

import com.iveely.framework.text.JSONUtil;
import java.io.File;

/**
 *
 * @author Administrator
 */
public class ConfigWrapper {

    private static Configurator configurator;

    public static Configurator get() {
        if (configurator == null) {
            synchronized (Configurator.class) {
                if (configurator == null) {
                    load();
                }
            }
        }
        return configurator;
    }

    private static void load() {
        Configurator instance = JSONUtil.fromFile(new File("conf/system.json"), Configurator.class);
        if (instance != null) {
            configurator = instance;
        } else {
            configurator.setMaster(new MasterConfig("127.0.0.1", 8000, 9000, "", "/iveely.computing/master"));
            configurator.setSlave(new SlaveConfig(4000, 6000, 6, "/iveely.computing/slave"));
            configurator.setZookeeper(new ZookeeperConfig("127.0.0.1", 2181));
            JSONUtil.toFile(configurator, new File("conf/system.json"));
        }
    }
}
