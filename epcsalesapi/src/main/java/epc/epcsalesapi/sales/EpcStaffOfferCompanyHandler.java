/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.sales.bean.EpcStaffOfferCompany;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author DannyChan
 */
@Service
public class EpcStaffOfferCompanyHandler {
	@Autowired
	private DataSource epcDataSource;

    public ArrayList<EpcStaffOfferCompany> getAllStaffOfferCompanies() {
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<EpcStaffOfferCompany> attrList = new ArrayList<EpcStaffOfferCompany>();
        EpcStaffOfferCompany epcStaffOfferCompany = null;
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "SELECT company_id, company_name, company_code " + 
				  "FROM epc_staff_offer_company WHERE status = ? ORDER BY company_id";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "A"); 
            rset = pstmt.executeQuery();
            while(rset.next()) {
            	epcStaffOfferCompany = new EpcStaffOfferCompany();
				epcStaffOfferCompany.setCompanyId(rset.getInt(1));
				epcStaffOfferCompany.setCompanyName(rset.getString(2));
				epcStaffOfferCompany.setCompanyCode(rset.getString(3));
            	
            	attrList.add(epcStaffOfferCompany);
            } 
			
				/*epcStaffOfferCompany = new EpcStaffOfferCompany();
				epcStaffOfferCompany.setCompanyId(1);
				epcStaffOfferCompany.setCompanyName("Company A");
				epcStaffOfferCompany.setCompanyCode("AAA");
            attrList.add(epcStaffOfferCompany);*/
			
			rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            e.printStackTrace();
            
            attrList = null;
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return attrList;
    }	
}
