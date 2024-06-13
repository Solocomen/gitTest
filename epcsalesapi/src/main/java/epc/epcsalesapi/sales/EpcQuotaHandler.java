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
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import epc.epcsalesapi.sales.bean.EpcStaffOfferQuota;

@Service
public class EpcQuotaHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcQuotaHandler.class);

    private final int INCOMING_OFFER_COUNT = 1; // assume add 1 offer at once

    private DataSource epcDataSource;
    private EpcSecurityHelper epcSecurityHelper;
    private EpcOrderAttrHandler epcOrderAttrHandler;

    public EpcQuotaHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper, EpcOrderAttrHandler epcOrderAttrHandler) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcOrderAttrHandler = epcOrderAttrHandler;
    }

    
    public void countStaffOfferQuota(EpcStaffOfferQuota epcStaffOfferQuota) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtCount = null;
        ResultSet rset = null;
        ResultSet rsetCount = null;
        String sql = "";
        String offerName = "";
        int orderId = 0;
        String company = "";
        String staffId = "";
        String quotaGroup = "";
        int quotaGroupId = 0;
        int quotaCap = 0;
        int quotaConsumed = 0;
        String canEnjoy = "";

        try {
            conn = epcDataSource.getConnection();

            // get data
            orderId = epcStaffOfferQuota.getOrderId();
            offerName = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcStaffOfferQuota.getOfferName()));
            company = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcStaffOfferQuota.getCompany()));
            staffId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcStaffOfferQuota.getStaffId()));
            // end of get data


            // get quota header / setting
            sql = "select b.* " +
                  "  from epc_quota_detail a, epc_quota_hdr b " +
                  " where ref_code = ? " +
                  "   and a.hdr_id = b.rec_id ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, offerName); // ref_code - offerName
            rset = pstmt.executeQuery();
            if(rset.next()) {
                // if quota group is found
                quotaGroupId = rset.getInt("rec_id");
                quotaGroup = StringHelper.trim(rset.getString("group_name"));
                quotaCap = rset.getInt("quota");

                canEnjoy = "N"; // reset
                quotaConsumed = 0; // reset

                // count quota consumed (already paid, not that current order)
                sql = "select /*+ use_nl(a b c oo cc ii qd qh) */ " +
                      "       count(1) as quota_consumed " +
                      "  from epc_order_attr a, epc_order_attr b, epc_order_attr c, epc_order oo, epc_order_case cc, epc_quota_detail qd, epc_quota_hdr qh " +
                      " where a.attr_value = ? " +
                      "   and a.attr_type = ? " +
                      "   and a.status = ? " +
                      "   and b.attr_value = ? " +
                      "   and b.attr_type = ? " +
                      "   and b.order_id = a.order_id " +
                      "   and b.case_id = a.case_id " +
                      "   and b.status = ? " +
                      "   and c.order_id = a.order_id " +
                      "   and c.attr_type = ? " +
                      "   and c.attr_value in (?,?) " +
                      "   and c.status = ? " +
                      "   and oo.order_id = a.order_id " +
                      "   and oo.order_id != ? " +
                      "   and cc.order_id = oo.order_id " +
                      "   and cc.case_id = a.case_id " +
                      "   and cc.cancel_receipt is null " +
                      "   and qd.ref_code = cc.cpq_offer_desc " +
                      "   and qh.rec_id = qd.hdr_id " +
                      "   and qh.rec_id = ? " +
                      "   and trunc(sysdate) - trunc(oo.place_order_date) <= qh.quota_period " +
                      " group by qh.group_name, qh.quota ";
                pstmtCount = conn.prepareStatement(sql);
                pstmtCount.setString(1, company); // attr_value - company
                pstmtCount.setString(2, "STAFF_COMPANY"); // attr_type - STAFF_COMPANY
                pstmtCount.setString(3, "A"); // status - A
                pstmtCount.setString(4, staffId); // attr_value - staffId
                pstmtCount.setString(5, "STAFF_ID"); // attr_type - STAFF_ID
                pstmtCount.setString(6, "A"); // status - A
                pstmtCount.setString(7, "ORDER_TYPE"); // attr_type - ORDER_TYPE
                pstmtCount.setString(8, epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_NORMAL); // attr_value - NORMAL
                pstmtCount.setString(9, epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_CHECKOUT); // attr_value - CHECKOUT
                pstmtCount.setString(10, "A"); // status - A
                pstmtCount.setInt(11, orderId); // order_id
                pstmtCount.setInt(12, quotaGroupId); // rec_id
                
                rsetCount = pstmtCount.executeQuery();
                if(rsetCount.next()) {
                    quotaConsumed = rsetCount.getInt("quota_consumed");
                } rsetCount.close();
                pstmtCount.close();
                // end of count quota consumed (already paid, not that current order)

                // count quota consumed (not paid, current order)
                sql = "select /*+ use_nl(a b c oo cc ii qd qh) */ " +
                      "       count(1) as quota_consumed " +
                      "  from epc_order_attr a, epc_order_attr b, epc_order_attr c, epc_order oo, epc_order_case cc, epc_quota_detail qd, epc_quota_hdr qh " +
                      " where a.attr_value = ? " +
                      "   and a.attr_type = ? " +
                      "   and a.status = ? " +
                      "   and b.attr_value = ? " +
                      "   and b.attr_type = ? " +
                      "   and b.order_id = a.order_id " +
                      "   and b.case_id = a.case_id " +
                      "   and b.status = ? " +
                      "   and c.order_id = a.order_id " +
                      "   and c.attr_type = ? " +
                      "   and c.attr_value in (?,?) " +
                      "   and c.status = ? " +
                      "   and oo.order_id = a.order_id " +
                      "   and oo.order_id = ? " +
                      "   and cc.order_id = oo.order_id " +
                      "   and cc.case_id = a.case_id " +
                      "   and cc.cancel_receipt is null " +
                      "   and qd.ref_code = cc.cpq_offer_desc " +
                      "   and qh.rec_id = qd.hdr_id " +
                      "   and qh.rec_id = ? " +
                      " group by qh.group_name, qh.quota ";
                pstmtCount = conn.prepareStatement(sql);
                pstmtCount.setString(1, company); // attr_value - company
                pstmtCount.setString(2, "STAFF_COMPANY"); // attr_type - STAFF_COMPANY
                pstmtCount.setString(3, "A"); // status - A
                pstmtCount.setString(4, staffId); // attr_value - staffId
                pstmtCount.setString(5, "STAFF_ID"); // attr_type - STAFF_ID
                pstmtCount.setString(6, "A"); // status - A
                pstmtCount.setString(7, "ORDER_TYPE"); // attr_type - ORDER_TYPE
                pstmtCount.setString(8, epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_NORMAL); // attr_value - NORMAL
                pstmtCount.setString(9, epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_CHECKOUT); // attr_value - CHECKOUT
                pstmtCount.setString(10, "A"); // status - A
                pstmtCount.setInt(11, orderId); // order_id
                pstmtCount.setInt(12, quotaGroupId); // rec_id
                
                rsetCount = pstmtCount.executeQuery();
                if(rsetCount.next()) {
                    quotaConsumed += rsetCount.getInt("quota_consumed");
                } rsetCount.close();
                pstmtCount.close();
                // end of count quota consumed (not paid, current order)

                if(quotaConsumed + INCOMING_OFFER_COUNT <= quotaCap) {
                    canEnjoy = "Y";
                }
            } else {
                // NO quota group !!!
                quotaGroup = "";
                quotaCap = 0;
                canEnjoy = "Y";
                quotaConsumed = 0;
            }
            rset.close();
            pstmt.close();
            // end of get quota header / setting

            epcStaffOfferQuota.setQuotaGroup(quotaGroup);
            epcStaffOfferQuota.setQuotaCap(quotaCap);
            epcStaffOfferQuota.setQuotaConsumed(quotaConsumed);
            epcStaffOfferQuota.setCanEnjoy(canEnjoy);

            epcStaffOfferQuota.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            
            epcStaffOfferQuota.setResult(EpcApiStatusReturn.RETURN_FAIL);
            epcStaffOfferQuota.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }
}
