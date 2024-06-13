/* AppleCareConnectInterface.java (Apple Care enhancement): created by Danny Chan on 2021-6-25 */
package epc.epcsalesapi.helper;

import java.io.*;
import java.net.URL;
import java.net.Proxy;
import java.net.InetSocketAddress;
import java.security.SecureRandom;
import java.security.KeyStore;
import java.sql.*;
import java.text.*;
import java.util.Date;
import javax.net.ssl.*;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import javax.annotation.PostConstruct;

@Configuration
public class AppleCareConnectInterface {

    @Autowired
    private DataSource fesDataSource;
	
    @Autowired
    private DataSource crmFesDataSource;
    
    @Value("${APPLECARE_VERIFY_LINK}")
    private String VERIFY_ORDER_URL;
    
    @Value("${APPLECARE_CREATE_LINK}")
    private String CREATE_ORDER_URL;
    
    @Value("${APPLECARE_CANCEL_LINK}")
    private String CANCEL_ORDER_URL;
    
    @Value("${EPC_PROXY_SERVER}")
    private String proxyServer;
    
    @Value("${APPLECARE_CERT}")
    private String APPLECARE_CERT;
    
    private final String ADDRESS_LINE_ENG_1 = "Reseller-SmarTone";
    private final String ADDRESS_LINE_ENG_2 = "31/F, Millennium City 2, Kowloon";

    private final String TIME_ZONE = "-480";
    private final String LANG_CODE = "en";
    
    private final String COMPANY_NAME = "SmarTone Telecommunication Ltd";
    private final String STATE_CODE = "ZZ";
    private final String COUNTRY_CODE = "HK";
    private final String PRIMARY_PHONE_NUMBER = "28802688";
    private final String ZIP_CODE = "999077";
    private final String MRC = "";
    private final String EMAIL_FLAG = "Y";     

    private final DateFormat sdf = new SimpleDateFormat("dd/MM/yy");
    
    private byte[] appleKey = new byte[16384]; 
    private String addressLineChi1 = "";
    private String addressLineChi2 = ""; 
//modified by John Yue 18 aug 2016 for app scan change    
    private String ekp = "DUMMY";
//End modified by John Yue 18 aug 2016 for app scan change
    private String kp = "";
    private String shipTo = "";
    // added by Danny Chan on 2021-7-20 (Apple Care enhancement: change of encryption method for ekp): start
    private String ekp2 = "DUMMY";
    private String kp2 = "";
    // added by Danny Chan on 2021-7-20 (Apple Care enhancement: change of encryption method for ekp): end


    public AppleCareConnectInterface() {
        System.out.println("In AppleCareConnectInterface(), APPLECARE_CERT = " + EpcProperty.getValue("APPLECARE_CERT"));
    }
        
    @PostConstruct
    public void init() {
        // init appleKey
        System.out.println( "APPLECARE_CERT = " + APPLECARE_CERT );        
        System.out.println( "proxyServer = " + proxyServer );

        // ClassLoader classLoader = null;
        // InputStream is = null;
        // ByteArrayOutputStream buffer = null;
        
        // try {
        //     classLoader = getClass().getClassLoader();
        //     is = classLoader.getResourceAsStream(APPLECARE_CERT);

        //     buffer = new ByteArrayOutputStream();

        //     int nRead;

        //     while ((nRead = is.read(appleKey, 0, appleKey.length)) != -1) {
        //         buffer.write(appleKey, 0, nRead);
        //         System.out.println("nRead = " + nRead);
        //     }

        //     buffer.flush();
        //     is.close();
        // } catch (Exception e) {
        //     e.printStackTrace();
        // } finally {
        //     try { if (is != null) { is.close(); } } catch (Exception ignore) {}
        // }
        
        // init addressLineChi1 and addressLineChi2
        Statement stmt = null;      
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        Blob sslCertBlob = null;

        String queryStr = "";
        
        Connection conn = null, crmConn = null;
        
        System.out.println("fesDataSource = " + fesDataSource);
        System.out.println("crmFesDataSource = " + crmFesDataSource);
        
        try {  
            conn = fesDataSource.getConnection();
            crmConn = crmFesDataSource.getConnection();
            
            stmt = conn.createStatement();  

//            queryStr = "SELECT ship_to, encrypted_key_password, encrypted_key_pwd " +       // added the field 'encrypted_key_pwd' in table 'eapp_api' by Danny Chan on 2021-7-20
            queryStr = "SELECT ship_to, encrypted_key_password, encrypted_key_pwd, ssl_cert " + 
                       "FROM eapp_api " + 
                       "WHERE module = 'EAPP_REG' ";
            rset = stmt.executeQuery(queryStr);
            
            if (rset.next()) {
                shipTo = StringHelper.trim(rset.getString("ship_to"));
// //modified by John Yue 18 aug 2016 for app scan change    
                 ekp = StringHelper.trim(rset.getString("encrypted_key_password"));
// //End modified by John Yue 18 aug 2016 for app scan change    

                 // added by Danny Chan on 2021-7-20 (Apple Care enhancement: change of encryption method for ekp): start
                 ekp2 = StringHelper.trim(rset.getString("encrypted_key_pwd"));
                 // added by Danny Chan on 2021-7-20 (Apple Care enhancement: change of encryption method for ekp): end

                 sslCertBlob = rset.getBlob("ssl_cert");
                 if(sslCertBlob != null) {
                    appleKey = sslCertBlob.getBytes(1l, (int) sslCertBlob.length());
                 }
            }
            rset.close();
            
//            System.out.println("ekp = " + ekp);
//            System.out.println("ekp2 = " + ekp2);
            
               // commented out by Danny Chan on 2021-7-20
               /*FESCryptoEncryption fescr = new FESCryptoEncryption();
// //modified by John Yue 18 aug 2016 for app scan change                
               kp = fescr.decrypt(ekp, "B49671CA0F4DED725610A03EDC", "EAPP", "NIL", "NIL");
// //End modified by John Yue 18 aug 2016 for app scan change    */
               // commented out by Danny Chan on 2021-7-20 

            // added by Danny Chan on 2021-7-20 (Apple Care enhancement: change of encryption method for ekp): start
            kp2 = EpcCrypto.dGet(ekp2);
            // added by Danny Chan on 2021-7-20 (Apple Care enhancement: change of encryption method for ekp): end
            
            //System.out.println("kp = " + kp + ", size of kp = " + kp.length());
            //System.out.println("kp2 = " + kp2 + ", size of kp2 = " + kp2.length());
            
            queryStr = "SELECT value " +
                       "FROM zz_pinv_eapp_info " +
                       "WHERE item = 'address_line_chi_1' ";
            pstmt = crmConn.prepareStatement(queryStr);
            rset = pstmt.executeQuery();
            
            if(rset.next()) {
                addressLineChi1 = StringHelper.trim(rset.getString("value"));
            }   
            rset.close();
            
            queryStr = "SELECT value " +
                       "FROM zz_pinv_eapp_info " +
                       "WHERE item = 'address_line_chi_2' ";
            pstmt = crmConn.prepareStatement(queryStr);
            rset = pstmt.executeQuery();
            
            if(rset.next()) {
                addressLineChi2 = StringHelper.trim(rset.getString("value"));
            }   
            rset.close();            
        } catch (Exception e) {
            System.out.println("Exception in calling constructor: " + e.toString());
            e.printStackTrace(System.out);
            //throw e;
        } finally { 
//modified by John Yue 18 aug 2016 for app scan change            
            try { if(rset != null) { rset.close(); rset = null; } } catch (Exception ignored) {}
            try { if(stmt != null) { stmt.close(); stmt = null; } } catch (Exception ignored) {}
            try { if(pstmt != null) { pstmt.close(); pstmt = null; } } catch (Exception ignored) {}
//End modified by John Yue 18 aug 2016 for app scan change        

            try { if(conn != null) { conn.close(); } } catch (Exception e) {}
            try { if(crmConn != null) { crmConn.close(); } } catch (Exception e) {}
        }     
    }    
    
    private JSONObject readJsonFromUrlByPostMethod(String host, String accAccessToken, JSONObject param) throws Exception {
// added by Danny Chan on 2019-11-5: start
        KeyStore clientStore = KeyStore.getInstance("PKCS12");

//        System.out.println("readJsonFromUrlByPostMethod: kp2 = " + kp2 );
//        System.out.println("readJsonFromUrlByPostMethod: appleKey = " + appleKey );
        
        clientStore.load(new ByteArrayInputStream(appleKey), kp2.toCharArray());          // Apple Care Enhancement: changed from kp to kp2 by Danny Chan on 2021-7-20

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, kp2.toCharArray());          // Apple Care Enhancement: changed from kp to kp2 by Danny Chan on 2021-7-20

        KeyManager[] kms = kmf.getKeyManagers();

        SSLContext sslContext = null;
//        sslContext = SSLContext.getInstance("TLS");
        sslContext = SSLContext.getInstance(EpcProperty.getTlsVersion());
        sslContext.init(kms, null, new SecureRandom());

        /* commented out by Danny Chan on 2021-6-11: start 
        URL url = new URL(null, host, new sun.net.www.protocol.https.Handler());
        commented out by Danny Chan on 2021-6-11: end */
        System.out.println("host = " + host);
        URL url = new URL(host);

        Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyServer, 443));
        HttpsURLConnection conn = (HttpsURLConnection)url.openConnection(proxy);
        conn.setSSLSocketFactory( sslContext.getSocketFactory() );

        System.out.println("[AppleCareConnectInterface][readJsonFromUrlByPostMethod]open HTTPS connection.");
// added by Danny Chan in 2019-11-5: end

        
        BufferedReader in = null;
//added by John Yue 18 aug 2016 for app scan change            
        InputStreamReader isr = null;
//End added by John Yue 18 aug 2016 for app scan change            
        DataOutputStream wr = null;
        int responseCode = 0;         
        String inputLine;
        StringBuffer response = null;

        try {
            // Add reuqest header
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json;");
            
            
            if (!accAccessToken.equals("")) {
                conn.setRequestProperty("accAccessToken", accAccessToken);
            }
            
            // Send post request
            conn.setDoOutput(true);
            
            wr = new DataOutputStream(conn.getOutputStream());
            //param.put("targetUrl", host);         // commented out by Danny Chan on 2021-6-29
            wr.writeBytes(param.toString());
            wr.flush();
            wr.close();

            responseCode = conn.getResponseCode();
//modified by John Yue 18 aug 2016 for app scan change            
            isr = new InputStreamReader(conn.getInputStream());
            in = new BufferedReader(isr);
            response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                System.out.println(inputLine);
            } 
            in.close();
            isr.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);                        
            throw e;
        } finally {
            try { if (wr != null) {wr.close(); wr = null;} } catch(Exception e) {}
            try { if (in != null) {in.close(); in = null;} } catch(Exception e) {}
            try { if (isr != null) {isr.close(); isr = null;} } catch(Exception e) {}
//End modified by John Yue 18 aug 2016 for app scan change            
        }
        //added by Raphael Leung for TLS1.2 connection
        System.out.println("[fes.iphone.AppleCareConnectInterface][readJsonFromUrlByPostMethod()] response:" + response.toString());
        //end added by Raphael Leung for TLS1.2 connection
        return new JSONObject(response.toString());
    }

    private void writeLog(String functionName, String invoiceNo, String content, String error, String returnContent) {
        Connection conn = null;
        PreparedStatement pstmt= null;
        
        try {
            conn = fesDataSource.getConnection();
            conn.setAutoCommit(true);
                    
            pstmt = conn.prepareCall(
                "INSERT INTO apple_ext_log (func_name, purchase_order, content, error, return_content) " +
                "VALUES (?, ?, SUBSTR(?, 1, 1000), SUBSTR(?, 1, 300), SUBSTR(?, 1, 4000)) "
            );
            
            pstmt.setString(1, functionName);
            pstmt.setString(2, invoiceNo);
            pstmt.setString(3, content);
            pstmt.setString(4, error);
            pstmt.setString(5, returnContent);
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        } finally {
//modified by John Yue 18 aug 2016 for app scan change            
            try { if(pstmt != null) { pstmt.close(); pstmt = null; } } catch (Exception ignored) {}
//End modified by John Yue 18 aug 2016 for app scan change        

            try { if(conn != null) { conn.close(); } } catch (Exception e) {}
        }
    }
    
    public VerifyResult verifyOrder(char purchaseMode, String invoiceNo, String imei, String firstName, String lastName, 
                                    String emailAddress, java.util.Date dateOfPurchase, char pocDeliveryPreference, char pocLanguage) throws Exception {
        VerifyResult result = new VerifyResult();

        String errorMessageLog = ""; 
        String content = "";
        String contentResponse = ""; 

        JSONObject verifyOrderResult = new JSONObject();
        JSONObject param = new JSONObject();
        JSONObject requestContext = new JSONObject();
        JSONObject customerRequest = new JSONObject();
        JSONObject deviceRequest = new JSONObject();

        JSONObject orderDetailsResponses = new JSONObject();
        JSONObject deviceEligibility = new JSONObject();            
        JSONObject deviceErrorResponse = new JSONObject();
        JSONArray deviceMultipleErrorResponse = new JSONArray();
        JSONObject orderErrorResponse = new JSONObject();
        JSONArray orderMultipleErrorResponse = new JSONArray();

        String purchaseOrder = StringHelper.trim(invoiceNo).length() > 2 ? invoiceNo.substring(0, 2) + "/" + invoiceNo.substring(2):"";
        String spocDeliveryPreferenceVar = pocDeliveryPreference == 'E' ? "E" : (pocDeliveryPreference == 'H' ? "H" : "");
        String sPocLanguage = pocLanguage == 'C' ? "ZHT" : (pocLanguage == 'E' ? "ENG" : "");
        String sDateOfPurchase = sdf.format(dateOfPurchase);              
        
        try {   
                requestContext.put("shipTo", shipTo);
                requestContext.put("timeZone", TIME_ZONE);
                requestContext.put("langCode", LANG_CODE);
                customerRequest.put("customerFirstName", firstName);
                customerRequest.put("customerLastName", lastName);
                customerRequest.put("companyName", COMPANY_NAME);
                customerRequest.put("customerEmailId", emailAddress);
                customerRequest.put("addressLine1", pocLanguage == 'C' ? addressLineChi1 : ADDRESS_LINE_ENG_1);
                customerRequest.put("addressLine2", pocLanguage == 'C' ? addressLineChi2 : ADDRESS_LINE_ENG_2);
                customerRequest.put("city", "");
                customerRequest.put("stateCode", STATE_CODE);
                customerRequest.put("countryCode", COUNTRY_CODE);
                customerRequest.put("primaryPhoneNumber", PRIMARY_PHONE_NUMBER);
                customerRequest.put("zipCode", ZIP_CODE);

                deviceRequest.put("deviceId", imei);
                deviceRequest.put("secondarySerialNumber", "");
                deviceRequest.put("hardwareDateOfPurchase", sDateOfPurchase);
                //added by Raphael leung on 24/01/2019 for a
                //ding hardwareShipDate param
                deviceRequest.put("hardwareShipDate", "");
                //end by added by Raphael leung on 24/01/2019 for adding hardwareShipDate param

                param.put("requestContext", requestContext);
                param.put("appleCareSalesDate", sDateOfPurchase);
                param.put("pocLanguage", sPocLanguage);
                param.put("pocDeliveryPreference", spocDeliveryPreferenceVar);
                param.put("purchaseOrderNumber", purchaseOrder);
                param.put("MRC", MRC);
                param.put("marketID", "");
                param.put("overridePocFlag", "");
                param.put("emailFlag", EMAIL_FLAG);
                param.put("customerRequest", customerRequest);
                param.put("deviceRequest", deviceRequest);

                verifyOrderResult = readJsonFromUrlByPostMethod(VERIFY_ORDER_URL, "", param);
                
                result.result = true;
                
                try {
                    orderDetailsResponses = verifyOrderResult.getJSONObject("orderDetailsResponses");
                } catch (JSONException j) { 
                    result.result = false;
                    result.errorMessage = "Error occurs in getting response by AppleCare Connect.";
                    
                    errorMessageLog = j.getMessage();                    
                }

                try {
                    deviceEligibility = orderDetailsResponses.getJSONObject("deviceEligibility");
                    deviceMultipleErrorResponse = deviceEligibility.getJSONArray("deviceErrorResponse");
                    deviceErrorResponse = deviceMultipleErrorResponse.getJSONObject(0);
                    
                    result.result = false;
                    result.errorCode = deviceErrorResponse.getString("errorCode");
                    result.errorMessage = deviceErrorResponse.getString("errorMessage");     

                    errorMessageLog = "[" + result.errorCode + "] " + result.errorMessage;
                } catch (JSONException j) {        
                }                 
                
                try {
                    deviceEligibility = orderDetailsResponses.getJSONObject("deviceEligibility");
                    deviceErrorResponse = deviceEligibility.getJSONObject("deviceErrorResponse");
                    
                    result.result = false;
                    result.errorCode = deviceErrorResponse.getString("errorCode");
                    result.errorMessage = deviceErrorResponse.getString("errorMessage");     

                    errorMessageLog = "[" + result.errorCode + "] " + result.errorMessage;
                } catch (JSONException j) {        
                } 

                try {
                    orderMultipleErrorResponse = verifyOrderResult.getJSONArray("orderErrorResponse");
                    orderErrorResponse = orderMultipleErrorResponse.getJSONObject(0);
                    
                    result.result = false;
                    result.errorCode = orderErrorResponse.getString("errorCode");
                    result.errorMessage = orderErrorResponse.getString("errorMessage");     

                    errorMessageLog = "[" + result.errorCode + "] " + result.errorMessage;
                } catch (JSONException j) {        
                }                 
                
                try {
                    orderErrorResponse = verifyOrderResult.getJSONObject("orderErrorResponse");
                    
                    result.result = false;
                    result.errorCode = orderErrorResponse.getString("errorCode");
                    result.errorMessage = orderErrorResponse.getString("errorMessage");     

                    errorMessageLog = "[" + result.errorCode + "] " + result.errorMessage;
                } catch (JSONException j) {   
                }            
            
        } catch (Exception e) {
            e.printStackTrace(System.out);
            System.out.println("Exception in calling verifyOrder: " + e.toString());
            
            result.result = false;
            result.errorMessage = "Error occurs in verifying order by AppleCare Connect.";
            
            errorMessageLog = e.getMessage();
        } finally {
            try {   
                try {
                    content = param.toString();
                    contentResponse = verifyOrderResult.toString();         
                } catch (Exception e) {
                }

                writeLog("AppleCareConnectInterface.verifyOrder", invoiceNo, content, errorMessageLog, contentResponse);
            } catch (Exception e) {
                System.out.println("Error in logginig: " + e.toString());
                e.printStackTrace(System.out);
            }            
        }
        
        return result;
    }
    
    public OrderResult createOrder(char purchaseMode, String invoiceNo, String imei, String firstName, String lastName, 
                                   String emailAddress, java.util.Date dateOfPurchase, char pocDeliveryPreference, char pocLanguage) throws Exception {
        OrderResult result = new OrderResult();

        String errorMessageLog = ""; 
        String content = "";
        String contentResponse = "";
        
        JSONObject createOrderResult = new JSONObject();
        JSONObject param = new JSONObject();
        JSONObject requestContext = new JSONObject();
        JSONObject customerRequest = new JSONObject();
        JSONObject deviceRequest = new JSONObject();

        JSONObject orderDetailsResponses = new JSONObject();
        JSONObject deviceEligibility = new JSONObject();            
        JSONObject orderConfirmation = new JSONObject();
        JSONObject deviceErrorResponse = new JSONObject();
        JSONArray deviceMultipleErrorResponse = new JSONArray();
        JSONObject orderErrorResponse = new JSONObject();
        JSONArray orderMultipleErrorResponse = new JSONArray();

        String purchaseOrder = StringHelper.trim(invoiceNo).length() > 2 ? invoiceNo.substring(0, 2) + "/" + invoiceNo.substring(2):"";
        String spocDeliveryPreferenceVar = pocDeliveryPreference == 'E' ? "E" : (pocDeliveryPreference == 'H' ? "H" : "");
        String sPocLanguage = pocLanguage == 'C' ? "ZHT" : (pocLanguage == 'E' ? "ENG" : "");
        String sDateOfPurchase = sdf.format(dateOfPurchase);
        
        try {        
            
            
            
                requestContext.put("shipTo", shipTo);
                requestContext.put("timeZone", TIME_ZONE);
                requestContext.put("langCode", LANG_CODE);

                customerRequest.put("customerFirstName", firstName);
                customerRequest.put("customerLastName", lastName);
                customerRequest.put("companyName", COMPANY_NAME);
                customerRequest.put("customerEmailId", emailAddress);
                customerRequest.put("addressLine1", pocLanguage == 'C' ? addressLineChi1 : ADDRESS_LINE_ENG_1);
                customerRequest.put("addressLine2", pocLanguage == 'C' ? addressLineChi2 : ADDRESS_LINE_ENG_2);
                customerRequest.put("city", "");
                customerRequest.put("stateCode", STATE_CODE);
                customerRequest.put("countryCode", COUNTRY_CODE);
                customerRequest.put("primaryPhoneNumber", PRIMARY_PHONE_NUMBER);
                customerRequest.put("zipCode", ZIP_CODE);

                deviceRequest.put("deviceId", imei);
                deviceRequest.put("secondarySerialNumber", "");
                deviceRequest.put("hardwareDateOfPurchase", sDateOfPurchase);

                param.put("requestContext", requestContext);
                param.put("appleCareSalesDate", sDateOfPurchase);
                param.put("pocLanguage", sPocLanguage);
                param.put("pocDeliveryPreference", spocDeliveryPreferenceVar);
                param.put("purchaseOrderNumber", purchaseOrder);
                param.put("MRC", MRC);
                param.put("marketID", "");
                param.put("overridePocFlag", "");
                param.put("emailFlag", EMAIL_FLAG);
                param.put("customerRequest", customerRequest);
                param.put("deviceRequest", deviceRequest);
               
                createOrderResult = readJsonFromUrlByPostMethod(CREATE_ORDER_URL, "", param);

                try {
                    orderDetailsResponses = createOrderResult.getJSONObject("orderDetailsResponses");
                } catch (JSONException j) { 
                    result.result = false;
                    result.errorMessage = "Error occurs in getting response by AppleCare Connect.";
                    
                    errorMessageLog = j.getMessage();                    
                }
                
                try {
                    orderConfirmation = orderDetailsResponses.getJSONObject("orderConfirmation");
                    
                    result.result = true;
                    result.confirmationNumber = orderConfirmation.getString("agreementNumber");
                    result.coverageDurationStatement = orderConfirmation.getString("coverageDurationStatement");   
                    result.errorCode = "";
                    result.errorMessage = "";
                    errorMessageLog = "";             
                } catch (JSONException j) {
                    result.result = false;
                    result.errorMessage = "Error occurs in getting order confirmation by AppleCare Connect.";
                    
                    errorMessageLog = j.getMessage();    
                } 

                try {
                    deviceEligibility = orderDetailsResponses.getJSONObject("deviceEligibility");
                    deviceMultipleErrorResponse = deviceEligibility.getJSONArray("deviceErrorResponse");
                    deviceErrorResponse = deviceMultipleErrorResponse.getJSONObject(0);
                    
                    result.result = false;
                    result.errorCode = deviceErrorResponse.getString("errorCode");
                    result.errorMessage = deviceErrorResponse.getString("errorMessage");     

                    errorMessageLog = "[" + result.errorCode + "] " + result.errorMessage;
                } catch (JSONException j) {        
                }  
                
                try {
                    deviceEligibility = orderDetailsResponses.getJSONObject("deviceEligibility");
                    deviceErrorResponse = deviceEligibility.getJSONObject("deviceErrorResponse");
                    
                    result.result = false;
                    result.errorCode = deviceErrorResponse.getString("errorCode");
                    result.errorMessage = deviceErrorResponse.getString("errorMessage");     

                    errorMessageLog = "[" + result.errorCode + "] " + result.errorMessage;
                } catch (JSONException j) {        
                } 

                try {
                    orderMultipleErrorResponse = createOrderResult.getJSONArray("orderErrorResponse");
                    orderErrorResponse = orderMultipleErrorResponse.getJSONObject(0);
                    
                    result.result = false;
                    result.errorCode = orderErrorResponse.getString("errorCode");
                    result.errorMessage = orderErrorResponse.getString("errorMessage");     

                    errorMessageLog = "[" + result.errorCode + "] " + result.errorMessage;
                } catch (JSONException j) {        
                }                 
                
                try {
                    orderErrorResponse = createOrderResult.getJSONObject("orderErrorResponse");
                    
                    result.result = false;
                    result.errorCode = orderErrorResponse.getString("errorCode");
                    result.errorMessage = orderErrorResponse.getString("errorMessage");     

                    errorMessageLog = "[" + result.errorCode + "] " + result.errorMessage;
                } catch (JSONException j) {   
                }  
            
        } catch (Exception e) {
            e.printStackTrace(System.out);
            
            result.result = false;
            result.errorMessage = "Error occurs in verifying order by AppleCare Connect.";
            
            errorMessageLog = e.getMessage();
        } finally {
            try {   
                try {
                    content = param.toString();
                    contentResponse = createOrderResult.toString();         
                } catch (Exception e) {
                }

                writeLog("AppleCareConnectInterface.createOrder", invoiceNo, content, errorMessageLog, contentResponse);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }            
        }        
        
        return result;
    }
    
    public CancelResult cancelOrder(String invoiceNo, String imei) throws Exception {        
        CancelResult result = new CancelResult();

        String errorMessageLog = ""; 
        String content = "";
        String contentResponse = "";
          
        String sCancellationDate = sdf.format(new Date());

        JSONObject cancelOrderResult = new JSONObject();
        JSONObject param = new JSONObject();
        JSONObject requestContext = new JSONObject();

        JSONObject cancelOrderResponse = new JSONObject();
        JSONObject orderErrorResponse = new JSONObject();
        JSONArray orderMultipleErrorResponse = new JSONArray();

        String purchaseOrder = StringHelper.trim(invoiceNo).length() > 2 ? invoiceNo.substring(0, 2) + "/" + invoiceNo.substring(2):"";
        
        try {
            
                requestContext.put("shipTo", shipTo);
                requestContext.put("timeZone", TIME_ZONE);
                requestContext.put("langCode", LANG_CODE);

                param.put("requestContext", requestContext);
                param.put("deviceId", imei);
                param.put("cancellationDate", sCancellationDate);
                param.put("purchaseOrderNumber", purchaseOrder);
               
                cancelOrderResult = readJsonFromUrlByPostMethod(CANCEL_ORDER_URL, "", param);

                try {
                    cancelOrderResponse = cancelOrderResult.getJSONObject("cancelOrderResponse");
                    
                    result.result = true;
                    result.confirmationNumber = cancelOrderResponse.getString("agreementNumber");           
                    result.errorCode = "";
                    result.errorMessage = "";
                    errorMessageLog = "";                   
                } catch (JSONException j) {
                    result.result = false;
                    result.errorMessage = "Error occurs in cancelling order by AppleCare Connect.";
                    
                    errorMessageLog = j.getMessage();            
                }                

                try {
                    orderMultipleErrorResponse = cancelOrderResult.getJSONArray("orderErrorResponse");
                    orderErrorResponse = orderMultipleErrorResponse.getJSONObject(0);
                    
                    result.result = false;
                    result.errorCode = orderErrorResponse.getString("errorCode");
                    result.errorMessage = orderErrorResponse.getString("errorMessage");     

                    errorMessageLog = "[" + result.errorCode + "] " + result.errorMessage;
                } catch (JSONException j) {        
                }     
                
                try {
                    orderErrorResponse = cancelOrderResult.getJSONObject("orderErrorResponse");
                    
                    result.result = false;
                    result.errorCode = orderErrorResponse.getString("errorCode");
                    result.errorMessage = orderErrorResponse.getString("errorMessage");     

                    errorMessageLog = "[" + result.errorCode + "] " + result.errorMessage;
                } catch (JSONException j) {        
                }
            
        } catch (Exception e) {
            e.printStackTrace(System.out);
            
            result.result = false;
            result.errorMessage = "Error occurs in cancelling order by AppleCare Connect.";
            
            errorMessageLog = e.getMessage();
        } finally {
            try {   
                try {
                    content = param.toString();
                    contentResponse = cancelOrderResult.toString();         
                } catch (Exception e) {
                }

                writeLog("AppleCareConnectInterface.cancelOrder", invoiceNo, content, errorMessageLog, contentResponse);
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }            
        }        
        
        return result;        
    }

    public class VerifyResult {
        private boolean result = false;
        private String confirmationNumber = "";
        private String totalAmount = "";
        private String coverageDurationStatement = "";
        private String errorCode = "";
        private String errorMessage = "";
        
        public VerifyResult(){
            super();
        }
        
        public boolean getResult() {
            return result;
        }
        
        public String getConfirmationNumber() {
            return confirmationNumber;
        }
        
        public String getTotalAmount() {
            return totalAmount;
        }        
        
        public String getCoverageDurationStatement() {
            return coverageDurationStatement;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }

    }    
    
    public class OrderResult {
        private boolean result = false;
        private String confirmationNumber = "";
        private String totalAmount = "";
        private String coverageDurationStatement = "";
        private String errorCode = "";
        private String errorMessage = "";
        
        public boolean getResult() {
            return result;
        }
        
        public String getConfirmationNumber() {
            return confirmationNumber;
        }
        
        public String getTotalAmount() {
            return totalAmount;
        }        
        
        public String getCoverageDurationStatement() {
            return coverageDurationStatement;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
    
    public class CancelResult {
        private boolean result = false;
        private String confirmationNumber = "";
        private String errorCode = "";
        private String errorMessage = "";
        
        public boolean getResult() {
            return result;
        }
        
        public String getConfirmationNumber() {
            return confirmationNumber;
        }
        
        public String getErrorCode() {
            return errorCode;
        }
        
        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
