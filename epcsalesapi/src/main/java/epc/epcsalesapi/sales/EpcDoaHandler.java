package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcDoa;
import epc.epcsalesapi.sales.bean.EpcDoaInvoice;
import epc.epcsalesapi.sales.bean.EpcFulfillOrder;
import epc.epcsalesapi.sales.bean.EpcFulfillResult;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcOrderItem;
import epc.epcsalesapi.sales.bean.EpcOrderItemDetail;
import epc.epcsalesapi.sales.bean.EpcReserveStock;
import epc.epcsalesapi.sales.bean.EpcReserveStockItem;
import epc.epcsalesapi.stock.EpcReserveStockHandler;
import epc.epcsalesapi.stock.EpcStockHandler;
import epc.epcsalesapi.stock.bean.EpcConfirmStockReserveResult;

@Service
public class EpcDoaHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcDoaHandler.class);

    private final DataSource epcDataSource;
    private final DataSource fesDataSource;
    private final EpcOrderHandler epcOrderHandler;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcStockHandler epcStockHandler;
    private final EpcAppleCareHandler epcAppleCareHandler;
    private final EpcInvoiceHandler epcInvoiceHandler;
    private final EpcSalesmanHandler epcSalesmanHandler;
    private final EpcFulfillHandler epcFulfillHandler;
    private final EpcReserveStockHandler epcReserveStockHandler;


    public EpcDoaHandler(
        DataSource epcDataSource, DataSource fesDataSource, EpcOrderHandler epcOrderHandler,
        EpcSecurityHelper epcSecurityHelper, EpcStockHandler epcStockHandler,
        EpcAppleCareHandler epcAppleCareHandler, EpcInvoiceHandler epcInvoiceHandler,
        EpcSalesmanHandler epcSalesmanHandler, EpcFulfillHandler epcFulfillHandler,
        EpcReserveStockHandler epcReserveStockHandler
    ) {
        this.epcDataSource = epcDataSource;
        this.fesDataSource = fesDataSource;
        this.epcOrderHandler = epcOrderHandler;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcStockHandler = epcStockHandler;
        this.epcAppleCareHandler = epcAppleCareHandler;
        this.epcInvoiceHandler = epcInvoiceHandler;
        this.epcSalesmanHandler = epcSalesmanHandler;
        this.epcFulfillHandler = epcFulfillHandler;
        this.epcReserveStockHandler = epcReserveStockHandler;
    }


    public void doa(EpcDoa epcDoa) {
        boolean isValid = true;
        String errMsg = "";
        int orderId = epcDoa.getOrderId(); // smc order id
        String custId = epcSecurityHelper.encodeForSQL(epcDoa.getCustId());
        String user = epcSecurityHelper.encodeForSQL(epcDoa.getUser());
        String salesman = epcSecurityHelper.encodeForSQL(epcDoa.getSalesman());
        String location = epcSecurityHelper.encodeForSQL(epcDoa.getLocation());
        String channel = epcSecurityHelper.encodeForSQL(epcDoa.getChannel());
        String doaLocation = epcSecurityHelper.encodeForSQL(epcDoa.getDoaLocation());
        String approveBy = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcDoa.getApproveBy()));
        String waiveFormCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcDoa.getWaiveFormCode()));
        ArrayList<EpcOrderItem> itemList = epcDoa.getItems();
        ArrayList<EpcOrderItem> newItemList = null;
        EpcOrderItem epcOrderItem = null;
        TreeMap<String, EpcOrderItemDetail> itemDetailMap = epcOrderHandler.getItemDetails(orderId); // get order, item info
        EpcOrderItemDetail epcOrderItemDetail = null;
        boolean isValidProduct = false;
        boolean isSameModelSeries = false;
        EpcFulfillOrder epcFulfillOrder = null;
        EpcFulfillResult epcFulfillResult = null;
        EpcConfirmStockReserveResult epcConfirmStockReserveResult = null;
        EpcReserveStock epcReserveStock = null;
        EpcReserveStockItem epcReserveStockItem = null;
        ArrayList<EpcReserveStockItem> reserveItems = null;
        EpcDoaInvoice epcDoaInvoice = null;
        ArrayList<String> doaInvoiceList = new ArrayList<String>();
        TreeMap<String, String> tmpInvoiceMap = new TreeMap<String, String>();
        String tmpInvoiceNos = "";
        String tmpItemIds = "";
        boolean isClearInvoiceNo = false;
        String salesmanLogRemarks = "";
        boolean isInvoiceValid = false;
        String tmpCurrProductCode = "";
        String tmpNewProductCode = "";
        String tmpCurrImeiSim = "";
        String tmpNewImeiSim = "";
        String currentDate = new SimpleDateFormat("yyyyMMdd").format(new java.util.Date());
        ArrayList<String> transferNoteList = null;
        boolean isPerformFulfillment = true;
        boolean isNeedReserve = false;
        ArrayList<EpcOrderItemDetail> detailListForReserve = null;
        String logStr = "[doa()][orderId:" + orderId + "]";
        String tmpLog = "";
        
// kerrytsang, 20230313, should reject ALL apple product
// kerrytsang, 20230316, should handle 1 invoice at a time
        
        try {
            // distinct invoice no
            for(EpcOrderItem a: itemList) {
                tmpInvoiceMap.put(a.getInvoiceNo(), "");

                if("".equals(tmpItemIds)) {
                    tmpItemIds = a.getItemId();
                } else {
                    tmpItemIds += "," + a.getItemId();
                }
            }
            doaInvoiceList = (ArrayList)tmpInvoiceMap.keySet().stream().collect(Collectors.toList());
            // end of distinct invoice no
            
            // for log creation
            for(String s : doaInvoiceList) {
                if("".equals(tmpInvoiceNos)) {
                    tmpInvoiceNos = s;
                } else {
                    tmpInvoiceNos += "," + s;
                }
            }
            
            logStr += "[invNo:" + tmpInvoiceNos + "][itemId:" + tmpItemIds + "] ";
            // end for log creation
            
                // added by Danny Chan on 2021-8-5 (Apple Care Enhancement): start
                Connection conn = null;
                
                PreparedStatement pstmt = null;
                ResultSet rs = null;
                
                for(String invoiceNo : doaInvoiceList) {
                    boolean hasEAppleCareProduct = false;
                    
                    String existing_imei = null, new_imei = null;
                    
                    for (int i=0; i<epcDoa.getItems().size(); i++) {
                        EpcOrderItem item = epcDoa.getItems().get(i);
                        
                        if (!invoiceNo.equals(item.getInvoiceNo())) {
                            continue;
                        }
                        
                        boolean isEAppleCareProduct = false;
                        
                        try {
                            conn = fesDataSource.getConnection();
                            isEAppleCareProduct = epcAppleCareHandler.isEAppleCareProduct(conn, item.getWarehouse(), item.getProductCode());
                        } catch (Exception e) {
                            epcDoa.setResult("FAIL");
                            epcDoa.setErrMsg("Error in calling isEAppleCareProduct: " + e.toString() + ". ");
                            return;
                        } finally {
                            try { if(conn != null) { conn.close(); }} catch (Exception e) {}
                        }
                        
                        hasEAppleCareProduct = hasEAppleCareProduct || isEAppleCareProduct;
                        
                        if (!isEAppleCareProduct && item.getWarehouse() != null && item.getWarehouse().equals("AH")) {
                            existing_imei = item.getImeiSim();
                            new_imei = item.getNewImeiSim();
                        }
                    }
                    
                    if (!hasEAppleCareProduct) {
                        continue;
                    }
                    
                    String sql1 = "select (TRUNC(SYSDATE) - TRUNC(invoice_date)) from zz_pinv_hdg where invoice_no = ?";

                    try {
                        conn = fesDataSource.getConnection();

                        pstmt = conn.prepareStatement(sql1);

                        pstmt.setString(1, invoiceNo);

                        rs = pstmt.executeQuery();

                        if (!rs.next()) {
                            epcDoa.setResult("FAIL");
                            epcDoa.setErrMsg("Invoice " + invoiceNo + " is not found. ");
                            return;
                        }

                        /* checking: reject doa action for invoice issued more than 7 days ago */
                        if (rs.getInt(1) > 7) {
                            epcDoa.setResult("FAIL");
                            epcDoa.setErrMsg("Invoice " + invoiceNo + " issued more than 7 days ago cannot be DOA.");
                            return;
                        }
                    } catch (Exception e) {
                        epcDoa.setResult("FAIL");
                        epcDoa.setErrMsg("Error in checking issue date of invoice " + invoiceNo + ": " + e.toString() + ". ");
                        return;
                    } finally {
                        try { if(rs != null) {rs.close(); } } catch (Exception ignored) {}
                        try { if(pstmt != null) {pstmt.close(); } } catch (Exception ignored) {}
                        try { if(conn != null) { conn.close(); } } catch (Exception ignored) {}
                    }
                    
                    if (StringHelper.trim(existing_imei).equals(StringHelper.trim(new_imei))) {
                        epcDoa.setResult("FAIL");
                        epcDoa.setErrMsg("Existing and new imei cannot be the same");
                        return;
                    } 
                    
                    // check if there is pending AppleCare+ registration process for cancel case
                    String sql2 = "select count(*) from ZZ_PINV_eapp_backend where invoice_no = ? and status = 'W'";

                    try {
                        conn = fesDataSource.getConnection();

                        pstmt = conn.prepareStatement(sql2);
                        pstmt.setString(1, existing_imei);

                        rs = pstmt.executeQuery();

                        if (rs.next()) {
                            if (rs.getInt(1) > 0) {
                                epcDoa.setResult("FAIL");
                                epcDoa.setErrMsg("Backend AppleCare+ registration process exists for invoice " + invoiceNo);
                                return;
                            }
                        }
                    } catch (Exception e) {
                        epcDoa.setResult("FAIL");
                        epcDoa.setErrMsg("Error in checking backend registration process for AppleCare+: " + e.toString());
                        return;
                    } finally {
                        try { if(conn != null) {conn.close();}} catch (Exception e) {}
                    }
                }
                // added by Danny Chan on 2021-8-5 (Apple Care Enhancement): end
            
            // basic checking
            if(doaInvoiceList.size() > 1) {
                errMsg += "cannot handle more than 1 invoice at a time. ";
                isValid = false;
            }

            for (EpcOrderItem item : itemList) {
                tmpCurrProductCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(item.getProductCode()));
                tmpNewProductCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(item.getNewProductCode()));
                tmpCurrImeiSim = epcSecurityHelper.encodeForSQL(StringHelper.trim(item.getImeiSim()));
                tmpNewImeiSim = epcSecurityHelper.encodeForSQL(StringHelper.trim(item.getNewImeiSim()));

                tmpLog = "currentProductCode:" + tmpCurrProductCode + ",newProductCode:" + tmpNewProductCode + ",currImeiSim:" + tmpCurrImeiSim + ",newImeiSim:" + tmpNewImeiSim;
logger.info("{}{}{}", logStr, " input ", tmpLog);

                // whether input items are belonged to input order
                epcOrderItemDetail = (EpcOrderItemDetail)itemDetailMap.get(item.getItemId());
                if(epcOrderItemDetail == null) {
                    errMsg += "item " + item.getItemId() + "[" + item.getProductCode() + "] is not belonged to order " + orderId + ". ";
                    isValid = false;
                } else {
                    // check input imei vs product code
                    if("AP".equals(epcOrderItemDetail.getWarehouse())) {
    //	                	// sim, should be no product at the beginning
    //	                	//  get product code from fes.stquem by input sim no.
    //	                	// no need to check quantity_free, assume ERP api will do so
    //	                	productCode = epcStockHandler.getProductCodeBySerialNo(item.getImeiSim());
    //	                	item.setProductCode(productCode);
    //	                	if("".equals(productCode)) {
    //	                		errMsg += "product code is not found for " + item.getItemId() + ". ";
    //	                        isValid = false;
    //	                	}
                    } else {
                        // AA / AH
                        
                        // check model series
                        if( !tmpNewProductCode.equals(tmpCurrProductCode) ) {
                            // only allow same model series to perform doa action
                            isSameModelSeries = epcStockHandler.isSameModelSeries(tmpCurrProductCode, tmpNewProductCode);
                            if(!isSameModelSeries) {
                                // not in same model series
                                errMsg += "different model series for item " + item.getItemId() + "[" + tmpCurrProductCode + "/" + tmpNewProductCode + "], DOA is not allowed. ";
                                isValid = false;
                            }
                        }

                        //  verify with fes.stquem
                        // no need to check quantity_free, assume ERP api will do so
                        if(!"".equals(tmpNewImeiSim)) {
                            isValidProduct = epcStockHandler.isValidProduct(tmpNewProductCode, tmpNewImeiSim);
                            if(!isValidProduct) {
                                // not matched
                                errMsg += "item " + item.getItemId() + "[" + tmpNewProductCode + "] is not matched with " + tmpNewImeiSim + ". ";
                                isValid = false;
                            }
                        }
                    }
                }
            }
            
            
            // check whether input invoice is/are valid
            isInvoiceValid = epcInvoiceHandler.isInvoiceValid(doaInvoiceList);
            if(!isInvoiceValid) {
                errMsg += "input invoice is not valid. ";
                isValid = false;
            }
            // end of check whether input invoice is/are valid


            // make reservation of new item - "DEVICE" item only
            detailListForReserve = epcOrderHandler.getAllDeviceItems(orderId, "FOR-RESERVE"); // items (in this list) need reserve ticket

            epcReserveStock = new EpcReserveStock();
            epcReserveStock.setOrderId(orderId);
            epcReserveStock.setCustId(custId);
            epcReserveStock.setLocation(location);
            epcReserveStock.setChannel(channel);
            reserveItems = new ArrayList<EpcReserveStockItem>();
            epcReserveStock.setItems(reserveItems);
            for(EpcOrderItem a: itemList) {
                epcOrderItemDetail = itemDetailMap.get(a.getItemId());
                
                if(EpcItemCategory.DEVICE.equals(epcOrderItemDetail.getItemCat())) {
                    isNeedReserve = false; // reset

                    for(EpcOrderItemDetail e : detailListForReserve) { // if exist in reserve list, then need to reserve !!!
                        if(a.getItemId().equals(e.getItemId())) {
                            isNeedReserve = true;
                            break;
                        }
                    }

                    if(isNeedReserve) {
                        tmpLog = "device " + a.getNewProductCode() + " reserve ticket";
logger.info("{}{}", logStr, tmpLog);

                        epcReserveStockItem = new EpcReserveStockItem();
                        epcReserveStockItem.setItemId(a.getItemId());
                        epcReserveStockItem.setProductCode(a.getNewProductCode());

                        reserveItems.add(epcReserveStockItem);
                    } else {
                        tmpLog = "device " + a.getNewProductCode() + " SKIP reserve ticket";
logger.info("{}{}", logStr, tmpLog);
                    }
                }
            }
            
            // create tmp ticket
            if(epcReserveStock.getItems().size() > 0) {
                epcReserveStock = epcReserveStockHandler.reserveStock(epcReserveStock, true); // from doa

                tmpLog = "epcReserveStock saveStatus:" + epcSecurityHelper.encodeForSQL(epcReserveStock.getSaveStatus()) + 
                         ",errorCode:" + epcSecurityHelper.encodeForSQL(epcReserveStock.getErrorCode()) +
                         ",errorMessage:" + epcSecurityHelper.encodeForSQL(epcReserveStock.getErrorMessage());
logger.info("{}{}", logStr, tmpLog);
                if("SUCCESS".equals(epcReserveStock.getSaveStatus())) {
                    // check pickup date, per item
                    for (EpcReserveStockItem item : reserveItems) {
                        if("Y".equals(item.getHasStock())) {
                            // with stock
                            tmpLog = "[" + item.getProductCode() + "/" + item.getItemId() + "] hasStock:" + item.getHasStock() + ",reserveNo:" + item.getReserveNo() + ",deliveryDate:" + item.getDeliveryDate();

                            if( !currentDate.equals(item.getDeliveryDate()) ) {
                                isPerformFulfillment = false;
                            }
                        } else {
                            // without stock
                            tmpLog = "[" + item.getProductCode() + "/" + item.getItemId() + "] hasStock:" + item.getHasStock();

                            isPerformFulfillment = false;
                        }
logger.info("{}{}", logStr, tmpLog);
                    }
                    // end of check pickup date, per item

// move to main logic block, kerrytsang, 20231003
//                    // confirm reservation of new item (confirm tmp ticket / create reservation ticket)
//                    epcConfirmStockReserveResult = epcStockHandler.confirmEpcTmpReserve(custId, orderId, user, salesman, channel, location, itemList, true);
//                    tmpLog = "epcConfirmStockReserve result:" + epcSecurityHelper.encodeForSQL(epcConfirmStockReserveResult.getResult()) +
//                             ",errMsg:" + epcSecurityHelper.encodeForSQL(epcConfirmStockReserveResult.getErrMsg());
//logger.info("{}{}", logStr, tmpLog);
//                    if(!"SUCCESS".equals(epcConfirmStockReserveResult.getResult())) {
//                        errMsg += "cannot confirm reserve, " + epcConfirmStockReserveResult.getErrMsg() + ". ";
//                        isValid = false;
//                    }
//                    // end of confirm reservation of new item
// end of move to main logic block, kerrytsang, 20231003
                } else {
                    errMsg += "cannot make reserve, " + epcReserveStock.getErrorMessage() + ". ";
                    isValid = false;
                }
            } else {
                tmpLog = "no reserve item";
logger.info("{}{}", logStr, tmpLog);  
            }
            // end of make reservation of new item - device only

            // end of basic checking


            if(isValid) {
                // clear invoice no & update new reserve id, pickup date, product code
                isClearInvoiceNo = clearInvoiceNoAndUpdateReserveInfo(orderId, itemList, reserveItems);
                tmpLog = "clearInvoiceNoAndUpdateReserveInfo:" + isClearInvoiceNo;
logger.info("{}{}", logStr, tmpLog);
                // end of clear invoice no & update new reserve id, pickup date, product code


                // convert tmp ticket to real
                if(epcReserveStock.getItems().size() > 0) {
                    // confirm reservation of new item (confirm tmp ticket / create reservation ticket)
                    epcConfirmStockReserveResult = epcStockHandler.confirmEpcTmpReserve(custId, orderId, user, salesman, channel, location, itemList, true);
                    tmpLog = "epcConfirmStockReserve result:" + epcSecurityHelper.encodeForSQL(epcConfirmStockReserveResult.getResult()) +
                            ",errMsg:" + epcSecurityHelper.encodeForSQL(epcConfirmStockReserveResult.getErrMsg());
logger.info("{}{}", logStr, tmpLog);
                    if(!"SUCCESS".equals(epcConfirmStockReserveResult.getResult())) {
                        // error ...
                    }
                    // end of confirm reservation of new item
                }
                // end of convert tmp ticket to real

                
                // void old pos invoice (also update epc_order_item.invoice_no = null)
                //  kerrytsang, 20230823 - include rollback preorder record (by fulfill reference) and consume again (by dummy inv no)
                epcDoaInvoice = new EpcDoaInvoice();
                epcDoaInvoice.setInvoiceList(doaInvoiceList);
                epcDoaInvoice.setUser(user);
                epcDoaInvoice.setSalesman(salesman);
                epcDoaInvoice.setLocation(location);
                epcDoaInvoice.setDoaLocation(doaLocation);
                epcDoaInvoice.setSymptom(epcDoa.getSymptom());

                epcInvoiceHandler.doaInvoice(epcDoaInvoice, itemList, orderId, approveBy, waiveFormCode, true);
                tmpLog = "epcDoaInvoice result:" + epcSecurityHelper.encodeForSQL(epcDoaInvoice.getResult()) +
                         ",errMsg:" + epcSecurityHelper.encodeForSQL(epcDoaInvoice.getErrMsg());
logger.info("{}{}", logStr, tmpLog);
                if(!"SUCCESS".equals(epcDoaInvoice.getResult())) {
                    throw new Exception(epcDoaInvoice.getErrMsg());
                }

                // get transfer note if any
                transferNoteList = epcInvoiceHandler.getTransferNotes(doaInvoiceList);
                epcDoa.setTransferNotes(transferNoteList);
                // get transfer note if any

                // end of void old pos invoice (also update epc_order_item.invoice_no = null)


                // fulfill - create new invoice
// if one of the item is "reserved" or pickup date != today, then not perform fulfillment !!!
// [kerry,20230823] for preorder item, no need to reserve, but fulfill immediately
                tmpLog = "fulfill items, isPerformFulfillment:" + isPerformFulfillment;
logger.info("{}{}", logStr, tmpLog);
                if(isPerformFulfillment) {
                    newItemList = new ArrayList<EpcOrderItem>();
                    for(EpcOrderItem a: itemList) {
                        epcOrderItem = new EpcOrderItem();
                        epcOrderItem.setItemId(a.getItemId());
                        epcOrderItem.setProductCode(a.getNewProductCode());
                        epcOrderItem.setImeiSim(a.getNewImeiSim()); // use new imei/sim
                        epcOrderItem.setParentItemId(StringHelper.trim(a.getParentItemId()));

                        newItemList.add(epcOrderItem);
                    }

                    epcFulfillOrder = new EpcFulfillOrder();
                    epcFulfillOrder.setOrderId(orderId);
                    epcFulfillOrder.setFulfillUser(user);
                    epcFulfillOrder.setFulfillSalesman(salesman);
                    epcFulfillOrder.setFulfillLocation(location);
                    epcFulfillOrder.setFulfillChannel(channel);
                    epcFulfillOrder.setItems(newItemList);

                    epcFulfillResult = epcFulfillHandler.fulfill(epcFulfillOrder);
                    tmpLog = "epcFulfill result:" + epcSecurityHelper.encodeForSQL(epcFulfillResult.getResult()) +
                             ",errMsg:" + epcSecurityHelper.encodeForSQL(epcFulfillResult.getErrMsg());
logger.info("{}{}", logStr, tmpLog);
                    if(!"SUCCESS".equals(epcFulfillResult.getResult())) {
                        throw new Exception(epcFulfillResult.getErrMsg());
                    }
                } else {
logger.info("{}{}", logStr, "  no need to perform fulfillment");
                }
                // end of fulfill - create new invoice


                // create salesman log
                salesmanLogRemarks = "itemId:" + tmpItemIds + "@" + "doaInvNo:" + tmpInvoiceNos;
                epcSalesmanHandler.createSalesmanLog(orderId, "", user, salesman, location, channel, epcSalesmanHandler.actionDoaItem, salesmanLogRemarks);
                // end of create salesman log

                
                epcDoa.setResult("SUCCESS");
            } else {
                // error
                epcDoa.setResult("FAIL");
                epcDoa.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcDoa.setResult("FAIL");
            epcDoa.setErrMsg(e.getMessage());
        } finally {
        }
    }


    /***
     * for cancel order, no re-issue
     *  need to resume preorder record if any (free up preorder)
     * @param epcDoa
     */
    public void doaAndStockIn(EpcDoa epcDoa) {
        boolean isValid = true;
        String errMsg = "";
        int orderId = epcDoa.getOrderId(); // smc order id
        String custId = epcSecurityHelper.encodeForSQL(epcDoa.getCustId());
        String user = epcSecurityHelper.encodeForSQL(epcDoa.getUser());
        String salesman = epcSecurityHelper.encodeForSQL(epcDoa.getSalesman());
        String location = epcSecurityHelper.encodeForSQL(epcDoa.getLocation());
        String channel = epcSecurityHelper.encodeForSQL(epcDoa.getChannel());
        String doaLocation = epcSecurityHelper.encodeForSQL(epcDoa.getDoaLocation());
        String approveBy = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcDoa.getApproveBy()));
        String waiveFormCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcDoa.getWaiveFormCode()));
        ArrayList<EpcOrderItem> itemList = epcDoa.getItems();
        EpcDoaInvoice epcDoaInvoice = null;
        ArrayList<String> doaInvoiceList = new ArrayList<String>();
        TreeMap<String, String> tmpInvoiceMap = new TreeMap<String, String>();
        String tmpInvoiceNos = "";
        String tmpItemIds = "";
        String salesmanLogRemarks = "";
        String logStr = "[doaAndStockIn][orderId:" + orderId + "]";
        String tmpLog = "";
        ArrayList<String> transferNoteList = null;

        
        try {
            // distinct invoice no
            for(EpcOrderItem a: itemList) {
                tmpInvoiceMap.put(a.getInvoiceNo(), "");

                if("".equals(tmpItemIds)) {
                    tmpItemIds = a.getItemId();
                } else {
                    tmpItemIds += "," + a.getItemId();
                }
            }
            doaInvoiceList = (ArrayList)tmpInvoiceMap.keySet().stream().collect(Collectors.toList());
            // end of distinct invoice no
            
            // for log creation
            for(String s : doaInvoiceList) {
                if("".equals(tmpInvoiceNos)) {
                    tmpInvoiceNos = s;
                } else {
                    tmpInvoiceNos += "," + s;
                }
            }
            
            logStr += "[invNo:" + tmpInvoiceNos + "][itemId:" + tmpItemIds + "] ";
            // end for log creation
            
            
            // basic checking
            // ...
            // end of basic checking


            if(isValid) {
                // void old pos invoice (also update epc_order_item.invoice_no = null)
                epcDoaInvoice = new EpcDoaInvoice();
                epcDoaInvoice.setInvoiceList(doaInvoiceList);
                epcDoaInvoice.setUser(user);
                epcDoaInvoice.setSalesman(salesman);
                epcDoaInvoice.setLocation(location);
                epcDoaInvoice.setDoaLocation(doaLocation);

                epcDoaInvoice.setSymptom(epcDoa.getSymptom());
                epcInvoiceHandler.doaInvoice(epcDoaInvoice, itemList, orderId, approveBy, waiveFormCode, false);
logger.info("{}{}{}", logStr, "epcDoaInvoice.getResult():", epcSecurityHelper.encode(epcDoaInvoice.getResult()));
logger.info("{}{}{}", logStr, "epcDoaInvoice.getErrMsg():", epcSecurityHelper.encode(epcDoaInvoice.getErrMsg()));
                if(!"SUCCESS".equals(epcDoaInvoice.getResult())) {
                    throw new Exception(epcDoaInvoice.getErrMsg());
                }


                // get transfer note if any
                transferNoteList = epcInvoiceHandler.getTransferNotes(doaInvoiceList);
                epcDoa.setTransferNotes(transferNoteList);
                // get transfer note if any


                // create salesman log
//                salesmanLogRemarks = "doa invoice " + tmpInvoiceNos + ",doa location:" + doaLocation;
                salesmanLogRemarks = "itemId:" + tmpItemIds + "@" + "doaInvNo:" + tmpInvoiceNos;
                epcSalesmanHandler.createSalesmanLog(orderId, "", user, salesman, location, channel, epcSalesmanHandler.actionDoaItem, salesmanLogRemarks);
                // end of create salesman log

                // end of void old pos invoice (also update epc_order_item.invoice_no = null)

                
                epcDoa.setResult("SUCCESS");
            } else {
                // error
                epcDoa.setResult("FAIL");
                epcDoa.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcDoa.setResult("FAIL");
            epcDoa.setErrMsg(e.getMessage());
        } finally {
        }
    }


    public boolean clearInvoiceNoAndUpdateReserveInfo(int orderId, ArrayList<EpcOrderItem> itemList, ArrayList<EpcReserveStockItem> reserveItemList) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        boolean isUpdate = false;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_item " +
                  "   set invoice_no = null, invoice_date = null " +
                  " where order_id = ? " +
                  "   and item_id = ? " +
                  "   and invoice_no = ? ";
            pstmt = conn.prepareStatement(sql);
            
            for(EpcOrderItem item : itemList) {
                pstmt.setInt(1, orderId); // order_id
                pstmt.setString(2, item.getItemId()); // item_id
                pstmt.setString(3, item.getInvoiceNo()); // invoice_no
                pstmt.executeUpdate();
            }
            
            sql = "update epc_order_item " +
                  "   set reserve_id = ?, " +
                  "       pickup_date = ?, " +
                  "       item_code = ? " +
                  " where order_id = ? " +
                  "   and item_id = ? ";
            pstmt = conn.prepareStatement(sql);
            
            for(EpcReserveStockItem item : reserveItemList) {
                pstmt.setString(1, item.getReserveNo()); // reserve_id
                pstmt.setString(2, item.getDeliveryDate()); // pickup_date
                pstmt.setString(3, item.getProductCode()); // item_code - product_code
                pstmt.setInt(4, orderId); // order_id
                pstmt.setString(5, item.getItemId()); // item_id
                pstmt.executeUpdate();
            }
            
            conn.commit();
            
            // update new Product desc
            ArrayList<EpcOrderItem> itemListForDesc = new ArrayList<>();
            for(EpcReserveStockItem item : reserveItemList) {
            	EpcOrderItem epcOrderItem = new EpcOrderItem();
                epcOrderItem.setItemId(item.getItemId());
                epcOrderItem.setProductCode(item.getProductCode());
                itemListForDesc.add(epcOrderItem);
            }
            
            if(itemListForDesc.size() > 0) {
                epcOrderHandler.getAndUpdateProductDescFromVakaCms(orderId, itemListForDesc);
            }
            
            isUpdate = true;
        } catch (Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
}
