package epc.epcsalesapi.sales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashSet;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import epc.epcsalesapi.helper.DateHelper;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcNotificationMessage;
import epc.epcsalesapi.sales.bean.EpcSignature;
import epc.epcsalesapi.sales.bean.genSa.EpcGenPdfResult;
import epc.epcsalesapi.sales.bean.genSa.EpcGenPdfResultDetail;

@Service
public class EpcGenDeliveryNoteHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcGenDeliveryNoteHandler.class);

    private final DataSource epcDataSource;
    private final DataSource fesDataSource;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcMsgHandler epcMsgHandler;
    private final EpcSignatureHandler epcSignatureHandler;
    private final EpcDocumentHandler epcDocumentHandler;

    public EpcGenDeliveryNoteHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper, EpcMsgHandler epcMsgHandler, DataSource fesDataSource, 
    		EpcSignatureHandler epcSignatureHandler, EpcDocumentHandler epcDocumentHandler) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcMsgHandler = epcMsgHandler;
        this.fesDataSource = fesDataSource;
        this.epcSignatureHandler = epcSignatureHandler;
        this.epcDocumentHandler = epcDocumentHandler;
    }
    
    public byte[] genDnPdf(int orderId) {
        byte[] dnPdfArray = null;
        String apiUrl = EpcProperty.getValue("EPC_MERGE_PDF_LINK");
        RestTemplate restTemplate = new RestTemplate();
        EpcGenPdfResult epcGenPdfResult = null;
        EpcGenPdfResultDetail epcGenPdfResultDetail = null;
        HttpHeaders headers = new HttpHeaders();
        String xmlString = "";
//        String saPath = "https://epcdev-sales.smartone.com/api/salesDocument/sa/" + orderId + "?signId=" + signId;
        String dnPath = EpcProperty.getValue("EPC_DN_HTML_LINK") + orderId;
        String pdfAuthorName = null;

        try {
            //Construct HKID as PDF author if has HKID
            pdfAuthorName = epcDocumentHandler.genPdfAuthor(orderId);
            
            headers.setContentType(MediaType.APPLICATION_XML);

            xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<cams>" +
                        "<merge_doc>" +
                        "<doc_links>" +
                        "<doc_link>" +
                        "<form_link>" + dnPath + "</form_link>" +
                        "<need_to_convert_pdf>Y</need_to_convert_pdf>" +
                        "<pdf_author_name>" + pdfAuthorName + "</pdf_author_name>" +
                        "</doc_link>" +
                        "</doc_links>" +
                        "</merge_doc>" +
                        "</cams>";
            epcGenPdfResult = restTemplate.postForObject(apiUrl, new HttpEntity<>(xmlString, headers), EpcGenPdfResult.class);
            if(epcGenPdfResult != null) {
                epcGenPdfResultDetail = epcGenPdfResult.getEpcGenPdfResultDetail();
                dnPdfArray = Base64.getDecoder().decode(epcGenPdfResultDetail.getMergeFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dnPdfArray;
    }
    
    public String genDnHtml(int orderId) {
        String dnHtml = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtProductTotalCharge = null;
        ResultSet rset = null;
        ResultSet rsetProduct = null;
        ResultSet rsetProductCharge = null;
        ResultSet rsetPayment = null;
        ResultSet rsetDelivery = null;
        ResultSet rsetDOALog = null;
        String sql = "";
        String orderReference = "";
        String orderDate = ""; // DD/MM/YYYY
        String contactEmail = "";
        String contactNo = "";
        String custName = "";
        String custFirstName = "";
        String custLastName = "";
        String productSectionTemplate = "";
        String tmpProductSection = "";
        String tmpItemId = "";
        String tmpProductDesc = "";
        String tmpProductCode = "";
        String tmpPickUpDate = "";
        String tmpImei = "";
        BigDecimal tmpProductTotalCharge = null;
        BigDecimal finalCharge = new BigDecimal(0);
        finalCharge.setScale(1);
        StringBuilder productSectionSB = new StringBuilder();
        String tmpSignSection = "";
        StringBuilder signSectionSB = new StringBuilder();
        String signSectionTemplate = "";
        String tmpSignSrc = "";
        String tmpSignDate = "";
        String paymentSectionTemplate = "";
        String tmpPaymentSection = "";
        StringBuilder paymentSectionSB = new StringBuilder();
        String tmpPaymentCode = "";
        String tmpCCNoMasked = "";
        String tmpCCNo = "";
        String tmpPickupArrange = "";
        String tmpPickupLocation = "";
        String tmpPickupMethod = "";
        HashSet<String> doaItem = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String logStr = "[genDnHtml][orderId:" + orderId + "] ";

        try {
            conn = epcDataSource.getConnection();

            sql = "select nvl(sum(charge_amount),0) " +
                  "  from epc_order_charge " + 
                  " where order_id = ? " + 
                  "   and parent_item_id = ? " +
                  "   and need_to_pay = ? ";
            pstmtProductTotalCharge = conn.prepareStatement(sql);

            // get order info
            sql = "select order_reference, to_char(order_date,'dd/mm/yyyy') as o_date, order_lang, contact_email, contact_no, " +
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
                contactEmail = StringHelper.trim(rset.getString("contact_email"));
                contactNo = StringHelper.trim(rset.getString("contact_no"));
                custFirstName = StringHelper.trim(rset.getString("contact_person_first_name"));
                custLastName = StringHelper.trim(rset.getString("contact_person_last_name"));
            } rset.close();
            pstmt.close();

            if(!"".equals(contactEmail)) {
                try {
                    contactEmail = StringHelper.trim(EpcCrypto.dGet(contactEmail, "utf-8"));
                } catch (Exception e) {
                    contactEmail = "";
                }
            }

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
            sql = "select item_id, cpq_item_desc, cpq_item_desc_chi, cpq_item_value, stock_status, item_code, to_char(invoice_date,'dd/mm/yyyy') as pickup_date " +
                  "  from epc_order_item " +
                  " where order_id = ? " + 
                  "   and item_cat in (?,?,?,?,?,?) " +
                  " order by case_id ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, EpcItemCategory.DEVICE); // itemCat - DEVICE
            pstmt.setString(3, EpcItemCategory.SIM); // itemCat - SIM
            pstmt.setString(4, EpcItemCategory.APPLECARE); // itemCat - APPLECARE
            pstmt.setString(5, EpcItemCategory.SCREEN_REPLACE); // itemCat - SCREEN_REPLACE
            pstmt.setString(6, EpcItemCategory.GIFT_WRAPPING); // itemCat - GIFT_WRAPPING
            pstmt.setString(7, EpcItemCategory.PLASTIC_BAG); // itemCat - PLASTIC_BAG
            rsetProduct = pstmt.executeQuery();
            // end of get products            

            // get payment
            sql = "select payment_code, sum(payment_amount) as p_amount, cc_no_masked " +
                  "  from epc_order_payment " +
                  " where order_id = ? " +
                  " group by payment_code, cc_no_masked ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rsetPayment = pstmt.executeQuery();
            // end of get payment
            
            // get delivery
            sql = "select delivery_method, pickup_location " +
                    "  from epc_order_delivery " +
                    " where order_id = ? " + 
                    " and status = 'A' ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rsetDelivery = pstmt.executeQuery();

            ArrayList<EpcSignature> signList = epcSignatureHandler.getSignature(orderId);
            
            // get DOA log
            sql = "select item_id, product_code " +
                    "  from epc_order_doa_log " +
                    " where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rsetDOALog = pstmt.executeQuery();
            // end of get order info


            // get template
            dnHtml = getTemplateWithNullLang(conn, "DELIVERY_NOTE");
            productSectionTemplate = getTemplateWithNullLang(conn, "DELIVERY_NOTE_PRODUCTS");
            paymentSectionTemplate = getTemplateWithNullLang(conn, "DELIVERY_NOTE_PAYMENT");
            signSectionTemplate = getTemplateWithNullLang(conn, "DELIVERY_NOTE_SIGN");
            // end of get template


            // replace data into template
            dnHtml = dnHtml.replaceAll("\\[ORDER_REF\\]", orderReference);
            dnHtml = dnHtml.replaceAll("\\[ORDER_DATE\\]", orderDate);

            custName = ""; // default
            if(!"".equals(custLastName) && !"".equals(custFirstName)) {
                custName = custLastName + " " + custFirstName;
            } else if(!"".equals(custLastName) && "".equals(custFirstName)) {
                custName = custLastName;
            }
            dnHtml = dnHtml.replaceAll("\\[CUST_NAME\\]", custName);

            if("".equals(contactEmail)) {
                contactEmail = "";
            }
            dnHtml = dnHtml.replaceAll("\\[CONTACT_EMAIL\\]", contactEmail);

            if("".equals(contactNo)) {
                contactNo = "";
            }
            dnHtml = dnHtml.replaceAll("\\[CONTACT_NO\\]", contactNo);
            
            // loop thru doa log
            while(rsetDOALog.next()) {
            	doaItem.add(StringHelper.trim(rsetDOALog.getString("item_id")));
            } rsetDOALog.close();

            // loop thru products
            while(rsetProduct.next()) {
                tmpProductSection = productSectionTemplate; // reset per item

                tmpItemId = StringHelper.trim(rsetProduct.getString("item_id"));
                tmpProductDesc = StringHelper.trim(rsetProduct.getString("cpq_item_desc"));
                tmpImei = StringHelper.trim(rsetProduct.getString("cpq_item_value"));
                tmpProductTotalCharge = new BigDecimal(0); // reset
                tmpProductTotalCharge.setScale(1);
                tmpProductCode = StringHelper.trim(rsetProduct.getString("item_code"));
                tmpPickUpDate = StringHelper.trim(rsetProduct.getString("pickup_date"));
                
                if (doaItem.contains(tmpItemId)) {
                	tmpProductDesc = "* " + tmpProductDesc;
                }

                tmpProductSection = tmpProductSection.replace("[PRODUCT_DESC]", tmpProductDesc);

                if(!"".equals(tmpImei)) {
                    tmpProductSection = tmpProductSection.replaceAll("\\[IMEI\\]", tmpImei + "<br>" + "(" + tmpProductCode + ")");
                } else {
                    tmpProductSection = tmpProductSection.replaceAll("\\[IMEI\\]", tmpProductCode);
                }

                // calc total charge
                pstmtProductTotalCharge.setInt(1, orderId); // order_id
                pstmtProductTotalCharge.setString(2, tmpItemId); // parent_item_id
                pstmtProductTotalCharge.setString(3, "Y"); // need_to_pay
                rsetProductCharge = pstmtProductTotalCharge.executeQuery();
                if(rsetProductCharge.next()) {
                    tmpProductTotalCharge = rsetProductCharge.getBigDecimal(1).setScale(1);
                } rsetProductCharge.close();

                tmpProductSection = tmpProductSection.replaceAll("\\[PRICE\\]", tmpProductTotalCharge + "");

                finalCharge = finalCharge.add(tmpProductTotalCharge);
                // end of calc total charge
                
                tmpProductSection = tmpProductSection.replaceAll("\\[PICKUP_DATE\\]", tmpPickUpDate);

                productSectionSB.append(tmpProductSection);
            } rsetProduct.close();

            dnHtml = dnHtml.replace("[PRODUCT_LIST]", productSectionSB.toString());
            dnHtml = dnHtml.replaceAll("\\[TOTAL\\]", finalCharge + "");
            // end of loop thru products
            

            // loop thru payment
            while(rsetPayment.next()) {
                tmpPaymentSection = paymentSectionTemplate; // reset per item
                tmpPaymentCode = StringHelper.trim(rsetPayment.getString("payment_code"));
                tmpCCNoMasked = StringHelper.trim(rsetPayment.getString("cc_no_masked"));
                tmpPaymentSection = tmpPaymentSection.replaceAll("\\[PAYMENT_CODE\\]", tmpPaymentCode + "");
                tmpCCNo = ""; //reset per item
                if (!"".equals(tmpCCNoMasked)) {
                	tmpCCNo ="CC Num: " + tmpCCNoMasked + "";
                }
                tmpPaymentSection = tmpPaymentSection.replaceAll("\\[REF_1\\]", tmpCCNo);
                paymentSectionSB.append(tmpPaymentSection);
            } rsetPayment.close();

            dnHtml = dnHtml.replaceAll("\\[PAYMENT_LIST\\]", paymentSectionSB.toString());
            // end of loop thru payment

            // replace customer signature
            for (EpcSignature signature : signList) {
            	if ("Y".equals(signature.getWithDn())){
            		tmpSignSection = signSectionTemplate; // reset per item
                    tmpSignSrc = signature.getContent();
                    tmpSignDate = signature.getCreateDate();
                    Date signDate = sdf.parse(tmpSignDate);
                    tmpSignDate = DateHelper.formatDateTime(signDate, DateHelper.DT_FMT_DN_SIGN_DATE);
                    tmpSignSection = tmpSignSection.replaceAll("\\[SIGN_SRC\\]", tmpSignSrc + "");
                    tmpSignSection = tmpSignSection.replaceAll("\\[DATE\\]", tmpSignDate + "");
                    signSectionSB.append(tmpSignSection);
            	}
            }
            dnHtml = dnHtml.replaceAll("\\[SIGN_LIST\\]", signSectionSB.toString());
            // end of replace customer signature
            
            // pickup arrangement
            if(rsetDelivery.next()) {
            	tmpPickupMethod = StringHelper.trim(rsetDelivery.getString("delivery_method"));
                tmpPickupLocation = StringHelper.trim(rsetDelivery.getString("pickup_location"));
                if ("COURIER".equals(tmpPickupMethod)) {
                	tmpPickupArrange = "Delivery";
                } else {
                	tmpPickupArrange = "Store pick up (" + tmpPickupLocation + ")";
                }
            } rsetDelivery.close();
            dnHtml = dnHtml.replaceAll("\\[PICKUP_ARRANGEMENT\\]", tmpPickupArrange + "");
            
            // end of replace data into template

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try { if(pstmtProductTotalCharge != null) { pstmtProductTotalCharge.close(); } } catch (Exception ee) {}
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return dnHtml;
    }
    
    public byte[] genDnArPdf(int orderId) {
        byte[] dnPdfArray = null;
        String apiUrl = EpcProperty.getValue("EPC_MERGE_PDF_LINK");
        RestTemplate restTemplate = new RestTemplate();
        EpcGenPdfResult epcGenPdfResult = null;
        EpcGenPdfResultDetail epcGenPdfResultDetail = null;
        HttpHeaders headers = new HttpHeaders();
        String xmlString = "";
//        String saPath = "https://epcdev-sales.smartone.com/api/salesDocument/sa/" + orderId + "?signId=" + signId;
        String dnPath = EpcProperty.getValue("EPC_DN_AR_HTML_LINK") + orderId;
        String pdfAuthorName = null;

        try {
            //Construct HKID as PDF author if has HKID
            pdfAuthorName = epcDocumentHandler.genPdfAuthor(orderId);
            
            headers.setContentType(MediaType.APPLICATION_XML);

            xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<cams>" +
                        "<merge_doc>" +
                        "<doc_links>" +
                        "<doc_link>" +
                        "<form_link>" + dnPath + "</form_link>" +
                        "<need_to_convert_pdf>Y</need_to_convert_pdf>" +
                        "<pdf_author_name>" + pdfAuthorName + "</pdf_author_name>" +
                        "</doc_link>" +
                        "</doc_links>" +
                        "</merge_doc>" +
                        "</cams>";
            epcGenPdfResult = restTemplate.postForObject(apiUrl, new HttpEntity<>(xmlString, headers), EpcGenPdfResult.class);
            if(epcGenPdfResult != null) {
                epcGenPdfResultDetail = epcGenPdfResult.getEpcGenPdfResultDetail();
                dnPdfArray = Base64.getDecoder().decode(epcGenPdfResultDetail.getMergeFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dnPdfArray;
    }
    
    public String genDnArHtml(int orderId) {
        String dnHtml = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtProductTotalCharge = null;
        ResultSet rset = null;
        ResultSet rsetProduct = null;
        ResultSet rsetProductCharge = null;
        ResultSet rsetDelivery = null;
        ResultSet rsetNonProductCharge = null;
        String sql = "";
        String orderReference = "";
        String orderDate = ""; // DD/MM/YYYY
        String contactEmail = "";
        String contactNo = "";
        String custName = "";
        String custFirstName = "";
        String custLastName = "";
        String productSectionTemplate = "";
        String tmpProductSection = "";
        String tmpItemId = "";
        String tmpProductDesc = "";
        String tmpProductCode = "";
        String tmpImei = "";
        BigDecimal tmpProductTotalCharge = null;
        BigDecimal tmpDeliveryCharge = null;
        BigDecimal billTotal = null;
        BigDecimal finalCharge = new BigDecimal(0);
        finalCharge.setScale(1);
        StringBuilder productSectionSB = new StringBuilder();
        String tncSectionTemplate = "";
        String recipientName = "";
        String deliveryAddr = "";
        String deliveryAddr1 = "";
        String deliveryAddr2 = "";
        String deliveryAddrDistrict = "";
        String deliveryAddrArea = "";
        String logStr = "[genDnArHtml][orderId:" + orderId + "] ";

        try {
            conn = epcDataSource.getConnection();

            sql = "select nvl(sum(charge_amount),0) " +
                  "  from epc_order_charge " + 
                  " where order_id = ? " + 
                  "   and parent_item_id = ? " +
                  "   and need_to_pay = ? ";
            pstmtProductTotalCharge = conn.prepareStatement(sql);

            // get order info
            sql = "select order_reference, to_char(order_date,'dd/mm/yyyy') as o_date, order_lang, contact_email, contact_no, " +
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
                contactEmail = StringHelper.trim(rset.getString("contact_email"));
                contactNo = StringHelper.trim(rset.getString("contact_no"));
                custFirstName = StringHelper.trim(rset.getString("contact_person_first_name"));
                custLastName = StringHelper.trim(rset.getString("contact_person_last_name"));
            } rset.close();
            pstmt.close();

            if(!"".equals(contactEmail)) {
                try {
                    contactEmail = StringHelper.trim(EpcCrypto.dGet(contactEmail, "utf-8"));
                } catch (Exception e) {
                    contactEmail = "";
                }
            }

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
            sql = "select item_id, cpq_item_desc, cpq_item_desc_chi, cpq_item_value, stock_status, item_code, to_char(invoice_date,'dd/mm/yyyy') as pickup_date " +
                  "  from epc_order_item " +
                  " where order_id = ? " + 
                  "   and item_cat in (?,?,?,?,?,?) " +
                  " order by case_id ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, EpcItemCategory.DEVICE); // itemCat - DEVICE
            pstmt.setString(3, EpcItemCategory.SIM); // itemCat - SIM
            pstmt.setString(4, EpcItemCategory.APPLECARE); // itemCat - APPLECARE
            pstmt.setString(5, EpcItemCategory.SCREEN_REPLACE); // itemCat - SCREEN_REPLACE
            pstmt.setString(6, EpcItemCategory.GIFT_WRAPPING); // itemCat - GIFT_WRAPPING
            pstmt.setString(7, EpcItemCategory.PLASTIC_BAG); // itemCat - PLASTIC_BAG
            rsetProduct = pstmt.executeQuery();
            // end of get products    
            
            // get delivery
            sql = "select deliver_contact_person, deliver_contact_no, deliver_addr_1, deliver_addr_2, deliver_district, deliver_area, " +
                  "       deliver_addr_1_chi, deliver_addr_2_chi, deliver_district_chi, deliver_area_chi, courier_company, courier_form_num, " +
                  "       to_char(to_date(delivery_date,'YYYYMMDD'), 'DD Mon YYYY') as delivery_date " +
                  "  from epc_order_delivery " +
                  " where order_id = ? and delivery_method='COURIER' and status='A' ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rsetDelivery = pstmt.executeQuery();
                       
            // get delivery fee charge
            sql = "select nvl(sum(charge_amount),0) "
            		+ "                  from epc_order_charge a  "
            		+ "                 where order_id = ? "  
            		+ " 				and need_to_pay = ?  "
            		+ "					and charge_code = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, "Y"); // need_to_pay = Y
            pstmt.setString(3, "E96"); // E96 = delivery fee
            rsetNonProductCharge = pstmt.executeQuery();
            // end of get order info


            // get template
            dnHtml = getTemplateWithNullLang(conn, "EPC_DELIVERY_AR");
            productSectionTemplate = getTemplateWithNullLang(conn, "EPC_DELIVERY_AR_PRODUCTS");
            tncSectionTemplate = getTemplateWithNullLang(conn, "EPC_DELIVERY_AR_TNC");
            // end of get template


            // replace data into template
            dnHtml = dnHtml.replaceAll("\\[ORDER_REF\\]", orderReference);
            dnHtml = dnHtml.replaceAll("\\[ORDER_DATE\\]", orderDate);

            custName = ""; // default
            if(!"".equals(custLastName) && !"".equals(custFirstName)) {
                custName = custLastName + " " + custFirstName;
            } else if(!"".equals(custLastName) && "".equals(custFirstName)) {
                custName = custLastName;
            }
            dnHtml = dnHtml.replaceAll("\\[CUST_NAME\\]", custName);

            if("".equals(contactEmail)) {
                contactEmail = "";
            }
            dnHtml = dnHtml.replaceAll("\\[CONTACT_EMAIL\\]", contactEmail);

            if("".equals(contactNo)) {
                contactNo = "";
            }
            dnHtml = dnHtml.replaceAll("\\[CONTACT_NO\\]", contactNo);
            
            // loop thru products
            while(rsetProduct.next()) {
                tmpProductSection = productSectionTemplate; // reset per item

                tmpItemId = StringHelper.trim(rsetProduct.getString("item_id"));
                tmpProductDesc = StringHelper.trim(rsetProduct.getString("cpq_item_desc"));
                tmpImei = StringHelper.trim(rsetProduct.getString("cpq_item_value"));
                tmpProductTotalCharge = new BigDecimal(0); // reset
                tmpProductTotalCharge.setScale(1);
                tmpProductCode = StringHelper.trim(rsetProduct.getString("item_code"));
                
                tmpProductSection = tmpProductSection.replace("[PRODUCT_DESC]", tmpProductDesc);

                if(!"".equals(tmpImei)) {
                    tmpProductSection = tmpProductSection.replaceAll("\\[IMEI\\]", tmpImei + "<br>" + "(" + tmpProductCode + ")");
                } else {
                    tmpProductSection = tmpProductSection.replaceAll("\\[IMEI\\]", tmpProductCode);
                }

                // calc total charge
                pstmtProductTotalCharge.setInt(1, orderId); // order_id
                pstmtProductTotalCharge.setString(2, tmpItemId); // parent_item_id
                pstmtProductTotalCharge.setString(3, "Y"); // need_to_pay
                rsetProductCharge = pstmtProductTotalCharge.executeQuery();
                if(rsetProductCharge.next()) {
                    tmpProductTotalCharge = rsetProductCharge.getBigDecimal(1).setScale(1);
                } rsetProductCharge.close();

                tmpProductSection = tmpProductSection.replaceAll("\\[PRICE\\]", tmpProductTotalCharge + "");

                finalCharge = finalCharge.add(tmpProductTotalCharge);
                // end of calc total charge

                productSectionSB.append(tmpProductSection);
            } rsetProduct.close();

            dnHtml = dnHtml.replace("[PRODUCT_LIST]", productSectionSB.toString());
            dnHtml = dnHtml.replaceAll("\\[TOTAL\\]", finalCharge + "");
            // end of loop thru products
            
            // delivery
            if(rsetDelivery.next()) {
              	recipientName = StringHelper.trim(rsetDelivery.getString("deliver_contact_person"));
              	deliveryAddr1 = StringHelper.trim(rsetDelivery.getString("deliver_addr_1"));
              	deliveryAddr2 = StringHelper.trim(rsetDelivery.getString("deliver_addr_2"));
              	deliveryAddrDistrict = StringHelper.trim(rsetDelivery.getString("deliver_district"));
              	deliveryAddrArea = StringHelper.trim(rsetDelivery.getString("deliver_area"));
            } rsetDelivery.close();
            
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
            deliveryAddr = deliveryAddr1 + " " + deliveryAddr2 + " " + deliveryAddrDistrict + " " + deliveryAddrArea;
            dnHtml = dnHtml.replaceAll("\\[DELIVERY_CONTACT\\]", recipientName + "");
            dnHtml = dnHtml.replaceAll("\\[DELIVERY_ADDRESS\\]", deliveryAddr + "");
            
            tmpDeliveryCharge = new BigDecimal(0); // reset
            tmpDeliveryCharge.setScale(1);
            if(rsetNonProductCharge.next()) {
            	tmpDeliveryCharge = rsetNonProductCharge.getBigDecimal(1).setScale(1);
            } rsetNonProductCharge.close();
            dnHtml = dnHtml.replaceAll("\\[DELIVERY_FEE\\]", tmpDeliveryCharge + "");
            
            billTotal = new BigDecimal(0);
            billTotal.setScale(1);
            billTotal = billTotal.add(finalCharge).add(tmpDeliveryCharge);
            
            dnHtml = dnHtml.replaceAll("\\[BILL_TOTAL\\]", billTotal + "");
            
            dnHtml = dnHtml.replaceAll("\\[GENERATED_DATE\\]", DateHelper.getCurrentDateTime(DateHelper.DT_FMT_AR_GEN_DATE) + "");
            
            // TNC
            dnHtml = dnHtml.replaceAll("\\[TNC\\]", tncSectionTemplate);
            
            // end of replace data into template

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try { if(pstmtProductTotalCharge != null) { pstmtProductTotalCharge.close(); } } catch (Exception ee) {}
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return dnHtml;
    }
    
    public byte[] genDnArStorePdf(int orderId) {
        byte[] dnPdfArray = null;
        String apiUrl = EpcProperty.getValue("EPC_MERGE_PDF_LINK");
        RestTemplate restTemplate = new RestTemplate();
        EpcGenPdfResult epcGenPdfResult = null;
        EpcGenPdfResultDetail epcGenPdfResultDetail = null;
        HttpHeaders headers = new HttpHeaders();
        String xmlString = "";
//        String saPath = "https://epcdev-sales.smartone.com/api/salesDocument/sa/" + orderId + "?signId=" + signId;
        String dnPath = EpcProperty.getValue("EPC_DN_AR_STORE_HTML_LINK") + orderId;
        String pdfAuthorName = null;

        try {
            //Construct HKID as PDF author if has HKID
            pdfAuthorName = epcDocumentHandler.genPdfAuthor(orderId);
            
            headers.setContentType(MediaType.APPLICATION_XML);

            xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                        "<cams>" +
                        "<merge_doc>" +
                        "<doc_links>" +
                        "<doc_link>" +
                        "<form_link>" + dnPath + "</form_link>" +
                        "<need_to_convert_pdf>Y</need_to_convert_pdf>" +
                        "<pdf_author_name>" + pdfAuthorName + "</pdf_author_name>" +
                        "</doc_link>" +
                        "</doc_links>" +
                        "</merge_doc>" +
                        "</cams>";
            epcGenPdfResult = restTemplate.postForObject(apiUrl, new HttpEntity<>(xmlString, headers), EpcGenPdfResult.class);
            if(epcGenPdfResult != null) {
                epcGenPdfResultDetail = epcGenPdfResult.getEpcGenPdfResultDetail();
                dnPdfArray = Base64.getDecoder().decode(epcGenPdfResultDetail.getMergeFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dnPdfArray;
    }
    
    public String genDnArStoreHtml(int orderId) {
        String dnHtml = "";
        Connection conn = null;
        Connection fesConn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtProductTotalCharge = null;
        ResultSet rset = null;
        ResultSet rsetProduct = null;
        ResultSet rsetProductCharge = null;
        ResultSet rsetPayment = null;
        String sql = "";
        String orderReference = "";
        String orderDate = ""; // DD/MM/YYYY
        String contactEmail = "";
        String contactNo = "";
        String productSectionTemplate = "";
        String tmpProductSection = "";
        String tmpItemId = "";
        String tmpProductDesc = "";
        String tmpProductCode = "";
        String tmpImei = "";
        BigDecimal tmpProductTotalCharge = null;
        BigDecimal finalCharge = new BigDecimal(0);
        finalCharge.setScale(1);
        StringBuilder productSectionSB = new StringBuilder();
        String tmpSignSection = "";
        StringBuilder signSectionSB = new StringBuilder();
        String signSectionTemplate = "";
        String tmpSignSrc = "";
        String tmpSignDate = "";
        String paymentSectionTemplate = "";
        String paymentCardDetailSectionTemplate = "";
        String tmpPaymentSection = "";
        String tmpPaymentCDSection = "";
        StringBuilder paymentSectionSB = new StringBuilder();
        String tmpPaymentCode = "";
        String tmpCCNoMasked = "";
        String tmpCCExpMasked = "";
        String tmpApprovalCode = "";
        String tmpCurrency = "";
        String tmpEcrNo = "";
        String tmpMerchantCode = "";
        String tmpTxnType = "";
        String tmpTxnDate = "";
        String tmpTxnTime = "";
        BigDecimal tmpPayAmount = new BigDecimal(0);
        tmpPayAmount.setScale(1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String tncSectionTemplate = "";
        String logStr = "[genDnArStoreHtml][orderId:" + orderId + "] ";

        try {
            conn = epcDataSource.getConnection();
            fesConn = fesDataSource.getConnection();

            sql = "select nvl(sum(charge_amount),0) " +
                  "  from epc_order_charge " + 
                  " where order_id = ? " + 
                  "   and parent_item_id = ? " +
                  "   and need_to_pay = ? ";
            pstmtProductTotalCharge = conn.prepareStatement(sql);

            // get order info
            sql = "select order_reference, to_char(order_date,'dd/mm/yyyy') as o_date, order_lang, contact_email, contact_no, " +
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
                contactEmail = StringHelper.trim(rset.getString("contact_email"));
                contactNo = StringHelper.trim(rset.getString("contact_no"));
            } rset.close();
            pstmt.close();

            if(!"".equals(contactEmail)) {
                try {
                    contactEmail = StringHelper.trim(EpcCrypto.dGet(contactEmail, "utf-8"));
                } catch (Exception e) {
                    contactEmail = "";
                }
            }            

            // get products
            sql = "select item_id, cpq_item_desc, cpq_item_desc_chi, cpq_item_value, stock_status, item_code, to_char(invoice_date,'dd/mm/yyyy') as pickup_date " +
                  "  from epc_order_item " +
                  " where order_id = ? " + 
                  "   and item_cat in (?,?,?,?,?,?) " +
                  " order by case_id ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, EpcItemCategory.DEVICE); // itemCat - DEVICE
            pstmt.setString(3, EpcItemCategory.SIM); // itemCat - SIM
            pstmt.setString(4, EpcItemCategory.APPLECARE); // itemCat - APPLECARE
            pstmt.setString(5, EpcItemCategory.SCREEN_REPLACE); // itemCat - SCREEN_REPLACE
            pstmt.setString(6, EpcItemCategory.GIFT_WRAPPING); // itemCat - GIFT_WRAPPING
            pstmt.setString(7, EpcItemCategory.PLASTIC_BAG); // itemCat - PLASTIC_BAG
            rsetProduct = pstmt.executeQuery();
            // end of get products            

            // get payment
            sql = "select payment_code, sum(payment_amount) as p_amount, cc_no_masked, currency_code, cc_expiry_masked, approval_code, ecr_no " +
                  "  from epc_order_payment " +
                  " where order_id = ? " +
                  " group by payment_code, cc_no_masked, currency_code, cc_expiry_masked, approval_code, ecr_no ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rsetPayment = pstmt.executeQuery();
            // end of get payment

            ArrayList<EpcSignature> signList = epcSignatureHandler.getSignature(orderId);
            
            // end of get order info


            // get template
            dnHtml = getTemplateWithNullLang(conn, "EPC_DNAR_STORE");
            productSectionTemplate = getTemplateWithNullLang(conn, "EPC_DNAR_STORE_PRODUCTS");
            paymentSectionTemplate = getTemplateWithNullLang(conn, "EPC_DNAR_STORE_PAYMENTS");
            paymentCardDetailSectionTemplate = getTemplateWithNullLang(conn, "EPC_DNAR_STORE_PAYMENTS_CD");
            signSectionTemplate = getTemplateWithNullLang(conn, "EPC_DNAR_STORE_SIGN");
            tncSectionTemplate = getTemplateWithNullLang(conn, "EPC_DNAR_STORE_TNC");
            // end of get template


            // replace data into template
            dnHtml = dnHtml.replaceAll("\\[ORDER_REF\\]", orderReference);
            dnHtml = dnHtml.replaceAll("\\[ORDER_DATE\\]", orderDate);

            if("".equals(contactEmail)) {
                contactEmail = "";
            }
            dnHtml = dnHtml.replaceAll("\\[CONTACT_EMAIL\\]", contactEmail);

            if("".equals(contactNo)) {
                contactNo = "";
            }
            dnHtml = dnHtml.replaceAll("\\[CONTACT_NO\\]", contactNo);
            
            // loop thru products
            while(rsetProduct.next()) {
                tmpProductSection = productSectionTemplate; // reset per item

                tmpItemId = StringHelper.trim(rsetProduct.getString("item_id"));
                tmpProductDesc = StringHelper.trim(rsetProduct.getString("cpq_item_desc"));
                tmpImei = StringHelper.trim(rsetProduct.getString("cpq_item_value"));
                tmpProductTotalCharge = new BigDecimal(0); // reset
                tmpProductTotalCharge.setScale(1);
                tmpProductCode = StringHelper.trim(rsetProduct.getString("item_code"));
                
                tmpProductSection = tmpProductSection.replace("[PRODUCT_DESC]", tmpProductDesc);

                if(!"".equals(tmpImei)) {
                    tmpProductSection = tmpProductSection.replaceAll("\\[IMEI\\]", tmpImei + "<br>" + "(" + tmpProductCode + ")");
                } else {
                    tmpProductSection = tmpProductSection.replaceAll("\\[IMEI\\]", tmpProductCode);
                }

                // calc total charge
                pstmtProductTotalCharge.setInt(1, orderId); // order_id
                pstmtProductTotalCharge.setString(2, tmpItemId); // parent_item_id
                pstmtProductTotalCharge.setString(3, "Y"); // need_to_pay
                rsetProductCharge = pstmtProductTotalCharge.executeQuery();
                if(rsetProductCharge.next()) {
                    tmpProductTotalCharge = rsetProductCharge.getBigDecimal(1).setScale(1);
                } rsetProductCharge.close();

                tmpProductSection = tmpProductSection.replaceAll("\\[PRICE\\]", tmpProductTotalCharge + "");

                finalCharge = finalCharge.add(tmpProductTotalCharge);
                // end of calc total charge                

                productSectionSB.append(tmpProductSection);
            } rsetProduct.close();

            dnHtml = dnHtml.replace("[PRODUCT_LIST]", productSectionSB.toString());
            dnHtml = dnHtml.replaceAll("\\[TOTAL\\]", finalCharge + "");
            // end of loop thru products
            

            // loop thru payment
            while(rsetPayment.next()) {
                tmpPaymentSection = paymentSectionTemplate; // reset per item
                tmpPaymentCode = StringHelper.trim(rsetPayment.getString("payment_code"));
                tmpCCNoMasked = StringHelper.trim(rsetPayment.getString("cc_no_masked"));
                tmpCCExpMasked = StringHelper.trim(rsetPayment.getString("cc_expiry_masked"));
                tmpCurrency = StringHelper.trim(rsetPayment.getString("currency_code"));                
                tmpPayAmount = rsetPayment.getBigDecimal("p_amount").setScale(1);
                tmpPaymentSection = tmpPaymentSection.replaceAll("\\[PAYMENT_CODE\\]", tmpPaymentCode + "");
                tmpPaymentSection = tmpPaymentSection.replaceAll("\\[CURRENCY\\]", tmpCurrency + "");
                tmpPaymentSection = tmpPaymentSection.replaceAll("\\[PAY_AMOUNT\\]", tmpPayAmount + "");
                tmpEcrNo = StringHelper.trim(rsetPayment.getString("ecr_no"));                
                
                // replace card detail
                tmpPaymentCDSection = ""; // reset per item
                if (!"".equals(tmpCCNoMasked)) {
                	tmpPaymentCDSection = paymentCardDetailSectionTemplate; // reset per item
                	// get card detail
                    sql = "select to_char(transaction_date,'dd-mm-yyyy') as t_date, to_char(transaction_date,'hh24:mi') as t_time, merchant_id, service_type, authorize_id " +
                          "   from pct_fes_pg_request_v where ecr_no = ? ";
                    pstmt = fesConn.prepareStatement(sql);
                    pstmt.setString(1, tmpEcrNo);
                    rset = pstmt.executeQuery();
                    
                    if(rset.next()) {
                    	tmpMerchantCode = StringHelper.trim(rset.getString("merchant_id"));
                    	tmpTxnType = StringHelper.trim(rset.getString("service_type"));
                    	tmpTxnDate = StringHelper.trim(rset.getString("t_date"));
                    	tmpTxnTime = StringHelper.trim(rset.getString("t_time"));
                    	tmpApprovalCode = StringHelper.trim(rset.getString("authorize_id"));
                    }
                    tmpPaymentCDSection = tmpPaymentCDSection.replace("[MERCHANT_CODE]", tmpMerchantCode);
                	tmpPaymentCDSection = tmpPaymentCDSection.replace("[TXN_TYPE]", tmpTxnType);
                	tmpPaymentCDSection = tmpPaymentCDSection.replace("[TXN_DATE]", tmpTxnDate);
                	tmpPaymentCDSection = tmpPaymentCDSection.replace("[TXN_TIME]", tmpTxnTime);
                	tmpPaymentCDSection = tmpPaymentCDSection.replace("[MASKED_CC_NO]", tmpCCNoMasked);
                	tmpPaymentCDSection = tmpPaymentCDSection.replace("[EXP_DATE]", tmpCCExpMasked);
                	tmpPaymentCDSection = tmpPaymentCDSection.replace("[APPROVAL_CODE]", tmpApprovalCode);
                }
                tmpPaymentSection = tmpPaymentSection.replace("[CARD_DETAIL]", tmpPaymentCDSection);
                
                paymentSectionSB.append(tmpPaymentSection);
            } rsetPayment.close();

            dnHtml = dnHtml.replaceAll("\\[PAYMENT_LIST\\]", paymentSectionSB.toString());
            // end of loop thru payment

            // replace customer signature
            for (EpcSignature signature : signList) {
            	if ("Y".equals(signature.getWithDn())){
            		tmpSignSection = signSectionTemplate; // reset per item
                    tmpSignSrc = signature.getContent();
                    tmpSignDate = signature.getCreateDate();
                    Date signDate = sdf.parse(tmpSignDate);
                    tmpSignDate = DateHelper.formatDateTime(signDate, DateHelper.DT_FMT_AR_GEN_DATE);
                    tmpSignSection = tmpSignSection.replaceAll("\\[SIGN_SRC\\]", tmpSignSrc + "");
                    tmpSignSection = tmpSignSection.replaceAll("\\[DATE\\]", tmpSignDate + "");
                    signSectionSB.append(tmpSignSection);
            	}
            }
            dnHtml = dnHtml.replaceAll("\\[SIGN_LIST\\]", signSectionSB.toString());
            // end of replace customer signature           
            
            // replace TNC
            dnHtml = dnHtml.replace("[TNC]", tncSectionTemplate);
            
            // end of replace data into template

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ee) {}
            try { if(pstmtProductTotalCharge != null) { pstmtProductTotalCharge.close(); } } catch (Exception ee) {}
        }
        return dnHtml;
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
}
