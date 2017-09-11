package toolbox;

import java.io.InputStream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class JunitReader {

  public static JunitReport parse(InputStream resourceAsStream) throws Exception {
    ObjectMapper om =new XmlMapper();
    return om.readValue(resourceAsStream, JunitReport.class);
  }

}
