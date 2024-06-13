package epc.epcsalesapi.stock;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.EpcContactInfoHandler;
import epc.epcsalesapi.sales.EpcMsgHandler;
import epc.epcsalesapi.sales.EpcOrderLogHandler;
import epc.epcsalesapi.sales.EpcPaymentHandler;
import epc.epcsalesapi.sales.EpcSalesmanHandler;
import epc.epcsalesapi.sales.bean.EpcCharge;
import epc.epcsalesapi.sales.bean.EpcCheckOrderReferenceWithItemIdAndReserveId;
import epc.epcsalesapi.sales.bean.EpcGetRemainingCharge;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcLogStockStatus;
import epc.epcsalesapi.sales.bean.EpcNotificationMessage;
import epc.epcsalesapi.sales.bean.EpcOrderContact;
import epc.epcsalesapi.sales.bean.EpcStockReady;

@Service
public class EpcStockReadyHandler {
    
    private final Logger logger = LoggerFactory.getLogger(EpcStockReadyHandler.class);

    private final DataSource epcDataSource;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcContactInfoHandler epcContactInfoHandler;
    private final EpcPaymentHandler epcPaymentHandler;
    private final EpcMsgHandler epcMsgHandler;
    private final EpcSalesmanHandler epcSalesmanHandler;
    private final EpcOrderLogHandler epcOrderLogHandler;
    private final EpcStockStatusDescHandler epcStockStatusDescHandler;

    final String DATE_TYPE_PICKUP = "PICKUP";
    final String DATE_TYPE_PICKUP_AND_SETTLE = "PICKUP_AND_SETTLE";


    public EpcStockReadyHandler(
        DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper,
        EpcContactInfoHandler epcContactInfoHandler, EpcPaymentHandler epcPaymentHandler,
        EpcMsgHandler epcMsgHandler, EpcSalesmanHandler epcSalesmanHandler,
        EpcOrderLogHandler epcOrderLogHandler, EpcStockStatusDescHandler epcStockStatusDescHandler
    ) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcContactInfoHandler = epcContactInfoHandler;
        this.epcPaymentHandler = epcPaymentHandler;
        this.epcMsgHandler = epcMsgHandler;
        this.epcSalesmanHandler = epcSalesmanHandler;
        this.epcOrderLogHandler = epcOrderLogHandler;
        this.epcStockStatusDescHandler = epcStockStatusDescHandler;
    }


    public EpcStockReady stockReady(EpcStockReady epcStockReady) {
        Connection conn = null;
        boolean isValid = true;
        String errMsg = "";
        int orderId = 0;
        String custId = "";
        String orderReference = "";
        String itemId = "";
        String reserveId = "";
        EpcCheckOrderReferenceWithItemIdAndReserveId epcCheckOrderReferenceWithItemIdAndReserveId = null;
        String tmpResult = "";
        String tmpErrMsg = "";
        boolean isUpdate = false;
        EpcGetRemainingCharge epcGetRemainingCharge = new EpcGetRemainingCharge();
        BigDecimal remainingAmount = null;
        ArrayList<EpcCharge> itemList = null;
        EpcCharge epcCharge = null;
        EpcNotificationMessage epcNotificationMessage = null;
        String emailContent = "";
        String emailSubject = "";
        String emailSender = "";
        String emailSenderAddr = "";
        String smsContent = "";
        String smsOA = "";
        EpcOrderContact epcOrderContact = null;
        String orderLang = "";
        String contactEmail = "";
        String premium = "";
        boolean isMainUnit = false;
        boolean isMainDeviceReady = false;
        HashMap<String, String> dateStringMap = null;
        String startDate = "";
        String endDate = "";
        ArrayList<String> premiumList = null;
        String tmpProductDesc = "";
        String logStr = "[stockReady]";
        String tmpLogStr = "";

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            orderReference = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcStockReady.getOrderReference()));
            itemId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcStockReady.getItemId()));
            reserveId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcStockReady.getReserveId()));

            logStr += "[orderReference:" + orderReference + "][itemId:" + itemId + "][reserveId:" + "] ";


            // basic checking

            // check whether input order reference, item id, reserve id in epc_order_item
            epcCheckOrderReferenceWithItemIdAndReserveId = checkOrderReferenceWithItemIdAndReserveId(conn, orderReference, itemId, reserveId);
            tmpResult = epcSecurityHelper.encodeForSQL(epcCheckOrderReferenceWithItemIdAndReserveId.getResult());
            tmpErrMsg = epcSecurityHelper.encodeForSQL(epcCheckOrderReferenceWithItemIdAndReserveId.getErrMsg());
            orderId = epcCheckOrderReferenceWithItemIdAndReserveId.getOrderId();
            custId = epcCheckOrderReferenceWithItemIdAndReserveId.getCustId();
            premium = epcCheckOrderReferenceWithItemIdAndReserveId.getPremium();
            if("Y".equals(premium)) {
                isMainUnit = false;
                isMainDeviceReady = isMainDeviceReady(conn, orderId, itemId); // by premium
            } else {
                isMainUnit = true;
                isMainDeviceReady = true;
            }

            tmpLogStr = "epcCheckOrderReferenceWithItemIdAndReserveId result:" + tmpResult +
                        ",errMsg:" + tmpErrMsg +
                        ",orderId:" + orderId +
                        ",premium:" + premium +
                        ",mainUnit:" + isMainUnit +
                        ",isMainDeviceReady:" + isMainDeviceReady;
logger.info("{}{}", logStr, tmpLogStr);
            if(!"VALID".equals(tmpResult)) {
                isValid = false;
                errMsg += tmpErrMsg + ". ";
            }

            // end of basic checking


            if(isValid) {
                // get order info
                epcOrderContact = epcContactInfoHandler.getContactInfo(custId, orderId, "N"); // use non-masked values
                orderLang = epcOrderContact.getOrderLang();
                try {
                    contactEmail = StringHelper.trim(EpcCrypto.dGet(epcOrderContact.getContactEmail(), "utf-8"));
                } catch (Exception e) {
                    contactEmail = "";
                }

                // update product status (epc_order_item.stock_status, stock_status_desc)
                // update charge (mark 02 charge to need to pay - needToPay from N to Y)
                isUpdate = updateStockWhenStockReady(conn, orderId, itemId, reserveId);
logger.info("{}{}", logStr, "updateStockWhenStockReady:" + isUpdate);

                // check whether the related charge is fully paid
                //   if yes
                //     just compile message (pickup)
                //   otherwise
                //     update epc_order_charge.need_to_pay = Y from N
                //     calc the remaining amount
                //     compile message (settle remaining amount)
                //   then save message in epc_msg_queue
                itemList = new ArrayList<EpcCharge>();
                epcCharge = new EpcCharge();
                epcCharge.setItemId(itemId);
                itemList.add(epcCharge);

                epcGetRemainingCharge = new EpcGetRemainingCharge();
                epcGetRemainingCharge.setOrderId(orderId);
                epcGetRemainingCharge.setItems(itemList);
                epcPaymentHandler.getRemainingCharges(conn, epcGetRemainingCharge);
                remainingAmount = epcGetRemainingCharge.getRemainingCharge();
logger.info("{}{}", logStr, "remainingAmount:" + remainingAmount);
                if(remainingAmount.compareTo(new BigDecimal(0)) == 1) {
                    // if remaining amount > 0
//                    emailContent = "Your reservation item is ready, kindly pay the remaining amount $" + remainingAmount + " thru xxxxxxxxx";
//                    smsContent = "Your reservation item is ready, kindly pay the remaining amount $" + remainingAmount + " thru xxxxxxxxx";

                    emailSubject = getTemplate(conn, "STOCK_READY_EMAIL_SETTLE_SUBJECT", orderLang);
                    emailContent = getTemplate(conn, "STOCK_READY_EMAIL_SETTLE", orderLang);

                    smsContent = getTemplate(conn, "STOCK_READY_SMS_SETTLE", orderLang);

                    dateStringMap = getDateStringMap(conn, DATE_TYPE_PICKUP_AND_SETTLE);
                } else {
                    // else just fulfill
//                    emailContent = "Your item is ready, kindly pickup at ...";
//                    smsContent = "Your item is ready, kindly pickup at ...";

                    emailSubject = getTemplate(conn, "STOCK_READY_EMAIL_SUBJECT", orderLang);
                    emailContent = getTemplate(conn, "STOCK_READY_EMAIL", orderLang);

                    smsContent = getTemplate(conn, "STOCK_READY_SMS", orderLang);

                    dateStringMap = getDateStringMap(conn, DATE_TYPE_PICKUP);
                }
                emailSender = getTemplate2(conn, "STOCK_READY_EMAIL_SENDER");
                emailSenderAddr = getTemplate2(conn, "STOCK_READY_EMAIL_SENDER_ADDR");
                smsOA = getTemplate2(conn, "STOCK_READY_SMS_OA");


                // prepare notification content
                emailSubject = emailSubject.replace("[ORDER_REFERENCE]", orderReference);
                emailContent = emailContent.replace("[ORDER_REFERENCE]", orderReference);

                smsContent = smsContent.replace("[ORDER_REFERENCE]", orderReference);

                // construct product desc, kerrytsang, 20230918
                //  will become multiple 
                //  case 1
                //   day 1
                //    main device not ready
                //    premium ready, NOT SEND
                //   day 2
                //    main device also ready => sent with ready premium
                if("C".equals(orderLang)) {
                    tmpProductDesc = epcCheckOrderReferenceWithItemIdAndReserveId.getProductDescChi();
                } else {
                    tmpProductDesc = epcCheckOrderReferenceWithItemIdAndReserveId.getProductDesc();
                }

                if(isMainUnit) {
                    // main device ready
                    //  get ready premium
                    premiumList = getPremiumProductDescList(conn, orderId, itemId, orderLang);

                    for(String s : premiumList) {
                        tmpProductDesc += " / " + s;
                    }
                }

                emailContent = emailContent.replace("[PRODUCT_DESC]", tmpProductDesc);
                smsContent = smsContent.replace("[PRODUCT_DESC]", tmpProductDesc);
                // end of construct product desc, kerrytsang, 20230918
                
                smsContent = smsContent.replace("[START_DATE]", dateStringMap.get("START_DATE_C"));
                smsContent = smsContent.replace("[END_DATE]", dateStringMap.get("END_DATE_C"));
                // end of prepare notification content


                // prepare email
                if("".equals(epcOrderContact.getContactEmail())) {
logger.info("{}{}", logStr, "not send email due to no contact email");
                } else if("Y".equals(premium) && !isMainDeviceReady) {
logger.info("{}{}", logStr, "not send email due to premium and main device not ready");
                } else {
logger.info("{}{}", logStr, "send email");
                    epcNotificationMessage = new EpcNotificationMessage();
                    epcNotificationMessage.setRequestSystem("EPC_STOCK_READY");
                    epcNotificationMessage.setSendType("EMAIL");
                    epcNotificationMessage.setSenderName(emailSender);
                    epcNotificationMessage.setSenderEmail(emailSenderAddr);
                    epcNotificationMessage.setToAddr(contactEmail);
                    epcNotificationMessage.setSubject(emailSubject);
                    epcNotificationMessage.setContent(emailContent);
                    epcNotificationMessage.setRequestId(itemId);
                    epcNotificationMessage.setOrderId(orderId + "");
// tmp comment out, users cannot provide email html template, kerrytsang, 20230518
//                    epcMsgHandler.createMsg(conn, epcNotificationMessage);

                    tmpLogStr = "createMsg result:" + epcSecurityHelper.encodeForSQL(epcNotificationMessage.getResult()) +
                                ",errMsg:" + epcSecurityHelper.encodeForSQL(epcNotificationMessage.getErrMsg());
logger.info("{}{}", logStr, tmpLogStr);
                }
                // end of prepare email

                // prepare sms
                if("".equals(epcOrderContact.getContactNo())) {
logger.info("{}{}", logStr, "not send sms due to no contact no");
                } else if("Y".equals(premium) && !isMainDeviceReady) {
                    logger.info("{}{}", logStr, "not send sms due to premium and main device not ready");
                } else {
logger.info("{}{}", logStr, "send sms");
                    epcNotificationMessage = new EpcNotificationMessage();
                    epcNotificationMessage.setRequestSystem("EPC_STOCK_READY");
                    epcNotificationMessage.setSendType("SMS");
                    epcNotificationMessage.setSenderName("");
                    epcNotificationMessage.setSenderEmail(smsOA); // OA
                    epcNotificationMessage.setToAddr(epcOrderContact.getContactNo()); // DA
                    epcNotificationMessage.setSubject("");
                    epcNotificationMessage.setContent(smsContent);
                    epcNotificationMessage.setRequestId(itemId);
                    epcNotificationMessage.setOrderId(orderId + "");
                    epcMsgHandler.createMsg(conn, epcNotificationMessage);

                    tmpLogStr = "createMsg result:" + epcSecurityHelper.encodeForSQL(epcNotificationMessage.getResult()) +
                                ",errMsg:" + epcSecurityHelper.encodeForSQL(epcNotificationMessage.getErrMsg());
logger.info("{}{}", logStr, tmpLogStr);
                }
                // end of prepare sms


                conn.commit();

                epcStockReady.setResult("SUCCESS");
            } else {
                epcStockReady.setResult("FAIL");
                epcStockReady.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcStockReady.setResult("FAIL");
            epcStockReady.setErrMsg(e.getMessage());

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return epcStockReady;
    }


    public HashMap<String, String> getDateStringMap(Connection conn, String dateType) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        HashMap<String, String> aMap = new HashMap<>();
        
        try {
            if(DATE_TYPE_PICKUP.equals(dateType)) {
                // already settle
                sql = "select to_char(sysdate,'dd/mm/yy') as start_e, " +
                      "       to_char(sysdate,'dd/mm/yyyy') as start_c, " + 
                      "       to_char(sysdate + 7,'dd/mm/yy') as end_e, " + 
                      "       to_char(sysdate + 7,'dd/mm/yyyy') as end_c " + 
                      "  from dual ";
                pstmt = conn.prepareStatement(sql);
                rset = pstmt.executeQuery();
                if(rset.next()) {
                    aMap.put("START_DATE_E", StringHelper.trim(rset.getString("start_e")));
                    aMap.put("END_DATE_E", StringHelper.trim(rset.getString("end_e")));
                    aMap.put("START_DATE_C", StringHelper.trim(rset.getString("start_c")));
                    aMap.put("END_DATE_C", StringHelper.trim(rset.getString("end_c")));
                }
                rset.close();
                pstmt.close();
            } else {
                // not yet settle
                sql = "select to_char(sysdate,'dd/mm/yy') as start_e, " +
                      "       to_char(sysdate,'dd/mm/yyyy') as start_c, " + 
                      "       to_char(sysdate + 7,'dd/mm/yy') as end_e, " + 
                      "       to_char(sysdate + 7,'dd/mm/yyyy') as end_c " + 
                      "  from dual ";
                pstmt = conn.prepareStatement(sql);
                rset = pstmt.executeQuery();
                if(rset.next()) {
                    aMap.put("START_DATE_E", StringHelper.trim(rset.getString("start_e")));
                    aMap.put("END_DATE_E", StringHelper.trim(rset.getString("end_e")));
                    aMap.put("START_DATE_C", StringHelper.trim(rset.getString("start_c")));
                    aMap.put("END_DATE_C", StringHelper.trim(rset.getString("end_c")));
                }
                rset.close();
                pstmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return aMap;
    }


    public EpcCheckOrderReferenceWithItemIdAndReserveId checkOrderReferenceWithItemIdAndReserveId(Connection conn, String orderReference, String itemId, String reserveId) {
        EpcCheckOrderReferenceWithItemIdAndReserveId epcCheckOrderReferenceWithItemIdAndReserveId = new EpcCheckOrderReferenceWithItemIdAndReserveId();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            sql = "select a.order_id, a.cust_id, b.cpq_item_desc, b.cpq_item_desc_chi, b.premium " +
                  "  from epc_order a, epc_order_item b " +
                  " where a.order_reference = ? " +
                  "   and b.order_id = a.order_id " +
                  "   and b.item_id = ? " +
                  "   and b.reserve_id = ? " + 
                  "   and b.stock_status not in (?,?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, orderReference); // order_reference
            pstmt.setString(2, itemId); // item_id
            pstmt.setString(3, reserveId); // reserve_id
            pstmt.setString(4, "F"); // stock_status - F (fulfilled)
            pstmt.setString(5, "CA"); // stock_status - CA (cancelled)
            rset = pstmt.executeQuery();
            if(rset.next()) {
                epcCheckOrderReferenceWithItemIdAndReserveId.setResult("VALID");
                epcCheckOrderReferenceWithItemIdAndReserveId.setOrderId(rset.getInt("order_id"));
                epcCheckOrderReferenceWithItemIdAndReserveId.setCustId(StringHelper.trim(rset.getString("cust_id")));
                epcCheckOrderReferenceWithItemIdAndReserveId.setProductDesc(StringHelper.trim(rset.getString("cpq_item_desc")));
                epcCheckOrderReferenceWithItemIdAndReserveId.setProductDescChi(StringHelper.trim(rset.getString("cpq_item_desc_chi")));
                epcCheckOrderReferenceWithItemIdAndReserveId.setPremium(StringHelper.trim(rset.getString("premium")));
            } else {
                epcCheckOrderReferenceWithItemIdAndReserveId.setResult("INVALID");
                epcCheckOrderReferenceWithItemIdAndReserveId.setErrMsg("valid item is not found");
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();

            epcCheckOrderReferenceWithItemIdAndReserveId.setResult("INVALID");
            epcCheckOrderReferenceWithItemIdAndReserveId.setErrMsg(e.getMessage());
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return epcCheckOrderReferenceWithItemIdAndReserveId;
    }


    public boolean updateStockWhenStockReady(Connection conn, int orderId, String itemId, String reserveId) {
        boolean isUpdate = false;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String remarks = "";
        String oldStockStatus = "";
        String newStockStatus = "PF";
        String newStockStatusDesc = "";
        EpcLogStockStatus epcLogStockStatus = null;

        try {
            // get info
            sql = "select stock_status " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and item_id = ? " +
                  "   and reserve_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, itemId); // item_id
            pstmt.setString(3, reserveId); // reserve_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                oldStockStatus = StringHelper.trim(rset.getString("stock_status"));
            }
            rset.close();
            pstmt.close();
            // end of get info


            newStockStatusDesc = epcStockStatusDescHandler.getEngDescByStatus(conn, newStockStatus);
            
            // update stock status
        	sql = "update epc_order_item " +
                  "   set stock_status = ?, " +
                  "       stock_status_desc = ?, " +
                  "       pickup_date = to_char(sysdate, 'yyyymmdd') " +
                  " where order_id = ? " +
                  "   and item_id = ? " +
                  "   and reserve_id = ? ";
        	pstmt = conn.prepareStatement(sql);
        	pstmt.setString(1, newStockStatus); // stock_status - PF
            pstmt.setString(2, newStockStatusDesc); // stock_status_desc
            pstmt.setInt(3, orderId); // order_id
            pstmt.setString(4, itemId); // item_id
            pstmt.setString(5, reserveId); // reserve_id
            pstmt.executeUpdate();
        	pstmt.close();

            // update charge
            sql = "update epc_order_charge " +
                  "   set need_to_pay = ? " +
                  " where order_id = ? " +
                  "   and parent_item_id = ? " +
                  "   and need_to_pay = ? " +
                  "   and paid = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "Y"); // need_to_pay - Y
            pstmt.setInt(2, orderId); // order_id
            pstmt.setString(3, itemId); // parent_item_id
            pstmt.setString(4, "N"); // need_to_pay - N
            pstmt.setString(5, "N"); // paid - N
            pstmt.executeUpdate();
            pstmt.close();

            // create log
            remarks += "update stock status (itemId:" + itemId + ") from " + oldStockStatus + " to " + newStockStatus;
            epcSalesmanHandler.createSalesmanLog(conn, orderId, "", "SysAdmin", "", "", "", epcSalesmanHandler.actionUpdateStockStatus, remarks);
            // end of create log

            // create log
            epcLogStockStatus = new EpcLogStockStatus();
            epcLogStockStatus.setOrderId(orderId);
            epcLogStockStatus.setItemId(itemId);
            epcLogStockStatus.setOldStockStatus(oldStockStatus);
            epcLogStockStatus.setNewStockStatus(newStockStatus);
            epcOrderLogHandler.logStockStatus(conn, epcLogStockStatus);
            // end of create log

            isUpdate = true;
        } catch (Exception e) {
        	e.printStackTrace();

            isUpdate = false;
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }


    public String getTemplate(Connection conn, String msgType, String lang) {
        String content = "";
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select msg_content  " +
                  "  from gp_msg_template " +
                  " where msg_type = ? " +
                  "   and lang = ? " +
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, msgType); // msg_type
            pstmt.setString(2, lang); // lang
            pstmt.setString(3, "A"); // status
            rset = pstmt.executeQuery();
            if(rset.next()) {
                content = StringHelper.trim(rset.getString("msg_content"));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return content;
    }


    public String getTemplate2(Connection conn, String msgType) {
        String content = "";
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select msg_content  " +
                  "  from gp_msg_template " +
                  " where msg_type = ? " +
                  "   and lang is null " +
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, msgType); // msg_type
            pstmt.setString(2, "A"); // status
            rset = pstmt.executeQuery();
            if(rset.next()) {
                content = StringHelper.trim(rset.getString("msg_content"));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return content;
    }


    public ArrayList<String> getPremiumProductDescList(Connection conn, int orderId, String itemId, String orderLang) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<String> pMap = new ArrayList<>();

        try {
            sql = "select b.item_id, b.cpq_item_desc, b.cpq_item_desc_chi  " +
                  "  from epc_order_item a, epc_order_item b " +
                  " where a.order_id = ? " +
                  "   and a.item_id = ? " +
                  "   and b.order_id = a.order_id " +
                  "   and b.case_id = a.case_id " +
                  "   and b.item_cat = ? " +
                  "   and b.premium = ? " +
                  "   and b.stock_status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, itemId); // item_id
            pstmt.setString(3, EpcItemCategory.DEVICE); // item_cat
            pstmt.setString(4, "Y"); // premium
            pstmt.setString(5, "PF"); // stock_status
            rset = pstmt.executeQuery();
            while(rset.next()) {
                if("C".equals(orderLang)) {
                    pMap.add(StringHelper.trim(rset.getString("cpq_item_desc_chi")));
                } else {
                    pMap.add(StringHelper.trim(rset.getString("cpq_item_desc")));
                }
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return pMap;
    }


    public boolean isMainDeviceReady(Connection conn, int orderId, String itemId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        boolean isMainDeviceReady = false;

        try {
            sql = "select 1 " +
                  "  from epc_order_item a, epc_order_item b " +
                  " where a.order_id = ? " +
                  "   and a.item_id = ? " +
                  "   and b.order_id = a.order_id " +
                  "   and b.case_id = a.case_id " +
                  "   and b.item_cat = ? " +
                  "   and b.premium = ? " +
                  "   and b.stock_status in (?,?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, itemId); // item_id
            pstmt.setString(3, EpcItemCategory.DEVICE); // item_cat
            pstmt.setString(4, "N"); // premium
            pstmt.setString(5, "PF"); // stock_status - PF
            pstmt.setString(6, "F"); // stock_status - F

            rset = pstmt.executeQuery();
            if(rset.next()) {
                isMainDeviceReady = true;
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return isMainDeviceReady;
    }
}
