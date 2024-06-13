package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcAddProductToQuoteResult;
import epc.epcsalesapi.sales.bean.EpcAddSigmaItemToQuote;
import epc.epcsalesapi.sales.bean.EpcConfiguredValue;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.EpcQuoteProductCandidate;

@Service
public class EpcAddQuoteItemHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcAddQuoteItemHandler.class);

    private DataSource epcDataSource;
    private EpcOrderHandler epcOrderHandler;
    private EpcQuoteHandler epcQuoteHandler;
    private EpcSalesmanHandler epcSalesmanHandler;
    private EpcOrderItemSql epcOrderItemSql;
    

    public EpcAddQuoteItemHandler(
        DataSource epcDataSource, EpcOrderHandler epcOrderHandler,
        EpcQuoteHandler epcQuoteHandler, EpcSalesmanHandler epcSalesmanHandler, EpcOrderItemSql epcOrderItemSql
    ) {
        this.epcDataSource = epcDataSource;
        this.epcOrderHandler = epcOrderHandler;
        this.epcQuoteHandler = epcQuoteHandler;
        this.epcSalesmanHandler = epcSalesmanHandler;
        this.epcOrderItemSql = epcOrderItemSql;
    }


    public EpcAddSigmaItemToQuote addSigmaItemToQuote(EpcAddSigmaItemToQuote epcAddSigmaItemToQuote) {
    	Connection conn = null;
    	PreparedStatement pstmt = null;
//    	String sql = "";
        String orderReference = "";
        String custId = "";
        int orderId = 0;
        int quoteId = 0;
        String quoteGuid = "";
        String tmpQuoteGuid = "";
        String productGuid = "";
        boolean isValid = true;
        String errMsg = "";
        EpcAddProductToQuoteResult epcAddProductToQuoteResult;
//        EpcQuote epcQuote = null;
        String createUser = "";
    	String createSalesman = "";
    	String createChannel = "";
    	String createLocation = "";
    	String remarks = "";
    	String caseId = "";
    	String sigmaItemId = "";
    	String offerName = "";
    	String offerGuid = "";
    	String subrNum = "";
    	String custNum = "";
    	String acctNum = "";
    	EpcQuoteItem epcQuoteItem = null;
    	HashMap<String, Object> tmpMap = null;
    	EpcQuoteProductCandidate epcQuoteProductCandidate = null;
    	HashMap<String, Object> cmsItemMapping = null;
    	String withSpec = "";
        String quoteItemString = "";
        String cmsItemMappingString = "";
        HashMap<String, String> specialProductMap = epcOrderHandler.getSpecialProductMap();
    	
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            
            custId = StringHelper.trim(epcAddSigmaItemToQuote.getCustId());
            orderId = epcAddSigmaItemToQuote.getOrderId();
            quoteId = epcAddSigmaItemToQuote.getQuoteId();
            productGuid = StringHelper.trim(epcAddSigmaItemToQuote.getProductGuid());
            cmsItemMapping = epcAddSigmaItemToQuote.getCmsItemMapping();
            createUser = StringHelper.trim(epcAddSigmaItemToQuote.getCreateUser());
            createSalesman = StringHelper.trim(epcAddSigmaItemToQuote.getCreateSalesman());
            createChannel = StringHelper.trim(epcAddSigmaItemToQuote.getCreateChannel());
            createLocation = StringHelper.trim(epcAddSigmaItemToQuote.getCreateLocation());
            withSpec = StringHelper.trim(epcAddSigmaItemToQuote.getWithSpec());

            
            // basic checking
            orderReference = epcOrderHandler.isOrderBelongCust(conn, custId, orderId);
        	if("NOT_BELONG".equals(orderReference)) {
        		errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
            	isValid = false;
        	}

            if(epcOrderHandler.isOrderLocked(conn, custId, orderId)) {
                errMsg += "input order [" + orderId + "] is locked. ";
                isValid = false;
            }
        	
        	quoteGuid = epcOrderHandler.getCurrentQuoteGuid(conn, orderId, quoteId);
        	if("".equals(quoteGuid)) {
        		errMsg += "input order/quote id [" + orderId + "/" + quoteId + "] is not valid. ";
            	isValid = false;
        	} 
// skip to get quote to speed up run time, kerrytsang, 20220805
//            else {
//        		epcQuote = epcQuoteHandler.getQuoteInfo(quoteGuid);
//        	}
        	
        	if(!"Y".equals(withSpec) && !"N".equals(withSpec)) {
        		errMsg += "withSpec [" + withSpec + "] is not valid. ";
            	isValid = false;
        	}
            // end of basic checking
            
            if(isValid) {
// not perform copy quote action, kerrytsang, 20220805
//            	if(epcQuote.getQuoteType() == 3) {
//            		// converted, need to perform copy action to generate another quote
//            		tmpQuoteGuid = epcQuoteHandler.copyQuote(quoteGuid);
//            	} else {
//            		tmpQuoteGuid = quoteGuid;
//            	}
                tmpQuoteGuid = quoteGuid;
                
                epcAddProductToQuoteResult = epcQuoteHandler.addProductToQuote(orderId, tmpQuoteGuid, productGuid, withSpec); // according to Alphie, withSpec is released for input, 20220120
                if("SUCCESS".equals(epcAddProductToQuoteResult.getResult())) {
                    // success
                    epcQuoteItem = epcAddProductToQuoteResult.getEpcQuoteItem();
                    
                    // prepare productCandidateObj, requested by Tim kwan, 20210527
                    epcQuoteProductCandidate = epcQuoteHandler.convertProductCandidate(
                        epcQuoteItem.getProductCandidate(), epcQuoteItem.getMetaDataLookup(), "N", specialProductMap
                    );
                    epcQuoteItem.setProductCandidateObj(epcQuoteProductCandidate);
                    // end of prepare productCandidateObj, requested by Tim kwan, 20210527
                    
//                    if(!tmpQuoteGuid.equals(quoteGuid)) {
//                        // save quote (if new) to epc table
//                        updateQuoteGuid(conn, orderId, quoteId, quoteGuid, tmpQuoteGuid, "update to new quote due to " + quoteGuid + " being converted");
//                    }
                    
                    // get case id
                    sigmaItemId = epcQuoteItem.getId();
                    offerGuid = epcQuoteItem.getProductId();
                    tmpMap = epcQuoteItem.getProductCandidate();
                    caseId = StringHelper.trim((String)tmpMap.get("ID"));
                    offerName = epcQuoteItem.getName();
                    // end of get case id
                    
                    // get subrNum and custNum from configuredValue of offer package, which SHOULD be the root of productCandidate
                    // path: EpcQuote -> EpcQuoteItem -> productCandidateObj -> EpcQuoteProductCandidate -> configuredValue -> 
                    // name = "msisdn" / "Customer_Number" / "Billing_Account_Number"
                    ArrayList<EpcConfiguredValue> configuredValueList = epcQuoteProductCandidate.getConfiguredValue();
                    for(EpcConfiguredValue epcConfiguredValue:configuredValueList) {
                        if ("MSISDN".equals(epcConfiguredValue.getName().toUpperCase())) {
                            subrNum = epcConfiguredValue.getValue();
                        }
                        if ("CUSTOMER_NUMBER".equals(epcConfiguredValue.getName().toUpperCase())) {
                            custNum = epcConfiguredValue.getValue();
                        }
                        if ("BILLING_ACCOUNT_NUMBER".equals(epcConfiguredValue.getName().toUpperCase())) {
                            acctNum = epcConfiguredValue.getValue();
                        }
                    }

                    // remove metatypelookup
    //                    epcQuoteItem.setMetaTypeLookup(null);
                    // end of remove metatypelookup

                    quoteItemString = new ObjectMapper().writeValueAsString(epcQuoteItem);

                    cmsItemMappingString = new ObjectMapper().writeValueAsString(cmsItemMapping);
                    
                    
                    // create record for ec_order_case
                    pstmt = conn.prepareStatement(epcOrderItemSql.SQL_INSERT_ORDER_CASE);
                    pstmt.setInt(1, orderId); // order_id
                    pstmt.setInt(2, quoteId); // quote_id
                    pstmt.setString(3, caseId); // case_id
                    pstmt.setString(4, offerGuid); // cpq_offer_guid
                    pstmt.setString(5, offerName); // cpq_offer_desc
                    pstmt.setString(6, offerName); // cpq_offer_desc_chi
                    pstmt.setString(7, sigmaItemId); // quote_item_guid
                    pstmt.setString(8, quoteItemString); // quote_item_content
                    pstmt.setString(9, cmsItemMappingString); // cms_item_mapping
                    pstmt.setString(10, subrNum); // subr_num
                    pstmt.setString(11, custNum); // cust_num
                    pstmt.setString(12, acctNum); // acct_num
                    pstmt.executeUpdate();
                    // end of create record for ec_order_case


                    // create salesman log
                    remarks = "add " + caseId + " [sigmaItemId:" + sigmaItemId + ",package guid:" + productGuid + "] to quote " + tmpQuoteGuid;
                    epcSalesmanHandler.createSalesmanLog(conn, orderId, caseId, createUser, createSalesman, createLocation, createChannel, epcSalesmanHandler.actionAddQuoteItem, remarks);
                    // end of create salesman log
                    
                    epcAddSigmaItemToQuote.setResult("SUCCESS");

                    epcQuoteItem.setMetaTypeLookup(null);
                    epcAddSigmaItemToQuote.setEpcQuoteItem(epcQuoteItem);
                    
                    conn.commit();
                } else {
                    // fail
                    epcAddSigmaItemToQuote.setResult("FAIL");
                    epcAddSigmaItemToQuote.setErrMsg(epcAddProductToQuoteResult.getErrMsg());
                    epcAddSigmaItemToQuote.setErrMsg2(epcAddProductToQuoteResult.getErrMsg2());
                }
            } else {
                // fail
                epcAddSigmaItemToQuote.setResult("FAIL");
                epcAddSigmaItemToQuote.setErrMsg(errMsg);
            }
        } catch(Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            epcAddSigmaItemToQuote.setResult("FAIL");
            epcAddSigmaItemToQuote.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcAddSigmaItemToQuote;
    }

}
