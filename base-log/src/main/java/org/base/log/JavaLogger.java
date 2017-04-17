package org.base.log;
import java.util.logging.Logger;  
public class JavaLogger {
	static String strClassName = JavaLogger.class.getName();  
    static Logger logger = Logger.getLogger(strClassName);  
      
    public static double division(int value1, int value2) {  
        double result = 0;  
        try {  
            result = value1 / value2;  
        } catch(ArithmeticException e) {  
        	 logger.severe("[severe]除数不能为0.");  
             logger.warning("[warning]除数不能为0.");  
             logger.info("[info]除数不能为0.");  
             logger.config("[config]除数不能为0.");  
             logger.fine("[fine]除数不能为0.");  
             logger.finer("[finer]除数不能为0.");  
             logger.finest("[finest]除数不能为0.");  
             e.printStackTrace();  
            e.printStackTrace();  
        }  
        return result;  
    }  
  
    public static void main(String[] args) {  
        System.out.println(division(5, 0));  
    }  
}
