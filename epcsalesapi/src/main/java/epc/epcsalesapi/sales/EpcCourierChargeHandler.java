package epc.epcsalesapi.sales;

import java.math.BigDecimal;
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
public class EpcCourierChargeHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcCourierChargeHandler.class);

    private final String TEMPLATE_NAME_PREPAID_RECHARGE_VOUCHER = "Prepaid_Recharge_Voucher";
    private final String TEMPLATE_NAME_PREPAID_CARD = "Prepaid_Card";
    private final String ADDRESS_TYPE_COMMERCIAL  = "C";
    private final String ADDRESS_TYPE_RESIDENTIAL   = "R";

    private final DataSource epcDataSource;
    private final EpcSecurityHelper epcSecurityHelper;

    public EpcCourierChargeHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
    }


    /***
     * calculate courier charge for EPC order
     *   channel - optional
     */
    public BigDecimal calculateCourierCharge(int smcOrderId, String channel) {
        Connection conn = null;
        BigDecimal charge = new BigDecimal(0);
        String iChannel = epcSecurityHelper.encodeForSQL(channel);

        try {
            conn = epcDataSource.getConnection();

            charge = calculateCourierCharge(conn, smcOrderId, channel);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception e) {}
        }

        return charge;
    }


    public BigDecimal calculateCourierCharge(Connection conn, int smcOrderId, String channel) {
        BigDecimal charge = new BigDecimal(0);
        PreparedStatement pstmt = null;
        PreparedStatement pstmtTemplate = null;
        PreparedStatement pstmtCharge = null;
        ResultSet rset = null;
        ResultSet rsetTemplate = null;
        ResultSet rsetCharge = null;
        String sql = "";
        int deliveryId = 0;
        String addrType = "";
        String templateName = "";
        int itemCnt = 0;
        boolean isPrepaidOnly = false;
        BigDecimal totalChargePerDelivery = new BigDecimal(0);
        String logStr = "[calculateCourierCharge][orderId:" + smcOrderId + "][channel:" + channel + "] ";
        String tmpLogStr = "";

        try {
            // courier charge is/are calculated per courier record !!!
            
            sql = "select template_name, count(1) as item_cnt " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and delivery_id = ? " + 
                  " group by template_name ";
            pstmtTemplate = conn.prepareStatement(sql);

            sql = "select sum(charge_amount) " +
                  "  from epc_order_item a, epc_order_charge b " +
                  " where a.order_id = ? " +
                  "   and a.delivery_id = ? " +
                  "   and b.order_id = a.order_id " +
                  "   and b.parent_item_id = a.item_id " +
                  "   and b.charge_code not in (?) ";
            pstmtCharge = conn.prepareStatement(sql);

            // get courier delivery record(s)
            tmpLogStr = "get courier delivery record(s)";
logger.info("{}{}", logStr, tmpLogStr);

            sql = "select delivery_id, addr_type " +
                  "  from epc_order_delivery " +
                  " where order_id = ? " +
                  "   and delivery_method = ? " +
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, "COURIER"); // delivery_method
            pstmt.setString(3, "A"); // status
            rset = pstmt.executeQuery();
            while(rset.next()) {
                isPrepaidOnly = true; // reset
                totalChargePerDelivery = new BigDecimal(0); // reset

                deliveryId = rset.getInt("delivery_id");
                addrType = StringHelper.trim(rset.getString("addr_type"));

                tmpLogStr = " deliveryId:" + deliveryId + ",addrType:" + addrType;
logger.info("{}{}", logStr, tmpLogStr);


                // check product type (AH / AA / Prepaid items / ...)
                pstmtTemplate.setInt(1, smcOrderId); // order_id
                pstmtTemplate.setInt(2, deliveryId); // delivery_id
                rsetTemplate = pstmtTemplate.executeQuery();
                while(rsetTemplate.next()) {
                    templateName = StringHelper.trim(rsetTemplate.getString("template_name"));
                    itemCnt = rsetTemplate.getInt("item_cnt");

                    tmpLogStr = "  deliveryId:" + deliveryId + ",templateName:" + templateName + ",itemCnt:" + itemCnt;
logger.info("{}{}", logStr, tmpLogStr);

                    if(!TEMPLATE_NAME_PREPAID_RECHARGE_VOUCHER.equals(templateName) && !TEMPLATE_NAME_PREPAID_CARD.equals(templateName)) {
                        isPrepaidOnly = false;
                    }
                } rsetTemplate.close();

                tmpLogStr = " deliveryId:" + deliveryId + ",isPrepaidOnly:" + isPrepaidOnly;
logger.info("{}{}", logStr, tmpLogStr);
                // end of check product type (AH / AA / Prepaid items / ...)


                // get charge 
                pstmtCharge.setInt(1, smcOrderId); // order_id
                pstmtCharge.setInt(2, deliveryId); // delivery_id
                pstmtCharge.setString(3, "99"); // charge_code - not in 
                rsetCharge = pstmtCharge.executeQuery();
                if(rsetCharge.next()) {
                    totalChargePerDelivery = rsetCharge.getBigDecimal(1);
                } rsetCharge.close();

                tmpLogStr = " deliveryId:" + deliveryId + ",totalChargePerDelivery:" + totalChargePerDelivery;
logger.info("{}{}", logStr, tmpLogStr);
                // end of get charge 


                // determine courier charge
                if(isPrepaidOnly) {
                    if(totalChargePerDelivery.compareTo(new BigDecimal(100)) == -1) {
                        // if total charge < $100, then courier charge = $15 
                        charge = charge.add(new BigDecimal(15));
                    } else {
                        // else (total charge >= $100) courier charge = $0 
                        charge = charge.add(new BigDecimal(0));
                    }
                } else {
                    if(totalChargePerDelivery.compareTo(new BigDecimal(500)) == -1) {
                        // if total charge < $500 
                        if(ADDRESS_TYPE_COMMERCIAL.equals(addrType)) {
                            charge = charge.add(new BigDecimal(50));
                        } else {
                            charge = charge.add(new BigDecimal(60));
                        }
                    } else {
                        // else (total charge >= $500)
                        if(ADDRESS_TYPE_COMMERCIAL.equals(addrType)) {
                            charge = charge.add(new BigDecimal(0));
                        } else {
                            charge = charge.add(new BigDecimal(50));
                        }
                    }
                }
                // end of determine courier charge
            } rset.close();
            // end of get courier delivery record(s)
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return charge;
    }
}
