/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.helper.api.ApiResponseErrorHandler
 * @author	TedKwan
 * @date	11-Oct-2022
 * Description:
 *
 * History:
 * 20221011-TedKwan: Created
 */
package epc.epcsalesapi.helper.api;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;

public class ApiResponseErrorHandler implements ResponseErrorHandler {
final static Logger logger = LoggerFactory.getLogger(ApiResponseErrorHandler.class);
	
	private String identifier;
	
	public ApiResponseErrorHandler(String uuidIdentifier) {
		this.identifier = uuidIdentifier;
	}
	
	@Override
	public boolean hasError(ClientHttpResponse httpResponse) throws IOException {
		return (
			httpResponse.getStatusCode().is4xxClientError() ||
			httpResponse.getStatusCode().is5xxServerError() 
				);
	}
	
	@Override
	public void handleError(ClientHttpResponse httpResponse) throws IOException {
		if(httpResponse.getStatusCode().is5xxServerError()) {
			throw new HttpServerErrorException(httpResponse.getStatusCode(),"Server Error. API log identifier: "+ identifier, httpResponse.getBody().readAllBytes(),null);
		} else if(httpResponse.getStatusCode().is4xxClientError()) {
			throw new HttpServerErrorException(httpResponse.getStatusCode(),"Error "+httpResponse.getStatusText()+". API log identifier: "+ identifier, httpResponse.getBody().readAllBytes(),null);
		}
		
	}
}
