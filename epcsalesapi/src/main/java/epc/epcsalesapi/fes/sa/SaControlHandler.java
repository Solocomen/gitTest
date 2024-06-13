package epc.epcsalesapi.fes.sa;

import java.sql.*;
import java.util.ArrayList;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import epc.epcsalesapi.fes.sa.bean.ZzHardcodeCtrl;
import epc.epcsalesapi.helper.StringHelper;

@Service
public class SaControlHandler {
	
    @Autowired
    private DataSource fesDataSource;

    public ArrayList<ZzHardcodeCtrl> getControl(ZzHardcodeCtrl control) throws Exception {
        
        ArrayList<ZzHardcodeCtrl> controlList = null;
        String sql = "";		
        
        try {
            sql = "SELECT * FROM zz_hardcode_ctrl WHERE ";
			
            if (!"".equals(StringHelper.trim(control.getRecType()))) sql += "rec_type = ? ";
			if (!"".equals(StringHelper.trim(control.getDescName()))) sql += "AND desc_name = ? ";
            if (!"".equals(StringHelper.trim(control.getCode1()))) sql += "AND code1 = ? ";
			if (!"".equals(StringHelper.trim(control.getCode2()))) sql += "AND code2 = ? ";
			if (!"".equals(StringHelper.trim(control.getCode3()))) sql += "AND code3 = ? ";
			if (!"".equals(StringHelper.trim(control.getCode4()))) sql += "AND code4 = ? ";
			
			controlList = getResultControl(sql, control);
			
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {}
        
        return controlList;
    }
	
    private ArrayList<ZzHardcodeCtrl> getResultControl(String sql, ZzHardcodeCtrl control) throws Exception {

        ArrayList<ZzHardcodeCtrl> controlList = new ArrayList<ZzHardcodeCtrl>();
        ZzHardcodeCtrl resultControl = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        int idx = 1;

        try {
            conn = fesDataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            if (!"".equals(StringHelper.trim(control.getRecType()))) {
                pstmt.setString(idx++, control.getRecType());
            }

            if (!"".equals(StringHelper.trim(control.getDescName()))) { 
                pstmt.setString(idx++, control.getDescName());
            }

            if (!"".equals(StringHelper.trim(control.getCode1()))) { 
                pstmt.setString(idx++, control.getCode1());
            }

            if (!"".equals(StringHelper.trim(control.getCode2()))) {
                pstmt.setString(idx++, control.getCode2());
            }

            if (!"".equals(StringHelper.trim(control.getCode3()))) {
                pstmt.setString(idx++, control.getCode3());
            }

            if (!"".equals(StringHelper.trim(control.getCode4()))) {
                pstmt.setString(idx++, control.getCode4());
            }

            rset = pstmt.executeQuery();
            while(rset.next()) {
                resultControl = new ZzHardcodeCtrl();
                resultControl.setDescName(StringHelper.trim(rset.getString("desc_name")));
                resultControl.setCode1(StringHelper.trim(rset.getString("code1")));
                resultControl.setCode2(StringHelper.trim(rset.getString("code2")));
                resultControl.setCode3(StringHelper.trim(rset.getString("code3")));
                resultControl.setCode4(StringHelper.trim(rset.getString("code4")));
                controlList.add(resultControl);
            } 

        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        } finally {
        	 try { if(rset != null) { rset.close(); } } catch (Exception ignore) {}
             try { if(pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
             try { if(conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        return controlList;
    }	
}
