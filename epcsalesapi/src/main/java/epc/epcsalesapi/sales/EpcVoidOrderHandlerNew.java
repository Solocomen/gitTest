package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.fes.contract.FesContractHandler;
import epc.epcsalesapi.fes.receipt.FesReceiptHandler;
import epc.epcsalesapi.fes.receipt.FesVoidReceipt;
import epc.epcsalesapi.fes.user.FesUser;
import epc.epcsalesapi.fes.user.FesUserHandler;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.img.EpcImgHandler;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import epc.epcsalesapi.sales.bean.EpcDoa;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcLogOrderStatus;
import epc.epcsalesapi.sales.bean.EpcLogStockStatus;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.EpcOrderItem;
import epc.epcsalesapi.sales.bean.EpcOrderItemInfo;
import epc.epcsalesapi.sales.bean.EpcVoidOrder;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelRedeem;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelRedeemResult;
import epc.epcsalesapi.sales.bean.vms.VmsVoucher2;
import epc.epcsalesapi.sales.bean.vms.redeem.VmsRedeem2;
import epc.epcsalesapi.stock.EpcStockHandler;

@Service
public class EpcVoidOrderHandlerNew {
    private final Logger logger = LoggerFactory.getLogger(EpcVoidOrderHandlerNew.class);

    private final DataSource epcDataSource;
    private final DataSource fesDataSource;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcVoucherHandlerNew epcVoucherHandlerNew;
    private final EpcOrderHandler epcOrderHandler;
    private final EpcStockHandler epcStockHandler;
    private final EpcDoaHandler epcDoaHandler;
    private final FesUserHandler fesUserHandler;
    private final EpcOrderLogHandler epcOrderLogHandler;
    private final FesContractHandler fesContractHandler;
    private final EpcBdayGiftHandler epcBdayGiftHandler;
    private final FesReceiptHandler fesReceiptHandler;
    private final EpcImgHandler epcImgHandler;

    public EpcVoidOrderHandlerNew(DataSource epcDataSource, DataSource fesDataSource,
            EpcSecurityHelper epcSecurityHelper, EpcVoucherHandlerNew epcVoucherHandlerNew,
            EpcOrderHandler epcOrderHandler, EpcStockHandler epcStockHandler, EpcDoaHandler epcDoaHandler,
            FesUserHandler fesUserHandler, EpcOrderLogHandler epcOrderLogHandler, FesContractHandler fesContractHandler,
            EpcBdayGiftHandler epcBdayGiftHandler, FesReceiptHandler fesReceiptHandler, EpcImgHandler epcImgHandler) {
        this.epcDataSource = epcDataSource;
        this.fesDataSource = fesDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcVoucherHandlerNew = epcVoucherHandlerNew;
        this.epcOrderHandler = epcOrderHandler;
        this.epcStockHandler = epcStockHandler;
        this.epcDoaHandler = epcDoaHandler;
        this.fesUserHandler = fesUserHandler;
        this.epcOrderLogHandler = epcOrderLogHandler;
        this.fesContractHandler = fesContractHandler;
        this.epcBdayGiftHandler = epcBdayGiftHandler;
        this.fesReceiptHandler = fesReceiptHandler;
        this.epcImgHandler = epcImgHandler;
    }


    public void voidOrder(EpcVoidOrder epcVoidOrder) {
        String custId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getCustId()));
        int orderId = epcVoidOrder.getOrderId();
        String voidUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getVoidUser()));
        String voidSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getVoidSalesman()));
        String voidChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getVoidChannel()));
        String voidLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getVoidLocation()));
        String approveBy = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getApproveBy()));
        String waiveFormCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getWaiveFormCode()));
        String doaLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getDoaLocation()));
        FesUser fesUser = null;
        int iUserid = 0;
        if(!"".equals(voidUser)) {
            fesUser = fesUserHandler.getUserByUsername(voidUser);
            iUserid = fesUser.getUserid();
        }
        int iSalesmanid = 0;
        if(!"".equals(voidSalesman)) {
            fesUser = fesUserHandler.getUserByUsername(voidSalesman);
            iSalesmanid = fesUser.getUserid();
        }
        int iApproverUserid = 0;
        if(!"".equals(approveBy)) {
            fesUser = fesUserHandler.getUserByUsername(approveBy);
            iApproverUserid = fesUser.getUserid();
        }
        EpcOrderInfo epcOrderInfo = null;
        String orderStatus = "";
        ArrayList<EpcOrderItemInfo> itemList = null;
        HashMap<String, EpcDoa> doaInvoiceMap = null;
        Iterator<String> iii = null;
        String tmpDoaInvoiceNo = "";
        EpcDoa epcDoa = null;
        ArrayList<EpcOrderItem> doaItemsList = null;
        EpcOrderItem doaItem = null;
        boolean isCancelRealTicket = false;
        boolean isCurrentDateOrder = false;
        boolean isPartOfOrderAlreadyCancelled = true;
        boolean isValid = true;
        StringBuilder errMsgSB = new StringBuilder();
        String logStr = "[voidOrder][custId:" + custId + "][orderId:" + orderId + "] ";
        String tmpLogStr = "";
        ArrayList<String> transferNoteList = new ArrayList<String>();
        
        try {
            // basic checking

            // check whether order status is allowed to be cancelled
            //  only for PF - pending for fulfill & CO - completed
            epcOrderInfo = epcOrderHandler.getOrderSlimInfo(orderId);
            orderStatus = epcOrderInfo.getOrderStatus();
            if(!"PF".equals(orderStatus) && !"CO".equals(orderStatus)) {
                isValid = false;
                errMsgSB.append("order " + orderId + "[" + orderStatus + "] is not allowed to be cancelled. ");

                tmpLogStr = "orderStatus:" + orderStatus + ", invalid for voiding";
logger.info("{}{}", logStr, tmpLogStr);
            }

            // check whether place order date = today
            isCurrentDateOrder = isCurrentDateOrder(orderId);
            if( ! isCurrentDateOrder ) {
                isValid = false;
                errMsgSB.append("not allow to void non-current date order");
            }
            // end of check whether place order date = today

            // not allow to void order if part of order is already cancelled
            isPartOfOrderAlreadyCancelled = isPartOfOrderAlreadyCancelled(orderId);
            if(isPartOfOrderAlreadyCancelled) {
                isValid = false;
                errMsgSB.append("not allow to void partial/fully cancelled order");
            }
            // end of not allow to void order if part of order is already cancelled

            // end of basic checking
            

            if(isValid) {
                // release stock if stock is not fulfilled
                //  get all items (itemCat=DEVICE / SIM / GIFT_WRAPPING / SCREEN_RELPACE / APPLECARE) under the order
                //  - cancel reservation ticket if not yet fulfilled & with reserve id
                //  - perform stock-in if fulfilled

                doaInvoiceMap = new HashMap<>(); // reset

                itemList = getAllItems(orderId);
                for(EpcOrderItemInfo epcItem : itemList) {
                    tmpLogStr = " proceed [itemId:" + epcItem.getItemId() + "] itemCat:" + epcItem.getItemCat() +
                                ",productCode:" + epcItem.getItemCode() + ",reserveId:" + epcItem.getReserveId() +
                                ",stockStatus:" + epcItem.getStatus() + ",parentItemId:" + epcItem.getParentItemId() + 
                                ",invoiceNo:" + epcItem.getInvoiceNo();
logger.info("{}{}", logStr, tmpLogStr);

                    if("".equals(epcItem.getInvoiceNo()) && epcItem.getStatus().startsWith("P")) {
                        // not yet fulfilled, cancel reserve ticket
                        if(!"".equals(epcItem.getReserveId())) {
                            isCancelRealTicket = epcStockHandler.cancelRealTicket(orderId, epcItem.getReserveId(), iUserid, iSalesmanid);

                            tmpLogStr = "cancel ticket " + epcItem.getReserveId() + ":" + isCancelRealTicket;
logger.info("{}{}", logStr, tmpLogStr);
                        } else {
                            tmpLogStr = "no need to cancel ticket (no ticket) ";
logger.info("{}{}", logStr, tmpLogStr);
                        }
                    } else if(!"".equals(epcItem.getInvoiceNo()) && epcItem.getStatus().startsWith("F")) {
                        // fulfilled
                        doaItem = new EpcOrderItem();
                        doaItem.setItemId(epcItem.getItemId());
                        doaItem.setInvoiceNo(epcItem.getInvoiceNo());
                        doaItem.setWarehouse(epcItem.getWarehouse());
                        doaItem.setProductCode(epcItem.getItemCode());
                        doaItem.setImeiSim(epcItem.getItemValue());
                        doaItem.setParentItemId(epcItem.getParentItemId());
                        doaItem.setNewProductCode("");
                        doaItem.setNewImeiSim("");

                        // group items based on invoice no.
                        if( doaInvoiceMap.containsKey(epcItem.getInvoiceNo()) ) {
                            // contain such invoice
                            epcDoa = doaInvoiceMap.get(epcItem.getInvoiceNo());
                            doaItemsList = epcDoa.getItems();

                            doaItemsList.add(doaItem);
                            doaInvoiceMap.put(epcItem.getInvoiceNo(), epcDoa); // add back to hashmap
                        } else {
                            // NOT contain such invoice yet
                            epcDoa = new EpcDoa();
                            epcDoa.setCustId(custId);
                            epcDoa.setOrderId(orderId);
                            epcDoa.setUser(voidUser);
                            epcDoa.setSalesman(voidSalesman);
                            epcDoa.setChannel(voidChannel);
                            epcDoa.setLocation(voidLocation);
                            epcDoa.setDoaLocation(doaLocation); // to repair location
                            epcDoa.setApproveBy(approveBy);
                            epcDoa.setWaiveFormCode(waiveFormCode);

                            doaItemsList = new ArrayList<>();
                            epcDoa.setItems(doaItemsList);
                            doaItemsList.add(doaItem);

                            doaInvoiceMap.put(epcItem.getInvoiceNo(), epcDoa);
                        }
                        
                        tmpLogStr = "group " + epcItem.getItemId() + " by " + epcItem.getInvoiceNo();
logger.info("{}{}", logStr, tmpLogStr);
                    } else {
                        // error ...
                        tmpLogStr = "invalid condition to proceed ";
logger.info("{}{}", logStr, tmpLogStr);
                    }
                }

                // perform doa (stock in) if any
                if( !doaInvoiceMap.isEmpty() ) {
                    iii = doaInvoiceMap.keySet().iterator();
                    while(iii.hasNext()) {
                        tmpDoaInvoiceNo = iii.next();
                        epcDoa = doaInvoiceMap.get(tmpDoaInvoiceNo);

                        tmpLogStr = tmpDoaInvoiceNo + " -> doa (stock in)";
logger.info("{}{}", logStr, tmpLogStr);

                        epcDoaHandler.doaAndStockIn(epcDoa);
                        
                        if("SUCCESS".equals(epcDoa.getResult())) {
                        	transferNoteList.addAll(epcDoa.getTransferNotes());
                        }

                        tmpLogStr = tmpDoaInvoiceNo + " result:" + epcDoa.getResult() + ",errMsg:" + epcDoa.getErrMsg();
logger.info("{}{}", logStr, tmpLogStr);
                    }
                }
                // end of perform doa (stock in) if any


                //void fes dummy invoice to cancel
                voidFesContractInvoice(orderId, voidLocation, voidUser, iUserid);
                tmpLogStr="void Fes Invoice : Done";
logger.info("{}{}", logStr, tmpLogStr);
                //end of void fes dummy invoice to cancel

                // end of release stock if stock is not fulfilled

                
                // get transfer note
                epcVoidOrder.setTransferNotes(transferNoteList);
                // end of get transfer note


                // void fes receipt (order receipt, today)
                voidFesReceipt(orderId, iUserid, iSalesmanid, iApproverUserid, voidLocation);
                // end of void fes receipt (order receipt, today)
                
                
                // revoke all voucher (redeemed -> assigned / assigned -> init)
                voidVouchersAsync(orderId, voidUser, voidSalesman, voidChannel, voidLocation);
                // end of revoke all voucher (redeemed -> assigned / assigned -> init)


                // revoked b-day coupon 
                voidBDayCouponAsync(orderId, voidUser, voidSalesman, voidChannel, voidLocation);
                // end of revoked b-day coupon 


                // void epc order (place order date = today, include order status / stock status)
                voidEpcOrderAsync(orderId, voidUser, voidSalesman, voidChannel, voidLocation);
                // end of void epc order (place order date = today, include order status / stock status)

                // void all Sales Agreement, Delivery Notes of this order to Imaging system
                epcImgHandler.voidAttachmentToImgAsync(orderId);
                // end of void all Sales Agreement, Delivery Notes of this order to Imaging system
                
                epcVoidOrder.setResult("SUCCESS");
            } else {
                epcVoidOrder.setResult("FAIL");
                epcVoidOrder.setErrMsg(errMsgSB.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcVoidOrder.setResult("FAIL");
            epcVoidOrder.setErrMsg(e.getMessage());
        } finally {
        }
    }


    public void voidFesReceipt(int orderId, int userid, int salesmanid, int approverUserid, String location) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String receiptNo = "";
        FesVoidReceipt fesVoidReceipt = null;
        String logStr = "[voidFesReceipt][orderId:" + orderId + "] ";
        String tmpLogStr = "";
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "select receipt_no " +
                  "  from epc_order_receipt " +
                  " where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            while(rset.next()) {
                receiptNo = StringHelper.trim(rset.getString("receipt_no"));

            // william advice
//i_receipt_no     zz_pvoid_rec.receipt_no%TYPE := <receipt_no>
//i_bin_number     zz_pvoid_rec.bin_number%TYPE := <current_location_code>
//i_void_code1     zz_pvoid_rec.void_code%TYPE := '09'
//i_correct_info1  zz_pvoid_rec.correct_info%TYPE := 'wrong input'
//i_void_code2     zz_pvoid_rec.void_code%TYPE := NULL
//i_correct_info2  zz_pvoid_rec.correct_info%TYPE := NULL
//i_void_code3     zz_pvoid_rec.void_code%TYPE := NULL
//i_correct_info3  zz_pvoid_rec.correct_info%TYPE := NULL
//i_reissued       zz_pvoid_rec.reissued%TYPE := 'N'
//i_ref_no         zz_pvoid_rec.ref_no%TYPE := NULL
//i_nori_code      zz_pvoid_rec.nori_code%TYPE := '1'
//i_nori_desc      zz_pvoid_rec.nori_desc%TYPE := 'Wrong input'
//i_pos_userid     zz_pvoid_rec.user_id%TYPE := <machine_acct_userid>
//i_fes_userid     zz_pvoid_rec.fes_user_id%TYPE := <salesman_userid>
//i_approve_userid zz_pvoid_rec.appr_user_id%TYPE := <approver_userid>

                fesVoidReceipt = new FesVoidReceipt();
                fesVoidReceipt.setReceiptNo(receiptNo);
                fesVoidReceipt.setLocation(location);
                fesVoidReceipt.setVoidCode1("09");
                fesVoidReceipt.setCorrectInfo1("wrong input");
                fesVoidReceipt.setVoidCode2("");
                fesVoidReceipt.setCorrectInfo2("");
                fesVoidReceipt.setVoidCode3("");
                fesVoidReceipt.setCorrectInfo3("");
                fesVoidReceipt.setReissued("N");
                fesVoidReceipt.setRefNo("");
                fesVoidReceipt.setNoriCode("1");
                fesVoidReceipt.setNoriDesc("Wrong input");
                fesVoidReceipt.setUserId(userid);
                fesVoidReceipt.setSalesmanId(salesmanid);
                fesVoidReceipt.setApproverUserid(approverUserid);

                fesReceiptHandler.voidReceipt(fesVoidReceipt);
                tmpLogStr = "receipt no:" + receiptNo;
                if(EpcApiStatusReturn.RETURN_SUCCESS.equals(fesVoidReceipt.getResult())) {
                    tmpLogStr += ", void success, void ref:" + fesVoidReceipt.getVoidRef();
                } else {
                    tmpLogStr += ", void fail, errMsg:" + fesVoidReceipt.getErrMsg();
                }

logger.info("{}{}", logStr, tmpLogStr);
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }


    public ArrayList<EpcOrderItemInfo> getAllItems(int orderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<EpcOrderItemInfo> itemList = new ArrayList<>();
        EpcOrderItemInfo epcOrderItemInfo = null;
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "select * " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and item_cat in (?,?,?,?,?,?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, EpcItemCategory.DEVICE); // item_cat - DEVICE
            pstmt.setString(3, EpcItemCategory.SIM); // item_cat - SIM
            pstmt.setString(4, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
            pstmt.setString(5, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
            pstmt.setString(6, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
            pstmt.setString(7, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG

            rset = pstmt.executeQuery();
            while(rset.next()) {
                epcOrderItemInfo = new EpcOrderItemInfo();

                epcOrderItemInfo.setItemId(StringHelper.trim(rset.getString("item_id")));
                epcOrderItemInfo.setItemCat(StringHelper.trim(rset.getString("item_cat")));
                epcOrderItemInfo.setWarehouse(StringHelper.trim(rset.getString("warehouse")));
                epcOrderItemInfo.setItemCode(StringHelper.trim(rset.getString("item_code")));
                epcOrderItemInfo.setItemValue(StringHelper.trim(rset.getString("cpq_item_value")));
                epcOrderItemInfo.setReserveId(StringHelper.trim(rset.getString("reserve_id")));
                epcOrderItemInfo.setStatus(StringHelper.trim(rset.getString("stock_status")));
                epcOrderItemInfo.setInvoiceNo(StringHelper.trim(rset.getString("invoice_no"))); // for doa
                epcOrderItemInfo.setParentItemId(StringHelper.trim(rset.getString("parent_item_id")));

                itemList.add(epcOrderItemInfo);
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return itemList;
    }


    public void voidEpcOrderAsync(int orderId, String voidUser, String voidSalesman, String voidChannel, String voidLocation) {
        try {
            CompletableFuture.completedFuture(orderId).thenApplyAsync(s -> voidOrderStatus(orderId, voidUser, voidSalesman, voidChannel, voidLocation));

            CompletableFuture.completedFuture(orderId).thenApplyAsync(s -> voidStockStatus(orderId, voidUser, voidSalesman, voidChannel, voidLocation));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean voidOrderStatus(int orderId, String voidUser, String voidSalesman, String voidChannel, String voidLocation) {
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        PreparedStatement getOrderStatusPstmt = null;
        ResultSet rset = null;
        String sql = "";
        EpcLogOrderStatus epcLogOrderStatus = null;
        final String ORDER_STATUS_PF = "PF";
        final String ORDER_STATUS_CO = "CO";
        String oldOrderStatus = "";
        String newOrderStatus = "VOID";

        try {
            epcConn = epcDataSource.getConnection();
            
            // get old status
            sql = "select order_status " +
                  "from epc_order " +
                  "where order_id = ? "+
                  "and order_status IN (?, ?) ";
            getOrderStatusPstmt = epcConn.prepareStatement(sql);
            getOrderStatusPstmt.setInt(1, orderId);
            getOrderStatusPstmt.setString(2, ORDER_STATUS_PF);
            getOrderStatusPstmt.setString(3, ORDER_STATUS_CO);
            rset = getOrderStatusPstmt.executeQuery();
            if (rset.next()) {
                oldOrderStatus = StringHelper.trim(rset.getString("order_status"));
            }
            rset.close();

            // update order status, if any
            if (!"".equals(oldOrderStatus)) {
                sql = "update epc_order " +
                      "   set order_status = ? " +
                      " where order_id = ? " +
                      "  and order_status = ? ";
                pstmt = epcConn.prepareStatement(sql);
                pstmt.setString(1, newOrderStatus); // order_status - VOID
                pstmt.setInt(2, orderId); // order_id
                pstmt.setString(3, oldOrderStatus); // order_status
                pstmt.executeUpdate();

                epcLogOrderStatus = new EpcLogOrderStatus();
                epcLogOrderStatus.setOrderId(orderId);
                epcLogOrderStatus.setOldOrderStatus(oldOrderStatus);
                epcLogOrderStatus.setNewOrderStatus(newOrderStatus);
                epcLogOrderStatus.setCreateUser(voidUser);
                epcLogOrderStatus.setCreateSalesman(voidSalesman);
                epcLogOrderStatus.setCreateChannel(voidChannel);
                epcLogOrderStatus.setCreateLocation(voidLocation);
                epcOrderLogHandler.logOrderStatus(epcConn, epcLogOrderStatus);
                // end of update order status
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception e) {}
            try { if(getOrderStatusPstmt != null) { getOrderStatusPstmt.close(); } } catch (Exception e) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception e) {}
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception e) {}
        }

        return true;
    }


    public boolean voidStockStatus(int orderId, String voidUser, String voidSalesman, String voidChannel, String voidLocation) {
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtUpdate = null;
        ResultSet rset = null;
        String sql = "";
        String oldOrderStatus = "";
        String newOrderStatus = "VOID";
        EpcLogStockStatus epcLogStockStatus = null;
        String itemId = "";

        try {
            epcConn = epcDataSource.getConnection();

            // stock status
            sql = "update epc_order_item " +
                  "   set stock_status = ?, " +
                  "       stock_status_desc = ? " +
                  " where order_id = ? " + 
                  "   and item_id = ? ";
            pstmtUpdate = epcConn.prepareStatement(sql);


            sql = "select item_id, stock_status " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and item_cat in (?,?,?,?,?,?) ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id 
            pstmt.setString(2, EpcItemCategory.DEVICE); // item_cat - DEVICE
            pstmt.setString(3, EpcItemCategory.SIM); // item_cat - SIM
            pstmt.setString(4, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
            pstmt.setString(5, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
            pstmt.setString(6, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
            pstmt.setString(7, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE

            rset = pstmt.executeQuery();
            while(rset.next()) {
                itemId = StringHelper.trim(rset.getString("item_id"));
                oldOrderStatus = StringHelper.trim(rset.getString("stock_status"));

                pstmtUpdate.setString(1, newOrderStatus); // stock_status
                pstmtUpdate.setString(2, "Voided"); // stock_status_desc
                pstmtUpdate.setInt(3, orderId); // order_id
                pstmtUpdate.setString(4, itemId); // item_id
                pstmtUpdate.executeUpdate();

                // create log
                epcLogStockStatus = new EpcLogStockStatus();
                epcLogStockStatus.setOrderId(orderId);
                epcLogStockStatus.setItemId(itemId);
                epcLogStockStatus.setOldStockStatus(oldOrderStatus);
                epcLogStockStatus.setNewStockStatus(newOrderStatus);
                epcOrderLogHandler.logStockStatus(epcConn, epcLogStockStatus);
                // end of create log
            }

            // end of stock status
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception e) {}
        }

        return true;
    }


    public void voidBDayCouponAsync(int orderId, String voidUser, String voidSalesman, String voidChannel, String voidLocation) {
        ArrayList<EpcCancelRedeem> bdayList = getBDayList(orderId, voidUser, voidSalesman, voidChannel, voidLocation);

        try {
            for (EpcCancelRedeem epcCancelRedeem : bdayList) {
                CompletableFuture.completedFuture(epcCancelRedeem).thenApplyAsync(s -> voidBDayCoupon(orderId, s));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<EpcCancelRedeem> getBDayList(int orderId, String voidUser, String voidSalesman, String voidChannel, String voidLocation) {
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<EpcCancelRedeem> bdayList = new ArrayList<>();
        EpcCancelRedeem cancelRedeem = null;
        String caseId = "";

        try {
            epcConn = epcDataSource.getConnection();

            sql = "select case_id, cust_num, subr_num " +
                  "  from epc_order_case " +
                  " where order_id = ? " +
                  "   and cancel_receipt is null ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            while(rset.next()) {
                caseId = StringHelper.trim(rset.getString("case_id"));

                if(epcBdayGiftHandler.hasBirthdayCharge(caseId, epcConn)) {
                    cancelRedeem = new EpcCancelRedeem();
                    cancelRedeem.setCustNum(StringHelper.trim(rset.getString("cust_num")));
                    cancelRedeem.setSubrNum(StringHelper.trim(rset.getString("subr_num")));
                    cancelRedeem.setUserName(voidUser);
                    cancelRedeem.setSalesman(voidSalesman);

                    bdayList.add(cancelRedeem);
                }
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception e) {}
        }

        return bdayList;
    }


    public boolean voidBDayCoupon(int orderId, EpcCancelRedeem cancelRedeem) {
        String custNum = cancelRedeem.getCustNum();
        String subrNum = cancelRedeem.getSubrNum();
        String logStr = "[voidBDayCoupon][orderId:" + orderId + "][custNum:" + custNum + "][subrNum:" + subrNum + "] ";
        String tmpLogStr = "";

        EpcCancelRedeemResult epcCancelRedeemResult = epcBdayGiftHandler.cancelRedeem(cancelRedeem);
        tmpLogStr = "result:" + epcCancelRedeemResult.getResult() +
                    ",errorCode:" + epcCancelRedeemResult.getErrorCode() + 
                    ",errorMessage:" + epcCancelRedeemResult.getErrorMessage();

logger.info("{}{}", logStr, tmpLogStr);
        return true;
    }


    public void voidVouchersAsync(int orderId, String voidUser, String voidSalesman, String voidChannel, String voidLocation) {
        ArrayList<VmsVoucher2> voucherList = getVouchers(orderId);

        try {
            CompletableFuture.completedFuture(voucherList).thenApplyAsync(s -> voidVouchers(orderId, s));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public ArrayList<VmsVoucher2> getVouchers(int orderId) {
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<VmsVoucher2> voucherList = new ArrayList<>();
        VmsVoucher2 vmsVoucher = null;

        try {
            epcConn = epcDataSource.getConnection();

            sql = "select voucher_master_id, assign_id, transaction_id " +
                  "  from epc_order_voucher " +
                  " where order_id = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            while(rset.next()) {
                vmsVoucher = new VmsVoucher2();
                vmsVoucher.setMasterCouponId(StringHelper.trim(rset.getString("voucher_master_id")));
                vmsVoucher.setCouponId(StringHelper.trim(rset.getString("assign_id")));
                vmsVoucher.setTransactionId(StringHelper.trim(rset.getString("transaction_id")));

                voucherList.add(vmsVoucher);
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception e) {}
        }

        return voucherList;
    }


    public boolean voidVouchers(int orderId, ArrayList<VmsVoucher2> voucherList) {
        String logStr = "[voidVouchers][orderId:" + orderId + "] ";
        String tmpLogStr = "";
        VmsVoucher2 vmsVoucher = null;
        StringBuilder sb = new StringBuilder(1024);

        VmsRedeem2 vmsRedeem = new VmsRedeem2();
        vmsRedeem.setForceRemove(true);
        vmsRedeem.setAssignCancel(true);
        vmsRedeem.setRemoveTransactionLog(true);

        ArrayList<VmsVoucher2> vouchers = new ArrayList<>();
        vmsRedeem.setVouchers(vouchers);

        for(VmsVoucher2 v : voucherList) {
            vmsVoucher = new VmsVoucher2();
            vmsVoucher.setTransactionId(v.getTransactionId());
            vouchers.add(vmsVoucher);
        }

        epcVoucherHandlerNew.cancelVmsVoucher(orderId, vmsRedeem, sb);

        tmpLogStr = sb.toString();
logger.info("{}{}", logStr, tmpLogStr);

        return true;
    }


    public void voidFesContractInvoice(int orderId, String location, String userName, int userId) {
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String custNum = "";
        String subrNum = "";
        String cpqOrderId = "";
        String caseId = "";
        List<String> invoiceNoList = null;
        String tmpLogStr = "";
        String logStr= "[voidFesContractInvoice][orderId:" + orderId + "] ";
        
        try {
            epcConn = epcDataSource.getConnection();

            sql = "select a.case_id, a.cust_num, a.subr_num, b.cpq_order_id " +
                  "  from epc_order_case a, epc_order_quote b " +
                  " where a.order_id = ? " +
                  "   and b.order_id = a.order_id " +
                  "   and b.quote_id = a.quote_id ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            while(rset.next()) {
                caseId = StringHelper.trim(rset.getString("case_id"));
                custNum = StringHelper.trim(rset.getString("cust_num"));
                subrNum = StringHelper.trim(rset.getString("subr_num"));
                cpqOrderId = StringHelper.trim(rset.getString("cpq_order_id"));

                invoiceNoList = fesContractHandler.getFesDummyInvoice(cpqOrderId, caseId, custNum, subrNum);
                if (invoiceNoList.size() > 0){
                    fesContractHandler.voidFesInvoice(invoiceNoList,custNum,subrNum,location,userName,userId);

                    tmpLogStr = "void fes invoice for " + custNum + "," + subrNum + " DONE";
                } else {
                    tmpLogStr = "no fes invoice to void for " + custNum + "," + subrNum;
                }
logger.info("{}{}", logStr, tmpLogStr);
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception e) {}
        }
    }


    public boolean isCurrentDateOrder(int orderId) {
        boolean isCurrentDateOrder = false;
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String placeOrderDate = "";
        int cnt = 99;
        String tmpLogStr = "";
        String logStr= "[isCurrentDateOrder][orderId:" + orderId + "] ";
        
        try {
            epcConn = epcDataSource.getConnection();

            sql = "select to_char(place_order_date, 'yyyymmdd') as p_date, trunc(place_order_date) - trunc(sysdate) as p_cnt " +
                  "  from epc_order a " +
                  " where a.order_id = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                placeOrderDate = StringHelper.trim(rset.getString("p_date"));
                cnt = rset.getInt("p_cnt");

                if(cnt == 0) {
                    isCurrentDateOrder = true;
                }
            } rset.close();
            pstmt.close();

            tmpLogStr = "placeOrderDate:" + placeOrderDate + ",isCurrentDateOrder:" + isCurrentDateOrder;
logger.info("{}{}", logStr, tmpLogStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception e) {}
        }

        return isCurrentDateOrder;
    }

    
    public boolean isPartOfOrderAlreadyCancelled(int orderId) {
        boolean isPartOfOrderAlreadyCancelled = false;
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String caseId = "";
        String cancelReceipt = "";
        String cancelDate = "";
        String tmpLogStr = "";
        String logStr= "[isPartOfOrderAlreadyCancelled][orderId:" + orderId + "] ";
        
        try {
            epcConn = epcDataSource.getConnection();

            sql = "select case_id, cancel_receipt, to_char(cancel_date,'yyyymmdd') as c_date " +
                  "  from epc_order_case a " +
                  " where a.order_id = ? " +
                  "   and a.cancel_receipt is not null ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                caseId = StringHelper.trim(rset.getString("p_date"));
                cancelReceipt = StringHelper.trim(rset.getString("cancel_receipt"));
                cancelDate = StringHelper.trim(rset.getString("c_date"));

                isPartOfOrderAlreadyCancelled = true;

                tmpLogStr = "caseId:" + caseId + ",cancelReceipt:" + cancelReceipt + ",cancelDate:" + cancelDate;
logger.info("{}{}", logStr, tmpLogStr);
            } rset.close();
            pstmt.close();

            tmpLogStr = "isPartOfOrderAlreadyCancelled:" + isPartOfOrderAlreadyCancelled;
logger.info("{}{}", logStr, tmpLogStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception e) {}
        }

        return isPartOfOrderAlreadyCancelled;
    }
}
