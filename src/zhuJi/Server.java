package zhuJi;

import TablePane.table;
import com.sun.deploy.util.StringUtils;
import keHuDuan.CUser;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

/**
 * 服务器
 * @author fantomboss
 * @date 2018/12/28-18:50
 */
public class Server {

  private Vector<User> userList = new Vector<User>();   //记录用户
  Vector<table> tableList = new Vector<table>();    //记录卓子,初始化卓子

  public static void main(String args[]) {
    //打印主机名及IP地址
    InetAddress ip;
    try {
      ip = InetAddress.getLocalHost();
      System.out.println("主机名："+ip.getHostName());
      System.out.println("主机内网IP地址："+ip.getHostAddress());    //测试机子IP：169.254.146.119
    } catch (UnknownHostException e) {
      e.printStackTrace();
    }
    //开启服务器
    Server server = new Server();
    server.waitConnect();
  }

  //开放端口6666,等待连接
  private void waitConnect() {
    ServerSocket ss = null;
    Long ID = Long.valueOf(0);
    try {
      ss = new ServerSocket(1314);
      while (true) {
        Socket s = ss.accept();
        DataInputStream dis = new DataInputStream(s.getInputStream());
        DataOutputStream dos = new DataOutputStream(s.getOutputStream());
        User user = new User(dis, dos,ID);
        ++ID;
        userList.add(user);
        // 创建用户线程
        serverUser userThread = new serverUser(user);
        userThread.start();
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if(ss != null){
        try {
          ss.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  //内部类，处理玩家信息
  class serverUser extends Thread {

    private User user = null;
    private boolean run = true;
    private DataInputStream dis = null;

    public serverUser(User user) {
      super();
      this.user = user;
      dis = user.getDis();
    }

    public void run() {
      //初始化信息
      String gets = null;
      //初始化卓子
      for(int i = 0;i<18;i++) {
        tableList.add(new table());
      }

      int pos;
      String commandMsg = null;
      try {
        while (run) {
          try {
            System.out.println("等待"+user.getName()+"发送信息----");
            gets = dis.readUTF();
          } catch (Exception e) {
            //用户断开连接后续处理!!!重要
            System.out.println(user.getName() + " 断开连接!");
            exitGame();
            break;
          }
            //处理信息
          try {
            String station = gets.substring(0,gets.indexOf(":"));           //获取状态标识
            String msg = gets.substring(gets.indexOf(":")+1,gets.length()); //获取内容
            System.out.println(user.getName()+"标识："+station);
            if(station.equals("set")){System.out.println(user+"初始化信息...");setUserMsg(msg);}   //0、初始化玩家信息
            if(station.equals("log")){System.out.println(user.getName()+" 登录了");}               //1、登录
            if(station.equals("all")){sendToAllUser("all:"+msg);}                            //2、发送到大厅(大厅聊天/广播)
            if(station.equals("sit")){setUserSitInfo(msg);}                                       //3、设置座位信息
            if(station.equals("inRoom")){sendEnemyInfo(msg);}                                     //4、向某用户发送对手信息
            if(station.equals("exitRoom")){exitRoom();}                                           //5、某用户退出房间
            if(station.equals("sayBye")){sayBay(msg);}                                            //6、向用户发送离开房间信号
            if(station.equals("talkToEnemy")){sendMsgToEnemy(msg);}                               //7、向对手发送聊天信息
            if(station.equals("setReadyAtHall")){sendReadyAtHall();}                              //8、向其他玩家大厅发送设置某玩家进入准备状态
            if(station.equals("setReadyAtRoom")){snedReadyAtRoom(msg);}                           //9、向对手的房间设置准备信息
            if(station.equals("changeTurn")){changeTurnToEnemy(msg);}                             //10、向对手发送轮换信号
            if(station.equals("victory")){tellVictroryTo(msg);}                                   //11、向对手宣布胜利
            if(station.equals("getHallInfo")){sendHallSit();}                                     //12、发送大厅卓子信息
            if(station.equals("exitHall")){exitGame();}                                           //13、用户退出游戏大厅
            if(station.equals("askGiveUp")){askGiveUp(msg);}                                      //14、请求认输
            if(station.equals("askPeace")){askPeace(msg);}                                        //15、请求和局
            if(station.equals("peace")){sendPeace(msg);}                                          //16、确认和局
            if(station.equals("NO")){sayNo(msg);}                                                 //17、对手不同意认输或者和局
          }catch (Exception e){
            System.out.println("异常攻击信号：----- !!" + gets);
            e.printStackTrace();
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    //发送同意和局信号
    private void sendPeace(String msg) {
      user.setReady(false);
      User uu = checkUser(Long.valueOf(msg));
      try {
        uu.setReady(false);
        uu.getDos().writeUTF("peace:");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    //对手不同意认输或者和局
    private void sayNo(String msg) {
      try {
        checkUser(Long.valueOf(msg)).getDos().writeUTF("NO:");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    //向某对手发送求合请求
    private void askPeace(String msg) {
      Long id = Long.valueOf(msg);
      if(id!=-1) {
        try {
          checkUser(Long.valueOf(msg)).getDos().writeUTF("askPeace:");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    //向某对手发送认输请求
    private void askGiveUp(String msg) {
      Long id = Long.valueOf(msg);
      if(id!=-1) {
        try {
          checkUser(Long.valueOf(msg)).getDos().writeUTF("askGiveUp:");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    //大厅桌子信息整理并发送
    private void sendHallSit() {
      for (User uu : userList) {
        if (uu.getTableNum() != -1) {
          String tableNum = String.valueOf(uu.getTableNum());
          String leftOrRight = String.valueOf(uu.getLeftOrRight());
          Boolean ready = uu.getReady();
          String name = uu.getName();
          String ID = String.valueOf(uu.getID());
          String head = uu.getHead().toString();
          String station = "sendHallInfo:";
          String hallInfo = station
                  + tableNum + ","
                  + leftOrRight + ","
                  + ready + ","
                  + name + ","
                  + ID + ","
                  + head;
          //桌号，左右，是否准备，姓名，ID，头像
          System.out.println("msg:  --  :"+hallInfo);
        try {
            user.getDos().writeUTF(hallInfo);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
      }
    }

    //11、向对手宣布胜利
    private void tellVictroryTo(String msg) {
      user.setReady(false);
      String station = "enemyVictory:";
      User uu = checkUser(Long.valueOf(msg));
      try {
        uu.setReady(false);
        uu.getDos().writeUTF(station);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    //10、向对手发送轮换信号
    private void changeTurnToEnemy(String msg) {
      String info[] = msg.split(",");
      Long id = Long.valueOf(info[0]);
      int x = Integer.valueOf(info[1]);
      int y = Integer.valueOf(info[2]);
      if(id!=-1) {
        User uu = checkUser(id);
        try {
          uu.getDos().writeUTF("yourTurn:" + x + "," + y);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    //判断对手是否准备,如果准备就发送开始对战信息,uu为对手
    private void checkReady(User uu){
      if(uu.getReady()){
        //双方都进入了准备状态，对战开始
        User first = new User();
        User second = new User();
        uu.setEnemyId(user.getID());            //!!!互相设置ID
        user.setEnemyId(uu.getID());            //!!!互相设置ID
        int who =1 + new Random().nextInt(2);   //摇到1 就 自己开始，否则 对手先开始
        if (who==1){first = user;second = uu;}
        else if(who==2){first = uu;second = user;}
        try {
          //向先手玩家发送开始信号
          first.getDos().writeUTF("first:");
          //向后手玩家发送等待信号
          second.getDos().writeUTF("second:");
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    //向对手的房间设置准备信息
    private void snedReadyAtRoom(String msg) {
      sendReadyAtHall();
      String station="setReadyAtRoom:";
      Long id = Long.valueOf(msg);      //new Long("100") != Long.valueOf("100");
      User uu = checkUser(id);
      try {
        checkReady(uu);
        uu.getDos().writeUTF(station);
      } catch (IOException e) {
        e.printStackTrace();
      }
      user.setReady(true);
    }

    //向其他玩家大厅发送设置某玩家进入准备状态
    private void sendReadyAtHall() {
      String station = "setReadyAtHall:";
      String tnum = String.valueOf(user.getTableNum());
      String lor = String.valueOf(user.getLeftOrRight());
      String msg = station+tnum+","+lor;
      sendOtherUser(msg);   //桌号+座位
      user.setReady(true);
    }

    //向对手发送聊天记录
    //ID+发送者姓名+要发送的信息
    private void sendMsgToEnemy(String msg) {
      String info[] = msg.split(",");
      long enemyId = new Long(info[0]);
      String name = info[1];
      String sendMsg = info[2];
      String station = "roomTalk:";
      if(enemyId!=-1) {
        User uu = checkUser(enemyId);
        try {
          uu.getDos().writeUTF(station + name + "," + sendMsg);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

    //向用户发送离开房间信号
    public void sayBay(String msg){
      User uu = checkUser(Long.valueOf(msg));
      try {
        uu.getDos().writeUTF("bye:");
        uu.setEnemyId(-1);
        user.setEnemyId(-1);
      } catch (IOException e) {
        e.printStackTrace();
      }
      user.setReady(false);
    }

    //某用户退出房间
    private void exitRoom(){
      clearSit();
      int num = (int) user.getTableNum();
      int lorr = user.getLeftOrRight();
      user.setTableNum(-1);
      user.setLeftOrRight(0);
      String msg = "removeSit:"+num+","+lorr;
      sendOtherUser(msg);
      user.setReady(false);
    }

    //向某用户发送对手信息
    private void sendEnemyInfo(String msg) {
      try {
        User sendU = checkUser(Long.valueOf(msg));
        String myInfo = "setEnemy:"
                +user.getHead().toString()+","
                +user.getName()+","
                +user.getID();
        sendU.getDos().writeUTF(myInfo);
      }catch (Exception e){
       System.out.println("没有找到对应ID的用户");
      }
    }

    // 向所有人转发信息
    public void sendToAllUser(String msg) {
      try {
        Iterator<User> iterator = userList.iterator();
        while (iterator.hasNext()) {
          iterator.next().getDos().writeUTF(msg);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    //向除自己以外的玩家发送信息
    public void sendOtherUser(String msg){
      try {
        for (User uu:userList) {
          if(uu.getID()!=user.getID()){
            uu.getDos().writeUTF(msg);
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    //根据ID查找对应用户
    public User checkUser(Long ID){
      for (User u:userList) {
        if(u.getID()==ID){
          return u;
        }
      }
      return null;
    }

    //设置玩家信息
    public void setUserMsg(String msg){
      String userInfo[] = msg.split(",");
      String name = userInfo[0];
      String headImgPath = userInfo[1];
      System.out.println("name: "+name);
      System.out.println("headImgPath: "+headImgPath);
      user.setHead(new ImageIcon(headImgPath));
      user.setName(name);
    }

    //处理接收到的座位请求，并发送结果
    public void setUserSitInfo(String msg){
      String info[] = msg.split(",");
      String num = info[0];
      String leftOrRight = info[1];
      int tNum = new Integer(num);
      int lOr = new Integer(leftOrRight);
      if(checkUserTable(tNum,lOr)){
        checkEnemyID(tNum);
        long lastTableNum = -1; //上次，桌子编号
        int lastsitLorR = 0;        //上次，左还是右
        addPlayerToSit(tNum,lOr);
        if(user.getTableNum()!= -1){    //判断之前是否有座位
          lastTableNum = user.getTableNum();
          lastsitLorR = user.getLeftOrRight();
        }
        user.setTableNum(tNum);
        user.setLeftOrRight(lOr);
        sendToThisUser("setSit:"+
                "SUCCESS"+","
                +user.getHead()+","
                +lastTableNum+","
                +String.valueOf(tNum)+","
                +lastsitLorR+","
                +String.valueOf(leftOrRight));
        sendOtherUser("setOtherSit:"
                +user.getHead()+","
                +user.getName()+","
                +user.getID()+","
                +lastTableNum+","
                +String.valueOf(tNum)+","
                +lastsitLorR+","
                +String.valueOf(leftOrRight));
        //头像+姓名+ID+上次落座桌子编号+新桌子编号+上一次左边还是右边+凳子左或者右
      }else{
        sendToThisUser("setSit:"+"FAIL");
      }
    }

    //检查对手ID并互相设置绑定
    private void checkEnemyID(int tnum) {
      for (User uu:userList) {
        if(uu.getTableNum()==tnum)
          user.setEnemyId(uu.getID());
          uu.setEnemyId(user.getID());
      }
    }

    //判断桌位上是否有人
    public Boolean checkUserTable(int num,int leftOrRight){
      table t = tableList.get(num-1);
      if(leftOrRight==1) {
        if (!t.getSitL()){
          //清理座位
          clearSit();
          return true;
        }
      }
      else if(leftOrRight==2) {
        if (!t.getSitR()) {
          clearSit();
          return true;
        }
      }
      return false;         //座位上有人，设置失败
    }

    //添加座位上的玩家
    public void addPlayerToSit(int num,int leftOrRight){
      if(leftOrRight==1) {
        tableList.get(num-1).setSitL(true);
      }
      if(leftOrRight==2){
        tableList.get(num-1).setSitR(true);
      }
    }

    //向当前用户发送请求
    public void sendToThisUser(String msg){
      try {
        user.getDos().writeUTF(msg);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    //清除玩家之前的座位被占用的信息,并通知大厅用户
    public void clearSit() {
      if (user.getTableNum() != -1)    //判断之前是否有座位
      {
        int sLoR = user.getLeftOrRight();
        if (sLoR == 1) {
          tableList.get((int) user.getTableNum()-1).setSitL(false);
        } else if (sLoR == 2) {
          tableList.get((int) user.getTableNum()-1).setSitR(false);
        }
        long id = user.getEnemyId();
        if(id!=-1) {
          User enemy = checkUser(id);
          enemy.setEnemyId(-1);
        }
      }
    }

    //用户离线后的收尾工作，服务端存储信息删除------调用此方法建议放于最后调用！！！
    public void clearRubbish(){
      clearSit();
      userList.removeElement(user);
    }

    //用户离开大厅        或者    用户意外断开连接或者强制退出
    private void exitGame(){
      if(user.getEnemyId()!=-1) {         //有对手的话就和对手说再见      ------!!!一定要先说再见啊！！！！
        sayBay(String.valueOf(user.getEnemyId()));
      }
      exitRoom();
      clearRubbish();
    }
  }
}
