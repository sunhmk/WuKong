package org.base.log;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

//import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogBackExample {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public void test()
    {
		logger.debug("输出debug级别的日志.....");  
        logger.info("输出info级别的日志.....");  
        logger.error("输出error级别的日志.....");  
    }
	public static void main(String[]args)
	{    	
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
    	
		LogBackExample log= new LogBackExample();
		log.test();
	}
}
