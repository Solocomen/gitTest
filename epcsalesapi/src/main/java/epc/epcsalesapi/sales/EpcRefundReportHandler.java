package epc.epcsalesapi.sales;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.sales.bean.refundReport.EpcRefundReportDO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class EpcRefundReportHandler {

    private final Logger logger = LoggerFactory.getLogger(EpcRefundReportHandler.class);

    @Autowired
    private DataSource epcDataSource;

    @Autowired
    private DataSource fesDataSource;

    @Autowired
    private EpcSecurityHelper epcSecurityHelper;

    private static final String EPCSQL = "SELECT to_char(D.CREATE_DATE, 'DD/MM/YYYY') AS cancel_order_date,"
            + "C.ORDER_REFERENCE AS order_reference,"
            + "B.CUST_NUM AS customer_num,"
            + "B.SUBR_NUM AS subsriber_num,"
            + "C.PLACE_ORDER_LOCATION AS receipt_store_code,"
            + "E.RECEIPT_NO AS order_receipt_num,"
            + "D.CANCEL_RECEIPT_NO AS cancel_receipt_num,"
            + "C.TOTAL_CHARGE_AMOUNT AS order_amout,"
            + "E.PAY_AMOUNT,"
            + "A.REFUND_METHOD AS refund_method,"
            + "A.REFUND_REFERENCE_NO AS refund_num,"
            + "A.REFUND_RECEIPT AS refund_receit,"
            + "to_char(A.CREATE_DATE, 'DD/MM/YYYY') AS refund_receipt_date,"
            + "A.REFUND_AMOUNT AS refund_receipt_amount,"
            + "A.APPROVE_BY AS approver,"
            + "A.create_location AS create_location,"
            + "MANUAL_REFUND AS is_manual_refund, "
            + "to_char(CASH_OUT_DATE, 'DD/MM/YYYY') AS refund_cash_out_date,"
            + "PAYEE_NAME AS payee_name,"
            + "POSTAL_ADDR_1 || ' ' || POSTAL_ADDR_2 || ' ' || POSTAL_AREA  AS refund_postal_addrs, "
            + "A.IS_DONE,"
            + "NVL((SELECT SUM(r.REFUND_AMOUNT) FROM EPC_ORDER_REFUND r JOIN EPC_ORDER_REFUND_DETAIL l ON r.REC_ID = l.HDR_ID"
            + " WHERE l.CANCEL_ORDER_ID =c.ORDER_ID AND r.REC_ID <=a.REC_ID AND r.IS_DONE <> 'R'),0) AS REFUND_AMOUNT ,"
            + "A.MODIFY_DATE "
            + "FROM EPC_ORDER_REFUND A "
            + "JOIN EPC_ORDER_REFUND_DETAIL B ON "
            + "B.HDR_ID = A.REC_ID "
            + "JOIN EPC_ORDER C ON "
            + "C.ORDER_ID = B.CANCEL_ORDER_ID "
            + "JOIN EPC_ORDER_CANCEL D ON "
            + "B.CANCEL_RECEIPT_NO = D.CANCEL_RECEIPT_NO AND REFUND='Y' "
            + "JOIN (SELECT LISTAGG(RECEIPT_NO ,',') RECEIPT_NO,SUM(PAY_AMOUNT) AS PAY_AMOUNT,ORDER_ID FROM epc_order_receipt GROUP BY ORDER_ID) E"
            + " ON E.ORDER_ID = C.ORDER_ID "
            + "WHERE A.CREATE_DATE BETWEEN TO_DATE(?,'yyyy-MM-dd') AND TO_DATE(?,'yyyy-MM-dd hh24:mi:ss') ";

    private static final String FESSQL = "SELECT a.RECEIPT_NO,TO_CHAR(a.RECEIPT_DATE, 'DD/MM/YYYY') AS RECEIPT_DATE,b.CHARGE_TYPE,a.AMOUNT,a.PAYMENT_CODE,a.REFERENCE1,a.REFERENCE2,c.MERCHANT_ID,c.AUTHORIZE_ID,d.ORDER_REFERENCE FROM ZZ_PREC_PAY_REF a JOIN (SELECT LISTAGG(DISTINCT CHARGE_TYPE,',') AS CHARGE_TYPE,RECEIPT_NO FROM ZZ_PREC_CHG_DTL GROUP BY RECEIPT_NO ) b ON a.RECEIPT_NO =b.RECEIPT_NO LEFT JOIN (SELECT MAX(AUTHORIZE_ID) AS AUTHORIZE_ID,MAX(MERCHANT_ID) AS MERCHANT_ID, ECR_NO FROM PCT_FES_PG_REQUEST_V GROUP BY ECR_NO) c ON c.ECR_NO=a.REFERENCE2 JOIN ZZ_PREC_HDR d ON a.RECEIPT_NO =d.RECEIPT_NO WHERE a.RECEIPT_NO IN "
            + "(RECEIPT_NO_ARRAY) ORDER BY a.RECEIPT_DATE";

    public List<EpcRefundReportDO> getRefundReport(String startDate, String endDate, String state, String method, String location) throws Exception {


        StringBuffer receiptNoSB = new StringBuffer();
        List<EpcRefundReportDO> refundReportDOList = new ArrayList<>();
        String epcSql = EPCSQL;


        if (StringUtils.isNotEmpty(state) && !"null".equals(state)) {
            epcSql = epcSql + " and A.IS_DONE= '" + epcSecurityHelper.validateString(state) + "' ";
        }
        if (StringUtils.isNotEmpty(method) && !"null".equals(method)) {
            epcSql = epcSql + " and A.REFUND_METHOD = '" + epcSecurityHelper.validateString(method) + "' ";
        }
        if (StringUtils.isNotEmpty(location) && !"null".equals(location)) {
            epcSql = epcSql + " and C.PLACE_ORDER_LOCATION = '" + epcSecurityHelper.validateString(location) + "' ";
        }
        
        epcSql+=" ORDER BY a.REC_ID";
        final String split = "\r\n";
        // select EPC data
        try (Connection epcConn = epcDataSource.getConnection()) {
            String nowStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            try (PreparedStatement pstmt = epcConn.prepareStatement(epcSql)) {
                if (StringUtils.isNotEmpty(startDate) && !"null".equals(startDate)) {
//                Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(epcSecurityHelper.validateString(startDate));
//                String formatStartDate = new SimpleDateFormat("yyyy-MM-dd").format(parse);
                    String valiStartDate = epcSecurityHelper.validateString(startDate);
                    pstmt.setString(1, valiStartDate);
                } else {
                    pstmt.setString(1, nowStr);
                }
                final String endtime = " 23:59:59";
                if (StringUtils.isNotEmpty(endDate) && !"null".equals(endDate)) {
//                Date parse = new SimpleDateFormat("yyyy-MM-dd").parse(epcSecurityHelper.validateString(endDate));
//                String formatEndDate = new SimpleDateFormat("yyyy-MM-dd").format(parse);
                    String vailEndDate = epcSecurityHelper.validateString(endDate);
                    pstmt.setString(2, vailEndDate + endtime);
                } else {
                    pstmt.setString(2, nowStr + endtime);
                }

                logger.info("EPC_sql:" + epcSql);

                try (ResultSet rs = pstmt.executeQuery()) {

                    while (rs.next()) {

                        EpcRefundReportDO reportDO = new EpcRefundReportDO();
                        BigDecimal orderAmout = rs.getBigDecimal("ORDER_AMOUT");
                        BigDecimal refundReceiptAmount = rs.getBigDecimal("REFUND_RECEIPT_AMOUNT");
                        BigDecimal refundAmount = rs.getBigDecimal("REFUND_AMOUNT");
                        BigDecimal payAmount = rs.getBigDecimal("PAY_AMOUNT");
                        String order_receipt_num=rs.getString("ORDER_RECEIPT_NUM");

                        reportDO.setCancelOrderDate(rs.getString("cancel_order_date"));
                        reportDO.setOrderReference(rs.getString("order_reference"));
                        reportDO.setCustomerNum(rs.getString("customer_num"));
                        reportDO.setSubscriberNum(rs.getString("subsriber_num"));
                        reportDO.setReceiptStoreCode(rs.getString("receipt_store_code"));
                        reportDO.setOrderAmount(String.format("%.2f", orderAmout));
                        reportDO.setSettlementReceipt(rs.getString("cancel_receipt_num"));
                        reportDO.setInterfacedtoLedgerAmount("N/A");
                        reportDO.setRefundMethod(rs.getString("refund_method"));
                        reportDO.setRefundNo(rs.getString("refund_num"));
                        reportDO.setRefundReceipt(rs.getString("refund_receit"));
                        reportDO.setRefundReceiptDate(rs.getString("refund_receipt_date"));
                        reportDO.setRefundReceiptAmount(String.format("%.2f", refundReceiptAmount));
                        reportDO.setApprover(rs.getString("approver"));
                        reportDO.setRefundAmount(String.format("%.2f", refundAmount));
                        reportDO.setReconciliation(String.format("%.2f",payAmount.subtract(refundAmount)));
                        reportDO.setAutoRefund("N");
                        reportDO.setManualRefund(rs.getString("is_manual_refund"));
                        reportDO.setRefundStoreCode(rs.getString("create_location"));
                        reportDO.setCashOutDate(rs.getString("refund_cash_out_date"));
                        reportDO.setCHQRefund("N/A");
                        reportDO.setPayeeName(rs.getString("payee_name"));
                        reportDO.setRefundPostaladdrs(rs.getString("refund_postal_addrs"));
                        reportDO.setIsDone(rs.getString("IS_DONE"));
                        reportDO.setModifyDate(rs.getString("MODIFY_DATE"));
                        refundReportDOList.add(reportDO);
                        // make the RECEIPT_NO list to StringBuffer
                        String[] orns=order_receipt_num.split(",");
                        for (String num : orns) {
                            if (receiptNoSB.length() > 0) {
                                receiptNoSB.append(",");
                            }
                            receiptNoSB.append("'" + num + "'"); 
                        }
                        

                    }
                }
            }
        } catch (Exception e) {
            logger.error(String.valueOf(e));
            e.printStackTrace();
            throw e;
        }

        logger.info("get " + refundReportDOList.size() + " data in EPC DB");
        // select FES db
        Map<String,EpcRefundReportDO> fesMap = new HashMap<String,EpcRefundReportDO>();
        if (!refundReportDOList.isEmpty())
            try (Connection conn = fesDataSource.getConnection()) {

                try (Statement stmt = conn.createStatement()) {
                    // make filter by RECEIPT_NO SQL
                    String sql = FESSQL.replaceFirst("RECEIPT_NO_ARRAY", receiptNoSB.toString());
                    logger.info("FES_sql:" + sql);
                    try (ResultSet rs = stmt.executeQuery(sql)) {
                        while (rs.next()) {
                            String ORDER_REFERENCE=rs.getString("ORDER_REFERENCE");
                        	EpcRefundReportDO fesSqlData=fesMap.get(ORDER_REFERENCE);
                        	if(fesSqlData==null) {
                            fesSqlData = new EpcRefundReportDO();
                            fesSqlData.setFesReceiptNum(rs.getString("RECEIPT_NO"));
                            fesSqlData.setOrderReceiptDate(rs.getString("RECEIPT_DATE"));
                            fesSqlData.setReceiptChargeType(rs.getString("CHARGE_TYPE"));
                            fesSqlData.setPaymentCode(rs.getString("PAYMENT_CODE"));
                            fesSqlData.setCreditCardNum(rs.getString("REFERENCE1"));
                            fesSqlData.setMidNum(rs.getString("MERCHANT_ID"));
                            fesSqlData.setApprovalCodeForCreditCard(rs.getString("AUTHORIZE_ID"));
                            fesSqlData.setPaymentGatewayReferenceNo(rs.getString("REFERENCE2"));
                            fesSqlData.setPaymentAmount(rs.getString("AMOUNT"));
                        	}else {
                        		if(rs.getString("RECEIPT_NO")!=null) {
                                fesSqlData.setFesReceiptNum(fesSqlData.getFesReceiptNum()+split+rs.getString("RECEIPT_NO"));
                                }
                                if(rs.getString("RECEIPT_DATE")!=null) {
                                fesSqlData.setOrderReceiptDate(fesSqlData.getOrderReceiptDate()+split+rs.getString("RECEIPT_DATE"));
                                }
                        		if(rs.getString("PAYMENT_CODE")!=null) {
                        		fesSqlData.setPaymentCode(fesSqlData.getPaymentCode()+split+rs.getString("PAYMENT_CODE"));
                        		}
                        		if(rs.getString("REFERENCE1")!=null) {
                                fesSqlData.setCreditCardNum(fesSqlData.getCreditCardNum()+split+rs.getString("REFERENCE1"));
                        		}
                        		if(rs.getString("MERCHANT_ID")!=null) {
                                fesSqlData.setMidNum(fesSqlData.getMidNum()+split+rs.getString("MERCHANT_ID"));
                        		}
                        		if(rs.getString("AUTHORIZE_ID")!=null) {
                                fesSqlData.setApprovalCodeForCreditCard(fesSqlData.getApprovalCodeForCreditCard()+split+rs.getString("AUTHORIZE_ID"));
                        		}
                        		if(rs.getString("REFERENCE2")!=null) {
                                fesSqlData.setPaymentGatewayReferenceNo(fesSqlData.getPaymentGatewayReferenceNo()+split+rs.getString("REFERENCE2"));
                        		}
                        		if(rs.getString("AMOUNT")!=null) {
                                    fesSqlData.setPaymentAmount(fesSqlData.getPaymentAmount()+split+rs.getString("AMOUNT"));
                            	}
                                String CHARGE_TYPE=rs.getString("CHARGE_TYPE");
                                if(CHARGE_TYPE!=null&&(fesSqlData.getReceiptChargeType()==null||fesSqlData.getReceiptChargeType().indexOf(CHARGE_TYPE)<0)) {
                                    fesSqlData.setReceiptChargeType(fesSqlData.getReceiptChargeType()+split+CHARGE_TYPE);
                            	}
                        	}
                        	fesMap.put(ORDER_REFERENCE, fesSqlData);
                        }
                    }
                    }
                } catch (Exception e) {
                	logger.error(String.valueOf(e));
                    e.printStackTrace();
                    throw e;
				}

        List<EpcRefundReportDO> refundlist= new ArrayList<EpcRefundReportDO>();
                    if (refundReportDOList.size()>0){
                            refundReportDOList.forEach(epc->{
                            	EpcRefundReportDO fesDo= fesMap.get(epc.getOrderReference());
                            	if(fesDo==null) {
                            		fesDo= fesMap.get(epc.getOrderReference().replaceFirst("SOR", "SMC"));
                            	}
                            	if(fesDo!=null) {
                                    epc.setFesReceiptNum(fesDo.getFesReceiptNum());
                                    epc.setOrderReceiptDate(fesDo.getOrderReceiptDate());
                            		epc.setReceiptChargeType(fesDo.getReceiptChargeType());
                            		epc.setPaymentCode(fesDo.getPaymentCode());
                            		epc.setCreditCardNum(fesDo.getCreditCardNum());
                            		epc.setMidNum(fesDo.getMidNum());
                            		epc.setApprovalCodeForCreditCard(fesDo.getApprovalCodeForCreditCard());
                            		epc.setPaymentGatewayReferenceNo(fesDo.getPaymentGatewayReferenceNo());
                            		epc.setPaymentAmount(fesDo.getPaymentAmount());
                            		refundlist.add(epc);
                            	}
                            });
                    }


        return refundlist;
    }

}
