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
public class EpcItemTemplateHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcItemTemplateHandler.class);

    private DataSource epcDataSource;
    private EpcSecurityHelper epcSecurityHelper;
    
    public EpcItemTemplateHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
    }

    
    public String getItemTemplate(int orderId, String itemId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String iItemId = epcSecurityHelper.encodeForSQL(itemId);
        String tmeplateName = "";

        try {
            conn = epcDataSource.getConnection();

            sql = "select template_name " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and item_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, iItemId); // item_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                tmeplateName = StringHelper.trim(rset.getString("template_name"));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return tmeplateName;
    }


    public String getPackageTemplate(int orderId, String itemId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String iItemId = epcSecurityHelper.encodeForSQL(itemId);
        String tmeplateName = "";

        try {
            conn = epcDataSource.getConnection();

            sql = "select b.template_name " +
                  "  from epc_order_item a, epc_order_item b " +
                  " where a.order_id = ? " +
                  "   and a.item_id = ? " +
                  "   and b.order_id = a.order_id " +
                  "   and b.case_id = a.case_id " +
                  "   and b.parent_item_id is null ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, iItemId); // item_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                tmeplateName = StringHelper.trim(rset.getString("template_name"));
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return tmeplateName;
    }
}
