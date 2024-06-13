package epc.epcsalesapi.sales;

import java.io.ByteArrayInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.reactive.function.client.WebClient;

import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcNotificationAttachment;
import epc.epcsalesapi.sales.bean.EpcNotificationMessage;
import epc.epcsalesapi.sales.bean.EpcSendMsgRequest;
import epc.epcsalesapi.sales.bean.EpcSendShoppingBag;
import java.net.URI;
import java.util.HashMap;
import java.util.Properties;
//import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class EpcMsgHandler {
    
	private final Logger logger = LoggerFactory.getLogger(EpcMsgHandler.class);
    
    @Autowired
    private DataSource epcDataSource;
    
    @Autowired
    private EpcQrCodeHandler epcQrCodeHandler;
    
    @Autowired
    private EpcSecurityHelper epcSecurityHelper;

    public String sendMsg(EpcSendMsgRequest request) {
    	Connection conn = null;
    	String result="";
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            result=sendMsg(conn, request);

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) { conn.rollback(); } } catch (Exception ignore) {}
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
		return result;
    }
    // added by Danny Chan on 2022-11-30 (SHK Point Payment Enhancement): start
    /**
     * Updated to a common method
     * @author lincolnliu 
     * @param conn
     * @param request
     * @return
     */
    public String sendMsg(Connection conn, EpcSendMsgRequest request) {
        String result = "";
        
        String recipient = StringHelper.trim(request.getRecipient());
        String senderName = StringHelper.trim(request.getSenderName());
        String senderEmail = StringHelper.trim(request.getSenderEmail());
        String requestId = StringHelper.trim(request.getRequestId());
        String language = StringHelper.trim(request.getLanguage());
        String sendType = StringHelper.trim(request.getSendType());
        String orderId = StringHelper.trim(request.getOrderId());
        String templateType = StringHelper.trim(request.getTemplateType());
        HashMap<?, ?> params =  request.getParams();

        if (!sendType.equals(EpcSendMsgRequest.EMAIL) && !sendType.equals(EpcSendMsgRequest.SMS)) {
            result = "{\"resultcode\": 1, \"resultMsg\": \"Invalid sendType\"}";
            return result;
        }
        
        EpcNotificationMessage msg = initMsg(templateType, language, sendType);
        
        if (msg==null) {
            result = "{\"resultcode\": 2, \"resultMsg\": \"The template type cannot be found.\"}";
            return result;			
        }
        
        msg.setToAddr(recipient);
        msg.setRequestId(requestId);
        msg.setOrderId(orderId);
        msg.setCcAddr(request.getCpRecipient());
        
        if (sendType.equals(EpcSendMsgRequest.EMAIL)) {
            //if(senderEmail!=null)
            //msg.setSenderEmail(senderEmail);
            //if(senderName!=null)
            //msg.setSenderName(senderName);
            if (!"".equals(senderEmail)) msg.setSenderEmail(senderEmail);

            if (!"".equals(senderName)) msg.setSenderName(senderName);
        } else {
            //if(msg.getSenderEmail()==null)
            //    msg.setSenderEmail(getTemplate("SMS_OA"));
            if(!"".equals(senderName)) msg.setSenderName(senderName);
        }
        
        if (params!=null) {
            Object keys[] = params.keySet().toArray();
        
            String content = msg.getContent();
            String subject =msg.getSubject();
            for (int i=0; i<keys.length; i++) {
                String value = (String)params.get(keys[i]);
                // globally replace the $ character to \$, to prevent incorrect group string replacement by escaping $
                value = value.replaceAll("[$]", "\\\\$0");
                System.out.println( i + ": key = " + keys[i] + ", value = " + value );
                        
                content = content.replaceAll("\\{" + keys[i] + "\\}", value);
                if(subject!=null)
                subject = subject.replaceAll("\\{" + keys[i] + "\\}", value);
            }
        
            msg.setContent(content);
            msg.setSubject(subject);
        }
        
        createMsg(conn, msg);
        
        if (msg.getResult().equals("FAIL")) {
            result = "{\"resultcode\": 2, \"resultMsg\": \"" + msg.getErrMsg() + "\"}";
        } else {
            result = "{\"resultcode\": 0, \"resultMsg\": \"Success\"}";
        }
        
        return result;
    }
    
    public EpcNotificationMessage initMsg(String templateType, String language, String sendType ) {
        if (!sendType.equals(EpcSendMsgRequest.EMAIL) && !sendType.equals(EpcSendMsgRequest.SMS)) {
            return null;
        }
        
        Properties prop_params = new Properties();
        
        Connection conn = null;
        
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        String sql = "SELECT msg_type, msg_content, lang FROM gp_msg_template WHERE msg_type like ? || '_%'";
        
        try {
            conn = epcDataSource.getConnection();
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, templateType);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                String msg_type = rs.getString(1).replace(templateType + "_", "");

//                char clobVal[] = new char[(int) rs.getClob(2).length()];
//                Reader r = rs.getClob(2).getCharacterStream();
//                r.read(clobVal);
//                StringWriter sw = new StringWriter();
//                sw.write(clobVal);
//
//                String msg_content = sw.toString();
                String msg_content = StringHelper.trim(rs.getString(2));
                
                String lang = rs.getString(3);
                
                if (lang==null) {
                    lang = "E";
                }
                
                prop_params.setProperty(msg_type + "_" + lang, msg_content);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {if(rs!=null)rs.close();} catch (Exception ignore) {}
            try {if(pstmt!=null)pstmt.close();} catch (Exception ignore) {}
            try { if (conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        
        EpcNotificationMessage msg = new EpcNotificationMessage();
        
        msg.setSendType(sendType);
        msg.setRequestSystem( templateType );
        
        if (sendType.contains(EpcSendMsgRequest.EMAIL)) {
            if (prop_params.getProperty("EMAIL_SENDER_ADDR_" + language)!=null) {
                msg.setSenderEmail( prop_params.getProperty("EMAIL_SENDER_ADDR_" + language) );
            } else {
                msg.setSenderEmail( prop_params.getProperty("EMAIL_SENDER_ADDR_E") );
            }

            if (prop_params.getProperty("EMAIL_SENDER_" + language)!=null) {
                msg.setSenderName( prop_params.getProperty("EMAIL_SENDER_" + language) );
            } else {
                msg.setSenderName( prop_params.getProperty("EMAIL_SENDER_E") );
            }
            
            if (prop_params.getProperty("EMAIL_SUBJECT_" + language)!=null) {
                msg.setSubject( prop_params.getProperty("EMAIL_SUBJECT_" + language) );
            } else {
                msg.setSubject( prop_params.getProperty("EMAIL_SUBJECT_E") );
            }
            
            if (prop_params.getProperty("EMAIL_" + language)!=null) {
                msg.setContent( prop_params.getProperty("EMAIL_" + language) );
            } else {
                msg.setContent( prop_params.getProperty("EMAIL_E") );
            }
            
            if (msg.getSenderEmail()==null || msg.getSenderName()==null || msg.getSubject()==null || msg.getContent()==null ) {
                msg = null;
            }
        } else {
            if (prop_params.getProperty("SMS_" + language)!=null) {
                msg.setContent( prop_params.getProperty("SMS_" + language) );
            } else {
                msg.setContent( prop_params.getProperty("SMS_E") );
            }
            if (prop_params.getProperty("SMS_OA_" + language)!=null) {
                msg.setSenderEmail( prop_params.getProperty("SMS_OA_" + language) );
            } else {
                msg.setSenderEmail( prop_params.getProperty("SMS_OA_E") );
            }
            if ( msg.getContent()==null ) {
                msg=  null;
            }
        }
        
        return msg;
    }
    // added by Danny Chan on 2022-11-30 (SHK Point Payment Enhancement): end

    public void createMsg(EpcNotificationMessage epcNotificationMessage) {
        Connection conn = null;

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            createMsg(conn, epcNotificationMessage);

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();

            epcNotificationMessage.setResult("FAIL");
            epcNotificationMessage.setErrMsg(e.getMessage());

            try { if (conn != null) { conn.rollback(); } } catch (Exception ignore) {}
        } finally {
            try { if (conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
    }
        
    public void createMsg(Connection conn, EpcNotificationMessage epcNotificationMessage) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        boolean isValid = true;
        String errMsg = "";
        String sendType = "";
        String senderName = "";
        String senderEmail = "";
        String toAddr = "";
        String ccAddr = "";
        String bccAddr = "";
        String subject = "";
        String content = "";
        String requestSystem = "";
        String requestId = "";
        String msgId = "";
        ArrayList<EpcNotificationAttachment> attachmentList = null;
        String orderId = "";		// added by Danny Chan on 2022-12-1 (SHK Point Payment Enhancement) 
        
        try {
            sendType = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcNotificationMessage.getSendType()));
            senderName = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcNotificationMessage.getSenderName()));
            senderEmail = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcNotificationMessage.getSenderEmail()));
            toAddr = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcNotificationMessage.getToAddr()));
            ccAddr = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcNotificationMessage.getCcAddr()));
            bccAddr = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcNotificationMessage.getBccAddr()));
            subject = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcNotificationMessage.getSubject()));
            content = StringHelper.trim(epcNotificationMessage.getContent());
            requestSystem = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcNotificationMessage.getRequestSystem()));
            requestId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcNotificationMessage.getRequestId()));
            attachmentList = epcNotificationMessage.getAttachmentList();
            orderId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcNotificationMessage.getOrderId()));	// added by Danny Chan on 2022-12-1 (SHK Point Payment Enhancement) 
            
            // basic checking
            if(!EpcSendMsgRequest.EMAIL.equals(sendType) && !EpcSendMsgRequest.SMS.equals(sendType)) {
                isValid = false;
                errMsg += "invalid send type [" + sendType + "]. ";
            }
            
            if("".equals(requestSystem)) {
                isValid = false;
                errMsg += "request system is empty. ";
            }
            
            if("".equals(requestId)) {
                isValid = false;
                errMsg += "request id is empty. ";
            }
            
            if(EpcSendMsgRequest.EMAIL.equals(sendType)) {
                if("".equals(senderName)) {
                    isValid = false;
                    errMsg += "sender name is empty. ";
                }
                
                if("".equals(senderEmail)) {
                    isValid = false;
                    errMsg += "sender email is empty. ";
                }
                
                if("".equals(toAddr)) {
                    isValid = false;
                    errMsg += "to addr list is empty. ";
                }
                
                if("".equals(subject)) {
                    isValid = false;
                    errMsg += "email subject is empty. ";
                }
                
                if("".equals(content)) {
                    isValid = false;
                    errMsg += "email content is empty. ";
                }
            } else if(EpcSendMsgRequest.SMS.equals(sendType)) {
                if("".equals(senderEmail)) {
                    isValid = false;
                    errMsg += "OA is empty. ";
                }
                
                if("".equals(toAddr)) {
                    isValid = false;
                    errMsg += "DA is empty. ";
                }
                
                if("".equals(content)) {
                    isValid = false;
                    errMsg += "sms content is empty. ";
                }
            }
            
            if(attachmentList != null && attachmentList.size() > 0) {
                for(EpcNotificationAttachment e : attachmentList) {
                    if("".equals(StringHelper.trim(e.getCid()))) {
                        isValid = false;
                        errMsg += "cid of attachment is empty. ";
                    }
                    
                    if("".equals(StringHelper.trim(e.getContentType()))) {
                        isValid = false;
                        errMsg += "content type of attachment is empty. ";
                    }
                    
                    if(e.getContent() == null) {
                        isValid = false;
                        errMsg += "content of attachment is empty. ";
                    }
                }
            }
            // end of basic checking
            
            
            if(isValid) {
                sql = "select lpad(epc_msg_id_seq.nextval,20,'0') from dual ";
                pstmt = conn.prepareStatement(sql);
                rset = pstmt.executeQuery();
                if(rset.next()) {
                    msgId = epcSecurityHelper.validateId(StringHelper.trim(rset.getString(1)));
                } rset.close();
                pstmt.close();
                
                
                sql = "insert into epc_msg_queue ( " +
                      "  msg_id, send_type, sender_name, sender_email, to_addr, " + 
                      "  cc_addr, bcc_addr, subject, content, status, " + 
                      "  request_system, request_id, create_date, order_id " +
                      ") values ( " +
                      "  ?,?,?,?,?, " +
                      "  ?,?,?,?,?, " +
                      "  ?,?,sysdate, ? " +
                      ") ";
                pstmt = conn.prepareStatement(sql);
                
                pstmt.setString(1, msgId); // msg_id
                pstmt.setString(2, sendType); // send_type
                pstmt.setString(3, senderName); // sender_name
                pstmt.setString(4, senderEmail); // sender_email
                pstmt.setString(5, toAddr); // to_addr
                pstmt.setString(6, ccAddr); // cc_addr
                pstmt.setString(7, bccAddr); // bcc_addr
                pstmt.setString(8, subject); // subject
                pstmt.setString(9, content); // content
                pstmt.setString(10, "W"); // status
                pstmt.setString(11, requestSystem); // request_system
                pstmt.setString(12, requestId); // request_id
                pstmt.setString(13, orderId);    // added by Danny Chan on 2022-12-1 (SHK Point Payment Enhancement) 

                pstmt.executeUpdate();
                pstmt.close();
                
                
                if(attachmentList != null && attachmentList.size() > 0) {
                    sql = "insert into epc_msg_attach ( " +
                          "  attach_id, attach_cid, content, content_type, msg_id " +
                          ") values ( " +
                          "  'A' || lpad(epc_msg_id_seq.nextval,19,'0'),?,?,?,? " +
                          ") ";
                    pstmt = conn.prepareStatement(sql);
                    
                    for(EpcNotificationAttachment e : attachmentList) {
                        pstmt.setString(1, StringHelper.trim(e.getCid())); // attach_cid
                        pstmt.setBinaryStream(2, new ByteArrayInputStream(e.getContent()), e.getContent().length); // content
                        pstmt.setString(3, StringHelper.trim(e.getContentType())); // content_type
                        pstmt.setString(4, msgId); // msg_id
                        pstmt.addBatch();
                    }
                    pstmt.executeBatch();
                    pstmt.close();
                }


                epcNotificationMessage.setResult("SUCCESS");
                epcNotificationMessage.setMsgId(msgId);
            } else {
                epcNotificationMessage.setResult("FAIL");
                epcNotificationMessage.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcNotificationMessage.setResult("FAIL");
            epcNotificationMessage.setErrMsg(e.getMessage());
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
        }
    }

    
// kerry testing only
    public ArrayList<EpcNotificationAttachment> getAttach() {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<EpcNotificationAttachment> attachmentList = new ArrayList<EpcNotificationAttachment>();
        EpcNotificationAttachment epcNotificationAttachment = null;
        Blob b = null;
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "select * from epc_msg_attach where email_id = -1 ";
            pstmt = conn.prepareStatement(sql);
            rset = pstmt.executeQuery();
            while(rset.next()) {
                epcNotificationAttachment = new EpcNotificationAttachment();
                epcNotificationAttachment.setCid(rset.getString("attach_cid"));
                b = rset.getBlob("content");
                epcNotificationAttachment.setContent(b.getBytes(1, (int)b.length()));
                epcNotificationAttachment.setContentType(rset.getString("content_type"));
                
                attachmentList.add(epcNotificationAttachment);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return attachmentList;
    }
// end of kerry testing only

    
    public void sendShoppingBag(EpcSendShoppingBag epcSendShoppingBag) {
        EpcNotificationMessage epcNotificationMessage = null;
        String url = EpcProperty.getValue("ONLINE_STORE_LINK");
        String urlParam = "";
        String encryptedUrlParam = "";
        boolean isValid = true;
        String errMsg = "";
        String custId = "";
        String orderReference = "";
        int orderId = 0;
        String custEmail = "";
        String lang = "";
        byte[] qrCodeArray = null;
        String senderName = "";
        String senderEmail = "";
        String emailSubject = "";
        String emailContent = "";
        String cid = "";
        ArrayList<EpcNotificationAttachment> attachmentList = null;
        EpcNotificationAttachment epcNotificationAttachment = null;
        
        
        try {
            custId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSendShoppingBag.getCustId()));
            orderReference = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSendShoppingBag.getOrderReference()));
            orderId = epcSendShoppingBag.getOrderId();
            custEmail = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSendShoppingBag.getCustEmail()));
            lang = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSendShoppingBag.getLang()));
            if("".equals(lang)) {
                lang = "E"; // default
            }
            
            
            // basic checking
            if("".equals(custId)) {
                isValid = false;
                errMsg += "cust id is empty. ";
            }
            
            if("".equals(orderReference)) {
                isValid = false;
                errMsg += "order reference is empty. ";
            }
            
            if(orderId <= 0) {
                isValid = false;
                errMsg += "order id is invalid. ";
            }
            
            if("".equals(custEmail)) {
                isValid = false;
                errMsg += "cust email is empty. ";
            }
            
            if(!"E".equals(lang) && !"C".equals(lang)) {
                isValid = false;
                errMsg += "lang is invalid. ";
            }
            // end of basic checking
            
            
            if(isValid) {
                // compile parameters needed & online store url
                urlParam = "{ \"custId\"=\"" + custId + "\", \"orderReference\" = \"" + orderReference + "\" }";
                encryptedUrlParam = EpcCrypto.eGet(urlParam, "utf-8");
                url += "?s=" + encryptedUrlParam;
                
                // compile QR code
                qrCodeArray = epcQrCodeHandler.createQRCode(url);
                
                // get email template & sender info
                // ...
                senderName = "";
                senderEmail = "";
                emailSubject = "";
                emailContent = "";
// testing only, kerrytsang, 20211012
senderName = "SmarTone";
senderEmail = "comms@service.smartone.com";
emailSubject = "SmarTone Order (testing)";
emailContent = "<html>Your order: [EPC_ORDER_REFERENCE]<br><img src='cid:[EPC_CID]' /></html>";
// end of testing only, kerrytsang, 20211012
                cid = "EPC_ORDER_" + orderId;
                
                // compile email content
                emailContent = emailContent.replaceAll("\\[EPC_ORDER_REFERENCE\\]", orderReference);
                emailContent = emailContent.replaceAll("\\[EPC_CID\\]", cid);
                
                // create an email request
                epcNotificationMessage = new EpcNotificationMessage();
                epcNotificationMessage.setSendType(EpcSendMsgRequest.EMAIL);
                epcNotificationMessage.setSenderName(senderName);
                epcNotificationMessage.setSenderEmail(senderEmail);
                epcNotificationMessage.setToAddr(custEmail);
                epcNotificationMessage.setSubject(emailSubject);
                epcNotificationMessage.setContent(emailContent);
                epcNotificationMessage.setRequestSystem("EPCSALES");
                epcNotificationMessage.setRequestId("" + orderId);
                
                attachmentList = new ArrayList<EpcNotificationAttachment>();
                epcNotificationMessage.setAttachmentList(attachmentList);
                
                epcNotificationAttachment = new EpcNotificationAttachment();
                epcNotificationAttachment.setCid(cid);
                epcNotificationAttachment.setContent(qrCodeArray);
                epcNotificationAttachment.setContentType("image/jpeg");
                attachmentList.add(epcNotificationAttachment);
                
                createMsg(epcNotificationMessage);
                if("SUCCESS".equals(epcNotificationMessage.getResult())) {
                    epcSendShoppingBag.setResult("SUCCESS");				
                } else {
                    epcSendShoppingBag.setResult("FAIL");
                    epcSendShoppingBag.setErrMsg(epcNotificationMessage.getErrMsg());
                }
            } else {
                epcSendShoppingBag.setResult("FAIL");
                epcSendShoppingBag.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcSendShoppingBag.setResult("FAIL");
            epcSendShoppingBag.setErrMsg(e.getMessage());
        } finally {
        }
    }


    /***
     * create a thread to send confirmation email
     *  invoked by placeOrder()
     */
    public void sendConfirmationEmailAsync(String custId, String orderId) {
        String[] strArray = new String[2];
        strArray[0] = custId;
        strArray[1] = orderId;

        try {
            CompletableFuture.completedFuture(strArray).thenApplyAsync(s -> sendConfirmationEmail(s[0], s[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String sendConfirmationEmail(String custId, String orderId) {
        String apiUrl = EpcProperty.getValue("EPC_CONFIRMATION_EMAIL_LINK") + "?cid=" + custId + "&oid=" + orderId;
        String logStr = "[sendConfirmationEmail][custId:" + custId + "][orderId:" + orderId + "] ";
        String rtn = "";

        try {
//            WebClient.create(apiUrl)
//            .post()
//            .retrieve()
//            .bodyToMono(String.class);
            rtn = new RestTemplate().postForObject(new URI(apiUrl), null, String.class);
logger.info("{}{}{}", logStr, "result:", rtn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "OK";
    }
    
    public String getTemplate(String msgType) {
        String content = "";
        PreparedStatement pstmt = null;
        Connection conn = null;
        ResultSet rset = null;
        String sql = "";

        try {
        	conn = epcDataSource.getConnection();
            sql = "select msg_content  " +
                  "  from gp_msg_template " +
                  " where msg_type = ? " +
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, msgType); // msg_type
            pstmt.setString(2, "A"); // status
            rset = pstmt.executeQuery();
            if(rset.next()) {
                content = StringHelper.trim(rset.getString("msg_content"));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return content;
    }
}
