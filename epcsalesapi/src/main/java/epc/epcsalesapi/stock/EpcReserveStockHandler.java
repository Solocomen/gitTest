package epc.epcsalesapi.stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcActionLogHandler;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.helper.bean.EpcActionLog;
import epc.epcsalesapi.sales.EpcGetDeliveryInfoHandler;
import epc.epcsalesapi.sales.EpcOrderAttrHandler;
import epc.epcsalesapi.sales.EpcOrderHandler;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import epc.epcsalesapi.sales.bean.EpcDeliveryDetail;
import epc.epcsalesapi.sales.bean.EpcDeliveryInfo;
import epc.epcsalesapi.sales.bean.EpcExtendAllReserveTmpTicket;
import epc.epcsalesapi.sales.bean.EpcLoginChannel;
import epc.epcsalesapi.sales.bean.EpcOrderItemDetail;
import epc.epcsalesapi.sales.bean.EpcReserveStock;
import epc.epcsalesapi.sales.bean.EpcReserveStockItem;
import epc.epcsalesapi.stock.bean.EpcCreateTmpReserveResult;
import epc.epcsalesapi.stock.bean.EpcRemoveTmpReserve;

@Service
public class EpcReserveStockHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcReserveStockHandler.class);

    private final DataSource epcDataSource;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcOrderHandler epcOrderHandler;
    private final EpcStockHandler epcStockHandler;
    private final EpcOrderAttrHandler epcOrderAttrHandler;
    private final EpcActionLogHandler epcActionLogHandler;
    private final EpcGetDeliveryInfoHandler epcGetDeliveryInfoHandler;
    
    public EpcReserveStockHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper,
            EpcOrderHandler epcOrderHandler, EpcStockHandler epcStockHandler, EpcOrderAttrHandler epcOrderAttrHandler,
            EpcActionLogHandler epcActionLogHandler, EpcGetDeliveryInfoHandler epcGetDeliveryInfoHandler) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcOrderHandler = epcOrderHandler;
        this.epcStockHandler = epcStockHandler;
        this.epcOrderAttrHandler = epcOrderAttrHandler;
        this.epcActionLogHandler = epcActionLogHandler;
        this.epcGetDeliveryInfoHandler = epcGetDeliveryInfoHandler;
    }


    public EpcReserveStock reserveStock(EpcReserveStock epcReserveStock) {
        return reserveStock(epcReserveStock, false); // default - not from doa
    }


    public EpcReserveStock reserveStock(EpcReserveStock epcReserveStock, boolean isFromDoa) {
        String custId = StringHelper.trim(epcReserveStock.getCustId());
        int smcOrderId = epcReserveStock.getOrderId();
        String location = StringHelper.trim(epcReserveStock.getLocation());
        String channel = StringHelper.trim(epcReserveStock.getChannel());
        String previousChannel = "";
        ArrayList<EpcReserveStockItem> itemList = epcReserveStock.getItems();
        EpcReserveStockItem epcReserveStockItem = null;
        String itemId = "";
        String warehouse = "";
        String productCode = "";
        String smcOrderReference = "";
        boolean isValid = true;
        String errMsg = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        String tmpReserveTicket = "";
        String tmpPickupDate = "";
        String tmpIsReserve = "";
        EpcCreateTmpReserveResult epcCreateTmpReserveResult = null;
        boolean isItemIdBelongToOrder = false;
        int availableCount = 0;
        String logStr = "[reserveStock][custId:" + custId + "][orderId:" + smcOrderId + "] ";
        String tmpLogStr = "";
        
        
        try {
            tmpLogStr = "location:" + location + ",channel:" + channel;
logger.info("{}{}", logStr, tmpLogStr);

            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_item " +
                  "   set reserve_id = ?, " +
            	  "       pickup_date = ?, " +
                  "       is_reserve = ? " +
            	  " where order_id = ? " +
                  "   and item_id = ? ";
            pstmt = conn.prepareStatement(sql);
            
            
            // basic checking
            smcOrderReference = epcOrderHandler.isOrderBelongCust(conn, custId, smcOrderId);
        	if("NOT_BELONG".equals(smcOrderReference)) {
        		errMsg += "input order id [" + smcOrderId + "] is not belonged to input cust id [" + custId + "]. ";
            	isValid = false;
        	}
            
            // check quota for items
            // ...

            // check input channel
            if(!EpcLoginChannel.isChannelValid(channel)) {
                errMsg += "input channel [" + channel + "] is not valid. ";
                isValid = false;
            }
            
        	// check whether items are under the order input 
            if(itemList == null) {
                isValid = false;
                errMsg += "item list is empty. ";
            } else {
            	for(EpcReserveStockItem stockItem : itemList) {
            		itemId = stockItem.getItemId();
            		isItemIdBelongToOrder = epcOrderHandler.isItemIdBelongToOrder(conn, smcOrderId, itemId);
            		
            		if(!isItemIdBelongToOrder) {
            			errMsg += "item id [" + itemId + "] is not belonged to order [" + smcOrderId + "]. ";
                    	isValid = false;
            		}
            	}
            }
            
            // end of basic checking
            
            
            if(isValid) {           
                for (int i = 0; i < itemList.size(); i++) {
                    epcReserveStockItem = itemList.get(i);
                    
                    tmpReserveTicket = ""; // reset
                    tmpIsReserve = ""; // reset
                    
                    itemId = epcSecurityHelper.encode(StringHelper.trim(epcReserveStockItem.getItemId()));
                    productCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcReserveStockItem.getProductCode()));
                    warehouse = epcSecurityHelper.encode(epcStockHandler.getWarehouse(productCode));

                    // check availablity of stock
                    availableCount = epcStockHandler.getNoOfAvailableStock(channel, location, warehouse, productCode);
                    // end of check availablity of stock

                    tmpLogStr = "warehouse:" + warehouse + ",productCode:" + productCode + ",location:" + location + ",itemId:" + itemId + ",availableCount:" + availableCount;
logger.info("{}{}", logStr, tmpLogStr);
                    
                    // create tmp reserve ticket by ERP API


//                    epcCreateTmpReserveResult = epcStockHandler.createEpcTmpReserve(smcOrderReference, channel, location, warehouse, productCode);
                    if(EpcLoginChannel.STORE.equals(channel) && availableCount > 0) {
                        epcCreateTmpReserveResult = epcStockHandler.createEpcTmpReserve(smcOrderReference, channel, "STORE", location, warehouse, productCode);
                    } else {
                        epcCreateTmpReserveResult = epcStockHandler.createEpcTmpReserve(smcOrderReference, channel, "WEB", location, warehouse, productCode);
                    }
                    tmpReserveTicket = epcSecurityHelper.encode(epcCreateTmpReserveResult.getReserveNo());
                    tmpPickupDate = epcSecurityHelper.encode(epcCreateTmpReserveResult.getLatestDeliveryDate());

                    tmpLogStr = "epcCreateTmpReserveResult result:" + epcSecurityHelper.encode(epcCreateTmpReserveResult.getResult()) +
                                ",errMsg:" + epcSecurityHelper.encode(epcCreateTmpReserveResult.getErrMsg()) +
                                ",reserveNo:" + tmpReserveTicket +
                                ",latestDeliveryDate:" + tmpPickupDate;
logger.info("{}{}", logStr, tmpLogStr);
                    if("SUCCESS".equals(epcCreateTmpReserveResult.getResult())) {
                        // success
                        if("".equals(tmpReserveTicket)) {
                            // no stock
                            tmpReserveTicket = "NO_STOCK"; // this value is used to invoke reservation api (without stock, create reservation ticket)
                            tmpIsReserve = "Y";
 
                            epcReserveStockItem.setReserveNo("");
                            epcReserveStockItem.setHasStock("N");
                            epcReserveStockItem.setDeliveryDate("");
                        } else {
                            epcReserveStockItem.setReserveNo(tmpReserveTicket);
                            epcReserveStockItem.setHasStock("Y");
                            epcReserveStockItem.setDeliveryDate(tmpPickupDate);
                        }
 
                        if(!isFromDoa) { // for normal case - not doa
                            // update reserve id, pickup date to epc table
                            pstmt.setString(1, tmpReserveTicket); // reserve_id
                            pstmt.setString(2, tmpPickupDate); // pickup_date
                            pstmt.setString(3, tmpIsReserve); // is_reserve
                            pstmt.setInt(4, smcOrderId); // order_id
                            pstmt.setString(5, itemId); // item_id
                            pstmt.executeUpdate();

logger.info("{}{}", logStr, " update reserve id[" + tmpReserveTicket + "], pickup date[" + tmpPickupDate + "] to epc table SUCCESS, item_id:" + itemId);
                            // end of update reserve id, pickup date to epc table
                        }

                        // save tmp reserve no to log table
                        epcOrderAttrHandler.addAttr(conn, smcOrderId, "", itemId, epcOrderAttrHandler.ATTR_TYPE_STOCK_RESERVE_ID, tmpReserveTicket + "," + tmpPickupDate);
                        // end of save tmp reserve no to log table
                    } else {
                        // error
                        throw new Exception(epcSecurityHelper.encode(epcCreateTmpReserveResult.getErrMsg()));
                    }
                }


                // save current channel (will be used when extension of tickets)
                previousChannel = epcOrderAttrHandler.getAttrValue(conn, smcOrderId, "", "", epcOrderAttrHandler.ATTR_TYPE_STOCK_RESERVE_CURRENT_CHANNEL);
logger.info("{}{}", logStr, " previousChannel:" + previousChannel + ", channel:" + channel);
                if("".equals(previousChannel)) {
                    // 1st reserve
                    epcOrderAttrHandler.addAttr(conn, smcOrderId, "", "", epcOrderAttrHandler.ATTR_TYPE_STOCK_RESERVE_CURRENT_CHANNEL, channel);
                } else if (!previousChannel.equals(channel)) {
                    // current channel is diff with previous one
                    epcOrderAttrHandler.updateAttrValue(conn, smcOrderId, "", "", epcOrderAttrHandler.ATTR_TYPE_STOCK_RESERVE_CURRENT_CHANNEL, channel);
                }
                // end of save current channel
                

                conn.commit();
                
                epcReserveStock.setSaveStatus("SUCCESS");
            } else {
                epcReserveStock.setSaveStatus("FAIL");
                epcReserveStock.setErrorCode("1000");
                epcReserveStock.setErrorMessage(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            epcReserveStock.setSaveStatus("FAIL");
            epcReserveStock.setErrorCode("1001");
            epcReserveStock.setErrorMessage(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return epcReserveStock;
    }


    public void regenerateTmpTicket(EpcExtendAllReserveTmpTicket epcExtendAllReserveTmpTicket) {
        String custId = StringHelper.trim(epcExtendAllReserveTmpTicket.getCustId());
        int smcOrderId = epcExtendAllReserveTmpTicket.getOrderId();
        String location = StringHelper.trim(epcExtendAllReserveTmpTicket.getCreateLocation()); // login location
        String channel = StringHelper.trim(epcExtendAllReserveTmpTicket.getCreateChannel());
        ArrayList<String> failItemList = new ArrayList<>();
        epcExtendAllReserveTmpTicket.setFailItemList(failItemList);
        ArrayList<EpcOrderItemDetail> itemList = null;
        String smcOrderReference = "";
        boolean isValid = true;
        String errMsg = "";
        EpcDeliveryInfo epcDeliveryInfo = null;
        String lockReserve = "";
        String logStr = "[regenerateTmpTicket][custId:" + custId + "][orderId:" + smcOrderId + "] ";
        String tmpLogStr = "";
        
        
        try {
            tmpLogStr = "location:" + location + ",channel:" + channel;
logger.info("{}{}", logStr, tmpLogStr);

            // basic checking
            smcOrderReference = epcOrderHandler.isOrderBelongCust(custId, smcOrderId);
            if("NOT_BELONG".equals(smcOrderReference)) {
                errMsg += "input order id [" + smcOrderId + "] is not belonged to input cust id [" + custId + "]. ";
                isValid = false;
            }

            if(epcOrderHandler.isOrderLocked(custId, smcOrderId)) {
                errMsg += "input order [" + smcOrderId + "] is locked. ";
                isValid = false;
            }
            
            // check quota for items
            // ...

            // check input channel
            if(!EpcLoginChannel.isChannelValid(channel)) {
                errMsg += "input channel [" + channel + "] is not valid. ";
                isValid = false;
            }
           
            // end of basic checking
            
            
            if(isValid) {
                // check lock
                lockReserve = epcOrderAttrHandler.getAttrValue(smcOrderId, "", "", epcOrderAttrHandler.ATTR_TYPE_LOCK_RESERVE);
                if("Y".equals(lockReserve)) {
                    // locked by other process, exit !
                    //  to prevent concurrent calls
                    epcExtendAllReserveTmpTicket.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
                    epcExtendAllReserveTmpTicket.setErrMsg("locked by other process");
                } else {
                    // make a lock
                    epcOrderAttrHandler.addAttr(smcOrderId, "", "", epcOrderAttrHandler.ATTR_TYPE_LOCK_RESERVE, "Y");

                    // get all delivery info filled
                    epcDeliveryInfo = epcGetDeliveryInfoHandler.getPickupLocation(smcOrderId);


                    // get item list per order
                    itemList = epcOrderHandler.getAllDeviceItems(smcOrderId, "FOR-RESERVE");


                    failItemList = reserveByThread(custId, smcOrderId, smcOrderReference, itemList, channel, location, epcDeliveryInfo);
                    epcExtendAllReserveTmpTicket.setFailItemList(failItemList);

                    epcExtendAllReserveTmpTicket.setResult(EpcApiStatusReturn.RETURN_SUCCESS);

                    // free lock
                    epcOrderAttrHandler.obsoleteAttr(smcOrderId, "", "", epcOrderAttrHandler.ATTR_TYPE_LOCK_RESERVE);
                }
            } else {
                epcExtendAllReserveTmpTicket.setResult(EpcApiStatusReturn.RETURN_FAIL);
                epcExtendAllReserveTmpTicket.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcExtendAllReserveTmpTicket.setResult(EpcApiStatusReturn.RETURN_FAIL);
            epcExtendAllReserveTmpTicket.setErrMsg(e.getMessage());
        }
    }


    public ArrayList<String> reserveByThread(String custId, int smcOrderId, String smcOrderReference, ArrayList<EpcOrderItemDetail> itemList, String channel, String location, EpcDeliveryInfo epcDeliveryInfo) {
        CompletableFuture<String> future = null;
        ArrayList<CompletableFuture<String>> futureList = new ArrayList<CompletableFuture<String>>();
        CompletableFuture<Void> combinedFuture = null;
        ArrayList<String> failList = new ArrayList<>();
        String tmpReturn = "";

        try {
            for (EpcOrderItemDetail item : itemList) {
                future = CompletableFuture.completedFuture(item).thenApplyAsync(s -> reservePerItem(custId, smcOrderId, smcOrderReference, s, channel, location, epcDeliveryInfo));
                futureList.add(future);
            }

            combinedFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            combinedFuture.get(); // wait for all threads

            for(CompletableFuture<String> f : futureList) {
                tmpReturn = f.get();

                if(!"Y".equals(tmpReturn)) {
                    failList.add(tmpReturn);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return failList;
    }


    /***
     * 
     * @param custId
     * @param smcOrderId
     * @param smcOrderReference
     * @param epcOrderItemDetail
     * @param channel
     * @param location - current location location
     * @param epcDeliveryInfo
     * @return
     */
    public String reservePerItem(String custId, int smcOrderId, String smcOrderReference, EpcOrderItemDetail epcOrderItemDetail, String channel, String location, EpcDeliveryInfo epcDeliveryInfo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        String itemId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderItemDetail.getItemId()));
        String productCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderItemDetail.getItemCode()));
        String warehouse = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderItemDetail.getWarehouse()));
        EpcCreateTmpReserveResult epcCreateTmpReserveResult = null;
        String tmpReserveTicket = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderItemDetail.getReserveId()));
        String tmpPickupDate = "";
        String tmpIsReserve = "";
        String tmpErrMsg = "";
        String tmpPickupLocation = location; // default to login location
        EpcActionLog epcActionLog = null;
        StringBuilder sb = new StringBuilder(2 * 1024 * 1024); // default log size 2MB
        String rtnStr = "Y";
        String tmpOrderChannel = "";
        int availableCount = 0;
        String logStr = "[reservePerItem][custId:" + custId + "][orderId:" + smcOrderId + "][itemId:" + itemId + "][productCode:" + productCode + "] ";
        String tmpLogStr = "";

        sb.append(logStr + "\n");


        // determine location
        for(EpcDeliveryDetail d : epcDeliveryInfo.getDetails()) {
            for(String itemIdWithDelivery : d.getItems()) {
                if(itemIdWithDelivery.equals(itemId)) {
                    tmpPickupLocation = d.getPickupStore();
                    break;
                }
            }
        }
        // end of determine location
        

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_item " +
                  "   set reserve_id = ?, " +
                  "       pickup_date = ?, " +
                  "       is_reserve = ? " +
                  " where order_id = ? " +
                  "   and item_id = ? ";
            pstmt = conn.prepareStatement(sql);


            // reset ticket info in item table
            pstmt.setString(1, ""); // reserve_id
            pstmt.setString(2, ""); // pickup_date
            pstmt.setString(3, ""); // is_reserve
            pstmt.setInt(4, smcOrderId); // order_id
            pstmt.setString(5, itemId); // item_id
            pstmt.executeUpdate();
            // end of reset ticket info in item table


            // remove previous ticket
            if(!"".equals(tmpReserveTicket) && !"NO_STOCK".equals(tmpReserveTicket)) {
//                removeTmpTicketByThread(smcOrderId, itemId, tmpReserveTicket);
                removeTmpTicket(smcOrderId, itemId, tmpReserveTicket);

                sb.append("remove tmp ticket:" + tmpReserveTicket + "\n");
            } else {
                sb.append("NO tmp ticket to remove" + "\n");
            }
            // end of remove previous ticket


            // check availability and determine orderChannel
            availableCount = epcStockHandler.getNoOfAvailableStock(channel, tmpPickupLocation, warehouse, productCode);
            // end of check availablity of stock

            if(EpcLoginChannel.STORE.equals(channel) && availableCount > 0) {
                tmpOrderChannel = "STORE";
            } else {
                tmpOrderChannel = "WEB";
            }

            sb.append("availableCount:" + availableCount + "\n");
            sb.append("orderChannel:" + tmpOrderChannel + "\n");
            sb.append("location:" + tmpPickupLocation + "\n");
            // end of check availability and determine orderChannel


            epcCreateTmpReserveResult = epcStockHandler.createEpcTmpReserve(smcOrderReference, channel, tmpOrderChannel, tmpPickupLocation, warehouse, productCode);
            tmpReserveTicket = epcSecurityHelper.encode(epcCreateTmpReserveResult.getReserveNo());
            tmpPickupDate = epcSecurityHelper.encode(epcCreateTmpReserveResult.getLatestDeliveryDate());

            tmpLogStr = "epcCreateTmpReserveResult result:" + epcSecurityHelper.encode(epcCreateTmpReserveResult.getResult()) +
                        ",errMsg:" + epcSecurityHelper.encode(epcCreateTmpReserveResult.getErrMsg()) +
                        ",reserveNo:" + tmpReserveTicket +
                        ",latestDeliveryDate:" + tmpPickupDate;
logger.info("{}{}", logStr, tmpLogStr);

            sb.append(tmpLogStr + "\n");

            if("SUCCESS".equals(epcCreateTmpReserveResult.getResult())) {
                // success
                if("".equals(tmpReserveTicket)) {
                    // no stock
                    tmpReserveTicket = "NO_STOCK"; // this value is used to invoke reservation api (without stock, create reservation ticket)
                    tmpIsReserve = "Y";
                }

                // update result to item table
                pstmt.setString(1, tmpReserveTicket); // reserve_id
                pstmt.setString(2, tmpPickupDate); // pickup_date
                pstmt.setString(3, tmpIsReserve); // is_reserve
                pstmt.setInt(4, smcOrderId); // order_id
                pstmt.setString(5, itemId); // item_id
                pstmt.executeUpdate();
                // end of update result to item table

                // save tmp reserve no to log table
                epcOrderAttrHandler.addAttr(conn, smcOrderId, "", itemId, epcOrderAttrHandler.ATTR_TYPE_STOCK_RESERVE_ID, tmpReserveTicket + "," + tmpPickupDate);
                // end of save tmp reserve no to log table
            } else {
                // error
                tmpErrMsg = epcSecurityHelper.encodeForSQL(epcCreateTmpReserveResult.getErrMsg());

                tmpReserveTicket = "NO_STOCK"; // this value is used to invoke reservation api (without stock, create reservation ticket)
                tmpIsReserve = "Y";

                rtnStr = itemId; // return to caller !!!

                // update result to item table (treated as no stock case !)
                pstmt.setString(1, tmpReserveTicket); // reserve_id
                pstmt.setString(2, tmpPickupDate); // pickup_date
                pstmt.setString(3, tmpIsReserve); // is_reserve
                pstmt.setInt(4, smcOrderId); // order_id
                pstmt.setString(5, itemId); // item_id
                pstmt.executeUpdate();
                // end of update result to item table

                // save tmp reserve no to log table
                epcOrderAttrHandler.addAttr(conn, smcOrderId, "", itemId, epcOrderAttrHandler.ATTR_TYPE_STOCK_RESERVE_ID, tmpErrMsg);
                // end of save tmp reserve no to log table
            }

            conn.commit();

            epcActionLog = new EpcActionLog();
            epcActionLog.setAction("REGEN_TICKET");
            epcActionLog.setUri("");
            epcActionLog.setInString(sb.toString());
            epcActionLog.setOutString("");

            epcActionLogHandler.writeApiLogAsync(smcOrderId, epcActionLog);

        } catch (Exception e) {
            e.printStackTrace();

            try{ if(conn != null) { conn.rollback(); } } catch (Exception ee) {ee.printStackTrace(); }
        } finally {
            try{ if(conn != null) { conn.close(); } } catch (Exception e) { e.printStackTrace(); }
        }

        return rtnStr;
    }


    public void removeTmpTicketByThread(int smcOrderId, String itemId, String tmpReserveId) {
        CompletableFuture<EpcRemoveTmpReserve> future = null;
        String logStr = "[removeTmpTicketByThread][orderId:" + smcOrderId + "][itemId:" + itemId + "][reserveId:" + tmpReserveId + "] ";
        

        try {
            future = CompletableFuture.completedFuture(tmpReserveId).thenApplyAsync(s -> epcStockHandler.removeTmpReserve(s));
            future.whenComplete((result, ex) -> {
                String tmpLogStr = "result:" + result.getResult() + ",errMsg:" + result.getErrMsg();
logger.info("{}{}", logStr, tmpLogStr);

                EpcActionLog epcActionLog = new EpcActionLog();
                epcActionLog.setAction("CANCEL_TICKET");
                epcActionLog.setUri("");
                epcActionLog.setInString(logStr + "\n" + tmpLogStr);
                epcActionLog.setOutString("");

                epcActionLogHandler.writeApiLogAsync(smcOrderId, epcActionLog);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void removeTmpTicket(int smcOrderId, String itemId, String tmpReserveId) {
        EpcRemoveTmpReserve epcRemoveTmpReserve = null;
        EpcActionLog epcActionLog = null;
        String logStr = "[removeTmpTicket][orderId:" + smcOrderId + "][itemId:" + itemId + "][reserveId:" + tmpReserveId + "] ";
        String tmpLogStr = "";

        try {
            epcRemoveTmpReserve = epcStockHandler.removeTmpReserve(tmpReserveId);
            tmpLogStr = "result:" + epcRemoveTmpReserve.getResult() + ",errMsg:" + epcRemoveTmpReserve.getErrMsg();
logger.info("{}{}", logStr, tmpLogStr);
            
            epcActionLog = new EpcActionLog();
            epcActionLog.setAction("CANCEL_TICKET");
            epcActionLog.setUri("");
            epcActionLog.setInString(logStr + "\n" + tmpLogStr);
            epcActionLog.setOutString("");

            epcActionLogHandler.writeApiLogAsync(smcOrderId, epcActionLog);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
