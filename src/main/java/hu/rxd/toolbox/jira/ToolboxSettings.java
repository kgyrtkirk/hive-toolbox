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

package hu.rxd.toolbox.jira;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class ToolboxSettings {

  public static class JenkinsSettings {
    public String username;
    public String password;
  }

  public static class JiraSettings {
    public String userid;
    public String password;
    public Map<String, String> userEmailAddresses;
  }

  public static class DataClass {
    public JenkinsSettings jenkins = new JenkinsSettings();
    public JiraSettings jira = new JiraSettings();
  }

  private static ToolboxSettings i;
  private DataClass dataClass;
  private File configFile;

  private ToolboxSettings(File configFile) {
    this.configFile = configFile;
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

  public String getJiraUserId() {
    if (dataClass.jira.userid == null) {
      throw new RuntimeException("dataClass.jira.userid is unset");
    }
    return dataClass.jira.userid;
  }

  public String getJiraPassword() {
    if (dataClass.jira.password == null) {
      throw new RuntimeException("dataClass.jira.password is unset");
    }
    return dataClass.jira.password;
  }

  private Map<String, String> getUserEmailAddresses() {
    Map<String, String> ret = dataClass.jira.userEmailAddresses;
    if (ret == null) {
      System.err.println("dataClass.jira.userEmailAddresses is unset");
      ret = new HashMap<>();
    }
    return ret;
  }

  public String getEmailAddressesForJiraUser(String userName) {
    Map<String, String> m = getUserEmailAddresses();
    String email = m.get(userName);
    if (email == null) {
      throw new RuntimeException("can't get email for " + userName + "; try adding it to " + configFile);
    }
    return email;
  }

}
