package hu.rxd.model.junit;

import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class JunitReader {

  public static JunitReport parse(InputStream resourceAsStream) throws Exception {
    ObjectMapper om =new XmlMapper();
    om.disable(JsonParser.Feature.AUTO_CLOSE_SOURCE);
    return om.readValue(resourceAsStream, JunitReport.class);
  }

}
