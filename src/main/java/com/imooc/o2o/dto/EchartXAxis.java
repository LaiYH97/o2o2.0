package com.imooc.o2o.dto;

import java.util.HashSet;
/*
 * 迎合echart的XAxis
 */
public class EchartXAxis {
    private String type = "category";

    private HashSet<String> data;

    public String getType() {
        return type;
    }

    public HashSet<String> getData() {
        return data;
    }

    public void setData(HashSet<String> data) {
        this.data = data;
    }
}
