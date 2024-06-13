package epc.epcsalesapi.gup;

import epc.epcsalesapi.gup.bean.EpcCreateGup;
import epc.epcsalesapi.gup.bean.EpcCreateGupUserPin;
import epc.epcsalesapi.gup.bean.EpcCustomerType;
import epc.epcsalesapi.gup.bean.EpcDeleteGup;
import epc.epcsalesapi.gup.bean.EpcGupEncryptField;
import epc.epcsalesapi.gup.bean.EpcGupField;
import epc.epcsalesapi.gup.bean.EpcGupInput;
import epc.epcsalesapi.gup.bean.EpcGupResult;
import epc.epcsalesapi.gup.bean.EpcUpdateGup;
import epc.epcsalesapi.gup.bean.EpcUpdateGupUserPin;
import epc.epcsalesapi.gup.bean.EpcUpdateGupUserPinUsername;
import epc.epcsalesapi.gup.bean.GupError;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.OracleTypes;

import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;
//import javax.xml.soap.*;
import jakarta.xml.soap.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Hashtable;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import javax.net.ssl.*;
import javax.sql.DataSource;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class GupHandler {
    
    private static SimpleDateFormat yyyymmddSdf = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat yyyymmddhhmmssSdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private static EpcSecurityHelper epcSecurityHelper = new EpcSecurityHelper();
    private final Logger logger = LoggerFactory.getLogger(GupHandler.class);

    @Autowired
    private DataSource epcDataSource;
    
    static {
        yyyymmddSdf.setLenient(false);
        yyyymmddhhmmssSdf.setLenient(false);
    }

    public EpcGupResult validateCreateGup(EpcCreateGup input) {
        EpcGupResult result = new EpcGupResult();
        
        if ("".equals(StringHelper.trim(input.getSubrNum()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing subrNum");
            return result;
        }
        if ("".equals(StringHelper.trim(input.getCustNum()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing custNum");
            return result;
        }
        
        if (!"".equals(StringHelper.trim(input.getSwitchOnDate()))) {
            try {
                yyyymmddSdf.parse(input.getSwitchOnDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid switch on date format (YYYYMMDD)");
                return result;
            }
        }
        
        if (!"".equals(StringHelper.trim(input.getStatus()))) {
            try {
                Integer.parseInt(input.getStatus());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of status");
                return result;
            }
        }
        
        if ("".equals(StringHelper.trim(input.getSmcPin1()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing smcPin1");
            return result;
        }
        
        if ("".equals(StringHelper.trim(input.getSmcPin2()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing smcPin2");
            return result;
        }
        
        return result;
    }
    
    public EpcGupResult validateUpdateGup(EpcUpdateGup input) {
        EpcGupResult result = new EpcGupResult();

        if ("".equals(StringHelper.trim(input.getSubrNum()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing subrNum");
            return result;
        }
        
        if (!"".equals(StringHelper.trim(input.getSwitchOnDate()))) {
            try {
                yyyymmddSdf.parse(input.getSwitchOnDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid switch on date format (YYYYMMDD)");
                return result;
            }
        }
        
        if (!"".equals(StringHelper.trim(input.getStatus()))) {
            try {
                Integer.parseInt(input.getStatus());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of status");
                return result;
            }
        }
        
        if (!"".equals(StringHelper.trim(input.getStatusBarDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getStatusBarDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid status bar date format (YYYYMMDDHHMMSS)");
                return result;
            }
        }
        
        if (!"".equals(StringHelper.trim(input.getStatusDisconnectDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getStatusDisconnectDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid status disconnection date format (YYYYMMDDHHMMSS)");
                return result;
            }
        }

        return result;
    }
    
    public EpcGupResult validateDeleteGup(EpcDeleteGup input) {
        EpcGupResult result = new EpcGupResult();

        if ("".equals(StringHelper.trim(input.getSubrNum()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing subrNum");
            return result;
        }
        return result;
    }
    
    public EpcGupResult validateCreateGupUserPin(EpcCreateGupUserPin input) {
        EpcGupResult result = new EpcGupResult();

        if ("".equals(StringHelper.trim(input.getUserName()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing username");
            return result;
        }
        if ("".equals(StringHelper.trim(input.getPin1()))){
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing pin1");
            return result;
        }
        if (!"".equals(StringHelper.trim(input.getLoginFailCounter()))) {
            try {
                Integer.parseInt(input.getLoginFailCounter());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of loginFailCounter");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getLoginFailDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getLoginFailDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid login fail date(YYYYMMDDHHMMSS)");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getResetPIN()))) {
            try {
                Integer.parseInt(input.getResetPIN());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of resetPin");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getSqFailCounter()))) {
            try {
                Integer.parseInt(input.getSqFailCounter());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of SQFailCounter");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getDateOfBirth()))) {
            try {
                yyyymmddSdf.parse(input.getDateOfBirth());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid birth date format (YYYYMMDD)");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getSqFailDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getSqFailDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid SQFailDate format (YYYYMMDDHHMMSS)");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getUserNameUpdateDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getUserNameUpdateDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid userNameUpdateDate format (YYYYMMDDHHMMSS)");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getPinUpdateDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getPinUpdateDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid pinUpdateDate format (YYYYMMDDHHMMSS)");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getResetSimplePin()))) {
            try {
                Integer.parseInt(input.getResetSimplePin());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of resetSimplePin");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getResetOnlinePin()))) {
            try {
                Integer.parseInt(input.getResetOnlinePin());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of resetOnlinePin");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getOnlineLoginFailCounter()))) {
            try {
                Integer.parseInt(input.getOnlineLoginFailCounter());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of onlineLoginFailCounter");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getOnlineLoginFailDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getOnlineLoginFailDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid onlineLoginFailDate format (YYYYMMDDHHMMSS)");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getOnlinePinUpdateDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getOnlinePinUpdateDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid onlinePinUpdateDate format (YYYYMMDDHHMMSS)");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getOnlinePinUpdateDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getOnlinePinUpdateDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid onlinePinUpdateDate format (YYYYMMDDHHMMSS)");
                return result;
            }
        }
        return result;
    }
    
    public EpcGupResult validateUpdateGupUserPin(EpcUpdateGupUserPin input) {
        EpcGupResult result = new EpcGupResult();

        if ("".equals(StringHelper.trim(input.getUserName()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing userName");
            return result;
        }
        if (!"".equals(StringHelper.trim(input.getLoginFailCounter()))) {
            try {
                Integer.parseInt(input.getLoginFailCounter());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of loginFailCounter");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getLoginFailDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getLoginFailDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid login fail date(YYYYMMDDHHMMSS)");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getResetPIN()))) {
            try {
                Integer.parseInt(input.getResetPIN());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of resetPin");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getSqFailCounter()))) {
            try {
                Integer.parseInt(input.getSqFailCounter());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of SQFailCounter");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getDateOfBirth()))) {
            try {
                yyyymmddSdf.parse(input.getDateOfBirth());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid birth date format (YYYYMMDD)");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getSqFailDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getSqFailDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid SQFailDate format (YYYYMMDDHHMMSS)");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getUserNameUpdateDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getUserNameUpdateDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid userNameUpdateDate format (YYYYMMDDHHMMSS)");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getResetSimplePin()))) {
            try {
                Integer.parseInt(input.getResetSimplePin());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of resetSimplePin");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getResetOnlinePin()))) {
            try {
                Integer.parseInt(input.getResetOnlinePin());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of resetOnlinePin");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getOnlineLoginFailCounter()))) {
            try {
                Integer.parseInt(input.getOnlineLoginFailCounter());
            } catch (NumberFormatException ne) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid number format of onlineLoginFailCounter");
                return result;
            }
        }
        if (!"".equals(StringHelper.trim(input.getOnlineLoginFailDate()))) {
            try {
                yyyymmddhhmmssSdf.parse(input.getOnlineLoginFailDate());
            } catch (ParseException pe) {
                result.setResultCode(-1);
                result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
                result.setErrorMessage("Invalid onlineLoginFailDate format (YYYYMMDDHHMMSS)");
                return result;
            }
        }
        return result;
    }
    
    public EpcGupResult validateUpdateGupUserPinUsername(EpcUpdateGupUserPinUsername input) {
        EpcGupResult result = new EpcGupResult();
        if ("".equals(StringHelper.trim(input.getUserName()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing userName");
            return result;
        }
        if ("".equals(StringHelper.trim(input.getOriginalUserName()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing originalUserName");
            return result;
        }
        if(input.getOriginalUserName().equals(StringHelper.trim(input.getUserName()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Invalid userName");
            return result;
        }
        return result;
    }
    
    public EpcGupResult validateGup(EpcGupInput input) {
        EpcGupResult result = new EpcGupResult();
        if ("".equals(StringHelper.trim(input.getActionUsername()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing actionUsername");
            return result;
        }
        if ("".equals(StringHelper.trim(input.getOrderId()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing orderId");
            return result;
        }
        if ("".equals(StringHelper.trim(input.getActionSystem()))) {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Missing actionSystem");
            return result;
        }
        
        if ("create".equals(input.getActionType())) {
            return validateCreateGup((EpcCreateGup) input);
        } else if ("update".equals(input.getActionType())) {
            return validateUpdateGup((EpcUpdateGup) input);
        } else if ("delete".equals(input.getActionType())) {
            return validateDeleteGup((EpcDeleteGup) input);
        } else if ("createUserPin".equals(input.getActionType())) {
            return validateCreateGupUserPin((EpcCreateGupUserPin) input);
        } else if ("updateUserPin".equals(input.getActionType())) {
            return validateUpdateGupUserPin((EpcUpdateGupUserPin) input);
        } else if ("updateUserPinUsername".equals(input.getActionType())) {
            return validateUpdateGupUserPinUsername((EpcUpdateGupUserPinUsername) input);
        } else {
            result.setResultCode(-1);
            result.setErrorCode(GupError.INPUT_ERROR.getErrorCode());
            result.setErrorMessage("Invalid ActionType");
            return result;
        }
    }
    
    public EpcGupResult deleteThenCreateGup(EpcCreateGup input) {

        String customerType = getCustomerType(input.getSubrNum()).getCustomerType();
        if ("PostPaid".equals(customerType) || "FixedLine".equals(customerType) || "VAR".equals(customerType) || "Postpaid".equals(customerType) || "Fixedline".equals(customerType)) {
            EpcDeleteGup deleteInput = new EpcDeleteGup();
            deleteInput.setOrderId(input.getOrderId());
            deleteInput.setSubrNum(input.getSubrNum());
            deleteInput.setActionType("delete");
            deleteInput.setActionUsername(input.getActionUsername());
            deleteInput.setActionSystem(input.getActionSystem());
            EpcGupResult deleteResult = processGup(deleteInput);
            if (deleteResult.getResultCode() < 0 || !"".equals(deleteResult.getErrorCode()) ) {
                return deleteResult;
            }
        } else if (customerType != null) {
            EpcGupResult result = new EpcGupResult();
            result.setResultCode(-1);
            result.setErrorCode(GupError.SYSTEM_ERROR.getErrorCode());
            result.setErrorMessage("Fail to operate on prepaid smcCustomerType:" + customerType);
            return result;
        }
        return createGup(input);
    }
    
    public EpcGupResult createGup(EpcCreateGup input) {
        input.setActionType("create");
        return processGup(input);
    }
    
    public EpcGupResult deleteGup(EpcDeleteGup input) {
        input.setActionType("delete");
        return processGup(input);
    }
        
    public EpcGupResult updateGup(EpcUpdateGup input) {
        input.setActionType("update");
        return processGup(input);
    }
    
    public EpcGupResult createGupUserPin(EpcCreateGupUserPin input) {
        input.setActionType("createUserPin");
        return processGup(input);
    }
    
    public EpcGupResult updateGupUserPin(EpcUpdateGupUserPin input) {
        input.setActionType("updateUserPin");
        return processGup(input);
    }
    
    public EpcGupResult updateGupUserPinUsername(EpcUpdateGupUserPinUsername input) {
        input.setActionType("updateUserPinUsername");
        return processGup(input);
    }

    public EpcGupResult processGup(EpcGupInput input) {
        EpcGupInput decryptedInput = null;
        EpcGupResult result = null;
        ByteArrayOutputStream buffer = null;
        ByteArrayOutputStream reqBuffer = null;
        SSLContext sslc = null;
        HttpURLConnection urlConn = null;
        GupError error = new GupError("", "");
        InputStream is = null;
        OutputStream os = null;
        Connection epcConn = null;
        String sql;
        ResultSet rset = null;
        OraclePreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        String orderId = input.getOrderId();
        String actionType = input.getActionType();
        String actionSystem = input.getActionSystem();
        String actionUsername = input.getActionUsername();
        int logid = 0;
        String gupErrorMessage;
        String status;

        try {

            epcConn = epcDataSource.getConnection();
            epcConn.setAutoCommit(false);
            
            sql = "INSERT INTO epc_gup_log_hdr (log_username, action_system, action_type, reference_no, log_datetime, status) " +
                   "VALUES (?, ?, ?, ?, SYSDATE, ? ) RETURNING log_id INTO ? ";
            pstmt = (OraclePreparedStatement) epcConn.prepareStatement(sql).unwrap(PreparedStatement.class);
          
            sql = "INSERT INTO epc_gup_log_dtl (log_id, gup_field, gup_value) " +
                  "VALUES (?, ? , ? ) ";
            pstmt2 = epcConn.prepareStatement(sql);
            
            sql = "UPDATE epc_gup_log_hdr SET status = ?, gup_error_message = ? " +
                  "WHERE log_id = ? ";
            pstmt3 = epcConn.prepareStatement(sql);
            
            // log input first without process result
            pstmt.setString(1, actionUsername);
            pstmt.setString(2, actionSystem);
            pstmt.setString(3, actionType);
            pstmt.setString(4, orderId);
            pstmt.setString(5, "P");
            pstmt.registerReturnParameter(6, OracleTypes.INTEGER);
            if (pstmt.executeUpdate() > 0) {
                rset = pstmt.getReturnResultSet();
                if (rset.next()) {
                    logid = rset.getInt(1);
                }
                rset.close();
            } else {
                result = new EpcGupResult();
                result.setResultCode(-1);
                result.setErrorCode(GupError.SYSTEM_ERROR.getErrorCode());
                result.setErrorMessage(GupError.SYSTEM_ERROR.getErrorMessage());
                return result;
            }
            
            Class<?> clazz = input.getClass();
            for (Field field : clazz.getDeclaredFields()) {
                
                ReflectionUtils.makeAccessible(field);
                Object fieldValue = field.get(input);
                
                // save log detail for encrypted input
                if (fieldValue != null) {
                    pstmt2.setInt(1, logid);
                    pstmt2.setString(2, field.getName());
                    pstmt2.setString(3, fieldValue.toString());
                    pstmt2.addBatch();
                }
            }
            pstmt2.executeBatch();
            epcConn.commit();

            try {
                decryptedInput = getDecryptedGupInput(input);
            } catch (Exception e) {
                log(pstmt3, logid, "F", GupError.DECRYPTION_ERROR.getErrorMessage());
                
                result = new EpcGupResult();
                result.setResultCode(-1);
                result.setErrorCode(GupError.DECRYPTION_ERROR.getErrorCode());
                result.setErrorMessage(GupError.DECRYPTION_ERROR.getErrorMessage());
                return result;
            }
            result = validateGup(decryptedInput);
            if (result.getErrorCode()!= null) {
                return result;
            }

// 20230607, kerrytsang
// gup cert is already generated under SMC root CA
//  no need to skip cert validation anymore
//            // Create a trust manager that does not validate certificate chains
//            TrustManager[] trustAllCerts = new TrustManager[] {
//              new X509TrustManager() {
//                public X509Certificate[] getAcceptedIssuers() { 
//                  return new X509Certificate[0]; 
//                }
//              public void checkClientTrusted(X509Certificate[] certs, String authType) {}
//              public void checkServerTrusted(X509Certificate[] certs, String authType) {}
//            }};
//
//            // Ignore differences between given hostname and certificate hostname
//            HostnameVerifier hv = new HostnameVerifier() {
//              public boolean verify(String hostname, SSLSession session) { return true; }
//            };
//            
//            //sslc = SSLContext.getInstance("TLSv1");
////            sslc = SSLContext.getInstance("TLS");
//            sslc = SSLContext.getInstance(EpcProperty.getTlsVersion());
//            sslc.init(null, trustAllCerts, new SecureRandom());
//            HttpsURLConnection.setDefaultSSLSocketFactory(sslc.getSocketFactory());
//            HttpsURLConnection.setDefaultHostnameVerifier(hv);

            String actionURL = "";
            if ("create".equals(actionType) || "update".equals(actionType) || "delete".equals(actionType)) {
                actionURL = "Create_update_SPROFILE_table_with_username_API.php";
            } else if ("createUserPin".equals(actionType)) {
                actionURL = "create_UserPIN_table_without_MSISDN_API.php";
            } else if ("updateUserPin".equals(actionType)) {
                actionURL = "change_UserPIN_table_API.php";
            } else if ("updateUserPinUsername".equals(actionType)) {
                actionURL = "change_UserPIN_table_change_username_API.php";
            }

            URL gupEndpoint = new URL(EpcProperty.getValue("EPC_GUP_LINK") + actionURL);
            String gupUserid = EpcProperty.getValue("EPC_GUP_USERID");
            String gupDwp = EpcProperty.getValue("EPC_GUP_PWD");
            
            SOAPMessage soapMessage;
            SOAPEnvelope envelope;
            SOAPBody body;
            SOAPElement operationElement;
            SOAPElement msgTextElement;
            SOAPElement detailElement;
            
            buffer = new ByteArrayOutputStream();
            reqBuffer = new ByteArrayOutputStream();
            
            soapMessage = MessageFactory.newInstance().createMessage();
            envelope = soapMessage.getSOAPPart().getEnvelope();
            
            //remove the default SOAP-ENV
            envelope.removeNamespaceDeclaration("SOAP-ENV");
            //Set the prefix to soap instead of SOAP-ENV
            envelope.setPrefix("xsd");
            // Add namespace to envelope
            envelope.addNamespaceDeclaration("SOAP-ENV","http://schemas.xmlsoap.org/soap/envelope/");
            envelope.addNamespaceDeclaration("soap",    "http://schemas.xmlsoap.org/soap/envelope/");
            envelope.addNamespaceDeclaration("xsi",     "http://www.w3.org/2001/XMLSchema-instance");
            envelope.addNamespaceDeclaration("xsd",     "http://schemas.xmlsoap.org/soap/envelope/");
            
            //remove header
            envelope.getHeader().detachNode();
            body = envelope.getBody();
            body.removeNamespaceDeclaration("SOAP-ENV");
            body.setPrefix("xsd");
            
            Name nameName = envelope.createName("name");
            Name valueName = envelope.createName("value");
            
            operationElement = body.addBodyElement(envelope.createName("getMessage","", "urn:msg-processor"));
            
            String gupAction = "";
            if ("create".equals(actionType)) {
                gupAction = "user-profile:create";
            } else if ("update".equals(actionType)) {
                gupAction = "user-profile:update";
            } else if ("delete".equals(actionType)) {
                gupAction = "user-profile:delete";
            } else if ("createUserPin".equals(actionType)) {
                gupAction = "user-pin:create";
            } else if ("updateUserPin".equals(actionType)) {
                gupAction = "user-pin:update";
            } else if ("updateUserPinUsername".equals(actionType)) {
                gupAction = "user-pin:keyupdate";
            }

            operationElement.addAttribute(envelope.createName("action"), gupAction);
            operationElement.addAttribute(envelope.createName("dsname"), "uid=" + gupUserid + ",ou=people,o=smc");
            operationElement.addAttribute(envelope.createName("dspassword"), gupDwp);
            operationElement.addAttribute(envelope.createName("priority"), "0");
            operationElement.addAttribute(envelope.createName("timeout"), "60");
            
            String referenceNo = "";
                
            if ("delete".equals(actionType)) {
                referenceNo = orderId+"PRE_DEL";
            } else {
                referenceNo = orderId;
            }
            operationElement.addAttribute(envelope.createName("ref"), referenceNo);
            
            msgTextElement = operationElement.addChildElement("msgText");
            
            for (Field field : clazz.getDeclaredFields()) {
                
                ReflectionUtils.makeAccessible(field);
                Object fieldValue = field.get(decryptedInput);

                if (field.isAnnotationPresent(EpcGupField.class)) {
                     EpcGupField anno = field.getAnnotation(EpcGupField.class);
                     String value = (fieldValue != null ? StringHelper.trim(fieldValue.toString()) : "");

                     System.out.println("fieldName:" + field.getName() + ",AnnoFieldName:" + anno.fieldName() + ", FieldValue:" + value);
                     if ("gupfield".equals(anno.fieldType()) && !"".equals(value)) {
                         detailElement = msgTextElement.addChildElement("Field");
                         detailElement.addAttribute(nameName, anno.fieldName());
                         detailElement.addAttribute(valueName, value);
                     }
                }
            }
            epcConn.commit();

            soapMessage.saveChanges();
            soapMessage.writeTo(reqBuffer);
            String reqStr = new String(reqBuffer.toByteArray());
            // DEBUG USE
            System.out.println(reqStr.replaceAll("><",">\n<"));
            
            urlConn = (HttpURLConnection)gupEndpoint.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.setDoOutput(true);
            urlConn.setUseCaches(false);
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            
            os = urlConn.getOutputStream();
            soapMessage.writeTo(os);
            String read;
            String respStr = "";
            is = urlConn.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            while ((read = br.readLine()) != null) {
                respStr += read;
            }
            br.close(); 
            is.close();
            os.close();
            
            // DEBUG USE
            System.out.println(respStr.replaceAll("><",">\n<"));
            error = getGupError(respStr);
            if (!"".equals(error.getErrorCode())) {
                result.setResultCode(-1);
                status = "F";
            } else {
                status = "S";
            }
            gupErrorMessage = epcSecurityHelper.encodeForSQL(error.getErrorMessage());
            log(pstmt3, logid, status, gupErrorMessage);
            result.setErrorCode(error.getErrorCode());
            result.setErrorMessage(gupErrorMessage);
            
            epcConn.commit();
            
        } catch (Exception e) {
            try { if (epcConn != null) { epcConn.rollback(); } } catch (Exception ignore) {}
            logger.info(e.getMessage(), e);
            try {
                if (logid > 0) {
                    log(pstmt3, logid, "F", "NO GUP RESULT");
                }
            } catch (Exception ignore) {
                logger.info(ignore.getMessage(), ignore);
            }
            result = new EpcGupResult();
            result.setResultCode(-1);
            result.setErrorCode(GupError.SYSTEM_ERROR.getErrorCode());
            result.setErrorMessage(GupError.SYSTEM_ERROR.getErrorMessage());
        } finally {
            try { if(is != null) { is.close(); } } catch (Exception ignore) {}
            try { if(os != null) { os.close(); } } catch (Exception ignore) {}
            try { if(buffer != null) { buffer.close(); } } catch (Exception ignore) {}
            try { if(reqBuffer != null) { reqBuffer.close(); } } catch (Exception ignore) {}
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt = null; } } catch (Exception ignore) {}
            try { if (pstmt2 != null) { pstmt2.close(); } } catch (Exception ignore) {}
            try { if (pstmt2 != null) { pstmt2 = null; } } catch (Exception ignore) {}
            try { if (pstmt3 != null) { pstmt3.close(); } } catch (Exception ignore) {}
            try { if (pstmt3 != null) { pstmt3 = null; } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
        }
        return result;
    }
    
    public EpcCustomerType getCustomerType(String subrNum) {
        EpcCustomerType result = new EpcCustomerType();
        DirContext dirContext = null;
        Hashtable<String, String> env = new Hashtable<String, String> ();
        String[] attributes = new String[2];
        SearchResult sr = null;
        Attributes returnAttributes = null;
        Attribute returnAttribute = null;
        String url = epcSecurityHelper.encodeForSQL(EpcProperty.getValue("EPC_GUP_LDAP_LINK") + "ou=gup,o=smc");
        String processSubrNum = epcSecurityHelper.encodeForSQL(subrNum);
 
        try {
            env.put("com.sun.jndi.ldap.connect.pool", "true");
            env.put("com.sun.jndi.ldap.connect.pool.timeout", "10000");
            env.put("com.sun.jndi.ldap.connect.timeout", "10000");
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, url);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "uid="+EpcProperty.getValue("EPC_GUP_LDAP_USERID")+",ou=people,o=smc");
            env.put(Context.SECURITY_CREDENTIALS, EpcProperty.getValue("EPC_GUP_LDAP_PWD"));
            dirContext = new InitialDirContext(env);
            NamingEnumeration ldapResults = null;
            SearchControls constraints = new SearchControls();
            attributes[0] = "smcSubscriberNumber";
            attributes[1] = "smcCustomerType";
            constraints.setReturningAttributes(attributes);
            constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            ldapResults = dirContext.search("ou=subscribers,ou=GUP,o=smc", "smcSubscriberNumber=" + processSubrNum, constraints);
            if (ldapResults != null && ldapResults.hasMore()) {
                sr = (SearchResult) ldapResults.next();
                returnAttributes = sr.getAttributes();
                returnAttribute = returnAttributes.get("smcCustomerType");
                result.setCustomerType((String) returnAttribute.get());
            }
        
        } catch (NamingException ne) {
            logger.info(ne.getMessage(), ne);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            try { if(dirContext != null) { dirContext.close(); } } catch (Exception ignore) {}
        }
        return result;
    }
    
    public GupError getGupError(String inputStr) {
        String gupId = null;
        String gupStatus = null;
        String gupCode = null;
        String gupTs = null;
        String errDesc = null;
        GupError returnError = new GupError("", "");
        GupError receivedError = null;

        String [] b = null;
        String [] c = null;
        b = inputStr.split("\\<Result\\>|\\</Result\\>");
        //System.out.println("b[1]:" + b[1]);
        c = b[1].split("@|\\|");
        for(int i=0;i<c.length-1;i=i+2){
            //System.out.println("c[i]:" + c[i].trim() + ", c[i+1]:" + c[i+1].trim());
            if ("status".equals(c[i].trim())){
                gupStatus = c[i+1].trim();
            } else if ("id".equals(c[i].trim())){
                gupId = c[i+1].trim();
            } else if ("overallerrorcode".equals(c[i].trim())){
                gupCode = c[i+1].trim();
            } else if ("timestamp".equals(c[i].trim())){
                gupTs = c[i+1].trim();
            } else if ("errordesc".equals(c[i].trim())){
                errDesc = c[i+1].trim();
            }
        }
        if (!"S".equals(gupStatus)) {
            if (errDesc == null) {
                receivedError = GupError.getGupError(gupCode);
            }
            if (receivedError == null) {
                returnError.setErrorCode(GupError.SYSTEM_ERROR.getErrorCode());
                returnError.setErrorMessage(gupCode);
            } else {
                returnError = receivedError;
            }
        }
        return returnError;
    }
    
    private void log (PreparedStatement pstmt, int logid, String status, String gupErrorMessage) throws Exception {
        try {
            pstmt.setString(1, status);
            pstmt.setString(2, gupErrorMessage);
            pstmt.setInt(3,  logid);
            pstmt.executeUpdate();
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw e;
        }
    }

    public <T extends EpcGupInput> T getDecryptedGupInput(T input) throws Exception {
        Class<?> clazz = input.getClass();
        T resultInput = (T) clazz.getConstructor(null).newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            
            ReflectionUtils.makeAccessible(field);
            Object fieldValue = field.get(input);
            
            String value = (fieldValue != null ? StringHelper.trim(fieldValue.toString()) : "");
            if (field.isAnnotationPresent(EpcGupEncryptField.class) && !"".equals(value)) {
                try {
                    value = EpcCrypto.dGet(value, "UTF-8");
                } catch (Exception e) {
                    throw e;
                }
            }
            field.set(resultInput, value);
        }
        return resultInput;
    }
    
    public <T extends EpcGupInput> T getEncodedGupInput(T input) {
        Class<?> clazz = input.getClass();
        T resultInput = null;
        
        try {
            resultInput = (T) clazz.getConstructor(null).newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                
                ReflectionUtils.makeAccessible(field);
                Object fieldValue = field.get(input);
                
                String value = (fieldValue != null ? StringHelper.trim(fieldValue.toString()) : "");
                value = epcSecurityHelper.encodeForSQL(value);
                field.set(resultInput, value);
                
                Object fieldValue2 = field.get(resultInput);
                logger.info(field.getName() + "|" + fieldValue2.toString());
            }
        } catch (Exception ne) {
            logger.info(ne.getMessage(), ne);
        }
        return resultInput;
        
    }
    
    public String getGupDetail(String subrNum) {
        EpcCustomerType result = new EpcCustomerType();
        DirContext dirContext = null;
        Hashtable<String, String> env = new Hashtable<String, String> ();
        String[] attributes = new String[2];
        SearchResult sr = null;
        Attributes returnAttributes = null;
        Attribute returnAttribute = null;
        String url = epcSecurityHelper.encodeForSQL(EpcProperty.getValue("EPC_GUP_LDAP_LINK") + "ou=gup,o=smc");
        String processSubrNum = epcSecurityHelper.encodeForSQL(subrNum);
        String resultStr = null;
        
        try {
            env.put("com.sun.jndi.ldap.connect.pool", "true");
            env.put("com.sun.jndi.ldap.connect.pool.timeout", "10000");
            env.put("com.sun.jndi.ldap.connect.timeout", "10000");
            env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            env.put(Context.PROVIDER_URL, url);
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.SECURITY_PRINCIPAL, "uid="+EpcProperty.getValue("EPC_GUP_LDAP_USERID")+",ou=people,o=smc");
            env.put(Context.SECURITY_CREDENTIALS, EpcProperty.getValue("EPC_GUP_LDAP_PWD"));
            dirContext = new InitialDirContext(env);
            NamingEnumeration ldapResults = null;
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.ONELEVEL_SCOPE);
            ldapResults = dirContext.search("ou=subscribers,ou=GUP,o=smc", "smcSubscriberNumber=" + processSubrNum, constraints);
            if (ldapResults != null && ldapResults.hasMore()) {
                sr = (SearchResult) ldapResults.next();
                returnAttributes = sr.getAttributes();
                NamingEnumeration<String> ids = returnAttributes.getIDs();
                while(ids.hasMore()) {
                    String id = ids.next();
                    String value = (String) returnAttributes.get(id).get();
                    resultStr += id + "=" + value + "\r\n"; 
                }
                ids.close();
            }
        
        } catch (NamingException ne) {
            logger.info(ne.getMessage(), ne);
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            try { if(dirContext != null) { dirContext.close(); } } catch (Exception ignore) {}
        }
        
        if (sr != null) {
            return resultStr;
        } else {
            return "NO RECORD FOUND";
        }
    }

}
