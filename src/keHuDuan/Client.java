package keHuDuan;

import TablePane.table;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;

/**
 * @author fantomboss
 * @date 2018/12/28-19:44
 */
public class Client extends Thread {

  private Socket socket = null;
  private DataInputStream dis = null;
  private DataOutputStream dos = null;
  private boolean run = true;
  private GameHall hall;

  //尝试连接
  public boolean connect(String address) {
    try {
      socket = new Socket(address, 1314);
      dis = new DataInputStream(socket.getInputStream());
      dos = new DataOutputStream(socket.getOutputStream());
      this.start();
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  //发送信息
  public void sendMessage(String msg) {
    System.out.println("发送消息：----->" + msg);
    try {
      dos.writeUTF(msg);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  // 接收消息
  public void run() {
    String recvMsg = null;
    String commandMsg = null;
    int pos;
    try {
      while (run) {
        recvMsg = dis.readUTF();
        //处理信息
        try {
          String station = recvMsg.substring(0, recvMsg.indexOf(":"));         //获取状态标识
          String msg = recvMsg.substring(recvMsg.indexOf(":")+1, recvMsg.length()); //获取内容
          System.out.println( "获取到的标识：" + station);
          System.out.println("接收到的信息："+msg);
          if (station.equals("all")) {hall.setHallTalk(msg);}//设置大厅信息
          if(station.equals("setSit")){setUserSit(msg);}      //设置玩家自己入座
          if(station.equals("setOtherSit")){setOhterUserSit(msg);} //设置其他玩家入座
          if(station.equals("setEnemy")){setEnemy(msg);}//设置对手信息
          if(station.equals("removeSit")){removeSit(msg);} //清除座位
          if(station.equals("bye")){clearEnemy();}   //设置对手信息为空
          if(station.equals("roomTalk")){setRoomTalk(msg);}//设置房间对话内容
          if(station.equals("setReadyAtHall")){setReadyAtHall(msg);} //在大厅中设置玩家进入准备状态
          if(station.equals("setReadyAtRoom")){setReadyAtRoom();}//在房间中设置对手准备信息
          if(station.equals("first")){setGameFirst();}//房间游戏开始，我方为先手（黑棋）
          if(station.equals("second")){setGameSecond();}//房间游戏开始，我方为后手（白棋）
          if(station.equals("yourTurn")){setMyTurn(msg);} //轮到我方下棋
          if(station.equals("enemyVictory")){setEnemyVictory();}//对手胜利
          if(station.equals("sendHallInfo")){setHalInfo(msg);}//设置大厅中玩家的信息
          if(station.equals("askGiveUp")){askGiveUp();}//询问是否让对手认输
          if(station.equals("askPeace")){askPeace();}//询问是否和对手平局
          if(station.equals("NO")){getNo();}//对手不同意认输或者和局的请求
          if(station.equals("peace")){hall.setPeace();}//设置平局
          }catch(Exception e){
            System.out.println("异常攻击信号：" + recvMsg);
            e.printStackTrace();
          }
        }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  //对手不同意认输或者和局的请求
  private void getNo() {
    JOptionPane.showMessageDialog(null,"对手不同意哦~~","来自对手的回答",JOptionPane.WARNING_MESSAGE);
  }

  //询问是否让对手认输
  private void askGiveUp() {
    Object[] options = {" 确定 "," 取消 "};
    int response=JOptionPane.showOptionDialog(null, "对手请求认输，是否同意", "对手请求",JOptionPane.YES_OPTION,
            JOptionPane.NO_OPTION,null, options, options[0]);
    if(response==0){
      hall.getPlayRoom().getQp().getQiPan().sendVictoryToEnemy();
    } else if(response==1) {
      sendMessage("NO:"+hall.getPlayRoom().getEnemyPlayer().getID());
    }
  }

  //询问是否同意和局
  private void askPeace(){
    Object[] options = {" 确定 "," 取消 t"};
    int response=JOptionPane.showOptionDialog(null, "对手请求和局，是否同意", "对手请求",JOptionPane.YES_OPTION,
            JOptionPane.NO_OPTION,null, options, options[0]);
    if(response==0){
      hall.setPeace();
      sendMessage("peace:"+hall.getPlayRoom().getEnemyPlayer().getID());
    } else if(response==1) {
      sendMessage("NO:"+hall.getPlayRoom().getEnemyPlayer().getID());
    }
  }

  //设置大厅信息
  private void setHalInfo(String msg){
    String info[] = msg.split(",");
    //桌号，左右，是否准备，姓名，ID，头像
    int tableNum = Integer.valueOf(info[0]);
    int leftOrRight = Integer.valueOf(info[1]);
    Boolean ready = Boolean.valueOf(info[2]);
    String name = info[3];
    Long id = Long.valueOf(info[4]);
    Icon head = new ImageIcon();
    try {
      head = new ImageIcon(ImageIO.read(new File(info[5])));
    } catch (IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null,"设置大厅玩家头像时出错"+info[5],"错误",0);
    }
    table tt = hall.tableList.get(tableNum-1);
    CUser ccc = new CUser(); JButton sit = new JButton();
    if(leftOrRight==1){
      ccc = tt.getLcUser();
      sit = tt.getJb1();
    }else if(leftOrRight==2){
      ccc = tt.getRcuser();
      sit = tt.getJb2();
    }
    ccc.setReady(ready);
    ccc.setName(name);
    ccc.setID(id);
    ccc.setHead(head);
    sit.setSize(32,32);
    sit.setIcon(head);
  }

  //获取大厅信息
  public void getHallInfo(){
    sendMessage("getHallInfo:");
  }

  //对手胜利
  private void setEnemyVictory() {
    hall.getPlayRoom().getQp().getQiPan().setEnd("对手太强了！！");
    hall.getPlayRoom().tts.cancel();
    hall.getPlayRoom().tt.cancel();
    hall.getPlayRoom().getQp().peace.setEnabled(false);
    hall.getPlayRoom().getQp().giveUp.setEnabled(false);
  }

  //向对手发送我方胜利消息
  public void sendVictory(Long id) {
    String station="victory:";
    String ID = String.valueOf(id);
    sendMessage(station+ID);
  }

  //轮到我方下棋
  private void setMyTurn(String msg) {
    //!!!建议使用此分割字符串的方法，否则项目导出不能使用！！！！！！！
    String info[] = msg.split(",");
    int xx = Integer.valueOf(info[0]);
    int yy = Integer.valueOf(info[1]);
    hall.getPlayRoom().getQp().getQiPan().setEnemyStep(xx,yy);
    hall.getPlayRoom().tts.cancel();
    hall.getPlayRoom().enemyTime.setText("我下完啦，到你了");
    hall.getPlayRoom().getMe().setEnable(true);   //设置下棋权（true 为可以下棋，false 为不能下棋）
    hall.getPlayRoom().setMtime(60);              //设置下棋时间 60s
    hall.getPlayRoom().sartTime();                //开始下棋倒计时
  }

  //通知对手下棋
  public void enemyTime(Long id,int x,int y) {
    String station = "changeTurn:";
    String ID = String.valueOf(id);
    String xx = String.valueOf(x);
    String yy = String.valueOf(y);
    sendMessage(station+ID+","+xx+","+yy);
  }

  //房间游戏开始，我方为先手（黑棋）
  private void setGameFirst() {
    hall.getPlayRoom().getQp().getQiPan().setBorW(1); //设置棋子
    hall.getPlayRoom().getMe().setEnable(true);       //设置下棋权（true 为可以下棋，false 为不能下棋）
    hall.getPlayRoom().setMtime(60);                  //设置下棋时间 60s
    hall.getPlayRoom().sartTime();                    //开始下棋倒计时
    hall.getPlayRoom().getQp().getQiPan().setStart(true);
    hall.getPlayRoom().getQp().giveUp.setEnabled(true);
    hall.getPlayRoom().getQp().peace.setEnabled(true);
  }

  //房间游戏开始，我方为后手（白棋）
  private void setGameSecond() {
    hall.getPlayRoom().getQp().getQiPan().setBorW(2); //设置棋子
    hall.getPlayRoom().myTime.setText("等待对手下棋~");
    hall.getPlayRoom().setEtime(60);                //设置下棋时间 60s
    hall.getPlayRoom().sartEtime();                 //开始下棋倒计时
    hall.getPlayRoom().getQp().getQiPan().setStart(true);
    hall.getPlayRoom().getQp().giveUp.setEnabled(true);
    hall.getPlayRoom().getQp().peace.setEnabled(true);
  }

  //在房间中设置对手准备信息
  private void setReadyAtRoom() {
    hall.getPlayRoom().enemyTime.setText("我准备好了~");
  }

  //在大厅中设置玩家进入准备状态
  private void setReadyAtHall(String msg) {
    //!!!建议使用此分割字符串的方法，否则项目导出不能使用！！！！！！！
    String info[] = msg.split(",");
    //桌号+座位
    int tnum = new Integer(info[0]);
    int sit = new Integer(info[1]);
    if(sit==1)
      hall.tableList.get(tnum-1).getLcUser().setReady(true);
    else if(sit==2)
      hall.tableList.get(tnum-1).getRcuser().setReady(true);
  }

  //设置房间对话内容
  private void setRoomTalk(String msg) {
    //!!!建议使用此分割字符串的方法，否则项目导出不能使用！！！！！！！
    String info[] = msg.split(",");
    String name = info[0];
    String talk = info[1];
    String talkNode = name+": "+talk;
    hall.getPlayRoom().getTalkP().setTalkMsg(talkNode);
  }

  //清空对手信息(自己房间)
  public void clearEnemy(){
    System.out.println("clearUser");
    hall.getPlayRoom().getQp().getQiPan().setStart(false);    //确保游戏战是停止或者关闭的
    hall.getPlayRoom().getQp().start.setEnabled(true);
    hall.getPlayRoom().getQp().peace.setEnabled(false);
    hall.getPlayRoom().getQp().giveUp.setEnabled(false);
    hall.getPlayRoom().tts.cancel();
    hall.getPlayRoom().tt.cancel();
    hall.getPlayRoom().getMe().setEnable(false);
    hall.lastplayRoom.setEnemyPlayer(new CUser());
  }

  //添加对手信息(自己房间)
  public void setEnemy(String msg){
    //!!!建议使用此分割字符串的方法，否则项目导出不能使用！！！！！！！
    String inFo[] = msg.split(",");
    String headImgPath = inFo[0];
    String name = inFo[1];
    String Id = inFo[2];
    CUser cc = new CUser();
    ImageIcon head = new ImageIcon();
    try {
      head = new ImageIcon(ImageIO.read(new File(headImgPath)));
    } catch (IOException e) {
      e.printStackTrace();
    }
    cc.setHead(head);
    cc.setName(name);
    cc.setID(new Long(Id));
    hall.addPlayerToRoom(cc);
  }

  //设置大厅中座位为空（大厅）
  private void removeSit(String msg) {
    String info[] = msg.split(",");
    int num = new Integer(info[0]);
    int lorr = new Integer(info[1]);
    hall.removeLastSit(num,lorr);
  }
  //发送消息请求卓子
  public void checkUserSit(int num,int leftOrRight){
    String msg = String.valueOf(num)+","+String.valueOf(leftOrRight);
    sendMessage("sit:"+msg);
  }

  //发送个人信息到指定玩家
  public void sendInfoToEnemy(Long id){
    String sendInfo = String.valueOf(id);
    sendMessage("inRoom:"+sendInfo);
  }

  //1、向某用户说再见，让对方清理房间座位（对方房间）
  public void sayByeToEnemy(long id) {
    String send = "sayBye:"+String.valueOf(id);
    sendMessage(send);
  }

  //2、退出当前房间,清理大厅座位（大厅）
  public void exitRoom() {
    String send = "exitRoom:";
    sendMessage(send);
  }

  //设置用户自己桌位（大厅）
  public void setUserSit(String msg){
    //!!!建议使用此分割字符串的方法，否则项目导出不能使用！！！！！！！
    String info[] = msg.split(",");     //头像+上次落座桌子编号+新桌子编号+上一次左边还是右边+凳子左或者右
    if(info[0].equals("FAIL")){
      JOptionPane.showMessageDialog(null,"入座失败","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
    }else {
      Icon head = null;
      try {
        head = new ImageIcon(ImageIO.read(new File(info[1])));
      } catch (IOException e) {
        e.printStackTrace();
      }
      int lastNum = new Integer(info[2]);
      int num = new Integer(info[3]);
      int lastLorR = new Integer(info[4]);
      int leftOrRight = new Integer(info[5]);
      if (info[0].equals("SUCCESS")) {
        table t = hall.tableList.get(num - 1);
          hall.removeLastSit(lastNum, lastLorR);
          t.setUserSit(hall, num, head, leftOrRight);
      }
    }
  }

  //设置其他用户的桌位（大厅）
  public void setOhterUserSit(String msg){
    //!!!建议使用此分割字符串的方法，否则项目导出不能使用！！！！！！！
    String info[] = msg.split(",");     //头像+上次落座桌子编号+新桌子编号+上一次左边还是右边+凳子左或者右
    Icon head = new ImageIcon();
    try {
      head = new ImageIcon(ImageIO.read(new File(info[0])));
    } catch (IOException e) {
      e.printStackTrace();
    }
    String name = info[1];
    Long ID = new Long(info[2]);
    int lastNum = new Integer(info[3]);
    int num = new Integer(info[4]);
    int lastLorR = new Integer(info[5]);
    int leftOrRight = new Integer(info[6]);
      table t = hall.tableList.get(num-1);
        hall.removeLastSit(lastNum,lastLorR);
        t.setHallUser(head,ID,name,leftOrRight);
  }

  //向对手发送聊天消息
  public void talkToEnemy(String msg) {
    CUser enemy = hall.getPlayRoom().getEnemyPlayer();
    CUser me = hall.getCume();
    String station = "talkToEnemy:";
    String sends = station+enemy.getID()+","+me.getName()+","+msg;
    sendMessage(sends);
                      //ID+发送者姓名+要发送的信息
  }

  //退出游戏大厅
  public void sentExit() {
    sendMessage("exitHall:");
  }

  //在大厅中设置准备信息
  public void sendGetReadyToHall() {
    sendMessage("setReadyAtHall:");
  }

  //在对手的房间中设置准备信息
  public void sendGetReadyToEnemy(Long id) {
    sendMessage("setReadyAtRoom:"+String.valueOf(id));
  }

  //---------getterAndSetter-------------//

  public void setHall(GameHall hall){
    this.hall = hall;
  }

  private void setRun(boolean run) {
    this.run = run;
  }

  public void setNew() {
    this.setRun(false);
  }
}
