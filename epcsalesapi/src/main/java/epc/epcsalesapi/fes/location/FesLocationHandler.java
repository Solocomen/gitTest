package epc.epcsalesapi.fes.location;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.StringHelper;

@Service
public class FesLocationHandler {
    private DataSource fesDataSource;

    public FesLocationHandler(DataSource fesDataSource) {
        this.fesDataSource = fesDataSource;
    }

    public FesLocation getLocation(String rbdUnitCode) {
        FesLocation fesLocation = new FesLocation();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            conn = fesDataSource.getConnection();

            sql = "select rbd_unit_code from rbd_unit where rbd_unit_code = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, rbdUnitCode);
            rset = pstmt.executeQuery();
            if(rset.next()) {
                fesLocation.setRbdUnitCode(StringHelper.trim(rset.getString("rbd_unit_code")));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return fesLocation;
    }
}
