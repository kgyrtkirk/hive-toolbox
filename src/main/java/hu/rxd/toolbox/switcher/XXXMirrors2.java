package hu.rxd.toolbox.switcher;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hu.rxd.toolbox.qtest.diff.CachedURL;

public class XXXMirrors2 implements Mirrors {

  static Logger LOG = LoggerFactory.getLogger(XXXMirrors2.class);

  @Override
  public String getComponentVersion(Version version, Component c) throws Exception {
    String artifacts=
    String.format(
        "http://cloudera-build-us-west-1.vpc.cloudera.com/s3/build/%s/cdh/7.x/redhat7/yum/artifacts.txt",
        version.stackVersion);
    return determineComponentVerFromArtifactsTxt(artifacts, version, c);

  }

  static String determineComponentVerFromArtifactsTxt(String artifacts, Version v, Component c)
      throws Exception, IOException {
    Path path = new CachedURL(new URL(artifacts)).getFile().toPath();
    String versionMatchingPattern = String.format("tars/%s/%s-(.*)-source.tar.gz", c, c);
    Set<String> matches = Files.lines(path).filter(
        s -> s.matches(versionMatchingPattern)
    ).collect(Collectors.toSet());

    if (matches.size() != 1) {
      throw new RuntimeException("Expected to match 1 file; found: " + matches.toString());
    }
    String m = matches.iterator().next();
    Matcher match = Pattern.compile(versionMatchingPattern).matcher(m);
    if(!match.find()) { 
      throw new RuntimeException("no match?!");
    }
    String version = match.group(1);
    LOG.info("Version of " + c + " for " + v.getVerStr() + " is " + version);
    return version;

  }

}
