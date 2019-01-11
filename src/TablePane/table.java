package TablePane;

import keHuDuan.CUser;
import keHuDuan.GameHall;
import keHuDuan.Room;
import zhuJi.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * @author fantomboss
 * @date 2018/12/27-12:28
 */
public class table extends JPanel {

  JButton jb1,jb2;
  JLabel tableL,tableNum;
  String nullPepole = "resPackage/res/img/noone.gif";
  String tablePath = "resPackage/res/img/xqnoone.gif";
  Boolean sitL,sitR;                              //记录座位上是否有人
  CUser lcUser = new CUser();                     //左边玩家的信息
  CUser rcuser = new CUser();                     //右边玩家的信息

  /**
   * 卓子
   * @param playRoom 登陆的游戏客户端类
   * @param num       卓子的编号
   * @param headImg   当前登录用户的头像
   */
  public table(GameHall playRoom, int num, Icon headImg){

    //初始化
    try {
      jb1 = new JButton(new ImageIcon(ImageIO.read(new File(nullPepole))));
      jb2 = new JButton(new ImageIcon(ImageIO.read(new File(nullPepole))));
      tableL = new JLabel(new ImageIcon(ImageIO.read(new File(tablePath))));
    } catch (IOException e) {
      e.printStackTrace();
    }
    tableNum = new JLabel(String.valueOf(num));

    //设置监听
    jb1.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(e.getSource()==jb1) {
          if(playRoom.getLastplayRoom()!=null) {
            long id = playRoom.getLastplayRoom().getEnemyPlayer().getID();
              if (id != -1) {                                                         //是否有对手
                if (!playRoom.getPlayRoom().getQp().getQiPan().getStart()) {          //对战是否开始
                  playRoom.getC().sayByeToEnemy(id);
                } else {
                  JOptionPane.showMessageDialog(playRoom, "对战中不得随意退出哦~~", "房间提示", JOptionPane.WARNING_MESSAGE);
                  return;
                }
              }
            }
          playRoom.chekUserTable(num, 1);
        }
      }
    });
    jb2.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() == jb2) {
          if(playRoom.getLastplayRoom()!=null){
            long id = playRoom.getLastplayRoom().getEnemyPlayer().getID();
              if(id!=-1) {                                                            //是否有对手
                if (!playRoom.getPlayRoom().getQp().getQiPan().getStart()) {          //对战是否开始
                  playRoom.getC().sayByeToEnemy(id);
                }
                else {
                  JOptionPane.showMessageDialog(playRoom,"对战中不得随意退出哦~~","房间提示",JOptionPane.WARNING_MESSAGE);
                  return;
                }
              }
            }
          playRoom.chekUserTable(num, 2);
        }
      }
    });

    //设置布局
    setLayout(null);
    jb1.setBounds(10,10,40,45);
    tableL.setBounds(55,6,53,53);
    jb2.setBounds(113,10,40,45);
    tableNum.setBounds(75,64,30,10);

    //设置样式
    setBackground(new Color(81,118,158));

    //添加到容器中
    add(jb1);
    add(tableL);
    add(jb2);
    add(tableNum);
  }

  //检查对面是否有玩家
  public boolean checkEnemy(int leftOrRight){
    if(leftOrRight==1){if(lcUser.getID()!=-1)return true;}
    if(leftOrRight==2){if(rcuser.getID()!=-1)return true;}
    return false;
  }

  //设置卓子  me表示是否是自己调整位置,leftOrRight表示左右
  public void setUserSit(GameHall playRoom,int num,Icon headImg,int leftOrRight){
    JButton j = new JButton();
    CUser cUser = new CUser();
    //自己入座
    if(leftOrRight==1){j = jb1;cUser = rcuser;}
    else if(leftOrRight==2){j = jb2;cUser = lcUser;}
    j.setSize(32, 32);
    j.setIcon(headImg);
    //判断对手
    playRoom.addHall(num,j);
    if(cUser.getID()!=-1){          //判断对面是否有玩家入座
      playRoom.addPlayerToRoom(cUser);    //添加对手信息到房间
      playRoom.getC().sendInfoToEnemy(cUser.getID());           //给对手发送自己的个人信息
    }
  }

  //设置大厅玩家信息
  public void setHallUser(Icon headImg,Long ID,String name,int leftOrRight){
    JButton j = new JButton();
    CUser cUser = new CUser();
    if(leftOrRight==1){j = jb1;cUser = lcUser;}
    else if(leftOrRight==2){j = jb2;cUser = rcuser;}
    cUser.setHead(headImg);
    cUser.setID(ID);
    cUser.setName(name);
    j.setSize(32, 32);
    j.setIcon(headImg);
  }

  //清楚桌子上存储的用户信息
  public void clearUserInfo(int lor){
    if(lor==1){
      lcUser = new CUser();
    }else if(lor==2){
      rcuser = new CUser();
    }
  }

  //-----getterAndSetter------//

  public Boolean getSitL() {
    return sitL;
  }

  public void setSitL(Boolean sitL) {
    this.sitL = sitL;
  }

  public Boolean getSitR() {
    return sitR;
  }

  public void setSitR(Boolean sitR) {
    this.sitR = sitR;
  }

  public JButton getJb1() {
    return jb1;
  }

  public void setJb1(JButton jb1) {
    this.jb1 = jb1;
  }

  public JButton getJb2() {
    return jb2;
  }

  public void setJb2(JButton jb2) {
    this.jb2 = jb2;
  }

  public CUser getLcUser() {
    return lcUser;
  }

  public void setLcUser(CUser lcUser) {
    this.lcUser = lcUser;
  }

  public CUser getRcuser() {
    return rcuser;
  }

  public void setRcuser(CUser rcuser) {
    this.rcuser = rcuser;
  }

  public table(){
    this.jb1 = new JButton();
    this.jb2 = new JButton();
    this.sitL = false;
    this.sitR = false;
  }
}
