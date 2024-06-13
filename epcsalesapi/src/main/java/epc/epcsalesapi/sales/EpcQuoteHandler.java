/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.catalogService.CatalogServiceHandler;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.KeycloakHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcAddProduct;
import epc.epcsalesapi.sales.bean.EpcAddProductToQuoteResult;
import epc.epcsalesapi.sales.bean.EpcAddProductWithCandidate;
import epc.epcsalesapi.sales.bean.EpcCharacteristicUse;
import epc.epcsalesapi.sales.bean.EpcConfiguredValue;
import epc.epcsalesapi.sales.bean.EpcConvertQuote;
import epc.epcsalesapi.sales.bean.EpcConvertQuoteResult;
import epc.epcsalesapi.sales.bean.EpcConvertQuoteResult2;
import epc.epcsalesapi.sales.bean.EpcCopyQuote;
import epc.epcsalesapi.sales.bean.EpcCopyQuoteResult;
import epc.epcsalesapi.sales.bean.EpcCpqError;
import epc.epcsalesapi.sales.bean.EpcCreateQuoteResult;
import epc.epcsalesapi.sales.bean.EpcDeleteQuoteItemResult;
import epc.epcsalesapi.sales.bean.EpcDeleteQuoteResult;
import epc.epcsalesapi.sales.bean.EpcEvaluateConfiguration;
import epc.epcsalesapi.sales.bean.EpcEvaluateQuoteItem;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.EpcQuoteItemForUpdate;
import epc.epcsalesapi.sales.bean.EpcQuoteProductCandidate;
import epc.epcsalesapi.sales.bean.EpcSmcQuote;
import epc.epcsalesapi.sales.bean.EpcSparseAdd;
import epc.epcsalesapi.sales.bean.EpcSparseAddProductToQuoteResult;
import epc.epcsalesapi.sales.bean.EpcSparseAddResult;
import epc.epcsalesapi.sales.bean.EpcSparseAddResultResponseBody;
import epc.epcsalesapi.sales.bean.EpcSubmitQuote;
import epc.epcsalesapi.sales.bean.EpcSubmitQuoteResult;
import epc.epcsalesapi.sales.bean.EpcSubmitQuoteToOrderResult;
import epc.epcsalesapi.sales.bean.EpcSubmitQuoteToOrderResult2;
import epc.epcsalesapi.sales.bean.EpcTmpDeleteQuote;
import epc.epcsalesapi.sales.bean.EpcTmpDeleteQuoteItem;
import epc.epcsalesapi.sales.bean.EpcTmpQuote;
import epc.epcsalesapi.sales.bean.EpcUpdateModifiedItemToQuoteResult;
import epc.epcsalesapi.sales.bean.EpcUpdatePortfolioToQuote;
import epc.epcsalesapi.sales.bean.EpcUpdatePortfolioToQuoteResult;
import epc.epcsalesapi.sales.bean.EpcUpdateQuote;
import epc.epcsalesapi.sales.bean.EpcValidateAndPrice;

import java.math.BigDecimal;
//import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.*;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.core.HttpHeaders;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
import java.util.concurrent.CompletableFuture;
//import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
//import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.keycloak.admin.client.Keycloak;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// added by Danny Chan on 2022-9-8: start
//import org.springframework.cache.annotation.CacheConfig;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.Cacheable;
// added by Danny Chan on 2022-9-8: end
/**
 *
 * @author KerryTsang
 */

@Service
//@CacheConfig		// added by Danny Chan on 2022-9-8
public class EpcQuoteHandler {
	
    private final Logger logger = LoggerFactory.getLogger(EpcQuoteHandler.class);

    @Autowired
    private EpcOrderHandler epcOrderHandler;

    @Autowired
    private EpcOrderAttrHandler epcOrderAttrHandler;

    @Autowired
    private EpcSecurityHelper epcSecurityHelper;

    @Autowired
    private Keycloak keycloak;
    
    @Autowired
    private KeycloakHelper keycloakHelper;

    @Autowired
    private CatalogServiceHandler catalogServiceHandler;
	
    
    public EpcCreateQuoteResult createQuote(EpcTmpQuote epcTmpQuote) {
        EpcCreateQuoteResult epcCreateQuoteResult = new EpcCreateQuoteResult();
        EpcQuote epcQuote2 = null;
        EpcCpqError epcCpqError = null;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes";
        String logStr = "[createQuote][custid:" + epcTmpQuote.getCustomerRef() + "] ";
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        HttpHeaders headers = new HttpHeaders();
        String keyCloakAccessToken = "";      
        
        try {
logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
//logger.info("{}{}{}", logStr, "keyCloakAccessToken:", keyCloakAccessToken);
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }

logger.info("{}{}", logStr, "send POST to CPQ");
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcTmpQuote>(epcTmpQuote, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");
            if(responseEntity.getStatusCodeValue() == 200) {
                // good case
                epcQuote2 = objectMapper.readValue(responseEntity.getBody(), EpcQuote.class);
                
                epcCreateQuoteResult.setCpqQuoteGUID(epcQuote2.getId());
                epcCreateQuoteResult.setCustId(epcTmpQuote.getCustomerRef());
                epcCreateQuoteResult.setResult("SUCCESS");
                epcCreateQuoteResult.setEpcQuote(epcQuote2);
            } else {
                // error
                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
logger.info("{}{}{}", logStr, "CPQ err:", objectMapper.writeValueAsString(responseEntity.getBody()));

                epcCreateQuoteResult.setErrMsg(objectMapper.writeValueAsString(responseEntity.getBody()));
                epcCreateQuoteResult.setResult("FAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return epcCreateQuoteResult;
    }
    
    
    public EpcDeleteQuoteResult deleteQuote(EpcQuote epcQuote) {
        EpcDeleteQuoteResult epcDeleteQuoteResult = new EpcDeleteQuoteResult();
        EpcCpqError epcCpqError = null;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        String quoteGuid = epcQuote.getId();
        EpcTmpDeleteQuote epcTmpDeleteQuote = new EpcTmpDeleteQuote();
        //epcTmpDeleteQuote.setQuoteLastUpdated(epcQuote.getUpdated());
        epcTmpDeleteQuote.setQuoteLastUpdated("2018-12-04 07:11:59.411Z");
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + quoteGuid;
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        String logStr = "[deleteQuote][quoteGuid:" + quoteGuid + "] ";
        

        try {
logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }

logger.info("{}{}", logStr, "send DELETE to CPQ");
        	headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.DELETE, new HttpEntity(epcTmpDeleteQuote, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");

           if(responseEntity.getStatusCodeValue() == 200) {
               // good case
logger.info("{}{}", logStr, "delete success");
               epcDeleteQuoteResult.setResult("SUCCESS");
           } else {
               // error
logger.info("{}{}{}", logStr, "error. ", responseEntity.getBody());
               epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
               epcDeleteQuoteResult.setErrMsg(epcCpqError.getResponseText());
               epcDeleteQuoteResult.setResult("FAIL");
           }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return epcDeleteQuoteResult;
    }
    
    
    public EpcDeleteQuoteItemResult deleteQuoteItem(EpcQuote epcQuote, String sigmaItemId) {
    	EpcDeleteQuoteItemResult epcDeleteQuoteItemResult = new EpcDeleteQuoteItemResult();
    	EpcCpqError epcCpqError = null;
    	RestTemplate restTemplate = new RestTemplate();
    	ResponseEntity<String> responseEntity = null;
    	ObjectMapper objectMapper = new ObjectMapper();
    	HttpHeaders headers = new HttpHeaders();
    	String iQuoteGuid = epcSecurityHelper.encodeForSQL(epcQuote.getId());
        String iSigmaItemId = epcSecurityHelper.encodeForSQL(sigmaItemId);
    	EpcTmpDeleteQuoteItem epcTmpDeleteQuoteItem = new EpcTmpDeleteQuoteItem();
    	epcTmpDeleteQuoteItem.setQuoteLastUpdated(epcQuote.getUpdated());
    	String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + iQuoteGuid + "/items/" + iSigmaItemId;
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        String logStr = "[deleteQuoteItem][quoteGuid:" + iQuoteGuid + "][sigmaItemId:" + iSigmaItemId + "] ";

        try {
logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }

logger.info("{}{}", logStr, "send DELETE to CPQ");
    		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    		responseEntity = restTemplate.exchange(apiUrl, HttpMethod.DELETE, new HttpEntity(epcTmpDeleteQuoteItem, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");

    		if(responseEntity.getStatusCodeValue() == 200) {
    			// good case
logger.info("{}{}", logStr, "delete success");
                epcDeleteQuoteItemResult.setResult("SUCCESS");
    		} else {
logger.info("{}{}{}", logStr, "error. ", responseEntity.getBody());
    			epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
    			epcDeleteQuoteItemResult.setErrMsg(epcCpqError.getResponseText());
    			epcDeleteQuoteItemResult.setResult("FAIL");
    		}
        } catch(HttpStatusCodeException hsce) {
            epcDeleteQuoteItemResult.setResult("FAIL");
            epcDeleteQuoteItemResult.setErrMsg("CPQError");
            try {
                String b = hsce.getResponseBodyAsString();
logger.info("{}{}{}", logStr, "CPQ error:", b);
                epcDeleteQuoteItemResult.setErrMsg2(objectMapper.readValue(b, EpcCpqError.class));
            } catch (Exception eeeee) {
                eeeee.printStackTrace();
            }
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    	}
    	return epcDeleteQuoteItemResult;
    }


    public HashMap getQuoteContext(String quoteGuid) {
        HashMap quoteContext = new HashMap<>();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        HttpHeaders headers = new HttpHeaders();
        String iQuoteGuid = epcSecurityHelper.encodeForSQL(quoteGuid);
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + iQuoteGuid + "/contextData";
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        String logStr = "[getQuoteContext][quoteGuid:" + iQuoteGuid + "] ";

        try {
            logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }

logger.info("{}{}", logStr, "send GET to CPQ");
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            quoteContext = objectMapper.readValue(responseEntity.getBody(), HashMap.class);
logger.info("{}{}", logStr, "Got result from CPQ");
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return quoteContext;
    }


    public boolean updateQuoteContext(String quoteGuid, HashMap quoteContext) {
        boolean isUpdate = false;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        String iQuoteGuid = epcSecurityHelper.encodeForSQL(quoteGuid);
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + iQuoteGuid + "/contextData";
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        String logStr = "[updateQuoteContext][quoteGuid:" + iQuoteGuid + "] ";
        HashMap aMap = new HashMap<>();

        try {
            logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }

            aMap.put("quoteLastUpdated", "018-12-04 07:11:59.411");
            aMap.put("contextData", quoteContext);


logger.info("{}{}", logStr, "send PUT to CPQ");
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            restTemplate.exchange(apiUrl, HttpMethod.PUT, new HttpEntity<>(aMap, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return isUpdate;
    }
    
    
    public EpcQuote getQuoteInfo(String quoteId) {
    	return getQuoteInfo(quoteId, "");
    }
    
    
    public EpcQuote getQuoteInfo(String quoteId, String param) {
        String iQuoteId = epcSecurityHelper.encodeForSQL(quoteId);
        String iParam = epcSecurityHelper.encodeForSQL(param);
        EpcQuote epcQuote = null;
        EpcCpqError epcCpqError = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + iQuoteId + "?include=candidate";
        if(!"".equals(StringHelper.trim(iParam))) {
        	apiUrl += "," + StringHelper.trim(iParam);
        }
        HashMap<String, Object> cmsItemMapping = null;
        String cmsItemMappingString = "";
        String caseId = "";
        HashMap<String, Object> contextMap = null;
        String smcOrderIdString = "";
        int orderId = 0;
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        String logStr = "[getQuoteInfo][quoteGuid:" + iQuoteId + "] ";
        HashMap<String, String> specialProductMap = epcOrderHandler.getSpecialProductMap();

        try {
logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }
        	headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

logger.info("{}{}", logStr, "send GET to CPQ");
//        	responseEntity = restTemplate.getForEntity(apiUrl, String.class, headers);
            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");

            if(responseEntity.getStatusCodeValue() == 200) {
                // good case
                epcQuote = objectMapper.readValue(responseEntity.getBody(), EpcQuote.class);
                
                // get quote context
                contextMap = epcQuote.getContextData();
                smcOrderIdString = StringHelper.trim((String)contextMap.get("SMCOrderId"));
                if(!"".equals(smcOrderIdString)) {
                	orderId = Integer.parseInt(smcOrderIdString);
                }
                // end of get quote context

                EpcQuoteItem [] epcQuoteItemArray = epcQuote.getItems();
                if(epcQuoteItemArray != null) {
                    for(int i = 0; i < epcQuoteItemArray.length; i++) {
                        EpcQuoteItem epcQuoteItem = epcQuoteItemArray[i];

                        HashMap<String, Object> metaDataLookup = epcQuoteItem.getMetaDataLookup();

                        EpcQuoteProductCandidate epcQuoteProductCandidate = convertProductCandidate(epcQuoteItem.getProductCandidate(), metaDataLookup, "N", specialProductMap);
                        epcQuoteItem.setProductCandidateObj(epcQuoteProductCandidate);
                        
                        caseId = epcQuoteProductCandidate.getId(); // smc caseId
                        
//                         print hier of epcQuoteProductCandidate
// for debug only, kerry
//                        printQuoteProductCandidate("-", epcQuoteProductCandidate);
// for debug only, kerry

                        // get cmsItemMapping
//                        cmsItemMappingString = epcOrderAttrHandler.getAttrValue(orderId, caseId, "", epcOrderAttrHandler.ATTR_TYPE_CMS_ITEM_MAPPING);
                        cmsItemMappingString = epcOrderHandler.getCmsItemMapping(orderId, caseId);
                    	if(!"".equals(cmsItemMappingString)) {
                    		cmsItemMapping = new ObjectMapper().readValue(cmsItemMappingString, HashMap.class);
                    		epcQuoteItem.setCmsItemMapping(cmsItemMapping);
                    	} else {
                    		epcQuoteItem.setCmsItemMapping(null);
                    	}
                        // end of get cmsItemMapping
                    }
                }
            } else {
                // error
                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return epcQuote;
    }

    // added by Danny Chan on 2022-9-8: start
//    @Cacheable(value="quoteCache", key="#entityId")  
    public String getEntityName(String entityId) {
        String result = null;
	
	HashMap api_result = null;
        EpcCpqError epcCpqError = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "entities/" + entityId;
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        String logStr = "[getEntityName][entityId:" + entityId + "] ";

        try {
            logger.info("{}{}", logStr, "start");

            if ("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }
	    
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

            logger.info("{}{}", logStr, "send GET to CPQ");
            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            logger.info("{}{}", logStr, "Got result from CPQ");

            if(responseEntity.getStatusCodeValue() == 200) {
		api_result = objectMapper.readValue(responseEntity.getBody(), HashMap.class);
                
		result = (String)api_result.get("name");
            } else {
                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
	
	return result;
    }

    // function to evict the cache "quoteCache"
//    @CacheEvict(value="quoteCache", allEntries=true)
    public void clearCache() {
    }
    
    // function to get build id in cache
//    @Cacheable(value = "quoteCache", key = "#root.method.name")
    public String getCachedImportId() {
        return getImportId();
    }
    
    // function to get build id of deployment (no caching)
    public String getImportId() {
        String result = null;

        HashMap api_result = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;

	ObjectMapper objectMapper = new ObjectMapper();
        //String apiUrl = "https://ccsgcatsvstg-vip.smartone.com/CS3/api/status";
	//String apiUrl = "http://localhost:8118/api/getinfo.jsp";
	String apiUrl = EpcProperty.getValue("CS_STATUS_LINK");
	
	logger.info("CS_STATUS_LINK = " + apiUrl);

        try {
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);	   
	    
            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);

            if (responseEntity.getStatusCodeValue() == 200) {
		api_result = objectMapper.readValue(responseEntity.getBody(), HashMap.class);
                
		
		result = (String)api_result.get("ImportId");
		
		logger.info("result = " + result );
		
            } else {
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
	
	if (result==null) result = "";
	
	return result;
    }
    // added by Danny Chan on 2022-9-8: end

    public EpcQuoteItem getQuoteItem(String quoteGuid, String quoteItemGuid, String param) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + quoteGuid + "/items/" + quoteItemGuid + "?include=candidate";
        if(!"".equals(StringHelper.trim(param))) {
            apiUrl += "," + StringHelper.trim(param);
        }
        EpcQuoteItem epcQuoteItem = null;
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        String logStr = "[getQuoteItem][quoteGuid:" + quoteItemGuid + "][quoteItemGuid:" + quoteItemGuid + "] ";

        try {
            logger.info("{}{}", logStr, "start");
            
            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

logger.info("{}{}", logStr, "send GET to CPQ");
            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.GET, new HttpEntity<>(headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");

            if(responseEntity.getStatusCodeValue() == 200) {
                // good case
                epcQuoteItem = objectMapper.readValue(responseEntity.getBody(), EpcQuoteItem.class);
            } else {
                // error
                epcQuoteItem = null;
logger.info("{}{}{}", logStr, "err:", responseEntity.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return epcQuoteItem;
    }
    
    
    public boolean containsCase(EpcQuote epcQuote, String caseId) {
        EpcQuoteItem[] items = epcQuote.getItems();
        EpcQuoteItem epcQuoteItem = null;
        String str = "";
        
        for(int i = 0; i < items.length; i++) {
            epcQuoteItem = items[i];
            str = epcQuoteItem.getProductCandidate().toString();
            if(str.indexOf(caseId) > -1) {
                return true;
            }
        }
        return false;
    }
    
    
    public String determineCaseIdByItemId(EpcQuote epcQuote, String itemId) {
    	String caseId = "";
    	String str = "";
    	for(EpcQuoteItem item : epcQuote.getItems()) {
    		str = item.getProductCandidate().toString();
    		if(str.indexOf(itemId) > -1) {
    			caseId = StringHelper.trim((String)item.getProductCandidate().get("ID"));
    			break;
    		}
    	}
    	return caseId;
    }
    
    
    public void printQuoteProductCandidate(String indent, EpcQuoteProductCandidate epcQuoteProductCandidate) {
//System.out.println("xxxxxxx " + indent + epcQuoteProductCandidate.getId() + " " + epcQuoteProductCandidate.getEntityID());
System.out.println(indent + epcQuoteProductCandidate.getId() + " " + epcQuoteProductCandidate.getEntityID());
if("Mobile Product Spec".equals(epcQuoteProductCandidate.getCpqItemDesc())) {
System.out.println("~~~ " + epcQuoteProductCandidate.getId() + ":" + epcQuoteProductCandidate.getConfiguredValue());
}
//logger.info("log4j:" + indent + epcQuoteProductCandidate.getId() + " " + epcQuoteProductCandidate.getEntityID());
        if(epcQuoteProductCandidate.getChildEntity() != null) {
            for(int i = 0; i < epcQuoteProductCandidate.getChildEntity().size(); i++) {
                printQuoteProductCandidate(indent + "-", epcQuoteProductCandidate.getChildEntity().get(i));
            }
        } else {
            return;
        }
    }
    
    
    public void getAllDeviceItems(TreeMap<String, EpcQuoteProductCandidate> itemMap, EpcQuoteProductCandidate epcQuoteProductCandidate) {
    	String id = epcQuoteProductCandidate.getId();
    	if(EpcItemCategory.DEVICE.equals(epcQuoteProductCandidate.getItemCat())) {
    		itemMap.put(id, epcQuoteProductCandidate);
    	}

    	for(EpcQuoteProductCandidate child: epcQuoteProductCandidate.getChildEntity()) {
    		getAllDeviceItems(itemMap, child);
    	}
    }
    
    
    public void getAllDeviceItems(TreeMap<String, EpcQuoteProductCandidate> itemMap, TreeMap<String, String> caseItemMap, EpcQuoteProductCandidate epcQuoteProductCandidate, String caseId) {
    	String id = epcQuoteProductCandidate.getId();
    	if(EpcItemCategory.DEVICE.equals(epcQuoteProductCandidate.getItemCat())) {
    		itemMap.put(id, epcQuoteProductCandidate);
    		caseItemMap.put(id, caseId); // item id, case id
    	}

    	for(EpcQuoteProductCandidate child: epcQuoteProductCandidate.getChildEntity()) {
    		getAllDeviceItems(itemMap, child);
    	}
    }
    
    
    public void getMainLineItem(TreeMap<String, EpcQuoteProductCandidate> itemMap, EpcQuoteProductCandidate epcQuoteProductCandidate) {
    	String id = epcQuoteProductCandidate.getId();
    	if(EpcItemCategory.MAIN_LINE.equals(epcQuoteProductCandidate.getItemCat())) { // the "mobile product spec" entity 
    		itemMap.put(id, epcQuoteProductCandidate);
    	}

    	for(EpcQuoteProductCandidate child: epcQuoteProductCandidate.getChildEntity()) {
    		getMainLineItem(itemMap, child);
    	}
    }


    /***
     * 
     * @param epcQuoteProductCandidateMap
     * @param metaDataLookup
     * @param premium - set to Y start from the premium group entity to all its children
     * @return
     */
    public EpcQuoteProductCandidate convertProductCandidate(
        HashMap<String, Object> epcQuoteProductCandidateMap, HashMap<String, Object> metaDataLookup, String premium, HashMap<String, String> specialProductMap
    ) {
        EpcQuoteProductCandidate epcQuoteProductCandidate = new EpcQuoteProductCandidate();
        ArrayList<EpcQuoteProductCandidate> childList = new ArrayList<EpcQuoteProductCandidate>();
        String tmpFactGuid = "";
        String tmpFactValue = "";
        boolean isPrimaryLine = false;
        
        epcQuoteProductCandidate.setId((String)epcQuoteProductCandidateMap.get("ID"));
        epcQuoteProductCandidate.setEntityID((String)epcQuoteProductCandidateMap.get("EntityID"));
        
        // get desc from metadatalookup
        HashMap<String, Object> childMap = (HashMap<String, Object>)metaDataLookup.get(epcQuoteProductCandidate.getId());
        String tmpDesc = StringHelper.trim((String)childMap.get("name"));
        String tmpDescChi = StringHelper.trim((String)childMap.get("name"));
        epcQuoteProductCandidate.setCpqItemDesc(tmpDesc);
        epcQuoteProductCandidate.setCpqItemDescChi(tmpDescChi);
        epcQuoteProductCandidate.setCatalogItemDesc(tmpDesc); // requested by william, 202302
        // end of get desc from metadatalookup
        
        // get ExactType
        String exactType = StringHelper.trim((String)epcQuoteProductCandidateMap.get("ExactType"));
        // end of get ExactType
        
        // get typePath, template name
        String templateName = ""; // i.e. "typePath": "Handset_Offer_Specification/Package/Product/Launch_Entity"
        String[] typePathArray = null;
        String typePath = StringHelper.trim((String)childMap.get("typePath"));
        if(!"".equals(typePath)) {
        	typePathArray = typePath.split("/");
        	if(typePathArray != null && typePathArray.length > 0) {
        		templateName = typePathArray[0];
        	}
        }

        epcQuoteProductCandidate.setTemplateName(templateName);
        // end of get typePath, template name

        // alter and set premium value
        if("Pre_Order_Premium_Group_Specifications".equals(templateName)
          || "Premium_Group_Offer_Specification".equals(templateName)
        ) {
        	premium = "Y";
        }

        epcQuoteProductCandidate.setPremium(premium);
        // end of alter and set premium value
        
        // get ConfiguredValue
        EpcConfiguredValue epcConfiguredValue = null;
        ArrayList<EpcConfiguredValue> epcConfiguredValueList = new ArrayList<EpcConfiguredValue>();
        ArrayList<HashMap<String, Object>> configuredValueList = (ArrayList<HashMap<String, Object>>)epcQuoteProductCandidateMap.get("ConfiguredValue");
        for(int i = 0; i < configuredValueList.size(); i++) {
            HashMap<String, Object> cMap = configuredValueList.get(i);

            epcConfiguredValue = new EpcConfiguredValue();
            epcConfiguredValueList.add(epcConfiguredValue);
            
            epcConfiguredValue.setName(StringHelper.trim((String)cMap.get("UseArea")));
            epcConfiguredValue.setId(StringHelper.trim((String)cMap.get("CharacteristicID")));

            ArrayList<HashMap<String, Object>> valueList = (ArrayList<HashMap<String, Object>>)cMap.get("Value");
            for(int ii = 0; ii < valueList.size(); ii++) {
                HashMap<String, Object> vMap = valueList.get(ii);
                // Updated by WilliamTam on 28 Mar 2023, to reverse order to get the configured value
                // use "Value" as key to get value first, then use "ValueDetail" as key 
                //if(vMap.containsKey("ValueDetail") && !"".equals(StringHelper.trim((String)vMap.get("ValueDetail")))) {
                //    epcConfiguredValue.setValue(StringHelper.trim((String)vMap.get("ValueDetail")));
                //} else if(vMap.containsKey("Value") && !"".equals(StringHelper.trim((String)vMap.get("Value")))) {
                //    epcConfiguredValue.setValue(StringHelper.trim((String)vMap.get("Value")));
                //}
                if(vMap.containsKey("Value") && !"".equals(StringHelper.trim((String)vMap.get("Value")))) {
                    epcConfiguredValue.setValue(StringHelper.trim((String)vMap.get("Value")));
                } else if (vMap.containsKey("ValueDetail") && !"".equals(StringHelper.trim((String)vMap.get("ValueDetail")))) {
                    epcConfiguredValue.setValue(StringHelper.trim((String)vMap.get("ValueDetail")));
                }
            }

            // special for master_coupon_id, kerrytsang, 20230301
            //  only refer to "Value", not "ValueDetail"
            if("Master_Voucher_Id".equals(epcConfiguredValue.getName())) {
                for(int ii = 0; ii < valueList.size(); ii++) {
                    HashMap<String, Object> vMap = valueList.get(ii);
                    if(vMap.containsKey("Value") && !"".equals(StringHelper.trim((String)vMap.get("Value")))) {
                        epcConfiguredValue.setValue(StringHelper.trim((String)vMap.get("Value")));
                    }
                }
            }
            // end of special for master_coupon_id, kerrytsang, 20230301
        }
        epcQuoteProductCandidate.setConfiguredValue(epcConfiguredValueList);
        // end of get ConfiguredValue
        
        // get CharacteristicUse
        EpcCharacteristicUse epcCharacteristicUse = null;
        ArrayList<EpcCharacteristicUse> epcCharacteristicUseList = new ArrayList<EpcCharacteristicUse>();
        ArrayList<HashMap<String, Object>> characteristicUseList = (ArrayList<HashMap<String, Object>>)epcQuoteProductCandidateMap.get("CharacteristicUse");
        for(int i = 0; i < characteristicUseList.size(); i++) {
            HashMap<String, Object> cMap = characteristicUseList.get(i);
            
            epcCharacteristicUse = new EpcCharacteristicUse();
            epcCharacteristicUseList.add(epcCharacteristicUse);
            
            epcCharacteristicUse.setId(StringHelper.trim((String)cMap.get("CharacteristicID")));
            epcCharacteristicUse.setName(StringHelper.trim((String)cMap.get("UseArea")));
            
            ArrayList<HashMap<String, Object>> epcValueList = (ArrayList<HashMap<String, Object>>)cMap.get("Value");
            ArrayList<String> valueList = new ArrayList<String>();
            for(int ii = 0; ii < epcValueList.size(); ii++) {
                HashMap<String, Object> vMap = epcValueList.get(ii);
                if(vMap.containsKey("ValueDetail")) {
                    valueList.add(StringHelper.trim((String)vMap.get("ValueDetail")));
                } else if(vMap.containsKey("ValueID")) {
                    tmpFactGuid = StringHelper.trim((String)vMap.get("ValueID"));
                    tmpFactValue = catalogServiceHandler.getFactValueByFactGuid(tmpFactGuid);
                    valueList.add(tmpFactValue);
                }
            }
            valueList.sort(Comparator.naturalOrder());
            epcCharacteristicUse.setValue(valueList);
        }
        epcQuoteProductCandidate.setCharacteristicUse(epcCharacteristicUseList);
        // end of get CharacteristicUse
        
        // get bill code & product code
        if(epcQuoteProductCandidateMap.containsKey("RateAttribute") && epcQuoteProductCandidateMap.get("RateAttribute") != null) {
            ArrayList<HashMap<String, Object>> bList = (ArrayList<HashMap<String, Object>>)epcQuoteProductCandidateMap.get("RateAttribute");
            for(int i = 0; i < bList.size(); i++) {
                HashMap<String, Object> bCodeMap = bList.get(i);
                String tmpName = StringHelper.trim((String)bCodeMap.get("Name"));
                String tmpValue = StringHelper.trim((String)bCodeMap.get("Value"));
                if("Bill_Code".equals(tmpName)) {
                    epcQuoteProductCandidate.setCpqItemValue(tmpName);
                    if("".equals(StringHelper.trim(epcQuoteProductCandidate.getItemCode()))) {
                        epcQuoteProductCandidate.setItemCode(tmpValue);
                    } else {
                        epcQuoteProductCandidate.setItemCode(StringHelper.trim(epcQuoteProductCandidate.getItemCode()) + "," + tmpValue);
                    }
                } else if ("Product_Code".equals(tmpName)) {
                    epcQuoteProductCandidate.setCpqItemValue(tmpName);
                    epcQuoteProductCandidate.setItemCode(tmpValue);
                } else if("Product_Description".equals(tmpName)) {
                    epcQuoteProductCandidate.setCatalogItemDesc(tmpValue);
                } else if("RRP".equals(tmpName)) {
                    if("".equals(tmpValue)) {
                        epcQuoteProductCandidate.setCatalogRrp(null);
                    } else {
                        epcQuoteProductCandidate.setCatalogRrp(new BigDecimal(tmpValue));
                    }
                } 
                // added by Danny Chan on 2023-1-31: start
                else if ("Other".equals(tmpName)) {
                    if (epcQuoteProductCandidate.getCatalogRrp()==null) {
                        if("".equals(tmpValue)) {
                            epcQuoteProductCandidate.setCatalogRrp(null);
                        } else {
                            epcQuoteProductCandidate.setCatalogRrp(new BigDecimal(tmpValue));
                        }
                    }
                }
                // added by Danny Chan on 2023-1-31: end
                
                if ("Installment_Handling".equals(tmpName)) {
                    epcQuoteProductCandidate.setItemCode2(tmpValue);
                }
            }
        }
        // end of get bill code & product code
        
        // get charge amount & define charge item
        if(epcQuoteProductCandidateMap.containsKey("Rate") && epcQuoteProductCandidateMap.get("Rate") != null) {
        	epcQuoteProductCandidate.setItemCat(EpcItemCategory.CHARGE);

            HashMap<String, Object> rList = (HashMap<String, Object>)epcQuoteProductCandidateMap.get("Rate");
            if(rList.get("Value") != null) {
                //String tmpRate = ((java.math.BigDecimal)rList.get("Value")).toString();
                //String tmpRate = StringHelper.trim((String)rList.get("Value"));
                String tmpRate = "";
                if(rList.get("Value") instanceof String) {
                    tmpRate = StringHelper.trim((String)rList.get("Value"));
                    if("".equals(tmpRate)) {
                        epcQuoteProductCandidate.setItemCharge(null);
                    } else {
                        epcQuoteProductCandidate.setItemCharge(new BigDecimal(tmpRate));
                    }
                } else if(rList.get("Value") instanceof Integer) {
                	epcQuoteProductCandidate.setItemCharge(new BigDecimal((Integer)rList.get("Value")));
                } else if(rList.get("Value") instanceof Long) {
                	epcQuoteProductCandidate.setItemCharge(new BigDecimal((Long)rList.get("Value")));
                } else if(rList.get("Value") instanceof Double) {
                	epcQuoteProductCandidate.setItemCharge(new BigDecimal((Double)rList.get("Value")));
                } else if(rList.get("Value") instanceof Float) {
                	epcQuoteProductCandidate.setItemCharge(new BigDecimal((Float)rList.get("Value")));
                } else {
                    // assume it is a bigdecimal
//                    tmpRate = ((java.math.BigDecimal)rList.get("Value")).toString();
                    epcQuoteProductCandidate.setItemCharge((BigDecimal)rList.get("Value"));
                }
//                epcQuoteProductCandidate.setCpqItemValue(tmpRate);
            }
        }
        // end of get charge amount & define charge item
        
        // vas
        if("VAS_Charge".equals(exactType)) {
        	epcQuoteProductCandidate.setCpqItemValue(exactType); // used for parent node to determine itself as a vas
        }
        // end of vas
        
        
        epcQuoteProductCandidate.setChildEntity(childList);
        if(epcQuoteProductCandidateMap.containsKey("ChildEntity") && epcQuoteProductCandidateMap.get("ChildEntity") != null) {
            ArrayList<HashMap<String, Object>> cList = (ArrayList<HashMap<String, Object>>)epcQuoteProductCandidateMap.get("ChildEntity");
            for(int i = 0; i < cList.size(); i++) {
                childList.add(convertProductCandidate(cList.get(i), metaDataLookup, premium, specialProductMap));
            }
            
            //  set itemcat(DEVICE) & product code (values are set in child node - charge entity)
            for(EpcQuoteProductCandidate c : childList) {
            	if("Product_Code".equals(StringHelper.trim(c.getCpqItemValue()))) {
            		epcQuoteProductCandidate.setItemCat(EpcItemCategory.DEVICE);
                	epcQuoteProductCandidate.setItemCode(c.getItemCode());
            		break;
            	} 

                // determine line type
                if(EpcItemCategory.MAIN_LINE.equals(c.getItemCat()) || EpcItemCategory.CHILD_LINE.equals(c.getItemCat())) {
                    if("5GBB Plan Product Specification".equals(epcQuoteProductCandidate.getCpqItemDesc())) {
                        c.setItemCode("5GBB");
                    } else if("HomePhone Plus Product Spec".equals(epcQuoteProductCandidate.getCpqItemDesc())) {
                        c.setItemCode("HPP");
                    } else {
                        c.setItemCode("MOBILE");
                    }
                }
                // end of determine line type
            }

            // for gift wrapping
            if(EpcItemCategory.GIFT_WRAPPING.equals(specialProductMap.get(epcQuoteProductCandidate.getItemCode()))
              && !EpcItemCategory.CHARGE.equals(epcQuoteProductCandidate.getItemCat())
            ) {
                epcQuoteProductCandidate.setItemCat(EpcItemCategory.GIFT_WRAPPING);
            }
            // end for gift wrapping

            // for plastic bag
            if(EpcItemCategory.PLASTIC_BAG.equals(specialProductMap.get(epcQuoteProductCandidate.getItemCode()))
              && !EpcItemCategory.CHARGE.equals(epcQuoteProductCandidate.getItemCat())
            ) {
                epcQuoteProductCandidate.setItemCat(EpcItemCategory.PLASTIC_BAG);
            }
            // end for plastic bag
        } else {
        }
        
        
        // screen replace & apple care
//        if(epcQuoteProductCandidate.getCpqItemDesc().toUpperCase().indexOf("SCREEN REPLACE") > -1) {
        if("iPhone Screen Replace".equals(epcQuoteProductCandidate.getCpqItemDesc()) 
        	|| "Other Screen Replace".equals(epcQuoteProductCandidate.getCpqItemDesc()) 
        ) {
        	epcQuoteProductCandidate.setItemCat(EpcItemCategory.SCREEN_REPLACE);
//        } else if(epcQuoteProductCandidate.getCpqItemDesc().toUpperCase().indexOf("APPLECARE") > -1) {
        } else if("AppleCare+".equals(epcQuoteProductCandidate.getCpqItemDesc())) {
        	epcQuoteProductCandidate.setItemCat(EpcItemCategory.APPLECARE);
        }
        // end of screen replace & apple care
        
        
        // check by template to identify those entities
        if("Mobile_Plan_Package_Template".equals(templateName)) {
        	epcQuoteProductCandidate.setItemCat(EpcItemCategory.PLAN); // i.e. supercare plan
        } else if("Trade_In_Specification".equals(templateName)) {
        	epcQuoteProductCandidate.setItemCat(EpcItemCategory.TRADE_IN);

            for(EpcConfiguredValue ccc: epcQuoteProductCandidate.getConfiguredValue()) {
                if(ccc.getName().equals("Trade_In_Note_Reference_No")) {
                    epcQuoteProductCandidate.setCpqItemValue(ccc.getValue());
                }
            }
        } else if("Voucher_Redeem_Specification".equals(templateName)) {
        	epcQuoteProductCandidate.setItemCat(EpcItemCategory.VOUCHER_REDEEMED);
        } else if ("Master_Voucher_Specification".equals(templateName)) {
        	epcQuoteProductCandidate.setItemCat(EpcItemCategory.VOUCHER_ASSIGNED);
        } else if ("Airmiles_Specification".equals(templateName)) { // asia miles
            epcQuoteProductCandidate.setItemCat(EpcItemCategory.ASIA_MILES);

            for(EpcCharacteristicUse ccc: epcQuoteProductCandidate.getCharacteristicUse()) {
                if(ccc.getName().equals("Rule_Id")) {
                    epcQuoteProductCandidate.setCpqItemValue(ccc.getName());

                    if(ccc.getValue() != null && ccc.getValue().size() > 0) {
                        epcQuoteProductCandidate.setItemCode((String)ccc.getValue().get(0));
                    } else {
                        epcQuoteProductCandidate.setItemCode("-1");
                    }
                }
            }
        } else if("Physical_Gift_Specification".equals(templateName)) {
            epcQuoteProductCandidate.setItemCat(EpcItemCategory.DEVICE);
            for(EpcCharacteristicUse ccc: epcQuoteProductCandidate.getCharacteristicUse()) {
                if(ccc.getName().equals("Product_Code")) {
                    if(ccc.getValue() != null && ccc.getValue().size() > 0) {
                        epcQuoteProductCandidate.setItemCode((String)ccc.getValue().get(0));
                    }
                }
            }
        } else if ("Prepaid_Card".equals(templateName)) {
            epcQuoteProductCandidate.setItemCat(EpcItemCategory.SIM);
        } else if ("Payment_Program".equals(templateName)) {
            epcQuoteProductCandidate.setItemCat(EpcItemCategory.REQUIRED_PAYMENT);
            for(EpcCharacteristicUse ccc: epcQuoteProductCandidate.getCharacteristicUse()) {
                if(ccc.getName().equals("Payment_Method")) {
                    if(ccc.getValue() != null && ccc.getValue().size() == 1) { // get the only one payment code
                        epcQuoteProductCandidate.setItemCode((String)ccc.getValue().get(0));
                    }
                }
            }
        } else if ("Gift_Specification".equals(templateName)) {
            epcQuoteProductCandidate.setItemCat(EpcItemCategory.BONUS_GIFT);
        } else if ("Contract_Product_Specification".equals(templateName)) {
            epcQuoteProductCandidate.setItemCat(EpcItemCategory.CONTRACT);
        } else if ("Mobile_Line_Specification".equals(templateName)) {
            // mobile line
            for(EpcCharacteristicUse ccc: epcQuoteProductCandidate.getCharacteristicUse()) {
                if(ccc.getName().equals("isPrimary")) {
                    if(ccc.getValue() != null) {
                        for(Object s : ccc.getValue()) {
                            if(s instanceof String && "True".equals((String)s)) {
                                isPrimaryLine = true;
                            }
                        }
                    }
                }
            }

            if(isPrimaryLine) {
                epcQuoteProductCandidate.setItemCat(EpcItemCategory.MAIN_LINE);
            } else {
                epcQuoteProductCandidate.setItemCat(EpcItemCategory.CHILD_LINE);
            }
        } else if ("VAS_Atomic_Product_Specification".equals(templateName)) {
            if(!"Roaming Day Plan Group".equals(tmpDesc)) {
                epcQuoteProductCandidate.setItemCat(EpcItemCategory.VAS);
            }
        }
        // end of check by template to identify those entities
        
        
        return epcQuoteProductCandidate;
    }
    
    
    public EpcUpdatePortfolioToQuoteResult updatePortfolioToQuote(String quoteGuid, String portfolioId) {
    	return updatePortfolioToQuote(quoteGuid, portfolioId, "update"); // default
    }
    
    
    public EpcUpdatePortfolioToQuoteResult updatePortfolioToQuote(String quoteGuid, String portfolioId, String itemAction) {
        EpcUpdatePortfolioToQuote epcUpdatePortfolioToQuote = null;
        EpcUpdatePortfolioToQuoteResult epcUpdatePortfolioToQuoteResult = new EpcUpdatePortfolioToQuoteResult();
        EpcQuoteItem epcQuoteItem = null;
        EpcCpqError epcCpqError = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + quoteGuid + "/items?include=candidate";
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        
        try {
            epcUpdatePortfolioToQuote = new EpcUpdatePortfolioToQuote();
            epcUpdatePortfolioToQuote.setPortfolioItemId(portfolioId);
            epcUpdatePortfolioToQuote.setItemAction(itemAction);

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity(epcUpdatePortfolioToQuote, headers), String.class);
            
            if(responseEntity.getStatusCodeValue() == 200) {
                // good case
                epcQuoteItem = objectMapper.readValue(responseEntity.getBody(), EpcQuoteItem.class);

                epcUpdatePortfolioToQuoteResult.setEpcQuoteItem(epcQuoteItem);
                epcUpdatePortfolioToQuoteResult.setResult("SUCCESS");
            } else {
                // error
                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);

                epcUpdatePortfolioToQuoteResult.setErrMsg(epcCpqError.getResponseText());
                epcUpdatePortfolioToQuoteResult.setResult("FAIL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return epcUpdatePortfolioToQuoteResult;
    }
    
    
    public EpcSparseAddProductToQuoteResult sparseAddProductToQuote(String quoteGuid, String productGuid, EpcSparseAdd epcSparseAdd) {
        EpcSparseAddProductToQuoteResult epcSparseAddProductToQuoteResult = new EpcSparseAddProductToQuoteResult();
        EpcCpqError epcCpqError = null;
        ArrayList<HashMap<String, Object>> errorResponseBody = null;
        String errMsg = "";
        String iQuoteGuid = epcSecurityHelper.encode(quoteGuid);
        String iProductGuid = epcSecurityHelper.encode(productGuid);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        EpcSparseAddResult epcSparseAddResult = null;
        EpcSparseAddResultResponseBody epcSparseAddResultResponseBody = null;
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + iQuoteGuid + "/items/fromSelections?offerGuid=" + iProductGuid;
        String logStr = "[sparseAddProductToQuote][quoteGuid:" + iQuoteGuid + "][productGuid:" + iProductGuid + "] ";
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";

        try {
logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

logger.info("{}{}", logStr, "send POST to CPQ");
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcSparseAdd>(epcSparseAdd, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");

            if(responseEntity.getStatusCodeValue() == 200) {
                // success
                epcSparseAddResult = objectMapper.readValue(responseEntity.getBody(), EpcSparseAddResult.class);
                if(epcSparseAddResult != null) {
                    epcSparseAddResultResponseBody = epcSparseAddResult.getResponseBody();

                    if(epcSparseAddResultResponseBody != null) {
                        epcSparseAddProductToQuoteResult.setResult("SUCCESS");
                        epcSparseAddProductToQuoteResult.setQuoteItemGuids(epcSparseAddResult.getResponseBody().getSparseQuoteItemIds());
                    } else {
                        epcSparseAddProductToQuoteResult.setResult("FAIL");
                        epcSparseAddProductToQuoteResult.setErrMsg("cannot get CPQ response - response body");
                    }
                } else {
                    epcSparseAddProductToQuoteResult.setResult("FAIL");
                    epcSparseAddProductToQuoteResult.setErrMsg("cannot get CPQ response");
                }
            } else {
                // fail
                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
logger.info(logStr + "epcCpqError.getResponseCode():" + epcSecurityHelper.encode(epcCpqError.getResponseCode()));
logger.info(logStr + "epcCpqError.getResponseText():" + epcSecurityHelper.encode(epcCpqError.getResponseText()));
logger.info(logStr + "epcCpqError.getExceptionType():" + epcSecurityHelper.encode(epcCpqError.getExceptionType()));
logger.info(logStr + "epcCpqError.getResolutionText():" + epcSecurityHelper.encode(epcCpqError.getResolutionText()));

                errMsg = epcCpqError.getResponseText() + ". ";
                errorResponseBody = epcCpqError.getResponseBody();
                if(errorResponseBody != null) {
                    for(HashMap<String, Object> e : errorResponseBody) {
                        errMsg += "[" + StringHelper.trim((String)e.get("responseCode")) + "] " + StringHelper.trim((String)e.get("responseText")) + ". ";
                    }
                }

                epcSparseAddProductToQuoteResult.setResult("FAIL");
                epcSparseAddProductToQuoteResult.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcSparseAddProductToQuoteResult.setResult("FAIL");
            epcSparseAddProductToQuoteResult.setErrMsg(e.getMessage());
        }

        return epcSparseAddProductToQuoteResult;
    }

    
    public EpcAddProductToQuoteResult addProductToQuote(int smcOrderId, String quoteGuid, String productGuid) {
    	return addProductToQuote(smcOrderId, quoteGuid, productGuid, "N");
    }


    public EpcAddProductToQuoteResult addProductToQuote(int smcOrderId, String quoteGuid, String productGuid, String withSpec) {
        String iQuoteGuid = epcSecurityHelper.encodeForSQL(quoteGuid);
        String iProductGuid = epcSecurityHelper.encodeForSQL(productGuid);
        String iWithSpec = epcSecurityHelper.encodeForSQL(withSpec);
        EpcCpqError epcCpqError = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + iQuoteGuid + "/items?include=candidate,validation";
        EpcAddProduct epcAddProduct = null;
        EpcQuoteItem epcQuoteItem = null;
        EpcAddProductToQuoteResult epcAddProductToQuoteResult = new EpcAddProductToQuoteResult();
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        String logStr = "[addProductToQuote][orderId:" + smcOrderId + "][quoteGuid:" + iQuoteGuid + "][productGuid:" + iProductGuid +"]";
        
        try {
logger.info("{}{}", logStr, "start");

        	if("Y".equals(iWithSpec)) {
        		apiUrl += ",compiledSpecification";
        	}
        	
        	
            epcAddProduct = new EpcAddProduct();
            epcAddProduct.setProductId(iProductGuid);
            epcAddProduct.setItemAction("add");
            epcAddProduct.setLinkedItemId("");
            epcAddProduct.setQuoteLastUpdated("2019-08-22T10:34:47.067Z");
            
            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

            String a = objectMapper.writeValueAsString(epcAddProduct);
logger.info("{}{}{}", logStr, "add item input:", a);

logger.info("{}{}", logStr, "send POST to CPQ");
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity(epcAddProduct, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");
            
            if(responseEntity.getStatusCodeValue() == 200) {
                // good case
                epcQuoteItem = objectMapper.readValue(responseEntity.getBody(), EpcQuoteItem.class);
				logger.info("[API call]: request [POST {}]request:{}|||response:{}", apiUrl, new ObjectMapper().writeValueAsString(epcAddProduct), new ObjectMapper().writeValueAsString(epcQuoteItem));
                epcAddProductToQuoteResult.setEpcQuoteItem(epcQuoteItem);
                epcAddProductToQuoteResult.setResult("SUCCESS");
            } else {
                // error
                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
logger.info("addProductToQuote epcCpqError.getResponseCode():" + epcCpqError.getResponseCode());
logger.info("addProductToQuote epcCpqError.getResponseText():" + epcCpqError.getResponseText());
logger.info("addProductToQuote epcCpqError.getExceptionType():" + epcCpqError.getExceptionType());
logger.info("addProductToQuote epcCpqError.getResolutionText():" + epcCpqError.getResolutionText());

                epcAddProductToQuoteResult.setResult("FAIL");
                epcAddProductToQuoteResult.setErrMsg(epcCpqError.getResponseText());
            }
        } catch(HttpStatusCodeException hsce) {
            epcAddProductToQuoteResult.setResult("FAIL");
            epcAddProductToQuoteResult.setErrMsg("CPQError");
            try {
                String b = hsce.getResponseBodyAsString();
logger.info("{}{}{}", logStr, "CPQ error:", b);
                epcAddProductToQuoteResult.setErrMsg2(objectMapper.readValue(b, EpcCpqError.class));
            } catch (Exception eeeee) {
                eeeee.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcAddProductToQuoteResult.setResult("FAIL");
            epcAddProductToQuoteResult.setErrMsg(e.getMessage());
        }
        
        return epcAddProductToQuoteResult;
    }


    public EpcAddProductToQuoteResult addProductToQuoteWithCandidate(String quoteGuid, String productGuid, HashMap<String, Object> productCandidate) {
    	return addProductToQuoteWithCandidate(quoteGuid, productGuid, productCandidate, "N");
    }


    /**
     * add package to a quote with product candidate
     *  mainly used for moving quote item in tmp quote to normal quote (shopping bag)
     *  or from normal quote to checkout quote
     * 
     * @param quoteGuid
     * @param productGuid
     * @param productCandidate
     * @param withSpec
     * @return
     */
    public EpcAddProductToQuoteResult addProductToQuoteWithCandidate(String quoteGuid, String productGuid, HashMap<String, Object> productCandidate, String withSpec) {
        EpcCpqError epcCpqError = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + quoteGuid + "/items?include=candidate";
        EpcQuoteProductCandidate epcQuoteProductCandidate = null;
        EpcAddProductWithCandidate epcAddProductWithCandidate = null;
        EpcQuoteItem epcQuoteItem = null;
        EpcAddProductToQuoteResult epcAddProductToQuoteResult = new EpcAddProductToQuoteResult();
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        String logStr = "[addProductToQuoteWithCandidate][quoteGuid:" + quoteGuid + "][productGuid:" + productGuid + "] ";
        HashMap<String, String> specialProductMap = epcOrderHandler.getSpecialProductMap();

        
        try {
logger.info("{}{}", logStr, "start");

            if("Y".equals(withSpec)) {
                apiUrl += ",compiledSpecification";
            }
            
           
            epcAddProductWithCandidate = new EpcAddProductWithCandidate();
            epcAddProductWithCandidate.setProductId(productGuid);
            epcAddProductWithCandidate.setItemAction("add");
            epcAddProductWithCandidate.setLinkedItemId("");
            epcAddProductWithCandidate.setQuoteLastUpdated("2019-08-22T10:34:47.067Z");
            epcAddProductWithCandidate.setProductCandidate(productCandidate);
            
            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

logger.info("{}{}", logStr, "send POST to CPQ");
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity(epcAddProductWithCandidate, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");
            
            if(responseEntity.getStatusCodeValue() == 200) {
                // good case
                epcQuoteItem = objectMapper.readValue(responseEntity.getBody(), EpcQuoteItem.class);
                epcQuoteProductCandidate = convertProductCandidate(epcQuoteItem.getProductCandidate(), epcQuoteItem.getMetaDataLookup(), "N", specialProductMap);
                epcQuoteItem.setProductCandidateObj(epcQuoteProductCandidate);

                epcAddProductToQuoteResult.setEpcQuoteItem(epcQuoteItem);
                epcAddProductToQuoteResult.setResult("SUCCESS");
            } else {
                // error
                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
logger.info("{}{}{}", logStr, "addProductToQuote epcCpqError.getResponseCode():", epcCpqError.getResponseCode());
logger.info("{}{}{}", logStr, "addProductToQuote epcCpqError.getResponseText():", epcCpqError.getResponseText());
logger.info("{}{}{}", logStr, "addProductToQuote epcCpqError.getExceptionType():", epcCpqError.getExceptionType());
logger.info("{}{}{}", logStr, "addProductToQuote epcCpqError.getResolutionText():", epcCpqError.getResolutionText());

                epcAddProductToQuoteResult.setResult("FAIL");
                epcAddProductToQuoteResult.setErrMsg(epcCpqError.getResponseText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return epcAddProductToQuoteResult;
    }
    
    
    public EpcUpdateModifiedItemToQuoteResult updateModifiedItemToQuote(int smcOrderId, String quoteGuid, String itemGuid, EpcQuoteItemForUpdate epcQuoteItemForUpdate) {
        EpcUpdateModifiedItemToQuoteResult epcUpdateModifiedItemToQuoteResult = new EpcUpdateModifiedItemToQuoteResult();
        EpcCpqError epcCpqError = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        EpcQuoteItem returnQuoteItem = null;
        EpcQuoteProductCandidate epcQuoteProductCandidate = null;
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + quoteGuid + "/items/" + itemGuid + "?include=validation,candidate";
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        HashMap<String, Object> currentValidation = null;
        ArrayList<HashMap<String, Object>> errors = null;
        boolean valid = false;
        HashMap<String, String> specialProductMap = epcOrderHandler.getSpecialProductMap();
        String logStr = "[updateModifiedItemToQuote][orderId:" + smcOrderId + "][quoteGuid:" + quoteGuid + "][itemGuid:" + itemGuid + "] ";
        
        try {
logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }
            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

            String a = objectMapper.writeValueAsString(epcQuoteItemForUpdate);
logger.info("{}{}{}", logStr, "PUT call input:", a);            

logger.info("{}{}", logStr, "send PUT to CPQ");
            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.PUT, new HttpEntity(epcQuoteItemForUpdate, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");

            if(responseEntity.getStatusCodeValue() == 200) {
            	returnQuoteItem = objectMapper.readValue(responseEntity.getBody(), EpcQuoteItem.class);
                currentValidation = returnQuoteItem.getCurrentValidation();
                if(currentValidation != null) {
                    valid = ((Boolean)currentValidation.get("valid")).booleanValue();
                    if(valid) {
                        // good case
                        epcQuoteProductCandidate = convertProductCandidate(returnQuoteItem.getProductCandidate(), returnQuoteItem.getMetaDataLookup(), "N", specialProductMap);
                        returnQuoteItem.setProductCandidateObj(epcQuoteProductCandidate);
						logger.info("[API call]: request [POST {}]request:{}|||response:{}", apiUrl, new ObjectMapper().writeValueAsString(epcQuoteItemForUpdate), new ObjectMapper().writeValueAsString(returnQuoteItem));
                        epcUpdateModifiedItemToQuoteResult.setResult("SUCCESS");
                        epcUpdateModifiedItemToQuoteResult.setEpcQuoteItem(returnQuoteItem);
                    } else {
                        // currentValidation != true
                        epcUpdateModifiedItemToQuoteResult.setResult("FAIL");
                        epcUpdateModifiedItemToQuoteResult.setErrMsg("entity currentValidation is false");
                        epcUpdateModifiedItemToQuoteResult.setErrMsg2(currentValidation);
                    }
                } else {
                    // currentValidation not found
                    epcUpdateModifiedItemToQuoteResult.setResult("FAIL");
                    epcUpdateModifiedItemToQuoteResult.setErrMsg("entity currentValidation is not found");
                    epcUpdateModifiedItemToQuoteResult.setErrMsg2(null);
                }
            } else {
                // error
                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
logger.info(logStr + "error:" + epcSecurityHelper.encode(responseEntity.getBody()));

                epcUpdateModifiedItemToQuoteResult.setResult("FAIL");
                epcUpdateModifiedItemToQuoteResult.setErrMsg(epcCpqError.getResponseText());
                epcUpdateModifiedItemToQuoteResult.setErrMsg2(null);
            }
        } catch(HttpStatusCodeException hsce) {
            epcUpdateModifiedItemToQuoteResult.setResult("FAIL");
            epcUpdateModifiedItemToQuoteResult.setErrMsg("CPQError");
            try {
                String b = hsce.getResponseBodyAsString();
logger.info("{}{}{}", logStr, "CPQ error:", b);
                epcUpdateModifiedItemToQuoteResult.setErrMsg2(objectMapper.readValue(b, HashMap.class));
            } catch (Exception eeeee) {
                eeeee.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcUpdateModifiedItemToQuoteResult.setResult("FAIL");
            epcUpdateModifiedItemToQuoteResult.setErrMsg(e.getMessage());
            epcUpdateModifiedItemToQuoteResult.setErrMsg2(null);
        } finally {
        }
        return epcUpdateModifiedItemToQuoteResult;
    }


//    public EpcUpdateModifiedItemToQuoteResult evaluateQuoteItem(String quoteGuid, String itemGuid, EpcQuoteItemForUpdate epcQuoteItemForUpdate) {
//        EpcUpdateModifiedItemToQuoteResult epcUpdateModifiedItemToQuoteResult = new EpcUpdateModifiedItemToQuoteResult();
//        EpcCpqError epcCpqError = null;
//        RestTemplate restTemplate = new RestTemplate();
//        HttpHeaders headers = new HttpHeaders();
//        ResponseEntity<String> responseEntity = null;
//        ObjectMapper objectMapper = new ObjectMapper();
//        EpcQuoteItem returnQuoteItem = null;
//        EpcQuoteProductCandidate epcQuoteProductCandidate = null;
//        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + quoteGuid + "/items/" + itemGuid + "/evaluateRules";
//        String logStr = "[evaluateQuoteItem][quoteGuid:" + quoteGuid + "][itemGuid:" + itemGuid + "] ";
//        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
//        String keyCloakAccessToken = "";
//        
//        try {
//logger.info("{}{}", logStr, "start");
//
//            if("Y".equals(isKeyCloakEnabled)){
//                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
//                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
//            }
//            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
//
//logger.info("{}{}", logStr, "send POST to CPQ");
//            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, new HttpEntity(epcQuoteItemForUpdate, headers), String.class);
//logger.info("{}{}", logStr, "Got result from CPQ");
//
//            if(responseEntity.getStatusCodeValue() == 200) {
//                // good case
//            	returnQuoteItem = objectMapper.readValue(responseEntity.getBody(), EpcQuoteItem.class);
//            	epcQuoteProductCandidate = convertProductCandidate(returnQuoteItem.getProductCandidate(), returnQuoteItem.getMetaDataLookup(), "N");
//            	returnQuoteItem.setProductCandidateObj(epcQuoteProductCandidate);
//            	
//                epcUpdateModifiedItemToQuoteResult.setResult("SUCCESS");
//                epcUpdateModifiedItemToQuoteResult.setEpcQuoteItem(returnQuoteItem);
//            } else {
//                // error
//                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
//logger.info(logStr + "error:" + epcSecurityHelper.encode(responseEntity.getBody()));
//
//                epcUpdateModifiedItemToQuoteResult.setErrMsg(epcCpqError.getResponseText());
//                epcUpdateModifiedItemToQuoteResult.setResult("FAIL");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//        }
//        return epcUpdateModifiedItemToQuoteResult;
//    }


    public EpcEvaluateQuoteItem evaluateQuoteItem(EpcEvaluateQuoteItem epcEvaluateQuoteItem) {
        EpcEvaluateQuoteItem epcEvaluateQuoteItemReturn = new EpcEvaluateQuoteItem();
//        EpcCpqError epcCpqError = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String quoteGuid = "";
        String itemGuid = "";
        EpcEvaluateConfiguration epcEvaluateConfiguration = epcEvaluateQuoteItem.getConfiguration();
        if(epcEvaluateConfiguration != null) {
            quoteGuid = epcSecurityHelper.encodeForSQL(epcEvaluateConfiguration.getId());
            itemGuid = epcSecurityHelper.encodeForSQL(epcEvaluateConfiguration.getItemId());
        }
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "configuration/candidateConfiguration/evaluateRules";
        String logStr = "[evaluateQuoteItem][quoteGuid:" + quoteGuid + "][itemGuid:" + itemGuid + "] ";
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        
        try {
logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }
//            headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

logger.info("{}{}", logStr, "send POST to CPQ");
            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, new HttpEntity(epcEvaluateQuoteItem, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");

            epcEvaluateQuoteItemReturn = objectMapper.readValue(responseEntity.getBody(), EpcEvaluateQuoteItem.class);
logger.info("[API call]: request [POST {}]request:{}|||response:{}", apiUrl, new ObjectMapper().writeValueAsString(epcEvaluateQuoteItem), new ObjectMapper().writeValueAsString(epcEvaluateQuoteItemReturn));				
            
            epcEvaluateQuoteItemReturn.setResult("SUCCESS");
        } catch(HttpStatusCodeException hsce) {
            epcEvaluateQuoteItemReturn.setResult("FAIL");
            epcEvaluateQuoteItemReturn.setErrMsg("CPQError");
            try {
                String b = hsce.getResponseBodyAsString();
logger.info("{}{}{}", logStr, "CPQ error:", b);
                epcEvaluateQuoteItemReturn.setErrMsg2(objectMapper.readValue(b, HashMap.class));
            } catch (Exception eeeee) {
                eeeee.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcEvaluateQuoteItemReturn.setResult("FAIL");
            epcEvaluateQuoteItemReturn.setErrMsg(e.getMessage());
        } finally {
        }
        return epcEvaluateQuoteItemReturn;
    }
    
    
//    public EpcSubmitQuoteToOrderResult2 submitQuoteToOrder(EpcQuote[] epcQuoteArray) {
//    	EpcSubmitQuoteToOrderResult2 epcSubmitQuoteToOrderResult2 = new EpcSubmitQuoteToOrderResult2();
//    	epcSubmitQuoteToOrderResult2.setResult("SUCCESS");
//    	epcSubmitQuoteToOrderResult2.setErrMsg("");
//    	ArrayList<EpcSubmitQuoteToOrderResult> epcSubmitQuoteToOrderResultList = new ArrayList<EpcSubmitQuoteToOrderResult>();
//    	epcSubmitQuoteToOrderResult2.setEpcSubmitQuoteToOrderResultList(epcSubmitQuoteToOrderResultList);
//    	EpcSubmitQuoteToOrderResult epcSubmitQuoteToOrderResult = null;
//    	for(EpcQuote q : epcQuoteArray) {
//    		epcSubmitQuoteToOrderResult = submitQuoteToOrder(q);
//    		epcSubmitQuoteToOrderResultList.add(epcSubmitQuoteToOrderResult);
//    		
//    		if(!"SUCCESS".equals(epcSubmitQuoteToOrderResult.getResult())) {
//    			epcSubmitQuoteToOrderResult2.setResult("FAIL");
//    			epcSubmitQuoteToOrderResult2.setErrMsg("[quoteId:" + q.getId() + "] err:" + epcSubmitQuoteToOrderResult.getErrMsg());
////    			break;
//    		}
//    	}
//    	return epcSubmitQuoteToOrderResult2;
//    }
    
    
    public EpcSubmitQuoteToOrderResult2 submitQuoteToOrderAsync(ArrayList<EpcSmcQuote> quoteList) {
    	EpcSubmitQuoteToOrderResult2 epcSubmitQuoteToOrderResult2 = new EpcSubmitQuoteToOrderResult2();
    	epcSubmitQuoteToOrderResult2.setResult("SUCCESS");
    	epcSubmitQuoteToOrderResult2.setErrMsg("");
    	ArrayList<EpcSubmitQuoteToOrderResult> epcSubmitQuoteToOrderResultList = new ArrayList<EpcSubmitQuoteToOrderResult>();
    	epcSubmitQuoteToOrderResult2.setEpcSubmitQuoteToOrderResultList(epcSubmitQuoteToOrderResultList);
    	EpcSubmitQuoteToOrderResult epcSubmitQuoteToOrderResult = null;
    	ArrayList<CompletableFuture<EpcSubmitQuoteToOrderResult>> futureList = new ArrayList<CompletableFuture<EpcSubmitQuoteToOrderResult>>();
    	CompletableFuture<EpcSubmitQuoteToOrderResult> future = null;
    	CompletableFuture<Void> combinedFuture = null;
    	
    	try {
	    	for(EpcSmcQuote q : quoteList) {
//	    		future = CompletableFuture.completedFuture(q).thenApplyAsync(s -> submitQuoteToOrder(s.getOrderId(), s.getQuoteId(), s.getEpcQuote()));
                future = CompletableFuture.completedFuture(q).thenApplyAsync(s -> submitQuoteToOrder(s.getOrderId(), s.getQuoteId(), s.getQuoteGuid()));
	    		futureList.add(future);
	    	}
    	
	    	combinedFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
	    	combinedFuture.get();
	    	
	    	for(CompletableFuture<EpcSubmitQuoteToOrderResult> f : futureList) {
	    		epcSubmitQuoteToOrderResult = f.get();
	    		
	    		epcSubmitQuoteToOrderResultList.add(epcSubmitQuoteToOrderResult);
	    		if(!"SUCCESS".equals(epcSubmitQuoteToOrderResult.getResult())) {
	    			epcSubmitQuoteToOrderResult2.setResult("FAIL");
	    			epcSubmitQuoteToOrderResult2.setErrMsg("[quoteId:" + epcSubmitQuoteToOrderResult.getQuoteId() + "] err:" + epcSubmitQuoteToOrderResult.getErrMsg());
	    		}
	    	}
    	} catch (Exception e) {
    		e.printStackTrace();
    		
    		epcSubmitQuoteToOrderResult2.setResult("FAIL");
    		epcSubmitQuoteToOrderResult2.setErrMsg(e.getMessage());
    	}
    	
    	return epcSubmitQuoteToOrderResult2;
    }


    public void submitQuoteToOrderAsync2(ArrayList<EpcSmcQuote> quoteList) {
        try {
            for(EpcSmcQuote q : quoteList) {
                CompletableFuture.completedFuture(q).thenApplyAsync(s -> submitQuoteToOrder(s.getOrderId(), s.getQuoteId(), s.getQuoteGuid()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    public EpcConvertQuoteResult2 convertQuote(String quoteGuid) {
        String iQuoteGuid = epcSecurityHelper.encodeForSQL(quoteGuid);
    	EpcConvertQuoteResult2 epcConvertQuoteResult2 = new EpcConvertQuoteResult2();
    	epcConvertQuoteResult2.setOriginQuoteGuid(iQuoteGuid);
    	RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        EpcConvertQuote epcConvertQuote = null;
        EpcConvertQuoteResult epcConvertQuoteResult = null;
        String newQuoteId = ""; // new quote (after success conversion - type 3)
        java.util.Date createDate = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        EpcCpqError epcCpqError = null;
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + iQuoteGuid + "/convertToOrder";
        String logStr = "[convertQuote][quoteGuid:" + iQuoteGuid + "] ";
        HttpHeaders headers = new HttpHeaders();
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        
        try {
logger.info(logStr + "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }

			epcConvertQuote = new EpcConvertQuote();
			epcConvertQuote.setQuoteLastUpdated("2018-12-04 07:11:59.411Z"); // no checking now
			epcConvertQuote.setExternalOrderId("");
			epcConvertQuote.setActivationDate(sdf.format(createDate)); // sysdate ???

logger.info("{}{}", logStr, "send POST to CPQ");
			responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcConvertQuote>(epcConvertQuote, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");

			if(responseEntity.getStatusCodeValue() == 200) {
				epcConvertQuoteResult = objectMapper.readValue(responseEntity.getBody(), EpcConvertQuoteResult.class);
                newQuoteId = epcConvertQuoteResult.getId();

// this update will be done in epcOrderHandler.validateOrder(), kerrytsang, 20210225
//                // update new quote (type 3) to epc table
//                epcOrderHandler.updateConvertedQuoteToOrder(epcQuote.getId(), newQuoteId);
                epcConvertQuoteResult2.setResult("SUCCESS");
                epcConvertQuoteResult2.setNewQuoteGuid(newQuoteId);
                epcConvertQuoteResult2.setOrderLastUpdated(epcConvertQuoteResult.getOrderLastUpdated());
			} else {
				// error
              epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
              epcConvertQuoteResult2.setResult("FAIL");
              epcConvertQuoteResult2.setErrMsg(epcCpqError.getExceptionType() + "|" + epcCpqError.getResponseText());
			}
        } catch (Exception e) {
            e.printStackTrace();
            
            epcConvertQuoteResult2.setResult("FAIL");
            epcConvertQuoteResult2.setErrMsg(e.getMessage());
        } finally {
logger.info(logStr + "end");
        }
        return epcConvertQuoteResult2;
    }
    
    
    public EpcSubmitQuoteToOrderResult submitQuoteToOrder(int smcOrderId, int smcQuoteId, EpcQuote epcQuote) {
    	EpcSubmitQuoteToOrderResult epcSubmitQuoteToOrderResult = new EpcSubmitQuoteToOrderResult();
        epcSubmitQuoteToOrderResult.setQuoteId(epcQuote.getId());
        EpcSubmitQuoteResult epcSubmitQuoteResult = null;
        EpcConvertQuoteResult2 epcConvertQuoteResult2 = null;
        EpcSubmitQuote epcSubmitQuote = null;
        EpcCpqError epcCpqError = null;
//        java.util.Date createDate = new java.util.Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String newQuoteId = ""; // new quote (after success conversion - type 3)
        String newQuoteLastUpdated = "";
        int quoteType = epcQuote.getQuoteType();
        String apiUrl = "";
        String logStr = "[submitQuoteToOrder][" + epcQuote.getId() + "] ";
        HttpHeaders headers = new HttpHeaders();
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        
        try {
logger.info(logStr + "start");
logger.info(logStr + "quoteType:" + quoteType);

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }

			// convert quote if need
			if(quoteType != 3) {
				epcConvertQuoteResult2 = convertQuote(epcQuote.getId());
				if("SUCCESS".equals(epcConvertQuoteResult2.getResult())) {
					// success
					newQuoteId = epcConvertQuoteResult2.getNewQuoteGuid();
	                newQuoteLastUpdated = epcConvertQuoteResult2.getOrderLastUpdated();
logger.info(logStr + "converted to type3 quote:" + newQuoteId);

					// update new quote (type 3) to epc table
					epcOrderHandler.updateQuoteGuid(smcOrderId, smcQuoteId, epcQuote.getId(), newQuoteId, "convert " + epcQuote.getId() + " for submission");
				} else {
					throw new Exception(epcConvertQuoteResult2.getErrMsg());
				}
			} else {
				newQuoteId = epcQuote.getId();
				newQuoteLastUpdated = epcQuote.getUpdated();
			}
			// end of convert quote if need

			
			// submit type 3 quote
			apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "orders/" + newQuoteId + "/submit";
               
            epcSubmitQuote = new EpcSubmitQuote();
            epcSubmitQuote.setQuoteLastUpdated(newQuoteLastUpdated);

logger.info("{}{}", logStr, "send POST to CPQ");
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcSubmitQuote>(epcSubmitQuote, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");

            if(responseEntity.getStatusCodeValue() == 200) {
                // good case
                epcSubmitQuoteResult = objectMapper.readValue(responseEntity.getBody(), EpcSubmitQuoteResult.class);
                
                // update sigma order id to epc table 
                epcOrderHandler.updateSigmaOrderIdToOrder(newQuoteId, epcSubmitQuoteResult.getOrderId());
logger.info(logStr + "submitted successfully, sigma order id:" + epcSubmitQuoteResult.getOrderId());
                
                epcSubmitQuoteToOrderResult.setResult("SUCCESS");
                epcSubmitQuoteToOrderResult.setOrderId(epcSubmitQuoteResult.getOrderId());
                epcSubmitQuoteToOrderResult.setNewQuoteId(newQuoteId);
            } else {
                // error
                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
                epcSubmitQuoteToOrderResult.setResult("FAIL");
                epcSubmitQuoteToOrderResult.setErrMsg(epcCpqError.getExceptionType() + "|" + epcCpqError.getResponseText());
            }
            // end of submit type 3 quote
        } catch (Exception e) {
            e.printStackTrace();
            
            epcSubmitQuoteToOrderResult.setResult("FAIL");
            epcSubmitQuoteToOrderResult.setErrMsg(e.getMessage());
        } finally {
logger.info(logStr + "end");
        }
        
        return epcSubmitQuoteToOrderResult;
    }


    public EpcSubmitQuoteToOrderResult submitQuoteToOrder(int smcOrderId, int smcQuoteId, String quoteGuid) {
        String iQuoteGuid = epcSecurityHelper.encodeForSQL(quoteGuid);
    	EpcSubmitQuoteToOrderResult epcSubmitQuoteToOrderResult = new EpcSubmitQuoteToOrderResult();
        epcSubmitQuoteToOrderResult.setQuoteId(iQuoteGuid);
        EpcSubmitQuoteResult epcSubmitQuoteResult = null;
        EpcConvertQuoteResult2 epcConvertQuoteResult2 = null;
        EpcSubmitQuote epcSubmitQuote = null;
        EpcCpqError epcCpqError = null;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String newQuoteId = ""; // new quote (after success conversion - type 3)
        String newQuoteLastUpdated = "2018-12-04 07:11:59.411Z";
        String apiUrl = "";
        String logStr = "[submitQuoteToOrder][quoteGuid:" + iQuoteGuid + "] ";
        HttpHeaders headers = new HttpHeaders();
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        HashMap quoteContext = null;
        HashMap orderMap = null;
        String migration = "";
        java.util.Date createDate = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        
        try {
logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }


            // update quote context if need, kerrytsang, 20231201
            quoteContext = getQuoteContext(iQuoteGuid);
            if(quoteContext != null) {
                orderMap = (HashMap)quoteContext.get("order");
                if(orderMap != null) {
                    migration = StringHelper.trim((String)orderMap.get("Migration"));
                    if("P1".equals(migration)) {
                        orderMap.put("Requested_Activation_Date", sdf.format(createDate));
                    }
                }

                updateQuoteContext(iQuoteGuid, quoteContext);
            }
            // end of update quote context if need, kerrytsang, 20231201


//			// convert quote if need
//			if(quoteType != 3) {
//				epcConvertQuoteResult2 = convertQuote(epcQuote.getId());
//				if("SUCCESS".equals(epcConvertQuoteResult2.getResult())) {
//					// success
//					newQuoteId = epcConvertQuoteResult2.getNewQuoteGuid();
//	                newQuoteLastUpdated = epcConvertQuoteResult2.getOrderLastUpdated();
//logger.info(logStr + "converted to type3 quote:" + newQuoteId);
//
//					// update new quote (type 3) to epc table
//					epcOrderHandler.updateQuoteGuid(smcOrderId, smcQuoteId, epcQuote.getId(), newQuoteId, "convert " + epcQuote.getId() + " for submission");
//				} else {
//					throw new Exception(epcConvertQuoteResult2.getErrMsg());
//				}
//			} else {
//				newQuoteId = epcQuote.getId();
//				newQuoteLastUpdated = epcQuote.getUpdated();
//			}
//			// end of convert quote if need
            // convert quote
            epcConvertQuoteResult2 = convertQuote(iQuoteGuid);
            if("SUCCESS".equals(epcConvertQuoteResult2.getResult())) {
                // success
                newQuoteId = epcConvertQuoteResult2.getNewQuoteGuid();
                newQuoteLastUpdated = epcConvertQuoteResult2.getOrderLastUpdated();
logger.info("{}{}{}", logStr, "converted to type3 quote:", newQuoteId);

                // update new quote (type 3) to epc table
                epcOrderHandler.updateQuoteGuid(smcOrderId, smcQuoteId, iQuoteGuid, newQuoteId, "convert " + iQuoteGuid + " for submission");
            } else {
                throw new Exception(epcConvertQuoteResult2.getErrMsg());
            }
            // end of convert quote

			
			// submit type 3 quote
			apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "orders/" + newQuoteId + "/submit";
               
            epcSubmitQuote = new EpcSubmitQuote();
            epcSubmitQuote.setQuoteLastUpdated(newQuoteLastUpdated);

logger.info("{}{}", logStr, "send POST to CPQ");
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcSubmitQuote>(epcSubmitQuote, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");

            if(responseEntity.getStatusCodeValue() == 200) {
                // good case
                epcSubmitQuoteResult = objectMapper.readValue(responseEntity.getBody(), EpcSubmitQuoteResult.class);
                
                // update sigma order id to epc table 
                epcOrderHandler.updateSigmaOrderIdToOrder(iQuoteGuid, epcSubmitQuoteResult.getOrderId());
logger.info("{}{}{}", logStr, "submitted successfully, sigma order id:", epcSubmitQuoteResult.getOrderId());
                
                epcSubmitQuoteToOrderResult.setResult("SUCCESS");
                epcSubmitQuoteToOrderResult.setOrderId(epcSubmitQuoteResult.getOrderId());
                epcSubmitQuoteToOrderResult.setNewQuoteId(newQuoteId);
            } else {
                // error
                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
                epcSubmitQuoteToOrderResult.setResult("FAIL");
                epcSubmitQuoteToOrderResult.setErrMsg(epcCpqError.getExceptionType() + "|" + epcCpqError.getResponseText());
            }
            // end of submit type 3 quote
        } catch (Exception e) {
            e.printStackTrace();
            
            epcSubmitQuoteToOrderResult.setResult("FAIL");
            epcSubmitQuoteToOrderResult.setErrMsg(e.getMessage());
        } finally {
logger.info("{}{}", logStr, "end");
        }
        
        return epcSubmitQuoteToOrderResult;
    }
    
    
//     public EpcSubmitQuoteToOrderResult submitQuoteToOrderOld(EpcQuote epcQuote) {
//         EpcSubmitQuoteToOrderResult epcSubmitQuoteToOrderResult = new EpcSubmitQuoteToOrderResult();
//         epcSubmitQuoteToOrderResult.setQuoteId(epcQuote.getId());
//         EpcSubmitQuoteResult epcSubmitQuoteResult = null;
//         EpcConvertQuote epcConvertQuote = null;
//         EpcConvertQuoteResult epcConvertQuoteResult = null;
//         EpcSubmitQuote epcSubmitQuote = null;
//         EpcCpqError epcCpqError = null;
//         java.util.Date createDate = new java.util.Date();
//         SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
// //        javax.ws.rs.client.Client restClient = null;
// //        javax.ws.rs.core.Response restResponse = null;
//         RestTemplate restTemplate = new RestTemplate();
//         ResponseEntity<String> responseEntity = null;
//         ObjectMapper objectMapper = new ObjectMapper();
//         String newQuoteId = ""; // new quote (after success conversion - type 3)
//         String newQuoteLastUpdated = "";
// //        String apiUrl = EpcProperty.getValue("EPC.CPQ.LINK") + "quotes/" + epcQuote.getId() + "/convertToOrder";
//         String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + epcQuote.getId() + "/convertToOrder";
        
//         try {
// logger.info("[submitQuoteToOrder] submit " + epcQuote.getId() + " start");
        	
//             epcConvertQuote = new EpcConvertQuote();
//             epcConvertQuote.setQuoteLastUpdated(epcQuote.getUpdated());
//             epcConvertQuote.setExternalOrderId("");
//             epcConvertQuote.setActivationDate(sdf.format(createDate)); // sysdate ???
            
// //            restClient = ClientBuilder.newClient();
// //            
// //            restResponse = restClient.target(apiUrl).request(MediaType.APPLICATION_JSON).post(
// //                Entity.entity(epcConvertQuote, MediaType.APPLICATION_JSON), Response.class
// //            );
//             responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcConvertQuote>(epcConvertQuote), String.class);

// //            if(restResponse.getStatus() == 200) {
//             if(responseEntity.getStatusCodeValue() == 200) {
//                 // good case
// //                epcConvertQuoteResult = restResponse.readEntity(EpcConvertQuoteResult.class);
//                 epcConvertQuoteResult = objectMapper.readValue(responseEntity.getBody(), EpcConvertQuoteResult.class);
//                 newQuoteId = epcConvertQuoteResult.getId();
//                 newQuoteLastUpdated = epcConvertQuoteResult.getOrderLastUpdated();
                
//                 // update new quote (type 3) to epc table
//                 epcOrderHandler.updateConvertedQuoteToOrder(epcQuote.getId(), newQuoteId);
                
               
//                 // submit type 3 quote
// //                apiUrl = EpcProperty.getValue("EPC.CPQ.LINK") + "orders/" + newQuoteId + "/submit";
//                 apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "orders/" + newQuoteId + "/submit";
               
//                 epcSubmitQuote = new EpcSubmitQuote();
//                 epcSubmitQuote.setQuoteLastUpdated(newQuoteLastUpdated);
               
// //                restResponse = restClient.target(apiUrl).request(MediaType.APPLICATION_JSON).post(
// //                    Entity.entity(epcSubmitQuote, MediaType.APPLICATION_JSON), Response.class
// //                );
//                 responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcSubmitQuote>(epcSubmitQuote), String.class);
                
// //                if(restResponse.getStatus() == 200) {
//                 if(responseEntity.getStatusCodeValue() == 200) {
//                     // good case
// //                    epcSubmitQuoteResult = restResponse.readEntity(EpcSubmitQuoteResult.class);
//                     epcSubmitQuoteResult = objectMapper.readValue(responseEntity.getBody(), EpcSubmitQuoteResult.class);
                    
//                     // update sigma order id to epc table 
//                     epcOrderHandler.updateSigmaOrderIdToOrder(epcQuote.getId(), epcSubmitQuoteResult.getOrderId());
                    
//                     epcSubmitQuoteToOrderResult.setResult("SUCCESS");
//                     epcSubmitQuoteToOrderResult.setOrderId(epcSubmitQuoteResult.getOrderId());
//                     epcSubmitQuoteToOrderResult.setNewQuoteId(newQuoteId);
//                 } else {
//                     // error
// //                    epcCpqError = restResponse.readEntity(EpcCpqError.class);
//                     epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
//                     epcSubmitQuoteToOrderResult.setResult("FAIL");
//                     epcSubmitQuoteToOrderResult.setErrMsg(epcCpqError.getExceptionType() + "|" + epcCpqError.getResponseText());
//                 }
//                 // end of submit type 3 quote
//             } else {
//                // error
// //               epcCpqError = restResponse.readEntity(EpcCpqError.class);
//                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
//                epcSubmitQuoteToOrderResult.setResult("FAIL");
//                epcSubmitQuoteToOrderResult.setErrMsg(epcCpqError.getExceptionType() + "|" + epcCpqError.getResponseText());
//             }
//         } catch (Exception e) {
//             e.printStackTrace();
            
//             epcSubmitQuoteToOrderResult.setResult("FAIL");
//             epcSubmitQuoteToOrderResult.setErrMsg(e.getMessage());
//         } finally {
// logger.info("[submitQuoteToOrder] submit " + epcQuote.getId() + " end");
//         }
//         return epcSubmitQuoteToOrderResult;
//     }
    
    
    public String copyQuote(String originalQuoteGuid) {
    	String newQuoteGuid = "";
    	EpcCopyQuote epcCopyQuote = null;
//    	EpcCpqError epcCpqError = null;
    	EpcCopyQuoteResult epcCopyQuoteResult = null;
    	RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + originalQuoteGuid + "/copy";
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        String logStr = "[copyQuote][originalQuoteGuid:" + originalQuoteGuid + "] ";
        
        try {
logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }

        	epcCopyQuote = new EpcCopyQuote();
        	epcCopyQuote.setQuoteType(0); // set quote type to 0
        	
        	headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

logger.info("{}{}", logStr, "send POST to CPQ");
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcCopyQuote>(epcCopyQuote, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");
            
            if(responseEntity.getStatusCodeValue() == 200) {
            	epcCopyQuoteResult = objectMapper.readValue(responseEntity.getBody(), EpcCopyQuoteResult.class);
            	newQuoteGuid = epcCopyQuoteResult.getNewQuoteId();
            } else {
//            	epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);
            	throw new Exception(responseEntity.getBody());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return newQuoteGuid;
    }


    public EpcUpdateQuote updateQuote(EpcQuote epcQuote) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + epcQuote.getId();
        //String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        //String keyCloakAccessToken = "";
        EpcCpqError epcCpqError = null;
        EpcUpdateQuote epcUpdateQuote = new EpcUpdateQuote();
        String logStr = "[updateQuote][quoteId:" + epcQuote.getId() + "] ";
        
        
        try {
logger.info("{}{}", logStr, "start");

			/*
            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }
            */
            keycloakHelper.addAccessToken(headers);
            //headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
            headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
            headers.setContentType(MediaType.APPLICATION_JSON);

logger.info("{}{}", logStr, "send PUT to CPQ");
            //responseEntity = restTemplate.exchange(apiUrl, HttpMethod.PUT, new HttpEntity(epcQuote, headers), String.class);
			//To exclude those null value element
            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.PUT, new HttpEntity<String>(objectMapper.writeValueAsString(epcQuote), headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");
            
            if(responseEntity.getStatusCodeValue() == 200) {
                epcUpdateQuote.setResult("SUCCESS");
            } else {
                epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);

                epcUpdateQuote.setResult("FAIL");
                epcUpdateQuote.setErrMsg(epcCpqError.getExceptionType() + ", " + epcCpqError.getResponseText());
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcUpdateQuote.setResult("FAIL");
            epcUpdateQuote.setErrMsg(e.getMessage());
        } finally {
        }
        return epcUpdateQuote;
    }

	// added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): start 
    public String getConfiguredValue(EpcQuoteItem epcQuoteItem, String key) {
        ArrayList<HashMap<String, Object>> configuredValueList = (ArrayList<HashMap<String, Object>>)epcQuoteItem.getProductCandidate().get("ConfiguredValue");

        try {
            for (int i=0; i<configuredValueList.size(); i++) {
                HashMap<String, Object> m = configuredValueList.get(i);

                String useArea = (String)m.get("UseArea");

                ArrayList<HashMap<String,Object>> n = (ArrayList<HashMap<String,Object>>)m.get("Value");

                String value = null;

                for (int j=0; j<n.size(); j++) {
                    HashMap<String, Object> o = n.get(j);
                    value = (String)o.get("Value");
                }

                if (useArea != null) {
                    if (useArea.toUpperCase().equals(key.toUpperCase())) {
                        return value;
                    }
                }
            }
        } catch (Exception e) {

        }

        return null;
    }
    // added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): end 


    public boolean validateAndPrice(String quoteGuid) {
    	RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
//        ObjectMapper objectMapper = new ObjectMapper();
        String iQuoteGuid = epcSecurityHelper.encodeForSQL(StringHelper.trim(quoteGuid));
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes/" + iQuoteGuid + "/validateAndPrice";
        String isKeyCloakEnabled = EpcProperty.getValue("EPCAPI_KEYCLOAK_ENABLED");
        String keyCloakAccessToken = "";
        String logStr = "[validateAndPrice][quoteGuid:" + iQuoteGuid + "] ";
        EpcValidateAndPrice epcValidateAndPrice = null;
        boolean isValidate = false;
        
        try {
logger.info("{}{}", logStr, "start");

            if("Y".equals(isKeyCloakEnabled)){
                keyCloakAccessToken = keycloak.tokenManager().getAccessTokenString();
                headers.add(HttpHeaders.AUTHORIZATION, "bearer " + keyCloakAccessToken);
            }

        	headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);


            epcValidateAndPrice = new EpcValidateAndPrice();
            epcValidateAndPrice.setQuoteLastUpdated("2019-01-10T19:47:12.337Z");

logger.info("{}{}", logStr, "send POST to CPQ");
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcValidateAndPrice, headers), String.class);
logger.info("{}{}", logStr, "Got result from CPQ");
            
            isValidate = true;
        } catch (Exception e) {
            e.printStackTrace();

            isValidate = false;
        } finally {
        }
        return isValidate;
    }
}
