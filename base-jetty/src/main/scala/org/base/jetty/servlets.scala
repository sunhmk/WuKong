package org.base.jetty
import java.net.URL
import java.net.URI
import javax.servlet.http.{ HttpServlet, HttpServletRequest, HttpServletResponse }
import org.eclipse.jetty.client.api.Response
import org.eclipse.jetty.servlet._
import org.eclipse.jetty.proxy.ProxyServlet

private[jetty] object servlets {

  type Responder[T] = HttpServletRequest => T

  class ServletParams[T <% AnyRef](val responder: Responder[T],
                                   val contentType: String,
                                   val extractFn: T => String = (in: Any) => in.toString) {}

  def createServlet[T <% AnyRef](
    servletParams: ServletParams[T],
    isEnabled: Boolean): HttpServlet =
    {
      new HttpServlet {
        override def doGet(request: HttpServletRequest, response: HttpServletResponse) {
          try {
            if (isEnabled) {
              response.setContentType("%s;charset=utf-8".format(servletParams.contentType))
              response.setStatus(HttpServletResponse.SC_OK)
              val result = servletParams.responder(request)
              response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate")
              //response.setHeader("X-Frame-Options", xFrameOptionsValue)
              response.getWriter.print(servletParams.extractFn(result))
            } else {
              response.setStatus(HttpServletResponse.SC_UNAUTHORIZED)
              response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate")
              response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
                "User is not authorized to access this page.")
            }
          } catch {
            case e: IllegalArgumentException =>
              response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage)
            case e: Exception =>
              //logWarning(s"GET ${request.getRequestURI} failed: $e", e)
              throw e
          }
        }
        // SPARK-5983 ensure TRACE is not supported
        protected override def doTrace(req: HttpServletRequest, res: HttpServletResponse): Unit = {
          res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
        }
      }
    }

  def createServletHandler[T <% AnyRef](path: String,
                                        servletParams: ServletParams[T],
                                        securityMgr: SecurityManager,
                                        isEnabled: Boolean,
                                        basePath: String = ""): ServletContextHandler =
    {
      createServletHandler(path, createServlet(servletParams, isEnabled), basePath)
    }

  def createServletHandler(path: String,
                           servlet: HttpServlet,
                           basePath: String): ServletContextHandler =
    {
      val prefixedPath = if (basePath == "" && path == "/") {
        path
      } else {
        (basePath + path).stripSuffix("/")
      }
      val contextHandler = new ServletContextHandler
      val holder = new ServletHolder(servlet)
      contextHandler.setContextPath(prefixedPath)
      contextHandler.addServlet(holder, "/")
      contextHandler
    }

  /** Create a handler that always redirects the user to the given path */
  def createRedirectHandler(
    srcPath: String,
    destPath: String,
    beforeRedirect: HttpServletRequest => Unit = x => (),
    basePath: String = "",
    httpMethods: Set[String] = Set("GET")): ServletContextHandler = {
    val prefixedDestPath = basePath + destPath
    val servlet = new HttpServlet {
      override def doGet(request: HttpServletRequest, response: HttpServletResponse): Unit = {
        if (httpMethods.contains("GET")) {
          doRequest(request, response)
        } else {
          response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
        }
      }
      override def doPost(request: HttpServletRequest, response: HttpServletResponse): Unit = {
        if (httpMethods.contains("POST")) {
          doRequest(request, response)
        } else {
          response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
        }
      }
      private def doRequest(request: HttpServletRequest, response: HttpServletResponse): Unit = {
        beforeRedirect(request)
        // Make sure we don't end up with "//" in the middle
        val newUrl = new URL(new URL(request.getRequestURL.toString), prefixedDestPath).toString
        response.sendRedirect(newUrl)
      }
      // SPARK-5983 ensure TRACE is not supported
      protected override def doTrace(req: HttpServletRequest, res: HttpServletResponse): Unit = {
        res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED)
      }
    }
    createServletHandler(srcPath, servlet, basePath)
  }

  /** Create a handler for serving files from a static directory */
  def createStaticHandler(resourceBase: String, path: String): ServletContextHandler = {
    val contextHandler = new ServletContextHandler
    contextHandler.setInitParameter("org.eclipse.jetty.servlet.Default.gzip", "false")
    val staticHandler = new DefaultServlet
    val holder = new ServletHolder(staticHandler)
    Option(getClass.getClassLoader.getResource(resourceBase)) match {
      case Some(res) =>
        holder.setInitParameter("resourceBase", res.toString)
      case None =>
        throw new Exception("Could not find resource path for Web UI: " + resourceBase)
    }
    contextHandler.setContextPath(path)
    contextHandler.addServlet(holder, "/")
    contextHandler
  }

  /** Create a handler for proxying request to Workers and Application Drivers */
  def createProxyHandler(
    prefix: String,
    target: String): ServletContextHandler = {
    val servlet = new ProxyServlet {
      override def rewriteTarget(request: HttpServletRequest): String = {
        val rewrittenURI = createProxyURI(
          prefix, target, request.getRequestURI(), request.getQueryString())
        if (rewrittenURI == null) {
          return null
        }
        if (!validateDestination(rewrittenURI.getHost(), rewrittenURI.getPort())) {
          return null
        }
        rewrittenURI.toString()
      }

      override def filterServerResponseHeader(
        clientRequest: HttpServletRequest,
        serverResponse: Response,
        headerName: String,
        headerValue: String): String = {
        if (headerName.equalsIgnoreCase("location")) {
          val newHeader = createProxyLocationHeader(
            prefix, headerValue, clientRequest, serverResponse.getRequest().getURI())
          if (newHeader != null) {
            return newHeader
          }
        }
        super.filterServerResponseHeader(
          clientRequest, serverResponse, headerName, headerValue)
      }
    }

    val contextHandler = new ServletContextHandler
    val holder = new ServletHolder(servlet)
    contextHandler.setContextPath(prefix)
    contextHandler.addServlet(holder, "/")
    contextHandler
  }
  def createProxyURI(prefix: String, target: String, path: String, query: String): URI = {
    if (!path.startsWith(prefix)) {
      return null
    }

    val uri = new StringBuilder(target)
    val rest = path.substring(prefix.length())

    if (!rest.isEmpty()) {
      if (!rest.startsWith("/")) {
        uri.append("/")
      }
      uri.append(rest)
    }

    val rewrittenURI = URI.create(uri.toString())
    if (query != null) {
      return new URI(
        rewrittenURI.getScheme(),
        rewrittenURI.getAuthority(),
        rewrittenURI.getPath(),
        query,
        rewrittenURI.getFragment()).normalize()
    }
    rewrittenURI.normalize()
  }

  def createProxyLocationHeader(
    prefix: String,
    headerValue: String,
    clientRequest: HttpServletRequest,
    targetUri: URI): String = {
    val toReplace = targetUri.getScheme() + "://" + targetUri.getAuthority()
    if (headerValue.startsWith(toReplace)) {
      clientRequest.getScheme() + "://" + clientRequest.getHeader("host") +
        prefix + headerValue.substring(toReplace.length())
    } else {
      null
    }
  }
}