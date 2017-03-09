/*
 * notes to hbase,spark,opentsdb,hadopp,es,flume…
 */
package org.base.jetty

import java.util.Arrays
import java.util.Set

import scala.collection.Seq

import org.eclipse.jetty.servlet.ServletContextHandler

object Main {
  def main(argStrings: Array[String])
  {
    // http://localhost:8989/file/
    var statichandler: ServletContextHandler = null
    var servinfo: ServerInfo = null
    var sslOptions: SSLOptions = null
    statichandler = Servlets.createStaticHandler(".", "/file/")
    sslOptions = SSLOptions.self
    servinfo = Utils.startJettyServer("127.0.0.1", 8988, sslOptions, java.util.Arrays.asList(statichandler), "localhost")
    servinfo.server.join()
  }

}
