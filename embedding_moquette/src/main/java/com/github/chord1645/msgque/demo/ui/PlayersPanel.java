package com.github.chord1645.msgque.demo.ui;


import com.google.common.base.Joiner;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class PlayersPanel extends Panel {
    JTextArea textArea = new JTextArea();
    public PlayersPanel() {
        textArea.setFont(PaintFrame.defaultFont);
        textArea.setColumns(10);
        textArea.setRows(300);
        textArea.setText("游客");
        this.add(textArea);
        setBounds(0, 80, 200, 300);
//        setBackground(Color.LIGHT_GRAY);
        setVisible(true);
    }

    public void reflesh(Collection<String> players) {
        textArea.setText(Joiner.on("\n").join(players));
    }

    public void clear() {
        textArea.setText("");
    }
}// end PlayersPanel