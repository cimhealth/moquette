package com.github.chord1645.msgque.demo.ui;

import org.msgpack.annotation.Message;

import java.io.Serializable;

@Message
public class Apoint implements Serializable{
    Integer x;
    Integer y;
    Integer type=0;
    public Apoint() {
    }

    public Apoint(int x, int y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}