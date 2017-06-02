package org.base.log
//import org.base.log.LoggerExample
import org.slf4j.LoggerFactory
import org.apache.log4j.{Level, LogManager, PropertyConfigurator}
import org.slf4j.Logger
import scala.reflect
object Main extends App {
     var  logger :Logger  = LoggerFactory.getLogger(Main.getClass)
  def main(argStrings: Array[String]) {
        	PropertyConfigurator.configure("src/log4j.properties")

    var log = new LoggerExample
    log.log
    log.logInfo("test")
// 记录error信息
    logger.error("[info message]")
// 记录info，还可以传入参数
   // logger.info("[info message]{},{},{},{}", "abc", false, 123, new Main())
// 记录deubg信息
    logger.debug("[debug message]")
// 记录trace信息
    logger.trace("[trace message]")
    System.out.println("hello world")
  }
}