package epc.epcsalesapi.sales;

import org.keycloak.admin.client.Keycloak;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;

import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcCpqError;
import epc.epcsalesapi.sales.bean.EpcQuote;


@Service
public class EpcQuoteHandler2 {

    private Keycloak keycloak;

    public EpcQuoteHandler2(Keycloak keycloak) {
        this.keycloak = keycloak;
    }
    
    public Object getQuote(String quoteGuid, String param) {
//        HttpHeaders headers = new HttpHeaders();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + quoteGuid + "?include=candidate";
        if(!"".equals(StringHelper.trim(param))) {
            apiUrl += "," + StringHelper.trim(param);
        }
//        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
//        String keyCloakAccessToken = "";
//      
//
//        if("Y".equals(isKeyCloakEnabled)){
//            keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
//            headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
//        }
//        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

//        return WebClient
//            .create(apiUrl)
//            .get()
//            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//            .header(HttpHeaders.AUTHORIZATION, "bearer " + keycloak.tokenManager().getAccessTokenString())
//            .retrieve()
//            .onStatus(HttpStatus::isError,
//                response -> Mono.error(new Exception("something went wrong"))
//            )
//            .bodyToMono(EpcQuote.class);
        
//        return WebClient
//            .create(apiUrl)
//            .get()
//            .header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
//            .header(HttpHeaders.AUTHORIZATION, "bearer " + keycloak.tokenManager().getAccessTokenString())
//            .exchangeToMono(response -> {
//                if(response.statusCode().equals(HttpStatus.OK)) {
//                    return response.bodyToMono(EpcQuote.class);
//                } else {
//                    return response.bodyToMono(EpcCpqError.class);
//                }
//            });
        return null;
    }
}
