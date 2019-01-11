package keHuDuan;

import TablePane.table;
import zhuJi.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

/**
 * @author fantomboss
 * @date 2018/12/27-10:00
 */
public class GameHall extends JFrame {

  JTabbedPane hall,userInfo,serverInfo;     //<!选项卡!>主选项卡(游戏大厅,五子棋)，用户信息，服务器信息
  JSplitPane spA,spB;               //<!分割面板!>对战房间,主分割面板A，子分割面板B
                                                    //<!容器!>用户选项卡内容，服务器选项卡内容,房间面板,房间选择面板头,房间选择面板
  JPanel userP,serviceP,roomP,roomTitleP,roomTitlePa,roomChooseP;
  JButton atuoFind,exitHall;                        //<!按钮!>自动查找,退出
  JLabel title;                                     //<!标签!>五子棋对战
  Room playRoom,lastplayRoom;
  CUser cume;                              //-----用户信息存储在大厅中------//

  JSplitPane talk;        //大厅聊天窗口
  JPanel sends;           //底部菜单容器
  JTextField sendMsg;     //要发送的信息
  JButton send;           //发送按钮
  JTextArea hallTalk;     //聊天记录

  Client c;   //客户端信息

  String userImg = "resPackage/res/img/boy1.gif";

  Vector<table> tableList = new Vector<table>();    //记录卓子

  /**
   * 游戏客户端登录主界面
   * @param u  当前登录用户的头像
   */
  GameHall(CUser u,Client c){

    //------初始化------//
    cume = u;
    this.c = c;
    ImageIcon ic = new ImageIcon();
    try {
      ic = new ImageIcon(ImageIO.read(new File(userImg)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    //初始化选项卡
    hall = new JTabbedPane();
    userInfo = new JTabbedPane();
    serverInfo = new JTabbedPane();
        //初始化分割面板
    spA = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
    spB = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    talk = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        //初始化容器
    userP = new JPanel(new BorderLayout());
    serviceP = new JPanel(new FlowLayout());
    roomP = new JPanel(new BorderLayout());
    roomTitleP = new JPanel(new BorderLayout());
    roomTitlePa = new JPanel(new FlowLayout());
    roomChooseP = new JPanel(null);
    sends = new JPanel(new BorderLayout());
        //初始化各个组件
    title = new JLabel("<<<----五子棋对战---->>>");
    atuoFind = new JButton("自动查找");
    exitHall = new JButton("退出大厅");
    hallTalk = new JTextArea();
    sendMsg = new JTextField();
    send = new JButton("发送");

    JScrollPane scroll = new JScrollPane(hallTalk);   //JTextare放入滚动条中
    scroll.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);   //设置滚动条总是出现
    talk.setTopComponent(scroll);
    talk.setBottomComponent(sends);
    talk.setDividerLocation(480);
    sends.add(BorderLayout.CENTER,sendMsg);
    sends.add(BorderLayout.EAST,send);
    hallTalk.setLineWrap(true);     //设置聊天记录自动换行

    //绑定监听
        //自动查找对手
    atuoFind.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(e.getSource()==atuoFind) {
          if(getLastplayRoom()!=null) {
            long id = getLastplayRoom().getEnemyPlayer().getID();
            if (id == -1) {                                                         //是否有对手
              //无对手，开始查找房间
              findSit();
              return;
            }else{              //有对手的话
              JOptionPane.showMessageDialog(null, "你已经有对手了哦~~", "房间提示", JOptionPane.WARNING_MESSAGE);
              return;
            }
          }
          findSit();
        }
      }
    });
        //发送大厅广播
    send.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String msg = sendMsg.getText();
        if(!msg.equals("")){
          c.sendMessage("all:"+u.getName()+": "+msg);
          sendMsg.setText("");
        }else{
          JOptionPane.showMessageDialog(playRoom,"发送内容不能为空","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
        }
      }
    });
        //退出大厅
    exitHall.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(e.getSource()==exitHall){
          if(!getPlayRoom().getQp().getQiPan().getStart()) {
            new Log();
            c.setNew();
            c.sentExit();
            dispose();
          }else{
            JOptionPane.showMessageDialog(playRoom,"对战中不得随意退出哦~~","房间提示",JOptionPane.WARNING_MESSAGE);
            return;
          }
        }
      }
    });

    //房间区-->房间头
    roomTitleP.add(BorderLayout.WEST,title);
    roomTitleP.add(BorderLayout.EAST,roomTitlePa);
    //房间区-->房间头-->右上角组件
    roomTitlePa.add(atuoFind);
    roomTitlePa.add(exitHall);
    //房间区-->选择区
    roomChooseP.setBackground(new Color(81,118,158));
        //---------选择区添加桌子---------//   //H为桌子之间高的空隙,W为桌子之间宽的空隙,tNum为桌子编号
    int H=30,W=55;
    int tNum = 1;
      for(int j = 30;j<30+62*8+H*8;j+=62+H){
        for(int i = 50;i<20+153*3+W*3;i+=153+W) {
          table t = new table(this,tNum, u.getHead());
          t.setBounds(i, j, 153, 75);
          tableList.add(t);
          roomChooseP.add(t);
          tNum++;
        }
      }

    //房间区
    roomP.add(BorderLayout.NORTH,roomTitleP);
    roomP.add(BorderLayout.CENTER,roomChooseP);

    //服务器信息
    hall.add("游戏大厅",spA);
    userInfo.add("用户信息",userP);
    serverInfo.add("服务器信息",talk);

    //用户信息界面
    userP.add(BorderLayout.CENTER,new JLabel(ic));
    userP.add(BorderLayout.SOUTH,new JLabel(u.getName(),SwingConstants.CENTER));

    //设置主分割面板
    spA.setLeftComponent(spB);
    spA.setRightComponent(roomP);
    //设置左边子分割面板
    spB.setTopComponent(userInfo);
    spB.setBottomComponent(serverInfo);

    //添加主选项卡
    add(hall);

    setVisible(true);
    setTitle("客户端游戏主界面");
    setSize(900,900);
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
  }

  /**
   * 添加房间,lastRoom用来记录当前或者最后进入的房间
   * @param num 新增的房间编号
   * @param sit 当前卓子的座位
   */
  public void addHall(int num,JButton sit){
    if(lastplayRoom==null) {
      playRoom = new Room(cume,this,num,sit);
      lastplayRoom = playRoom;
      hall.add("五子棋游戏-"+num+"房间", playRoom);
      hall.setSelectedComponent(playRoom);
    } else if(lastplayRoom.tnum!=num){
      hall.remove(lastplayRoom);
      int BorW = 1 + new Random().nextInt(2);
      playRoom = new Room(cume,this,num,sit);
      lastplayRoom = playRoom;
      hall.add("五子棋游戏-"+num+"房间", playRoom);
      hall.setSelectedComponent(playRoom);
    }
  }


  //查找对手
  public void findSit(){
    int tnum = 1;
    for (table tt:tableList) {
      if (tt.getLcUser().getID()==-1&&tt.getRcuser().getID()!=-1){
        System.out.println(tt.getLcUser().getID());
        System.out.println(tt.getRcuser().getID());
        chekUserTable(tnum,1);
        return;
      }
      else if (tt.getLcUser().getID()!=-1&&tt.getRcuser().getID()==-1){
        System.out.println(tt.getLcUser().getID());
        System.out.println(tt.getRcuser().getID());
        chekUserTable(tnum, 2);
        return;
      }
      tnum++;
    }
  }

  //添加对面玩家到房间中
  public void addPlayerToRoom(CUser cc){
    lastplayRoom.setEnemyPlayer(cc);
  }

  /**
   * 删除房间
   */
  public void deleHall(){
    hall.remove(lastplayRoom);
    lastplayRoom = null;
  }

  /**
   * 添加大厅信息
   * @param msg
   */
  public void setHallTalk(String msg){
    hallTalk.append(msg+"\r\n");
  }

  //向服务器发起询问，该座位上是否有人
  public Boolean chekUserTable(int num,int leftOrRight){
    c.checkUserSit(num, leftOrRight);
    return true;
  }

  //设置座位为空(大厅)
  public void removeLastSit(int lastTableNum,int LorR){
    String nullPepole = "resPackage/res/img/noone.gif";
    if (lastTableNum != -1) {
      table t = tableList.get(lastTableNum-1);
      t.clearUserInfo(LorR);
      if(LorR==1) {
        t.getJb1().setSize(40, 45);
        try {
          t.getJb1().setIcon(new ImageIcon(ImageIO.read(new File(nullPepole))));
        } catch (IOException e) {
          e.printStackTrace();
        }
      }else if(LorR == 2){
        t.getJb2().setSize(40, 45);
        t.getJb2().setIcon(new ImageIcon(nullPepole));
      }
    }
  }

  //和局
  public void setPeace() {
    playRoom.getQp().getQiPan().setEnd("~^.^~和局~^.^~");
    getPlayRoom().tt.cancel();
    getPlayRoom().tts.cancel();
    getPlayRoom().getQp().peace.setEnabled(false);
    getPlayRoom().getQp().giveUp.setEnabled(false);
  }

  //getterAndsetter

  public Client getC() {
    return c;
  }

  public void setC(Client c) {
    this.c = c;
  }

  public CUser getCume() {
    return cume;
  }

  public void setCume(CUser cume) {
    this.cume = cume;
  }

  public Room getPlayRoom() {
    return playRoom;
  }

  public void setPlayRoom(Room playRoom) {
    this.playRoom = playRoom;
  }

  public Room getLastplayRoom() {
    return lastplayRoom;
  }

  public void setLastplayRoom(Room lastplayRoom) {
    this.lastplayRoom = lastplayRoom;
  }
}
