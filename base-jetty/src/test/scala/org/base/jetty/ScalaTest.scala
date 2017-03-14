package org.base.jetty

import java.util.Arrays
import java.util.Set
import java.io.File

import org.scalatest.junit.JUnitSuite
import org.junit.{Test, Before, After}
import org.junit.Assert._
import org.eclipse.jetty.servlet.ServletContextHandler

import javax.servlet.http.{HttpServlet, HttpServletRequest, HttpServletResponse}

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
  
  /*
   * http://localhost:8992/proxy/ to http://localhost:8992/file/
   */
  @Test
  def startProxy()
  {
    var proxyhandler: ServletContextHandler = null
    var servinfo: ServerInfo = null
    var sslOptions: SSLOptions = null
    proxyhandler = Servlets.createProxyHandler("/proxy", "http://localhost:8992/file/")
    sslOptions = SSLOptions.self
    servinfo = Utils.startJettyServer("127.0.0.1", 8992, sslOptions, java.util.Arrays.asList(Servlets.createStaticHandler(".", "/file/"),proxyhandler), "localhost")
    servinfo.server.stop()
    // servinfo.server.join()
  }
  
  /*
   * http://localhost:8992
   * 
   */
  @Test
  def startServerHandler()
  {
     var serverhandler: ServletContextHandler = null
     var servinfo: ServerInfo = null
     var sslOptions: SSLOptions = null
     def func(rq:HttpServletRequest):String ={
       val str = "TEST"
       str
     }
     serverhandler = Servlets.createServletHandler("/", new Servlets.ServletParams(func(_),"text/html"),new java.lang.SecurityManager(), true, "/")
     sslOptions = SSLOptions.self
     servinfo = Utils.startJettyServer("127.0.0.1", 8993, sslOptions, java.util.Arrays.asList(Servlets.createStaticHandler(".", "/file/"),serverhandler), "localhost")
     servinfo.server.stop()
     // servinfo.server.join()
  }
  
  protected final def getTestResourceFile(file: String): File = {
    new File(getClass.getClassLoader.getResource(file).getFile)
  }

  protected final def getTestResourcePath(file: String): String = {
    getTestResourceFile(file).getCanonicalPath
  }

  @Test
  def startHttps()
  {
    val keyStoreFilePath = getTestResourceFile("spark.keystore")
     var serverhandler: ServletContextHandler = null
     var servinfo: ServerInfo = null
     var sslOptions: SSLOptions = null
     def func(rq:HttpServletRequest):String ={
       val str = "TEST"
       str
     }
     serverhandler = Servlets.createServletHandler("/", new Servlets.ServletParams(func(_),"text/html"),new java.lang.SecurityManager(), true, "/")
     sslOptions = new SSLOptions(true,Some(keyStoreFilePath),Some("123456"),Some("123456"),needClientAuth=false)
     servinfo = Utils.startJettyServer("0.0.0.0", 8994, sslOptions, java.util.Arrays.asList(Servlets.createStaticHandler(".", "/file/"),serverhandler), "tests")
     servinfo.server.stop()
      //servinfo.server.join()
  }
  /*
  @Test
  def startHttpsTrust()
  {
    val keyStoreFilePath = getTestResourceFile("dotuian.keystore")
     var serverhandler: ServletContextHandler = null
     var servinfo: ServerInfo = null
     var sslOptions: SSLOptions = null
     def func(rq:HttpServletRequest):String ={
       val str = "TEST"
       str
     }
     serverhandler = Servlets.createServletHandler("/", new Servlets.ServletParams(func(_),"text/html"),new java.lang.SecurityManager(), true, "/")
     sslOptions = new SSLOptions(true,Some(keyStoreFilePath),Some("wukong"),Some("wukong"),needClientAuth=false)
     servinfo = Utils.startJettyServer("0.0.0.0", 8995, sslOptions, java.util.Arrays.asList(Servlets.createStaticHandler(".", "/file/"),serverhandler), "tests")
     servinfo.server.stop()
     // servinfo.server.join()
  }
  * 
  */
  
  @After     
  def shutDown() {
  }
}