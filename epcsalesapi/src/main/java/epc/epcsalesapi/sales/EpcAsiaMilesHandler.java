package epc.epcsalesapi.sales;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.asiaMiles.CreateAsiaMiles;


@Service
public class EpcAsiaMilesHandler {

    private final Logger logger = LoggerFactory.getLogger(EpcAsiaMilesHandler.class);

    @Autowired
    private DataSource crmFesDataSource;
    
    @Autowired
    private EpcSecurityHelper epcSecurityHelper;


    public CreateAsiaMiles createAsiaMilesRecord(CreateAsiaMiles createAsiaMiles) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        CallableStatement cstmt = null;
        ResultSet rset = null;
        String sql = "";
        int arMiles = 0;
        int arRuleId = 0;
        String custNum = "";
        String subrNum = "";
        String orderReference = "";
        String quoteItemGuid = "";
        String acknowledgeMobile = "";
        int userid = 0;
        int salesmanId = 0;
        int oResult = 0;
        String oErrMsg = "";
        boolean isValid = true;
        StringBuilder errMsgSB = new StringBuilder();
        int idx = 1;
        String logStr = "[createAsiaMilesRecord]";

        try {
            conn = crmFesDataSource.getConnection();
            conn.setAutoCommit(false);

            // get data
            arRuleId = createAsiaMiles.getAsiaMilesRuleId();
            custNum = epcSecurityHelper.encodeForSQL(StringHelper.trim(createAsiaMiles.getCustNum()));
            subrNum = epcSecurityHelper.encodeForSQL(StringHelper.trim(createAsiaMiles.getSubrNum()));
            orderReference = epcSecurityHelper.encodeForSQL(StringHelper.trim(createAsiaMiles.getOrderReference()));
            quoteItemGuid = epcSecurityHelper.encodeForSQL(StringHelper.trim(createAsiaMiles.getQuoteItemGuid()));
            acknowledgeMobile = epcSecurityHelper.encodeForSQL(StringHelper.trim(createAsiaMiles.getAcknowledgeMobile()));
            userid = createAsiaMiles.getUserid();
            salesmanId = createAsiaMiles.getSalesmanId();
            // end of get data

            logStr += "[orderRef:" + orderReference + "] ";


            // basic checking
            sql = "select am_point from ar_rule_hdr where rule_id = ? and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, arRuleId); // rule_id
            pstmt.setString(2, "A"); // status - A: active
            rset = pstmt.executeQuery();
            if(rset.next()) {
                arMiles = rset.getInt("am_point");
            } else {
                isValid = false;
                errMsgSB.append("rule id [" + arRuleId + "] is not valid. ");
            } rset.close();
            pstmt.close();
            // end of basic checking


            if(isValid) {
                // create ar record
                sql = "{call ar_handler.create_redemption( " +
                      " ?,?,?,?,?,?,?,?,?,?, " +
                      " ?,?,?,?,?,?,?,?,?,?, " +
                      " ?,?,?,?,?,? " +
                      " )}";
                cstmt = conn.prepareCall(sql);
                cstmt.registerOutParameter(25, Types.INTEGER);
                cstmt.registerOutParameter(26, Types.VARCHAR);

                cstmt.setString(idx++, "EPC"); // i_ref_system
                cstmt.setNull(idx++, Types.VARCHAR); // i_ref_id
                cstmt.setString(idx++, custNum); // i_cust_num
                cstmt.setString(idx++, subrNum); // i_subr_num
                cstmt.setNull(idx++, Types.INTEGER); // i_login_id
                cstmt.setString(idx++, orderReference); // i_order_id
                cstmt.setNull(idx++, Types.VARCHAR); // i_invoice_no
                cstmt.setNull(idx++, Types.INTEGER); // i_sa_logid
                cstmt.setNull(idx++, Types.VARCHAR); // i_om_pend_id
                cstmt.setNull(idx++, Types.INTEGER); // i_ar_profile_id
                cstmt.setNull(idx++, Types.DATE); // i_ar_submission_date
                cstmt.setInt(idx++, arMiles); // i_ar_miles
                cstmt.setString(idx++, quoteItemGuid); // i_remarks - used to store quote_item_guid
                cstmt.setInt(idx++, arRuleId); // i_ar_rule_id
                cstmt.setNull(idx++, Types.VARCHAR); // i_market_code
                cstmt.setNull(idx++, Types.VARCHAR); // i_price_list
                cstmt.setNull(idx++, Types.VARCHAR); // i_product_code
                cstmt.setNull(idx++, Types.VARCHAR); // i_pp_marker
                if(!"".equals(acknowledgeMobile)) {
                    cstmt.setString(idx++, acknowledgeMobile); // i_send_ack_number
                } else {
                    cstmt.setString(idx++, subrNum); // i_send_ack_number
                }
                cstmt.setNull(idx++, Types.INTEGER); // i_member_id
                cstmt.setNull(idx++, Types.VARCHAR); // i_member_last_name
                cstmt.setNull(idx++, Types.VARCHAR); // i_member_other_name
                if(userid == 0) {
                    cstmt.setInt(idx++, 1); // i_create_user_id
                } else {
                    cstmt.setInt(idx++, userid); // i_create_user_id
                }
                if(salesmanId == 0) {
                    cstmt.setInt(idx++, 1); // i_create_salesman_id
                } else {
                    cstmt.setInt(idx++, salesmanId); // i_create_salesman_id
                }

                cstmt.execute();

                oResult = cstmt.getInt(25);
                oErrMsg = StringHelper.trim(cstmt.getString(26));
logger.info("{}{}{}", logStr, "oResult:", oResult);
logger.info("{}{}{}", logStr, "oErrMsg:", oErrMsg);

//                procedure create_redemption (
//                    i_ref_system          in varchar2,
//                    i_ref_id              in varchar2,
//                    i_cust_num            in ar_redemption_info.cust_num%type default null,
//                    i_subr_num            in ar_redemption_info.subr_num%type default null,
//                    i_login_id            in ar_redemption_info.loginid%type default null,
//                    i_order_id            in ar_redemption_info.order_id%type default null,
//                    i_invoice_no          in ar_redemption_info.invoice_no%type default null,
//                    i_sa_logid            in ar_redemption_info.sa_logid%type default null,
//                    i_om_pend_id          in ar_redemption_info.om_pend_id%type default null,
//                    i_ar_profile_id       in ar_redemption_info.ar_profile_id%type default null,
//                    i_ar_submission_date  in ar_redemption_info.ar_submission_date%type default null,
//                    i_ar_miles            in ar_redemption_info.ar_miles%type default null,
//                    i_remarks             in ar_redemption_info.remarks%type default null,
//                    i_ar_rule_id          in ar_redemption_info.ar_rule_id%type default null,
//                    i_market_code         in ar_redemption_info.market_code%type default null,
//                    i_price_list          in ar_redemption_info.price_list%type default null,
//                    i_product_code        in ar_redemption_info.product_code%type default null,
//                    i_pp_marker           in ar_redemption_info.pp_marker%type default null,
//                    i_send_ack_number     in ar_redemption_info.send_ack_number%type default null,
//                    i_member_id           in ar_profile_info.ar_member_id%type,
//                    i_member_last_name    in ar_profile_info.ar_member_last_name%type,
//                    i_member_other_name   in ar_profile_info.ar_member_other_name%type, 
//                    i_create_user_id      in user_info.userid%type,
//                    i_create_salesman_id  in user_info.userid%type,
//                    o_result              out number,   -- 1: OK, 0: Fail 
//                    o_err_msg             out varchar2  
//                  ) 


                if(oResult == 1) {
                    // success
                    sql = "update ar_redemption_info " +
                          "   set quote_item_guid = ?, " +
                          "       remarks = null " +
                          " where order_id = ? " +
                          "   and status = ? " + 
                          "   and remarks = ? ";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, quoteItemGuid); // quote_item_guid
                    pstmt.setString(2, orderReference); // order_id
                    pstmt.setString(3, "I"); // status
                    pstmt.setString(4, quoteItemGuid); // remarks
                    pstmt.executeUpdate();

                    conn.commit();

                    createAsiaMiles.setResult("SUCCESS");
                } else {
                    // error

                    conn.rollback();

                    createAsiaMiles.setResult("FAIL");
                    createAsiaMiles.setErrMsg(oErrMsg);
                }
            } else {
                createAsiaMiles.setResult("FAIL");
                createAsiaMiles.setErrMsg(errMsgSB.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            createAsiaMiles.setResult("FAIL");
            createAsiaMiles.setErrMsg(e.getMessage());
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return createAsiaMiles;
    }
}
