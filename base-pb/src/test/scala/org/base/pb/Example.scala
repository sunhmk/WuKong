/*
 * notes to hbase,spark,opentsdb,hadopp,es,flume…
 */
package org.base.pb

import org.scalatest.junit.JUnitSuite
import org.base.collector.SensorDataModel._
import org.scalatest.{FunSpec, ShouldMatchers}

class Example extends FunSpec with ShouldMatchers {
  var message:Array[Byte] = Array.empty[Byte]
  describe("test sensordata"){
    it("ToBytesFromSensorData") {
      ToBytesFromSensorData()
    }
    it("ToSensorDataFromBytes"){
      ToSensorDataFromBytes()
    }
  }

  def ToBytesFromSensorData() {
    var geoLst: List[Geo] = List()
    for (i <- 1 to 10) {
      geoLst :+ Geo(i.toLong, i, "2017", i, i, i.toFloat)
    }

    var waveLst: List[Wave] = List()
    for (i <- 1 to 10) {
      waveLst :+ Wave(i.toLong, i, "2017", i, i, i.toFloat)
    }
    message = toByteFromSensorData(geoLst,waveLst)
  }

  def print(geoLst:List[Geo],waveLst:List[Wave]){
    var i:Int = 1;
    for(geo:Geo <- geoLst)
    {
      geo.id should be (i)
      i += 1
    }
    /*
     * geoLst.foreach { 
      geo =>println("id:%d,lane:%d,date:%s,flows:%d,speed:%d,occ:%f\r\n", geo.id,geo.lane,geo.date,geo.flows,geo.flows,geo.occu) 
    }
    waveLst.foreach { 
      wave => println("id:%d,lane:%d,date:%s,flows:%d,speed:%d,occ:%f\r\n", wave.id,wave.lane,wave.date,wave.flows,wave.flows,wave.occu) 
    }
    */
  }
  def ToSensorDataFromBytes()
  {
    val (gs, ws) = SensorDataFromByte(message)
    print(gs,ws)
  }
  
  def shutDown() {
  }
}
