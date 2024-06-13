package epc.epcsalesapi.sales;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcCharge;
import epc.epcsalesapi.sales.bean.EpcCreateReceipt;
import epc.epcsalesapi.sales.bean.EpcCreateReceiptResult;
import epc.epcsalesapi.sales.bean.EpcLoginChannel;
import epc.epcsalesapi.sales.bean.EpcPayment;
import epc.epcsalesapi.sales.bean.EpcRefundRecord;
import epc.epcsalesapi.sales.bean.EpcRefundRemark;
import epc.epcsalesapi.sales.bean.EpcSendMsgRequest;
import epc.epcsalesapi.sales.bean.RefundOrderRequest;
import epc.epcsalesapi.sales.bean.RefundRequest;
import epc.epcsalesapi.sales.bean.receipt.ReceiptCode;
import epc.epcsalesapi.stock.EpcStockHandler;

@Service
public class EpcRefundHandler {

    private final Logger logger = LoggerFactory.getLogger(EpcRefundHandler.class);

    @Autowired
    private DataSource epcDataSource;
    
    @Autowired
    private DataSource fesDataSource;
    
    @Autowired
    private EpcReceiptHandler epcReceiptHandler;

    @Autowired
    private EpcStockHandler epcStockHandler;

    @Autowired
    private EpcSecurityHelper epcSecurityHelper;

    @Autowired
    private EpcWaivingReportHandler epcWaivingReportHandler;
    
    @Autowired
    private EpcPaymentHandler epcPaymentHandler;
    
    @Autowired
    private EpcMsgHandler epcMsgHandler;
    
    @Autowired
    private EpcCancelHandler epcCancelHandler;
    
    @Autowired
    private EpcOrderHandler epcOrderHandler;
    
    private static final String EPC_ORDER_REFUND_SEQ="SELECT EPC_ORDER_REFUND_SEQ.nextval AS id FROM dual";
    
    private static final String EPC_REFUND_REF_NO_SEQ="SELECT epc_refund_ref_no_seq.nextval FROM dual";
    
    private static final String EPC_ORDER_REFUND="INSERT INTO EPC_ORDER_REFUND (REC_ID, REFUND_AMOUNT, REFUND_RECEIPT, REFUND_METHOD, CREATE_USER, CREATE_SALESMAN, CREATE_DATE, PAYEE_NAME, POSTAL_ADDR_1, POSTAL_ADDR_2, POSTAL_DISTRICT, POSTAL_AREA, MANUAL_REFUND, CASH_OUT_DATE,REFUND_REFERENCE_NO, CUST_ID, APPROVE_BY, WAIVE_FORM_CODE, CREATE_LOCATION,IS_DONE,MODIFY_USER,MODIFY_DATE,PAYMENT_ID) VALUES(?, ?, ?, ?, ?, ?, SYSDATE, ?, ?, ?, ?, ?, ?, ?,?, ?,?,?,?,?,?,SYSDATE,?) ";

    private static final String EPC_ORDER_REFUND_DETAIL="INSERT INTO EPC_ORDER_REFUND_DETAIL (DTL_ID, HDR_ID, CANCEL_ORDER_ID, CANCEL_RECEIPT_NO, CUST_NUM, SUBR_NUM, REFUND_AMOUNT) VALUES(EPC_ORDER_REFUND_DTL_SEQ.nextval, ?, ?, ?, ?, ?, ?)";

    /**
     * @author LincolnLiu
     * @param refundRequest
     * @throws Exception 
     */
    public void refundCancelOrder(RefundRequest refundRequest) throws Exception {
    	
    	verify(refundRequest);
    	
        String custId = refundRequest.getCustId();
        boolean isMarkedItem = false;
        String approveBy = epcSecurityHelper.encodeForSQL(StringHelper.trim(refundRequest.getApproveBy()));
        String waiveFormCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(refundRequest.getWaiveFormCode()));
        String location = epcSecurityHelper.encodeForSQL(StringHelper.trim(refundRequest.getLocation()));
        String iSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(refundRequest.getSalesman()));
        String iCreateUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(refundRequest.getCreateUser()));

        //Generate parameters for createReceipt
        EpcCreateReceipt createReceipt = new EpcCreateReceipt();
        createReceipt.setCustId(custId);
        createReceipt.setSalesman(iSalesman);
        createReceipt.setCreateUser(iCreateUser);
        createReceipt.setLocation(location);
        List<RefundOrderRequest> list = refundRequest.getOrderList();

        String logStr = "[refundCancelOrder][" + custId + "] ";
        String tmpLogStr = "";

        boolean isCreateWaivingReportRecord = false;
        
        Connection fesconnection=null;
        Connection connection=null;
        
        try{
        fesconnection=fesDataSource.getConnection();
        fesconnection.setAutoCommit(false);
        
        //Save data after create Receipt succeeds
       connection=epcDataSource.getConnection();
       connection.setAutoCommit(false);

		Date orderDate = null;
		try (PreparedStatement ps = connection.prepareStatement("SELECT PLACE_ORDER_DATE FROM  EPC_ORDER WHERE ORDER_ID=?")) {
			ps.setInt(1, refundRequest.getCancelOrderId());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					orderDate = rs.getDate(1);
				}
			}
		}
        String receiptNo =null;
        try (PreparedStatement ps = connection.prepareStatement("SELECT a.REFUND_RECEIPT FROM EPC_ORDER_REFUND a JOIN EPC_ORDER_REFUND_DETAIL b ON a.REC_ID =b.HDR_ID WHERE  b.CANCEL_RECEIPT_NO=?")) {
			ps.setString(1, refundRequest.getReceiptNo());
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					receiptNo = rs.getString(1);
				}
			}
		}
        if(receiptNo ==null){
        String CustNum=null,SubrNum=null;
        BigDecimal ChargeAmount=BigDecimal.ZERO;
        ArrayList<EpcPayment> payments=new ArrayList<EpcPayment>();
        for(RefundOrderRequest refundOrder : list) {
                EpcPayment payment = new EpcPayment();
                payment.setPaymentCode(ReceiptCode.RefundPaymentCode);
                payment.setPaymentAmount(refundOrder.getCancelAmount());
                payment.setReference1(refundRequest.getReceiptNo());
                payment.setCurrencyCode("HKD");
                payment.setExchangeRate(BigDecimal.ONE);
                payment.setCurrencyAmount(BigDecimal.ZERO);
                if(StringUtils.isNotBlank(refundOrder.getCustNum()))
                CustNum=refundOrder.getCustNum();
                if(StringUtils.isNotBlank(refundOrder.getSubrNum()))
                SubrNum=refundOrder.getSubrNum();
                ChargeAmount=ChargeAmount.add(refundOrder.getCancelAmount());
                payments.add(payment);
        }
            createReceipt.setPaymentList(payments);
            createReceipt.setCustNum(CustNum);
            createReceipt.setSubrNum(SubrNum);
            
            EpcCharge charge = new EpcCharge();
            charge.setChargeAmount(ChargeAmount);
            charge.setChargeCode(ReceiptCode.RefundChargeType);
            createReceipt.setCharges(new ArrayList<EpcCharge>(Arrays.asList(charge)));
            
            EpcCreateReceiptResult createReceiptResult = epcReceiptHandler.createReceipt(fesconnection,createReceipt);
            receiptNo =createReceiptResult.getReceiptNo();

            tmpLogStr = "refund receipt generated:" + receiptNo;
            logger.info("{}{}", logStr, tmpLogStr);
        }

       boolean  isRefunded=true;    
            for(RefundOrderRequest refundOrder : list) {
            	
            	if("Card".equals(refundOrder.getSelectedName())) {
            		refundOrder.setManual(DateUtils.isSameDay(orderDate, new Date()));
            	}
                
            
            BigDecimal REC_ID=null;
            try(Statement ps=connection.createStatement()){
                try(ResultSet rs=ps.executeQuery(EPC_ORDER_REFUND_SEQ)){
                if(rs.next()) {
                    REC_ID=rs.getBigDecimal(1);
                }
                }
            }

            if(REC_ID!=null) {
            try(PreparedStatement ps=connection.prepareStatement(EPC_ORDER_REFUND)){
                ps.setBigDecimal(1, REC_ID);
                ps.setBigDecimal(2, refundOrder.getCancelAmount());
                ps.setString(3, receiptNo);
                ps.setString(4, refundOrder.getSelectedName());
                ps.setString(5,refundRequest.getCreateUser());
                ps.setString(6,refundRequest.getSalesman());
                ps.setString(7,refundOrder.getPayeeName());
                ps.setString(8,refundOrder.getPostalAddress1());
                ps.setString(9,refundOrder.getPostalAddress2());
                ps.setString(10,refundOrder.getPostalDistrict());
                ps.setString(11,refundOrder.getPostalArea());
                if(refundOrder.getManual()!=null) {
                ps.setString(12, refundOrder.getManual()?"Y":"N");
                }else {
                    ps.setNull(12, Types.VARCHAR);    
                }
                if(refundOrder.getCashOutDate()!=null) {
                ps.setObject(13, new java.sql.Date(refundOrder.getCashOutDate().getTime()));
                }else {
                ps.setNull(13, Types.DATE);    
                }
                ps.setString(14,refundOrder.getRefundReferenceNo());
                ps.setString(15, custId); // cust_id
                ps.setString(16, approveBy); // approve_by
                ps.setString(17, waiveFormCode); // waive_form_code
                ps.setString(18, location); // create_location
                if(refundOrder.getManual()!=null&&refundOrder.getManual()) {
                    ps.setString(19, "Y");
                }else {
                	ps.setString(19, "N");
                	isRefunded=false;
                }
                ps.setString(20,refundRequest.getCreateUser());
                if(refundOrder.getPaymentId()!=null) {
                ps.setInt(21,refundOrder.getPaymentId());
                }else {
                	ps.setNull(21, Types.INTEGER);
                }
                ps.execute();

            }

            //save refund obviously
            tmpLogStr = "include cancel receipt:";

            try(PreparedStatement ps=connection.prepareStatement(EPC_ORDER_REFUND_DETAIL)){

                    tmpLogStr += refundRequest.getReceiptNo() + ",";

                    ps.setBigDecimal(1, REC_ID);
                    ps.setInt(2, refundRequest.getCancelOrderId());
                    ps.setString(3, refundRequest.getReceiptNo());
                    ps.setString(4, refundOrder.getSubrNum());
                    ps.setString(5, refundOrder.getCustNum());
                    ps.setBigDecimal(6, refundOrder.getCancelAmount());
                    ps.execute();

            }
            }else {
                throw new SQLException("no REC_ID");
            }         

            isCreateWaivingReportRecord = epcWaivingReportHandler.createWaivingRecord(
            		connection, EpcWaivingReportHandler.WAIVE_TYPE_REFUND, REC_ID.intValue() + "");
            tmpLogStr = "isCreateWaivingReportRecord:" + isCreateWaivingReportRecord;

            }

            if(isRefunded) {
            	// mark item(s) as "refunding" by cancel receipt, kerrytsang, 20230505
            	isMarkedItem = epcStockHandler.markStockAsRefunded(connection, refundRequest.getReceiptNo());
            	if(isMarkedItem)
            	sendMail(connection, refundRequest.getCancelOrderId(),refundRequest.getReceiptNo());
            } else {
            	isMarkedItem = epcStockHandler.markStockAsRefunding(connection, refundRequest.getReceiptNo());
            }
            if(!isMarkedItem) {
                throw new SQLException("Fail to market items to refunding");
            }
            // end of mark item(s) as "refunding" by cancel receipt, kerrytsang, 20230505
            logger.info("{}{}", logStr, tmpLogStr);
        connection.commit();
        fesconnection.commit();
        
        } catch (SQLException e) {
        	try { if(fesconnection != null) { fesconnection.rollback(); } } catch (Exception ee) {ee.printStackTrace();}
        	try { if(connection != null) { connection.rollback(); } } catch (Exception ee) {ee.printStackTrace();}
            e.printStackTrace();
            throw e;
        }finally {
        try { if(fesconnection != null) { fesconnection.setAutoCommit(true); } } catch (Exception ee) {}
//        DataSourceUtils.releaseConnection(fesconnection, fesDataSource);
        try { if(fesconnection != null) { fesconnection.close(); } } catch (Exception ee) {}
        try { if(connection != null) { connection.setAutoCommit(true); } } catch (Exception ee) {}
//        DataSourceUtils.releaseConnection(connection, fesDataSource);
        try { if(connection != null) { connection.close(); } } catch (Exception ee) {}
		}
    }
    
    private void verify(RefundRequest refundRequest) throws Exception {
    	List<RefundOrderRequest> orderList=	refundRequest.getOrderList();
    	if(orderList==null||orderList.isEmpty()) {
    		throw new Exception("Refund selection cannot be empty data");
    	}
    	boolean can=epcCancelHandler.validateCancelReceipt(refundRequest.getReceiptNo(), null);
    	if(!can) {
    		throw new Exception("Cancel receipt is used to payment");
    	}
    	final String EPC_ORDER_CANCEL="SELECT (CANCEL_AMOUNT-NVL((SELECT SUM(a.REFUND_AMOUNT)  FROM EPC_ORDER_REFUND_DETAIL a JOIN EPC_ORDER_REFUND b ON a.HDR_ID=b.REC_ID AND b.IS_DONE <>'R' WHERE a.CANCEL_RECEIPT_NO=c.CANCEL_RECEIPT_NO ),0))  FROM EPC_ORDER_CANCEL c WHERE CANCEL_RECEIPT_NO=? ";
    	 BigDecimal sumAmount=BigDecimal.ZERO;  
    	 BigDecimal CANCEL_AMOUNT=null;
    	try(Connection connection=epcDataSource.getConnection()){
            try(PreparedStatement ps=connection.prepareStatement(EPC_ORDER_CANCEL)){
            	ps.setString(1,refundRequest.getReceiptNo());
                try(ResultSet rs=ps.executeQuery()){
                if(rs.next()) {
                	CANCEL_AMOUNT=rs.getBigDecimal(1);
                }
                }
            }
        }
    	int notCardPer=0;
    	for (Iterator<RefundOrderRequest> iterator = orderList.iterator(); iterator.hasNext();) {
			RefundOrderRequest refundOrderRequest = iterator.next();
			sumAmount=sumAmount.add(refundOrderRequest.getCancelAmount());
			if("Card".equals(refundOrderRequest.getSelectedName())) {
				EpcPayment payment=epcPaymentHandler.getPayment(refundOrderRequest.getPaymentId());
				int ct=refundOrderRequest.getCancelAmount().compareTo(payment.getBalance());
				if(ct>0) {
					throw new Exception("Refund amount cannot be larger than original payment");
				}
			}else {
				notCardPer++;
			}
			if(notCardPer>=2) {
				throw new Exception("Only one refund for non-creditCard");
			}
		}
    	if(sumAmount.compareTo(CANCEL_AMOUNT)!=0) {
    		throw new Exception("Refund amounts do not match");
    	}
	}
    
    private void sendMail(Connection connection,Integer orderId,String  cancelReceiptNo) throws Exception {
    	
    	String ORDER_REFERENCE="";
    	String ORDER_DATE="";;
    	String CONTACT_EMAIL="";
    	String ORDER_LANG="";
    	String CONTACT_PERSON="";
    	try (PreparedStatement ps = connection.prepareStatement(
    			"SELECT ORDER_REFERENCE,PLACE_ORDER_CHANNEL,ORDER_DATE,CONTACT_EMAIL,ORDER_LANG,CONTACT_PERSON_FIRST_NAME,CONTACT_PERSON_LAST_NAME FROM  EPC_ORDER WHERE ORDER_ID=?")) {
			ps.setInt(1, orderId);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					String ORDER_CHANNEL=rs.getString("PLACE_ORDER_CHANNEL");
					if(!EpcLoginChannel.ONLINE.equals(ORDER_CHANNEL)||rs.getString("CONTACT_EMAIL")==null) { return; }
					ORDER_REFERENCE=rs.getString("ORDER_REFERENCE");
					ORDER_DATE= DateFormatUtils.format(rs.getDate("ORDER_DATE"), "dd MMM yyyy") ;
					CONTACT_EMAIL=EpcCrypto.dGet(rs.getString("CONTACT_EMAIL"),"utf-8");
					ORDER_LANG=rs.getString("ORDER_LANG");
					CONTACT_PERSON=EpcCrypto.dGet(rs.getString("CONTACT_PERSON_FIRST_NAME"),"utf-8")+" "+EpcCrypto.dGet(rs.getString("CONTACT_PERSON_LAST_NAME"),"utf-8");
				}
			}
		}

    	BigDecimal CANCEL_AMOUNT=BigDecimal.ZERO;
    	try (PreparedStatement ps = connection.prepareStatement(
    			"SELECT CANCEL_AMOUNT  FROM EPC_ORDER_CANCEL WHERE CANCEL_RECEIPT_NO = ?")) {
			ps.setString(1,cancelReceiptNo);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					CANCEL_AMOUNT=rs.getBigDecimal(1);
				}
			}
		}
    	
    	StringBuffer sBuffer=new StringBuffer();
    	BigDecimal totalDecimal=BigDecimal.ZERO;
    	String EPC_ORDER_CANCEL="SELECT b.CPQ_ITEM_DESC ,b.CPQ_ITEM_DESC_CHI ,NVL(c.AMOUNT,0) AS AMOUNT FROM  EPC_ORDER_CASE a JOIN EPC_ORDER_ITEM b ON a.ORDER_ID =b.ORDER_ID AND a.CASE_ID =b.CASE_ID JOIN (SELECT ORDER_ID,PARENT_ITEM_ID,SUM(CHARGE_AMOUNT) AS AMOUNT FROM EPC_ORDER_CHARGE GROUP BY ORDER_ID,PARENT_ITEM_ID ) c ON a.ORDER_ID =c.ORDER_ID AND b.ITEM_ID =c.PARENT_ITEM_ID  WHERE a.ORDER_ID =? AND a.CANCEL_RECEIPT =?";
           try(PreparedStatement ps=connection.prepareStatement(EPC_ORDER_CANCEL)){
        	ps.setInt(1,orderId);
           	ps.setString(2,cancelReceiptNo);
               try(ResultSet rs=ps.executeQuery()){
            	   int i=1;
               while(rs.next()) {
            	   String name="";
            	   if("C".equals(ORDER_LANG)) {
            		   name=rs.getString("CPQ_ITEM_DESC_CHI");
            	   }else {
            		   name=rs.getString("CPQ_ITEM_DESC");
            	   }
            	   BigDecimal amount=rs.getBigDecimal("AMOUNT");
            	   sBuffer.append("<tr><td class=tabStyle1>"+i+"."+name+
            			   "</td><td class=tdStyle2>HK $"+amount+"</td></tr>");
            	   i++;
            	   totalDecimal=totalDecimal.add(amount);
               }
               }
           }
           
           BigDecimal courierFee=epcOrderHandler.getCourierFee(orderId);
           if(courierFee!=null) {
        	   totalDecimal=totalDecimal.add(courierFee);
        	   String name="";
        	   if("C".equals(ORDER_LANG)) {
        		   name="運費";
        	   }else {
        		   name="Delivery Fee";
        	   }
        	   sBuffer.append("<tr><td class=tabStyle1>"+name+
        			   "</td><td class=tdStyle2>HK $"+courierFee+"</td></tr>");
           }
           String ORDER_ITEMS_TAD=Matcher.quoteReplacement(sBuffer.toString()) ;
           HashMap<String, String> params=new HashMap<String, String>();
           params.put("ORDER_REFERENCE", ORDER_REFERENCE);
           params.put("CUST_NAME", CONTACT_PERSON);
           params.put("ORDER_DATE", ORDER_DATE);
           params.put("ORDER_ITEMS_TAD", ORDER_ITEMS_TAD);
           params.put("REFUND_ITEAMS_AMOUNT", totalDecimal.toString());
           params.put("COUPON_DISCOUNT", totalDecimal.subtract(CANCEL_AMOUNT).toString());
           params.put("TOTAL_REFUND_AMOUNT", CANCEL_AMOUNT.toString());
           params.put("YEAR", LocalDate.now().getYear()+"");
           
           EpcSendMsgRequest msg = new EpcSendMsgRequest();
            msg.setLanguage(ORDER_LANG);
            msg.setOrderId(orderId + "");
            msg.setRequestId(cancelReceiptNo);
            msg.setTemplateType("REFUND");
            msg.setParams(params);
            msg.setRecipient(CONTACT_EMAIL);
            msg.setCpRecipient("Lincoln_Liu@smartone.com");
            msg.setSendType(EpcSendMsgRequest.EMAIL);
           String re =epcMsgHandler.sendMsg(connection,msg);
          logger.info("send mail:{}", re);
	}
    /**
     * @author LincolnLiu
     * @return RefundReferenceNo
     * @throws Exception
     */
    public String getRefundReferenceNo() throws Exception {
        try(Connection connection=epcDataSource.getConnection()){
            BigDecimal num=BigDecimal.ZERO;            
            try(Statement ps=connection.createStatement()){
                try(ResultSet rs=ps.executeQuery(EPC_REFUND_REF_NO_SEQ)){
                if(rs.next()) {
                    num=rs.getBigDecimal(1);
                }
                }
            }
            String reference =String.format("REF%010d", num.intValue());
            return reference;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    public EpcRefundRecord getRefundRecord(String refundReference) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String iRefundReference = StringHelper.trim(epcSecurityHelper.encodeForSQL(refundReference));
        EpcRefundRecord epcRefundRecord = new EpcRefundRecord();

        try {
            conn = epcDataSource.getConnection();

            sql = "select rec_id, refund_receipt, refund_amount, refund_method, create_user, " +
                  "       create_salesman, to_char(create_date,'yyyymmddhh24mi') as create_date_txt, payee_name, postal_addr_1, postal_addr_2, " +
                  "       postal_district, postal_area, manual_refund, to_char(cash_out_date,'yyyymmddhh24mi') as cash_out_date_txt, refund_reference_no, " +
                  "       cust_id, is_done, modify_user, to_char(modify_date,'yyyymmddhh24mi') as modify_date_txt " +
                  "  from epc_order_refund " +
                  " where refund_reference_no = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, iRefundReference); // refund_reference_no
            rset = pstmt.executeQuery();
            if(rset.next()) {
                epcRefundRecord.setRecId(rset.getInt("rec_id"));
                epcRefundRecord.setRefundAmount(rset.getBigDecimal("refund_amount"));
                epcRefundRecord.setRefundReceipt(StringHelper.trim(rset.getString("refund_receipt")));
                epcRefundRecord.setRefundMethod(StringHelper.trim(rset.getString("refund_method")));
                epcRefundRecord.setCreateUser(StringHelper.trim(rset.getString("create_user")));
                epcRefundRecord.setCreateSalesman(StringHelper.trim(rset.getString("create_salesman")));
                epcRefundRecord.setCreateDate(StringHelper.trim(rset.getString("create_date_txt")));
                epcRefundRecord.setPayeeName(StringHelper.trim(rset.getString("payee_name")));
                epcRefundRecord.setPostalAddr1(StringHelper.trim(rset.getString("postal_addr_1")));
                epcRefundRecord.setPostalAddr2(StringHelper.trim(rset.getString("postal_addr_2")));
                epcRefundRecord.setPostalDistrict(StringHelper.trim(rset.getString("postal_district")));
                epcRefundRecord.setPostalArea(StringHelper.trim(rset.getString("postal_area")));
                epcRefundRecord.setManualRefund(StringHelper.trim(rset.getString("manual_refund")));
                epcRefundRecord.setCashOutDate(StringHelper.trim(rset.getString("cash_out_date_txt")));
                epcRefundRecord.setRefundReferenceNo(StringHelper.trim(rset.getString("refund_reference_no")));
                epcRefundRecord.setCustId(StringHelper.trim(rset.getString("cust_id")));
                epcRefundRecord.setIsDone(StringHelper.trim(rset.getString("is_done")));
                epcRefundRecord.setModifyUser(StringHelper.trim(rset.getString("modify_user")));
                epcRefundRecord.setModifyDate(StringHelper.trim(rset.getString("modify_date_txt")));
            } rset.close();
            pstmt.close();

            pstmt = conn.prepareStatement(
            		"SELECT e.CREATE_DATE,e.CREATE_USER,e.REJECT_TYPE,e.MESSAGE,r.REFUND_REFERENCE_NO  FROM EPC_ORDER_REFUND_REMARK e JOIN EPC_ORDER_REFUND r ON e.HDR_ID =r.REC_ID  WHERE e.HDR_ID IN "+
                    "(SELECT b.HDR_ID  FROM EPC_ORDER_REFUND_DETAIL a JOIN EPC_ORDER_REFUND_DETAIL b ON a.CANCEL_RECEIPT_NO=b.CANCEL_RECEIPT_NO  WHERE a.HDR_ID=?) ORDER BY CREATE_DATE DESC ");
            pstmt.setInt(1, epcRefundRecord.getRecId());
            rset = pstmt.executeQuery();
            List<EpcRefundRemark> remarks=new ArrayList<EpcRefundRemark>();
            while (rset.next()) {
            	EpcRefundRemark remark=new EpcRefundRemark();
            	remark.setCreateDate(rset.getString("CREATE_DATE"));
            	remark.setCreateUser(rset.getString("CREATE_USER"));
            	remark.setRejectType(rset.getString("REJECT_TYPE"));
            	remark.setMessage(rset.getString("MESSAGE"));
                remark.setRefundReferenceNo(rset.getString("REFUND_REFERENCE_NO"));
            	remarks.add(remark);
			}
            epcRefundRecord.setRemarks(remarks);
            rset.close();
            pstmt.close();
            
            epcRefundRecord.setResult("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();

            epcRefundRecord.setResult("FAIL");
            epcRefundRecord.setErrMsg(e.getMessage());
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcRefundRecord;
    }


    public void updateRefundRecord(EpcRefundRecord epcRefundRecord) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String iRefundReference = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcRefundRecord.getRefundReferenceNo()));
        String iIsDone = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcRefundRecord.getIsDone()));
        String iModifyUser = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcRefundRecord.getModifyUser()));
        String iRejectType = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcRefundRecord.getRejectType()));
        String iMessage = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcRefundRecord.getMessage()));
        String iLocation = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcRefundRecord.getCreateLocation()));
        String iSalesman = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcRefundRecord.getCreateSalesman()));
        String iCreateChannel = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcRefundRecord.getCreateChannel()));
        String iUserName = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcRefundRecord.getUserName()));
        boolean isValid = true;
        StringBuilder sb = new StringBuilder();
        String isDone = "";
        String modifyUser = "";
        String modifyDate = "";
        Connection fesconn = null;
        try {
        	logger.info("updateRefundRecord:{}", epcRefundRecord.toString());
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            // basic checking
            sql = "select is_done, modify_user, to_char(modify_date, 'yyyy-mm-dd hh24:mi') as m_date " +
                    "  from epc_order_refund " +
                    " where refund_reference_no = ? ";
              pstmt = conn.prepareStatement(sql);
              pstmt.setString(1, iRefundReference); // refund_reference_no
              rset = pstmt.executeQuery();
              if(rset.next()) {
                  isDone = StringHelper.trim(rset.getString("is_done"));
                  modifyUser = StringHelper.trim(rset.getString("modify_user"));
                  modifyDate = StringHelper.trim(rset.getString("m_date"));

                if("Y".equals(isDone)||"R".equals(isDone)) {
                    isValid = false;
                    sb.append(iRefundReference + " is updated before [by " + modifyUser + "," + modifyDate + "]");
                }
            } else {
                isValid = false;
                sb.append(iRefundReference + " is not found");
            }
            rset.close();
            pstmt.close();
            // end of basic checking


            if(isValid) {
                sql = "update epc_order_refund " +
                      "   set is_done = ?, modify_user = ?, modify_date = sysdate " +
                      " where refund_reference_no = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, iIsDone); // is_done
                pstmt.setString(2, iModifyUser); // modify_user
                pstmt.setString(3, iRefundReference); // refund_reference_no
                pstmt.executeUpdate();
                pstmt.close();

                
                sql = "select b.cancel_receipt_no,a.create_location,a.create_date,a.CUST_ID,a.REC_ID,b.CANCEL_ORDER_ID " +
                        "from epc_order_refund a, epc_order_refund_detail b " +
                        "where a.refund_reference_no = ? " +
                        "and b.hdr_id = a.rec_id ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1,iRefundReference);
                rset = pstmt.executeQuery();
               
                    	if("Y".equals(iIsDone)) {
                            /*
                             * update items/devices under corresponding cancel receipt to “Refunded” (from “Refunding” to “Refunded”)
                             */
                    	while (rset.next()){
                                 String cancelRecepiptNo = StringHelper.trim(rset.getString("cancel_receipt_no"));
                        if (StringUtils.isNotEmpty(cancelRecepiptNo)){
                        boolean isMarkedItem = epcStockHandler.markStockAsRefunded(conn, cancelRecepiptNo);
                        if (!isMarkedItem){
                            throw new Exception("Can't update items to refunded");
                        }
                        try(PreparedStatement cps= conn.prepareStatement(
                        		"SELECT CANCEL_AMOUNT,NVL((SELECT SUM(a.REFUND_AMOUNT) FROM EPC_ORDER_REFUND_DETAIL a JOIN EPC_ORDER_REFUND b ON a.HDR_ID = b.REC_ID AND b.IS_DONE = 'Y' WHERE a.CANCEL_RECEIPT_NO = c.CANCEL_RECEIPT_NO ), 0) "
                        		+ "FROM EPC_ORDER_CANCEL c WHERE CANCEL_RECEIPT_NO = ?")){
                        	cps.setString(1, cancelRecepiptNo);
                        	try(ResultSet rs =cps.executeQuery()){
                        		if(rs.next()&&rs.getBigDecimal(1).compareTo(rs.getBigDecimal(2))==0) {
                        			int orderId=rset.getInt("CANCEL_ORDER_ID");
                        			sendMail(conn, orderId, cancelRecepiptNo);
                        		}else {
                        		logger.warn("cancelRecepiptNo:"+cancelRecepiptNo+" has not send Email");
                        		}
                        	}
                        }
                        }
                        }
                    	}
                    	
                    if("R".equals(iIsDone)) {
                    	fesconn =fesDataSource.getConnection();
                    	fesconn.setAutoCommit(false);
                    	if (rset.next()){
                        String cancelRecepiptNo = StringHelper.trim(rset.getString("cancel_receipt_no"));
                    	Integer remarkId= Integer.MIN_VALUE;
                    	try(CallableStatement rstmt = conn.prepareCall("SELECT MAX(REMARK_ID) FROM EPC_ORDER_REFUND_REMARK")){
                    		try(ResultSet resultSet=rstmt.executeQuery()){
                    			if(resultSet.next()) {
                    				remarkId=resultSet.getInt(1)+1;
                    			}
                    		}
                    	}
                    	sql ="INSERT INTO EPC_ORDER_REFUND_REMARK (REMARK_ID, HDR_ID, MESSAGE, REJECT_TYPE, CREATE_USER, CREATE_SALESMAN, CREATE_CHANNEL, CREATE_LOCATION, CREATE_DATE) VALUES(?, ?, ?, ?, ?, ?, ?, ?, sysdate)";
                    	int recId=rset.getInt("REC_ID");
                    	try(PreparedStatement rstmt =conn.prepareStatement(sql)){
                    		rstmt.setInt(1, remarkId);
                    		rstmt.setInt(2, recId);
                    		Clob clob=conn.createClob();
                    		clob.setString(1, iMessage);
                    		rstmt.setClob(3, clob);
                    		rstmt.setString(4, iRejectType);
                    		rstmt.setString(5, iUserName);
                    		rstmt.setString(6, iSalesman);
                    		rstmt.setString(7, iCreateChannel);
                    		rstmt.setString(8, iLocation);
                    		rstmt.execute();
                    	}
                    	
                    	 String emailAddress="";
                    	 String ccEmailAddress="";
                    	 String orderChannel=getChannel(rset.getInt("CANCEL_ORDER_ID"));
                    	 String createLocation ="";
                    	 
                    	 if(EpcLoginChannel.STORE.equals(orderChannel)) {
                    	 createLocation = StringHelper.trim(rset.getString("create_location"));
                    	 sql = "SELECT EMAIL_ADDRESS FROM RBD_UNIT WHERE RBD_UNIT_CODE =?";
                         try(PreparedStatement fespstmt = fesconn.prepareStatement(sql)){
                         	fespstmt.setString(1,createLocation);
                         	try(ResultSet resultSet=fespstmt.executeQuery()){
                    			if(resultSet.next()) {
                    				emailAddress=resultSet.getString(1);
                    			}
                    		}
                         }
                    	 }
                    	 if(StringUtils.isNotBlank(orderChannel)) {
                    		 try(PreparedStatement fesStatement = fesconn.prepareStatement(
               			"SELECT TYPDESCTYPDESC FROM EXT_TYPDESC WHERE TYPDESCTBLNAME=? AND TYPDESCCLASS='Email' AND TYPDESCTYPNAME='Refund'")){
                    			 fesStatement.setString(1, orderChannel);
               					try(ResultSet fesSet=fesStatement.executeQuery()){
               						if(fesSet.next()) {
               							if(EpcLoginChannel.STORE.equals(orderChannel)) {
               							ccEmailAddress=fesSet.getString(1);
               							}
               							if(EpcLoginChannel.ONLINE.equals(orderChannel)) {
               								emailAddress=fesSet.getString(1);
                   							}
               						}
               					}
      							} 
                    	 }
                    	 if(StringUtils.isNotBlank(emailAddress)) {
                        String domain="";
          				try(CallableStatement fesStatement = fesconn.prepareCall(
          						"SELECT TYPDESCTYPDESC FROM EXT_TYPDESC WHERE TYPDESCCLASS='DMAIN' AND TYPDESCTBLNAME='EPC_API'")){
          					try(ResultSet fesSet=fesStatement.executeQuery()){
          						if(fesSet.next()) {
          							domain=fesSet.getString(1);
          						}
          					}
 							}
          				String custID=rset.getString("CUST_ID");
                        Date createDate = rset.getDate("create_date");
                        
         				HashMap<String,String> params=new HashMap<String,String>();
         				params.put("refund_no", iRefundReference);
         				params.put("cancel_recepipt", cancelRecepiptNo);
         				params.put("store_code", createLocation);
         				params.put("reject_reason", iRejectType);
         				params.put("create_date", 
         						DateFormatUtils.format(createDate, "yyyy-MM-dd hh:mm"));
         				params.put("reject_date", 
         						DateFormatUtils.format(new Date(), "yyyy-MM-dd hh:mm"));
         				params.put("remark", iMessage);
         				String url=URLEncoder.encode(domain+"/#!/cancelReceipt?custID="+custID, StandardCharsets.UTF_8);
         				params.put("url", domain+"/reloadCrmTokenToEpcSales?redirect="+url);
         				
         				EpcSendMsgRequest msg = new EpcSendMsgRequest();
         	            msg.setLanguage("E");
         	            msg.setOrderId(recId + "");
         	            msg.setRequestId(remarkId+"");
         	            msg.setTemplateType("REFUND_REJECT");
         	            msg.setParams(params);
         	            msg.setRecipient(emailAddress);
         	            if(StringUtils.isNotEmpty(ccEmailAddress))
         	            msg.setCpRecipient(ccEmailAddress);
         	            //msg.setCpRecipient("Wallis_Cheuk@smartone.com,Andrew-MD_Lee@smartone.com");
         	            msg.setSendType("EMAIL");
         	           String re =epcMsgHandler.sendMsg(msg);
         	          logger.info("send mail:{}", re);
                     }
                    }
                    }
                    
                rset.close();

                conn.commit();
                if(fesconn!=null)fesconn.commit();

                epcRefundRecord.setResult("SUCCESS");
            } else {
                epcRefundRecord.setResult("FAIL");
                epcRefundRecord.setErrMsg(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("update refund", e);
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            try { if(fesconn != null) { fesconn.rollback(); } } catch (Exception ee) {}
            epcRefundRecord.setResult("FAIL");
            epcRefundRecord.setErrMsg(e.getMessage());
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
            try { if(fesconn != null) { fesconn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(fesconn != null) { fesconn.close(); } } catch (Exception ee) {}
        }
    }
    
    private String getChannel(Integer orderId) {
    	String orderChannel=null;
    	String sqlString="SELECT PLACE_ORDER_CHANNEL FROM EPC_ORDER WHERE ORDER_ID=?";
    	try(Connection conn = epcDataSource.getConnection()){
    		try(PreparedStatement rstmt =conn.prepareStatement(sqlString)){
    			rstmt.setInt(1, orderId);
				try(ResultSet rs=rstmt.executeQuery()){
					if(rs.next()) {
						orderChannel=rs.getString(1);
					}
				}
			}
    	} catch (SQLException e) {
    		logger.info("update refund", e);
			e.printStackTrace();
		}
		return orderChannel;
	}
}
