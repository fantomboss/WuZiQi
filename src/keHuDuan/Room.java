package keHuDuan;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author fantomboss
 * @date 2018/12/27-14:33
 */
public class Room extends JSplitPane {

  JTabbedPane enemyP,myP;                 //<!选项卡!>对手,自己
  JSplitPane leftSP,leftSPa;              //<!分割面板!>左边
  JLabel enemyHeadImg,myHeadImg,
          enemyName,myName,
          enemyTime,myTime;
  Play qp;
  JPanel enemy,my;               //<!布局!>对战,聊天,对手信息,自己
  TalkPane talkP;
  String nullPepole = "resPackage/res/img/noone.gif";    //无玩家
  int tnum;
  CUser enemyPlayer = new CUser();                    //对战玩家
  int Mtime = 60;                              //我方下棋时间
  int Etime = 60;                              //对方下棋时间
  Timer tt = new Timer();               //我方时间
  Timer tts = new Timer();              //对手时间
  GameHall hall;
  CUser me;

  /**
   * 对战房间
   * @param u       当前登录用户
   * @param gamehall 游戏大厅
   * @param num       卓子（房间）的编号
   * @param sit      在大厅中的位置
   */
  public Room(CUser u, GameHall gamehall, int num, JButton sit){

    ImageIcon ic = new ImageIcon();
    try {
      ic = new ImageIcon(ImageIO.read(new File(nullPepole)));
    } catch (IOException e) {
      e.printStackTrace();
    }


    //--------------对局界面初始化--------------//
    //--------------对局界面初始化--------------//
    tnum = num;
    me = u;
    hall = gamehall;
    //初始化检测线程
        //初始化分割面板
    leftSP = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    leftSPa = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        //初始化选项卡
    enemyP = new JTabbedPane();
    myP = new JTabbedPane();
        //初始化布局
    enemy = new JPanel(new BorderLayout());
    my = new JPanel(new BorderLayout());
    talkP = new TalkPane(gamehall);
    qp = new Play(num,gamehall,sit,this);
        //初始化组件
    enemyHeadImg = new JLabel(ic);
    enemyName = new JLabel("  ");
    enemyTime = new JLabel("等待准备中~ ");
    myHeadImg = new JLabel(u.getHead());
    myName = new JLabel(u.getName());
    myTime = new JLabel("等待准备中~ ");

    //设置主分割面板
    setRightComponent(qp);
    setLeftComponent(leftSP);
    setDividerLocation(170);

    //设置右边分割面板
    leftSP.setTopComponent(leftSPa);
    leftSP.setBottomComponent(talkP);
    leftSP.setDividerLocation(370);   //上下分割面板的距离
    leftSPa.setTopComponent(enemyP);
    leftSPa.setBottomComponent(myP);
    leftSPa.setDividerLocation(170);    //上下分割面板的距离

    //设置右边上部分玩家信息分割面板
    leftSPa.setTopComponent(enemy);
    leftSPa.setBottomComponent(my);

    //添加对战玩家信息到对应的分割面板的容器中
    enemy.add(BorderLayout.CENTER,enemyHeadImg);
    enemy.add(BorderLayout.EAST,enemyName);
    enemy.add(BorderLayout.SOUTH,enemyTime);
    my.add(BorderLayout.CENTER,myHeadImg);
    my.add(BorderLayout.EAST,myName);
    my.add(BorderLayout.SOUTH,myTime);
  }

  public void setReady(){
    myTime.setText("我准备好了~");
  }

  //设置对手信息-
  public void setEnemyPlayer(CUser cc){
    System.out.println("setTable: "+tnum);
    if(cc.getReady())
      enemyTime.setText("我准备好了~");
    else
      enemyTime.setText("等待准备中~");
    enemyPlayer = cc;
    enemyHeadImg.setIcon(cc.getHead());
    enemyName.setText(cc.getName());
    tts.cancel();
  }

  //60s倒计时开始
  public void sartTime(){
    tt = new Timer();
    tt.schedule(new setMyTime(),0,1000);
  }
  //对手60s倒计时开始
  public void sartEtime(){
    tts = new Timer();
    tts.schedule(new setEnemyTime(),0,1000);
  }

  //1妙钟倒计时
  private class setMyTime extends TimerTask {
    @Override
    public void run() {
      if(Mtime!=0){
        myTime.setText("本轮下棋时间："+String.valueOf(Mtime));
        Mtime--;
      }else {
        //下棋结束--轮换
        tt.cancel();              //倒计时结束
        me.setEnable(false);      //设置下棋权为false
        hall.getC().enemyTime(getEnemyPlayer().getID(),-1,-1);  //通知对手下棋
        myTime.setText("下棋时间结束");       //调整自己显示
        setEtime(60);
        sartEtime();
      }
    }
  }

  //1妙钟倒计时
  private class setEnemyTime extends TimerTask {
    @Override
    public void run() {
      if(Etime!=0){
        enemyTime.setText("本轮下棋时间："+String.valueOf(Etime));
        Etime--;
      }else {
        //下棋结束--轮换
        tts.cancel();              //倒计时结束
        enemyTime.setText("我下棋时间到了哦，到你啦~");
      }
    }
  }

  //--------getterAndSetter------------//

  public CUser getEnemyPlayer() {
    return enemyPlayer;
  }

  public TalkPane getTalkP() {
    return talkP;
  }

  public int getMtime() {
    return Mtime;
  }

  public void setMtime(int mtime) {
    Mtime = mtime;
  }

  public int getEtime() {
    return Etime;
  }

  public void setEtime(int etime) {
    Etime = etime;
  }


  public CUser getMe() {
    return me;
  }

  public void setMe(CUser me) {
    this.me = me;
  }

  public Timer getTt() {
    return tt;
  }

  public void setTt(Timer tt) {
    this.tt = tt;
  }

  public Play getQp() {
    return qp;
  }
}
