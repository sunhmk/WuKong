package org.base.jetty

import java.util.Arrays
import java.util.Set

import scala.collection.Seq
import scala.collection.JavaConversions
import scala.collection.JavaConverters

/*import org.base.jetty.servlets;
import org.base.jetty.ServerInfo;
import org.base.jetty.utils;
import org.base.jetty.SSLOptions;*/

import org.eclipse.jetty.servlet.ServletContextHandler

object Main {
  def main(argStrings: Array[String]) {

    var statichandler: ServletContextHandler = null
    var servinfo: ServerInfo = null
    var sslOptions: SSLOptions = null

    statichandler = servlets.createStaticHandler(".", "/file/")
    sslOptions = SSLOptions.self
    servinfo = utils.startJettyServer("127.0.0.1", 8989, sslOptions, java.util.Arrays.asList(statichandler), "localhost")
    servinfo.server.join()
  }

}