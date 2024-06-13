package epc.epcsalesapi.sales;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcConfiguredValue;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcOrderItem;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.EpcQuoteItemForUpdate;
import epc.epcsalesapi.sales.bean.EpcQuoteProductCandidate;
import epc.epcsalesapi.sales.bean.EpcUpdateModifiedItemToQuoteResult;
import epc.epcsalesapi.sales.bean.EpcUpdateQuoteItem;
import epc.epcsalesapi.vakaCms.VakaCmsHandler;
import epc.epcsalesapi.vakaCms.bean.VakaCmsProduct;

@Service
public class EpcUpdateQuoteItemHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcUpdateQuoteItemHandler.class);

    private DataSource epcDataSource;
    private EpcOrderHandler epcOrderHandler;
    private EpcQuoteHandler epcQuoteHandler;
    private EpcSalesmanHandler epcSalesmanHandler;
    private EpcOrderItemSql epcOrderItemSql;
    private VakaCmsHandler vakaCmsHandler;
    private EpcSecurityHelper epcSecurityHelper;
    private EpcSaRemarkHandler epcSaRemarkHandler;
    private EpcTncHandler epcTncHandler;


    public EpcUpdateQuoteItemHandler(
        DataSource epcDataSource, EpcOrderHandler epcOrderHandler,
        EpcQuoteHandler epcQuoteHandler, EpcSalesmanHandler epcSalesmanHandler, EpcOrderItemSql epcOrderItemSql,
        VakaCmsHandler vakaCmsHandler, EpcSecurityHelper epcSecurityHelper, EpcSaRemarkHandler epcSaRemarkHandler,
        EpcTncHandler epcTncHandler
    ) {
        this.epcDataSource = epcDataSource;
        this.epcOrderHandler = epcOrderHandler;
        this.epcQuoteHandler = epcQuoteHandler;
        this.epcSalesmanHandler = epcSalesmanHandler;
        this.epcOrderItemSql = epcOrderItemSql;
        this.vakaCmsHandler = vakaCmsHandler;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcSaRemarkHandler = epcSaRemarkHandler;
        this.epcTncHandler = epcTncHandler;
    }


    public EpcUpdateQuoteItem updateProductToQuote(EpcUpdateQuoteItem epcUpdateQuoteItem) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtItem = null;
        PreparedStatement pstmtItem2 = null;
        PreparedStatement pstmtVoucher = null;
        ResultSet rset = null;
        String sql = "";
        String orderReference = "";
        String custId = "";
        int orderId = 0;
        int quoteId = 0;
        String quoteGuid = "";
        boolean isValid = true;
        String errMsg = "";
        String updateUser = "";
        String updateSalesman = "";
        String updateChannel = "";
        String updateLocation = "";
        String sigmaItemId;
        String itemAction = "";
        EpcQuoteItem epcQuoteItem = null;
        EpcQuoteItemForUpdate epcQuoteItemForUpdate = null;
        HashMap<String, Object> productCandidateMap = null;
        HashMap<String, Object> previousProductCandidateMap = null;
        HashMap<String, Object> cmsItemMappingMap = null;
        String cmsItemMappingString = "";
        EpcUpdateModifiedItemToQuoteResult epcUpdateModifiedItemToQuoteResult = null;
        EpcQuoteProductCandidate epcQuoteProductCandidateObj = null;
        String remarks = "";
        String caseId = "";
        HashMap<String, Object> tmpMap = null;
        String quoteItemString = "";
        HashMap<String, String> tmpReserveIdMap = new HashMap<>();
        HashMap<String, String> tmpPickupDateMap = new HashMap<>();
        HashMap<String, String> tmpStockStatusMap = new HashMap<>();
        Iterator iiiii = null;
        String tmpItemId = "";
        ArrayList<EpcOrderItem> itemListForDesc = new ArrayList<>();
        EpcOrderItem epcOrderItem = null;
        String subrNum = "";
        String custNum = "";
        String acctNum = "";
        String offerGuid = "";
        VakaCmsProduct vakaCmsProduct = null;
        String offerDesc = "";
        String offerDescChi = "";
        String logStr = "[updateProductToQuote]";
        String tmpLogStr = "";
        
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            custId = StringHelper.trim(epcUpdateQuoteItem.getCustId());
            orderId = epcUpdateQuoteItem.getOrderId();
            quoteId = epcUpdateQuoteItem.getQuoteId();
            sigmaItemId = StringHelper.trim(epcUpdateQuoteItem.getSigmaItemId());
            itemAction = StringHelper.trim(epcUpdateQuoteItem.getItemAction());
            if("".equals(itemAction)) {
                itemAction = "add"; // default
            }
            productCandidateMap = epcUpdateQuoteItem.getProductCandidate();
            updateUser = StringHelper.trim(epcUpdateQuoteItem.getUpdateUser());
            updateSalesman = StringHelper.trim(epcUpdateQuoteItem.getUpdateSalesman());
            updateChannel = StringHelper.trim(epcUpdateQuoteItem.getUpdateChannel());
            updateLocation = StringHelper.trim(epcUpdateQuoteItem.getUpdateLocation());
            cmsItemMappingMap = epcUpdateQuoteItem.getCmsItemMapping();
            
            logStr += "[custId:" + custId + "][orderId:" + orderId + "][quoteId:" + quoteId + "][sigmaItemId:" + sigmaItemId + "] ";

            // basic checking
            orderReference = epcOrderHandler.isOrderBelongCust(custId, orderId);
            if("NOT_BELONG".equals(orderReference)) {
                errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
                isValid = false;
            }

            if(epcOrderHandler.isOrderLocked(conn, custId, orderId)) {
                errMsg += "input order [" + orderId + "] is locked. ";
                isValid = false;
            }
            
            quoteGuid = epcOrderHandler.getCurrentQuoteGuid(orderId, quoteId);
            if("".equals(quoteGuid)) {
                errMsg += "input order/quote id [" + orderId + "/" + quoteId + "] is not valid. ";
                isValid = false;
            } 
            // end of basic checking

            
            if(isValid) {
//                epcQuoteItem = epcQuoteHandler.getQuoteItem(quoteGuid, sigmaItemId, "");
                epcQuoteItem = epcOrderHandler.getQuoteItemInEpc(orderId, quoteId, quoteGuid, sigmaItemId);
                if(epcQuoteItem == null) {
                    throw new Exception("quote item is not found");
                } else {
                    // backup previous candidate
                    previousProductCandidateMap = epcQuoteItem.getProductCandidate();

                    epcQuoteItemForUpdate = new EpcQuoteItemForUpdate();
                    epcQuoteItemForUpdate.setProductId(epcQuoteItem.getProductId());
                    epcQuoteItemForUpdate.setLinkedItemId(epcQuoteItem.getLinkedItemId());
                    epcQuoteItemForUpdate.setItemAction(itemAction);
                    epcQuoteItemForUpdate.setProductCandidate(productCandidateMap); // use new, input product candidate
                    epcQuoteItemForUpdate.setMetaTypeLookup(epcQuoteItem.getMetaTypeLookup());
                    epcQuoteItemForUpdate.setName(epcQuoteItem.getName());
                    epcQuoteItemForUpdate.setPrePricedCandidate(epcQuoteItem.getPrePricedCandidate());
                    epcQuoteItemForUpdate.setMetaDataLookup(epcQuoteItem.getMetaDataLookup());
                    epcQuoteItemForUpdate.setId(epcQuoteItem.getId());
                    epcQuoteItemForUpdate.setItemNumber(epcQuoteItem.getItemNumber());
                    epcQuoteItemForUpdate.setCreated(epcQuoteItem.getCreated());
                    epcQuoteItemForUpdate.setDescription(epcQuoteItem.getDescription());
                    epcQuoteItemForUpdate.setSupersededById(epcQuoteItem.getSupersededById());
                    epcQuoteItemForUpdate.setSupersededFromId(epcQuoteItem.getSupersededFromId());
                    epcQuoteItemForUpdate.setDecorators(epcQuoteItem.getDecorators());
                    epcQuoteItemForUpdate.setHonourExistingPrice(false);
                    epcQuoteItemForUpdate.setPortfolioItemId(epcQuoteItem.getPortfolioItemId());
                    epcQuoteItemForUpdate.setPortfolioItem(epcQuoteItem.getPortfolioItem());
                }
                

                epcUpdateModifiedItemToQuoteResult = epcQuoteHandler.updateModifiedItemToQuote(orderId, quoteGuid, sigmaItemId, epcQuoteItemForUpdate);
                tmpLogStr = "epcUpdateModifiedItemToQuote result:" + epcSecurityHelper.encodeForSQL(epcUpdateModifiedItemToQuoteResult.getResult()) + 
                            ",errMsg:" + epcSecurityHelper.encodeForSQL(epcUpdateModifiedItemToQuoteResult.getErrMsg()) +
                            ",errMsg2:" + epcUpdateModifiedItemToQuoteResult.getErrMsg2();
logger.info("{}{}", logStr, tmpLogStr);

                if("SUCCESS".equals(epcUpdateModifiedItemToQuoteResult.getResult())) {
                    // create records in epc_order_item
                    epcQuoteItem = epcUpdateModifiedItemToQuoteResult.getEpcQuoteItem(); // used the new item
                    tmpMap = epcQuoteItem.getProductCandidate();
                    caseId = StringHelper.trim((String)tmpMap.get("ID"));
                    epcQuoteProductCandidateObj = epcQuoteItem.getProductCandidateObj();

                    // get subrNum and custNum from configuredValue of offer package, which SHOULD be the root of productCandidate
                    // path: EpcQuote -> EpcQuoteItem -> productCandidateObj -> EpcQuoteProductCandidate -> configuredValue -> 
                    // name = "msisdn" / "Customer_Number" / "Billing_Account_Number"
                    ArrayList<EpcConfiguredValue> configuredValueList = epcQuoteProductCandidateObj.getConfiguredValue();
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

                    quoteItemString = new ObjectMapper().writeValueAsString(epcQuoteItem);
                    // update quote item content
                    sql = "update epc_order_case " +
                          "   set quote_item_content = ?, " +
                          "       subr_num = ?, " +
                          "       cust_num = ?, " +
                          "       acct_num = ? " +
                          " where order_id = ? " +
                          "   and quote_id = ? " +
                          "   and quote_item_guid = ? ";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, quoteItemString); // quote_item_content
                    pstmt.setString(2, subrNum); // subr_num
                    pstmt.setString(3, custNum); // cust_num
                    pstmt.setString(4, acctNum); // acct_num
                    pstmt.setInt(5, orderId); // order_id
                    pstmt.setInt(6, quoteId); // quote_id
                    pstmt.setString(7, sigmaItemId); // quote_item_guid
                    pstmt.executeUpdate();
                    // end of update quote item content

                    // update cms mapping
                    if(cmsItemMappingMap != null) {
                        cmsItemMappingString = new ObjectMapper().writeValueAsString(cmsItemMappingMap);

                        sql = "update epc_order_case " +
                              "   set cms_item_mapping = ? " +
                              " where order_id = ? " +
                              "   and quote_id = ? " +
                              "   and quote_item_guid = ? ";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, cmsItemMappingString); // quote_item_content
                        pstmt.setInt(2, orderId); // order_id
                        pstmt.setInt(3, quoteId); // quote_id
                        pstmt.setString(4, sigmaItemId); // quote_item_guid
                        pstmt.executeUpdate();
                    }
                    // end of update cms mapping

                    // get previous reserve no / pickup date / stock status
                    sql = "select item_id, reserve_id, pickup_date, stock_status " +
                          "  from epc_order_item " +
                          " where order_id = ? " +
                          "   and case_id = ? " +
                          "   and item_cat = ? ";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, orderId); // order_id
                    pstmt.setString(2, caseId); // case_id
                    pstmt.setString(3, "DEVICE"); // item_cat
                    rset = pstmt.executeQuery();
                    while(rset.next()) {
                        tmpReserveIdMap.put(StringHelper.trim(rset.getString("item_id")), StringHelper.trim(rset.getString("reserve_id")));
                        tmpPickupDateMap.put(StringHelper.trim(rset.getString("item_id")), StringHelper.trim(rset.getString("pickup_date")));
                        tmpStockStatusMap.put(StringHelper.trim(rset.getString("item_id")), StringHelper.trim(rset.getString("stock_status")));
                    } rset.close();
                    // end of get previous reserve no / pickup date / stock status

                    // delete item records if any
                    sql = "delete from epc_order_item where order_id = ? and case_id = ? ";
                    pstmtItem = conn.prepareStatement(sql);
                    pstmtItem.setInt(1, orderId); // order_id
                    pstmtItem.setString(2, caseId); // case_id
                    pstmtItem.executeUpdate();
                    // end of delete item records if any
                    
                    
                    pstmtVoucher = conn.prepareStatement(epcOrderItemSql.SQL_INSERT_ORDER_VOUCHER);
                    pstmtItem = conn.prepareStatement(epcOrderItemSql.SQL_INSERT_ORDER_ITEM);
                    
                    epcOrderHandler.createEPCItemRecords(orderId, quoteId, caseId, epcQuoteProductCandidateObj, "", pstmtItem, pstmtVoucher);
                    pstmtItem.executeBatch();
                                        
                    // added by Danny Chan on 2023-3-25: start
                    // delete all TnC items first 
                    sql = "delete from epc_order_tnc where order_id = ? and case_id = ?";
                    
                    pstmtItem = conn.prepareStatement(sql);
                    pstmtItem.setInt(1, orderId);
                    pstmtItem.setString(2, caseId);
                    pstmtItem.executeUpdate();
                    
                    ArrayList<String> listOfTnCItem = epcOrderHandler.getListOfItemIdForType(epcQuoteItem.getMetaDataLookup(), "Terms_And_Conditions_Specification");
                    
                    // add back TnC Items 
                    //pstmtItem = conn.prepareStatement(epcOrderItemSql.SQL_INSERT_TNC_ITEM);
                    
                    //epcOrderHandler.createTnCItemRecords(orderId, caseId, "", pstmtItem, epcQuoteProductCandidateObj, listOfTnCItem);
                    //pstmtItem.executeBatch();
                    // added by Danny Chan on 2023-3-25: end
                    // refresh Tnc
                    epcTncHandler.updateOrderOfferTnc(conn, orderId, epcQuoteItem);
                    
                    // refresh SA remark
                    epcSaRemarkHandler.updateOrderSaRemark(conn, orderId, epcQuoteItem);

                    // added by Danny Chan on 2023-3-30: start
                    sql = "delete from epc_order_contract_details_hdr where order_id = ? and case_id = ?";

                    pstmtItem = conn.prepareStatement(sql);
                    pstmtItem.setInt(1, orderId);
                    pstmtItem.setString(2, caseId);
                    pstmtItem.executeUpdate();
                    
                    sql = "delete from epc_order_contract_details_dtl where order_id = ? and case_id = ?";

                    pstmtItem = conn.prepareStatement(sql);
                    pstmtItem.setInt(1, orderId);
                    pstmtItem.setString(2, caseId);
                    pstmtItem.executeUpdate();
                    
                    ArrayList<String> listOfContractDetailsItem = epcOrderHandler.getListOfItemIdForType(epcQuoteItem.getMetaDataLookup(), "Contract_Details_Product_Specification");
                    
                    pstmtItem = conn.prepareStatement(epcOrderItemSql.SQL_INSERT_CONTRACT_DETAILS_HDR_ITEM);
                    pstmtItem2 = conn.prepareStatement(epcOrderItemSql.SQL_INSERT_CONTRACT_DETAILS_DTL_ITEM);
                    
                    epcOrderHandler.createContratDetailsRecords(orderId, caseId, "", pstmtItem, pstmtItem2, epcQuoteProductCandidateObj, listOfContractDetailsItem );
                    
                    pstmtItem.executeBatch();
                    pstmtItem2.executeBatch();
                    // added by Danny Chan on 2023-3-30: end
                    
                    // added by Danny Chan on 2023-4-21 (save BuyBack info): start
                    sql = "delete from epc_order_buyback where order_id = ? and case_id = ?";
                    
                    pstmtItem = conn.prepareStatement(sql);
                    pstmtItem.setInt(1, orderId);
                    pstmtItem.setString(2, caseId);
                    pstmtItem.executeUpdate();
                    
                    ArrayList<String> listOfBuyBackItem = epcOrderHandler.getListOfItemIdForType(epcQuoteItem.getMetaDataLookup(), "Device_Buyback");

                    pstmtItem = conn.prepareStatement(epcOrderItemSql.SQL_INSERT_BUYBACK_ITEM);
                    
                    epcOrderHandler.createBuyBackRecords(orderId, caseId, "", pstmtItem, epcQuoteProductCandidateObj, listOfBuyBackItem );
                    
                    pstmtItem.executeBatch();
                    // added by Danny Chan on 2023-4-21 (save BuyBack info): end
                    
//                    pstmtVoucher.executeBatch(); // pstmtVoucher use executeQuery inside createEPCItemRecords()
                    // end of create records in epc_order_item

                    // update reserve no / pickup date / stock status back
                    if(tmpReserveIdMap.isEmpty()) {
logger.info("{}{}", logStr, "no reserve item to update back");
                    } else {
logger.info("{}{}", logStr, "update reserve item back");
                        sql = "update epc_order_item " +
                            "   set reserve_id = ?, pickup_date = ?, stock_status = ? " +
                            " where order_id = ? " +
                            "   and case_id = ? " +
                            "   and item_id = ? ";
                        pstmt = conn.prepareStatement(sql);
                        iiiii = tmpReserveIdMap.keySet().iterator();
                        while(iiiii.hasNext()) {
                            tmpItemId = (String)iiiii.next();

                            tmpLogStr = "item " + tmpItemId + " -> reserve id:" + tmpReserveIdMap.get(tmpItemId);
logger.info("{}{}", logStr, tmpLogStr);

                            pstmt.setString(1, tmpReserveIdMap.get(tmpItemId)); // reserve_id
                            pstmt.setString(2, tmpPickupDateMap.get(tmpItemId)); // pickup_date
                            pstmt.setString(3, tmpStockStatusMap.get(tmpItemId)); // stock_status
                            pstmt.setInt(4, orderId); // order_id
                            pstmt.setString(5, caseId); // case_id
                            pstmt.setString(6, tmpItemId); // item_id
                            pstmt.addBatch();
                        }
                        pstmt.executeBatch();
                    }
                    // end of update reserve no / pickup date / stock status back
                    
                    // create salesman log
                    remarks = "update " + caseId + " to " + quoteGuid;
                    epcSalesmanHandler.createSalesmanLog(conn, orderId, caseId, updateUser, updateSalesman, updateLocation, updateChannel, epcSalesmanHandler.actionUpdateQuoteItem, remarks);
                    // end of create salesman log
                    
                    epcUpdateQuoteItem.setResult("SUCCESS");
                    epcUpdateQuoteItem.setProductCandidate(null);
                    epcUpdateQuoteItem.setEpcQuoteItem(epcUpdateModifiedItemToQuoteResult.getEpcQuoteItem());
                    
                    conn.commit();


                    // get product / offer desc from Vaka cms, kerrytsang, 20231116
                    sql = "select item_id, item_code " +
                          "  from epc_order_item " +
                          " where order_id = ? " +
                          "   and case_id = ? " +
                          "   and item_cat in (?,?,?) ";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, orderId); // order_id
                    pstmt.setString(2, caseId); // case_id
                    pstmt.setString(3, EpcItemCategory.DEVICE); // item_cat - DEVICE
                    pstmt.setString(4, EpcItemCategory.SIM); // item_cat - SIM
                    pstmt.setString(5, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
                    rset = pstmt.executeQuery();
                    while(rset.next()) {
                        epcOrderItem = new EpcOrderItem();
                        epcOrderItem.setItemId(StringHelper.trim(rset.getString("item_id")));
                        epcOrderItem.setProductCode(StringHelper.trim(rset.getString("item_code")));

                        itemListForDesc.add(epcOrderItem);
                    } rset.close();

                    sql = "select cpq_item_guid " +
                          "  from epc_order_item " +
                          " where order_id = ? " +
                          "   and case_id = ? " +
                          "   and parent_item_id is null ";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setInt(1, orderId); // order_id
                    pstmt.setString(2, caseId); // case_id
                    rset = pstmt.executeQuery();
                    if(rset.next()) {
                        offerGuid = StringHelper.trim(rset.getString("cpq_item_guid"));
                    } rset.close();
                    
                    epcOrderHandler.getAndUpdateProductDescFromVakaCms(orderId, itemListForDesc, caseId, offerGuid);
                    // end of get product / offer desc from Vaka cms, kerrytsang, 20231116
                } else {
                    epcUpdateQuoteItem.setResult("FAIL");
                    epcUpdateQuoteItem.setErrMsg(epcUpdateModifiedItemToQuoteResult.getErrMsg());
                    epcUpdateQuoteItem.setErrMsg2(epcUpdateModifiedItemToQuoteResult.getErrMsg2());

                    // rollback to previous candidate
                    if(epcUpdateQuoteItem.getErrMsg2() != null) {
                        // cater currentvalidation = false
                        epcQuoteItemForUpdate.setProductCandidate(previousProductCandidateMap); // use previous product candidate
                        epcUpdateModifiedItemToQuoteResult = epcQuoteHandler.updateModifiedItemToQuote(orderId, quoteGuid, sigmaItemId, epcQuoteItemForUpdate);

                        tmpLogStr = "rollback quote item, result:" + epcSecurityHelper.encodeForSQL(epcUpdateModifiedItemToQuoteResult.getResult()) + 
                                    ",errMsg:" + epcSecurityHelper.encodeForSQL(epcUpdateModifiedItemToQuoteResult.getErrMsg()) +
                                    ",errMsg2:" + epcUpdateModifiedItemToQuoteResult.getErrMsg2();
logger.info("{}{}", logStr, tmpLogStr);
                    }
                    // end of rollback to previous candidate
                }
            } else {
                epcUpdateQuoteItem.setResult("FAIL");
                epcUpdateQuoteItem.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            epcUpdateQuoteItem.setResult("FAIL");
            epcUpdateQuoteItem.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return epcUpdateQuoteItem;
    }
}
