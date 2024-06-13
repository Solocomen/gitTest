package epc.epcsalesapi.fes;

import epc.epcsalesapi.fes.bean.EpcPushNotificationReq;
import epc.epcsalesapi.fes.bean.EpcPushNotificationResult;
import epc.epcsalesapi.fes.bean.FesPushNotificationResult;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.*;


@Service
public class EpcIphonePushHandler {

    private final Logger logger = LoggerFactory.getLogger(EpcIphonePushHandler.class);

    @Autowired
    private DataSource fesDataSource;

    @Autowired
    private EpcSecurityHelper epcSecurityHelper;
    
    public String getToken(String machineId, String salesman){    	
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtDel = null;
        ResultSet rset = null;
        String sql = "";
        String str = "";

        try {
            conn = fesDataSource.getConnection();

            sql = "select token_id from sa_iphone_token where device_id = ? and device_type = ? ";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, machineId);
            pstmt.setString(2, "IPAD");
            
            rset = pstmt.executeQuery();
            if (rset.next()) {
            	str = StringHelper.trim(rset.getString("token_id"));
            } 
            
            sql = "delete from sa_iphone_apps_barcode where fes_login = ? and salesman = ? ";
            pstmtDel = conn.prepareStatement(sql);
            pstmtDel.setString(1, machineId);
            pstmtDel.setString(2, salesman);
            pstmtDel.executeUpdate();            
            
            rset.close(); rset = null;
            pstmt.close(); pstmt = null;
            pstmtDel.close(); pstmtDel = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return str;
    }



    public EpcPushNotificationResult push(EpcPushNotificationReq req){
    	EpcPushNotificationResult epcPushNotiResult = new EpcPushNotificationResult();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("FES_PUSH_NOTIFICATION_LINK");
        FesPushNotificationResult fesPushResult = null;
        String logStr = "[applePush]";
        String tmpLogStr = "";
        String machineId = req.getMachineId();
        String salesman = req.getSalesman();
        
        try {
        	
        	String token = getToken(machineId, salesman);
        	if ("".equals(token)) {
        		epcPushNotiResult.setResult("FAIL");
            	epcPushNotiResult.setErrMsg("Device ID Invalid");
            	return epcPushNotiResult;
        	}

        	apiUrl = apiUrl + "?deviceToken="+ token +"&saLogId=0&docTypeId=0&fesLogin="+ machineId +"&salesman="+ salesman +"&functionId=2&subrNum=00000000&language=E&userLanguage=&isSAForm=N&opt=";
            
        	
        	tmpLogStr = "request url:" + apiUrl;
logger.info("{}{}", logStr, tmpLogStr);

            responseEntity = restTemplate.getForEntity(apiUrl, String.class);

            tmpLogStr = "result json:" + objectMapper.writeValueAsString(responseEntity);
logger.info("{}{}", logStr, tmpLogStr);

            if(responseEntity.getStatusCodeValue() == 200) {
            	fesPushResult = objectMapper.readValue(responseEntity.getBody(), FesPushNotificationResult.class);
                
                if("SUCCESS".equals(fesPushResult.getResultCode())) {
                    // success
                	epcPushNotiResult.setResult("SUCCESS");
                } else {
                    // error
                	epcPushNotiResult.setResult("FAIL");
                	epcPushNotiResult.setErrMsg(fesPushResult.getResultMessage());
                }
            } else {
                // error
            	epcPushNotiResult.setResult("FAIL");
                epcPushNotiResult.setErrMsg("http status:" + responseEntity.getStatusCodeValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            epcPushNotiResult.setResult("FAIL");
            epcPushNotiResult.setErrMsg(e.getMessage());
        } finally {
        	
        }
        return epcPushNotiResult;
    }

    public EpcPushNotificationResult getScanResult(String machineId, String salesman){
    	EpcPushNotificationResult epcPushNotiResult = new EpcPushNotificationResult();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String str = "";

        try {
            conn = fesDataSource.getConnection();

            sql = "select bar_code from sa_iphone_apps_barcode a where fes_login = ? and salesman = ? ";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, machineId);
            pstmt.setString(2, salesman);
            
            rset = pstmt.executeQuery();
            if (rset.next()) {
            	epcPushNotiResult.setBarcode(StringHelper.trim(rset.getString("bar_code")));
            }
            rset.close(); rset = null;
            pstmt.close(); pstmt = null;
            epcPushNotiResult.setResult("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            epcPushNotiResult.setResult("FAIL");
            epcPushNotiResult.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return epcPushNotiResult;
    }

}
