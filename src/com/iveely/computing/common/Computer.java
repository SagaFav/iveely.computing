/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveely.computing.common;

import com.sun.management.OperatingSystemMXBean;
import java.lang.management.ManagementFactory;

/**
 *
 * @author Administrator
 */
public class Computer {

    public static int getFreeMemory() {
        OperatingSystemMXBean osmb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return (int) (osmb.getFreePhysicalMemorySize() / 1024 / 1024);
    }

    public static String getCurrentJar() {
        return Computer.class.getProtectionDomain().getCodeSource().getLocation().toString();
    }

}
