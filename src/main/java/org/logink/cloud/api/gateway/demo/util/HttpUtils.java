/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.logink.cloud.api.gateway.demo.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

/**
 * Http工具类
 */
public class HttpUtils {
	
    //签名算法HmacSha256
    public static final String HMAC_SHA256 = "HmacSHA256";
    //编码UTF-8
    public static final String ENCODING = "UTF-8";
    //UserAgent
    public static final String USER_AGENT = "demo/aliyun/java";
    //换行符
    public static final String LF = "\n";
    //串联符
    public static final String SPE1 = ",";
    //示意符
    public static final String SPE2 = ":";
    //连接符
    public static final String SPE3 = "&";
    //赋值符
    public static final String SPE4 = "=";
    //问号符
    public static final String SPE5 = "?";
    //默认请求超时时间,单位毫秒
    public static final int DEFAULT_TIMEOUT = 1000;
    
    //请求Header Accept
    public static final String HTTP_HEADER_ACCEPT = "Accept";
    //请求Body内容MD5 Header
    public static final String HTTP_HEADER_CONTENT_MD5 = "Content-MD5";
    //请求Header Content-Type
    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    //请求Header UserAgent
    public static final String HTTP_HEADER_USER_AGENT = "User-Agent";
    //请求Header Date
    public static final String HTTP_HEADER_DATE = "Date";
    
    //参与签名的系统Header前缀,只有指定前缀的Header才会参与到签名中
    public static final String CA_HEADER_TO_SIGN_PREFIX_SYSTEM = "X-Ca-";
    //签名Header
    public static final String X_CA_SIGNATURE = "X-Ca-Signature";
    //所有参与签名的Header
    public static final String X_CA_SIGNATURE_HEADERS = "X-Ca-Signature-Headers";
    //请求时间戳
    public static final String X_CA_TIMESTAMP = "X-Ca-Timestamp";
    //请求防重放Nonce,15分钟内保持唯一,建议使用UUID
    public static final String X_CA_NONCE = "X-Ca-Nonce";
    //APP KEY
    public static final String X_CA_KEY = "X-Ca-Key";
    
  

    /**
     * Http POST
     * @param host
     * @param path
     * @param connectTimeout
     * @param headers
     * @param querys
     * @param body
     * @param signHeaderPrefixList
     * @param appKey
     * @param appSecret
     * @return
     * @throws Exception
     */
    public static HttpResponse httpPost(String host, String path, int connectTimeout, Map<String, String> headers, Map<String, String> querys, String body)
            throws Exception {

    	HttpClient httpClient = wrapClient(host);
        httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, getTimeout(connectTimeout));

        HttpPost post = new HttpPost(initUrl(host, path, querys));
        for (Map.Entry<String, String> e : headers.entrySet()) {
            post.addHeader(e.getKey(), utf8ToIso88591(e.getValue()));
        }

        if (StringUtils.isNotBlank(body)) {
            post.setEntity(new StringEntity(body, ENCODING));

        }

        return httpClient.execute(post);
    }


    private static String initUrl(String host, String path, Map<String, String> querys) throws UnsupportedEncodingException {
    	StringBuilder sbUrl = new StringBuilder();
    	sbUrl.append(host);
    	if (!StringUtils.isBlank(path)) {
    		sbUrl.append(path);
        }
    	if (null != querys) {
    		StringBuilder sbQuery = new StringBuilder();
        	for (Map.Entry<String, String> query : querys.entrySet()) {
        		if (0 < sbQuery.length()) {
        			sbQuery.append(SPE3);
        		}
        		if (StringUtils.isBlank(query.getKey()) && !StringUtils.isBlank(query.getValue())) {
        			sbQuery.append(query.getValue());
                }
        		if (!StringUtils.isBlank(query.getKey())) {
        			sbQuery.append(query.getKey());
        			if (!StringUtils.isBlank(query.getValue())) {
        				sbQuery.append(SPE4);
        				sbQuery.append(URLEncoder.encode(query.getValue(), ENCODING));
        			}        			
                }
        	}
        	if (0 < sbQuery.length()) {
        		sbUrl.append(SPE5).append(sbQuery);
        	}
        }
    	
    	return sbUrl.toString();
    }
    	

    /**
     * Get time out
     * 
     * @param timeout
     * @return
     */
    private static int getTimeout(int timeout) {
        if (timeout == 0) {
            return DEFAULT_TIMEOUT;
        }

        return timeout;
    }
    
   

	/**
	 * Read Stream as String
	 *
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static String readStreamAsStr(InputStream is) throws IOException {
	    ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    WritableByteChannel dest = Channels.newChannel(bos);
	    ReadableByteChannel src = Channels.newChannel(is);
	    ByteBuffer bb = ByteBuffer.allocate(4096);
	
	    while (src.read(bb) != -1) {
	        bb.flip();
	        dest.write(bb);
	        bb.clear();
	    }
	    src.close();
	    dest.close();
	
	    return new String(bos.toByteArray(), ENCODING);
	}

	private static HttpClient wrapClient(String host) {
		HttpClient httpClient = new DefaultHttpClient();
		if (host.startsWith("https://")) {
			sslClient(httpClient);
		}
		
		return httpClient;
	}
	
	private static void sslClient(HttpClient httpClient) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(X509Certificate[] xcs, String str) {
                	
                }
                public void checkServerTrusted(X509Certificate[] xcs, String str) {
                	
                }
            };
            ctx.init(null, new TrustManager[] { tm }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            ClientConnectionManager ccm = httpClient.getConnectionManager();
            SchemeRegistry registry = ccm.getSchemeRegistry();
            registry.register(new Scheme("https", 443, ssf));
        } catch (KeyManagementException ex) {
            throw new RuntimeException(ex);
        } catch (NoSuchAlgorithmException ex) {
        	throw new RuntimeException(ex);
        }
    }
	  /**
     * UTF-8编码转换为ISO-9959-1
     *
     * @param str
     * @return
     */
    private static String utf8ToIso88591(String str) {
        if (str == null) {
            return str;
        }

        try {
            return new String(str.getBytes("UTF-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}