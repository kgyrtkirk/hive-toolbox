package hu.rxd.toolbox.switcher;

import java.net.URL;

interface Mirror {

  URL getFor(Component tez, String componentVersion) throws Exception;

}