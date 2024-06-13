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

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcAddProductToQuoteResult;
import epc.epcsalesapi.sales.bean.EpcConfiguredValue;
import epc.epcsalesapi.sales.bean.EpcDeleteSigmaItemFromQuote;
import epc.epcsalesapi.sales.bean.EpcMoveQuoteItemToOtherQuote;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;

@Service
public class EpcMoveQuoteItemHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcMoveQuoteItemHandler.class);

    private DataSource epcDataSource;
    private EpcOrderHandler epcOrderHandler;
    private EpcQuoteHandler epcQuoteHandler;
    private EpcDeleteQuoteItemHandler epcDeleteQuoteItemHandler;
    private EpcSalesmanHandler epcSalesmanHandler;
    private EpcSecurityHelper epcSecurityHelper;
    private EpcOrderItemSql epcOrderItemSql;


    public EpcMoveQuoteItemHandler(
        DataSource epcDataSource, EpcOrderHandler epcOrderHandler,
        EpcQuoteHandler epcQuoteHandler, EpcDeleteQuoteItemHandler epcDeleteQuoteItemHandler,
        EpcSalesmanHandler epcSalesmanHandler, EpcSecurityHelper epcSecurityHelper,
        EpcOrderItemSql epcOrderItemSql
    ) {
        this.epcDataSource = epcDataSource;
        this.epcOrderHandler = epcOrderHandler;
        this.epcQuoteHandler = epcQuoteHandler;
        this.epcDeleteQuoteItemHandler = epcDeleteQuoteItemHandler;
        this.epcSalesmanHandler = epcSalesmanHandler;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcOrderItemSql = epcOrderItemSql;
    }


    public void moveQuoteItemToOtherQuote(EpcMoveQuoteItemToOtherQuote epcMoveQuoteItemToOtherQuote) {
        Connection conn = null;
        PreparedStatement pstmtCase = null;
        PreparedStatement pstmtItem = null;
        PreparedStatement pstmtVoucher = null;
        String orderReference = "";
        String tmpCustId = "";
        int tmpOrderId = 0;
        int tmpQuoteId = 0;
        String tmpCaseId = "";
        String tmpQuoteItemGuid = "";
        String targetCustId = "";
        int targetOrderId = 0;
        int targetQuoteId = 0;
        String targetQuoteGuid = "";
        String tmpQuoteGuid = "";
        String tmpPackageGuid = "";
        String tmpOfferName = "";
        String tmpCmsMapping = "";
        String tmpSubrNum = "";
        String tmpCustNum = "";
        String tmpAcctNum = "";
        boolean isValid = true;
        StringBuilder errMsgSb = new StringBuilder();
        EpcAddProductToQuoteResult epcAddProductToQuoteResult;
        EpcQuoteItem tmpQuoteItem = null;
        EpcQuoteItem targetQuoteItem = null;
        HashMap<String, Object> tmpProductCandidate = null;
        String createUser = "";
        String createSalesman = "";
        String createChannel = "";
        String createLocation = "";
        String remarks = "";
        EpcDeleteSigmaItemFromQuote epcDeleteSigmaItemFromQuote = null;
        String quoteItemString = "";
        String logStr = "[moveQuoteItemToOtherQuote]";


        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            tmpCustId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcMoveQuoteItemToOtherQuote.getTmpCustId()));
            tmpOrderId = epcMoveQuoteItemToOtherQuote.getTmpOrderId();
            tmpQuoteId = epcMoveQuoteItemToOtherQuote.getTmpQuoteId();
            tmpQuoteItemGuid = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcMoveQuoteItemToOtherQuote.getTmpQuoteItemGuid()));
            targetCustId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcMoveQuoteItemToOtherQuote.getTargetCustId()));
            targetOrderId = epcMoveQuoteItemToOtherQuote.getTargetOrderId();
            targetQuoteId = epcMoveQuoteItemToOtherQuote.getTargetQuoteId();
            createUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcMoveQuoteItemToOtherQuote.getCreateUser()));
            createSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcMoveQuoteItemToOtherQuote.getCreateSalesman()));
            createChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcMoveQuoteItemToOtherQuote.getCreateChannel()));
            createLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcMoveQuoteItemToOtherQuote.getCreateLocation()));

            logStr += "[tmpCustId:" + tmpCustId + "][tmpOrderId:" + tmpOrderId + "][tmpQuoteId:" + tmpQuoteId + "][tmpQuoteItemGuid:" + tmpQuoteItemGuid + "]";
            logStr += "[targetCustId:" + targetCustId + "][targetOrderId:" + targetOrderId + "][targetQuoteId:" + targetQuoteId + "] ";


            // basic checking
            orderReference = epcOrderHandler.isOrderBelongCust(conn, tmpCustId, tmpOrderId);
            if("NOT_BELONG".equals(orderReference)) {
                errMsgSb.append("input tmp order id [" + tmpOrderId + "] is not belonged to tmp cust id [" + tmpCustId + "]. ");
                isValid = false;
            }

            if(epcOrderHandler.isOrderLocked(conn, tmpCustId, tmpOrderId)) {
                errMsgSb.append("input tmp order [" + tmpOrderId + "] is locked. ");
                isValid = false;
            }

            orderReference = epcOrderHandler.isOrderBelongCust(conn, targetCustId, targetOrderId);
            if("NOT_BELONG".equals(orderReference)) {
                errMsgSb.append("input target order id [" + targetOrderId + "] is not belonged to target cust id [" + targetCustId + "]. ");
                isValid = false;
            }

            if(epcOrderHandler.isOrderLocked(conn, targetCustId, targetOrderId)) {
                errMsgSb.append("input target order [" + targetOrderId + "] is locked. ");
                isValid = false;
            }
            
            tmpQuoteGuid = epcOrderHandler.getCurrentQuoteGuid(conn, tmpOrderId, tmpQuoteId);
            if("".equals(tmpQuoteGuid)) {
                errMsgSb.append("input tmp order/quote id [" + tmpOrderId + "/" + tmpQuoteId + "] is not valid. ");
                isValid = false;
            } else {
//                tmpQuoteItem = epcQuoteHandler.getQuoteItem(tmpQuoteGuid, tmpQuoteItemGuid, "");
                tmpQuoteItem = epcOrderHandler.getQuoteItemInEpc(tmpOrderId, tmpQuoteId, tmpQuoteGuid, tmpQuoteItemGuid);

                if(tmpQuoteItem == null) {
                    errMsgSb.append("tmp quote item [" + tmpQuoteItemGuid + "] is not found in tmp quote [" + tmpQuoteId + "]. ");
                    isValid = false;
                } else {
                    tmpPackageGuid = tmpQuoteItem.getProductId();
                    tmpOfferName = tmpQuoteItem.getName();
                    tmpProductCandidate = tmpQuoteItem.getProductCandidate();
                    tmpCaseId = StringHelper.trim((String)tmpProductCandidate.get("ID"));
//                    tmpCmsMapping = epcOrderAttrHandler.getAttrValue(conn, tmpOrderId, tmpCaseId, "", epcOrderAttrHandler.ATTR_TYPE_CMS_ITEM_MAPPING);
                    tmpCmsMapping = epcOrderHandler.getCmsItemMapping(conn, tmpOrderId, tmpCaseId);
                    
                    // get subrNum and custNum from configuredValue of offer package, which SHOULD be the root of productCandidate
                    // path: EpcQuote -> EpcQuoteItem -> productCandidateObj -> EpcQuoteProductCandidate -> configuredValue -> 
                    // name = "msisdn" / "Customer_Number" / "Billing_Account_Number"
                    ArrayList<EpcConfiguredValue> configuredValueList = tmpQuoteItem.getProductCandidateObj().getConfiguredValue();
                    for(EpcConfiguredValue epcConfiguredValue:configuredValueList) {
                        if ("MSISDN".equals(epcConfiguredValue.getName().toUpperCase())) {
                            tmpSubrNum = epcConfiguredValue.getValue();
                        }
                        if ("CUSTOMER_NUMBER".equals(epcConfiguredValue.getName().toUpperCase())) {
                           tmpCustNum = epcConfiguredValue.getValue();
                        }
                        if ("BILLING_ACCOUNT_NUMBER".equals(epcConfiguredValue.getName().toUpperCase())) {
                            tmpAcctNum = epcConfiguredValue.getValue();
                         }
                    }

                    epcMoveQuoteItemToOtherQuote.setPackageGuid(tmpPackageGuid);
                }
            }

            targetQuoteGuid = epcOrderHandler.getCurrentQuoteGuid(conn, targetOrderId, targetQuoteId);
            if("".equals(targetQuoteGuid)) {
                errMsgSb.append("input target order/quote id [" + targetOrderId + "/" + targetQuoteId + "] is not valid. ");
                isValid = false;
            }
            // end of basic checking


            if(isValid) {
                // add tmp package & product candidate to target quote
logger.info("{}{}{}", logStr, "move tmpPackageGuid:", tmpPackageGuid);
                epcAddProductToQuoteResult = epcQuoteHandler.addProductToQuoteWithCandidate(targetQuoteGuid, tmpPackageGuid, tmpProductCandidate);
logger.info("{}{}{}", logStr, "result:", epcAddProductToQuoteResult.getResult());
logger.info("{}{}{}", logStr, "errMsg:", epcAddProductToQuoteResult.getErrMsg());
                if("SUCCESS".equals(epcAddProductToQuoteResult.getResult())) {
                    targetQuoteItem = epcAddProductToQuoteResult.getEpcQuoteItem();

                    epcMoveQuoteItemToOtherQuote.setEpcQuoteItem(targetQuoteItem);
                    epcMoveQuoteItemToOtherQuote.setTargetQuoteItemGuid(targetQuoteItem.getId());

                    // remove metatypelookup
                    targetQuoteItem.setMetaTypeLookup(null);
                    // end of remove metatypelookup

                    quoteItemString = new ObjectMapper().writeValueAsString(targetQuoteItem);

                    // save epc records
                    pstmtCase = conn.prepareStatement(epcOrderItemSql.SQL_INSERT_ORDER_CASE);
                    pstmtItem = conn.prepareStatement(epcOrderItemSql.SQL_INSERT_ORDER_ITEM);
                    pstmtVoucher = conn.prepareStatement(epcOrderItemSql.SQL_INSERT_ORDER_VOUCHER);

                    pstmtCase.setInt(1, targetOrderId); // order_id
                    pstmtCase.setInt(2, targetQuoteId); // quote_id
                    pstmtCase.setString(3, tmpCaseId); // case_id
                    pstmtCase.setString(4, tmpPackageGuid); // cpq_offer_guid
                    pstmtCase.setString(5, tmpOfferName); // cpq_offer_desc
                    pstmtCase.setString(6, tmpOfferName); // cpq_offer_desc_chi
                    pstmtCase.setString(7, targetQuoteItem.getId()); // quote_item_guid
                    pstmtCase.setString(8, quoteItemString); // quote_item_content
                    pstmtCase.setString(9, tmpCmsMapping); // cms_item_mapping
                    pstmtCase.setString(10, tmpSubrNum); // subr_num
                    pstmtCase.setString(11, tmpCustNum); // cust_num
                    pstmtCase.setString(12, tmpAcctNum); // acct_num
                    pstmtCase.executeUpdate();
                    pstmtCase.close();

                    // create item records
                    epcOrderHandler.createEPCItemRecords(targetOrderId, targetQuoteId, tmpCaseId, targetQuoteItem.getProductCandidateObj(), "", pstmtItem, pstmtVoucher);
                    pstmtItem.executeBatch();
                    pstmtItem.close();

                    // create salesman action
                    remarks = "Add " + tmpCaseId + " [sigmaItemId:" + targetQuoteItem.getId() + "] to quote " + targetQuoteGuid;
                    epcSalesmanHandler.createSalesmanLog(conn, targetOrderId, tmpCaseId, createUser, createSalesman, createLocation, createChannel, epcSalesmanHandler.actionAddQuoteItem, remarks);
                } else {
                    throw new Exception(epcAddProductToQuoteResult.getErrMsg());
                }
                // end of add tmp package & product candidate to target quote

                // delete tmp quote item from tmp quote
                epcDeleteSigmaItemFromQuote = new EpcDeleteSigmaItemFromQuote();
                epcDeleteSigmaItemFromQuote.setCustId(tmpCustId);
                epcDeleteSigmaItemFromQuote.setOrderId(tmpOrderId);
                epcDeleteSigmaItemFromQuote.setQuoteId(tmpQuoteId);
                epcDeleteSigmaItemFromQuote.setSigmaItemId(tmpQuoteItemGuid);
                epcDeleteSigmaItemFromQuote.setDeleteUser(createUser);
                epcDeleteSigmaItemFromQuote.setDeleteSalesman(createSalesman);
                epcDeleteSigmaItemFromQuote.setDeleteChannel(createChannel);
                epcDeleteSigmaItemFromQuote.setDeleteLocation(createLocation);

logger.info("{}{}{}", logStr, "delete tmpQuoteItemGuid:", tmpQuoteItemGuid);
                epcDeleteQuoteItemHandler.deleteSigmaItemFromQuote(conn, epcDeleteSigmaItemFromQuote);
logger.info("{}{}{}", logStr, "result:", epcDeleteSigmaItemFromQuote.getResult());
logger.info("{}{}{}", logStr, "errMsg:", epcDeleteSigmaItemFromQuote.getErrMsg());
                // end of delete tmp quote item from tmp quote


                conn.commit();
logger.info("{}{}", logStr, "done");

                epcMoveQuoteItemToOtherQuote.setResult("SUCCESS");
            } else {
                epcMoveQuoteItemToOtherQuote.setResult("FAIL");
                epcMoveQuoteItemToOtherQuote.setErrMsg(errMsgSb.toString());
            }
        } catch(Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            epcMoveQuoteItemToOtherQuote.setResult("FAIL");
            epcMoveQuoteItemToOtherQuote.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

    }
    
}
