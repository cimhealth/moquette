package com.github.chord1645.msgque.demo.model;

import com.github.chord1645.msgque.demo.ui.Apoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@org.msgpack.annotation.Message
public class BaseData implements Serializable {
    List<String> data = new ArrayList<String>();

    public BaseData() {
    }

    public BaseData(List<String> data) {
        this.data = data;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}