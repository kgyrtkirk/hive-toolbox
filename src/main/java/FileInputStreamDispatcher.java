import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.function.Function;

public class FileInputStreamDispatcher implements IInputStreamDispatcher {

  private String[] args;

  public FileInputStreamDispatcher(String[] args) {
    this.args = args;
  }

  @Override
  public void visit(Function<InputStream, Void> function) {
    for (String string : args) {
      try {
        File f = new File(string);
        try (FileInputStream a = new FileInputStream(f)) {
          function.apply(a);
        }
      } catch (Exception e) {
        throw new RuntimeException("Error processing file: " + string, e);
      }
    }

  }

}
