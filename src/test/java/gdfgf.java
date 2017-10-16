import org.junit.Test;

import hu.rxd.lexirex.RegexRangeBuilder;

public class gdfgf {

  @Test
  public void fdfd() {
    String a = RegexRangeBuilder.fromInclusive("asd").toExclusive("qwe").toRegex();
    System.out.println(a);
  }

}
