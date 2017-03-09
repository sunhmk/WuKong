package org.base.jetty;

import java.io.Serializable;

import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Unit test for simple App.
 */
public class JavaTest implements Serializable {
  /**
	 * 
	 */
  private static final long serialVersionUID = 1L;
  ServletContextHandler statichandler;
  ServerInfo servinfo;
  SSLOptions sslOptions;

  @Before
  public void setUp() {
    statichandler = Servlets.createStaticHandler(".", "/file/");
    sslOptions = SSLOptions.self();
    
  }
  
  /**
   * start static handler
   */
  @Test
  public void startstaticJetty()
  {
    servinfo = Utils.startJettyServer("127.0.0.1", 8989, sslOptions,
              java.util.Arrays.asList(statichandler), "localhost");
        try {
			servinfo.server().stop();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
  
  @After
  public void tearDown() {
    servinfo.stop();
  }
}
