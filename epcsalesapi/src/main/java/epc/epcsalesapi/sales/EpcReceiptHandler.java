/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.fes.sa.SaCodeHandler;
import epc.epcsalesapi.fes.sa.bean.ZzPservice;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcCharge;
import epc.epcsalesapi.sales.bean.EpcCreateReceipt;
import epc.epcsalesapi.sales.bean.EpcCreateReceiptResult;
import epc.epcsalesapi.sales.bean.EpcOrderItemInfo;
import epc.epcsalesapi.sales.bean.EpcPayment;
import epc.epcsalesapi.sales.bean.receipt.EpcGetPaymentReceipt;
import epc.epcsalesapi.sales.bean.receipt.EpcGetPaymentReceiptResult;
import epc.epcsalesapi.sales.bean.receipt.EpcReceipt;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
//import java.util.List;
//import javax.persistence.EntityManager;
//import javax.persistence.Persistence;
//import javax.persistence.Query;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author KerryTsang
 */

@Service
public class EpcReceiptHandler {
	
	private final Logger logger = LoggerFactory.getLogger(EpcReceiptHandler.class);
	
	@Autowired
	private DataSource fesDataSource;
	
	@Autowired
	private DataSource epcDataSource;
	
	@Autowired
	private SaCodeHandler saCodeHandler;
	
	@Autowired
	private EpcSecurityHelper epcSecurityHelper;
	
	@Autowired
	private EpcOrderHandler epcOrderHandler;
    
    public EpcCreateReceiptResult createReceipt(EpcCreateReceipt epcCreateReceipt) {
        Connection conn = null;
        EpcCreateReceiptResult epcCreateReceiptResult = new EpcCreateReceiptResult();

        try {
            conn = fesDataSource.getConnection();
            conn.setAutoCommit(false);

            epcCreateReceiptResult = createReceipt(conn, epcCreateReceipt);
            if("SUCCESS".equals(epcCreateReceiptResult.getResult())) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            e.printStackTrace();

            epcCreateReceiptResult.setResult("FAIL");
            epcCreateReceiptResult.setErrorCode("1001");
            epcCreateReceiptResult.setErrorMessage(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcCreateReceiptResult;
    }


    public EpcCreateReceiptResult createReceipt(Connection conn, EpcCreateReceipt epcCreateReceipt) {
        EpcCreateReceiptResult epcCreateReceiptResult = new EpcCreateReceiptResult();
        PreparedStatement pstmt = null;
        PreparedStatement pstmtHdr = null;
        PreparedStatement pstmtChgDtl = null;
        PreparedStatement pstmtPayRef = null;
        PreparedStatement pstmtPInterface = null;
        PreparedStatement pstmtUserInfo = null;
        PreparedStatement pstmtProduct = null;
        ResultSet rset = null;
        String sql = "";
        String orderId = "";
        String custId = "";
        String location = "";
        String createUser = "";
        String salesman = "";
        String receiptNo = "";
        String orderReference = "";
        ArrayList<EpcCharge> charges = null;
        ArrayList<EpcPayment> paymentList = null;
        ArrayList<ZzPservice> resultList = null;
        ArrayList<EpcOrderItemInfo> itemList = null;
        ZzPservice zzPservice = null;
        boolean isValid = true;
        String errMsg = "";
        HashMap<String, String> subIndMap = new HashMap<String, String>();
        BigDecimal totalSubFee = new BigDecimal(0);
        BigDecimal totalCharge = new BigDecimal(0);
        BigDecimal totalPayment = new BigDecimal(0);
        ArrayList<String> tmpCodeList = null;
        HashMap <String, ZzPservice> tmpMap = null;
        String custNum = "";
        String subrNum = "";
        int approverUserid = 0;
        String logStr = "[createReceipt]";
        
        try {
            // prepare statement
            sql = "select rtrim(pos_lib.psmt_gettxref2('S', 'N', prefix)) from zz_binmas where bin_number = ? ";
            pstmt = conn.prepareStatement(sql);
            
            sql = "insert into zz_prec_hdr ( " +
                    "  bin_number, receipt_no, receipt_date, rece_amount, non_sub_fee, " +
                    "  sub_fee, adv_payment, user_id, time_issued, stat, " +
                    "  order_id, cust_id, subscriber, cellular ,ORDER_REFERENCE" +
                    ") values ( " +
                    "  ?,?,trunc(sysdate),?,?, " +
                    "  ?,?,?,to_char(sysdate,'hh24miss'),?, " +
                    "  ?,?,?,?,? " +
                    ") ";
            pstmtHdr = conn.prepareStatement(sql);
            
            sql = "insert into zz_prec_chg_dtl ( " +
                    "  receipt_no, receipt_date, charge_type, sub_ind, amount, " +
                    "  adv_payment, bin_number, charge_desc, charge_desc_chi, case_id, " +
                    "  waive_user, waive_reason, charge_item_id " +
                    ") values ( " +
                    "  ?,trunc(sysdate),?,?,?, " + 
                    "  ?,?,?,?,?, " +
                    "  ?,?,?" +
                    ") ";
            pstmtChgDtl = conn.prepareStatement(sql);
            
            sql = "insert into zz_prec_pay_ref ( " +
                    "  bin_number, receipt_no, receipt_date, payment_code, reference1, " +
                    "  reference2, currency_code, currency_amount, exchange_rate, amount " +
                    ") values ( " +
                    "  ?,?,trunc(sysdate),?,?, " +
                    "  ?,?,?,?,? " +
                    ") ";
            pstmtPayRef = conn.prepareStatement(sql);
            
            sql = "insert into zz_pinterface ( " +
                    "  subscriber, cellular, receipt_no, receipt_date, charge_type, " +
                    "  amount, stat, date_int, time_int " +
                    ") values ( " +
                    "  ?,?,?,trunc(sysdate),?, " +
                    "  ?,?,trunc(sysdate),to_char(sysdate, 'hh24miss') " +
                    ") ";
            pstmtPInterface = conn.prepareStatement(sql);

            sql = "insert into zz_prec_product ( " +
                  "  receipt_no, warehouse, product_code " +
                  ") values ( " +
                  "  ?,?,? " +
                  ") ";
            pstmtProduct = conn.prepareStatement(sql);
            // end of prepare statement
            
            sql = "select userid from user_info where username = ? ";
            pstmtUserInfo = conn.prepareStatement(sql);
            
            orderId = epcSecurityHelper.validateId(epcCreateReceipt.getOrderId());
            custId = epcSecurityHelper.validateId(epcCreateReceipt.getCustId());
            custNum = epcSecurityHelper.validateId(epcCreateReceipt.getCustNum());
            subrNum = epcSecurityHelper.validateId(epcCreateReceipt.getSubrNum());
            location = epcSecurityHelper.validateId(epcCreateReceipt.getLocation());
            charges = epcCreateReceipt.getCharges();
            paymentList = epcCreateReceipt.getPaymentList();
            itemList = epcCreateReceipt.getItemList();
            if(!"".equals(StringHelper.trim(epcCreateReceipt.getSalesman()))) {
                createUser = epcCreateReceipt.getSalesman();
            } else {
                createUser = epcCreateReceipt.getCreateUser();
            }
            
            if (!"".equals(orderId)) {
                orderReference = epcOrderHandler.getOrderReferenceByOrderId(Integer.parseInt(orderId));
            }
            
            logStr += "[orderId:" + orderId + "][custId:" + custId + "] ";
            
            
            // basic checking
            // check charge list
            tmpCodeList = new ArrayList<String>();
            for (EpcCharge epcCharge : charges) {
//System.out.println("YYYYY charge -> " + epcCharge.getChargeCode() + " " + epcCharge.getChargeAmount());
                totalCharge = totalCharge.add(epcCharge.getChargeAmount());
                tmpCodeList.add(epcCharge.getChargeCode());
            }
            
//            q.setParameter("serviceCodes", tmpCodeList);
//            resultList = q.getResultList();
            resultList = saCodeHandler.getCodes(tmpCodeList);
            if(resultList == null) {
                isValid = false;
                errMsg += "charge code(s) is/are not found. ";
            } else {
                tmpMap = new HashMap <String, ZzPservice>();
                for (ZzPservice p: resultList) {
                    tmpMap.put(p.getServiceCode(), p);
                }
                
                for (EpcCharge epcCharge : charges) {
                    if(!tmpMap.containsKey(epcCharge.getChargeCode())) {
                        isValid = false;
                        errMsg += "charge code " + epcCharge.getChargeCode() + " is not found. ";
                    } 
//                    else if (!"A".equals(tmpMap.get(epcCharge.getChargeCode()).getStatusInd())) {
//                        isValid = false;
//                        errMsg += "charge code " + StringHelper.trim(epcCharge.getChargeCode()) + " is not active. ";
//                    } 
                    else {
                        zzPservice = tmpMap.get(StringHelper.trim(epcCharge.getChargeCode()));
                        subIndMap.put(StringHelper.trim(epcCharge.getChargeCode()), zzPservice.getSubInd());
                    }
                }
            }
            // end of check charge list
            
            // check payment list
            tmpCodeList = new ArrayList<String>();
            for (EpcPayment epcPayment : paymentList) {
logger.info(epcSecurityHelper.encode("YYYYY payment -> " + epcPayment.getPaymentCode() + " " + epcPayment.getPaymentAmount()));
                totalPayment = totalPayment.add(epcPayment.getPaymentAmount());
                tmpCodeList.add(epcPayment.getPaymentCode());
            }
            
//            q.setParameter("serviceCodes", tmpCodeList);
//            resultList = q.getResultList();
            resultList = saCodeHandler.getCodes(tmpCodeList);
            if(resultList == null) {
                isValid = false;
                errMsg += "payment code(s) is/are not found. ";
            } else {
                tmpMap = new HashMap <String, ZzPservice>();
                for (ZzPservice p: resultList) {
                    tmpMap.put(p.getServiceCode(), p);
                }
                
                for (EpcPayment epcPayment : paymentList) {
                    if(!tmpMap.containsKey(epcPayment.getPaymentCode())) {
                        isValid = false;
                        errMsg += "payment code " + epcPayment.getPaymentCode() + " is not found. ";
                    } 
//                    else if (!"A".equals(tmpMap.get(epcPayment.getPaymentCode()).getStatusInd())) {
//                        isValid = false;
//                        errMsg += "payment code " + StringHelper.trim(epcPayment.getPaymentCode()) + " is not active. ";
//                    }
                }
            }
            // end of check payment list
            
            // check total charges vs total payment
            if(totalCharge.compareTo(totalPayment) == 1) {
                // total charge > total payment
                isValid = false;
                errMsg += "total charge is greater than total payment. ";
            }
            // end of check total charges vs total payment

            // end of basic checking
            
            if(isValid) {
                // generate receipt no
                pstmt.setString(1, location); // bin_number
                rset = pstmt.executeQuery();
                if(rset.next()) {
                    receiptNo = epcSecurityHelper.validateId(StringHelper.trim(rset.getString(1)));
                } rset.close();
                // end of generate receipt no
                
                // create zz_prec_hdr
                pstmtHdr.setString(1, location); // bin_number
                pstmtHdr.setString(2, receiptNo); // receipt_no
                pstmtHdr.setBigDecimal(3, new BigDecimal(0)); // rece_amount
                pstmtHdr.setBigDecimal(4, new BigDecimal(0)); // non_sub_fee
                pstmtHdr.setBigDecimal(5, new BigDecimal(0)); // sub_fee
                pstmtHdr.setBigDecimal(6, new BigDecimal(0)); // adv_payment
                pstmtHdr.setString(7, createUser); // user_id
                pstmtHdr.setString(8, "N"); // stat
                pstmtHdr.setString(9, orderId); // order_id
                pstmtHdr.setString(10, custId); // cust_id
                pstmtHdr.setString(11, custNum); // subscriber
                pstmtHdr.setString(12, subrNum); // cellular
                pstmtHdr.setString(13, orderReference);//OrderReference
                pstmtHdr.executeUpdate();
                // end of create zz_prec_hdr
                
                // create zz_prec_chg_dtl
                for (EpcCharge epcCharge : charges) {
                    String subInd = StringHelper.trim((String)subIndMap.get(epcCharge.getChargeCode()));

                    pstmtChgDtl.setString(1, receiptNo); // receipt_no
                    pstmtChgDtl.setString(2, epcCharge.getChargeCode()); // charge_type
                    pstmtChgDtl.setString(3, subInd); // sub_ind
                    pstmtChgDtl.setBigDecimal(4, epcCharge.getChargeAmount()); // amount
                    pstmtChgDtl.setString(5, ""); // adv_payment
                    pstmtChgDtl.setString(6, location); // bin_number
                    pstmtChgDtl.setString(7, epcCharge.getChargeDesc()); // charge_desc
                    pstmtChgDtl.setString(8, epcCharge.getChargeDescChi()); // charge_desc_chi
                    pstmtChgDtl.setString(9, epcCharge.getCaseId()); // case_id
                    if ("Y".equals(epcCharge.getWaived())) {
                        
                        pstmtUserInfo.setString(1, epcCharge.getApproveBy());
                        rset = pstmtUserInfo.executeQuery();
                        if (rset.next()) {
                            approverUserid = rset.getInt("userid");
                        } else {
                            approverUserid = 0;
                        }
                        rset.close();
                        pstmtChgDtl.setInt(10, approverUserid); // waive_user
                        pstmtChgDtl.setString(11,  epcCharge.getWaiveReason()); // waive_reason
                        
                    } else {
                        pstmtChgDtl.setNull(10, Types.INTEGER); // waive_user
                        pstmtChgDtl.setNull(11, Types.VARCHAR); // waive_reason
                    }
                    pstmtChgDtl.setString(12, epcCharge.getItemId()); // charge_item_id
                    pstmtChgDtl.addBatch();
                    
                    if("Y".equals(subInd)) {
                        totalSubFee = totalSubFee.add(epcCharge.getChargeAmount());
                        
                        pstmtPInterface.setString(1, custNum); // subscriber
                        pstmtPInterface.setString(2, subrNum); // cellular
                        pstmtPInterface.setString(3, receiptNo); // receipt_no
                        pstmtPInterface.setString(4, epcCharge.getChargeCode()); // charge_type
                        pstmtPInterface.setBigDecimal(5, epcCharge.getChargeAmount()); // amount
                        pstmtPInterface.setString(6, "N"); // stat
                        pstmtPInterface.executeUpdate();
                    }
                }
                pstmtChgDtl.executeBatch();
                // end of create zz_prec_chg_dtl
                
                // create zz_prec_pay_ref
                for (EpcPayment epcPayment : paymentList) {
                    pstmtPayRef.setString(1, location); // bin_number
                    pstmtPayRef.setString(2, receiptNo); // receipt_no
                    pstmtPayRef.setString(3, epcPayment.getPaymentCode()); // payment_code
                    if("VISA".equals(epcPayment.getPaymentCode())
                        || "MASTER".equals(epcPayment.getPaymentCode())
                        || "AE".equals(epcPayment.getPaymentCode())
                    ) {
                        pstmtPayRef.setString(4, epcSecurityHelper.validateString(StringHelper.trim(epcPayment.getCcNoMasked()))); // reference1 - cc no. masked 
                        pstmtPayRef.setString(5, epcSecurityHelper.validateString(StringHelper.trim(epcPayment.getEcrNo()))); // reference2 - ecr no.
                    } else {
                        pstmtPayRef.setString(4, StringHelper.trim(epcPayment.getReference1())); // reference1
                        pstmtPayRef.setString(5, StringHelper.trim(epcPayment.getReference2())); // reference2
                    }
                    pstmtPayRef.setString(6, "HKD"); // currency_code
                    pstmtPayRef.setInt(7, 0); // currency_amount
                    pstmtPayRef.setInt(8, 1); // exchange_rate
                    pstmtPayRef.setBigDecimal(9, epcPayment.getPaymentAmount()); // amount
                    pstmtPayRef.addBatch();
                }
                pstmtPayRef.executeBatch();
                // end of create zz_prec_pay_ref

                // create zz_prec_product
                if(itemList != null) {
                    for(EpcOrderItemInfo i : itemList) {
                        pstmtProduct.setString(1, receiptNo); // receipt_no
                        pstmtProduct.setString(2, i.getWarehouse()); // warehouse
                        pstmtProduct.setString(3, i.getItemCode()); // product_code
                        pstmtProduct.addBatch();
                    }
                    pstmtProduct.executeBatch();
                }
                // end of create zz_prec_product
                
                // update zz_prec_hdr
//System.out.println("YYYYY totalCharge:" + totalCharge);
//System.out.println("YYYYY totalPayment:" + totalPayment);
//System.out.println("YYYYY totalPayment.subtract(totalSubFee):" + totalPayment.subtract(totalSubFee));
//System.out.println("YYYYY totalSubFee:" + totalSubFee);

                sql = "update zz_prec_hdr " +
                        "   set rece_amount = ?, " +
                        "       non_sub_fee = ?, " +
                        "       sub_fee = ? " +
                        " where receipt_no = ? ";
                pstmtHdr = conn.prepareStatement(sql);

                pstmtHdr.setBigDecimal(1, totalPayment); // rece_amount
                pstmtHdr.setBigDecimal(2, totalPayment.subtract(totalSubFee)); // non_sub_fee
                pstmtHdr.setBigDecimal(3, totalSubFee); // sub_fee
                pstmtHdr.setString(4, receiptNo); // receipt_no
                pstmtHdr.executeUpdate();
                // end of update zz_prec_hdr
                
                epcCreateReceiptResult.setResult("SUCCESS");
                epcCreateReceiptResult.setOrderId(orderId);
                epcCreateReceiptResult.setReceiptNo(receiptNo);
            } else {
                // error
                epcCreateReceiptResult.setResult("FAIL");
                epcCreateReceiptResult.setErrorCode("1000");
                epcCreateReceiptResult.setErrorMessage(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcCreateReceiptResult.setResult("FAIL");
            epcCreateReceiptResult.setErrorCode("1001");
            epcCreateReceiptResult.setErrorMessage(e.getMessage());
        } finally {
            try { if(rset != null) { rset.close(); }} catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(pstmtHdr != null) { pstmtHdr.close(); } } catch (Exception ee) {}
            try { if(pstmtPayRef != null) { pstmtPayRef.close(); } } catch (Exception ee) {}
            try { if(pstmtChgDtl != null) { pstmtChgDtl.close(); } } catch (Exception ee) {}
            try { if(pstmtPInterface != null) { pstmtPInterface.close(); } } catch (Exception ee) {}
            try { if(pstmtUserInfo != null) { pstmtUserInfo.close(); } } catch (Exception ee) {}
            try { if(pstmtProduct != null) { pstmtProduct.close(); } } catch (Exception ee) {}
        }
        return epcCreateReceiptResult;
    }
    
    public EpcGetPaymentReceiptResult getPaymentReceipt(EpcGetPaymentReceipt epcGetPaymentReceipt) throws Exception {
        Connection fesConn = null;
        Connection epcConn = null;
        EpcGetPaymentReceiptResult epcGetReceiptResult = new EpcGetPaymentReceiptResult();
        
        try {
            fesConn = fesDataSource.getConnection();
            epcConn = epcDataSource.getConnection();
            fesConn.setAutoCommit(false);
            epcConn.setAutoCommit(false);
            epcGetReceiptResult = getPaymentReceipt(fesConn, epcConn, epcGetPaymentReceipt);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            epcGetReceiptResult.setResult("FAIL");
            epcGetReceiptResult.setErrorMessage(e.getMessage());
            throw e;
        } finally {
            try { if(fesConn != null) { fesConn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ee) {}
            try { if(epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception ee) {}
        }
        return epcGetReceiptResult;
    }

    
    public EpcGetPaymentReceiptResult getPaymentReceipt(Connection fesConn, Connection epcConn, EpcGetPaymentReceipt epcGetReceipt) throws Exception {
        
        EpcGetPaymentReceiptResult epcGetReceiptResult = new EpcGetPaymentReceiptResult();
        PreparedStatement pstmtEpcReceiptByOrderId = null;
        PreparedStatement pstmtFesReceiptByReceiptNo = null;
        ResultSet rset = null;
        ResultSet rset2 = null;
        String sql;
        ArrayList<EpcReceipt> epcReceiptList = new ArrayList<EpcReceipt>();
        
        try {
            
            sql = "SELECT receipt_no " +
                  "FROM epc_order_receipt " +
                  "WHERE order_id = ? ";
            pstmtEpcReceiptByOrderId = epcConn.prepareStatement(sql);
            
            sql = "SELECT receipt_no, receipt_date, time_issued, bin_number, cellular, subscriber, rece_amount, user_id, stat " +
                  "FROM zz_prec_hdr " +
                  "WHERE receipt_no = ?";
            pstmtFesReceiptByReceiptNo = fesConn.prepareStatement(sql);
            
            pstmtEpcReceiptByOrderId.setInt(1, epcGetReceipt.getOrderId());
            rset = pstmtEpcReceiptByOrderId.executeQuery();
            while(rset.next()) {
                pstmtFesReceiptByReceiptNo.setString(1, rset.getString("receipt_no"));
                rset2 = pstmtFesReceiptByReceiptNo.executeQuery();
                while (rset2.next()) {
                    EpcReceipt epcReceipt = new EpcReceipt();
                    epcReceipt.setReceiptNo(rset2.getString("receipt_no"));
                    epcReceipt.setReceiptDate(rset2.getDate("receipt_date"));
                    epcReceipt.setTimeIssued(rset2.getString("time_issued"));
                    epcReceipt.setBinNumber(rset2.getString("bin_number"));
                    epcReceipt.setCellular(rset2.getString("cellular"));
                    epcReceipt.setSubscriber(rset2.getString("subscriber"));
                    epcReceipt.setReceAmount(rset2.getBigDecimal("rece_amount"));
                    epcReceipt.setUserId(rset2.getString("user_id"));
                    epcReceipt.setStat(rset2.getString("stat"));
                    epcReceiptList.add(epcReceipt);
                }
                rset2.close();
            }
            rset.close();
            
            epcGetReceiptResult.setResult("SUCCESS");
            epcGetReceiptResult.setReceiptList(epcReceiptList);
            
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            epcGetReceiptResult.setResult("FAIL");
            epcGetReceiptResult.setErrorMessage(e.getMessage());
        } finally {
            try { if(rset != null) { rset.close(); }} catch (Exception ee) {}
            try { if(rset2 != null) { rset2.close(); }} catch (Exception ee) {}
            try { if(pstmtEpcReceiptByOrderId != null) { pstmtEpcReceiptByOrderId.close(); } } catch (Exception ee) {}
            try { if(pstmtFesReceiptByReceiptNo != null) { pstmtFesReceiptByReceiptNo.close(); } } catch (Exception ee) {}
        }
        return epcGetReceiptResult;
    }
}
