/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.crm.EpcCustomerHandler;
import epc.epcsalesapi.helper.DBHelper;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.preorder.EpcPreorderHandler;
import epc.epcsalesapi.preorder.bean.EpcPreorder;
import epc.epcsalesapi.sales.bean.EpcCreateInvoice;
import epc.epcsalesapi.sales.bean.EpcCreateInvoiceResult;
import epc.epcsalesapi.sales.bean.EpcDoaInvoice;
import epc.epcsalesapi.sales.bean.EpcInvoice;
import epc.epcsalesapi.sales.bean.EpcInvoiceItem;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
//import epc.epcsalesapi.sales.bean.EpcInvoicingEAppleCareBean;       // added by Danny Chan on 2021-6-25 (Apple Care enhancement)
import epc.epcsalesapi.sales.bean.EpcOrderItem;
import epc.epcsalesapi.sales.bean.cancelOrder.EpcCancelOrder;
import epc.epcsalesapi.stock.EpcStockHandler;
import epc.epcsalesapi.stock.bean.EpcDeductStockResult;
import epc.epcsalesapi.stock.bean.EpcDoaStock;
import epc.epcsalesapi.stock.bean.EpcDoaStockResult;
import epc.epcsalesapi.stock.bean.EpcUpdateStock;
//import io.lettuce.core.OrderingReadFromAccessor;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author KerryTsang
 */

@Service
public class EpcInvoiceHandler {

    private final Logger logger = LoggerFactory.getLogger(EpcInvoiceHandler.class);

    @Autowired
    private DataSource fesDataSource;

    @Autowired
    private DataSource crmFesDataSource;

    @Autowired
    private DataSource epcDataSource;

    @Autowired
    private EpcAppleCareHandler epcAppleCareHandler;
	
	@Autowired
	private EpcScreenReplaceHandler epcScreenReplaceHandler;
	
	@Autowired
	private EpcStockHandler epcStockHandler;
	
	@Autowired
	private EpcSecurityHelper epcSecurityHelper;

    @Autowired
    private EpcWaivingReportHandler epcWaivingReportHandler;

    @Autowired
    private EpcOrderAttrHandler epcOrderAttrHandler;

    @Autowired
    private EpcPreorderHandler epcPreorderHandler;

    @Autowired
    private EpcCustomerHandler epcCustomerHandler;

	
    public EpcCreateInvoiceResult createInvoice(EpcCreateInvoice epcCreateInvoice) {
        ArrayList<EpcInvoice> invoiceList = epcCreateInvoice.getInvoiceList();
        EpcCreateInvoiceResult epcCreateInvoiceResult = new EpcCreateInvoiceResult();
        epcCreateInvoiceResult.setInvoiceList(invoiceList);
        EpcInvoice epcInvoice = null;
        ArrayList<EpcInvoiceItem> invoiceItems = null;
        EpcInvoiceItem epcInvoiceItem = null;
        Connection conn = null;
        Connection crmConn = null;
        Connection epcConn = null;
        String orderIdTxt = "";
        int orderId = 0;
        String caseId = "";
        String itemId = "";
        String orderLang = "";
        String custId = "";
        String hkidBr = "";
        String custNum = "";
        String subrNum = "";
        String location = "";
        String createUser = "";
        String salesman = "";
        int salesmanUserid = 0;
        String offerId = "";
        String offerDesc = "";
        String offerDescChi = "";
        String invoiceNo = "";
        String parentProductCode = "";
        String parentProductImei = "";
        String despatchNo = "";
        PreparedStatement pstmtInvNo = null;
//        PreparedStatement pstmtInvHdr = null;
//        PreparedStatement pstmtInvDtl = null;
        PreparedStatement pstmtDespatchNo  = null;
        ResultSet rset = null;
        String sql = "";
        EpcDeductStockResult epcDeductStockResult = null;
        EpcUpdateStock epcUpdateStock = null;
        TreeMap<String, String> orderAttrMap = null;
        String preorderCaseIdStr = "";
        int preorderCaseId = 0;
        EpcPreorder epcPreorder = null;
        String logStr = "[createInvoice]";
        String tmpLogStr = "";
        
        try {
            conn = fesDataSource.getConnection();
            conn.setAutoCommit(false);
            
            crmConn = crmFesDataSource.getConnection();
            crmConn.setAutoCommit(false);

            epcConn = epcDataSource.getConnection();

            
            // prepare statement
//            sql = "insert into zz_pinv_hdg ( " +
//                 "  bin_number, invoice_no, invoice_date, user_id, salesman, " +
//                 "  time_issued, order_id, cust_id, offer_id, offer_desc, " +
//                 "  offer_desc_chi, subscriber, cellular, despatch_no " +
//                 ") values ( " +
//                 "  ?,?,trunc(sysdate),?,?, " +
//                 "  to_char(sysdate,'hh24miss'),?,?,?,?, " +
//                 "  ?,?,?,? " +
//                 ") ";
//            pstmtInvHdr = conn.prepareStatement(sql);
//
//            sql = "insert into zz_pinv_dtl ( " +
//                    "  invoice_no, line_seq, warehouse, prod_serv_code, item_desc, " +
//                    "  item_desc_chi, imei_sim, reserve_id, item_id, quantity, " + 
//                    "  epc_net_amt, epc_dis_amt " +
//                    ") values ( " +
//                    "  ?,?,?,?,?, " +
//                    "  ?,?,?,?,?, " +
//                    "  ?,? " +
//                    ") ";
//            pstmtInvDtl = conn.prepareStatement(sql);
            
            // tmp use, need to discuss, kerry, 20200603
//            sql = "select pos_lib.psmt_gettxref2('I', 'N') from dual ";
            sql = "select 'E' || lpad(epc_inv_no_seq.nextval,9,0) from dual ";
            pstmtInvNo = epcConn.prepareStatement(sql);
            // end of prepare statement
            
//            sql = "select pos_lib.psmt_gettxref2('D', 'N') from dual ";
            sql = "select 'EE' || lpad(epc_inv_despatch_no_seq.nextval,8,0) from dual";
            pstmtDespatchNo = epcConn.prepareStatement(sql);


            for (int i = 0; i < invoiceList.size(); i++) {
                epcInvoice = invoiceList.get(i);
                orderIdTxt = epcInvoice.getOrderId();
                if(!"".equals(orderIdTxt)) {
                    orderId = Integer.parseInt(orderIdTxt);
                }
                orderLang = epcInvoice.getOrderLang();
                custId = epcInvoice.getCustId();
//                hkidBr = epcCustomerHandler.getHkidByCustId(custId);
                hkidBr = epcCustomerHandler.getHkidByCustIdSlim(custId);
                if("".equals(hkidBr)) {
                    hkidBr = "00000000";
                }

                tmpLogStr = "[orderId:" + orderId + "][custId:" + custId + "] " + hkidBr.substring(0,2);
logger.info("{}{}", logStr, tmpLogStr);

                custNum = StringHelper.trim(epcInvoice.getCustNum());
                if("".equals(custNum)) {
                    custNum = "00000000";
                }
                subrNum = StringHelper.trim(epcInvoice.getSubrNum());
                if("".equals(subrNum)) {
                    subrNum = "00000000";
                }
                location = epcSecurityHelper.encode(epcInvoice.getLocation());
                createUser = epcSecurityHelper.encode(epcInvoice.getCreateUser());
                salesman = epcSecurityHelper.encode(epcInvoice.getSalesmanCode());
                salesmanUserid = getUserid(conn, salesman);
                offerId = epcSecurityHelper.encode(epcInvoice.getOfferId());
                offerDesc = epcSecurityHelper.encode(epcInvoice.getOfferDesc());
                offerDescChi = epcSecurityHelper.encode(epcInvoice.getOfferDescChi());
                invoiceItems = epcInvoice.getItems();

                orderAttrMap = epcOrderAttrHandler.getAttrValuesWithItemIds(epcConn, orderId, epcOrderAttrHandler.ATTR_TYPE_PREORDER_CASE_ID);
                
                // generate invoice no
                rset = pstmtInvNo.executeQuery();
                if(rset.next()) {
                    invoiceNo = epcSecurityHelper.validateId(StringHelper.trim(rset.getString(1)));
                } rset.close();
                // end of generate invoice no
                
                // generate depatch no
                rset = pstmtDespatchNo.executeQuery();
                if(rset.next()) {
                	despatchNo = epcSecurityHelper.validateId(StringHelper.trim(rset.getString(1)));
                } rset.close();
                // end of generate depatch no
                

                // create inv hdr
//                pstmtInvHdr.setString(1, location); // bin_number
//                pstmtInvHdr.setString(2, invoiceNo); // invoice_no
//                pstmtInvHdr.setString(3, createUser); // user_id
//                pstmtInvHdr.setString(4, salesman); // salesman
//                pstmtInvHdr.setString(5, orderId); // order_id
//                pstmtInvHdr.setString(6, custId); // cust_id
//                pstmtInvHdr.setString(7, offerId); // offer_id
//                pstmtInvHdr.setString(8, offerDesc); // offer_desc
//                pstmtInvHdr.setString(9, offerDescChi); // offer_desc_chi
//                pstmtInvHdr.setString(10, custNum); // subscriber
//                pstmtInvHdr.setString(11, subrNum); // cellular
//                pstmtInvHdr.setString(12, despatchNo); // despatch_no
//
//                pstmtInvHdr.addBatch();
//logger.info(logStr + "[" + invoiceNo + "] " + "create inv hdr " + location + "," + despatchNo);
                // end of create inv hdr
                
                                
                for (int ii = 0; ii < invoiceItems.size(); ii++) {
                    epcInvoiceItem = invoiceItems.get(ii);

                    itemId = epcInvoiceItem.getItemId();
                    caseId = getCaseIdByItemId(epcConn, orderId, itemId);


                    // rollback preorder record (with dummy invoice one)
                    preorderCaseIdStr = StringHelper.trim(orderAttrMap.get(itemId));
                    if(!"".equals(preorderCaseIdStr)) {
                        preorderCaseId = Integer.parseInt(preorderCaseIdStr);

                        // resume preorder record with dummy invoice no
                        epcPreorder = new EpcPreorder();
                        epcPreorder.setOrderId(orderId);
                        epcPreorder.setCaseId(caseId);
                        epcPreorder.setItemId(itemId);
                        epcPreorder.setInvoiceNo("");
                        epcPreorder.setCreateUser(epcInvoice.getCreateUser());
                        epcPreorder.setCreateSalesman(epcInvoice.getSalesmanCode());
                        epcPreorder.setCreateChannel("");
                        epcPreorder.setCreateLocation(epcInvoice.getLocation());

                        epcPreorderHandler.resumePreorderRecord(epcPreorder);
                        tmpLogStr = "[orderId:" + orderId + "][itemId:" + itemId + "]" + 
                                    "[" + invoiceNo + "][" + epcInvoiceItem.getItemCode() + "] " + 
                                    "rollback preorder record (with dummy invoice)" + 
                                    ",preorderCaseId:" + preorderCaseIdStr +
                                    ",result:" + epcPreorder.getResult() +
                                    ",errMsg:" + epcPreorder.getErrMsg();
logger.info("{}{}", logStr, tmpLogStr);
                        // end of resume preorder record with dummy invoice no
                    }
                    // end of consume preorder record


                    epcInvoiceItem.setInvoiceNo(invoiceNo);
                    
                    // transfer stock (ERP api)
                    epcUpdateStock = new EpcUpdateStock();
                    epcUpdateStock.setReserveId(epcInvoiceItem.getReserveId());
                    epcUpdateStock.setSubrNum(subrNum);
                    epcUpdateStock.setHkidBr(hkidBr);
                    epcUpdateStock.setWarehouse(epcInvoiceItem.getWarehouse());
                    epcUpdateStock.setProductCode(epcInvoiceItem.getItemCode());
                    epcUpdateStock.setQty(1);
                    epcUpdateStock.setPickupLoc(location);
                    epcUpdateStock.setImei(epcInvoiceItem.getItemValue());
                    epcUpdateStock.setCreateUser(createUser);
                    epcUpdateStock.setCreateSalesman(salesman);
                    epcUpdateStock.setInvoiceNo(invoiceNo);
                    epcUpdateStock.setMovementComment("POS INVOICE# : " + invoiceNo);
                    epcUpdateStock.setReferNo(despatchNo);
                    if(EpcItemCategory.SCREEN_REPLACE.equals(epcInvoiceItem.getItemCat())
                      || EpcItemCategory.APPLECARE.equals(epcInvoiceItem.getItemCat())
                      || EpcItemCategory.SIM.equals(epcInvoiceItem.getItemCat())
                      || EpcItemCategory.PLASTIC_BAG.equals(epcInvoiceItem.getItemCat())
                      || EpcItemCategory.GIFT_WRAPPING.equals(epcInvoiceItem.getItemCat())
                      || "".equals(epcInvoiceItem.getReserveId())
                    ) {
                    	epcUpdateStock.setCheckBy("OTHER");
                    } else {
                    	// normal product
// advised by Connie on 20 Jan 2023 email, modified to use RESERVE_ID, kerrytsang, 20230120
//                    	epcUpdateStock.setCheckBy("ONLINE_ID");
                        epcUpdateStock.setCheckBy("RESERVE_ID");
// end of advised by Connie on 20 Jan 2023 email, modified to use RESERVE_ID, kerrytsang, 20230120
                    }

                    tmpLogStr = "[orderId:" + orderId + "][itemId:" + itemId + "]" + "[" + invoiceNo + "][" + epcInvoiceItem.getItemCode() + "] " + "deduct stock input:" + epcUpdateStock.toString();
logger.info("{}{}", logStr, tmpLogStr);
                    
                    epcDeductStockResult = epcStockHandler.deductStock(epcUpdateStock);
                    if(!"SUCCESS".equals(epcDeductStockResult.getResult())) {
                        // consume preorder record
                        if(!"".equals(preorderCaseIdStr)) {
                            // consume again with real fulfillment reference
                            epcPreorder = new EpcPreorder();
                            epcPreorder.setOrderId(orderId);
                            epcPreorder.setCaseId(caseId);
                            epcPreorder.setItemId(itemId);
                            epcPreorder.setPreorderCaseId(preorderCaseId);
                            epcPreorder.setInvoiceNo("");
                            epcPreorder.setCreateUser(epcInvoice.getCreateUser());
                            epcPreorder.setCreateSalesman(epcInvoice.getSalesmanCode());
                            epcPreorder.setCreateChannel("");
                            epcPreorder.setCreateLocation(epcInvoice.getLocation());

                            epcPreorderHandler.consumePreorderRecord(epcPreorder);
                            tmpLogStr = "[orderId:" + orderId + "][itemId:" + itemId + "]" + 
                                        "[" + invoiceNo + "][" + epcInvoiceItem.getItemCode() + "] " + 
                                        "consume preorder record again (with dummy invoice)" + 
                                        ",preorderCaseId:" + preorderCaseIdStr +
                                        ",result:" + epcPreorder.getResult() +
                                        ",errMsg:" + epcPreorder.getErrMsg();
    logger.info("{}{}", logStr, tmpLogStr);
                            // end of consume again with real fulfillment reference
                        }
                        // end of consume preorder record

                        throw new Exception("deduct stock error. " + epcDeductStockResult.getErrMsg());
                    }
                    // end of transfer stock (ERP api)

                    // create inv dtl
//                    pstmtInvDtl.setString(1, invoiceNo); // invoice_no
//                    pstmtInvDtl.setInt(2, ii + 1); // line_seq
//                    pstmtInvDtl.setString(3, epcInvoiceItem.getWarehouse()); // warehouse
//                    pstmtInvDtl.setString(4, epcInvoiceItem.getItemCode()); // prod_serv_code
//                    pstmtInvDtl.setString(5, epcInvoiceItem.getItemDesc()); // item_desc
//                    pstmtInvDtl.setString(6, epcInvoiceItem.getItemDescChi()); // item_desc_chi
//                    pstmtInvDtl.setString(7, epcInvoiceItem.getItemValue()); // imei_sim
//                    pstmtInvDtl.setString(8, epcInvoiceItem.getReserveId()); // reserve_id
//                    pstmtInvDtl.setString(9, epcInvoiceItem.getItemId()); // item id
//                    pstmtInvDtl.setInt(10, 1); // quantity
//                    if(epcInvoiceItem.getNetAmt() != null) {
//                        pstmtInvDtl.setBigDecimal(11, epcInvoiceItem.getNetAmt()); // epc_net_amt
//                    } else {
//                        pstmtInvDtl.setInt(11, 0); // epc_net_amt
//                    }
//                    if(epcInvoiceItem.getDisAmt() != null) {
//                        pstmtInvDtl.setBigDecimal(12, epcInvoiceItem.getDisAmt()); // epc_dis_amt
//                    } else {
//                        pstmtInvDtl.setInt(12, 0); // epc_dis_amt
//                    }
//                    pstmtInvDtl.addBatch();
//logger.info(logStr + "[" + invoiceNo + "][" + epcInvoiceItem.getItemCode() + "] " + "create inv detail ");
                    // end of create inv dtl

                    // create screen replace / applecare record
                    if(EpcItemCategory.SCREEN_REPLACE.equals(epcInvoiceItem.getItemCat())) {
                    	parentProductCode = ""; // reset
                        parentProductImei = ""; // reset
                        
                    	for(EpcInvoiceItem parentItem : invoiceItems) {
                    		if(epcInvoiceItem.getParentItemId().equals(parentItem.getItemId())) {
                    			parentProductCode = parentItem.getItemCode();
                    			parentProductImei = parentItem.getItemValue();
                    			break;
                    		}
                    	}
                    	
                    	epcScreenReplaceHandler.createScreenReplace(crmConn, invoiceNo, custNum, subrNum, parentProductCode, parentProductImei, epcInvoiceItem.getItemCode(), salesmanUserid);
                        tmpLogStr = "[orderId:" + orderId + "][itemId:" + itemId + "]" + "[" + invoiceNo + "][" + epcInvoiceItem.getItemCode() + "] " + "create screen replace";
logger.info("{}{}", logStr, tmpLogStr);
//logger.info(logStr + "[" + invoiceNo + "][" + epcInvoiceItem.getItemCode() + "] " + "create screen replace");
                    } else if(EpcItemCategory.APPLECARE.equals(epcInvoiceItem.getItemCat())) {
                    	parentProductCode = ""; // reset
                        parentProductImei = ""; // reset
                        
                    	for(EpcInvoiceItem parentItem : invoiceItems) {
                    		if(epcInvoiceItem.getParentItemId().equals(parentItem.getItemId())) {
                    			parentProductCode = parentItem.getItemCode();
                    			parentProductImei = parentItem.getItemValue();
                    			break;
                    		}
                    	}
                    	
                        // added by Danny Chan on 2021-6-25 (Apple Care enhancement): start
                        /*EpcInvoicingEAppleCareBean bean = epcAppleCareHandler.getEpcInvoicingEAppleCareBean( conn, custNum, subrNum, parentProductCode, 
                                                                                                             parentProductImei, epcInvoiceItem.getAppleCareFirstName(), epcInvoiceItem.getAppleCareLastName(),
                                                                                                             epcInvoiceItem.getAppleCareEmail() );
                        
                        if (!epcAppleCareHandler.verifyAppleCare(conn, crmConn, bean, invoiceNo)) {
                            throw new Exception("Failed to register AppleCare+ as verification by Apple is failed.");
                        }*/
                        // added by Danny Chan on 2021-6-25 (Apple Care enhancement): end                        

// tmp commented by kerrytsang, 20230914
//                        // added by Danny Chan on 2022-2-4 (Apple Care enhancement): start
//                        if (!epcAppleCareHandler.verifyAppleCare(conn, invoiceNo, custNum, subrNum, parentProductImei, epcInvoiceItem.getAppleCareFirstName(), epcInvoiceItem.getAppleCareLastName(), epcInvoiceItem.getAppleCareEmail(), orderLang) )  {
//                           throw new Exception("Failed to register AppleCare+ as verification by Apple is failed.");
//                        }
//                        // added by Danny Chan on 2022-2-4 (Apple Care enhancement): end
// end of tmp commented by kerrytsang, 20230914
                        
                    	epcAppleCareHandler.createAppleCare(conn, invoiceNo, custNum, subrNum, parentProductCode, parentProductImei, epcInvoiceItem.getAppleCareFirstName(), epcInvoiceItem.getAppleCareLastName(), epcInvoiceItem.getAppleCareEmail(), orderLang);
                        tmpLogStr = "[orderId:" + orderId + "][itemId:" + itemId + "]" + "[" + invoiceNo + "][" + epcInvoiceItem.getItemCode() + "] " + "create applecare";
logger.info("{}{}", logStr, tmpLogStr);
//                        logger.info(logStr + "[" + invoiceNo + "][" + epcInvoiceItem.getItemCode() + "] " + "create applecare");
                    }
                    // end of create screen replace / applecare record


                    // consume preorder record
                    if(!"".equals(preorderCaseIdStr)) {
                        // consume again with real fulfillment reference
                        epcPreorder = new EpcPreorder();
                        epcPreorder.setOrderId(orderId);
                        epcPreorder.setCaseId(caseId);
                        epcPreorder.setItemId(itemId);
                        epcPreorder.setPreorderCaseId(preorderCaseId);
                        epcPreorder.setInvoiceNo(invoiceNo);
                        epcPreorder.setCreateUser(epcInvoice.getCreateUser());
                        epcPreorder.setCreateSalesman(epcInvoice.getSalesmanCode());
                        epcPreorder.setCreateChannel("");
                        epcPreorder.setCreateLocation(epcInvoice.getLocation());

                        epcPreorderHandler.consumePreorderRecord(epcPreorder);
                        tmpLogStr = "[orderId:" + orderId + "][itemId:" + itemId + "]" + 
                                    "[" + invoiceNo + "][" + epcInvoiceItem.getItemCode() + "] " + 
                                    "consume preorder record again (with real fulfillment ref)" + 
                                    ",preorderCaseId:" + preorderCaseIdStr +
                                    ",result:" + epcPreorder.getResult() +
                                    ",errMsg:" + epcPreorder.getErrMsg();
logger.info("{}{}", logStr, tmpLogStr);
                        // end of consume again with real fulfillment reference
                    }
                    // end of consume preorder record
                }
            }
//            pstmtInvHdr.executeBatch();
//            pstmtInvDtl.executeBatch();
            
            conn.commit();
            crmConn.commit();

            epcCreateInvoiceResult.setResult("SUCCESS");
        } catch (Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            try { if(crmConn != null) { crmConn.rollback(); } } catch (Exception ee) {}

            e.printStackTrace();

            epcCreateInvoiceResult.setResult("FAIL");
            epcCreateInvoiceResult.setErrorCode("1000");
            epcCreateInvoiceResult.setErrorMessage(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
            try { if(crmConn != null) { crmConn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(crmConn != null) { crmConn.close(); } } catch (Exception ee) {}
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception ee) {}
        }
        return epcCreateInvoiceResult;
    }
    
    
    /***
     * @param epcDoaInvoice
     * @param doaItemList
     * @param orderId
     * @param approveBy
     * @param waiveFormCode
     * @param isRetainPreOrder
     */
    public void doaInvoice(EpcDoaInvoice epcDoaInvoice, ArrayList<EpcOrderItem> doaItemList, int orderId, String approveBy, String waiveFormCode, boolean isRetainPreOrder) {
        Connection conn = null;
        Connection crmConn = null;
        Connection epcConn = null;
//        PreparedStatement pstmtInvHdr = null;
//        PreparedStatement pstmtInvDtl = null;
        PreparedStatement pstmtReturnRef = null;
        PreparedStatement pstmtReturnHdr = null;
        PreparedStatement pstmtReturnDtl = null;
        PreparedStatement pstmtUpdateDOAReason = null;
        PreparedStatement pstmtUpdateSymptom = null;
		PreparedStatement pstmtDoaLog = null;
        PreparedStatement pstmtOldLocation = null;
        PreparedStatement pstmtDoaLogRecId = null;
        PreparedStatement pstmtGetCustSubrNum = null;
        ResultSet rset = null;
        ResultSet rsetOldLocation = null;
        ResultSet rsetDoaLogRecId = null;
        ResultSet rsetGetCustSubrNum = null;
        String sql = "";
        ArrayList<String> invoiceList = epcDoaInvoice.getInvoiceList();
    	String user = epcDoaInvoice.getUser();
        String salesman = epcDoaInvoice.getSalesman();
        String location = epcDoaInvoice.getLocation();
        String doaLocation = epcDoaInvoice.getDoaLocation();
        int fesUserid = 0;
        int fesSalesmanUserid = 0;
        String returnRef = "";
        EpcDoaStock epcDoaStock = null;
        EpcDoaStockResult epcDoaStockResult = null;
        boolean isVoid = false;
        String tmpImeiSim = "";
        int tmpLineSeq = 0;
        // added by Danny Chan on 2021-6-17 (Apple Care enhancement): start
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        // added by Danny Chan on 2021-6-17 (Apple Care enhancement): end
        String oldLocation = "";
        int doaLogRecId = 0;
        boolean isCreateWaivingReportRecord = false;
        TreeMap<String, String> orderAttrMap = null;
        String preorderCaseIdStr = "";
        int preorderCaseId = 0;
        EpcPreorder epcPreorder = null;
        String caseId = "";
        String itemId = "";
        String logStr = "[doaInvoice]";
        String tmpLogStr = "";
        
        
        try {
            conn = fesDataSource.getConnection();
            conn.setAutoCommit(false);
            
            crmConn = crmFesDataSource.getConnection();
            crmConn.setAutoCommit(false);

            epcConn = epcDataSource.getConnection();
            epcConn.setAutoCommit(false);
            
            fesUserid = getUserid(conn, user);
            fesSalesmanUserid = getUserid(conn, salesman);


            orderAttrMap = epcOrderAttrHandler.getAttrValuesWithItemIds(epcConn, orderId, epcOrderAttrHandler.ATTR_TYPE_PREORDER_CASE_ID);
            
            
//            sql = "update zz_pinv_hdg set stat = ? where invoice_no = ? ";
//            pstmtInvHdr = conn.prepareStatement(sql);
            
//            sql = "select pos_lib.psmt_gettxref2('R', 'N') from dual ";
            sql = "select 'EE' || lpad(epc_inv_despatch_no_seq.nextval,8,0) from dual";
            pstmtReturnRef = epcConn.prepareStatement(sql);
            
//            sql = "insert into zz_pret_hdg ( " + 
//            	  "  bin_number, tx_type, return_ref, tx_date, bin_original, " + 
//            	  "  invoice_no, ret_tot_price, user_id, salesman, time_issued, " + 
//              	  "  doa_reason, doa_reason_desc, stat, fes_userid " + 
//              	  ") " + 
//            	  "  select ?, ?, ?, trunc(sysdate), bin_number, " + 
//            	  "         invoice_no, ?, ?, ?, to_char(sysdate, 'hh24miss'), " + 
//            	  "         ?, ?, ?, ? " + 
//            	  "    from zz_pinv_hdg " + 
//            	  "   where invoice_no = ? ";
            sql = "insert into zz_pret_hdg ( " + 
                  "  bin_number, tx_type, return_ref, tx_date, bin_original, " + 
                  "  invoice_no, ret_tot_price, user_id, salesman, time_issued, " + 
                  "  doa_reason, doa_reason_desc, stat, fes_userid " + 
                  ") values ( " + 
                  "  ?,?,?,trunc(sysdate),?, " +
                  "  ?,?,?,?,to_char(sysdate, 'hh24miss'), " +
                  "  ?,?,?,? " +
                  ") ";
            pstmtReturnHdr = conn.prepareStatement(sql);

//            sql = "insert into zz_pret_dtl ( " + 
//                    "  return_ref, line_seq, warehouse, prod_serv_code, imei_sim, quantity " + 
//                    ") " + 
//                    "  select ?, line_seq, warehouse, prod_serv_code, imei_sim, quantity " + 
//                    "    from zz_pinv_dtl " + 
//                    "   where invoice_no = ? ";
            sql = "insert into zz_pret_dtl ( " + 
                  "  return_ref, line_seq, warehouse, prod_serv_code, imei_sim, " +
                  "  quantity, line_ind " + 
                  ") values ( " + 
                  "  ?,?,?,?,?, " +
                  "  ?,? " +
                  ") ";
            pstmtReturnDtl = conn.prepareStatement(sql);

            sql ="update zz_pret_hdg set DOA_REASON = ?, DOA_REASON_DESC = ? where return_ref = ? ";
            pstmtUpdateDOAReason = conn.prepareStatement(sql);

            sql = "update zz_pret_dtl set symptom = ? where return_ref = ? ";
            pstmtUpdateSymptom = conn.prepareStatement(sql);

			sql = "insert into epc_order_doa_log ( " +
                  "  rec_id, action_type, old_invoice_no, new_invoice_no, return_ref, " +
                  "  transfer_note, location, product_code, serial_num, create_user, " +
                  "  create_salesman, create_date, order_id, approve_by, waive_form_code, " +
                  "  item_id " +
                  ") values ( " +
                  "  ?,?,?,?,?, " +
                  "  ?,?,?,?,?, " +
                  "  ?,sysdate,?,?,?, " +
                  "  ? " +
                  ") ";
            pstmtDoaLog = epcConn.prepareStatement(sql);

            sql = "select location from epc_order_invoice_log where invoice_no = ? ";
            pstmtOldLocation = epcConn.prepareStatement(sql);

            sql = "select epc_order_id_seq.nextval from dual ";
            pstmtDoaLogRecId = epcConn.prepareStatement(sql);

            sql = "select distinct cust_num, subr_num " + 
                  "  from epc_order_item a, epc_order_case b " +
                  " where a.order_id = ? " +
                  "   and a.invoice_no = ? " + 
                  "   and b.order_id = a.order_id " +
                  "   and b.case_id = a.case_id ";
            pstmtGetCustSubrNum = epcConn.prepareStatement(sql);

            ArrayList<String> cancel_applecare_invoice_list = new ArrayList();     // added by Danny Chan on 2024-3-5 (fix bug related to applecare for cancel order api)

            for(String invoiceNo : invoiceList) {
            	// generate return ref
            	rset = pstmtReturnRef.executeQuery();
            	if(rset.next()) {
            		returnRef = epcSecurityHelper.validateId(StringHelper.trim(rset.getString(1)));
            	} rset.close(); rset = null;

                tmpLogStr = "doa invoice:" + invoiceNo + ",returnRef:" + returnRef;
logger.info("{}{}", logStr, tmpLogStr);
            	// generate return ref


                // get original location
                pstmtOldLocation.setString(1, invoiceNo); // invoice_no
                rsetOldLocation = pstmtOldLocation.executeQuery();
                if(rsetOldLocation.next()) {
                    oldLocation = StringHelper.trim(rsetOldLocation.getString("location"));
                }
                rsetOldLocation.close();
                // end of get original location
            	
            	
            	// update stock
            	epcDoaStock = new EpcDoaStock();
            	
                // added by Danny Chan on 2021-6-25 (Apple Care enhancement): start
                boolean hasEAppleCareProduct = false;
                String handset_imei = "";
                /*String new_handset_imei = null;*/
                // added by Danny Chan on 2021-6-25 (Apple Care enhancement): end
                
            	for(EpcOrderItem orderItem : doaItemList) {
                    tmpImeiSim = StringHelper.trim(orderItem.getImeiSim()); // need to pass empty string, requested by pen, 20230324

            		if(invoiceNo.equals(orderItem.getInvoiceNo())) {  
                        // add into main location first
                        epcDoaStock.setAction("ADD");
                        epcDoaStock.setSource("R"); // DOA return
                        epcDoaStock.setReferNo(returnRef);
                        epcDoaStock.setWarehouse(orderItem.getWarehouse());
                        epcDoaStock.setProductCode(orderItem.getProductCode());
                        epcDoaStock.setFromLoc("");
                        epcDoaStock.setToLoc(location);
                        epcDoaStock.setSerialNo(tmpImeiSim);
                        epcDoaStock.setQty("1");
                        epcDoaStock.setCreateBy(user);
                        epcDoaStock.setCreateSalesman(salesman);
                        epcDoaStock.setInvoiceNo("");
                        epcDoaStock.setComment("POS RETURN#  : " + returnRef);
                        epcDoaStock.setBmReserveNo("");
                        epcDoaStock.setRemark1("");
                        epcDoaStock.setRemark2("");
                        epcDoaStock.setRemark3("");

                        epcDoaStockResult = epcStockHandler.doaStock(epcDoaStock);
                        if (!"SUCCESS".equals(epcDoaStockResult.getResult())) {
                            throw new Exception("add stock error. " + epcDoaStockResult.getErrMsg());
                        } else {
                            rsetDoaLogRecId = pstmtDoaLogRecId.executeQuery();
                            if(rsetDoaLogRecId.next()) {
                                doaLogRecId = rsetDoaLogRecId.getInt(1);
                            } rsetDoaLogRecId.close();

                            // create doa log
                            pstmtDoaLog.setInt(1, doaLogRecId); // rec_id
                            pstmtDoaLog.setString(2, "ADD"); // action_type
                            pstmtDoaLog.setString(3, invoiceNo); // old_invoice_no
                            pstmtDoaLog.setString(4, ""); // new_invoice_no
                            pstmtDoaLog.setString(5, returnRef); // return_ref
                            pstmtDoaLog.setString(6, ""); // transfer_note
                            pstmtDoaLog.setString(7, location); // location
                            pstmtDoaLog.setString(8, orderItem.getProductCode()); // product_code
                            pstmtDoaLog.setString(9, orderItem.getImeiSim()); // serial_num
                            pstmtDoaLog.setString(10, user); // create_user
                            pstmtDoaLog.setString(11, salesman); // create_salesman
                            pstmtDoaLog.setInt(12, orderId); // order_id
                            pstmtDoaLog.setString(13, approveBy); // approve_by
                            pstmtDoaLog.setString(14, waiveFormCode); // waive_form_code
                            pstmtDoaLog.setString(14, waiveFormCode); // waive_form_code
                            pstmtDoaLog.setString(15, orderItem.getItemId()); // item_id
                            pstmtDoaLog.addBatch();
                            // end of create doa log

                            // create waiving report record
                            if(!"".equals(approveBy)) {
                                isCreateWaivingReportRecord = epcWaivingReportHandler.createWaivingRecord(epcConn, EpcWaivingReportHandler.WAIVE_TYPE_DOA, doaLogRecId + "");

                                tmpLogStr = "isCreateWaivingReportRecord:" + isCreateWaivingReportRecord;
logger.info("{}{}", logStr, tmpLogStr);
                            }
                            // end of create waiving report record
                        }
                        // end of add into main location first

                        // transfer stock to RP location if need
                        if(!location.equals(doaLocation) && doaLocation.contains("RP")) {
                            epcDoaStock.setAction("TRANSFER");
//                            epcDoaStock.setSource("T"); // transfer
                            epcDoaStock.setSource("X"); // transfer
                            epcDoaStock.setReferNo(returnRef);
                            epcDoaStock.setWarehouse(orderItem.getWarehouse());
                            epcDoaStock.setProductCode(orderItem.getProductCode());
                            epcDoaStock.setFromLoc(location);
                            epcDoaStock.setToLoc(doaLocation);
                            epcDoaStock.setSerialNo(tmpImeiSim);
                            epcDoaStock.setQty("1");
                            epcDoaStock.setCreateBy(user);
                            epcDoaStock.setCreateSalesman(salesman);
                            epcDoaStock.setInvoiceNo("");
                            epcDoaStock.setComment("");
                            epcDoaStock.setBmReserveNo("");
                            epcDoaStock.setRemark1("");
                            epcDoaStock.setRemark2("");
                            epcDoaStock.setRemark3("");
    
                            epcDoaStockResult = epcStockHandler.doaStock(epcDoaStock);
                            if (!"SUCCESS".equals(epcDoaStockResult.getResult())) {
                                throw new Exception("transfer stock error. " + epcDoaStockResult.getErrMsg());
                            } else {
                                rsetDoaLogRecId = pstmtDoaLogRecId.executeQuery();
                                if(rsetDoaLogRecId.next()) {
                                    doaLogRecId = rsetDoaLogRecId.getInt(1);
                                } rsetDoaLogRecId.close();

                                // create doa log
                                pstmtDoaLog.setInt(1, doaLogRecId); // rec_id
                                pstmtDoaLog.setString(2, "TRANSFER"); // action_type
                                pstmtDoaLog.setString(3, invoiceNo); // old_invoice_no
                                pstmtDoaLog.setString(4, ""); // new_invoice_no
                                pstmtDoaLog.setString(5, returnRef); // return_ref
                                pstmtDoaLog.setString(6, StringHelper.trim(epcDoaStockResult.getTransferNote())); // transfer_note
                                pstmtDoaLog.setString(7, doaLocation); // location
                                pstmtDoaLog.setString(8, orderItem.getProductCode()); // product_code
                                pstmtDoaLog.setString(9, tmpImeiSim); // serial_num
                                pstmtDoaLog.setString(10, user); // create_user
                                pstmtDoaLog.setString(11, salesman); // create_salesman
                                pstmtDoaLog.setInt(12, orderId); // order_id
                                pstmtDoaLog.setString(13, approveBy); // approve_by
                                pstmtDoaLog.setString(14, waiveFormCode); // waive_form_code
                                pstmtDoaLog.setString(15, orderItem.getItemId()); // item_id
                                pstmtDoaLog.addBatch();
                                // end of create doa log
                            }
                        }
                        // end of transfer stock to RP location if need

                        
                        // added by Danny Chan on 2021-6-25 (Apple Care enhancement): start
                        if ( epcAppleCareHandler.isEAppleCareProduct(conn, orderItem.getWarehouse(), orderItem.getProductCode()) ) {
                            hasEAppleCareProduct = true;
                        }
                        
// tmp modified by kerrytsang, 20230720
//                        if (orderItem.getWarehouse().equals("AH")) {
//                            handset_imei = tmpImeiSim;
//                            /*new_handset_imei = orderItem.getNewImeiSim();*/
//                        }
                        if (!"".equals(tmpImeiSim)) {
                            handset_imei = tmpImeiSim; // imei for main device
                            /*new_handset_imei = orderItem.getNewImeiSim();*/
                        }
// end of tmp modified by kerrytsang, 20230720
                        // added by Danny Chan on 2021-6-25 (Apple Care enhancement): end


                        // revert preorder record
                        itemId = orderItem.getItemId();
                        caseId = getCaseIdByItemId(epcConn, orderId, itemId);
                        preorderCaseIdStr = StringHelper.trim(orderAttrMap.get(itemId));
                        tmpLogStr = "orderId:" + orderId + ",itemId:" + itemId + 
                                    ",invoiceNo:" + invoiceNo + ",productCode:" + orderItem.getProductCode() + 
                                    ",preorderCaseId:" + preorderCaseIdStr +
                                    ",isRetainPreOrder:" + isRetainPreOrder;
logger.info("{}{}", logStr, tmpLogStr);
                        
                        if(!"".equals(preorderCaseIdStr)) {
                            preorderCaseId = Integer.parseInt(preorderCaseIdStr);

                            // resume preorder record with fulfill reference
                            epcPreorder = new EpcPreorder();
                            epcPreorder.setOrderId(orderId);
                            epcPreorder.setCaseId(caseId);
                            epcPreorder.setItemId(itemId);
                            epcPreorder.setInvoiceNo(invoiceNo);
                            epcPreorder.setCreateUser(user);
                            epcPreorder.setCreateSalesman(salesman);
                            epcPreorder.setCreateChannel("");
                            epcPreorder.setCreateLocation(location);

                            epcPreorderHandler.resumePreorderRecord(epcPreorder);
                            tmpLogStr = "[orderId:" + orderId + "][itemId:" + itemId + "]" + 
                                        "[" + invoiceNo + "][" + orderItem.getProductCode() + "] " + 
                                        "resumePreorderRecord (by fulfill reference)" + 
                                        ",preorderCaseId:" + preorderCaseIdStr +
                                        ",result:" + epcPreorder.getResult() +
                                        ",errMsg:" + epcPreorder.getErrMsg();
logger.info("{}{}", logStr, tmpLogStr);
                            // end of resume preorder record with fulfill reference

                            // consume again with dummy inv no
                            if(isRetainPreOrder) {
                                epcPreorder = new EpcPreorder();
                                epcPreorder.setOrderId(orderId);
                                epcPreorder.setCaseId(caseId);
                                epcPreorder.setItemId(itemId);
                                epcPreorder.setPreorderCaseId(preorderCaseId);
                                epcPreorder.setInvoiceNo("");
                                epcPreorder.setCreateUser(user);
                                epcPreorder.setCreateSalesman(salesman);
                                epcPreorder.setCreateChannel("");
                                epcPreorder.setCreateLocation(location);

                                epcPreorderHandler.consumePreorderRecord(epcPreorder);
                                tmpLogStr = "[orderId:" + orderId + "][itemId:" + itemId + "]" + 
                                            "[" + invoiceNo + "][" + orderItem.getProductCode() + "] " + 
                                            "consume preorder record again (with dummy invoice)" + 
                                            ",preorderCaseId:" + preorderCaseIdStr +
                                            ",result:" + epcPreorder.getResult() +
                                            ",errMsg:" + epcPreorder.getErrMsg();
logger.info("{}{}", logStr, tmpLogStr);
                            }
                            // end of consume again with dummy inv no
                        }
                        // end of revert preorder record
            		}
            	}
            	// end of update stock
            	
            	
//            	// void inv hdr
//            	pstmtInvHdr.setString(1, "V"); // stat - void
//            	pstmtInvHdr.setString(2, invoiceNo); // invoice_no
//            	pstmtInvHdr.addBatch();
//            	// end of void inv hdr

            	// create return hdr
                //  keep to use login current location for return hdr, kerrytsang, 20220308
//            	pstmtReturnHdr.setString(1, location); // bin_number
//            	pstmtReturnHdr.setString(2, "D"); // tx_type
//            	pstmtReturnHdr.setString(3, returnRef); // return_ref
//            	pstmtReturnHdr.setInt(4, 0); // ret_tot_price
//            	pstmtReturnHdr.setString(5, user); // userid
//            	pstmtReturnHdr.setString(6, salesman); // salesman
//            	pstmtReturnHdr.setString(7, ""); // doa_reason
//            	pstmtReturnHdr.setString(8, ""); // doa_reason_desc
//            	pstmtReturnHdr.setString(9, "N"); // doa_reason_desc
//            	pstmtReturnHdr.setInt(10, fesUserid); // fes_userid
//            	pstmtReturnHdr.setString(11, invoiceNo); // invoice_no
//            	pstmtReturnHdr.addBatch();
                pstmtReturnHdr.setString(1, location); // bin_number
                pstmtReturnHdr.setString(2, "D"); // tx_type
                pstmtReturnHdr.setString(3, returnRef); // return_ref
                pstmtReturnHdr.setString(4, oldLocation); // bin_original
                pstmtReturnHdr.setString(5, invoiceNo); // invoice_no
                pstmtReturnHdr.setInt(6, 0); // ret_tot_price
                pstmtReturnHdr.setString(7, user); // userid
                pstmtReturnHdr.setString(8, salesman); // salesman
                pstmtReturnHdr.setString(9, ""); // doa_reason
                pstmtReturnHdr.setString(10, ""); // doa_reason_desc
                pstmtReturnHdr.setString(11, "N"); // stat
                pstmtReturnHdr.setInt(12, fesUserid); // fes_userid
                pstmtReturnHdr.addBatch();
            	// end of create return hdr
            	
            	// create return dtl
//            	pstmtReturnDtl.setString(1, returnRef); // return_ref
//            	pstmtReturnDtl.setString(2, invoiceNo); // invoice_no
//            	pstmtReturnDtl.addBatch();
                tmpLineSeq = 1;
                for(EpcOrderItem orderItem : doaItemList) {
                    tmpImeiSim = StringHelper.trim(orderItem.getImeiSim()); // need to pass empty string, requested by pen, 20230324

                    pstmtReturnDtl.setString(1, returnRef); // return_ref
                    pstmtReturnDtl.setInt(2, tmpLineSeq++); // line_seq
                    pstmtReturnDtl.setString(3, orderItem.getWarehouse()); // warehouse
                    pstmtReturnDtl.setString(4, orderItem.getProductCode()); // prod_serv_code
                    pstmtReturnDtl.setString(5, tmpImeiSim); // imei_sim
                    pstmtReturnDtl.setInt(6, 1); // quantity
                    pstmtReturnDtl.setString(7, "P"); // line_ind
                    pstmtReturnDtl.addBatch();
                }
            	// end of create return dtl
            	
            	// update return DOA reason
            	pstmtUpdateDOAReason.setString(1,"07");	// doa reason
            	pstmtUpdateDOAReason.setString(2,"DOA For Fault Symptoms");// doa reason desc
            	pstmtUpdateDOAReason.setString(3,returnRef);// return_ref
            	pstmtUpdateDOAReason.addBatch();
            	// end of update return DOA reason
            	
            	// update return symptom
            	pstmtUpdateSymptom.setString(1, epcDoaInvoice.getSymptom()); //symptom
            	pstmtUpdateSymptom.setString(2, returnRef); // return_ref
            	pstmtUpdateSymptom.addBatch();
            	// end of update return symptom
            	
            	// void screen replace record
            	isVoid = epcScreenReplaceHandler.voidScreenReplace(crmConn, invoiceNo, fesSalesmanUserid);
            	// end of void screen replace record
            	
            	// void applecare record
            	// ...
                // added by Danny Chan on 2021-6-17 (Apple Care enhancement): start
                if (hasEAppleCareProduct) {
                    String customerNum = "";
                    String subrNum = "";

                    pstmtGetCustSubrNum.setInt(1, orderId); // order_id
                    pstmtGetCustSubrNum.setString(2, invoiceNo); // invoice_no
                    rsetGetCustSubrNum = pstmtGetCustSubrNum.executeQuery();
                    if(rsetGetCustSubrNum.next()) {
                        customerNum = StringHelper.trim(rsetGetCustSubrNum.getString("cust_num"));
                        subrNum = StringHelper.trim(rsetGetCustSubrNum.getString("subr_num"));
                    } rsetGetCustSubrNum.close();
                
                    if("".equals(customerNum)) {
                        customerNum = "00000000";
                    }

                    if("".equals(subrNum)) {
                        subrNum = "00000000";
                    }
                    
                
                    /*if (handset_imei.equals(new_handset_imei)) {                        
                        epcAppleCareHandler.reissueAppleCare(conn, invoiceNo, handset_imei, customerNum, subrNum);
                    } else {*/
                        if (!epcAppleCareHandler.cancelAppleCare(conn, crmConn, invoiceNo, customerNum, subrNum, handset_imei)) {
                            // throw new Exception("Error in cancelling AppleCare+ for imei " + handset_imei);     // commented out by Danny Chan on 2024-3-5 (fix bug related to applecare for cancel order api)
                            cancel_applecare_invoice_list.add(invoiceNo);     // added by Danny Chan on 2024-3-5 (fix bug related to applecare for cancel order api)
                        }
                    /*}*/
                }
                // added by Danny Chan on 2021-6-17 (Apple Care enhancement): end
            	// end of void applecare record
            }


//            pstmtInvHdr.executeBatch();
            pstmtReturnHdr.executeBatch();
            pstmtReturnDtl.executeBatch();
            pstmtUpdateDOAReason.executeBatch();
            pstmtUpdateSymptom.executeBatch();
            pstmtDoaLog.executeBatch();
            
            conn.commit();
            crmConn.commit();
            epcConn.commit();
  
            // added by Danny Chan on 2024-3-5 (fix bug related to applecare for cancel order api): start
            if (cancel_applecare_invoice_list.size()>0) {
                String errormsg = "Problem in cancelling eAppleCare+ service for the invoice(s): ";
                
                for (int i=0; i<cancel_applecare_invoice_list.size(); i++) {
                    if (i==0) {
                        errormsg = errormsg + cancel_applecare_invoice_list.get(i);
                    } else {
                        errormsg = errormsg + ", " + cancel_applecare_invoice_list.get(i);
                    }
                }

                errormsg = errormsg + ". Please contact Apple to follow up.";
                epcDoaInvoice.setResult("FAIL");
                epcDoaInvoice.setErrMsg(errormsg);
            } else {
			// added by Danny Chan on 2024-3-5 (fix bug related to applecare for cancel order api): end
                epcDoaInvoice.setResult("SUCCESS");
            }     //added by Danny Chan on 2024-3-5 (fix bug related to applecare for cancel order api)
        } catch (Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            try { if(crmConn != null) { crmConn.rollback(); } } catch (Exception ee) {}
            try { if(epcConn != null) { epcConn.rollback(); } } catch (Exception ee) {}

            e.printStackTrace();
            
            epcDoaInvoice.setResult("FAIL");
            epcDoaInvoice.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
            try { if(crmConn != null) { crmConn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(crmConn != null) { crmConn.close(); } } catch (Exception ee) {}
            try { if(epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception ee) {}
        }
    }
    
    
    public int getUserid(Connection conn, String username) {
    	int userid = 0;
    	PreparedStatement pstmt = null;
    	ResultSet rset = null;
    	String sql = "";
    	
    	try {
    		sql = "select userid from user_info where username = ? ";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, username); // username
    		rset = pstmt.executeQuery();
    		if(rset.next()) {
    			userid = rset.getInt("userid");
    		} rset.close(); rset = null;
    		pstmt.close(); pstmt = null;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	
    	return userid;
    }
    
    
    public boolean isInvoiceValid(ArrayList<String> invoiceList) {
    	boolean isValid = true;
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String tmpSql = "";
        int idx = 1;
        String logStr = "[isInvoiceValid] ";
        
        try {
        	conn = fesDataSource.getConnection();

        	sql = "select invoice_no, (select count(1) from zz_pret_hdg b where b.invoice_no = a.invoice_no) as void_cnt " +
        	      "  from zz_pinv_hdg a " +
        		  " where a.invoice_no in ( ";
        	for(String s : invoiceList) {
        		if("".equals(tmpSql)) {
        			tmpSql = "?";
        		} else {
        			tmpSql += ",?";
        		}
        	}
        	sql += tmpSql + " ) ";
        	pstmt = conn.prepareStatement(sql);
        	for(String s : invoiceList) {
        		pstmt.setString(idx++, s); // invoice_no
        	}
        	
        	rset = pstmt.executeQuery();
        	while(rset.next()) {
logger.info("{}{}", logStr, epcSecurityHelper.encode(StringHelper.trim(rset.getString("invoice_no"))), ", void_cnt:" + rset.getInt("void_cnt"));
				if(rset.getInt("void_cnt") > 0) {
					isValid = false;
				}
        	}
        	rset.close(); rset = null;
        	pstmt.close(); pstmt = null;
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    	
    	return isValid;
    }
    
    /**
     * Check invoice issue date within accepted cancel period
     * @param conn
     * @param invoiceNo
     * @return
     * @throws Exception
     */
    public boolean invoiceWithinCancelPeriod(Connection conn, String invoiceNo) throws Exception {
    	PreparedStatement pstmt = null;
    	ResultSet rset = null;
    	
    	try {
			String sql= "select (TRUNC(SYSDATE) - TRUNC(invoice_date)) as dayafter from zz_pinv_hdg where invoice_no = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, invoiceNo);
			rset = pstmt.executeQuery();
			
			if (!rset.next()) {
                throw new Exception("Invoice [" + invoiceNo + "] not found. ");
            }

            if (rset.getInt("dayafter") > 7) {
            	throw new Exception("Invoice [" + invoiceNo + "] issued over 7 days.");
            }
            return true;
            
		} catch (Exception e) {
			throw e;
		} finally {
			DBHelper.closeAll(rset,pstmt);
		}
    	
    }


    public ArrayList<String> getTransferNotes(ArrayList<String> doaInvoiceList) {
        ArrayList<String> tList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String tmpSql = "";
        int idx = 1;

        try {
            conn = epcDataSource.getConnection();

            sql = "select distinct transfer_note " +
                    "  from epc_order_doa_log " +
                    " where old_invoice_no in ( ";
            for(String s : doaInvoiceList) {
                if("".equals(tmpSql)) {
                    tmpSql = "?";
                } else {
                    tmpSql += ",?";
                }
            }
            sql += tmpSql + " ) ";
            sql += "  and action_type = ? ";
            pstmt = conn.prepareStatement(sql);
            for(String s : doaInvoiceList) {
                pstmt.setString(idx++, s); // old_invoice_no
            }
            pstmt.setString(idx++, "TRANSFER"); // action_type
            
            rset = pstmt.executeQuery();
            while(rset.next()) {
                tList.add(StringHelper.trim(rset.getString("transfer_note")));
            }

            rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return tList;
    }


    public String getCaseIdByItemId(Connection epcConn, int orderId, String itemId) {
        String caseId = "";
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select case_id " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and item_id = ? " +
                  "   and parent_item_id is null ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, itemId); // item_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                caseId = StringHelper.trim(rset.getString("case_id"));
            }

            rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return caseId;
    }
    
}
