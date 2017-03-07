package org.base.jetty.hbase;


public class Server {
	 public static void main( String[] args )
	 {
		 
	 }
}

/*
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.servlet.Context;

public class Server {
	private org.mortbay.jetty.Server masterJettyServer;
	private String addr = "0.0.0.0";
	private int infoPort = 8989;

	private int putUpJettyServer() throws IOException {

		masterJettyServer = new org.mortbay.jetty.Server();

		Connector connector = new SelectChannelConnector();
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
			new Server().putUpJettyServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }
}*/
