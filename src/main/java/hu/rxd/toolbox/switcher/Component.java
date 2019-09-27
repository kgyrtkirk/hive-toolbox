package hu.rxd.toolbox.switcher;

import java.util.Arrays;

public enum Component {
  hive(new HiveComponent()), hadoop(new HadoopComponent()), tez(new TezComponent()),;

  private IComponent component;

  private Component(IComponent component) {
    this.component = component;
  }

  public static Component valueOf1(String string) {
    try {
      return valueOf(string);
    } catch (Exception e) {
      throw new RuntimeException(string + " is not a valid component name; try: " + Arrays.toString(values()), e);
    }
  }

  public IComponent get() {
    return component;
  }
}