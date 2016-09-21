package com.github.chord1645.msgque.demo.ui;

import java.awt.*;
import java.io.Serializable;

class Point1 implements Serializable {
    int x, y;// 鼠标的位置
    Color col;// 画图选择的颜色
    int tool;// 画图选择画哪种图形
    int boarder;// 线条宽度


    Point1(int x, int y, Color col, int tool, int boarder) {
        this.x = x;
        this.y = y;
        this.col = col;
        this.tool = tool;
        this.boarder = boarder;
    }
}
