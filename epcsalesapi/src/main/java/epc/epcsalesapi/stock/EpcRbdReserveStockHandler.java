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
import epc.epcsalesapi.sales.EpcOrderAttrHandler;
import epc.epcsalesapi.sales.EpcOrderHandler;
import epc.epcsalesapi.sales.bean.EpcLoginChannel;
import epc.epcsalesapi.sales.bean.EpcReserveStock;
import epc.epcsalesapi.sales.bean.EpcReserveStockItem;
import epc.epcsalesapi.stock.bean.EpcCreateTmpReserveResult;
import epc.epcsalesapi.stock.bean.EpcRemoveTmpReserve;

@Service
public class EpcRbdReserveStockHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcReserveStockHandler.class);

    private final DataSource epcDataSource;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcOrderHandler epcOrderHandler;
    private final EpcStockHandler epcStockHandler;
    private final EpcOrderAttrHandler epcOrderAttrHandler;
    private final EpcActionLogHandler epcActionLogHandler;

    public EpcRbdReserveStockHandler(
        DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper, EpcOrderHandler epcOrderHandler, 
        EpcStockHandler epcStockHandler, EpcOrderAttrHandler epcOrderAttrHandler, EpcActionLogHandler epcActionLogHandler
    ) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcOrderHandler = epcOrderHandler;
        this.epcStockHandler = epcStockHandler;
        this.epcOrderAttrHandler = epcOrderAttrHandler;
        this.epcActionLogHandler = epcActionLogHandler;
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
//        String warehouse = "";
//        String productCode = "";
        String smcOrderReference = "";
        boolean isValid = true;
        String errMsg = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        String tmpReserveTicket = "";
        String tmpPickupDate = "";
        String tmpIsReserve = "";
//        String tmpOrderChannel = "";
//        EpcCreateTmpReserveResult epcCreateTmpReserveResult = null;
        boolean isItemIdBelongToOrder = false;
//        int availableCount = 0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String todayStr = sdf.format(new java.util.Date()); // today
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
                reserveByThread(custId, smcOrderId, smcOrderReference, itemList, channel, location);

                for (int i = 0; i < itemList.size(); i++) {
                    // check result 

                    epcReserveStockItem = itemList.get(i);

                    tmpPickupDate = epcReserveStockItem.getDeliveryDate();

                    if("Y".equals(epcReserveStockItem.getHasStock())) {
                        // with stock
                        if(todayStr.equals(tmpPickupDate)) {
                            // current store has stock
                            tmpReserveTicket = ""; // no need to create stock ticket
                        } else {
                            tmpReserveTicket = "WITH_STOCK_PA"; // need to create stock ticket, transfer stock, ...
                        }
                    } else if ("N".equals(epcReserveStockItem.getHasStock())) {
                        // no stock, need to make reserve
                        tmpReserveTicket = "NO_STOCK";
                        tmpIsReserve = "Y";
                    } else {
                        // with error
                        throw new Exception(epcReserveStockItem.getReserveNo());
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


    public void reserveByThread(String custId, int smcOrderId, String smcOrderReference, ArrayList<EpcReserveStockItem> itemList, String channel, String location) {
        CompletableFuture<EpcReserveStockItem> future = null;
        ArrayList<CompletableFuture<EpcReserveStockItem>> futureList = new ArrayList<CompletableFuture<EpcReserveStockItem>>();
        CompletableFuture<Void> combinedFuture = null;
//        EpcReserveStockItem epcReserveStockItem = null;

        try {
            for (EpcReserveStockItem item : itemList) {
                future = CompletableFuture.completedFuture(item).thenApplyAsync(s -> reservePerItem(custId, smcOrderId, smcOrderReference, s, channel, location));
                futureList.add(future);
            }

            combinedFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            combinedFuture.get(); // wait for all threads

//            for(CompletableFuture<EpcReserveStockItem> f : futureList) {
//                epcReserveStockItem = f.get();
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void removeTmpTicketByThread(int smcOrderId, String itemId, String tmpReserveId) {
        CompletableFuture<EpcRemoveTmpReserve> future = null;
        String logStr = "[removeTmpTicketByThread][orderId:" + smcOrderId + "][itemId:" + itemId + "][reserveId:" + tmpReserveId + "] ";
        

        try {
            future = CompletableFuture.completedFuture(tmpReserveId).thenApplyAsync(s -> epcStockHandler.removeTmpReserve(s));
            future.whenComplete((result, ex) -> {
                String tmpLogStr = "result:" + result.getResult() + ",errMsg:" + result.getErrMsg();
logger.info("{}{}", logStr, tmpLogStr);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public EpcReserveStockItem reservePerItem(String custId, int smcOrderId, String smcOrderReference, EpcReserveStockItem epcReserveStockItem, String channel, String location) {
        String itemId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcReserveStockItem.getItemId()));
        String productCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcReserveStockItem.getProductCode()));
        String warehouse = epcSecurityHelper.encodeForSQL(epcStockHandler.getWarehouse(productCode));
        EpcCreateTmpReserveResult epcCreateTmpReserveResult = null;
        String tmpReserveTicket = "";
        String tmpPickupDate = "";
//        EpcRemoveTmpReserve epcRemoveTmpReserve = null;
        String tmpErrMsg = "";
        EpcActionLog epcActionLog = null;
        StringBuilder sb = new StringBuilder(2 * 1024 * 1024); // default log size 2MB


        // check availablity of stock
        int availableCount = epcStockHandler.getNoOfAvailableStock(channel, location, warehouse, productCode);
        // end of check availablity of stock

        String tmpOrderChannel = "";
        if(EpcLoginChannel.STORE.equals(channel) && availableCount > 0) {
            tmpOrderChannel = "STORE";
        } else {
            tmpOrderChannel = "WEB";
        }

        String logStr = "[reservePerItem][custId:" + custId + "][orderId:" + smcOrderId + "][itemId:" + itemId + "][productCode:" + productCode + "] ";
        String tmpLogStr = "";

        sb.append(logStr + "\n");

        sb.append("availableCount:" + availableCount + "\n");
        sb.append("orderChannel:" + tmpOrderChannel + "\n");
        sb.append("location:" + location + "\n");

        epcCreateTmpReserveResult = epcStockHandler.createEpcTmpReserve(smcOrderReference, channel, tmpOrderChannel, location, warehouse, productCode);
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

                epcReserveStockItem.setReserveNo("");
                epcReserveStockItem.setHasStock("N");
                epcReserveStockItem.setDeliveryDate("");
            } else {
                epcReserveStockItem.setReserveNo(tmpReserveTicket);
                epcReserveStockItem.setHasStock("Y");
                epcReserveStockItem.setDeliveryDate(tmpPickupDate);

                // remove tmp ticket
//                epcRemoveTmpReserve = epcStockHandler.removeTmpReserve(tmpReserveTicket);
//                tmpLogStr = "  remove tmp ticket " + tmpReserveTicket + " for itemId:" + itemId + ",productCode:" + productCode + ",location:" + location +
//                            ",result:" + epcRemoveTmpReserve.getResult();
//logger.info("{}{}", logStr, tmpLogStr);
//
//                sb.append(tmpLogStr + "\n");
                removeTmpTicketByThread(smcOrderId, itemId, tmpReserveTicket);
                // end of remove tmp ticket
            }

            // save tmp reserve no to log table
            epcOrderAttrHandler.addAttr(smcOrderId, "", itemId, epcOrderAttrHandler.ATTR_TYPE_STOCK_RESERVE_ID, tmpReserveTicket + "," + tmpPickupDate);
            // end of save tmp reserve no to log table
        } else {
            // error
//            throw new Exception(epcSecurityHelper.encode(epcCreateTmpReserveResult.getErrMsg()));
            tmpErrMsg = epcSecurityHelper.encodeForSQL(epcCreateTmpReserveResult.getErrMsg());

            epcReserveStockItem.setHasStock("X");
            epcReserveStockItem.setReserveNo(tmpErrMsg);

            // save tmp reserve no to log table
            epcOrderAttrHandler.addAttr(smcOrderId, "", itemId, epcOrderAttrHandler.ATTR_TYPE_STOCK_RESERVE_ID, tmpErrMsg);
            // end of save tmp reserve no to log table
        }

        epcActionLog = new EpcActionLog();
        epcActionLog.setAction("CHECK_RESERVE");
        epcActionLog.setUri("");
        epcActionLog.setInString(sb.toString());
        epcActionLog.setOutString("");

        epcActionLogHandler.writeApiLogAsync(smcOrderId, epcActionLog);

        return epcReserveStockItem;
    }
}
