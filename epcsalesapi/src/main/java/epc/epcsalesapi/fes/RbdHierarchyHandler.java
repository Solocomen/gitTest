package epc.epcsalesapi.fes;

import epc.epcsalesapi.fes.bean.StoreSalesHierarchyResult;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.LinkedList;


@Service
public class RbdHierarchyHandler {

    private final Logger logger = LoggerFactory.getLogger(RbdHierarchyHandler.class);

    @Autowired
    private DataSource fesDataSource;

    @Autowired
    private EpcSecurityHelper epcSecurityHelper;

    final static String SMSQL = "select u.username, u.userid, r.rbd_unit_id, r.rbd_unit_code " +
            "from rbd_unit r, user_info u " +
            "where r.rbd_unit_code in (?) " +
            "and r.in_chrg_sales_mgr = u.userid(+)";

    final static String SICSQL = "select username, userid  " +
            "from user_info  " +
            "where rbd_unit_id = 10051  " +
            "and groupid = 47  " +
            "and nvl(to_date,date'2099-12-31') > sysdate";

    public StoreSalesHierarchyResult getStoreSalesHierarchy(String loginLocation ){

        StoreSalesHierarchyResult result = new StoreSalesHierarchyResult();
        result.setLoginLocation(epcSecurityHelper.validateString(loginLocation));
        LinkedList<HashMap<String, String>> storeManagersList = new LinkedList<>();
        LinkedList<HashMap<String, String>> storeInChargeList = new LinkedList<>();

        try (Connection conn = fesDataSource.getConnection()){

            try(PreparedStatement pstmt = conn.prepareStatement(SMSQL)){

                pstmt.setString(1, epcSecurityHelper.validateString(loginLocation));
                try(ResultSet rset = pstmt.executeQuery()){
                while (rset.next()) {
                    HashMap<String, String> smMap = new HashMap<>();
                    smMap.put("username",StringHelper.trim(rset.getString("username")));
                    smMap.put("userid",StringHelper.trim(rset.getString("userid")));
                    storeManagersList.add(smMap);
                }
                result.setStoreManagers(storeManagersList);
                }
            }

            try (PreparedStatement sicPstmt = conn.prepareStatement(SICSQL)){
            	try(ResultSet rset = sicPstmt.executeQuery()){
                while (rset.next()) {
                    HashMap<String, String> sicMap = new HashMap<>();
                    sicMap.put("username",StringHelper.trim(rset.getString("username")));
                    sicMap.put("userid",StringHelper.trim(rset.getString("userid")));
                    storeInChargeList.add(sicMap);
                }
                result.setStoreInCharge(storeInChargeList);
            }
            }


        } catch (Exception e) {
            e.printStackTrace();
            logger.info(loginLocation,e);
        }
        return result;
    }

}
