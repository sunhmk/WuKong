package org.base.log;
import org.apache.log4j.*;
import org.apache.log4j.PropertyConfigurator;

public class Log4jExample {
	private static Logger logger = Logger.getLogger(Log4jExample.class);  

    /** 
     * @param args 
     */  
    public static void main(String[] args) {  
    	PropertyConfigurator.configure("src/log4j.properties");
        // System.out.println("This is println message.");  

        // 记录debug级别的信息  
        logger.debug("This is debug message.");  
        // 记录info级别的信息  
        logger.info("This is info message.");  
        // 记录error级别的信息  
        logger.error("This is error message.");  
        logger.log(Priority.DEBUG, "Testing a log message use a alternate form");
    }  
}
