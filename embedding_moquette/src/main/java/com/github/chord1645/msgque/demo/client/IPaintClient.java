package com.github.chord1645.msgque.demo.client;

import com.github.chord1645.msgque.demo.ui.Apoint;

public interface IPaintClient {

    void clearCache();

    void flushCache()   ;

    void append(Apoint apoint);

    boolean join(String room, String player);

    boolean quit(String room, String player) ;
}