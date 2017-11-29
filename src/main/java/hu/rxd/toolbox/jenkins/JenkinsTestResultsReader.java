
package hu.rxd.toolbox.jenkins;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JenkinsTestResultsReader {

	public static TestResults parseTestResults(InputStream jsonStream)
			throws IOException, JsonParseException, JsonMappingException {
		ObjectMapper mapper = new ObjectMapper();
		TestResults results = mapper.readValue(jsonStream, TestResults.class);
		return results;
	}

	public static TestResults fromFile(File f) throws Exception {
		try (InputStream fis = new FileInputStream(f)) {
			return parseTestResults(fis);
		}
	}

	/**
	 * example buildURL: http://j1:8080/job/tmp_kx_2/lastCompletedBuild/
	 *
	 * @param buildURL
	 * @return
	 * @throws Exception
	 */
	public static TestResults fromJenkinsBuild(String buildURL) throws Exception {
		URL u = new URL(buildURL + "/testReport/api/json?pretty=true&tree=suites[cases[className,name,duration,status]]");
		try (InputStream jsonStream = u.openStream()) {
			return parseTestResults(jsonStream);
		}
	}

  public static void main(String[] args) throws Exception {
    String url = "https://builds.apache.org/job/PreCommit-HIVE-Build/8020/";
    fromJenkinsBuild(url);
  }


}
