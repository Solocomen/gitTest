package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcDeliveryDetail;
import epc.epcsalesapi.sales.bean.EpcDeliveryInfo;

@Service
public class EpcGetDeliveryInfoHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcGetDeliveryInfoHandler.class);

    private DataSource epcDataSource;

    public EpcGetDeliveryInfoHandler(DataSource epcDataSource) {
        this.epcDataSource = epcDataSource;
    }


    public EpcDeliveryInfo getPickupLocation(int smcOrderId) {
        EpcDeliveryInfo epcDeliveryInfo = new EpcDeliveryInfo();
        epcDeliveryInfo.setOrderId(smcOrderId);
        ArrayList<EpcDeliveryDetail> detailList = new ArrayList<EpcDeliveryDetail>();
        epcDeliveryInfo.setDetails(detailList);
        EpcDeliveryDetail epcDeliveryDetail = null;
        ArrayList<String> itemIdList = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtItem = null;
        ResultSet rset = null;
        ResultSet rsetItem = null;
        String sql = "";
        
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "select item_id from epc_order_item where delivery_id = ? ";
            pstmtItem = conn.prepareStatement(sql);
            
            sql = "select * " +
                    "  from epc_order_delivery " +
                    " where order_id = ? " +
                    "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, "A"); // status
            rset = pstmt.executeQuery();
            while(rset.next()) {
                epcDeliveryDetail = new EpcDeliveryDetail();
                epcDeliveryDetail.setDeliveryId(rset.getInt("delivery_id"));
                epcDeliveryDetail.setDeliveryMethod(StringHelper.trim(rset.getString("delivery_method")));
                epcDeliveryDetail.setPickupStore(StringHelper.trim(rset.getString("pickup_location")));
                epcDeliveryDetail.setDeliveryAddress1(StringHelper.trim(rset.getString("deliver_addr_1")));
                epcDeliveryDetail.setDeliveryAddress2(StringHelper.trim(rset.getString("deliver_addr_2")));
                epcDeliveryDetail.setDeliveryAddress3(StringHelper.trim(rset.getString("deliver_district")));
                epcDeliveryDetail.setDeliveryAddress4(StringHelper.trim(rset.getString("deliver_area")));
                epcDeliveryDetail.setDeliveryContactPerson(StringHelper.trim(rset.getString("deliver_contact_person")));
                epcDeliveryDetail.setDeliveryContactNo(StringHelper.trim(rset.getString("deliver_contact_no")));
                epcDeliveryDetail.setAddrType(StringHelper.trim(rset.getString("addr_type")));
                epcDeliveryDetail.setAddrToProfile(StringHelper.trim(rset.getString("addr_to_profile")));

//            	itemIdList = epcOrderAttrHandler.getItemIdsByAttrValue(conn, smcOrderId, "" + rset.getInt("delivery_id"), epcOrderAttrHandler.ATTR_TYPE_DELIVERY_ID);
                itemIdList = new ArrayList<String>();
                epcDeliveryDetail.setItems(itemIdList);
                pstmtItem.setInt(1, rset.getInt("delivery_id")); // delivery_id
                rsetItem = pstmtItem.executeQuery();
                while(rsetItem.next()) {
                    itemIdList.add(StringHelper.trim(rsetItem.getString("item_id")));
                }
                
                
                detailList.add(epcDeliveryDetail);
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;
            
            epcDeliveryInfo.setResult("SUCCESS");
        } catch(Exception e) {
            e.printStackTrace();
            
            epcDeliveryInfo.setResult("FAIL");
            epcDeliveryInfo.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcDeliveryInfo;
    }
}
