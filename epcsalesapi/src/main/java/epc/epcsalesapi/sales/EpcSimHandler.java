/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.billing.rmm.EpcNumberHandler;
import epc.epcsalesapi.billing.rmm.bean.EpcNextSimResult;
import epc.epcsalesapi.change.EpcChangeHandler;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcGenerateDummySimResult;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.EpcQuoteProductCandidate;
import epc.epcsalesapi.sales.bean.EpcUpdateModifiedItemToQuoteResult;
import java.sql.*;
import java.util.*;
import javax.sql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author KerryTsang
 */

@Service
public class EpcSimHandler {
	
	@Autowired
	private EpcOrderHandler epcOrderHandler;
	
	@Autowired
	private EpcQuoteHandler epcQuoteHandler;
	
	@Autowired
	private EpcOrderProcessCtrlHandler epcOrderProcessCtrlHandler;
	
	@Autowired
	private EpcOrderAttrHandler epcOrderAttrHandler;
	
	@Autowired
	private EpcCustProfileHandler epcCustProfileHandler;
	
    
    public final String dummySimValue = "AAAAA";
    public final String dummyImsiValue = "BBBBB";
    
    
    public EpcGenerateDummySimResult generateDummySim(EpcQuote epcQuote) {
        EpcGenerateDummySimResult epcGenerateDummySimResult = new EpcGenerateDummySimResult();
        String custId = StringHelper.trim(epcQuote.getCustomerRef());
        String quoteId = StringHelper.trim(epcQuote.getId());
        HashMap<String, Object> contextData = null;
        ArrayList<HashMap<String, Object>> objList = null;
        HashMap<String, Object> obj = null;
        HashMap<String, Object> objSMCCustInfo = null;
        int smcOrderId = 0;
        int smcQuoteId = 0;
        String smcCaseId = "";
        String processStatus = "";
        String dummySimNo = "";
        String dummyImsi = "";
        EpcQuoteItem[] epcQuoteItemArray = null;
        EpcQuoteItem epcQuoteItem = null;
        EpcQuoteProductCandidate epcQuoteProductCandidate = null;
        HashMap<String, Object> productCandidateMap = null;
        String productCandidateString = "";
        boolean isAdded = false;
        String dealerCode = "";
        HashMap<String, Object> obj2 = null;
        EpcNextSimResult epcNextSimResult = null;
        String itemGuid = "";
        EpcUpdateModifiedItemToQuoteResult epcUpdateModifiedItemToQuoteResult = null;
        String logStr = "";
        
        try {
            contextData = epcQuote.getContextData();
            smcOrderId = Integer.parseInt(StringHelper.trim((String)contextData.get("SMCOrderId")));
            smcQuoteId = Integer.parseInt(StringHelper.trim((String)contextData.get("SMCQuoteId")));
            logStr = "[generateDummySim][smcOrderId:" + smcOrderId + "] ";
            
            
            // get dealer code
            obj2 = (HashMap<String, Object>)contextData.get("order");
            if(obj2 != null) {
                dealerCode = StringHelper.trim((String)obj2.get("Dealer_Code"));
            }
            
            if("".equals(dealerCode)) {
                // if no dealer code, NOT proceed "replace dummy sim" action
                epcGenerateDummySimResult.setResult("SUCCESS");
                return epcGenerateDummySimResult;
            }
            // end of get dealer code


            // get quote items
            epcQuoteItemArray = epcQuote.getItems();
            // end of get quote items


            objList = epcCustProfileHandler.getTmpCustProfile(smcOrderId, custId); // get cust info from epc table instead of quote context, kerrytsang, 20200908
            if(objList == null) {
                // no customer info, no need to perform further action
            } else {
                for (int i = 0; i < objList.size(); i++) {
                    obj = (HashMap<String, Object>)objList.get(i);
                    smcCaseId = StringHelper.trim((String)obj.get("SMCCaseId"));
                    
                    if(!epcQuoteHandler.containsCase(epcQuote, smcCaseId)) {
                        // proceed cases only if they are existed in productcandidate
                        continue;
                    }
                    
                    processStatus = epcOrderProcessCtrlHandler.getProcessStatus(quoteId, smcCaseId, epcOrderProcessCtrlHandler.processGenerateDummySim);
                    if("NOT_EXIST".equals(processStatus)) {
                        throw new Exception("process for " + quoteId + "/" + smcCaseId + " is not existed, program exit");
                    } else if (epcOrderProcessCtrlHandler.processStatusDone.equals(processStatus)) {
System.out.println(logStr + "process for " + quoteId + "/" + smcCaseId + " is DONE, no need to perform again");
                    } else {
                        // set it in product candidate
                        for (int xx = 0; xx < epcQuoteItemArray.length; xx++) {
                            epcQuoteItem = epcQuoteItemArray[xx];
                            itemGuid = epcQuoteItem.getId();
                            epcQuoteProductCandidate = epcQuoteItem.getProductCandidateObj();
                            productCandidateMap = epcQuoteItem.getProductCandidate();
                            productCandidateString = productCandidateMap.toString();
                            
                            if(epcQuoteProductCandidate.getId().equals(smcCaseId)) {
                                // proceed activation case only (with customer profile), kerrytsang, 20201207
                                //  replace sim & imsi if need
                                if(productCandidateString.contains(dummySimValue) && productCandidateString.contains(dummyImsiValue)) {
                                    // get new dummy sim
                                    epcNextSimResult = getDummySimFromPool(dealerCode);
                                    dummySimNo = epcNextSimResult.getSim();
                                    dummyImsi = epcNextSimResult.getImsi();
System.out.println(logStr + "caseId:" + smcCaseId + ",dummySimNo:" + dummySimNo + ",dummyImsi:" + dummyImsi);
                                    // end of get new dummy sim
                                    
                                    // replace sim & imsi
                                    EpcChangeHandler.replaceConfiguredValue(productCandidateMap, EpcChangeHandler.replaceTypeChangeSim, dummySimValue, dummySimNo);
                                    EpcChangeHandler.replaceConfiguredValue(productCandidateMap, EpcChangeHandler.replaceTypeChangeImsi, dummyImsiValue, dummyImsi);
                                    // end of replace sim & imsi

                                    // submit to cpq
System.out.println(logStr + "caseId:" + smcCaseId + " update dummy sim/imsi to CPQ");
// for dummy, kerrytsang
//                                    epcUpdateModifiedItemToQuoteResult = epcQuoteHandler.updateModifiedItemToQuote(quoteId, itemGuid, epcQuoteItem);
                                    epcUpdateModifiedItemToQuoteResult = new EpcUpdateModifiedItemToQuoteResult();
System.out.println(logStr + "caseId:" + smcCaseId + ",epcUpdateModifiedItemToQuoteResult.getResult():" + epcUpdateModifiedItemToQuoteResult.getResult());
System.out.println(logStr + "caseId:" + smcCaseId + ",epcUpdateModifiedItemToQuoteResult.getErrMsg():" + epcUpdateModifiedItemToQuoteResult.getErrMsg());
                                    if("SUCCESS".equals(epcUpdateModifiedItemToQuoteResult.getResult())) {
                                        // success
                                    
                                        // insert attr record for dummy sim
                                        isAdded = epcOrderAttrHandler.addAttr(smcOrderId, smcCaseId, genDummySimItemId(smcCaseId), epcOrderAttrHandler.ATTR_TYPE_DUMMY_SIM, dummySimNo);
                                        isAdded = epcOrderAttrHandler.addAttr(smcOrderId, smcCaseId, genDummySimItemId(smcCaseId), epcOrderAttrHandler.ATTR_TYPE_DUMMY_IMSI, dummyImsi);
System.out.println(logStr + "caseId:" + smcCaseId + ",add dummy attr record:" + isAdded);
                                        // end of insert attr record for dummy sim
                                    } else {
                                        // error
                                        throw new Exception("submit dummy sim to sigma fail, caseId:" + smcCaseId + ", err:" + epcUpdateModifiedItemToQuoteResult.getErrMsg());
                                    }
                                    // end of submit to cpq
                                } else {
System.out.println(logStr + "caseId:" + smcCaseId + " not contain dummy value. NOT perform 'replace dummy sim' action");
                                }
                            }
                        }
                        // end of set it in product candidate
                    }
                }
            }
            
            epcGenerateDummySimResult.setResult("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            
            epcGenerateDummySimResult.setResult("FAIL");
            epcGenerateDummySimResult.setErrMsg(e.getMessage());
        }
        return epcGenerateDummySimResult;
    }
    
    
    public static String genDummySimItemId(String prefix) {
        return prefix + "_SIM";
    }
    
    
    public static EpcNextSimResult getDummySimFromPool(String dealerCode) {
        EpcNextSimResult epcNextSimResult = EpcNumberHandler.nextSim("MOBILE", dealerCode); // tmp use, kerrytsang, 20201125
        return epcNextSimResult;
    }
}
