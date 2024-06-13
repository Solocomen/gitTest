package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcGetOrder;
import epc.epcsalesapi.sales.bean.EpcOrderCaseInfo;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.EpcOrderItemInfo;

@Service
public class EpcAsyncHandler {
	
	private static final Logger logger = LoggerFactory.getLogger(EpcAsyncHandler.class);
	
	@Autowired
	private DataSource epcDataSource;
	

  @Async
  public CompletableFuture<EpcGetOrder> getOrdersAsync(String custId) {
logger.info("getOrdersAsync custId:" + custId);
      String cid = StringHelper.trim(custId);
      EpcGetOrder epcGetOrder = new EpcGetOrder();
      epcGetOrder.setCustId(cid);
      ArrayList<EpcOrderInfo> orderList = new ArrayList<EpcOrderInfo>();
      EpcOrderInfo epcOrderInfo = null;
      epcGetOrder.setOrders(orderList);
      ArrayList<EpcOrderCaseInfo> epcOrderCaseInfoList = null;
      EpcOrderCaseInfo epcOrderCaseInfo = null;
      ArrayList<EpcOrderItemInfo> epcOrderItemInfoList = null;
      EpcOrderItemInfo epcOrderItemInfo = null;
      Connection conn = null;
      PreparedStatement pstmt = null;
      PreparedStatement pstmtCase = null;
      PreparedStatement pstmtItem = null;
      ResultSet rset = null;
      ResultSet rsetCase = null;
      ResultSet rsetItem = null;
      String sql = "";
  String isSerial = ""; // according to fes.stockm
  String attrValue = "";
  TreeMap<String, String> orderStatusMap = new TreeMap<String, String>();
  TreeMap<String, String> itemStatusMap = new TreeMap<String, String>();
  String orderStatusDesc = "";
  String itemStatus = "";
  String itemStatusDesc = "";
  
  try {
      if("".equals(cid)) {
          epcGetOrder.setStatus("FAIL");
          epcGetOrder.setErrorCode("1000");
          epcGetOrder.setErrorMessage("cust id is empty");
      } else {
      	conn = epcDataSource.getConnection();
          
          sql = "select case_id, cpq_offer_desc, cpq_offer_desc_chi, cust_num, subr_num, " +
                "      subr_key, activation_type, to_char(effective_date, 'yyyy-mm-dd hh24:mi:ss') as eff_date " +
                "  from epc_order_case " +
                " where order_id = ? ";
          pstmtCase = conn.prepareStatement(sql);
          
          sql = "select item_id, item_cat, item_code, cpq_item_desc, cpq_item_desc_chi, " +
                "     cpq_item_value, warehouse, reserve_id " +
                "  from epc_order_item " +
                " where order_id = ? " +
                "   and case_id = ? " +
                "   and item_cat in (?,?) ";
          pstmtItem = conn.prepareStatement(sql);
          
          sql = "select key_str1, value_str1 from epc_control_tbl where rec_type = ? ";
          pstmt = conn.prepareStatement(sql);
          pstmt.setString(1, "ORDER_STATUS"); // rec_type
          rset = pstmt.executeQuery();
          while (rset.next()) {
              orderStatusMap.put(StringHelper.trim(rset.getString("key_str1")), StringHelper.trim(rset.getString("value_str1")));
          }
          
          sql = "select key_str1, value_str1 from epc_control_tbl where rec_type = ? ";
          pstmt = conn.prepareStatement(sql);
          pstmt.setString(1, "ORDER_ITEM_STATUS"); // rec_type
          rset = pstmt.executeQuery();
          while (rset.next()) {
              itemStatusMap.put(StringHelper.trim(rset.getString("key_str1")), StringHelper.trim(rset.getString("value_str1")));
          }
          
          
          sql = "select order_id, to_char(order_date, 'yyyy-mm-dd hh24:mi:ss') as o_date, order_reference, order_status, fulfill_method, " +
                "      pickup_location, total_charge_amount " +
                "  from epc_order " +
                " where cust_id = ? " +
                " order by order_date desc ";
          pstmt = conn.prepareStatement(sql);
          pstmt.setString(1, cid); // cust_id
          rset = pstmt.executeQuery();
          while (rset.next()) {
              epcOrderInfo = new EpcOrderInfo();
              epcOrderInfo.setOrderId(rset.getInt("order_id"));
              epcOrderInfo.setOrderDate(StringHelper.trim(rset.getString("o_date")));
              epcOrderInfo.setOrderReference(StringHelper.trim(rset.getString("order_reference")));
              epcOrderInfo.setOrderStatus(StringHelper.trim(rset.getString("order_status")));
              epcOrderInfo.setDeliveryMethod(StringHelper.trim(rset.getString("fulfill_method")));
              epcOrderInfo.setPickupLocation(StringHelper.trim(rset.getString("pickup_location")));
              epcOrderInfo.setTotalAmount(rset.getBigDecimal("total_charge_amount"));
//              epcOrderInfo.setCpqQuoteGUID("");
              
              orderStatusDesc = StringHelper.trim(orderStatusMap.get(StringHelper.trim(rset.getString("order_status"))));
              if("".equals(orderStatusDesc)) {
                  orderStatusDesc = "???";
              }
              epcOrderInfo.setOrderStatusDesc(orderStatusDesc);
              
              orderList.add(epcOrderInfo);
              
              // get case(s) under this order
              epcOrderCaseInfoList = new ArrayList<EpcOrderCaseInfo>();
//              epcOrderInfo.setEpcOrderCaseInfoList(epcOrderCaseInfoList);
              
              pstmtCase.setInt(1, rset.getInt("order_id")); // order_id
              rsetCase = pstmtCase.executeQuery();
              while(rsetCase.next()) {
                  epcOrderCaseInfo = new EpcOrderCaseInfo();
                  epcOrderCaseInfo.setCaseId(StringHelper.trim(rsetCase.getString("case_id")));
                  epcOrderCaseInfo.setOfferDesc(StringHelper.trim(rsetCase.getString("cpq_offer_desc")));
                  epcOrderCaseInfo.setOfferDescChi(StringHelper.trim(rsetCase.getString("cpq_offer_desc_chi")));
                  epcOrderCaseInfo.setCustNum(StringHelper.trim(rsetCase.getString("cust_num")));
                  epcOrderCaseInfo.setSubrNum(StringHelper.trim(rsetCase.getString("subr_num")));
                  epcOrderCaseInfo.setSubrKey(StringHelper.trim(rsetCase.getString("subr_key")));
                  epcOrderCaseInfo.setActivationType(StringHelper.trim(rsetCase.getString("activation_type")));
                  epcOrderCaseInfo.setEffectiveDate(StringHelper.trim(rsetCase.getString("eff_date")));
                  
                  epcOrderCaseInfoList.add(epcOrderCaseInfo);
                  
                  
                  // get item(s) under this case
                  epcOrderItemInfoList = new ArrayList<EpcOrderItemInfo>();
                  
                  pstmtItem.setInt(1, rset.getInt("order_id")); // order_id
                  pstmtItem.setString(2, StringHelper.trim(rsetCase.getString("case_id"))); // case_id
                  pstmtItem.setString(3, "SIM"); // item_cat
                  pstmtItem.setString(4, "DEVICE"); // item_cat
                  rsetItem = pstmtItem.executeQuery();
                  while(rsetItem.next()) {
                      epcOrderItemInfo = new EpcOrderItemInfo();
                      epcOrderItemInfo.setItemId(StringHelper.trim(rsetItem.getString("item_id")));
                      epcOrderItemInfo.setItemCat(StringHelper.trim(rsetItem.getString("item_cat")));
                      epcOrderItemInfo.setItemCode(StringHelper.trim(rsetItem.getString("item_code")));
                      epcOrderItemInfo.setItemDesc(StringHelper.trim(rsetItem.getString("cpq_item_desc")));
                      epcOrderItemInfo.setItemDescChi(StringHelper.trim(rsetItem.getString("cpq_item_desc_chi")));
                      epcOrderItemInfo.setItemValue(StringHelper.trim(rsetItem.getString("cpq_item_value")));
                      epcOrderItemInfo.setWarehouse(StringHelper.trim(rsetItem.getString("warehouse")));
                      epcOrderItemInfo.setReserveId(StringHelper.trim(rsetItem.getString("reserve_id")));
                      
                      if("SIM".equals(StringHelper.trim(rsetItem.getString("item_cat")))) {
                          epcOrderItemInfo.setSerial("S");
                          
//                          attrValue = epcOrderAttrHandler.getAttrValue(conn, rset.getInt("order_id"), "", StringHelper.trim(rsetItem.getString("item_id")), epcOrderAttrHandler.ATTR_TYPE_DUMMY_SIM);
//                          if(!"".equals(attrValue)) {
//                              // if active record is found
//                              epcOrderItemInfo.setIsDummy("Y");
//                          }
                          
                          if("".equals(StringHelper.trim(rsetItem.getString("cpq_item_value")))) {
                              // sim no
                              itemStatus = "PF"; // pending for fulfill
                          } else {
                              itemStatus = "FF"; // fulfilled
                          }
                          epcOrderItemInfo.setStatus(itemStatus);
                          itemStatusDesc = StringHelper.trim(itemStatusMap.get(itemStatus));
                          if("".equals(itemStatusDesc)) {
                              itemStatusDesc = "???";
                          }
                          epcOrderItemInfo.setStatusDesc(itemStatusDesc);
                      } else {
//                          epcOrderItemInfo.setSerial(epcStockHandler.getSerial(StringHelper.trim(rsetItem.getString("item_code"))));
                          epcOrderItemInfo.setIsDummy("N");
                          
                          // ... from reserve ticket
                          epcOrderItemInfo.setStatus("");
                          epcOrderItemInfo.setStatusDesc("");
                      }
                      
                      epcOrderItemInfoList.add(epcOrderItemInfo);
                  } rsetItem.close();
                  rsetItem = null;
                  
                  epcOrderCaseInfo.setEpcOrderItemList(epcOrderItemInfoList);
                  // end of get item(s) under this case
              } rsetCase.close(); 
              rsetCase = null;
              // end of get(s) case under this order
          }
          epcGetOrder.setStatus("OK");
      }
  } catch (Exception e) {
      e.printStackTrace();
      
      epcGetOrder.setStatus("FAIL");
      epcGetOrder.setErrorCode("1001");
      epcGetOrder.setErrorMessage(e.getMessage());
  } finally {
      try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
  }
      
      return CompletableFuture.completedFuture(epcGetOrder);
  }
}
