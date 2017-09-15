
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class QTestDiffExtractor {

  private String[] allLines;
  private String qOutName;
  private int diffOffset;
  private boolean reverse;

  public QTestDiffExtractor(String input) {

    if (input.contains("Output was too long and had to be truncated...")) {
      throw new RuntimeException("too-long");
    }

    // System.out.println(input);
    String[] lines = input.split("\\r?\\n");

    for (diffOffset = 0; diffOffset < lines.length; diffOffset++) {
      if (lines[diffOffset].startsWith("Running: diff"))
        break;
    }
    if (diffOffset >= lines.length) {
      throw new RuntimeException("not supported string format!");
    }

    String[] cmdParts = lines[diffOffset].split(" ");
    int off = cmdParts[3].indexOf("itests/");
    reverse=true;
    if (off < 0) {
      cmdParts=new String[]{cmdParts[0],cmdParts[1],cmdParts[2],cmdParts[4],cmdParts[3]};
      off = cmdParts[3].indexOf("itests/");
      reverse=false;
      if (off < 0) {
        throw new RuntimeException("diffline?: " + lines[diffOffset]);
      }
    }
    qOutName = cmdParts[4].substring(off);
    System.out.println(qOutName);
    allLines = lines;

  }

  public void writePatch(PrintStream patchFile) {
    for (int i = diffOffset + 1; i < allLines.length; i++) {
      patchFile.println(allLines[i]);
    }
  }

  public Iterable<String> getDiffIterable() {
    List<String> l = new ArrayList<>();
    for (int i = diffOffset + 1; i < allLines.length; i++) {
      l.add(allLines[i]);
    }
    return l;
  }
  public boolean isReverse() {
    return reverse;
  }

  public String getQFile() {
    return qOutName;
  }

  public boolean canPatch() {
    return getQFile().endsWith("q.out");
  }

}
