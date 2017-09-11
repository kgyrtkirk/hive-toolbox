package toolbox;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

public class JunitReport {

  public static class Property {
    public String name;
    public String value;
  }

  public static class Failure {

    @JsonProperty
    String message;
    @JsonProperty
    String type;
    @JacksonXmlText
    String value;

  }

  public static class TestCase {

    public String classname;
    //
    public String name;
    public double time;
    public Failure failure;
    @JacksonXmlElementWrapper(localName = "system-out")
    public String systemOut; 
    @JacksonXmlElementWrapper(localName = "system-err")
    public String systemErr; 
    
    String message;
    // public String value;
    // public String message;
    // public String type;
  }

  public String name;
  public double time;
  public int tests;
  public int errors;
  public int skipped;
  public int failures;
  public List<Property> properties;

  @JacksonXmlElementWrapper(localName = "testcase")
  // @JsonProperty("testcase")
  public List<TestCase> testcase = new ArrayList<>();

  @JsonSetter
  public void setTestcase(TestCase card) {
    this.testcase.add(card);
  }
}
