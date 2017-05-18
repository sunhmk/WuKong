package org.base.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
public class SolrWriter {
	public static void main(String[] args) {

		String urlString = "http://localhost:8983/solr/cltx";
		
		//SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		//((HttpSolrClient) solr).setParser(new XMLResponseParser());
		ConcurrentUpdateSolrClient solr = new ConcurrentUpdateSolrClient.Builder(urlString).withQueueSize(100).withThreadCount(4).build();
		solr.setParser(new XMLResponseParser());
		Collection<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
		for(int i = 0; i < 1000;i++)
		{
			SolrInputDocument input = new SolrInputDocument();
			//String date = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());
			input.addField("id", (long)i);
			input.addField("jgsj", new java.util.Date());
			input.addField("hphm", "浙A"+i);
			input.addField("cllx", i);
			input.addField("fx", "东向西"+i);
			input.addField("dw", "测试点"+i);
			docs.add(input);
		}
		try {
			solr.add(docs);
			UpdateResponse response= solr.commit();
			System.out.println(response.getStatus());
			solr.close();
		} catch (SolrServerException e) {
			try {
				solr.rollback();
			} catch (SolrServerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			try {
				solr.rollback();
			} catch (SolrServerException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		finally{
		}
	}
}
