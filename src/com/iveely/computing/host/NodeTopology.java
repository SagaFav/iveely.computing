/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveely.computing.host;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Administrator
 */
public class NodeTopology {

    private static NodeTopology nodeTopology;

    private static HashMap<String, HashSet<String>> list;

    private NodeTopology() {
        list = new HashMap<>();
    }

    public static NodeTopology getInstance() {
        if (nodeTopology == null) {
            nodeTopology = new NodeTopology();
        }
        return nodeTopology;
    }

    public void set(String node, String tpName) {
        if (list.containsKey(node)) {
            if (!list.get(node).contains(tpName)) {
                list.get(node).add(tpName);
            }
        } else {
            HashSet<String> set = new HashSet<>();
            set.add(tpName);
            list.put(node, set);
        }
    }

    public String[] get(String node) {
        if (list.containsKey(node)) {
            HashSet<String> set = list.get(node);
            String[] res = new String[set.size()];
            res = set.toArray(res);
            return res;
        }
        return null;
    }

}
