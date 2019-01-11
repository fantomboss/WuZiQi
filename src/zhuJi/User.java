package zhuJi;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * @author fantomboss
 * @date 2018/12/28-10:00
 */

public class User {
  private Icon head;
  private String name;
  private DataInputStream dis;
  private DataOutputStream dos;
  private Long ID;                //用户ID
  private long tableNum = -1;     //现在卓子编号
  private int leftOrRight;        //左边或者右边(1,2)
  private Boolean ready = false;          //是否准备
  private long enemyId = -1;                   //!!!!对手ID，仅在双方入座后，且离开房间,和对战开始时设置ID，和对战中一方意外退出的情况下使用!!!!

  public long getEnemyId() {
    return enemyId;
  }

  public void setEnemyId(long enemyId) {
    this.enemyId = enemyId;
  }

  public Icon getHead() {
    return head;
  }

  public void setHead(Icon head) {
    this.head = head;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public User(Icon head, String name) {
    this.head = head;
    this.name = name;
  }

  public DataInputStream getDis() {
    return dis;
  }

  public void setDis(DataInputStream dis) {
    this.dis = dis;
  }

  public DataOutputStream getDos() {
    return dos;
  }

  public void setDos(DataOutputStream dos) {
    this.dos = dos;
  }

  public Long getID() {
    return ID;
  }

  public void setID(Long ID) {
    this.ID = ID;
  }

  public long getTableNum() {
    return tableNum;
  }

  public void setTableNum(long tableNum) {
    this.tableNum = tableNum;
  }

  public int getLeftOrRight() {
    return this.leftOrRight;
  }

  public void setLeftOrRight(int leftOrRight) {
    this.leftOrRight = leftOrRight;
  }

  public Boolean getReady() {
    return ready;
  }

  public void setReady(Boolean ready) {
    this.ready = ready;
  }

  @Override
  public String toString() {
    return "User{" +
            "head=" + head +
            ", name='" + name + '\'' +
            ", ID=" + ID +
            ", tableNum=" + tableNum +
            ", leftOrRight=" + leftOrRight +
            '}';
  }


  public User(){}

  public User(DataInputStream dis, DataOutputStream dos, Long ID) {
    this.dis = dis;
    this.dos = dos;
    this.ID = ID;
  }
}
