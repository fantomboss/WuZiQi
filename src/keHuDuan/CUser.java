package keHuDuan;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.geom.FlatteningPathIterator;
import java.io.File;
import java.io.IOException;

/**
 * @author fantomboss
 * @date 2018/12/30-8:21
 */
public class CUser {
  String nullPepole = "resPackage/res/img/noone.gif";
  private Icon head;

  {       //!!!!!!!!!!!!!!!!!!!!
    try {
      head = new ImageIcon(ImageIO.read(new File(nullPepole)));
    } catch (IOException e) {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null,"玩家初始化头像缺失");
    }
  }

  private String name = "";
  private long ID = -1;
  private Boolean ready = false;
  private Boolean enable = false;

  public CUser() {

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public long getID() {
    return ID;
  }

  public void setID(long ID) {
    this.ID = ID;
  }

  public Icon getHead() {
    return head;
  }

  public void setHead(Icon head) {
    this.head = head;
  }

  public Boolean getReady() {
    return ready;
  }

  public void setReady(Boolean ready) {
    this.ready = ready;
  }

  public Boolean getEnable() {
    return enable;
  }

  public void setEnable(Boolean enable) {
    this.enable = enable;
  }

  public CUser(Icon head, String name){
    this.head = head;
    this.name = name;
  }

  @Override
  public String toString() {
    return "CUser{" +
            "head=" + head +
            ", name='" + name + '\'' +
            ", ID=" + ID +
            '}';
  }
}
