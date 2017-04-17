package org.base.log;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.logging.Log;   
import org.apache.commons.logging.LogFactory;   
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.impl.SimpleLoggerConfiguration;
public class Commonslogging {
	public  Log log = LogFactory.getLog(Commonslogging.class);   
	  
    public void log() {   
        log.debug("Debug info.");   
        log.info("Info info");   
        log.warn("Warn info");   
        log.error("Error info");   
        log.fatal("Fatal info");   
    }   
  
    public static void main(String[] args) { 
    	//PropertyConfigurator.configure("src/log4j.properties");
    	File programRootDir = new File(System.getProperty("user.dir") + "/src");

    	ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    	URLClassLoader classLoader = (URLClassLoader) classloader;//ClassLoader.getSystemClassLoader();
    	Method add = null;
		try {
			add = URLClassLoader.class.getDeclaredMethod("addURL", new Class[]{URL.class});
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	add.setAccessible(true);
    	try {
			add.invoke(classLoader, programRootDir.toURI().toURL());
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	System.out.println(classloader.getResource("").toString());
    	System.out.println(System.getProperty("user.dir"));
        Commonslogging test = new Commonslogging();   
        System.out.println("log obj = " + test.log);   
        test.log();   
    }   
}
