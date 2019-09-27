package hu.rxd.toolbox;

import java.net.URL;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import hu.rxd.toolbox.switcher.Component;
import hu.rxd.toolbox.switcher.Version;

public class HiveDevBoxSwitcher {

  private static List<? extends URL> getApacheMirrorPaths(String path) throws Exception {
    String apache_mirror = "http://xenia.sote.hu/ftp/mirrors/www.apache.org/";
    String archive_mirror = "https://archive.apache.org/dist/";
    return Lists.newArrayList(
        new URL(apache_mirror + path),
        new URL(archive_mirror + path));
  }


  public static void main(String[] args) throws Exception {
    Component c = Component.valueOf1(args[0]);
    Version version = new Version(args[1]);

    c.get().switchTo(version);
  }

}
