package epc.epcsalesapi.fes.waiving;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.fes.waiving.bean.EpcCheckWaive;
import epc.epcsalesapi.fes.waiving.bean.EpcCheckWaiveResult;
import epc.epcsalesapi.fes.waiving.bean.EpcChgCheckWaive;
import epc.epcsalesapi.fes.waiving.bean.EpcChgCheckWaiveResult;
import epc.epcsalesapi.fes.waiving.bean.EpcChgWaiveDetail;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.login.EpcLoginHandler;
import epc.epcsalesapi.login.bean.EpcValidateDwpResult;


@Service
public class WaivingHandler {

    private final Logger logger = LoggerFactory.getLogger(WaivingHandler.class);
    
    private final String DIFFERENT_WAIVE_BRANCH = "Different Waive Branch";
    private final String INSUFFICIENT_PRIVILEGE = "Insufficient Privilege of Approver";
    private final String INVALID_DWP = "Invalid Password";
    private final String MISSING_DWP = "Missing Password";
    private final String APPROVER_SUSPEND = "Approver Suspend";
    private final String INVALID_APPROVER = "Invalid Approver";
    
    @Autowired
    private DataSource fesDataSource;
    
    @Autowired
    private EpcLoginHandler epcLoginHandler;
    
    @Autowired
    private EpcSecurityHelper epcSecurityHelper;
    
    // Returns the form type of a user group
    public String getFormType(int groupid) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String formType = "";
        
        try {
            conn = fesDataSource.getConnection();
            sql = "SELECT NVL(typdescclass, 'OTHER') type_desc_class " +
                  "FROM ext_typdesc " +
                  "WHERE typdesctblname = ? " +
                  "AND typdesctypname = ?";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "Waive_Mapping");
            pstmt.setInt(2, groupid);
            rset = pstmt.executeQuery();
            if (rset.next()) {
                formType = StringHelper.trim(rset.getString("type_desc_class"));
            } else {
                formType = "OTHER";
            }
            
            rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw e;
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        return formType;
    }

    // Returns the waive group of a user
	public double getWaiveGroup(int userid) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        double waiveGroup = 0;
        
        try {
            conn = fesDataSource.getConnection();
            sql = "SELECT uwa_waive_grpid " +
                  "FROM usr_waive_audit " +
                  "WHERE uwa_userid = ? " +
                  "AND cur_status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userid);
            pstmt.setString(2, "Y");
            rset = pstmt.executeQuery();
            if (rset.next()) {
                waiveGroup = Double.valueOf(rset.getString("uwa_waive_grpid"));
            } else {
                waiveGroup = -1;
            }
            
            rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw e;
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        return waiveGroup;
    }

    // Returns the waive branch of a user
    public double getWaiveBranch(int userid) throws Exception {
        double waiveBranch = 0;
        
        try {
            waiveBranch = Math.floor(getWaiveGroup(userid) / 10) * 10;
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw e;
        }
        return waiveBranch;
    }

    // Calculate the minimum waive group that can waive a certain amount of a form type
    public double calMinWaiveGroup (String formType, String formCode, BigDecimal price, BigDecimal oldPrice) throws Exception {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        double minWaiveGrpid = 0;
        
        try {
            conn = fesDataSource.getConnection();
            if (price.compareTo(BigDecimal.ZERO) >= 0) {
                sql = "SELECT NVL(MIN(wa_waive_grpid), -1) min_waive_groupid " +
                      "FROM waive_audit " +
                      "WHERE wa_form_type = ? " +
                      "AND wa_form_code = ? " + 
                      "AND cur_status = ? " +
                      "AND ( " +
                      "(wa_waive_amt >= ? - ? OR wa_waive_amt = ?) " +
                      " AND ( (use_percent = ? AND ? * wa_waive_percent / ? >= ? - ?) OR use_percent = ? ) " +
                      ") ";
                 pstmt = conn.prepareStatement(sql);
                 pstmt.setString(1, formType);
                 pstmt.setString(2, formCode);
                 pstmt.setString(3, "Y");
                 pstmt.setBigDecimal(4, oldPrice);
                 pstmt.setBigDecimal(5, price);
                 pstmt.setInt(6, 0);
                 pstmt.setString(7, "Y");
                 pstmt.setBigDecimal(8, oldPrice);
                 pstmt.setDouble(9, 100.0);
                 pstmt.setBigDecimal(10, oldPrice);
                 pstmt.setBigDecimal(11, price);
                 pstmt.setString(12, "N");
            } else {
                sql = "SELECT NVL(MIN(wa_waive_grpid), -1) min_waive_groupid " +
                      "FROM waive_audit " +
                      "WHERE wa_form_type = ? " +
                      "AND wa_form_code = ? " +
                      "AND cur_status = ? " +
                      "AND use_percent = ? " +
                      "AND wa_waive_percent = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, formType);
                pstmt.setString(2, formCode);
                pstmt.setString(3, "Y");
                pstmt.setString(4, "Y");
                pstmt.setDouble(5, 100);
            }
            rset = pstmt.executeQuery();
            if (rset.next()) {
                minWaiveGrpid = Double.valueOf(rset.getString("min_waive_groupid"));
            } else {
            	minWaiveGrpid = -1;
            }
            
            rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw e;
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        return minWaiveGrpid;
    }

    // Check whether a waive group can waive a certain amount of a form type (Y/N/A -- Yes/No/Approver req'd)
    public String allowWaive(double waiveGrpid, String formType, String formCode, BigDecimal price, BigDecimal oldPrice) throws Exception {
        String waive = "";
        double minWaiveGrpid = 0;
        
        try {
            if (waiveGrpid == -1) {
                waive = "N";
            }
            minWaiveGrpid = calMinWaiveGroup(formType, formCode, price, oldPrice);
            if (minWaiveGrpid == -1) {
                waive = "N";
            } else if ( (waiveGrpid >= minWaiveGrpid) && (waiveGrpid <= (Math.floor(minWaiveGrpid / 10) * 10 + 9)) ) {
                waive = "Y";
            } else {
                waive = "A";
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw e;
        }
        return waive;
    }

    public EpcCheckWaiveResult checkWaiveForEPC(EpcCheckWaive epcCheckWaive) {
        EpcCheckWaiveResult result = new EpcCheckWaiveResult();
        try {
            epcCheckWaive.setApproveDwp(EpcCrypto.eGet(epcCheckWaive.getApproveDwp(), "UTF-8"));
            result = checkWaive(epcCheckWaive);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            result.setResult("FAIL");
            result.setErrorMessage("System Error");
        }
        return result;
    }
    
    // Check whether a user/approver can waive a certain amount. If not, the error code is returned
    public EpcCheckWaiveResult checkWaive(EpcCheckWaive epcCheckWaive) {
        EpcCheckWaiveResult result = new EpcCheckWaiveResult();
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        PreparedStatement pstmt4 = null;
        //PreparedStatement pstmt5 = null;
        PreparedStatement pstmtUserid = null;
        ResultSet rset1 = null;
        ResultSet rset2 = null;
        ResultSet rset3 = null;
        ResultSet rset4 = null;
        //ResultSet rset5 = null;

        String sql1 = "";
        String sql2 = "";
        String sql3 = "";
        String sql4 = "";
        String allow = "Y";
        String queryFormType = "";
        int queryWaiveGrpid = 0;
        int userid = 0;
        int loginUserid = 0;
        int waiveUserid = 0;
        String waiveFormCode;
        BigDecimal price;
        BigDecimal oldPrice;
        String approveUsername;
        String approveDwp;
        String checkDwp;
        int approveUserid = 0;

        result.setResult("SUCCESS");
        result.setErrorMessage(null);
        try {
            
            conn = fesDataSource.getConnection();
            
            loginUserid = epcCheckWaive.getLoginUser();
            waiveUserid = epcCheckWaive.getWaiveUserid();
            waiveFormCode = epcSecurityHelper.encodeForSQL(epcCheckWaive.getFormCode());
            price = epcCheckWaive.getPrice();
            oldPrice = epcCheckWaive.getOldPrice();
            approveUsername = epcSecurityHelper.encodeForSQL(epcCheckWaive.getApproveUsername());
            approveDwp = epcSecurityHelper.encodeForSQL(epcCheckWaive.getApproveDwp());
            checkDwp = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcCheckWaive.getCheckDwp()), "Y");

            if (StringHelper.trim(approveUsername).equals("")) {
                userid = waiveUserid;
            } else {
                
                pstmtUserid = conn.prepareStatement(
                                "SELECT userid FROM user_info u " +
                                "WHERE u.username = ? "
                                );
                pstmtUserid.setString(1, approveUsername);
                rset1 = pstmtUserid.executeQuery();
                if (rset1.next()) {
                    approveUserid = rset1.getInt("userid");
                } else {
                    result.setResult("FAIL");
                    result.setErrorMessage(INVALID_APPROVER);
                    return result;
                }
                rset1.close();
                
                userid = approveUserid;
                if (getWaiveBranch(approveUserid) != getWaiveBranch(waiveUserid)) {
                    result.setResult("FAIL");
                    result.setErrorMessage(DIFFERENT_WAIVE_BRANCH);
                    return result;
                }
            }
            
            sql1 = "SELECT 1 FROM user_info u " +
                   "WHERE u.userid = ? " +
                   "AND to_date IS NOT NULL " +
                   "AND to_date <= TRUNC(SYSDATE) ";
            pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setInt(1, userid);
            rset1 = pstmt1.executeQuery();
            if (rset1.next()) {
                result.setResult("FAIL");
                result.setErrorMessage(APPROVER_SUSPEND);
                return result;
            }
            rset1.close();
            
            sql2 = "SELECT 1 FROM user_info u " +
                   "WHERE u.userid = ? " +
                   "AND NOT EXISTS( " +
                       "SELECT 1 FROM user_info " +
                       "WHERE userid = ? " +
                       "AND rbd_unit_id = u.rbd_unit_id) " +
                   "AND NOT EXISTS( " +
                       "SELECT 1 FROM sa_rbd_unit " +
                       "WHERE rbd_unit_id = u.rbd_unit_id " +
                       "AND sales_mgr = ?) " +
                   "AND NOT EXISTS( " +
                       "SELECT 1 FROM user_info " +
                       "WHERE userid IN (?, ?) " +
                       "AND groupid = ?) ";
            pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setInt(1, loginUserid);
            pstmt2.setInt(2, userid);
            pstmt2.setInt(3, userid);
            pstmt2.setInt(4, approveUserid);
            pstmt2.setInt(5, userid);
            pstmt2.setInt(6, 68);
            rset2 = pstmt2.executeQuery();
            while (rset2.next()) {
                result.setResult("FAIL");
                result.setErrorMessage(INSUFFICIENT_PRIVILEGE);
                return result;
            }
            
            sql3 = "SELECT waive_grpid FROM sa_waiver_view WHERE userid = ? ";
            pstmt3 = conn.prepareStatement(sql3);
            pstmt3.setInt(1, userid);
            rset3 = pstmt3.executeQuery();
            if (rset3.next()) {
                queryWaiveGrpid = rset3.getInt("waive_grpid");
            }
            
            sql4 = "SELECT groupid FROM sa_waiver_view WHERE userid = ? ";
            pstmt4 = conn.prepareStatement(sql4);
            pstmt4.setInt(1, userid);
            rset4 = pstmt4.executeQuery();
            if (rset4.next()) {
                queryFormType = getFormType(rset4.getInt("groupid"));
            }
            
            allow = allowWaive(queryWaiveGrpid, queryFormType, waiveFormCode, price, oldPrice);
            
            /*
            sql5 = "SELECT ?, password " +
                   "FROM sa_waiver_view " +
                   "WHERE userid = ? ";
            pstmt5 = conn.prepareStatement(sql5);
            pstmt5.setString(1, queryAllowWaive);
            pstmt5.setInt(2, userid);
            rset5 = pstmt5.executeQuery();
            if (rset5.next()) {
                allow = queryAllowWaive;
                dwp = rset5.getString("password");
            } else {
                allow = "N";
            }
            */

            if (!allow.equals("Y")) {
                result.setResult("FAIL");
                result.setErrorMessage(INSUFFICIENT_PRIVILEGE);
                return result;
            } else if (approveUserid == userid) {
                if (checkDwp.equals("Y") && approveDwp == null) {
                    result.setResult("FAIL");
                    result.setErrorMessage(MISSING_DWP);
                    return result;
                } else if (checkDwp.equals("Y")) {
                    EpcValidateDwpResult epcValidateDwpResult = epcLoginHandler.validateDwp(approveUsername, approveDwp);
                    if (!epcValidateDwpResult.isValid()) {
                        result.setResult("FAIL");
                        result.setErrorMessage(INVALID_DWP);
                        return result;
                    }
                }
            } else {
                result.setErrorMessage(null);
            }
            
            if (rset1 != null) { rset1.close(); }
            if (pstmt1 != null) { pstmt1.close(); }
            if (rset2 != null) { rset2.close(); }
            if (pstmt2 != null) { pstmt2.close(); }
            if (rset3 != null) { rset3.close(); }
            if (pstmt3 != null) { pstmt3.close(); }
            if (rset4 != null) { rset4.close(); }
            if (pstmt4 != null) { pstmt4.close();}
            if (pstmtUserid != null) { pstmtUserid.close(); }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            result.setResult("FAIL");
            result.setErrorMessage("System Error");
        } finally {
            try { if (rset1 != null) { rset1.close(); } } catch (Exception ignore) {}
            try { if (pstmt1 != null) { pstmt1.close(); } } catch (Exception ignore) {}
            try { if (rset2 != null) { rset2.close(); } } catch (Exception ignore) {}
            try { if (pstmt2 != null) { pstmt2.close(); } } catch (Exception ignore) {}
            try { if (rset3 != null) { rset3.close(); } } catch (Exception ignore) {}
            try { if (pstmt3 != null) { pstmt3.close(); } } catch (Exception ignore) {}
            try { if (rset4 != null) { rset4.close(); } } catch (Exception ignore) {}
            try { if (pstmt4 != null) { pstmt4.close(); } } catch (Exception ignore) {}
            try { if (pstmtUserid != null) { pstmtUserid.close(); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        return result;
    }
    
    // Check whether same form type can waive amount
    public EpcChgCheckWaiveResult chgCheckWaive(EpcChgCheckWaive epcChgCheckWaive) {
        EpcChgCheckWaiveResult result = new EpcChgCheckWaiveResult();
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        ResultSet rset1 = null;
        ResultSet rset2 = null;
        ResultSet rset3 = null;
        String sql1 = "";
        String sql2 = "";
        String sql3 = "";
        String formType = "";
        String apprGrp = "";
        String waiveGrp = "";
        String tempWaiveGrp = "";
        EpcChgWaiveDetail epcChgWaiveDetailList = new EpcChgWaiveDetail();
        int len = 0;
        int nCount = 0;
        
        try {
            conn = fesDataSource.getConnection();
            formType = getFormType(epcChgCheckWaive.getGroupid());
            if (StringHelper.trim(epcChgCheckWaive.getWaiveGrp()) != null) {
                waiveGrp = epcChgCheckWaive.getWaiveGrp();
            } else {
                sql1 = "SELECT MIN(typdesctypname) min_typ_desc_typname " +
                       "FROM ext_typdesc " +
                       "WHERE typdesctblname = ? " +
                       "AND typdescclass = TRIM(?) ";
                pstmt1 = conn.prepareStatement(sql1);
                pstmt1.setString(1, "Waive_Group");
                pstmt1.setString(2, formType);
                rset1 = pstmt1.executeQuery();
                if (rset1.next()) {
                    waiveGrp = rset1.getString("min_typ_desc_typname");
                }
            }
            epcChgWaiveDetailList.setResult(1);
            epcChgWaiveDetailList.setMsg(waiveGrp);
            
            result.setResult("SUCCESS");
            result.setErrorMessage("");
            if(formType.equals("BB")) {
                if (epcChgCheckWaive.getFormCode().equals("A1")) {
                    epcChgWaiveDetailList.setResult(0);
                    epcChgWaiveDetailList.setMsg("92");
                    result.setChgWaiveDetailList(epcChgWaiveDetailList);
                    return result;
                } else if (epcChgCheckWaive.getFormCode().equals("BB-1")) {
                    epcChgWaiveDetailList.setResult(1);
                    epcChgWaiveDetailList.setMsg("92");
                    result.setChgWaiveDetailList(epcChgWaiveDetailList);
                    return result;
                } else if (epcChgCheckWaive.getFormCode().equals("BB-2")) {
                    epcChgWaiveDetailList.setResult(1);
                    epcChgWaiveDetailList.setMsg("54");
                    result.setChgWaiveDetailList(epcChgWaiveDetailList);
                    return result;
                } else if (epcChgCheckWaive.getFormCode().equals("B4")) {
                    epcChgWaiveDetailList.setResult(1);
                    epcChgWaiveDetailList.setMsg("54");
                    result.setChgWaiveDetailList(epcChgWaiveDetailList);
                    return result;
                } else if (epcChgCheckWaive.getFormCode().equals("BB-3")) {
                    epcChgWaiveDetailList.setResult(1);
                    epcChgWaiveDetailList.setMsg("102");
                    result.setChgWaiveDetailList(epcChgWaiveDetailList);
                    return result;
                } else if (epcChgCheckWaive.getFormCode().equals("BB-4")) {
                    if (waiveGrp.equals("92")) {
                        epcChgWaiveDetailList.setResult(0);
                        epcChgWaiveDetailList.setMsg("92");
                        result.setChgWaiveDetailList(epcChgWaiveDetailList);
                        return result;
                    } else {
                        epcChgWaiveDetailList.setResult(1);
                        epcChgWaiveDetailList.setMsg("92");
                        result.setChgWaiveDetailList(epcChgWaiveDetailList);
                        return result;
                    }
                } else if (epcChgCheckWaive.getFormCode().equals("BB-5")) {
                    epcChgWaiveDetailList.setResult(1);
                    epcChgWaiveDetailList.setMsg("102");
                    result.setChgWaiveDetailList(epcChgWaiveDetailList);
                    return result;
                } else if (epcChgCheckWaive.getFormCode().equals("BB-6")) {
                    if (waiveGrp.equals("92")) {
                        epcChgWaiveDetailList.setResult(0);
                        epcChgWaiveDetailList.setMsg("92");
                        result.setChgWaiveDetailList(epcChgWaiveDetailList);
                        return result;
                    } else {
                        epcChgWaiveDetailList.setResult(0);
                        epcChgWaiveDetailList.setMsg("92");
                        result.setChgWaiveDetailList(epcChgWaiveDetailList);
                        return result;
                    }
                } else {
                    epcChgWaiveDetailList.setResult(0);
                    epcChgWaiveDetailList.setMsg("0");
                    result.setChgWaiveDetailList(epcChgWaiveDetailList);
                    return result;
                }
            }
            
            if (formType.equals("CA")) {
                if (epcChgCheckWaive.getFormCode().equals("CA-01")) {
                    if (waiveGrp.equals("13")) {
                        epcChgWaiveDetailList.setResult(0);
                        epcChgWaiveDetailList.setMsg("13");
                        result.setChgWaiveDetailList(epcChgWaiveDetailList);
                        return result;
                    } else {
                        epcChgWaiveDetailList.setResult(1);
                        epcChgWaiveDetailList.setMsg("13");
                        result.setChgWaiveDetailList(epcChgWaiveDetailList);
                        return result;
                    }
                }
            }
            
            // Handle waiving for delete IDD case
            if (epcChgCheckWaive.getFormCode().equals("DEL-1")) {
                switch (waiveGrp) {
                    case "4": case "24": case "34": case "44": case "54": case "64": case "74": case "84":
                        epcChgWaiveDetailList.setResult(0);
                        epcChgWaiveDetailList.setMsg(waiveGrp);
                        result.setChgWaiveDetailList(epcChgWaiveDetailList);
                        break;
                    default:
                        len = waiveGrp.length();
                        if (len > 1) {
                            tempWaiveGrp = waiveGrp.substring(1, len - 1) + "4";
                        } else {
                            tempWaiveGrp = "4";
                        }
                        epcChgWaiveDetailList.setResult(1);
                        epcChgWaiveDetailList.setMsg(tempWaiveGrp);
                        result.setChgWaiveDetailList(epcChgWaiveDetailList);
                        break;
                }
                return result;
            }
            
            // Handle waiving license fee
            if (epcChgCheckWaive.getFormCode().equals("LIC-1")) {
                switch (waiveGrp) {
                    case "7": case "17": case "27": case "37": case "47": case "57": case "67": case "77": case "87": case "107":
                        epcChgWaiveDetailList.setResult(0);
                        epcChgWaiveDetailList.setMsg(waiveGrp);
                        result.setChgWaiveDetailList(epcChgWaiveDetailList);
                        break;
                    default:
                        len = waiveGrp.length();
                        if (len > 1) {
                            tempWaiveGrp = waiveGrp.substring(1, len - 1) + "7";
                        } else {
                            tempWaiveGrp = "7";
                        }
                        epcChgWaiveDetailList.setResult(1);
                        epcChgWaiveDetailList.setMsg(tempWaiveGrp);
                        result.setChgWaiveDetailList(epcChgWaiveDetailList);
                        break;
                }
                return result;
            }
            
            sql2 = "SELECT COUNT(*) count_result " +
                   "FROM waive_audit_view " +
                   "WHERE wa_form_type = TRIM(?) " +
                   "AND wa_form_code = TRIM(?) " +
                   "AND (wa_form_class LIKE ? " +
                   " OR wa_form_class LIKE ? " +
                   " OR wa_form_class LIKE ? " +
                   " OR wa_form_class LIKE ?) " +
                   "AND wa_waive_grpid = TRIM(?) " +
                   "AND cur_status = ? " +
                   "AND amt >= ? ";
            pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setString(1, formType);
            pstmt2.setString(2, epcChgCheckWaive.getFormCode());
            pstmt2.setString(3, "%CForm%");
            pstmt2.setString(4, "%SA%");
            pstmt2.setString(5, "%RS%");
            pstmt2.setString(6, "%CB%");
            pstmt2.setString(7, waiveGrp);
            pstmt2.setString(8, "Y");
            pstmt2.setBigDecimal(9, epcChgCheckWaive.getWaiveAmt());
            rset2 = pstmt2.executeQuery();
            if (rset2.next()) {
                nCount = rset2.getInt("count_result");
            }
            
            if (nCount > 0) {
                epcChgWaiveDetailList.setResult(0);
                epcChgWaiveDetailList.setMsg(waiveGrp);
                result.setChgWaiveDetailList(epcChgWaiveDetailList);
                return result;
            }
            
            sql3 = "SELECT wa_waive_grpid " +
                   "FROM waive_audit_view " +
                   "WHERE wa_form_type = ? " +
                   "AND (wa_form_class LIKE ? " +
                   " OR wa_form_class LIKE ? " +
                   " OR wa_form_class LIKE ? " +
                   " OR wa_form_class LIKE ?) " +
                   "AND cur_status = ? " +
                   "AND amt >= ? " +
                   "ORDER BY wa_waive_grpid ";
            pstmt3 = conn.prepareStatement(sql3);
            pstmt3.setString(1, formType);
            pstmt3.setString(2, epcChgCheckWaive.getFormCode());
            pstmt3.setString(3, "%CForm%");
            pstmt3.setString(4, "%SA%");
            pstmt3.setString(5, "%RS%");
            pstmt3.setString(6, "%CB%");
            pstmt3.setString(7, "Y");
            pstmt3.setBigDecimal(8, epcChgCheckWaive.getWaiveAmt());
            rset3 = pstmt3.executeQuery();
            while (rset3.next()) {
                epcChgWaiveDetailList.setResult(1);
                epcChgWaiveDetailList.setMsg(apprGrp);
                result.setChgWaiveDetailList(epcChgWaiveDetailList);
                return result;
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            result.setResult("FAIL");
            result.setErrorMessage("System Error");
            result.setChgWaiveDetailList(null);
        } finally {
            try { if (rset1 != null) { rset1.close(); } } catch (Exception ignore) {}
            try { if (pstmt1 != null) { pstmt1.close(); } } catch (Exception ignore) {}
            try { if (rset2 != null) { rset2.close(); } } catch (Exception ignore) {}
            try { if (pstmt2 != null) { pstmt2.close(); } } catch (Exception ignore) {}
            try { if (rset3 != null) { rset3.close(); } } catch (Exception ignore) {}
            try { if (pstmt3 != null) { pstmt3.close(); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        return result;
    }
}
