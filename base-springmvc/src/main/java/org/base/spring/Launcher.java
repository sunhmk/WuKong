package org.base.spring;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.ProtectionDomain;
import java.util.ServiceLoader;

import org.apache.juli.logging.Log;
import org.apache.tomcat.util.scan.StandardJarScanner;
import org.eclipse.jetty.apache.jsp.JettyJasperInitializer;
import org.eclipse.jetty.jsp.JettyJspServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.webapp.Configuration;

/**
 * 
 * web应用启动器
 * 
 * @author CrazyPig
 * @since 2016-08-03
 *
 */
public class Launcher {

	public static final int DEFAULT_PORT = 8080;
	public static final String DEFAULT_CONTEXT_PATH = "/base-springmvc";
	private static final String DEFAULT_APP_CONTEXT_PATH = "src/main/webapp";

	public static class JspStarter extends AbstractLifeCycle implements
			ServletContextHandler.ServletContainerInitializerCaller {
		JettyJasperInitializer sci;
		WebAppContext context;

		public JspStarter(WebAppContext context) {
			this.sci = new JettyJasperInitializer();
			this.context = context;
			this.context.setAttribute("org.apache.tomcat.JarScanner",
					new StandardJarScanner());
		}

		@Override
		protected void doStart() throws Exception {
			ClassLoader old = Thread.currentThread().getContextClassLoader();
			Thread.currentThread().setContextClassLoader(
					context.getClassLoader());
			try {
				sci.onStartup(null, context.getServletContext());
				super.doStart();
			} finally {
				Thread.currentThread().setContextClassLoader(old);
			}
		}
	}

	public static void main(String[] args) {

		runJettyServer(DEFAULT_PORT, DEFAULT_CONTEXT_PATH);

	}

	public static void runJettyServer(int port, String contextPath) {

		System.out.println("test...");
		Server server = createJettyServer(port, contextPath);
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public static Server createJettyServer(int port, String contextPath) {

		Server server = new Server();//port);
		ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.addConnector(connector);
        Configuration.ClassList classlist = Configuration.ClassList
                .setServerDefault( server );
        classlist.addBefore(
                "org.eclipse.jetty.webapp.JettyWebXmlConfiguration",
                "org.eclipse.jetty.annotations.AnnotationConfiguration" );
		server.setStopAtShutdown(true);

		ProtectionDomain protectionDomain = Launcher.class
				.getProtectionDomain();
		URL location = protectionDomain.getCodeSource().getLocation();
		String warFile = location.toExternalForm();
		System.out.println("wardir:" + warFile);
		WebAppContext context = new WebAppContext(warFile, contextPath);
		//context.setServer(server);
		// 设置work dir,war包将解压到该目录，jsp编译后的文件也将放入其中。
		String currentDir = new File(location.getPath()).getParent();
		File workDir = new File(currentDir, "work");
		System.out.println("workdir:" + currentDir);
		context.setTempDirectory(workDir);
		// ClassLoader jspClassLoader = new URLClassLoader(new URL[0], (new Launcher()).getClass().getClassLoader());
	     //   context.setClassLoader(jspClassLoader);
		context.addBean(new JspStarter(context));
		server.setHandler(context);
		return server;

	}

	public static Server createDevServer(int port, String contextPath) {

		Server server = new Server();
		server.setStopAtShutdown(true);

		ServerConnector connector = new ServerConnector(server);
		// 设置服务端口
		connector.setPort(port);
		connector.setReuseAddress(false);
		server.setConnectors(new Connector[] { connector });
		ClassLoader classloader = Thread.currentThread()
				.getContextClassLoader();
		// 设置web资源根路径以及访问web的根路径
		/*
		 * File programRootDir = new File(System.getProperty("user.dir") + "/");
		 * 
		 * 
		 * URLClassLoader classLoader = (URLClassLoader)
		 * classloader;//ClassLoader.getSystemClassLoader(); Method add = null;
		 * try { add = URLClassLoader.class.getDeclaredMethod("addURL", new
		 * Class<?>[]{URL.class}); } catch (NoSuchMethodException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (SecurityException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } add.setAccessible(true); try {
		 * add.invoke(classLoader, programRootDir.toURI().toURL());
		 * //add.invoke(
		 * classLoader,org.eclipse.jetty.apache.jsp.JuliLog.class.getResource
		 * ("").getPath()); } catch (IllegalAccessException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (IllegalArgumentException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } catch (InvocationTargetException e) { // TODO
		 * Auto-generated catch block e.printStackTrace(); } catch
		 * (MalformedURLException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */

		ProtectionDomain protectionDomain = Launcher.class
				.getProtectionDomain();
		URL location = protectionDomain.getCodeSource().getLocation();
		String warFile = location.toExternalForm();
		WebAppContext webAppCtx = new WebAppContext(DEFAULT_APP_CONTEXT_PATH,
				contextPath);
		webAppCtx.setDescriptor(DEFAULT_APP_CONTEXT_PATH + "/WEB-INF/web.xml");
		webAppCtx.setResourceBase(DEFAULT_APP_CONTEXT_PATH);
		webAppCtx.setClassLoader(classloader);
		webAppCtx.setParentLoaderPriority(true);
		ClassLoader jspClassLoader = new URLClassLoader(new URL[0],
				Launcher.class.getClassLoader());
		webAppCtx.setClassLoader(jspClassLoader);
		// webAppCtx.setClassLoader(Thread.currentThread().getContextClassLoader());
		ServletHolder holderJsp = new ServletHolder("jsp",
				JettyJspServlet.class);
		holderJsp.setInitOrder(0);
		holderJsp.setInitParameter("logVerbosityLevel", "DEBUG");
		holderJsp.setInitParameter("fork", "false");
		holderJsp.setInitParameter("xpoweredBy", "false");
		holderJsp.setInitParameter("compilerTargetVM", "1.8");
		holderJsp.setInitParameter("compilerSourceVM", "1.8");
		holderJsp.setInitParameter("keepgenerated", "true");
		webAppCtx.addServlet(holderJsp, "*.jsp");
		ServletHolder holderDefault = new ServletHolder("default",
				DefaultServlet.class);
		holderDefault
				.setInitParameter("resourceBase", DEFAULT_APP_CONTEXT_PATH);
		webAppCtx.addServlet(holderDefault, "/");
		server.setHandler(webAppCtx);
		String currentDir = new File(location.getPath()).getParent();
		File workDir = new File(currentDir, "work");
		webAppCtx.setTempDirectory(workDir);

		return server;
	}

}