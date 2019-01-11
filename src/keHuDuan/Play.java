package keHuDuan;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author fantomboss
 * @date 2018/12/27-15:09
 */
public class Play extends JPanel {

  JButton exitRoom,start,peace,giveUp,sit;
  JPanel buttomJP;
  QiPan qiPan;
  JLabel title;
  String nullPepole = "resPackage/res/img/noone.gif";    //无玩家

  /**
   * 对战区
   * @param num   房间编号
   * @param gamehall  游戏大厅信息
   * @param sit      游戏大厅中的位置
   */
  Play(int num,GameHall gamehall,JButton sit,Room r){

    //初始化对战布局
    exitRoom = new JButton("退出房间");
    start = new JButton("开始");
    peace = new JButton("求和");peace.setEnabled(false);
    giveUp = new JButton("认输");giveUp.setEnabled(false);
    title = new JLabel("<---------五子棋对战-"+String.valueOf(num)+"-桌--------->");
    qiPan = new QiPan(r);

    //添加底部按钮
    buttomJP = new JPanel(new FlowLayout());
    buttomJP.add(exitRoom);
    buttomJP.add(start);
    buttomJP.add(peace);
    buttomJP.add(giveUp);

    //绑定监听事件
          //求合
    peace.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(e.getSource()==peace)
        gamehall.c.sendMessage("askPeace:"+r.getEnemyPlayer().getID());
      }
    });
        //认输
    giveUp.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(e.getSource()==giveUp)
        gamehall.c.sendMessage("askGiveUp:"+r.getEnemyPlayer().getID());
      }
    });
        //退出房间
    exitRoom.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (e.getSource() == exitRoom) {
          if (!qiPan.start) {
            try {
              sit.setIcon(new ImageIcon(ImageIO.read(new File(nullPepole))));
            } catch (IOException e1) {
              e1.printStackTrace();
            }
            gamehall.deleHall();
            gamehall.c.exitRoom();
            if(r.getEnemyPlayer().getID()!=-1) {
              gamehall.c.sayByeToEnemy(r.getEnemyPlayer().getID());
            }
          }else{
            JOptionPane.showMessageDialog(qiPan,"对战中，不得随意退出","ERROR_MESSAGE",JOptionPane.WARNING_MESSAGE);
          }
        }
      }
    });
        //开始
    start.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if(e.getSource()==start){
          Long id = gamehall.getPlayRoom().getEnemyPlayer().getID();
          if(id==-1)
            gamehall.getC().sendGetReadyToHall();
          else
            gamehall.getC().sendGetReadyToEnemy(id);
          qiPan.reset();
          start.setEnabled(false);
        }
      }
    });

    //添加组件到布局
    setLayout(new BorderLayout());
    add(BorderLayout.NORTH,title);
    add(BorderLayout.SOUTH,buttomJP);
    add(BorderLayout.CENTER,qiPan);
  }

  //-------getterAndSetter--------//
  public QiPan getQiPan() {
    return qiPan;
  }

  public JButton getStart() {
    return start;
  }
}
