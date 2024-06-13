/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.fes.user.FesUser;
import epc.epcsalesapi.fes.user.FesUserHandler;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcCharge;
import epc.epcsalesapi.sales.bean.EpcControlTbl;
import epc.epcsalesapi.sales.bean.EpcInstallmentCharge;
import epc.epcsalesapi.sales.bean.EpcPayment;
import epc.epcsalesapi.sales.bean.EpcPaymentCode;
import epc.epcsalesapi.sales.bean.EpcRequiredCardPrefix;
import epc.epcsalesapi.sales.bean.EpcRequiredPaymentCode;
import epc.epcsalesapi.sales.bean.EpcValidateItem;
import epc.epcsalesapi.sales.bean.EpcValidatePayment;
import epc.epcsalesapi.sales.bean.EpcValidatePaymentError;
import epc.epcsalesapi.sales.bean.EpcValidatePaymentResult;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import epc.epcsalesapi.sales.bean.EpcPaymentCtrl;

/**
 *
 * @author DannyChan
 */
@Service
public class EpcValidatePaymentHandler {
    private final DataSource epcDataSource;
    private final DataSource fesDataSource;	
    private final EpcControlHandler epcControlHandler;
    private final FesUserHandler fesUserHandler;	
    private final EpcSecurityHelper epcSecurityHelper;
	private final EpcPaymentHandler epcPaymentHandler;
	private final EpcCancelHandler epcCancelHandler;
	
	public EpcValidatePaymentHandler(DataSource epcDataSource, DataSource fesDataSource, EpcControlHandler epcControlHandler, FesUserHandler fesUserHandler, EpcSecurityHelper epcSecurityHelper, EpcPaymentHandler epcPaymentHandler, EpcCancelHandler epcCancelHandler)  {
		this.epcDataSource = epcDataSource;
		this.fesDataSource = fesDataSource;
		this.epcControlHandler = epcControlHandler;
		this.fesUserHandler = fesUserHandler;
		this.epcSecurityHelper = epcSecurityHelper;
		this.epcPaymentHandler = epcPaymentHandler;
		this.epcCancelHandler = epcCancelHandler;
	}
	
	private final Logger logger = LoggerFactory.getLogger(EpcValidatePaymentHandler.class);
		
    public EpcValidatePaymentResult validatePayment(EpcValidatePayment epcValidatePayment) {

        Connection epcConn = null;
        Connection fesConn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        PreparedStatement pstmt4 = null;
        ResultSet rset = null;
        ResultSet rset2 = null;
        int orderId;
        EpcValidatePaymentResult result = new EpcValidatePaymentResult();
        ArrayList<EpcValidatePaymentError> errorList = new ArrayList<EpcValidatePaymentError>();
        EpcControlTbl enquiryControl;
        ArrayList<EpcControlTbl> resultControlList;
        BigDecimal maxInstallmentAmount;
        HashMap <String, BigDecimal> grandChargeMap = new HashMap<String, BigDecimal>();
        HashMap <String, BigDecimal> grandPaymentMap = new HashMap<String, BigDecimal>();
        HashMap <String, BigDecimal> reqChargeMap = new HashMap<String, BigDecimal>();
        HashMap <String, BigDecimal> reqPaymentMap = new HashMap<String, BigDecimal>();
        HashMap <String, BigDecimal> offerChargeMap = new HashMap<String, BigDecimal>();
        ArrayList<EpcValidateItem> validateItemList;
        ArrayList<EpcCharge> inputChargeList;
        ArrayList<EpcPayment> inputPaymentList;
        boolean hasMobilePayment = false;
        boolean hasRequiredPaymentCode = false;
        boolean isInvalidInputCharge = false;
        result.setErrorList(errorList);
        int seqId = -1;

        try {

            epcConn = epcDataSource.getConnection();
            epcConn.setAutoCommit(false);
            fesConn = fesDataSource.getConnection();
            fesConn.setAutoCommit(false);

            // added by Danny Chan on 2024-1-22 (filter out readonly payment code): start
            ArrayList<EpcPaymentCtrl> epcPaymentCtrlList = getPaymentCtrlList(epcConn);
            // added by Danny Chan on 2024-1-22 (filter out readonly payment code): end
			
            inputChargeList = (ArrayList<EpcCharge>) epcPaymentHandler.getSanityList(epcValidatePayment.getChargeList());
            inputPaymentList = (ArrayList<EpcPayment>) epcPaymentHandler.getSanityList(epcValidatePayment.getPaymentList());

            // check input argument format
            if (epcValidatePayment.getOrderId() == null) {
                EpcValidatePaymentError error = new EpcValidatePaymentError();
                error.setErrorCode(EpcValidatePaymentError.E1001);
                error.setErrorMessage("Missing Order Id");
                errorList.add(error);
            }
			
            // added by Danny Chan on 2023-9-4: check salesman in validatePayment API - start
            if (epcValidatePayment.getSalesman()!=null) {
                FesUser fesUser = fesUserHandler.getUserByUsername( epcValidatePayment.getSalesman() );
            
                if (fesUser.getUsername()==null) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1001);
                    error.setErrorMessage("Salesman is invalid");
                    errorList.add(error);
                }
            }
            // added by Danny Chan on 2023-9-4: check salesman in validatePayment API - end
			
            pstmt = fesConn.prepareStatement("SELECT 1 FROM zz_pservice WHERE service_code = ? AND service_ind = ? ");

            ArrayList<EpcPaymentCode> availablePaymentCodeList = epcPaymentHandler.getPaymentCodeList(null);     // modified by Danny Chan on 2022-10-11 (nehancement of payment page to support SHKP)

            // added by Danny Chan on 2022-11-17 (SHK Point Payment Enhancement): start
            enquiryControl = new EpcControlTbl();
            enquiryControl.setRecType("MAX_ONE_PAYMENT_CODE");
            resultControlList = epcControlHandler.getControl(enquiryControl);        
        
            ArrayList<String> tooManyRecordsPaymentCodeList = new ArrayList();
        
            for (int i=0; i<resultControlList.size(); i++) {
                EpcControlTbl item = resultControlList.get(i);
                String payment_code = item.getKeyStr1(); 
        
                int count = 0;
    
                for (EpcPayment payment:inputPaymentList) {
                    if (payment.getPaymentCode().equals(payment_code)) {
                        count++;
                    }
                }

                if (count>1) {
                    tooManyRecordsPaymentCodeList.add(payment_code);
                }
            }
            // added by Danny Chan on 2022-11-17 (SHK Point Payment Enhancement): end
        
            //for (EpcPayment payment:epcValidatePayment.getPaymentList()) {
            for (EpcPayment payment:inputPaymentList) {
                if ("".equals(StringHelper.trim(payment.getPaymentCode()))) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1002);
                    error.setErrorMessage("Missing Payment Code");
                    error.setErrorPaymentId(payment.getPaymentId());
                    errorList.add(error);
                    continue;
                }

                // added by Danny Chan on 2022-11-16 (SHK Point Payment Enhancement): start 
                if (tooManyRecordsPaymentCodeList.contains(payment.getPaymentCode())) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1008);
                    error.setErrorMessage("There should be only 1 record for payment code " + payment.getPaymentCode());
                    error.setErrorPaymentId(payment.getPaymentId());
                    errorList.add(error);
                    continue;
                }
                // added by Danny Chan on 2022-11-16 (SHK Point Payment Enhancement): end
        
                pstmt.setString(1, payment.getPaymentCode());
                pstmt.setString(2, "N");
                rset = pstmt.executeQuery();
                if (!rset.next()) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1003);
                    error.setErrorMessage("Invalid Payment Code " + payment.getPaymentCode());
                    error.setErrorPaymentId(payment.getPaymentId());
                    errorList.add(error);
                    continue;
                }
                rset.close();
				
                // added by Danny Chan on 2024-1-22 (filter out readonly payment code): start
                boolean isReadOnlyPaymentCode = false;
                    
                for (int j=0; j<epcPaymentCtrlList.size(); j++) {
                    EpcPaymentCtrl paymentCtrl = epcPaymentCtrlList.get(j);
                    if ( paymentCtrl.getPaymentCode().equals(payment.getPaymentCode()) && 
                        paymentCtrl.isReadOnly() ) {
                        isReadOnlyPaymentCode = true;
                    }
                }
				
                if (payment.isNewRecord() && isReadOnlyPaymentCode) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1003);
                    error.setErrorMessage("Invalid Payment Code " + payment.getPaymentCode());
                    error.setErrorPaymentId(payment.getPaymentId());
                    errorList.add(error);
                    continue;
                }
                // added by Danny Chan on 2024-1-22 (filter out readonly payment code): end

                if (payment.getPaymentAmount() == null) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1004);
                    error.setErrorMessage("Missing Payment Amount");
                    error.setErrorPaymentId(payment.getPaymentId());
                    errorList.add(error);
                    continue;
                }

                // added by Danny Chan on 2022-11-16 (SHK Point Payment Enhancement): start 
                if ( "SHKP".equals(StringHelper.trim(payment.getPaymentCode())) ) {
                    if ( payment.getPaymentAmount()==null || payment.getPaymentAmount().remainder(new BigDecimal(1)).compareTo(new BigDecimal(0))!=0  ) {
                        EpcValidatePaymentError error = new EpcValidatePaymentError();
                        error.setErrorCode(EpcValidatePaymentError.E1007);
                        error.setErrorMessage("Payment amount for SHKP should be a multiple of 1");
                        error.setErrorPaymentId(payment.getPaymentId());
                        errorList.add(error);
                        continue;
                    } 
					
					if (payment.getPaymentAmount().compareTo(new BigDecimal(10))<0) {
						EpcValidatePaymentError error = new EpcValidatePaymentError();
                        error.setErrorCode(EpcValidatePaymentError.E1007);
                        error.setErrorMessage("Payment amount for SHKP should be at least 10.");
                        error.setErrorPaymentId(payment.getPaymentId());
                        errorList.add(error);
                        continue;						
					}
                }
                // added by Danny Chan on 2022-11-16 (SHK Point Payment Enhancement): end
        
                // added by Danny Chan on 2022-6-7: start
                boolean isBankInstallment = false, isCreditCard = false;

                for (int i=0; i<availablePaymentCodeList.size(); i++) {
                    EpcPaymentCode availablePaymentCode = availablePaymentCodeList.get(i);

                    if (payment.getPaymentCode().equals(availablePaymentCode.getPaymentCode())) {
                        if ( availablePaymentCode.isBankInstallment() ) {
                            isBankInstallment = true;
                        }
                        
                        if ( availablePaymentCode.isCreditCardType() ) {
                            isCreditCard = true;
                        }
                    }
                }

                if (isBankInstallment) {
                    if (payment.getReference1()==null || payment.getReference1().equals("")) {
                        EpcValidatePaymentError error = new EpcValidatePaymentError();
                        error.setErrorCode(EpcValidatePaymentError.E1006);
                        error.setErrorMessage("Missing credit card number for installment payment");
                        error.setErrorPaymentId(payment.getPaymentId());
                        errorList.add(error);
                    }
                } else {
                    if (isCreditCard) {
                        if (payment.getReference1()==null || payment.getReference1().equals("")) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E1006);
                            error.setErrorMessage("Missing credit card number for credit card payment");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                        }
                    }
                }
                // added by Danny Chan on 2022-6-7: end
				
            }

            if (errorList.size() == 0) {

                orderId = Integer.parseInt(epcSecurityHelper.validateId(epcValidatePayment.getOrderId()));


                // check total payment amount = total charge amount
                BigDecimal totalPaymentAmount = new BigDecimal(0);
                BigDecimal totalChargeAmount = new BigDecimal(0);

                for (EpcPayment payment:epcValidatePayment.getPaymentList()) {
                    totalPaymentAmount = totalPaymentAmount.add(payment.getPaymentAmount());

                    // added by Danny Chan on 2023-11-13 (validate cancel receipt number): start
                    if (payment.getPaymentCode().equals("EPCCRNOTE")) {
                        boolean chk = epcCancelHandler.validateCancelReceipt(payment.getReference1(), orderId);

                        if (!chk) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E1006);
                            error.setErrorMessage("The cancel receipt is invalid.");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                        }
                    }
                    // added by Danny Chan on 2023-11-13 (validate cancel receipt number): end
                }

                /*
                pstmt = epcConn.prepareStatement("SELECT NVL(SUM(charge_amount), 0) total_charge_amount FROM epc_order_charge WHERE order_id = ? AND need_to_pay = ? AND paid = ? ");
                pstmt.setInt(1, orderId);
                pstmt.setString(2,  "Y");
                pstmt.setString(3,  "N");
                rset = pstmt.executeQuery();
                if (rset.next()) {
                    totalChargeAmount = rset.getBigDecimal("total_charge_amount");
                }
                rset.close();
                pstmt.close();

                if (totalPaymentAmount.compareTo(totalChargeAmount) != 0) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E2001);
                    error.setErrorMessage("Total Charge Amount is not equals to total Payment Amount");
                    errorList.add(error);
                }
                 */

                // get validate items from quote
                validateItemList = epcPaymentHandler.getValidateItemFromQuote(orderId);

                HashMap<String, ArrayList<EpcInstallmentCharge>> installmentChargeMap = new HashMap<String, ArrayList<EpcInstallmentCharge>>();
                HashMap<String, ArrayList<EpcPayment>> installmentPaymentMap = new HashMap<String, ArrayList<EpcPayment>>();
                ArrayList<EpcInstallmentCharge> installmentChargeList = new ArrayList<EpcInstallmentCharge>();
                HashMap<String, BigDecimal> maxInstallmentAmountMap = new HashMap<String, BigDecimal>();
                HashMap<String, BigDecimal> installmentAmountMap = new HashMap<String, BigDecimal>();

                if (inputChargeList == null || inputChargeList.size() == 0) {
                    pstmt = epcConn.prepareStatement("SELECT case_id, charge_desc, item_id, parent_item_id, item_code, charge_amount, allow_installment, handling_fee_waive, " +
                                                     "msisdn, charge_code, seq_id, need_to_pay, paid, catalog_item_desc " +
                                                     "FROM epc_order_charge " +
                                                     "WHERE order_id = ? " +
                                                     "AND need_to_pay = ? " +
                                                     "AND paid = ? " +
                                                     "ORDER BY case_id");
                    pstmt.setInt(1, orderId);
                    pstmt.setString(2, "Y");
                    pstmt.setString(3, "N");
                } else {
                    pstmt = epcConn.prepareStatement("SELECT case_id, charge_desc, item_id, parent_item_id, item_code, charge_amount, allow_installment, handling_fee_waive, " +
                                                     "msisdn, charge_code, seq_id, need_to_pay, paid, catalog_item_desc " +
                                                     "FROM epc_order_charge " +
                                                     "WHERE order_id = ? " +
                                                     "AND need_to_pay = ? " +
                                                     "ORDER BY case_id");
                    pstmt.setInt(1, orderId);
                    pstmt.setString(2, "Y");
                }
                rset = pstmt.executeQuery();
                while (rset.next()) {

                    EpcInstallmentCharge installmentCharge = new EpcInstallmentCharge();
                    installmentCharge.setCaseId(StringHelper.trim(rset.getString("case_id")));
                    installmentCharge.setMsisdn(StringHelper.trim(rset.getString("msisdn")));
                    installmentCharge.setChargeCode(StringHelper.trim(rset.getString("charge_code")));
                    installmentCharge.setCatalogItemDesc(StringHelper.trim(rset.getString("catalog_item_desc")));
                    installmentCharge.setLabel(StringHelper.trim(rset.getString("item_code")));
                    installmentCharge.setChargeAmount(rset.getBigDecimal("charge_amount"));
                    installmentCharge.setAllowInstallment("Y".equals(rset.getString("allow_installment")));
                    installmentCharge.setHandlingFeeWaive("Y".equals(rset.getString("handling_fee_waive")));
                    installmentCharge.setSeqId(rset.getInt("seq_id"));
                    installmentCharge.setItemId(StringHelper.trim(rset.getString("item_id")));
                    installmentCharge.setParentItemId(StringHelper.trim(rset.getString("parent_item_id")));
                    installmentCharge.setNeedToPay(rset.getString("need_to_pay"));
                    installmentCharge.setPaid(rset.getString("paid"));

                    if (inputChargeList != null && inputChargeList.size() > 0) {
                        isInvalidInputCharge = true;
                        for(EpcCharge inputCharge:inputChargeList) {
                            if (inputCharge.equals(installmentCharge)) {
                                isInvalidInputCharge = false;
                                break;
                            }
                        }
                        if (isInvalidInputCharge) {
                            continue;
                        }
                    }

                    String caseId = StringHelper.trim(rset.getString("case_id"));
                    if (installmentChargeMap.get(caseId) == null) {
                        installmentChargeMap.put(caseId, new ArrayList<EpcInstallmentCharge>());
                    }
                    if (inputChargeList != null && inputChargeList.size() > 0 && "99".equals(installmentCharge.getChargeCode())) {
                        installmentCharge.setChargeAmount(installmentCharge.getChargeAmount().negate());
                    }
                    installmentChargeMap.get(caseId).add(installmentCharge);
                    installmentChargeList.add(installmentCharge);

                    if (offerChargeMap.get(caseId) == null) {
                        offerChargeMap.put(caseId, new BigDecimal(0));
                    }
                    offerChargeMap.put(caseId, offerChargeMap.get(caseId).add(installmentCharge.getChargeAmount()));


                    if (grandChargeMap.get(rset.getString("charge_code")) == null) {
                        grandChargeMap.put(rset.getString("charge_code"), new BigDecimal(0));
                    }
                    grandChargeMap.put(rset.getString("charge_code"), grandChargeMap.get(rset.getString("charge_code")).add(installmentCharge.getChargeAmount()));

                    totalChargeAmount = totalChargeAmount.add(installmentCharge.getChargeAmount());
                }
                rset.close();
                pstmt.close();
                
                // add those default payment like COUP, tradein and etc as charges for instalment calculation
                pstmt = epcConn.prepareStatement("SELECT a.payment_code, a.case_id, a.payment_amount * -1 payment_amount " +
                                                 "FROM epc_order_payment a, epc_payment_ctrl c " +
                                                 "WHERE a.order_id = ? " +
                                                 "AND a.payment_code = c.payment_code " +
                                                 "AND c.default_payment = ? " +
                                                 "AND a.tx_no IS NULL " +
                                                 "ORDER BY a.case_id");
                pstmt.setInt(1, orderId);
                pstmt.setString(2, "Y"); //default_payment
                rset = pstmt.executeQuery();
                while (rset.next()) {
                
                    String caseId = StringHelper.trim(rset.getString("case_id"));
                
                    EpcInstallmentCharge installmentCharge = new EpcInstallmentCharge();
                    installmentCharge.setCaseId(caseId);
                    installmentCharge.setMsisdn("");
                    installmentCharge.setChargeCode(StringHelper.trim(rset.getString("payment_code")));
                    installmentCharge.setCatalogItemDesc("default_payment");
                    installmentCharge.setLabel("");
                    installmentCharge.setChargeAmount(rset.getBigDecimal("payment_amount"));
                    installmentCharge.setAllowInstallment(false);
                    installmentCharge.setHandlingFeeWaive(false);
                    installmentCharge.setSeqId(seqId--);
                    installmentCharge.setItemId("");
                    installmentCharge.setParentItemId("");
                    installmentCharge.setNeedToPay("Y");
                    installmentCharge.setPaid("N");

                    if (installmentChargeMap.get(caseId) == null) {
                        installmentChargeMap.put(caseId, new ArrayList<EpcInstallmentCharge>());
                    }
                    installmentChargeMap.get(caseId).add(installmentCharge);
                    installmentChargeList.add(installmentCharge);
                }
                rset.close();
                pstmt.close();

                logger.info("installmentChargeList size:" + installmentChargeList.size());
                if (inputChargeList != null) {
                    logger.info("inputChargeList size:" + inputChargeList.size());
                } else {
                    logger.info("inputChargeList is null");
                }

                if (inputChargeList != null && inputChargeList.size() > 0 && installmentChargeList.size() != inputChargeList.size()) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1005);
                    error.setErrorMessage("Invalid charges for validation");
                    errorList.add(error);
                    return result;
                }

                if (totalPaymentAmount.compareTo(totalChargeAmount) != 0) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E2001);
                    error.setErrorMessage("Total Charge Amount is not equals to total Payment Amount");
                    errorList.add(error);
                }

                ArrayList<EpcInstallmentCharge> generalInstallmentChargeList = new ArrayList<EpcInstallmentCharge>();
                for (Map.Entry<String, ArrayList<EpcInstallmentCharge>> entry : installmentChargeMap.entrySet()) {
                    if (!"".equals(entry.getKey())) {						
                        maxInstallmentAmount = epcPaymentHandler.getEligibleInstallmentPaymentAmount(entry.getValue(), true, generalInstallmentChargeList);
                        maxInstallmentAmountMap.put(entry.getKey(), maxInstallmentAmount);
                    } else {
                        generalInstallmentChargeList.addAll(entry.getValue());
                    }
                }

                maxInstallmentAmount = epcPaymentHandler.getEligibleInstallmentPaymentAmount(generalInstallmentChargeList, false, generalInstallmentChargeList);
                maxInstallmentAmountMap.put("", maxInstallmentAmount);

                pstmt = fesConn.prepareStatement("SELECT NVL(SUM(d.amount), 0) receipt_amount FROM zz_prec_hdr h, zz_prec_chg_dtl d " +
                                                 "WHERE h.receipt_no = d.receipt_no "+
                                                 "AND h.receipt_no = ? " +
                                                 "AND h.stat = ? " +
                                                 "AND d.charge_type = ? "
                                               );
                pstmt2 = fesConn.prepareStatement("SELECT NVL(SUM(amount), 0) redeemed_amount " +
                                                  "FROM zz_prec_pay_ref p " +
                                                  "WHERE payment_code = ? " +
                                                  "AND reference1 = ? " +
                                                  "AND NOT EXISTS (SELECT 1 FROM zz_pvoid_rec WHERE receipt_no = p.receipt_no)"
                                                  );
                pstmt3 = fesConn.prepareStatement("SELECT 1 from zz_pass_reg x, zz_prec_hdr y " +
                                                  "WHERE x.refund_receipt = ? " +
                                                  "AND x.invoice_no = y.receipt_no " +
                                                  "AND y.stat = ? "
                                                  );
                
                //for (EpcPayment payment:epcValidatePayment.getPaymentList()) {
                for (EpcPayment payment:inputPaymentList) {

                    // Control: check payment vs charge
                    // (1) check reference1 as prerequisite receipt number
                    // (2) prerequisite charge type in input receipt is correct
                    // (3) no duplicate input of prerequisite receipt
                    // (4) amount of prerequisite receipt is correct
                    // (5) amount of prerequisite receipt is not used up
                    enquiryControl = new EpcControlTbl();
                    enquiryControl.setRecType("PAYMENT_VS_CHARGE");
                    enquiryControl.setKeyStr1(payment.getPaymentCode());
                    resultControlList = epcControlHandler.getControl(enquiryControl);

                    if (resultControlList.size() > 0) {
                        BigDecimal redeemedAmount = BigDecimal.ZERO;
                        BigDecimal inputReceiptAmount = BigDecimal.ZERO;
                        int paymentCount = 0;

                        if ("".equals(StringHelper.trim(payment.getReference1()))) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2002);
                            error.setErrorMessage("Missing Receipt Number");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }

                        pstmt.setString(1, payment.getReference1());
                        pstmt.setString(2, "N");
                        pstmt.setString(3, resultControlList.get(0).getValueStr1());
                        rset = pstmt.executeQuery();
                        if (rset.next()) {
                            inputReceiptAmount = rset.getBigDecimal("receipt_amount");
                        }
                        rset.close();

                        if (inputReceiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2003);
                            error.setErrorMessage("Invalid Receipt Number " + payment.getReference1());
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }

                        //for (EpcPayment payment2:epcValidatePayment.getPaymentList()) {
                        for (EpcPayment payment2:inputPaymentList) {
                            if (payment.getPaymentCode().equals(payment2.getPaymentCode()) && payment.getReference1().equals(payment2.getReference1())) {
                                paymentCount++;
                            }
                        }
                        if (paymentCount >= 2) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2004);
                            error.setErrorMessage("Duplicated Receipt Number " + payment.getReference1());
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }

                        pstmt2.setString(1, payment.getPaymentCode());
                        pstmt2.setString(2, payment.getReference1());
                        rset = pstmt2.executeQuery();
                        if (rset.next()) {
                            redeemedAmount = rset.getBigDecimal("redeemed_amount");
                        }
                        rset.close();

                        if (redeemedAmount.compareTo(inputReceiptAmount) >= 0) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2005);
                            error.setErrorMessage("Receipt "+ payment.getReference1() +" has already been fully used.");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }

                        pstmt3.setString(1, payment.getReference1());
                        pstmt3.setString(2, "N");
                        rset = pstmt3.executeQuery();
                        if (rset.next()) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2005);
                            error.setErrorMessage("Receipt "+ payment.getReference1() +" has already been fully used.");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }
                        rset.close();

                        if (redeemedAmount.add(payment.getPaymentAmount()).compareTo(inputReceiptAmount) > 0) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2006);
                            error.setErrorMessage("Input Payment Amount is too much");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }
                    }

                    enquiryControl = new EpcControlTbl();
                    enquiryControl.setRecType("INSTALL_PAYMENT");
                    enquiryControl.setKeyStr1(payment.getPaymentCode());
                    resultControlList = epcControlHandler.getControl(enquiryControl);

                    // added by Danny Chan on 2023-4-20: start
                    for (int y=0; y<resultControlList.size(); y++) {
                        EpcControlTbl tbl = resultControlList.get(y);
                        BigDecimal minAmount = tbl.getValueNumber1();

                        if (minAmount!=null && payment.getPaymentAmount().compareTo(minAmount)<0) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2014);
                            error.setErrorMessage("Installment is not allowed as installment payment amount is not enough.");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                        }
                    }
                    // added by Danny Chan on 2023-4-20: end

                    if (resultControlList.size() > 0) {
                        String caseId = StringHelper.trim(payment.getCaseId());
                        if (installmentAmountMap.get(caseId) == null) {
                            installmentAmountMap.put(caseId, payment.getPaymentAmount());
                        } else {
                            installmentAmountMap.put(caseId, installmentAmountMap.get(caseId).add(payment.getPaymentAmount()));
                        }
                        if (installmentPaymentMap.get(caseId) == null) {
                            installmentPaymentMap.put(caseId, new ArrayList<EpcPayment>());
                        }
                        installmentPaymentMap.get(caseId).add(payment);
                    }

                    // check exists mobile payment
                    /*
                    if (validateItem.isNeedMobilePayment()) {
                        enquiryControl = new EpcControlTbl();
                        enquiryControl.setRecType("MOBILE_PAYMENT");
                        enquiryControl.setKeyStr1(payment.getPaymentCode());
                        resultControlList = epcControlHandler.getControl(enquiryControl);
                        if (resultControlList.size() > 0) {
                            hasMobilePayment = true;
                        }
                    }
                     */

                    if (grandPaymentMap.get(payment.getPaymentCode()) == null) {
                        grandPaymentMap.put(payment.getPaymentCode(), payment.getPaymentAmount());
                    } else {
                        grandPaymentMap.put(payment.getPaymentCode(), grandPaymentMap.get(payment.getPaymentCode()).add(payment.getPaymentAmount()));
                    }

                    if (reqPaymentMap.get(payment.getPaymentCode()) == null) {
                        reqPaymentMap.put(payment.getPaymentCode(), payment.getPaymentAmount());
                    } else {
                        reqPaymentMap.put(payment.getPaymentCode(), reqPaymentMap.get(payment.getPaymentCode()).add(payment.getPaymentAmount()));
                    }

                }

                // validate installment amount
                for (Map.Entry<String, BigDecimal> entry : installmentAmountMap.entrySet()) {					
                    maxInstallmentAmount = maxInstallmentAmountMap.get(entry.getKey());
                    if (maxInstallmentAmount == null) {
                        maxInstallmentAmount = BigDecimal.ZERO;
                    }
                    logger.info("case_id:" + entry.getKey());
                    logger.info("installmentAmount:" + entry.getValue());
                    logger.info("maxInstallmentAmount:" + maxInstallmentAmount);
                    if (entry.getValue().compareTo(maxInstallmentAmount) > 0 && maxInstallmentAmount.compareTo(BigDecimal.ZERO) == 0) {
                        if (installmentPaymentMap.get(entry.getKey()) != null) {
                            for(EpcPayment payment:installmentPaymentMap.get((entry.getKey()))) {
                                EpcValidatePaymentError error = new EpcValidatePaymentError();
                                error.setErrorCode(EpcValidatePaymentError.E2007);
                                error.setErrorMessage("No installment is allowed");
                                error.setErrorPaymentId(payment.getPaymentId());
                                errorList.add(error);
                            }
                        }
                    } else if (entry.getValue().compareTo(maxInstallmentAmount) > 0) {
                        if (installmentPaymentMap.get(entry.getKey()) != null) {
                            for(EpcPayment payment:installmentPaymentMap.get((entry.getKey()))) {
                                EpcValidatePaymentError error = new EpcValidatePaymentError();
                                error.setErrorCode(EpcValidatePaymentError.E2008);
                                error.setErrorMessage("Invalid Installment Amount (Max: $" + maxInstallmentAmount.setScale(2) + ")");
                                error.setErrorPaymentId(payment.getPaymentId());
                                errorList.add(error);
                            }
                        }
                    }
                }

                // validate mobile payment
                /*
                if (validateItem.isNeedMobilePayment() && !hasMobilePayment) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E2009);
                    error.setErrorMessage("Missing mobile payment");
                    errorList.add(error);
                }
                 */

                // validate required payment by charge

                for (Map.Entry<String, BigDecimal> charge : grandChargeMap.entrySet()) {
                    enquiryControl = new EpcControlTbl();
                    enquiryControl.setRecType("PAYMENT_FOR_CHARGE");
                    enquiryControl.setKeyStr1(charge.getKey());
                    resultControlList = epcControlHandler.getControl(enquiryControl);
                    if (resultControlList.size() > 0 ) {
                        reqChargeMap.put(charge.getKey(), charge.getValue());
                        ArrayList<String> tmpPaymentCodeList = new ArrayList<String>();
                        for (int i=0; i < resultControlList.size(); i++) {
                            for (Map.Entry<String, BigDecimal> payment : reqPaymentMap.entrySet()) {
                                if (payment.getKey().equals(resultControlList.get(i).getValueStr1())) {
                                    tmpPaymentCodeList.add(payment.getKey());
                                    if (reqChargeMap.get(charge.getKey()).compareTo(BigDecimal.ZERO) > 0) {
                                        if (reqPaymentMap.get(payment.getKey()).subtract(reqChargeMap.get(charge.getKey())).compareTo(BigDecimal.ZERO) >= 0) {
                                            reqPaymentMap.put(payment.getKey(), reqPaymentMap.get(payment.getKey()).subtract(reqChargeMap.get(charge.getKey())));
                                            reqChargeMap.put(charge.getKey(), new BigDecimal(0));
                                        } else {
                                            reqPaymentMap.put(payment.getKey(), new BigDecimal(0));
                                            reqChargeMap.put(charge.getKey(), reqChargeMap.get(charge.getKey()).subtract(reqPaymentMap.get(payment.getKey())));
                                        }
                                    }
                                }
                            }
                        }
                        if (reqChargeMap.get(charge.getKey()).compareTo(BigDecimal.ZERO) > 0) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2009);
                            error.setErrorMessage("Missing or Invalid Payment Amount(required Payment: "+ tmpPaymentCodeList.stream().collect(Collectors.joining(",")) +")");
                            errorList.add(error);
                        }
                    }
                }

                // check exists mobile payment
                validateItemLoop:
                for (EpcValidateItem epcValidateItem:validateItemList) {

                    //DEBUG
                    logger.info("epcValidateItem:" + epcValidateItem.getCaseId());

                    // find any charge for the offer is required to pay. If no, skip the checking of that validation item
                    if (offerChargeMap.get(epcValidateItem.getCaseId()) == null) {
                        continue validateItemLoop;
                    }

                    if (offerChargeMap.get(epcValidateItem.getCaseId()).compareTo(BigDecimal.ZERO) <= 0) {
                        continue validateItemLoop;
                    }

                    // added by Danny Chan on 2022-6-2: start
                    if (epcValidateItem instanceof EpcRequiredCardPrefix) {
                        List cardPrefixList  = ((EpcRequiredCardPrefix)epcValidateItem).getCardPrefixList();
                        for (EpcPayment payment:inputPaymentList) {
                            boolean isCreditCard = false;
                            for (int i=0; i<availablePaymentCodeList.size(); i++) {
                                EpcPaymentCode availablePaymentCode = availablePaymentCodeList.get(i);
                                if (payment.getPaymentCode().equals(availablePaymentCode.getPaymentCode())) {
                                    if ( availablePaymentCode.isCreditCardType() ) {
                                        isCreditCard = true;
                                    }
                                }
                            }

                            if (!isCreditCard) {
                                continue;
                            }

                            boolean isValidCreditCardPrefix = false;

                            for (Object cardPrefix: cardPrefixList) {
                                if (payment.getReference1().startsWith((String)cardPrefix)) {
                                    isValidCreditCardPrefix = true;
                                    break;
                                }
                            }

                            if (!isValidCreditCardPrefix) {
                                EpcValidatePaymentError error = new EpcValidatePaymentError();
                                error.setErrorCode(EpcValidatePaymentError.E2013);
                                error.setErrorMessage("Invalid credit card prefix");
                                error.setErrorPaymentId(payment.getPaymentId());
                                errorList.add(error);
                            }
                        }
                    }
                    // added by Danny Chan on 2022-6-2: end


                    /*
                    if (installmentChargeMap.get(epcValidateItem.getCaseId()) != null) {
                        for(EpcCharge epcCharge:installmentChargeMap.get(epcValidateItem.getCaseId())) {
                            if ("99".equals(epcCharge.getChargeCode())) {
                                continue validateItemLoop;
                            }
                        }
                    }
                     */

                    if (epcValidateItem instanceof EpcRequiredPaymentCode) {
                        hasMobilePayment = false;
                        hasRequiredPaymentCode = false;
                        for (String requiredPaymentCode:((EpcRequiredPaymentCode) epcValidateItem).getPaymentCodeList()) {
                            if ("Mobile Payment".equals(requiredPaymentCode)) {
                                //for (EpcPayment payment:epcValidatePayment.getPaymentList()) {
                                for (EpcPayment payment:inputPaymentList) {
                                    enquiryControl = new EpcControlTbl();
                                    enquiryControl.setRecType("MOBILE_PAYMENT");
                                    enquiryControl.setKeyStr1(payment.getPaymentCode());
                                    resultControlList = epcControlHandler.getControl(enquiryControl);
                                    if (resultControlList.size() > 0) {
                                        hasMobilePayment = true;
                                    }
                                }
                                if (!hasMobilePayment) {
                                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                                    error.setErrorCode(EpcValidatePaymentError.E2010);
                                    error.setErrorMessage("Missing mobile payment");
                                    errorList.add(error);
                                }
                            }
                        }
                        // check required payment
                        if (!hasMobilePayment || (hasMobilePayment && ((EpcRequiredPaymentCode) epcValidateItem).getPaymentCodeList().size() > 1)) {
                            for (String requiredPaymentCode:((EpcRequiredPaymentCode) epcValidateItem).getPaymentCodeList()) {
                                if (!"Mobile Payment".equals(requiredPaymentCode)) {
                                    //for (EpcPayment payment:epcValidatePayment.getPaymentList()) {
                                    for (EpcPayment payment:inputPaymentList) {
                                        if (payment.getPaymentCode().equals(requiredPaymentCode) && ("".equals(payment.getCaseId()) || epcValidateItem.getCaseId().equals(payment.getCaseId()))) {
                                            hasRequiredPaymentCode = true;
                                            break;
                                        }
                                    }
                                }
                                if (hasRequiredPaymentCode) {
                                    break;
                                }
                            }

                            if (!hasRequiredPaymentCode) {
                                EpcValidatePaymentError error = new EpcValidatePaymentError();
                                error.setErrorCode(EpcValidatePaymentError.E2011);
                                error.setErrorMessage("Wrong payment code");
                                errorList.add(error);
                            }
                        }
                    }
                }

            }
            result.setResult("SUCCESS");

        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            result.setResult("FAIL");
            EpcValidatePaymentError error = new EpcValidatePaymentError();
            error.setErrorCode(EpcValidatePaymentError.E0001);
            error.setErrorMessage("System Error");
            errorList.add(error);
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (rset2 != null) { rset2.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if (pstmt2 != null) { pstmt2.close(); } } catch (Exception ignore) {}
            try { if (pstmt3 != null) { pstmt3.close(); } } catch (Exception ignore) {}
            try { if (pstmt4 != null) { pstmt4.close(); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
            try { if (fesConn != null) { fesConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (fesConn != null) { fesConn.close(); } } catch (Exception ignore) {}
                }

        return result;
    }

    // added by Danny Chan on 2024-1-22 (filter out readonly payment code): start	
    public ArrayList<EpcPaymentCtrl> getPaymentCtrlList(Connection epcConn) throws Exception {

        ResultSet rset = null;
        PreparedStatement pstmt = null;
        String sql;
        ArrayList<EpcPaymentCtrl> epcPaymentCtrlList = new ArrayList<EpcPaymentCtrl>();
        
        try {

            sql = "SELECT rec_id, payment_code, disable_reference, default_payment, cal_default_payment_amount, " +
                  "default_ref1_mandatory, default_ref1_mandatory_message, default_ref2_mandatory, default_ref2_mandatory_message, " +
                  "default_payment_amount_message, maximum_one_payment, read_only " +
                  "FROM epc_payment_ctrl ";
            
            pstmt = epcConn.prepareStatement(sql);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                EpcPaymentCtrl epcPaymentCtrl = new EpcPaymentCtrl();
                epcPaymentCtrl.setRecId(rset.getInt("rec_id"));
                epcPaymentCtrl.setPaymentCode(StringHelper.trim(rset.getString("payment_code")));
                epcPaymentCtrl.setDisableReference("Y".equals(rset.getString("disable_reference")));
                epcPaymentCtrl.setDefaultPayment("Y".equals(rset.getString("default_payment")));
                epcPaymentCtrl.setCalDefaultPaymentAmount("Y".equals(rset.getString("cal_default_payment_amount")));
                epcPaymentCtrl.setDefaultRef1Mandatory("Y".equals(rset.getString("default_ref1_mandatory")));
                epcPaymentCtrl.setDefaultRef1Message(StringHelper.trim(rset.getString("default_ref1_mandatory_message")));
                epcPaymentCtrl.setDefaultRef2Mandatory("Y".equals(rset.getString("default_ref2_mandatory")));
                epcPaymentCtrl.setDefaultRef2Message(StringHelper.trim(rset.getString("default_ref2_mandatory_message")));
                epcPaymentCtrl.setDefaultPaymentAmountMessage(StringHelper.trim(rset.getString("default_payment_amount_message")));
                epcPaymentCtrl.setMaximumOnePayment("Y".equals(rset.getString("maximum_one_payment")));
                epcPaymentCtrl.setReadOnly("Y".equals(rset.getString("read_only")));
                
                epcPaymentCtrlList.add(epcPaymentCtrl);
            }
            rset.close();
   
        } catch (Exception e) {
            throw e;
        } finally {
            if (rset != null) { try { rset.close(); } catch (Exception ignore) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception ignore) {} }
        }

        return epcPaymentCtrlList;
    }	
    // added by Danny Chan on 2024-1-22 (filter out readonly payment code): end
}
