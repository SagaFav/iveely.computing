/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveely.computing.common;

/**
 *
 * @author Administrator
 */
public class ProcessBuilder {

    public static Process start(String jarPath, String args) {
        Runtime rt = Runtime.getRuntime();
        Process p = null;
        String fileLac = "";
        try {
            fileLac = "java -Djava.ext.dirs=lib/ -jar -Xms32m -Xmx1724m " + jarPath + " " + args;
            //fileLac = "java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,address=8765 -Djava.ext.dirs=lib/ -jar -Xms32m -Xmx1024m " + jarPath + " " + args;
            p = rt.exec(fileLac);
            return p;
        } catch (Exception e) {
            System.out.println("open failure");
        }
        return null;
    }

    public static void kill(Process p) {
        if (p != null && p.isAlive()) {
            p.destroy();
        }
    }

}
