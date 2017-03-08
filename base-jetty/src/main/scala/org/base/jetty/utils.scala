/*
 * notes to hbase,spark,opentsdb,hadopp,es,flume…
 */
package org.base.jetty
import java.net.{ URI, URL }
import javax.servlet.http.{ HttpServlet, HttpServletRequest, HttpServletResponse }

import org.eclipse.jetty.client.api.Response
import org.eclipse.jetty.proxy.ProxyServlet
import org.eclipse.jetty.server.{ HttpConnectionFactory, Request, Server, ServerConnector }
import org.eclipse.jetty.server.handler._
import org.eclipse.jetty.server.handler.gzip.GzipHandler
import org.eclipse.jetty.servlet._
import org.eclipse.jetty.util.component.LifeCycle
import org.eclipse.jetty.util.thread.{ QueuedThreadPool, ScheduledExecutorScheduler }

import scala.collection.mutable.ArrayBuffer
import scala.collection.JavaConversions._

private[jetty] object Utils {
  /*
   * 添加filter
   */
  def addFilter(handlers: java.util.List[ServletContextHandler]): Unit =
    {

    }

  // Create a new URI from the arguments, handling IPv6 host encoding and default ports.
  private def createRedirectURI(
    scheme: String, server: String, port: Int, path: String, query: String) = {
    val redirectServer = if (server.contains(":") && !server.startsWith("[")) {
      s"[${server}]"
    } else {
      server
    }
    val authority = s"$redirectServer:$port"
    new URI(scheme, authority, path, query, null).toString
  }

  private def createRedirectHttpsHandler(securePort: Int, scheme: String): ContextHandler = {
    val redirectHandler: ContextHandler = new ContextHandler
    redirectHandler.setContextPath("/")
    redirectHandler.setHandler(new AbstractHandler {
      override def handle(
        target: String,
        baseRequest: Request,
        request: HttpServletRequest,
        response: HttpServletResponse): Unit = {
        if (baseRequest.isSecure) {
          return
        }
        val httpsURI = createRedirectURI(scheme, baseRequest.getServerName, securePort,
          baseRequest.getRequestURI, baseRequest.getQueryString)
        response.setContentLength(0)
        response.encodeRedirectURL(httpsURI)
        response.sendRedirect(httpsURI)
        baseRequest.setHandled(true)
      }
    })
    redirectHandler
  }
  
  def startJettyServer(
    hostName: String,
    port: Int,
    sslOptions: SSLOptions,
    handlers: java.util.List[ServletContextHandler],
    serverName: String = ""): ServerInfo = {
    val collection = new ContextHandlerCollection
    this.addFilter(handlers)
    val gzipHandlers = handlers.map { h =>
      val gzipHandler = new GzipHandler
      gzipHandler.setHandler(h)
      gzipHandler
    }
    def connect(port: Int): (Server, Int) = {
      val pool = new QueuedThreadPool
      if (serverName.nonEmpty) {
        pool.setName(serverName)
      }
      pool.setDaemon(true)
      val server = new Server(pool)
      val connectors = new ArrayBuffer[ServerConnector]
      val httpConnector = new ServerConnector(
        server,
        null,
        // Call this full constructor to set this, which forces daemon threads:
        new ScheduledExecutorScheduler(s"$serverName-JettyScheduler", true),
        null,
        -1,
        -1,
        new HttpConnectionFactory())
      httpConnector.setPort(port)
      connectors += httpConnector
      sslOptions.createJettySslContextFactory().foreach { factory =>
        val securePort =
          if (port != 0) {
            (port + 400 - 1024) % (65536 - 1024) + 1024
          } else {
            0
          }
        val scheme = "https"
        val connector = new ServerConnector(server, factory)
        connector.setPort(securePort)
        connectors += connector
        collection.addHandler(createRedirectHttpsHandler(securePort, scheme))
      }
      gzipHandlers.foreach(collection.addHandler)
      // As each acceptor and each selector will use one thread, the number of threads should at
      // least be the number of acceptors and selectors plus 1. (See SPARK-13776)
      var minThreads = 1
      connectors.foreach { connector =>
        // Currently we only use "SelectChannelConnector"
        // Limit the max acceptor number to 8 so that we don't waste a lot of threads
        connector.setAcceptQueueSize(math.min(connector.getAcceptors, 8))
        connector.setHost(hostName)
        // The number of selectors always equals to the number of acceptors
        minThreads += connector.getAcceptors * 2
      }
      server.setConnectors(connectors.toArray)
      pool.setMaxThreads(math.max(pool.getMaxThreads, minThreads))

      val errorHandler = new ErrorHandler()
      errorHandler.setShowStacks(true)
      errorHandler.setServer(server)
      server.addBean(errorHandler)
      server.setHandler(collection)
      try {
        server.start()
        (server, httpConnector.getLocalPort)
      } catch {
        case e: Exception =>
          e.printStackTrace()
          server.stop()
          pool.stop()
          throw e
      }
    }
    val (server, boundPort) = Tools.startservice(port, connect, serverName)
    ServerInfo(server, boundPort)
  }
}

private[jetty] case class ServerInfo(server: Server,
                                     boundPort: Int) {
  def stop(): Unit = {
    server.stop()
    val threadPool = server.getThreadPool
  }
}
