package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import epc.epcsalesapi.sales.bean.EpcLogOrderStatus;
import epc.epcsalesapi.sales.bean.EpcLogStockStatus;

@Service
public class EpcOrderLogHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcOrderLogHandler.class);

    private final DataSource epcDataSource;
    private final EpcSecurityHelper epcSecurityHelper;


    public EpcOrderLogHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
    }


    public void logOrderStatus(EpcLogOrderStatus epcLogOrderStatus) {
        Connection epcConn = null;

        try {
            epcConn = epcDataSource.getConnection();
            epcConn.setAutoCommit(false);

            logOrderStatus(epcConn, epcLogOrderStatus);

            if(EpcApiStatusReturn.RETURN_SUCCESS.equals(epcLogOrderStatus.getResult())) {
                epcConn.commit();
            } else {
                epcConn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();

            try { if(epcConn != null) { epcConn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception e) {}
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception e) {}
        }
    }


    public void logOrderStatus(Connection epcConn, EpcLogOrderStatus epcLogOrderStatus) {
        PreparedStatement pstmt = null;
        String sql = "";
        int orderId = epcLogOrderStatus.getOrderId();
        String oldOrderStatus = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcLogOrderStatus.getOldOrderStatus()));
        String newOrderStatus = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcLogOrderStatus.getNewOrderStatus()));
        String createUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcLogOrderStatus.getCreateUser()));
        String createSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcLogOrderStatus.getCreateSalesman()));
        String createChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcLogOrderStatus.getCreateChannel()));
        String createLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcLogOrderStatus.getCreateLocation()));
        
        try {
            sql = "insert into epc_order_status_log ( " +
                  "  rec_id, order_id, old_order_status, new_order_status, create_user, " +
                  "  create_salesman, create_channel, create_location, create_date " +
                  ") values ( " +
                  "  ?,?,?,?,?, " +
                  "  ?,?,?,sysdate " +
                  ") ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setString(1, UUID.randomUUID().toString()); // rec_id
            pstmt.setInt(2, orderId); // order_id
            pstmt.setString(3, oldOrderStatus); // old_order_status
            pstmt.setString(4, newOrderStatus); // new_order_status
            pstmt.setString(5, createUser); // create_user
            pstmt.setString(6, createSalesman); // create_salesman
            pstmt.setString(7, createChannel); // create_channel
            pstmt.setString(8, createLocation); // create_location
            pstmt.executeUpdate();

            epcLogOrderStatus.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();

            epcLogOrderStatus.setResult(EpcApiStatusReturn.RETURN_FAIL);
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception e) {}
        }
    }


    public void logStockStatus(EpcLogStockStatus epcLogStockStatus) {
        Connection epcConn = null;

        try {
            epcConn = epcDataSource.getConnection();
            epcConn.setAutoCommit(false);

            logStockStatus(epcConn, epcLogStockStatus);

            if(EpcApiStatusReturn.RETURN_SUCCESS.equals(epcLogStockStatus.getResult())) {
                epcConn.commit();
            } else {
                epcConn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();

            try { if(epcConn != null) { epcConn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception e) {}
            try { if(epcConn != null) { epcConn.close(); } } catch (Exception e) {}
        }
    }


    public void logStockStatus(Connection epcConn, EpcLogStockStatus epcLogStockStatus) {
        PreparedStatement pstmt = null;
        String sql = "";
        int orderId = epcLogStockStatus.getOrderId();
        String oldStockStatus = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcLogStockStatus.getOldStockStatus()));
        String newStockStatus = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcLogStockStatus.getNewStockStatus()));
        String itemId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcLogStockStatus.getItemId()));
        
        try {
            sql = "insert into epc_order_stock_status_log ( " +
                  "  rec_id, order_id, item_id, old_stock_status, new_stock_status, " +
                  "  create_date " +
                  ") values ( " +
                  "  ?,?,?,?,?, " +
                  "  sysdate " +
                  ") ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setString(1, UUID.randomUUID().toString()); // rec_id
            pstmt.setInt(2, orderId); // order_id
            pstmt.setString(3, itemId); // item_id
            pstmt.setString(4, oldStockStatus); // old_stock_status
            pstmt.setString(5, newStockStatus); // new_stock_status
            pstmt.executeUpdate();

            epcLogStockStatus.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();

            epcLogStockStatus.setResult(EpcApiStatusReturn.RETURN_FAIL);
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception e) {}
        }
    }
}
