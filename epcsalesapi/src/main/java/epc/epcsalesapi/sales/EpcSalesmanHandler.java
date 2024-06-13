/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import java.sql.*;
import javax.sql.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author KerryTsang
 */

@Service
public class EpcSalesmanHandler {
	
	@Autowired
	private DataSource epcDataSource;
    
    public final String actionCreateQuote = "CREATE_QUOTE";
    public final String actionDeleteQuote = "DELETE_QUOTE";
    public final String actionTransferQuote = "TRANSFER_QUOTE";
    public final String actionPlaceOrder = "PLACE_ORDER";
    public final String actionAddQuoteItem = "ADD_QUOTE_ITEM";
    public final String actionUpdateQuoteItem = "UPDATE_QUOTE_ITEM";
    public final String actionDeleteQuoteItem = "DELETE_QUOTE_ITEM";
    public final String actionFulfillItem = "FULFILL_ITEM";
    public final String actionDoaItem = "DOA_ITEM";
    public final String actionUpdateStockStatus = "UPDATE_STOCK_STATUS";
    public final String actionModifyContactInfo = "MODIFY_CONTACT_INFO";
    
    
    /**
     * no commit & rollback action
     * 
     * @param epcConn
     * @param orderId
     * @param createUser
     * @param createSalesman
     * @param createLocation
     * @param createChannel
     * @param action
     * @return 
     */
    public boolean createSalesmanLog(Connection epcConn, int orderId, String createUser, String createSalesman, String createLocation, String createChannel, String action) {
        boolean isCreate = false;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            sql = "insert into epc_order_salesman ( " + 
                    "  rec_id, order_id, create_user, create_salesman, create_location, " + 
                    "  create_channel, create_date, action " + 
                    ") values ( " +
                    "  epc_order_id_seq.nextval,?,?,?,?, " +
                    "  ?,sysdate,? " +
                    ") ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, createUser); // create_user
            pstmt.setString(3, createSalesman); // create_salesman
            pstmt.setString(4, createLocation); // create_location
            pstmt.setString(5, createChannel); // create_channel
            pstmt.setString(6, action); // action
            pstmt.executeUpdate();
            
            isCreate = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCreate;
    }
    
    
    public boolean createSalesmanLog(Connection epcConn, int orderId, String caseId, String createUser, String createSalesman, String createLocation, String createChannel, String action, String remarks) {
        boolean isCreate = false;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            sql = "insert into epc_order_salesman ( " + 
                    "  rec_id, order_id, create_user, create_salesman, create_location, " + 
                    "  create_channel, create_date, action, case_id, remarks " + 
                    ") values ( " +
                    "  epc_order_id_seq.nextval,?,?,?,?, " +
                    "  ?,sysdate,?,?,? " +
                    ") ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, createUser); // create_user
            pstmt.setString(3, createSalesman); // create_salesman
            pstmt.setString(4, createLocation); // create_location
            pstmt.setString(5, createChannel); // create_channel
            pstmt.setString(6, action); // action
            pstmt.setString(7, caseId); // case_id
            pstmt.setString(8, remarks); // remarks
            pstmt.executeUpdate();
            
            isCreate = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isCreate;
    }
    
    
    /**
     * with commit & rollback action
     * 
     * @param orderId
     * @param createUser
     * @param createSalesman
     * @param createLocation
     * @param createChannel
     * @param action
     * @return 
     */
    public boolean createSalesmanLog(int orderId, String createUser, String createSalesman, String createLocation, String createChannel, String action) {
        boolean isCreate = false;
        Connection conn = null;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            isCreate = createSalesmanLog(conn, orderId, createUser, createSalesman, createLocation, createChannel, action);
            if(isCreate) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isCreate;
    }
    
    
    public boolean createSalesmanLog(int orderId, String caseId, String createUser, String createSalesman, String createLocation, String createChannel, String action, String remarks) {
        boolean isCreate = false;
        Connection conn = null;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            isCreate = createSalesmanLog(conn, orderId, caseId, createUser, createSalesman, createLocation, createChannel, action, remarks);
            if(isCreate) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isCreate;
    }


    public boolean updateSalesmanLog(int tmpOrderId, int newOrder, String remarks) {
        boolean isUpdate = false;
        Connection conn = null;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            isUpdate = updateSalesmanLog(conn, tmpOrderId, newOrder, remarks);
            if(isUpdate) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }


    public boolean updateSalesmanLog(Connection conn, int tmpOrderId, int newOrder, String remarks) {
        boolean isUpdate = false;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            sql = "update epc_order_salesman " +
                  "   set order_id = ?, " + 
                  "       remarks = substr(1,300, remarks || '|' || ?) " +
                  " where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, newOrder); // order_id - new order id
            pstmt.setString(2, remarks); // remarks
            pstmt.setInt(3, tmpOrderId); // order_id - tmp order id
            pstmt.executeUpdate();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
}
