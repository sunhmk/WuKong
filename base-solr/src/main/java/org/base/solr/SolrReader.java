package org.base.solr;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.ListIterator;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.SolrParams;

public class SolrReader {
	public static void main(String[] args) {

		String urlString = "http://localhost:8983/solr/cltx";

		SolrClient solr = new HttpSolrClient.Builder(urlString).build();
		((HttpSolrClient) solr).setParser(new XMLResponseParser());
		SolrQuery query = new SolrQuery();
		// query.setQuery(mQueryString);
		query.set("fl", "id,jgsj,hphm,cllx,fx,dw");
		// query.setFields("id", "title", "price");
		// query.set("q", "*:*");
		// query.set("q", "hphm:浙A?2*");//?或者*
		// query.set("q", "hphm:浙A?2* AND id:12");//?或者*
		//query.set("q", "hphm:浙A?2* AND id:[2 TO 120]");//?或者* 整数不能模糊查询, AND
		// OR
		//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");  
		//String time = "jgsj:["+sdf.format(new Date())+" TO "+sdf.format(new Date())+"]"; 
		query.set("q", "jgsj:[2013-01-01T00:00:00Z TO 2018-01-01T00:00:00Z]");//
		/*
		 * []表示查询一个日期范围，包括边界即createtime>=a and createtime
		 * <=b。{}表示查询一个日期范围不包括边界即createtime>a and createtime <b。
		 * 
		 * A TO * 表示没有上界即>=A或是>A ,视使用的是[]还是{}而定
		 * 
		 * TO A 表示没有下界即<=A或是<A ,视使用的是[]还是{}而定
		 * 
		 * NOW表示当前时间,NOW/DAY表示当前日期.
		 */
		QueryResponse response;
		try {
			int index = 0;
			int pagesize = 10;
			int gets = 0;
			while (true) {
				query.setStart(index);
				index++;
				query.setRows(pagesize);
				response = solr.query(query);
				SolrDocumentList list = response.getResults();
				ListIterator<SolrDocument> itr = list.listIterator();
				gets = 0;
				while (itr.hasNext()) {
					SolrDocument doc = itr.next();
					System.out.println("id=" + doc.getFieldValue("id")
							+ " jgsj=" + doc.getFieldValue("jgsj").toString()
							+ " hphm=" + doc.getFieldValue("hphm") + " cllx="
							+ doc.getFieldValue("cllx") + " fx ="
							+ doc.getFieldValue("fx") + " dw="
							+ doc.getFieldValue("dw"));
					gets++;
				}
				if (gets < pagesize) {
					break;
				}
			}
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
