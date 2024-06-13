package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.sales.bean.EpcConfiguredValue;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.EpcQuoteProductCandidate;

@Service
public class EpcSaRemarkHandler {

    public static final String SA_REMARK_TEMPLATE = "Sales_Agreement_Remarks_Specification_Template";
    private final Logger logger = LoggerFactory.getLogger(EpcSaRemarkHandler.class);
    
    public void updateOrderSaRemark(Connection conn, int orderId, EpcQuoteItem epcQuoteItem) throws Exception {

        String caseId = "";
        String sql = "";

        PreparedStatement pstmtDelete = null;
        PreparedStatement pstmtInsert = null;

        try {
            caseId = epcQuoteItem.getProductCandidateObj().getId();

            // delete all SA remark
            sql = "delete from epc_order_sa_remark where order_id = ? and case_id = ?";
            
            pstmtDelete = conn.prepareStatement(sql);
            pstmtDelete.setInt(1, orderId);
            pstmtDelete.setString(2, caseId);
            pstmtDelete.executeUpdate();
            
            // use recursive function to insert SA remark records
            sql = "insert into epc_order_sa_remark ( " +
                  "  order_id, case_id, item_id, parent_item_id," + 
                  "  sa_remark_zh_hk, sa_remark_en, create_date " + 
                  ") values (" + 
                  "  ?, ?, ?, ?, " + 
                  "  ?, ?, sysdate " + 
                  ") "; 
            pstmtInsert = conn.prepareStatement(sql);
            
            createOrderSaRemark(orderId, caseId, "", pstmtInsert, epcQuoteItem.getProductCandidateObj());
            pstmtInsert.executeBatch();

        } catch (Exception e) {
            throw e;
        } finally {
            try { pstmtDelete.close(); } catch (Exception ignoreE) {}
            try { pstmtInsert.close(); } catch (Exception ignoreE) {}
        }
    }

    public void createOrderSaRemark(int orderId, String caseId, String parentId, PreparedStatement pstmtItem, EpcQuoteProductCandidate productCandidateObj) throws Exception {

        String itemId = productCandidateObj.getId();
        String itemTemplate = productCandidateObj.getTemplateName();
        
        if (itemTemplate.equals(SA_REMARK_TEMPLATE)) {
            pstmtItem.setInt(1, orderId);
            pstmtItem.setString(2, caseId);
            pstmtItem.setString(3, itemId);
            pstmtItem.setString(4, parentId);
            pstmtItem.setString(5, null);   // sa_remark_zh_hk
            pstmtItem.setString(6, null);   // sa_remark_en

            ArrayList<EpcConfiguredValue> configuredValueList = productCandidateObj.getConfiguredValue();
                        
            for (int i=0; i<configuredValueList.size(); i++) {
                
                EpcConfiguredValue val = configuredValueList.get(i);
                switch ( val.getName()) {
                    case "Sales_Agreement_Remarks_Chinese":
                        pstmtItem.setString(5,val.getValue());
                        break;                  
                    case "Sales_Agreement_Remarks_English": 
                        pstmtItem.setString(6,val.getValue());
                        break;
                }
            }   
            pstmtItem.addBatch();
        }
        
        for (int i=0; i<productCandidateObj.getChildEntity().size(); i++) {
            createOrderSaRemark(orderId, caseId, itemId, pstmtItem, productCandidateObj.getChildEntity().get(i));
        }
    }
    
    public ArrayList<String> getOrderSaRemarkForSA(Connection conn, int orderId, String orderLang) {
        
        ArrayList<String> orderSaRemarkList = new ArrayList<String>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql;
        
        try {
            
            sql = "SELECT　sa_remark_zh_hk, sa_remark_en FROM ( " +
                  "SELECT a.sa_remark_zh_hk, a.sa_remark_en, " +
                  "ROW_NUMBER() OVER (PARTITION BY c.cpq_item_guid ORDER BY a.create_date DESC) item_guid_time_rank " +
                  "FROM epc_order_sa_remark a, epc_order_item b, epc_order_item c " +
                  "WHERE a.order_id = ? " +
                  "AND a.order_id = b.order_id " +
                  "AND a.case_id = b.case_id " +
                  "AND a.parent_item_id = b.item_id " +
                  "AND (b.item_cat IS NULL OR b.item_cat NOT IN (?, ?)) " +
                  "AND a.order_id = c.order_id " +
                  "AND a.case_id = c.case_id " +
                  "AND a.item_id = c.item_id " +
                  ") " +
                  "WHERE item_guid_time_rank = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // epc_order_sa_remark.order_id
            pstmt.setString(2, "APPLECARE"); // item_cat of parent item, not APPLECARE
            pstmt.setString(3, "CONTRACT"); // item_cat of parent item, not CONTRACT
            pstmt.setInt(4, 1); // item_guid_time_rank, use to get the latest remark by create_date with the same guid
            rset = pstmt.executeQuery();
            while (rset.next()) {
                if ("C".equals(orderLang)) {
                    orderSaRemarkList.add(rset.getString("sa_remark_zh_hk"));
                } else { // default is ENG
                    orderSaRemarkList.add(rset.getString("sa_remark_en"));
                }
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            try { if (rset!=null) rset.close(); } catch (Exception ignoreE) {}
            try { if (pstmt!=null) pstmt.close(); } catch (Exception ignoreE) {}
        }
        
        return orderSaRemarkList;
    }
}
