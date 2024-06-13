package epc.epcsalesapi.sales;

import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Connection;
import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.Base64;
import java.util.HashMap;

import epc.epcsalesapi.sales.bean.*;
import epc.epcsalesapi.sales.bean.genSa.EpcGenPdfResult;
import epc.epcsalesapi.sales.bean.genSa.EpcGenPdfResultDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

@Service
public class EpcBuyBackHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcBuyBackHandler.class);

    private final DataSource epcDataSource;
    private final DataSource fesDataSource;
    private final DataSource crmFesDataSource;
    private final EpcSecurityHelper epcSecurityHelper;

    public EpcBuyBackHandler(DataSource epcDataSource, DataSource fesDataSource, DataSource crmFesDataSource, EpcSecurityHelper epcSecurityHelper) {
        this.epcDataSource = epcDataSource;
        this.fesDataSource = fesDataSource;
        this.crmFesDataSource = crmFesDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
    }

    public EpcBuyBackSaveInfoResult saveBuyBackInfo(EpcBuyBackInfoBean epcBuyBackInfoBean) {
        int index = 1;
        String sql = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        EpcBuyBackSaveInfoResult epcBuyBackSaveInfoResult = new EpcBuyBackSaveInfoResult();

        String imei =  StringHelper.trim(epcBuyBackInfoBean.getImei());
        String caseId =  StringHelper.trim(epcBuyBackInfoBean.getCaseId());
        String orderId =  StringHelper.trim(epcBuyBackInfoBean.getOrderId());
        String invoiceNo =  StringHelper.trim(epcBuyBackInfoBean.getInvoiceNo());
        String creditCardNo1 =  StringHelper.trim(epcBuyBackInfoBean.getCard1());
        String creditCardNo2 =  StringHelper.trim(epcBuyBackInfoBean.getCard2());
        String creditCardNo3 =  StringHelper.trim(epcBuyBackInfoBean.getCard3());
        String creditCardHolderName =  StringHelper.trim(epcBuyBackInfoBean.getCreditCardHolderName());
        String creditCardApprovalCode =  StringHelper.trim(epcBuyBackInfoBean.getCreditApprovalCode());

        try {
            sql =  " update epc_order_buyback " +
                   " set invoice_no = ?,  serial_number = ?, receipt_no = ?, card_holder_name = ?, masked_card_no = ?, tx_approval_code = ? " +
                   " where order_id = ? and case_id = ? ";

            conn = epcDataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(index++, invoiceNo);
            pstmt.setString(index++, imei);
            pstmt.setString(index++, getEpcBuyBackReceiptNo(orderId, imei));
            pstmt.setString(index++, creditCardHolderName);
            pstmt.setString(index++, (creditCardNo1 + creditCardNo2 + creditCardNo3));
            pstmt.setString(index++, creditCardApprovalCode);
            pstmt.setString(index++, orderId);
            pstmt.setString(index++, caseId);
            pstmt.executeUpdate();

            epcBuyBackSaveInfoResult.setErrMsg("");
            epcBuyBackSaveInfoResult.setSaveResult(EpcApiStatusReturn.RETURN_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            epcBuyBackSaveInfoResult.setErrMsg(e.getMessage());
            epcBuyBackSaveInfoResult.setSaveResult(EpcApiStatusReturn.RETURN_FAIL);
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception e) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return epcBuyBackSaveInfoResult;
    }

    public EpcBuyBackUpdateReceiptNoResult updateBuyBackReceiptNo(String receiptNo, String orderId, String caseId) {
        int index = 1;
        String sql = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        EpcBuyBackUpdateReceiptNoResult epcBuyBackUpdateReceiptNoResult = new EpcBuyBackUpdateReceiptNoResult();

        try {
            sql =  " update epc_order_buyback " +
                    " set receipt_no = ? " +
                    " where order_id = ? and case_id = ? ";

            conn = epcDataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(index++, StringHelper.trim(receiptNo));
            pstmt.setString(index++, StringHelper.trim(orderId));
            pstmt.setString(index++, StringHelper.trim(caseId));
            pstmt.executeUpdate();

            epcBuyBackUpdateReceiptNoResult.setErrMsg("");
            epcBuyBackUpdateReceiptNoResult.setUpdateResult(EpcApiStatusReturn.RETURN_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            epcBuyBackUpdateReceiptNoResult.setErrMsg(e.getMessage());
            epcBuyBackUpdateReceiptNoResult.setUpdateResult(EpcApiStatusReturn.RETURN_FAIL);
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception e) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return epcBuyBackUpdateReceiptNoResult;
    }

    public EpcBuyBackUpdateImeiResult updateBuyBackImei(String imei, String orderId, String caseId) {
        int index = 1;
        String sql = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        EpcBuyBackUpdateImeiResult epcBuyBackUpdateImeiResult = new EpcBuyBackUpdateImeiResult();

        try {
            sql =  " update epc_order_buyback " +
                   " set serial_number = ? " +
                   " where order_id = ? and case_id = ? ";

            conn = epcDataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(index++, StringHelper.trim(imei));
            pstmt.setString(index++, StringHelper.trim(orderId));
            pstmt.setString(index++, StringHelper.trim(caseId));
            pstmt.executeUpdate();

            epcBuyBackUpdateImeiResult.setErrMsg("");
            epcBuyBackUpdateImeiResult.setUpdateResult(EpcApiStatusReturn.RETURN_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            epcBuyBackUpdateImeiResult.setErrMsg(e.getMessage());
            epcBuyBackUpdateImeiResult.setUpdateResult(EpcApiStatusReturn.RETURN_FAIL);
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception e) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return epcBuyBackUpdateImeiResult;
    }

    public boolean isEpcBuyBackCase(String orderId, String caseId) {
        int index = 1;
        String sql = "";
        Connection conn = null;
        ResultSet resultSet = null;
        PreparedStatement pstmt = null;
        boolean isEpcBuyBackCase = false;

        try {
            sql = "SELECT 1 " +
                  "  FROM epc_order_buyback b " +
                  " WHERE EXISTS " +
                  "          (SELECT 1 " +
                  "             FROM epc_order_payment p " +
                  "            WHERE     p.order_id = b.order_id " +
                  "                  AND p.case_id = b.case_id " +
                  "                  AND b.order_id = ? " +
                  "                  AND b.case_id = ?)";

            conn = epcDataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(index++, StringHelper.trim(orderId));
            pstmt.setString(index++, StringHelper.trim(caseId));
            resultSet = pstmt.executeQuery();

            if(resultSet.next()){
                isEpcBuyBackCase = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception e) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return isEpcBuyBackCase;
    }

    public String getEpcBuyBackReceiptNo(String orderId, String imei){
        int index = 1;
        String sql = "";
        String receiptNo = "";
        Connection conn = null;
        ResultSet resultSet = null;
        PreparedStatement pstmt = null;

        try {
            sql = "SELECT a.receipt_no " +
                    "  FROM epc_order_receipt a, " +
                    "       epc_order_payment b, " +
                    "       epc_control_tbl c, " +
                    "       epc_order_charge d, " +
                    "       epc_order_item e," +
                    "       epc_order_buyback f " +
                    " WHERE     a.order_id = ?  " +
                    "       AND a.order_id = b.order_id " +
                    "       AND a.tx_no = b.tx_no " +
                    "       AND b.payment_code = c.key_str1 " +
                    "       AND c.rec_type = 'INSTALL_PAYMENT' " +
                    "       AND b.order_id = d.order_id " +
                    "       AND b.tx_no = d.tx_no " +
                    "       AND d.order_id = e.order_id " +
                    "       AND d.case_id = e.case_id " +
                    "       AND d.parent_item_id = e.item_id " +
                    "       AND e.item_cat = 'DEVICE' " +
                    "       AND e.warehouse = 'AH' " +
                    "       AND e.cpq_item_value = ? " +
                    "       AND e.order_id = f.order_id " +
                    "       AND e.case_id = f.case_id ";

            conn = epcDataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(index++, StringHelper.trim(orderId));
            pstmt.setString(index++, StringHelper.trim(imei));
            resultSet = pstmt.executeQuery();

            if(resultSet.next()){
                receiptNo = StringHelper.trim(resultSet.getString("receipt_no"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception e) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return receiptNo;
    }

    public byte[] genBuyBackFormPdf(int orderId, String caseId, String signId) {
        byte[] saPdfArray = null;
        String apiUrl = EpcProperty.getValue("EPC_MERGE_PDF_LINK");
        RestTemplate restTemplate = new RestTemplate();
        EpcGenPdfResult epcGenPdfResult = null;
        EpcGenPdfResultDetail epcGenPdfResultDetail = null;
        HttpHeaders headers = new HttpHeaders();
        String xmlString = "";
        String saPath = EpcProperty.getValue("EPC_SA_HTML_LINK") + orderId + "?signId=" + signId;

        try {
            headers.setContentType(MediaType.APPLICATION_XML);

            xmlString = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                    "<cams>" +
                    "<merge_doc>" +
                    "<doc_links>" +
                    "<doc_link>" +
                    "<form_link>" + saPath + "</form_link>" +
                    "<need_to_convert_pdf>Y</need_to_convert_pdf>" +
                    "</doc_link>" +
                    "</doc_links>" +
                    "</merge_doc>" +
                    "</cams>";
            epcGenPdfResult = restTemplate.postForObject(apiUrl, new HttpEntity<>(xmlString, headers), EpcGenPdfResult.class);
            if(epcGenPdfResult != null) {
                epcGenPdfResultDetail = epcGenPdfResult.getEpcGenPdfResultDetail();
                saPdfArray = Base64.getDecoder().decode(epcGenPdfResultDetail.getMergeFile());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return saPdfArray;
    }

    public String genBuyBackFormHtml(int orderId, String caseId, String signId) {
        //Input params
        System.out.println("orderId:" + orderId);
        System.out.println("caseId:" + caseId);
        System.out.println("signId:" + signId);

        //Html templates
        String buyBackFormHdr = "";
        String buyBackFormCcNProdInfo = "";
        String buyBackFormTermsNConditions1 = "";
        String buyBackFormTermsNConditions2 = "";
        String buyBackFormProdAcceptCriteria1 = "";
        String buyBackFormProdAcceptCriteria2 = "";
        String buyBackFormReturnDateTermStatic = "";
        String buyBackFormReturnDateTermNonStatic = "";
        String buyBackFormCampaignTermWithBank = "";
        String buyBackFormCampaignTerm = "";
        String buyBackFormUrlTermWithBackDesc = "";
        String buyBackFormUrlTerm = "";
        String buyBackFormSignPart = "";
        String buyBackFormFootPage = "";
        String bankCampaignDesc = "";
        String bankNameDesc = "";
        String bankCampaignUrl = "";

        String lang = "";
        StringBuilder buyBackFormHtml = new StringBuilder(2 * 1024);

        Connection conn = null;
        Connection fesConn = null;
        Connection crmConn = null;

        try {
            conn = epcDataSource.getConnection();
            fesConn = fesDataSource.getConnection();
            crmConn = crmFesDataSource.getConnection();

            // get template
            buyBackFormHdr = getTemplate(conn, "BUY_BACK_FORM_HDR", lang);
            buyBackFormCcNProdInfo = getTemplate(conn, "BUY_BACK_FORM_CC_N_PROD_INFO", lang);
            buyBackFormTermsNConditions1 = getTemplate(conn, "BUY_BACK_FORM_TERMS_N_CONDITIONS_1", lang);
            buyBackFormTermsNConditions2 = getTemplate(conn, "BUY_BACK_FORM_TERMS_N_CONDITIONS_2", lang);
            buyBackFormProdAcceptCriteria1 = getTemplate(conn, "BUY_BACK_FORM_PROD_ACCEPT_CRITERIA_1", lang);
            buyBackFormProdAcceptCriteria2 = getTemplate(conn, "BUY_BACK_FORM_PROD_ACCEPT_CRITERIA_2", lang);
            buyBackFormReturnDateTermStatic = getTemplate(conn, "BUY_BACK_FORM_RETURN_DATE_TERM_STATIC", lang);
            buyBackFormReturnDateTermNonStatic = getTemplate(conn, "BUY_BACK_FORM_RETURN_DATE_TERM_NON_STATIC", lang);
            buyBackFormCampaignTermWithBank = getTemplate(conn, "BUY_BACK_FORM_CAMPAIGN_TERM_WITH_BANK", lang);
            buyBackFormCampaignTerm = getTemplate(conn, "BUY_BACK_FORM_CAMPAIGN_TERM", lang);
            buyBackFormUrlTermWithBackDesc = getTemplate(conn, "BUY_BACK_FORM_URL_TERM_WITH_BANK_DESC", lang);
            buyBackFormUrlTerm = getTemplate(conn, "BUY_BACK_FORM_URL_TERM", lang);
            buyBackFormSignPart = getTemplate(conn, "BUY_BACK_FORM_SIGN_PART", lang);
            buyBackFormFootPage = getTemplate(conn, "BUY_BACK_FORM_FOOT_PAGE", lang);

            //Get language
            lang = getLanguage(conn, orderId);

            //Get language
            bankCampaignDesc = getBankCampaignDesc(crmConn, orderId);
            bankNameDesc = getBankNameDesc(crmConn, orderId);
            bankCampaignUrl = getBankCampaignUrl(fesConn, orderId, "" , "");

            //Append html
            buyBackFormHtml.append(buyBackFormHdr);
            buyBackFormHtml.append(buyBackFormCcNProdInfo);
            buyBackFormHtml.append(buyBackFormTermsNConditions1);
            buyBackFormHtml.append(buyBackFormTermsNConditions2);
            buyBackFormHtml.append(buyBackFormProdAcceptCriteria1);
            buyBackFormHtml.append(buyBackFormProdAcceptCriteria2);
            buyBackFormHtml.append(buyBackFormReturnDateTermStatic);
            buyBackFormHtml.append(buyBackFormReturnDateTermNonStatic);
            buyBackFormHtml.append(buyBackFormCampaignTermWithBank);
            buyBackFormHtml.append(buyBackFormCampaignTerm);
            buyBackFormHtml.append(buyBackFormUrlTermWithBackDesc);
            buyBackFormHtml.append(buyBackFormUrlTerm);
            buyBackFormHtml.append(buyBackFormSignPart);
            buyBackFormHtml.append(buyBackFormFootPage);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ee) {}
            try { if(crmConn != null) { crmConn.close(); } } catch (Exception ee) {}
        }
        return buyBackFormHtml.toString();
    }

    //Get template
    public String getTemplate(Connection conn,  String msgType, String orderLang) {
        String sql = "";
        String content = "";

        ResultSet rset = null;
        PreparedStatement pstmt = null;

        try {
            sql = "select msg_content  " +
                  "  from gp_msg_template " +
                  " where msg_type = ? " +
                  "   and lang = ? " +
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, msgType); // msg_type
            pstmt.setString(2, orderLang); // lang
            pstmt.setString(3, "A"); // status

            rset = pstmt.executeQuery();
            if(rset.next()) {
                content = StringHelper.trim(rset.getString("msg_content"));
            }

            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }

        return content;
    }

    //Get the language
    public String getLanguage(Connection conn, int orderId){
        String lang = "";

        String sqlGetLang = "";
        ResultSet resultSetGetLang = null;
        PreparedStatement pstmtGetLang = null;

        try {
            sqlGetLang = "select order_lang from epc_order where order_id = ? ";
            pstmtGetLang = conn.prepareStatement(sqlGetLang);
            pstmtGetLang.setInt(1, orderId);
            resultSetGetLang = pstmtGetLang.executeQuery();
            if(resultSetGetLang.next()) {
                lang = resultSetGetLang.getString("order_lang");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(resultSetGetLang != null) { resultSetGetLang.close(); } } catch (Exception ee) {}
            try { if(pstmtGetLang != null) { pstmtGetLang.close(); } } catch (Exception ee) {}
        }

        System.out.println("lang:" + lang);
        return lang;
    }

    //Get bankCampaignDesc
    public String getBankCampaignDesc(Connection conn, int orderId){
        String bankCampaignDesc = "";

        String sql = "";
        ResultSet resultSet= null;
        PreparedStatement pstmt = null;

        try {
            sql = "SELECT rec_content_3  " +
                  "  FROM fes_param_ctrl " +
                  "WHERE rec_type_1 = 'BUYBACK_MAINTENACE' " +
                  "   AND rec_type_2 = 'CAMPAIGN_DESC' " +
                  "   AND rec_type_3 = 'HSBC' " +
                  "   AND rec_content_1 = 'C' " +
                  "   AND rec_content_2 = 'iPhone' " +
                  "   AND status = 'A' ";
            pstmt= conn.prepareStatement(sql);
            resultSet = pstmt.executeQuery();

            if(resultSet.next()) {
                bankCampaignDesc = resultSet.getString("rec_content_3");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(resultSet != null) { resultSet.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }

        System.out.println("bankCampaignDesc: " + bankCampaignDesc);
        return bankCampaignDesc;
    }

    //Get backNameDesc
    public String getBankNameDesc(Connection conn, int orderId){
        String backNameDesc = "";

        String sql = "";
        ResultSet resultSet= null;
        PreparedStatement pstmt = null;

        try {
            sql = "SELECT rec_content_3 " +
                  "  FROM fes_param_ctrl " +
                  "WHERE rec_type_1 = 'BUYBACK_MAINTENACE' " +
                  "   AND rec_type_2 = 'CAMPAIGN_DESC' " +
                  "   AND rec_type_3 = 'HSBC' " +
                  "   AND rec_content_1 = 'C' " +
                  "   AND rec_content_2 = 'iPhone' " +
                  "   AND status = 'A' ";
            pstmt= conn.prepareStatement(sql);
            resultSet = pstmt.executeQuery();
            if(resultSet.next()) {
                backNameDesc = resultSet.getString("rec_content_3");
                System.out.println("backNameDesc: " + backNameDesc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(resultSet != null) { resultSet.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }

        System.out.println("backNameDesc: " + backNameDesc);
        return backNameDesc;
    }

    //Get bankCampaignUrl
    public String getBankCampaignUrl(Connection conn, int orderId, String product, String warehouse){
        String bankCampaignUrl = "";

        String sql = "";
        ResultSet resultSet= null;
        PreparedStatement pstmt = null;

        try {
            sql = "SELECT CASE WHEN handset_brand = 'APPLE' AND s.smc_os = 'Iphone' THEN 'iPhone' " +
                    "       WHEN handset_brand = 'APPLE' AND s.smc_os = 'Tablet' THEN 'iPad' " +
                    "       WHEN s.smc_os IS NULL AND INSTR(LOWER(accessory_nature) , 'smart wearable') > 0 THEN 'AppleWatch' " +
                    "       WHEN s.smc_os IS NULL AND INSTR(LOWER(accessory_nature) , 'headset and earpiece') > 0 THEN 'AirPod' " +
                    "       ELSE 'N/A' END product_type " +
                    "  FROM STOCKM s WHERE product = ? AND warehouse = ? ";
            pstmt= conn.prepareStatement(sql);
            pstmt.setString(1, product);
            pstmt.setString(2, warehouse);
            resultSet = pstmt.executeQuery();

            if(resultSet.next()) {
                bankCampaignUrl = resultSet.getString("product_type");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(resultSet != null) { resultSet.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }

        System.out.println("backCampaignUrl: " + bankCampaignUrl);
        return bankCampaignUrl;
    }
}
