package hu.rxd.toolbox;

import hu.rxd.toolbox.switcher.Component;
import hu.rxd.toolbox.switcher.Version;

public class HiveDevBoxSwitcher {

  public static void main(String[] args) throws Exception {
    Component c = Component.valueOf1(args[0]);
    Version version = new Version(args[1]);

    c.get().switchTo(version);
  }

}
