package com.github.chord1645.msgque.demo.ui;


import com.github.chord1645.msgque.demo.client.IPaintClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

public class PaintFrame extends Frame implements ActionListener, MouseMotionListener, MouseListener, ItemListener {
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
    Vector paintInfo = null;// 点信息向量组
    int n = 1;
    FileInputStream picIn = null;
    FileOutputStream picOut = null;
    ObjectInputStream VIn = null;
    ObjectOutputStream VOut = null;
    // *工具面板--画笔，直线，圆，矩形，多边形,橡皮，清除*/
    Panel toolPanel;
    public PaintPanel paper1;
    public PlayersPanel playersPanel;
    TextField room = new TextField("房间号");
    TextField player = new TextField("游客1");
    JButton join,quit;
    JButton eraser, drLine, drCircle, drRect;
    JButton clear, pen;
    Choice ColChoice, SizeChoice, EraserChoice;
    JButton colchooser;
    JLabel 颜色, 大小B, 大小E;
    // 保存功能
    JButton openPic, savePic;
    FileDialog openPicture, savePicture;

    IPaintClient client;
    public static Font defaultFont = new Font(Font.SANS_SERIF, Font.BOLD, 20);

    private void makeFont() {
        try {

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            String names[] = {"Label ", "CheckBox ", "PopupMenu ",
                    "MenuItem ", "CheckBoxMenuItem ", "JRadioButtonMenuItem ",
                    "ComboBox ", "Button ", "Tree ", "ScrollPane ", "TabbedPane ",
                    "EditorPane ", "TitledBorder ", "Menu ", "TextArea ", "OptionPane ",
                    "MenuBar ", "ToolBar ", "ToggleButton ", "ToolTip ", "ProgressBar ",
                    "TableHeader ", "Panel ", "List ", "ColorChooser ", "PasswordField ",
                    "TextField ", "Table ", "Label ", "Viewport ", "RadioButtonMenuItem ",

                    "RadioButton ", "DesktopPane ", "InternalFrame "

            };

            for (int i = 0; i < names.length; i++) {

                UIManager.put(names[i] + ".font ", defaultFont);

            }

            UIManager.put("OptionPane.messageFont ", defaultFont);

            UIManager.put("OptionPane.buttonFont ", defaultFont);
            UIManager.put("Label.foreground ", Color.black);

            UIManager.put("Border.foreground ", Color.black);
            UIManager.put("TitledBorder.titleColor ", Color.black);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public PaintFrame(String s, IPaintClient client) {
        super(s);
        this.client = client;
        makeFont();
        addMouseMotionListener(this);
        addMouseListener(this);


        paintInfo = new Vector();


/* 各工具按钮及选择项 */
// 颜色选择
        ColChoice = new Choice();
        ColChoice.add("black");
        ColChoice.add("red");
        ColChoice.add("blue");
        ColChoice.add("green");
        ColChoice.addItemListener(this);
// 画笔大小选择
        SizeChoice = new Choice();
        SizeChoice.add("1");
        SizeChoice.add("3");
        SizeChoice.add("5");
        SizeChoice.add("7");
        SizeChoice.add("9");
        SizeChoice.addItemListener(this);
// 橡皮大小选择
        EraserChoice = new Choice();
        EraserChoice.add("5");
        EraserChoice.add("9");
        EraserChoice.add("13");
        EraserChoice.add("17");
        EraserChoice.addItemListener(this);
// //////////////////////////////////////////////////
        toolPanel = new Panel();


        clear = new JButton("清除");
        eraser = new JButton("橡皮");
        pen = new JButton("画笔");
        drLine = new JButton("画直线");
        drCircle = new JButton("画圆形");
        drRect = new JButton("画矩形");


        openPic = new JButton("打开图画");
        savePic = new JButton("保存图画");


        colchooser = new JButton("显示调色板");

        //TODO 按钮
        room = new TextField("房间号");
        player = new TextField("游客1");
        room.setColumns(10);
        join = new JButton("加入");
        quit = new JButton("退出");
// 各组件事件监听
        join.addActionListener(this);
        quit.addActionListener(this);
        clear.addActionListener(this);
        eraser.addActionListener(this);
        pen.addActionListener(this);
        drLine.addActionListener(this);
        drCircle.addActionListener(this);
        drRect.addActionListener(this);
        openPic.addActionListener(this);
        savePic.addActionListener(this);
        colchooser.addActionListener(this);


        颜色 = new JLabel("画笔颜色", JLabel.CENTER);
        大小B = new JLabel("画笔大小", JLabel.CENTER);
        大小E = new JLabel("橡皮大小", JLabel.CENTER);
// 面板添加组件

//        join.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                boolean bool = client.join(room.getText(), player.getText());
//            }
//        });
        toolPanel.add(room);
        toolPanel.add(player);
        toolPanel.add(join);
        toolPanel.add(quit);
        toolPanel.add(openPic);
        toolPanel.add(savePic);
        toolPanel.add(pen);
        toolPanel.add(drLine);
        toolPanel.add(drCircle);
        toolPanel.add(drRect);
        toolPanel.add(颜色);
        toolPanel.add(ColChoice);
        toolPanel.add(大小B);
        toolPanel.add(SizeChoice);
        toolPanel.add(colchooser);
        toolPanel.add(eraser);
        toolPanel.add(大小E);
        toolPanel.add(EraserChoice);
        toolPanel.add(clear);
        paper1 = new PaintPanel(paintInfo);//TODO
        playersPanel = new PlayersPanel();//TODO
        add(paper1, BorderLayout.PAGE_START);
        add(playersPanel, BorderLayout.PAGE_START);
        add(toolPanel, BorderLayout.PAGE_START);
        setBounds(230, 50, 1200, 650);
        setVisible(true);
        validate();
// dialog for save and load


        openPicture = new FileDialog(this, "打开图画", FileDialog.LOAD);
        openPicture.setVisible(false);
        savePicture = new FileDialog(this, "保存图画", FileDialog.SAVE);
        savePicture.setVisible(false);


        openPicture.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                openPicture.setVisible(false);
            }
        });


        savePicture.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                savePicture.setVisible(false);
            }
        });


        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });


    }


    public void paint(Graphics g) {
//        System.out.println("paint");
        Graphics2D g2d = (Graphics2D) g;


        Point1 p1, p2;
//        System.out.println(paintInfo.size());
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
        }// end for

    }


    public void itemStateChanged(ItemEvent e) {
        System.out.println("itemStateChanged");
        if (e.getSource() == ColChoice)// 预选颜色
        {
            String name = ColChoice.getSelectedItem();


            if (name == "black") {
                c = new Color(0, 0, 0);
            } else if (name == "red") {
                c = new Color(255, 0, 0);
            } else if (name == "green") {
                c = new Color(0, 255, 0);
            } else if (name == "blue") {
                c = new Color(0, 0, 255);
            }
        } else if (e.getSource() == SizeChoice)// 画笔大小
        {
            String selected = SizeChoice.getSelectedItem();


            if (selected == "1") {
                con = 1;
                size = new BasicStroke(con, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL);


            } else if (selected == "3") {
                con = 3;
                size = new BasicStroke(con, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL);


            } else if (selected == "5") {
                con = 5;
                size = new BasicStroke(con, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL);


            } else if (selected == "7") {
                con = 7;
                size = new BasicStroke(con, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL);


            } else if (selected == "9") {
                con = 9;
                size = new BasicStroke(con, BasicStroke.CAP_BUTT,
                        BasicStroke.JOIN_BEVEL);


            }
        } else if (e.getSource() == EraserChoice)// 橡皮大小
        {
            String Esize = EraserChoice.getSelectedItem();


            if (Esize == "5") {
                Econ = 5 * 2;
            } else if (Esize == "9") {
                Econ = 9 * 2;
            } else if (Esize == "13") {
                Econ = 13 * 2;
            } else if (Esize == "17") {
                Econ = 17 * 3;
            }


        }


    }


    public void mouseDragged(MouseEvent e) {
        client.append(new Apoint(e.getX() - 700, e.getY() - 70, toolFlag));
//        System.out.println("mouseDragged");
        Point1 p1;
        switch (toolFlag) {
            case 0:// 画笔
                x = (int) e.getX();
                y = (int) e.getY();
                p1 = new Point1(x, y, c, toolFlag, con);
                paintInfo.addElement(p1);
                repaint();
                break;


            case 1:// 橡皮
                x = (int) e.getX();
                y = (int) e.getY();
                p1 = new Point1(x, y, null, toolFlag, Econ);
                paintInfo.addElement(p1);
                repaint();
                break;


            default:
        }
    }


    public void mouseMoved(MouseEvent e) {
    }


    public void update(Graphics g) {
//        System.out.println("update");
        paint(g);
    }


    public void mousePressed(MouseEvent e) {
        client.clearCache();
//        System.out.println("mousePressed");
        Point1 p2;
        switch (toolFlag) {
            case 3:// 直线
                x = (int) e.getX();
                y = (int) e.getY();
                p2 = new Point1(x, y, c, toolFlag, con);
                paintInfo.addElement(p2);
                break;


            case 4: // 圆
                x = (int) e.getX();
                y = (int) e.getY();
                p2 = new Point1(x, y, c, toolFlag, con);
                paintInfo.addElement(p2);
                break;


            case 5: // 矩形
                x = (int) e.getX();
                y = (int) e.getY();
                p2 = new Point1(x, y, c, toolFlag, con);
                paintInfo.addElement(p2);
                break;


            default:
        }
    }


    public void mouseReleased(MouseEvent e) {
        client.flushCache();
//        System.out.println("mouseReleased");
        Point1 p3;
        switch (toolFlag) {
            case 0: // 画笔
                paintInfo.addElement(cutflag);
                break;


            case 1: // eraser
                paintInfo.addElement(cutflag);
                break;


            case 3: // 直线
                x = (int) e.getX();
                y = (int) e.getY();
                p3 = new Point1(x, y, c, toolFlag, con);
                paintInfo.addElement(p3);
                paintInfo.addElement(cutflag);
                repaint();
                break;


            case 4: // 圆
                x = (int) e.getX();
                y = (int) e.getY();
                p3 = new Point1(x, y, c, toolFlag, con);
                paintInfo.addElement(p3);
                paintInfo.addElement(cutflag);
                repaint();
                break;


            case 5: // 矩形
                x = (int) e.getX();
                y = (int) e.getY();
                p3 = new Point1(x, y, c, toolFlag, con);
                paintInfo.addElement(p3);
                paintInfo.addElement(cutflag);
                repaint();
                break;
            default:
        }
        paintInfo.clear();
    }


    public void mouseEntered(MouseEvent e) {
    }


    public void mouseExited(MouseEvent e) {
    }


    public void mouseClicked(MouseEvent e) {
    }


    public void actionPerformed(ActionEvent e) {
//        System.out.println("actionPerformed");
        if (e.getSource() == join) {
            boolean bool = client.join(room.getText(), player.getText());
        }
        if (e.getSource() == quit) {
            boolean bool = client.quit(room.getText(), player.getText());
        }
        if (e.getSource() == pen) {
            System.out.println("pen");
            toolFlag = 0;
        }


        if (e.getSource() == eraser){
            System.out.println("eraser");
            toolFlag = 1;
        }


        if (e.getSource() == clear)// 清除

        {
            System.out.println("clear");
            toolFlag = 2;
            paintInfo.removeAllElements();
            repaint();
        }


        if (e.getSource() == drLine)// 画线

        {
            System.out.println("drLine");
            toolFlag = 3;
        }


        if (e.getSource() == drCircle)// 画圆

        {
            System.out.println("drCircle");
            toolFlag = 4;
        }


        if (e.getSource() == drRect)// 画矩形

        {
            System.out.println("drRect");
            toolFlag = 5;
        }


        if (e.getSource() == colchooser)// 调色板

        {
            System.out.println("colchooser");
            Color newColor = JColorChooser.showDialog(this, "我的调色板", c);
            c = newColor;
        }


        if (e.getSource() == openPic)// 打开图画

        {


            openPicture.setVisible(true);


            if (openPicture.getFile() != null) {
                int tempflag;
                tempflag = toolFlag;
                toolFlag = 2;
                repaint();


                try {
                    paintInfo.removeAllElements();
                    File filein = new File(openPicture.getDirectory(),
                            openPicture.getFile());
                    picIn = new FileInputStream(filein);
                    VIn = new ObjectInputStream(picIn);
                    paintInfo = (Vector) VIn.readObject();
                    VIn.close();
                    repaint();
                    toolFlag = tempflag;


                } catch (ClassNotFoundException IOe2) {
                    repaint();
                    toolFlag = tempflag;
                    System.out.println("can not read object");
                } catch (IOException IOe) {
                    repaint();
                    toolFlag = tempflag;
                    System.out.println("can not read file");
                }
            }


        }


        if (e.getSource() == savePic)// 保存图画

        {
            savePicture.setVisible(true);
            try {
                File fileout = new File(savePicture.getDirectory(),
                        savePicture.getFile());
                picOut = new FileOutputStream(fileout);
                VOut = new ObjectOutputStream(picOut);
                VOut.writeObject(paintInfo);
                VOut.close();
            } catch (IOException IOe) {
                System.out.println("can not write object");
            }


        }

    }

    /**
     * 追加绘画笔迹
     *
     * @param data
     */
    public void addData(List<Apoint> data) {
        data.forEach(apoint -> {
            Point1 p1 = new Point1(apoint.getX(), apoint.getY(), Color.black, apoint.getType(), con);
            paper1.paintInfo.addElement(p1);
        });
        paper1.repaint();
    }

    public void refleshRoom(Collection<String> players) {
        playersPanel.reflesh(players);
    }

    public void clearRoom() {
        playersPanel.clear();
    }
}// end PaintFrame