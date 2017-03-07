package org.base.jetty;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Set;

import scala.collection.Seq;
import scala.collection.JavaConversions;
import scala.collection.JavaConverters;

import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import org.junit.Test;
import org.base.jetty.servlets;
import org.base.jetty.ServerInfo;
import org.base.jetty.utils;
import org.base.jetty.SSLOptions;
import org.eclipse.jetty.servlet.ServletContextHandler;


/**
 * Unit test for simple App.
 */
public class AppTest 
     implements Serializable
{
	ServletContextHandler statichandler;
	ServerInfo servinfo;
	SSLOptions sslOptions;
	  @Before
	  public void setUp() {
		  statichandler = servlets.createStaticHandler(".","/file/");
		  sslOptions = SSLOptions.self();
		  servinfo = utils.startJettyServer("127.0.0.1", 8989, sslOptions,java.util.Arrays.asList(statichandler), "localhost");
		  try {
			servinfo.server().join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }
 
}
