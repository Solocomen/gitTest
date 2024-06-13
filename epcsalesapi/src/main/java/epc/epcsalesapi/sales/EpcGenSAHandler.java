package epc.epcsalesapi.sales;

//import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import java.util.HashMap;
import javax.sql.DataSource;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
//import org.xhtmlrenderer.layout.SharedContext;
//import org.xhtmlrenderer.pdf.ITextRenderer;
//import com.fasterxml.jackson.databind.ObjectMapper;
import epc.epcsalesapi.helper.EpcCharsetHelper;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.genSa.EpcGenPdfResult;
import epc.epcsalesapi.sales.bean.genSa.EpcGenPdfResultDetail;

@Service
public class EpcGenSAHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcGenSAHandler.class);

    private final DataSource epcDataSource;
    private final DataSource fesDataSource;
    private final EpcSecurityHelper epcSecurityHelper;

    public EpcGenSAHandler(DataSource epcDataSource, DataSource fesDataSource, EpcSecurityHelper epcSecurityHelper) {
        this.epcDataSource = epcDataSource;
        this.fesDataSource = fesDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
    }


//    public byte[] genSaPdfTest(int orderId, String signId) {
//        byte[] saPdfArray = null;
//        Document jsoupDoc = null;
//        ITextRenderer renderer = new ITextRenderer();
//        SharedContext sharedContext = null;
//        String saString = genSaHtml(orderId, signId);
//        ByteArrayOutputStream baos = null;
//        String logStr = "[genSaPdf][orderId:" + orderId + "] ";
//
//        try {
//logger.info("{}{}", logStr, "start");
//            jsoupDoc = Jsoup.parse(saString);
//            jsoupDoc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
//logger.info("{}{}", logStr, "converted to xhtml");
//
//            baos = new ByteArrayOutputStream();
//
//            sharedContext = renderer.getSharedContext();
//            sharedContext.setPrint(true);
//            sharedContext.setInteractive(false);
//            renderer.setDocumentFromString(jsoupDoc.html());
//            renderer.layout();
//            renderer.createPDF(baos);
//logger.info("{}{}", logStr, "converted to pdf");
//
//            saPdfArray = baos.toByteArray();
//            baos.close();
//        } catch(Exception e) {
//            e.printStackTrace();
//        }
//        return saPdfArray;
//    }


    public byte[] genSaPdf(int orderId, String signId) {
        byte[] saPdfArray = null;
//        String apiUrl = "https://fesformuat/CamsMergeDocServlet";
        String apiUrl = EpcProperty.getValue("EPC_MERGE_PDF_LINK");
        RestTemplate restTemplate = new RestTemplate();
        EpcGenPdfResult epcGenPdfResult = null;
        EpcGenPdfResultDetail epcGenPdfResultDetail = null;
        HttpHeaders headers = new HttpHeaders();
        String xmlString = "";
//        String saPath = "https://epcdev-sales.smartone.com/api/salesDocument/sa/" + orderId + "?signId=" + signId;
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


    public String genSaHtml(int orderId, String signId) {
        String saHtml = "";
        Connection conn = null;
        Connection fesConn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtProductTotalCharge = null;
        PreparedStatement pstmtProductDiscountCharge = null;
        PreparedStatement pstmtProductOnSpotDiscountCharge = null;
        PreparedStatement pstmtProductRrp = null;
        PreparedStatement pstmtPayment = null;
        PreparedStatement pstmtTnc = null;
        PreparedStatement pstmtContractCustSubrNum = null;
        ResultSet rset = null;
        ResultSet rsetProduct = null;
        ResultSet rsetProductCharge = null;
        ResultSet rsetContract = null;
        ResultSet rsetPayment = null;
        ResultSet rsetTnc = null;
        ResultSet rsetContractCustSubrNum = null;
        ResultSet rsetNonProductCharge = null;
        String sql = "";
        String orderReference = "";
        String orderDate = ""; // DD/MM/YYYY
        String orderLang = "";
        String orderLangDisplay = "";
        String contactEmail = "";
        String contactNo = "";
        String custName = "";
        String custFirstName = "";
        String custLastName = "";
        String custNum = "";
        String subrNum = "";
        String productSectionTemplate = "";
        String tmpProductSection = "";
        String tmpItemId = "";
        String tmpProductDesc = "";
        String tmpProductDescChi = "";
        String tmpImei = "";
        String tmpChargeCode = "";
        BigDecimal tmpProductTotalCharge = null;
        BigDecimal tmpProductRrp = null;
        BigDecimal tmpProductDiscountCharge = null;
        BigDecimal finalCharge = new BigDecimal(0);
        StringBuilder productSectionSB = new StringBuilder();
        String contractSectionTemplate = "";
        String tmpContractSection = "";
        StringBuilder contractSectionSB = new StringBuilder();
        String paymentSectionTemplate = "";
        String tmpPaymentSection = "";
        StringBuilder paymentSectionSB = new StringBuilder();
        String tmpPaymentCode = "";
        String tmpCurrencyCode = "";
        BigDecimal tmpPaymentAmount = null;
        String appleCareSectionTemplate = "";
        String tmpCaseId = "";
        String tmpParentItemId = "";
        BigDecimal tmpPenaltyAmount = null;
        int tmpContractDuration = 0;
        String tmpContractDurationStr = "";
        String tmpContractStartDate = "";
        String tmpContractEndDate = "";
        String tmpContractCustNum = "";
        String tmpContractSubrNum = "";
        String tmpTncString = "";
        String tmpTncUrl = "";
        HashMap<String, String> contractMonthDisplayMap = null;
        String placeOrderUser = "";
        String placeOrderSalesman = "";
        String placeOrderLocation = "";
        String placeOrderDate = "";
        HashMap<String, String> storeMap = null;
        String salesmanName = "";
        String iSignId = epcSecurityHelper.encodeForSQL(signId);
        String custSignatureImg = "";
        HashMap<String, String> imgMap = null;
        String hasAppleCareItem = "N";
        HashMap<String, String> nonProductChargeDescMap = null;
        boolean isAddedDummyRow = false;
        String logStr = "[genSaHtml][orderId:" + orderId + "] ";

        try {
            conn = epcDataSource.getConnection();
            fesConn = fesDataSource.getConnection();

            sql = "select nvl(sum(charge_amount),0) " +
                  "  from epc_order_charge " + 
                  " where order_id = ? " + 
                  "   and parent_item_id = ? " +
                  "   and need_to_pay = ? ";
            pstmtProductTotalCharge = conn.prepareStatement(sql);

            sql = "select -1 * nvl(sum(charge_amount),0) " +
                  "  from epc_order_charge " + 
                  " where order_id = ? " + 
                  "   and parent_item_id = ? " +
                  "   and need_to_pay = ? " +
                  "   and charge_code = ? "; // charge_code = DISCOUNT
            pstmtProductDiscountCharge = conn.prepareStatement(sql);

            sql = "select nvl(sum(discount_amount),0) " +
                  "  from epc_order_charge " + 
                  " where order_id = ? " + 
                  "   and parent_item_id = ? " +
                  "   and need_to_pay = ? " +
                  "   and discount_amount is not null " +
                  "   and charge_code != ? "; // charge_code = DISCOUNT
            pstmtProductOnSpotDiscountCharge = conn.prepareStatement(sql);

            sql = "select nvl(sum(origin_charge_amount),0) " +
                  "  from epc_order_charge " + 
                  " where order_id = ? " + 
                  "   and parent_item_id = ? " +
                  "   and need_to_pay = ? " +
                  "   and charge_code != ? "; // charge_code = DISCOUNT
            pstmtProductRrp = conn.prepareStatement(sql);

            sql = "select * " +
                  "  from epc_order_tnc a " +
                  " where order_id = ? " +
                  "   and case_id = ? " +
                  "   and parent_item_id = ? ";
            pstmtTnc = conn.prepareStatement(sql);

            sql = "select cust_num, subr_num " +
                  "  from epc_order_case " +
                  " where order_id = ? " +
                  "   and case_id = ? ";
            pstmtContractCustSubrNum = conn.prepareStatement(sql);

            contractMonthDisplayMap = getContractMonthDisplay(conn);

            hasAppleCareItem = hasAppleCareItem(conn, orderId);

            imgMap = getImgDisplay(conn);

            nonProductChargeDescMap = getNonProductChargeDesc(conn);


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
                orderLang = StringHelper.trim(rset.getString("order_lang"));
                contactEmail = StringHelper.trim(rset.getString("contact_email"));
                contactNo = StringHelper.trim(rset.getString("contact_no"));
                custFirstName = StringHelper.trim(rset.getString("contact_person_first_name"));
                custLastName = StringHelper.trim(rset.getString("contact_person_last_name"));
                placeOrderUser = StringHelper.trim(rset.getString("place_order_user"));
                placeOrderSalesman = StringHelper.trim(rset.getString("place_order_salesman"));
                placeOrderLocation = StringHelper.trim(rset.getString("place_order_location"));
                placeOrderDate = StringHelper.trim(rset.getString("po_date"));
            } rset.close();
            pstmt.close();

            storeMap = getStoreInfo(fesConn, placeOrderLocation);

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

            orderLangDisplay = getLangDisplay(conn, orderLang);

            if(!"".equals(placeOrderSalesman)) {
                salesmanName = getSalesmanName(conn, fesConn, placeOrderSalesman, orderLang);
            }


            // choose 1 of cust num / subr num which not empty
            sql = "select cust_num, subr_num " +
                  "  from epc_order_case " +
                  " where order_id = ? " + 
                  "   and cust_num is not null ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                custNum = StringHelper.trim(rset.getString("cust_num"));
                subrNum = StringHelper.trim(rset.getString("subr_num"));
            } rset.close();
            pstmt.close();

            if("".equals(custNum)) {
                custNum = "N/A";
            }
            if("".equals(subrNum)) {
                subrNum = "N/A";
            }
            // end of choose 1 of cust num / subr num which not empty

            // get products
            sql = "select item_id, cpq_item_desc, cpq_item_desc_chi, cpq_item_value, stock_status " +
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

            // get non-product charge
            sql = "select charge_code, charge_desc, charge_desc_chi, nvl(sum(charge_amount),0) as c_amt " +
                  "  from epc_order_charge a " + 
                  " where order_id = ? " + 
                  "   and need_to_pay = ? " +
                  "   and parent_item_id not in ( " + 
                  "     select b.item_id " + 
                  "       from epc_order_item b " +
                  "      where b.order_id = a.order_id " +
                  "        and b.item_cat in (?,?,?,?,?,?) " + 
                  "   ) " +
                  " group by charge_code, charge_desc, charge_desc_chi ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, "Y"); // need_to_pay = Y
            pstmt.setString(3, EpcItemCategory.DEVICE); // itemCat - DEVICE
            pstmt.setString(4, EpcItemCategory.SIM); // itemCat - SIM
            pstmt.setString(5, EpcItemCategory.APPLECARE); // itemCat - APPLECARE
            pstmt.setString(6, EpcItemCategory.SCREEN_REPLACE); // itemCat - SCREEN_REPLACE
            pstmt.setString(7, EpcItemCategory.GIFT_WRAPPING); // itemCat - GIFT_WRAPPING
            pstmt.setString(8, EpcItemCategory.PLASTIC_BAG); // itemCat - PLASTIC_BAG
            rsetNonProductCharge = pstmt.executeQuery();
            // end of get non-product charge

            // get contracts
            sql = "select a.*, " +
                  "       case " +
                  "         when duration is null then 'N/A' " +
                  "         else to_char(add_months(to_date(?,'yyyymmdd'), duration) -1, 'dd/mm/yyyy') " +
                  "       end as contract_end_date_str, " +
                  "       to_char(to_date(?,'yyyymmdd'),'dd/mm/yyyy') as contract_start_date_str " +
                  "  from epc_order_contract_details_hdr a " +
                  " where a.order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, placeOrderDate); // place_order_date
            pstmt.setString(2, placeOrderDate); // place_order_date
            pstmt.setInt(3, orderId); // order_id
            rsetContract = pstmt.executeQuery();
            // end of get contracts

            // get payment
            sql = "select payment_code, sum(payment_amount) as p_amount, currency_code " +
                  "  from epc_order_payment " +
                  " where order_id = ? " +
                  " group by payment_code, currency_code ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rsetPayment = pstmt.executeQuery();
            // end of get payment

            custSignatureImg = getCustSignature(conn, orderId, iSignId);
            // end of get order info


            // get template
            saHtml = getTemplate(conn, orderLang);
            productSectionTemplate = getProductDisplay(conn);
            contractSectionTemplate = getContractDisplay(conn, orderLang);
            paymentSectionTemplate = getPaymentDisplay(conn);
            appleCareSectionTemplate = getAppleCareDisplay(conn, orderLang);
            // end of get template


            // replace data into template
            saHtml = saHtml.replaceAll("\\[ORDER_REFERENCE\\]", orderReference);
            saHtml = saHtml.replaceAll("\\[ORDER_DATE\\]", orderDate);
            saHtml = saHtml.replaceAll("\\[ORDER_LANG\\]", orderLangDisplay);

//            if(!"".equals(custLastName)) {
//                custName = custLastName + " " + custFirstName;
//            } else {
//                custName = custFirstName;
//            }
            custName = "NA"; // default
            if(!"".equals(custLastName) && !"".equals(custFirstName)) {
                custName = custLastName + " " + custFirstName;
            } else if(!"".equals(custLastName) && "".equals(custFirstName)) {
                custName = custLastName;
            }
            saHtml = saHtml.replaceAll("\\[CUST_NAME\\]", custName);

            if("".equals(contactEmail)) {
                contactEmail = "NA";
            }
            saHtml = saHtml.replaceAll("\\[CONTACT_EMAIL\\]", contactEmail);

            if("".equals(contactNo)) {
                contactNo = "NA";
            }
            saHtml = saHtml.replaceAll("\\[CONTACT_NO\\]", contactNo);

            saHtml = saHtml.replaceAll("\\[CUST_NUM\\]", custNum);
            saHtml = saHtml.replaceAll("\\[SUBR_NUM\\]", subrNum);

            // loop thru products
            while(rsetProduct.next()) {
                tmpProductSection = productSectionTemplate; // reset per item

                tmpItemId = StringHelper.trim(rsetProduct.getString("item_id"));
                tmpProductDesc = StringHelper.trim(rsetProduct.getString("cpq_item_desc"));
                tmpProductDescChi = StringHelper.trim(rsetProduct.getString("cpq_item_desc_chi"));
                tmpImei = StringHelper.trim(rsetProduct.getString("cpq_item_value"));
                tmpProductTotalCharge = new BigDecimal(0); // reset
                tmpProductDiscountCharge = new BigDecimal(0); // reset
                tmpProductRrp = new BigDecimal(0); // reset

                if("E".equals(orderLang)) {
                    tmpProductSection = tmpProductSection.replace("[PRODUCT_DESC]", tmpProductDesc);
                } else {
                    tmpProductSection = tmpProductSection.replace("[PRODUCT_DESC]", tmpProductDescChi);
                }

                if(!"".equals(tmpImei)) {
                    tmpProductSection = tmpProductSection.replaceAll("\\[IMEI\\]", tmpImei);
                } else {
                    tmpProductSection = tmpProductSection.replaceAll("\\[IMEI\\]", "N/A");
                }

                tmpProductSection = tmpProductSection.replaceAll("\\[QUANTITY\\]", "1");

                // calc total charge
                pstmtProductTotalCharge.setInt(1, orderId); // order_id
                pstmtProductTotalCharge.setString(2, tmpItemId); // parent_item_id
                pstmtProductTotalCharge.setString(3, "Y"); // need_to_pay
                rsetProductCharge = pstmtProductTotalCharge.executeQuery();
                if(rsetProductCharge.next()) {
                    tmpProductTotalCharge = rsetProductCharge.getBigDecimal(1);
                } rsetProductCharge.close();

                tmpProductSection = tmpProductSection.replaceAll("\\[TOTAL_CHARGE\\]", tmpProductTotalCharge.intValue() + "");

                finalCharge = finalCharge.add(tmpProductTotalCharge);
                // end of calc total charge

                // calc discount charge
                pstmtProductDiscountCharge.setInt(1, orderId); // order_id
                pstmtProductDiscountCharge.setString(2, tmpItemId); // parent_item_id
                pstmtProductDiscountCharge.setString(3, "Y"); // need_to_pay
                pstmtProductDiscountCharge.setString(4, "DISCOUNT"); // charge_code - DISCOUNT
                rsetProductCharge = pstmtProductDiscountCharge.executeQuery();
                if(rsetProductCharge.next()) {
                    tmpProductDiscountCharge = rsetProductCharge.getBigDecimal(1);
                } rsetProductCharge.close();

                pstmtProductOnSpotDiscountCharge.setInt(1, orderId); // order_id
                pstmtProductOnSpotDiscountCharge.setString(2, tmpItemId); // parent_item_id
                pstmtProductOnSpotDiscountCharge.setString(3, "Y"); // need_to_pay
                pstmtProductOnSpotDiscountCharge.setString(4, "DISCOUNT"); // charge_code - DISCOUNT
                rsetProductCharge = pstmtProductOnSpotDiscountCharge.executeQuery();
                if(rsetProductCharge.next()) {
                    tmpProductDiscountCharge = tmpProductDiscountCharge.add(rsetProductCharge.getBigDecimal(1));
                } rsetProductCharge.close();

                tmpProductSection = tmpProductSection.replaceAll("\\[DISCOUNT\\]", tmpProductDiscountCharge.intValue() + "");
                // end of calc discount charge

                // get original charge
                pstmtProductRrp.setInt(1, orderId); // order_id
                pstmtProductRrp.setString(2, tmpItemId); // parent_item_id
                pstmtProductRrp.setString(3, "Y"); // need_to_pay
                pstmtProductRrp.setString(4, "DISCOUNT"); // charge_code - DISCOUNT
                rsetProductCharge = pstmtProductRrp.executeQuery();
                if(rsetProductCharge.next()) {
                    tmpProductRrp = rsetProductCharge.getBigDecimal(1);
                } rsetProductCharge.close();

                tmpProductSection = tmpProductSection.replaceAll("\\[RRP\\]", tmpProductRrp.intValue() + "");
                // end of get original charge

                productSectionSB.append(tmpProductSection);
            } rsetProduct.close();

            // loop thru non-product charge
            while(rsetNonProductCharge.next()) {
                // add a dummy row
                if(!isAddedDummyRow) {
                    tmpProductSection = productSectionTemplate; // reset
                    tmpProductSection = tmpProductSection.replace("[PRODUCT_DESC]", " ");
                    tmpProductSection = tmpProductSection.replaceAll("\\[IMEI\\]", " ");
                    tmpProductSection = tmpProductSection.replaceAll("\\[QUANTITY\\]", " ");
                    tmpProductSection = tmpProductSection.replaceAll("\\[RRP\\]", " ");
                    tmpProductSection = tmpProductSection.replaceAll("\\[DISCOUNT\\]", " ");
                    tmpProductSection = tmpProductSection.replaceAll("\\[TOTAL_CHARGE\\]", " ");

                    productSectionSB.append(tmpProductSection);
                    isAddedDummyRow = true;
                }
                // end of add a dummy row

                tmpProductSection = productSectionTemplate; // reset per non-product charge item

                tmpChargeCode = StringHelper.trim(rsetNonProductCharge.getString("charge_code"));
                tmpProductDesc = StringHelper.trim(rsetNonProductCharge.getString("charge_desc"));
                tmpProductDescChi = StringHelper.trim(rsetNonProductCharge.getString("charge_desc_chi"));
                tmpProductTotalCharge = rsetNonProductCharge.getBigDecimal("c_amt");
                tmpProductDiscountCharge = rsetNonProductCharge.getBigDecimal("c_amt");

                if("DISCOUNT".equals(tmpChargeCode)) {
                    tmpProductSection = tmpProductSection.replace("[PRODUCT_DESC]", nonProductChargeDescMap.get(orderLang));
                } else {
                    if("E".equals(orderLang)) {
                        tmpProductSection = tmpProductSection.replace("[PRODUCT_DESC]", tmpProductDesc);
                    } else {
                        tmpProductSection = tmpProductSection.replace("[PRODUCT_DESC]", tmpProductDescChi);
                    }
                }

                tmpProductSection = tmpProductSection.replaceAll("\\[IMEI\\]", " ");

                tmpProductSection = tmpProductSection.replaceAll("\\[QUANTITY\\]", " ");

                tmpProductSection = tmpProductSection.replaceAll("\\[RRP\\]", " ");

                if("DISCOUNT".equals(tmpChargeCode)) {
                    // discount 
                    tmpProductSection = tmpProductSection.replaceAll("\\[DISCOUNT\\]", (tmpProductDiscountCharge.intValue() * -1) + "");
                    tmpProductSection = tmpProductSection.replaceAll("\\[TOTAL_CHARGE\\]", " ");

                    finalCharge = finalCharge.add(tmpProductDiscountCharge);
                } else {
                    // i.e. courier charge
                    tmpProductSection = tmpProductSection.replaceAll("\\[DISCOUNT\\]", " ");
                    tmpProductSection = tmpProductSection.replaceAll("\\[TOTAL_CHARGE\\]", tmpProductTotalCharge.intValue() + "");

                    finalCharge = finalCharge.add(tmpProductTotalCharge);
                }

                productSectionSB.append(tmpProductSection);
            }  rsetNonProductCharge.close();
            // end of loop thru non-product charge

            saHtml = saHtml.replace("[PRODUCTS]", productSectionSB.toString());
            saHtml = saHtml.replaceAll("\\[FINAL_CHARGE\\]", finalCharge.intValue() + "");
            // end of loop thru products

            // loop thru contracts
            while(rsetContract.next()) {
                tmpContractSection = contractSectionTemplate; // reset per contract

                tmpCaseId = StringHelper.trim(rsetContract.getString("case_id"));
                tmpParentItemId = StringHelper.trim(rsetContract.getString("parent_item_id"));
                tmpPenaltyAmount = rsetContract.getBigDecimal("penalty_amount");
                tmpContractDuration = rsetContract.getInt("duration");
                tmpContractStartDate = StringHelper.trim(rsetContract.getString("contract_start_date_str"));
                tmpContractEndDate = StringHelper.trim(rsetContract.getString("contract_end_date_str"));

                // get cust num / subr num
                tmpContractCustNum = ""; // reset
                tmpContractSubrNum = ""; // reset

                pstmtContractCustSubrNum.setInt(1, orderId); // order_id
                pstmtContractCustSubrNum.setString(2, tmpCaseId); // case_id
                rsetContractCustSubrNum = pstmtContractCustSubrNum.executeQuery();
                if(rsetContractCustSubrNum.next()) {
                    tmpContractCustNum = StringHelper.trim(rsetContractCustSubrNum.getString("cust_num"));
                    tmpContractSubrNum = StringHelper.trim(rsetContractCustSubrNum.getString("subr_num"));
                } rsetContractCustSubrNum.close();
                // end of get cust num / subr num

                // get corresponding tnc
                tmpTncString = ""; // reset

                pstmtTnc.setInt(1, orderId); // order_id
                pstmtTnc.setString(2, tmpCaseId); // case_id
                pstmtTnc.setString(3, tmpParentItemId); // parent_item_id
                rsetTnc = pstmtTnc.executeQuery();
                while(rsetTnc.next()) {
                    if("E".equals(orderLang)) {
                        tmpTncUrl = StringHelper.trim(rsetTnc.getString("tnc_zh_hk"));
                    } else {
                        tmpTncUrl = StringHelper.trim(rsetTnc.getString("tnc_en"));
                    }

                    if(!"".equals(tmpTncString)) {
                        tmpTncString += ", ";
                    }
                    tmpTncString += "<b><a href='" + tmpTncUrl + "'>" + StringHelper.trim(rsetTnc.getString("tnc_number")) + "</a></b>";
                } rsetTnc.close();
                // end of get corresponding tnc

                if("E".equals(orderLang)) {
                    tmpContractDurationStr = tmpContractDuration + " " + contractMonthDisplayMap.get(orderLang);
                } else {
                    tmpContractDurationStr = tmpContractDuration + contractMonthDisplayMap.get(orderLang);
                }

                tmpContractSection = tmpContractSection.replaceAll("\\[CUST_NUM\\]", tmpContractCustNum);
                tmpContractSection = tmpContractSection.replaceAll("\\[SUBR_NUM\\]", tmpContractSubrNum);
                tmpContractSection = tmpContractSection.replaceAll("\\[CONTRACT_PERIOD\\]", tmpContractDurationStr);
                tmpContractSection = tmpContractSection.replace("[T_N_C]", tmpTncString);
                tmpContractSection = tmpContractSection.replaceAll("\\[CONTRACT_PENALITY_AMOUNT\\]", tmpPenaltyAmount + "");
                tmpContractSection = tmpContractSection.replaceAll("\\[CONTRACT_DATE_PERIOD\\]", tmpContractStartDate + " - " + tmpContractEndDate);

                contractSectionSB.append(tmpContractSection);
            } rsetContract.close();

            if(contractSectionSB.isEmpty()) {
                tmpContractSection = contractSectionTemplate; // create a default one
                tmpContractSection = tmpContractSection.replaceAll("\\[CUST_NUM\\]", "");
                tmpContractSection = tmpContractSection.replaceAll("\\[SUBR_NUM\\]", "");
                tmpContractSection = tmpContractSection.replaceAll("\\[CONTRACT_PERIOD\\]", "N/A");
                tmpContractSection = tmpContractSection.replace("[T_N_C]", "");
                tmpContractSection = tmpContractSection.replaceAll("\\[CONTRACT_PENALITY_AMOUNT\\]", "");
                tmpContractSection = tmpContractSection.replaceAll("\\[CONTRACT_DATE_PERIOD\\]", "");

                contractSectionSB.append(tmpContractSection);
            }

            saHtml = saHtml.replace("[CONTRACTS]", contractSectionSB.toString());
            // end of loop thru contracts

            // loop thru payment
            while(rsetPayment.next()) {
                tmpPaymentSection = paymentSectionTemplate; // reset per item
                tmpPaymentCode = StringHelper.trim(rsetPayment.getString("payment_code"));
                tmpCurrencyCode = StringHelper.trim(rsetPayment.getString("currency_code"));
                tmpPaymentAmount = rsetPayment.getBigDecimal("p_amount");

                tmpPaymentSection = tmpPaymentSection.replaceAll("\\[PAYMENT_CODE\\]", tmpPaymentCode + "");
                tmpPaymentSection = tmpPaymentSection.replaceAll("\\[CURRENCY_CODE\\]", tmpCurrencyCode + "");
                tmpPaymentSection = tmpPaymentSection.replaceAll("\\[PAYMENT_AMOUNT\\]", tmpPaymentAmount.intValue() + "");

                paymentSectionSB.append(tmpPaymentSection);
            } rsetPayment.close();

            saHtml = saHtml.replaceAll("\\[PAYMENTS\\]", paymentSectionSB.toString());
            // end of loop thru payment

            // replace store info
            saHtml = saHtml.replaceAll("\\[STORE_PHONE_NO\\]", storeMap.get("PHONE_NO"));
            saHtml = saHtml.replaceAll("\\[STORE_FAX_NO\\]", storeMap.get("FAX_NO"));
            if("C".equals(orderLang)) {
                saHtml = saHtml.replaceAll("\\[STORE_ADDR\\]", storeMap.get("STORE_ADDR_CHI"));
            } else {
                saHtml = saHtml.replaceAll("\\[STORE_ADDR\\]", storeMap.get("STORE_ADDR_ENG"));
            }
            saHtml = saHtml.replaceAll("\\[STORE_SIGNATURE\\]", storeMap.get("STORE_STAMP_IMG"));
            // end of replace store info

            // replace salesman name
            saHtml = saHtml.replaceAll("\\[SALESMAN_NAME\\]", salesmanName);
            // end of replace salesman name

            // replace customer signature
            saHtml = saHtml.replaceAll("\\[CUST_SIGNATURE\\]", custSignatureImg);
            // end of replace customer signature

            // replace img
            saHtml = saHtml.replaceAll("\\[EPC_SA_FORM_IMG_SINGLELINE\\]", imgMap.get("EPC_SA_FORM_IMG_SINGLELINE"));
            saHtml = saHtml.replaceAll("\\[EPC_SA_FORM_IMG_DOUBLELINE\\]", imgMap.get("EPC_SA_FORM_IMG_DOUBLELINE"));
            saHtml = saHtml.replaceAll("\\[EPC_SA_FORM_IMG_EMPTYBOX\\]", imgMap.get("EPC_SA_FORM_IMG_EMPTYBOX"));
            saHtml = saHtml.replaceAll("\\[EPC_SA_FORM_IMG_LOGO\\]", imgMap.get("EPC_SA_FORM_IMG_LOGO"));
            // end of replace img

            // replace sa remark
            if("Y".equals(hasAppleCareItem)) {
                saHtml = saHtml.replace("[SA_REMARKS]", appleCareSectionTemplate);
            } else {
                saHtml = saHtml.replace("[SA_REMARKS]", "");
            }
            // end of replace sa remark
            
            // end of replace data into template

//logger.info("{}{}", logStr, saHtml);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ee) {}
        }
        return saHtml;
    }


    public String getTemplate(Connection conn, String orderLang) {
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
            pstmt.setString(1, "EPC_SA_FORM"); // msg_type
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


    public String getProductDisplay(Connection conn) {
        String content = "";
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select msg_content  " +
                  "  from gp_msg_template " +
                  " where msg_type = ? " +
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "EPC_SA_FORM_PRODUCT"); // msg_type
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


    public String getContractDisplay(Connection conn, String orderLang) {
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
            pstmt.setString(1, "EPC_SA_FORM_CONTRACT"); // msg_type
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


    public HashMap<String, String> getContractMonthDisplay(Connection conn) {
        HashMap<String, String>  displayMap = new HashMap<>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select lang, msg_content  " +
                  "  from gp_msg_template " +
                  " where msg_type = ? " +
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "EPC_SA_FORM_CONTRACT_MONTH"); // msg_type
            pstmt.setString(2, "A"); // status
            rset = pstmt.executeQuery();
            while(rset.next()) {
                displayMap.put(StringHelper.trim(rset.getString("lang")), StringHelper.trim(rset.getString("msg_content")));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return displayMap;
    }


    public String getPaymentDisplay(Connection conn) {
        String content = "";
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select msg_content  " +
                  "  from gp_msg_template " +
                  " where msg_type = ? " +
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "EPC_SA_FORM_PAYMENT"); // msg_type
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


    public String getAppleCareDisplay(Connection conn, String orderLang) {
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
            pstmt.setString(1, "EPC_SA_FORM_APPLECARE_REMARK"); // msg_type
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


    public HashMap<String, String> getImgDisplay(Connection conn) {
        HashMap<String, String> imgMap = new HashMap<>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select msg_type, msg_content  " +
                  "  from gp_msg_template " +
                  " where msg_type in (?,?,?,?) " +
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "EPC_SA_FORM_IMG_LOGO"); // msg_type
            pstmt.setString(2, "EPC_SA_FORM_IMG_EMPTYBOX"); // msg_type
            pstmt.setString(3, "EPC_SA_FORM_IMG_SINGLELINE"); // msg_type
            pstmt.setString(4, "EPC_SA_FORM_IMG_DOUBLELINE"); // msg_type
            pstmt.setString(5, "A"); // status
            rset = pstmt.executeQuery();
            while(rset.next()) {
                imgMap.put(StringHelper.trim(rset.getString("msg_type")), StringHelper.trim(rset.getString("msg_content")));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return imgMap;
    }


    public String getCustSignature(Connection conn, int orderId, String signId) {
        String content = "";
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select content  " +
                  "  from epc_order_sign " +
                  " where order_id = ? " +
                  "   and sign_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, signId); // sign_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                content = StringHelper.trim(rset.getString("content"));
            } rset.close();
            pstmt.close();

            if("".equals(content)) {
                // get a default one (white pic)
                // ...
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return content;
    }


    public String hasAppleCareItem(Connection conn, int orderId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        int cnt = 0;
        String hasAppleCareItem = "N";

        try {
            sql = "select count(1)  " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and item_cat = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
            rset = pstmt.executeQuery();
            if(rset.next()) {
                cnt = rset.getInt(1);
            } rset.close();
            pstmt.close();

            if(cnt > 0) {
                hasAppleCareItem = "Y";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return hasAppleCareItem;
    }


    public HashMap<String, String> getStoreInfo(Connection fesConn, String location) {
        HashMap<String, String> storeMap = new HashMap<>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        Blob stampBlob = null;
        byte[] stampArray = null;
        String stampString = "";

        try {
            sql = "select phone_no, fax_no, store_sms_addr_eng, store_sms_addr_chi, store_stamp " +
                  "  from rbd_unit " +
                  " where rbd_unit_code = ? ";
            pstmt = fesConn.prepareStatement(sql);
            pstmt.setString(1, location); // rbd_unit_code
            rset = pstmt.executeQuery();
            if(rset.next()) {
                storeMap.put("PHONE_NO", StringHelper.trim(rset.getString("phone_no")));
                storeMap.put("FAX_NO", StringHelper.trim(rset.getString("fax_no")));
                storeMap.put("STORE_ADDR_ENG", StringHelper.trim(rset.getString("store_sms_addr_eng")));
                storeMap.put("STORE_ADDR_CHI", EpcCharsetHelper.convertCharset2View(StringHelper.trim(rset.getString("store_sms_addr_chi"))));

                stampBlob = rset.getBlob("store_stamp");
                if(stampBlob != null) {
                    stampArray = stampBlob.getBytes(1, (int)stampBlob.length());
                    stampString = "data:image/png;base64," + Base64.getEncoder().encodeToString(stampArray);
                }
                storeMap.put("STORE_STAMP_IMG", stampString);
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return storeMap;
    }


    public String getSalesmanName(Connection epcConn, Connection fesConn, String salesmanCode, String orderLang) {
        String name = "";
        String sex = "";
        String title = "";
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select fullname, sex " +
                  "  from user_info " +
                  " where username = ? ";
            pstmt = fesConn.prepareStatement(sql);
            pstmt.setString(1, salesmanCode); // rbd_unit_code
            rset = pstmt.executeQuery();
            if(rset.next()) {
                name = StringHelper.trim(rset.getString("fullname"));
                sex = StringHelper.trim(rset.getString("sex"));
            } rset.close();
            pstmt.close();

            sql = "select value_str1 " +
                  "  from epc_control_tbl " + 
                  " where rec_type = ? " +
                  "   and key_str1 = ? " +
                  "   and key_str2 = ? " +
                  "   and value_str2 = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setString(1, "SA_SALESMAN_TITLE"); // rec_type
            pstmt.setString(2, orderLang); // key_str1 - orderLang
            pstmt.setString(3, sex); // key_str2 - sex
            pstmt.setString(4, "A"); // value_str2 - status: A/O
            rset = pstmt.executeQuery();
            if(rset.next()) {
                title = StringHelper.trim(rset.getString("value_str1"));
            } rset.close();
            pstmt.close();

            if ("E".equals(orderLang)) {
                name = title + " " + name;
            } else {
                name = name + " " + title;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return name;
    }


    public HashMap<String, String> getNonProductChargeDesc(Connection epcConn) {
        HashMap<String, String> aMap = new HashMap<>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select lang, msg_content " +
                  "  from gp_msg_template " +
                  " where msg_type = ? " +
                  "   and status = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setString(1, "EPC_SA_FORM_NON_PRODUCT_CHARGE_DESC"); // msg_type
            pstmt.setString(2, "A"); // status
            rset = pstmt.executeQuery();
            while(rset.next()) {
                aMap.put(StringHelper.trim(rset.getString("lang")), StringHelper.trim(rset.getString("msg_content")));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return aMap;
    }
}
