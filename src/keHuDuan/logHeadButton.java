package keHuDuan;

import javax.swing.*;

/**
 * @author fantomboss
 * @date 2019/1/3-19:37
 */
public class logHeadButton extends JButton {
  int num;

  public logHeadButton(Icon icon) {
    super(icon);
  }

  public int getNum() {
    return num;
  }

  public void setNum(int num) {
    this.num = num;
  }
}
