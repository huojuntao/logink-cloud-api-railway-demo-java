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
import org.logink.cloud.api.gateway.demo.constant.Constants;
import org.logink.cloud.api.gateway.demo.constant.HttpHeader;
import org.logink.cloud.api.gateway.demo.constant.SystemHeader;

/**
 * 绛惧悕宸ュ叿
 */
public class SignUtil {

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
            Mac hmacSha256 = Mac.getInstance(Constants.HMAC_SHA256);
            byte[] keyBytes = secret.getBytes(Constants.ENCODING);
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, Constants.HMAC_SHA256));

            return new String(Base64.encodeBase64(
                    hmacSha256.doFinal(buildStringToSign(headers,signHeaderPrefixList)
                            .getBytes(Constants.ENCODING))),
                    Constants.ENCODING);
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
        	if (null != headers.get(HttpHeader.HTTP_HEADER_ACCEPT)) {
                sb.append(headers.get(HttpHeader.HTTP_HEADER_ACCEPT));
            }
        	sb.append(Constants.LF);
        	if (null != headers.get(HttpHeader.HTTP_HEADER_CONTENT_MD5)) {
                sb.append(headers.get(HttpHeader.HTTP_HEADER_CONTENT_MD5));
            }
            sb.append(Constants.LF);
            if (null != headers.get(HttpHeader.HTTP_HEADER_CONTENT_TYPE)) {
                sb.append(headers.get(HttpHeader.HTTP_HEADER_CONTENT_TYPE));
            }
            sb.append(Constants.LF);
            if (null != headers.get(HttpHeader.HTTP_HEADER_DATE)) {
                sb.append(headers.get(HttpHeader.HTTP_HEADER_DATE));
            }
        }
        sb.append(Constants.LF);
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
    		signHeaderPrefixList.remove(SystemHeader.X_CA_SIGNATURE);
    		signHeaderPrefixList.remove(HttpHeader.HTTP_HEADER_ACCEPT);
    		signHeaderPrefixList.remove(HttpHeader.HTTP_HEADER_CONTENT_MD5);
    		signHeaderPrefixList.remove(HttpHeader.HTTP_HEADER_CONTENT_TYPE);
    		signHeaderPrefixList.remove(HttpHeader.HTTP_HEADER_DATE);
    		Collections.sort(signHeaderPrefixList);
    		if (null != headers) {
    			Map<String, String> sortMap = new TreeMap<String, String>();
    			sortMap.putAll(headers);
    			StringBuilder signHeadersStringBuilder = new StringBuilder();
    			for (Map.Entry<String, String> header : sortMap.entrySet()) {
                    if (isHeaderToSign(header.getKey(), signHeaderPrefixList)) {
//                    	sb.append(header.getKey());
//                    	sb.append(Constants.SPE2);
                        if (!StringUtils.isBlank(header.getValue())) {
                        	sb.append(header.getValue());
                        }
                        sb.append(Constants.LF);
                        if (0 < signHeadersStringBuilder.length()) {
                        	signHeadersStringBuilder.append(Constants.SPE1);
                        }
                        signHeadersStringBuilder.append(header.getKey());
                    }
                }
    			headers.put(SystemHeader.X_CA_SIGNATURE_HEADERS, signHeadersStringBuilder.toString());
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

        if (headerName.startsWith(Constants.CA_HEADER_TO_SIGN_PREFIX_SYSTEM)) {
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
