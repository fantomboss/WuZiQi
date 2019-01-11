package keHuDuan;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author fantomboss
 * @date 2018/12/27-15:12
 */
public class TalkPane extends JSplitPane {
  JScrollPane talkPP;
  JTextField sendText;
  JButton send;
  JTextArea talkNode;
  JPanel jp;

  public TalkPane(GameHall gameHall){
    //初始化聊天布局
    setLayout(new BorderLayout());
    jp = new JPanel(new BorderLayout());
    send = new JButton("发送");
    talkNode = new JTextArea();
    sendText = new JTextField();
    talkPP = new JScrollPane(talkNode);
    talkPP.setVerticalScrollBarPolicy(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);   //设置滚动条总是出现

    //添加组件到布局
    setOrientation(VERTICAL_SPLIT);
    setTopComponent(talkPP);
    setBottomComponent(jp);
    setDividerLocation(410);

    jp.add(BorderLayout.EAST,send);
    jp.add(BorderLayout.CENTER,sendText);

    talkNode.setLineWrap(true);     //设置聊天记录自动换行

    //绑定监听事件
    send.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        String msg = sendText.getText();
        if(!msg.equals("")){
          gameHall.getC().talkToEnemy(msg);
          talkNode.append(gameHall.getCume().getName()+": "+msg+"\r\n");
          sendText.setText("");
        }else{
          JOptionPane.showMessageDialog(gameHall,"发送内容不能为空","ERROR_MESSAGE",JOptionPane.ERROR_MESSAGE);
        }
      }
    });
  }

  public void setTalkMsg(String msg){
    talkNode.append(msg+"\r\n");
  }
}
