package hu.rxd.toolbox.switcher;

import java.net.URL;

interface Mirror {

  URL getFor(Component component, String componentVersion) throws Exception;

}