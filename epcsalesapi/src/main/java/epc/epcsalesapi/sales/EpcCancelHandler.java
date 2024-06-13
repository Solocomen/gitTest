package epc.epcsalesapi.sales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;

import epc.epcsalesapi.fes.contract.FesContractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.fes.user.FesUser;
import epc.epcsalesapi.fes.user.FesUserHandler;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.img.EpcImgHandler;
import epc.epcsalesapi.sales.bean.AvailableCancelReceiptType;
import epc.epcsalesapi.sales.bean.EpcCancelReceipt;
import epc.epcsalesapi.sales.bean.EpcCharge;
import epc.epcsalesapi.sales.bean.EpcCreateReceipt;
import epc.epcsalesapi.sales.bean.EpcCreateReceiptResult;
import epc.epcsalesapi.sales.bean.EpcDoa;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcLogOrderStatus;
import epc.epcsalesapi.sales.bean.EpcLogStockStatus;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.EpcOrderItem;

import epc.epcsalesapi.sales.bean.EpcOrderItemInfo;
import epc.epcsalesapi.sales.bean.EpcPayment;
import epc.epcsalesapi.sales.bean.cancelOrder.EpcCancelOrder;
import epc.epcsalesapi.sales.bean.receipt.ReceiptCode;
import epc.epcsalesapi.sales.bean.vms.VmsVoucher2;
import epc.epcsalesapi.sales.bean.vms.redeem.VmsRedeem2;
import epc.epcsalesapi.stock.EpcStockHandler;

@Service
public class EpcCancelHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcCancelHandler.class);

    private final DataSource epcDataSource;
    private final DataSource fesDataSource; 
    private final EpcSecurityHelper epcSecurityHelper;
    private EpcVoucherHandlerNew epcVoucherHandlerNew;
    private EpcOrderHandler epcOrderHandler;
    private EpcReceiptHandler epcReceiptHandler;
    private EpcStockHandler epcStockHandler;
    private EpcDoaHandler epcDoaHandler;
    private FesUserHandler fesUserHandler;
    private EpcOrderLogHandler epcOrderLogHandler;
    private EpcWaivingReportHandler epcWaivingReportHandler;
    private EpcImgHandler epcImgHandler;

    @Autowired
    private FesContractHandler fesContractHandler;


    public EpcCancelHandler(
        DataSource epcDataSource, DataSource fesDataSource, EpcSecurityHelper epcSecurityHelper,
        EpcVoucherHandlerNew epcVoucherHandlerNew, EpcOrderHandler epcOrderHandler,
        EpcReceiptHandler epcReceiptHandler, EpcStockHandler epcStockHandler,
        EpcDoaHandler epcDoaHandler, FesUserHandler fesUserHandler,
        EpcOrderLogHandler epcOrderLogHandler, EpcWaivingReportHandler epcWaivingReportHandler, EpcImgHandler epcImgHandler
    ) {
        this.epcDataSource = epcDataSource;
        this.fesDataSource = fesDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcVoucherHandlerNew = epcVoucherHandlerNew;
        this.epcOrderHandler = epcOrderHandler;
        this.epcReceiptHandler = epcReceiptHandler;
        this.epcStockHandler = epcStockHandler;
        this.epcDoaHandler = epcDoaHandler;
        this.fesUserHandler = fesUserHandler;
        this.epcOrderLogHandler = epcOrderLogHandler;
        this.epcWaivingReportHandler = epcWaivingReportHandler;
        this.epcImgHandler = epcImgHandler;
    }


    public BigDecimal getCancelAmount(int orderId, ArrayList<String> caseList) {
        Connection conn = null;
        BigDecimal finalCancelAmount = new BigDecimal(0);
        BigDecimal totalCaseAmount = new BigDecimal(0);
        BigDecimal totalPayAmount = new BigDecimal(0);
        BigDecimal voucherAmountInOrderLevel = new BigDecimal(0);
        BigDecimal totalAmountInPreviousCancelReceipt = new BigDecimal(0);
        BigDecimal totalAmountNonRefundable = new BigDecimal(0);
        BigDecimal totalCap = new BigDecimal(0);
        String logStr = "[getCancelAmount][orderId:" + orderId + "] ";
        String tmpLogStr = "";

        try {
            conn = epcDataSource.getConnection();


            // get cancel charge
            for(String caseId : caseList) {
                totalCaseAmount = totalCaseAmount.add(getCaseTotalCharge(conn, orderId, caseId));
            }

            tmpLogStr = "total cancel charge:" + totalCaseAmount;
logger.info("{}{}", logStr, tmpLogStr);
            // end of get cancel charge


            // get payment
            totalPayAmount = getTotalPayment(conn, orderId);

            tmpLogStr = "total payment:" + totalPayAmount;
logger.info("{}{}", logStr, tmpLogStr);
            // end of get payment


            // get coupon amount (order level)
            voucherAmountInOrderLevel = epcVoucherHandlerNew.getVoucherAmountInOrderLevel(conn, orderId);
            
            tmpLogStr = "voucherAmountInOrderLevel:" + voucherAmountInOrderLevel;
logger.info("{}{}", logStr, tmpLogStr);
            // end of get coupon amount (order level)


            // get previous cancel receipt
            totalAmountInPreviousCancelReceipt = getTotalAmountInPreviousCancelReceipt(conn, orderId);
            
            tmpLogStr = "totalAmountInPreviousCancelReceipt:" + totalAmountInPreviousCancelReceipt;
logger.info("{}{}", logStr, tmpLogStr);
            // end of get previous cancel receipt

            // get non-refundable payment amount
            totalAmountNonRefundable = getTotalNonRefundableAmount(conn, orderId);

            tmpLogStr = "totalAmountNonRefundable:" + totalAmountNonRefundable;
logger.info("{}{}", logStr, tmpLogStr);
            // end of get non-refundable payment amount


            // calc total cap (totalPayAmount - voucherAmountInOrderLevel - totalAmountInPreviousCancelReceipt)
            totalCap = totalPayAmount.subtract(voucherAmountInOrderLevel);
            totalCap = totalCap.subtract(totalAmountInPreviousCancelReceipt);
            totalCap = totalCap.subtract(totalAmountNonRefundable);

            tmpLogStr = "total cap (totalPayAmount - voucherAmountInOrderLevel - totalAmountInPreviousCancelReceipt):" + totalCap;
logger.info("{}{}", logStr, tmpLogStr);
            // end of calc total cap (payment)


            if(totalCap.compareTo(totalCaseAmount) >= 0) {
                // totalCap >= totalCaseAmount
                finalCancelAmount = totalCaseAmount;
                BigDecimal CourierFee=epcOrderHandler.getCourierFee(orderId);
                finalCancelAmount=finalCancelAmount.add(CourierFee);
            } else {
                // totalCap < totalCaseAmount
                finalCancelAmount = totalCap;
            }
            
            // if final cancel amount < 0, assign cancel amount to 0
            if (finalCancelAmount.compareTo(BigDecimal.ZERO) < 0 ) {
                finalCancelAmount = new BigDecimal(0);
            }

            tmpLogStr = "finalCancelAmount:" + finalCancelAmount;
logger.info("{}{}", logStr, tmpLogStr);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return finalCancelAmount;
    }


    public BigDecimal getTotalNonRefundableAmount(Connection conn, int orderId) {
        BigDecimal amount = new BigDecimal(0);
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select nvl(sum(payment_amount),0) " +
                  "  from epc_order_payment " +
                  " where order_id = ? " +
                  "   and payment_code in ( " +
                  "     select key_str1 " +
                  "       from epc_control_tbl " +
                  "      where rec_type = ? " +
                  "        and value_str1 = ? " +
                  "   ) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, "NON_REFUND_PAY_CODE"); // rec_type
            pstmt.setString(3, "A"); // value_str1
            rset = pstmt.executeQuery();
            if(rset.next()) {
                amount = rset.getBigDecimal(1);

                if(amount == null) {
                    amount = new BigDecimal(0);
                }
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return amount;
    }


    public BigDecimal getTotalAmountInPreviousCancelReceipt(Connection conn, int orderId) {
        BigDecimal amount = new BigDecimal(0);
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select nvl(sum(cancel_amount),0) " +
                  "  from epc_order_cancel " +
                  " where order_id_cancelled = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id_cancelled
            rset = pstmt.executeQuery();
            if(rset.next()) {
                amount = rset.getBigDecimal(1);

                if(amount == null) {
                    amount = new BigDecimal(0);
                }
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return amount;
    }


    public BigDecimal getTotalPayment(Connection conn, int orderId) {
        BigDecimal payAmount = new BigDecimal(0);
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select nvl(sum(payment_amount),0) " +
                  "  from epc_order_payment " +
                  " where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                payAmount = rset.getBigDecimal(1);

                if(payAmount == null) {
                    payAmount = new BigDecimal(0);
                }
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return payAmount;
    }


    public BigDecimal getCaseTotalCharge(Connection conn, int orderId, String caseId) {
        BigDecimal cAmount = new BigDecimal(0);
        PreparedStatement pstmt = null;
        PreparedStatement pstmtItem = null;
        ResultSet rset = null;
        ResultSet rsetItem = null;
        String sql = "";
        String tmpParentItemId = "";
        String tmpChargeCode = "";
        BigDecimal tmpChargeAmount = null;
        BigDecimal tmpItemAmount = null;
        BigDecimal tmpVoucherAmountInProductLevel = null;
        BigDecimal voucherAmountInCaseLevel = null;
        HashMap<String, BigDecimal> tmpMap = null;
        Iterator<String> iii = null;
        String logStr = "[getCaseTotalCharge][orderId:" + orderId + "][caseId:" + caseId + "] ";
        String tmpLogStr = "";

        try {
            sql = "select charge_code, charge_amount " +
                  "  from epc_order_charge " +
                  " where order_id = ? " +
                  "   and case_id = ? " +
                  "   and parent_item_id = ? " +
                  "   and paid = ? ";
            pstmtItem = conn.prepareStatement(sql);


            sql = "select distinct parent_item_id " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and case_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, caseId); // case_id
            rset = pstmt.executeQuery();
            while(rset.next()) {
                tmpParentItemId = StringHelper.trim(rset.getString("parent_item_id"));

                tmpItemAmount = new BigDecimal(0); // reset
                tmpMap = new HashMap<>(); // reset

                pstmtItem.setInt(1, orderId); // order_id
                pstmtItem.setString(2, caseId); // case_id
                pstmtItem.setString(3, tmpParentItemId); // parent_item_id
                pstmtItem.setString(4, "Y"); // paid
                rsetItem = pstmtItem.executeQuery();
                while(rsetItem.next()) {
                    tmpChargeCode = StringHelper.trim(rsetItem.getString("charge_code"));
                    tmpChargeAmount = rsetItem.getBigDecimal("charge_amount");

                    //some charges will be existed more than 1 time,i.e.99
                    if(tmpMap.containsKey(tmpChargeCode)){
                        tmpChargeAmount=tmpMap.get(tmpChargeCode).add(tmpChargeAmount);
                    }
                    tmpLogStr = " itemId:" + tmpParentItemId + ",chargeCode:" + tmpChargeCode + ",chargeAmount:" + tmpChargeAmount;
logger.info("{}{}", logStr, tmpLogStr);

                    tmpMap.put(tmpChargeCode, tmpChargeAmount);
                } rsetItem.close();

                if(tmpMap.containsKey("02") && tmpMap.containsKey("99")) {
                    // paid already for reserve fee + remaining amount
                    tmpItemAmount = tmpMap.get("02");
                } else if (!tmpMap.containsKey("02") && tmpMap.containsKey("99")) {
                    // paid reserve fee only
                    tmpItemAmount = tmpMap.get("99");
                } else if (tmpMap.containsKey("02") && !tmpMap.containsKey("99")) {
                    // normal case, no reserve
                    tmpItemAmount = tmpMap.get("02");
                } else {
                    // no action
                }

                // add the rest charge (not in 02, 99)
                iii = tmpMap.keySet().iterator();
                while(iii.hasNext()) {
                    tmpChargeCode = iii.next();
                    if("02".equals(tmpChargeCode) || "99".equals(tmpChargeCode)) {
                        continue;
                    }

                    tmpItemAmount = tmpItemAmount.add(tmpMap.get(tmpChargeCode));
                }
                // end of add the rest charge

                tmpVoucherAmountInProductLevel = epcVoucherHandlerNew.getVoucherAmountInProductLevel(conn, orderId, tmpParentItemId);
                tmpLogStr = " itemId:" + tmpParentItemId + ",voucherAmountInProductLevel:" + tmpVoucherAmountInProductLevel;
logger.info("{}{}", logStr, tmpLogStr);

                tmpItemAmount = tmpItemAmount.subtract(tmpVoucherAmountInProductLevel);
                tmpLogStr = "itemId:" + tmpParentItemId + ",total item charge:" + tmpItemAmount;
logger.info("{}{}", logStr, tmpLogStr);

                cAmount = cAmount.add(tmpItemAmount);
            } rset.close();

            // get case level voucher
            voucherAmountInCaseLevel = epcVoucherHandlerNew.getVoucherAmountInCaseLevel(conn, orderId, caseId);
            tmpLogStr = " voucherAmountInCaseLevel:" + voucherAmountInCaseLevel;
logger.info("{}{}", logStr, tmpLogStr);
            // end of get case level voucher

            // final case charge
            cAmount = cAmount.subtract(voucherAmountInCaseLevel);
            tmpLogStr = "total charge:" + cAmount;
logger.info("{}{}", logStr, tmpLogStr);
            // end of final case charge
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return cAmount;
    }
    


    public ArrayList<EpcCancelReceipt> getAvailableCancelReceipt(String custId,AvailableCancelReceiptType availableCancelType) throws Exception {
        Connection epcConn = null;
        Connection fesConn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtReceipt = null;
        String sql = "";
        String tmpReceiptNo = "";
        BigDecimal tmpCancelAmount = null;
        int tmpCancelOrderId = 0;
        String tmpOrderNo=null;
        String tmpReceiptSubrNum = null;
        String tmpReceiptCustNum = null;
        String tmpReceiptDate = null;
        String tmpReceiptLocation = null;
        BigDecimal REFUND_AMOUNT = BigDecimal.ZERO;
        ArrayList<EpcCancelReceipt> rList = new ArrayList<>();
        EpcCancelReceipt epcCancelReceipt = null;

        try {
            epcConn = epcDataSource.getConnection();
            fesConn = fesDataSource.getConnection();


            
            sql = "SELECT bin_number, cellular, subscriber, to_char(receipt_date, 'YYYY-MM-DD') receipt_date " +
                  "FROM zz_prec_hdr " +
                  "WHERE receipt_no = ? ";
            pstmtReceipt = fesConn.prepareStatement(sql);


            sql = "SELECT A.CANCEL_RECEIPT_NO, A.CANCEL_AMOUNT, A.ORDER_ID_CANCELLED, B.ORDER_REFERENCE,B.PLACE_ORDER_DATE,B.PLACE_ORDER_CHANNEL "
                    + "FROM EPC_ORDER_CANCEL A LEFT JOIN EPC_ORDER B ON A.ORDER_ID_CANCELLED = B.ORDER_ID LEFT JOIN"
                    + " (SELECT DISTINCT CANCEL_RECEIPT_NO AS REFUND_CANCEL_RECEIPT FROM EPC_ORDER_REFUND_DETAIL )d ON d.REFUND_CANCEL_RECEIPT = a.CANCEL_RECEIPT_NO "
                    + "LEFT JOIN EPC_ORDER_PAYMENT c ON c.PAYMENT_CODE = '"+ReceiptCode.RefundPaymentCode+"' AND c.REFERENCE_1 = a.CANCEL_RECEIPT_NO WHERE A.CUST_ID =? AND c.PAYMENT_ID IS NULL";
            if(AvailableCancelReceiptType.payment.equals(availableCancelType)) {
                sql +=" AND d.REFUND_CANCEL_RECEIPT IS NULL";
            }
            if(AvailableCancelReceiptType.refund.equals(availableCancelType)) {
            	sql +=" AND A.REFUND='Y'";
            }
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setString(1, custId); // cust_id
            try(ResultSet rset = pstmt.executeQuery()){
            while(rset.next()) {
                tmpReceiptNo = StringHelper.trim(rset.getString("cancel_receipt_no"));
                tmpCancelAmount = rset.getBigDecimal("cancel_amount");
                if(tmpCancelAmount == null) {
                    tmpCancelAmount = BigDecimal.ZERO;
                }
                tmpCancelOrderId = rset.getInt("order_id_cancelled");
                tmpOrderNo=rset.getString("ORDER_REFERENCE");
                String PLACE_ORDER_DATE=rset.getString("PLACE_ORDER_DATE");
                
                // check whether the cancel receipt is used in other receipt
                REFUND_AMOUNT = BigDecimal.ZERO; // reset
                if(AvailableCancelReceiptType.refund.equals(availableCancelType)) {
                String InvalidReceiptSql = "SELECT SUM(b.REFUND_AMOUNT) AS REFUND_AMOUNT FROM EPC_ORDER_REFUND a JOIN EPC_ORDER_REFUND_DETAIL b ON a.REC_ID =b.HDR_ID WHERE a.IS_DONE <>'R' AND b.CANCEL_RECEIPT_NO=?";
                try(PreparedStatement pstmtInvalidReceipt = epcConn.prepareStatement(InvalidReceiptSql)){
                pstmtInvalidReceipt.setString(1, tmpReceiptNo); // CANCEL_RECEIPT_NO
                try(ResultSet rsetInvalidReceipt = pstmtInvalidReceipt.executeQuery()){
                if(rsetInvalidReceipt.next()) {
                    REFUND_AMOUNT = rsetInvalidReceipt.getBigDecimal(1);
                    if(REFUND_AMOUNT==null)
                    REFUND_AMOUNT = BigDecimal.ZERO;
                }
                }
                }
                }

                if(REFUND_AMOUNT.compareTo(tmpCancelAmount)!=0) {
                    // cancel receipt => valid to use for settle new order
                    
                    // get receipt info
                    
                    tmpReceiptSubrNum = "";
                    tmpReceiptCustNum = "";
                    tmpReceiptDate = "";
                    tmpReceiptLocation = "";
                    
                    pstmtReceipt.setString(1, tmpReceiptNo);
                    try(ResultSet rsetReceipt = pstmtReceipt.executeQuery()){
                    if (rsetReceipt.next()) {
                        tmpReceiptSubrNum = StringHelper.trim(rsetReceipt.getString("cellular"));
                        tmpReceiptCustNum = StringHelper.trim(rsetReceipt.getString("subscriber"));
                        tmpReceiptDate = StringHelper.trim(rsetReceipt.getString("receipt_date"));
                        tmpReceiptLocation = StringHelper.trim(rsetReceipt.getString("bin_number"));
                    }}
                    
                    epcCancelReceipt = new EpcCancelReceipt();
                    epcCancelReceipt.setReceiptNo(tmpReceiptNo);
                    epcCancelReceipt.setCancelAmount(tmpCancelAmount.subtract(REFUND_AMOUNT));
                    epcCancelReceipt.setCancelOrderId(tmpCancelOrderId);
                    epcCancelReceipt.setOrderNo(tmpOrderNo);
                    epcCancelReceipt.setReceiptDate(tmpReceiptDate);
                    epcCancelReceipt.setLocation(tmpReceiptLocation);
                    epcCancelReceipt.setSubrNum(tmpReceiptSubrNum);
                    epcCancelReceipt.setCustNum(tmpReceiptCustNum);
                    epcCancelReceipt.setPlaceOrderDate(PLACE_ORDER_DATE);
                    epcCancelReceipt.setPlaceOrderChannel(rset.getString("PLACE_ORDER_CHANNEL"));

                    
                    if(AvailableCancelReceiptType.refund.equals(availableCancelType)) {
                    try(PreparedStatement pstmtRemark = 
                            epcConn.prepareStatement(
                                    "SELECT a.CREATE_DATE,a.CREATE_USER,a.REJECT_TYPE,a.MESSAGE FROM EPC_ORDER_REFUND_REMARK a JOIN EPC_ORDER_REFUND_DETAIL b ON b.HDR_ID=a.HDR_ID WHERE b.CANCEL_RECEIPT_NO =? ORDER BY a.CREATE_DATE DESC")){
                        pstmtRemark.setString(1, tmpReceiptNo);
                        try(ResultSet rsetRemark=pstmtRemark.executeQuery()){
                            if(rsetRemark.next()) {
                                epcCancelReceipt.setRemarkDate(rsetRemark.getString(1));
                                epcCancelReceipt.setRemarkUser(rsetRemark.getString(2));
                                epcCancelReceipt.setRemarkMessage(rsetRemark.getString(3)+" "+rsetRemark.getString(4));
                            }
                        }
                    }}
                    
                    rList.add(epcCancelReceipt);
                }
                // end of check whether the cancel receipt is used in other receipt
            } 
            }
            
            pstmtReceipt.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception ee) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ee) {}
        }

        return rList;
    }

    public boolean validateCancelReceipt(String cancelReceiptNo, Integer orderId) throws Exception {
        boolean can=false;
        String sql = "SELECT 1 FROM EPC_ORDER_CANCEL A LEFT JOIN EPC_ORDER_REFUND_DETAIL b ON b.CANCEL_RECEIPT_NO = a.CANCEL_RECEIPT_NO LEFT JOIN EPC_ORDER_PAYMENT c ON c.PAYMENT_CODE = '"+ReceiptCode.RefundPaymentCode+"' AND c.REFERENCE_1 = a.CANCEL_RECEIPT_NO WHERE a.CANCEL_RECEIPT_NO=? ";
    
    try(Connection epcConn = epcDataSource.getConnection()){
        if(orderId!=null) {
            sql+=" AND b.DTL_ID IS NULL AND (c.PAYMENT_ID IS NULL OR c.ORDER_ID = ?)";
        }else {
        	sql+=" AND c.PAYMENT_ID IS NULL";
        }
       try(PreparedStatement pstmt=epcConn.prepareStatement(sql)){
        pstmt.setString(1, cancelReceiptNo);
        if(orderId!=null) {
        	pstmt.setInt(2, orderId);
        }
        try(ResultSet result=pstmt.executeQuery()){
        if(result.next())
            can=true;
        }
       }
    } catch (Exception e) {
        e.printStackTrace();
        throw e;
    }
    return can;
    }
    
    public void cancelOrder(EpcCancelOrder epcCancelOrder) {
        String custId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcCancelOrder.getCustId()));
        int orderId = epcCancelOrder.getOrderId();
        ArrayList<String> caseIdList = epcCancelOrder.getCaseIdList();
        String cancelUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcCancelOrder.getCancelUser()));
        String cancelSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcCancelOrder.getCancelSalesman()));
        String cancelChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcCancelOrder.getCancelChannel()));
        String cancelLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcCancelOrder.getCancelLocation()));
        BigDecimal cancelAmount = epcCancelOrder.getCancelAmount();
        String approveBy = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcCancelOrder.getApproveBy()));
        String waiveFormCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcCancelOrder.getWaiveFormCode()));
        String doaLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcCancelOrder.getDoaLocation()));
        FesUser fesUser = null;
        int iUserid = 0;
        if(!"".equals(cancelUser)) {
            fesUser = fesUserHandler.getUserByUsername(cancelUser);
            iUserid = fesUser.getUserid();
        }
        int iSalesmanid = 0;
        if(!"".equals(cancelSalesman)) {
            fesUser = fesUserHandler.getUserByUsername(cancelSalesman);
            iSalesmanid = fesUser.getUserid();
        }
        EpcOrderInfo epcOrderInfo = null;
        String orderStatus = "";
        String orderReceiptNo = "";
        String isCaseValid = "";
        ArrayList<EpcOrderItemInfo> itemList = null;
        ArrayList<EpcCharge> chargeList = null;
        EpcCharge epcCharge = null;
        ArrayList<EpcPayment> paymentList = null;
        EpcPayment epcPayment = null;
        EpcCreateReceipt epcCreateReceipt = null;
        EpcCreateReceiptResult epcCreateReceiptResult = null;
        String cancelReceiptNo = "";
        boolean isMarkCancelReceipt = false;
        boolean isInsertCancelRecord = false;
        boolean isUpdateOrderStatus = false;
        boolean isCancelRealTicket = false;
        HashMap<String, EpcDoa> doaInvoiceMap = null;
        Iterator<String> iii = null;
        String tmpDoaInvoiceNo = "";
        EpcDoa epcDoa = null;
        ArrayList<EpcOrderItem> doaItemsList = null;
        EpcOrderItem doaItem = null;
        boolean isUpdate = false;
        boolean isValid = true;
        StringBuilder errMsgSB = new StringBuilder();
        String logStr = "[cancelOrder][custId:" + custId + "][orderId:" + orderId + "] ";
        String tmpLogStr = "";
        ArrayList<String> transferNoteList = new ArrayList<String>();
        Connection fesConn = null;
        try {
            // basic checking
            if(caseIdList == null || caseIdList.size() == 0) {
                isValid = false;
                errMsgSB.append("case id list is empty. ");
            }
            
            // check whether order status is allowed to be cancelled
            //  only for PF - pending for fulfill & CO - completed
            epcOrderInfo = epcOrderHandler.getOrderSlimInfo(orderId);
            orderStatus = epcOrderInfo.getOrderStatus();
            orderReceiptNo = epcOrderInfo.getReceiptNo();
            if(!"PF".equals(orderStatus) && !"CO".equals(orderStatus)) {
                isValid = false;
                errMsgSB.append("order " + orderId + "[" + orderStatus + "] is not allowed to be cancelled. ");

                tmpLogStr = "orderStatus:" + orderStatus + ", invalid for cancellation";
logger.info("{}{}", logStr, tmpLogStr);
            } else {
                tmpLogStr = "orderStatus:" + orderStatus + ",orderReceiptNo:" + orderReceiptNo;
logger.info("{}{}", logStr, tmpLogStr);
            }


    		if(caseIdList!=null)
            // check whether case id is belonged to the order / the case is already cancelled
            for(String caseId : caseIdList) {
                isCaseValid = epcOrderHandler.isCaseValid(orderId, caseId);


                tmpLogStr = "caseId:" + caseId + " isCaseValid:" + isCaseValid;
logger.info("{}{}", logStr, tmpLogStr);

                if("VALID_CASE".equals(isCaseValid)) {
                    // good / valid case
                } else if("ALREADY_CANCELLED".equals(isCaseValid)) {
                    isValid = false;
                    errMsgSB.append("case " + caseId + " is already cancelled. ");
                } else if ("CASE_NOT_UNDER_ORDER".equals(isCaseValid)) {
                    isValid = false;
                    errMsgSB.append("case " + caseId + " is not found. ");
                } else {
                    isValid = false;
                    errMsgSB.append("case " + caseId + " is invalid, err:" + isCaseValid + ". ");
                }
            }
            // end of basic checking
            

            if(isValid) {
                // release stock if stock is not fulfilled
                //  get all items (itemCat=DEVICE / SIM / GIFT_WRAPPING / SCREEN_RELPACE / APPLECARE) under each case
                //  - cancel reservation ticket if not yet fulfilled & with reserve id
                //  - perform stock-in if fulfilled
            	if(caseIdList!=null)
                for(String caseId : caseIdList) {
                    doaInvoiceMap = new HashMap<>(); // reset

                    itemList = getItemsByCaseId(orderId, caseId);
                    for(EpcOrderItemInfo epcItem : itemList) {
                        tmpLogStr = " proceed [caseId:" + caseId + "][itemId:" + epcItem.getItemId() + "] itemCat:" + epcItem.getItemCat() +
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
                                tmpLogStr = "no need to cancel ticket ";
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
                                epcDoa.setUser(cancelUser);
                                epcDoa.setSalesman(cancelSalesman);
                                epcDoa.setChannel(cancelChannel);
                                epcDoa.setLocation(cancelLocation);
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
                            tmpLogStr = tmpDoaInvoiceNo + " result:" + epcDoa.getResult() + ",errMsg:" + epcDoa.getErrMsg();
                            logger.info("{}{}", logStr, tmpLogStr);
                            
                            if("SUCCESS".equals(epcDoa.getResult())) {
                            	transferNoteList.addAll(epcDoa.getTransferNotes());
                            }else {
                            	throw new Exception(epcDoa.getErrMsg());
                            }

                            
                        }
                    }
                    // end of perform doa (stock in) if any

                    // update stock status to cancel
                    isUpdate = updateStockStatusToCancel(orderId, caseId);
                    tmpLogStr = "update stock status:" + isUpdate;
logger.info("{}{}", logStr, tmpLogStr);
                    // end of update stock status to cancel


                    // remove assigned quota (voucher)
                    epcVoucherHandlerNew.removeAssignedVoucherWhenCancelOrder(orderId, caseId);
                    tmpLogStr = "remove assigned quota: DONE";
logger.info("{}{}", logStr, tmpLogStr);
                    // end of remove assigned quota (voucher)

                    //void fes dummy invoice to cancel
                    this.voidFesInvoice(orderId,caseId,cancelLocation,cancelUser,iUserid);
                    tmpLogStr="void Fes Invoice : Done";
                    logger.info("{}{}",logStr,tmpLogStr);
                    //end of void fes dummy invoice to cancel
                }

    			// end of release stock if stock is not fulfilled
                
                // get transfer note
                epcCancelOrder.setTransferNotes(transferNoteList);
                // end of get transfer note                

                // generate cancel receipt
logger.info("{}{}", logStr, "generate cancel receipt");
                epcCreateReceipt = new EpcCreateReceipt();

                // charges covered the case, except voucher(s)
                tmpLogStr = "cancelAmount:" + cancelAmount;
logger.info("{}{}", logStr, tmpLogStr);
                
                chargeList = new ArrayList<EpcCharge>();
                epcCharge = new EpcCharge();
                epcCharge.setCaseId("");
                epcCharge.setChargeAmount(cancelAmount);
                epcCharge.setChargeCode(ReceiptCode.CancelChargeType);
                epcCharge.setChargeDesc("");
                epcCharge.setChargeDescChi("");
                epcCharge.setMsisdn("");
                chargeList.add(epcCharge);
                
                paymentList = new ArrayList<EpcPayment>();
                epcPayment = new EpcPayment();
                epcPayment.setPaymentCode(ReceiptCode.CancelPaymentCode); // not yet defined, need to check with Fin
                epcPayment.setPaymentAmount(cancelAmount);
                epcPayment.setReference1(orderReceiptNo); // 1st receipt from the order
                epcPayment.setReference2("");
                epcPayment.setCurrencyCode("HKD");
                epcPayment.setCurrencyAmount(new BigDecimal(0));
                epcPayment.setExchangeRate(new BigDecimal(1));
                paymentList.add(epcPayment);

                epcCreateReceipt.setCustId(custId);
                epcCreateReceipt.setOrderId(orderId + "");
                epcCreateReceipt.setLocation(cancelLocation);
                epcCreateReceipt.setCreateUser(cancelUser);
                epcCreateReceipt.setSalesman(cancelSalesman);
                epcCreateReceipt.setCharges(chargeList);
                epcCreateReceipt.setPaymentList(paymentList);
                epcCreateReceipt.setCustNum("");
                epcCreateReceipt.setSubrNum("");
                
                fesConn =fesDataSource.getConnection();
                fesConn.setAutoCommit(false);
                epcCreateReceiptResult = epcReceiptHandler.createReceipt(fesConn,epcCreateReceipt);
                if("SUCCESS".equals(epcCreateReceiptResult.getResult())) {
                    cancelReceiptNo = epcCreateReceiptResult.getReceiptNo();

                    tmpLogStr = "cancelReceiptNo:" + cancelReceiptNo;
logger.info("{}{}", logStr, tmpLogStr);
                } else {
                    // error
                    //  throw exception ???
                    tmpLogStr = "errMsg:" + epcCreateReceiptResult.getErrorMessage();
logger.info("{}{}", logStr, tmpLogStr);
                   throw new Exception(tmpLogStr);
                }
                

                // mark cancel receipt to related case(s) in epc_order_case
                isMarkCancelReceipt = markCancelReceiptToCase(orderId, caseIdList, cancelReceiptNo);

                tmpLogStr = "isMarkCancelReceipt:" + isMarkCancelReceipt;
logger.info("{}{}", logStr, tmpLogStr);
                // end of mark cancel receipt to related case(s) in epc_order_case


                // create cancel record
                isInsertCancelRecord = insertCancelRecord(
                    custId, orderId, cancelReceiptNo, cancelAmount, cancelUser, 
                    cancelSalesman, cancelChannel, cancelLocation, approveBy, waiveFormCode,true
                );

                tmpLogStr = "isInsertCancelRecord:" + isInsertCancelRecord;
logger.info("{}{}", logStr, tmpLogStr);
                // end of create cancel record

                
                // update order status
                isUpdateOrderStatus = updateOrderStatusToCancel(orderId, cancelUser, cancelSalesman, cancelChannel, cancelLocation);

                tmpLogStr = "isUpdateOrderStatus:" + isUpdateOrderStatus;
logger.info("{}{}", logStr, tmpLogStr);
                // end of update order status
                
                // send email to customer ?
                // ...
                // end of send email to customer ?
                
                BigDecimal VoucherAmount= epcVoucherHandlerNew.getVoucherAmount(orderId);
                if(!BigDecimal.ZERO.equals(VoucherAmount)) {
                	tmpLogStr = " VoucherAmount:" + VoucherAmount;
                	logger.info("{}{}", logStr, tmpLogStr);
                	
                	
                        
                        chargeList = new ArrayList<EpcCharge>();
                        epcCharge = new EpcCharge();
                        epcCharge.setCaseId("");
                        epcCharge.setChargeAmount(VoucherAmount);
                        epcCharge.setChargeCode(ReceiptCode.CouponChargeType);
                        epcCharge.setChargeDesc("");
                        epcCharge.setChargeDescChi("");
                        epcCharge.setMsisdn("");
                        chargeList.add(epcCharge);
                        
                        paymentList = new ArrayList<EpcPayment>();
                        epcPayment = new EpcPayment();
                        epcPayment.setPaymentCode(ReceiptCode.CouponPaymentCode); // not yet defined, need to check with Fin
                        epcPayment.setPaymentAmount(VoucherAmount);
                        epcPayment.setReference1(orderReceiptNo); // 1st receipt from the order
                        epcPayment.setReference2("");
                        epcPayment.setCurrencyCode("HKD");
                        epcPayment.setCurrencyAmount(BigDecimal.ZERO);
                        epcPayment.setExchangeRate(BigDecimal.ONE);
                        paymentList.add(epcPayment);

                        epcCreateReceipt = new EpcCreateReceipt();
                        epcCreateReceipt.setCustId(custId);
                        epcCreateReceipt.setOrderId(orderId + "");
                        epcCreateReceipt.setLocation(cancelLocation);
                        epcCreateReceipt.setCreateUser(cancelUser);
                        epcCreateReceipt.setSalesman(cancelSalesman);
                        epcCreateReceipt.setCharges(chargeList);
                        epcCreateReceipt.setPaymentList(paymentList);
                        epcCreateReceipt.setCustNum("");
                        epcCreateReceipt.setSubrNum("");
                        
                        epcCreateReceiptResult = epcReceiptHandler.createReceipt(fesConn,epcCreateReceipt);
                        if("SUCCESS".equals(epcCreateReceiptResult.getResult())) {
                            cancelReceiptNo = epcCreateReceiptResult.getReceiptNo();
                            isInsertCancelRecord = insertCancelRecord(
                                custId, orderId, cancelReceiptNo, VoucherAmount, cancelUser, 
                                cancelSalesman, cancelChannel, cancelLocation, approveBy, waiveFormCode,false
                            );
    
                            tmpLogStr = " isInsertCancelVoucherRecord:" + isInsertCancelRecord;
                            logger.info("{}{}", logStr, tmpLogStr);

                            tmpLogStr = "cancelVoucherReceiptNo:" + cancelReceiptNo;
                            logger.info("{}{}", logStr, tmpLogStr);
                        } else {
                            tmpLogStr = "errMsg:" + epcCreateReceiptResult.getErrorMessage();
                        logger.info("{}{}", logStr, tmpLogStr);
                        throw new Exception(tmpLogStr);
                        }
                }
                
                // void all Sales Agreement, Delivery Notes of this order to Imaging system
                epcImgHandler.voidAttachmentToImgAsync(orderId);
                // end of void all Sales Agreement, Delivery Notes of this order to Imaging system

                // revoke entitlement voucher
                revokeVouchersAsync(orderId);
                // end of revoke entitlement voucher
                
                epcCancelOrder.setResult("SUCCESS");
            } else {
                epcCancelOrder.setResult("FAIL");
                epcCancelOrder.setErrMsg(errMsgSB.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            try { if(fesConn != null) { fesConn.rollback(); } } catch (Exception ee) {}
            epcCancelOrder.setResult("FAIL");
            epcCancelOrder.setErrMsg(e.getMessage());
        } finally {
        	try { if(fesConn != null) { fesConn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ee) {}
        }
    }


    public boolean insertCancelRecord(
        String custId, int orderId, String cancelRecept, BigDecimal cancelAmount, String cancelUser, 
        String cancelSalesman, String cancelChannel, String cancelLocation, String approveBy, String waiveFormCode,
        boolean isRefund
    ) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        boolean isCreateWaivingReportRecord = false;
        int recId = 0;

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            sql = "select epc_order_id_seq.nextval from dual ";
            pstmt = conn.prepareStatement(sql);
            rset = pstmt.executeQuery();
            if(rset.next()) {
                recId = rset.getInt(1);
            } rset.close();
            pstmt.close();

            sql = "insert into epc_order_cancel ( " +
                  "  rec_id, cust_id, order_id_cancelled, cancel_receipt_no, cancel_amount, " +
                  "  create_user, create_salesman, create_channel, approve_by, waive_form_code, " +
                  "  create_location,REFUND, create_date " +
                  ") values ( " +
//                  "  epc_order_id_seq.nextval,?,?,?,?, " +
                  "  ?,?,?,?,?, " + 
                  "  ?,?,?,?,?, " +
                  "  ?,?,sysdate " +
                  ") ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, recId); // rec_id
            pstmt.setString(2, custId); // cust_id
            pstmt.setInt(3, orderId); // order_id_cancelled
            pstmt.setString(4, cancelRecept); // cancel_receipt_no
            pstmt.setBigDecimal(5, cancelAmount); // cancel_amount
            pstmt.setString(6, cancelUser); // create_user
            pstmt.setString(7, cancelSalesman); // create_salesman
            pstmt.setString(8, cancelChannel); // create_channel
            pstmt.setString(9, approveBy); // approve_by
            pstmt.setString(10, waiveFormCode); // waive_form_code
            pstmt.setString(11, cancelLocation); // create_location
            pstmt.setString(12, isRefund?"Y":"N");
            pstmt.executeUpdate();

            if(isRefund)
            // add waiving report record
            isCreateWaivingReportRecord = epcWaivingReportHandler.createWaivingRecord(conn, EpcWaivingReportHandler.WAIVE_TYPE_CANCEL, recId + "");
            // end of add waiving report record


            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return isCreateWaivingReportRecord;
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
                    "       cancel_date = sysdate " +
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


    public boolean updateStockStatusToCancel(int orderId, String caseId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        boolean isUpdate = false;
        String itemId = "";
        String oldStockStatus = "";
        String newStockStatus = "CA";
        EpcLogStockStatus epcLogStockStatus = null;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            sql = "select item_id, stock_status " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and case_id = ? " + 
                  "   and item_cat in (?,?,?,?,?,?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, caseId); // case_id
            pstmt.setString(3, EpcItemCategory.DEVICE); // item_cat - DEVICE
            pstmt.setString(4, EpcItemCategory.SIM); // item_cat - SIM
            pstmt.setString(5, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
            pstmt.setString(6, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
            pstmt.setString(7, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
            pstmt.setString(8, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
            rset = pstmt.executeQuery();
            while(rset.next()) {
                itemId = StringHelper.trim(rset.getString("item_id"));
                oldStockStatus = StringHelper.trim(rset.getString("stock_status"));

                // create log
                epcLogStockStatus = new EpcLogStockStatus();
                epcLogStockStatus.setOrderId(orderId);
                epcLogStockStatus.setItemId(itemId);
                epcLogStockStatus.setOldStockStatus(oldStockStatus);
                epcLogStockStatus.setNewStockStatus(newStockStatus);
                epcOrderLogHandler.logStockStatus(conn, epcLogStockStatus);
                // end of create log
            } rset.close();
            pstmt.close();
            
            sql = "update epc_order_item " +
                  "   set stock_status = ?, " +
                  "       stock_status_desc = ? " +
                  " where order_id = ? " +
                  "   and case_id = ? " + 
                  "   and item_cat in (?,?,?,?,?,?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newStockStatus); // stock_status
            pstmt.setString(2, "Cancelled"); // stock_status_desc
            pstmt.setInt(3, orderId); // order_id
            pstmt.setString(4, caseId); // case_id
            pstmt.setString(5, EpcItemCategory.DEVICE); // item_cat - DEVICE
            pstmt.setString(6, EpcItemCategory.SIM); // item_cat - SIM
            pstmt.setString(7, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
            pstmt.setString(8, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
            pstmt.setString(9, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
            pstmt.setString(10, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG

            pstmt.executeUpdate();
            pstmt.close();
            
            conn.commit();
            
            isUpdate = true;
        } catch (Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(false); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
                
        return isUpdate;
    }


    public boolean updateOrderStatusToCancel(int orderId, String cancelUser, String cancelSalesman, String cancelChannel, String cancelLocation) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        int cnt = 99;
        boolean isUpdate = false;
        String oldOrderStatus = "";
        EpcLogOrderStatus epcLogOrderStatus = null;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "select count(1) " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and item_cat in (?,?,?,?,?,?) " +
                  "   and stock_status not in (?,?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, EpcItemCategory.DEVICE); // item_cat - DEVICE
            pstmt.setString(3, EpcItemCategory.SIM); // item_cat - SIM
            pstmt.setString(4, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
            pstmt.setString(5, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
            pstmt.setString(6, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
            pstmt.setString(7, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
            pstmt.setString(8, "F"); // stock_status - fulfilled
            pstmt.setString(9, "CA"); // stock_status - cancelled
            rset = pstmt.executeQuery();
            if(rset.next()) {
                cnt = rset.getInt(1);
            } rset.close();
            pstmt.close();

            if(cnt == 0) {
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
                // end of get current order status


                sql = "update epc_order " +
                    "   set order_status = ? " +
                    " where order_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "CA"); // order_status
                pstmt.setInt(2, orderId); // order_id
                pstmt.executeUpdate();
                pstmt.close();
                

                // create log
                epcLogOrderStatus = new EpcLogOrderStatus();
                epcLogOrderStatus.setOrderId(orderId);
                epcLogOrderStatus.setOldOrderStatus(oldOrderStatus);
                epcLogOrderStatus.setNewOrderStatus("CA");
                epcLogOrderStatus.setCreateUser(cancelUser);
                epcLogOrderStatus.setCreateSalesman(cancelSalesman);
                epcLogOrderStatus.setCreateChannel(cancelChannel);
                epcLogOrderStatus.setCreateLocation(cancelLocation);
                epcOrderLogHandler.logOrderStatus(conn, epcLogOrderStatus);
                // end of create log
            }

            conn.commit();
            
            isUpdate = true;
        } catch (Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
                
        return isUpdate;
    }


    public ArrayList<EpcOrderItemInfo> getItemsByCaseId(int orderId, String caseId) {
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
                  "   and case_id = ? " + 
                  "   and item_cat in (?,?,?,?,?,?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, caseId); // case_id
            pstmt.setString(3, EpcItemCategory.DEVICE); // item_cat - DEVICE
            pstmt.setString(4, EpcItemCategory.SIM); // item_cat - SIM
            pstmt.setString(5, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
            pstmt.setString(6, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
            pstmt.setString(7, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
            pstmt.setString(8, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG

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

    public void voidFesInvoice(int orderId, String caseId, String location, String userName, int userId) throws Exception{
        String custNum = "";
        String subrNum = "";
        String cpqOrderId = "";
        String tmpLogStr = "";
        //[location:" + location +"][userName:" + userName + "][userId:" + userId +"]
        String logStr= "[voidFesInvoice][orderId:" + orderId + "][caseId:" + caseId + "]";
                String sql = "SELECT a.ORDER_ID ,a.quote_id ,a.CPQ_ORDER_ID,b.SUBR_NUM ,b.CUST_NUM,b.CASE_ID FROM epc_order_quote a\n" +
                "LEFT JOIN epc_order_case b ON a.ORDER_ID = b.ORDER_ID WHERE a.order_id = ? AND b.CASE_ID = ?";


        tmpLogStr="voidFesInvoice start time: "+ LocalDateTime.now();
        logger.info("{}",tmpLogStr);
        try(Connection conn = epcDataSource.getConnection()){
            try(PreparedStatement pstmt = conn.prepareStatement(sql)){
                pstmt.setInt(1,orderId);
                pstmt.setString(2,caseId);

                try(ResultSet rs = pstmt.executeQuery()){
                    while (rs.next()){
                        custNum = rs.getString("CUST_NUM");
                        subrNum = rs.getString("SUBR_NUM");
                        cpqOrderId = rs.getString("CPQ_ORDER_ID");

                        tmpLogStr = " get { custNum: "+custNum+",subrNum: "+subrNum+" }";
                        logger.info("{}{}",logStr,tmpLogStr);

                        List<String> invoiceNoList = fesContractHandler.getFesDummyInvoice(cpqOrderId, caseId, custNum, subrNum);

                        logStr += "[custNum: " + custNum + "][subrNum: " + userName +  "]";
                        tmpLogStr = "invoice included: "+invoiceNoList;
                        logger.info("{}{}",logStr,tmpLogStr);

                        if (invoiceNoList.size() > 0){
                            fesContractHandler.voidFesInvoice(invoiceNoList,custNum,subrNum,location,userName,userId);
                        }

                    }
                }
            }
        }
        tmpLogStr="voidFesInvoice end time: "+LocalDateTime.now();
        logger.info("{}",tmpLogStr);

    }


    public void revokeVouchersAsync(int orderId) {
        ArrayList<VmsVoucher2> voucherList = getVouchers(orderId);

        try {
            CompletableFuture.completedFuture(voucherList).thenApplyAsync(s -> revokeVouchers(orderId, s));
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
                  " where order_id = ? " +
                  "   and assign_redeem = ? " +
                  "   and category = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, epcVoucherHandlerNew.VOUCHER_REDEEM); // assign_redeem
            pstmt.setString(3, epcVoucherHandlerNew.VOUCHER_CATEGORY_ENTITLEMENT); // category
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


    public boolean revokeVouchers(int orderId, ArrayList<VmsVoucher2> voucherList) {
        String logStr = "[revokeVouchers][orderId:" + orderId + "] ";
        String tmpLogStr = "";
        VmsVoucher2 vmsVoucher = null;
        StringBuilder sb = new StringBuilder(1024);

        VmsRedeem2 vmsRedeem = new VmsRedeem2();
        vmsRedeem.setForceRemove(true);
        vmsRedeem.setAssignCancel(false);
        vmsRedeem.setRemoveTransactionLog(false);

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

    public Boolean checkIfTradeInPaymentExistsNotVoid(String orderReference){


        String sql = "select 1 from SA_TI_PR where invoice_no=? and stat in ('O','P')";

        try(Connection fesConn = fesDataSource.getConnection()){
            try(PreparedStatement pstmt = fesConn.prepareStatement(sql);){
                pstmt.setString(1,orderReference);
               try(ResultSet rset = pstmt.executeQuery()){
                   if (rset.next()){
                       return true;
                   }
               }

            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
