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
import epc.epcsalesapi.sales.bean.tnc.EpcOfferTnc;

@Service
public class EpcTncHandler {

    public static final String TNC_TEMPLATE = "Terms_And_Conditions_Specification";
    private final Logger logger = LoggerFactory.getLogger(EpcTncHandler.class);
    
    public void updateOrderOfferTnc(Connection conn, int orderId, EpcQuoteItem epcQuoteItem) throws Exception {

        String caseId = "";
        String sql = "";

        PreparedStatement pstmtDelete = null;
        PreparedStatement pstmtInsert = null;

        try {
            caseId = epcQuoteItem.getProductCandidateObj().getId();

            // delete all SA remark
            sql = "delete from epc_order_tnc where order_id = ? and case_id = ?";
            
            pstmtDelete = conn.prepareStatement(sql);
            pstmtDelete.setInt(1, orderId);
            pstmtDelete.setString(2, caseId);
            pstmtDelete.executeUpdate();
            
            // use recursive function to insert SA remark records
            sql = "insert into epc_order_tnc ( " + 
                  "  order_id, case_id, item_id, parent_item_id, tnc_number, " + 
                  "  tnc_short_description_zh_hk, tnc_zh_hk, tnc_short_description_en, tnc_en, create_date " + 
                  ") values (" + 
                  "  ?, ?, ?, ?, ?, " + 
                  "  ?, ?, ?, ?, sysdate " + 
                  ") "; 
            pstmtInsert = conn.prepareStatement(sql);
            
            createOrderOfferTnc(orderId, caseId, "", pstmtInsert, epcQuoteItem.getProductCandidateObj());
            pstmtInsert.executeBatch();

        } catch (Exception e) {
            throw e;
        } finally {
            try { pstmtDelete.close(); } catch (Exception ignoreE) {}
            try { pstmtInsert.close(); } catch (Exception ignoreE) {}
        }
    }

    public void createOrderOfferTnc(int orderId, String caseId, String parentId, PreparedStatement pstmtItem, EpcQuoteProductCandidate productCandidateObj) throws Exception {

        String itemId = productCandidateObj.getId();
        String itemTemplate = productCandidateObj.getTemplateName();
        
        if (itemTemplate.equals(TNC_TEMPLATE)) {
            pstmtItem.setInt(1, orderId);
            pstmtItem.setString(2, caseId);
            pstmtItem.setString(3, itemId);
            pstmtItem.setString(4, parentId);
            pstmtItem.setString(5, null); // tnc_number
            pstmtItem.setString(6, null); // tnc_short_description_zh_hk
            pstmtItem.setString(7, null); // tnc_zh_hk
            pstmtItem.setString(8, null); // tnc_short_description_en  
            pstmtItem.setString(9, null); // tnc_en

            ArrayList<EpcConfiguredValue> configuredValueList = productCandidateObj.getConfiguredValue();
                        
            for (int i=0; i<configuredValueList.size(); i++) {
                
                EpcConfiguredValue val = configuredValueList.get(i);
                switch ( val.getName()) {
                    case "TnCNumber":
                        pstmtItem.setString(5,val.getValue());
                        break;
                    case "TnCShortDescriptionZhHK": 
                        pstmtItem.setString(6,val.getValue());
                        break;
                    case "TnCZhHK":
                        pstmtItem.setString(7,val.getValue());
                        break;
                    case "TnCShortDescriptionEN": 
                        pstmtItem.setString(8,val.getValue());
                        break;
                    case "TnCEN":
                        pstmtItem.setString(9,val.getValue());
                }
            }   
            pstmtItem.addBatch();
        }
        
        for (int i=0; i<productCandidateObj.getChildEntity().size(); i++) {
            createOrderOfferTnc(orderId, caseId, itemId, pstmtItem, productCandidateObj.getChildEntity().get(i));
        }
    }
    
    public ArrayList<EpcOfferTnc> getOrderOfferTncForSA(Connection conn, int orderId) {
        
        ArrayList<EpcOfferTnc> orderOfferTncList = new ArrayList<EpcOfferTnc>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql;
        
        try {
            
            sql = "SELECT　case_id, item_id, parent_item_id, tnc_number,  " +
                  "tnc_short_description_zh_hk, tnc_short_description_en, tnc_zh_hk, tnc_en " +
                  "FROM ( " +
                  "SELECT a.case_id, a.item_id, a.parent_item_id, a.tnc_number,  " +
                  "a.tnc_short_description_zh_hk, a.tnc_short_description_en, a.tnc_zh_hk, a.tnc_en, " +
                  "ROW_NUMBER() OVER (PARTITION BY c.cpq_item_guid ORDER BY a.create_date DESC) item_guid_time_rank " +
                  "FROM epc_order_tnc a, epc_order_item b, epc_order_item c " +
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
            pstmt.setInt(1, orderId); // epc_order_tnc.order_id
            pstmt.setString(2, "APPLECARE"); // item_cat of parent item, not APPLECARE
            pstmt.setString(3, "CONTRACT"); // item_cat of parent item, not CONTRACT
            pstmt.setInt(4, 1); // item_guid_time_rank, use to get the latest tnc by create_date with the same guid
            rset = pstmt.executeQuery();
            while (rset.next()) {
                EpcOfferTnc epcOfferTnc = new EpcOfferTnc();
                epcOfferTnc.setCaseId(rset.getString("case_id"));
                epcOfferTnc.setItemId(rset.getString("item_id"));
                epcOfferTnc.setParentItemId(rset.getString("parent_item_id"));
                epcOfferTnc.setTncNumber(rset.getString("tnc_number"));
                epcOfferTnc.setTncShortDescZhHk(rset.getString("tnc_short_description_zh_hk"));
                epcOfferTnc.setTncShortDescEn(rset.getString("tnc_short_description_en"));
                epcOfferTnc.setTncUrlZhHk(rset.getString("tnc_zh_hk"));
                epcOfferTnc.setTncUrlEn(rset.getString("tnc_en"));
                orderOfferTncList.add(epcOfferTnc);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            try { if (rset!=null) rset.close(); } catch (Exception ignoreE) {}
            try { if (pstmt!=null) pstmt.close(); } catch (Exception ignoreE) {}
        }
        
        return orderOfferTncList;
    }
    
    public ArrayList<EpcOfferTnc> getOrderOfferTnc(Connection conn, int orderId) {
        
        ArrayList<EpcOfferTnc> orderOfferTncList = new ArrayList<EpcOfferTnc>();
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql;
        
        try {
            
            sql = "SELECT case_id, item_id, parent_item_id, tnc_number,  " +
                  "tnc_short_description_zh_hk, tnc_short_description_en, tnc_zh_hk, tnc_en " +
                  "FROM epc_order_tnc " +
                  "WHERE order_id = ? ";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // epc_order_tnc.order_id

            rset = pstmt.executeQuery();
            while (rset.next()) {
                EpcOfferTnc epcOfferTnc = new EpcOfferTnc();
                epcOfferTnc.setCaseId(rset.getString("case_id"));
                epcOfferTnc.setItemId(rset.getString("item_id"));
                epcOfferTnc.setParentItemId(rset.getString("parent_item_id"));
                epcOfferTnc.setTncNumber(rset.getString("tnc_number"));
                epcOfferTnc.setTncShortDescZhHk(rset.getString("tnc_short_description_zh_hk"));
                epcOfferTnc.setTncShortDescEn(rset.getString("tnc_short_description_en"));
                epcOfferTnc.setTncUrlZhHk(rset.getString("tnc_zh_hk"));
                epcOfferTnc.setTncUrlEn(rset.getString("tnc_en"));
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            try { if (rset!=null) rset.close(); } catch (Exception ignoreE) {}
            try { if (pstmt!=null) pstmt.close(); } catch (Exception ignoreE) {}
        }
        
        return orderOfferTncList;
    }

}
