package org.base.collector
import org.base.collector
import org.base.collector.SensorData._
import scala.collection.Seq
import scala.collection.JavaConverters._
import com.google.protobuf.CodedInputStream
import scala.Tuple2

object SensorDataModel
{
  case class Geo (
    id:Long,// 点位编号
    lane:Int,// 车道号
    date:String,// 日期
    flows:Int,// 流量
    speed:Int,// 速度
    occu:Float// 占有率
		)
  case class Wave (
		id:Long,// 点位编号
		lane:Int,// 车道号
		date:String,// 日期
		flows:Int,// 流量
		speed:Int,// 速度
		occu:Float// 占有率
		)
		

  
  def toByteFromSensorData(geoList:List[Geo]=List.empty, waveLst:List[Wave]=List.empty):Array[Byte]=
  {
    val databuilder:sensordata.Builder  = sensordata.newBuilder()
		for(geo:Geo <-geoList)
		{
			val geobuilder:SensorData.geo.Builder = SensorData.geo.newBuilder();
			geobuilder.setId(geo.id);
			geobuilder.setLane(geo.lane);
			geobuilder.setDate(geo.date);
			geobuilder.setFlows(geo.flows);
			geobuilder.setSpped(geo.speed);
			geobuilder.setOccu(geo.occu);
			databuilder.addGeoinfos(geobuilder);
		}

		for(wave:Wave <- waveLst)
		{
			val wavebuilder:SensorData.macrowave.Builder = SensorData.macrowave.newBuilder();
			wavebuilder.setId(wave.id);
			wavebuilder.setLane(wave.lane);
			wavebuilder.setDate(wave.date);
			wavebuilder.setFlows(wave.flows);
			wavebuilder.setSpped(wave.speed);
			wavebuilder.setOccu(wave.occu);
			databuilder.addWaveinfos(wavebuilder);
		}
		databuilder.build().toByteArray();
  }
  
  def SensorDataFromByte(message:Array[Byte]):(List[Geo],List[Wave]) = {
    var geoLst:List[Geo] = List()
    var waveLst:List[Wave] = List()
    val databuilder:sensordata.Builder  = sensordata.newBuilder()
    val codedInput:CodedInputStream = CodedInputStream.newInstance(message)
	  codedInput.setSizeLimit(message.length)
	  databuilder.mergeFrom(codedInput)
	  codedInput.checkLastTagWas(0)
	  for(geo:SensorData.geo <- databuilder.getGeoinfosList.asScala)
	  {
	    geoLst :+ Geo(geo.getId, geo.getLane, geo.getDate, geo.getFlows, geo.getSpped, geo.getOccu)
	  }
    
    for(wave:SensorData.macrowave <-databuilder.getWaveinfosList.asScala){
      waveLst :+ Wave(wave.getId, wave.getLane, wave.getDate, wave.getFlows, wave.getSpped, wave.getOccu)
    }
    
    (geoLst,waveLst)
  }
  
}

