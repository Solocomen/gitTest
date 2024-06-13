package epc.epcsalesapi.sales;

import com.fasterxml.jackson.databind.ObjectMapper;
import epc.epcsalesapi.crm.EpcCustomerHandler;
import epc.epcsalesapi.crm.EpcSubscriberHandler;
import epc.epcsalesapi.crm.bean.EpcAddress;
import epc.epcsalesapi.crm.bean.EpcContact;
import epc.epcsalesapi.crm.bean.EpcCustomerProfile;
import epc.epcsalesapi.crm.bean.EpcGetSubrInfoResult;
import epc.epcsalesapi.crm.bean.EpcResponse;
import epc.epcsalesapi.crm.bean.EpcSubscriber;
import epc.epcsalesapi.gup.GupHandler;
import epc.epcsalesapi.gup.bean.EpcCreateGup;
import epc.epcsalesapi.gup.bean.EpcGupResult;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.preorder.EpcPreorderHandler;
import epc.epcsalesapi.reservation.EpcReservation;
import epc.epcsalesapi.reservation.bean.EpcNumReservation;
import epc.epcsalesapi.reservation.bean.EpcNumReservationResult;
import epc.epcsalesapi.sales.bean.EpcAddOrderAttrs;
import epc.epcsalesapi.sales.bean.EpcAddProductToQuoteResult;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import epc.epcsalesapi.sales.bean.EpcChangeBag;
import epc.epcsalesapi.sales.bean.EpcChangeBagResult;
import epc.epcsalesapi.sales.bean.EpcCharacteristicUse;
import epc.epcsalesapi.sales.bean.EpcCharge;
import epc.epcsalesapi.sales.bean.EpcConfiguredValue;
import epc.epcsalesapi.sales.bean.EpcConfirmNewMobileResult;
import epc.epcsalesapi.sales.bean.EpcConfirmOrderResult;
import epc.epcsalesapi.sales.bean.EpcCreateBillingAccountResult;
import epc.epcsalesapi.sales.bean.EpcCreateEPCReceiptResult;
import epc.epcsalesapi.sales.bean.EpcCreateEPCRecordResult;
import epc.epcsalesapi.sales.bean.EpcCreateOrder;
import epc.epcsalesapi.sales.bean.EpcCreateOrderResult;
import epc.epcsalesapi.sales.bean.EpcCreateQuoteResult;
import epc.epcsalesapi.sales.bean.EpcCreateReceipt;
import epc.epcsalesapi.sales.bean.EpcCreateReceiptResult;
import epc.epcsalesapi.sales.bean.EpcCustNumSubrNum;
import epc.epcsalesapi.sales.bean.EpcDealerCode;
import epc.epcsalesapi.sales.bean.EpcDeleteQuoteFromOrder;
import epc.epcsalesapi.sales.bean.EpcDeleteQuoteItemResult;
import epc.epcsalesapi.sales.bean.EpcDeleteQuoteResult;
import epc.epcsalesapi.sales.bean.EpcGetCharge;
import epc.epcsalesapi.sales.bean.EpcGetChargeResult;
import epc.epcsalesapi.sales.bean.EpcGetDealerCode;
import epc.epcsalesapi.sales.bean.EpcGetReserveItemListByOrderIdResult;
import epc.epcsalesapi.sales.bean.EpcGetSpec;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcLockOrderResult;
import epc.epcsalesapi.sales.bean.EpcLogOrderStatus;
import epc.epcsalesapi.sales.bean.EpcLogStockStatus;
import epc.epcsalesapi.sales.bean.EpcLoginChannel;
import epc.epcsalesapi.sales.bean.EpcOfferCharge;
import epc.epcsalesapi.sales.bean.EpcOrderCaseInfo;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.EpcOrderItem;
import epc.epcsalesapi.sales.bean.EpcOrderItemDetail;
import epc.epcsalesapi.sales.bean.EpcOrderItemInfo;
import epc.epcsalesapi.sales.bean.EpcOrderQuoteInfo;
import epc.epcsalesapi.sales.bean.EpcPayment;
import epc.epcsalesapi.sales.bean.EpcProceedOrder;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.EpcQuoteItemForUpdate;
import epc.epcsalesapi.sales.bean.EpcQuoteProductCandidate;
import epc.epcsalesapi.sales.bean.EpcReserveItem;
import epc.epcsalesapi.sales.bean.EpcSmcQuote;
import epc.epcsalesapi.sales.bean.EpcSubr;
import epc.epcsalesapi.sales.bean.EpcTmpQuote;
import epc.epcsalesapi.sales.bean.EpcTmpUpdOrder;
import epc.epcsalesapi.sales.bean.EpcTransferOrder;
import epc.epcsalesapi.sales.bean.EpcUpdateModifiedItemToQuoteResult;
import epc.epcsalesapi.sales.bean.EpcUpdateOrderType;
import epc.epcsalesapi.sales.bean.EpcVerifyExistingMobile;
import epc.epcsalesapi.sales.bean.asiaMiles.CreateAsiaMiles;
import epc.epcsalesapi.sales.bean.orderReservedItems.ReservedItem;
import epc.epcsalesapi.sales.bean.orderReservedItems.UpdateReservedItemsRequest;
import epc.epcsalesapi.stock.EpcStockHandler;
import epc.epcsalesapi.vakaCms.VakaCmsHandler;
import epc.epcsalesapi.vakaCms.bean.VakaCmsProduct;


import java.math.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.CompletableFuture;
//import java.util.stream.Collectors;
import javax.sql.*;

import org.apache.commons.lang.StringUtils;
//import org.apache.commons.validator.routines.BigIntegerValidator;
//import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class EpcOrderHandler {
	
	private final Logger logger = LoggerFactory.getLogger(EpcOrderHandler.class);

    private final String SYSTEM_CUST_ID = "SysAdmin";
	
	@Autowired
	private DataSource epcDataSource;
	
	@Autowired
	private EpcOrderProcessCtrlHandler epcOrderProcessCtrlHandler;
	
	@Autowired
	private EpcOrderAttrHandler epcOrderAttrHandler;
	
	@Autowired
	private EpcQuoteHandler epcQuoteHandler;
	
	@Autowired
	private EpcSalesmanHandler epcSalesmanHandler;
	
//	@Autowired
//	private EpcSimHandler epcSimHandler;
	
	@Autowired
	private EpcStockHandler epcStockHandler;
	
	@Autowired
	private EpcReceiptHandler epcReceiptHandler;
	
//	@Autowired
//	private EpcBillingHandler epcBillingHandler;
	
	@Autowired
	private EpcReservation epcReservation;
	
	@Autowired
	private EpcCustProfileHandler epcCustProfileHandler;
	
	@Autowired
	private EpcPaymentHandler epcPaymentHandler;
	
	@Autowired
	private EpcCustomerHandler epcCustomerHandler;
	
	@Autowired
	private EpcOrderTypeHandler epcOrderTypeHandler;
	
	@Autowired
	private EpcSecurityHelper epcSecurityHelper;

    @Autowired
    private EpcSubscriberHandler epcSubscriberHandler;

    @Autowired
    private GupHandler gupHandler;

//    @Autowired
//    private EpcVoucherHandlerNew epcVoucherHandlerNew;

    @Autowired
    private EpcAsiaMilesHandler epcAsiaMilesHandler;

    @Autowired
    private VakaCmsHandler vakaCmsHandler;

    @Autowired
    private EpcOrderLogHandler epcOrderLogHandler;

//    @Autowired
//    private FesUserHandler fesUserHandler;

//    @Autowired
//    private FesLocationHandler fesLocationHandler;

    @Autowired
    private EpcPreorderHandler epcPreorderHandler;


    @Autowired
    private EpcSkipReserveHandler epcSkipReserveHandler;

	   
	/***
	 * called by Sigma OM
	 * 
	 * @param epcConfirmOrder
	 * @return
	 */
    public EpcConfirmOrderResult confirmOrder(HashMap<String, Object> epcConfirmOrderMap) {
        EpcConfirmOrderResult epcConfirmOrderResult = new EpcConfirmOrderResult();
        ArrayList<HashMap<String, Object>> epcConfirmOrderItemList = null;
        HashMap<String, Object> epcConfirmOrderItem = null;
        EpcSubscriber epcSubscriber = null;
        EpcResponse epcResponse = null;
        String orderId = "";
        String custId = "";
        String custNum = "";
        String subrNum = "";
        String accountNum = ""; // gnv account num
        String subrStatus = "";
        String subrKey = "";
        String tvbActCode = "";
        ArrayList<EpcSubr> subrList = null;
        EpcSubr epcSubr = null;
        TreeMap<String, ArrayList<EpcSubr>> orderMap = new TreeMap<String, ArrayList<EpcSubr>>();
        String logStr = "[confirmOrder()]";
        String tmpLogStr = "";
     
        try {
logger.info("{}{}", logStr, "start");
            if(epcConfirmOrderMap != null) {
                epcConfirmOrderItemList = (ArrayList<HashMap<String, Object>>)epcConfirmOrderMap.get("ConfirmOrderList");
            }

            if(epcConfirmOrderItemList != null) {
logger.info("{}{}", logStr, "item list is not null");
                for (int i = 0; i < epcConfirmOrderItemList.size(); i++) {
                    epcConfirmOrderItem = epcConfirmOrderItemList.get(i); // per line

                    orderId = epcSecurityHelper.encode(StringHelper.trim((String)epcConfirmOrderItem.get("OrderID"))); // actually it's smc quote id
                    custId = epcSecurityHelper.encode(StringHelper.trim((String)epcConfirmOrderItem.get("CustID")));
                    custNum = epcSecurityHelper.encode(StringHelper.trim((String)epcConfirmOrderItem.get("CustNum")));
                    subrNum = epcSecurityHelper.encode(StringHelper.trim((String)epcConfirmOrderItem.get("SubrNum")));
                    accountNum = epcSecurityHelper.encode(StringHelper.trim((String)epcConfirmOrderItem.get("AcctNum")));
                    subrStatus = epcSecurityHelper.encode(StringHelper.trim((String)epcConfirmOrderItem.get("SubrStatus")));
                    tvbActCode = epcSecurityHelper.encode(StringHelper.trim((String)epcConfirmOrderItem.get("TVBActCode")));
logger.info("{}{}{}", logStr, "orderId:", orderId);
logger.info("{}{}{}", logStr, "custId:", custId);

                    subrKey = ""; // reset
                    
                    epcConfirmOrderResult.setCustID(custId);
                    epcConfirmOrderResult.setOrderID(orderId);
                    
                    if(StringUtils.isNotBlank(custNum) && StringUtils.isNotBlank(subrNum) && StringUtils.isNotBlank(accountNum) && StringUtils.isNotBlank(subrStatus)) {
                    	// perform update (subr status, gnv account num) to crm when cust num & subr num are not empty
                    	
                    	if(!orderMap.containsKey(orderId)) {
                            subrList = epcCustProfileHandler.getSubrKeyListBySmcQuoteId(Integer.parseInt(orderId));
                            orderMap.put(orderId, subrList);
                        } else {
                            subrList = orderMap.get(orderId);
                        }
                    	
                    	// update crm
                        for(int x = 0; x < subrList.size(); x++) {
                            epcSubr = subrList.get(x);
                            if(epcSubr.getCustNum().equals(custNum) && epcSubr.getSubrNum().equals(subrNum)) {
                                subrKey = StringHelper.trim(epcSubr.getSubrKey());
                                break;
                            }
                        }
                        tmpLogStr = "[quoteId:" + orderId + "][custId:" + custId + "][custNum:" + custNum + "][subrNum:" + subrNum + "][subrKey:" + subrKey + "] accountNum:" + accountNum + ",subrStatus:" + subrStatus;
logger.info("{}{}", logStr, tmpLogStr);
                        if("".equals(subrKey)) {
                            throw new Exception("cannot find subr key " + custNum + "/" + subrNum);
                        }
                        
                        epcSubscriber = new EpcSubscriber();
                        epcSubscriber.setRecId(subrKey);
                        epcSubscriber.setAccountNum(accountNum);
                        epcSubscriber.setSubrStatus(subrStatus);
//                        epcSubscriber.setSubrActivateDate();
                        epcSubscriber.setLastUpdatedBy("SysAdmin");
                                
                        epcResponse = new EpcSubscriberHandler().update(epcSubscriber);
                        tmpLogStr = "[quoteId:" + orderId + "][custId:" + custId + "][custNum:" + custNum + "][subrNum:" + subrNum + "][subrKey:" + subrKey + "] update crm result:" + epcSecurityHelper.encode(epcResponse.getResultCode()) + ", msg:" + epcSecurityHelper.encode(epcResponse.getResultMsg());
logger.info("{}{}", logStr, tmpLogStr);
                        if(!"0".equals(epcResponse.getResultCode())) {
                            throw new Exception("cannot update crm, " + tmpLogStr);
                        }
                        // end of update crm
                        
                        // loginnow
                        // end of loginnow
                        
                        // send notifications
                        // ...
                        // end of send notifications
                        
                        // create another order ?
                        // ...
                        // end of create another order ?
                    }
                }
            }
            
            epcConfirmOrderResult.setResult("SUCCESS");
logger.info("{}{}", logStr, "end");
        } catch (Exception e) {
            e.printStackTrace();
            
            epcConfirmOrderResult.setResult("FAIL");
            epcConfirmOrderResult.setErrorCode("1000");
            epcConfirmOrderResult.setErrorMessage(e.getMessage());
        } finally {
        }
        
        return epcConfirmOrderResult;
    }
    
    
    // added by Danny Chan on 2022-12-5 (SHK Point Payment Enhancement): start
    public EpcGetReserveItemListByOrderIdResult getReserveItemListByOrderId(int orderId) {
	EpcGetReserveItemListByOrderIdResult result = new EpcGetReserveItemListByOrderIdResult();
	    
        ArrayList<EpcReserveItem> items = new ArrayList();

    	Connection conn = null;
    	PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
        	conn = epcDataSource.getConnection();
        	
        	//sql = "select item_id, cpq_item_desc, item_code, reserve_type from epc_order_item where order_id = ? and reserve_id is not null";
		sql = "select item_id, cpq_item_desc, item_code, reserve_type from epc_order_item where order_id = ?  and item_cat = 'DEVICE' and is_reserve = 'Y'";
		
        	pstmt = conn.prepareStatement(sql);
        	pstmt.setInt(1, orderId); // order_id
        	rset = pstmt.executeQuery();
        	while (rset.next()) {
			String item_id = rset.getString(1);
			String cpq_item_desc = rset.getString(2);
			String item_code = rset.getString(3);
			String reserve_type = rset.getString(4);
			
			EpcReserveItem item = new EpcReserveItem();
			item.setItemId(item_id);
			item.setItemDesc(cpq_item_desc);
			item.setProductCode(item_code);
			item.setReserveType(reserve_type);
			
			items.add(item);
        	} 
		
		result.setResult("SUCCESS");
		result.setErrorCode(0);
		result.setItems(items);
        } catch (Exception e) {
		result.setErrorCode(1002);
		result.setErrorMessage(e.toString());
		result.setResult("FAIL");
        	e.printStackTrace();
       } finally {
	    try {rset.close(); rset = null;} catch (Exception ee) {}
            try {pstmt.close(); pstmt = null;} catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
	
	return result;
    }
    // added by Danny Chan on 2022-12-5 (SHK Point Payment Enhancement): end
    
    public String getOrderReferenceByOrderId(int orderId) {
    	String orderReference = "";
    	Connection conn = null;
    	PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
        	conn = epcDataSource.getConnection();
        	
        	sql = "select order_reference from epc_order where order_id = ? ";
        	pstmt = conn.prepareStatement(sql);
        	pstmt.setInt(1, orderId); // order_id
        	rset = pstmt.executeQuery();
        	if(rset.next()) {
        		orderReference = StringHelper.trim(rset.getString("order_reference"));
        	} rset.close(); rset = null;
        	pstmt.close(); pstmt = null;
        } catch (Exception e) {
        	e.printStackTrace();

        	orderReference = "";
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    	return orderReference;
    }


    public void updateOrderType(EpcUpdateOrderType epcUpdateOrderType) {
        Connection conn = null;
        String orderReference = "xxx";
        String custId = "";
        String orderChannel = "";
        String orderUser = "";
        String orderSalesman = "";
        String orderLocation = "";
        int existingOrderId = 0;
        String orderType = "";
        boolean isValid = true;
        String errMsg = "";


        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            // get input param
            custId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcUpdateOrderType.getCustId()));
            orderChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcUpdateOrderType.getOrderChannel()));
            orderUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcUpdateOrderType.getOrderUser()));
            orderSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcUpdateOrderType.getOrderSalesman()));
            orderLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcUpdateOrderType.getOrderLocation()));
            existingOrderId = epcUpdateOrderType.getOrderId();
            orderType = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcUpdateOrderType.getOrderType()));
            // end of get input param

            // basic checking
            if("".equals(custId)) {
                errMsg += "input cust id is empty. ";
                isValid = false;
            }
            if(existingOrderId != 0) {
            	// check whether this order id is belonged to input cust id (existingOrderId vs custId)
            	orderReference = isOrderBelongCust(conn, custId, existingOrderId);
            	if("NOT_BELONG".equals(orderReference)) {
            		errMsg += "input order id [" + existingOrderId + "] is not belonged to input cust id [" + custId + "]. ";
                	isValid = false;
            	}
            }
            if(isOrderLocked(conn, custId, existingOrderId)) {
                errMsg += "input order [" + existingOrderId + "] is locked. ";
                isValid = false;
            }
            if(!epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_NORMAL.equals(orderType) 
                && !epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_FAST_CHECKOUT.equals(orderType)
                && !epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_TEMP.equals(orderType)
                && !epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_CHECKOUT.equals(orderType)
            ) {
            	errMsg += "order type [" + orderType + "] is invald. ";
            	isValid = false;
            }
            // end of basic checking


            if(isValid) {
                epcOrderAttrHandler.updateAttrValue(conn, existingOrderId, "", "", epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE, orderType);

                conn.commit();

                epcUpdateOrderType.setResult("SUCCESS");
            } else {
                epcUpdateOrderType.setResult("FAIL");
                epcUpdateOrderType.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            e.printStackTrace();

            epcUpdateOrderType.setResult("FAIL");
            epcUpdateOrderType.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }


    public void transferOrder(EpcTransferOrder epcTransferOrder) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        String orderReference = "xxx";
        String custId = "";
        String orderChannel = "";
        String orderUser = "";
        String orderSalesman = "";
        String orderLocation = "";
        int newOrderId = 0;
        int tmpOrderId = 0;
        int tmpQuoteId = 0;
        String tmpQuoteGuid = "";
        boolean isValid = true;
        String errMsg = "";
        String remarks = "";


        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            // get input param
            custId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcTransferOrder.getCustId()));
            orderChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcTransferOrder.getOrderChannel()));
            orderUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcTransferOrder.getOrderUser()));
            orderSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcTransferOrder.getOrderSalesman()));
            orderLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcTransferOrder.getOrderLocation()));
            newOrderId = epcTransferOrder.getOrderId();
            tmpOrderId = epcTransferOrder.getTmpOrderId();
            tmpQuoteId = epcTransferOrder.getTmpQuoteId();
            // end of get input param


            // basic checking
            if("".equals(custId)) {
                errMsg += "input cust id is empty. ";
                isValid = false;
            }

            if(newOrderId != 0) {
                // check whether this order id is belonged to input cust id (existingOrderId vs custId)
                orderReference = isOrderBelongCust(conn, custId, newOrderId);
                if("NOT_BELONG".equals(orderReference)) {
                    errMsg += "input order id [" + newOrderId + "] is not belonged to input cust id [" + custId + "]. ";
                    isValid = false;
                }
            } else {
                errMsg += "input order id [" + newOrderId + "] is not valid. ";
                isValid = false;
            }

            if(isOrderLocked(conn, custId, newOrderId)) {
                errMsg += "input order [" + newOrderId + "] is locked. ";
                isValid = false;
            }

            if(tmpOrderId != 0) {
                // check whether this order id is belonged to input cust id (existingOrderId vs custId)
                orderReference = isOrderBelongCust(conn, custId, tmpOrderId);
                if("NOT_BELONG".equals(orderReference)) {
                    errMsg += "input tmp order id [" + tmpOrderId + "] is not belonged to input cust id [" + custId + "]. ";
                    isValid = false;
                }
            } else {
                errMsg += "input tmp order id [" + tmpOrderId + "] is not valid. ";
                isValid = false;
            }

            if(isOrderLocked(conn, custId, tmpOrderId)) {
                errMsg += "input order [" + tmpOrderId + "] is locked. ";
                isValid = false;
            }

            if(tmpQuoteId != 0) {
                tmpQuoteGuid = getCurrentQuoteGuid(conn, tmpOrderId, tmpQuoteId);
                if("".equals(tmpQuoteGuid)) {
                    errMsg += "input tmp quote id [" + tmpQuoteId + "] is not belonged to input tmp order id [" + tmpOrderId + "]. ";
                    isValid = false;
                }
            } else {
                errMsg += "input tmp quote id [" + tmpQuoteId + "] is not valid. ";
                isValid = false;
            }
            // end of basic checking


            if(isValid) {
                sql = "delete from epc_order where order_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, tmpOrderId); // order_id - tmp order id
                pstmt.executeUpdate();
                pstmt.close();

                sql = "update epc_order_quote " +
                      "   set order_id = ? " +
                      " where order_id = ? " +
                      "   and quote_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, newOrderId); // order_id - new order id
                pstmt.setInt(2, tmpOrderId); // order_id - tmp order id
                pstmt.setInt(3, tmpQuoteId); // quote_id - tmp quote id
                pstmt.executeUpdate();
                pstmt.close();

                sql = "update epc_order_item " +
                      "   set order_id = ? " +
                      " where order_id = ? " +
                      "   and quote_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, newOrderId); // order_id - new order id
                pstmt.setInt(2, tmpOrderId); // order_id - tmp order id
                pstmt.setInt(3, tmpQuoteId); // quote_id - tmp quote id
                pstmt.executeUpdate();
                pstmt.close();

                // create salesman log
                remarks = "transfer tmp order [" + tmpOrderId + "] tmp quote [" + tmpQuoteId + "] to order [" + newOrderId + "]";
                epcSalesmanHandler.createSalesmanLog(conn, newOrderId, "", orderUser, orderSalesman, orderLocation, orderChannel, epcSalesmanHandler.actionTransferQuote, remarks);
                // end of create salesman log

                // update salesman log of tmp order
                epcSalesmanHandler.updateSalesmanLog(conn, tmpOrderId, newOrderId, remarks);
                // end of update salesman log of tmp order

                conn.commit();

                epcTransferOrder.setResult("SUCCESS");
            } else {
                epcTransferOrder.setResult("FAIL");
                epcTransferOrder.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            e.printStackTrace();

            epcTransferOrder.setResult("FAIL");
            epcTransferOrder.setErrMsg(e.getMessage());
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }
    
    
    public EpcCreateOrderResult createOrder(EpcCreateOrder epcCreateOrder) {
        EpcCreateOrderResult epcCreateOrderResult = new EpcCreateOrderResult();
        Connection conn = null;
        String orderReference = "xxx";
        EpcTmpQuote epcTmpQuote = null;
        EpcQuote epcQuote = null;
        EpcCreateQuoteResult epcCreateQuoteResult = null;
        String cpqQuoteGuid = "";
        String custId = "";
        String orderChannel = "";
        String orderUser = "";
        String orderSalesman = "";
        String orderLocation = "";
        int tmpOrderId = 0;
        int tmpQuoteId = 0;
        int existingOrderId = 0;
        String orderType = "";
        String quoteContextOrderType = "";
        String quoteContextMigration = "";
        PreparedStatement pstmt = null;
        PreparedStatement pstmtOrder = null;
        PreparedStatement pstmtOrderQuote = null;
        ResultSet rset = null;
        String sql = "";
        int cnt = 0;
        boolean isValid = true;
        String errMsg = "";
        HashMap <String, Object> contextMap = new HashMap <String, Object>();
        HashMap <String, Object> orderMap = new HashMap <String, Object>();
        String remarks = "";
        String quoteContentStr = "";
        java.util.Date createDate = new java.util.Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        EpcLogOrderStatus epcLogOrderStatus = null;

        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            // get input param
            custId = StringHelper.trim(epcCreateOrder.getCustId());
            orderChannel = StringHelper.trim(epcCreateOrder.getOrderChannel());
            orderUser = StringHelper.trim(epcCreateOrder.getOrderUser());
            orderSalesman = StringHelper.trim(epcCreateOrder.getOrderSalesman());
            orderLocation = StringHelper.trim(epcCreateOrder.getOrderLocation());
            existingOrderId = epcCreateOrder.getOrderId();
            orderType = StringHelper.trim(epcCreateOrder.getOrderType());
            // end of get input param
            
            
            // basic checking
            if("".equals(custId)) {
            	errMsg += "input cust id is empty. ";
            	isValid = false;
            }
            if(StringUtils.isNotBlank(orderUser)) {
                // check with FES ?
            	//  ...
            }
            if(StringUtils.isNotBlank(orderSalesman)) {
                // check with FES ?
            	//  ...
            }
            if(existingOrderId != 0) {
            	// check whether this order id is belonged to input cust id (existingOrderId vs custId)
            	orderReference = isOrderBelongCust(conn, custId, existingOrderId);
            	if("NOT_BELONG".equals(orderReference)) {
            		errMsg += "input order id [" + existingOrderId + "] is not belonged to input cust id [" + custId + "]. ";
                	isValid = false;
            	}

                if(isOrderLocked(conn, custId, existingOrderId)) {
                    errMsg += "input order [" + existingOrderId + "] is locked. ";
                    isValid = false;
                }
            	
            	// check whether this order is valid in status (allow order with I - init to add quote only)
            	sql = "select count(1) from epc_order where order_id = ? and order_status = ? ";
            	pstmt = conn.prepareStatement(sql);
            	pstmt.setInt(1, existingOrderId); // order_id
            	pstmt.setString(2, "I"); // order_status - init
            	rset = pstmt.executeQuery();
            	if(rset.next()) {
            		cnt = rset.getInt(1);
            	} rset.close(); rset = null;
            	pstmt.close(); pstmt = null;
            	
            	if(cnt == 0) {
            		errMsg += "input order id [" + existingOrderId + "] is not valid to add new quote. ";
                	isValid = false;
            	}
            }
            if(!epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_NORMAL.equals(orderType) 
                && !epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_FAST_CHECKOUT.equals(orderType)
                && !epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_TEMP.equals(orderType)
                && !epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_CHECKOUT.equals(orderType)
            ) {
            	errMsg += "order type [" + orderType + "] is invald. ";
            	isValid = false;
            }
            // end of basic checking
            
            if(isValid) {
                // prepare statement
                sql = "insert into epc_order ( " +
                      "  order_id, cust_id, order_reference, order_channel, order_user, " + 
                      "  order_salesman, order_date, order_status, create_date, order_lang " +
                      ") values ( " +
                      "  ?,?,?,?,?, " + 
                      "  ?, sysdate, ?, sysdate, 'E' " + 
                      ") ";
                pstmtOrder = conn.prepareStatement(sql);

                sql = "insert into epc_order_quote ( " +
                      "  order_id, quote_id, cpq_quote_guid, " +
                	  "  create_user, create_salesman, create_channel, create_date, quote_content " + 
                      ") values ( " +
                      "  ?,?,?, " +
                      "  ?,?,?, sysdate,? " +
                      ") ";
                pstmtOrderQuote = conn.prepareStatement(sql);
                
                sql = "select value_str1 " +
                      "  from epc_control_tbl " + 
                	  " where rec_type = ? " +
                      "   and value_str2 = ? ";
            	pstmt = conn.prepareStatement(sql);
            	pstmt.setString(1, "QC_MIGRATION"); // rec_type
            	pstmt.setString(2, "A"); // value_str2 - Active
            	rset = pstmt.executeQuery();
            	if(rset.next()) {
            		quoteContextMigration = StringHelper.trim(rset.getString("value_str1"));
            	} else {
            		quoteContextMigration = "P1";
            	}
            	rset.close(); rset = null;
            	pstmt.close(); pstmt = null;
                // end of prepare statement
                
                
                // generate order / quote id
                if(existingOrderId > 0) {
                	// use existing smc order
                	//  orderReference = existing order reference (from basic checking)
                	tmpOrderId = existingOrderId;
                	
                	remarks = "add to order [" + tmpOrderId + "], ";
                } else {
                	// new smc order
                	tmpOrderId = genOrderId(conn);
                	orderReference = genOrderRef(); // generate order reference
                	
                	remarks = "create new order [" + tmpOrderId + "], ";
                }
                
                tmpQuoteId = genOrderId(conn);
                // end of generate order / quote id
                
                
                // determine order type for quote context
                quoteContextOrderType = epcOrderTypeHandler.determineSigmaOrderType(epcOrderTypeHandler.actionProductPurchase, "");
                // end of determine order type for quote context

                
                // prepare quote context
                contextMap.put("SMCOrderReference", orderReference);
                contextMap.put("SMCOrderId", tmpOrderId + "");
                contextMap.put("SMCQuoteId", tmpQuoteId + "");
                contextMap.put("SMCOrderChannel", orderChannel);
// modified to use smc quote id as CRM_Order_ID to serve single smc order multiple sigma quote, kerrytsang, 20210223
//                orderMap.put("CRM_Order_ID", tmpOrderId + "");
                orderMap.put("CRM_Order_ID", tmpQuoteId + "");
// end of modified to use smc quote id as CRM_Order_ID to serve single smc order multiple sigma quote, kerrytsang, 20210223
                orderMap.put("Order_Channel", orderChannel);
                orderMap.put("Order_Location", orderLocation);
                orderMap.put("User_ID", orderUser);
                orderMap.put("Order_Created_By", orderSalesman);
                orderMap.put("Order_Type", quoteContextOrderType);
                orderMap.put("Migration", quoteContextMigration);
                orderMap.put("Requested_Activation_Date", sdf.format(createDate));

                contextMap.put("order", orderMap);
                // end of prepare quote context
                

                // prepare json input
                epcTmpQuote = new EpcTmpQuote();
                epcTmpQuote.setCustomerRef(custId);
//                epcTmpQuote.setItems(null);
                epcTmpQuote.setQuoteType(0);
                epcTmpQuote.setContextData(contextMap);
                // end of prepare json input

                epcCreateQuoteResult = epcQuoteHandler.createQuote(epcTmpQuote);
                if("SUCCESS".equals(epcCreateQuoteResult.getResult())) {
                    // get quote guid from cpq api call
                    cpqQuoteGuid = StringHelper.trim(epcCreateQuoteResult.getCpqQuoteGUID());
                    epcQuote = epcCreateQuoteResult.getEpcQuote();
                    if(epcQuote != null) {
                        quoteContentStr = new ObjectMapper().writeValueAsString(epcQuote);
                    }

                    // insert epc_order record
                    if(existingOrderId == 0) {
                    	// new smc order
	                    pstmtOrder.setInt(1, tmpOrderId); // order_id
	                    pstmtOrder.setString(2, custId); // cust_id
	                    pstmtOrder.setString(3, orderReference); // order_reference
	                    pstmtOrder.setString(4, orderChannel); // order_channel
	                    pstmtOrder.setString(5, orderUser); // order_user
	                    pstmtOrder.setString(6, orderSalesman); // order_salesman
	                    pstmtOrder.setString(7, "I"); // order_status, initial
	                    pstmtOrder.executeUpdate();
	                    
	                    // insert order type (when newly create an order)
	                  	epcOrderAttrHandler.addAttr(conn, tmpOrderId, "", "", epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE, orderType);
	                    // end of insert order type (when newly create an order)
                    }
                    // end of insert epc_order record

                    // insert epc_order_quote record
                    pstmtOrderQuote.setInt(1, tmpOrderId);
                    pstmtOrderQuote.setInt(2, tmpQuoteId);
                    pstmtOrderQuote.setString(3, cpqQuoteGuid);
                    pstmtOrderQuote.setString(4, orderUser); // order_user
                    pstmtOrderQuote.setString(5, orderSalesman); // order_salesman
                    pstmtOrderQuote.setString(6, orderChannel); // order_channel
                    pstmtOrderQuote.setString(7, quoteContentStr); // quote content
                    pstmtOrderQuote.executeUpdate();
                    // end of insert epc_order_quote record
                    
                    // create salesman log
                    remarks += "quote [" + tmpQuoteId + "][" + cpqQuoteGuid + "] is added";
                    epcSalesmanHandler.createSalesmanLog(conn, tmpOrderId, "", orderUser, orderSalesman, orderLocation, orderChannel, epcSalesmanHandler.actionCreateQuote, remarks);
                    // end of create salesman log

                    // create log
                    epcLogOrderStatus = new EpcLogOrderStatus();
                    epcLogOrderStatus.setOrderId(tmpOrderId);
                    epcLogOrderStatus.setOldOrderStatus("");
                    epcLogOrderStatus.setNewOrderStatus("I");
                    epcLogOrderStatus.setCreateUser(orderUser);
                    epcLogOrderStatus.setCreateSalesman(orderSalesman);
                    epcLogOrderStatus.setCreateChannel(orderChannel);
                    epcLogOrderStatus.setCreateLocation(orderLocation);
                    epcOrderLogHandler.logOrderStatus(conn, epcLogOrderStatus);
                    // end of create log


                    epcCreateOrderResult.setCpqQuoteGUID(cpqQuoteGuid);
                    epcCreateOrderResult.setCustId(custId);
                    epcCreateOrderResult.setOrderReference(orderReference);
                    epcCreateOrderResult.setOrderId(tmpOrderId);
                    epcCreateOrderResult.setQuoteId(tmpQuoteId);
                    epcCreateOrderResult.setResult("SUCCESS");
                    
                    conn.commit();
                } else {
                    epcCreateOrderResult.setResult("FAIL");
                    epcCreateOrderResult.setErrorCode("1002");
                    epcCreateOrderResult.setErrorMessage(epcCreateQuoteResult.getErrMsg());
                    
                    conn.rollback();
                }
            } else {
                epcCreateOrderResult.setResult("FAIL");
                epcCreateOrderResult.setErrorCode("1001");
                epcCreateOrderResult.setErrorMessage(errMsg);
            }
        } catch (Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            e.printStackTrace();

            epcCreateOrderResult.setResult("FAIL");
            epcCreateOrderResult.setErrorCode("1000");
            epcCreateOrderResult.setErrorMessage(e.getMessage());
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcCreateOrderResult;
    }
    
    
    public EpcDeleteQuoteFromOrder deleteQuoteFromOrder(EpcDeleteQuoteFromOrder epcDeleteQuoteFromOrder) {
    	Connection conn = null;
    	PreparedStatement pstmt = null;
    	String sql = "";
        String orderReference = "xxx";
        String custId = "";
        int orderId = 0;
        int quoteId = 0;
        String deleteChannel = "";
        String deleteUser = "";
        String deleteSalesman = "";
        String deleteLocation = "";
        String quoteGuid = "";
        String errMsg = "";
        boolean isValid = true;
        EpcQuote epcQuote = null;
        EpcDeleteQuoteResult epcDeleteQuoteResult = null;
        String remarks = "";
        ArrayList<String> smcCaseIdList = new ArrayList<String>();
        
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            // get input param
            custId = StringHelper.trim(epcDeleteQuoteFromOrder.getCustId());
            deleteChannel = StringHelper.trim(epcDeleteQuoteFromOrder.getDeleteChannel());
            deleteUser = StringHelper.trim(epcDeleteQuoteFromOrder.getDeleteUser());
            deleteSalesman = StringHelper.trim(epcDeleteQuoteFromOrder.getDeleteSalesman());
            deleteLocation = StringHelper.trim(epcDeleteQuoteFromOrder.getDeleteLocation());
            orderId = epcDeleteQuoteFromOrder.getOrderId();
            quoteId = epcDeleteQuoteFromOrder.getQuoteId();
            // end of get input param
            
            
            // basic checking
            if("".equals(custId)) {
            	errMsg += "input cust id is empty. ";
            	isValid = false;
            }
            if(StringUtils.isNotBlank(deleteUser)) {
                // check with FES ?
            	//  ...
            }
            if(StringUtils.isNotBlank(deleteSalesman)) {
                // check with FES ?
            	//  ...
            }
            
            if(orderId == 0) {
            	errMsg += "input order id [" + orderId + "] is not valid. ";
            	isValid = false;
            } else {
            	// check whether this order id is belonged to input cust id (existingOrderId vs custId)
            	orderReference = isOrderBelongCust(conn, custId, orderId);
            	if("NOT_BELONG".equals(orderReference)) {
            		errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
                	isValid = false;
            	}

                if(isOrderLocked(conn, custId, orderId)) {
                    errMsg += "input order [" + orderId + "] is locked. ";
                    isValid = false;
                }
            }
            
            if(quoteId == 0) {
            	errMsg += "input quote id [" + quoteId + "] is not valid. ";
            	isValid = false;
            } else {
            	quoteGuid = getCurrentQuoteGuid(conn, orderId, quoteId);
            	if("".equals(quoteGuid)) {
            		errMsg += "input order/quote id [" + orderId + "/" + quoteId + "] is not valid. ";
                	isValid = false;
            	} else {
//            		epcQuote = epcQuoteHandler.getQuoteInfo(quoteGuid);
                    epcQuote = getCPQQuoteInEpc(orderId, quoteId);
            		
            		// for attr deletion
            		for(EpcQuoteItem item : epcQuote.getItems()) {
            			smcCaseIdList.add(StringHelper.trim((String)item.getProductCandidate().get("ID")));
            		}
            	}
            }
            // end of basic checking

            
            if(isValid) {
            	epcDeleteQuoteResult = epcQuoteHandler.deleteQuote(epcQuote);
                if("SUCCESS".equals(epcDeleteQuoteResult.getResult())) {
                	// delete epc table
                	sql = "delete from epc_order_quote where order_id = ? and quote_id = ? ";
                	pstmt = conn.prepareStatement(sql);
                	pstmt.setInt(1, orderId); // order_id
                	pstmt.setInt(2, quoteId); // quote_id
                	pstmt.executeUpdate();
                	// end of delete epc table
                	
                	// delete attr (all cases under the quote)
                	for(String caseId : smcCaseIdList) {
                		epcOrderAttrHandler.obsoleteAttrByCaseId(conn, orderId, caseId);
                	}
                	// end of delete attr (all cases under the quote)
                    
                    // create salesman log
                	remarks = "delete quote [" + quoteId + "][" + quoteGuid + "] from order";
                    epcSalesmanHandler.createSalesmanLog(conn, orderId, "", deleteUser, deleteSalesman, deleteLocation, deleteChannel, epcSalesmanHandler.actionDeleteQuote, remarks);
                    // end of create salesman log

                    epcDeleteQuoteFromOrder.setResult("SUCCESS");
                    
                    conn.commit();
                } else {
                	epcDeleteQuoteFromOrder.setResult("FAIL");
                	epcDeleteQuoteFromOrder.setErrMsg(epcDeleteQuoteResult.getErrMsg());
                    
                    conn.rollback();
                }
            } else {
            	epcDeleteQuoteFromOrder.setResult("FAIL");
            	epcDeleteQuoteFromOrder.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            e.printStackTrace();

            epcDeleteQuoteFromOrder.setResult("FAIL");
            epcDeleteQuoteFromOrder.setErrMsg(e.getMessage());
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcDeleteQuoteFromOrder;
    }
    
    
    public int genOrderId() {
        int orderId = 0;
        Connection conn = null;
        
        try {
            conn = epcDataSource.getConnection();
            
            orderId = genOrderId(conn);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return orderId;
    }
    
    
    public int genOrderId(Connection conn) {
        int orderId = 0;
        PreparedStatement pstmtId = null;
        String sql = "";
        ResultSet rset = null;
        
        try {
            sql = "select epc_order_id_seq.nextval from dual ";
            pstmtId = conn.prepareStatement(sql);
            rset = pstmtId.executeQuery();
            if(rset.next()) {
                orderId = rset.getInt(1);
            } rset.close();
            pstmtId.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmtId != null) { pstmtId.close(); } } catch (Exception ee) {}
        }
        return orderId;
    }
    
    
    public String isOrderBelongCust(String custId, int smcOrderId) {
    	String str = "";
        Connection conn = null;
        
        try {
            conn = epcDataSource.getConnection();
            str = isOrderBelongCust(conn, custId, smcOrderId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return str;
    }
    
    
    public String isOrderBelongCust(Connection conn, String custId, int smcOrderId) {
    	PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        String str = "NOT_BELONG";
        
        try {
            sql = "select order_reference from epc_order where order_id = ? and cust_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, custId); // cust_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                str = StringHelper.trim(rset.getString("order_reference"));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return str;
    }


    public boolean isOrderLocked(String custId, int smcOrderId) {
        boolean isLocked = false;
        Connection conn = null;
        
        try {
            conn = epcDataSource.getConnection();
            isLocked = isOrderLocked(conn, custId, smcOrderId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isLocked;
    }
    
    
    public boolean isOrderLocked(Connection conn, String custId, int smcOrderId) {
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        boolean isLocked = true;
        
        try {
            sql = "select 1 from epc_order where order_id = ? and cust_id = ? and order_status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, custId); // cust_id
            pstmt.setString(3, "I"); // order_status
            rset = pstmt.executeQuery();
            if(rset.next()) {
                isLocked = false;
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();

            isLocked = true;
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return isLocked;
    }
    
    
    public String isCaseValid(int orderId, String caseId) {
    	String str = "";
    	Connection conn = null;
    	PreparedStatement pstmt = null;
    	ResultSet rset = null;
    	String sql = "";
    	String cancelReceipt = "";
    	
    	try {
    		conn = epcDataSource.getConnection();
    		
    		sql = "select cancel_receipt from epc_order_case where order_id = ? and case_id = ? ";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setInt(1, orderId); // order_id
    		pstmt.setString(2, caseId); // case_id
    		rset = pstmt.executeQuery();
    		if(rset.next()) {
    			cancelReceipt = StringHelper.trim(rset.getString("cancel_receipt"));
    			
    			if("".equals(cancelReceipt)) {
    				str = "VALID_CASE";
    			} else {
    				str = "ALREADY_CANCELLED";
    			}
    		} else {
    			// not found
    			str = "CASE_NOT_UNDER_ORDER";
    		} rset.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    		
    		str = e.getMessage();
    	} finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
    		try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
    	}
    	
    	return str;
    }
    
    
    public String getCurrentQuoteGuid(int smcOrderId, int smcQuoteId) {
    	String str = "";
        Connection conn = null;
        
        try {
            conn = epcDataSource.getConnection();
            str = getCurrentQuoteGuid(conn, smcOrderId, smcQuoteId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return str;
    }
    
    
    public String getCurrentQuoteGuid(Connection conn, int smcOrderId, int smcQuoteId) {
    	PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        String quoteGuid = "";
        
        try {
            sql = "select quote_id, cpq_quote_guid from epc_order_quote where order_id = ? and quote_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setInt(2, smcQuoteId); // quote_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
            	quoteGuid = epcSecurityHelper.encode(StringHelper.trim(rset.getString("cpq_quote_guid")));
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return quoteGuid;
    }
    
    
    public boolean updateQuoteGuid(int smcOrderId, int smcQuoteId, String originalQuoteGuid, String newQuoteGuid, String remark) {
    	boolean isUpdate = false;
        Connection conn = null;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            isUpdate = updateQuoteGuid(conn, smcOrderId, smcQuoteId, originalQuoteGuid, newQuoteGuid, remark);
            
            if(isUpdate) {
            	conn.commit();
            } else {
            	conn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
        	try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
    
    
    public boolean updateQuoteGuid(Connection conn, int smcOrderId, int smcQuoteId, String originalQuoteGuid, String newQuoteGuid, String remark) {
    	PreparedStatement pstmt = null;
        String sql = "";
        boolean isUpdate = false;
        
        try {
            sql = "update epc_order_quote " +
//                  "   set cpq_quote_guid = ? " +
                  "   set cpq_quote_guid_submit = ? " +
                  " where order_id = ? and quote_id = ? and cpq_quote_guid = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newQuoteGuid); // cpq_quote_guid - new quote guid
            pstmt.setInt(2, smcOrderId); // order_id
            pstmt.setInt(3, smcQuoteId); // quote_id
            pstmt.setString(4, originalQuoteGuid); // cpq_quote_guid - original quote guid
            pstmt.executeUpdate();
            
            // create quote movement history
            sql = "insert into epc_order_quote_hist ( " +
                  "  hist_id, order_id, quote_id, old_quote_guid, new_quote_guid, " +
                  "  remark, create_date " + 
                  ") values ( " +
                  "  epc_order_id_seq.nextval,?,?,?,?, " +
                  "  substr(?,1,400),sysdate " +
                  ") ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setInt(2, smcQuoteId); // quote_id
            pstmt.setString(3, originalQuoteGuid); // cpq_quote_guid - original quote guid
            pstmt.setString(4, newQuoteGuid); // cpq_quote_guid - new quote guid
            pstmt.setString(5, remark); // remark 
            pstmt.executeUpdate();
            // end of create quote movement history
            
            isUpdate = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
    
    
    public String genOrderRef() {
        return "SMC" + new java.util.Date().getTime();
    }
    
    
    public int getOrderIdByQuoteGuid(String quoteGuid) {
        int smcOrderId = 0;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "select order_id " +
                  " from epc_order_quote " +
                  " where cpq_quote_guid = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, quoteGuid); // cpq_quote_guid
            rset = pstmt.executeQuery();
            if(rset.next()) {
                smcOrderId = rset.getInt("order_id");
            } rset.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return smcOrderId;
    }


    

    
    public ArrayList<EpcSmcQuote> getQuoteByOrderId(int orderId) {
    	Connection conn = null;
    	PreparedStatement pstmt = null;
    	ResultSet rset = null;
        String sql = "";
        ArrayList<EpcSmcQuote> quoteList = new ArrayList<EpcSmcQuote>();
        EpcSmcQuote epcSmcQuote = null;
        
        try {
            conn = epcDataSource.getConnection();
            sql = " select quote_id, cpq_quote_guid from epc_order_quote a where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            while(rset.next()) {
            	epcSmcQuote = new EpcSmcQuote();
            	epcSmcQuote.setOrderId(orderId);
            	epcSmcQuote.setQuoteId(rset.getInt("quote_id"));
            	epcSmcQuote.setQuoteGuid(StringHelper.trim(rset.getString("cpq_quote_guid")));
            	
            	quoteList.add(epcSmcQuote);
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            e.printStackTrace();
            
            quoteList = null;
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return quoteList;
    }


    public ArrayList<EpcSmcQuote> getQuoteInEpcByOrderId(int orderId) {
    	Connection conn = null;
    	PreparedStatement pstmt = null;
        PreparedStatement pstmtCase = null;
    	ResultSet rset = null;
        ResultSet rsetCase = null;
        String sql = "";
        ArrayList<EpcSmcQuote> quoteList = new ArrayList<EpcSmcQuote>();
        EpcSmcQuote epcSmcQuote = null;
//        EpcQuote epcQuote = null;
        int smcQuoteId = 0;
        int cnt = 0;
        
        try {
            conn = epcDataSource.getConnection();

            sql = "select count(1) from epc_order_case where order_id = ? and quote_id = ? ";
            pstmtCase = conn.prepareStatement(sql);

            sql = " select quote_id, cpq_quote_guid from epc_order_quote a where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            while(rset.next()) {
//                epcQuote = getCPQQuoteInEpc(orderId, rset.getInt("quote_id"));
                smcQuoteId = rset.getInt("quote_id");

                // check whether the quote has quote item
                //  if no quote item, NOT include it when submit quote
                pstmtCase.setInt(1, orderId); // order_id
                pstmtCase.setInt(2, smcQuoteId); // quote_id
                rsetCase = pstmtCase.executeQuery();
                if(rsetCase.next()) {
                    cnt = rsetCase.getInt(1);
                } rsetCase.close();

                if(cnt > 0) {
                    epcSmcQuote = new EpcSmcQuote();
                    epcSmcQuote.setOrderId(orderId);
                    epcSmcQuote.setQuoteId(smcQuoteId);
                    epcSmcQuote.setQuoteGuid(StringHelper.trim(rset.getString("cpq_quote_guid")));
//                epcSmcQuote.setEpcQuote(epcQuote);
                
                    quoteList.add(epcSmcQuote);
                }
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            e.printStackTrace();
            
            quoteList = null;
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(rsetCase != null) { rsetCase.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(pstmtCase != null) { pstmtCase.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return quoteList;
    }


    /***
     * get tmp stored quote & quote items json string
     *  and convert to object
     * 
     * @param orderId
     * @param quoteId
     * @return
     */
    public EpcQuote getCPQQuoteInEpc(int orderId, int quoteId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        EpcQuote epcQuote = null;
        EpcQuoteItem[] epcQuoteItemArray = null;
        EpcQuoteItem epcQuoteItem = null;
        String quoteContentStr = "";
        String quoteItemContentStr = "";
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayList<EpcQuoteItem> itemList = new ArrayList<>();
        String cmsItemMappingString = "";
        String caseId = "";

        try {
            conn = epcDataSource.getConnection();

            sql = " select quote_content from epc_order_quote a where order_id = ? and quote_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setInt(2, quoteId); // quote_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                quoteContentStr = StringHelper.trim(rset.getString("quote_content"));
            } rset.close();
            pstmt.close();

            if(StringUtils.isNotBlank(quoteContentStr)) {
                epcQuote = objectMapper.readValue(quoteContentStr, EpcQuote.class);

                // then get quote item(s)
                sql = "select /*+ index (a EPC_ORDER_CASE_PK) */ " +
                      "       case_id, quote_item_content, cms_item_mapping " +
                      "  from epc_order_case a " +
                      " where order_id = ? " +
                      "   and quote_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId); // order_id
                pstmt.setInt(2, quoteId); // quote_id
                rset = pstmt.executeQuery();
                while(rset.next()) {
                    caseId = StringHelper.trim(rset.getString("case_id"));
                    quoteItemContentStr = StringHelper.trim(rset.getString("quote_item_content"));
                    cmsItemMappingString = StringHelper.trim(rset.getString("cms_item_mapping"));

                    if(StringUtils.isNotBlank(quoteItemContentStr)) {
                        epcQuoteItem = objectMapper.readValue(quoteItemContentStr, EpcQuoteItem.class);

                        if(StringUtils.isNotBlank(cmsItemMappingString)) {
                            epcQuoteItem.setCmsItemMapping(objectMapper.readValue(cmsItemMappingString, HashMap.class));
                        }

                        itemList.add(epcQuoteItem);
                    }
                }

                if(!itemList.isEmpty()) {
                    epcQuoteItemArray = new EpcQuoteItem[itemList.size()];
                    for(int i = 0; i < itemList.size(); i++) {
                        epcQuoteItemArray[i] = itemList.get(i);
                    }
                    epcQuote.setItems(epcQuoteItemArray);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcQuote = null;
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return epcQuote;
    }

    
    public void getQuotes(ArrayList<EpcSmcQuote> quoteList) {
    	EpcSmcQuote epcSmcQuote = null;
    	if(quoteList != null) {
    		for(int i = 0; i < quoteList.size(); i++) {
    			epcSmcQuote = quoteList.get(i);
    			epcSmcQuote.setEpcQuote(epcQuoteHandler.getQuoteInfo(epcSmcQuote.getQuoteGuid()));
    		}
    	}
    }
    
    
    public void getQuotesWithItemValidation(ArrayList<EpcSmcQuote> quoteList) {
    	EpcSmcQuote epcSmcQuote = null;
    	if(quoteList != null) {
    		for(int i = 0; i < quoteList.size(); i++) {
    			epcSmcQuote = quoteList.get(i);
    			epcSmcQuote.setEpcQuote(epcQuoteHandler.getQuoteInfo(epcSmcQuote.getQuoteGuid(), "validation"));
    		}
    	}
    }


    public void removeQuoteMetaData(ArrayList<EpcSmcQuote> epcQuoteList) {
        for(EpcSmcQuote epcSmcQuote: epcQuoteList) {
            if(epcSmcQuote.getEpcQuote() != null) {
                for(EpcQuoteItem epcQuoteItem: epcSmcQuote.getEpcQuote().getItems()) {
                    epcQuoteItem.setMetaDataLookup(null);
                    epcQuoteItem.setMetaTypeLookup(null);
                }
            }
        }
    }


    public TreeMap<String, String> getOrderStatusDescMap() {
    	Connection conn = null;
    	TreeMap<String, String> orderStatusMap = null;
    	
    	try {
    		conn = epcDataSource.getConnection();
    		
    		orderStatusMap = getOrderStatusDescMap(conn);
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
    	}
    	return orderStatusMap;
    }
    
    
    public TreeMap<String, String> getOrderStatusDescMap(Connection conn) {
    	TreeMap<String, String> orderStatusMap = new TreeMap<String, String>();
    	PreparedStatement pstmt = null;
    	ResultSet rset = null;
    	String sql = "";
    	
    	try {
            // value_str1 = eng desc / value_str2 = chi desc
    		sql = "select key_str1, value_str1, value_str2 " +
                  "  from epc_control_tbl " +
                  " where rec_type = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "ORDER_STATUS"); // rec_type
            rset = pstmt.executeQuery();
            while (rset.next()) {
                orderStatusMap.put(StringHelper.trim(rset.getString("key_str1")), StringHelper.trim(rset.getString("value_str1")));

                orderStatusMap.put(StringHelper.trim(rset.getString("key_str1") + "_CHI"), StringHelper.trim(rset.getString("value_str2")));
            } rset.close();
            pstmt.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
    	}
    	return orderStatusMap;
    }


    public TreeMap<String, String> getItemStatusDescMap() {
        Connection conn = null;
        TreeMap<String, String> itemStatusMap = null;
        
        try {
            conn = epcDataSource.getConnection();
            
            itemStatusMap = getItemStatusDescMap(conn);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return itemStatusMap;
    }


    public TreeMap<String, String> getItemStatusDescMap(Connection conn) {
        TreeMap<String, String> itemStatusMap = new TreeMap<String, String>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            // value_str1 = eng desc / value_str2 = chi desc
            sql = "select key_str1, value_str1, value_str2 " +
                    "  from epc_control_tbl " +
                    " where rec_type = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "ORDER_ITEM_STATUS"); // rec_type
            rset = pstmt.executeQuery();
            while (rset.next()) {
                itemStatusMap.put(StringHelper.trim(rset.getString("key_str1")), StringHelper.trim(rset.getString("value_str1")));

                itemStatusMap.put(StringHelper.trim(rset.getString("key_str1") + "_CHI"), StringHelper.trim(rset.getString("value_str2")));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return itemStatusMap;
    }
    
    
    public EpcOrderInfo getOrderSlimInfo(int orderId) {
    	EpcOrderInfo epcOrderInfo = null;
    	Connection conn = null;
//    	String orderStatus = "";
    	
    	try {
    		conn = epcDataSource.getConnection();
    		
    		epcOrderInfo = getOrderSlimInfo(conn, orderId);
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
    	}
    	return epcOrderInfo;
    }
    
    
    public EpcOrderInfo getOrderSlimInfo(Connection conn, int orderId) {
    	EpcOrderInfo epcOrderInfo = new EpcOrderInfo();
        epcOrderInfo.setOrderId(orderId);
    	PreparedStatement pstmt = null;
    	ResultSet rset = null;
    	String sql = "";
        TreeMap<String, String> orderStatusMap = getOrderStatusDescMap();
        String orderStatusDesc = "";
    	
    	try {
    		sql = "select cust_id, receipt_no, order_status, order_reference, to_char(order_date, 'yyyymmddhh24mi') as o_date, order_lang,place_order_channel, place_order_salesman " +
                  "  from epc_order where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if (rset.next()) {
            	epcOrderInfo.setCustId(StringHelper.trim(rset.getString("cust_id")));
            	epcOrderInfo.setOrderStatus(StringHelper.trim(rset.getString("order_status")));
            	epcOrderInfo.setReceiptNo(StringHelper.trim(rset.getString("receipt_no")));
                epcOrderInfo.setOrderReference(StringHelper.trim(rset.getString("order_reference")));
                epcOrderInfo.setOrderDate(StringHelper.trim(rset.getString("o_date")));
                epcOrderInfo.setOrderLang(StringHelper.trim(rset.getString("order_lang")));
                epcOrderInfo.setPlaceOrderChannel(StringHelper.trim(rset.getString("place_order_channel")));
                epcOrderInfo.setPlaceOrderSalesman(StringHelper.trim(rset.getString("place_order_salesman")));
                orderStatusDesc = StringHelper.trim(orderStatusMap.get(StringHelper.trim(rset.getString("order_status"))));
                if("".equals(orderStatusDesc)) {
                    orderStatusDesc = "???";
                }
                epcOrderInfo.setOrderStatusDesc(orderStatusDesc);

            } rset.close();
            pstmt.close();
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
    	}
    	return epcOrderInfo;
    }
    
    public BigDecimal getCourierFee(Integer orderId) {
    	try (Connection conn =epcDataSource.getConnection()){
			String sqlString="SELECT CHARGE_AMOUNT FROM EPC_ORDER_CHARGE WHERE ORDER_ID = ? AND CHARGE_CODE = 'E96'";
			try(PreparedStatement ps=conn.prepareStatement(sqlString)){
				ps.setInt(1, orderId);
				try(ResultSet rs=ps.executeQuery()){
					if(rs.next()) {
						BigDecimal CHARGE_AMOUNT= rs.getBigDecimal(1);
						return CHARGE_AMOUNT;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return BigDecimal.ZERO;
    }
    
    public boolean updateOrderStatusToComplete(int orderId, String fulfillUser, String fulfillSalesman, String fulfillChannel, String fulfillLocation) {
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        boolean isUpdate = false;
        int totalCnt = 0;
        int filfillCnt = 0;
        String oldOrderStatus = "";
        EpcLogOrderStatus epcLogOrderStatus = null;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "select sum(t_cnt) as ttl_cnt, sum(f_cnt) as fulfill_cnt " + 
            	  "  from ( " + 
            	  " select 1 as t_cnt, case when invoice_no is not null then 1 else 0 end as f_cnt " + 
            	  "  from epc_order_item  " + 
            	  " where order_id = ? " + 
            	  "   and item_cat in (?,?,?,?,?,?) " + 
            	  ") ";
            pstmt = conn.prepareStatement(sql);
        	pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, EpcItemCategory.DEVICE); // item_cat - DEVICE
            pstmt.setString(3, EpcItemCategory.SIM); // item_cat - SIM
            pstmt.setString(4, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
            pstmt.setString(5, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
            pstmt.setString(6, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
            pstmt.setString(7, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG

        	rset = pstmt.executeQuery();
        	if(rset.next()) {
        		totalCnt = rset.getInt("ttl_cnt");
                filfillCnt = rset.getInt("fulfill_cnt");
        	} rset.close(); rset = null;
            pstmt.close();
        	
        	if(totalCnt == filfillCnt) {
                // get current order status
                sql = "select order_status " +
                      "  from epc_order " +
                      " where order_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId); // order_id
                rset = pstmt.executeQuery();
                if(rset.next()) {
                    oldOrderStatus = StringHelper.trim(rset.getString("order_status"));
                } rset.close();
                pstmt.close();
                // end of get current order status


        		sql = "update epc_order " +
        	          "   set order_status = ? " +
        			  " where order_id = ? ";
        		pstmt = conn.prepareStatement(sql);
        		pstmt.setString(1, "CO"); // order_status - complete
            	pstmt.setInt(2, orderId); // order_id
            	pstmt.executeUpdate();
                pstmt.close();


                // create log
                epcLogOrderStatus = new EpcLogOrderStatus();
                epcLogOrderStatus.setOrderId(orderId);
                epcLogOrderStatus.setOldOrderStatus(oldOrderStatus);
                epcLogOrderStatus.setNewOrderStatus("CO");
                epcLogOrderStatus.setCreateUser(fulfillUser);
                epcLogOrderStatus.setCreateSalesman(fulfillSalesman);
                epcLogOrderStatus.setCreateChannel(fulfillChannel);
                epcLogOrderStatus.setCreateLocation(fulfillLocation);
                epcOrderLogHandler.logOrderStatus(conn, epcLogOrderStatus);
                // end of create log
        	} else {
        		// no action
        	}
            
            conn.commit();
            
            isUpdate = true;
        } catch (Exception e) {
        	e.printStackTrace();
        	
        	try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        	try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
        	try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }


    public TreeMap<String, EpcOrderItemDetail> getItemDetails(int orderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        EpcOrderItemDetail epcOrderItemDetail = null;
        TreeMap<String, EpcOrderItemDetail> detailMap = new TreeMap<String, EpcOrderItemDetail>();
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "select c.cust_id, a.cpq_offer_guid, a.cpq_offer_desc, a.cpq_offer_desc_chi, b.cpq_item_desc, b.cpq_item_desc_chi, " +
                 "       b.warehouse, b.reserve_id, b.item_id, a.cust_num, a.subr_num, b.item_cat, b.parent_item_id " +
                 " from epc_order c, epc_order_case a, epc_order_item b " +
                 "where c.order_id = ? " +
                 "  and a.order_id = c.order_id " +
                 "  and b.order_id = a.order_id " +
                 "  and b.quote_id = a.quote_id " +
                 "  and b.case_id = a.case_id " +
                 "  and b.item_cat in (?,?,?,?,?,?) ";
//                 "  and b.invoice_no is null ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            pstmt.setString(2, EpcItemCategory.DEVICE); // item_cat - DEVICE
            pstmt.setString(3, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
            pstmt.setString(4, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
            pstmt.setString(5, EpcItemCategory.SIM); // item_cat - SIM
            pstmt.setString(6, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
            pstmt.setString(7, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
            rset = pstmt.executeQuery();
            while(rset.next()) {
                epcOrderItemDetail = new EpcOrderItemDetail();
                epcOrderItemDetail.setCustId(StringHelper.trim(rset.getString("cust_id")));
                epcOrderItemDetail.setItemId(StringHelper.trim(rset.getString("item_id")));
                epcOrderItemDetail.setItemCat(StringHelper.trim(rset.getString("item_cat")));
                epcOrderItemDetail.setCpqItemDesc(StringHelper.trim(rset.getString("cpq_item_desc")));
                epcOrderItemDetail.setCpqItemDescChi(StringHelper.trim(rset.getString("cpq_item_desc_chi")));
                epcOrderItemDetail.setCpqOfferGuid(StringHelper.trim(rset.getString("cpq_offer_guid")));
                epcOrderItemDetail.setCpqOfferDesc(StringHelper.trim(rset.getString("cpq_offer_desc")));
                epcOrderItemDetail.setCpqOfferDescChi(StringHelper.trim(rset.getString("cpq_offer_desc_chi")));
                epcOrderItemDetail.setWarehouse(StringHelper.trim(rset.getString("warehouse")));
                epcOrderItemDetail.setReserveId(StringHelper.trim(rset.getString("reserve_id")));
                epcOrderItemDetail.setCustNum(StringHelper.trim(rset.getString("cust_num")));
                epcOrderItemDetail.setSubrNum(StringHelper.trim(rset.getString("subr_num")));
                epcOrderItemDetail.setParentItemId(StringHelper.trim(rset.getString("parent_item_id")));
                
                detailMap.put(StringHelper.trim(rset.getString("item_id")), epcOrderItemDetail);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            detailMap = null;
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return detailMap;
    }
    
    
    public EpcCreateEPCRecordResult createEPCRecords(int orderId, ArrayList<EpcSmcQuote> quoteList) {
    	EpcCreateEPCRecordResult epcCreateEPCRecordResult = new EpcCreateEPCRecordResult();
    	Connection conn = null;
    	TreeMap<String, String> attrMap = null;
    	
    	try {
    		conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            attrMap = epcOrderAttrHandler.getOrderAttr(orderId);
            
            for(EpcSmcQuote q : quoteList) {
            	epcCreateEPCRecordResult = createEPCRecords(conn, orderId, q.getQuoteId(), q.getEpcQuote(), attrMap);
            	if(!"SUCCESS".equals(epcCreateEPCRecordResult.getResult())) {
            		throw new Exception(epcSecurityHelper.encode("cannot create EPC records for quote " + q.getQuoteGuid()));
            	}
            }
            
            conn.commit();
    	} catch (Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            epcCreateEPCRecordResult.setResult("FAIL");
            epcCreateEPCRecordResult.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    	
    	return epcCreateEPCRecordResult;
    }
    
    
    public EpcCreateEPCRecordResult createEPCRecords(Connection conn, int orderId, int quoteId, EpcQuote epcQuote, TreeMap<String, String> attrMap) {
        EpcCreateEPCRecordResult epcCreateEPCRecordResult = new EpcCreateEPCRecordResult();
//        String custId = StringHelper.trim(epcQuote.getCustomerRef());
//        String quoteGuid = StringHelper.trim(epcQuote.getId());
        HashMap<String, Object> contextData = null;
        EpcQuoteItem[] epcQuoteItems = null;
        EpcQuoteItem epcQuoteItem = null;
        EpcQuoteProductCandidate productCandidateObj = null;
        String smcCaseId = "";
        String custNum = "";
        String subrNum = "";
        String tmpDesc = "";
        String tmpDescChi = "";
        String effectiveDate = "";
        PreparedStatement pstmtCase = null;
        PreparedStatement pstmtItem = null;
        PreparedStatement pstmtVoucher = null;
        PreparedStatement pstmtCustProfile = null;
        ResultSet rsetCustProfile = null;
        String sql = "";
        HashMap<String, Object> metaDataLookupMap = null;
        HashMap<String, Object> childMap = null;
        Iterator<String> iter = null;
//        ArrayList<HashMap<String, Object>> smcCaseList = null;
//        HashMap<String, Object> smcCase = null;
//        HashMap<String, Object> smcCustInfo = null;
        String activationType = "";
        String processStatus = "";
        String mainLineItemId = "";
        TreeMap<String, EpcQuoteProductCandidate> mainLineItemMap = null;
        String logStr = "[createEPCRecords][orderId:" + orderId + "][quoteId:" + quoteId + "] ";
        
        try {
            // get process status
            processStatus = epcOrderProcessCtrlHandler.getProcessStatus(orderId, quoteId, epcOrderProcessCtrlHandler.processCreateEPCRecord);
            if("NOT_EXIST".equals(processStatus)) {
                throw new Exception("createEPCRecords process for quoteId " + quoteId + " is not existed, program exit");
            } else if(epcOrderProcessCtrlHandler.processStatusDone.equals(processStatus)) {
logger.info(logStr + epcSecurityHelper.encode("createEPCRecords process for quoteId " + quoteId + " is DONE, no need to perform again"));
            } else {
                contextData = epcQuote.getContextData();
//                smcOrderId = orderId;
//                smcQuoteId = Integer.parseInt(StringHelper.trim((String)contextData.get("SMCQuoteId")));
//                smcCaseList = (ArrayList<HashMap<String, Object>>)contextData.get("SMCCases");
//                smcCaseList = epcCustProfileHandler.getTmpCustProfile(orderId, custId); // get cust info from epc table instead of quote context, kerrytsang, 20200908
                epcQuoteItems = epcQuote.getItems();
                

                // prepare statement
                sql = "insert into epc_order_case ( " +
                      "  order_id, quote_id, case_id, cpq_offer_guid, cpq_offer_desc, " +
                      "  cpq_offer_desc_chi, cust_num, subr_num, action_type, effective_date, " +
                      "  activation_type " +
                      ") values ( " +
                      "  ?,?,?,?,?, " +
                      "  ?,?,?,?,to_date(?,'yyyymmddhh24miss'), " +
                      "  ? " +
                      ") ";
                pstmtCase = conn.prepareStatement(sql);
                
                sql = "select cust_num, subr_num, effective_date, activation_type " +
                      "  from epc_order_cust_profile " +
                	  " where order_id = ? " +
                      "   and item_id = ? " +
                	  "   and status = ? ";
                pstmtCustProfile = conn.prepareStatement(sql);

                sql = "insert into epc_order_item ( " +
                      "  order_id, quote_id, case_id, item_id, parent_item_id, " +
                      "  cpq_item_guid, item_cat, item_code, cpq_item_desc, cpq_item_desc_chi, " +
                      "  cpq_item_value, item_charge, reserve_id, warehouse, delivery_id, " +
                      "  serial " +
                      ") values ( " +
                      "  ?,?,?,?,?, " +
                      "  ?,?,?,?,?, " +
                      "  ?,?,?,?,?, " +
                      "  ? " +
                      ") ";
                pstmtItem = conn.prepareStatement(sql);
                
                sql = "insert into epc_order_voucher ( " +
          		      "  order_id, case_id, item_id, assign_redeem, voucher_guid, " +
          			  "  voucher_master_id, status, create_date, modify_date " +
          		      ") values ( " +
          		      "  ?,?,?,?,?, " +
                      "  ?,?,sysdate,sysdate " +
          			  ") ";
          		pstmtVoucher = conn.prepareStatement(sql);
                // end of prepare statement


                for(int i = 0; i < epcQuoteItems.length; i++) {
                    epcQuoteItem = epcQuoteItems[i];
                    productCandidateObj = epcQuoteItem.getProductCandidateObj();
                    metaDataLookupMap = epcQuoteItem.getMetaDataLookup();

                    // reset param
                    custNum = ""; // reset
                    subrNum = ""; // reset
                    activationType = ""; // reset
                    effectiveDate = ""; // reset
                    mainLineItemId = ""; // reset
                    // reset param

                    smcCaseId = productCandidateObj.getId();
                    childMap = (HashMap<String, Object>)metaDataLookupMap.get(smcCaseId);
                    tmpDesc = StringHelper.trim((String)childMap.get("name")); // offer desc
                    tmpDescChi = StringHelper.trim((String)childMap.get("name")); // offer desc

                    // get cust / subr num if any
                    mainLineItemMap = new TreeMap<String, EpcQuoteProductCandidate>();
                    epcQuoteHandler.getMainLineItem(mainLineItemMap, productCandidateObj);
                    iter = mainLineItemMap.keySet().iterator();
                    while(iter.hasNext()) {
                    	mainLineItemId = iter.next();
                    	break;
                    }
                    
                    if(StringUtils.isNotBlank(mainLineItemId)) {
                          pstmtCustProfile.setInt(1, orderId); // order_id
                          pstmtCustProfile.setString(2, mainLineItemId); // item_id - item id of mobile product spec
                          pstmtCustProfile.setString(3, "A"); // status - Active
                          rsetCustProfile = pstmtCustProfile.executeQuery();
                          if(rsetCustProfile.next()) {
                        	  custNum = epcSecurityHelper.validateId(StringHelper.trim((String)rsetCustProfile.getString("cust_num")));
                        	  subrNum = epcSecurityHelper.validateId(StringHelper.trim((String)rsetCustProfile.getString("subr_num")));
                        	  effectiveDate = StringHelper.trim((String)rsetCustProfile.getString("effective_date"));
                        	  activationType = StringHelper.trim((String)rsetCustProfile.getString("activation_type"));
                          } rsetCustProfile.close(); rsetCustProfile = null;
                    }
                    // end of get cust / subr num if any

                    // create EPC_ORDER_CASE
                    pstmtCase.setString(1, orderId + ""); // order_id
                    pstmtCase.setString(2, quoteId + ""); // quote_id
                    pstmtCase.setString(3, smcCaseId); // case_id
                    pstmtCase.setString(4, epcQuoteItem.getProductId()); // cpq_quote_guid
                    pstmtCase.setString(5, tmpDesc); // cpq_offer_desc
                    pstmtCase.setString(6, tmpDescChi); // cpq_offer_desc_chi
                    pstmtCase.setString(7, custNum); // cust_num
                    pstmtCase.setString(8, subrNum); // subr_num
                    pstmtCase.setString(9, "ADD"); // action_type
                    pstmtCase.setString(10, effectiveDate); // effective_date
                    pstmtCase.setString(11, activationType); // activation_type
//                    pstmtCase.executeUpdate();
                    pstmtCase.addBatch();
                    // end of create EPC_ORDER_CASE

                    
                    // create EPC_ORDER_ITEM (recursive)
                    createEPCItemRecords(orderId, quoteId, smcCaseId, productCandidateObj, "", pstmtItem, pstmtVoucher);
                    // end of create EPC_ORDER_ITEM (recursive)
                }
                
                pstmtCase.executeBatch();
                pstmtItem.executeBatch();
//                pstmtVoucher.executeBatch(); // pstmtVoucher use executeQuery inside createEPCItemRecords()

//                conn.commit();
                
                // update process ctrl
                epcOrderProcessCtrlHandler.updateProcess(orderId, quoteId, epcOrderProcessCtrlHandler.processCreateEPCRecord, epcOrderProcessCtrlHandler.processStatusDone, "");
logger.info(logStr + epcSecurityHelper.encode("createEPCRecords process for " + quoteId + " is updated to DONE"));
            }
            
            epcCreateEPCRecordResult.setResult("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            
//            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            epcCreateEPCRecordResult.setResult("FAIL");
            epcCreateEPCRecordResult.setErrMsg(e.getMessage());
        } finally {
//            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
//            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcCreateEPCRecordResult;
    }
    
    
    public void createEPCItemRecords(
        int smcOrderId, int smcQuoteId, String smcCaseId, EpcQuoteProductCandidate productCandidateObj, String parentId, 
        PreparedStatement pstmtItem, PreparedStatement pstmtVoucher
    ) throws Exception {
        String itemId = productCandidateObj.getId();
        String guid = productCandidateObj.getEntityID();
        String itemCat = StringHelper.trim(productCandidateObj.getItemCat());
        String itemCode = StringHelper.trim(productCandidateObj.getItemCode());
        String cpqItemValue = StringHelper.trim(productCandidateObj.getCpqItemValue());
        String cpqItemDesc = productCandidateObj.getCpqItemDesc();
        String cpqItemDescChi = productCandidateObj.getCpqItemDescChi();
        BigDecimal itemCharge = productCandidateObj.getItemCharge();
        ArrayList<EpcConfiguredValue> configuredValueList = productCandidateObj.getConfiguredValue();
        ArrayList<EpcCharacteristicUse> epcCharacteristicUseList = productCandidateObj.getCharacteristicUse();
        String premium = StringHelper.trim(productCandidateObj.getPremium());
        String templateName = StringHelper.trim(productCandidateObj.getTemplateName());
        String catalogItemDesc = StringHelper.trim(productCandidateObj.getCatalogItemDesc());
        BigDecimal catalogRrp = productCandidateObj.getCatalogRrp();
        String reserveId = "";
        String warehouse = "";
        String serial = "";
        String deliveryIdStr = "";
        String[] tmpArray = null;
        final String configuredValueSimNumber = "sim_number";
        final String configuredValueImsi = "imsi";
        final String configuredValueMsisdn = "msisdn";
        final String configuredValueTradeInNoteReferenceNo = "Trade In Note Reference No";
        final String characteristicUsePlanBillCode = "Plan_Bill_Code";
        final String characteristicUsePlan = "Plan";
        final String templateNameMobileLineSpecification = "Mobile_Line_Specification";

//        String logStr = "[createEPCItemRecords][" + itemId + "] ";
//logger.info(logStr + "itemCat:" + itemCat + ", reserveId:" + reserveId + ", warehouse:" + warehouse);
//logger.info(logStr + "start");

        if(EpcItemCategory.DEVICE.equals(itemCat)
        	|| EpcItemCategory.SCREEN_REPLACE.equals(itemCat)
        	|| EpcItemCategory.APPLECARE.equals(itemCat)
            || EpcItemCategory.SIM.equals(itemCat)
            || EpcItemCategory.PLASTIC_BAG.equals(itemCat)
        ) {
//            warehouse = epcStockHandler.getWarehouse(itemCode);
//            serial = epcStockHandler.getSerial(itemCode);
            tmpArray = epcStockHandler.getWarehouseAndSerial(itemCode);
            warehouse = tmpArray[0];
            serial = tmpArray[1];
        } else if(EpcItemCategory.VOUCHER_REDEEMED.equals(itemCat) || EpcItemCategory.VOUCHER_ASSIGNED.equals(itemCat)) {
        	for(EpcConfiguredValue v : configuredValueList) {
        		if("Master_Voucher_Id".equals(v.getName())) {
        			cpqItemValue = v.getValue(); // store master voucher id into cpq_item_value
        			break;
        		}
        	}
        	
        	// create epc_order_voucher record
        	pstmtVoucher.setString(1, smcOrderId + ""); // order_id
        	pstmtVoucher.setString(2, smcCaseId); // case_id
        	pstmtVoucher.setString(3, itemId); // item_id
        	if("VOUCHER_REDEEMED".equals(itemCat)) {
        		pstmtVoucher.setString(4, "REDEEM"); // assign_redeem
        	} else {
        		pstmtVoucher.setString(4, "ASSIGN"); // assign_redeem
        	}
        	pstmtVoucher.setString(5, guid); // voucher_guid
        	pstmtVoucher.setString(6, cpqItemValue); // voucher_master_id
        	pstmtVoucher.setString(7, "A"); // status
//        	pstmtVoucher.addBatch();
        	pstmtVoucher.executeQuery();
        }
        
//logger.info("==>" + smcOrderId + "," + smcQuoteId + "," + smcCaseId + "," + itemId + "," + cpqItemDesc);
        // create / update own record
        pstmtItem.setString(1, smcOrderId + ""); // order_id
        pstmtItem.setString(2, smcQuoteId + ""); // quote_id
        pstmtItem.setString(3, smcCaseId); // case_id
        pstmtItem.setString(4, itemId); // item_id
        pstmtItem.setString(5, parentId); // parent_item_id
        pstmtItem.setString(6, guid); // cpq_item_guid
        pstmtItem.setString(7, itemCat); // item_cat
        pstmtItem.setString(8, itemCode); // item_code
        pstmtItem.setString(9, cpqItemDesc); // cpq_item_desc
        pstmtItem.setString(10, cpqItemDescChi); // cpq_item_desc_chi
        pstmtItem.setString(11, cpqItemValue); // cpq_item_value
        if(itemCharge == null) {
            pstmtItem.setNull(12, Types.INTEGER); // item_charge
        } else {
            pstmtItem.setBigDecimal(12, itemCharge); // item_charge
        }
        pstmtItem.setString(13, reserveId); // reserve_id
        if(StringUtils.isNotBlank(warehouse)) {
        	pstmtItem.setString(14, warehouse); // warehouse
        } else {
        	pstmtItem.setNull(14, Types.VARCHAR); // warehouse
        }
        if(StringUtils.isNotBlank(deliveryIdStr)) {
        	pstmtItem.setInt(15, Integer.parseInt(deliveryIdStr)); // delivery_id
        } else {
        	pstmtItem.setNull(15, Types.INTEGER); // delivery_id
        }
        if(StringUtils.isNotBlank(serial)) {
        	pstmtItem.setString(16, serial); // serial
        } else {
        	pstmtItem.setNull(16, Types.VARCHAR); // serial
        }
        if(StringUtils.isNotBlank(premium)) {
            pstmtItem.setString(17, premium); // premium
        } else {
            pstmtItem.setNull(17, Types.VARCHAR); // premium
        }
        if(StringUtils.isNotBlank(templateName)) {
            pstmtItem.setString(18, templateName); // template_name
        } else {
            pstmtItem.setNull(18, Types.VARCHAR); // template_name
        }
        if(StringUtils.isNotBlank(catalogItemDesc)) {
            pstmtItem.setString(19, catalogItemDesc); // catalog_item_desc
        } else {
            pstmtItem.setNull(19, Types.VARCHAR); // catalog_item_desc
        }
        if(catalogRrp == null) {
            pstmtItem.setNull(20, Types.INTEGER); // catalog_rrp
        } else {
            pstmtItem.setBigDecimal(20, catalogRrp); // catalog_rrp
        }
        
        pstmtItem.addBatch();
        // end of create / update own record
        
        // loop thru configuredValueList
        for(EpcConfiguredValue v : configuredValueList) {
        	if((configuredValueSimNumber.equals(v.getName()) && templateNameMobileLineSpecification.equals(templateName))
        		|| (configuredValueImsi.equals(v.getName()) && templateNameMobileLineSpecification.equals(templateName))
        		|| (configuredValueMsisdn.equals(v.getName()) && templateNameMobileLineSpecification.equals(templateName))
        		|| configuredValueTradeInNoteReferenceNo.equals(v.getName())
        	) {
	        	pstmtItem.setString(1, smcOrderId + ""); // order_id
	            pstmtItem.setString(2, smcQuoteId + ""); // quote_id
	            pstmtItem.setString(3, smcCaseId); // case_id
	            pstmtItem.setString(4, itemId + "_" + v.getName()); // item_id
	            pstmtItem.setString(5, parentId); // parent_item_id
	            pstmtItem.setString(6, v.getId()); // cpq_item_guid

                if(configuredValueSimNumber.equals(v.getName())) {
                    pstmtItem.setString(7, EpcItemCategory.SIM); // item_cat
                } else if(configuredValueMsisdn.equals(v.getName())) {
                    pstmtItem.setString(7, EpcItemCategory.MSISDN); // item_cat
                } else {
                    pstmtItem.setNull(7, Types.VARCHAR); // item_cat
                }

                if(configuredValueSimNumber.equals(v.getName())) {
                    pstmtItem.setNull(8, Types.VARCHAR); // item_code
                } else {
                    pstmtItem.setString(8, v.getValue()); // item_code
                }

                pstmtItem.setString(9, v.getName()); // cpq_item_desc
                pstmtItem.setString(10, v.getName()); // cpq_item_desc_chi

	            if(configuredValueTradeInNoteReferenceNo.equals(v.getName())) {
	            	pstmtItem.setString(11, ""); // cpq_item_value
	            } else {
	            	pstmtItem.setString(11, v.getValue()); // cpq_item_value
	            }

	            pstmtItem.setNull(12, Types.INTEGER); // item_charge
	            pstmtItem.setNull(13, Types.VARCHAR); // reserve_id
	            if(configuredValueSimNumber.equals(v.getName())) {
	            	pstmtItem.setString(14, "AP"); // warehouse
	            } else {
	            	pstmtItem.setNull(14, Types.VARCHAR); // warehouse
	            }
	            if(StringUtils.isNotBlank(deliveryIdStr)) {
	            	pstmtItem.setInt(15, Integer.parseInt(deliveryIdStr)); // delivery_id
	            } else {
	            	pstmtItem.setNull(15, Types.INTEGER); // delivery_id
	            }
	            if(configuredValueSimNumber.equals(v.getName())) {
	            	pstmtItem.setString(16, "S"); // serial
	            } else {
	            	pstmtItem.setNull(16, Types.VARCHAR); // serial
	            }
                if(StringUtils.isNotBlank(premium)) {
                    pstmtItem.setString(17, premium); // premium
                } else {
                    pstmtItem.setNull(17, Types.VARCHAR); // premium
                }
                pstmtItem.setNull(18, Types.VARCHAR); // template_name
                pstmtItem.setNull(19, Types.VARCHAR); // catalog_item_desc
                pstmtItem.setNull(20, Types.INTEGER); // catalog_rrp
//logger.info("==>" + smcOrderId + "," + smcQuoteId + "," + smcCaseId + "," + itemId + "_" + v.getName() + "," + v.getName());
	            pstmtItem.addBatch();
        	}
        }
        // end of loop thru configuredValueList

        // loop thru characteristicUseList
        for (EpcCharacteristicUse v : epcCharacteristicUseList) {
            if((characteristicUsePlanBillCode.equals(v.getName()) && templateNameMobileLineSpecification.equals(templateName))
                || (characteristicUsePlan.equals(v.getName()) && templateNameMobileLineSpecification.equals(templateName))
            ) {
                pstmtItem.setString(1, smcOrderId + ""); // order_id
                pstmtItem.setString(2, smcQuoteId + ""); // quote_id
                pstmtItem.setString(3, smcCaseId); // case_id
                pstmtItem.setString(4, itemId + "_" + v.getName()); // item_id
                pstmtItem.setString(5, parentId); // parent_item_id
                pstmtItem.setString(6, v.getId()); // cpq_item_guid

                if(characteristicUsePlanBillCode.equals(v.getName())) {
                    pstmtItem.setString(7, EpcItemCategory.PLAN); // item_cat
                } else if(characteristicUsePlan.equals(v.getName())) {
                    pstmtItem.setString(7, EpcItemCategory.PLAN_NAME); // item_cat
                }

                if(v.getValue() != null && v.getValue().size() > 0) {
                    pstmtItem.setString(8, (String)(v.getValue().get(0))); // item_code
                } else {
                    pstmtItem.setString(8, ""); // item_code
                }
                pstmtItem.setString(9, v.getName()); // cpq_item_desc
                pstmtItem.setString(10, v.getName()); // cpq_item_desc_chi
                pstmtItem.setString(11, ""); // cpq_item_value
                pstmtItem.setNull(12, Types.INTEGER); // item_charge
                pstmtItem.setNull(13, Types.VARCHAR); // reserve_id
                pstmtItem.setNull(14, Types.VARCHAR); // warehouse
                pstmtItem.setNull(15, Types.INTEGER); // delivery_id
                pstmtItem.setNull(16, Types.VARCHAR); // serial
                pstmtItem.setNull(17, Types.VARCHAR); // premium
                pstmtItem.setNull(18, Types.VARCHAR); // template_name
                pstmtItem.setNull(19, Types.VARCHAR); // catalog_item_desc
                pstmtItem.setNull(20, Types.INTEGER); // catalog_rrp

                pstmtItem.addBatch();
            }
        }
        // end of loop thru characteristicUseList
        
        if(productCandidateObj.getChildEntity() != null) {
            for(int i = 0; i < productCandidateObj.getChildEntity().size(); i++) {
                createEPCItemRecords(smcOrderId, smcQuoteId, smcCaseId, productCandidateObj.getChildEntity().get(i), itemId, pstmtItem, pstmtVoucher);
            }
        } else {
            return;
        }
    }
    
    
    public EpcCreateEPCRecordResult updateEPCRecords(String custId, int orderId, String receiptNo, BigDecimal totalOrderAmount, String fulfillUser, String fulfillSalesman, String fulfillLocation, String fulfillChannel) {
        EpcCreateEPCRecordResult epcCreateEPCRecordResult = new EpcCreateEPCRecordResult();
//        HashMap<String, Object> contextData = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
//        EpcDeliveryInfo epcDeliveryInfo = null;
//        String deliveryMethod = "";
//        String pickupStore = "";
        boolean isValid = true;
        String errMsg = "";
        String processRemarks = "";
        String txNo = "" + new java.util.Date().getTime();
        BigDecimal totalCharge = new BigDecimal(0);
        String oldOrderStatus = "";
        EpcLogOrderStatus epcLogOrderStatus = null;
        String orderReference = "";

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            // basic checking
            // end of basic checking
            
            if(isValid) {
                // get current order status
                sql = "select order_status, order_reference " +
                      "  from epc_order " +
                      " where order_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId); // order_id
                rset = pstmt.executeQuery();
                if(rset.next()) {
                    oldOrderStatus = StringHelper.trim(rset.getString("order_status"));
                    orderReference = StringHelper.trim(rset.getString("order_reference"));
                } rset.close();
                pstmt.close();
                // end of get current order status

                // modify order prefix to SOR, EPC-12241, kerrytsang, 20231026
                if(EpcLoginChannel.ONLINE.equals(fulfillChannel)) {
                    orderReference = orderReference.replace("SMC", "SOR");
                }
                // end of modify order prefix to SOR, EPC-12241, kerrytsang, 20231026

                // calc total charge (not the upfront payment)
                sql = "select sum(charge_amount) " +
                      "  from epc_order_charge " +
                      " where order_id = ? " +
                      "   and charge_code not in (?) ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId); // order_id
                pstmt.setString(2, "99"); // charge_code
                rset = pstmt.executeQuery();
                if(rset.next()) {
                    totalCharge = rset.getBigDecimal(1);
                }
                rset.close();
                pstmt.close();
                // end of calc total charge

                // update epc_order
                sql = "update epc_order " +
                      "  set order_date = sysdate, order_status = ?, receipt_no = ?, total_charge_amount = ?, " + 
                      "    place_order_channel = ?, place_order_user = ?, place_order_salesman = ?, place_order_location = ?, place_order_date = sysdate, " +
                      "    order_reference = ? " +
                      " where order_id = ? " +
                      "  and cust_id = ? " +
                      "  and order_status in (?,?) ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "PF"); // order_status
                pstmt.setString(2, receiptNo); // receipt_no
                pstmt.setBigDecimal(3, totalCharge); // total_charge_amount
                pstmt.setString(4, fulfillChannel); // place_order_channel
                pstmt.setString(5, fulfillUser); // place_order_user
                pstmt.setString(6, fulfillSalesman); // place_order_salesman
                pstmt.setString(7, fulfillLocation); // place_order_location
                pstmt.setString(8, orderReference); // order_reference
                pstmt.setInt(9, orderId); // order_id
                pstmt.setString(10, custId); // cust_id
                pstmt.setString(11, "I"); // order_status - init
                pstmt.setString(12, "LOCK"); // order_status - locked
                pstmt.executeUpdate();
                pstmt.close();
                // end of update epc_order

                // update epc_order_charge
                sql = "update epc_order_charge " +
                      "   set paid = ?, " +
                      "       tx_no = ? " +
                      " where order_id = ? " +
                      "   and paid = ? " +
                      "   and need_to_pay = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "Y"); // paid - Y
                pstmt.setString(2, txNo); // tx_no
                pstmt.setInt(3, orderId); // order_id
                pstmt.setString(4, "N"); // paid - N
                pstmt.setString(5, "Y"); // need_to_pay
                pstmt.executeUpdate();
                pstmt.close();
                // end of update epc_order_charge

                // update epc_order_payment
                sql = "update epc_order_payment " +
                      "   set tx_no = ? " +
                      " where order_id = ? " +
                      "   and tx_no is null ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txNo); // tx_no
                pstmt.setInt(2, orderId); // order_id
                pstmt.executeUpdate();
                pstmt.close();
                // end of update epc_order_payment
                
                // update epc_order_receipt
                sql = "update epc_order_receipt " +
                      "   set tx_no = ? " +
                      " where order_id = ? " +
                      "   and receipt_no = ? " +
                      "   and tx_no is null ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txNo); // tx_no
                pstmt.setInt(2, orderId); // order_id
                pstmt.setString(3, receiptNo); // receipt_no
                pstmt.executeUpdate();
                pstmt.close();
                // end of update epc_order_receipt
                
                // create salesman log
                epcSalesmanHandler.createSalesmanLog(conn, orderId, fulfillUser, fulfillSalesman, fulfillLocation, fulfillChannel, epcSalesmanHandler.actionPlaceOrder);
                // end of create salesman log

                // create log
                epcLogOrderStatus = new EpcLogOrderStatus();
                epcLogOrderStatus.setOrderId(orderId);
                epcLogOrderStatus.setOldOrderStatus(oldOrderStatus);
                epcLogOrderStatus.setNewOrderStatus("PF");
                epcLogOrderStatus.setCreateUser(fulfillUser);
                epcLogOrderStatus.setCreateSalesman(fulfillSalesman);
                epcLogOrderStatus.setCreateChannel(fulfillChannel);
                epcLogOrderStatus.setCreateLocation(fulfillLocation);
                epcOrderLogHandler.logOrderStatus(conn, epcLogOrderStatus);
                // end of create log
                
                // update process ctrl
                processRemarks = "Done";
                epcOrderProcessCtrlHandler.updateProcess(orderId, epcOrderProcessCtrlHandler.processUpdateEPCRecord, epcOrderProcessCtrlHandler.processStatusDone, processRemarks);

                conn.commit();

                epcCreateEPCRecordResult.setResult("SUCCESS");
            } else {
                // error
                epcCreateEPCRecordResult.setResult("FAIL");
                epcCreateEPCRecordResult.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            epcCreateEPCRecordResult.setResult("FAIL");
            epcCreateEPCRecordResult.setErrMsg(e.getMessage());
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return epcCreateEPCRecordResult;
    }
    
    
    public EpcCreateBillingAccountResult createBillingAccountWithGup(String custId, int orderId, String createUser) {
        EpcCreateBillingAccountResult epcCreateBillingAccountResult = new EpcCreateBillingAccountResult();
        ArrayList<HashMap<String, Object>> objList = null;
        HashMap<String, Object> obj = null;
        HashMap<String, Object> objSMCCustInfo = null;
        String smcCaseId = "";
        String smcItemId = "";
        String custNum = "";
        String subrNum = "";
        String smcCustFirstName = "";
        String smcCustFirstNameDecrypt = "";
        String smcCustLastName = "";
        String smcCustLastNameDecrypt = "";
        String smcCustTitle = "";
        String smcHKIDBR = "";
        String smcHKIDBRDecrypt = "";
        String smcIdType = "";
        String smcEmail = "";
        String smcEmailDecrypt = "";
        String smcLang = "";
        String smcEffectiveDate = "";
        String smcPaymentMethod = "";
        String smcDOB = "";
        String smcDOBDecrypt = "";
        String smcContactNo1 = "";
        String smcContactNo2 = "";
        String smcContactPerson = "";
        String smcContactPersonDecrypt = "";
        String smcAddress1 = "";
        String smcAddress1Decrypt = "";
        String smcAddress2 = "";
        String smcAddress2Decrypt = "";
        String smcAddress3 = "";
        String smcAddress3Decrypt = "";
        String smcAddress4 = "";
        String smcAddress4Decrypt = "";
        String smcDM = "";
        String smcDMCompany = "";
        String smcDno = "";
        String smcMnpPrepaidSim = "";
        String smcMnpHKIDBR = "";
        String activationType = "";
        String smcBillDay = "";
        EpcGupResult epcCreateGupResult = null;
        EpcCreateGup epcCreateGup = null;
        EpcCustomerProfile epcCustomerProfile = null;
        EpcResponse epcResponse = null;
        EpcAddress epcAddress = null;
        EpcAddress[] epcAddressList = null;
        EpcContact epcContact = null;
        EpcSubscriber epcSubscriber = null;
        String custType = "PERS";
        String processStatus = "";
        String processRemarks = "";
        String logStr = "[createBillingAccountWithGup][orderId:" + orderId + "] ";
        boolean isExistedInTree = false;
        boolean isUpdate = false;

        
        try {
//            objList = (ArrayList<HashMap<String, Object>>)contextData.get("SMCCases");
            objList = epcCustProfileHandler.getTmpCustProfile(orderId, custId); // get cust info from epc table instead of quote context, kerrytsang, 20200908
            if(objList == null) {
                // no customer info, no need to perform further action
logger.info(logStr + "no case to proceed ");
            } else {
                for (int i = 0; i < objList.size(); i++) {
                	isExistedInTree = false; // reset
                	isUpdate = false; // reset
                    
                    obj = (HashMap<String, Object>)objList.get(i);
                    smcCaseId = StringHelper.trim((String)obj.get("SMCCaseId"));
                    smcItemId = StringHelper.trim((String)obj.get("SMCItemId"));
                    custNum = StringHelper.trim((String)obj.get("SMCCustNum"));
                    subrNum = StringHelper.trim((String)obj.get("SMCSubrNum"));
                    smcBillDay = StringHelper.trim((String)obj.get("SMCBillDay"));
                    
                    isExistedInTree = epcCustProfileHandler.isSubrExistInOrderTree(orderId, smcItemId);
logger.info(logStr + epcSecurityHelper.encode("isExistedInTree for " + subrNum + " (itemId:" + smcItemId + "):" + isExistedInTree));
                    if(!isExistedInTree) {
                        // proceed cases only if they are existed in productcandidate
logger.info(logStr + epcSecurityHelper.encode(" " + subrNum + " (itemId:" + smcItemId + ") NOT proceed "));
                        continue;
                    }
                    
                    
                    processStatus = epcOrderProcessCtrlHandler.getProcessStatus(orderId, smcCaseId, smcItemId, epcOrderProcessCtrlHandler.processCreateBillingAccountWithGup);
                    if("NOT_EXIST".equals(processStatus)) {
                        throw new Exception("createBillingAccountWithGup process for " + subrNum + " (itemId:" + smcItemId + ") is not existed, program exit");
                    } else if (epcOrderProcessCtrlHandler.processStatusDone.equals(processStatus)) {
logger.info(logStr + epcSecurityHelper.encode("createBillingAccountWithGup process for " + subrNum + " (itemId:" + smcItemId + ") is DONE, no need to perform again"));
                    } else {
                        objSMCCustInfo = (HashMap<String, Object>)obj.get("SMCCustInfo");
                        smcCustFirstName = StringHelper.trim((String)objSMCCustInfo.get("SMCCustFirstName"));
                        smcCustFirstNameDecrypt = StringHelper.trim(EpcCrypto.dGet(smcCustFirstName, "utf-8"));
                        smcCustLastName = StringHelper.trim((String)objSMCCustInfo.get("SMCCustLastName"));
                        smcCustLastNameDecrypt = StringHelper.trim(EpcCrypto.dGet(smcCustLastName, "utf-8"));
                        smcCustTitle = StringHelper.trim((String)objSMCCustInfo.get("SMCCustTitle"));
                        smcHKIDBR = StringHelper.trim((String)objSMCCustInfo.get("SMCHKIDBR"));
                        smcHKIDBRDecrypt = StringHelper.trim(EpcCrypto.dGet(smcHKIDBR, "utf-8"));
                        smcIdType = StringHelper.trim((String)objSMCCustInfo.get("SMCIdType"));
                        smcEmail = StringHelper.trim((String)objSMCCustInfo.get("SMCEmail"));
                        smcEmailDecrypt = StringHelper.trim(EpcCrypto.dGet(smcEmail, "utf-8"));
                        smcLang = StringHelper.trim((String)objSMCCustInfo.get("SMCLang"));
                        smcEffectiveDate = StringHelper.trim((String)objSMCCustInfo.get("SMCEffectiveDate"));
                        smcPaymentMethod = StringHelper.trim((String)objSMCCustInfo.get("SMCPaymentMethod"));
                        smcDOB = StringHelper.trim((String)objSMCCustInfo.get("SMCDOB"));
                        smcDOBDecrypt = StringHelper.trim(EpcCrypto.dGet(smcDOB, "utf-8"));
                        smcContactNo1 = StringHelper.trim((String)objSMCCustInfo.get("SMCContactNo1"));
                        smcContactNo2 = StringHelper.trim((String)objSMCCustInfo.get("SMCContactNo2"));
                        smcContactPerson = StringHelper.trim((String)objSMCCustInfo.get("SMCContactPerson"));
                        smcContactPersonDecrypt = StringHelper.trim(EpcCrypto.dGet(smcContactPerson, "utf-8"));
                        smcAddress1 = StringHelper.trim((String)objSMCCustInfo.get("SMCAddress1"));
                        smcAddress1Decrypt = StringHelper.trim(EpcCrypto.dGet(smcAddress1, "utf-8"));
                        smcAddress2 = StringHelper.trim((String)objSMCCustInfo.get("SMCAddress2"));
                        smcAddress2Decrypt = StringHelper.trim(EpcCrypto.dGet(smcAddress2, "utf-8"));
                        smcAddress3 = StringHelper.trim((String)objSMCCustInfo.get("SMCAddress3"));
                        smcAddress3Decrypt = StringHelper.trim(EpcCrypto.dGet(smcAddress3, "utf-8"));
                        smcAddress4 = StringHelper.trim((String)objSMCCustInfo.get("SMCAddress4"));
                        smcAddress4Decrypt = StringHelper.trim(EpcCrypto.dGet(smcAddress4, "utf-8"));
                        smcDM = StringHelper.trim((String)objSMCCustInfo.get("SMCDM"));
                        smcDMCompany = StringHelper.trim((String)objSMCCustInfo.get("SMCDMCompany"));
                        smcDno = StringHelper.trim((String)objSMCCustInfo.get("SMCDno"));
                        smcMnpPrepaidSim = StringHelper.trim((String)objSMCCustInfo.get("SMCMnpPrepaidSim"));
                        smcMnpHKIDBR = StringHelper.trim((String)objSMCCustInfo.get("SMCMnpHKIDBR"));
                        activationType = StringHelper.trim((String)objSMCCustInfo.get("SMCActivationType"));
                        
                        processRemarks = ""; // reset

                        // invoke crm api
                        epcAddressList = new EpcAddress[2];
                        epcAddress = new EpcAddress();
                        epcAddress.setAddressType("REGISTER_ADDRESS");
                        epcAddress.setAddrLine1(smcAddress1Decrypt);
                        epcAddress.setAddrLine2(smcAddress2Decrypt);
                        epcAddress.setDistrict(smcAddress3Decrypt);
                        epcAddress.setArea(smcAddress4Decrypt);
                        epcAddressList[0] = epcAddress;

                        epcAddress = new EpcAddress();
                        epcAddress.setAddressType("BILLING_ADDRESS");
                        epcAddress.setAddrLine1(smcAddress1Decrypt);
                        epcAddress.setAddrLine2(smcAddress2Decrypt);
                        epcAddress.setDistrict(smcAddress3Decrypt);
                        epcAddress.setArea(smcAddress4Decrypt);
                        epcAddressList[1] = epcAddress;

                        epcContact = new EpcContact();
                        epcContact.setAttention("");
                        epcContact.setBirth(new SimpleDateFormat("yyyyMMdd").parse(smcDOBDecrypt));
                        epcContact.setCommLangCode(smcLang);
                        epcContact.setContactNum(smcContactNo1);
                        epcContact.setContactNum2(smcContactNo2);
                        epcContact.setContactPerson(smcContactPersonDecrypt);
                        epcContact.setCustTitle(smcCustTitle);
                        epcContact.setFirstName(smcCustFirstNameDecrypt);
                        epcContact.setLastName(smcCustLastNameDecrypt);
                        epcContact.setIdTypeCode(smcIdType);
                        epcContact.setIdbr(smcHKIDBRDecrypt);

                        epcSubscriber = new EpcSubscriber();
                        epcSubscriber.setCustNum(custNum);
                        epcSubscriber.setSubrNum(subrNum);
                        epcSubscriber.setPortinSource(smcDno);
                        epcSubscriber.setLineCategory("M");
    //                    epcSubscriber.setCommChannelId("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY");

                        epcCustomerProfile = new EpcCustomerProfile();
                        epcCustomerProfile.setAccountType("POSTPAID");
                        epcCustomerProfile.setAddress(epcAddressList);
                        epcCustomerProfile.setCommChannel("YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYY"); // for eNewsletter
                        epcCustomerProfile.setContact(epcContact);
                        epcCustomerProfile.setCustId(custId);
                        epcCustomerProfile.setCustTypeCode(custType);
                        epcCustomerProfile.setDivertCode("EWA");
                        epcCustomerProfile.setDmCompanyConsent(smcDMCompany);
                        epcCustomerProfile.setDmConsent(smcDM);
                        //epcCustomerProfile.setEffectiveDate(new SimpleDateFormat("yyyyMMdd").parse(smcEffectiveDate));
                        epcCustomerProfile.setEffectiveDate(new SimpleDateFormat("yyyyMMddHHmmss").parse(smcEffectiveDate));
                        epcCustomerProfile.setEmail(smcEmailDecrypt);
                        epcCustomerProfile.setPaymentMethod(smcPaymentMethod);
                        epcCustomerProfile.setRequesterID(createUser);
                        epcCustomerProfile.setSubscriber(epcSubscriber);
                        epcResponse = epcCustomerHandler.create(epcCustomerProfile);
logger.info(logStr + epcSecurityHelper.encode(subrNum + " (itemId:" + smcItemId + ")->crm, epcResponse.getResultCode():" + epcResponse.getResultCode()));
logger.info(logStr + epcSecurityHelper.encode(subrNum + " (itemId:" + smcItemId + ")->crm, epcResponse.getResultMsg():" + epcResponse.getResultMsg()));
logger.info(logStr + epcSecurityHelper.encode(subrNum + " (itemId:" + smcItemId + ")->crm, epcResponse.getSubscriberRecId() (subr key):" + epcResponse.getSubscriberRecId()));
                        if(!"0".equals(epcResponse.getResultCode())) {
                            // error
                            throw new Exception(logStr + epcSecurityHelper.encode("save cust info " + custNum + "/" + subrNum + " to crm error. " + epcResponse.getResultMsg()));
                        } else {
                            processRemarks = "crm result:" + epcResponse.getResultCode() + ",err:" + epcResponse.getResultMsg() + ",subr key:" + epcResponse.getSubscriberRecId() + " ";
                            
                            // update subr key to epc table
                            isUpdate = epcCustProfileHandler.updateSubrKey(orderId, smcItemId, epcResponse.getSubscriberRecId());
                            // end of update subr key to epc table
                        }
                        // end of invoke crm api


                        // invoke gup api
                        epcCreateGup = new EpcCreateGup();
                        epcCreateGup.setOrderId(orderId + "");
                        epcCreateGup.setActionSystem("EPC");
                        epcCreateGup.setActionUsername(createUser);
                        epcCreateGup.setCustNum(custNum);
                        epcCreateGup.setSubrNum(subrNum);
                        epcCreateGup.setAddOnNumber("");
                        epcCreateGup.setBillLang(smcLang);
                        epcCreateGup.setCommEmailAddress(smcEmailDecrypt);
                        epcCreateGup.setCommLang(smcLang);
                        epcCreateGup.setContactNo1(smcContactNo1);
                        epcCreateGup.setContactNo2(smcContactNo2);
                        epcCreateGup.setCustomerType("PostPaid");
                        epcCreateGup.setGivenNameChi("");
                        epcCreateGup.setGivenNameEng(smcCustFirstNameDecrypt);
                        epcCreateGup.setIdType(smcIdType);
                        epcCreateGup.setImsi("");
                        epcCreateGup.setPlanCode("");
                        epcCreateGup.setStatus("");
                        epcCreateGup.setSurNameChi("");
                        epcCreateGup.setSurNameEng(smcCustLastNameDecrypt);
                        epcCreateGup.setSwitchOnDate("");
                        epcCreateGup.setTitleChi("");
                        epcCreateGup.setTitleEng(smcCustTitle);
                        epcCreateGup.setSmcPin1(EpcCrypto.eGet("123456", "UTF-8"));
                        epcCreateGup.setSmcPin2(EpcCrypto.eGet("1234", "UTF-8"));

                        epcCreateGupResult = gupHandler.deleteThenCreateGup(epcCreateGup);
logger.info(logStr + epcSecurityHelper.encode(subrNum + " (itemId:" + smcItemId + ")->gup, epcCreateGupResult.getResultCode():" + epcCreateGupResult.getResultCode()));
logger.info(logStr + epcSecurityHelper.encode(subrNum + " (itemId:" + smcItemId + ")->gup, epcCreateGupResult.getErrorCode():" + epcCreateGupResult.getErrorCode()));
logger.info(logStr + epcSecurityHelper.encode(subrNum + " (itemId:" + smcItemId + ")->gup, epcCreateGupResult.getErrorMessage():" + epcCreateGupResult.getErrorMessage()));
                        // if not success, ...
                        
                        processRemarks += "gup result:" + epcCreateGupResult.getResultCode() + ",err:" + epcCreateGupResult.getErrorCode();
                        // end of invoke gup api
                        

                        // update process ctrl
                        epcOrderProcessCtrlHandler.updateProcess(orderId, smcCaseId, smcItemId, epcOrderProcessCtrlHandler.processCreateBillingAccountWithGup, epcOrderProcessCtrlHandler.processStatusDone, processRemarks);
logger.info(logStr + epcSecurityHelper.encode("createBillingAccountWithGup process for " + subrNum + " (itemId:" + smcItemId + ") is updated to DONE"));
                    }
                }
            }
            
            epcCreateBillingAccountResult.setResult("SUCCESS");
        } catch(Exception e) {
            e.printStackTrace();
            
            epcCreateBillingAccountResult.setResult("FAIL");
            epcCreateBillingAccountResult.setErrMsg(e.getMessage());
        } finally {
        }
        return epcCreateBillingAccountResult;
    }

    
    public EpcConfirmNewMobileResult confirmNewMobile(String custId, int orderId, String fulfillUser, String fulfillSalesman, String fulfillLocation) {
        EpcConfirmNewMobileResult epcConfirmNewMobileResult = new EpcConfirmNewMobileResult();
        epcConfirmNewMobileResult.setResult("SUCCESS"); // init
        ArrayList<HashMap<String, Object>> objList = null;
        HashMap<String, Object> obj = null;
        HashMap<String, Object> obj2 = null;
        String smcCaseId = "";
        String smcItemId = "";
        String custNum = "";
        String subrNum = "";
        String hkidBr = "";
        String decryptedHkidBr = "";
        String dealerCode = "";
        EpcNumReservation epcNumReservation = null;
        EpcNumReservationResult epcNumReservationResult = null;
        String logStr = "[confirmNewMobile][orderId:" + orderId + "]";
        String processRemarks = "";
        boolean isNeeded = false;
        boolean isExistedInTree = false;

        
        try {
//            objList = (ArrayList<HashMap<String, Object>>)contextData.get("SMCCases");
            objList = epcCustProfileHandler.getTmpCustProfile(orderId, custId); // get cust info from epc table instead of quote context, kerrytsang, 20200908
            if(objList == null) {
                // no customer info, no need to perform further action
            } else {
                for (int i = 0; i < objList.size(); i++) {
                	isNeeded = false; // reset
                	isExistedInTree = false; // reset
                	
                    obj = (HashMap<String, Object>)objList.get(i);
                    smcCaseId = StringHelper.trim((String)obj.get("SMCCaseId"));
                    smcItemId = StringHelper.trim((String)obj.get("SMCItemId"));
                    custNum = StringHelper.trim((String)obj.get("SMCCustNum"));
                    subrNum = StringHelper.trim((String)obj.get("SMCSubrNum"));
                    hkidBr = StringHelper.trim((String)obj.get("SMCHKIDBR"));
                    decryptedHkidBr = StringHelper.trim(EpcCrypto.dGet(hkidBr, "utf-8"));
                    dealerCode = StringHelper.trim((String)obj.get("SMCDealerCode"));
                    

                    isExistedInTree = epcCustProfileHandler.isSubrExistInOrderTree(orderId, smcItemId);
logger.info(logStr + epcSecurityHelper.encode("[subrNum:" + subrNum + "][itemId:" + smcItemId + "] isExistedInTree:" + isExistedInTree));
                    if(!isNeeded) {
                        // proceed cases only if they are existed in productcandidate
logger.info(logStr + epcSecurityHelper.encode("[subrNum:" + subrNum + "][itemId:" + smcItemId + "]  NOT proceed "));
                        continue;
                    }
                    

                    // call rmm api
                    epcNumReservation = new EpcNumReservation();
                    epcNumReservation.setAction("RESERVE");
                    epcNumReservation.setDealerCode(dealerCode);
                    epcNumReservation.setExpiryTime("2099-12-31 23:59:59"); // format=YYYY-MM-DD HH24:MI:SS
                    epcNumReservation.setNumber(subrNum);
                    epcNumReservation.setReserveKey(custId); // use cust Id as reservation key
                    epcNumReservation.setUpdateBy(fulfillUser);
                    epcNumReservationResult = epcReservation.numberReservation(epcNumReservation);
logger.info(logStr + epcSecurityHelper.encode("[subrNum:" + subrNum + "][itemId:" + smcItemId + "] epcNumReservationResult.getMessage():" + epcNumReservationResult.getMessage()));
logger.info(logStr + epcSecurityHelper.encode("[subrNum:" + subrNum + "][itemId:" + smcItemId + "] epcNumReservationResult.getErrorCode():" + epcNumReservationResult.getErrorCode()));
logger.info(logStr + epcSecurityHelper.encode("[subrNum:" + subrNum + "][itemId:" + smcItemId + "] epcNumReservationResult.getErrorMessage():" + epcNumReservationResult.getErrorMessage()));
//                    if(!"SUCCESS".equals(epcNumReservationResult.getMessage())) {
                    if(epcNumReservationResult.getErrorCode() != 0) {
                    	processRemarks = "error is returned when confirming reservation of " + subrNum + ". " + epcNumReservationResult.getErrorCode() + ", " + epcNumReservationResult.getErrorMessage();
                    	
//                        epcConfirmNewMobileResult.setResult("FAIL");
//                        epcConfirmNewMobileResult.setErrMsg(processRemarks);
// break ???
                    	
                        // update process ctrl
                        epcOrderProcessCtrlHandler.updateProcess(orderId, smcCaseId, smcItemId, epcOrderProcessCtrlHandler.processConfirmNewMobile, epcOrderProcessCtrlHandler.processStatusFail, processRemarks);
logger.info(logStr + "update process DONE");
                    } else {
                    	// update process ctrl
                    	processRemarks = "succeed to confirm " + subrNum;
                    	
                        epcOrderProcessCtrlHandler.updateProcess(orderId, smcCaseId, smcItemId, epcOrderProcessCtrlHandler.processConfirmNewMobile, epcOrderProcessCtrlHandler.processStatusDone, processRemarks);
logger.info(logStr + "update process DONE");
                    }
                }
                
                epcConfirmNewMobileResult.setResult("SUCCESS");
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcConfirmNewMobileResult.setResult("FAIL");
            epcConfirmNewMobileResult.setErrMsg(e.getMessage());
        } finally {
        }
        return epcConfirmNewMobileResult;
    }
    
    
    public EpcCreateEPCReceiptResult createEPCReceipt(String custId, int orderId, EpcOrderInfo epcOrderInfo, String fulfillUser, String fulfillSalesman, String fulfillLocation, String fulfillChannel) {
        EpcCreateEPCReceiptResult epcCreateEPCReceiptResult = new EpcCreateEPCReceiptResult();
        EpcCreateReceiptResult epcCreateReceiptResult = null;
        EpcCreateReceipt epcCreateReceipt = null;
        ArrayList<EpcCharge> chargeList = null;
        ArrayList<EpcPayment> paymentList = null;
        ArrayList<EpcOrderItemInfo> itemList = null;
        EpcPayment epcPayment = null;
        int smcOrderId = orderId;
        BigDecimal totalOrderAmount = new BigDecimal(0);
        BigDecimal totalPaymentAmount = new BigDecimal(0);
        String custNum = "";
        String subrNum = "";
        String receiptLocation = "";
        String processRemarks = "";
        EpcCustNumSubrNum epcCustNumSubrNum = null;
        EpcGetCharge epcGetCharge = null;
        EpcGetChargeResult epcGetChargeResult = null;
        
        
        try {
            // get cust num, subr num from epc table
            epcCustNumSubrNum = getCustNumSubrNumForReceipt(orderId);
            custNum = epcCustNumSubrNum.getCustNum();
            subrNum = epcCustNumSubrNum.getSubrNum();
            // end of get cust num, subr num from epc table
            
            
            // get payment list
            paymentList = getPaymentInfo(smcOrderId);
            if(paymentList == null) {
            	throw new Exception("cannot find payment list");
            }
            // end of get payment list
            
            
            // prepare charge list
            chargeList = new ArrayList<EpcCharge>();

            epcGetCharge = new EpcGetCharge();
            epcGetCharge.setOrderId(orderId + "");
            epcGetCharge.setFromPlaceOrder("Y"); // not to trigger charge re-generate
            epcGetCharge.setPaymentList(paymentList);
            epcGetChargeResult = epcPaymentHandler.getChargeResult(epcGetCharge, epcOrderInfo);
            if("SUCCESS".equals(epcGetChargeResult.getResult())) {
            	for(EpcOfferCharge c : epcGetChargeResult.getOfferChargeList()) {
                    for(EpcCharge cc : c.getChargeList()) {
                        if("Y".equals(cc.getNeedToPay()) && "N".equals(cc.getPaid())) {
                            if("COUP".equals(cc.getChargeCode())) {
                                // shopping bag level voucher charge in general charge
                                epcPayment = new EpcPayment();
                                epcPayment.setPaymentCode(cc.getChargeCode());
                                epcPayment.setPaymentAmount(cc.getChargeAmount().multiply(new BigDecimal(-1)));
                                epcPayment.setReference1(cc.getItemCode());

                                paymentList.add(epcPayment);
                            } else {
                                // normal charge
                                chargeList.add(cc);
                            }
                            
                        }

                        // sum all charges as the order total amount
                        //  exclude all "99" items (presales / deposit)
                        //  exclude COUP item (move COUP item to payment)
                        if(!"99".equals(cc.getChargeCode()) && "COUP".equals(cc.getChargeCode())) {
                            totalOrderAmount = totalOrderAmount.add(cc.getChargeAmount());
                        }
                    }
                }

                epcCreateEPCReceiptResult.setEpcChargeList(epcGetChargeResult.getOfferChargeList());
            } else {
            	throw new Exception("get charge list. " + epcGetChargeResult.getErrorMessage());
            }
            // end of prepare charge list

            // prepare item list
            itemList = new ArrayList<>();

            for(EpcOrderQuoteInfo q : epcOrderInfo.getEpcOrderQuoteInfoList()) {
                if(q.getEpcOrderCaseInfoList() != null) {
                    for(EpcOrderCaseInfo c : q.getEpcOrderCaseInfoList()) {
                        if(c.getEpcOrderItemList() != null) {
                            for(EpcOrderItemInfo i : c.getEpcOrderItemList()) {
                                if(EpcItemCategory.DEVICE.equals(i.getItemCat())
                                    || EpcItemCategory.SIM.equals(i.getItemCat())
                                    || EpcItemCategory.APPLECARE.equals(i.getItemCat())
                                    || EpcItemCategory.SCREEN_REPLACE.equals(i.getItemCat())
                                    || EpcItemCategory.GIFT_WRAPPING.equals(i.getItemCat())
                                    || EpcItemCategory.PLASTIC_BAG.equals(i.getItemCat())
                                ) {
                                    itemList.add(i);
                                }
                            }
                        }
                    }
                }
            }
            // end of prepare item list
        

            // prepare epcCreateReceipt obj
            epcCreateReceipt = new EpcCreateReceipt();
            epcCreateReceipt.setCustId(custId);
            // since channels other than STORE is paid via payment gateway which is only interpreted as ONLINE, may need to revise the logic
            if(EpcLoginChannel.ONLINE.equals(fulfillChannel)) { 
                //epcCreateReceipt.setLocation("ECO");
                receiptLocation = "ECO";
            } else {
                //epcCreateReceipt.setLocation(fulfillLocation);
                receiptLocation = fulfillLocation;
            }
            epcCreateReceipt.setLocation(receiptLocation);
            epcCreateReceipt.setOrderId(smcOrderId + "");
            if(EpcLoginChannel.ONLINE.equals(fulfillChannel)) {
            	epcCreateReceipt.setCreateUser("SysAdmin");
	            epcCreateReceipt.setSalesman("");
            } else {
	            epcCreateReceipt.setCreateUser(fulfillUser);
	            epcCreateReceipt.setSalesman(fulfillSalesman);
            }
            epcCreateReceipt.setCustNum(custNum);
            epcCreateReceipt.setSubrNum(subrNum);
            
            epcCreateReceipt.setPaymentList(paymentList);
            for (EpcPayment p : paymentList) {
            	totalPaymentAmount = totalPaymentAmount.add(p.getPaymentAmount());
            }
            
            epcCreateReceipt.setCharges(chargeList);
            epcCreateReceipt.setItemList(itemList);
            // end of prepare epcCreateReceipt obj
            
            epcCreateReceiptResult = epcReceiptHandler.createReceipt(epcCreateReceipt);
            if("SUCCESS".equals(epcCreateReceiptResult.getResult())) {
                epcCreateEPCReceiptResult.setResult("SUCCESS");
                epcCreateEPCReceiptResult.setReceiptNo(epcCreateReceiptResult.getReceiptNo());
                epcCreateEPCReceiptResult.setTotalAmount(totalPaymentAmount); // total payment amount

                // update receipt to epc_order_receipt
                saveReceiptToOrder(smcOrderId, epcCreateReceiptResult.getReceiptNo(), totalPaymentAmount, fulfillUser, fulfillSalesman, fulfillLocation, fulfillChannel);
            } else {
                epcCreateEPCReceiptResult.setResult("FAIL");
                epcCreateEPCReceiptResult.setErrMsg(epcCreateReceiptResult.getErrorCode() + "|" + epcCreateReceiptResult.getErrorMessage());
            }
            
            
            // update process ctrl
            processRemarks = "receiptNo:" + epcCreateReceiptResult.getReceiptNo() + ",totalPaymentAmount:" + totalPaymentAmount;
            epcOrderProcessCtrlHandler.updateProcess(orderId, epcOrderProcessCtrlHandler.processCreateEPCReceipt, epcOrderProcessCtrlHandler.processStatusDone, processRemarks);
        } catch (Exception e) {
            e.printStackTrace();

            epcCreateEPCReceiptResult.setResult("FAIL");
            epcCreateEPCReceiptResult.setErrMsg(e.getMessage());
        }
        return epcCreateEPCReceiptResult;
    }
    
    
    public EpcCustNumSubrNum getCustNumSubrNumForReceipt(int orderId) {
    	EpcCustNumSubrNum epcCustNumSubrNum = new EpcCustNumSubrNum();
    	epcCustNumSubrNum.setCustNum("00000000"); // default
    	epcCustNumSubrNum.setSubrNum("00000000"); // default
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String custNum = "";
        String subrNum = "";
        
        try {
        	conn = epcDataSource.getConnection();
        	sql = "select cust_num, subr_num " + 
        			" from epc_order_case " + 
        			" where order_id = ? " + 
        			"order by activation_type ";
          	pstmt = conn.prepareStatement(sql);
          	pstmt.setInt(1, orderId); // order_id
          	rset = pstmt.executeQuery();
          	while (rset.next()) {
          		custNum = StringHelper.trim(rset.getString("cust_num"));
          		subrNum = StringHelper.trim(rset.getString("subr_num"));
          		
          		//if(!"00000000".equals(custNum)) {
          		//	epcCustNumSubrNum.setCustNum(custNum);
          		//}
          		//if(!"00000000".equals(subrNum)) {
          		//	epcCustNumSubrNum.setSubrNum(subrNum);
          		//}
          		if(!"00000000".equals(custNum) && !"00000000".equals(subrNum)) {
                    epcCustNumSubrNum.setCustNum(custNum);
                    epcCustNumSubrNum.setSubrNum(subrNum);
                    break;
          		}
          	} rset.close(); rset = null;
          	pstmt.close(); pstmt = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcCustNumSubrNum;
    }
    
    
    public void createChargeList(ArrayList<EpcCharge> chargeList, EpcQuoteProductCandidate productCandidateObj, String caseId) {
        // kerrytsang, 20200618
        // need to use 1st level ID as our smc case id (epc_order_case)
        // it will be used to get cust num, subr num from quote context
        
        EpcCharge epcCharge = null;
        String cid = "";
        if("".equals(caseId)) {
            // 1st level
            cid = productCandidateObj.getId();
        } else {
            cid = caseId;
        }
        
        
        String chargeCode = "";
        if(EpcItemCategory.CHARGE.equals(productCandidateObj.getItemCat())) {
//            if(new BigDecimal(productCandidateObj.getCpqItemValue()).compareTo(BigDecimal.ZERO) == 1) {
            if(productCandidateObj.getItemCharge() != null && productCandidateObj.getItemCharge().compareTo(BigDecimal.ZERO) == 1) {
                // charge amount > zero
                
                if("Admin Charge".equals(productCandidateObj.getCpqItemDesc())) {
                    chargeCode = "D14";
                } else if("Mobile Plan Charge".equals(productCandidateObj.getCpqItemDesc())) {
                    chargeCode = "D10";
                } else {
                    // default vas
                    chargeCode = "D11";
                }

                epcCharge = new EpcCharge();
                epcCharge.setChargeCode(chargeCode); // ???
                epcCharge.setChargeDesc(productCandidateObj.getCpqItemDesc());
                epcCharge.setChargeDescChi(productCandidateObj.getCpqItemDescChi());
//                epcCharge.setChargeAmount(new BigDecimal(productCandidateObj.getCpqItemValue()));
                epcCharge.setChargeAmount(productCandidateObj.getItemCharge());
                epcCharge.setCaseId(caseId);

                chargeList.add(epcCharge);
            }
        }
        
        if(productCandidateObj.getChildEntity() != null) {
            for(int i = 0; i < productCandidateObj.getChildEntity().size(); i++) {
                createChargeList(chargeList, productCandidateObj.getChildEntity().get(i), cid);
            }
        } else {
            // nothing to do
        }
        return;
    }
    
    
    public ArrayList<EpcPayment> getPaymentInfo(int orderId) {
    	Connection conn = null;
    	ArrayList<EpcPayment> paymentList = null;
    	
    	try {
        	conn = epcDataSource.getConnection();
        	
        	paymentList = getPaymentInfo(conn, orderId);
    	} catch (Exception e) {
    		e.printStackTrace();
            
            paymentList = null;
        } finally {
        	try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return paymentList;
    }
    
    
    public ArrayList<EpcPayment> getPaymentInfo(Connection conn, int orderId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<EpcPayment> paymentList = new ArrayList<EpcPayment>();
        EpcPayment epcPayment = null;
        
        try {
            sql = "select payment_code, payment_amount, reference_1, reference_2, currency_code, " +
                  "       currency_amount, exchange_rate, cc_no_masked, cc_name_masked, cc_expiry_masked, " +
                  "       ecr_no, case_id, payment_id " + 
                  "  from epc_order_payment " +
                  " where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            rset = pstmt.executeQuery();
            while(rset.next()) {
                epcPayment = new EpcPayment();
                epcPayment.setPaymentCode(StringHelper.trim(rset.getString("payment_code")));
                epcPayment.setPaymentAmount(rset.getBigDecimal("payment_amount"));
                epcPayment.setReference1(StringHelper.trim(rset.getString("reference_1")));
                epcPayment.setReference2(StringHelper.trim(rset.getString("reference_2")));
                epcPayment.setCurrencyCode(StringHelper.trim(rset.getString("currency_code")));
                epcPayment.setCurrencyAmount(rset.getBigDecimal("currency_amount"));
                epcPayment.setExchangeRate(rset.getBigDecimal("exchange_rate"));
                epcPayment.setCcNoMasked(StringHelper.trim(rset.getString("cc_no_masked")));
                epcPayment.setCcNameMasked(StringHelper.trim(rset.getString("cc_name_masked")));
                epcPayment.setCcExpiryMasked(StringHelper.trim(rset.getString("cc_expiry_masked")));
                epcPayment.setEcrNo(StringHelper.trim(rset.getString("ecr_no")));
                epcPayment.setCaseId(StringHelper.trim(rset.getString("case_id")));
                epcPayment.setPaymentId(rset.getInt("payment_id"));
                
                paymentList.add(epcPayment);
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            //e.printStackTrace();
            logger.info(e.getMessage(), e);
            paymentList = null;
        } finally {
            if (rset!=null) {try {rset.close();} catch (Exception ignore) {} }
            if (pstmt!=null) {try {pstmt.close();} catch (Exception ignore) {} }
        }
        return paymentList;
    }
    
    
    public ArrayList<EpcSubr> getSubrKeyList(int sigmaOrderId) {
        ArrayList<EpcSubr> subrList = new ArrayList<EpcSubr>();
        EpcSubr epcSubr = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        
        try {
            conn = epcDataSource.getConnection();
            sql = "select b.cust_num, b.subr_num, b.subr_key " +
                 "  from epc_order_quote a, epc_order_case b " +
                 " where a.cpq_order_id = ? " +
                 "  and b.order_id = a.order_id " +
                 "  and b.quote_id = a.quote_id ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, sigmaOrderId);
            rset = pstmt.executeQuery();
            while(rset.next()) {
                epcSubr = new EpcSubr();
                epcSubr.setSigmaOrderId(sigmaOrderId);
                epcSubr.setCustNum(StringHelper.trim(rset.getString("cust_num")));
                epcSubr.setSubrNum(StringHelper.trim(rset.getString("subr_num")));
                epcSubr.setSubrKey(StringHelper.trim(rset.getString("subr_key")));
                
                subrList.add(epcSubr);
            } rset.close();
        } catch (Exception e) {
            e.printStackTrace();
            
            subrList = null;
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return subrList;
    }
    
    
    public void getOrderAttrs(EpcAddOrderAttrs epcAddOrderAttrs) {
    	String custId = StringHelper.trim(epcAddOrderAttrs.getCustId());
        int orderId = epcAddOrderAttrs.getOrderId();
        String smcOrderReference = "";
        
        boolean isValid = true;
        String errMsg = "";
        
        // basic checking
        smcOrderReference = isOrderBelongCust(custId, orderId);
    	if("NOT_BELONG".equals(smcOrderReference)) {
    		errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
        	isValid = false;
    	}
    	// end of basic checking
    	
    	if(isValid) {
    		epcAddOrderAttrs.setAttrs(epcOrderAttrHandler.getAllAttrsUnderOrder(orderId));
    		
    		epcAddOrderAttrs.setResult("SUCCESS");
        } else {
        	epcAddOrderAttrs.setResult("FAIL");
        	epcAddOrderAttrs.setErrMsg(errMsg);
        }
    }
    
    
    public void addOrderAttrs(EpcAddOrderAttrs epcAddOrderAttrs) {
    	String custId = StringHelper.trim(epcAddOrderAttrs.getCustId());
        int orderId = epcAddOrderAttrs.getOrderId();
        String smcOrderReference = "";
//        String caseId = "";
//        String itemId = "";
//        ArrayList<EpcSmcQuote> smcQuoteList = null;
//        ArrayList<EpcQuote> sigmaQuoteList = new ArrayList<EpcQuote>();
        boolean isValid = true;
        String errMsg = "";
        
        // basic checking
        smcOrderReference = isOrderBelongCust(custId, orderId);
    	if("NOT_BELONG".equals(smcOrderReference)) {
    		errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
        	isValid = false;
    	}

        if(isOrderLocked(custId, orderId)) {
            errMsg += "input order [" + orderId + "] is locked. ";
            isValid = false;
        }

// commented by kerrytsang, 20220303        
//        // check item belong to this quote
//    	smcQuoteList = getQuoteByOrderId(orderId);
//    	for(EpcSmcQuote smcQuote : smcQuoteList) {
//    		sigmaQuoteList.add(epcQuoteHandler.getQuoteInfo(smcQuote.getQuoteGuid()));
//    	}
//
//
//    	for(EpcOrderAttr attr : epcAddOrderAttrs.getAttrs()) {
//    		caseId = ""; // reset
//    		
//    		itemId = attr.getItemId();
//    		
//    		for(EpcQuote epcQuote: sigmaQuoteList) {
//    			caseId = epcQuoteHandler.determineCaseIdByItemId(epcQuote, itemId);
//    			if(StringUtils.isNotBlank(caseId)) {
//    				break;
//    			}
//    		}
//    		
//    		if("".equals(caseId)) {
//    			errMsg += "case id is not found for item id [" + itemId + "]. ";
//            	isValid = false;
//    		} else {
//    			attr.setCaseId(caseId);
//    		}
//    	}
        
        // check quota for items
        // ...

        // end of basic checking
        
        if(isValid) {
        	epcOrderAttrHandler.addAttrs(epcAddOrderAttrs);
        } else {
        	epcAddOrderAttrs.setResult("FAIL");
        	epcAddOrderAttrs.setErrMsg(errMsg);
        }
    }
    
    
    public boolean isItemIdBelongToOrder(Connection conn, int orderId, String itemId) {
    	PreparedStatement pstmt = null;
    	ResultSet rset = null;
    	String sql = "";
    	boolean isBelong = false;
    	int cnt = 0;
    	
    	try {
    		sql = "select count(1) from epc_order_item where order_id = ? and item_id = ? ";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setInt(1, orderId); // order_id
    		pstmt.setString(2, itemId); // item_id
    		rset = pstmt.executeQuery();
    		if(rset.next()) {
    			cnt = rset.getInt(1);
    		} rset.close(); rset = null;
    		pstmt.close(); pstmt = null;
    		
    		if(cnt > 0) {
    			isBelong = true;
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return isBelong;
    }
    
    
    public TreeMap<String, String> getReserveIdsByItemId(Connection conn, int orderId) {
    	TreeMap<String, String> aMap = new TreeMap<String, String>(); // item_id, reserve_id
    	PreparedStatement pstmt = null;
    	ResultSet rset = null;
    	String sql = "";
    	
    	try {
    		sql = "select item_id, reserve_id from epc_order_item where order_id = ? ";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setInt(1, orderId); // order_id
    		rset = pstmt.executeQuery();
    		while(rset.next()) {
    			aMap.put(StringHelper.trim(rset.getString("item_id")), StringHelper.trim(rset.getString("reserve_id")));
    		} rset.close(); rset = null;
    		pstmt.close(); pstmt = null;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return aMap;
    }


    public String getOrderReferenceByItemSerialNo(String serialNo) {
    	Connection conn = null;
    	PreparedStatement pstmt = null;
    	ResultSet rset = null;
    	String sql = "";
    	String orderReference = "";
    	
    	try {
    		conn = epcDataSource.getConnection();
            
            sql = "select a.order_reference " +
                  "  from epc_order a, epc_order_item b " +
                  " where b.cpq_item_value = ? " + 
                  "   and a.order_id = b.order_id ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, serialNo); // cpq_item_value (imei / sim no)
            rset = pstmt.executeQuery();
            if(rset.next()) {
            	orderReference = StringHelper.trim(rset.getString("order_reference"));
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
    		try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
    	}
    	return orderReference;
    }
    
    
    


    
    
    
    

    
    
    
    
    public EpcVerifyExistingMobile verifyExistingMobile(EpcVerifyExistingMobile epcVerifyExistingMobile) {
        String iSubrNum = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVerifyExistingMobile.getSubrNum()));
        boolean isValid = true;
        StringBuilder errMsgSb = new StringBuilder();
        EpcGetSubrInfoResult epcGetSubrInfoResult = null;
        EpcSubscriber epcSubscriber = null;
        
        try {
            // basic checking
            if("".equals(iSubrNum)) {
                isValid = false;
                errMsgSb.append("subr num is empty");
            }
            // end of basic checking

            if(isValid) {
                epcGetSubrInfoResult = epcSubscriberHandler.getSubrInfoBySubrNum(iSubrNum);
                if("0".equals(epcGetSubrInfoResult.getResultCode())) {
                    for(EpcSubscriber subr : epcGetSubrInfoResult.getSubscriberList()) {
                        if(subr.getSubrStatus().equals("OK")) {
                            epcSubscriber = subr;

                            break;
                        }
                    }

                    if(epcSubscriber != null) {
                        epcVerifyExistingMobile.setCustId(epcSubscriber.getCustId());
                        epcVerifyExistingMobile.setCustNum(epcSubscriber.getCustNum());
                        epcVerifyExistingMobile.setSubrNum(iSubrNum);
                        epcVerifyExistingMobile.setAccountNum(epcSubscriber.getAccountNum());

                        epcVerifyExistingMobile.setResult("SUCCESS");
                    } else {
                        epcVerifyExistingMobile.setResult("FAIL");
                        epcVerifyExistingMobile.setErrMsg("active subr is not found");
                    }
                } else {
                    epcVerifyExistingMobile.setResult("FAIL");
                    epcVerifyExistingMobile.setErrMsg(epcGetSubrInfoResult.getResultMsg());
                }
            } else {
                epcVerifyExistingMobile.setResult("FAIL");
                epcVerifyExistingMobile.setErrMsg(errMsgSb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcVerifyExistingMobile.setResult("FAIL");
            epcVerifyExistingMobile.setErrMsg(e.getMessage());
        } finally {
        }
        
        return epcVerifyExistingMobile;
    }
    
    
    public EpcGetDealerCode getDealerCode() {
        EpcGetDealerCode epcGetDealerCode = new EpcGetDealerCode();
        ArrayList<EpcDealerCode> dealerCodes = new ArrayList<EpcDealerCode>();
        epcGetDealerCode.setDealerCodes(dealerCodes);
        // ...        
        
        return epcGetDealerCode;
    }
    
    
    public EpcChangeBagResult changeBag(EpcChangeBag epcChangeBag) {
        EpcChangeBagResult epcChangeBagResult = new EpcChangeBagResult();
        String oldCustId = StringHelper.trim(epcChangeBag.getOldCustId());
        String oldQuoteGuid = StringHelper.trim(epcChangeBag.getOldQuoteGuid());
        String newCustId = StringHelper.trim(epcChangeBag.getNewCustId());
        String newQuoteGuid = "";
        EpcQuote epcQuoteOld = null;
        EpcTmpQuote epcTmpQuote = null;
        EpcQuoteItem[] epcQuoteItemOldArray = null;
        EpcCreateQuoteResult epcCreateQuoteResult = null;
        EpcDeleteQuoteResult epcDeleteQuoteResult = null;
        String packageGuid = "";
        HashMap<String, Object> productCandidate = null;
        EpcUpdateModifiedItemToQuoteResult epcUpdateModifiedItemToQuoteResult = null;
        EpcAddProductToQuoteResult epcAddProductToQuoteResult = null;
        EpcQuoteItem epcQuoteItemNew = null;
        EpcQuoteItemForUpdate epcQuoteItemForUpdate = null;
        String itemGuidNew = "";
        boolean isValid = true;
        String errMsg = "";
        String logStr = "[changeBag][oldCustId:" + oldCustId + "][newCustId:" + newCustId + "][oldQuoteGuid:" + oldQuoteGuid + "] ";
        
        epcChangeBagResult.setOldCustId(oldCustId);
        epcChangeBagResult.setOldQuoteGuid(oldQuoteGuid);
        epcChangeBagResult.setNewCustId(newCustId);
        epcChangeBagResult.setNewQuoteGuid("");
        epcChangeBagResult.setErrMsg("");
        
        try {
            // basic checking
            if("".equals(oldCustId)) {
                isValid = false;
                errMsg += "old cust id is empty. ";
            }
            
            if("".equals(newCustId)) {
                isValid = false;
                errMsg += "new cust id is empty. ";
            }
            
            if("".equals(oldQuoteGuid)) {
                isValid = false;
                errMsg += "old quote guid is empty. ";
            } else {
                // get old quote detail
                epcQuoteOld = epcQuoteHandler.getQuoteInfo(oldQuoteGuid);
                if(epcQuoteOld == null) {
                    isValid = false;
                    errMsg += "quote " + oldQuoteGuid + " is not found. ";
                } else {
                    if(!oldCustId.equals(epcQuoteOld.getCustomerRef())) {
                        isValid = false;
                        errMsg += "this quote is not for this customer. ";
                    }
                }
                // end of get old quote detail
            }
            
            
            // end of basic checking
            
            if(isValid) {
                // create process ctrl
                // ...
                // end of create process ctrl


                // create a new empty quote (with new cust id)
                epcTmpQuote = new EpcTmpQuote();
                epcTmpQuote.setCustomerRef(newCustId);
                epcTmpQuote.setContextData(epcQuoteOld.getContextData());
//                epcTmpQuote.setItems(null);
                epcTmpQuote.setQuoteType(epcQuoteOld.getQuoteType());

                epcCreateQuoteResult = epcQuoteHandler.createQuote(epcTmpQuote);
                newQuoteGuid = epcCreateQuoteResult.getCpqQuoteGUID();
                if(!"SUCCESS".equals(epcCreateQuoteResult.getResult())) {
                    throw new Exception(epcCreateQuoteResult.getErrMsg());
                } else {
                    epcChangeBagResult.setNewQuoteGuid(newQuoteGuid);
                }
                // end of create a new empty quote (with new cust id)
                
                
                // loop thru quote items
                for(EpcQuoteItem epcQuoteItem: epcQuoteOld.getItems()) {
                    packageGuid = epcQuoteItem.getProductId();
                    productCandidate = epcQuoteItem.getProductCandidate();
                    
                    // add package to new quote
                    //  tmp modified as zero, kerrytsang, 20230320
                    epcAddProductToQuoteResult = epcQuoteHandler.addProductToQuote(0, newQuoteGuid, packageGuid);
                    if(!"SUCCESS".equals(epcAddProductToQuoteResult.getResult())) {
                        throw new Exception(epcAddProductToQuoteResult.getErrMsg());
                    } else {
                        epcChangeBagResult.setNewQuoteGuid(newQuoteGuid);
                        
                        epcQuoteItemNew = epcAddProductToQuoteResult.getEpcQuoteItem();
                        itemGuidNew = epcQuoteItemNew.getId();
                    }
                    
                    // set old productcandidate to new quote item
                    epcQuoteItemNew.setProductCandidate(productCandidate);
                    
                    // prepare quote item for update
                    epcQuoteItemForUpdate = new EpcQuoteItemForUpdate();
        			epcQuoteItemForUpdate.setProductId(epcQuoteItemNew.getProductId());
        			epcQuoteItemForUpdate.setLinkedItemId(epcQuoteItemNew.getLinkedItemId());
        			epcQuoteItemForUpdate.setItemAction(epcQuoteItemNew.getItemAction());
        			epcQuoteItemForUpdate.setProductCandidate(epcQuoteItemNew.getProductCandidate());
        			epcQuoteItemForUpdate.setMetaTypeLookup(epcQuoteItemNew.getMetaTypeLookup());
        			epcQuoteItemForUpdate.setName(epcQuoteItemNew.getName());
        			epcQuoteItemForUpdate.setPrePricedCandidate(epcQuoteItemNew.getPrePricedCandidate());
        			epcQuoteItemForUpdate.setMetaDataLookup(epcQuoteItemNew.getMetaDataLookup());
        			epcQuoteItemForUpdate.setId(epcQuoteItemNew.getId());
        			epcQuoteItemForUpdate.setItemNumber(epcQuoteItemNew.getItemNumber());
        			epcQuoteItemForUpdate.setCreated(epcQuoteItemNew.getCreated());
        			epcQuoteItemForUpdate.setDescription(epcQuoteItemNew.getDescription());
        			epcQuoteItemForUpdate.setSupersededById(epcQuoteItemNew.getSupersededById());
        			epcQuoteItemForUpdate.setSupersededFromId(epcQuoteItemNew.getSupersededFromId());
        			epcQuoteItemForUpdate.setDecorators(epcQuoteItemNew.getDecorators());
        			epcQuoteItemForUpdate.setHonourExistingPrice(false);
        			epcQuoteItemForUpdate.setPortfolioItemId(epcQuoteItemNew.getPortfolioItemId());
        			epcQuoteItemForUpdate.setPortfolioItem(epcQuoteItemNew.getPortfolioItem());
                    
                    // update product candidate to new quote
                    //  tmp modified as zero, kerrytsang, 20230320
                    epcUpdateModifiedItemToQuoteResult = epcQuoteHandler.updateModifiedItemToQuote(0, newQuoteGuid, itemGuidNew, epcQuoteItemForUpdate);
                    if(!"SUCCESS".equals(epcUpdateModifiedItemToQuoteResult.getResult())) {
                        throw new Exception(epcUpdateModifiedItemToQuoteResult.getErrMsg());
                    }
                }
                // end of loop thru quote items
                
                
                // update reserved mobile
                // ...
                
                
                // update epc tables - epc_order / epc_order_quote / epc_order_cust_profile
                updateNewCustIdNewQuoteGuidToOrder(oldCustId, newCustId, oldQuoteGuid, newQuoteGuid);
                
                
                // delete old quote
                epcDeleteQuoteResult = epcQuoteHandler.deleteQuote(epcQuoteOld);
                if(!"SUCCESS".equals(epcDeleteQuoteResult.getResult())) {
                    throw new Exception(epcDeleteQuoteResult.getErrMsg());
                }
                // end of delete old quote


                epcChangeBagResult.setResult("SUCCESS");
            } else {
                // error 
                epcChangeBagResult.setResult("FAIL");
                epcChangeBagResult.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            // error 
            epcChangeBagResult.setResult("FAIL");
            epcChangeBagResult.setErrMsg(e.getMessage());
        } finally {
            
        }
        return epcChangeBagResult;
    }
    
    
    public boolean updateNewCustIdNewQuoteGuidToOrder(String oldCustId, String newCustId, String oldQuoteGuid, String newQuoteGuid) {
        boolean isUpdate = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        int smcOrderId = 0;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            // check and get smc order id
            sql = "select a.order_id " +
                  " from epc_order a, epc_order_quote b " +
                  " where b.cpq_quote_guid = ? " +
                  " and a.order_id = b.order_id " +
                  " and a.cust_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, oldQuoteGuid); // cpq_quote_guid
            pstmt.setString(2, oldCustId); // cust_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                smcOrderId = rset.getInt("order_id");
            } else {
                throw new Exception("order is not found by " + oldQuoteGuid + "/" + oldCustId);
            }
            // end of check and get smc order id
            
            
            // update new cust id to epc_order
            sql = "update epc_order " +
                  "  set cust_id = ? " +
                  " where order_id = ? " +
                  "   and cust_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newCustId); // cust_id - new cust id
            pstmt.setInt(2, smcOrderId); // order_id
            pstmt.setString(3, oldCustId); // cust_id - old cust id
            pstmt.executeUpdate();
            // end of update new cust id to epc_order
            
            
            // update new quote created 
            sql = "update epc_order_quote " +
                  "  set cpq_quote_guid = ? " +
                  " where order_id = ? " +
                  "  and cpq_quote_guid = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newQuoteGuid); // cpq_quote_guid - new quote guid
            pstmt.setInt(2, smcOrderId); // order_id
            pstmt.setString(3, oldQuoteGuid); // cpq_quote_guid - old quote guid
            pstmt.executeUpdate();
            // end of update new quote created 
            
            
            // update new cust id to cust profile
            sql = "update epc_order_cust_profile " +
                  "  set cust_id = ? " +
                  " where order_id = ? " +
                  "  and cust_id = ? " +
                  "  and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newCustId); // cust_id - new cust id
            pstmt.setInt(2, smcOrderId); // order_id
            pstmt.setString(3, oldCustId); // cust_id - old cust id
            pstmt.setString(4, "A"); // status - Active
            pstmt.executeUpdate();
            // end of update new cust id to cust profile
            
            
            conn.commit();
            
            isUpdate = true;
        } catch(Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return isUpdate;
    }
    
    
    public boolean updateConvertedQuoteToOrder(String quoteGuid, String newQuoteGuid) {
        boolean isUpdate = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_quote " +
                  "  set cpq_quote_guid_submit = ? " +
                  " where cpq_quote_guid = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newQuoteGuid); // cpq_quote_guid_submit
            pstmt.setString(2, quoteGuid); // cpq_quote_guid
            pstmt.executeUpdate();
            
            conn.commit();
            
            isUpdate = true;
        } catch(Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return isUpdate;
    }
    
    
    public boolean updateSigmaOrderIdToOrder(String quoteGuid, String sigmaOrderId) {
        boolean isUpdate = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_quote " +
                  "  set cpq_order_id = ? " +
                  " where cpq_quote_guid = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, sigmaOrderId); // cpq_order_id
            pstmt.setString(2, quoteGuid); // cpq_quote_guid
            pstmt.executeUpdate();
            
            conn.commit();
            
            isUpdate = true;
        } catch(Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return isUpdate;
    }
    
    
    public boolean updateReceiptInfoToOrder(int smcOrderId, String receiptNo, BigDecimal totalOrderAmount) {
        boolean isUpdate = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order " +
                  "  set receipt_no = ?, " +
                  "      total_charge_amount = ? " +
                  " where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, epcSecurityHelper.validateId(receiptNo)); // cpq_order_id
            pstmt.setBigDecimal(2, totalOrderAmount); // total_charge_amount
            pstmt.setInt(3, smcOrderId); // order_id
            pstmt.executeUpdate();
            
            conn.commit();
            
            isUpdate = true;
        } catch(Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return isUpdate;
    }


    public boolean saveReceiptToOrder(int smcOrderId, String receiptNo, BigDecimal totalPaymentAmount, String createUser, String createSalesman, String createLocation, String createChannel) {
        boolean isUpdate = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "insert into epc_order_receipt ( " +
                  "  rec_id, order_id, receipt_no, pay_amount, create_user, " +
                  "  create_salesman, create_location, create_channel, create_date " +
                  ") values ( " +
                  "  epc_order_id_seq.nextval,?,?,?,?, " + 
                  "  ?,?,?,sysdate " +
                  ") ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, epcSecurityHelper.validateId(receiptNo)); // receipt_no
            pstmt.setBigDecimal(3, totalPaymentAmount); // pay_amount
            pstmt.setString(4, epcSecurityHelper.validateId(createUser)); // create_user
            pstmt.setString(5, epcSecurityHelper.validateId(createSalesman)); // create_salesman
            pstmt.setString(6, epcSecurityHelper.validateId(createLocation)); // create_location
            pstmt.setString(7, epcSecurityHelper.validateId(createChannel)); // create_channel
            pstmt.executeUpdate();
            
            conn.commit();
            
            isUpdate = true;
        } catch(Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return isUpdate;
    }
    
    
    // invoked during placeOrder()
    //  placeOrder() -> epcStockHandler.confirmTmpReserve() -> getOrderItemsForReservation()
    public ArrayList<EpcOrderItemInfo> getOrderItemsForReservation(int smcOrderId) {
    	ArrayList<EpcOrderItemInfo> epcOrderItemInfoList = new ArrayList<EpcOrderItemInfo>();
    	EpcOrderItemInfo epcOrderItemInfo = null;
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
    	try {
            conn = epcDataSource.getConnection();
            
            sql = "select item_id, item_code, warehouse, reserve_id, pickup_date, " +
                  "       is_reserve, reserve_type " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and item_cat = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, "DEVICE"); // item_cat
            rset = pstmt.executeQuery();
            while(rset.next()) {
            	epcOrderItemInfo = new EpcOrderItemInfo();
            	epcOrderItemInfo.setItemId(StringHelper.trim(rset.getString("item_id")));
            	epcOrderItemInfo.setWarehouse(StringHelper.trim(rset.getString("warehouse")));
            	epcOrderItemInfo.setItemCode(StringHelper.trim(rset.getString("item_code")));
            	epcOrderItemInfo.setReserveId(StringHelper.trim(rset.getString("reserve_id")));
            	epcOrderItemInfo.setPickupDate(StringHelper.trim(rset.getString("pickup_date")));
                epcOrderItemInfo.setIsReserveItem(StringHelper.trim(rset.getString("is_reserve")));
                epcOrderItemInfo.setReserveType(StringHelper.trim(rset.getString("reserve_type")));
            	
            	epcOrderItemInfoList.add(epcOrderItemInfo);
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    	return epcOrderItemInfoList;
    }
    
    
    public ArrayList<EpcOrderItemInfo> getDoaItemsForReservation(int smcOrderId, ArrayList<EpcOrderItem> itemList) {
    	ArrayList<EpcOrderItemInfo> epcOrderItemInfoList = new ArrayList<EpcOrderItemInfo>();
    	EpcOrderItemInfo epcOrderItemInfo = null;
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String sqlItemId = "";
        int idx = 3;
        
    	try {
            conn = epcDataSource.getConnection();
            
            sql = "select item_id, item_code, warehouse, reserve_id, pickup_date, " +
                  "       is_reserve, reserve_type " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and item_cat = ? " +
                  "   and item_id in ( ";
            for(EpcOrderItem i : itemList) {
            	if("".equals(sqlItemId)) {
            		sqlItemId = "?";
            	} else {
            		sqlItemId += "," + "?";
            	}
            }
            sql += sqlItemId + "   ) ";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, "DEVICE"); // item_cat
            for(EpcOrderItem i : itemList) {
            	pstmt.setString(idx++, i.getItemId()); // item_id
            }
            
            rset = pstmt.executeQuery();
            while(rset.next()) {
            	epcOrderItemInfo = new EpcOrderItemInfo();
            	epcOrderItemInfo.setItemId(StringHelper.trim(rset.getString("item_id")));
            	epcOrderItemInfo.setWarehouse(StringHelper.trim(rset.getString("warehouse")));
            	epcOrderItemInfo.setItemCode(StringHelper.trim(rset.getString("item_code")));
            	epcOrderItemInfo.setReserveId(StringHelper.trim(rset.getString("reserve_id")));
            	epcOrderItemInfo.setPickupDate(StringHelper.trim(rset.getString("pickup_date")));
                epcOrderItemInfo.setIsReserveItem(StringHelper.trim(rset.getString("is_reserve")));
                epcOrderItemInfo.setReserveType(StringHelper.trim(rset.getString("reserve_type")));
            	
            	epcOrderItemInfoList.add(epcOrderItemInfo);
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    	return epcOrderItemInfoList;
    }
    
    
    public ArrayList<EpcOrderItemInfo> getOrderItemsForOrderValidation(int smcOrderId) {
    	ArrayList<EpcOrderItemInfo> epcOrderItemInfoList = new ArrayList<EpcOrderItemInfo>();
    	EpcOrderItemInfo epcOrderItemInfo = null;
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
    	try {
            conn = epcDataSource.getConnection();
            
            sql = "select item_id, delivery_id, item_code " +
                    "  from epc_order_item " +
                    " where order_id = ? " +
                    "   and item_cat in (?,?,?,?,?,?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, EpcItemCategory.DEVICE); // item_cat
            pstmt.setString(3, EpcItemCategory.SCREEN_REPLACE); // item_cat
            pstmt.setString(4, EpcItemCategory.APPLECARE); // item_cat
            pstmt.setString(5, EpcItemCategory.SIM); // item_cat
            pstmt.setString(6, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
            pstmt.setString(7, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
            rset = pstmt.executeQuery();
            while(rset.next()) {
            	epcOrderItemInfo = new EpcOrderItemInfo();
            	epcOrderItemInfo.setItemId(StringHelper.trim(rset.getString("item_id")));
            	epcOrderItemInfo.setDeliveryId(rset.getInt("delivery_id"));
                epcOrderItemInfo.setItemCode(StringHelper.trim(rset.getString("item_code")));
            	
            	epcOrderItemInfoList.add(epcOrderItemInfo);
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    	return epcOrderItemInfoList;
    }
    
    
    // invoked during placeOrder()
    //  placeOrder() -> epcStockHandler.confirmTmpReserve() -> updateReserveIdBackToOrder()
    public boolean updateReserveIdBackToOrder(int smcOrderId, ArrayList<EpcOrderItemInfo> epcOrderItemInfoList) {
    	Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        boolean isUpdate = false;
        EpcLogStockStatus epcLogStockStatus = null;
        String itemId = "";
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_item " +
                  "   set reserve_id = ?, " +
            	  "       stock_status = ?, " +
                  "       stock_status_desc = ? " +
                  " where order_id = ? " + 
                  "   and item_id = ? " +
                  "   and item_cat = ? ";
            pstmt = conn.prepareStatement(sql);
            
            for(EpcOrderItemInfo e : epcOrderItemInfoList) {
                itemId = epcSecurityHelper.validateId(e.getItemId());

	            pstmt.setString(1, epcSecurityHelper.validateId(e.getReserveId())); // reserve_id
	            pstmt.setString(2, e.getStatus()); // stock_status
                pstmt.setString(3, e.getStatusDesc()); // stock_status_desc
	            pstmt.setInt(4, smcOrderId); // order_id
	            pstmt.setString(5, itemId); // item_id
	            pstmt.setString(6, EpcItemCategory.DEVICE); // item_cat - DEVICE
	            pstmt.executeUpdate();

                // create log
                epcLogStockStatus = new EpcLogStockStatus();
                epcLogStockStatus.setOrderId(smcOrderId);
                epcLogStockStatus.setItemId(itemId);
                epcLogStockStatus.setOldStockStatus("");
                epcLogStockStatus.setNewStockStatus(e.getStatus());
                epcOrderLogHandler.logStockStatus(conn, epcLogStockStatus);
                // end of create log
            }
            
            conn.commit();
            
            isUpdate = true;
        } catch(Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
    
    
    public EpcLockOrderResult lockOrder(EpcLockOrderResult epcLockOrderResult) {
    	String custId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcLockOrderResult.getCustId()));
        int orderId = epcLockOrderResult.getOrderId();
        String action = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcLockOrderResult.getAction()));
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String orderReference = "";
        int cnt = 0;
        boolean isValid = true;
        String errMsg = "";
        String oldOrderStatus = "";
        String newOrderStatus = "";
        EpcLogOrderStatus epcLogOrderStatus = null;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            // basic checking
            orderReference = isOrderBelongCust(conn, custId, orderId);
        	if("NOT_BELONG".equals(orderReference)) {
        		errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
            	isValid = false;
        	}
        	
        	if(!"LOCK".equals(action) && !"UNLOCK".equals(action)) {
        		errMsg += "input action [" + action + "] is not valid. ";
            	isValid = false;
        	}
        	
        	if("LOCK".equals(action)) {
	        	sql = "select count(1) " +
	        	      "  from epc_order " +
	        	      " where order_id = ? " +
	        	      "   and cust_id = ? " +
	        	      "   and order_status in (?) ";
	        	pstmt = conn.prepareStatement(sql);
	        	pstmt.setInt(1, orderId); // order_id
	            pstmt.setString(2, custId); // cust_id
	            pstmt.setString(3, "I"); // order_status - init
	            rset = pstmt.executeQuery();
	            if(rset.next()) {
	            	cnt = rset.getInt(1);
	            } rset.close(); rset = null;
	            pstmt.close(); pstmt = null;
	            
	            if(cnt == 0) {
	            	errMsg += "input order id [" + orderId + "] cannot be locked due to unsuitable order status. ";
	            	isValid = false;
	            }
        	} if("UNLOCK".equals(action)) {
        		sql = "select count(1) " +
  	        	      "  from epc_order " +
  	        	      " where order_id = ? " +
  	        	      "   and cust_id = ? " +
  	        	      "   and order_status in (?) ";
  	        	pstmt = conn.prepareStatement(sql);
  	        	pstmt.setInt(1, orderId); // order_id
  	            pstmt.setString(2, custId); // cust_id
  	            pstmt.setString(3, "LOCK"); // order_status - LOCK
  	            rset = pstmt.executeQuery();
  	            if(rset.next()) {
  	            	cnt = rset.getInt(1);
  	            } rset.close(); rset = null;
  	            pstmt.close(); pstmt = null;
  	            
  	            if(cnt == 0) {
  	            	errMsg += "input order id [" + orderId + "] cannot be locked due to unsuitable order status. ";
  	            	isValid = false;
  	            }
        	}
            
            // end of basic checking
            
            if(isValid) {
                // get current order status
                sql = "select order_status " +
                      "  from epc_order " +
                      " where order_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId); // order_id
                rset = pstmt.executeQuery();
                if(rset.next()) {
                    oldOrderStatus = StringHelper.trim(rset.getString("order_status"));
                } rset.close();
                pstmt.close();
                // end of get current order status


                sql = "update epc_order " +
                        "   set order_status = ? " +
                        " where order_id = ? " + 
                        "   and cust_id = ? " +
                        "   and order_status = ? ";
                pstmt = conn.prepareStatement(sql);
                
                if("LOCK".equals(action)) {
                    // lock the order
                    newOrderStatus = "LOCK";
                } else {
                    // unlock the order
                    newOrderStatus = "I";
                }
                pstmt.setString(1, newOrderStatus); // order_status
                pstmt.setInt(2, orderId); // order_id
                pstmt.setString(3, custId); // cust_id
                pstmt.setString(4, oldOrderStatus); // order_status
                pstmt.executeUpdate();

                // create log
                epcLogOrderStatus = new EpcLogOrderStatus();
                epcLogOrderStatus.setOrderId(orderId);
                epcLogOrderStatus.setOldOrderStatus(oldOrderStatus);
                epcLogOrderStatus.setNewOrderStatus(newOrderStatus);
                epcLogOrderStatus.setCreateUser("");
                epcLogOrderStatus.setCreateSalesman("");
                epcLogOrderStatus.setCreateChannel("");
                epcLogOrderStatus.setCreateLocation("");
                epcOrderLogHandler.logOrderStatus(conn, epcLogOrderStatus);
                // end of create log
	            
	            conn.commit();
	            
	            epcLockOrderResult.setResult("SUCCESS");
            } else {
            	// error
            	epcLockOrderResult.setResult("FAIL");
            	epcLockOrderResult.setErrMsg(errMsg);
            }
        } catch(Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            epcLockOrderResult.setResult("FAIL");
        	epcLockOrderResult.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcLockOrderResult;
    }
    
    
    public boolean deleteCaseItemRecord(Connection conn, int orderId, int quoteId, String caseId) {
    	PreparedStatement pstmt = null;
    	String sql = "";
    	boolean isDelete = false;
    	
    	try {
            sql = "delete from epc_order_item where order_id = ? and quote_id = ? and case_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setInt(2, quoteId); // quote_id
            pstmt.setString(3, caseId); // case_id
            pstmt.executeUpdate();
            
            sql = "delete from epc_order_case where order_id = ? and quote_id = ? and case_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setInt(2, quoteId); // quote_id
            pstmt.setString(3, caseId); // case_id
            pstmt.executeUpdate();

            sql = "delete from epc_order_contract_details_hdr where order_id = ? and case_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, caseId); // case_id
            pstmt.executeUpdate();

            sql = "delete from epc_order_contract_details_dtl where order_id = ? and case_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, caseId); // case_id
            pstmt.executeUpdate();
            
            sql = "delete from epc_order_tnc where order_id = ? and case_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, caseId); // case_id
            pstmt.executeUpdate();

            sql = "delete from epc_order_sa_remark where order_id = ? and case_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, caseId); // case_id
            pstmt.executeUpdate();

    		isDelete = true;
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return isDelete;
    }


    public String getCaseIdByQuoteItemGuid(Connection conn, int orderId, int quoteId, String quoteItemGuid) {
        String caseId = "";
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select case_id from epc_order_case where order_id = ? and quote_id = ? and quote_item_guid = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, quoteId);
            pstmt.setString(3, quoteItemGuid);
            rset = pstmt.executeQuery();
            if(rset.next()) {
                caseId = StringHelper.trim(rset.getString("case_id"));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();

            caseId = "";
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }

        return caseId;
    }


    public EpcQuoteItem getQuoteItemInEpc(int orderId, int quoteId, String quoteGuid, String quoteItemGuid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String quoteItemContent = "";
        EpcQuoteItem epcQuoteItem = null;

        try {
            conn = epcDataSource.getConnection();

            sql = "select quote_item_content " +
                  "  from epc_order_case " +
                  " where order_id = ? " +
                  "   and quote_id = ? " +
                  "   and quote_item_guid = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setInt(2, quoteId); // quote_id
            pstmt.setString(3, quoteItemGuid); // quote_item_guid
            rset = pstmt.executeQuery();
            if(rset.next()) {
                quoteItemContent = StringHelper.trim(rset.getString("quote_item_content"));
                epcQuoteItem = new ObjectMapper().readValue(quoteItemContent, EpcQuoteItem.class);
            } else {
                epcQuoteItem = epcQuoteHandler.getQuoteItem(quoteGuid, quoteItemGuid, "");
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcQuoteItem;
    }
    
    
    public boolean withValidDelivery(int smcOrderId, String sigmaItemId) {
    	Connection conn = null;
    	PreparedStatement pstmt = null;
    	ResultSet rset = null;
    	String sql = "";
    	int cnt = 0;
    	boolean isValid = false;
    	
    	try {
    		conn = epcDataSource.getConnection();
    		
//    		sql = "select count(1) " + 
//                  "  from epc_order_attr a, epc_order_delivery b " + 
//                  " where a.order_id = ? " + 
//                  "   and a.attr_type = ? " + 
//                  "   and a.item_id = ? " + 
//                  "   and a.status = ? " + 
//                  "   and b.delivery_id = to_number(a.attr_value) " + 
//                  "   and b.order_id = a.order_id " + 
//                  "   and b.status = ? ";
    		sql = "select count(1) " + 
                    "  from epc_order_item a, epc_order_delivery b " + 
                    " where a.order_id = ? " + 
                    "   and a.item_id = ? " + 
                    "   and b.delivery_id = a.delivery_id " + 
                    "   and b.order_id = a.order_id " + 
                    "   and b.status = ? ";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setInt(1, smcOrderId); // order_id
    		pstmt.setString(2, epcSecurityHelper.validateId(sigmaItemId)); // item_id
    		pstmt.setString(3, "A"); // epc_order_delivery.status
    		rset = pstmt.executeQuery();
    		if(rset.next()) {
    			cnt = rset.getInt(1);
    		} rset.close(); rset = null;
    		pstmt.close(); pstmt = null;
    		
    		if(cnt > 0) {
    			isValid = true;
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	} finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    	return isValid;
    }
    
    
    public ArrayList<EpcOrderItemDetail> getAllDeviceItems(int orderId, String retrieveType) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<EpcOrderItemDetail> itemList = new ArrayList<EpcOrderItemDetail>();
        EpcOrderItemDetail epcOrderItemDetail = null;
        String rType = StringHelper.trim(retrieveType);
        if("".equals(rType)) {
            rType = "FOR-RESERVE";
        }
        String isPreOrdering = "N";
        String productCode = "";
        String offerDesc = ""; // catalog offer desc
        boolean isSkipReserve = false;
        
        try {
            conn = epcDataSource.getConnection();
            
            if("FOR-DELIVERY".equals(rType)) {
                // FOR-DELIVERY
                //  get DEVICE / SCREEN_REPLACE / APPLECARE / SIM items
                sql = "select * " +
                      "  from epc_order_item " + 
                      " where order_id = ? " +
                      "   and item_cat in (?,?,?,?,?,?) ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId); // order_id
                pstmt.setString(2, EpcItemCategory.DEVICE); // item_cat - DEVICE
                pstmt.setString(3, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
                pstmt.setString(4, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
                pstmt.setString(5, EpcItemCategory.SIM); // item_cat - SIM
                pstmt.setString(6, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
                pstmt.setString(7, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
                rset = pstmt.executeQuery();
                while(rset.next()) {
                    epcOrderItemDetail = new EpcOrderItemDetail();
                    epcOrderItemDetail.setOrderId(orderId);
                    epcOrderItemDetail.setItemId(StringHelper.trim(rset.getString("item_id"))); // item_id
                    epcOrderItemDetail.setItemCat(StringHelper.trim(rset.getString("item_cat"))); // item_cat
                    epcOrderItemDetail.setWarehouse(StringHelper.trim(rset.getString("warehouse"))); // warehouse
                    epcOrderItemDetail.setItemCode(StringHelper.trim(rset.getString("item_code"))); // item_code - product_code
                    epcOrderItemDetail.setReserveId(StringHelper.trim(rset.getString("reserve_id"))); // reserve_id
                    epcOrderItemDetail.setItemCode(StringHelper.trim(rset.getString("item_code"))); // item_code
                    epcOrderItemDetail.setCpqItemDesc(StringHelper.trim(rset.getString("cpq_item_desc"))); // cpq_item_desc
                    epcOrderItemDetail.setCpqItemDescChi(StringHelper.trim(rset.getString("cpq_item_desc_chi"))); // cpq_item_desc_chi
                    
                    itemList.add(epcOrderItemDetail);
                }
                rset.close();
                pstmt.close();
            } else {
                // FOR-RESERVE
                //  get DEVICE items only
                sql = "select a.*, b.cpq_offer_desc " +
                      "  from epc_order_item a, epc_order_case b " + 
                      " where a.order_id = ? " +
                      "   and a.item_cat = ? " +
                      "   and b.order_id = a.order_id " +
                      "   and b.case_id = a.case_id ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId); // order_id
                pstmt.setString(2, EpcItemCategory.DEVICE); // item_cat - DEVICE
                rset = pstmt.executeQuery();
                while(rset.next()) {
                    productCode = StringHelper.trim(rset.getString("item_code"));
                    offerDesc = StringHelper.trim(rset.getString("cpq_offer_desc"));

                    // if preorder, skip reserve tmp ticket
                    isPreOrdering = epcPreorderHandler.isPreOrdering(productCode);
                    if("P".equals(isPreOrdering)) {
                        continue; // no ticket for preorder item, advised by ERP team, 20230822
                    }
                    // end of if preorder, skip reserve tmp ticket

                    // if product code exist in skip reserve setting, then skip
                    isSkipReserve = epcSkipReserveHandler.isSkip(productCode);
                    if(isSkipReserve) {
                        continue;
                    }
                    // end of if product code exist in skip reserve setting, then skip

                    // if catalog offer desc exist in skip reserve setting, then skip
                    isSkipReserve = epcSkipReserveHandler.isSkip(offerDesc);
                    if(isSkipReserve) {
                        continue;
                    }
                    // end of if catalog offer desc exist in skip reserve setting, then skip


                    epcOrderItemDetail = new EpcOrderItemDetail();
                    epcOrderItemDetail.setOrderId(orderId);
                    epcOrderItemDetail.setItemId(StringHelper.trim(rset.getString("item_id"))); // item_id
                    epcOrderItemDetail.setItemCat(StringHelper.trim(rset.getString("item_cat"))); // item_cat
                    epcOrderItemDetail.setWarehouse(StringHelper.trim(rset.getString("warehouse"))); // warehouse
                    epcOrderItemDetail.setItemCode(productCode); // item_code - product_code
                    epcOrderItemDetail.setReserveId(StringHelper.trim(rset.getString("reserve_id"))); // reserve_id
                    epcOrderItemDetail.setItemCode(StringHelper.trim(rset.getString("item_code"))); // item_code
                    epcOrderItemDetail.setCpqItemDesc(StringHelper.trim(rset.getString("cpq_item_desc"))); // cpq_item_desc
                    epcOrderItemDetail.setCpqItemDescChi(StringHelper.trim(rset.getString("cpq_item_desc_chi"))); // cpq_item_desc_chi
                    
                    itemList.add(epcOrderItemDetail);
                }
                rset.close();
                pstmt.close();
            }          
        } catch (Exception e) {
            e.printStackTrace();
            
            itemList.clear();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return itemList;
    }


    public CreateAsiaMiles createAsiaMiles(int orderId, String createUser, String createSalesman) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String custNum = "";
        String subrNum = "";
        String contactNo = "";
        CreateAsiaMiles createAsiaMiles = new CreateAsiaMiles();

        try {
            conn = epcDataSource.getConnection();
            
            sql = "select item_code as rule_id, b.quote_item_guid, c.order_reference, b.cust_num, b.subr_num, " +
                  "       c.contact_no " +
                  "  from epc_order_item a, epc_order_case b, epc_order c " +
                  " where a.order_id = ? " +
                  "   and a.item_cat = ? " +
                  "   and b.case_id = a.case_id " +
                  "   and c.order_id = a.order_id ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, "ASIA_MILES"); // item_cat
            rset = pstmt.executeQuery();
            if(rset.next()) {
                custNum = StringHelper.trim(rset.getString("cust_num"));
                subrNum = StringHelper.trim(rset.getString("subr_num"));
                contactNo = StringHelper.trim(rset.getString("contact_no"));

                createAsiaMiles.setOrderReference(StringHelper.trim(rset.getString("order_reference")));
                createAsiaMiles.setQuoteItemGuid(StringHelper.trim(rset.getString("quote_item_guid")));
                createAsiaMiles.setCustNum(custNum);
                createAsiaMiles.setSubrNum(subrNum);
                createAsiaMiles.setAsiaMilesRuleId(rset.getInt("rule_id"));
                if(StringUtils.isNotBlank(contactNo)) {
                    createAsiaMiles.setAcknowledgeMobile(contactNo);
                } else {
                    createAsiaMiles.setAcknowledgeMobile(subrNum);
                }
                createAsiaMiles.setUserid(1);
                createAsiaMiles.setSalesmanId(1);

                epcAsiaMilesHandler.createAsiaMilesRecord(createAsiaMiles);
            } else {
                // no need to proceed !!!
                createAsiaMiles.setResult("SUCCESS");
                createAsiaMiles.setErrMsg("No need to proceed!!!");
            }
        } catch (Exception e) {
            e.printStackTrace();

            createAsiaMiles.setResult("FAIL");
            createAsiaMiles.setErrMsg(e.getMessage());
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return createAsiaMiles;
    }

    
    public boolean markCancelReceiptToCase(int orderId, ArrayList<String> caseIdList, String receiptNo) {
    	Connection conn = null;
    	PreparedStatement pstmt = null;
    	String sql = "";
    	boolean isMarked = false;
    	
    	try {
        	conn = epcDataSource.getConnection();
        	conn.setAutoCommit(false);
        	
        	sql = "update epc_order_case " +
        	      "   set cancel_receipt = ?, " +
        	      "       cancenl_date = sysdate " +
        		  " where order_id = ? " +
        	      "   and case_id = ? ";
        	pstmt = conn.prepareStatement(sql);
        	
        	for(String caseId: caseIdList) {
        		pstmt.setString(1, epcSecurityHelper.validateId(receiptNo)); // cancel_receipt
        		pstmt.setInt(2, orderId); // order_id
        		pstmt.setString(3, caseId); // case_id
        		pstmt.executeUpdate();
        	}
        	pstmt.close(); pstmt = null;
        	
        	conn.commit();
        	
        	isMarked = true;
    	} catch (Exception e) {
    		e.printStackTrace();
    		
    		try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
    	} finally {
    		try { if(conn != null) { conn.setAutoCommit(false); } } catch (Exception ee) {}
    		try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
    	}
    	return isMarked;
    }
    
    
    public HashMap<String, Object> getOfferSpec(String offerGuid, String deviceGuid) {
    	HashMap<String, Object> offerSpecMap = null;
    	RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "entities/" + offerGuid;

    	try {
    		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
    		responseEntity = restTemplate.getForEntity(apiUrl, String.class, headers);
    		if(responseEntity.getStatusCodeValue() == 200) {
    			offerSpecMap = objectMapper.readValue(responseEntity.getBody(), HashMap.class);
    			recureOfferSpec(offerSpecMap, deviceGuid);
    		} else {
    			
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return offerSpecMap;
    }
    
    
    public void recureOfferSpec(HashMap<String, Object> offerSpecMap, String deviceGuid) {
    	ArrayList<HashMap<String, Object>> newProductToProductList = new ArrayList<HashMap<String, Object>>();
    	
    	if(offerSpecMap != null && offerSpecMap.containsKey("productToProduct")) {
    		ArrayList<HashMap<String, Object>> productToProductList = (ArrayList<HashMap<String, Object>>)offerSpecMap.get("productToProduct");
    		for(HashMap<String, Object> productToProductMap : productToProductList) {
//System.out.println("loop:" + (String)productToProductMap.get("id"));
    			HashMap<String, Object> productMap = (HashMap<String, Object>)productToProductMap.get("product");
    			String id = (String) productMap.get("id");
    			if(id.equals(deviceGuid)) {
    				// entity is found !!!
//System.out.println("found it, id:" + id);
    				newProductToProductList.add(productToProductMap);
    			}
    		}
    		
    		if(newProductToProductList.size() > 0) {
    			// cut the others in the same level
    			offerSpecMap.put("productToProduct", newProductToProductList);
    		} else {
    			// keep recured
	    		for(HashMap<String, Object> productToProductMap : productToProductList) {
	    			recureOfferSpec((HashMap<String, Object>)productToProductMap.get("product"), deviceGuid);
	    		}
    		}
    	}
    }


    public void recureCandidate(HashMap<String, Object> productCandidateMap, String entityGuid, HashMap<String, Object> targetMap) {
        if(productCandidateMap != null && productCandidateMap.containsKey("EntityID")) {
            if(entityGuid.equals((String)productCandidateMap.get("EntityID"))) {
                targetMap = productCandidateMap;
            } else {
                // recure
                ArrayList<HashMap<String, Object>> childEntityList = (ArrayList<HashMap<String, Object>>)productCandidateMap.get("ChildEntity");
                for(HashMap<String, Object> tmpMap : childEntityList) {
                    recureCandidate(tmpMap, entityGuid, targetMap);
                }
            }
        }
    }


    public void getCompiledSpec(EpcGetSpec epcGetSpec) {
        Connection conn = null;
    	PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String iProductGuid = "";
        String quoteGuid = ""; // for system use
        String quoteItemGuid = "";
        EpcAddProductToQuoteResult epcAddProductToQuoteResult = null;
        EpcQuoteItem epcQuoteItem = null;
        EpcQuote epcQuote = null;
        EpcDeleteQuoteItemResult epcDeleteQuoteItemResult = null;
        EpcTmpQuote epcTmpQuote = null;
        EpcCreateQuoteResult epcCreateQuoteResult = null;


        try {
            conn = epcDataSource.getConnection();

            iProductGuid = epcSecurityHelper.encodeForSQL(epcGetSpec.getProductGuid());

            sql = "select key_str1 " +
                  "  from epc_control_tbl " +
                  " where rec_type = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "LOAD_SPEC_QUOTE_GUID"); // rec_type
            rset = pstmt.executeQuery();
            if(rset.next()) {
                quoteGuid = StringHelper.trim(rset.getString("key_str1"));
            } rset.close();
            pstmt.close();

            if("".equals(quoteGuid)) {
                // create new quote
                epcTmpQuote = new EpcTmpQuote();
                epcTmpQuote.setCustomerRef(SYSTEM_CUST_ID);
                epcTmpQuote.setQuoteType(0);
                epcTmpQuote.setContextData(new HashMap<String, Object>());
                epcCreateQuoteResult = epcQuoteHandler.createQuote(epcTmpQuote);
                if("SUCCESS".equals(epcCreateQuoteResult.getResult())) {
                    quoteGuid = epcCreateQuoteResult.getCpqQuoteGUID();

                    sql = "insert into epc_control_tbl ( " +
                          "  rec_id, rec_type, key_str1, value_date1 " +
                          ") values ( " +
                          "  epc_control_tbl_seq.nextval, ?, ?, sysdate " +
                          ") ";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, "LOAD_SPEC_QUOTE_GUID"); // rec_type
                    pstmt.setString(2, quoteGuid); // key_str1
                    pstmt.executeUpdate();
                    pstmt.close();
                } else {
                    throw new Exception("cannot create new quote. " + epcCreateQuoteResult.getErrMsg());
                }
            }

            // add package
            epcAddProductToQuoteResult = epcQuoteHandler.addProductToQuote(0, quoteGuid, iProductGuid, "Y");
            if("SUCCESS".equals(epcAddProductToQuoteResult.getResult())) {
                epcQuoteItem = epcAddProductToQuoteResult.getEpcQuoteItem();
                quoteItemGuid = epcQuoteItem.getId();
                epcGetSpec.setCompiledSpecification(epcQuoteItem.getCompiledSpecification());

                // delete quote item
                epcQuote = new EpcQuote();
                epcQuote.setId(quoteGuid);
                epcQuote.setUpdated("2017-08-28T06:19:11.733Z");
                epcDeleteQuoteItemResult = epcQuoteHandler.deleteQuoteItem(epcQuote, quoteItemGuid);

                epcGetSpec.setResult("SUCCESS");
            } else {
                epcGetSpec.setResult("FAIL");
                epcGetSpec.setErrMsg(epcAddProductToQuoteResult.getErrMsg());
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }


    public void getCompiledSpec2(EpcGetSpec epcGetSpec) {
        String iProductGuid = "";
        String quoteGuid = ""; // for system use
//        String quoteItemGuid = "";
        EpcAddProductToQuoteResult epcAddProductToQuoteResult = null;
        EpcQuoteItem epcQuoteItem = null;
        EpcQuote epcQuote = null;
        EpcDeleteQuoteResult epcDeleteQuoteResult = null;
        EpcTmpQuote epcTmpQuote = null;
        EpcCreateQuoteResult epcCreateQuoteResult = null;


        try {
            iProductGuid = epcSecurityHelper.encodeForSQL(epcGetSpec.getProductGuid());

            // create new quote
            epcTmpQuote = new EpcTmpQuote();
            epcTmpQuote.setCustomerRef(SYSTEM_CUST_ID);
            epcTmpQuote.setQuoteType(0);
            epcTmpQuote.setContextData(new HashMap<String, Object>());
            epcCreateQuoteResult = epcQuoteHandler.createQuote(epcTmpQuote);
            if("SUCCESS".equals(epcCreateQuoteResult.getResult())) {
                quoteGuid = epcCreateQuoteResult.getCpqQuoteGUID();

                epcAddProductToQuoteResult = epcQuoteHandler.addProductToQuote(0, quoteGuid, iProductGuid, "Y");
                if("SUCCESS".equals(epcAddProductToQuoteResult.getResult())) {
                    epcQuoteItem = epcAddProductToQuoteResult.getEpcQuoteItem();
//                    quoteItemGuid = epcQuoteItem.getId();
                    epcGetSpec.setCompiledSpecification(epcQuoteItem.getCompiledSpecification());
    
                    // delete quote 
                    epcQuote = new EpcQuote();
                    epcQuote.setId(quoteGuid);
                    epcDeleteQuoteResult = epcQuoteHandler.deleteQuote(epcQuote);
    
                    epcGetSpec.setResult("SUCCESS");
                } else {
                    epcGetSpec.setResult("FAIL");
                    epcGetSpec.setErrMsg(epcAddProductToQuoteResult.getErrMsg());
                }
            } else {
                epcGetSpec.setResult("FAIL");
                epcGetSpec.setErrMsg("cannot create new quote. " + epcCreateQuoteResult.getErrMsg());
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
        }
    }


	public String updateReservedItemsReservedType(UpdateReservedItemsRequest updateRequest) {
    	Connection conn = null;
    	PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String itemId = "";
        String reserveType = "";
        String logStr = "[updateReservedItemsReservedType][orderId:" + updateRequest.getOrderId() + "] ";
        String tmpLogStr = "";
        String rtnStr = "";
        
        try {
			conn = epcDataSource.getConnection();
			conn.setAutoCommit(false);
			sql="UPDATE EPC_ORDER_ITEM SET RESERVE_TYPE=? WHERE ITEM_CAT = 'DEVICE' AND IS_RESERVE = 'Y' AND ORDER_ID=? AND ITEM_ID=?";
			pstmt = conn.prepareStatement(sql);
        	
        	
        	for(ReservedItem item : updateRequest.getItems()) {
                itemId = epcSecurityHelper.encodeForSQL(StringHelper.trim(item.getItemId()));
                reserveType = epcSecurityHelper.encodeForSQL(StringHelper.trim(item.getReserveType()));
                
                tmpLogStr = "Item "+item.getItemId()+" set reserve type to '"+item.getReserveType()+"'";
logger.info("", logStr, tmpLogStr);
                pstmt.setString(1, item.getReserveType());
                pstmt.setInt(2, Integer.parseInt(updateRequest.getOrderId()));
                pstmt.setString(3, item.getItemId());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            
            conn.commit();
            
            rtnStr = EpcApiStatusReturn.RETURN_SUCCESS;
        } catch(Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            e.printStackTrace();

            rtnStr = EpcApiStatusReturn.RETURN_FAIL;
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return rtnStr;
    }
    

    public void getAndUpdateProductDescFromVakaCms(int orderId, ArrayList<EpcOrderItem> itemList) {
        try {
            for(EpcOrderItem item : itemList) {
                CompletableFuture.completedFuture(item).thenApplyAsync(s -> getAndUpdateProductDescFromVakaCms(orderId, s));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getAndUpdateProductDescFromVakaCms(int orderId, ArrayList<EpcOrderItem> itemList, String caseId, String offerGuid) {
        try {
            for(EpcOrderItem item : itemList) {
                CompletableFuture.completedFuture(item).thenApplyAsync(s -> getAndUpdateProductDescFromVakaCms(orderId, s));
            }

            CompletableFuture.completedFuture(offerGuid).thenApplyAsync(s -> getAndUpdateOfferDescFromVakaCms(orderId, caseId, s));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean getAndUpdateProductDescFromVakaCms(int orderId, EpcOrderItem epcOrderItem) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        VakaCmsProduct vakaCmsProduct = null;
        String productCode = epcOrderItem.getProductCode();
        String itemId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderItem.getItemId()));
        String descEng = "";
        String descChi = "";
        String logStr = "[getAndUpdateProductDescFromVakaCms][orderId:" + orderId + "][itemId:" + itemId + "] ";
        String tmpLogStr = "";

        try {
            if(StringUtils.isNotBlank(productCode)) {
                vakaCmsProduct = vakaCmsHandler.getProductDesc(productCode);
                if(vakaCmsProduct != null) {
                    descEng = epcSecurityHelper.encodeForSQL(StringHelper.trim(vakaCmsProduct.getProductNameEng()));
                    descChi = epcSecurityHelper.encodeForSQL(StringHelper.trim(vakaCmsProduct.getProductNameChi()));

                    if(StringUtils.isNotBlank(descEng) && StringUtils.isNotBlank(descChi)) {
                        conn = epcDataSource.getConnection();
                        conn.setAutoCommit(false);

                        sql = "update epc_order_item " +
                            "   set cpq_item_desc = ?, " +
                            "       cpq_item_desc_chi = ? " +
                            " where order_id = ? " +
                            "   and item_id = ? " +
                            "   and item_code = ? ";
                        pstmt = conn.prepareStatement(sql);
                        pstmt.setString(1, descEng); // cpq_item_desc
                        pstmt.setString(2, descChi); // cpq_item_desc_chi
                        pstmt.setInt(3, orderId); // order_id
                        pstmt.setString(4, itemId); // item_id
                        pstmt.setString(5, productCode); // item_code
                        pstmt.executeUpdate();

                        conn.commit();

                        tmpLogStr = " update to descEng:" + descEng + ". descChi:" + descChi;
logger.info("{}{}", logStr, tmpLogStr);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return true;
    }


    public boolean getAndUpdateOfferDescFromVakaCms(int orderId, String caseId, String offerGuid) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        VakaCmsProduct vakaCmsProduct = null;
        String iCaseId = epcSecurityHelper.encodeForSQL(StringHelper.trim(caseId));
        String iOfferGuid = epcSecurityHelper.encodeForSQL(StringHelper.trim(offerGuid));
        String descEng = "";
        String descChi = "";
        String logStr = "[getAndUpdateOfferDescFromVakaCms][orderId:" + orderId + "][caseId:" + iCaseId + "][offerGuid:" + iOfferGuid + "] ";
        String tmpLogStr = "";

        try {
            vakaCmsProduct = vakaCmsHandler.getOfferDesc(iOfferGuid);
            if(vakaCmsProduct != null) {
                descEng = epcSecurityHelper.encodeForSQL(StringHelper.trim(vakaCmsProduct.getProductNameEng()));
                descChi = epcSecurityHelper.encodeForSQL(StringHelper.trim(vakaCmsProduct.getProductNameChi()));

                if(StringUtils.isNotBlank(descEng) && StringUtils.isNotBlank(descChi)) {
                    conn = epcDataSource.getConnection();
                    conn.setAutoCommit(false);

                    sql = "update epc_order_item " +
                          "   set cpq_item_desc = ?, cpq_item_desc_chi = ? " +
                          " where order_id = ? " +
                          "   and case_id = ? " +
                          "   and parent_item_id is null ";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, descEng); // cpq_item_desc
                    pstmt.setString(2, descChi); // cpq_item_desc_chi
                    pstmt.setInt(3, orderId); // order_id
                    pstmt.setString(4, iCaseId); // case_id
                    pstmt.executeUpdate();

                    conn.commit();

                    tmpLogStr = " update to descEng:" + descEng + ". descChi:" + descChi;
logger.info("{}{}", logStr, tmpLogStr);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return true;
    }


    public String getCmsItemMapping(int orderId, String caseId) {
        Connection conn = null;
        String cmsItemMapping = "";
        
        try {
            conn = epcDataSource.getConnection();
            cmsItemMapping = getCmsItemMapping(conn, orderId, caseId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return cmsItemMapping;
    }


    public String getCmsItemMapping(Connection conn, int orderId, String caseId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String cmsItemMapping = "";
        
        try {
            sql = "select cms_item_mapping " +
                  "  from epc_order_case " +
                  " where order_id = ? " +
                  "   and case_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, caseId); // case_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                cmsItemMapping = StringHelper.trim(rset.getString("cms_item_mapping"));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return cmsItemMapping;
    }


    public HashMap<String, String> getSpecialProductMap() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        HashMap<String, String> specialProductMap = new HashMap<>();
        
        try {
            conn = epcDataSource.getConnection();

            sql = "select key_str1, value_str1 " +
                  "  from epc_control_tbl " +
                  " where rec_type = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "SPECIAL_PRODUCT"); // rec_type
            rset = pstmt.executeQuery();
            while(rset.next()) {
                specialProductMap.put(StringHelper.trim(rset.getString("key_str1")), StringHelper.trim(rset.getString("value_str1")));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return specialProductMap;
    }


    public void proceedOrder(EpcProceedOrder epcProceedOrder) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        int orderId = epcProceedOrder.getOrderId();
        String proceedBy = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcProceedOrder.getProceedBy()));
        String action = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcProceedOrder.getAction())); // LOCK / UNLOCK / OVERRIDE_LOCK
        String previousProceedBy = "";
        String previousProceedDate = "";


        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            sql = "select proceed_by, to_char(proceed_date,'yyyymmddhh24miss') as p_date " +
                  "  from epc_order " +
                  " where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                previousProceedBy = StringHelper.trim(rset.getString("proceed_by"));
                previousProceedDate = StringHelper.trim(rset.getString("p_date"));
            } rset.close();
            pstmt.close();

            epcProceedOrder.setPreviousProceedBy(previousProceedBy);
            epcProceedOrder.setPreviousProceedDate(previousProceedDate);
            epcProceedOrder.setResult(EpcApiStatusReturn.RETURN_FAIL); // init

            if("LOCK".equals(action)) {
                if("".equals(previousProceedBy)) {
                    // lock the order
                    sql = "update epc_order " + 
                        "   set proceed_by = ?, " +
                        "       proceed_date = sysdate " +
                        " where order_id = ? ";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, proceedBy); // proceed_by
                    pstmt.setInt(2, orderId); // order_id
                    pstmt.executeUpdate();
                    pstmt.close();

                    epcProceedOrder.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
                } else if(proceedBy.equals(previousProceedBy)) {
                    // same people
                    //  no action

                    epcProceedOrder.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
                } else {
                    epcProceedOrder.setResult(EpcApiStatusReturn.RETURN_FAIL);
                }
            } else if("UNLOCK".equals(action)) {
                // clear the lock
                sql = "update epc_order " + 
                      "   set proceed_by = ?, " +
                      "       proceed_date = null " +
                      " where order_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, ""); // proceed_by
                pstmt.setInt(2, orderId); // order_id
                pstmt.executeUpdate();
                pstmt.close();

                epcProceedOrder.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
            } else if("OVERRIDE_LOCK".equals(action)) {
                // override the lock
                sql = "update epc_order " + 
                      "   set proceed_by = ?, " +
                      "       proceed_date = sysdate " +
                      " where order_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, proceedBy); // proceed_by
                pstmt.setInt(2, orderId); // order_id
                pstmt.executeUpdate();
                pstmt.close();

                epcProceedOrder.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
            }

            if(EpcApiStatusReturn.RETURN_SUCCESS.equals(epcProceedOrder.getResult())) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            epcProceedOrder.setResult(EpcApiStatusReturn.RETURN_FAIL);
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }

	// added by Danny Chan on 2023-3-25: start
	public ArrayList<String> getListOfItemIdForType(HashMap metaDataLookupMap, String type) {
		ArrayList<String> list = new ArrayList<String>();
		
		String items[] = (String [])metaDataLookupMap.keySet().toArray(new String[0]);
			
		for (int j=0; j<items.length; j++) {
			if ( metaDataLookupMap.get( items[j]) instanceof HashMap ) {
					HashMap item_map = (HashMap)metaDataLookupMap.get( items[j] );

				if ( item_map.get( "typePath" )!=null ) {
					String typePath = (String)item_map.get("typePath");

					if (typePath.startsWith(type)) {
						list.add( items[j] );
					}
				}
			}
		}		
		
		return list;
	}

	public void createTnCItemRecords(int smcOrderId, String smcCaseId, String parentId, PreparedStatement pstmtItem, EpcQuoteProductCandidate productCandidateObj, ArrayList<String> tnCItemIdList ) throws SQLException {
		String id = productCandidateObj.getId();
		
		if (tnCItemIdList.contains(id)) {
			pstmtItem.setInt(1, smcOrderId);
			pstmtItem.setString(2, smcCaseId);
			pstmtItem.setString(3, id);
			pstmtItem.setString(4, parentId);
			pstmtItem.setString(5, null);
			pstmtItem.setString(6, null);
			pstmtItem.setString(7, null);
			pstmtItem.setString(8, null);
			pstmtItem.setString(9, null);
			
			ArrayList<EpcConfiguredValue> configuredValueList = productCandidateObj.getConfiguredValue();
			 
			for (int i=0; i<configuredValueList.size(); i++) {
				EpcConfiguredValue  val = configuredValueList.get(i);
				switch ( val.getName() ) {
					case "TnCNumber":
						pstmtItem.setString(5,val.getValue());
						break;
					case "TnCShortDescriptionZhHK": 
						pstmtItem.setString(6,val.getValue());
						break;
					case "TnCZhHK":
						pstmtItem.setString(7,val.getValue());
						break;
					case "TnCShortDescriptionEN": 
						pstmtItem.setString(8,val.getValue());
						break;
					case "TnCEN":
						pstmtItem.setString(9,val.getValue());
				}
			}
			
			pstmtItem.addBatch();
		}
		
		for (int i=0; i<productCandidateObj.getChildEntity().size(); i++) {
			createTnCItemRecords(smcOrderId, smcCaseId, id, pstmtItem, productCandidateObj.getChildEntity().get(i), tnCItemIdList);
		}
	}	
	// added by Danny Chan on 2023-3-25: end
	
	// added by Danny Chan on 2023-3-30: start
	public void createContratDetailsRecords(int smcOrderId, String smcCaseId, String parentId, PreparedStatement pstmtItem, PreparedStatement pstmtItem2, 
		EpcQuoteProductCandidate productCandidateObj, ArrayList<String> itemIdList ) throws SQLException {
		
		String id = productCandidateObj.getId();
		
		if (itemIdList.contains(id)) {
			pstmtItem2.setInt(1, smcOrderId);
			pstmtItem2.setString(2, smcCaseId);
			pstmtItem2.setString(3, id);
			
			pstmtItem.setInt(1, smcOrderId);
			pstmtItem.setString(2, smcCaseId);
			pstmtItem.setString(3, id);
			pstmtItem.setString(4, parentId);
			pstmtItem.setString(5, null);	// action_on_contract
			pstmtItem.setString(6, null);	// can_be_appended
			pstmtItem.setString(7, null);	// can_be_voided
			pstmtItem.setString(8, null);	// contract_expiration_date_align_extend_options
			pstmtItem.setString(9, null);	// contract_renewal_option
			pstmtItem.setString(10, null);	// contract_type
			pstmtItem.setNull(11, Types.INTEGER);	// cooling_period
			pstmtItem.setNull(12, Types.INTEGER);	// duration
			pstmtItem.setNull(13, Types.DATE);	// effective_start_date
			pstmtItem.setNull(14, Types.DATE);	// effective_end_date
			pstmtItem.setString(15, null);	// effective_start_date_type
			pstmtItem.setNull(16, Types.NUMERIC);	// fixed_penalty_amount
			pstmtItem.setString(17, null);	// fixed_penalty_amount_cpqchargelink
			pstmtItem.setNull(18, Types.INTEGER);	// grace_period
			pstmtItem.setString(19, null);	// is_contract_valid
			pstmtItem.setNull(20, Types.NUMERIC);	// max_penalty_amount
			pstmtItem.setNull(21, Types.NUMERIC);	// min_penalty_amount
			pstmtItem.setNull(22, Types.NUMERIC);	// min_req_price_value
			pstmtItem.setNull(23, Types.NUMERIC);	// min_req_total_vas_value
			pstmtItem.setNull(24, Types.INTEGER);	// min_service_period
			pstmtItem.setNull(25, Types.NUMERIC);	// penalty_amount
			pstmtItem.setString(26, null);	// penalty_type
			pstmtItem.setString(27, null);	// reason
			pstmtItem.setString(28, null);	// reference_id
			pstmtItem.setString(29, null);	// required_payment_method
			pstmtItem.setString(30, null);	// status
			pstmtItem.setString(31, null);	// target_align_extend_options
			pstmtItem.setNull(32, Types.NUMERIC);	// variable_penalty_amount
			
			ArrayList<EpcConfiguredValue> configuredValueList = productCandidateObj.getConfiguredValue();
						
			for (int i=0; i<configuredValueList.size(); i++) {
				int i_v = -1;
				double d_v = -1;
				java.sql.Date date_v = null;
				
				EpcConfiguredValue  val = configuredValueList.get(i);
				switch ( val.getName() ) {
					case "cmsCoolingPeriod":
						try {i_v = Integer.parseInt(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setString(11,String.valueOf(i_v));
						break;
					case "cmsEffectiveStartDate": 
						try {date_v = java.sql.Date.valueOf(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setDate(13,date_v);
						break;
					case "cmsEffectiveEndDate":
						try {date_v = java.sql.Date.valueOf(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setDate(14,date_v);
						break;
					case "cmsFixedPenaltyAmount": 
						try {d_v = Double.parseDouble(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setString(16,String.valueOf(d_v));
						break;
					case "cmsFixedPenaltyAmount_CPQChargeLink":
						pstmtItem.setString(17,val.getValue());
						break;
					case "cmsGracePeriod":
						try {i_v = Integer.parseInt(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setString(18,String.valueOf(i_v));
						break;
					case "cmsMaxPenaltyAmount":
						try {d_v = Double.parseDouble(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setString(20,String.valueOf(d_v));
						break;
					case "cmsMinPenaltyAmount":
						try {d_v = Double.parseDouble(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setString(21,String.valueOf(d_v));
						break;
					case "cmsMinReqPriceValue":
						try {d_v = Double.parseDouble(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setString(22,String.valueOf(d_v));
						break;
					case "cmsMinReqTotalVASValue":
						try {d_v = Double.parseDouble(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setString(23,String.valueOf(d_v));
						break;
					case "cmsMinServicePeriod":
						try {i_v = Integer.parseInt(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setString(24,String.valueOf(i_v));
						break;
					case "cmsPenaltyAmount":
						try {d_v = Double.parseDouble(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setString(25,String.valueOf(d_v));
						break;
					case "cmsReason":
						pstmtItem.setString(27,val.getValue());
						break;
					case "cmsReferenceID":
						pstmtItem.setString(28,val.getValue());
						break;
					case "cmsVariablePenaltyAmount":
						try {d_v = Double.parseDouble(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setString(32,String.valueOf(d_v));
				}
			}
			
			ArrayList<EpcCharacteristicUse> characteristicUseList = productCandidateObj.getCharacteristicUse();

			for (int i=0; i<characteristicUseList.size(); i++) {
				EpcCharacteristicUse  val = characteristicUseList.get(i);

				int index = -1;
				String v = null;			
				int i_v = -1;				
				
				switch ( val.getName() ) {
					case "cmsActionOnContract":
						index = 5;	
						v = val.getValue().get(0).toString();
						break;
					case "cmsCanBeAppended": 
						index = 6;
						v = val.getValue().get(0).toString();
						if (v.equals("Yes")) {
							v = "Y";
						} else {
							v = "N";
						}
						break;
					case "cmsCanBeVoided":
						index = 7;
						v = val.getValue().get(0).toString();
						if (v.equals("Yes")) {
							v = "Y";
						} else {
							v = "N";
						}
						break;
					case "cmsContractExpirationDateAlignExtendOptions": 						
						index = 8;
						v = val.getValue().get(0).toString();
						break;
					case "cmsContractRenewalOption":
						index = 9;
						v = val.getValue().get(0).toString();
						break;
					case "cmsContractType":
						index = 10;
						v = val.getValue().get(0).toString();
						break;
					case "cmsDuration":
						index = 12;
						try {i_v = Integer.parseInt(val.getValue().get(0).toString());}
						catch (Exception e) {}
						v = String.valueOf(i_v);
						break;
					case "cmsEffectiveStartDateType":
						index = 15;
						v = val.getValue().get(0).toString();
						break;
					case "cmsIsContractValid":
						index = 19;
						v = val.getValue().get(0).toString();
						break;
					case "cmsPenaltyType":
						index = 26;
						v = val.getValue().get(0).toString();
						break;
					case "cmsRequiredPaymentMethod":
						index = 29;
						v = val.getValue().get(0).toString();
						break;
					case "cmsStatus":
						index = 30;
						v = val.getValue().get(0).toString();
						break;
					case "cmsTargetAlignExtendOptions":
						v = val.getValue().get(0).toString();
						index = 31;
				}
				
				if (index > 0) {
					pstmtItem.setString(index, v);
				} else {
					if (val.getName().equals("cmsRequiredVASList") || val.getName().equals("cmsContractPlanCodeList")) {
						pstmtItem2.setString(4, val.getName());
					
						for (int j=0; j<val.getValue().size(); j++) {
							pstmtItem2.setString(5, val.getValue().get(j).toString());
							pstmtItem2.addBatch();
						}
					}
				}
			}
			
			pstmtItem.addBatch();
		}
		
		for (int i=0; i<productCandidateObj.getChildEntity().size(); i++) {
			createContratDetailsRecords(smcOrderId, smcCaseId, id, pstmtItem, pstmtItem2, productCandidateObj.getChildEntity().get(i), itemIdList );
		}		
	} 
	// added by Danny Chan on 2023-3-30: end
	
	// added by Danny Chan on 2023-4-21 (save BuyBack info): start
	public void createBuyBackRecords(int smcOrderId, String smcCaseId, String parentId, PreparedStatement pstmtItem, EpcQuoteProductCandidate productCandidateObj, 
		ArrayList<String> itemIdList ) throws SQLException {
		
		String id = productCandidateObj.getId();
		
		if (itemIdList.contains(id)) {
			pstmtItem.setInt(1, smcOrderId);
			pstmtItem.setString(2, smcCaseId);
			pstmtItem.setString(3, id);
			pstmtItem.setString(4, parentId);
			pstmtItem.setString(5, null);	// bank
			pstmtItem.setString(6, null);	// trade_in_product_code
			pstmtItem.setNull(7, Types.NUMERIC);	// price
			pstmtItem.setNull(8, Types.DATE);	// buyback_date_from
			pstmtItem.setNull(9, Types.DATE);	// buyback_date_to
			pstmtItem.setNull(10, Types.INTEGER);	// buyback_month
			pstmtItem.setNull(11, Types.INTEGER);	// buyback_grace_period
			
			ArrayList<EpcConfiguredValue> configuredValueList = productCandidateObj.getConfiguredValue();
						
			for (int i=0; i<configuredValueList.size(); i++) {
				int i_v = -1;
				double d_v = -1;
				java.sql.Date date_v = null;
				
				EpcConfiguredValue  val = configuredValueList.get(i);
				switch ( val.getName() ) {
					case "Trade_in_Product_Code":
						pstmtItem.setString(6,val.getValue());
						break;					
					case "Guaranteed_Buyback_Amount": 
						try {d_v = Double.parseDouble(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setString(7,String.valueOf(d_v));
						break;						
					case "Buyback_Start_Date": 
						try {date_v = java.sql.Date.valueOf(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setDate(8,date_v);
						break;
					case "Buyback_End_Date": 
						try {date_v = java.sql.Date.valueOf(val.getValue());}
						catch (Exception e) {}
						pstmtItem.setDate(9,date_v);
				}
			}	
			
			ArrayList<EpcCharacteristicUse> characteristicUseList = productCandidateObj.getCharacteristicUse();

			for (int i=0; i<characteristicUseList.size(); i++) {
				EpcCharacteristicUse  val = characteristicUseList.get(i);

				int index = -1;
				String v = null;			
				int i_v = -1;				
				
				switch ( val.getName() ) {
					case "Bank":
						index = 5;
						v = val.getValue().get(0).toString();
						break;
					case "Buyback_allowed_period":
						index = 10;
						try {i_v = Integer.parseInt(val.getValue().get(0).toString().toLowerCase().replaceAll(" ", "").replaceAll("months$", ""));}
						catch (Exception e) {}
						v = String.valueOf(i_v);
						break;
					case "Buyback_guarantee_period":
						index = 11;
						try {i_v = Integer.parseInt(val.getValue().get(0).toString().toLowerCase().replaceAll(" ", "").replaceAll("days$", ""));}
						catch (Exception e) {}
						v = String.valueOf(i_v);
				}
				
				if (index > 0) {
					pstmtItem.setString(index, v);
				} 
			}
			
			pstmtItem.addBatch();
		}
		
		for (int i=0; i<productCandidateObj.getChildEntity().size(); i++) {
			createBuyBackRecords(smcOrderId, smcCaseId, id, pstmtItem, productCandidateObj.getChildEntity().get(i), itemIdList );
		}				
	}	
	// added by Danny Chan on 2023-4-21 (save BuyBack info): end
	
	public boolean tmpUpdateOrderInfoForPreviewSA(EpcTmpUpdOrder epcTmpUpdOrder) {
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        boolean isUpdate = false;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
        	
    		sql = "update epc_order " +
    	          "   set place_order_user = ? , place_order_salesman = ?, place_order_location = ?, place_order_date = sysdate " +
    			  " where order_id = ? ";
    		pstmt = conn.prepareStatement(sql);
    		pstmt.setString(1, epcTmpUpdOrder.getUser());
    		pstmt.setString(2, epcTmpUpdOrder.getSalesman());
    		pstmt.setString(3, epcTmpUpdOrder.getLocation());
        	pstmt.setInt(4, epcTmpUpdOrder.getOrderId()); // order_id
        	pstmt.executeUpdate();
            pstmt.close();

            conn.commit();
            
            isUpdate = true;
        } catch (Exception e) {
        	e.printStackTrace();
        	
        	try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        	try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
        	try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
	
	public boolean removeTmpUpdateOrderInfoForPreviewSA(EpcTmpUpdOrder epcTmpUpdOrder) {
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        boolean isUpdate = false;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
        	
    		sql = "update epc_order " +
    	          "   set place_order_user = null , place_order_salesman = null, place_order_location = null, place_order_date = null " +
    			  " where order_id = ? ";
    		pstmt = conn.prepareStatement(sql);
        	pstmt.setInt(1, epcTmpUpdOrder.getOrderId()); // order_id
        	pstmt.executeUpdate();
            pstmt.close();

            conn.commit();
            
            isUpdate = true;
        } catch (Exception e) {
        	e.printStackTrace();
        	
        	try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        	try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
        	try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
}
