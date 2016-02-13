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
public class Utils {

    public static void sleep(int seconds) {
        if (seconds < 1) {
            return;
        }
        try {
            Thread.sleep(seconds * 1000);
        } catch (Exception e) {
        }
    }

}
