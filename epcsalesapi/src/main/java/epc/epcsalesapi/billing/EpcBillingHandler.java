package epc.epcsalesapi.billing;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.billing.bean.EpcNewBillDay;
import epc.epcsalesapi.billing.bean.EpcNewCustNum;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.StringHelper;

@Service
public class EpcBillingHandler {

	public String getNewCustomerReference() {
		RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_BILLING_LINK") + "int/getnewcustref";
        String newCustNum = "";
        EpcNewCustNum epcNewCustNum = null;
        
        try {
        	responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(null), String.class);
        	if(responseEntity.getStatusCodeValue() == 200) {
        		epcNewCustNum = objectMapper.readValue(responseEntity.getBody(), EpcNewCustNum.class);
        		if(epcNewCustNum.getErrorCode() == 0) {
        			// ok
        			newCustNum = StringHelper.trim(epcNewCustNum.getCustomerRef());
        		} else {
        			// error
        			newCustNum = "NOT_FOUND";
        		}
        	} else {
        		// error
        		newCustNum = "NOT_FOUND";
        	}
        } catch (Exception e) {
            e.printStackTrace();
            
            newCustNum = "NOT_FOUND";
        } finally {
        }
		return newCustNum;
	}
	
	public String getNextBillDay() {
		RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_BILLING_LINK") + "int/getnextbillday";
        String newBillDay = "";
        EpcNewBillDay epcNewBillDay = null;
        
        try {
        	responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(null), String.class);
        	if(responseEntity.getStatusCodeValue() == 200) {
        		epcNewBillDay = objectMapper.readValue(responseEntity.getBody(), EpcNewBillDay.class);
        		if(epcNewBillDay.getErrorCode() == 0) {
        			// ok
        			newBillDay = StringHelper.trim(epcNewBillDay.getNextBillDay());
        		} else {
        			// error
        			newBillDay = "NOT_FOUND";
        		}
        	} else {
        		// error
        		newBillDay = "NOT_FOUND";
        	}
        } catch (Exception e) {
            e.printStackTrace();
            
            newBillDay = "NOT_FOUND";
        } finally {
        }
		return newBillDay;
	}
}
