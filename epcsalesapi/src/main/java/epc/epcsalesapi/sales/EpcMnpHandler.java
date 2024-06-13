/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcCreateMnp;
import epc.epcsalesapi.sales.bean.EpcCreateMnpResult;
import epc.epcsalesapi.sales.bean.EpcCustNumSubrNum;
import epc.epcsalesapi.sales.bean.EpcMnpResult;
import java.text.SimpleDateFormat;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author KerryTsang
 */

@Service
public class EpcMnpHandler {
	
	private final Logger logger = LoggerFactory.getLogger(EpcMnpHandler.class);
	
	@Autowired
	private EpcOrderHandler epcOrderHandler;
	
	@Autowired
	private EpcOrderProcessCtrlHandler epcOrderProcessCtrlHandler;
	
	@Autowired
	private EpcCustProfileHandler epcCustProfileHandler;
	
    
    public EpcCreateMnpResult createMnpRecord(String custId, int orderId, String createUser) {
        EpcCreateMnpResult epcCreateMnpResult = new EpcCreateMnpResult();
//        String custId = StringHelper.trim(epcQuote.getCustomerRef());
//        String quoteId = StringHelper.trim(epcQuote.getId());
//        HashMap<String, Object> contextData = null;
        ArrayList<HashMap<String, Object>> objList = null;
        HashMap<String, Object> obj = null;
//        HashMap<String, Object> obj2 = null;
        HashMap<String, Object> objSMCCustInfo = null;
        int smcOrderId = orderId;
        String smcCaseId = "";
        String smcItemId = "";
        String custNum = "";
        String subrNum = "";
        String smcCustFirstName = "";
        String smcCustFirstNameDecrypt = "";
        String smcCustLastName = "";
        String smcCustLastNameDecrypt = "";
        String smcHKIDBR = "";
        String smcHKIDBRDecrypt = "";
        String activationType = "";
        String activationDate = "";
        String activationDateForOm = "";
        String dno = "";
        String dealerCode = "";
        SimpleDateFormat sdfYYYYMMDDHHMMSS = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String processStatus = "";
        String processRemarks = "";
        EpcCreateMnp epcCreateMnp = null;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<EpcMnpResult> responseEntity = null;
        EpcMnpResult epcMnpResult = null;
        boolean isNeeded = false;
        boolean isExistedInTree = false;
        String apiUrl = EpcProperty.getValue("OM_MNP_CREATE_MNP");
        String logStr = "[createMnpRecord][orderId:" + orderId + "]";
        
        
        try {
            objList = epcCustProfileHandler.getTmpCustProfile(smcOrderId, custId); // get cust info from epc table instead of quote context, kerrytsang, 20200908
            if(objList == null || objList.size() == 0) {
                // no customer info, no need to perform further action
            	epcCreateMnpResult.setResult("SUCCESS");
            } else {
                for (int i = 0; i < objList.size(); i++) {
                	isNeeded = false; // reset
                	isExistedInTree = false; // reset
                	
                    obj = (HashMap<String, Object>)objList.get(i);
                    smcCaseId = StringHelper.trim((String)obj.get("SMCCaseId"));
                    smcItemId = StringHelper.trim((String)obj.get("SMCItemId"));
                    custNum = StringHelper.trim((String)obj.get("SMCCustNum"));
                    subrNum = StringHelper.trim((String)obj.get("SMCSubrNum"));
                    objSMCCustInfo = (HashMap<String, Object>)obj.get("SMCCustInfo");
                    smcCustFirstName = StringHelper.trim((String)objSMCCustInfo.get("SMCCustFirstName"));
                    smcCustFirstNameDecrypt = StringHelper.trim(EpcCrypto.dGet(smcCustFirstName, "utf-8"));
                    smcCustLastName = StringHelper.trim((String)objSMCCustInfo.get("SMCCustLastName"));
                    smcCustLastNameDecrypt = StringHelper.trim(EpcCrypto.dGet(smcCustLastName, "utf-8"));
                    smcHKIDBR = StringHelper.trim((String)objSMCCustInfo.get("SMCHKIDBR"));
                    smcHKIDBRDecrypt = StringHelper.trim(EpcCrypto.dGet(smcHKIDBR, "utf-8"));
                    activationDate = StringHelper.trim((String)objSMCCustInfo.get("SMCEffectiveDate"));
                    dno = StringHelper.trim((String)objSMCCustInfo.get("SMCDno"));
                    activationType = StringHelper.trim((String)objSMCCustInfo.get("SMCActivationType"));
                    dealerCode = StringHelper.trim((String)objSMCCustInfo.get("SMCDealerCode"));
                    

                    isExistedInTree = epcCustProfileHandler.isSubrExistInOrderTree(orderId, smcItemId);
logger.info(logStr + "[caseId:" + smcCaseId + "][itemId:" + smcItemId + "][subrNum:" + subrNum + "] isExistedInTree:" + isExistedInTree);
                    if(!isNeeded) {
                        // proceed cases only if they are existed in productcandidate
logger.info(logStr + "[caseId:" + smcCaseId + "][itemId:" + smcItemId + "][subrNum:" + subrNum + "]  NOT PROCEED");
                        continue;
                    }
                    
                    if(!EpcActivationTypeHandler.mnp.equals(activationType)) {
                        // only proceed mnp case
logger.info(logStr + "[caseId:" + smcCaseId + "][itemId:" + smcItemId + "][subrNum:" + subrNum + "] this case is not an MNP case [activationType:" + activationType + "], NOT PROCEED");
                        continue;
                    }
                    
                    // parse activation date for om
                    activationDateForOm = sdf.format(sdfYYYYMMDDHHMMSS.parse(activationDate));
                    
                    processStatus = epcOrderProcessCtrlHandler.getProcessStatus(orderId, smcCaseId, smcItemId, epcOrderProcessCtrlHandler.processCreateMnp);
                    if("NOT_EXIST".equals(processStatus)) {
                        throw new Exception("createMnpRecord process for " + smcCaseId + " is not existed, program exit");
                    } else if (epcOrderProcessCtrlHandler.processStatusDone.equals(processStatus)) {
logger.info(logStr + "[caseId:" + smcCaseId + "][itemId:" + smcItemId + "][subrNum:" + subrNum + "] createMnpRecord process is DONE, no need to perform again");
                    } else {
                        // call om api
                        epcCreateMnp = new EpcCreateMnp();
                        epcCreateMnp.setNumberType("MOBILE");
                        epcCreateMnp.setMsisdn(subrNum);
                        epcCreateMnp.setRno("SG");
                        epcCreateMnp.setDno(dno);
                        epcCreateMnp.setCutoverDate(activationDateForOm); // in YYYY-MM-DD HH24:MI:SS
                        epcCreateMnp.setDealerCode(dealerCode);
                        epcCreateMnp.setIsPrepaid("N");
                        epcCreateMnp.setServiceType("");
                        epcCreateMnp.setOrgService("");
                        epcCreateMnp.setExistService("");
                        epcCreateMnp.setSubrName(smcCustLastNameDecrypt + " " + smcCustFirstNameDecrypt);
                        epcCreateMnp.setSubrIDBR(smcHKIDBRDecrypt);
                        epcCreateMnp.setUserName(createUser);
                    
                        responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcCreateMnp>(epcCreateMnp), EpcMnpResult.class);
                        if(responseEntity.getStatusCodeValue() == 200) {
                            epcMnpResult = responseEntity.getBody();

                            processRemarks = "resultCode:" + epcMnpResult.getResultCode() + ", errorMessage:" + epcMnpResult.getErrorMessage(); // capture om api result
logger.info(logStr + "[caseId:" + smcCaseId + "][itemId:" + smcItemId + "][subrNum:" + subrNum + "] processRemarks:" + processRemarks);
                            
                            if("0".equals(epcMnpResult.getResultCode())) {
                                // success
                                // update process ctrl
                                epcOrderProcessCtrlHandler.updateProcess(orderId, smcCaseId, smcItemId, epcOrderProcessCtrlHandler.processCreateMnp, epcOrderProcessCtrlHandler.processStatusDone, processRemarks);
logger.info(logStr + "[caseId:" + smcCaseId + "][itemId:" + smcItemId + "][subrNum:" + subrNum + "] create MNP DONE");

								epcCreateMnpResult.setResult("SUCCESS");
                            } else {
                                // fail
                                // update process ctrl
                                epcOrderProcessCtrlHandler.updateProcess(orderId, smcCaseId, smcItemId, epcOrderProcessCtrlHandler.processCreateMnp, epcOrderProcessCtrlHandler.processStatusFail, processRemarks);
logger.info(logStr + "[caseId:" + smcCaseId + "][itemId:" + smcItemId + "][subrNum:" + subrNum + "] create MNP FAIL");

								epcCreateMnpResult.setResult("FAIL");
								epcCreateMnpResult.setErrMsg(epcMnpResult.getErrorMessage());
                            }
                        } else {
                            // fail
                            // update process ctrl
                            epcOrderProcessCtrlHandler.updateProcess(orderId, smcCaseId, smcItemId, epcOrderProcessCtrlHandler.processCreateMnp, epcOrderProcessCtrlHandler.processStatusFail, processRemarks);
logger.info(logStr + "[caseId:" + smcCaseId + "][itemId:" + smcItemId + "][subrNum:" + subrNum + "] create MNP FAIL");

							epcCreateMnpResult.setResult("FAIL");
							epcCreateMnpResult.setErrMsg("invalid http status:" + responseEntity.getStatusCodeValue());
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcCreateMnpResult.setResult("FAIL");
            epcCreateMnpResult.setErrMsg(e.getMessage());
        }
        
        return epcCreateMnpResult;
    }
}
