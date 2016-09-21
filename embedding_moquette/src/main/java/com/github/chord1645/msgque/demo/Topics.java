package com.github.chord1645.msgque.demo;

import java.awt.*;
import java.io.Serializable;

public class Topics implements Serializable {
    //server
    public final static String S_JOIN = "S_JOIN";
    public final static String S_JOIN_DONE = "S_JOIN_DONE";
    public final static String S_QUIT = "QUIT";
    public final static String S_READY = "READY";
    public final static String S_ANSWER = "ANSWER";
    //client
    public final static String C_START = "START";
    public final static String C_DRAWER = "DRAWER";
    public final static String C_PAINT = "C_PAINT";
    public final static String C_END = "END";

    public final static String C_PING = "C_PING";


    static public String room(Object roomId) {
        return S_JOIN + "_" + roomId;
    }
    static public String paint(Object roomId) {
        return C_PAINT + "_" + roomId;
    }

    public static String roomId(String topic) {
        return topic.replaceAll(S_JOIN+"_","");
    }
}
