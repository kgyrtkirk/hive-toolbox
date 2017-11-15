package hu.rxd.toolbox.qtest;
import java.io.InputStream;
import java.util.function.Function;

public interface IInputStreamDispatcher {

  void visit(Function<InputStream, Void> function) throws Exception;

}
