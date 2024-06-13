package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.springframework.stereotype.Service;

@Service
public class EpcScreenReplaceHandler {

	public boolean createScreenReplace(Connection crmConn, String invoiceNo, String custNum, String subrNum, String handsetProductCode, String imei, String screenReplaceProductCode, int salesmanUserid) {
		PreparedStatement pstmt = null;
        String sql = "";
        boolean isCreate = false;
        
        try {
        	sql = "insert into sa_screen_repl_info ( " +
                  "  invoice_no, subr_no, cust_no, hs_product, imei, " + 
                  "  screen_prod, status, create_by, create_date " +
                  ") values ( " +
                  "  ?,?,?,?,?, " + 
                  "  ?,?,?,sysdate " +
        	      ") ";
        	pstmt = crmConn.prepareStatement(sql);
        	pstmt.setString(1, invoiceNo); // invoice_no
        	pstmt.setString(2, subrNum); // subr_no
        	pstmt.setString(3, custNum); // cust_no
        	pstmt.setString(4, handsetProductCode); // hs_product
        	pstmt.setString(5, imei); // imei
        	pstmt.setString(6, screenReplaceProductCode); // screen_prod
        	pstmt.setString(7, "N"); // status
        	pstmt.setInt(8, salesmanUserid); // create_by
        	pstmt.executeUpdate();
        	pstmt.close();
        	
        	isCreate = true;
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        }
		return isCreate;
	}
	
	
	// refer to fesweb fes.doa.DOA.java
	public boolean voidScreenReplace(Connection crmConn, String invoiceNo, int salesmanUserid) {
		PreparedStatement pstmt = null;
        String sql = "";
        boolean isVoid = false;
        
        try {
        	sql = "update sa_screen_repl_info " +
                  "  set status = ?, update_by = ?, update_date = sysdate " + 
                  " where invoice_no = ? ";
        	pstmt = crmConn.prepareStatement(sql);
        	pstmt.setString(1, "V"); // status
        	pstmt.setInt(2, salesmanUserid); // update_by
        	pstmt.setString(3, invoiceNo); // invoice_no
        	pstmt.executeUpdate();
        	pstmt.close();
        	
        	isVoid = true;
        } catch (Exception e) {
        	e.printStackTrace();
        } finally {
        }
		return isVoid;
	}
}
