/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcAddOrderAttrs;
import epc.epcsalesapi.sales.bean.EpcOrderAttr;

import java.sql.*;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author KerryTsang
 */
@Service
public class EpcOrderAttrHandler {
    
	@Autowired
	private DataSource epcDataSource;
	
    public final String ATTR_TYPE_DUMMY_SIM = "DUMMY_SIM";
    public final String ATTR_TYPE_DUMMY_IMSI = "DUMMY_IMSI";
    public final String ATTR_TYPE_STOCK_RESERVE_ID = "STOCK_RESERVE_ID";
    public final String ATTR_TYPE_STOCK_RESERVE_PICKUP_DATE = "STOCK_RESERVE_PICKUP_DATE";
    public final String ATTR_TYPE_STOCK_WAREHOUSE = "STOCK_WAREHOUSE";
    public final String ATTR_TYPE_ORDER_TYPE = "ORDER_TYPE";
    public final String ATTR_TYPE_ORDER_TYPE_NORMAL = "NORMAL";
    public final String ATTR_TYPE_ORDER_TYPE_FAST_CHECKOUT = "FAST_CHECKOUT";
    public final String ATTR_TYPE_ORDER_TYPE_TEMP = "TEMP";
    public final String ATTR_TYPE_ORDER_TYPE_CHECKOUT = "CHECKOUT";
    public final String ATTR_TYPE_DELIVERY_ID = "DELIVERY_ID";
    public final String ATTR_TYPE_CMS_ITEM_MAPPING = "CMS_ITEM_MAPPING";
    public final String ATTR_TYPE_SCANNED_PRODUCT = "SCANNED_PRODUCT";
    public final String ATTR_TYPE_STOCK_RESERVE_CURRENT_CHANNEL = "STOCK_RESERVE_CURRENT_CHANNEL";
    public final String ATTR_TYPE_GIFT_WRAPPING_BELONG_TO = "GIFT_WRAPPING_BELONG_TO";
    public final String ATTR_TYPE_GIFT_WRAPPING_SEND_FROM = "GIFT_WRAPPING_SEND_FROM";
    public final String ATTR_TYPE_GIFT_WRAPPING_SEND_TO = "GIFT_WRAPPING_SEND_TO";
    public final String ATTR_TYPE_GIFT_WRAPPING_TITLE = "GIFT_WRAPPING_TITLE";
    public final String ATTR_TYPE_GIFT_WRAPPING_SEND_MSG = "GIFT_WRAPPING_SEND_MSG";
    public final String ATTR_TYPE_GIFT_APPLECARE_FIRST_NAME = "APPLECARE_FIRST_NAME";
    public final String ATTR_TYPE_GIFT_APPLECARE_LAST_NAME = "APPLECARE_LAST_NAME";
    public final String ATTR_TYPE_GIFT_APPLECARE_APPLEID = "APPLECARE_APPLEID";
    public final String ATTR_TYPE_STAFF_ID = "STAFF_ID";
    public final String ATTR_TYPE_STAFF_COMPANY = "STAFF_COMPANY";
    public final String ATTR_TYPE_PREORDER_CASE_ID = "PREORDER_CASE_ID";
    public final String ATTR_TYPE_PREORDER_INV_NO = "PREORDER_INV_NO";
    public final String ATTR_TYPE_LOCK_RESERVE = "LOCK_RESERVE";
    public final String ATTR_TYPE_LOCK_VOUCHER = "LOCK_VOUCHER";
    
    
    public ArrayList<EpcOrderAttr> getAllAttrsUnderOrder(int smcOrderId) {
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<EpcOrderAttr> attrList = new ArrayList<EpcOrderAttr>();
        EpcOrderAttr epcOrderAttr = null;
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "select order_id, case_id, item_id, attr_type, attr_value, status " +
                 " from epc_order_attr " +
                 " where order_id = ? " +
                 "  and status = ? " +
                 " order by case_id, item_id, attr_type ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, "A"); // status - active
            rset = pstmt.executeQuery();
            while(rset.next()) {
            	epcOrderAttr = new EpcOrderAttr();
            	epcOrderAttr.setOrderId(rset.getInt("order_id"));
            	epcOrderAttr.setCaseId(StringHelper.trim(rset.getString("case_id")));
            	epcOrderAttr.setItemId(StringHelper.trim(rset.getString("item_id")));
            	epcOrderAttr.setAttrType(StringHelper.trim(rset.getString("attr_type")));
            	epcOrderAttr.setAttrValue(StringHelper.trim(rset.getString("attr_value")));
            	epcOrderAttr.setStatus(StringHelper.trim(rset.getString("status")));
            	
            	attrList.add(epcOrderAttr);
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            e.printStackTrace();
            
            attrList = null;
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return attrList;
    }
    
    
    public TreeMap<String, String> getOrderAttr(int smcOrderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        TreeMap<String, String> attrMap = new TreeMap<String, String>();
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "select item_id, attr_type, attr_value " +
                 " from epc_order_attr " +
                 " where order_id = ? " +
                 "  and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, "A"); // status - active
            rset = pstmt.executeQuery();
            while(rset.next()) {
                attrMap.put(StringHelper.trim(rset.getString("item_id")) + "@" + StringHelper.trim(rset.getString("attr_type")), StringHelper.trim(rset.getString("attr_value")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            attrMap = null;
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return attrMap;
    }
    
    
    public void addAttrs(EpcAddOrderAttrs epcAddOrderAttrs) {
    	Connection conn = null;
    	boolean isAdded = false;
    	int orderId = 0;
        String tmpCaseId = "";
        String tmpItemId = "";
        String tmpAttrType = "";
        String tmpAttrValue = "";
    	
    	try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            orderId = epcAddOrderAttrs.getOrderId();
            
            for(EpcOrderAttr attr : epcAddOrderAttrs.getAttrs()) {
                tmpCaseId = StringHelper.trim(attr.getCaseId());
                tmpItemId = StringHelper.trim(attr.getItemId());
                tmpAttrType = StringHelper.trim(attr.getAttrType());
                tmpAttrValue = StringHelper.trim(attr.getAttrValue());

                // obsolete old / previous record
                obsoleteAttr(conn, orderId, tmpCaseId, tmpItemId, tmpAttrType);

                // then add new one
            	isAdded = addAttr(conn, orderId, tmpCaseId, tmpItemId, tmpAttrType, tmpAttrValue);
            	if(!isAdded) {
            		throw new Exception("cannot add attr " + StringHelper.trim(attr.getAttrType()) + " for item " + StringHelper.trim(attr.getItemId()));
            	}
            }

            conn.commit();
            
            epcAddOrderAttrs.setResult("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            epcAddOrderAttrs.setResult("FAIL");
            epcAddOrderAttrs.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }
    
    
    public boolean addAttr(int smcOrderId, String caseId, String itemId, String attrType, String attrValue) {
        Connection conn = null;
        boolean isAdded = false;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            isAdded = addAttr(conn, smcOrderId, caseId, itemId, attrType, attrValue);
            
            if(isAdded) {
                conn.commit();
                isAdded = true;
            } else {
                conn.rollback();
                isAdded = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return isAdded;
    }
    
    
    // commit by caller
    public boolean addAttr(Connection conn, int smcOrderId, String caseId, String itemId, String attrType, String attrValue) {
        PreparedStatement pstmtInsert = null;
        String sql = "";
        boolean isAdded = false;
        
        try {
            sql = "insert into epc_order_attr ( " +
                  "  rec_id, order_id, case_id, item_id, attr_type, attr_value, " +
                  "  status, create_date " +
                  ") values ( " +
                  "  epc_order_id_seq.nextval,?,?,?,?,substr(?,1,1000), " +
                  "  ?,sysdate " +
                  ") ";
            pstmtInsert = conn.prepareStatement(sql);
            
            pstmtInsert.setInt(1, smcOrderId); // order_id
            pstmtInsert.setString(2, caseId); // case_id
            pstmtInsert.setString(3, itemId); // item_id
            pstmtInsert.setString(4, attrType); // attr_type
            pstmtInsert.setString(5, attrValue); // attr_value
            pstmtInsert.setString(6, "A"); // status - active
            pstmtInsert.executeUpdate();
            
            isAdded = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return isAdded;
    }
    
    
    public ArrayList<String> getAttrValues(Connection conn, int smcOrderId, String attrType) {
    	ArrayList<String> attrValueList = new ArrayList<String>();
    	PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            sql = "select attr_value " +
                  " from epc_order_attr " +
                  " where order_id = ? " +
                  "  and attr_type = ? " +
                  "  and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, attrType); // attr_type
            pstmt.setString(3, "A"); // status - active
            rset = pstmt.executeQuery();
            while(rset.next()) {
            	attrValueList.add(StringHelper.trim(rset.getString("attr_value")));
            } rset.close(); rset = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    	return attrValueList;
    }
    
    
    public TreeMap<String, String> getAttrValuesWithItemIds(int smcOrderId, String attrType) {
    	Connection conn = null;
    	TreeMap<String, String> aMap = new TreeMap<String, String>();
        
        try {
            conn = epcDataSource.getConnection();
            aMap = getAttrValuesWithItemIds(conn, smcOrderId, attrType);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return aMap;
    }
    
    
    public TreeMap<String, String> getAttrValuesWithItemIds(Connection conn, int smcOrderId, String attrType) {
    	TreeMap<String, String> aMap = new TreeMap<String, String>();
    	PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            sql = "select item_id, attr_value " +
                  " from epc_order_attr " +
                  " where order_id = ? " +
                  "  and attr_type = ? " +
                  "  and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, attrType); // attr_type
            pstmt.setString(3, "A"); // status - active
            rset = pstmt.executeQuery();
            while(rset.next()) {
            	aMap.put(StringHelper.trim(rset.getString("item_id")), StringHelper.trim(rset.getString("attr_value")));
            } rset.close(); rset = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    	return aMap;
    }
    
    
    public ArrayList<String> getItemIdsByAttrValue(Connection conn, int smcOrderId, String attrValue, String attrType) {
    	ArrayList<String> itemIdList = new ArrayList<String>();
    	PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            sql = "select item_id " +
                  " from epc_order_attr " +
                  " where order_id = ? " +
                  "  and attr_type = ? " +
                  "  and attr_value = ? " +
                  "  and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, attrType); // attr_type
            pstmt.setString(3, attrValue); // attr_value
            pstmt.setString(4, "A"); // status - active
            rset = pstmt.executeQuery();
            while(rset.next()) {
            	itemIdList.add(StringHelper.trim(rset.getString("item_id")));
            } rset.close(); rset = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    	return itemIdList;
    }
    
    
    public String getAttrValue(int smcOrderId, String caseId, String itemId, String attrType) {
    	Connection conn = null;
    	String attrValue = "";
        
        try {
            conn = epcDataSource.getConnection();

            attrValue = getAttrValue(conn, smcOrderId, caseId, itemId, attrType);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return attrValue;
    }
    
    
    public String getAttrValue(Connection conn, int smcOrderId, String caseId, String itemId, String attrType) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        int idx = 4;
        String attrValue = "";
        
        try {
            sql = "select attr_value " +
                  " from epc_order_attr " +
                  " where order_id = ? " +
                  "  and attr_type = ? " +
                  "  and status = ? ";
            if(!"".equals(caseId)) {
                sql += "  and case_id = ? ";
            }
            if(!"".equals(itemId)) {
                sql += "  and item_id = ? ";
            }
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, attrType); // attr_type
            pstmt.setString(3, "A"); // status - active
            if(!"".equals(caseId)) {
                pstmt.setString(idx, caseId); // case_id
                idx++;
            }
            if(!"".equals(itemId)) {
                pstmt.setString(idx, itemId); // item_id
                idx++;
            }
            rset = pstmt.executeQuery();
            if(rset.next()) {
                attrValue = StringHelper.trim(rset.getString("attr_value"));
            } rset.close(); 
            rset = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return attrValue;
    }
    
    
    // commit by caller
    public boolean obsoleteAttr(Connection conn, int smcOrderId, String itemId, String attrType) {
        PreparedStatement pstmtUpdate = null;
        String sql = "";
        boolean isUpdated = false;
        
        try {
            sql = "update epc_order_attr " +
                  "  set status = ?, " + 
                  "      modify_date = sysdate " +
                  " where order_id = ? " +
                  "  and item_id = ? " + 
                  "  and attr_type = ? " +
                  "  and status = ? ";
            pstmtUpdate = conn.prepareStatement(sql);
            
            pstmtUpdate.setString(1, "O"); // status - obsolete
            pstmtUpdate.setInt(2, smcOrderId); // order_id
            pstmtUpdate.setString(3, itemId); // item_id
            pstmtUpdate.setString(4, attrType); // attr_type
            pstmtUpdate.setString(5, "A"); // status - active
            pstmtUpdate.executeUpdate();
            
            isUpdated = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return isUpdated;
    }


    public boolean obsoleteAttr(int smcOrderId, String caseId, String itemId, String attrType) {
        Connection conn = null;
        boolean isUpdated = false;
        
        try {
            conn = epcDataSource.getConnection();

            isUpdated = obsoleteAttr(conn, smcOrderId, caseId, itemId, attrType);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdated;
    }


    public boolean obsoleteAttr(Connection conn, int smcOrderId, String caseId, String itemId, String attrType) {
        PreparedStatement pstmtUpdate = null;
        String sql = "";
        boolean isUpdated = false;
        int idx = 5;
        
        try {
            sql = "update epc_order_attr " +
                  "  set status = ?, " + 
                  "      modify_date = sysdate " +
                  " where order_id = ? " +
                  "  and attr_type = ? " +
                  "  and status = ? ";
            if(!"".equals(caseId)) {
                sql += "  and case_id = ? ";
            }
            if(!"".equals(itemId)) {
                sql += "  and item_id = ? ";
            }
            pstmtUpdate = conn.prepareStatement(sql);
            
            pstmtUpdate.setString(1, "O"); // status - obsolete
            pstmtUpdate.setInt(2, smcOrderId); // order_id
            pstmtUpdate.setString(3, attrType); // attr_type
            pstmtUpdate.setString(4, "A"); // status - active
            if(!"".equals(caseId)) {
                pstmtUpdate.setString(idx, caseId); // case_id
                idx++;
            }
            if(!"".equals(itemId)) {
                pstmtUpdate.setString(idx, itemId); // item_id
                idx++;
            }
            pstmtUpdate.executeUpdate();
            
            isUpdated = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return isUpdated;
    }
    
    
    public boolean obsoleteAttrs(Connection conn, int smcOrderId, String attrType) {
        PreparedStatement pstmtUpdate = null;
        String sql = "";
        boolean isUpdated = false;
        
        try {
            sql = "update epc_order_attr " +
                  "  set status = ?, " + 
                  "      modify_date = sysdate " +
                  " where order_id = ? " +
                  "  and attr_type = ? " +
                  "  and status = ? ";
            pstmtUpdate = conn.prepareStatement(sql);
            
            pstmtUpdate.setString(1, "O"); // status - obsolete
            pstmtUpdate.setInt(2, smcOrderId); // order_id
            pstmtUpdate.setString(3, attrType); // attr_type
            pstmtUpdate.setString(4, "A"); // status - active
            pstmtUpdate.executeUpdate();
            
            isUpdated = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return isUpdated;
    }
    
    
    public boolean obsoleteAttrByCaseId(Connection conn, int smcOrderId, String caseId) {
        PreparedStatement pstmtUpdate = null;
        String sql = "";
        boolean isUpdated = false;
        
        try {
            sql = "update epc_order_attr " +
                  "  set status = ?, " + 
                  "      modify_date = sysdate " +
                  " where order_id = ? " +
                  "  and case_id = ? " + 
                  "  and status = ? ";
            pstmtUpdate = conn.prepareStatement(sql);
            
            pstmtUpdate.setString(1, "O"); // status - obsolete
            pstmtUpdate.setInt(2, smcOrderId); // order_id
            pstmtUpdate.setString(3, caseId); // case_id
            pstmtUpdate.setString(4, "A"); // status - active
            pstmtUpdate.executeUpdate();
            
            isUpdated = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return isUpdated;
    }


    public boolean updateAttrValue(Connection conn, int smcOrderId, String caseId, String itemId, String attrType, String attrValue) {
        PreparedStatement pstmtUpdate = null;
        String sql = "";
        boolean isUpdated = false;
        int idx = 5;
        
        try {
            sql = "update epc_order_attr " +
                  "  set attr_value = ?, " + 
                  "      modify_date = sysdate " +
                  " where order_id = ? " +
                  "  and attr_type = ? " +
                  "  and status = ? ";
            if(!"".equals(caseId)) {
                sql += "  and case_id = ? ";
            }
            if(!"".equals(itemId)) {
                sql += "  and item_id = ? ";
            }
            pstmtUpdate = conn.prepareStatement(sql);
            
            pstmtUpdate.setString(1, attrValue); // attr_value
            pstmtUpdate.setInt(2, smcOrderId); // order_id
            pstmtUpdate.setString(3, attrType); // attr_type
            pstmtUpdate.setString(4, "A"); // status - active
            if(!"".equals(caseId)) {
                pstmtUpdate.setString(idx, caseId); // case_id
                idx++;
            }
            if(!"".equals(itemId)) {
                pstmtUpdate.setString(idx, itemId); // item_id
                idx++;
            }

            pstmtUpdate.executeUpdate();
            pstmtUpdate.close();
            
            isUpdated = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(pstmtUpdate != null) { pstmtUpdate.close(); } } catch (Exception e) {}
        }
        return isUpdated;
    }
}
