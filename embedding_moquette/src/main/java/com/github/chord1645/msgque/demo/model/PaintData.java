package com.github.chord1645.msgque.demo.model;

import com.github.chord1645.msgque.demo.ui.Apoint;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@org.msgpack.annotation.Message
public class PaintData implements Serializable {
    Date date;
    List<Apoint> data = new ArrayList<>();

    public List<Apoint> getData() {
        return data;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setData(List<Apoint> data) {
        this.date = new Date();
        this.data = data;
    }

}