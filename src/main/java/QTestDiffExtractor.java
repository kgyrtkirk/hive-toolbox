
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class QTestDiffExtractor {

  private String[] allLines;
  private String qOutName;
  private int diffOffset;

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
    if (off < 0) {
      throw new RuntimeException("diffline?: " + lines[diffOffset]);
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

  public String getQFile() {
    return qOutName;
  }

}
