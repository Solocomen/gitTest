/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.helper.api.ApiLogInterceptor
 * @author	TedKwan
 * @date	11-Oct-2022
 * Description:
 *
 * History:
 * 20221011-TedKwan: Created
 */
package epc.epcsalesapi.helper.api;

import java.io.IOException;
import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.StreamUtils;

import epc.epcsalesapi.helper.DateHelper;

public class ApiLogInterceptor implements ClientHttpRequestInterceptor {

	final static Logger logger = LoggerFactory.getLogger(ApiLogInterceptor.class);
	private String identifier;
	
	public ApiLogInterceptor(String uuidIdentifier) {
		this.identifier=uuidIdentifier;
	}
	
	@Override
	public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
		//String identifier = UUID.randomUUID().toString();
		logRequest(request, body);
		ClientHttpResponse response = execution.execute(request, body);
		logResponse(response);
		return response;
	}
	
	private void logRequest(HttpRequest request, byte[] body) throws IOException {
		//if (logger.isDebugEnabled()) {
			logger.info("========================= request begin ==============================");
			logger.info("Identifier   : {}", identifier.toString());
			logger.info("Timestamp    : {}", DateHelper.getCurrentDateTime(DateHelper.DT_FMT_FULL));
			logger.info("URI          : {}", request.getURI());
			logger.info("Method       : {}", request.getMethod());
			logger.info("Headers      : {}", request.getHeaders());
			logger.info("Request body : {}", new String(body, "UTF-8"));
			logger.info("========================= request end   ==============================");
		//}
	}
 
	private void logResponse(ClientHttpResponse response) throws IOException {
		//if (logger.isDebugEnabled()) {
			logger.info("========================= response begin ==============================");
			logger.info("Identifier   : {}", identifier.toString());
			logger.info("Timestamp    : {}", DateHelper.getCurrentDateTime(DateHelper.DT_FMT_FULL));
			logger.info("Status code  : {}", response.getStatusCode());
			logger.info("Status text  : {}", response.getStatusText());
			logger.info("Headers      : {}", response.getHeaders());
			logger.info("Response body: {}", StreamUtils.copyToString(response.getBody(), Charset.defaultCharset()));
			logger.info("========================= response end   ==============================");
		//}
	}
}
