package hu.rxd.toolbox.switcher;

public interface Mirrors {

  String getComponentVersion(Version version, Component c) throws Exception;

  /** Supposed to decode the stack version
   * 
   * example:
   * for HDP this is by adding the build number 3.1.4.8 to 3.1.4.8-1
   * for CDP this is by acquiring the build id
   */
  public String decodeStackVersion(String version);
}
