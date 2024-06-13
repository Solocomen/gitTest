package epc.epcsalesapi.fes.user;

import java.sql.*;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.StringHelper;

@Service
public class FesUserHandler {
    @Autowired
    private DataSource fesDataSource;

    public FesUser getUserByUsername(String username) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        FesUser user = new FesUser();

        try {
            conn = fesDataSource.getConnection();

            sql = "select userid, salesman from user_info where username = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            rset = pstmt.executeQuery();
            if(rset.next()) {
                user.setUserid(rset.getInt("userid"));
                user.setUsername(username);
                user.setSalesman(StringHelper.trim(rset.getString("salesman")));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return user;
    }
}
