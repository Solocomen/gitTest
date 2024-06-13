package epc.epcsalesapi.stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.StringHelper;

@Service
public class EpcStockStatusDescHandler {

    private DataSource epcDataSource;

    public EpcStockStatusDescHandler(DataSource epcDataSource) {
        this.epcDataSource = epcDataSource;
    }


    public String getEngDescByStatus(String stockStatus) {
        String desc = "";
        Connection conn = null;

        try {
            conn = epcDataSource.getConnection();
            desc = getEngDescByStatus(conn, stockStatus);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return desc;
    }


    public String getEngDescByStatus(Connection conn, String stockStatus) {
        String desc = "";
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;

        try {
            sql = "select value_str1 " +
                  "  from epc_control_tbl a " +
                  " where rec_type = ? " +
                  "   and key_str1 = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "ORDER_ITEM_STATUS"); // rec_type - ORDER_ITEM_STATUS
            pstmt.setString(2, stockStatus); // key_str1
            rset = pstmt.executeQuery();
            if(rset.next()) {
                desc = StringHelper.trim(rset.getString("value_str1"));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return desc;
    }
}
