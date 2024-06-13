package epc.epcsalesapi.stock;
import epc.epcsalesapi.fes.user.FesUser;
import epc.epcsalesapi.fes.user.FesUserHandler;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.EpcContactInfoHandler;
import epc.epcsalesapi.sales.EpcGetDeliveryInfoHandler;
import epc.epcsalesapi.sales.EpcOrderHandler;
import epc.epcsalesapi.sales.EpcOrderLogHandler;
import epc.epcsalesapi.sales.bean.EpcDeliveryDetail;
import epc.epcsalesapi.sales.bean.EpcDeliveryInfo;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcLogStockStatus;
import epc.epcsalesapi.sales.bean.EpcLoginChannel;
import epc.epcsalesapi.sales.bean.EpcOrderContact;
import epc.epcsalesapi.sales.bean.EpcOrderItem;
import epc.epcsalesapi.sales.bean.EpcOrderItemInfo;
import epc.epcsalesapi.soap.CancelTmpReserveSoapClient;
import epc.epcsalesapi.soap.CreateTmpReserveSoapClient;
import epc.epcsalesapi.soap.ExtendTmpReserveSoapClient;
import epc.epcsalesapi.soap.UpdateTmpReservePickupLocationSoapClient;
import epc.epcsalesapi.soap.wsclient.cancelTmpReserve.CancelTmpReserve;
import epc.epcsalesapi.soap.wsclient.cancelTmpReserve.CancelTmpReserveResponse;
import epc.epcsalesapi.soap.wsclient.cancelTmpReserve.InvCancelTmpReserveBean;
import epc.epcsalesapi.soap.wsclient.cancelTmpReserve.InvCancelTmpReserveResultBean;
import epc.epcsalesapi.soap.wsclient.createTmpReserve.CreateTmpReserveEpc;
import epc.epcsalesapi.soap.wsclient.createTmpReserve.CreateTmpReserveEpcResponse;
import epc.epcsalesapi.soap.wsclient.createTmpReserve.InvCreateTmpReserveBean;
import epc.epcsalesapi.soap.wsclient.createTmpReserve.InvCreateTmpReserveResultBean;
import epc.epcsalesapi.soap.wsclient.extendTmpReserve.ExtendTmpReserve;
import epc.epcsalesapi.soap.wsclient.extendTmpReserve.ExtendTmpReserveResponse;
import epc.epcsalesapi.soap.wsclient.extendTmpReserve.InvExtendTmpReserveBean;
import epc.epcsalesapi.soap.wsclient.extendTmpReserve.InvExtendTmpReserveResultBean;
import epc.epcsalesapi.soap.wsclient.updateTmpReservePickupLoc.InvUpdateTmpReservePickupLocBean;
import epc.epcsalesapi.soap.wsclient.updateTmpReservePickupLoc.InvUpdateTmpReservePickupLocResultBean;
import epc.epcsalesapi.soap.wsclient.updateTmpReservePickupLoc.UpdateTmpReservePickupLocEpc;
import epc.epcsalesapi.soap.wsclient.updateTmpReservePickupLoc.UpdateTmpReservePickupLocEpcResponse;
import epc.epcsalesapi.stock.bean.EpcCancelRealTicket;
import epc.epcsalesapi.stock.bean.EpcCancelRealTicketDetail;
import epc.epcsalesapi.stock.bean.EpcCancelRealTicketResult;
import epc.epcsalesapi.stock.bean.EpcCheckProduct;
import epc.epcsalesapi.stock.bean.EpcCheckStockAvailable;
import epc.epcsalesapi.stock.bean.EpcCheckStockAvailableResult;
import epc.epcsalesapi.stock.bean.EpcCheckStockAvailableResultDetail;
import epc.epcsalesapi.stock.bean.EpcConfirmErpReserveResult;
import epc.epcsalesapi.stock.bean.EpcConfirmErpReserveResult2;
import epc.epcsalesapi.stock.bean.EpcConfirmReserveItem;
import epc.epcsalesapi.stock.bean.EpcConfirmReserveItemReturn;
import epc.epcsalesapi.stock.bean.EpcConfirmStockReserveResult;
import epc.epcsalesapi.stock.bean.EpcCreateTmpReserveResult;
import epc.epcsalesapi.stock.bean.EpcDeductStockResult;
import epc.epcsalesapi.stock.bean.EpcDoaStock;
import epc.epcsalesapi.stock.bean.EpcDoaStockResult;
import epc.epcsalesapi.stock.bean.EpcExtendTmpReserve;
import epc.epcsalesapi.stock.bean.EpcUpdateStock;
import epc.epcsalesapi.stock.bean.EpcUpdateStockDoaResult;
import epc.epcsalesapi.stock.bean.EpcUpdateStockResult;
import epc.epcsalesapi.stock.bean.erp.ERPStock;
import epc.epcsalesapi.stock.bean.erp.ERPStockUpdate;
import epc.epcsalesapi.stock.bean.erp.ERPStockUpdateResult;
import io.micrometer.common.util.StringUtils;
import epc.epcsalesapi.stock.bean.EpcNoStockReserve;
import epc.epcsalesapi.stock.bean.EpcProductDetail;
import epc.epcsalesapi.stock.bean.EpcRemoveTmpReserve;
import epc.epcsalesapi.stock.bean.EpcStockReserveStatus;
import epc.epcsalesapi.stock.bean.EpcTmpReserve;
import epc.epcsalesapi.stock.bean.EpcUpdateLocationToTmpReserve;
import java.net.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 *
 * @author KerryTsang
 */

@Service
public class EpcStockHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcStockHandler.class);

    private final String ERP_API_USERID = "epc_api_client";
    private final String ERP_API_IS_EPC = "Y";

    private final String REFUND_TYPE_REFUNDING = "REFUNDING";
    private final String REFUND_TYPE_REFUNDED = "REFUNDED";

    
    @Autowired
    private DataSource fesDataSource;

    @Autowired
    private DataSource epcDataSource;
    
    @Autowired
    private EpcOrderHandler epcOrderHandler;

    @Autowired
    private FesUserHandler fesUserHandler;
    
//    @Autowired
//    private EpcOrderAttrHandler epcOrderAttrHandler;
    
    @Autowired
    private EpcContactInfoHandler epcContactInfoHandler;
    
    @Autowired
    private EpcSecurityHelper epcSecurityHelper;

    @Autowired
    private EpcOrderLogHandler epcOrderLogHandler;

    @Autowired
    private CreateTmpReserveSoapClient createTmpReserveSoapClient;

    @Autowired
    private CancelTmpReserveSoapClient cancelTmpReserveSoapClient;

    @Autowired
    private ExtendTmpReserveSoapClient extendTmpReserveSoapClient;

    @Autowired
    private UpdateTmpReservePickupLocationSoapClient updateTmpReservePickupLocationSoapClient;

    @Autowired
    private EpcStockStatusDescHandler epcStockStatusDescHandler;

    @Autowired
    private EpcGetDeliveryInfoHandler epcGetDeliveryInfoHandler;
    
    
    
    public String getWarehouse(String productCode) {
        String warehouse = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            conn = fesDataSource.getConnection();
            
            sql = "select warehouse from stockm where warehouse like 'A%' and product = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productCode); // product
            rset = pstmt.executeQuery();
            if(rset.next()) {
                warehouse = StringHelper.trim(rset.getString("warehouse"));
            } rset.close(); rset = null;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return warehouse;
    }
    
    
    public String getSerial(String productCode) {
        String serial = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            conn = fesDataSource.getConnection();
            
            sql = "select serial_numbers from stockm where warehouse like 'A%' and product = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productCode); // product
            rset = pstmt.executeQuery();
            if(rset.next()) {
                serial = StringHelper.trim(rset.getString("serial_numbers"));
            } rset.close(); rset = null;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return serial;
    }


    public String[] getWarehouseAndSerial(String productCode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String[] rtn = new String[2];
        rtn[0] = ""; // warehouse
        rtn[1] = ""; // serial
        
        try {
            conn = fesDataSource.getConnection();
            
            sql = "select warehouse, serial_numbers from stockm where warehouse like 'A%' and product = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productCode); // product
            rset = pstmt.executeQuery();
            if(rset.next()) {
                rtn[0] = StringHelper.trim(rset.getString("warehouse"));
                rtn[1] = StringHelper.trim(rset.getString("serial_numbers"));
            } rset.close();
            pstmt.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return rtn;
    }
    
    
    public String getProductDesc(String productCode) {
        String productDesc = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            conn = fesDataSource.getConnection();
            
            sql = "select description from stockm where warehouse like 'A%' and product = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productCode); // product
            rset = pstmt.executeQuery();
            if(rset.next()) {
                productDesc = StringHelper.trim(rset.getString("description"));
            } rset.close(); rset = null;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return productDesc;
    }
    
    public EpcProductDetail getProductDetail(String productCode) {
        
        EpcProductDetail epcProductDetail = new EpcProductDetail();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            conn = fesDataSource.getConnection();
            
            sql = "select product, warehouse, description, model_series from stockm where warehouse like 'A%' and product = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productCode); // product
            rset = pstmt.executeQuery();
            if(rset.next()) {
                epcProductDetail.setProductCode(StringHelper.trim(rset.getString("product")));
                epcProductDetail.setWarehouse(StringHelper.trim(rset.getString("warehouse")));
                epcProductDetail.setProductDesc(StringHelper.trim(rset.getString("description")));
                epcProductDetail.setModelSeries(StringHelper.trim(rset.getString("model_series")));
            } rset.close(); rset = null;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcProductDetail;
    }
    
    public TreeMap<String, String> getSerial(ArrayList<String> productCodeList) {
        TreeMap<String, String> productCodeMap = new TreeMap<String, String>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String sql2 = "";
        int idx = 1;
        
        try {
            conn = fesDataSource.getConnection();
            
            if(productCodeList.size() > 0) {
                sql = "select product, serial_numbers from stockm where warehouse like 'A%' and product in ( ";
                for (String s : productCodeList) {
                    if("".equals(sql2)) {
                        sql2 = "?";
                    } else {
                        sql2 += ",?";
                    }
                }
                sql += sql2 + " ) ";
                
                pstmt = conn.prepareStatement(sql);
                for (String s : productCodeList) {
                    pstmt.setString(idx++, s); // product
                }
                rset = pstmt.executeQuery();
                while(rset.next()) {
                    productCodeMap.put(StringHelper.trim(rset.getString("product")), StringHelper.trim(rset.getString("serial_numbers")));
                } rset.close(); rset = null;
                pstmt.close(); pstmt = null;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return productCodeMap;
    }


    public boolean updateStockStatusForUnlimitedItems(int orderId) {
        boolean isUpdate =false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String itemId = "";
        EpcLogStockStatus epcLogStockStatus = null;
        String newStockStatus = "PF";
        String newStockStatusDesc = "";
        
        try {
            conn = epcDataSource.getConnection();

            newStockStatusDesc = epcStockStatusDescHandler.getEngDescByStatus(conn, newStockStatus);
            
            sql = "update epc_order_item " +
                  "   set stock_status = ?, " +
                  "       stock_status_desc = ?, " +
                  "       pickup_date = to_char(sysdate, 'yyyymmdd') " +
                  " where order_id = ? " +
                  "   and item_cat in (?,?,?,?,?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newStockStatus); // stock_status - PF
            pstmt.setString(2, newStockStatusDesc); // stock_status_desc
            pstmt.setInt(3, orderId); // order_id
            pstmt.setString(4, EpcItemCategory.SIM); // item_cat - SIM
            pstmt.setString(5, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
            pstmt.setString(6, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
            pstmt.setString(7, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
            pstmt.setString(8, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
            
            pstmt.executeUpdate();
            pstmt.close();

            sql = "select item_id " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and item_cat in (?,?,?,?,?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, EpcItemCategory.SIM); // item_cat - SIM
            pstmt.setString(3, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
            pstmt.setString(4, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
            pstmt.setString(5, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
            pstmt.setString(6, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
            rset = pstmt.executeQuery();
            while(rset.next()) {
                itemId = StringHelper.trim(rset.getString("item_id"));

                // create log
                epcLogStockStatus = new EpcLogStockStatus();
                epcLogStockStatus.setOrderId(orderId);
                epcLogStockStatus.setItemId(itemId);
                epcLogStockStatus.setOldStockStatus("");
                epcLogStockStatus.setNewStockStatus("PF");
                epcOrderLogHandler.logStockStatus(conn, epcLogStockStatus);
                // end of create log
            } rset.close();
            pstmt.close();



            // update no ticket item
            sql = "select item_id " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and item_cat in (?) " + 
                  "   and reserve_id is null ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, EpcItemCategory.DEVICE); // item_cat - DEVICE
            rset = pstmt.executeQuery();
            while(rset.next()) {
                itemId = StringHelper.trim(rset.getString("item_id"));

                // create log
                epcLogStockStatus = new EpcLogStockStatus();
                epcLogStockStatus.setOrderId(orderId);
                epcLogStockStatus.setItemId(itemId);
                epcLogStockStatus.setOldStockStatus("");
                epcLogStockStatus.setNewStockStatus("PF");
                epcOrderLogHandler.logStockStatus(conn, epcLogStockStatus);
                // end of create log
            } rset.close();
            pstmt.close();

            sql = "update epc_order_item " +
                  "   set stock_status = ?, " +
                  "       stock_status_desc = ?, " +
                  "       pickup_date = to_char(sysdate, 'yyyymmdd') " +
                  " where order_id = ? " +
                  "   and item_cat in (?) " +
                  "   and reserve_id is null ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, newStockStatus); // stock_status - PF
            pstmt.setString(2, newStockStatusDesc); // stock_status_desc
            pstmt.setInt(3, orderId); // order_id
            pstmt.setString(4, EpcItemCategory.DEVICE); // item_cat - DEVICE
            
            pstmt.executeUpdate();
            pstmt.close();


            isUpdate = true;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
    
    
//    public EpcCreateTmpReserveResult createTmpReserve(String orderReference, String channel, String pickupLocation, String warehouse, String productCode) {
////        String path = EpcProperty.getValue("FES.STOCK.CREATE_TMP_RESERVE");
//        String path = EpcProperty.getValue("FES_STOCK_CREATE_TMP_RESERVE_LINK");
//        URL url = null;
//        InvCreateTmpReserveService invCreateTmpReserveService = null;
//        InvCreateTmpReserve invCreateTmpReserve = null;
//        InvCreateTmpReserveBean invCreateTmpReserveBean = null;
//        InvCreateTmpReserveResultBean invCreateTmpReserveResultBean = null;
//        EpcCreateTmpReserveResult epcCreateTmpReserveResult = new EpcCreateTmpReserveResult();
//        SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
//        SimpleDateFormat sdfYYYYMMDDHHMISS = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); // 05-02-2021 00:00:00
//        String tmpDeliveryDate = "";
//        int reserveMins = getReserveMins(channel, productCode);
//        
//        try {
//            url = new URL(path);
//            invCreateTmpReserveService = new InvCreateTmpReserveService(url);
//            invCreateTmpReserve = invCreateTmpReserveService.getInvCreateTmpReservePort();
//            
//            invCreateTmpReserveBean = new InvCreateTmpReserveBean();
//            invCreateTmpReserveBean.setOrderChannel(channel);
//            invCreateTmpReserveBean.setOrderReference(orderReference);
//            invCreateTmpReserveBean.setPickupStoreCode(pickupLocation);
//            invCreateTmpReserveBean.setProductCode(productCode);
//            invCreateTmpReserveBean.setProductType(warehouse); // AH / AA / AP
//            invCreateTmpReserveBean.setReserveMins(reserveMins);
//            invCreateTmpReserveBean.setReserveQty(1);
//            invCreateTmpReserveBean.setReserveSerialNo("");
//            invCreateTmpReserveBean.setUserID("1");
//            invCreateTmpReserveResultBean = invCreateTmpReserve.createTmpReserve(invCreateTmpReserveBean);
//            
//            if("0".equals("" + invCreateTmpReserveResultBean.getResult())) {
//                // with stock
//                tmpDeliveryDate = StringHelper.trim(invCreateTmpReserveResultBean.getLatestDeliveryDate());
//                if(!"".equals(tmpDeliveryDate)) {
//                    tmpDeliveryDate = sdfYYYYMMDD.format(sdfYYYYMMDDHHMISS.parse(tmpDeliveryDate));
//                }
//
//                epcCreateTmpReserveResult.setResult("SUCCESS");
//                epcCreateTmpReserveResult.setReserveNo(invCreateTmpReserveResultBean.getTmpReserveID() + "");
//                //epcCreateTmpReserveResult.setLatestDeliveryDate(invCreateTmpReserveResultBean.getLatestDeliveryDate());
//                epcCreateTmpReserveResult.setLatestDeliveryDate(tmpDeliveryDate); // YYYYMMDD
//            } else if ("-10".equals("" + invCreateTmpReserveResultBean.getResult())) {
//                // without stock
//                epcCreateTmpReserveResult.setResult("SUCCESS");
//                epcCreateTmpReserveResult.setReserveNo("");
//                epcCreateTmpReserveResult.setLatestDeliveryDate("");
//            } else {
//                // error
//                epcCreateTmpReserveResult.setResult("FAIL");
//                epcCreateTmpReserveResult.setErrMsg(invCreateTmpReserveResultBean.getErrMsg());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            
//            epcCreateTmpReserveResult.setResult("FAIL");
//            epcCreateTmpReserveResult.setErrMsg(e.getMessage());
//        }
//        
//        return epcCreateTmpReserveResult;
//    }


    // use new stock inventory api, kerrytsang, 20220404
    public EpcCreateTmpReserveResult createEpcTmpReserve(String orderReference, String channel, String pickupLocation, String warehouse, String productCode) {
        EpcCreateTmpReserveResult epcCreateTmpReserveResult = new EpcCreateTmpReserveResult();
        SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfYYYYMMDDHHMISS = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); // 15-02-2021 00:00:00
        String tmpDeliveryDate = "";
        int reserveMins = getReserveMins(channel, productCode);
        String iChannel = epcSecurityHelper.encodeForSQL(channel);
        String orderChannel = determineOrderChannel(iChannel);
        String createChannel = determineCreateChannel(iChannel);
        String iOrderReference = epcSecurityHelper.encodeForSQL(orderReference);
        String iPickupLocation = epcSecurityHelper.encodeForSQL(pickupLocation);
        String iWarehouse = epcSecurityHelper.encodeForSQL(warehouse);
        String iProductCode = epcSecurityHelper.encodeForSQL(productCode);
        CreateTmpReserveEpc createTmpReserveEpc = null;
        CreateTmpReserveEpcResponse createTmpReserveEpcResponse = null;
        InvCreateTmpReserveBean invCreateTmpReserveBean = null;
        InvCreateTmpReserveResultBean invCreateTmpReserveResultBean = null;
        String logStr = "[createEpcTmpReserve][orderReference:" + iOrderReference + "]" ;
        String tmpLogStr = "";
        
        try {
            createTmpReserveEpc = new CreateTmpReserveEpc();
            
            invCreateTmpReserveBean = new InvCreateTmpReserveBean();
            invCreateTmpReserveBean.setOrderChannel(orderChannel);
            invCreateTmpReserveBean.setOrderReference(iOrderReference);
            invCreateTmpReserveBean.setPickupStoreCode(iPickupLocation);
            invCreateTmpReserveBean.setProductCode(iProductCode);
            invCreateTmpReserveBean.setProductType(iWarehouse); // AH / AA / AP
            invCreateTmpReserveBean.setReserveMins(reserveMins);
            invCreateTmpReserveBean.setReserveQty(1);
            invCreateTmpReserveBean.setReserveSerialNo("");
            invCreateTmpReserveBean.setUserID("1");
            invCreateTmpReserveBean.setIsEpc(ERP_API_IS_EPC);
            invCreateTmpReserveBean.setApiUserId(ERP_API_USERID);
            invCreateTmpReserveBean.setCreateChannel(createChannel);

            createTmpReserveEpc.setInBean(invCreateTmpReserveBean);

            tmpLogStr = "orderChannel:" + orderChannel + ",pickupLocation:" + iPickupLocation + ",productCode:" + iProductCode + ",reserveMins:" + reserveMins + 
                        ",createChannel:" + createChannel;

logger.info("{}{}", logStr, tmpLogStr);
            createTmpReserveEpcResponse = createTmpReserveSoapClient.reserveTmp(createTmpReserveEpc);
            invCreateTmpReserveResultBean = createTmpReserveEpcResponse.getInvCreateTmpReserveResultBean();

            tmpLogStr = "result:" + invCreateTmpReserveResultBean.getResult() + "," +
                        "errMsg:" + epcSecurityHelper.encodeForSQL(invCreateTmpReserveResultBean.getErrMsg()) + "," +
                        "tmpReserveID:" + invCreateTmpReserveResultBean.getTmpReserveID() + "," +
                        "latestDeliveryDate:" + epcSecurityHelper.encodeForSQL(invCreateTmpReserveResultBean.getLatestDeliveryDate());
logger.info("{}{}", logStr, tmpLogStr);
            
            if("0".equals("" + invCreateTmpReserveResultBean.getResult())) {
                // with stock
                tmpDeliveryDate = StringHelper.trim(invCreateTmpReserveResultBean.getLatestDeliveryDate());
                if(!"".equals(tmpDeliveryDate)) {
                    tmpDeliveryDate = sdfYYYYMMDD.format(sdfYYYYMMDDHHMISS.parse(tmpDeliveryDate));
                }

                epcCreateTmpReserveResult.setResult("SUCCESS");
                epcCreateTmpReserveResult.setReserveNo(invCreateTmpReserveResultBean.getTmpReserveID() + "");
                epcCreateTmpReserveResult.setLatestDeliveryDate(tmpDeliveryDate); // YYYYMMDD
            } else if ("-10".equals("" + invCreateTmpReserveResultBean.getResult())) {
                // without stock
                epcCreateTmpReserveResult.setResult("SUCCESS");
                epcCreateTmpReserveResult.setReserveNo("");
                epcCreateTmpReserveResult.setLatestDeliveryDate("");
            } else {
                // error
                epcCreateTmpReserveResult.setResult("FAIL");
                epcCreateTmpReserveResult.setErrMsg(invCreateTmpReserveResultBean.getErrMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcCreateTmpReserveResult.setResult("FAIL");
            epcCreateTmpReserveResult.setErrMsg(e.getMessage());
        }
        
        return epcCreateTmpReserveResult;
    }


    // renew with input order channel, kerrytsang, 20230808
    public EpcCreateTmpReserveResult createEpcTmpReserve(String orderReference, String loginChannel, String erpOrderChannel, String pickupLocation, String warehouse, String productCode) {
        EpcCreateTmpReserveResult epcCreateTmpReserveResult = new EpcCreateTmpReserveResult();
        SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfYYYYMMDDHHMISS = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); // 15-02-2021 00:00:00
        String tmpDeliveryDate = "";
        int reserveMins = getReserveMins(loginChannel, productCode);
        String iChannel = epcSecurityHelper.encodeForSQL(loginChannel);
        String createChannel = determineCreateChannel(iChannel);
        String iOrderReference = epcSecurityHelper.encodeForSQL(orderReference);
        String iPickupLocation = epcSecurityHelper.encodeForSQL(pickupLocation);
        String iWarehouse = epcSecurityHelper.encodeForSQL(warehouse);
        String iProductCode = epcSecurityHelper.encodeForSQL(productCode);
        CreateTmpReserveEpc createTmpReserveEpc = null;
        CreateTmpReserveEpcResponse createTmpReserveEpcResponse = null;
        InvCreateTmpReserveBean invCreateTmpReserveBean = null;
        InvCreateTmpReserveResultBean invCreateTmpReserveResultBean = null;
        String logStr = "[createEpcTmpReserve][orderReference:" + iOrderReference + "]" ;
        String tmpLogStr = "";
        
        try {
            createTmpReserveEpc = new CreateTmpReserveEpc();
            
            invCreateTmpReserveBean = new InvCreateTmpReserveBean();
            invCreateTmpReserveBean.setOrderChannel(erpOrderChannel);
            invCreateTmpReserveBean.setOrderReference(iOrderReference);
            invCreateTmpReserveBean.setPickupStoreCode(iPickupLocation);
            invCreateTmpReserveBean.setProductCode(iProductCode);
            invCreateTmpReserveBean.setProductType(iWarehouse); // AH / AA / AP
            invCreateTmpReserveBean.setReserveMins(reserveMins);
            invCreateTmpReserveBean.setReserveQty(1);
            invCreateTmpReserveBean.setReserveSerialNo("");
            invCreateTmpReserveBean.setUserID("1");
            invCreateTmpReserveBean.setIsEpc(ERP_API_IS_EPC);
            invCreateTmpReserveBean.setApiUserId(ERP_API_USERID);
            invCreateTmpReserveBean.setCreateChannel(createChannel);

            createTmpReserveEpc.setInBean(invCreateTmpReserveBean);

            tmpLogStr = "loginChannel," + loginChannel + ",erpOrderChannel:" + erpOrderChannel + ",pickupLocation:" + iPickupLocation + ",productCode:" + iProductCode + ",reserveMins:" + reserveMins + 
                        ",createChannel:" + createChannel;

logger.info("{}{}", logStr, tmpLogStr);
            createTmpReserveEpcResponse = createTmpReserveSoapClient.reserveTmp(createTmpReserveEpc);
            invCreateTmpReserveResultBean = createTmpReserveEpcResponse.getInvCreateTmpReserveResultBean();

            tmpLogStr = "result:" + invCreateTmpReserveResultBean.getResult() + "," +
                        "errMsg:" + epcSecurityHelper.encodeForSQL(invCreateTmpReserveResultBean.getErrMsg()) + "," +
                        "tmpReserveID:" + invCreateTmpReserveResultBean.getTmpReserveID() + "," +
                        "latestDeliveryDate:" + epcSecurityHelper.encodeForSQL(invCreateTmpReserveResultBean.getLatestDeliveryDate());
logger.info("{}{}", logStr, tmpLogStr);
            
            if("0".equals("" + invCreateTmpReserveResultBean.getResult())) {
                // with stock
                tmpDeliveryDate = StringHelper.trim(invCreateTmpReserveResultBean.getLatestDeliveryDate());
                if(!"".equals(tmpDeliveryDate)) {
                    tmpDeliveryDate = sdfYYYYMMDD.format(sdfYYYYMMDDHHMISS.parse(tmpDeliveryDate));
                }

                epcCreateTmpReserveResult.setResult("SUCCESS");
                epcCreateTmpReserveResult.setReserveNo(invCreateTmpReserveResultBean.getTmpReserveID() + "");
                epcCreateTmpReserveResult.setLatestDeliveryDate(tmpDeliveryDate); // YYYYMMDD
            } else if ("-10".equals("" + invCreateTmpReserveResultBean.getResult())) {
                // without stock
                epcCreateTmpReserveResult.setResult("SUCCESS");
                epcCreateTmpReserveResult.setReserveNo("");
                epcCreateTmpReserveResult.setLatestDeliveryDate("");
            } else {
                // error
                epcCreateTmpReserveResult.setResult("FAIL");
                epcCreateTmpReserveResult.setErrMsg(invCreateTmpReserveResultBean.getErrMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcCreateTmpReserveResult.setResult("FAIL");
            epcCreateTmpReserveResult.setErrMsg(e.getMessage());
        }
        
        return epcCreateTmpReserveResult;
    }
    
    
    // normal place order -> itemList = null
    // doa -> itemList contains doa item(s)
    public EpcConfirmStockReserveResult confirmTmpReserve(String custId, int orderId, String createUser, String createSalesman, ArrayList<EpcOrderItem> itemList) {
        EpcConfirmStockReserveResult epcConfirmStockReserveResult = new EpcConfirmStockReserveResult();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("FES_STOCK_CONVERT_TMP_TO_REAL_LINK");
        EpcTmpReserve epcTmpReserve = null;
        EpcNoStockReserve epcNoStockReserve = null;
        int smcOrderId = orderId;
        String smcOrderReference = "";
//        TreeMap<String, String> attrMap = null;
        String itemId = "";
        String reserveId = "";
        String pickupDate = "";
        String pickupStore = "";
        EpcDeliveryInfo epcDeliveryInfo = null;
        EpcOrderContact epcOrderContact = null;
        EpcConfirmErpReserveResult epcConfirmErpReserveResult = null;
        ArrayList<EpcOrderItemInfo> epcOrderItemInfoList = new ArrayList<EpcOrderItemInfo>();
        boolean isUpdate = false;
        String custName = "";
        String subrNum = "";
        String lang = "";
        String logStr = "[confirmTmpReserve][orderId:" + orderId + "] ";
        String tmpLogStr = "";
        
        
        try {
            smcOrderReference = epcOrderHandler.isOrderBelongCust(custId, smcOrderId);
            epcDeliveryInfo = epcGetDeliveryInfoHandler.getPickupLocation(smcOrderId);
            
            epcOrderContact = epcContactInfoHandler.getContactInfo(custId, smcOrderId, "N"); // use unmasked info
            
            custName = "";
            if("".equals(custName)) {
                custName = "Customer";
            }
            
            subrNum = epcOrderContact.getContactNo();
            if("".equals(subrNum)) {
                subrNum = "00000000";
            }
            
            lang = epcOrderContact.getOrderLang();
            if("".equals(lang)) {
                lang = "E";
            }


            // get item(s) needed to confirm reserve ticket first
            if(itemList == null || itemList.size() == 0) {
                epcOrderItemInfoList = epcOrderHandler.getOrderItemsForReservation(smcOrderId);
            } else {
                epcOrderItemInfoList = epcOrderHandler.getDoaItemsForReservation(smcOrderId, itemList);
            }
            // end of get item(s) needed to confirm reserve ticket first
            
            
            // loop thru DEVICE item(s)
            for(EpcOrderItemInfo e : epcOrderItemInfoList) {
                itemId = epcSecurityHelper.encodeForSQL(e.getItemId());
                reserveId = epcSecurityHelper.encodeForSQL(e.getReserveId());
                pickupDate = epcSecurityHelper.encodeForSQL(e.getPickupDate());
                pickupStore = ""; // reset per item
                for(EpcDeliveryDetail d : epcDeliveryInfo.getDetails()) {
                    for(String iItemId : d.getItems()) {
                        if(iItemId.equals(e.getItemId())) {
                            pickupStore = epcSecurityHelper.encodeForSQL(d.getPickupStore());
                            break;
                        }
                    }
                }
                if("".equals(pickupStore)) {
logger.info(logStr + "pickup location not found for itemId:" + itemId);
                    throw new Exception("pickup location not found for itemId:" + itemId);
                }
logger.info(logStr + "itemId:" + itemId + ", reserveId:" + reserveId + ", pickupDate:" + pickupDate + ",pickupStore:" + pickupStore);
                
                if("NO_STOCK".equals(reserveId)) {
                    // generate reservation ticket (without stock)
                    epcNoStockReserve = new EpcNoStockReserve();
                    epcNoStockReserve.setCustName(custName);
                    epcNoStockReserve.setChannel("NW"); // set as online store order
                    epcNoStockReserve.setReferKey(smcOrderId + "@" + itemId); // smc order id + "@" + item id
                    epcNoStockReserve.setPickupDate(pickupDate);
                    epcNoStockReserve.setSubrNum(subrNum);
                    epcNoStockReserve.setPickupLoc(pickupStore);
                    epcNoStockReserve.setProductType(e.getWarehouse());
                    epcNoStockReserve.setProductCode(e.getItemCode());
                    epcNoStockReserve.setCommLang(lang);
                    epcNoStockReserve.setPriority("G");
                    epcNoStockReserve.setCreateUser(createUser);
                    epcNoStockReserve.setCreateSalesman(createSalesman);
                    epcNoStockReserve.setCreateChannel("W"); // set as online channel
                    epcNoStockReserve.setNotifyNeed("Y");
                    epcNoStockReserve.setNeedDeposit("N");
                    epcNoStockReserve.setOnlineReferenceNo(smcOrderReference);
                    
                    apiUrl = EpcProperty.getValue("FES_STOCK_CREATE_RESERVE_LINK");
                    responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcNoStockReserve>(epcNoStockReserve), String.class);
                    if(responseEntity.getStatusCodeValue() == 200) {
                        // good case
                        epcConfirmErpReserveResult = objectMapper.readValue(responseEntity.getBody(), EpcConfirmErpReserveResult.class);
                        
                        if(epcConfirmErpReserveResult.getResult() == 0) {
                            // good case
                            e.setReserveId(epcConfirmErpReserveResult.getCaseID() + "");
                            e.setStatus(epcConfirmErpReserveResult.getStatus()); // stock status
                            
                            tmpLogStr = "create reserve ticket (no stock) successfully, itemId:" + itemId + ", new reserveId:" + epcConfirmErpReserveResult.getCaseID();
logger.info("{}{}", logStr, tmpLogStr);
                        } else {
                            // error
                            tmpLogStr = "create reserve ticket (no stock) with ERROR, itemId:" + itemId + ", result:" + epcConfirmErpReserveResult.getResult() + ",errMsg:" + epcSecurityHelper.encodeForSQL(epcConfirmErpReserveResult.getErrorMessage());
logger.info("{}{}", logStr, tmpLogStr);
                            throw new Exception(tmpLogStr);
                        }
                    } else {
                        // error
                        tmpLogStr = "create reserve ticket (no stock) with ERROR, itemId:" + itemId + ", http code:" + responseEntity.getStatusCodeValue();
logger.info("{}{}", logStr, tmpLogStr);
                        throw new Exception(tmpLogStr);
                    }
                } else {
                    // confirm tmp ticket to real ticket (with stock)
                    epcTmpReserve = new EpcTmpReserve();
                    epcTmpReserve.setCustName(custName); // no cust name
                    epcTmpReserve.setChannel("NW"); // set as online store order
                    epcTmpReserve.setReferKey(smcOrderId + "@" + itemId); // smc order id + "@" + item id
                    epcTmpReserve.setPickupDate(pickupDate);
                    epcTmpReserve.setSubrNum(subrNum);
                    epcTmpReserve.setPickupLoc(pickupStore);
                    epcTmpReserve.setProductType(e.getWarehouse());
                    epcTmpReserve.setProductCode(e.getItemCode());
                    epcTmpReserve.setCommLang(lang);
                    epcTmpReserve.setCreateUser(createUser);
                    epcTmpReserve.setCreateSalesman(createSalesman);
                    epcTmpReserve.setCreateChannel("W"); // set as online channel
                    epcTmpReserve.setNotifyNeed("Y");
                    epcTmpReserve.setTmpReserveId(reserveId);
                    epcTmpReserve.setOnlineReferenceNo(smcOrderReference);
                    
                    apiUrl = EpcProperty.getValue("FES_STOCK_CONVERT_TMP_TO_REAL_LINK");
                    responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcTmpReserve>(epcTmpReserve), String.class);
                    if(responseEntity.getStatusCodeValue() == 200) {
                        // good case
                        epcConfirmErpReserveResult = objectMapper.readValue(responseEntity.getBody(), EpcConfirmErpReserveResult.class);
                        
                        if(epcConfirmErpReserveResult.getResult() == 0) {
                            // good case
                            e.setReserveId(reserveId + ""); // when doa, use this line to replace new reserve id into epc_order_item
                            e.setStatus(epcConfirmErpReserveResult.getStatus()); // stock status
                            
                            tmpLogStr = "confirm reserve ticket (with stock) successfully, itemId:" + itemId + ", result:" + epcConfirmErpReserveResult.getResult();
logger.info("{}{}", logStr, tmpLogStr);
                        } else {
                            // error
                            tmpLogStr = "confirm reserve ticket (with stock) with ERROR, itemId:" + itemId + ", result:" + epcConfirmErpReserveResult.getResult() + ",errMsg:" + epcSecurityHelper.encodeForSQL(epcConfirmErpReserveResult.getErrorMessage());
logger.info("{}{}", logStr, tmpLogStr);
                            throw new Exception(tmpLogStr);
                        }
                    } else {
                        // error
                        tmpLogStr = "confirm reserve ticket (with stock) with ERROR, itemId:" + itemId + ", http code:" + responseEntity.getStatusCodeValue();
logger.info("{}{}", logStr, tmpLogStr);
                        throw new Exception(tmpLogStr);
                    }
                }
            }
            // end of loop thru DEVICE item(s)
            
            
            //  update new reserve id to epc_order_item
            isUpdate = epcOrderHandler.updateReserveIdBackToOrder(smcOrderId, epcOrderItemInfoList);
logger.info(logStr + "update reserve id back to epc tables:" + isUpdate);
            //  end of update new reserve id to epc_order_item


            epcConfirmStockReserveResult.setResult("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            
            epcConfirmStockReserveResult.setResult("FAIL");
            epcConfirmStockReserveResult.setErrMsg(e.getMessage());
        } finally {
        }
        
        return epcConfirmStockReserveResult;
    }


    // normal place order -> itemList = null
    // doa -> itemList contains doa item(s)
    public EpcConfirmStockReserveResult confirmEpcTmpReserve(
        String custId, int orderId, String user, String salesman, String channel, 
        String location, ArrayList<EpcOrderItem> itemList, boolean isDoa
    ) {
        String iCustId = epcSecurityHelper.encodeForSQL(custId);
        String iChannel = epcSecurityHelper.encodeForSQL(channel);
        FesUser fesUser = null;
        String iUser = epcSecurityHelper.encodeForSQL(user);
        int iUserid = 0;
        if(!"".equals(iUser)) {
            fesUser = fesUserHandler.getUserByUsername(iUser);
            iUserid = fesUser.getUserid();
        }
        String iSalesman = epcSecurityHelper.encodeForSQL(salesman);
        int iSalesmanid = 0;
        if(!"".equals(iSalesman)) {
            fesUser = fesUserHandler.getUserByUsername(iSalesman);
            iSalesmanid = fesUser.getUserid();
        }
        String createChannel = determineCreateChannel(iChannel);
        String iLocation = epcSecurityHelper.encodeForSQL(location);
        EpcConfirmStockReserveResult epcConfirmStockReserveResult = new EpcConfirmStockReserveResult();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("FES_STOCK_CONFIRM_LINK");
        EpcConfirmReserveItem epcConfirmReserveItem = null;
        ArrayList<EpcConfirmReserveItem> epcConfirmReserveItemList = new ArrayList<>();
        int smcOrderId = orderId;
        String smcOrderReference = "";
        String itemId = "";
        String reserveId = "";
        String pickupDate = "";
        String pickupStore = "";
        String reserveType = "";
        EpcDeliveryInfo epcDeliveryInfo = null;
        EpcOrderContact epcOrderContact = null;
        EpcConfirmErpReserveResult2 epcConfirmErpReserveResult2 = null;
        ArrayList<EpcOrderItemInfo> epcOrderItemInfoList = new ArrayList<EpcOrderItemInfo>();
        boolean isUpdate = false;
        String custName = "";
        String subrNum = "";
        String lang = "";
        String logStr = "[confirmEpcTmpReserve][orderId:" + orderId + "] ";
        String tmpLogStr = "";
        StringBuilder errMsgSb = new StringBuilder();
        String orderChannel = "";//determineOrderChannelForConfirm(iChannel, isDoa);
        
        
        try {
logger.info("{}{}{}", logStr, "isDoa:", isDoa);

            smcOrderReference = epcOrderHandler.isOrderBelongCust(custId, smcOrderId);
            epcDeliveryInfo = epcGetDeliveryInfoHandler.getPickupLocation(smcOrderId);
            epcOrderContact = epcContactInfoHandler.getContactInfo(custId, smcOrderId, "N"); // use unmasked info
            
            custName = "";
            if("".equals(custName)) {
                custName = "Customer";
            }
            
            subrNum = epcOrderContact.getContactNo();
            if("".equals(subrNum)) {
                subrNum = "00000000";
            }
            
            lang = epcOrderContact.getOrderLang();
            if("".equals(lang)) {
                lang = "E";
            }


            // get item(s) needed to confirm reserve ticket first
            if(itemList == null || itemList.size() == 0) {
                epcOrderItemInfoList = epcOrderHandler.getOrderItemsForReservation(smcOrderId);
            } else {
                epcOrderItemInfoList = epcOrderHandler.getDoaItemsForReservation(smcOrderId, itemList);
            }
            // end of get item(s) needed to confirm reserve ticket first
            
            
            // loop thru DEVICE item(s)
            for(EpcOrderItemInfo e : epcOrderItemInfoList) {
                itemId = epcSecurityHelper.encodeForSQL(e.getItemId());
                reserveId = epcSecurityHelper.encodeForSQL(e.getReserveId());
                pickupDate = epcSecurityHelper.encodeForSQL(e.getPickupDate());
                reserveType = epcSecurityHelper.encodeForSQL(e.getReserveType());
                orderChannel = determineOrderChannelForConfirm(iChannel, isDoa, reserveType);

                pickupStore = ""; // reset per item
                if(isDoa) {
                    // doa should use login location
                    //  not refer from the delivery info
                    pickupStore = iLocation;
                } else {
                    for(EpcDeliveryDetail d : epcDeliveryInfo.getDetails()) {
                        for(String iItemId : d.getItems()) {
                            if(iItemId.equals(e.getItemId())) {
                                pickupStore = epcSecurityHelper.encodeForSQL(d.getPickupStore());
                                break;
                            }
                        }
                    }
                }

                if("".equals(pickupStore)) {
logger.info("{}{}{}", logStr, "pickup location not found for itemId:", itemId);
                    throw new Exception("pickup location not found for itemId:" + itemId);
                }

                tmpLogStr = "itemId:" + itemId + ",reserveId:" + reserveId + ",pickupDate:" + pickupDate + ",pickupStore:" + pickupStore + ",orderChannel:" + orderChannel;
logger.info("{}{}", logStr, tmpLogStr);

                if("".equals(reserveId)) {
                    tmpLogStr = " skip due to no tmp ticket ";
logger.info("{}{}", logStr, tmpLogStr);

                    continue;
                }


                epcConfirmReserveItem = new EpcConfirmReserveItem();
                epcConfirmReserveItemList.add(epcConfirmReserveItem);
                
                if("NO_STOCK".equals(reserveId)) {
                    // generate reservation ticket (without stock)
                    epcConfirmReserveItem.setCustName(custName);
                    epcConfirmReserveItem.setChannel(orderChannel);
                    epcConfirmReserveItem.setReferKey(smcOrderId + "@" + itemId); // smc order id + "@" + item id
                    epcConfirmReserveItem.setTmpReserveId("");
                    epcConfirmReserveItem.setPickupDate("");
                    epcConfirmReserveItem.setSubrNum(subrNum);
                    epcConfirmReserveItem.setPickupLoc(pickupStore);
                    epcConfirmReserveItem.setProductType(e.getWarehouse());
                    epcConfirmReserveItem.setProductCode(e.getItemCode());
                    epcConfirmReserveItem.setCommLang(lang);
                    if(isDoa || "VIP".equals(reserveType)) {
                        epcConfirmReserveItem.setPriority("H"); // High
                    } else {
                        epcConfirmReserveItem.setPriority("G"); // General
                    }
                    epcConfirmReserveItem.setCreateUser("" + iUserid);
                    epcConfirmReserveItem.setCreateSalesman("" + iSalesmanid);
                    epcConfirmReserveItem.setCreateChannel(createChannel);
                    epcConfirmReserveItem.setNotifyNeed("N");
                    epcConfirmReserveItem.setNeedDeposit("N");
                    epcConfirmReserveItem.setWithStock("N");
                    epcConfirmReserveItem.setItemReferenceNo(itemId);
                    epcConfirmReserveItem.setEpcItemId(itemId);
                    epcConfirmReserveItem.setOnlineReferenceNo(smcOrderReference);
                } else {
                    // confirm tmp ticket to real ticket (with stock)
                    epcConfirmReserveItem.setCustName(custName);
                    epcConfirmReserveItem.setChannel(orderChannel);
                    epcConfirmReserveItem.setReferKey(smcOrderId + "@" + itemId); // smc order id + "@" + item id
                    epcConfirmReserveItem.setTmpReserveId(reserveId);
                    epcConfirmReserveItem.setPickupDate(pickupDate);
                    epcConfirmReserveItem.setSubrNum(subrNum);
                    epcConfirmReserveItem.setPickupLoc(pickupStore);
                    epcConfirmReserveItem.setProductType(e.getWarehouse());
                    epcConfirmReserveItem.setProductCode(e.getItemCode());
                    epcConfirmReserveItem.setCommLang(lang);
                    if(isDoa || "VIP".equals(reserveType)) {
                        epcConfirmReserveItem.setPriority("H"); // High
                    } else {
                        epcConfirmReserveItem.setPriority("G"); // General
                    }
                    epcConfirmReserveItem.setCreateUser("" + iUserid);
                    epcConfirmReserveItem.setCreateSalesman("" + iSalesmanid);
                    epcConfirmReserveItem.setCreateChannel(createChannel);
                    epcConfirmReserveItem.setNotifyNeed("N");
                    epcConfirmReserveItem.setNeedDeposit("N");
                    epcConfirmReserveItem.setWithStock("Y");
                    epcConfirmReserveItem.setItemReferenceNo(itemId);
                    epcConfirmReserveItem.setEpcItemId(itemId);
                    epcConfirmReserveItem.setOnlineReferenceNo(smcOrderReference);
                }

                tmpLogStr = " add " + epcConfirmReserveItem.toString();
logger.info("{}{}", logStr, tmpLogStr);
            }
            // end of loop thru DEVICE item(s)


            // invoke erp api
            tmpLogStr = objectMapper.writeValueAsString(epcConfirmReserveItemList);
logger.info("{}{}{}", logStr, "request json:", tmpLogStr);
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcConfirmReserveItemList), String.class);
            epcConfirmErpReserveResult2 = objectMapper.readValue(responseEntity.getBody(), EpcConfirmErpReserveResult2.class);

            tmpLogStr = objectMapper.writeValueAsString(epcConfirmErpReserveResult2);
logger.info("{}{}{}", logStr, "return json:", tmpLogStr);

            if("0".equals(epcConfirmErpReserveResult2.getReturnCode())) {
                // map back reserve id, stock status to item list
                for(EpcOrderItemInfo e : epcOrderItemInfoList) {
                    for(EpcConfirmReserveItemReturn r : epcConfirmErpReserveResult2.getOrders()) {
                        if(e.getItemId().equals(r.getEpcItemId())) {
                            e.setReserveId(r.getTmpReserveId()); // when doa, use this line to replace new reserve id into epc_order_item
                            e.setStatus(r.getStatus()); // stock status
                            break;
                        }
                    }
                }
                // end of map back reserve id, stock status to item list

                // get stock status desc, kerrytsang, 20230202
                getReserveStatus(epcOrderItemInfoList);
                // end of get stock status desc, kerrytsang, 20230202

                //  update new reserve id to epc_order_item
                isUpdate = epcOrderHandler.updateReserveIdBackToOrder(smcOrderId, epcOrderItemInfoList);
logger.info("{}{}{}", logStr, "update reserve id back to epc tables:", isUpdate);
                //  end of update new reserve id to epc_order_item

                epcConfirmStockReserveResult.setResult("SUCCESS");
            } else {
                for(EpcConfirmReserveItemReturn r : epcConfirmErpReserveResult2.getOrders()) {
                    if(!"0".equals(r.getResultCode())) {
                        errMsgSb.append("[" + r.getEpcItemId() + "/" + r.getErrorMessage() + "]");
                    }
                }

                epcConfirmStockReserveResult.setResult("FAIL");
                epcConfirmStockReserveResult.setErrMsg(errMsgSb.toString());
            }
            // end of invoke erp api
        } catch (Exception e) {
            e.printStackTrace();
            
            epcConfirmStockReserveResult.setResult("FAIL");
            epcConfirmStockReserveResult.setErrMsg(e.getMessage());
        } finally {
        }
        
        return epcConfirmStockReserveResult;
    }


    public EpcRemoveTmpReserve removeTmpReserve(String reserveId) {
        EpcRemoveTmpReserve epcRemoveTmpReserve = new EpcRemoveTmpReserve();
        CancelTmpReserve cancelTmpReserve = null;
        CancelTmpReserveResponse cancelTmpReserveResponse = null;
        InvCancelTmpReserveBean invCancelTmpReserveBean = null;
        InvCancelTmpReserveResultBean invCancelTmpReserveResultBean = null;


        try {
            cancelTmpReserve = new CancelTmpReserve();

            invCancelTmpReserveBean = new InvCancelTmpReserveBean();
            invCancelTmpReserveBean.setTmpReserveID(Integer.parseInt(reserveId));
            invCancelTmpReserveBean.setOrderChannel("WEB");
            invCancelTmpReserveBean.setCancelReason("");
            invCancelTmpReserveBean.setSerialNo("");
            invCancelTmpReserveBean.setUserID("1");

            cancelTmpReserve.setInBean(invCancelTmpReserveBean);

            cancelTmpReserveResponse = cancelTmpReserveSoapClient.cancelReserveTmp(cancelTmpReserve);
            invCancelTmpReserveResultBean = cancelTmpReserveResponse.getInvCancelTmpReserveResultBean();
            if("0".equals("" + invCancelTmpReserveResultBean.getResult())) {
                // good case
                epcRemoveTmpReserve.setResult("SUCCESS");
            } else {
                // error
                epcRemoveTmpReserve.setResult("FAIL");
                epcRemoveTmpReserve.setErrMsg("result:" + invCancelTmpReserveResultBean.getResult());
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcRemoveTmpReserve.setResult("FAIL");
            epcRemoveTmpReserve.setErrMsg(e.getMessage());
        }
        
        return epcRemoveTmpReserve;
    }
    
    
    public EpcDeductStockResult deductStock(EpcUpdateStock epcUpdateStock) {
        EpcDeductStockResult epcDeductStockResult = new EpcDeductStockResult();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("FES_STOCK_INVOICE_LINK");
        EpcUpdateStockResult epcUpdateStockResult = null;
        EpcUpdateStock epcUpdateStockForLog = null;
        String logStr = "[deductStock]";
        String tmpLogStr = "";
        
        try {
            // for app log
            epcUpdateStockForLog = new EpcUpdateStock();
            epcUpdateStockForLog.setCheckBy(epcUpdateStock.getCheckBy());
            epcUpdateStockForLog.setCreateSalesman(epcUpdateStock.getCreateSalesman());
            epcUpdateStockForLog.setCreateUser(epcUpdateStock.getCreateUser());
            if(StringHelper.trim(epcUpdateStock.getHkidBr()).length() >= 2) {
                epcUpdateStockForLog.setHkidBr(StringHelper.trim(epcUpdateStock.getHkidBr()).substring(0, 1));
            } else {
                epcUpdateStockForLog.setHkidBr(StringHelper.trim(epcUpdateStock.getHkidBr()));
            }
            epcUpdateStockForLog.setImei(epcUpdateStock.getImei());
            epcUpdateStockForLog.setInvoiceNo(epcUpdateStock.getInvoiceNo());
            epcUpdateStockForLog.setMovementComment(epcUpdateStock.getMovementComment());
            epcUpdateStockForLog.setPickupLoc(epcUpdateStock.getPickupLoc());
            epcUpdateStockForLog.setProductCode(epcUpdateStock.getProductCode());
            epcUpdateStockForLog.setQty(epcUpdateStock.getQty());
            epcUpdateStockForLog.setReferNo(epcUpdateStock.getReferNo());
            epcUpdateStockForLog.setReserveId(epcUpdateStock.getReserveId());
            epcUpdateStockForLog.setSubrNum(epcUpdateStock.getSubrNum());
            epcUpdateStockForLog.setWarehouse(epcUpdateStock.getWarehouse());
            // end of app log

            tmpLogStr = "request json:" + objectMapper.writeValueAsString(epcUpdateStockForLog);
logger.info("{}{}", logStr, tmpLogStr);

            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcUpdateStock>(epcUpdateStock), String.class);

            tmpLogStr = "result json:" + objectMapper.writeValueAsString(responseEntity);
logger.info("{}{}", logStr, tmpLogStr);

            if(responseEntity.getStatusCodeValue() == 200) {
                epcUpdateStockResult = objectMapper.readValue(responseEntity.getBody(), EpcUpdateStockResult.class);
                
                if("0".equals(epcUpdateStockResult.getResult())) {
                    // success
                    epcDeductStockResult.setResult("SUCCESS");
                } else {
                    // error
                    epcDeductStockResult.setResult("FAIL");
                    epcDeductStockResult.setErrMsg(epcUpdateStockResult.getErrorMessage());
                }
            } else {
                // error
                epcDeductStockResult.setResult("FAIL");
                epcDeductStockResult.setErrMsg("http status:" + responseEntity.getStatusCodeValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcDeductStockResult.setResult("FAIL");
            epcDeductStockResult.setErrMsg(e.getMessage());
        } finally {
        }
        return epcDeductStockResult;
    }
    
    
    public EpcDoaStockResult doaStock(EpcDoaStock epcDoaStock) {
        EpcDoaStockResult epcDoaStockResult = new EpcDoaStockResult();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("FES_STOCK_DOA_LINK");
        EpcUpdateStockDoaResult epcUpdateStockDoaResult = null;
        String logStr = "[doaStock]";
        String tmpLogStr = "";
        
        try {
            tmpLogStr = "api url:" + apiUrl;
logger.info("{}{}", logStr, tmpLogStr);

            tmpLogStr = "input json:" + new ObjectMapper().writeValueAsString(epcDoaStock);
logger.info("{}{}", logStr, tmpLogStr);

            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcDoaStock>(epcDoaStock), String.class);

            tmpLogStr = "result json:" + responseEntity.getBody();
logger.info("{}{}", logStr, tmpLogStr);

            if(responseEntity.getStatusCodeValue() == 200) {
                epcUpdateStockDoaResult = objectMapper.readValue(responseEntity.getBody(), EpcUpdateStockDoaResult.class);
                
                if(epcUpdateStockDoaResult.getResult() == 0) {
                    // success
                    epcDoaStockResult.setResult("SUCCESS");
                    epcDoaStockResult.setTransferNote(epcUpdateStockDoaResult.getTransferNote());
                } else {
                    // error
                    epcDoaStockResult.setResult("FAIL");
                    epcDoaStockResult.setErrMsg(epcUpdateStockDoaResult.getErrorMessage());
                }
            } else {
                // error
                epcDoaStockResult.setResult("FAIL");
                epcDoaStockResult.setErrMsg("http status:" + responseEntity.getStatusCodeValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcDoaStockResult.setResult("FAIL");
            epcDoaStockResult.setErrMsg(e.getMessage());
        } finally {
        }
        return epcDoaStockResult;
    }
    
    
    public String getProductCodeBySerialNo(String serialNo) {
        String productCode = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        
        try {
            conn = fesDataSource.getConnection();
            
            sql = "select product from stquem where serial_number = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, serialNo); // serialNo
            rset = pstmt.executeQuery();
            if(rset.next()) {
                productCode = StringHelper.trim(rset.getString("product"));
            } rset.close(); rset = null;
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return productCode;
    }
    
    
    public EpcCheckProduct checkProduct(String value) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        EpcCheckProduct epcCheckProduct = new EpcCheckProduct();
        epcCheckProduct.setProductCode("");
        epcCheckProduct.setImeiSim("");
        
        try {
            conn = fesDataSource.getConnection();
            
            // check whether input is an imei / serial number
            sql = "select product from stquem where serial_number = ? and quantity_free > 0 ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, value); // serial_number
            rset = pstmt.executeQuery();
            if(rset.next()) {
                epcCheckProduct.setProductCode(StringHelper.trim(rset.getString("product")));
                epcCheckProduct.setImeiSim(value);
            } else {
                sql = "select product from stockm where warehouse like 'A%' and product = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, value); // product
                rset = pstmt.executeQuery();
                if(rset.next()) {
                    epcCheckProduct.setProductCode(StringHelper.trim(rset.getString("product")));
                    epcCheckProduct.setImeiSim("");
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcCheckProduct;
    }
    
    
    public boolean isSameModelSeries(String productCode, String newProductCode) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        boolean isValid = false;
        int idx = 0;
        String[] modelSeriesArray = new String[2];
        modelSeriesArray[0] = "";
        modelSeriesArray[1] = "";
        
        
        try {
            conn = fesDataSource.getConnection();
            
            sql = "select model_series from stockm where warehouse like 'A%' and product in (?,?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, productCode); // product
            pstmt.setString(2, newProductCode); // product
            rset = pstmt.executeQuery();
            while(rset.next()) {
                modelSeriesArray[idx] = StringHelper.trim(rset.getString("model_series"));
                idx++;
            } rset.close(); rset = null;
            
            if(StringUtils.isNotBlank(modelSeriesArray[0]) && StringUtils.isNotBlank(modelSeriesArray[1]) && 
            		modelSeriesArray[0].equals(modelSeriesArray[1])) {
                // matched, same series
                isValid = true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isValid;
    }
    
    
    public boolean isValidProduct(String productCode, String serialNo) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        boolean isValid = false;
        int cnt = 0;
        
        try {
            conn = fesDataSource.getConnection();
            
            sql = "select count(1) from stquem where serial_number = ? and product = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, serialNo); // serial_number
            pstmt.setString(2, productCode); // product
            rset = pstmt.executeQuery();
            if(rset.next()) {
                cnt = rset.getInt(1);
            } rset.close(); rset = null;
            
            if(cnt > 0) {
                // matched
                isValid = true;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isValid;
    }
    
    
    public void getReserveStatus(ArrayList<EpcOrderItemInfo> itemList) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("FES_STOCK_GET_RESERVE_STATUS_LINK");
        String reserveIds = "";
//        EpcStockReserveStatusResult epcStockReserveStatusResult = null;
//        ArrayList<EpcStockReserveStatus> statusList = null;
        TreeMap<String, EpcStockReserveStatus> statusMap = null;
        EpcStockReserveStatus epcStockReserveStatus = null;
        EpcStockReserveStatus[] statusArray = null;
        String stockStatusFulfill = "PF";
        String stockStatusDescFulfill = epcStockStatusDescHandler.getEngDescByStatus(stockStatusFulfill);
        String logStr = "[getReserveStatus] ";
        String tmpLogStr = "";
        
        try {
            for(EpcOrderItemInfo e : itemList) {
                if(!"".equals(e.getReserveId()) && !"NO_STOCK".equals(e.getReserveId())) {
                    if("".equals(reserveIds)) {
                        reserveIds = "caseIds=" + e.getReserveId();
                    } else {
                        reserveIds += "&caseIds=" + e.getReserveId();
                    }
                }
            }
            
            apiUrl += "?" + reserveIds;
            tmpLogStr = epcSecurityHelper.encodeForSQL("apiUrl:" + apiUrl);
logger.info(logStr + tmpLogStr);

            if("".equals(reserveIds)) {
logger.info(logStr + "NOT need to proceed");
            } else {
                responseEntity = restTemplate.getForEntity(apiUrl, String.class);
                if(responseEntity.getStatusCodeValue() == 200) {
    //                epcStockReserveStatusResult = objectMapper.readValue(responseEntity.getBody(), EpcStockReserveStatusResult.class);
    //                statusList = epcStockReserveStatusResult.getStatusList();
                    
                    tmpLogStr = epcSecurityHelper.encodeForSQL("result:" + responseEntity.getBody());
logger.info(logStr + tmpLogStr);
                    statusArray = objectMapper.readValue(responseEntity.getBody(), EpcStockReserveStatus[].class); 
                    
                    statusMap = new TreeMap<String, EpcStockReserveStatus>();
    //                for(EpcStockReserveStatus e : statusList) {
                    for(EpcStockReserveStatus e : statusArray) {
                        statusMap.put(e.getCaseid(), e);
                    }
                    
                    for(EpcOrderItemInfo e : itemList) {
                        if(!"".equals(e.getReserveId())) {
                            epcStockReserveStatus = statusMap.get(e.getReserveId());
                            if(epcStockReserveStatus != null) {
                                e.setStatus(epcStockReserveStatus.getStatus());
                                e.setStatusDesc(epcStockReserveStatus.getDescription());
                            } else {
                                e.setStatus("N/A");
                                e.setStatusDesc("N/A");
                            }
                        } else {
                            // no reserve ticket
                            e.setStatus(stockStatusFulfill);
                            e.setStatusDesc(stockStatusDescFulfill);
                        }
                    }
                } else {
                    // error
                    throw new Exception("http response status:" + responseEntity.getStatusCodeValue());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }
    
    
//    public EpcExtendTmpReserve extendTmpReserve(String reserveId, int extendMins) {
//        String path = EpcProperty.getValue("FES_STOCK_EXTEND_TMP_RESERVE_LINK");
//        URL url = null;
//        InvExtendTmpReserveService invExtendTmpReserveService = null;
//        InvExtendTmpReserve invExtendTmpReserve = null;
//        InvExtendTmpReserveBean invExtendTmpReserveBean = null;
//        InvExtendTmpReserveResultBean invExtendTmpReserveResultBean = null;
//        EpcExtendTmpReserve epcExtendTmpReserve = new EpcExtendTmpReserve();
//        int mins = 0;
//        
//        try {
//            if(extendMins <= 0) {
//                mins = 20;
//            } else {
//                mins = extendMins;
//            }
//            
//            url = new URL(path);
//            invExtendTmpReserveService = new InvExtendTmpReserveService(url);
//            invExtendTmpReserve = invExtendTmpReserveService.getInvExtendTmpReservePort();
//            
//            invExtendTmpReserveBean = new InvExtendTmpReserveBean();
//            invExtendTmpReserveBean.setTmpReserveID(Integer.parseInt(reserveId));
//            invExtendTmpReserveBean.setExtendReserveMins(mins);
//            invExtendTmpReserveBean.setUserID(1);
//            
//            invExtendTmpReserveResultBean = invExtendTmpReserve.extendTmpReserve(invExtendTmpReserveBean);
//            if(invExtendTmpReserveResultBean.getResult() == 0) {
//                epcExtendTmpReserve.setResult("SUCCESS");
//            } else {
//                epcExtendTmpReserve.setResult("FAIL");
//                if(invExtendTmpReserveResultBean.getResult() == -12) {
//                    epcExtendTmpReserve.setErrMsg("tmp reserve ID not found");
//                } else if(invExtendTmpReserveResultBean.getResult() == -13) {
//                    epcExtendTmpReserve.setErrMsg("tmp reserve is cancelled");
//                } else if(invExtendTmpReserveResultBean.getResult() == -15) {
//                    epcExtendTmpReserve.setErrMsg("tmp reserve is expired");
//                } else {
//                    epcExtendTmpReserve.setErrMsg("error code:" + invExtendTmpReserveResultBean.getResult());
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            
//            epcExtendTmpReserve.setResult("FAIL");
//            epcExtendTmpReserve.setErrMsg(e.getMessage());
//        } finally {
//        }
//        return epcExtendTmpReserve;
//    }


    public EpcExtendTmpReserve extendEpcTmpReserve(String reserveId, int extendMins, String channel) {
        EpcExtendTmpReserve epcExtendTmpReserve = new EpcExtendTmpReserve();
        int mins = 0;
        ExtendTmpReserve extendTmpReserve = null;
        ExtendTmpReserveResponse extendTmpReserveResponse = null;
        InvExtendTmpReserveBean invExtendTmpReserveBean = null;
        InvExtendTmpReserveResultBean invExtendTmpReserveResultBean = null;

        
        try {
            if(extendMins <= 0) {
                mins = 20;
            } else {
                mins = extendMins;
            }
            
            extendTmpReserve = new ExtendTmpReserve();
            
            invExtendTmpReserveBean = new InvExtendTmpReserveBean();
            invExtendTmpReserveBean.setTmpReserveID(Integer.parseInt(reserveId));
            invExtendTmpReserveBean.setExtendReserveMins(mins);
            invExtendTmpReserveBean.setUserID(1);

            extendTmpReserve.setInBean(invExtendTmpReserveBean);

            extendTmpReserveResponse = extendTmpReserveSoapClient.extendReserveTmp(extendTmpReserve);
            invExtendTmpReserveResultBean = extendTmpReserveResponse.getInvExtendTmpReserveResultBean();
            if(invExtendTmpReserveResultBean.getResult() == 0) {
                epcExtendTmpReserve.setResult("SUCCESS");
            } else {
                epcExtendTmpReserve.setResult("FAIL");
                if(invExtendTmpReserveResultBean.getResult() == -12) {
                    epcExtendTmpReserve.setErrMsg("tmp reserve ID not found");
                } else if(invExtendTmpReserveResultBean.getResult() == -13) {
                    epcExtendTmpReserve.setErrMsg("tmp reserve is cancelled");
                } else if(invExtendTmpReserveResultBean.getResult() == -15) {
                    epcExtendTmpReserve.setErrMsg("tmp reserve is expired");
                } else {
                    epcExtendTmpReserve.setErrMsg("error code:" + invExtendTmpReserveResultBean.getResult());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcExtendTmpReserve.setResult("FAIL");
            epcExtendTmpReserve.setErrMsg(e.getMessage());
        } finally {
        }
        return epcExtendTmpReserve;
    }
    
    
//    public EpcUpdateLocationToTmpReserve updateLocationToTmpReserve(String channel, String reserveId, String pickupLocation) {
//        String path = EpcProperty.getValue("FES_STOCK_UPDATE_TMP_LOCATION_LINK");
//        URL url = null;
//        InvUpdateTmpReservePickupLocService invUpdateTmpReservePickupLocService = null;
//        InvUpdateTmpReservePickupLoc invUpdateTmpReservePickupLoc = null;
//        InvUpdateTmpReservePickupLocBean invUpdateTmpReservePickupLocBean = null;
//        InvUpdateTmpReservePickupLocResultBean invUpdateTmpReservePickupLocResultBean = null;
//        SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
//        SimpleDateFormat sdfYYYYMMDDHHMISS = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); // 05-02-2021 00:00:00
//        String tmpDeliveryDate = "";
//        EpcUpdateLocationToTmpReserve epcUpdateLocationToTmpReserve = new EpcUpdateLocationToTmpReserve();
//        
//        try {
//            url = new URL(path);
//            invUpdateTmpReservePickupLocService = new InvUpdateTmpReservePickupLocService(url);
//            invUpdateTmpReservePickupLoc = invUpdateTmpReservePickupLocService.getInvUpdateTmpReservePickupLocPort();
//            
//            invUpdateTmpReservePickupLocBean = new InvUpdateTmpReservePickupLocBean();
//            invUpdateTmpReservePickupLocBean.setOrderChannel(channel);
//            invUpdateTmpReservePickupLocBean.setTmpReserveID(Integer.parseInt(reserveId));
//            invUpdateTmpReservePickupLocBean.setPickupStoreCode(pickupLocation);
//            invUpdateTmpReservePickupLocBean.setUserID(1);
//            
//            invUpdateTmpReservePickupLocResultBean = invUpdateTmpReservePickupLoc.updateTmpReservePickupLoc(invUpdateTmpReservePickupLocBean);
//            if(invUpdateTmpReservePickupLocResultBean.getResult() == 0) {
//                tmpDeliveryDate = StringHelper.trim(invUpdateTmpReservePickupLocResultBean.getLatestDeliveryDate()); // in DD-MM-YYYY HH24:MI:SS
//                if(!"".equals(tmpDeliveryDate)) {
//                    tmpDeliveryDate = sdfYYYYMMDD.format(sdfYYYYMMDDHHMISS.parse(tmpDeliveryDate));
//                }
//                
//                epcUpdateLocationToTmpReserve.setResult("SUCCESS");
//                epcUpdateLocationToTmpReserve.setPickupDate(tmpDeliveryDate); // in YYYYMMDD
//            } else {
//                epcUpdateLocationToTmpReserve.setResult("FAIL");
//                epcUpdateLocationToTmpReserve.setErrMsg(invUpdateTmpReservePickupLocResultBean.getErrMsg());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            
//            epcUpdateLocationToTmpReserve.setResult("FAIL");
//            epcUpdateLocationToTmpReserve.setErrMsg(e.getMessage());
//        } finally {
//        }
//        return epcUpdateLocationToTmpReserve;
//    }


    public EpcUpdateLocationToTmpReserve updateLocationToEpcTmpReserve(String loginChannel, String reserveId, String pickupLocation, String createChannel) {
        SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdfYYYYMMDDHHMISS = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss"); // 05-02-2021 00:00:00
        String tmpDeliveryDate = "";
        EpcUpdateLocationToTmpReserve epcUpdateLocationToTmpReserve = new EpcUpdateLocationToTmpReserve();
        String iLoginChannel = epcSecurityHelper.encodeForSQL(loginChannel);
        String orderChannel = determineOrderChannel(iLoginChannel);
        String iCreateChannel = epcSecurityHelper.encodeForSQL(createChannel);
        UpdateTmpReservePickupLocEpc updateTmpReservePickupLocEpc = null;
        UpdateTmpReservePickupLocEpcResponse updateTmpReservePickupLocEpcResponse = null;
        InvUpdateTmpReservePickupLocBean invUpdateTmpReservePickupLocBean = null;
        InvUpdateTmpReservePickupLocResultBean invUpdateTmpReservePickupLocResultBean = null;
        
        try {
            updateTmpReservePickupLocEpc = new UpdateTmpReservePickupLocEpc();
            
            invUpdateTmpReservePickupLocBean = new InvUpdateTmpReservePickupLocBean();
            invUpdateTmpReservePickupLocBean.setOrderChannel(orderChannel);
            invUpdateTmpReservePickupLocBean.setTmpReserveID(Integer.parseInt(reserveId));
            invUpdateTmpReservePickupLocBean.setPickupStoreCode(pickupLocation);
            invUpdateTmpReservePickupLocBean.setUserID(1);
            invUpdateTmpReservePickupLocBean.setApiUserId(ERP_API_USERID);
            invUpdateTmpReservePickupLocBean.setCreateChannel(iCreateChannel);

            updateTmpReservePickupLocEpc.setInBean(invUpdateTmpReservePickupLocBean);
            
            updateTmpReservePickupLocEpcResponse = updateTmpReservePickupLocationSoapClient.updateTmpReservePickupLocation(updateTmpReservePickupLocEpc);
            invUpdateTmpReservePickupLocResultBean = updateTmpReservePickupLocEpcResponse.getInvUpdateTmpReservePickupLocResultBean();
            if(invUpdateTmpReservePickupLocResultBean.getResult() == 0) {
                tmpDeliveryDate = StringHelper.trim(invUpdateTmpReservePickupLocResultBean.getLatestDeliveryDate()); // in DD-MM-YYYY HH24:MI:SS
                if(!"".equals(tmpDeliveryDate)) {
                    tmpDeliveryDate = sdfYYYYMMDD.format(sdfYYYYMMDDHHMISS.parse(tmpDeliveryDate));
                }
                
                epcUpdateLocationToTmpReserve.setResult("SUCCESS");
                epcUpdateLocationToTmpReserve.setPickupDate(tmpDeliveryDate); // in YYYYMMDD
            } else {
                epcUpdateLocationToTmpReserve.setResult("FAIL");
                epcUpdateLocationToTmpReserve.setErrMsg(invUpdateTmpReservePickupLocResultBean.getErrMsg());
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcUpdateLocationToTmpReserve.setResult("FAIL");
            epcUpdateLocationToTmpReserve.setErrMsg(e.getMessage());
        } finally {
        }
        return epcUpdateLocationToTmpReserve;
    }


    public String determineOrderChannel(String loginChannel) {
        String orderChannel = "WEB";
//        if(EpcLoginChannel.STORE.equals(loginChannel)) {
//            orderChannel = "STORE";
//        } else {
//            // other channels
//            orderChannel = "WEB";
//        }
        orderChannel = "WEB";
        return orderChannel;
    }


    public String determineOrderChannelForConfirm(String loginChannel, boolean isDoa, String reserveType) {
        String orderChannel = "";

        if(isDoa) {
            orderChannel = "D";
        } else if("VIP".equals(reserveType)) {
            if(EpcLoginChannel.STORE.equals(loginChannel)) {
                orderChannel = "VS";
            } else if (EpcLoginChannel.DS.equals(loginChannel)) {
                orderChannel = "VD";
            } else {
                // all hotline
                orderChannel = "VC";
            }
        } else if(EpcLoginChannel.ONLINE.equals(loginChannel)) {
            // online
            orderChannel = "NW";
        } else if(EpcLoginChannel.STORE.equals(loginChannel)) {
            // store, normal sales
            orderChannel = "NS";
        } else if(EpcLoginChannel.DS.equals(loginChannel)) {
            // ds, normal sales
            orderChannel = "ND";
        } else {
            // all hotline, normal sales
            orderChannel = "NC";
        }
        
        return orderChannel;
    }


    public String determineCreateChannel(String loginChannel) {
        String createChannel = "S";
        if(EpcLoginChannel.ONLINE.equals(loginChannel)) {
            createChannel = "W";
        } else if (EpcLoginChannel.TS.equals(loginChannel)) {
            createChannel = "C";
        } else if (EpcLoginChannel.PR.equals(loginChannel)) {
            createChannel = "C";
        } else if (EpcLoginChannel.CS.equals(loginChannel)) {
            createChannel = "C";
        } else if (EpcLoginChannel.DS.equals(loginChannel)) {
            createChannel = "D";
        } else {
            // default, set as store
            createChannel = "S";
        }
        return createChannel;
    }


    /**
     * product_code + channel => higher priority thand just channel
     * 
     * @param channel
     * @param productCode
     * @return
     */
    public int getReserveMins(String channel, String productCode) {
        int mins = 20; // default
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String iChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(channel));
        String iProductCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(productCode));

        try {
            conn = epcDataSource.getConnection();
            sql = "select case " +
                  "         when product_code is not null then 1 " +
                  "         else 0 " +
                  "       end, " +
                  "       reserve_mins " +
                  "  from epc_stock_reserve_ctrl " +
                  " where channel = ? " +
                  "   and status = ? " +
                  "   and product_code = ? " +
                  "union " +
                  "select case " +
                  "         when product_code is not null then 1 " +
                  "         else 0 " +
                  "       end, " +
                  "       reserve_mins " +
                  "  from epc_stock_reserve_ctrl " +
                  " where channel = ? " +
                  "   and status = ? " +
                  "   and product_code is null " +
                  " order by 1 desc ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, iChannel); // channel
            pstmt.setString(2, "A"); // status
            pstmt.setString(3, iProductCode); // product_code
            pstmt.setString(4, iChannel); // channel
            pstmt.setString(5, "A"); // status
            rset = pstmt.executeQuery();
            if(rset.next()) {
                mins = rset.getInt("reserve_mins");
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return mins;
    }
    
//    /**
//     * To update stock through ERP API
//     * @param epcUpdateStock
//     * @return
//     * @throws Exception 
//     */
//    public ERPStockUpdateResult updateERPStock(ERPStockUpdate erpStockUpdate) throws Exception  {
//    	ERPStockUpdateResult result = new ERPStockUpdateResult();
//        
//        try {
//        	String apiUrl = EpcProperty.getValue("FES_STOCK_STOCK_UPDATE_LINK");
//        	ApiHelper apiHelper=new ApiHelper();
//        	RestTemplate restTemplate = apiHelper.getRestTemplate();
//        	//RestTemplate restTemplate = new RestTemplate();
//            ObjectMapper objectMapper = new ObjectMapper();
//            
//            ResponseEntity<String> response = restTemplate.postForEntity(apiUrl, new HttpEntity<ERPStockUpdate>(erpStockUpdate), String.class);
//            result=objectMapper.readValue(response.getBody(), ERPStockUpdateResult.class);
//            if(response.getStatusCode() != HttpStatus.OK || !result.getResult().equals(ERPStock.RESULT_SUCCESS.intVal)) {
//            	throw new Exception("HTTP Error: "+response.getStatusCodeValue()+". ERP API response result: ["+result.getResult()+"]"+result.getErrorMessage());
//            }
//            
//        } catch (Exception e) {
//            logger.error("",e);
//            throw new Exception(e.getMessage());
//        }
//        
//        return result;
//    }


    public boolean cancelRealTicket(int orderId, String reserveId, int userid, int salesmanId) {
        String apiUrl = EpcProperty.getValue("FES_STOCK_CANCEL_REAL_TICKET_LINK");
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        EpcCancelRealTicket epcCancelRealTicket = null;
        EpcCancelRealTicketResult epcCancelRealTicketResult = null;
        ArrayList<EpcCancelRealTicketDetail> reserveIdList = new ArrayList<>();
        EpcCancelRealTicketDetail epcCancelRealTicketDetail = null;
        String iReserveId = epcSecurityHelper.encodeForSQL(StringHelper.trim(reserveId));
        String logStr = "[cancelRealTicket][orderId:" + orderId + "][reserveId:" + iReserveId + "] ";
        String tmpLogStr = "";
        boolean isCancel = false;
        
        try {
            epcCancelRealTicketDetail = new EpcCancelRealTicketDetail();
            epcCancelRealTicketDetail.setOrderId(Integer.parseInt(reserveId));

            epcCancelRealTicket = new EpcCancelRealTicket();
            epcCancelRealTicket.setModifyFesUserId(userid);
            epcCancelRealTicket.setModifySalesmanId(salesmanId);
            reserveIdList.add(epcCancelRealTicketDetail);
            epcCancelRealTicket.setOrdersList(reserveIdList);
            
            tmpLogStr = "request json:" + objectMapper.writeValueAsString(epcCancelRealTicket);
logger.info("{}{}", logStr, tmpLogStr);
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcCancelRealTicket), String.class);

            epcCancelRealTicketResult = objectMapper.readValue(responseEntity.getBody(), EpcCancelRealTicketResult.class);
            tmpLogStr = "result json:" + objectMapper.writeValueAsString(epcCancelRealTicketResult);
logger.info("{}{}", logStr, tmpLogStr);

            if(epcCancelRealTicketResult.getStatusCode() == 0) {
                isCancel = true;
            }
        } catch(HttpStatusCodeException hsce) {
            try {
                epcCancelRealTicketResult = objectMapper.readValue(hsce.getResponseBodyAsString(), EpcCancelRealTicketResult.class);
                tmpLogStr = "result json:" + objectMapper.writeValueAsString(epcCancelRealTicketResult);
logger.info("{}{}", logStr, tmpLogStr);
            } catch (Exception eee) {
                eee.printStackTrace();    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return isCancel;
    }


    public boolean markStockAsRefunding(Connection epcConn, String cancelReceipt) {
        return markStockAsRefund(epcConn, cancelReceipt, REFUND_TYPE_REFUNDING);
    }


    public boolean markStockAsRefunded(Connection epcConn, String cancelReceipt) {
        return markStockAsRefund(epcConn, cancelReceipt, REFUND_TYPE_REFUNDED);
    }


    public boolean markStockAsRefund(Connection epcConn, String cancelReceipt, String refundType) {
        PreparedStatement pstmt = null;
        PreparedStatement pstmtItem = null;
        PreparedStatement pstmtUpdateItem = null;
        ResultSet rset = null;
        ResultSet rsetItem = null;
        String sql = "";
        String iCancelReceipt = epcSecurityHelper.encodeForSQL(StringHelper.trim(cancelReceipt));
        boolean isValid = true;
        int orderId = 0;
        String caseId = "";
        String itemId = "";
        String oldStockStatus = "";
        String newStockStatus = "";
        String newStockStatusDesc = "";
        EpcLogStockStatus epcLogStockStatus = null;
        boolean isMarked = false;

        try {
            // basic checking
            if(!REFUND_TYPE_REFUNDING.equals(refundType) && !REFUND_TYPE_REFUNDED.equals(refundType)) {
                isValid = false;
            }
            // end of basic checking

            if(isValid) {
                if(REFUND_TYPE_REFUNDING.equals(refundType)) {
                    newStockStatus = REFUND_TYPE_REFUNDING; // refunding
                    newStockStatusDesc = "Refunding";
                } else {
                    newStockStatus = REFUND_TYPE_REFUNDED; // refunded
                    newStockStatusDesc = "Refunded";
                }

                sql = "select item_id, stock_status " +
                      "  from epc_order_item " +
                      " where order_id = ? " +
                      "   and case_id = ? " + 
                      "   and item_cat in (?,?,?,?,?,?) ";
                pstmtItem = epcConn.prepareStatement(sql);

                sql = "update epc_order_item " +
                      "   set stock_status = ?, stock_status_desc = ? " +
                      " where order_id = ? " +
                      "   and item_id = ? " + 
                      "   and item_cat in (?,?,?,?,?,?) ";
                pstmtUpdateItem = epcConn.prepareStatement(sql);

                sql = "select order_id, case_id " +
                      "  from epc_order_case " +
                      " where cancel_receipt = ? ";
                pstmt = epcConn.prepareStatement(sql);
                pstmt.setString(1, iCancelReceipt); // cancel_receipt
                rset = pstmt.executeQuery();
                while(rset.next()) {
                    orderId = rset.getInt("order_id");
                    caseId = StringHelper.trim(rset.getString("case_id"));

                    pstmtItem.setInt(1, orderId); // order_id
                    pstmtItem.setString(2, caseId); // case_id
                    pstmtItem.setString(3, EpcItemCategory.DEVICE); // item_cat - DEVICE
                    pstmtItem.setString(4, EpcItemCategory.SIM); // item_cat - SIM
                    pstmtItem.setString(5, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
                    pstmtItem.setString(6, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
                    pstmtItem.setString(7, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
                    pstmtItem.setString(8, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
                    rsetItem = pstmtItem.executeQuery();
                    while(rsetItem.next()) {
                        itemId = StringHelper.trim(rsetItem.getString("item_id"));
                        oldStockStatus = StringHelper.trim(rsetItem.getString("stock_status"));

                        // update stock status
                        pstmtUpdateItem.setString(1, newStockStatus); // stock_status
                        pstmtUpdateItem.setString(2, newStockStatusDesc); // stock_status_desc
                        pstmtUpdateItem.setInt(3, orderId); // order_id
                        pstmtUpdateItem.setString(4, itemId); // item_id
                        pstmtUpdateItem.setString(5, EpcItemCategory.DEVICE); // item_cat - DEVICE
                        pstmtUpdateItem.setString(6, EpcItemCategory.SIM); // item_cat - SIM
                        pstmtUpdateItem.setString(7, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
                        pstmtUpdateItem.setString(8, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
                        pstmtUpdateItem.setString(9, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
                        pstmtUpdateItem.setString(10, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
                        pstmtUpdateItem.executeUpdate();
                        // end of update stock status

                        // create log
                        epcLogStockStatus = new EpcLogStockStatus();
                        epcLogStockStatus.setOrderId(orderId);
                        epcLogStockStatus.setItemId(itemId);
                        epcLogStockStatus.setOldStockStatus(oldStockStatus);
                        epcLogStockStatus.setNewStockStatus(newStockStatus);
                        epcOrderLogHandler.logStockStatus(epcConn, epcLogStockStatus);
                        // end of create log
                    }
                    rsetItem.close();
                }
                rset.close();
                pstmt.close();

                isMarked = true;
            } else {
                isMarked = false;
            }
        } catch (Exception e) {
            e.printStackTrace();

            isMarked = false;
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(rsetItem != null) { rsetItem.close(); } } catch (Exception ee) {}
            try { if(pstmtUpdateItem != null) { pstmtUpdateItem.close(); } } catch (Exception ee) {}
            try { if(pstmtItem != null) { pstmtItem.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return isMarked;
    }


    public boolean removeTmpTicketUnderQuoteItem(Connection epcConn, int orderId, int quoteId, String caseId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String iCaseId = epcSecurityHelper.encodeForSQL(StringHelper.trim(caseId));
        String tmpReserveId = "";
        boolean isRemoved = false;
        String logStr = "[removeTmpTicket][orderId:" + orderId + "][quoteId:" + quoteId + "][caseId:" + iCaseId + "] ";
        String tmpLogStr = "";
        EpcRemoveTmpReserve epcRemoveTmpReserve = null;

        try {
            sql = "select reserve_id " +
                  "  from epc_order_item " +
                  " where order_id = ? " +
                  "   and quote_id = ? " +
                  "   and case_id = ? " +
                  "   and reserve_id is not null ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setInt(2, quoteId); // quote_id
            pstmt.setString(3, iCaseId); // case_id
            rset = pstmt.executeQuery();
            while(rset.next()) {
                tmpReserveId = StringHelper.trim(rset.getString("reserve_id"));

                if("NO_STOCK".equals(tmpReserveId)) {
                    continue;
                }

                epcRemoveTmpReserve = removeTmpReserve(tmpReserveId);
                tmpLogStr = "ticket cancel:" + tmpReserveId + ", result:" + epcRemoveTmpReserve.getResult() + ",errMsg:" + epcRemoveTmpReserve.getErrMsg();
logger.info("{}{}", logStr, tmpLogStr);
            }
            rset.close();
            pstmt.close();

            isRemoved = true;
        } catch (Exception e) {
            e.printStackTrace();

            isRemoved = false;
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return isRemoved;
    }


    public int getNoOfAvailableStock(String loginChannel, String location, String warehouse, String productCode) {
        String apiUrl = EpcProperty.getValue("FES_STOCK_AVAILABLE_LINK");
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        String logStr = "[checkStockAvailable][loginChannel:" + loginChannel + "][location:" + location + "][warehouse:" + warehouse + "][productCode:" + productCode + "] ";
        String tmpLogStr = "";
        EpcCheckStockAvailable epcCheckStockAvailable = null;
        String[] locationArray = new String[1];
        EpcCheckStockAvailableResult epcCheckStockAvailableResult = null;
        int cnt = 0;

        try {
            locationArray[0] = location;

            epcCheckStockAvailable = new EpcCheckStockAvailable();
            epcCheckStockAvailable.setProductType(warehouse);
            epcCheckStockAvailable.setProductCode(productCode);
            epcCheckStockAvailable.setLocationList(locationArray);

            tmpLogStr = "request json:" + objectMapper.writeValueAsString(epcCheckStockAvailable);
logger.info("{}{}", logStr, tmpLogStr);

            epcCheckStockAvailableResult = restTemplate.postForObject(apiUrl, new HttpEntity<>(epcCheckStockAvailable), EpcCheckStockAvailableResult.class);

            tmpLogStr = "result json:" + objectMapper.writeValueAsString(epcCheckStockAvailableResult);
logger.info("{}{}", logStr, tmpLogStr);

            if(epcCheckStockAvailableResult != null) {
                for(EpcCheckStockAvailableResultDetail d: epcCheckStockAvailableResult.getResult()) {
                    if(location.equals(d.getLocation())) {
                        cnt = d.getAvailable();
                    }
                }
            }
        } catch(HttpStatusCodeException hsce) {
            tmpLogStr = "return error:" + hsce.getResponseBodyAsString();
logger.info("{}{}", logStr, tmpLogStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cnt;
    }
    
}







