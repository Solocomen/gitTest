package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
// added by Danny Chan on 2021-6-9 (Apple Care enhancement): start
import epc.epcsalesapi.helper.StringHelper;
// import epc.epcsalesapi.sales.bean.EpcInvoicingEAppleCareBean;
import epc.epcsalesapi.sales.bean.EpcProductListBean;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import epc.epcsalesapi.helper.AppleCareConnectInterface;
import epc.epcsalesapi.sales.bean.EpcCreateInvoiceResult;
import org.springframework.context.annotation.Configuration;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import java.sql.Statement;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcSecurityHelper;
//import epc.epcsalesapi.helper.FESCryptoEncryption;
// added by Danny Chan on 2021-6-9 (Apple Care enhancement): end

import org.springframework.stereotype.Service;

@Service
@Configuration
public class EpcAppleCareHandler {
    // added by Danny Chan on 2021-6-9 (Apple Care enhancement): start
    @Autowired
    private DataSource fesDataSource;   // for testing only 
    
    /*@Autowired
    private DataSource fescryptoDataSource;*/
    
    @Autowired
    private AppleCareConnectInterface appleCareConnect;
    
    @Autowired
	private EpcSecurityHelper epcSecurityHelper;
    
                    
    public EpcAppleCareHandler() {
    }

    // function updateKeyPassword: access ekp, decrypt it and encrypt it using 7 brother)
    /*public void updateKeyPassword(Connection fesConn, Connection fesCryptoConn) {
        ResultSet rset = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;

        String ekp = null, ekp2 = null, kp = null, kp2 = null;

        try {
            stmt = fesConn.createStatement();

            String queryStr = "SELECT encrypted_key_password FROM eapp_api " +
                              "WHERE module = 'EAPP_REG' ";

            rset = stmt.executeQuery(queryStr);

            if (rset.next()) {
                ekp = StringHelper.trim(rset.getString("encrypted_key_password"));
            }

            FESCryptoEncryption fescr = new FESCryptoEncryption(fesCryptoConn);
            kp = fescr.decrypt(ekp, "B49671CA0F4DED725610A03EDC", "EAPP", "NIL", "NIL");
            ekp2 = EpcCrypto.eGet(kp);
            
            System.out.println("ekp = " + ekp);
            System.out.println("kp = " + kp);
            System.out.println("ekp2 = " + ekp2);

            String updateStr = "UPDATE eapp_api SET encrypted_key_pwd = ? WHERE module = 'EAPP_REG'";

            pstmt = fesConn.prepareStatement(updateStr);

            pstmt.setString(1, ekp2);
            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rset.close();
            } catch (Exception e) {
            }
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }

        try {
            stmt = fesConn.createStatement();

            String queryStr = "SELECT encrypted_key_password, encrypted_key_pwd FROM eapp_api " + 
                              "WHERE module = 'EAPP_REG'";

            rset = stmt.executeQuery(queryStr);

            if (rset.next()) {
                ekp = StringHelper.trim(rset.getString("encrypted_key_password"));
                ekp2 = StringHelper.trim(rset.getString("encrypted_key_pwd"));
            }
            
            System.out.println( "checking: ekp = " + ekp ); 
            System.out.println( "checking: ekp2 = " + ekp2 ); 

            FESCryptoEncryption fescr = new FESCryptoEncryption(fesCryptoConn);
            kp = fescr.decrypt(ekp, "B49671CA0F4DED725610A03EDC", "EAPP", "NIL", "NIL");
            kp2 = EpcCrypto.dGet(ekp2);
            
            System.out.println("checking: kp = " + kp);
            System.out.println("checking: kp2 = " + kp2);
            
            System.out.println("kp1=kp2: " + kp.equals(kp2));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                rset.close();
            } catch (Exception e) {
            }
            try {
                stmt.close();
            } catch (Exception e) {
            }
        }
    }*/
    
    public boolean isEAppleCareProduct(Connection fesConn, String warehouse, String productCode) {
        boolean isEAppleCareProduct = false;
        ResultSet rset = null;
        PreparedStatement pstmt = null;

        try {
            String queryStr = "";

            queryStr = "SELECT 1 "
                    + "FROM DUAL "
                    + "WHERE EXISTS (  SELECT 1 "
                    + "FROM zz_hardcode_ctrl "
                    + "WHERE rec_type = 'EACPD' "
                    + "AND code1 = ? "
                    + "AND code2 = ? ) ";
            pstmt = fesConn.prepareCall(queryStr);

            pstmt.setString(1, warehouse);
            pstmt.setString(2, productCode);
            rset = pstmt.executeQuery();

            if (rset.next()) {
                isEAppleCareProduct = true;
            }
            rset.close();
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        } finally {
            try { if(rset != null) {rset.close(); } } catch (Exception ignored) {}
            try { if(pstmt != null) {pstmt.close(); } } catch (Exception ignored) {}
            }

        return isEAppleCareProduct;
    }

    // added by Danny Chan on 2022-2-4 (Apple Care enhancement): start
    public boolean verifyAppleCare(Connection fesConn, String invoiceNumber, String custNum, String subrNum, String imei, String custFirstName, String custLastName, 
        String emailAddress, String pocLanguage) throws Exception {
        boolean isVerified = false;
        
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        
        try {
            String functionName = "verifyData";
            char purchaseMode = 'C', pocDeliveryPreference = 'E';
            
            String queryStr = "INSERT INTO zz_pinv_eapp_log (subr_num, cust_num, imei, invoice_no, log_date, func_name) " + 
                              "VALUES (?, ?, ?, ?, SYSDATE, ?) ";
            
            pstmt = fesConn.prepareStatement(queryStr);
            
            pstmt.setString(1, StringHelper.trim(subrNum));
            pstmt.setString(2, StringHelper.trim(custNum));
            pstmt.setString(3, StringHelper.trim(imei));
            pstmt.setString(4, StringHelper.trim(invoiceNumber));
            pstmt.setString(5, StringHelper.trim(functionName));
            
            pstmt.executeUpdate();
            
            try {pstmt.close();}
            catch (Exception e) {}
            
            boolean isOutage = isOutage(fesConn);
            
            String errorCode = "", errorMessage = "";
            
            if (!isOutage) {
                // AppleCare Connect: verify order
                AppleCareConnectInterface.VerifyResult verifyResult = appleCareConnect.verifyOrder( purchaseMode, invoiceNumber, imei, custFirstName, custLastName,
                                                                                                    emailAddress, new Date(), pocDeliveryPreference, pocLanguage.charAt(0) );

                isVerified = verifyResult.getResult();
                System.out.println("isVerified = " + isVerified);
                
                errorCode = verifyResult.getErrorCode();
                errorMessage = verifyResult.getErrorMessage();

                if (errorCode == null) {
                    errorCode = "";
                }

                if (errorMessage == null) {
                    errorMessage = "";
                }

                queryStr = "UPDATE zz_pinv_eapp_log SET log_date = SYSDATE, error_code = ?, err_msg = SUBSTR(?, 1, 3000) " +
                           "WHERE imei = ? AND invoice_no = ? AND func_name = ? ";
                
                pstmt = fesConn.prepareStatement(queryStr);

                pstmt.setString(1, StringHelper.trim(errorCode));
                pstmt.setString(2, StringHelper.trim(errorMessage));
                pstmt.setString(3, StringHelper.trim(imei));
                pstmt.setString(4, StringHelper.trim(invoiceNumber));
                pstmt.setString(5, StringHelper.trim(functionName));
                
                pstmt.executeUpdate();                
            } else {
                isVerified = true;
            }
        } catch (Exception e) {
            isVerified = false;
            e.printStackTrace(System.out);
            System.out.println("Exception in calling verifyAppleCare: " + e.toString());
        } finally {
            try { if(rset != null) {rset.close(); } } catch (Exception ignored) {}
            try { if(pstmt != null) {pstmt.close(); } } catch (Exception ignored) {}
            }
        
        return isVerified;
    }
    // added by Danny Chan on 2022-2-4 (Apple Care enhancement): end
    
    /* public boolean verifyAppleCare(Connection fesConn, Connection crmConn, EpcInvoicingEAppleCareBean invoicingEAppleCareBean, String invoiceNumber) throws Exception {
        String functionName = "verifyData";

        String subrNum = invoicingEAppleCareBean.getSubrNum();
        String custNum = invoicingEAppleCareBean.getCustNum();
        String custFirstName = invoicingEAppleCareBean.getCustFirstName();
        String custLastName = invoicingEAppleCareBean.getCustLastName();
        String emailAddress = invoicingEAppleCareBean.getEmailAddress();
        String pocDeliveryPreference = invoicingEAppleCareBean.getPocDeliveryPreference();
        String pocLanguage = invoicingEAppleCareBean.getPocLanguage();
        String selectedIphoneImei = invoicingEAppleCareBean.getSelectedIphoneImei();
        char purchaseMode = invoicingEAppleCareBean.getPurchaseMode();
        boolean isOutage = isOutage(fesConn);

        boolean isVerified = false;
        String eAppleCareOrderNumber = "";
        String totalAmount = "";
        String coverageDurationStatement = "";
        String errorCode = "";
        String errorMessage = "";

        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String queryStr = "";

        try {
            //fesConn.setAutoCommit(false);
            custFirstName = custFirstName.trim().replaceAll(" +", " ");
            custLastName = custLastName.trim().replaceAll(" +", " ");

            queryStr = "INSERT INTO zz_pinv_eapp_log (subr_num, cust_num, imei, invoice_no, log_date, func_name) " + 
                       "VALUES (?, ?, ?, ?, SYSDATE, ?) ";
            pstmt = fesConn.prepareStatement(queryStr);
            System.out.println("selectedIphoneImei = " + selectedIphoneImei);
            
            pstmt.setString(1, StringHelper.trim(subrNum));
            pstmt.setString(2, StringHelper.trim(custNum));
            pstmt.setString(3, StringHelper.trim(selectedIphoneImei));
            pstmt.setString(4, StringHelper.trim(invoiceNumber));
            pstmt.setString(5, StringHelper.trim(functionName));
            pstmt.executeUpdate();

            if (!isOutage) {
                // AppleCare Connect: verify order
                AppleCareConnectInterface.VerifyResult verifyResult = appleCareConnect.verifyOrder(purchaseMode, invoiceNumber, selectedIphoneImei, custFirstName, custLastName,
                        emailAddress, new Date(), pocDeliveryPreference.charAt(0), pocLanguage.charAt(0));

                isVerified = verifyResult.getResult();
                System.out.println("isVerified = " + isVerified);
                eAppleCareOrderNumber = verifyResult.getConfirmationNumber();
                totalAmount = verifyResult.getTotalAmount();
                coverageDurationStatement = verifyResult.getCoverageDurationStatement();
                errorCode = verifyResult.getErrorCode();
                errorMessage = verifyResult.getErrorMessage();

                if (eAppleCareOrderNumber == null) {
                    eAppleCareOrderNumber = "";
                }

                if (totalAmount == null) {
                    totalAmount = "";
                }

                if (coverageDurationStatement == null) {
                    coverageDurationStatement = "";
                }

                if (errorCode == null) {
                    errorCode = "";
                }

                if (errorMessage == null) {
                    errorMessage = "";
                }

                queryStr = "UPDATE zz_pinv_eapp_log "
                        + "SET log_date = SYSDATE, "
                        + "error_code = ?, "
                        + "err_msg = SUBSTR(?, 1, 3000) "
                        + "WHERE imei = ? "
                        + "AND invoice_no = ? "
                        + "AND func_name = ? ";
                pstmt = fesConn.prepareStatement(queryStr);

                pstmt.setString(1, StringHelper.trim(errorCode));
                pstmt.setString(2, StringHelper.trim(errorMessage));
                pstmt.setString(3, StringHelper.trim(selectedIphoneImei));
                pstmt.setString(4, StringHelper.trim(invoiceNumber));
                pstmt.setString(5, StringHelper.trim(functionName));
                pstmt.executeUpdate();
            } else {
                isVerified = true;
            }

            //fesConn.commit();
        } catch (Exception e) {
            //try {
            //    fesConn.rollback();
            //} catch (Exception ignored) {
            //}
            e.printStackTrace(System.out);
            System.out.println("Exception in calling verifyAppleCare: " + e.toString());
            return false;
        } finally {
            try {
                rset.close();
            } catch (Exception ignored) {
            }
            try {
                pstmt.close();
            } catch (Exception ignored) {
            }
            //try {
            //    fesConn.setAutoCommit(true);
            //} catch (Exception ignored) {
            //}
        }

        return isVerified;
    } */

    public boolean reissueAppleCare(Connection fesConn, String invoiceNumber, String iPhoneImei, String customerNumber, String subscriberNumber) throws Exception {
        String functionName = "reissueData";

        boolean isReissued = false;
        String errorCode = "";
        String errorMessage = "";

        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String queryStr = "";

        try {
            //fesConn.setAutoCommit(false);

            queryStr = "INSERT INTO zz_pinv_eapp_log (subr_num, cust_num, imei, invoice_no, log_date, func_name) " +
                       "VALUES (?, ?, ?, ?, SYSDATE, ?) ";
            pstmt = fesConn.prepareStatement(queryStr);

            pstmt.setString(1, StringHelper.trim(subscriberNumber));
            pstmt.setString(2, StringHelper.trim(customerNumber));
            pstmt.setString(3, StringHelper.trim(iPhoneImei));
            pstmt.setString(4, StringHelper.trim(invoiceNumber));
            pstmt.setString(5, StringHelper.trim(functionName));
            pstmt.executeUpdate();

            queryStr = "INSERT INTO zz_pinv_void_eapp (invoice_no, void_date, imei, cancel_eapp) " + 
                       "VALUES (?, SYSDATE, ?, 'N') ";
            pstmt = fesConn.prepareCall(queryStr);

            pstmt.setString(1, invoiceNumber);
            pstmt.setString(2, iPhoneImei);
            pstmt.executeUpdate();

            queryStr = "UPDATE zz_pinv_eapp_backend "
                    + "SET status = 'V', "
                    + "update_date_time = SYSDATE, "
                    + "reference = 'Voided' "
                    + "WHERE invoice_no = ? "
                    + "AND imei = ? ";
            pstmt = fesConn.prepareStatement(queryStr);
            pstmt.setString(1, invoiceNumber);
            pstmt.setString(2, iPhoneImei);
            pstmt.executeUpdate();
            
            queryStr = "UPDATE zz_pinv_eapp_log "
                    + "SET log_date = SYSDATE, "
                    + "error_code = ?, "
                    + "err_msg = SUBSTR(?, 1, 3000) "
                    + "WHERE imei = ? "
                    + "AND invoice_no = ? "
                    + "AND func_name = ? ";
            pstmt = fesConn.prepareStatement(queryStr);

            pstmt.setString(1, StringHelper.trim(errorCode));
            pstmt.setString(2, StringHelper.trim(errorMessage));
            pstmt.setString(3, StringHelper.trim(iPhoneImei));
            pstmt.setString(4, StringHelper.trim(invoiceNumber));
            pstmt.setString(5, StringHelper.trim(functionName));
            pstmt.executeUpdate();

            isReissued = true;

            //fesConn.commit();
        } catch (Exception e) {
            try { if(fesConn != null) { fesConn.rollback(); } } catch (Exception ignored) {}
            e.printStackTrace(System.out);
            return false;
        } finally {
            try { if(rset != null) {rset.close(); } } catch (Exception ignored) {}
            try { if(pstmt != null) {pstmt.close(); } } catch (Exception ignored) {}
            /*try {
                fesConn.setAutoCommit(true);
            } catch (Exception ignored) {
            }*/
        }

        return isReissued;
    }

    /*
    public EpcInvoicingEAppleCareBean getEpcInvoicingEAppleCareBean(Connection fesConn, String custNum, String subrNum, String deviceProductCode,
        String imei, String firstName, String lastName, String input_emailAddress) throws Exception {
        EpcInvoicingEAppleCareBean bean = new EpcInvoicingEAppleCareBean();

        String custFirstName = null;
        String custLastName = null;
        String emailAddress = null, emailAddressPrefix = null, emailAddressSuffix = null;
        String pocLanguage = null;
        String pocDeliveryPreference = "E";

        List<EpcProductListBean> iDevicePdtList = null;

        if (!custNum.equals("00000000")) {
            String queryStr = null;

            PreparedStatement pstmt = null;
            CallableStatement cstmt = null;

            ResultSet rset = null;

            if ((firstName != null && !firstName.equals("")) || (lastName != null && !lastName.equals(""))) {
                if (firstName == null || firstName.equals("")) {
                    custFirstName = "";
                } else {
                    custFirstName = firstName;
                }

                if (lastName == null || lastName.equals("")) {
                    custLastName = "";
                } else {
                    custLastName = lastName;
                }
            } else {
                queryStr = "SELECT cust_other_name, cust_last_name "
                        + "FROM sa_cust_info_view "
                        + "WHERE cust_num = ? ";

                try {
                    pstmt = fesConn.prepareCall(queryStr);
                    pstmt.setString(1, StringHelper.trim(custNum));
                    rset = pstmt.executeQuery();

                    if (rset.next()) {
                        custFirstName = StringHelper.trim(rset.getString("cust_other_name"));
                        custLastName = StringHelper.trim(rset.getString("cust_last_name"));
                    }

                } catch (Exception e) {
                    throw e;
                } finally {
                    try { if(rset != null) { rset.close(); }} catch (Exception e) {}
                    try { if(pstmt != null) {pstmt.close();}} catch (Exception e) {}
                    }
                    }

            if (input_emailAddress == null || input_emailAddress.equals("")) {
                queryStr = "{call lib_subscription.get_subr_email(?, ?, ?, ?, ?, ?, ?)}";

                try {
                    cstmt = fesConn.prepareCall(queryStr);

                    cstmt.setString(1, StringHelper.trim(custNum));
                    cstmt.setString(2, StringHelper.trim(subrNum));
                    cstmt.setNull(3, Types.CHAR);
                    cstmt.registerOutParameter(4, Types.CHAR);
                    cstmt.registerOutParameter(5, Types.CHAR);
                    cstmt.registerOutParameter(6, Types.CHAR);
                    cstmt.registerOutParameter(7, Types.CHAR);

                    cstmt.executeUpdate();

                    emailAddress = (StringHelper.trim(cstmt.getString(6)).equals("") ? StringHelper.trim(cstmt.getString(4)) : StringHelper.trim(cstmt.getString(6)));

                    if (emailAddress.indexOf("@") >= 0) {
//                        String[] emailAddressComponent = StringHelper.split(emailAddress, "@");
                        String[] emailAddressComponent = emailAddress.split("@");

                        emailAddressPrefix = emailAddressComponent[0];
                        emailAddressSuffix = emailAddressComponent[1];
                    } else {
                        emailAddressPrefix = emailAddress;
                        emailAddressSuffix = "";
                    }
                } catch (Exception e) {
                    throw e;
                } finally {
                    try { if(cstmt != null) {cstmt.close();}} catch (Exception e) {}
                    }
            } else {
                if (input_emailAddress.indexOf("@") >= 0) {
//                    String[] emailAddressComponent = StringHelper.split(input_emailAddress, "@");
                    String[] emailAddressComponent = input_emailAddress.split("@");

                    emailAddressPrefix = emailAddressComponent[0];
                    emailAddressSuffix = emailAddressComponent[1];
                } else {
                    emailAddressPrefix = input_emailAddress;
                    emailAddressSuffix = "";
                }
                emailAddress = input_emailAddress;
            }

            queryStr = "{? = call lib_customer.get_cust_lang_code_by_cust(?)}";

            try {
                cstmt = fesConn.prepareCall(queryStr);
                cstmt.registerOutParameter(1, Types.CHAR);
                cstmt.setString(2, StringHelper.trim(custNum));
                cstmt.executeUpdate();

                pocLanguage = StringHelper.trim(cstmt.getString(1));
            } catch (Exception e) {
                throw e;
            } finally {
                try { if(cstmt != null) { cstmt.close(); } } catch (Exception ignored) {}
                }

            queryStr = "SELECT description "
                    + "FROM stockm "
                    + "WHERE warehouse like 'A%' "
                    + "AND product = ? ";

            try {
                pstmt = fesConn.prepareCall(queryStr);
                pstmt.setString(1, StringHelper.trim(deviceProductCode));

                rset = pstmt.executeQuery();

                if (rset.next()) {
                    iDevicePdtList = new ArrayList<EpcProductListBean>();
                    List<String> iphoneImeiAList = new ArrayList<String>(Arrays.asList(imei.split(("[\\s,]+"))));

                    for (String tmpImei : iphoneImeiAList) {
                        EpcProductListBean productListBean = new EpcProductListBean();
                        productListBean.setPdtProduct(deviceProductCode);
                        productListBean.setPdtQuantity("1");
                        productListBean.setPdtSerialList(tmpImei);
                        productListBean.setPdtReference(StringHelper.trim(rset.getString("description")));
                        iDevicePdtList.add(productListBean);
                    }
                }
            } catch (Exception e) {
                throw e;
            } finally {
                try { if(rset != null) {rset.close(); } } catch (Exception ignored) {}
                try { if(pstmt != null) {pstmt.close(); } } catch (Exception ignored) {}
                }

        } else {
            custFirstName = firstName;
            custLastName = lastName;
            //emailAddress = "";
            //emailAddressPrefix = "";
            //emailAddressSuffix = "";
            if (input_emailAddress.indexOf("@") >= 0) {
//                String[] emailAddressComponent = StringHelper.split(input_emailAddress, "@");
                String[] emailAddressComponent = input_emailAddress.split("@");

                emailAddressPrefix = emailAddressComponent[0];
                emailAddressSuffix = emailAddressComponent[1];
            } else {
                emailAddressPrefix = input_emailAddress;
                emailAddressSuffix = "";
            }
            emailAddress = input_emailAddress;

            pocLanguage = "";
        }

        if (pocLanguage.equals("")) {
            pocLanguage = "E";
        }

        bean.setPurchaseMode('C');
        bean.setSubrNum(subrNum);
        bean.setCustNum(custNum);
        bean.setCustFirstName(custFirstName);
        bean.setCustLastName(custLastName);
        bean.setEmailAddressPrefix(emailAddressPrefix);
        bean.setEmailAddressSuffix(emailAddressSuffix);
        bean.setEmailAddress(emailAddress);
        bean.setPocDeliveryPreference(pocDeliveryPreference);
        bean.setPocLanguage(pocLanguage);
        bean.setIDeviceProductList(iDevicePdtList);

        return bean;
    } */

    private boolean isOutage(Connection fesConn) throws IOException, SQLException {
        boolean isOutage = false;
        String outageDaySet = "";
        String outageTimeSet = "";

        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        PreparedStatement pstmt4 = null;

        ResultSet rset1 = null;
        ResultSet rset2 = null;
        ResultSet rset3 = null;
        ResultSet rset4 = null;

        String queryStr = "";

        try {
            // Check outage
            queryStr = "SELECT typdescclass "
                    + "FROM ext_typdesc "
                    + "WHERE typdesctblname = 'EAPP_DOWN_STATUS' "
                    + "AND typdesctypname = 'A' ";
            pstmt1 = fesConn.prepareCall(queryStr);

            queryStr = "SELECT 1 "
                    + "FROM ext_typdesc "
                    + "WHERE typdesctblname = 'EAPP_DOWN_MAST' "
                    + "AND typdescclass = ? "
                    + "AND typdesctypname = TO_CHAR(SYSDATE, 'D') ";
            pstmt2 = fesConn.prepareCall(queryStr);

            queryStr = "SELECT typdesctypname "
                    + "FROM ext_typdesc "
                    + "WHERE typdesctblname = 'EAPP_DOWN_TIME' "
                    + "AND typdescclass = ? ";
            pstmt3 = fesConn.prepareCall(queryStr);

            queryStr = "SELECT 1 "
                    + "FROM ext_typdesc "
                    + "WHERE typdesctblname = 'EAPP_DOWN_TIME_DTL' "
                    + "AND typdescclass = ? "
                    + "AND (TO_DATE(TO_CHAR(SYSDATE, 'HH24:MI'), 'HH24:MI') BETWEEN TO_DATE(typdesctypname, 'HH24:MI') AND TO_DATE(typdesctypdesc, 'HH24:MI')) ";
            pstmt4 = fesConn.prepareCall(queryStr);

            rset1 = pstmt1.executeQuery();

            while (rset1.next()) {
                outageDaySet = StringHelper.trim(rset1.getString("typdescclass"));

                pstmt2.setString(1, StringHelper.trim(outageDaySet));
                rset2 = pstmt2.executeQuery();

                if (rset2.next()) {
                    pstmt3.setString(1, StringHelper.trim(outageDaySet));
                    rset3 = pstmt3.executeQuery();

                    while (rset3.next()) {
                        outageTimeSet = StringHelper.trim(rset3.getString("typdesctypname"));

                        pstmt4.setString(1, StringHelper.trim(outageTimeSet));
                        rset4 = pstmt4.executeQuery();

                        if (rset4.next()) {
                            isOutage = true;
                        }
                        rset4.close();
                    }
                    rset3.close();
                }
                rset2.close();
            }
            rset1.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
            try { if(rset1 != null) {rset1.close(); } } catch (Exception ignored) {}
            try { if(pstmt1 != null) {pstmt1.close(); } } catch (Exception ignored) {}
            try { if(rset2 != null) {rset2.close(); } } catch (Exception ignored) {}
            try { if(pstmt2 != null) {pstmt2.close(); } } catch (Exception ignored) {}
            try { if(rset3 != null) {rset3.close(); } } catch (Exception ignored) {}
            try { if(pstmt3 != null) {pstmt3.close(); } } catch (Exception ignored) {}
            try { if(rset4 != null) {rset4.close(); } } catch (Exception ignored) {}
            try { if(pstmt4 != null) {pstmt4.close(); } } catch (Exception ignored) {}
            }

        return isOutage;
    }
    // added by Danny Chan on 2021-6-9 (Apple Care enhancement): end

    public boolean createAppleCare(Connection fesConn, String invoiceNo, String custNum, String subrNum, String deviceProductCode, String imei, String firstName, String lastName, String email, String orderLang) {
        PreparedStatement pstmt = null;
        String sql = "";
        boolean isCreate = false;

        try {
            sql = "insert into zz_pinv_eapp_log ( "
                    + "  subr_num, cust_num, imei, invoice_no, log_date, "
                    + "  func_name "
                    + ") values ( "
                    + "  ?, ?, ?, ?, SYSDATE, "
                    + "  ? ) ";
            pstmt = fesConn.prepareStatement(sql);
            pstmt.setString(1, subrNum); // subr_num
            pstmt.setString(2, custNum); // cust_num
            pstmt.setString(3, imei); // imei
            pstmt.setString(4, invoiceNo); // invoice_no
            pstmt.setString(5, "orderData"); // func_name
            pstmt.executeUpdate();
            pstmt.close();

            sql = "insert into zz_pinv_eapp_backend ( "
                    + "  invoice_no, purchase_mode, imei, first_name, last_name, "
                    + "  email, date_of_purchase, poc_delivery, poc_lang, status, "
                    + "  no_of_retry, create_date_time, update_date_time, iphone_prod_code "
                    + ") values ( "
                    + "  ?,?,?,substr(?,1,30),substr(?,1,30), "
                    + "  ?,sysdate,?,?,?, "
                    + "  0,sysdate,sysdate,? "
                    + ") ";
            pstmt = fesConn.prepareStatement(sql);
            pstmt.setString(1, invoiceNo); // invoice_no
            pstmt.setString(2, "C"); // purchase_mode
            pstmt.setString(3, imei); // imei
            pstmt.setString(4, firstName); // first_name
            pstmt.setString(5, lastName); // last_name
            pstmt.setString(6, email); // email
            pstmt.setString(7, "E"); // poc_delivery
            pstmt.setString(8, orderLang); // poc_lang
            pstmt.setString(9, "W"); // status
            pstmt.setString(10, deviceProductCode); // iphone_prod_code
            pstmt.executeUpdate();
            pstmt.close();

            isCreate = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(pstmt != null) {pstmt.close(); } } catch (Exception ignored) {}
        }
        return isCreate;
    }

    // refer to fesweb fes.invoicing.InvoicingEAppleCareServlet
    public boolean cancelAppleCare(Connection fesConn, Connection crmConn, String invoiceNo, String custNum, String subrNum, String imei) {
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        boolean isCancel = false;
        int cnt = 0;
        String iInvoiceNo = epcSecurityHelper.validateId(StringHelper.trim(invoiceNo));
        String iCustNum = epcSecurityHelper.validateId(StringHelper.trim(custNum));
        String iSubrNum = epcSecurityHelper.validateId(StringHelper.trim(subrNum));
        String iImei = epcSecurityHelper.validateId(StringHelper.trim(imei));

        try {
            sql = "insert into zz_pinv_eapp_log ( subr_num, cust_num, imei, invoice_no, log_date, func_name ) " + 
                  "values ( ?, ?, ?, ?, SYSDATE, ? )";
            pstmt = fesConn.prepareStatement(sql);
            pstmt.setString(1, iSubrNum); // subr_num
            pstmt.setString(2, iCustNum); // cust_num
            pstmt.setString(3, iImei); // imei
            pstmt.setString(4, iInvoiceNo); // invoice_no
            pstmt.setString(5, "cancelData"); // func_name
            pstmt.executeUpdate();
            pstmt.close();

            sql = "SELECT count(1) "
                    + "FROM zz_pinv_eapp_backend a "
                    + "WHERE invoice_no = ? "
                    + "AND imei = ? "
                    + "AND (status = 'W' OR (status = 'E' AND no_of_retry = 2)) "
                    + "AND NOT EXISTS (SELECT 1 "
                    + "FROM zz_pret_hdg "
                    + "WHERE invoice_no = a.invoice_no) ";
            pstmt = fesConn.prepareStatement(sql);
            pstmt.setString(1, iInvoiceNo); // invoice_no
            pstmt.setString(2, iImei); // imei
            rset = pstmt.executeQuery();
            if (rset.next()) {
                cnt = rset.getInt(1);
            }
            rset.close();

            if (cnt > 0) {
                // if not yet submitted to Apple, just update zz records
                sql = "UPDATE zz_pinv_eapp_backend "
                        + "SET status = 'V', "
                        + "update_date_time = SYSDATE, "
                        + "reference = 'Voided' "
                        + "WHERE invoice_no = ? "
                        + "AND imei = ? ";
                pstmt = fesConn.prepareStatement(sql);
                pstmt.setString(1, iInvoiceNo); // invoice_no
                pstmt.setString(2, iImei); // imei
                pstmt.executeUpdate();
                pstmt.close();

                sql = "INSERT INTO zz_pinv_void_eapp (invoice_no, void_date, imei, cancel_eapp) VALUES (?, SYSDATE, ?, 'D') ";
                pstmt = fesConn.prepareStatement(sql);
                pstmt.setString(1, iInvoiceNo); // invoice_no
                pstmt.setString(2, iImei); // imei
                pstmt.executeUpdate();
                pstmt.close();
				isCancel = true;
            } else {
                // ...
                // added by Danny Chan on 2021-6-17 (Apple Care enhancement): start
                // AppleCare Connect: cancel order
                String eAppleCareOrderNumber = null;

                // boolean isOutage = isOutage(fesConn);     // comented out by Danny Chan on 2024-3-4 (fix bug related to applecare for cancel order api)

				
                // if (!isOutage) {     // comented out by Danny Chan on 2024-3-4 (fix bug related to applecare for cancel order api)
                    AppleCareConnectInterface.CancelResult cancelResult = appleCareConnect.cancelOrder(iInvoiceNo, iImei);

                    isCancel = cancelResult.getResult();
                    eAppleCareOrderNumber = cancelResult.getConfirmationNumber();
                    String errorCode = cancelResult.getErrorCode();
                    String errorMessage = cancelResult.getErrorMessage();
                    
                    // added by Danny Chan on 2022-2-4 (Apple Care enhancement): start
                    if (errorCode==null) {
                        errorCode = "";
                    }
                    
                    if (errorMessage==null) {
                        errorMessage = "";
                    }
                    
                    if (eAppleCareOrderNumber == null) {
                        eAppleCareOrderNumber = "";
                    }
                    
                    sql = "UPDATE zz_pinv_eapp_log SET log_date = SYSDATE, error_code = ?, err_msg = SUBSTR(?, 1, 3000) " +
                          "WHERE imei = ? AND invoice_no = ? AND func_name = ?";
                    
                    pstmt = fesConn.prepareStatement(sql);

                    pstmt.setString(1, StringHelper.trim(errorCode));
                    pstmt.setString(2, StringHelper.trim(errorMessage));
                    pstmt.setString(3, StringHelper.trim(iImei));
                    pstmt.setString(4, StringHelper.trim(iInvoiceNo));
                    pstmt.setString(5, "cancelData");
                    
                    pstmt.executeUpdate();
                    pstmt.close();
                    // added by Danny Chan on 2022-2-4 (Apple Care enhancement): end
				// comented out by Danny Chan on 2024-3-4 (fix bug related to applecare for cancel order api): start
                //} else {
                //    isCancel = true;
                //    eAppleCareOrderNumber = "APPLE_HANDLED";
                //}
				// comented out by Danny Chan on 2024-3-4 (fix bug related to applecare for cancel order api): end

                if (isCancel) {
                    sql = "SELECT 1 FROM zz_pinv_void_eapp WHERE invoice_no = ?";
                    pstmt = fesConn.prepareCall(sql);
                    pstmt.setString(1, StringHelper.trim(iInvoiceNo));
                    rset = pstmt.executeQuery();
                    
                    boolean hasRecord = false;
                    
                    if (rset.next()) {
                        hasRecord = true;
                    } 
                    
                    if(rset != null) {rset.close(); }
                    if(pstmt != null) {pstmt.close(); }
                    
                    if (hasRecord) {
                        sql = "UPDATE zz_pinv_void_eapp SET void_date = SYSDATE, void_confirm_no = ?, cancel_eapp = 'Y' WHERE invoice_no = ? ";
                        pstmt = fesConn.prepareStatement(sql);

                        pstmt.setString(1, StringHelper.trim(eAppleCareOrderNumber));
                        pstmt.setString(2, iInvoiceNo);
                    } else {
                        sql = "INSERT INTO zz_pinv_void_eapp (invoice_no, void_date, imei, void_confirm_no, cancel_eapp) VALUES (?, SYSDATE, ?, ?, 'Y')";
                        pstmt = fesConn.prepareStatement(sql);

                        pstmt.setString(1, iInvoiceNo);
                        pstmt.setString(2, iImei);
                        pstmt.setString(3, StringHelper.trim(eAppleCareOrderNumber));
                    }

                    pstmt.executeUpdate();
                    pstmt.close();
                }
                // added by Danny Chan on 2021-6-17 (Apple Care enhancement): end
            }

            //isCancel = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) {rset.close(); } } catch (Exception ignored) {}
            try { if(pstmt != null) {pstmt.close(); } } catch (Exception ignored) {}
        }
        return isCancel;
    }

    // added by Danny Chan on 2021-6-29 (Apple Care enhancement, for testing only): start
    public EpcCreateInvoiceResult getTestAppleCare() {
        EpcCreateInvoiceResult result = new EpcCreateInvoiceResult();
//
//        Connection fesConn = null, fesCryptoConn = null;
//        
//        try {
//            fesConn = fesDataSource.getConnection();            
//            fesConn.setAutoCommit(false);
//
//            /*fesCryptoConn = fescryptoDataSource.getConnection();
//            
//            updateKeyPassword(fesConn, fesCryptoConn);*/
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {fesConn.setAutoCommit(true);} 
//            catch (Exception e) {}
//            try {fesConn.close();} 
//            catch (Exception e) {}
//            try {fesCryptoConn.close();} 
//            catch (Exception e) {}
//        }
//        
        result.setErrorMessage("SUCCESS");
        result.setErrorCode("OK");        
        return result;
    }
    // added by Danny Chan on 2021-6-29 (Apple Care enhancement, for testing only): end
}
