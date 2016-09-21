package com.github.chord1645.msgque.demo.ui;


import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

public class PaintPanel extends Panel {
    int x = -1, y = -1;// 初始化鼠标位置
    int con = 5;// 画笔大小
    int Econ = 5;// 橡皮大小


    int toolFlag = 0;// toolFlag:工具标记
// toolFlag工具对应表：
// （0--画笔）；（1--橡皮）；（2--清除）；
// （3--直线）；（4--圆）；（5--矩形）；


    Color c = new Color(0, 0, 0); // 画笔颜色
    BasicStroke size = new BasicStroke(con, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);// 画笔粗细
    Point1 cutflag = new Point1(-1, -1, c, 6, con);// 截断标志

    Vector<Point1> paintInfo = null;// 点信息向量组
    int n = 1;


    FileInputStream picIn = null;
    FileOutputStream picOut = null;


    ObjectInputStream VIn = null;
    ObjectOutputStream VOut = null;


    PaintPanel(Vector paintInfo) {
        this.paintInfo = new Vector();
        setBounds(200, 80, 500, 550);
        setBackground(Color.LIGHT_GRAY);
        setVisible(true);
        validate();

    }


    public void paint(Graphics g) {
//        System.out.println("paint");
        Graphics2D g2d = (Graphics2D) g;
        Point1 p1, p2;
        n = paintInfo.size();

        if (toolFlag == 2)
            g2d.clearRect(0, 0, getSize().width - 100, getSize().height - 100);// 清除
        for (int i = 0; i < n - 1; i++) {
            p1 = (Point1) paintInfo.elementAt(i);
            p2 = (Point1) paintInfo.elementAt(i + 1);
            size = new BasicStroke(p1.boarder, BasicStroke.CAP_BUTT,
                    BasicStroke.JOIN_BEVEL);
            g2d.setColor(p1.col);
            g2d.setStroke(size);
            if (p1.tool == p2.tool) {
                switch (p1.tool) {
                    case 0:// 画笔


                        Line2D line1 = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
                        g2d.draw(line1);
                        break;


                    case 1:// 橡皮
                        g.clearRect(p1.x, p1.y, p1.boarder, p1.boarder);
                        break;


                    case 3:// 画直线
                        Line2D line2 = new Line2D.Double(p1.x, p1.y, p2.x, p2.y);
                        g2d.draw(line2);
                        break;


                    case 4:// 画圆
                        Ellipse2D ellipse = new Ellipse2D.Double(p1.x, p1.y,
                                Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
                        g2d.draw(ellipse);
                        break;


                    case 5:// 画矩形
                        Rectangle2D rect = new Rectangle2D.Double(p1.x, p1.y,
                                Math.abs(p2.x - p1.x), Math.abs(p2.y - p1.y));
                        g2d.draw(rect);
                        break;


                    case 6:// 截断，跳过
                        i = i + 1;
                        break;


                    default:
                }// end switch
            }// end if
            try {
                TimeUnit.NANOSECONDS.sleep(1500);  //绘画延迟,模仿真实笔触
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }// end for
        paintInfo.clear();
    }

    public void update(Graphics g) {
//        System.out.println("update");
        paint(g);
    }


    List<Apoint> list = new ArrayList<>();

}// end PaintFrame