package epc.epcsalesapi.sales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcSecurityHelper;

@Service
public class EpcCustCreditHandler {

	private final Logger logger = LoggerFactory.getLogger(EpcCustCreditHandler.class);
	
	@Autowired
	private EpcSecurityHelper epcSecurityHelper;
	
	
	public boolean createCustCredit(Connection conn, String custId, String creditType, BigDecimal availableAmount, int orderId, String refNo, String startDateYYYYMMDD, String endDateYYYYMMDD, String createUser) {
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String sql = "";
		int creditId = 0;
		boolean isCreated = false;
		
		try {
			sql = "select epc_order_id_seq.nextval from dual ";
			pstmt = conn.prepareStatement(sql);
			rset = pstmt.executeQuery();
			if(rset.next()) {
				creditId = rset.getInt(1);
			} rset.close(); rset = null;
			
			sql = "insert into epc_cust_credit ( " +
		          "  credit_id, cust_id, credit_type, credit_amt, available_amt, " + 
		          "  used_amt, start_date, end_date, ref_no, create_user, " + 
		          "  create_date, modify_user, modify_date " +
		          ") values ( " +
		          "  ?,?,?,?,?, " + 
		          "  ?,to_date(?,'yyyymmdd'),to_date(?,'yyyymmdd'),?,?, " +
		          "  sysdate,null,null " +
				  ") ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, creditId); // credit_amt
			pstmt.setString(2, custId); // cust_id
			pstmt.setString(3, creditType); // cust_type
			if(availableAmount != null) {
				pstmt.setBigDecimal(4, availableAmount); // credit_amt
				pstmt.setBigDecimal(5, availableAmount); // available_amt
			} else {
				pstmt.setInt(4, 0); // credit_amt
				pstmt.setInt(5, 0); // available_amt
			}
			pstmt.setInt(6, 0); // used_amt
			pstmt.setString(7, startDateYYYYMMDD); // start_date
			if(!"".equals(endDateYYYYMMDD)) {
				pstmt.setString(8, endDateYYYYMMDD); // end_date
			} else {
				pstmt.setString(8, "20991231"); // end_date
			}
			pstmt.setString(9, epcSecurityHelper.validateId(refNo)); // ref_no
			pstmt.setString(10, createUser); // create_user
			pstmt.executeUpdate();
			
			
			sql = "insert into epc_cust_credit_tx ( " +
			      "  tx_id, cust_id, order_id, credit_id, credit_type, " + 
			      "  tx_type, paid_amt, source_from, create_user, create_date " +
			      ") values ( " +
			      "  'TX'||lpad(epc_order_id_seq.nextval,18,'0'),?,?,?,?, " +
			      "  ?,?,?,?,sysdate " +
			      ") ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, custId); // cust_id
			pstmt.setInt(2, orderId); // order_id
			pstmt.setInt(3, creditId); // credit_id
			pstmt.setString(4, creditType); // cust_type
			pstmt.setString(5, "ADD"); // tx_type
			if(availableAmount != null) {
				pstmt.setBigDecimal(6, availableAmount); // paid_amt
			} else {
				pstmt.setInt(6, 0); // paid_amt
			}
			pstmt.setString(7, ""); // source_from
			pstmt.setString(8, createUser); // create_user
			pstmt.executeUpdate();
			
			isCreated = true;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return isCreated;
	}
}
