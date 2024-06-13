package epc.epcsalesapi.crm;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.crm.bean.EpcGetSubrInfo;
import epc.epcsalesapi.crm.bean.EpcGetSubrInfoResult;
import epc.epcsalesapi.crm.bean.EpcResponse;
import epc.epcsalesapi.crm.bean.EpcSubscriber;
import epc.epcsalesapi.helper.EpcProperty;
import org.springframework.stereotype.Service;


@Service
public class EpcSubscriberHandler {

    public EpcResponse update(EpcSubscriber epcSubscriber) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPCAPI_CRM_API_LINK") + "upsertSubr";
        EpcResponse epcResponse = new EpcResponse();
        
        try {
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcSubscriber), String.class);
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


    public EpcGetSubrInfoResult getSubrInfoBySubrNum(String subrNum) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPCAPI_CRM_API_LINK") + "subrInfo";
        EpcGetSubrInfo epcGetSubrInfo = new EpcGetSubrInfo();
		EpcGetSubrInfoResult epcGetSubrInfoResult = null;
        
        try {
			epcGetSubrInfo.setRequesterId("SysAdmin");
			epcGetSubrInfo.setSubrNum(subrNum);

            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcGetSubrInfo), String.class);
			epcGetSubrInfoResult = objectMapper.readValue(responseEntity.getBody(), EpcGetSubrInfoResult.class);
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
        }
		return epcGetSubrInfoResult;
    }
}
