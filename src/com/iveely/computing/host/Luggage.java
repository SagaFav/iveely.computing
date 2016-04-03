package com.iveely.computing.host;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

/**
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class Luggage {

    /**
     * Slaves sorted by performance.
     */
    public final static List<String> performanceSlaves = new ArrayList<>();

    /**
     * All slaves.
     */
    public final static TreeMap<String, Integer> slaves = new TreeMap<>();

}
