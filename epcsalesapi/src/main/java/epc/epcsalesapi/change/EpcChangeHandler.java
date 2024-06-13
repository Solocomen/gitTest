/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.change;

import epc.epcsalesapi.helper.StringHelper;
//import epc.billing.rmm.EpcNumberHandler;
//import epc.change.bean.*;
//import epc.portfolio.*;
//import epc.sales.*;
//import epc.sales.bean.*;
//import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author KerryTsang
 */
public class EpcChangeHandler {
    
    public final static String replaceTypeChangeSim = "SIM Number";
    public final static String replaceTypeChangeImsi = "IMSI";
    
    final static String sigmaOrderTypeChangeSim = "Change SIM"; // confirmed with hansen

    
    
    /**
     * use to replace values (msisdn, sim, imei) in productcandidate for cpq
     * 
     * @param portfolioItemMap
     * @param replaceType
     * @param currentValue
     * @param newValue 
     */
    public static void replaceConfiguredValue(HashMap<String, Object> productCandidateMap, String replaceType, String currentValue, String newValue) {
        // cater current node value
        ArrayList<HashMap<String, Object>> configuredValueArrayList = (ArrayList<HashMap<String, Object>>)productCandidateMap.get("ConfiguredValue");
        if(configuredValueArrayList != null) {
            for (HashMap<String, Object> configuredValueMap: configuredValueArrayList) {
//                if(configuredValueMap.containsKey("CharacteristicName") && replaceType.equals(StringHelper.trim((String)configuredValueMap.get("CharacteristicName")))) {
                if(configuredValueMap.get("Value") != null && configuredValueMap.get("Value") instanceof ArrayList) {
                    ArrayList<HashMap<String, Object>> valueArrayList = (ArrayList<HashMap<String, Object>>)configuredValueMap.get("Value");
                    if(valueArrayList != null) {
                        for(HashMap<String, Object> valueMap: valueArrayList) {
                            if(valueMap.containsKey("ValueDetail") && replaceType.equals(StringHelper.trim((String)valueMap.get("ValueDetail"))) &&
                               currentValue.equals(StringHelper.trim((String)valueMap.get("Value")))
                            ) {
                                valueMap.put("Value", newValue);
                            }
                        }
                    }
                }
            }
        }
        // end of cater current node value
        
        // cater child nodes
        ArrayList<HashMap<String, Object>> childEntityArrayList = (ArrayList<HashMap<String, Object>>)productCandidateMap.get("ChildEntity");
        if(childEntityArrayList != null) {
            for (int i = 0; i < childEntityArrayList.size(); i++) {
                replaceConfiguredValue(childEntityArrayList.get(i), replaceType, currentValue, newValue);
            }
        }
        // end of cater child nodes
    }
    
    
//    public static void changeSim(EpcChangeSim epcChangeSim) {
//        String portfolioId = "";
//        HashMap <String, Object> contextMap = new HashMap <String, Object>();
//        HashMap <String, Object> orderMap = new HashMap <String, Object>();
//        HashMap <String, Object> customerMap = new HashMap <String, Object>();
//        EpcTmpQuote epcTmpQuote = null;
//        EpcCreateQuoteResult epcCreateQuoteResult = null;
//        String quoteGuid = "";
//        int smcOrderId = EpcOrderHandler.genOrderId();
//        EpcUpdatePortfolioToQuoteResult epcUpdatePortfolioToQuoteResult = null;
//        EpcQuote epcQuote = null;
//        EpcQuoteItem epcQuoteItem = null;
//        EpcQuoteItem[] quoteItems = null;
//        HashMap<String, Object> productCandidateMap = null;
//        String productCandidateString = "";
//        String custId = "";
//        String custNum = "";
//        String subrNum = "";
//        String currentSim = "";
//        String newSim = "";
//        String createUser = "";
//        String createSalesman = "";
//        String createLocation = "";
//        String createChannel = "";
//        String dealerCode = "";
//        String currentImsi = "";
//        String newImsi = "";
//        String effectiveDateYYYYMMDDHH24MISS = "";
//        HashMap<String, EpcQuoteItem> changeItemMap = new HashMap<String, EpcQuoteItem>(); // item guid, EpcQuoteItem
//        String itemGuid = "";
//        EpcUpdateModifiedItemToQuoteResult epcUpdateModifiedItemToQuoteResult = null;
//        boolean isValid = true;
//        String errMsg = "";
//        String logStr = "[changeSim]";
//        
//        try {
//            // get data
//            custId = StringHelper.trim(epcChangeSim.getCustId());
//            custNum = StringHelper.trim(epcChangeSim.getCustNum());
//            subrNum = StringHelper.trim(epcChangeSim.getSubrNum());
//            currentSim = StringHelper.trim(epcChangeSim.getCurrentSim());
//            newSim = StringHelper.trim(epcChangeSim.getNewSim());
//            dealerCode = StringHelper.trim(epcChangeSim.getDealerCode());
//            createUser = StringHelper.trim(epcChangeSim.getCreateUser());
//            createSalesman = StringHelper.trim(epcChangeSim.getCreateSalesman());
//            createLocation = StringHelper.trim(epcChangeSim.getCreateLocation());
//            createChannel = StringHelper.trim(epcChangeSim.getCreateChannel());
//            effectiveDateYYYYMMDDHH24MISS = StringHelper.trim(epcChangeSim.getEffectiveDateYYYYMMDDHH24MISS());
//            
//            try {
//                currentImsi = EpcNumberHandler.getImsiFromSim(currentSim); // 89852xxxxxxxxxxxxxxx (20 digit)
//            } catch (Exception ee) {
//                throw new Exception("cannot retrieve imsi of currentSim:" + currentSim + ". " + ee.getMessage());
//            }
//            try {
//                newImsi = EpcNumberHandler.getImsiFromSim(newSim); // 89852xxxxxxxxxxxxxxx (20 digit)
//            } catch (Exception ee) {
//                throw new Exception("cannot retrieve imsi of newSim:" + newSim + ". " + ee.getMessage());
//            }
//            
//            logStr += "[custId:" + custId + "][custNum:" + custNum + "][subrNum:" + subrNum + "] ";
//System.out.println(logStr + "currentSim:" + currentSim + ",newSim:" + newSim + ",currentImsi:" + currentImsi + ",newImsi:" + newImsi + ",effectiveDate:" + effectiveDateYYYYMMDDHH24MISS);
//            // end of get data
//            
//
//            // basic checking
//            if("".equals(custId)) {
//                isValid = false;
//                errMsg += "cust id is empty. ";
//            }
//            
//            if("".equals(custNum)) {
//                isValid = false;
//                errMsg += "cust num is empty. ";
//            }
//            
//            if("".equals(subrNum)) {
//                isValid = false;
//                errMsg += "subr num is empty. ";
//            }
//            
//            // get portfolioId by cust id + cust num + subr num
//            portfolioId = EpcPortfolioHandler.getCustPortfolioId(custId, custNum, subrNum);
//System.out.println(logStr + "portfolioId:" + portfolioId);
//            if("NOT_FOUND".equals(portfolioId)) {
//                errMsg += "portfolioId is not found. ";
//            }
//            // end of get portfolioId by cust id + cust num + subr num
//            
//            if("".equals(currentSim)) {
//                isValid = false;
//                errMsg += "current sim is empty. ";
//            }
//            
//            if("".equals(newSim)) {
//                isValid = false;
//                errMsg += "new sim is empty. ";
//            }
//            
//            if("".equals(effectiveDateYYYYMMDDHH24MISS)) {
//                isValid = false;
//                errMsg += "effective date is empty. ";
//            } else {
//                try {
//                    new SimpleDateFormat("yyyyMMddHHmmss").parse(effectiveDateYYYYMMDDHH24MISS);
//                } catch (Exception e) {
//                    isValid = false;
//                    errMsg += "effective date is invalid. ";
//                }
//            }
//            // end of basic checking
//            
//            
//            if(isValid) {
//                // create empty quote (with quote context)
//                orderMap.put("CRM_Order_ID", smcOrderId + "");
//                orderMap.put("Order_Channel", createChannel);
//                orderMap.put("Order_Created_By", createUser);
//                orderMap.put("Requested_Activation_Date", effectiveDateYYYYMMDDHH24MISS); // yyyymmddhh24miss
//                orderMap.put("Dealer_Code", dealerCode);
//                orderMap.put("Order_Type", sigmaOrderTypeChangeSim);
//                orderMap.put("eSIM_Indicator", "N");
//                contextMap.put("order", orderMap);
//
//                customerMap.put("Cust_Num", custNum);
//                contextMap.put("customer", customerMap);
//
//                epcTmpQuote = new EpcTmpQuote();
//                epcTmpQuote.setCustomerRef(custId);
//                epcTmpQuote.setItems(null);
//                epcTmpQuote.setQuoteType(0);
//                epcTmpQuote.setContextData(contextMap);
//
//System.out.println(logStr + "epcCreateQuoteResult start");
//                epcCreateQuoteResult = EpcQuoteHandler.createQuote(epcTmpQuote);
//System.out.println(logStr + "epcCreateQuoteResult.getResult():" + epcCreateQuoteResult.getResult());
//System.out.println(logStr + "epcCreateQuoteResult.getErrMsg():" + epcCreateQuoteResult.getErrMsg());
//                if("SUCCESS".equals(epcCreateQuoteResult.getResult())) {
//                    // success
//                    quoteGuid = epcCreateQuoteResult.getCpqQuoteGUID();
//System.out.println(logStr + "epcCreateQuoteResult quoteGuid:" + quoteGuid);
//                } else {
//                    // error
//                    throw new Exception(epcCreateQuoteResult.getErrMsg());
//                }
//                // end of create empty quote (with quote context)
//
//
//                // update portfolioId to quote
//System.out.println(logStr + "epcUpdatePortfolioToQuoteResult start");
//                epcUpdatePortfolioToQuoteResult = EpcQuoteHandler.updatePortfolioToQuote(quoteGuid, portfolioId);
//System.out.println(logStr + "epcUpdatePortfolioToQuoteResult.getResult():" + epcUpdatePortfolioToQuoteResult.getResult());
//System.out.println(logStr + "epcUpdatePortfolioToQuoteResult.getErrMsg():" + epcUpdatePortfolioToQuoteResult.getErrMsg());
//                if("SUCCESS".equals(epcUpdatePortfolioToQuoteResult.getResult())) {
//                    // success
//                    epcQuoteItem = epcUpdatePortfolioToQuoteResult.getEpcQuoteItem();
//                } else {
//                    // error
//                    throw new Exception(epcUpdatePortfolioToQuoteResult.getErrMsg());
//                }
//                // end of update portfolioId to quote
//
//
//                // replace new sim (with imsi) to productcandidate
//System.out.println(logStr + "replace new sim start");
//                itemGuid = epcQuoteItem.getId();
//System.out.println(logStr + "  itemGuid:" + itemGuid);
//                productCandidateMap = epcQuoteItem.getProductCandidate();
//                productCandidateString = productCandidateMap.toString();
//
//                if(productCandidateString.contains(replaceTypeChangeSim) && productCandidateString.contains(replaceTypeChangeImsi)) {
//                    replaceConfiguredValue(productCandidateMap, replaceTypeChangeSim, currentSim, newSim);
//                    replaceConfiguredValue(productCandidateMap, replaceTypeChangeImsi, currentImsi, newImsi);
//
//                    changeItemMap.put(itemGuid, epcQuoteItem);
//                }
//System.out.println(logStr + "replace new sim end");
//                // end of replace new sim (with imsi) to productcandidate
//
//
//                // update new productcandidate to cpq (only submit modified item)
//System.out.println(logStr + "update new productcandidate to cpq start");
//                itemGuid = epcQuoteItem.getId();
//System.out.println(logStr + "epcUpdateModifiedItemToQuoteResult epcQuoteItem.getProductCandidate():" + epcQuoteItem.getProductCandidate());
//                epcUpdateModifiedItemToQuoteResult = EpcQuoteHandler.updateModifiedItemToQuote(quoteGuid, itemGuid, epcQuoteItem);
//System.out.println(logStr + "epcUpdateModifiedItemToQuoteResult.getResult():" + epcUpdateModifiedItemToQuoteResult.getResult());
//System.out.println(logStr + "epcUpdateModifiedItemToQuoteResult.getErrMsg():" + epcUpdateModifiedItemToQuoteResult.getErrMsg());
//                if("SUCCESS".equals(epcUpdateModifiedItemToQuoteResult.getResult())) {
//                    // success
//                } else {
//                    // error
//                    throw new Exception(epcUpdateModifiedItemToQuoteResult.getErrMsg());
//                }
//System.out.println(logStr + "update new productcandidate to cpq end");
//                // end of update new productcandidate to cpq (only submit modified item)
//
//
//                // validate quote ?
//                // ...
//                // end of validate quote ?
//
//
//                // convert quote 
//                // ...
//
//
//                // submit quote
//                // ...
//
//
//                // create invoice for that sim (if submit quote is succeeded)
//                // ...
//                // end of create invoice for that sim (if submit quote is succeeded)
//            } else {
//                // error
//                // ...
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            
//        }
//    }
    
}
