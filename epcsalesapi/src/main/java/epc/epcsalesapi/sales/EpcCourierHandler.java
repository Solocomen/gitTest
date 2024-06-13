package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcCourier;
import epc.epcsalesapi.sales.bean.EpcCourierInfo;

@Service
public class EpcCourierHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcCourierHandler.class);

    private final DataSource epcDataSource;
    private final DataSource crmFesDataSource;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcMsgHandler epcMsgHandler;

    public EpcCourierHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper, EpcMsgHandler epcMsgHandler, DataSource crmFesDataSource) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcMsgHandler = epcMsgHandler;
        this.crmFesDataSource = crmFesDataSource;
    }

	public EpcCourierInfo getCourierList() {
		EpcCourierInfo courierInfo = new EpcCourierInfo();
		ArrayList<EpcCourier> courierList = new ArrayList<>();
		EpcCourier courier = null;
		Connection conn = null;
		PreparedStatement pstmt = null;
        ResultSet rsetC = null;
        String sql = "";
		
        try {
        	conn = epcDataSource.getConnection();
			// get courier info
        	sql = "select key_str1, key_str2, key_str3, key_str4, key_str5, key_number1 from epc_control_tbl where rec_type = ? and key_str5 = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "EPC_COURIER_COMPANY"); // rec_type
            pstmt.setString(2, "A"); // status
            rsetC = pstmt.executeQuery();
            while(rsetC.next()) {
            	courier = new EpcCourier();
            	courier.setCourierCode(StringHelper.trim(rsetC.getString("key_str1")));
            	courier.setCourierName(StringHelper.trim(rsetC.getString("key_str2")));
            	courierList.add(courier);
            } rsetC.close();
            pstmt.close();
            // end of get courier info
            
            courierInfo.setCourierList(courierList);
            courierInfo.setResult("SUCCESS");
        } catch (Exception e) {
        	courierInfo.setResult("FAIL");
        	courierInfo.setErrMsg(e.getMessage());
            e.printStackTrace();
        } finally {
            try { if(rsetC != null) { rsetC.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return courierInfo;
	}
}
