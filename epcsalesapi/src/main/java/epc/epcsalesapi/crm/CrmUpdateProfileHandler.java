package epc.epcsalesapi.crm;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.crm.bean.CrmUpdateProfile;
import epc.epcsalesapi.crm.bean.CrmUpdateProfileResult;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;

@Service
public class CrmUpdateProfileHandler {

    private final Logger logger = LoggerFactory.getLogger(CrmUpdateProfileHandler.class);

    private EpcSecurityHelper epcSecurityHelper;
    
    public CrmUpdateProfileHandler(EpcSecurityHelper epcSecurityHelper) {
        this.epcSecurityHelper = epcSecurityHelper;
    }


    /***
     * create a thread to update crm profile
     *  invoked by placeOrder()
     */
    public void updateProfileAsync(CrmUpdateProfile crmUpdateProfile) {
        try {
            CompletableFuture.completedFuture(crmUpdateProfile).thenApplyAsync(s -> updateProfile(s));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String updateProfile(CrmUpdateProfile crmUpdateProfile) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, String> aMap = new HashMap<>();
        String apiUrl = EpcProperty.getValue("EPCAPI_CRM_API_LINK") + "updateGuestProfile";
        CrmUpdateProfileResult crmUpdateProfileResult = null;
        String custId = epcSecurityHelper.encodeForSQL(StringHelper.trim(crmUpdateProfile.getCustId()));
        String contactEmail = epcSecurityHelper.encodeForSQL(StringHelper.trim(crmUpdateProfile.getContactEmail()));
        String contactNo = epcSecurityHelper.encodeForSQL(StringHelper.trim(crmUpdateProfile.getContactNo()));
        String optIn = epcSecurityHelper.encodeForSQL(StringHelper.trim(crmUpdateProfile.getOptIn()));
        String orderReference = epcSecurityHelper.encodeForSQL(StringHelper.trim(crmUpdateProfile.getOrderReference()));
        CrmUpdateProfile crmUpdateProfileForLog = null;
        String effectiveDateStr = new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date());
        String logStr = "[updateProfile][custId:" + custId + "][orderReference:" + orderReference + "] ";
        String tmpLogStr = "";
        
        try {
            aMap.put("requesterId", "SysAdmin");
            aMap.put("effectiveDate", effectiveDateStr);
            aMap.put("custId", custId);
            aMap.put("remark", orderReference);
            aMap.put("type", "changeForm");
            aMap.put("subType", "updateGuestProfile");
            aMap.put("channel", "FES");
            aMap.put("title", "Update Guest Profile");
            aMap.put("contactEmail", contactEmail); // contactEmail - not Encrypted 
            aMap.put("contactMobile", contactNo); // contactMobile - not Encrypted
            aMap.put("srId", "");
            aMap.put("optIn", optIn);
            aMap.put("encrypted", "N");

            crmUpdateProfileForLog = new CrmUpdateProfile();
            crmUpdateProfileForLog.setCustId(custId);
            if(contactEmail.length() >= 2) {
                crmUpdateProfileForLog.setContactEmail(contactEmail.substring(0, 1));
            } else {
                crmUpdateProfileForLog.setContactEmail(contactEmail);
            }
            crmUpdateProfileForLog.setContactNo(contactNo);
            crmUpdateProfileForLog.setOptIn(optIn);
            crmUpdateProfileForLog.setOrderReference(orderReference);

            tmpLogStr = "request json:" + objectMapper.writeValueAsString(crmUpdateProfileForLog);
logger.info("{}{}", logStr, tmpLogStr);


            crmUpdateProfileResult = restTemplate.postForObject(apiUrl, new HttpEntity<>(aMap), CrmUpdateProfileResult.class);

            tmpLogStr = "result json:" + objectMapper.writeValueAsString(crmUpdateProfileResult);
logger.info("{}{}", logStr, tmpLogStr);


            crmUpdateProfile.setResult("SUCCESS");
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();

            crmUpdateProfile.setResult("FAIL");
            crmUpdateProfile.setErrMsg(hsce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();

            crmUpdateProfile.setResult("FAIL");
            crmUpdateProfile.setErrMsg(e.getMessage());
        }
        return "OK";
    }
}
