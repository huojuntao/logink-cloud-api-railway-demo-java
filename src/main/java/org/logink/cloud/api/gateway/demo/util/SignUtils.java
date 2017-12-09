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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

/**
 * 绛惧悕宸ュ叿
 */
public class SignUtils {
    //签名算法HmacSha256
    public static final String HMAC_SHA256 = "HmacSHA256";
    //编码UTF-8
    public static final String ENCODING = "UTF-8";
    //换行符
    public static final String LF = "\n";
    //串联符
    public static final String SPE1 = ",";
    //示意符
    public static final String SPE2 = ":";
    //参与签名的系统Header前缀,只有指定前缀的Header才会参与到签名中
    public static final String CA_HEADER_TO_SIGN_PREFIX_SYSTEM = "X-Ca-";
    
    //请求Header Accept
    public static final String HTTP_HEADER_ACCEPT = "Accept";
    //请求Body内容MD5 Header
    public static final String HTTP_HEADER_CONTENT_MD5 = "Content-MD5";
    //请求Header Content-Type
    public static final String HTTP_HEADER_CONTENT_TYPE = "Content-Type";
    //请求Header Date
    public static final String HTTP_HEADER_DATE = "Date";
    
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
     * 计算签名
     * 
     * @param secret 用户APP密钥
     * @param headers 所有header信息
     * @param signHeaderPrefixList 参与签名的header信息
     * @return 签名值
     */
    public static String sign(String secret, Map<String, String> headers, List<String> signHeaderPrefixList) {
        try {
            Mac hmacSha256 = Mac.getInstance(HMAC_SHA256);
            byte[] keyBytes = secret.getBytes(ENCODING);
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, HMAC_SHA256));

            return new String(Base64.encodeBase64(
                    hmacSha256.doFinal(buildStringToSign(headers,signHeaderPrefixList)
                            .getBytes(ENCODING))),
                    ENCODING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Build String used to sign
     * @param headers
     * @param signHeaderPrefixList
     * @return
     */
    private static String buildStringToSign(Map<String, String> headers, List<String> signHeaderPrefixList) {
        StringBuilder sb = new StringBuilder();

        if (null != headers) {
        	if (null != headers.get(HTTP_HEADER_ACCEPT)) {
                sb.append(headers.get(HTTP_HEADER_ACCEPT));
            }
        	sb.append(LF);
        	if (null != headers.get(HTTP_HEADER_CONTENT_MD5)) {
                sb.append(headers.get(HTTP_HEADER_CONTENT_MD5));
            }
            sb.append(LF);
            if (null != headers.get(HTTP_HEADER_CONTENT_TYPE)) {
                sb.append(headers.get(HTTP_HEADER_CONTENT_TYPE));
            }
            sb.append(LF);
            if (null != headers.get(HTTP_HEADER_DATE)) {
                sb.append(headers.get(HTTP_HEADER_DATE));
            }
        }
        sb.append(LF);
        sb.append(buildHeaders(headers, signHeaderPrefixList));
        
        return sb.toString();
    }
   

   
    /**
     * Build headers String that used to sign
     * @param headers
     * @param signHeaderPrefixList
     * @return
     */
    private static String buildHeaders(Map<String, String> headers, List<String> signHeaderPrefixList) {
    	StringBuilder sb = new StringBuilder();
    	
    	if (null != signHeaderPrefixList) {
    		signHeaderPrefixList.remove(X_CA_SIGNATURE);
    		signHeaderPrefixList.remove(HTTP_HEADER_ACCEPT);
    		signHeaderPrefixList.remove(HTTP_HEADER_CONTENT_MD5);
    		signHeaderPrefixList.remove(HTTP_HEADER_CONTENT_TYPE);
    		signHeaderPrefixList.remove(HTTP_HEADER_DATE);
    		Collections.sort(signHeaderPrefixList);
    		if (null != headers) {
    			Map<String, String> sortMap = new TreeMap<String, String>();
    			sortMap.putAll(headers);
    			StringBuilder signHeadersStringBuilder = new StringBuilder();
    			for (Map.Entry<String, String> header : sortMap.entrySet()) {
                    if (isHeaderToSign(header.getKey(), signHeaderPrefixList)) {
//                    	sb.append(header.getKey());
//                    	sb.append(SPE2);
                        if (!StringUtils.isBlank(header.getValue())) {
                        	sb.append(header.getValue());
                        }
                        sb.append(LF);
                        if (0 < signHeadersStringBuilder.length()) {
                        	signHeadersStringBuilder.append(SPE1);
                        }
                        signHeadersStringBuilder.append(header.getKey());
                    }
                }
    			headers.put(X_CA_SIGNATURE_HEADERS, signHeadersStringBuilder.toString());
    		}
    	}
        
        return sb.toString();
    }

    /**
     * Return true if header is used to sign
     */
    private static boolean isHeaderToSign(String headerName, List<String> signHeaderPrefixList) {
        if (StringUtils.isBlank(headerName)) {
            return false;
        }

        if (headerName.startsWith(CA_HEADER_TO_SIGN_PREFIX_SYSTEM)) {
            return true;
        }

        if (null != signHeaderPrefixList) {
            for (String signHeaderPrefix : signHeaderPrefixList) {
                if (headerName.equalsIgnoreCase(signHeaderPrefix)) {
                    return true;
                }
            }
        }

        return false;
    }
    
}
