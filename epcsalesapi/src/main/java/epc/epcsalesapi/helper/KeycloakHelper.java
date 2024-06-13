/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.helper.KeycloakHelper
 * @author	TedKwan
 * @date	22-Aug-2022
 * Description:
 *
 * History:
 * 20220822-TedKwan: Created
 */
package epc.epcsalesapi.helper;

import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
public class KeycloakHelper {
	private final Logger logger = LoggerFactory.getLogger(KeycloakHelper.class);

	@Autowired
    private Keycloak keycloak;
	
	
	public void addAccessToken(HttpHeaders headers) {
		
		if("Y".equals(EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED"))){
            headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keycloak.tokenManager().getAccessTokenString());
        }
		
	}
}
