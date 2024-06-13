package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EpcSkipReserveHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcSkipReserveHandler.class);

    private DataSource epcDataSource;

    public EpcSkipReserveHandler(DataSource epcDataSource) {
        this.epcDataSource = epcDataSource;
    }

    
    /***
     * skip to create tmp reserve ticket
     * 
     * @param code - product code / catalog offer desc
     * @return
     */
    public boolean isSkip(String code) {
        boolean isSkip = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            conn = epcDataSource.getConnection();

            sql = "select 1 " +
                  "  from epc_control_tbl " +
                  " where rec_type = ? " +
                  "   and value_str1 = ? " +
                  "   and value_str2 = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "SKIP_TMP_RESERVE"); // rec_type
            pstmt.setString(2, code); // value_str1
            pstmt.setString(3, "A"); // value_str2
            rset = pstmt.executeQuery();
            if(rset.next()) {
                isSkip = true;
            } rset.close();
            pstmt.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isSkip;
    }
}
