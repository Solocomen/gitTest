package epc.epcsalesapi.notification;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.notification.bean.EpcNotification;
import epc.epcsalesapi.notification.bean.EpcNotificationCharacteristic;
import epc.epcsalesapi.sales.EpcMsgHandler;
import epc.epcsalesapi.sales.bean.EpcNotificationMessage;

@Service
public class EpcNotificationHandler {
	
	private final Logger logger = LoggerFactory.getLogger(EpcNotificationHandler.class);

	private final DataSource epcDataSource;
	private final EpcMsgHandler epcMsgHandler;
	
	public EpcNotificationHandler(DataSource epcDataSource, EpcMsgHandler epcMsgHandler) {
        this.epcDataSource = epcDataSource;
        this.epcMsgHandler = epcMsgHandler;
    }
	
	public void sendNotification(List<EpcNotification> epcNotificationList) {
		boolean isValid = true;
		String errMsg = "";
		int sigmaOrderId = 0;
		String acctNum = "";
		String msgTemplateId = "";
		ArrayList<EpcNotificationCharacteristic> characteristicsList = null;
		String logStr = "[sendNotification]";
		
        try {
            // basic checking
            if(sigmaOrderId <= 0) {
                isValid = false;
                errMsg += "sigmaOrderId is empty. ";
            }
            
            if("".equals(msgTemplateId)) {
                isValid = false;
                errMsg += "msgTemplateId is empty. ";
            }
            // end of basic checking


            for(EpcNotification epcNotification: epcNotificationList) {
                sigmaOrderId = epcNotification.getSigmaOrderId();
                acctNum = StringHelper.trim(epcNotification.getAcctNum());
                msgTemplateId = StringHelper.trim(epcNotification.getMsgTemplateId());
                characteristicsList = epcNotification.getCharacteristics();
                
                logStr += "[sigmaOrderId:" + sigmaOrderId + "][acctNum:" + acctNum + "][msgTemplateId:" + msgTemplateId + "] ";

                
                
                epcNotification.setResult("SUCCESS");
                epcNotification.setErrMsg("");
            }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	


	public ArrayList<EpcNotificationMessage> getNotifications(int orderId) {
		ArrayList<EpcNotificationMessage> nList = new ArrayList<>();
		EpcNotificationMessage epcNotificationMessage = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            conn = epcDataSource.getConnection();
            sql = "select a.msg_id, a.send_type, a.to_addr, a.content, a.complete_date, a.status, a.order_id, a.request_id, a.request_system, b.key_str2 " +
                  "  from epc_msg_queue a, epc_control_tbl b" +
                  " where order_id = ? " + 
                  " and b.rec_type = 'NOTI_REQ_SYS_MAP' " +
                  " and b.key_str5 = 'A' " +
                  " and a.request_system = b.key_str1 order by a.create_date asc";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            while(rset.next()) {
            	epcNotificationMessage = new EpcNotificationMessage();
            	epcNotificationMessage.setMsgId(StringHelper.trim(rset.getString("msg_id")));
            	epcNotificationMessage.setSendType(StringHelper.trim(rset.getString("send_type")));
            	epcNotificationMessage.setToAddr(StringHelper.trim(rset.getString("to_addr")));
            	epcNotificationMessage.setContent(StringHelper.trim(rset.getString("content")));
            	epcNotificationMessage.setCompleteDate(StringHelper.trim(rset.getString("complete_date")));
            	epcNotificationMessage.setStatus(StringHelper.trim(rset.getString("status")));
            	epcNotificationMessage.setOrderId(StringHelper.trim(rset.getString("order_id")));
            	epcNotificationMessage.setRequestId(StringHelper.trim(rset.getString("request_id")));
            	epcNotificationMessage.setRequestSystem(StringHelper.trim(rset.getString("request_system")));
            	epcNotificationMessage.setRequestSystemDesc(StringHelper.trim(rset.getString("key_str2")));

                nList.add(epcNotificationMessage);
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return nList;
	}
	
	public EpcNotificationMessage resendMsg(String msgId) {
    	Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtGetOld = null;
        ResultSet rset = null;
        String sql = "";
        EpcNotificationMessage epcNotificationMessage = null;
        String currentContactNo = "";
        String currentEmail = "";
        String orderId = "";
        
        try {
        	conn = epcDataSource.getConnection();
            sql = "select send_type, sender_name, sender_email, to_addr, cc_addr, bcc_addr, subject, content, order_id, request_id, request_system " +
                  "  from epc_msg_queue " +
                  " where msg_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, msgId);
            rset = pstmt.executeQuery();
            while(rset.next()) {
            	orderId = StringHelper.trim(rset.getString("order_id"));
            	epcNotificationMessage = new EpcNotificationMessage();
            	epcNotificationMessage.setSendType(StringHelper.trim(rset.getString("send_type")));
            	epcNotificationMessage.setSenderName(StringHelper.trim(rset.getString("sender_name")));
                epcNotificationMessage.setSenderEmail(StringHelper.trim(rset.getString("sender_email")));
            	epcNotificationMessage.setToAddr(StringHelper.trim(rset.getString("to_addr")));
            	epcNotificationMessage.setCcAddr(StringHelper.trim(rset.getString("cc_addr")));
            	epcNotificationMessage.setBccAddr(StringHelper.trim(rset.getString("bcc_addr")));
            	epcNotificationMessage.setSubject(StringHelper.trim(rset.getString("subject")));
            	epcNotificationMessage.setContent(StringHelper.trim(rset.getString("content")));
            	epcNotificationMessage.setOrderId(orderId);
            	epcNotificationMessage.setRequestId(StringHelper.trim(rset.getString("request_id")));
            	epcNotificationMessage.setRequestSystem(StringHelper.trim(rset.getString("request_system")));

            } rset.close();
            pstmt.close();
            
            // get old contact
        	sql = "select contact_no, contact_email from epc_order where order_id = ? ";
            pstmtGetOld = conn.prepareStatement(sql);
            pstmtGetOld.setInt(1, Integer.valueOf(orderId)); // order_id
            rset = pstmtGetOld.executeQuery();
            if(rset.next()) {
            	currentContactNo = StringHelper.trim(rset.getString("contact_no"));
            	currentEmail = StringHelper.trim(rset.getString("contact_email"));
            } rset.close();
            pstmtGetOld.close();
            
            if(!"".equals(currentEmail)) {
                try {
                	currentEmail = StringHelper.trim(EpcCrypto.dGet(currentEmail, "utf-8"));
                } catch (Exception e) {
                	currentEmail = "";
                }
            }
            
            if ("SMS".equals(epcNotificationMessage.getSendType())) {
            	epcNotificationMessage.setToAddr(currentContactNo);
            } else {
            	epcNotificationMessage.setToAddr(currentEmail);
            }
            
            epcMsgHandler.createMsg(conn, epcNotificationMessage);
	        
        } catch (Exception e) {
            e.printStackTrace();
            epcNotificationMessage.setResult("FAIL");
            epcNotificationMessage.setErrMsg(e.getMessage());
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcNotificationMessage;
    }
}
