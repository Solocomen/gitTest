package epc.epcsalesapi.sales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcCreateInvoice;
import epc.epcsalesapi.sales.bean.EpcCreateInvoiceResult;
import epc.epcsalesapi.sales.bean.EpcCreateSerial;
import epc.epcsalesapi.sales.bean.EpcCreateSerialResult;
import epc.epcsalesapi.sales.bean.EpcFulfillLocation;
import epc.epcsalesapi.sales.bean.EpcFulfillOrder;
import epc.epcsalesapi.sales.bean.EpcFulfillResult;
import epc.epcsalesapi.sales.bean.EpcInvoice;
import epc.epcsalesapi.sales.bean.EpcInvoiceItem;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcLogStockStatus;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.EpcOrderItem;
import epc.epcsalesapi.sales.bean.EpcOrderItemDetail;
import epc.epcsalesapi.sales.bean.Fulfillment;
import epc.epcsalesapi.stock.EpcStockHandler;


@Service
public class EpcFulfillHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcFulfillHandler.class);

    private final EpcOrderAttrHandler epcOrderAttrHandler;
    private final EpcStockHandler epcStockHandler;
    private final EpcPaymentHandler epcPaymentHandler;
    private final EpcInvoiceHandler epcInvoiceHandler;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcSalesmanHandler epcSalesmanHandler;
    private final EpcOrderHandler epcOrderHandler;
    private final EpcOrderLogHandler epcOrderLogHandler;
    private final DataSource epcDataSource;
    private final DataSource fesDataSource;


    public EpcFulfillHandler(EpcOrderAttrHandler epcOrderAttrHandler, EpcStockHandler epcStockHandler,
            EpcPaymentHandler epcPaymentHandler, EpcInvoiceHandler epcInvoiceHandler,
            EpcSecurityHelper epcSecurityHelper, EpcSalesmanHandler epcSalesmanHandler, EpcOrderHandler epcOrderHandler,
            EpcOrderLogHandler epcOrderLogHandler, DataSource epcDataSource, DataSource fesDataSource) {
        this.epcOrderAttrHandler = epcOrderAttrHandler;
        this.epcStockHandler = epcStockHandler;
        this.epcPaymentHandler = epcPaymentHandler;
        this.epcInvoiceHandler = epcInvoiceHandler;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcSalesmanHandler = epcSalesmanHandler;
        this.epcOrderHandler = epcOrderHandler;
        this.epcOrderLogHandler = epcOrderLogHandler;
        this.epcDataSource = epcDataSource;
        this.fesDataSource = fesDataSource;
    }


    public EpcFulfillResult fulfill(EpcFulfillOrder epcFulfillOrder) {
        int orderId = epcFulfillOrder.getOrderId(); // smc order id
        EpcFulfillResult epcFulfillResult = new EpcFulfillResult();
        epcFulfillResult.setOrderId(orderId);
        boolean isValid = true;
        String fulfillUser = epcFulfillOrder.getFulfillUser();
        String fulfillSalesman = epcFulfillOrder.getFulfillSalesman();
        String fulfillLocation = epcFulfillOrder.getFulfillLocation();
        String fulfillChannel = epcFulfillOrder.getFulfillChannel();
        ArrayList<EpcOrderItem> itemList = epcFulfillOrder.getItems();
        EpcOrderItem epcOrderItem = null;
        EpcCreateSerial epcCreateSerial = null;
        EpcCreateSerialResult epcCreateSerialResult = null;
        EpcCreateInvoice epcCreateInvoice = null;
        ArrayList<EpcInvoice> invoiceList = null;
        EpcInvoice epcInvoice = null;
        ArrayList<EpcInvoiceItem> invoiceItems = null;
        EpcInvoiceItem epcInvoiceItem = null;
        EpcCreateInvoiceResult epcCreateInvoiceResult = null;
        EpcOrderInfo epcOrderInfo = epcOrderHandler.getOrderSlimInfo(orderId);
        TreeMap<String, EpcOrderItemDetail> itemDetailMap = epcOrderHandler.getItemDetails(orderId); // get order, item info
        TreeMap<String, String> attrMap = epcOrderAttrHandler.getOrderAttr(orderId); // all attr under this order
        EpcOrderItemDetail epcOrderItemDetail = null;
        TreeMap<String, String[]> invoiceHdrKeyMap = new TreeMap<String, String[]>(); // offer_id, offer_desc[]
        TreeMap<String, String> invoiceNoMap = new TreeMap<String, String>(); // item_id, invoice_no
        Iterator<String> invoiceHdrKeyIterator = null;
        String productCode = "";
        String custId = "";
        String hkidBr = "";
        boolean isValidProduct = false;
        boolean isUpdateOrderStatus = false;
        String logStr = "[fulfill()][orderId:" + orderId + "] ";
        String tmpLogStr = "";
        String errMsg = "";
        String salesmanLogRemarks = "";
        String tmpInvoiceNos = "";
        String tmpItemIds = "";
        TreeMap<String, String> orderStatusMap = epcOrderHandler.getOrderStatusDescMap();
        String orderStatus = "";
        String orderStatusDesc = "";
        String tmpItemId = "";
        String tmpProductCode = "";
        String tmpImeiSim = "";
        BigDecimal tmpNetAmt = null;
        BigDecimal tmpDisAmt = null;
        EpcFulfillLocation epcFulfillLocation = null;
        String offerDesc = "";
        boolean isDoaBefore = false;

        
        try {
            // basic checking
            for (EpcOrderItem item : itemList) {
                // whether input items are belonged to input order
                tmpItemId = StringHelper.trim(item.getItemId());
                tmpProductCode = StringHelper.trim(item.getProductCode());
                tmpImeiSim = StringHelper.trim(item.getImeiSim());

                epcOrderItemDetail = itemDetailMap.get(tmpItemId);
                if(epcOrderItemDetail == null) {
                    errMsg += "Fulfill failed, " + tmpProductCode + " is not belonged to this order. ";
                    isValid = false;

                    tmpLogStr = "item " + tmpItemId + "[" + tmpProductCode + "] is not belonged to order " + orderId;
logger.info("{}{}", logStr, tmpLogStr);
                } else {
                    // check input imei vs product code
                    if("AP".equals(epcOrderItemDetail.getWarehouse())) {
                        // sim, should be no product at the beginning
                        //  get product code from fes.stquem by input sim no.
                        // no need to check quantity_free, assume ERP api will do so
                        productCode = epcStockHandler.getProductCodeBySerialNo(tmpImeiSim);
                        item.setProductCode(productCode);
                        if("".equals(productCode)) {
                            errMsg += "Fulfill failed, product code is not found for IMEI/serial no. " + tmpImeiSim + ". ";
                            isValid = false;

                            tmpLogStr = "product code is not found for " + tmpItemId;
logger.info("{}{}", logStr, tmpLogStr);
                        }
                    } else {
                        // AA / AH
                        //  verify with fes.stquem
                        // no need to check quantity_free, assume ERP api will do so
                        if(!"".equals(tmpImeiSim)) {
                            isValidProduct = epcStockHandler.isValidProduct(tmpProductCode, tmpImeiSim);
                            if(!isValidProduct) {
                                // not matched
                                errMsg += "Fulfill failed, " + getItemDesc(orderId, tmpItemId) + "," + tmpProductCode + " is not matched with IMEI/serial no. " + tmpImeiSim + ". ";
                                isValid = false;

                                tmpLogStr = "item " + tmpItemId + "[" + tmpProductCode + "] is not matched with " + tmpImeiSim;
logger.info("{}{}", logStr, tmpLogStr);
                            }
                        }
                    }
                }
            }


            // check whether this offer need to fulfill by other location
            for (EpcOrderItem item : itemList) {
                tmpItemId = StringHelper.trim(item.getItemId());
                epcOrderItemDetail = itemDetailMap.get(tmpItemId);
                
                // remove duplicated
                String[] tmpDescArray = new String[2];
                tmpDescArray[0] = epcOrderItemDetail.getCpqOfferDesc();
                tmpDescArray[1] = epcOrderItemDetail.getCpqOfferDescChi();
                invoiceHdrKeyMap.put(epcOrderItemDetail.getCpqOfferGuid(), tmpDescArray); // offer_id, offer_desc[]
                
                custId = epcOrderItemDetail.getCustId(); // 1 order only belonged to 1 cust id

                isDoaBefore = isDoaBefore(orderId, tmpItemId);
            }

            invoiceHdrKeyIterator = invoiceHdrKeyMap.keySet().iterator();
            while(invoiceHdrKeyIterator.hasNext()) {
                String tmpOfferGuid = invoiceHdrKeyIterator.next();
                String[] tmpDescArray = invoiceHdrKeyMap.get(tmpOfferGuid);
                offerDesc = tmpDescArray[0];

                epcFulfillLocation = fulfillByOtherLocation(orderId, offerDesc, fulfillLocation);
                tmpLogStr = "basic checking" + 
                            ",offerDesc:" + offerDesc +
                            ",setting found:" + epcFulfillLocation.isFound() +
                            ",isDoaBefore:" + isDoaBefore +
                            ",invoiceLocation:" + epcFulfillLocation.getInvoiceLocation() +
                            ",doaLocation:" + epcFulfillLocation.getDoaLocation();
logger.info("{}{}", logStr, tmpLogStr);

                if(epcFulfillLocation.isFound()) {
                    if(isDoaBefore) {
                        fulfillLocation = epcFulfillLocation.getDoaLocation();
                    } else {
                        fulfillLocation = epcFulfillLocation.getInvoiceLocation();
                    }
                } else {
                    // if no setting is found, use login location, NO need to alter !!!
                }

                tmpLogStr = "basic checking" + 
                            ",offerDesc:" + offerDesc +
                            ",isDoaBefore:" + isDoaBefore +
                            ",final fulfill location:" + fulfillLocation;
logger.info("{}{}", logStr, tmpLogStr);
            }
            // end of check whether this offer need to fulfill by other location

            // end of basic checking


            if(isValid) {
                // loop thru input product

                // create inv (inv hdr) per cust_id + offer_id
                epcCreateInvoice = new EpcCreateInvoice();
                invoiceList = new ArrayList<EpcInvoice>();
                epcCreateInvoice.setInvoiceList(invoiceList);
                
//                for (EpcOrderItem item : itemList) {
//                    tmpItemId = StringHelper.trim(item.getItemId());
//                    epcOrderItemDetail = itemDetailMap.get(tmpItemId);
//                    
//                    // remove duplicated
//                    String[] tmpDescArray = new String[2];
//                    tmpDescArray[0] = epcOrderItemDetail.getCpqOfferDesc();
//                    tmpDescArray[1] = epcOrderItemDetail.getCpqOfferDescChi();
//                    invoiceHdrKeyMap.put(epcOrderItemDetail.getCpqOfferGuid(), tmpDescArray); // offer_id, offer_desc[]
//                    
//                    custId = epcOrderItemDetail.getCustId(); // 1 order only belonged to 1 cust id
//                }
                
                // get hkidbr from crm
                // ...
                if("".equals(hkidBr)) {
                    hkidBr = "00000000";
                }
                
                
                invoiceHdrKeyIterator = invoiceHdrKeyMap.keySet().iterator();
                while(invoiceHdrKeyIterator.hasNext()) {
                    String tmpOfferGuid = invoiceHdrKeyIterator.next();
                    String[] tmpDescArray = invoiceHdrKeyMap.get(tmpOfferGuid);
                    

                    // create pos inv obj
                    epcInvoice = new EpcInvoice();
                    epcInvoice.setOrderId(orderId + "");
                    epcInvoice.setOrderLang(epcOrderInfo.getOrderLang());
                    epcInvoice.setCustId(custId);
                    epcInvoice.setHkidBr(hkidBr);
                    epcInvoice.setCustNum("");
                    epcInvoice.setSubrNum("");
                    epcInvoice.setCreateUser(fulfillUser);
                    epcInvoice.setSalesmanCode(fulfillSalesman);
                    epcInvoice.setLocation(fulfillLocation);
                    epcInvoice.setOfferId(tmpOfferGuid);
                    epcInvoice.setOfferDesc(tmpDescArray[0]);
                    epcInvoice.setOfferDescChi(tmpDescArray[1]);
                    invoiceList.add(epcInvoice);
                    
                    invoiceItems = new ArrayList<EpcInvoiceItem>();
                    epcInvoice.setItems(invoiceItems);
                    
                    for (EpcOrderItem item : itemList) {
                        tmpItemId = StringHelper.trim(item.getItemId());
                        tmpProductCode = StringHelper.trim(item.getProductCode());
                        tmpImeiSim = StringHelper.trim(item.getImeiSim());
                        epcOrderItemDetail = (EpcOrderItemDetail)itemDetailMap.get(tmpItemId);

                        tmpNetAmt = epcPaymentHandler.getItemTotalCharge(orderId, tmpItemId);
                        tmpDisAmt = epcPaymentHandler.getItemDisCharge(orderId, tmpItemId);

                        // set back to itemList
                        item.setNetAmount(tmpNetAmt);
                        item.setDisAmount(tmpDisAmt);
                        // end of set back to itemList

                        // fill back to inv hdr level
                        //  if more than 1 item are included in the same inv, assume they are under same case_id (epc_order_case), i.e. screen replace & applecare
    //	                    epcInvoice.setCustId(epcOrderItemDetail.getCustId());
                        epcInvoice.setCustNum(epcOrderItemDetail.getCustNum());
                        epcInvoice.setSubrNum(epcOrderItemDetail.getSubrNum());
    //	                    epcInvoice.setOfferId(epcOrderItemDetail.getCpqOfferGuid());
    //	                    epcInvoice.setOfferDesc(epcOrderItemDetail.getCpqOfferDesc());
    //	                    epcInvoice.setOfferDescChi(epcOrderItemDetail.getCpqOfferDescChi());
                        // end of fill back to inv hdr level

                        epcInvoiceItem = new EpcInvoiceItem();
                        epcInvoiceItem.setItemId(tmpItemId);
                        epcInvoiceItem.setItemCode(tmpProductCode);
                        epcInvoiceItem.setWarehouse(epcOrderItemDetail.getWarehouse());
                        epcInvoiceItem.setReserveId(epcOrderItemDetail.getReserveId());
                        epcInvoiceItem.setItemValue(tmpImeiSim);
                        epcInvoiceItem.setItemDesc(epcOrderItemDetail.getCpqItemDesc());
                        epcInvoiceItem.setItemDescChi(epcOrderItemDetail.getCpqItemDescChi());
                        epcInvoiceItem.setItemCat(epcOrderItemDetail.getItemCat());
                        if(EpcItemCategory.APPLECARE.equals(epcOrderItemDetail.getItemCat())) {
                            epcInvoiceItem.setParentItemId(epcOrderItemDetail.getParentItemId()); // set parent item id
                            
                            epcInvoiceItem.setAppleCareEmail(StringHelper.trim(attrMap.get(tmpItemId + "@APPLECARE_APPLEID")));
                            epcInvoiceItem.setAppleCareFirstName(StringHelper.trim(attrMap.get(tmpItemId + "@APPLECARE_FIRST_NAME")));
                            epcInvoiceItem.setAppleCareLastName(StringHelper.trim(attrMap.get(tmpItemId + "@APPLECARE_LAST_NAME")));
                        } else if(EpcItemCategory.SCREEN_REPLACE.equals(epcOrderItemDetail.getItemCat())) {
                            epcInvoiceItem.setParentItemId(epcOrderItemDetail.getParentItemId()); // set parent item id
                        }
                        epcInvoiceItem.setNetAmt(tmpNetAmt);
                        epcInvoiceItem.setDisAmt(tmpDisAmt);

                        
                        invoiceItems.add(epcInvoiceItem);
                    }
                    // end of create pos inv obj
                }
                
                
                // invoke createInvoice
                epcCreateInvoiceResult = epcInvoiceHandler.createInvoice(epcCreateInvoice);

                tmpLogStr = "epcCreateInvoiceResult result:" + epcSecurityHelper.encodeForSQL(epcCreateInvoiceResult.getResult()) +
                            ",errorCode:" + epcSecurityHelper.encodeForSQL(epcCreateInvoiceResult.getErrorCode()) +
                            ",errorMessage:" + epcSecurityHelper.encodeForSQL(epcCreateInvoiceResult.getErrorMessage());
logger.info("{}{}", logStr, tmpLogStr);
                if("SUCCESS".equals(epcCreateInvoiceResult.getResult())) {
                    invoiceList = epcCreateInvoiceResult.getInvoiceList();
                    for(int x = 0; x < invoiceList.size(); x++) {
                        epcInvoice = invoiceList.get(x);
                        invoiceItems = epcInvoice.getItems();
                        for(int xx = 0; xx < invoiceItems.size(); xx++) {
                            epcInvoiceItem = invoiceItems.get(xx);
                            invoiceNoMap.put(epcInvoiceItem.getItemId(), epcInvoiceItem.getInvoiceNo());

                            tmpLogStr = epcSecurityHelper.encodeForSQL("  " + epcInvoiceItem.getItemId() + " -> " + epcInvoiceItem.getInvoiceNo());
logger.info("{}{}", logStr, tmpLogStr);
                            
                            if("".equals(tmpInvoiceNos)) {
                                tmpInvoiceNos = epcInvoiceItem.getInvoiceNo();
                            } else {
                                tmpInvoiceNos += "," + epcInvoiceItem.getInvoiceNo();
                            }

                            if("".equals(tmpItemIds)) {
                                tmpItemIds = epcInvoiceItem.getItemId();
                            } else {
                                tmpItemIds += "," + epcInvoiceItem.getItemId();
                            }
                        }
                    }
                    
                    // update invoice no to object list
                    for(int x = 0; x < itemList.size(); x++) {
                        epcOrderItem = itemList.get(x);
                        epcOrderItem.setInvoiceNo(StringHelper.trim((String)invoiceNoMap.get(epcOrderItem.getItemId())));
                    }
                    // end of update invoice no to object list
                    
                    
                    // create salesman log
                    salesmanLogRemarks = "itemId:" + tmpItemIds + "@" + "invNo:" + tmpInvoiceNos;
                    epcSalesmanHandler.createSalesmanLog(orderId, "", fulfillUser, fulfillSalesman, fulfillLocation, fulfillChannel, epcSalesmanHandler.actionFulfillItem, salesmanLogRemarks);
                    // end of create salesman log
                } else {
                    throw new Exception(epcCreateInvoiceResult.getErrorMessage());
                }
                // end of invoke createInvoice
                
                
                // invoke saveItemSerialNo, update imei / sim no / invoice / stock status / stock status desc to epc table
                //  include invoice log (epc_order_invoice_log)
                epcCreateSerial = new EpcCreateSerial();
                epcCreateSerial.setOrderId(orderId);
                epcCreateSerial.setItems(itemList);
                epcCreateSerial.setLocation(fulfillLocation);
                epcCreateSerial.setUsername(fulfillUser);
                epcCreateSerial.setSalesman(fulfillSalesman);
                epcCreateSerialResult = saveItemSerialNo(epcCreateSerial);

                tmpLogStr = "epcCreateSerialResult saveStatus:" + epcSecurityHelper.encodeForSQL(epcCreateSerialResult.getSaveStatus()) + 
                            ",errorCode:" + epcSecurityHelper.encodeForSQL(epcCreateSerialResult.getErrorCode()) +
                            ",errorMessage:" + epcSecurityHelper.encodeForSQL(epcCreateSerialResult.getErrorMessage());
logger.info("{}{}", logStr, tmpLogStr);
                if(!"SUCCESS".equals(epcCreateSerialResult.getSaveStatus())) {
                    throw new Exception(epcSecurityHelper.encode(epcCreateSerialResult.getErrorMessage()));
                }
                // end of invoke saveItemSerialNo, update imei / sim no / invoice to epc table

                
                // create a "change sim" order if item is a real sim
                //  ...
                // end of create a "change sim" order if item is a real sim

                
                // update order status
                isUpdateOrderStatus = epcOrderHandler.updateOrderStatusToComplete(orderId, fulfillUser, fulfillSalesman, fulfillChannel, fulfillLocation);
                tmpLogStr = "isUpdateOrderStatus:" + isUpdateOrderStatus;
logger.info("{}{}", logStr, tmpLogStr);
                // end of update order status


                // get order status & desc
                epcOrderInfo = epcOrderHandler.getOrderSlimInfo(orderId); // retrieve again
    //				orderStatus = getOrderStatus(orderId);
                orderStatus = epcOrderInfo.getOrderStatus();
                orderStatusDesc = StringHelper.trim(orderStatusMap.get(orderStatus));

                tmpLogStr = "orderStatus:" + epcSecurityHelper.encodeForSQL(orderStatus) + ",orderStatusDesc:" + epcSecurityHelper.encodeForSQL(orderStatusDesc);
logger.info("{}{}", logStr, tmpLogStr);

                epcFulfillResult.setOrderStatus(orderStatus);
                epcFulfillResult.setOrderStatusDesc(orderStatusDesc);
                // end of get order status & desc
                
                epcFulfillResult.setResult("SUCCESS");
            } else {
                // error
                epcFulfillResult.setResult("FAIL");
                epcFulfillResult.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcFulfillResult.setResult("FAIL");
            epcFulfillResult.setErrMsg(e.getMessage());
        } finally {
        }
        return epcFulfillResult;
    }


    public EpcCreateSerialResult saveItemSerialNo(EpcCreateSerial epcCreateSerial) {
        EpcCreateSerialResult epcCreateSerialResult = new EpcCreateSerialResult();
        Connection conn = null;
        String itemId = "";
        String imeiSim = "";
        String invoiceNo = "";
        String productCode = "";
        int smcOrderId = epcCreateSerial.getOrderId();
        ArrayList<EpcOrderItem> items = epcCreateSerial.getItems();
        String location = StringHelper.trim(epcCreateSerial.getLocation());
        EpcOrderItem epcOrderItem = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtUpdate = null;
        PreparedStatement pstmtInvoiceLog = null;
        ResultSet rset = null;
        String sql = "";
    //        EpcQuote epcQuote = null;
        boolean isValid = true;
        String errMsg = "";
        EpcLogStockStatus epcLogStockStatus = null;
        String newStockStatus = "F";
        String oldStockStatus = "";
        
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            // basic checking
            // ...
            // end of basic checking
            
            if(isValid) {
                sql = "select stock_status " +
                        "  from epc_order_item " +
                        " where order_id = ? " +
                        "   and item_id = ? ";
                pstmt = conn.prepareStatement(sql);

                sql = "update epc_order_item " +
                        "   set cpq_item_value = ?, invoice_no = ?, invoice_date = sysdate, " +
                        "       item_code = case when item_code is null then ? else item_code end, " +
                        "       stock_status = ?, stock_status_desc = ? " +
                        " where order_id = ? " +
                        "   and item_id = ? ";
                pstmtUpdate = conn.prepareStatement(sql);

                sql = "insert into epc_order_invoice_log ( " +
                        "  rec_id, order_id, item_id, invoice_no, create_date, " +
                        "  location, imei_sim, net_amt, dis_amt, fulfill_user, " +
                        "  fulfill_salesman, product_code " +
                        ") values ( " +
                        "  epc_order_id_seq.nextval,?,?,?,sysdate, " +
                        "  ?,?,?,?,?, " +
                        "  ?,? " +
                        ") ";
                pstmtInvoiceLog = conn.prepareStatement(sql);
                
                for(int i = 0; i < items.size(); i++) {
                    epcOrderItem = items.get(i);
                    
                    itemId = epcOrderItem.getItemId();
                    imeiSim = epcOrderItem.getImeiSim();
                    invoiceNo = epcOrderItem.getInvoiceNo();
                    productCode = epcOrderItem.getProductCode();

                    // get old stock status
                    oldStockStatus = ""; // reset

                    pstmt.setInt(1, smcOrderId); // order_id
                    pstmt.setString(2, itemId); // item_id
                    rset = pstmt.executeQuery();
                    if(rset.next()) {
                        oldStockStatus = StringHelper.trim(rset.getString("stock_status"));
                    } rset.close();
                    // end of get old stock status
                    
                    pstmtUpdate.setString(1, imeiSim); // cpq_item_value
                    pstmtUpdate.setString(2, invoiceNo); // invoice_no
                    pstmtUpdate.setString(3, productCode); // item_code
                    pstmtUpdate.setString(4, newStockStatus); // stock_status - F
                    pstmtUpdate.setString(5, "Fulfilled"); // stock_status_desc - Fulfilled
                    pstmtUpdate.setInt(6, smcOrderId); // order_id
                    pstmtUpdate.setString(7, itemId); // item_id
                    pstmtUpdate.executeUpdate();

                    pstmtInvoiceLog.setInt(1, smcOrderId); // order_id
                    pstmtInvoiceLog.setString(2, itemId); // item_id
                    pstmtInvoiceLog.setString(3, invoiceNo); // invoice_no
                    pstmtInvoiceLog.setString(4, location); // location
                    pstmtInvoiceLog.setString(5, imeiSim); // imei_sim
                    if(epcOrderItem.getNetAmount() != null) {
                        pstmtInvoiceLog.setBigDecimal(6, epcOrderItem.getNetAmount()); // net_amt
                    } else {
                        pstmtInvoiceLog.setInt(6, 0); // net_amt
                    }
                    if(epcOrderItem.getDisAmount() != null) {
                        pstmtInvoiceLog.setBigDecimal(7, epcOrderItem.getDisAmount()); // dis_amt
                    } else {
                        pstmtInvoiceLog.setInt(7, 0); // dis_amt
                    }
                    pstmtInvoiceLog.setString(8, epcCreateSerial.getUsername()); // fulfill_user
                    pstmtInvoiceLog.setString(9, epcCreateSerial.getSalesman()); // fulfill_salesman
                    pstmtInvoiceLog.setString(10, productCode); // product_code
                    pstmtInvoiceLog.executeUpdate();

                    // create log
                    epcLogStockStatus = new EpcLogStockStatus();
                    epcLogStockStatus.setOrderId(smcOrderId);
                    epcLogStockStatus.setItemId(itemId);
                    epcLogStockStatus.setOldStockStatus(oldStockStatus);
                    epcLogStockStatus.setNewStockStatus(newStockStatus);
                    epcOrderLogHandler.logStockStatus(conn, epcLogStockStatus);
                    // end of create log
                }

                
                conn.commit();
                
                epcCreateSerialResult.setSaveStatus("SUCCESS");
                epcCreateSerialResult.setOrderId(smcOrderId);
            } else {
                // error
                epcCreateSerialResult.setSaveStatus("FAIL");
                epcCreateSerialResult.setErrorCode("1001");
                epcCreateSerialResult.setErrorMessage(errMsg);
            }
        } catch (Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            e.printStackTrace();
            
            epcCreateSerialResult.setSaveStatus("FAIL");
            epcCreateSerialResult.setErrorCode("1002");
            epcCreateSerialResult.setErrorMessage(e.getMessage());
        } finally {
            try { if(pstmtInvoiceLog != null) { pstmtInvoiceLog.close(); } } catch (Exception ee) {}
            try { if(pstmtUpdate != null) { pstmtUpdate.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcCreateSerialResult;
    }


    public String getItemDesc(int orderId, String itemId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String iItemId = epcSecurityHelper.encodeForSQL(itemId);
        String desc = "";

        try {
            conn = epcDataSource.getConnection();

            sql = "select cpq_item_desc from epc_order_item where order_id = ? and item_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, iItemId); // item_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                desc = StringHelper.trim(rset.getString("cpq_item_desc"));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return desc;
    }


	public List<Fulfillment> getFulfillment(Integer orderId, String itemId) throws Exception {
		String iItemId = epcSecurityHelper.encodeForSQL(itemId);
		List<Fulfillment> list=new ArrayList<Fulfillment>();
		try(Connection conn = epcDataSource.getConnection()){
	    final String sql="select invoice_no as fulfill_reference, create_date as fulfill_date,"
					+ " imei_sim, fulfill_user, fulfill_salesman, location as fulfill_location"
					+ " from epc_order_invoice_log where order_id = ? and item_id = ? order by create_date desc";
	    try(PreparedStatement pstmt = conn.prepareStatement(sql)){
	    	pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, iItemId); // item_id
        try(ResultSet rset =pstmt.executeQuery()){
        	ResultSetMetaData md = rset.getMetaData();
        	int columnCount = md.getColumnCount();
        	while(rset.next()) {
        		Fulfillment fulfillment=new Fulfillment();
        		for (int i = 1; i <= columnCount; i++) {
        			BeanUtils.setProperty(fulfillment,
        					md.getColumnName(i).toLowerCase(), rset.getObject(i));
        		}
        		list.add(fulfillment);
        	}
		}
		}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return list;
	}


    public boolean isDoaBefore(int orderId, String itemId) {
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        int cnt = 0;
        boolean isDoaBefore = false;

        try {
            epcConn = epcDataSource.getConnection();

            sql = "select count(1) from epc_order_doa_log a where order_id = ? and item_id = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, itemId); // item_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                cnt = rset.getInt(1);
            }
            rset.close();
            pstmt.close();

            if(cnt > 0) {
                isDoaBefore = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception ee) {}
        }
        return isDoaBefore;
    }


    public EpcFulfillLocation fulfillByOtherLocation(int orderId, String offerDesc, String pickupLocation) {
        EpcFulfillLocation epcFulfillLocation = new EpcFulfillLocation();
        epcFulfillLocation.setFound(false);
        Connection epcConn = null;
        Connection fesConn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
//        String logStr = "[fulfillByOtherLocation][orderId:" + orderId + "] ";

        try {
            epcConn = epcDataSource.getConnection();
            fesConn = fesDataSource.getConnection();

            sql = "select replace(key_str2, 'XXX', ?) as invoice_location, replace(key_str3, 'XXX', ?) as doa_location " +
                  "  from epc_control_tbl " +
                  " where rec_type = ? " +
                  "   and key_str1 = ? " +
                  "   and key_str5 = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setString(1, pickupLocation); // location
            pstmt.setString(2, pickupLocation); // location
            pstmt.setString(3, "FULFILL_BY_OTHER_LOC"); // rec_type
            pstmt.setString(4, offerDesc); // key_str1
            pstmt.setString(5, "A"); // key_str5 - status: A
            rset = pstmt.executeQuery();
            if(rset.next()) {
                epcFulfillLocation.setInvoiceLocation(StringHelper.trim(rset.getString("invoice_location")));
                epcFulfillLocation.setDoaLocation(StringHelper.trim(rset.getString("doa_location")));
                epcFulfillLocation.setFound(true);
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception ee) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ee) {}
        }
        return epcFulfillLocation;
    }
}
