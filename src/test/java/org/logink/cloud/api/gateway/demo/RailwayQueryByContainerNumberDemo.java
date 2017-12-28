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

public class RailwayQueryByContainerNumberDemo {
	public static void main(String[] args) {
	    String host = "https://api.logink.org";
	    String path = "/railway-query/queryByContainerNumber";
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
        
	    String 	body = "{\"containerNumber\":\"TBJU1111111\"}";   //箱号
	   
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
			HttpResponse response = HttpUtils.httpPost(host, path, timeout, headers, querys, body);
			System.out.println(response.toString());
			//获取response的body
	    	System.out.println(EntityUtils.toString(response.getEntity()));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
