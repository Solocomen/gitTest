package epc.epcsalesapi.crm;

import java.util.ArrayList;
import java.util.HashMap;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import epc.epcsalesapi.crm.bean.EpcCustomerProfile;
import epc.epcsalesapi.crm.bean.EpcGetLoginId;
import epc.epcsalesapi.crm.bean.EpcResponse;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.StringHelper;

@Service
public class EpcCustomerHandler {

	public EpcResponse create(EpcCustomerProfile epcCustomerProfile) {
		RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPCAPI_CRM_API_LINK") + "customer";
		EpcResponse epcResponse = new EpcResponse();
		
		try {
        	responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcCustomerProfile), String.class);
        	if(responseEntity.getStatusCodeValue() == 200) {
        		epcResponse = objectMapper.readValue(responseEntity.getBody(), EpcResponse.class);
        	} else {
        		// error
        		epcResponse = new EpcResponse();
        		epcResponse.setResultCode("-99998");
        		epcResponse.setResultMsg("http response code:" + responseEntity.getStatusCodeValue());
        	}
        } catch (Exception e) {
            e.printStackTrace();
            
            epcResponse = new EpcResponse();
            epcResponse.setResultCode("-99999");
    		epcResponse.setResultMsg(e.getMessage());
        } finally {
        }
		
		return epcResponse;
	}
	
	
	public String getLoginIdByCustId(String custId) {
		String idStr = "";
		EpcGetLoginId epcGetLoginId = null;
		RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPCAPI_CRM_API_LINK") + "custInfo";
        HashMap<String, Object> returnMap = null;
        HashMap<String, Object> customerMap = null;
        ArrayList<HashMap<String, Object>> accountList = null;
        String resultCode = "";
        String type = "";
        String tmpIdStr = "";

        
        try {
        	epcGetLoginId = new EpcGetLoginId();
        	epcGetLoginId.setRequesterID("SysAdmin");
        	epcGetLoginId.setCustId(custId);
        	
        	responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcGetLoginId), String.class);
        	if(responseEntity.getStatusCodeValue() == 200) {
        		returnMap = objectMapper.readValue(responseEntity.getBody(), HashMap.class);
        		resultCode = StringHelper.trim((String)returnMap.get("resultCode"));
        		if("0".equals(resultCode)) {
        			// success
        			customerMap = (HashMap<String, Object>)returnMap.get("customer");
        			type = StringHelper.trim((String)customerMap.get("type"));
        			if("ECOMM".equals(type)) {
        				tmpIdStr = getId(customerMap.get("loginnowId"));
        				if(!"".equals(tmpIdStr)) {
        					idStr += Integer.parseInt(tmpIdStr);
        				}
        			} else if("POSTPAID".equals(type)) {
        				accountList = (ArrayList<HashMap<String, Object>>)customerMap.get("accountList");
        				for(HashMap<String, Object> accountMap : accountList) {
        					tmpIdStr = getId(accountMap.get("loginnowId"));
        					if(!"".equals(tmpIdStr)) {
        						if("".equals(idStr)) {
            						idStr += Integer.parseInt(tmpIdStr);
            					} else {
            						idStr += "|" + Integer.parseInt(tmpIdStr);
            					}
        					}
        				}
        			}
        		} else {
        			// fail
        		}
        	} else {
        		// error

        	}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        
		return idStr;
	}


    public String getHkidByCustId(String custId) {
        String hkid = "";
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = EpcProperty.getValue("EPCAPI_CRM_API_LINK") + "custInfo";
        String returnJsonStr = "";
        String resultCode = "";
        String jsonPath = "$.customer.idbr";
        HashMap<String, String> aMap = new HashMap<>();

        try {
            aMap.put("requesterId", "SysAdmin");
            aMap.put("custId", custId);
            
            returnJsonStr = restTemplate.postForObject(apiUrl, new HttpEntity<>(aMap), String.class);
            resultCode = StringHelper.trim(JsonPath.read(returnJsonStr, "$.resultCode"));
            if("0".equals(resultCode)) {
                hkid = StringHelper.trim(JsonPath.read(returnJsonStr, jsonPath));
            }
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hkid;
    }


	public String getHkidByCustIdSlim(String custId) {
        String hkid = "";
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = EpcProperty.getValue("EPCAPI_CRM_API_LINK") + "getCustId";
        String returnJsonStr = "";
        String resultCode = "";
		String returnCustId = "";
        String jsonPath = "$.idbr";
        HashMap<String, String> aMap = new HashMap<>();

        try {
            aMap.put("requesterId", "SysAdmin");
            aMap.put("custId", custId);
            
            returnJsonStr = restTemplate.postForObject(apiUrl, new HttpEntity<>(aMap), String.class);
            resultCode = StringHelper.trim(JsonPath.read(returnJsonStr, "$.resultCode"));
			returnCustId = StringHelper.trim(JsonPath.read(returnJsonStr, "$.custId"));

            if("0".equals(resultCode) && returnCustId.equals(custId)) {
                hkid = StringHelper.trim(JsonPath.read(returnJsonStr, jsonPath));

				if(!"".equals(hkid)) {
					hkid = EpcCrypto.dGet(hkid); // not include charset, advised by crm team, 20231016
				}
            }
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return hkid;
    }


	public String getId(Object o) {
		String str = "";
		if(o instanceof String) {
			str = StringHelper.trim((String)o);
		} else if(o instanceof Integer) {
			str = String.valueOf(((Integer)o).intValue());
		} else if(o instanceof Long) {
			str = String.valueOf(((Long)o).longValue());
		} else if(o instanceof Double) {
			str = String.valueOf(((Double)o).doubleValue());
		} else if(o instanceof Float) {
			str = String.valueOf(((Float)o).floatValue());
		}
		return str;
	}

}
