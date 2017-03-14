package org.base.netty;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.base.collector.SensorDataModel;
import org.base.collector.SensorData;
import org.base.collector.SensorData.*;

import scala.Int;

import java.util.List;
import java.util.ArrayList;
import scala.collection.JavaConverters;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );

    	List<SensorDataModel.Geo> geolst = new ArrayList<SensorDataModel.Geo>();
    	for(int i = 0 ; i < 10;i++)
    	{
    		geolst.add(new SensorDataModel.Geo(Int.int2long(i), i, "2017", i, i, Int.int2float(i)));
    	}
    	List<SensorDataModel.Wave> wavelst = new ArrayList<SensorDataModel.Wave>();
    	for(int i = 0 ; i < 10;i++)
    	{
    		wavelst.add(new SensorDataModel.Wave(Int.int2long(i), i, "2017", i, i, Int.int2float(i)));
    	}
    	SensorData.geo.Builder geobuilder = SensorData.geo.newBuilder();
    	
		geobuilder.setId(1);
		geobuilder.setLane(2);
		geobuilder.setDate("2018");
		geobuilder.setFlows(3);
		geobuilder.setSpped(2);
		geobuilder.setOccu((float)1.0);

		sensordata.Builder databuilder  = sensordata.newBuilder();
		databuilder.addGeoinfos(geobuilder);
		byte[] data = databuilder.build().toByteArray();
		System.out.println("ok");
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
