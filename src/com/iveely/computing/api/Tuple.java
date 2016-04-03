package com.iveely.computing.api;

import java.util.List;

/**
 *
 * @author sea11510@mail.ustc.edu.cn
 */
public class Tuple {

    /**
     * Data of the fileds.
     */
    private final List<Object> list;

    public Tuple(List<Object> list) {
        this.list = list;
    }

    public int getSize() {
        return this.list.size();
    }

    public Object get(int index) {
        return list.get(index);
    }
}
