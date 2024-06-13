package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;

@Service
public class EpcCancelReceiptReportHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcCancelReceiptReportHandler.class);

    private final DataSource epcDataSource;
    private final EpcSecurityHelper epcSecurityHelper;
    
    public EpcCancelReceiptReportHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
    }

    public String getCancelReceiptWithoutRefund(String location, String startDate, String endDate) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        StringBuilder sb = new StringBuilder(5 * 1024); // default 5MB
        String iStartDate = epcSecurityHelper.encodeForSQL(StringHelper.trim(startDate));
        String iEndDate = epcSecurityHelper.encodeForSQL(StringHelper.trim(endDate));
        String iLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(location));
        String tmpStr = "";
        
        try {
            conn = epcDataSource.getConnection();

            // Fin colleagues should view all records, kerrytsang, 20231130
            if("REC".equals(iLocation)) {
                iLocation = "";
            }
            // end of Fin colleagues should view all records, kerrytsang, 20231130

            // create header
            sb.append("Outstanding cancel reference report\n");
            sb.append("Date period: " + startDate + " - " + endDate + "\n");
            sb.append("\n");
            sb.append("Cancel Reference,Location,Cancel Date,Order Refernce,Cust Num,Subr Num,Cancel Amount,Create By,Create Salesman,\n");
            // end of create header
            
            if("".equals(iLocation)) {
                sql = "select /*+ use_nl(a zz yy) */ " +
                      "       a.cancel_receipt_no, a.create_location, to_char(a.create_date,'yyyymmdd') as c_date, zz.order_reference, " +
                      "       yy.cust_num, yy.subr_num, " +
                      "       a.cancel_amount, a.create_user, a.create_salesman  " +
                      "  from epc_order_cancel a, epc_order zz, epc_order_case yy " +
                      " where a.create_date between to_date(? || '000000','yyyymmddhh24miss') and to_date(? || '235959','yyyymmddhh24miss') " +
                      "   and zz.order_id = a.order_id_cancelled " +
                      "   and yy.order_id = a.order_id_cancelled " +
                      "   and yy.cancel_receipt = a.cancel_receipt_no " +
                      "   and not exists ( " +
                      "     select 1 from epc_order_refund_detail b where b.cancel_receipt_no = a.cancel_receipt_no " +
                      "   ) " +
                      " order by a.create_date desc ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, iStartDate); // create_date - start_date
                pstmt.setString(2, iEndDate); // create_date - end_date
            } else {
                sql = "select /*+ use_nl(a zz yy) */ " +
                      "       a.cancel_receipt_no, a.create_location, to_char(a.create_date,'yyyymmdd') as c_date, zz.order_reference, " +
                      "       yy.cust_num, yy.subr_num, " +
                      "       a.cancel_amount, a.create_user, a.create_salesman  " +
                      "  from epc_order_cancel a, epc_order zz, epc_order_case yy " +
                      " where a.create_date between to_date(? || '000000','yyyymmddhh24miss') and to_date(? || '235959','yyyymmddhh24miss') " +
                      "   and a.create_location = ? " +
                      "   and zz.order_id = a.order_id_cancelled " +
                      "   and yy.order_id = a.order_id_cancelled " +
                      "   and yy.cancel_receipt = a.cancel_receipt_no " +
                      "   and not exists ( " +
                      "     select 1 from epc_order_refund_detail b where b.cancel_receipt_no = a.cancel_receipt_no " +
                      "   ) " +
                      " order by a.create_date desc ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, iStartDate); // create_date - start_date
                pstmt.setString(2, iEndDate); // create_date - end_date
                pstmt.setString(3, iLocation); // create_location
            }
            
            rset = pstmt.executeQuery();
            while(rset.next()) {
                tmpStr = StringHelper.trim(rset.getString("cancel_receipt_no")) + "," +
                         StringHelper.trim(rset.getString("create_location")) + "," +
                         StringHelper.trim(rset.getString("c_date")) + "," +
                         StringHelper.trim(rset.getString("order_reference")) + "," +
                         StringHelper.trim(rset.getString("cust_num")) + "," +
                         StringHelper.trim(rset.getString("subr_num")) + "," +
                         rset.getBigDecimal("cancel_amount") + "," +
                         StringHelper.trim(rset.getString("create_user")) + "," +
                         StringHelper.trim(rset.getString("create_salesman")) + "," +
                         "\n";
                sb.append(tmpStr);
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return sb.toString();
    }
}
