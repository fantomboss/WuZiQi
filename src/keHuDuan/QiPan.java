package keHuDuan;

import sun.java2d.pipe.DrawImage;
import zhuJi.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Timer;

/**
 * @author fantomboss
 * @date 2018/12/27-15:38
 */
public class QiPan extends JPanel implements MouseListener {

  BufferedImage black,board,white;
  String boardPath = "resPackage/res/wuzi/board.gif";
  String blackPath = "resPackage/res/wuzi/baiqi.gif";
  String whitePath = "resPackage/res/wuzi/heiqi.gif";
  int qipan[][];
  int BorW;
  Room room;
  public Boolean start = false;
  AudioClip go,win;
  File gof = new File("resPackage/audio/go.wav");
  File winf = new File("resPackage/audio/jiang.wav");

  /**
   * 棋盘
   */
  QiPan(Room r){

    BorW = 0;
    room = r;
    //初始化棋盘布局
    qipan = new int[15][15];
    try {
      board = ImageIO.read(new File(boardPath));
      black = ImageIO.read(new File(blackPath));
      white = ImageIO.read(new File(whitePath));
    } catch (IOException e) {
      e.printStackTrace();
    }
    //添加组件到棋盘布局
    addMouseListener(this);
  }

  //重绘
  public void reset(){
    qipan = new int[15][15];
    repaint();
  }

  //重写重绘制方法
  @Override
  public void paintComponent(Graphics g){
    super.paintComponent(g);
    g.drawImage(board,80,80,this);
    for(int i = 0;i<15;i++){
      for(int j = 0;j<15;j++){
        if(qipan[i][j]==2) {
          g.drawImage(white,80+23+i*35-white.getWidth()/2,80+26+j*35-white.getHeight()/2,this);
        }
        else if(qipan[i][j]==1){
          g.drawImage(black,80+23+i*35-white.getWidth()/2,80+26+j*35-white.getHeight()/2,this);
        }
      }
    }

  }

  //发送我方胜利的消息
  public void sendVictoryToEnemy(){
    room.hall.getC().sendVictory(room.getEnemyPlayer().getID());
    room.hall.getPlayRoom().tt.cancel();
    room.hall.getPlayRoom().tts.cancel();
    room.getQp().peace.setEnabled(false);
    room.getQp().giveUp.setEnabled(false);
    System.out.println("123");
    setEnd("你胜利啦~^.^~");
  }

  //对战停止，msg表示对战停止的原因提示,fail,是否失败
  public void setEnd(String msg){
    JOptionPane.showMessageDialog(this,msg,"ERROR_MESSAGE",JOptionPane.WARNING_MESSAGE);
    CUser me =  room.hall.getCume();
    CUser enemyPlayer = room.hall.getPlayRoom().getEnemyPlayer();
    me.setReady(false);enemyPlayer.setReady(false);
    me.setEnable(false);enemyPlayer.setReady(false);
    room.hall.getPlayRoom().getQp().getStart().setEnabled(true);
    enemyPlayer.setReady(false);
    enemyPlayer.setEnable(false);
    start = false;
  }

  //判断是否胜利
  public void victroryOrFail(int Xi,int Yi){
    if(true){
      //判断横
      int xx = 0;
      for(int x=0;x<14;x++){
        if(qipan[x][Yi]==BorW){
          xx++;
          if(xx==5){ //是否集满5个
            playWin();
            sendVictoryToEnemy();
          }
        }
        else{xx=0;}
      }
      //判断竖
      int yy = 0;
      for(int y=0;y<14;y++){
        if(qipan[Xi][y]==BorW){
          yy++;
          if(yy==5) {  //是否集满5个
            playWin();
            sendVictoryToEnemy();
          }
        }
        else{yy=0;}
      }
      //判断上斜 (自下而上判断)     F(x)=>y-Yi=k(x-Xi),k=-1 && (-1<x<15,-1<y<15)
      //                              y=Yi+Xi-x;    min:y=Yi+Xi-14,max:y=Yi+Xi
      //                              x=Yi+Xi-y;    min:x=Yi+Xi-14,max:x=Yi+Xi
      int xy = 0;
      int Ymin,Xmin,Ymax,Xmax;
      Ymin = Xmin = Yi+Xi-14;
      Ymax = Xmax = Yi+Xi;
      //边界判断--->生成边界
      if(Ymin<0||Xmin<0){Ymin=Xmin=0;}if(Ymax>14||Xmax>14){Xmax=Ymax=14;}
      for(int x = Xmin,y=Ymax;x<Xmax&&y>Ymin; x++,y--){
        if(qipan[x][y]==BorW) {
          xy++;
          if(xy==5){
            playWin();
            sendVictoryToEnemy();
          }
        }
        else
          xy=0;
      }
      //判断下斜(自上由下判断)    ----优化上步算法（本质相同）同理 F(x)=>y-Yi=k(x-Yi)点斜式,y=Yi-Xi+x,x=y+Xi-Yi
      int yx = 0;
      int maxX = Xi-Yi+14,minX = Xi-Yi;
      int maxY = Yi-Xi+14,minY = Yi-Xi;
      if(minX<0){minX=0;}if(maxX>14){maxX=14;}
      if(minY<0){minY=0;}if(maxY>14){maxY=14;}
      for(int x=minX,y=minY;x<maxX&&y<maxY;x++,y++){
        if(qipan[x][y]==BorW) {
          yx++;
          if(yx==5){
            playWin();
            sendVictoryToEnemy();
          }
        }
        else
          yx=0;
      }
    }
  }

  //播放胜利音效
  public void playWin(){
    try {
      win = Applet.newAudioClip(winf.toURI().toURL());
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }
    win.play();
  }

  //接收到对手的走棋，处理并重新绘制棋盘
  public void setEnemyStep(int x,int y){
    if(x!=-1||y!=-1) {
      System.out.println("---getXY---");
      qipan[x][y] = 3 - BorW;                  // X=3-Y 得出对手棋子是黑棋还是白旗
      repaint();
    }
  }

  @Override
  public void mouseClicked(MouseEvent e) {
  }

  @Override     //当鼠标按下时
  public void mousePressed(MouseEvent e) {
    if(room.getMe().getEnable()) {
      int x, y = 0;
      x = (e.getX() - 80 - 23 + 18) / 36;
      y = (e.getY() - 80 - 26 + 18) / 36;
      if (x < 16 && x > -1 && y < 16 && y > -1) {
        if (qipan[x][y] == 0) {
          qipan[x][y] = BorW;
          System.out.println("BorW:"+BorW+" X: "+x+" Y: "+y);
          repaint();
          try {
            go = Applet.newAudioClip(gof.toURI().toURL());
          } catch (MalformedURLException e1) {
            e1.printStackTrace();
          }
          go.play();
          //一步走完-->轮换
          room.getTt().cancel();                //停止计时
          room.myTime.setText("轮到对手下棋");  //调整显示
          room.me.setEnable(false);             //设置棋权为false
          room.hall.getC().enemyTime(room.getEnemyPlayer().getID(),x,y);         //通知对手下棋
          room.setEtime(60);                     //对手开始计时
          room.sartEtime();
          victroryOrFail(x, y);
        }
      }
    }
  }

  @Override
  public void mouseReleased(MouseEvent e) {

  }

  @Override
  public void mouseEntered(MouseEvent e) {

  }

  @Override
  public void mouseExited(MouseEvent e) {

  }


  //-------getterAndSetter--------//
  public int getBorW() {
    return BorW;
  }

  public void setBorW(int borW) {
    BorW = borW;
  }

  public Boolean getStart() {
    return start;
  }

  public void setStart(Boolean start) {
    this.start = start;
  }
}
