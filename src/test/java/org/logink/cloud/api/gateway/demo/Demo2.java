package org.logink.cloud.api.gateway.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.logink.cloud.api.gateway.demo.util.HttpUtils;
import org.logink.cloud.api.gateway.demo.util.SignUtils;

public class Demo2 {
	public static void main(String[] args) {
	    String host = "https://api.logink.org";
	    String path = "/railway-query/subscribe";
	    int timeout = 1000;
	    String appkey = "你自己的AppKey";
	    String appsecret = "你自己的AppSecret";
	    
	    Map<String, String> headers = new HashMap<String, String>();
	  //根据API的要求，定义相对应的Accept,Content-Type
        headers.put("Accept", "application/json; charset=UTF-8");
        headers.put("Content-Type",  "application/json; charset=UTF-8");
        headers.put("X-Ca-Timestamp", String.valueOf(new Date().getTime()));
        headers.put("X-Ca-Nonce", UUID.randomUUID().toString());
        headers.put("X-Ca-Key", appkey);
        Map<String, String> querys = new HashMap<String, String>();
        List<String> signHeaderPrefixList = new ArrayList<String>();
        /**
    	* 重要提示如下:
    	* SignUtils请从
    	* https://github.com/huojuntao/logink-cloud-api-railway-demo-java/tree/master/src/main/java/org/logink/cloud/api/gateway/demo/util/SignUtils.java
    	* 下载
    	*/
        headers.put("X-Ca-Signature",
        		SignUtils.sign(appsecret, headers, signHeaderPrefixList));
        
	    String body = "{\"taskId\":\"4028830b5f512c68015f512f22e40000\","   //订阅标识符
	    		+ "\"railwayWagonNumber\":\"1640917\", " //车号
	    		+ "\"containerNumber\":\"\","  //集装箱箱号
	    		+ "\"shippingNodeNumber\":\"\","   //运单号
	    		+ "\"invoiceNumber\":\"\","     //货票号
	    		+ "\"placeOfDeparture\":\"\", "    //发站
	    		+ "\"destination\":\"小塘西\","    //到达站
	    		+ "\"endDate\":\"2017-12-20\"}";    //订阅截止时间
	   
		try {
			/**
	    	* 重要提示如下:
	    	* HttpUtils请从
	    	* https://github.com/huojuntao/logink-cloud-api-railway-demo-java/tree/master/src/main/java/org/logink/cloud/api/gateway/demo/util/HttpUtils.java
	    	* 下载
	    	*
	    	* 相应的依赖请参照
	    	* https://github.com/huojuntao/logink-cloud-api-railway-demo-java/pom.xml
	    	*/
			HttpResponse response = HttpUtils.httpPost(host, path, timeout, headers, querys, body, 
					signHeaderPrefixList, appkey, appsecret);
			System.out.println(response.toString());
			//获取response的body
	    	System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
