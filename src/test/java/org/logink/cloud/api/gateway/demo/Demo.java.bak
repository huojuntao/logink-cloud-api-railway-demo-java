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
package org.logink.cloud.api.gateway.demo;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.logink.cloud.api.gateway.demo.constant.Constants;
import org.logink.cloud.api.gateway.demo.constant.ContentType;
import org.logink.cloud.api.gateway.demo.constant.HttpHeader;
import org.logink.cloud.api.gateway.demo.constant.HttpSchema;
import org.logink.cloud.api.gateway.demo.enums.Method;

import com.alibaba.fastjson.JSON;

/**
 * Client Demo.
 */
public class Demo {
    //APP KEY
    private final static String APP_KEY = "4FD1CC4FDAA751CAE08321E1523142D9";
    // APP SECRET
    private final static String APP_SECRET = "201D54B6FF95C7CE213662970F365855";
    //API HOST
    private final static String HOST = "api.logink.org";
    //API PATH
    private final static String PATH = "/railway-query/subscribe";
    //REQUEST BODY - JSON
    private final static String BODY = "{\"taskId\":\"4028830b5f512c68015f512f22e40000\","
    		+ "\"railwayWagonNumber\":\"1640917\", "
    		+ "\"containerNumber\":\"\","
    		+ "\"shippingNodeNumber\":\"\","
    		+ "\"invoiceNumber\":\"\","
    		+ "\"placeOfDeparture\":\"\", "
    		+ "\"destination\":\"小塘西\","
    		+ "\"endDate\":\"2017-12-10\"}";
    /**
     * HTTP POST
     *
     * @throws Exception
     */
    @Test
    public void subscribe() throws Exception {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put(HttpHeader.HTTP_HEADER_ACCEPT, ContentType.CONTENT_TYPE_JSON);
        headers.put(HttpHeader.HTTP_HEADER_CONTENT_TYPE,  ContentType.CONTENT_TYPE_JSON);
        
        Request request = new Request(Method.POST_STRING, HttpSchema.HTTPS + HOST, PATH, 
        		APP_KEY, APP_SECRET, Constants.DEFAULT_TIMEOUT);
        request.setHeaders(headers);
        request.setStringBody(BODY);
        
        Response response = Client.execute(request);

        System.out.println(JSON.toJSONString(response));
    }

}
