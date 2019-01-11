package keHuDuan;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * @author fantomboss
 * @date 2018/12/26-22:08
 */
public class Log extends JFrame {

  JLabel logText,nameText,serviceText,imgText,head;
  JButton contect,reset,exit;
  JTextField name,service;
  JPanel JpA;                   //布局A(边界)
  JPanel JpB;                   //布局B(网格)
  JPanel JpC;                   //布局C(网格)
  JPanel JpD;                   //布局D(流)
  JPanel JpE;                   //布局E(流)
  int num;                  //图片计数
  String headimg = "resPackage/res/face/1-1.gif";           //默认头像路径            系统上线路径
  static Client c;
  int chooseHead = 1;         //选择的头像

  public Log(){

    //------test-------/
    ImageIcon ic = new ImageIcon();
    try {
       ic = new ImageIcon(ImageIO.read(new File(headimg)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    //------test-------/

    //创建客户端连接类

    c = new Client();

    //初始化组件
    logText = new JLabel("请输入您的个人信息");
    nameText = new JLabel("用户名：");
    serviceText = new JLabel("服务器：");
    imgText = new JLabel("头像：");
    name = new JTextField("test");
    service = new JTextField("169.254.146.119");
    head = new JLabel((ic),SwingConstants.LEFT);
    contect = new JButton("连接");
    reset = new JButton("重置");
    exit = new JButton("退出");
    num = 1;

    //设置点击事件
          //--连接
    contect.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(e.getSource()==contect){
          if(!name.getText().equals("")) {
            CUser u = new CUser(head.getIcon(),name.getText());
            if(c.connect(service.getText())) {
              GameHall g = new GameHall(u,c);
              c.setHall(g);
              c.sendMessage("set:"+name.getText()+","+"resPackage/res/face/"+chooseHead+"-1.gif");    //传递玩家信息（以逗号为分割符）
              c.sendMessage("log:"+"--登录了");
              c.getHallInfo();
              dispose();   //退出
            }else{
              JOptionPane.showMessageDialog(null,"服务器连接失败","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
            }
          }else{
            JOptionPane.showMessageDialog(null,"用户名不为空","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
          }
        }
      }
    });
          //--退出
    exit.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(e.getSource()==exit){
            System.exit(0);   //退出
        }
      }
    });
          //--重置
    reset.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(e.getSource()==reset){
            name.setText("");
            service.setText("");
            head.setIcon(new ImageIcon(headimg));
        }
      }
    });

    //设置各个容器的布局
    JpA = new JPanel(new BorderLayout());
    JpB = new JPanel(new GridLayout(3,1));
    JpC = new JPanel(new GridLayout(3,1));
    JpD = new JPanel(null);
    JpE = new JPanel(new FlowLayout());

    //添加总体的容器
    Container c = this.getContentPane();
    c.setLayout(new BorderLayout());
    c.add(BorderLayout.NORTH,JpA);
    c.add(BorderLayout.CENTER,JpD);
    c.add(BorderLayout.SOUTH,JpE);

    //--------添加各个容器的对应组件---------//
    JpA.add(BorderLayout.NORTH,logText);
    JpA.add(BorderLayout.WEST,JpB);
    JpA.add(BorderLayout.CENTER,JpC);

    JpB.add(nameText);
    JpB.add(serviceText);
    JpB.add(imgText);

    JpC.add(name);
    JpC.add(service);
    JpC.add(head);

          //添加头像
    for(int i = 10;i<32*8;i+=32) {
      ImageIcon img = new ImageIcon();
      for(int j = 9;j<32*11&&num<86;j+=32) {
        try {
          img = new ImageIcon(ImageIO.read(new File("resPackage/res/face/" + num + "-1.gif")));
        } catch (IOException e) {
          e.printStackTrace();
        }
        logHeadButton b = new logHeadButton(img);
        b.setNum(num);
        b.setBounds(j, i, 32, 32);
                //添加监听
        b.addActionListener(new ActionListener() {
          @Override
          public void actionPerformed(ActionEvent e) {
            if(b==e.getSource()){
              head.setIcon(b.getIcon());
              chooseHead = b.getNum();
            }
          }
        });
        JpD.add(b);
        num++;
      }
    }

    JpE.add(contect);
    JpE.add(reset);
    JpE.add(exit);

    setVisible(true);
    setSize(370,460);
    setResizable(false);                                    //设置不可变
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

  }

  public static void main(String []args){
    new Log();
  }
}
