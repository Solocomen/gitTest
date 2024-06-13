package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.fes.user.FesUser;
import epc.epcsalesapi.fes.user.FesUserHandler;
import epc.epcsalesapi.helper.EpcActionLogHandler;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcLogOrderStatus;
import epc.epcsalesapi.sales.bean.EpcLogStockStatus;
import epc.epcsalesapi.sales.bean.EpcVoidOrder;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelRedeem;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelRedeemResult;
import epc.epcsalesapi.sales.bean.vms.VmsVoucher2;
import epc.epcsalesapi.sales.bean.vms.redeem.VmsRedeem2;
import epc.epcsalesapi.stock.EpcStockHandler;

@Service
public class EpcVoidOrderHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcVoidOrderHandler.class);

    private final DataSource epcDataSource;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcActionLogHandler epcActionLogHandler;
    private final FesUserHandler fesUserHandler;
    private final EpcStockHandler epcStockHandler;
    private final EpcOrderLogHandler epcOrderLogHandler;
    private final EpcVoucherHandlerNew epcVoucherHandlerNew;
    private final EpcBdayGiftHandler epcBdayGiftHandler;

    public EpcVoidOrderHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper,
            EpcActionLogHandler epcActionLogHandler, FesUserHandler fesUserHandler, EpcStockHandler epcStockHandler,
            EpcOrderLogHandler epcOrderLogHandler, EpcVoucherHandlerNew epcVoucherHandlerNew,
            EpcBdayGiftHandler epcBdayGiftHandler) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcActionLogHandler = epcActionLogHandler;
        this.fesUserHandler = fesUserHandler;
        this.epcStockHandler = epcStockHandler;
        this.epcOrderLogHandler = epcOrderLogHandler;
        this.epcVoucherHandlerNew = epcVoucherHandlerNew;
        this.epcBdayGiftHandler = epcBdayGiftHandler;
    }


    public void voidOrder(EpcVoidOrder epcVoidOrder) {
        int orderId = epcVoidOrder.getOrderId();
        String voidUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getVoidUser()));
        String voidSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getVoidSalesman()));
        String voidLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getVoidLocation()));
        String voidChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcVoidOrder.getVoidChannel()));
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
        boolean isValid = true;
        StringBuilder sb = new StringBuilder(1024); 
        String logStr = "[voidOrder][orderId:" + orderId + "] ";

        try {
            // basic checking
            
            // end of basic checking

            if(isValid) {
                // void fes receipt (order receipt, today)
                // ...
                // end of void fes receipt (order receipt, today)

                // kill all stock reserve ticket 
                cancelStockTicketAsync(orderId, iUserid, iSalesmanid);
                // end of kill all stock reserve ticket 

                // revoke all voucher (redeemed -> assigned / assigned -> init)
                voidVouchersAsync(orderId, voidUser, voidSalesman, voidChannel, voidLocation);
                // end of revoke all voucher (redeemed -> assigned / assigned -> init)

                // revoked b-day coupon 
                voidBDayCouponAsync(orderId, voidUser, voidSalesman, voidChannel, voidLocation);
                // end of revoked b-day coupon 

                // void epc order (place order date = today)
                voidEpcOrderAsync(orderId, voidUser, voidSalesman, voidChannel, voidLocation);
                // end of void epc order (place order date = today)

                epcVoidOrder.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
            } else {
                epcVoidOrder.setResult(EpcApiStatusReturn.RETURN_FAIL);
                epcVoidOrder.setErrMsg(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcVoidOrder.setResult(EpcApiStatusReturn.RETURN_FAIL);
            epcVoidOrder.setErrMsg(e.getMessage());
        } finally {
        }
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


    public void cancelStockTicketAsync(int orderId, int userid, int salesmanId) {
        ArrayList<String> ticketList = getAllTicket(orderId);

        try {
            for (String reserveId : ticketList) {
                CompletableFuture.completedFuture(reserveId).thenApplyAsync(s -> cancelStockTicket(orderId, s, userid, salesmanId));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean cancelStockTicket(int orderId, String reserveId, int userid, int salesmanId) {
        String logStr = "[cancelStockTicket][orderId:" + orderId + "][reserveId:" + reserveId + "] ";

        boolean result = epcStockHandler.cancelRealTicket(orderId, reserveId, userid, salesmanId);
        String tmpLogStr = "result: " + result;
logger.info("{}{}", logStr, tmpLogStr);

        return result;
    }


    public ArrayList<String> getAllTicket(int orderId) {
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<String> ticketList = new ArrayList<>();

        try {
            epcConn = epcDataSource.getConnection();

            sql = "select reserve_id " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and reserve_id is not null ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            while(rset.next()) {
                ticketList.add(StringHelper.trim(rset.getString("reserve_id")));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception e) {}
        }

        return ticketList;
    }
}
