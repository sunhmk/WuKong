package org.base.jetty

import java.util.Arrays
import java.util.Set

import org.scalatest.junit.JUnitSuite
import org.junit.{Test, Before, After}
import org.junit.Assert._
import org.eclipse.jetty.servlet.ServletContextHandler

import scala.collection.Seq



class ScalaTest extends JUnitSuite {
  @Before
  def startUp() {
  }
  
  /*
   * start one static 
   */
  @Test     
  def startStaticJetty() {
    var statichandler: ServletContextHandler = null
    var servinfo: ServerInfo = null
    var sslOptions: SSLOptions = null
    statichandler = Servlets.createStaticHandler(".", "/file/")
    sslOptions = SSLOptions.self
    servinfo = Utils.startJettyServer("127.0.0.1", 8989, sslOptions, java.util.Arrays.asList(statichandler), "localhost")
    servinfo.server.stop()
    // servinfo.server.join()
  }
  
  @Test
  def startStatic2Jetty() {
    var statichandler: ServletContextHandler = null
    var statichandler1: ServletContextHandler = null
    var servinfo: ServerInfo = null
    var sslOptions: SSLOptions = null
    statichandler = Servlets.createStaticHandler(".", "/file/")
    statichandler1 = Servlets.createStaticHandler(".", "/file1/")
    sslOptions = SSLOptions.self
    servinfo = Utils.startJettyServer("127.0.0.1", 8990, sslOptions, java.util.Arrays.asList(statichandler, statichandler1), "localhost")
    servinfo.server.stop()
    // servinfo.server.join()
  }
  /*
   * localhost:8991/file2 redirect to localhost:8991/file
   */
  @Test     
  def startStaticAndRedirect() {
    var statichandler: ServletContextHandler = null
    statichandler = Servlets.createStaticHandler(".", "/file/")
    var redirectHandler: ServletContextHandler = null
    var servinfo: ServerInfo = null
    var sslOptions: SSLOptions = null
    redirectHandler   = Servlets.createRedirectHandler("/file2", "/file/")
    sslOptions = SSLOptions.self
    servinfo = Utils.startJettyServer("127.0.0.1", 8991, sslOptions, java.util.Arrays.asList(redirectHandler,statichandler), "localhost")
    servinfo.server.stop()
    // servinfo.server.join()
  }
  
  @Test
  def startProxy()
  {
    var statichandler: ServletContextHandler = null
    var servinfo: ServerInfo = null
    var sslOptions: SSLOptions = null
    statichandler = Servlets.createStaticHandler(".", "/file/")
    sslOptions = SSLOptions.self
    servinfo = Utils.startJettyServer("127.0.0.1", 8992, sslOptions, java.util.Arrays.asList(statichandler), "localhost")
    //servinfo.server.stop()
    servinfo.server.join()
  }
  
  @After     
  def shutDown() {
  }
}