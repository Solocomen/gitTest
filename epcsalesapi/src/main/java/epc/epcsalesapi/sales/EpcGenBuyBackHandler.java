package epc.epcsalesapi.sales;

import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.*;
import epc.epcsalesapi.sales.bean.genSa.EpcGenPdfResult;
import epc.epcsalesapi.sales.bean.genSa.EpcGenPdfResultDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

@Service
public class EpcGenBuyBackHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcGenBuyBackHandler.class);

    private final DataSource epcDataSource;
    private final DataSource fesDataSource;
    private final DataSource crmFesDataSource;
    private final EpcSecurityHelper epcSecurityHelper;

    public EpcGenBuyBackHandler(DataSource epcDataSource, DataSource fesDataSource, DataSource crmFesDataSource, EpcSecurityHelper epcSecurityHelper) {
        this.epcDataSource = epcDataSource;
        this.fesDataSource = fesDataSource;
        this.crmFesDataSource = crmFesDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
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

            //Get back info
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
