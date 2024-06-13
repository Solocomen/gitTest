/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;

/**
 *
 * @author KerryTsang
 */
@Service
public class EpcOrderTypeHandler {
    
    // those constant are confirmed with Hansen 
    final String newActivationImmediate = "New Subscription";
    final String newActivationPending = "New Subscription Pending";
    final String activatePE = "Activate Pending Subscription";
    final String changeMsisdn = "Change MSISDN";
    final String changeOwner = "Change Owner";
    final String disconnect = "Disconnect";
    final String changeSim = "Change SIM";
    final String reconnect = "Reconnect";
    final String productPurchase = "Product Purchase";
    
    final String actionTypeNewActivation = "NEW_ACTIVATION";
    final String actionTypeActivatePending = "ACTIVATE_PENDING";
    final String actionTypeChangeMsisdn = "CHANGE_MSISDN";
    final String actionTypeChangeOwner = "CHANGE_OWNER";
    final String actionTypeDisconnect = "DISCONNECT";
    final String actionTypeChangeSim = "CHANGE_SIM";
    final String actionTypeReconnect = "RECONNECT";
    final String actionProductPurchase = "PRODUCT_PURCHASE";
    
    
    public String determineSigmaOrderType(String actionType, String effectiveDateYYYYMMDDHHMISS) {
        Date effectiveDate = null;
        Date currentDate = null;
        String orderType = "";
        SimpleDateFormat sdfYYYYMMDDHHMISS = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
        
        try {
            if(actionTypeNewActivation.equals(actionType)) {
                effectiveDate = sdfYYYYMMDDHHMISS.parse(effectiveDateYYYYMMDDHHMISS);
                effectiveDate = sdfYYYYMMDD.parse(sdfYYYYMMDD.format(effectiveDate)); // date obj in yyyymmdd

                currentDate = sdfYYYYMMDD.parse(sdfYYYYMMDD.format(new java.util.Date()));

                if(effectiveDate.compareTo(currentDate) < 0 || effectiveDate.compareTo(currentDate) == 0) {
                    // effective date before/equal to today => activate immediately
                    orderType = newActivationImmediate;
                } else {
                    orderType = newActivationPending;
                }
            } else if(actionTypeActivatePending.equals(orderType)) {
                orderType = activatePE;
            } else if(actionTypeChangeMsisdn.equals(orderType)) {
                orderType = changeMsisdn;
            } else if(actionTypeChangeOwner.equals(orderType)) {
                orderType = changeOwner;
            } else if(actionTypeDisconnect.equals(orderType)) {
                orderType = disconnect;
            } else if(actionTypeChangeSim.equals(orderType)) {
                orderType = changeSim;
            } else if(actionTypeReconnect.equals(orderType)) {
                orderType = reconnect;
            } else if(actionProductPurchase.equals(orderType)) {
                orderType = productPurchase;
            } else {
                orderType = productPurchase; // default
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            orderType = "??";
        }
        return orderType;
    }
}
