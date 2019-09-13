//package hu.rxd.toolbox;
//
//import java.net.URL;
//import java.util.Arrays;
//
//import com.google.common.collect.Lists;
//
//public class HiveDevBox {
//
//  enum Component {
//    hive, hadoop, tez,;
//
//    public static Component valueOf1(String string) {
//      try {
//        return valueOf(string);
//      } catch (Exception e) {
//        throw new RuntimeException(string + " is not a valid component name; try: " + Arrays.toString(values()), e);
//      }
//    }
//  }
//
//  public static void main(String[] args) {
//    Component c = Component.valueOf1(args[0]);
//  }
//
//  public HiveDevBox() {
//    loadConfig();
//  }
//
//  private void loadConfig() {
//    addHives();
//  }
//
//  private void addHives() {
//    String hiveVer = "3.1.1";
//    addOption(Component.hive, hiveVer, new DownloadAndExtract(
//        ApacheArchive()
//
//    ));
//  }
//
//  //  private Iterable<URL> ApacheArchive() {
//  //
//  //    return Lists.newArrayList(new URL("http://archive.apache.org/dist/hive/hive-VER/apache-hive-VER-bin.tar.gz"));
//  //  }
//}
