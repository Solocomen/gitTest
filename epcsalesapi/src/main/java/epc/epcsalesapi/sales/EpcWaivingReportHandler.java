package epc.epcsalesapi.sales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcItemCategory;


@Service
public class EpcWaivingReportHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcWaivingReportHandler.class);

    public static final String WAIVE_TYPE_CANCEL = "CANCEL";
    public static final String WAIVE_TYPE_REFUND = "REFUND";
    public static final String WAIVE_TYPE_DOA = "DOA";
    public static final String WAIVE_TYPE_ONSPOT_DISCOUNT = "ONSPOT_DISCOUNT";

    private static final String WAIVE_SYSTEM_EPCSALES = "EPC_SALES";

    private final DataSource epcDataSource;
    private final DataSource fesDataSource;
    private final EpcSecurityHelper epcSecurityHelper;
    
    public EpcWaivingReportHandler(DataSource epcDataSource, DataSource fesDataSource,
            EpcSecurityHelper epcSecurityHelper) {
        this.epcDataSource = epcDataSource;
        this.fesDataSource = fesDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
    }


    /***
     * create a thread to create waiving record
     *  invoked by placeOrder()
     */
    public void createWaivingRecordAsync(String waiveType, String recId) {
        String[] strArray = new String[2];
        strArray[0] = waiveType;
        strArray[1] = recId;

        try {
            CompletableFuture.completedFuture(strArray).thenApplyAsync(s -> createWaivingRecord(s[0], s[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean createWaivingRecord(String waiveType, String recId) {
        Connection epcConn = null;
        boolean isCreate = false;

        try {
            epcConn = epcDataSource.getConnection();
            epcConn.setAutoCommit(false);

            isCreate = createWaivingRecord(epcConn, waiveType, recId);

            epcConn.commit();
        } catch (Exception e) {
            e.printStackTrace();

            try { if(epcConn != null) { epcConn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception e) {}
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception e) {}
        }

        return isCreate;
    }


    public boolean createWaivingRecord(Connection epcConn, String waiveType, String recId) {
        String iWaiveType = epcSecurityHelper.encodeForSQL(StringHelper.trim(waiveType));
        String iRecId = epcSecurityHelper.encodeForSQL(StringHelper.trim(recId));
        String waiveItem = "";
        String waiveBy = "";
        String waiveUserid = "";
        String waiveGrpid = "";
        String waiveByDept = "";
        String waiveByTitle = "";
        BigDecimal waiveAmount = null;
        String waiveReference = "";
        String waiveReason = "";
        String waiveDate = "";
        String referenceId = "";
        String custNum = "";
        String subrNum = "";
        String accountNum = "";
        String referenceSystem = "";
        String handleBy = "";
        String handleSalesman = "";
        String handleLocation = "";
        PreparedStatement pstmtRefund = null;
        PreparedStatement pstmtRefundDetail = null;
        PreparedStatement pstmtEpcItem = null;
        PreparedStatement pstmtCancel = null;
        PreparedStatement pstmtDoa = null;
        PreparedStatement pstmtDiscount = null;
        ResultSet rsetRefund = null;
        ResultSet rsetRefundDetail = null;
        ResultSet rsetEpcItem = null;
        ResultSet rsetCancel = null;
        ResultSet rsetDoa = null;
        ResultSet rsetDiscount = null;
        String sql = "";
        String tmpProductCodeList = "";
        int tmpCancelOrderId = 0;
        String tmpRefundReceipt = "";
        String tmpCancelReceipt = "";
        String tmpOrderReference = "";
        HashMap<String, String> approverInfoMap = null;
        boolean isCreate = false;
        String logStr = "[createWaivingRecord][waiveType:" + iWaiveType + "][recId:" + iRecId + "] ";
        String tmpLogStr = "";

        try {
            tmpLogStr = "start";
logger.info("{}{}", logStr, tmpLogStr);

            if(WAIVE_TYPE_REFUND.equals(iWaiveType)) {
                sql = "select waive_form_code, approve_by, refund_amount, refund_receipt, to_char(create_date, 'yyyymmddhh24miss') as c_date, " +
                      "       create_user, create_salesman " +
                      "  from epc_order_refund a " +
                      " where rec_id = ? " ;
                pstmtRefund = epcConn.prepareStatement(sql);
                pstmtRefund.setString(1, iRecId);
                rsetRefund = pstmtRefund.executeQuery();
                if(rsetRefund.next()) {
                    waiveItem = StringHelper.trim(rsetRefund.getString("waive_form_code"));
                    waiveBy = StringHelper.trim(rsetRefund.getString("approve_by"));
                    tmpRefundReceipt = StringHelper.trim(rsetRefund.getString("refund_receipt"));
                    waiveDate = StringHelper.trim(rsetRefund.getString("c_date"));
                    referenceSystem = WAIVE_SYSTEM_EPCSALES;
                    handleBy = StringHelper.trim(rsetRefund.getString("create_user"));
                    handleSalesman = StringHelper.trim(rsetRefund.getString("create_salesman"));
                }
                rsetRefund.close();
                pstmtRefund.close();

                // get approver info
                approverInfoMap = getApproverInfo(waiveBy);
                waiveUserid = approverInfoMap.get("waiveUserid");
                waiveGrpid = approverInfoMap.get("waiveGrpid");
                waiveByDept = approverInfoMap.get("waiveByDept");
                waiveByTitle = approverInfoMap.get("waiveByTitle");
                // end of get approver info

                handleLocation = getlocation(handleBy);

                sql = "select b.item_code, a.cust_num, a.subr_num, a.acct_num " +
                      "  from epc_order_case a, epc_order_item b " +
                      " where a.order_id = ? " +
                      "   and a.cancel_receipt = ? " +
                      "   and b.order_id = a.order_id " +
                      "   and b.case_id = a.case_id " +
                      "   and item_cat in (?,?,?,?,?,?) ";
                pstmtEpcItem = epcConn.prepareStatement(sql);

                sql = "select cancel_order_id, cancel_receipt_no, refund_amount, c.order_reference " +
                      "  from epc_order_refund_detail a, epc_order c " +
                      " where hdr_id = ? " +
                      "   and c.order_id = a.cancel_order_id ";
                pstmtRefundDetail = epcConn.prepareStatement(sql);
                pstmtRefundDetail.setString(1, iRecId);
                rsetRefundDetail = pstmtRefundDetail.executeQuery();
                while(rsetRefundDetail.next()) {
                    tmpCancelOrderId = rsetRefundDetail.getInt("cancel_order_id");
                    tmpCancelReceipt = StringHelper.trim(rsetRefundDetail.getString("cancel_receipt_no"));
                    waiveAmount = rsetRefundDetail.getBigDecimal("refund_amount");
                    referenceId = StringHelper.trim(rsetRefundDetail.getString("order_reference"));

                    pstmtEpcItem.setInt(1, tmpCancelOrderId);
                    pstmtEpcItem.setString(2, tmpCancelReceipt);
                    pstmtEpcItem.setString(3, EpcItemCategory.DEVICE);
                    pstmtEpcItem.setString(4, EpcItemCategory.APPLECARE);
                    pstmtEpcItem.setString(5, EpcItemCategory.SCREEN_REPLACE);
                    pstmtEpcItem.setString(6, EpcItemCategory.SIM);
                    pstmtEpcItem.setString(7, EpcItemCategory.GIFT_WRAPPING);
                    pstmtEpcItem.setString(8, EpcItemCategory.PLASTIC_BAG);
                    rsetEpcItem = pstmtEpcItem.executeQuery();
                    while(rsetEpcItem.next()) {
                        custNum = StringHelper.trim(rsetEpcItem.getString("cust_num"));
                        subrNum = StringHelper.trim(rsetEpcItem.getString("subr_num"));
                        accountNum = StringHelper.trim(rsetEpcItem.getString("acct_num"));

                        if("".equals(tmpProductCodeList)) {
                            tmpProductCodeList = StringHelper.trim(rsetEpcItem.getString("item_code"));
                        } else {
                            tmpProductCodeList += "/" + StringHelper.trim(rsetEpcItem.getString("item_code"));
                        }

                        waiveReference = tmpRefundReceipt + " " + tmpProductCodeList;

                        // insert waiving report record
                        isCreate = insertWaivingRecord(
                            epcConn, waiveItem, waiveBy, waiveUserid, waiveGrpid,
                            waiveByDept, waiveByTitle, waiveAmount, waiveReference, waiveReason,
                            waiveDate, referenceId, custNum, subrNum, accountNum,
                            referenceSystem, handleBy, handleSalesman, handleLocation
                        );
                        if(!isCreate) {
                            throw new Exception("cannot create waiving report record");
                        }
                        // end of insert waiving report record
                    }
                    rsetEpcItem.close();
                }
                rsetRefundDetail.close();
                pstmtRefundDetail.close();
            } else if(WAIVE_TYPE_CANCEL.equals(iWaiveType)) {
                sql = "select waive_form_code, approve_by, cancel_amount, cancel_receipt_no, to_char(a.create_date, 'yyyymmddhh24miss') as c_date, " +
                      "       a.create_user, a.create_salesman, a.create_location, b.order_reference, b.order_id " +
                      "  from epc_order_cancel a, epc_order b " +
                      " where rec_id = ? " +
                      "   and b.order_id = a.order_id_cancelled " ;
                pstmtCancel = epcConn.prepareStatement(sql);
                pstmtCancel.setString(1, iRecId);
                rsetCancel = pstmtCancel.executeQuery();
                if(rsetCancel.next()) {
                    waiveItem = StringHelper.trim(rsetCancel.getString("waive_form_code"));
                    waiveBy = StringHelper.trim(rsetCancel.getString("approve_by"));
                    waiveAmount = rsetCancel.getBigDecimal("cancel_amount");
                    waiveDate = StringHelper.trim(rsetCancel.getString("c_date"));
                    referenceSystem = WAIVE_SYSTEM_EPCSALES;
                    handleBy = StringHelper.trim(rsetCancel.getString("create_user"));
                    handleSalesman = StringHelper.trim(rsetCancel.getString("create_salesman"));
                    handleLocation = StringHelper.trim(rsetCancel.getString("create_location"));
                    referenceId = StringHelper.trim(rsetCancel.getString("order_reference"));

                    tmpCancelOrderId = rsetCancel.getInt("order_id");
                    tmpCancelReceipt = StringHelper.trim(rsetCancel.getString("cancel_receipt_no"));
                }
                rsetCancel.close();
                pstmtCancel.close();

                tmpLogStr = "cancelOrderId:" + tmpCancelOrderId + ",cancelReceipt:" + tmpCancelReceipt;
logger.info("{}{}", logStr, tmpLogStr);

                // get approver info
                approverInfoMap = getApproverInfo(waiveBy);
                waiveUserid = approverInfoMap.get("waiveUserid");
                waiveGrpid = approverInfoMap.get("waiveGrpid");
                waiveByDept = approverInfoMap.get("waiveByDept");
                waiveByTitle = approverInfoMap.get("waiveByTitle");
                // end of get approver info

                sql = "select b.item_code, a.cust_num, a.subr_num, a.acct_num " +
                      "  from epc_order_case a, epc_order_item b " +
                      " where a.order_id = ? " +
                      "   and a.cancel_receipt = ? " +
                      "   and b.order_id = a.order_id " +
                      "   and b.case_id = a.case_id " +
                      "   and item_cat in (?,?,?,?,?,?) ";
                pstmtEpcItem = epcConn.prepareStatement(sql);

                pstmtEpcItem.setInt(1, tmpCancelOrderId);
                pstmtEpcItem.setString(2, tmpCancelReceipt);
                pstmtEpcItem.setString(3, EpcItemCategory.DEVICE);
                pstmtEpcItem.setString(4, EpcItemCategory.APPLECARE);
                pstmtEpcItem.setString(5, EpcItemCategory.SCREEN_REPLACE);
                pstmtEpcItem.setString(6, EpcItemCategory.SIM);
                pstmtEpcItem.setString(7, EpcItemCategory.GIFT_WRAPPING);
                pstmtEpcItem.setString(8, EpcItemCategory.PLASTIC_BAG);
                rsetEpcItem = pstmtEpcItem.executeQuery();
                while(rsetEpcItem.next()) {
                    custNum = StringHelper.trim(rsetEpcItem.getString("cust_num"));
                    subrNum = StringHelper.trim(rsetEpcItem.getString("subr_num"));
                    accountNum = StringHelper.trim(rsetEpcItem.getString("acct_num"));

                    if("".equals(tmpProductCodeList)) {
                        tmpProductCodeList = StringHelper.trim(rsetEpcItem.getString("item_code"));
                    } else {
                        tmpProductCodeList += "/" + StringHelper.trim(rsetEpcItem.getString("item_code"));
                    }

                    waiveReference = tmpCancelReceipt + " " + tmpProductCodeList;

                    tmpLogStr = "custNum:" + custNum + ",subrNum:" + subrNum + 
                                "accountNum:" + accountNum + ",waiveReference:" + waiveReference;
logger.info("{}{}", logStr, tmpLogStr);

                    // insert waiving report record
                    isCreate = insertWaivingRecord(
                        epcConn, waiveItem, waiveBy, waiveUserid, waiveGrpid,
                        waiveByDept, waiveByTitle, waiveAmount, waiveReference, waiveReason,
                        waiveDate, referenceId, custNum, subrNum, accountNum,
                        referenceSystem, handleBy, handleSalesman, handleLocation
                    );
                    if(!isCreate) {
                        throw new Exception("cannot create waiving report record");
                    }
                    // end of insert waiving report record
                }
                rsetEpcItem.close();
            } else if(WAIVE_TYPE_DOA.equals(iWaiveType)) {
                sql = "select waive_form_code, approve_by, " +
                      "       a.product_code, to_char(a.create_date, 'yyyymmddhh24miss') as c_date, " +
                      "       b.order_reference, create_user, create_salesman " +
                      "  from epc_order_doa_log a, epc_order b " +
                      " where a.rec_id = ? " +
                      "   and a.action_type = ? " +
                      "   and b.order_id = a.order_id ";
                pstmtDoa = epcConn.prepareStatement(sql);
                pstmtDoa.setString(1, iRecId); // rec_id
                pstmtDoa.setString(2, "ADD"); // action_type
                rsetDoa = pstmtDoa.executeQuery();
                if(rsetDoa.next()) {
                    waiveItem = StringHelper.trim(rsetDoa.getString("waive_form_code"));
                    waiveBy = StringHelper.trim(rsetDoa.getString("approve_by"));
                    waiveAmount = null;
                    waiveDate = StringHelper.trim(rsetDoa.getString("c_date"));
                    referenceSystem = WAIVE_SYSTEM_EPCSALES;
                    handleBy = StringHelper.trim(rsetDoa.getString("create_user"));
                    handleSalesman = StringHelper.trim(rsetDoa.getString("create_salesman"));
                    referenceId = StringHelper.trim(rsetDoa.getString("order_reference"));

                    waiveReference = StringHelper.trim(rsetDoa.getString("product_code"));
                }
                rsetDoa.close();
                pstmtDoa.close();

                // get approver info
                approverInfoMap = getApproverInfo(waiveBy);
                waiveUserid = approverInfoMap.get("waiveUserid");
                waiveGrpid = approverInfoMap.get("waiveGrpid");
                waiveByDept = approverInfoMap.get("waiveByDept");
                waiveByTitle = approverInfoMap.get("waiveByTitle");
                // end of get approver info

                handleLocation = getlocation(handleBy);

                custNum = "";
                subrNum = "";
                accountNum = "";

                // insert waiving report record
                isCreate = insertWaivingRecord(
                    epcConn, waiveItem, waiveBy, waiveUserid, waiveGrpid,
                    waiveByDept, waiveByTitle, waiveAmount, waiveReference, waiveReason,
                    waiveDate, referenceId, custNum, subrNum, accountNum,
                    referenceSystem, handleBy, handleSalesman, handleLocation
                );
                if(!isCreate) {
                    throw new Exception("cannot create waiving report record");
                }
                // end of insert waiving report record
            } else if(WAIVE_TYPE_ONSPOT_DISCOUNT.equals(iWaiveType)) {
                sql = "select waive_form_code || discount_form_code as w_code, a.approve_by, " +
                      "       b.item_code || ' discount ' || to_char(discount_percent) || '%' as w_reference, to_char(sysdate, 'yyyymmddhh24miss') as w_date, " +
                      "       c.order_reference, a.handle_user, a.handle_salesman, a.handle_location, " +
                      "       d.cust_num, d.subr_num, d.acct_num, a.discount_amount " +
                      "  from epc_order_charge a, epc_order_item b, epc_order c, epc_order_case d " +
                      " where a.order_id = ? " +
                      "   and (a.waive_form_code is not null or a.discount_form_code is not null) " +
                      "   and a.discount_amount is not null " +
                      "   and b.order_id = a.order_id " +
                      "   and b.item_id = a.parent_item_id " +
                      "   and c.order_id = a.order_id " +
                      "   and d.order_id = a.order_id " +
                      "   and d.case_id = a.case_id ";
                pstmtDiscount = epcConn.prepareStatement(sql);
                pstmtDiscount.setString(1, iRecId); // order_id
                rsetDiscount = pstmtDiscount.executeQuery();
                while(rsetDiscount.next()) {
                    waiveItem = StringHelper.trim(rsetDiscount.getString("w_code"));
                    waiveBy = StringHelper.trim(rsetDiscount.getString("approve_by"));
                    waiveAmount = rsetDiscount.getBigDecimal("discount_amount");
                    waiveDate = StringHelper.trim(rsetDiscount.getString("w_date"));
                    referenceSystem = WAIVE_SYSTEM_EPCSALES;
                    handleBy = StringHelper.trim(rsetDiscount.getString("handle_user"));
                    handleSalesman = StringHelper.trim(rsetDiscount.getString("handle_salesman"));
                    handleLocation = StringHelper.trim(rsetDiscount.getString("handle_location"));
                    referenceId = StringHelper.trim(rsetDiscount.getString("order_reference"));

                    waiveReference = StringHelper.trim(rsetDiscount.getString("w_reference"));

                    custNum = StringHelper.trim(rsetDiscount.getString("cust_num"));
                    subrNum = StringHelper.trim(rsetDiscount.getString("subr_num"));
                    accountNum = StringHelper.trim(rsetDiscount.getString("acct_num"));

                    // get approver info
                    approverInfoMap = getApproverInfo(waiveBy);
                    waiveUserid = approverInfoMap.get("waiveUserid");
                    waiveGrpid = approverInfoMap.get("waiveGrpid");
                    waiveByDept = approverInfoMap.get("waiveByDept");
                    waiveByTitle = approverInfoMap.get("waiveByTitle");
                    // end of get approver info

                    // insert waiving report record
                    isCreate = insertWaivingRecord(
                        epcConn, waiveItem, waiveBy, waiveUserid, waiveGrpid,
                        waiveByDept, waiveByTitle, waiveAmount, waiveReference, waiveReason,
                        waiveDate, referenceId, custNum, subrNum, accountNum,
                        referenceSystem, handleBy, handleSalesman, handleLocation
                    );
                    if(!isCreate) {
                        throw new Exception("cannot create waiving report record");
                    }
                    // end of insert waiving report record
                }
                rsetDiscount.close();
                pstmtDiscount.close();
            }

            isCreate = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rsetRefund != null) { rsetRefund.close(); } } catch (Exception e) {}
            try { if(rsetRefundDetail != null) { rsetRefundDetail.close(); } } catch (Exception e) {}
            try { if(rsetEpcItem != null) { rsetEpcItem.close(); } } catch (Exception e) {}
            try { if(rsetCancel != null) { rsetCancel.close(); } } catch (Exception e) {}
            try { if(rsetDoa != null) { rsetDoa.close(); } } catch (Exception e) {}
            try { if(rsetDiscount != null) { rsetDiscount.close(); } } catch (Exception e) {}
            try { if(pstmtRefund != null) { pstmtRefund.close(); } } catch (Exception e) {}
            try { if(pstmtRefundDetail != null) { pstmtRefundDetail.close(); } } catch (Exception e) {}
            try { if(pstmtEpcItem != null) { pstmtEpcItem.close(); } } catch (Exception e) {}
            try { if(pstmtCancel != null) { pstmtCancel.close(); } } catch (Exception e) {}
            try { if(pstmtDoa != null) { pstmtDoa.close(); } } catch (Exception e) {}
            try { if(pstmtDiscount != null) { pstmtDiscount.close(); } } catch (Exception e) {}
        }

        tmpLogStr = "isCreate:" + isCreate;
logger.info("{}{}", logStr, tmpLogStr);

        return isCreate;
    }


    public boolean insertWaivingRecord(
        Connection epcConn, String waiveItem, String waiveBy, String waiveUserid, String waiveGrpid,
        String waiveByDept, String waiveByTitle, BigDecimal waiveAmount, String waiveReference, String waiveReason,
        String waiveDate, String referenceId, String custNum, String subrNum, String accountNum,
        String referenceSystem, String handleBy, String handleSalesman, String handleLocation
    ) {
        PreparedStatement pstmt = null;
        String sql = "";
        boolean isCreate = false;

        try {
            sql = "insert into epc_waiving_request ( " +
                  "  rec_id, waive_item, waive_by, waive_userid, waive_grpid, " +
                  "  waive_by_dept, waive_by_title, waive_amount, waive_reference, waive_reason, " +
                  "  waive_date, reference_id, cust_num, subr_num, account_num, " +
                  "  reference_system, handle_by, handle_salesman, handle_by_location, create_date "+
                  ") values ( " +
                  "  epc_waiving_request_seq.nextval,?,?,?,?, " +
                  "  ?,?,?,?,?, " +
                  "  to_date(?,'yyyymmddhh24miss'),?,?,?,?, " +
                  "  ?,?,?,?,sysdate " +
                  ") ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setString(1, waiveItem); // waive_item
            pstmt.setString(2, waiveBy); // waive_by
            pstmt.setString(3, waiveUserid); // waive_userid
            pstmt.setString(4, waiveGrpid); // waive_grpid

            pstmt.setString(5, waiveByDept); // waive_by_dept
            pstmt.setString(6, waiveByTitle); // waive_by_title
            pstmt.setBigDecimal(7, waiveAmount); // waive_amount
            pstmt.setString(8, waiveReference); // waive_reference
            pstmt.setString(9, waiveReason); // waive_reason

            pstmt.setString(10, waiveDate); // waive_date
            pstmt.setString(11, referenceId); // reference_id
            pstmt.setString(12, custNum); // cust_num
            pstmt.setString(13, subrNum); // subr_num
            pstmt.setString(14, accountNum); // account_num

            pstmt.setString(15, referenceSystem); // reference_system
            pstmt.setString(16, handleBy); // handle_by
            pstmt.setString(17, handleSalesman); // handle_salesman
            pstmt.setString(18, handleLocation); // handle_location

            pstmt.executeUpdate();

            isCreate = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return isCreate;
    }


    public HashMap<String, String> getApproverInfo(String approverUsername) {
        HashMap<String, String> aMap = new HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            conn = fesDataSource.getConnection();

            sql = "select b.uwa_userid, b.uwa_user_grpid, c.groupname as title, d.groupname as dept " +
                  "  from user_info a, usr_waive_audit b, group_info c, group_info d " +
                  " where a.username = ? " +
                  "   and b.uwa_userid = a.userid " +
                  "   and b.cur_status = ? " +
                  "   and c.groupid = a.groupid " +
                  "   and d.groupid = c.subgroupid ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, approverUsername);
            pstmt.setString(2, "Y");
            rset = pstmt.executeQuery();
            if(rset.next()) {
                aMap.put("waiveUserid", StringHelper.trim(rset.getString("uwa_userid")));
                aMap.put("waiveGrpid", StringHelper.trim(rset.getString("uwa_user_grpid")));
                aMap.put("waiveByTitle", StringHelper.trim(rset.getString("title")));
                aMap.put("waiveByDept", StringHelper.trim(rset.getString("dept")));
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception e) {}
        }
        return aMap;
    }


    public String getlocation(String HandleBy) {
        String location = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            conn = fesDataSource.getConnection();

            sql = "select acct_pos_loc " +
                  "  from user_info a " +
                  " where a.username = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, HandleBy);
            rset = pstmt.executeQuery();
            if(rset.next()) {
                location = StringHelper.trim(rset.getString("acct_pos_loc"));
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception e) {}
        }
        return location;
    }

}
