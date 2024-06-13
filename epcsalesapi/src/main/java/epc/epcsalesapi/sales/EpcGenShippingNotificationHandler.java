package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcNotificationMessage;

@Service
public class EpcGenShippingNotificationHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcGenShippingNotificationHandler.class);

    private final DataSource epcDataSource;
    private final DataSource crmFesDataSource;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcMsgHandler epcMsgHandler;

    public EpcGenShippingNotificationHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper, EpcMsgHandler epcMsgHandler, DataSource crmFesDataSource) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcMsgHandler = epcMsgHandler;
        this.crmFesDataSource = crmFesDataSource;
    }

    public EpcNotificationMessage sendEmail(String orderId) {
    	Connection conn = null;
    	Connection fesConn = null;
        EpcNotificationMessage epcNotificationMessage = null;
        String sender = "";
        String senderAddr = "";
        String emailContent = "";
        String emailSubject = "";
        String contactEmail = "";
        int orderIdInt = Integer.valueOf(orderId);
        
        try {
        	conn = epcDataSource.getConnection();
        	fesConn = crmFesDataSource.getConnection();
        	sender = getTemplateWithNullLang(conn, "SHIPPING_EMAIL_SENDER");
        	senderAddr = getTemplateWithNullLang(conn, "SHIPPING_EMAIL_SENDER_ADDR");
        	emailContent = genShippingEmail(conn, fesConn, orderIdInt);
            emailSubject = genShippingEmailSubject(conn, orderIdInt);
            contactEmail = getContact(conn, orderIdInt, "EMAIL");
        	
        	epcNotificationMessage = new EpcNotificationMessage();
        	epcNotificationMessage.setSendType("EMAIL");
        	epcNotificationMessage.setSenderName(sender);
            epcNotificationMessage.setSenderEmail(senderAddr);
            epcNotificationMessage.setToAddr(contactEmail);
            epcNotificationMessage.setSubject(emailSubject);
            epcNotificationMessage.setContent(emailContent);
            epcNotificationMessage.setRequestId(String.valueOf(orderId));
            epcNotificationMessage.setOrderId(orderId + "");
            epcNotificationMessage.setRequestSystem("EPC_SHIPPING");            
            
            epcMsgHandler.createMsg(conn, epcNotificationMessage);	        
        } catch (Exception e) {
            e.printStackTrace();
            epcNotificationMessage.setResult("FAIL");
            epcNotificationMessage.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ee) {}
        }
        return epcNotificationMessage;
    }
    
    public EpcNotificationMessage sendSms(String orderId) {
    	Connection conn = null;
    	Connection fesConn = null;
        EpcNotificationMessage epcNotificationMessage = null;
        String sender = "";
        String smsContent = "";
        String contactNo = "";
        int orderIdInt = Integer.valueOf(orderId);
        
        try {
        	conn = epcDataSource.getConnection();
        	fesConn = crmFesDataSource.getConnection();
        	sender = getTemplateWithNullLang(conn, "SHIPPING_SMS_OA");
        	smsContent = genShippingSms(conn, fesConn, orderIdInt);
            contactNo = getContact(conn, orderIdInt, "SMS");
        	
        	epcNotificationMessage = new EpcNotificationMessage();
        	epcNotificationMessage.setSendType("SMS");
            epcNotificationMessage.setSenderName("");
            epcNotificationMessage.setSenderEmail(sender); // OA
            epcNotificationMessage.setToAddr(contactNo); // DA
            epcNotificationMessage.setSubject("");
            epcNotificationMessage.setContent(smsContent);
            epcNotificationMessage.setRequestId(orderId);
            epcNotificationMessage.setOrderId(orderId + "");
            epcNotificationMessage.setRequestSystem("EPC_SHIPPING");            
            
            epcMsgHandler.createMsg(conn, epcNotificationMessage);
            epcNotificationMessage.setResult("SUCCESS");
	        
        } catch (Exception e) {
            e.printStackTrace();
            epcNotificationMessage.setResult("FAIL");
            epcNotificationMessage.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ee) {}
        }
        return epcNotificationMessage;
    }
    
    public String getContact(Connection conn, int orderId, String type) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String contactEmail = "";
        String contactNo = "";
    	// get email
        try {
	    	sql = "select contact_no, contact_email from epc_order where order_id = ? ";
	    	pstmt = conn.prepareStatement(sql);
	    	pstmt.setInt(1, Integer.valueOf(orderId)); // order_id
	        rset = pstmt.executeQuery();
	        if(rset.next()) {
	        	contactNo = StringHelper.trim(rset.getString("contact_no"));
	        	contactEmail = StringHelper.trim(rset.getString("contact_email"));
	        } rset.close();
	        pstmt.close();
        

	        if(!"".equals(contactEmail)) {
	            try {
	            	contactEmail = StringHelper.trim(EpcCrypto.dGet(contactEmail, "utf-8"));
	            } catch (Exception e) {
	            	contactEmail = "";
	            }
	        }
	        
	        if ("SMS".equals(type)) {
	        	return contactNo;
	        } else {
	        	return contactEmail;
	        }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return "";
    }

    public String genShippingEmail(Connection conn, Connection fesConn, int orderId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        PreparedStatement pstmtFes = null;
        ResultSet rsetC = null;
        ResultSet rsetProduct = null;
        String sql = "";
        String orderReference = "";
        String orderDate = ""; 
        String orderDateChi = ""; 
        String orderLang = "";
        String custName = "";
        String custFirstName = "";
        String custLastName = "";
        String tmpProductSection = "";
        String tmpProductDesc = "";
        String tmpProductDescChi = "";
        StringBuilder productSectionSB = new StringBuilder();
        String emailTemplate = "";
        String productTemplate = "";
        String recipientName = "";
        String recipientContactNo = "";
        String estDeliveryDate = "";
        String estDeliveryDateChi = "";
        String deliveryAddr = "";
        String deliveryAddr1 = "";
        String deliveryAddr2 = "";
        String deliveryAddrDistrict = "";
        String deliveryAddrArea = "";
        String deliveryAddr1Chi = "";
        String deliveryAddr2Chi = "";
        String deliveryAddrDistrictChi = "";
        String deliveryAddrAreaChi = "";
        String courierCode = "";
        String courierName = "";
        String courierContact = "";
        String wayBillNo = "";
        String trackingUrl = "";
        
        String logStr = "[genShippingEmail][orderId:" + orderId + "] ";

        try {
            // get order info
            sql = "select order_reference, to_char(order_date,'dd Mon yyyy') as o_date, to_char(order_date,'yyyy\"年\"mm\"月\"dd\"日\"') as o_date_chi, order_lang, contact_email, contact_no, " +
                  "       contact_person_first_name, contact_person_last_name, place_order_user, place_order_salesman, place_order_location, " +
                  "       to_char(place_order_date,'yyyymmdd') as po_date " +
                  "  from epc_order " +
                  " where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                orderReference = StringHelper.trim(rset.getString("order_reference"));
                orderDate = StringHelper.trim(rset.getString("o_date"));
                orderDateChi = StringHelper.trim(rset.getString("o_date_chi"));
                orderLang = StringHelper.trim(rset.getString("order_lang"));
                custFirstName = StringHelper.trim(rset.getString("contact_person_first_name"));
                custLastName = StringHelper.trim(rset.getString("contact_person_last_name"));
            } rset.close();
            pstmt.close();

            if(!"".equals(custFirstName)) {
                try {
                    custFirstName = StringHelper.trim(EpcCrypto.dGet(custFirstName, "utf-8"));
                } catch (Exception e) {
                    custFirstName = "";
                }
            }

            if(!"".equals(custLastName)) {
                try {
                    custLastName = StringHelper.trim(EpcCrypto.dGet(custLastName, "utf-8"));
                } catch (Exception e) {
                    custLastName = "";
                }
            }

            // get products
            sql = "select item_id, cpq_item_desc, cpq_item_desc_chi, cpq_item_value, stock_status " +
                  "  from epc_order_item " +
                  " where order_id = ? " + 
                  "   and item_cat in (?,?,?,?,?,?) " +
                  " order by case_id ";
            pstmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, EpcItemCategory.DEVICE); // itemCat - DEVICE
            pstmt.setString(3, EpcItemCategory.SIM); // itemCat - SIM
            pstmt.setString(4, EpcItemCategory.APPLECARE); // itemCat - APPLECARE
            pstmt.setString(5, EpcItemCategory.SCREEN_REPLACE); // itemCat - SCREEN_REPLACE
            pstmt.setString(6, EpcItemCategory.GIFT_WRAPPING); // itemCat - GIFT_WRAPPING
            pstmt.setString(7, EpcItemCategory.PLASTIC_BAG); // itemCat - PLASTIC_BAG
            rsetProduct = pstmt.executeQuery();
            
            rsetProduct.beforeFirst();
            // end of get products
            
            // get delivery info
            sql = "select deliver_contact_person, deliver_contact_no, deliver_addr_1, deliver_addr_2, deliver_district, deliver_area, " +
                  "       deliver_addr_1_chi, deliver_addr_2_chi, deliver_district_chi, deliver_area_chi, courier_company, courier_form_num, " +
                  "       to_char(to_date(delivery_date,'YYYYMMDD'), 'DD Mon YYYY') as delivery_date, to_char(to_date(delivery_date,'YYYYMMDD'),'yyyy\"年\"mm\"月\"dd\"日\"') as delivery_date_chi " +
                  "  from epc_order_delivery " +
                  " where order_id = ? and delivery_method='COURIER' and status='A' ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
            	recipientName = StringHelper.trim(rset.getString("deliver_contact_person"));
            	recipientContactNo = StringHelper.trim(rset.getString("deliver_contact_no"));
            	deliveryAddr1 = StringHelper.trim(rset.getString("deliver_addr_1"));
            	deliveryAddr2 = StringHelper.trim(rset.getString("deliver_addr_2"));
            	deliveryAddrDistrict = StringHelper.trim(rset.getString("deliver_district"));
            	deliveryAddrArea = StringHelper.trim(rset.getString("deliver_area"));
            	deliveryAddr1Chi = StringHelper.trim(rset.getString("deliver_addr_1_chi"));
            	deliveryAddr2Chi = StringHelper.trim(rset.getString("deliver_addr_2_chi"));
            	deliveryAddrDistrictChi = StringHelper.trim(rset.getString("deliver_district_chi"));
            	deliveryAddrAreaChi = StringHelper.trim(rset.getString("deliver_area_chi"));
            	courierCode = StringHelper.trim(rset.getString("courier_company"));
            	wayBillNo = StringHelper.trim(rset.getString("courier_form_num"));
            	estDeliveryDate = StringHelper.trim(rset.getString("delivery_date"));
            	estDeliveryDateChi = StringHelper.trim(rset.getString("delivery_date_chi"));
            } rset.close();
            pstmt.close();
            
            // get courier info
            sql = "select key_str1, key_str2, key_str3, key_str4, key_str5, key_number1 from epc_control_tbl where rec_type = ? and key_str1 = ? and key_str5 = ? ";
            pstmtFes = conn.prepareStatement(sql);
            pstmtFes.setString(1, "EPC_COURIER_COMPANY");
            pstmtFes.setString(2, courierCode); // courier_company
            pstmtFes.setString(3, "A");
            rsetC = pstmtFes.executeQuery();
            if(rsetC.next()) {
                courierContact = StringHelper.trim(String.valueOf(rsetC.getInt("key_number1")));
                trackingUrl = StringHelper.trim(rsetC.getString("key_str4"));
                courierName = StringHelper.trim(rsetC.getString("key_str2"));
                if ("C".equalsIgnoreCase(orderLang)) {
                	courierName = StringHelper.trim(rsetC.getString("key_str3"));
                }
            } rsetC.close();
            pstmtFes.close();
              
            // end of get order info


            // get template
            emailTemplate = getTemplate(conn, "SHIPPING_EMAIL", orderLang);
            productTemplate = getTemplate(conn, "SHIPPING_EMAIL_PRODUCTS", orderLang);

            // replace data into template
            emailTemplate = emailTemplate.replaceAll("\\[ORDER_REF\\]", orderReference);

            custName = "NA"; // default
            if(!"".equals(custLastName) && !"".equals(custFirstName)) {
                custName = custLastName + " " + custFirstName;
            } else if(!"".equals(custLastName) && "".equals(custFirstName)) {
                custName = custLastName;
            }
            emailTemplate = emailTemplate.replaceAll("\\[NAME\\]", custName);

            emailTemplate = emailTemplate.replaceAll("\\[SHIPPING_DATE\\]", "C".equalsIgnoreCase(orderLang) ? orderDateChi : orderDate);

            // loop thru products
            while(rsetProduct.next()) {
                tmpProductSection = productTemplate; // reset per item

                tmpProductDesc = StringHelper.trim(rsetProduct.getString("cpq_item_desc"));
                tmpProductDescChi = StringHelper.trim(rsetProduct.getString("cpq_item_desc_chi"));

                if("E".equals(orderLang)) {
                    tmpProductSection = tmpProductSection.replace("[PRODUCT_DESC]", tmpProductDesc);
                } else {
                    tmpProductSection = tmpProductSection.replace("[PRODUCT_DESC]", tmpProductDescChi);
                }

                productSectionSB.append(tmpProductSection);
            } rsetProduct.close();
            
            emailTemplate = emailTemplate.replace("[PRODUCTS]", productSectionSB.toString());
            
            // replace delivery info
            if(!"".equals(recipientName)) {
                try {
                	recipientName = StringHelper.trim(EpcCrypto.dGet(recipientName, "utf-8"));
                } catch (Exception e) {
                	recipientName = "";
                }
            }
            if(!"".equals(deliveryAddr1)) {
                try {
                	deliveryAddr1 = StringHelper.trim(EpcCrypto.dGet(deliveryAddr1, "utf-8"));
                } catch (Exception e) {
                	deliveryAddr1 = "";
                }
            }
            if(!"".equals(deliveryAddr2)) {
                try {
                	deliveryAddr2 = StringHelper.trim(EpcCrypto.dGet(deliveryAddr2, "utf-8"));
                } catch (Exception e) {
                	deliveryAddr2 = "";
                }
            }
            if(!"".equals(deliveryAddrDistrict)) {
                try {
                	deliveryAddrDistrict = StringHelper.trim(EpcCrypto.dGet(deliveryAddrDistrict, "utf-8"));
                } catch (Exception e) {
                	deliveryAddrDistrict = "";
                }
            }
            if(!"".equals(deliveryAddrArea)) {
                try {
                	deliveryAddrArea = StringHelper.trim(EpcCrypto.dGet(deliveryAddrArea, "utf-8"));
                } catch (Exception e) {
                	deliveryAddrArea = "";
                }
            }
            if(!"".equals(deliveryAddr1Chi)) {
                try {
                	deliveryAddr1Chi = StringHelper.trim(EpcCrypto.dGet(deliveryAddr1Chi, "utf-8"));
                } catch (Exception e) {
                	deliveryAddr1Chi = "";
                }
            }
            if(!"".equals(deliveryAddr2Chi)) {
                try {
                	deliveryAddr2Chi = StringHelper.trim(EpcCrypto.dGet(deliveryAddr2Chi, "utf-8"));
                } catch (Exception e) {
                	deliveryAddr2Chi = "";
                }
            }
            if(!"".equals(deliveryAddrDistrictChi)) {
                try {
                	deliveryAddrDistrictChi = StringHelper.trim(EpcCrypto.dGet(deliveryAddrDistrictChi, "utf-8"));
                } catch (Exception e) {
                	deliveryAddrDistrictChi = "";
                }
            }
            if(!"".equals(deliveryAddrAreaChi)) {
                try {
                	deliveryAddrAreaChi = StringHelper.trim(EpcCrypto.dGet(deliveryAddrAreaChi, "utf-8"));
                } catch (Exception e) {
                	deliveryAddrAreaChi = "";
                }
            }
            
            deliveryAddr = deliveryAddr1 + " " + deliveryAddr2 + " " + deliveryAddrDistrict + " " + deliveryAddrArea;
            
            emailTemplate = emailTemplate.replaceAll("\\[RECIPIENT_NAME\\]", recipientName);
            emailTemplate = emailTemplate.replaceAll("\\[RECIPIENT_CONTACT_NO\\]", recipientContactNo);
            emailTemplate = emailTemplate.replaceAll("\\[EST_DELIVERY_DATE\\]", "C".equalsIgnoreCase(orderLang) ? estDeliveryDateChi : estDeliveryDate); // to replace
            emailTemplate = emailTemplate.replaceAll("\\[DELIVERY_ADDR\\]", deliveryAddr); 
            emailTemplate = emailTemplate.replaceAll("\\[COURIER_NAME\\]", courierName);
            emailTemplate = emailTemplate.replaceAll("\\[COURIER_CONTACT_NO\\]", courierContact);
            emailTemplate = emailTemplate.replaceAll("\\[WAY_BILL_NO\\]", wayBillNo);
            emailTemplate = emailTemplate.replaceAll("\\[TRACKING_HYPERLINE\\]", trackingUrl);
            
            // end of replace data into template
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(rsetC != null) { rsetC.close(); } } catch (Exception ee) {}
            try { if(pstmtFes != null) { pstmtFes.close(); } } catch (Exception ee) {}
            try { if(rsetProduct != null) { rsetProduct.close(); } } catch (Exception ee) {}
        }
        return emailTemplate;
    }

    public String genShippingEmailSubject(Connection conn, int orderId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String orderReference = "";
        String orderLang = "";
        String emailSubjectTemplate = "";
        
        String logStr = "[genShippingEmailSubject][orderId:" + orderId + "] ";

        try {
            // get order info
            sql = "select order_reference, order_lang " +
                  "  from epc_order " +
                  " where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                orderReference = StringHelper.trim(rset.getString("order_reference"));
                orderLang = StringHelper.trim(rset.getString("order_lang"));
            } rset.close();
            pstmt.close();
            // end of get order info


            // get template
            emailSubjectTemplate = getTemplate(conn, "SHIPPING_EMAIL_SUBJECT", orderLang);

            // replace data into template
            emailSubjectTemplate = emailSubjectTemplate.replaceAll("\\[ORDER_REF\\]", orderReference);
            
            // end of replace data into template
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return emailSubjectTemplate;
    }
    
    public String genShippingSms(Connection conn, Connection fesConn, int orderId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        PreparedStatement pstmtFes = null;
        ResultSet rsetC = null;
        String sql = "";
        String orderReference = "";
        String orderLang = "";
        String deliveryDate = "";
        String courierName = "";
        String courierCode = "";
        String wayBillNo = "";
        String smsTemplate = "";
        
        String logStr = "[genShippingSms][orderId:" + orderId + "] ";

        try {
            // get order info
            sql = "select order_reference, order_lang " +
                  "  from epc_order " +
                  " where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                orderReference = StringHelper.trim(rset.getString("order_reference"));
                orderLang = StringHelper.trim(rset.getString("order_lang"));
            } rset.close();
            pstmt.close();
            
            sql = "select deliver_contact_person, deliver_contact_no, deliver_addr_1, deliver_addr_2, deliver_district, deliver_area, " +
                    "       deliver_addr_1_chi, deliver_addr_2_chi, deliver_district_chi, deliver_area_chi, courier_company, courier_form_num, " +
                    "       to_char(to_date(delivery_date,'YYYYMMDD'), 'DD/MM/YYYY') as delivery_date " +
                    "  from epc_order_delivery " +
                    " where order_id = ? and delivery_method='COURIER' and status='A' ";
		    pstmt = conn.prepareStatement(sql);
		    pstmt.setInt(1, orderId); // order_id
		    rset = pstmt.executeQuery();
		    if(rset.next()) {
		    	courierCode = StringHelper.trim(rset.getString("courier_company"));
				wayBillNo = StringHelper.trim(rset.getString("courier_form_num"));
				deliveryDate = StringHelper.trim(rset.getString("delivery_date"));
			} rset.close();  
			pstmt.close();
			
			// get courier info
			sql = "select key_str1, key_str2, key_str3, key_str4, key_str5, key_number1 from epc_control_tbl where rec_type = ? and key_str1 = ? and key_str5 = ? ";
            pstmtFes = conn.prepareStatement(sql);
            pstmtFes.setString(1, "EPC_COURIER_COMPANY");
            pstmtFes.setString(2, courierCode); // courier_company
            pstmtFes.setString(3, "A");
            rsetC = pstmtFes.executeQuery();
            if(rsetC.next()) {
                courierName = StringHelper.trim(rsetC.getString("key_str2"));
                if ("C".equalsIgnoreCase(orderLang)) {
                	courierName = StringHelper.trim(rsetC.getString("key_str3"));
                }
            } rsetC.close();
            pstmtFes.close();
            // end of get order info


            // get template
            smsTemplate = getTemplate(conn, "SHIPPING_SMS", orderLang);

            // replace data into template
            smsTemplate = smsTemplate.replaceAll("\\[ORDER_REF\\]", orderReference);
            smsTemplate = smsTemplate.replaceAll("\\[DELIVERY_DATE\\]", deliveryDate); // to replace
            smsTemplate = smsTemplate.replaceAll("\\[COURIER_NAME\\]", courierName);
            smsTemplate = smsTemplate.replaceAll("\\[WAY_BILL_NO\\]", wayBillNo);
            // end of replace data into template
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(rsetC != null) { rsetC.close(); } } catch (Exception ee) {}
            try { if(pstmtFes != null) { pstmtFes.close(); } } catch (Exception ee) {}
        }
        return smsTemplate;
    }

    public String getTemplate(Connection conn, String templateName, String orderLang) {
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
            pstmt.setString(1, templateName); // msg_type
            pstmt.setString(2, orderLang); // lang
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

    public String getTemplateWithNullLang(Connection conn, String msgType) {
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

    public String getLangDisplay(Connection conn, String orderLang) {
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
            pstmt.setString(1, "EPC_SA_FORM_ORDER_LANG"); // msg_type
            pstmt.setString(2, orderLang); // lang
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
}
