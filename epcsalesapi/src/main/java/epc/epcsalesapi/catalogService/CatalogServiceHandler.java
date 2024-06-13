package epc.epcsalesapi.catalogService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.StringHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CatalogServiceHandler {
    private final Logger logger = LoggerFactory.getLogger(CatalogServiceHandler.class);

    public CatalogServiceHandler() {}

    public String getFactValueByFactGuid(String factGuid) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        String apiUrl = EpcProperty.getValue("EPC_CS_LINK") + "/entities(" + factGuid + ")?id=GUID";
        HashMap<String, Object> resultMap = null;
        HashMap<String, Object> lookupMap = null;
        String factValue = "";

        try {
            resultMap = restTemplate.getForObject(apiUrl, HashMap.class, headers);
            if(resultMap != null) {
                lookupMap = (HashMap<String, Object>)resultMap.get("Lookup_Product_Code");
                if(lookupMap != null) {
                    factValue = (String)lookupMap.get("Name");
                }
                if ("".equals(StringHelper.trim(factValue))) {
                    factValue = getNameValueFromMap(resultMap);
                }
            }
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();

            factValue = "";
        } catch (Exception e) {
            e.printStackTrace();

            factValue = "";
        } finally {
        }
        return factValue;
    }
    
    private String getNameValueFromMap(Map<String, Object> map) {
        
        String nameValue = "";
        
        try {
            
            if (map.containsKey("Value")) {
                nameValue = (String) map.get("Value");
                return nameValue;
            }
            
            for (Iterator<Map.Entry<String, Object>> entries = map.entrySet().iterator(); entries.hasNext(); ) {
                Map.Entry<String, Object> entry = entries.next();
                if("Name".equals(entry.getKey())) {
                   nameValue = StringHelper.trim((String)entry.getValue());
                   break;
                } else {
                   if (entry.getValue() instanceof Map) {
                       nameValue = getNameValueFromMap((Map<String, Object>)entry.getValue());
                       if (!"".equals(nameValue)) {
                           break;
                       }
                   }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            nameValue = "";
        } finally {}

        return nameValue;
    }


    public HashMap<String, Object> getEntityByGuid(String guid) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        String apiUrl = EpcProperty.getValue("EPC_CS_LINK") + "/entities(" + guid + ")?id=GUID";
        HashMap<String, Object> resultMap = null;

        try {
            resultMap = restTemplate.getForObject(apiUrl, HashMap.class, headers);
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return resultMap;
    }
}
