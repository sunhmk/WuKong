package org.base.springboot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

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
        try {
			URL restServiceURL = new URL("http://localhost:1111/users/2?id=2&name=test&age=20");
			HttpURLConnection httpConnection;
			try {
				httpConnection = (HttpURLConnection) restServiceURL.openConnection();
				httpConnection.setDoOutput(true);
	            httpConnection.setRequestMethod("DELETE");//PUT
	            httpConnection.setRequestProperty("Accept", "application/json");
	            httpConnection.setRequestProperty("Content-Type", "application/json");
                String input = "";//"{\"id\":1,\"name\":\"Liam\",\"age\":22\"}";
                OutputStream outputStream = httpConnection.getOutputStream();
                outputStream.write(input.getBytes());
                outputStream.flush();

                if (httpConnection.getResponseCode() != 200) {
                       throw new RuntimeException("Failed : HTTP error code : "
                              + httpConnection.getResponseCode());
                }

                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(
                              (httpConnection.getInputStream())));

                String output;
                System.out.println("Output from Server:\n");
                while ((output = responseBuffer.readLine()) != null) {
                       System.out.println(output);
                }

                httpConnection.disconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
