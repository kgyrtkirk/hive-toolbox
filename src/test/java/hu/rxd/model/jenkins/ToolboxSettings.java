/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package hu.rxd.model.jenkins;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ToolboxSettings {

  public static class JenkinsSettings {
    public String username;
    public String password;
  }

  public static class DataClass {
    public JenkinsSettings jenkins = new JenkinsSettings();
  }

  private static ToolboxSettings i;
  private DataClass dataClass;

  private ToolboxSettings(File configFile) {
    ObjectMapper om = new ObjectMapper(new YAMLFactory());
    try {
      dataClass = om.readValue(configFile, DataClass.class);
    } catch (Exception e) {
      throw new RuntimeException("failed to open/or read configuration: " + configFile, e);
    }
  }

  public static ToolboxSettings instance() {
    if(i==null) {
      File configFile = new File(System.getProperty("user.home"), ".config/asf_toolbox.yml");

      i = new ToolboxSettings(configFile);
    }
    return i;
  }

  public String getJenkinsUser() {
    return dataClass.jenkins.username;
  }

  public String getJenkinsPass() {
    return dataClass.jenkins.password;
  }

  public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
    ObjectMapper om = new ObjectMapper(new YAMLFactory());
    DataClass dc = new DataClass();
    dc.jenkins.username = "asd";
    om.writeValue(System.out, dc);
    System.out.println(instance());
  }

}
