package org.base.jetty.hbase;

/*
 import javax.servlet.ServletException;
 import javax.servlet.http.HttpServlet;
 import javax.servlet.http.HttpServletRequest;
 import javax.servlet.http.HttpServletResponse;

 import org.mortbay.jetty.Connector;
 import org.mortbay.jetty.nio.SelectChannelConnector;
 import org.mortbay.jetty.servlet.Context;

 public class SslServer {
 private org.mortbay.jetty.Server masterJettyServer;
 private String addr = "0.0.0.0";
 private int infoPort = 8989;

 private int putUpJettyServer() throws IOException {

 masterJettyServer = new org.mortbay.jetty.Server();
 HttpConfiguration https_config = new HttpConfiguration();
 https_config.setSecureScheme("https");
 https_config.setSecurePort(8443);
 https_config.setOutputBufferSize(32768);
 https_config.addCustomizer(new SecureRequestCustomizer());

 SslContextFactory sslContextFactory = new SslContextFactory();
 URL url = getClass().getClassLoader().getResource("resources");

 String urlString = url.toString().substring(0, url.toString().lastIndexOf('/'));
 sslContextFactory.setKeyStorePath(urlString + "/keystore");
 sslContextFactory.setKeyStorePassword("Sun1002");
 sslContextFactory.setKeyManagerPassword("Sun1002");

 ServerConnector connector = new ServerConnector(server,
 new SslConnectionFactory(sslContextFactory,"http/1.1"),
 new HttpConnectionFactory(https_config));
 connector.setHost(addr);
 connector.setPort(infoPort);
 masterJettyServer.addConnector(connector);
 masterJettyServer.setStopAtShutdown(true);
 Context context = new Context(masterJettyServer, "/",
 Context.NO_SESSIONS);// in hbase Context.NO_SESSIONS,here to
 // verify connection.
 context.addServlet(RedirectServlet.class, "/*");
 try {
 masterJettyServer.start();
 } catch (Exception e) {
 throw new IOException("Failed to start redirecting jetty server", e);
 }
 return connector.getLocalPort();
 }

 public static class RedirectServlet extends HttpServlet {
 private static final long serialVersionUID = 2894774810058302472L;
 private static int regionServerInfoPort;

 @Override
 public void doGet(HttpServletRequest request,
 HttpServletResponse response) throws ServletException,
 IOException {
 String redirectUrl = request.getScheme() + "://"
 + request.getServerName() + ":" + regionServerInfoPort
 + request.getRequestURI();
 // response.sendRedirect(redirectUrl);
 response.getWriter().write("out put return");
 }
 }

 public static void main( String[] args )
 {
 try {
 new SslServer().putUpJettyServer();
 } catch (IOException e) {
 // TODO Auto-generated catch block
 e.printStackTrace();
 }
 }
 }
 */
