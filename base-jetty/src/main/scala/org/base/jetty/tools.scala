/*
 * notes to hbase,spark,opentsdb,hadopp,es,flume…
 */
package org.base.jetty

import org.eclipse.jetty.server.Server

private[jetty] object Tools {
  def startservice(port: Int, startup: (Int) => (Server, Int), name: String): (Server, Int) =
    {
      def isBindCollision(exception: Throwable): Boolean = {
        exception match {
          case e: Exception =>
            if (e.getMessage != null) {
              return true
            }
            false
          case _ => false
        }
      }
      try {
        startup(port)
      } catch {
        case e: Exception if isBindCollision(e) =>
          throw e
        case _: Throwable =>
          throw new Exception("")
      }
    }
}
