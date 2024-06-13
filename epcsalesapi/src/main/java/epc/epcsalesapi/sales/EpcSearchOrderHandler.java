package epc.epcsalesapi.sales;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.commons.validator.routines.BigIntegerValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.fes.location.FesLocation;
import epc.epcsalesapi.fes.location.FesLocationHandler;
import epc.epcsalesapi.fes.user.FesUser;
import epc.epcsalesapi.fes.user.FesUserHandler;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcCharacteristicUse;
import epc.epcsalesapi.sales.bean.EpcCustProfile2;
import epc.epcsalesapi.sales.bean.EpcDeliveryDetail;
import epc.epcsalesapi.sales.bean.EpcDeliveryInfo;
import epc.epcsalesapi.sales.bean.EpcGetCharge;
import epc.epcsalesapi.sales.bean.EpcGetChargeResult;
import epc.epcsalesapi.sales.bean.EpcGetOrder;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcLoginChannel;
import epc.epcsalesapi.sales.bean.EpcOfferCharge;
import epc.epcsalesapi.sales.bean.EpcOrderCaseInfo;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.EpcOrderItemInfo;
import epc.epcsalesapi.sales.bean.EpcOrderQuoteInfo;
import epc.epcsalesapi.sales.bean.EpcOrderRequiredItem;
import epc.epcsalesapi.sales.bean.EpcPayment;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.receipt.EpcGetPaymentReceipt;
import epc.epcsalesapi.sales.bean.receipt.EpcGetPaymentReceiptResult;
import epc.epcsalesapi.sales.bean.receipt.EpcReceipt;
import epc.epcsalesapi.stock.EpcStockHandler;

@SuppressWarnings("deprecation")
@Service
public class EpcSearchOrderHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcSearchOrderHandler.class);

    private DataSource epcDataSource;
    private DataSource fesDataSource;
    private EpcOrderHandler epcOrderHandler;
    private EpcSecurityHelper epcSecurityHelper;
    private FesUserHandler fesUserHandler;
    private FesLocationHandler fesLocationHandler;
    private EpcStockHandler epcStockHandler;
    private EpcOrderAttrHandler epcOrderAttrHandler;
    private EpcGetDeliveryInfoHandler epcGetDeliveryInfoHandler;
    private EpcVoucherHandlerNew epcVoucherHandlerNew;
    private EpcPaymentHandler epcPaymentHandler;
    private EpcReceiptHandler epcReceiptHandler;

    public EpcSearchOrderHandler(DataSource epcDataSource, DataSource fesDataSource, EpcOrderHandler epcOrderHandler,
            EpcSecurityHelper epcSecurityHelper, FesUserHandler fesUserHandler, FesLocationHandler fesLocationHandler,
            EpcStockHandler epcStockHandler, EpcOrderAttrHandler epcOrderAttrHandler,
            EpcGetDeliveryInfoHandler epcGetDeliveryInfoHandler, EpcVoucherHandlerNew epcVoucherHandlerNew,
            EpcPaymentHandler epcPaymentHandler, EpcReceiptHandler epcReceiptHandler) {
        this.epcDataSource = epcDataSource;
        this.fesDataSource = fesDataSource;
        this.epcOrderHandler = epcOrderHandler;
        this.epcSecurityHelper = epcSecurityHelper;
        this.fesUserHandler = fesUserHandler;
        this.fesLocationHandler = fesLocationHandler;
        this.epcStockHandler = epcStockHandler;
        this.epcOrderAttrHandler = epcOrderAttrHandler;
        this.epcGetDeliveryInfoHandler = epcGetDeliveryInfoHandler;
        this.epcVoucherHandlerNew = epcVoucherHandlerNew;
        this.epcPaymentHandler = epcPaymentHandler;
        this.epcReceiptHandler = epcReceiptHandler;
    }


    /***
     * used by epc sales UI
     * 
     */
	public EpcGetOrder searchOrders(String searchValue,String orderDateFrom, String orderDateTo,String orderStatus ) {
        String logStr = "[searchOrders] ";
        String searchType = "";
        String iSearchValue = epcSecurityHelper.encodeForSQL(searchValue);
        String dContactEmail = ""; // decrypted
        EpcGetOrder epcGetOrder = null;
        String orderReference = "";
        boolean isSalesman = false;
        FesUser fesUser = fesUserHandler.getUserByUsername(searchValue);
        if(fesUser != null && StringUtils.isNotBlank(fesUser.getUsername()) && "Y".equals(fesUser.getSalesman())) {
            isSalesman = true;
        }
        boolean isLocation = false;
        FesLocation fesLocation = fesLocationHandler.getLocation(searchValue);
        if(fesLocation != null && StringUtils.isNotBlank(fesLocation.getRbdUnitCode())) {
            isLocation = true;
        }

        try {
            dContactEmail = StringHelper.trim(EpcCrypto.dGet(iSearchValue, "utf-8"));
        } catch (Exception e) {
            dContactEmail = "";
        }
        
        if(StringUtils.isNotBlank(epcStockHandler.getProductCodeBySerialNo(iSearchValue))) {
            // imei (existed in stquem)
            searchType = "IMEI";

            orderReference = epcOrderHandler.getOrderReferenceByItemSerialNo(iSearchValue);
            epcGetOrder = getOrdersWithQuoteDetail("", orderReference, "", 0, false, false, "", orderStatus, "", false, false,orderDateFrom,orderDateTo);
        } else if(BigIntegerValidator.getInstance().isValid(iSearchValue)
            || (iSearchValue.startsWith("+") && BigIntegerValidator.getInstance().isValid(iSearchValue.substring(1)))
        ) {
            // mobile
            searchType = "MOBILE";

            epcGetOrder = getOrders("", "", "", 0, false, false, "", orderStatus, iSearchValue, "", "", false, false,orderDateFrom,orderDateTo);
        } else if(EmailValidator.getInstance().isValid(dContactEmail)) {
            // email
            searchType = "EMAIL";

            epcGetOrder = getOrders("", "", "", 0, false, false, "", orderStatus, "", iSearchValue, "", false, false,orderDateFrom,orderDateTo);
        } else if(isSalesman) {
            // salesmanCode
            searchType = "SALESMANCODE";

            epcGetOrder = getOrders("", "", "", 0, false, false, "", orderStatus, "", "", iSearchValue, false, false,orderDateFrom,orderDateTo);
        } else if(isLocation) {
        	// Location
            searchType = "LOCATION";
            
        	epcGetOrder = getOrders("", "", iSearchValue, 0, false, false, "", orderStatus, "", "", "", false, false,orderDateFrom,orderDateTo);
        } else {
            // default, order reference
            searchType = "ORDER_REFERENCE";

            epcGetOrder = getOrdersWithQuoteDetail("", iSearchValue, "", 0, false, false, "", orderStatus, "", false, false,orderDateFrom,orderDateTo);
        }
logger.info("{}{}{}{}{}", logStr, "searchType:", searchType, ",searchValue:", iSearchValue);
        return epcGetOrder;
    }


    public EpcGetOrder getOrdersWithQuoteDetail(
        String custId, String orderReference, String location, int noOfRecord, boolean withQuoteDetail, 
        boolean withCharge, String orderTypes, String orderStatus, String salesmanCode, boolean withRequiredItem, boolean refreshCharge,String orderDateFrom, String orderDateTo
    ) {
        return getOrders(custId, orderReference, location, noOfRecord, withQuoteDetail, withCharge, orderTypes, orderStatus, "", "", salesmanCode, withRequiredItem, refreshCharge,orderDateFrom,orderDateTo);
    }


    public EpcGetOrder getOrders(
        String custId, String orderReference, String location, int noOfRecord, boolean withQuoteDetail,
        boolean withCharge, String orderTypes, String orderStatus, String contactNo, String contactEmail, 
        String salesmanCode, boolean withRequiredItem, boolean refreshCharge,String orderDateFrom, String orderDateTo
    ) {
        // default regenerateCharge = true
        return getOrders(
            custId, orderReference, location, noOfRecord, withQuoteDetail, 
            withCharge, orderTypes, orderStatus, contactNo, contactEmail, 
            salesmanCode, true, withRequiredItem, refreshCharge,orderDateFrom,orderDateTo
        );
    }


    @SuppressWarnings("resource")
	public EpcGetOrder getOrders(
        String custId, String orderReference, String location, int noOfRecord, boolean withQuoteDetail,
        boolean withCharge, String orderTypes, String orderStatus, String contactNo, String contactEmail,
        String salesmanCode, boolean regenerateCharge, boolean withRequiredItem, boolean refreshCharge,String orderDateFrom, String orderDateTo
    ) {
    	String iOrderDateFrom = epcSecurityHelper.encodeForSQL(orderDateFrom);
        String iOrderDateTo = "";
        final String endtime = " 23:59:59";
    	if(StringUtils.isNotBlank(orderDateTo)){
    	    iOrderDateTo = epcSecurityHelper.encodeForSQL(orderDateTo + endtime);
        }

        String cid = epcSecurityHelper.validateId(StringHelper.trim(custId));
        String oRef = epcSecurityHelper.validateId(StringHelper.trim(orderReference));
        String loc = epcSecurityHelper.validateId(StringHelper.trim(location));
        String iContactNo = epcSecurityHelper.validateId(StringHelper.trim(contactNo));
        String iContactEmail = epcSecurityHelper.validateId(StringHelper.trim(contactEmail));
        String dContactEmail = ""; // decrypted
        String dContactEmailUpper = ""; // decrypted
        String iSalesmanCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(salesmanCode));
        EpcGetOrder epcGetOrder = new EpcGetOrder();
        epcGetOrder.setCustId(cid);
        ArrayList<EpcOrderInfo> orderList = new ArrayList<EpcOrderInfo>();
        EpcOrderInfo epcOrderInfo = null;
        epcGetOrder.setOrders(orderList);
        ArrayList<EpcOrderQuoteInfo> epcOrderQuoteInfoList = null;
        EpcOrderQuoteInfo epcOrderQuoteInfo = null;
        ArrayList<EpcOrderCaseInfo> epcOrderCaseInfoList = null;
        EpcOrderCaseInfo epcOrderCaseInfo = null;
        ArrayList<EpcOrderItemInfo> epcOrderItemInfoList = null;
        EpcOrderItemInfo epcOrderItemInfo = null;
        ArrayList<EpcCustProfile2> custProfileList = null;
        EpcCustProfile2 epcCustProfile2 = null;
        Connection conn = null;
        Connection fesConn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtQuote = null;
        PreparedStatement pstmtCase = null;
        PreparedStatement pstmtSalesOfferDesc = null;
        PreparedStatement pstmtItem = null;
        PreparedStatement pstmtSalesman = null;
        PreparedStatement pstmtCustProfile = null;
        ResultSet rset = null;
        ResultSet rsetQuote = null;
        ResultSet rsetCase = null;
        ResultSet rsetSalesAppOfferDesc = null;
        ResultSet rsetItem = null;
        ResultSet rsetSalesman = null;
        ResultSet rsetCustProfile = null;
        String sql = "";
        String tmpSql = "";
        TreeMap<String, String> orderStatusMap = epcOrderHandler.getOrderStatusDescMap();
        TreeMap<String, String> itemStatusMap = epcOrderHandler.getItemStatusDescMap();
        String tmpOrderStatus = "";
        String tmpOrderStatusDesc = "";
        String tmpOrderStatusDescChi = "";
        String tmpItemStatus = "";
        String tmpItemStatusDesc = "";
        String tmpItemStatusDescChi = "";
        String orderType = "";
        int currOrderId = 0;
        ArrayList<String> productCodeList = new ArrayList<String>();
        EpcDeliveryInfo epcDeliveryInfo = null;
        TreeMap<String, EpcDeliveryDetail> itemDeliveryMap = null;
        EpcDeliveryDetail tmpEpcDeliveryDetail = null;
        EpcGetCharge epcGetCharge = null;
        EpcGetChargeResult epcGetChargeResult = null;
        ArrayList<EpcPayment> epcPaymentList = null;
        EpcGetPaymentReceiptResult epcGetPaymentReceiptResult = null;
        EpcGetPaymentReceipt epcGetPaymentReceipt = null;
        BigDecimal tmpItemTotalCharge = null;
        BigDecimal tmpItemChargePaid = null;
        BigDecimal tmpItemRemainingCharge = null;
        String tmpItemId = "";
        String tmpItemCat = "";
        String tmpCustProfileString = "";
        String[] orderTypesArray = null;
        String iOrderTypes = epcSecurityHelper.encodeForSQL(StringHelper.trim(orderTypes));
        if(StringUtils.isNotBlank(iOrderTypes)) {
            orderTypesArray = iOrderTypes.split(",");
        }
        int tmpIdx = 2;
        String iOrderStatus = epcSecurityHelper.encodeForSQL(StringHelper.trim(orderStatus));
//        String quoteString = "";
        ObjectMapper objectMapper = new ObjectMapper();

        
        try {
            if(StringUtils.isNotBlank(iContactEmail)) {
                try {
                    dContactEmail = StringHelper.trim(EpcCrypto.dGet(iContactEmail, "utf-8"));
                    dContactEmailUpper = EpcCrypto.eGet(dContactEmail.toUpperCase(), "utf-8");
                } catch (Exception e) {
                    dContactEmail = "";
                    dContactEmailUpper = "";
                }
            }

            if("".equals(cid) && "".equals(oRef) && "".equals(loc) && "".equals(iContactNo) && "".equals(dContactEmail) && "".equals(iSalesmanCode)
            		&&"".equals(iOrderDateFrom)&&"".equals(iOrderDateTo)) {
                epcGetOrder.setStatus("FAIL");
                epcGetOrder.setErrorCode("1000");
                epcGetOrder.setErrorMessage("input is empty or invalid");
            } else {
            	
            	
            	if(StringUtils.isBlank(iOrderDateFrom)) {
            		iOrderDateFrom="19000101";
            	}
            	
            	if(StringUtils.isBlank(iOrderDateTo)) {
            		iOrderDateTo="29991231" + endtime;
            	}
            	
            	conn = epcDataSource.getConnection();
            	fesConn = fesDataSource.getConnection();
            	
            	sql = "select quote_id, cpq_quote_guid, quote_content " +
            	      "  from epc_order_quote " +
            		  " where order_id = ? ";
            	pstmtQuote = conn.prepareStatement(sql);
                
                sql = "select case_id, cpq_offer_desc, cpq_offer_desc_chi, cust_num, subr_num, " +
                      "       subr_key, activation_type, to_char(effective_date, 'yyyy-mm-dd hh24:mi') as eff_date, cancel_receipt, to_char(cancel_date, 'yyyy-mm-dd hh24:mi') as c_date " +
                      "  from epc_order_case " +
                      " where order_id = ? " +
                      "   and quote_id = ? ";
                pstmtCase = conn.prepareStatement(sql);
                
                sql = "select cpq_item_desc, cpq_item_desc_chi "
                		+ "  from epc_order_item "
                		+ "where order_id = ? "
                		+ "and case_id = ? "
                		+ "and parent_item_id is null ";
                pstmtSalesOfferDesc = conn.prepareStatement(sql);
                
                sql = "select item_id, item_cat, item_code, cpq_item_desc, cpq_item_desc_chi, " +
                      "       cpq_item_value, warehouse, reserve_id, invoice_no, to_char(invoice_date, 'yyyy-mm-dd hh24:mi') as inv_date, " +
                	  "       parent_item_id, delivery_id, serial, stock_status, stock_status_desc, " +
                      "       pickup_date, is_reserve " +
                      "  from epc_order_item " +
                      " where order_id = ? " +
                      "   and case_id = ? " +
                      "   and item_cat in (?,?,?,?,?,?,?) ";
                pstmtItem = conn.prepareStatement(sql);

                sql = "select create_channel, create_location " +
                      "  from epc_order_salesman " +
                      " where order_id = ? " +
                      "   and case_id = ? " +
                      "   and action = ? ";
                pstmtSalesman = conn.prepareStatement(sql);

                sql = "select item_id, cust_num, subr_num, subr_key, to_char(effective_date, 'yyyymmddhh24miss') as eff_date, " +
                      "       activation_type, cust_info " +
                      "  from epc_order_cust_profile " +
                      " where order_id = ? " +
                      "   and case_id = ? " +
                      "   and status = ? ";
                pstmtCustProfile = conn.prepareStatement(sql);

                
                if(StringUtils.isNotBlank(cid)) {
                	// by a customer
	                sql = "select order_id, to_char(order_date, 'yyyy-mm-dd hh24:mi:ss') as o_date, order_reference, order_status, total_charge_amount, " +
                          "       cust_id, contact_email, contact_no, order_lang, place_order_channel " +
	                      "  from epc_order a " +
	                      " where cust_id = ? and order_date between to_date(?,'yyyymmdd') and to_date(?,'yyyymmdd hh24:mi:ss')";
                    if(StringUtils.isNotBlank(iOrderStatus)) {
                        sql += "  and order_status = ? ";
                    }
                    if(orderTypesArray != null && orderTypesArray.length > 0) {
                        tmpSql = "";
                        sql += "  and exists ( " +
                               "    select 1 from epc_order_attr b " +
                               "     where b.order_id = a.order_id " +
                               "       and b.attr_type = ? " +
                               "       and b.status = ? " +
                               "       and b.attr_value in ( ";

                        for (int i = 0; i < orderTypesArray.length; i++) {
                            if("".equals(tmpSql)) {
                                tmpSql = "?";
                            } else {
                                tmpSql += ",?";
                            }
                        }

                        sql += tmpSql;
                        sql += "       ) ";
                        sql += "  ) ";
                    }

	                sql += " order by order_date desc ";

                    tmpIdx = 4;
	                pstmt = conn.prepareStatement(sql);
	                pstmt.setString(1, cid); // cust_id
	                pstmt.setString(2, iOrderDateFrom);
                    pstmt.setString(3, iOrderDateTo);
                    if(StringUtils.isNotBlank(iOrderStatus)) {
                        pstmt.setString(tmpIdx++, iOrderStatus); // order_status
                    }
                    
                    if(orderTypesArray != null && orderTypesArray.length > 0) {
                        pstmt.setString(tmpIdx++, epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE); // order_type
                        pstmt.setString(tmpIdx++, "A"); // status

                        for(String s : orderTypesArray) {
                            pstmt.setString(tmpIdx++, s); // order_type
                        }
                    }
                } else if(StringUtils.isNotBlank(oRef)) {
                	// by order reference
                	sql = "select order_id, to_char(order_date, 'yyyy-mm-dd hh24:mi:ss') as o_date, order_reference, order_status, total_charge_amount, " +
                          "       cust_id, contact_email, contact_no, order_lang, place_order_channel " +
  	                      "  from epc_order " +
  	                      " where order_reference = ? ";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, oRef); // order_reference
                } else if(StringUtils.isNotBlank(iContactNo)) {
                    // by contact no
                    sql = "select order_id, to_char(order_date, 'yyyy-mm-dd hh24:mi:ss') as o_date, order_reference, order_status, total_charge_amount, " +
                          "       cust_id, contact_email, contact_no, order_lang, place_order_channel " +
                          "  from epc_order a" +
                          " where contact_no = ? and order_date between to_date(?,'yyyymmdd') and to_date(?,'yyyymmdd hh24:mi:ss') "
                                + " and exists ("
                          		+ "     select 1 from epc_order_attr b "
                          		+ "      where b.order_id = a.order_id "
                          		+ "        and b.attr_type = 'ORDER_TYPE' "
                          		+ "        and b.status = 'A' "
                          		+ "        and b.attr_value ='CHECKOUT' "
                          		+ "   )";
                    if (StringUtils.isNotBlank(iOrderStatus )) {
                    	sql +=" and order_status = ?";
					}
                    sql +=" order by order_date desc";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, iContactNo); // contact_no
                    pstmt.setString(2, iOrderDateFrom);
                    pstmt.setString(3, iOrderDateTo);
                    if (StringUtils.isNotBlank(iOrderStatus )) {
                    	pstmt.setString(4, iOrderStatus);
					}
                } else if(StringUtils.isNotBlank(dContactEmailUpper)) {
                    // by contact email
                    sql = "select order_id, to_char(order_date, 'yyyy-mm-dd hh24:mi:ss') as o_date, order_reference, order_status, total_charge_amount, " +
                          "       cust_id, contact_email, contact_no, order_lang, place_order_channel " +
                          "  from epc_order a" +
                          " where contact_email_upper = ? and order_date between to_date(?,'yyyymmdd') and to_date(?,'yyyymmdd hh24:mi:ss') "
                            + " and exists ("
                    		+ "     select 1 from epc_order_attr b "
                    		+ "      where b.order_id = a.order_id "
                    		+ "        and b.attr_type = 'ORDER_TYPE' "
                    		+ "        and b.status = 'A' "
                    		+ "        and b.attr_value ='CHECKOUT' "
                    		+ "   )";
                    if (StringUtils.isNotBlank(iOrderStatus )) {
                    	sql +=" and order_status = ?";
					}
                    sql +=" order by order_date desc";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, dContactEmailUpper); // contact_email_upper
                    pstmt.setString(2, iOrderDateFrom);
                    pstmt.setString(3, iOrderDateTo);
                    if (StringUtils.isNotBlank(iOrderStatus )) {
                    	pstmt.setString(4, iOrderStatus);
					}
                } else if(StringUtils.isNotBlank(iSalesmanCode)) {
                    // by salesman code
                    sql = "select order_id, to_char(order_date, 'yyyy-mm-dd hh24:mi:ss') as o_date, order_reference, order_status, total_charge_amount, " +
                          "       cust_id, contact_email, contact_no, order_lang, place_order_channel " +
                          "  from epc_order a " +
                          " where a.order_salesman = ? " ;
                    if (!StringUtils.isNotBlank(orderDateFrom) && !StringUtils.isNotBlank(orderDateTo)){
                        sql += " AND a.order_date > trunc(sysdate) ";
                    }
                    sql += "   and exists ( " +
                          "     select 1 from epc_order_attr b " +
                          "      where b.order_id = a.order_id " +
                          "        and b.attr_type = ? " +
                          "        and b.status = ? " +
                          "        and b.attr_value in (?) " +
                          "   ) and order_date between to_date(?,'yyyymmdd') and to_date(?,'yyyymmdd hh24:mi:ss') " ;
                   if (StringUtils.isNotBlank(iOrderStatus )) {
                          sql +=" and order_status = ?";
      					}
                          sql +=" order by order_date desc";
                    pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, iSalesmanCode); // order_salesman
                    pstmt.setString(2, "ORDER_TYPE"); // attr_type
                    pstmt.setString(3, "A"); // status
                    pstmt.setString(4, "CHECKOUT"); // attr_value
                    pstmt.setString(5, iOrderDateFrom);
                    pstmt.setString(6, iOrderDateTo);
                    if (StringUtils.isNotBlank(iOrderStatus )) {
                    	pstmt.setString(7, iOrderStatus);
					}
                } else if(StringUtils.isNotBlank(loc)&&!"WHS".equals(loc)){
                	sql="select a.order_id, to_char(order_date, 'yyyy-mm-dd hh24:mi:ss') as o_date, order_reference, order_status, total_charge_amount, "
                			+ "       cust_id, contact_email, contact_no, order_lang, place_order_channel "
                			+ "  from epc_order a, epc_order_salesman c"
                			+ "   where c.create_location = ?"
                			+ "   and c.action = ?"
                			+ "   and a.order_id = c.order_id";
                    if (!StringUtils.isNotBlank(orderDateFrom) && !StringUtils.isNotBlank(orderDateTo)){
                        sql += " AND a.order_date > trunc(sysdate) ";
                    }
                    sql += "   and order_date between to_date(?,'yyyymmdd') and to_date(?,'yyyymmdd hh24:mi:ss')"
                			+ "   and exists ( "
                			+ "     select 1 from epc_order_attr b "
                			+ "      where b.order_id = a.order_id "
                			+ "        and b.attr_type = ?"
                			+ "        and b.status = ?"
                			+ "        and b.attr_value = ? "
                			+ "   ) ";
                	if (StringUtils.isNotBlank(iOrderStatus )) {
                    	sql +=" and order_status = ?";
					}
                	sql +=" order by order_date desc";
                	pstmt = conn.prepareStatement(sql);
                	pstmt.setString(1, loc);
                	pstmt.setString(2, "CREATE_QUOTE");
                	pstmt.setString(3, iOrderDateFrom);
                    pstmt.setString(4, iOrderDateTo);
                	pstmt.setString(5, "ORDER_TYPE");
                	pstmt.setString(6, "A");
                	pstmt.setString(7, "CHECKOUT");
                	if (StringUtils.isNotBlank(iOrderStatus )) {
                    	pstmt.setString(8, iOrderStatus);
					}
                }else {
                	// warehouse cases
                	//  ... need to wait all items ready (except premium)
                	sql = "select order_id, to_char(order_date, 'yyyy-mm-dd hh24:mi:ss') as o_date, order_reference, order_status, total_charge_amount, " +
                          "       cust_id, contact_email, contact_no, order_lang, place_order_channel " +
    	                  "  from epc_order " +
    	                  " where exists ( " +
    	                  "     select aa.order_id " +
    	                  "       from epc_order_item aa, epc_order_delivery bb, epc_order cc " +
    	                  "      where bb.pickup_location = ? " +
    	                  "        and bb.status = ? " +
    	                  "        and aa.delivery_id = bb.delivery_id " +
    	                  "        and cc.order_id = aa.order_id " +
    	                  "        and cc.order_status = ? " +
    	                  "   ) " +
    	                  " order by order_date ";
                  	pstmt = conn.prepareStatement(sql);
                  	pstmt.setString(1, loc); // pickup_location
                  	pstmt.setString(2, "A"); // status - Active
                  	pstmt.setString(3, "PF"); // order_status - PF
                }
                rset = pstmt.executeQuery();
                while (rset.next()) {
                	currOrderId = rset.getInt("order_id");
                			
                	orderType = epcOrderAttrHandler.getAttrValue(conn, currOrderId, "", "", epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE);
                	if("I".equals(StringHelper.trim(rset.getString("order_status"))) && epcOrderAttrHandler.ATTR_TYPE_ORDER_TYPE_FAST_CHECKOUT.equals(orderType)) {
                		// skip order with order type = FAST_CHECKOUT and its status = "I" - init
                		continue;
                	}
                	
                	// get delivery info (allow multiple)
                	epcDeliveryInfo = epcGetDeliveryInfoHandler.getPickupLocation(currOrderId);
                	itemDeliveryMap = new TreeMap<String, EpcDeliveryDetail>();
                	for(EpcDeliveryDetail d: epcDeliveryInfo.getDetails()) {
                		for(String itemId: d.getItems()) {
                			itemDeliveryMap.put(itemId, d);
                		}
                	}
                	// end of get delivery info (allow multiple)
                	
                	
                    epcOrderInfo = new EpcOrderInfo();
                    epcOrderInfo.setCustId(StringHelper.trim(rset.getString("cust_id")));
                    epcOrderInfo.setOrderId(currOrderId);
                    epcOrderInfo.setOrderDate(StringHelper.trim(rset.getString("o_date")));
                    epcOrderInfo.setOrderReference(StringHelper.trim(rset.getString("order_reference")));
                    epcOrderInfo.setTotalAmount(rset.getBigDecimal("total_charge_amount"));
                    epcOrderInfo.setContactNo(StringHelper.trim(rset.getString("contact_no")));
                    epcOrderInfo.setContactEmail(StringHelper.trim(rset.getString("contact_email")));
                    epcOrderInfo.setOrderLang(StringHelper.trim(rset.getString("order_lang")));
                    epcOrderInfo.setPlaceOrderChannel(StringHelper.trim(rset.getString("place_order_channel")));
                    epcOrderInfo.setEpcDeliveryDetailList(epcDeliveryInfo.getDetails());
                    epcOrderInfo.setOrderType(orderType);

                    tmpOrderStatus = StringHelper.trim(rset.getString("order_status"));
                    epcOrderInfo.setOrderStatus(tmpOrderStatus);
                    
                    tmpOrderStatusDesc = StringHelper.trim(orderStatusMap.get(tmpOrderStatus));
                    if("".equals(tmpOrderStatusDesc)) {
                        tmpOrderStatusDesc = "???";
                    }
                    epcOrderInfo.setOrderStatusDesc(tmpOrderStatusDesc);

                    tmpOrderStatusDescChi = StringHelper.trim(orderStatusMap.get(tmpOrderStatus + "_CHI"));
                    if("".equals(tmpOrderStatusDescChi)) {
                        tmpOrderStatusDescChi = "???";
                    }
                    epcOrderInfo.setOrderStatusDescChi(tmpOrderStatusDescChi);


                    // get redeemed vouchers
//                    epcOrderInfo.setVoucherList(epcVoucherHandler.getRedeemedVouchersInOrder(currOrderId));
                    epcOrderInfo.setVoucherList(epcVoucherHandlerNew.getRedeemedVouchersInOrder(epcOrderInfo.getCustId(), currOrderId));
                    // end get redeemed vouchers

                    // get attr under this order
                    epcOrderInfo.setAttrList(epcOrderAttrHandler.getAllAttrsUnderOrder(currOrderId));
                    // end of get attr under this order

                    orderList.add(epcOrderInfo);
                    
                    
                    // get quote(s) under this order
                    epcOrderQuoteInfoList = new ArrayList<EpcOrderQuoteInfo>();
                    epcOrderInfo.setEpcOrderQuoteInfoList(epcOrderQuoteInfoList);
                    
                    pstmtQuote.setInt(1, currOrderId); // order_id
                    rsetQuote = pstmtQuote.executeQuery();
                    while(rsetQuote.next()) {
//                        quoteString = StringHelper.trim(rsetQuote.getString("quote_content"));

                    	epcOrderQuoteInfo = new EpcOrderQuoteInfo();
                    	epcOrderQuoteInfo.setQuoteId(rsetQuote.getInt("quote_id"));
                    	epcOrderQuoteInfo.setQuoteGuid(StringHelper.trim(rsetQuote.getString("cpq_quote_guid")));
                    	
                    	epcOrderQuoteInfoList.add(epcOrderQuoteInfo);

                        /* added by Danny Chan on 2023-4-3 (return order derived attributes for getOrders): start */
                        EpcQuote q = null;
						
                        if(withCharge || withQuoteDetail || withRequiredItem) {
                            q = epcOrderHandler.getCPQQuoteInEpc(currOrderId, rsetQuote.getInt("quote_id"));
					    }
						
                        if (withRequiredItem&&q!=null) {
                            EpcQuoteItem items[] = q.getItems();
                    		
                            for ( int k=0; k<items.length; k++ ) {
                                if (items[k].getProductCandidateObj()==null || items[k].getProductCandidateObj().getCharacteristicUse()==null ) {
                                    continue;
                                }

                                boolean hasStaffInfo = false;
                    		
                                ArrayList<EpcCharacteristicUse> characteristics_list = items[k].getProductCandidateObj().getCharacteristicUse();
                    
                                for (int m=0; m<characteristics_list.size(); m++ ) {
                                    if ( characteristics_list.get(m).getName().equals("Require_Document") ) {
                                        ArrayList<String> value = characteristics_list.get(m).getValue();
                    					
                                        for ( int n=0; n<value.size(); n++ ) {
                                            if ( value.get(n).equals("SHK and SMC Company Name") || value.get(n).equals("SHK and SMC Staff ID") ) {
                                                hasStaffInfo = true;
                                            }
                                        }
                                    }
                                }
                            
                                if (!hasStaffInfo) {
                                    continue;
                                }
                    		
                                ArrayList<EpcOrderRequiredItem> requiredItemlist = epcOrderInfo.getRequiredItemList();
                    							
                                if ( requiredItemlist==null ) {
                                    requiredItemlist = new ArrayList<EpcOrderRequiredItem>();
                                    epcOrderInfo.setRequiredItemList(requiredItemlist);
                                }

                                EpcOrderRequiredItem requiredItem = new EpcOrderRequiredItem();
							
                                requiredItem.setItemType("hasStaffInfo");
                                requiredItem.setOrderId(currOrderId);
                                requiredItem.setItemId(items[k].getProductCandidateObj().getId());
                                requiredItem.setCaseId(items[k].getProductCandidateObj().getId());
                    							
                                requiredItemlist.add(requiredItem);
                            }
                        }
                        /* added by Danny Chan on 2023-4-3 (return order derived attributes for getOrders): end */				
						
                        if(withCharge || withQuoteDetail) { // need quote
                            epcOrderQuoteInfo.setEpcQuote(q);
//                            if("".equals(quoteString)) {
//                                // get from cpq
//                                epcOrderQuoteInfo.setEpcQuote(epcQuoteHandler.getQuoteInfo(epcOrderQuoteInfo.getQuoteGuid()));
//                            } else {
//                                epcOrderQuoteInfo.setEpcQuote(objectMapper.readValue(quoteString, EpcQuote.class));
//                            }                          
                        }
                    
                    
	                    // get case(s) under this quote
	                    epcOrderCaseInfoList = new ArrayList<EpcOrderCaseInfo>();
	                    epcOrderQuoteInfo.setEpcOrderCaseInfoList(epcOrderCaseInfoList);
	                    
	                    pstmtCase.setInt(1, currOrderId); // order_id
	                    pstmtCase.setInt(2, rsetQuote.getInt("quote_id")); // quote_id
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
	                        epcOrderCaseInfo.setCancelDate(StringHelper.trim(rsetCase.getString("c_date")));
	                        epcOrderCaseInfo.setCancelReceipt(StringHelper.trim(rsetCase.getString("cancel_receipt")));
	                        
	                        // get sales app offer desc
	                        pstmtSalesOfferDesc.setInt(1, currOrderId);
	                        pstmtSalesOfferDesc.setString(2, StringHelper.trim(rsetCase.getString("case_id")));
	                        rsetSalesAppOfferDesc = pstmtSalesOfferDesc.executeQuery();
	                        if(rsetSalesAppOfferDesc.next()) {
	                        	epcOrderCaseInfo.setSalesAppOfferDesc(StringHelper.trim(rsetSalesAppOfferDesc.getString("cpq_item_desc")));
	                        	epcOrderCaseInfo.setSalesAppOfferDescChi(StringHelper.trim(rsetSalesAppOfferDesc.getString("cpq_item_desc_chi")));
	                        } else {
	                        	epcOrderCaseInfo.setSalesAppOfferDesc("");
	                        	epcOrderCaseInfo.setSalesAppOfferDescChi("");
	                        }rsetSalesAppOfferDesc.close();
	                        // end of get sales app offer desc

                            // get channel / location when add this case (quote item)
                            pstmtSalesman.setInt(1, currOrderId); // order_id
                            pstmtSalesman.setString(2, StringHelper.trim(rsetCase.getString("case_id"))); // case_id
                            pstmtSalesman.setString(3, "ADD_QUOTE_ITEM"); // action
                            rsetSalesman = pstmtSalesman.executeQuery();
                            if(rsetSalesman.next()) {
                                epcOrderCaseInfo.setCreateChannel(StringHelper.trim(rsetSalesman.getString("create_channel")));
                                epcOrderCaseInfo.setCreateLocation(StringHelper.trim(rsetSalesman.getString("create_location")));
                            } else {
                                epcOrderCaseInfo.setCreateChannel("");
                                epcOrderCaseInfo.setCreateLocation("");
                            }rsetSalesman.close();
	                        
	                        epcOrderCaseInfoList.add(epcOrderCaseInfo);
	                        // end of get channel / location when add this case (quote item)

	                        
	                        // get item(s) under this case
	                        epcOrderItemInfoList = new ArrayList<EpcOrderItemInfo>();
	                        
	                        pstmtItem.setInt(1, currOrderId); // order_id
	                        pstmtItem.setString(2, StringHelper.trim(rsetCase.getString("case_id"))); // case_id
	                        pstmtItem.setString(3, EpcItemCategory.SIM); // item_cat - SIM
	                        pstmtItem.setString(4, EpcItemCategory.DEVICE); // item_cat - DEVICE
	                        pstmtItem.setString(5, EpcItemCategory.SCREEN_REPLACE); // item_cat - SCREEN_REPLACE
	                        pstmtItem.setString(6, EpcItemCategory.APPLECARE); // item_cat - APPLECARE
                            pstmtItem.setString(7, EpcItemCategory.GIFT_WRAPPING); // item_cat - GIFT_WRAPPING
                            pstmtItem.setString(8, EpcItemCategory.PLASTIC_BAG); // item_cat - PLASTIC_BAG
                            pstmtItem.setString(9, EpcItemCategory.BONUS_GIFT); // item_cat - BONUS_GIFT
	                        rsetItem = pstmtItem.executeQuery();
	                        while(rsetItem.next()) {
                                tmpItemId = StringHelper.trim(rsetItem.getString("item_id"));
                                tmpItemCat = StringHelper.trim(rsetItem.getString("item_cat"));

	                            epcOrderItemInfo = new EpcOrderItemInfo();
	                            epcOrderItemInfo.setItemId(tmpItemId);
	                            epcOrderItemInfo.setItemCat(tmpItemCat);
	                            epcOrderItemInfo.setItemCode(StringHelper.trim(rsetItem.getString("item_code")));
	                            epcOrderItemInfo.setItemDesc(StringHelper.trim(rsetItem.getString("cpq_item_desc")));
	                            epcOrderItemInfo.setItemDescChi(StringHelper.trim(rsetItem.getString("cpq_item_desc_chi")));
	                            epcOrderItemInfo.setItemValue(StringHelper.trim(rsetItem.getString("cpq_item_value")));
	                            epcOrderItemInfo.setWarehouse(StringHelper.trim(rsetItem.getString("warehouse")));
	                            epcOrderItemInfo.setReserveId(StringHelper.trim(rsetItem.getString("reserve_id")));
	                            epcOrderItemInfo.setInvoiceNo(StringHelper.trim(rsetItem.getString("invoice_no")));
	                            epcOrderItemInfo.setInvoiceDate(StringHelper.trim(rsetItem.getString("inv_date")));
	                            epcOrderItemInfo.setSerial(StringHelper.trim(rsetItem.getString("serial")));
                                epcOrderItemInfo.setIsReserveItem(StringHelper.trim(rsetItem.getString("is_reserve")));
                                epcOrderItemInfo.setPickupDate(StringHelper.trim(rsetItem.getString("pickup_date")));

                                tmpItemStatus = StringHelper.trim(rsetItem.getString("stock_status"));
                                epcOrderItemInfo.setStatus(tmpItemStatus);

                                tmpItemStatusDesc = StringHelper.trim(itemStatusMap.get(tmpItemStatus));
                                if("".equals(tmpItemStatusDesc)) {
                                    tmpItemStatusDesc = "???";
                                }
                                epcOrderItemInfo.setStatusDesc(tmpItemStatusDesc);

                                tmpItemStatusDescChi = StringHelper.trim(itemStatusMap.get(tmpItemStatus + "_CHI"));
                                if("".equals(tmpItemStatusDescChi)) {
                                    tmpItemStatusDescChi = "???";
                                }
                                epcOrderItemInfo.setStatusDescChi(tmpItemStatusDescChi);


                                if(EpcItemCategory.DEVICE.equals(tmpItemCat)) {
                                    tmpItemTotalCharge = epcPaymentHandler.getItemTotalCharge(conn, currOrderId, tmpItemId);
                                    tmpItemChargePaid = epcPaymentHandler.getItemChargePaid(conn, currOrderId, tmpItemId);
                                    tmpItemRemainingCharge = tmpItemTotalCharge.subtract(tmpItemChargePaid);

                                    epcOrderItemInfo.setTotalCharge(tmpItemTotalCharge);
                                    epcOrderItemInfo.setChargePaid(tmpItemChargePaid);
                                    epcOrderItemInfo.setRemainingCharge(tmpItemRemainingCharge);
                                } else if(EpcItemCategory.SIM.equals(tmpItemCat)
                                 || EpcItemCategory.GIFT_WRAPPING.equals(tmpItemCat)
                                 || EpcItemCategory.PLASTIC_BAG.equals(tmpItemCat) || EpcItemCategory.APPLECARE.equals(tmpItemCat)
                                ) {
                                    tmpItemTotalCharge = epcPaymentHandler.getItemTotalCharge(conn, currOrderId, tmpItemId);

                                    epcOrderItemInfo.setTotalCharge(tmpItemTotalCharge);
                                    epcOrderItemInfo.setChargePaid(new BigDecimal(0));
                                    epcOrderItemInfo.setRemainingCharge(new BigDecimal(0));
                                } else {
                                    epcOrderItemInfo.setTotalCharge(new BigDecimal(0));
                                    epcOrderItemInfo.setChargePaid(new BigDecimal(0));
                                    epcOrderItemInfo.setRemainingCharge(new BigDecimal(0));
                                }
	                            
	                            if(EpcItemCategory.SCREEN_REPLACE.equals(epcOrderItemInfo.getItemCat()) || EpcItemCategory.APPLECARE.equals(epcOrderItemInfo.getItemCat())) {
	                            	epcOrderItemInfo.setParentItemId(StringHelper.trim(rsetItem.getString("parent_item_id")));
	                            } else {
	                            	epcOrderItemInfo.setParentItemId("");
	                            }
	                            
	                            tmpEpcDeliveryDetail = itemDeliveryMap.get(epcOrderItemInfo.getItemId());
	                            if(tmpEpcDeliveryDetail != null) {
	                            	epcOrderItemInfo.setDeliveryId(tmpEpcDeliveryDetail.getDeliveryId());
	                            	epcOrderItemInfo.setDeliveryMethod(tmpEpcDeliveryDetail.getDeliveryMethod());
	                            	epcOrderItemInfo.setPickupLocation(tmpEpcDeliveryDetail.getPickupStore());
	                            } else {
	                            	epcOrderItemInfo.setDeliveryId(0);
	                            	epcOrderItemInfo.setDeliveryMethod("");
	                            	epcOrderItemInfo.setPickupLocation("");
	                            }
	                            
	                            // used to get product config (i.e. serial flag)
	                            if(StringUtils.isNotBlank(StringHelper.trim(rsetItem.getString("item_code")))) {
	                            	productCodeList.add(StringHelper.trim(rsetItem.getString("item_code")));
	                            }
	                            
	                            epcOrderItemInfoList.add(epcOrderItemInfo);
	                        } rsetItem.close();
	                        rsetItem = null;
	                        
	                        epcOrderCaseInfo.setEpcOrderItemList(epcOrderItemInfoList);
	                        // end of get item(s) under this case


                            // get cust profile(s)
                            custProfileList = new ArrayList<EpcCustProfile2>();
                            epcOrderCaseInfo.setCustProfileList(custProfileList);

                            pstmtCustProfile.setInt(1, currOrderId); // order_id
                            pstmtCustProfile.setString(2, StringHelper.trim(rsetCase.getString("case_id"))); // case_id
                            pstmtCustProfile.setString(3, "A"); // status - A: active
                            rsetCustProfile = pstmtCustProfile.executeQuery();
	                        while(rsetCustProfile.next()) {
                                epcCustProfile2 = new EpcCustProfile2();
                                epcCustProfile2.setItemId(StringHelper.trim(rsetCustProfile.getString("item_id")));
                                epcCustProfile2.setCustNum(StringHelper.trim(rsetCustProfile.getString("cust_num")));
                                epcCustProfile2.setSubrNum(StringHelper.trim(rsetCustProfile.getString("subr_num")));
                                epcCustProfile2.setSubrKey(StringHelper.trim(rsetCustProfile.getString("subr_key")));
                                epcCustProfile2.setEffectiveDate(StringHelper.trim(rsetCustProfile.getString("eff_date")));
                                epcCustProfile2.setActivationType(StringHelper.trim(rsetCustProfile.getString("activation_type")));

                                tmpCustProfileString = StringHelper.trim(rsetCustProfile.getString("cust_info"));
                                if(!"".equals(tmpCustProfileString)) {
                                    epcCustProfile2.setCustInfoMap((HashMap<String, Object>)objectMapper.readValue(tmpCustProfileString, HashMap.class));
                                } else {
                                    epcCustProfile2.setCustInfoMap(new HashMap<String, Object>());
                                }

                                custProfileList.add(epcCustProfile2);
                            } rsetCustProfile.close();
                            // end of cust profile(s) 
	                    } rsetCase.close(); 
	                    rsetCase = null;
	                    // end of get(s) case under this quote
                    } rsetQuote.close();
                    rsetQuote = null;
                    // end of get quote(s) under this order
                    
                    
                    // get & set charge list
                    if(epcOrderQuoteInfoList.size() > 0 && withCharge) {
                    	epcGetCharge = new EpcGetCharge();
                        epcGetCharge.setOrderId("" + currOrderId);
                        epcGetCharge.setPaymentList(new ArrayList<EpcPayment>());
                        if( !regenerateCharge ) {
                            epcGetCharge.setFromPlaceOrder("Y");
                        }
                        
                        epcGetCharge.setSavePayment(!refreshCharge);
                        
                        epcGetChargeResult = epcPaymentHandler.getChargeResult(epcGetCharge, epcOrderInfo);
                        if(!"SUCCESS".equals(epcGetChargeResult.getResult())) {
                        	throw new Exception(epcSecurityHelper.encode("fail to get charge list [orderId:" + rset.getInt("order_id") + "], err:" + epcGetChargeResult.getErrorMessage()));
                        }
                        epcOrderInfo.setEpcChargeList((ArrayList<EpcOfferCharge>)epcGetChargeResult.getOfferChargeList());                    	
                    } else {
                    	epcOrderInfo.setEpcChargeList(null);
                    }
                    // end of get & set charge list
                    
                    
                    // get related payment (if any)
                    epcPaymentList = epcOrderHandler.getPaymentInfo(conn, currOrderId);
                    if(epcPaymentList == null) {
                        epcPaymentList = new ArrayList<>();
                    }
                    epcOrderInfo.setEpcPaymentList(epcPaymentList);
                    // end of get related payment (if any)
                    
                    // payment receipt list
                    epcGetPaymentReceipt = new EpcGetPaymentReceipt();
                    epcGetPaymentReceipt.setOrderId(currOrderId);
                    epcGetPaymentReceiptResult = epcReceiptHandler.getPaymentReceipt(fesConn, conn, epcGetPaymentReceipt);
                    if ("SUCCESS".equals(epcGetPaymentReceiptResult.getResult())) {
                        epcOrderInfo.setEpcPaymentReceiptList(epcGetPaymentReceiptResult.getReceiptList());
                    } else {
                        epcOrderInfo.setEpcPaymentReceiptList(new ArrayList<EpcReceipt>());
                    }
                    
                }
                
                epcGetOrder.setStatus("OK");
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcGetOrder.setStatus("FAIL");
            epcGetOrder.setErrorCode("1001");
            epcGetOrder.setErrorMessage(e.getMessage());
        } finally {
            try { if(rset != null) { rset.close(); rset = null; } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); pstmt = null; } } catch (Exception ee) {}
            try { if(rsetQuote != null) { rsetQuote.close(); rsetQuote = null; } } catch (Exception ee) {}
            try { if(pstmtQuote != null) { pstmtQuote.close(); pstmtQuote = null; } } catch (Exception ee) {}
            try { if(rsetCase != null) { rsetCase.close(); rsetCase = null; } } catch (Exception ee) {}
            try { if(pstmtCase != null) { pstmtCase.close(); pstmtCase = null; } } catch (Exception ee) {}
            try { if(rsetItem != null) { rsetItem.close(); rsetItem = null; } } catch (Exception ee) {}
            try { if(pstmtItem != null) { pstmtItem.close(); pstmtItem = null; } } catch (Exception ee) {}

            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ee) {}
        }
        
        return epcGetOrder;
    }

    

    public EpcGetOrder getWarehouseOrders(String location) {
        String loc = StringHelper.trim(location);
        EpcGetOrder epcGetOrder = new EpcGetOrder();
        epcGetOrder.setCustId("");
        ArrayList<EpcOrderInfo> orderList = new ArrayList<EpcOrderInfo>();
        EpcOrderInfo epcOrderInfo = null;
        epcGetOrder.setOrders(orderList);
        ArrayList<EpcOrderQuoteInfo> epcOrderQuoteInfoList = null;
        EpcOrderQuoteInfo epcOrderQuoteInfo = null;
        ArrayList<EpcOrderCaseInfo> epcOrderCaseInfoList = null;
        EpcOrderCaseInfo epcOrderCaseInfo = null;
        ArrayList<EpcOrderItemInfo> epcOrderItemInfoList = null;
        EpcOrderItemInfo epcOrderItemInfo = null;
        ArrayList<EpcOrderItemInfo> tmpItemList2 = new ArrayList<EpcOrderItemInfo>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtQuote = null;
        PreparedStatement pstmtCase = null;
        PreparedStatement pstmtItem = null;
        PreparedStatement pstmtItemWarehouse = null;
        ResultSet rset = null;
        ResultSet rsetQuote = null;
        ResultSet rsetCase = null;
        ResultSet rsetItem = null;
        ResultSet rsetItemWarehouse = null;
        String sql = "";
        TreeMap<String, String> orderStatusMap = epcOrderHandler.getOrderStatusDescMap();
        String orderStatusDesc = "";
        EpcDeliveryInfo epcDeliveryInfo = null;
        TreeMap<String, EpcDeliveryDetail> itemDeliveryMap = null;
        EpcDeliveryDetail tmpEpcDeliveryDetail = null;
        int pfCnt = 0;
        ArrayList<EpcPayment> epcPaymentList = null;
        int currOrderId = 0;
        String tmpItemCat = "";
        String tmpItemId = "";
        BigDecimal tmpItemTotalCharge = null;
        BigDecimal tmpItemChargePaid = null;
        BigDecimal tmpItemRemainingCharge = null;
        
        
        try {
            if("".equals(loc)) {
                epcGetOrder.setStatus("FAIL");
                epcGetOrder.setErrorCode("1000");
                epcGetOrder.setErrorMessage("input is empty");
            } else {
            	conn = epcDataSource.getConnection();
            	
            	sql = "select quote_id, cpq_quote_guid " +
            	      "  from epc_order_quote " +
            		  " where order_id = ? ";
            	pstmtQuote = conn.prepareStatement(sql);
                
                sql = "select case_id, cpq_offer_desc, cpq_offer_desc_chi, cust_num, subr_num, " +
                      "       subr_key, activation_type, to_char(effective_date, 'yyyy-mm-dd hh24:mi') as eff_date " +
                      "  from epc_order_case " +
                      " where order_id = ? " +
                      "   and quote_id = ? ";
                pstmtCase = conn.prepareStatement(sql);
                
                sql = "select item_id, item_cat, item_code, cpq_item_desc, cpq_item_desc_chi, " +
                      "       cpq_item_value, warehouse, reserve_id, invoice_no, to_char(invoice_date, 'yyyy-mm-dd hh24:mi') as inv_date, " +
                	  "       parent_item_id, delivery_id, serial, stock_status, stock_status_desc " +
                      "  from epc_order_item " +
                      " where order_id = ? " +
                      "   and case_id = ? " +
                      "   and item_cat in (?,?,?,?) ";
                pstmtItem = conn.prepareStatement(sql);
                
                sql = "select item_id, item_cat, item_code, cpq_item_desc, cpq_item_desc_chi, " +
                      "       cpq_item_value, warehouse, reserve_id, invoice_no, to_char(invoice_date, 'yyyy-mm-dd hh24:mi') as inv_date, " +
                      "       parent_item_id, delivery_id, serial, stock_status " +
                      "  from epc_order_item a" +
                      " where order_id = ? " +
                      "   and item_cat in (?,?,?,?) " +
                      "   and exists ( " +
                      "     select 1 from epc_order_delivery bb " +
                      "      where bb.order_id = a.order_id " +
                      "        and a.delivery_id = bb.delivery_id " +
                      "        and bb.pickup_location = ? " +
                      "        and bb.status = ? " +
                      "   ) ";
                pstmtItemWarehouse = conn.prepareStatement(sql);

                
            	// warehouse cases
            	//  ... need to wait all items ready (except premium)
            	sql = "select order_id, to_char(order_date, 'yyyy-mm-dd hh24:mi:ss') as o_date, order_reference, order_status, total_charge_amount, " +
                      "       cust_id, contact_email, contact_no, place_order_channel " +
	                  "  from epc_order " +
	                  " where order_id in ( " +
	                  "   select aa.order_id " +
	                  "     from epc_order_item aa, epc_order_delivery bb, epc_order cc " +
	                  "    where bb.pickup_location = ? " +
	                  "      and bb.status = ? " +
	                  "      and aa.delivery_id = bb.delivery_id " +
	                  "      and cc.order_id = aa.order_id " +
	                  "      and cc.order_status = ? " +
	                  " ) " +
	                  " order by order_date ";
              	pstmt = conn.prepareStatement(sql);
              	pstmt.setString(1, loc); // pickup_location
              	pstmt.setString(2, "A"); // status - Active
              	pstmt.setString(3, "PF"); // order_status - PF
                rset = pstmt.executeQuery();
                while (rset.next()) {
                    currOrderId = rset.getInt("order_id");

                	// get delivery info (allow multiple)
                	epcDeliveryInfo = epcGetDeliveryInfoHandler.getPickupLocation(currOrderId);
                	itemDeliveryMap = new TreeMap<String, EpcDeliveryDetail>();
                	for(EpcDeliveryDetail d: epcDeliveryInfo.getDetails()) {
                		for(String itemId: d.getItems()) {
                			itemDeliveryMap.put(itemId, d);
                		}
                	}
                	// end of get delivery info (allow multiple)
                	
                	
                	// check items
                	tmpItemList2 = new ArrayList<EpcOrderItemInfo>(); // reset
                	
                	pstmtItemWarehouse.setInt(1, currOrderId); // order_id
                	pstmtItemWarehouse.setString(2, "SIM"); // item_cat
                	pstmtItemWarehouse.setString(3, "DEVICE"); // item_cat
                	pstmtItemWarehouse.setString(4, "SCREEN_REPLACE"); // item_cat
                	pstmtItemWarehouse.setString(5, "APPLECARE"); // item_cat
                	pstmtItemWarehouse.setString(6, loc); // pickup_location
                	pstmtItemWarehouse.setString(7, "A"); // status - Active
                	rsetItemWarehouse = pstmtItemWarehouse.executeQuery();
                	while(rsetItemWarehouse.next()) {
                		epcOrderItemInfo = new EpcOrderItemInfo();
                        epcOrderItemInfo.setItemId(StringHelper.trim(rsetItemWarehouse.getString("item_id")));
                        epcOrderItemInfo.setItemCat(StringHelper.trim(rsetItemWarehouse.getString("item_cat")));
                        epcOrderItemInfo.setItemCode(StringHelper.trim(rsetItemWarehouse.getString("item_code")));
                        epcOrderItemInfo.setWarehouse(StringHelper.trim(rsetItemWarehouse.getString("warehouse")));
                        epcOrderItemInfo.setReserveId(StringHelper.trim(rsetItemWarehouse.getString("reserve_id")));
                        epcOrderItemInfo.setSerial(StringHelper.trim(rsetItemWarehouse.getString("serial")));
                        epcOrderItemInfo.setStatus(StringHelper.trim(rsetItemWarehouse.getString("stock_status")));
                        
                      	tmpItemList2.add(epcOrderItemInfo);
                	} rsetItemWarehouse.close(); rsetItemWarehouse = null;
                	
                	// get and update reserve ticket status (will ignore items without reserve ticket)
// not needed anymore, read stock status from our own table, kerrytsang, 20211027
//                    epcStockHandler.getReserveStatus(tmpItemList2);
// end of not needed anymore, read stock status from our own table, kerrytsang, 20211027
                	
                    pfCnt = 0;
                    for(EpcOrderItemInfo e : tmpItemList2) {
                    	if(EpcItemCategory.SIM.equals(e.getItemCat())
                    	  || EpcItemCategory.SCREEN_REPLACE.equals(e.getItemCat())
                    	  || EpcItemCategory.APPLECARE.equals(e.getItemCat())
                    	) {
                    		// dummy products have stock forever !
                    		pfCnt++;
                    	} else if("PF".equals(e.getStatus())) {
                    		// other product
                    		pfCnt++;
                    	}
                    }
                    
                    if(pfCnt == tmpItemList2.size()) {
                    	// all stock under this order are ready, then proceed !
                    } else {
                    	// skip
                    	continue;
                    }
                    // end of check items
                	
                	
                	
                    epcOrderInfo = new EpcOrderInfo();
                    epcOrderInfo.setOrderId(currOrderId);
                    epcOrderInfo.setCustId(StringHelper.trim(rset.getString("cust_id")));
                    epcOrderInfo.setOrderDate(StringHelper.trim(rset.getString("o_date")));
                    epcOrderInfo.setOrderReference(StringHelper.trim(rset.getString("order_reference")));
                    epcOrderInfo.setOrderStatus(StringHelper.trim(rset.getString("order_status")));
                    epcOrderInfo.setTotalAmount(rset.getBigDecimal("total_charge_amount"));
                    epcOrderInfo.setEpcDeliveryDetailList(epcDeliveryInfo.getDetails()); // get delivery info too
                    epcOrderInfo.setContactEmail(StringHelper.trim(rset.getString("contact_email")));
                    epcOrderInfo.setContactNo(StringHelper.trim(rset.getString("contact_no")));
                    epcOrderInfo.setPlaceOrderChannel(StringHelper.trim(rset.getString("place_order_channel")));
                    
                    orderStatusDesc = StringHelper.trim(orderStatusMap.get(StringHelper.trim(rset.getString("order_status"))));
                    if("".equals(orderStatusDesc)) {
                        orderStatusDesc = "???";
                    }
                    epcOrderInfo.setOrderStatusDesc(orderStatusDesc);
                    
                    orderList.add(epcOrderInfo);

                    // get payment list
                    epcPaymentList = epcOrderHandler.getPaymentInfo(conn, rset.getInt("order_id"));
                    if(epcPaymentList == null) {
                        epcPaymentList = new ArrayList<>();
                    }
                    epcOrderInfo.setEpcPaymentList(epcPaymentList);
                    // end of get payment list
                    
                    
                    // get quote(s) under this order
                    epcOrderQuoteInfoList = new ArrayList<EpcOrderQuoteInfo>();
                    epcOrderInfo.setEpcOrderQuoteInfoList(epcOrderQuoteInfoList);
                    
                    pstmtQuote.setInt(1, currOrderId); // order_id
                    rsetQuote = pstmtQuote.executeQuery();
                    while(rsetQuote.next()) {
                    	epcOrderQuoteInfo = new EpcOrderQuoteInfo();
                    	epcOrderQuoteInfo.setQuoteId(rsetQuote.getInt("quote_id"));
                    	epcOrderQuoteInfo.setQuoteGuid(StringHelper.trim(rsetQuote.getString("cpq_quote_guid")));
                    	
                    	epcOrderQuoteInfoList.add(epcOrderQuoteInfo);
                    
                    
	                    // get case(s) under this quote
	                    epcOrderCaseInfoList = new ArrayList<EpcOrderCaseInfo>();
	                    epcOrderQuoteInfo.setEpcOrderCaseInfoList(epcOrderCaseInfoList);
	                    
	                    pstmtCase.setInt(1, currOrderId); // order_id
	                    pstmtCase.setInt(2, rsetQuote.getInt("quote_id")); // quote_id
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
	                        
	                        pstmtItem.setInt(1, currOrderId); // order_id
	                        pstmtItem.setString(2, StringHelper.trim(rsetCase.getString("case_id"))); // case_id
	                        pstmtItem.setString(3, "SIM"); // item_cat
	                        pstmtItem.setString(4, "DEVICE"); // item_cat
	                        pstmtItem.setString(5, "SCREEN_REPLACE"); // item_cat
	                        pstmtItem.setString(6, "APPLECARE"); // item_cat
	                        rsetItem = pstmtItem.executeQuery();
	                        while(rsetItem.next()) {
                                tmpItemId = StringHelper.trim(rsetItem.getString("item_id"));
                                tmpItemCat = StringHelper.trim(rsetItem.getString("item_cat"));

	                            epcOrderItemInfo = new EpcOrderItemInfo();
	                            epcOrderItemInfo.setItemId(tmpItemId);
	                            epcOrderItemInfo.setItemCat(tmpItemCat);
	                            epcOrderItemInfo.setItemCode(StringHelper.trim(rsetItem.getString("item_code")));
	                            epcOrderItemInfo.setItemDesc(StringHelper.trim(rsetItem.getString("cpq_item_desc")));
	                            epcOrderItemInfo.setItemDescChi(StringHelper.trim(rsetItem.getString("cpq_item_desc_chi")));
	                            epcOrderItemInfo.setItemValue(StringHelper.trim(rsetItem.getString("cpq_item_value")));
	                            epcOrderItemInfo.setWarehouse(StringHelper.trim(rsetItem.getString("warehouse")));
	                            epcOrderItemInfo.setReserveId(StringHelper.trim(rsetItem.getString("reserve_id")));
	                            epcOrderItemInfo.setInvoiceNo(StringHelper.trim(rsetItem.getString("invoice_no")));
	                            epcOrderItemInfo.setInvoiceDate(StringHelper.trim(rsetItem.getString("inv_date")));
	                            epcOrderItemInfo.setSerial(StringHelper.trim(rsetItem.getString("serial")));
	                            epcOrderItemInfo.setStatus(StringHelper.trim(rsetItem.getString("stock_status")));
	                            epcOrderItemInfo.setStatusDesc(StringHelper.trim(rsetItem.getString("stock_status_desc")));

                                if(EpcItemCategory.DEVICE.equals(tmpItemCat)) {
                                    tmpItemTotalCharge = epcPaymentHandler.getItemTotalCharge(conn, currOrderId, tmpItemId);
                                    tmpItemChargePaid = epcPaymentHandler.getItemChargePaid(conn, currOrderId, tmpItemId);
                                    tmpItemRemainingCharge = tmpItemTotalCharge.subtract(tmpItemChargePaid);

                                    epcOrderItemInfo.setTotalCharge(tmpItemTotalCharge);
                                    epcOrderItemInfo.setChargePaid(tmpItemChargePaid);
                                    epcOrderItemInfo.setRemainingCharge(tmpItemRemainingCharge);
                                } else if(EpcItemCategory.SIM.equals(tmpItemCat)
                                        || EpcItemCategory.GIFT_WRAPPING.equals(tmpItemCat)
                                        || EpcItemCategory.PLASTIC_BAG.equals(tmpItemCat) || EpcItemCategory.APPLECARE.equals(tmpItemCat)
                                       ) {
                                    tmpItemTotalCharge = epcPaymentHandler.getItemTotalCharge(conn, currOrderId, tmpItemId);

                                    epcOrderItemInfo.setTotalCharge(tmpItemTotalCharge);
                                    epcOrderItemInfo.setChargePaid(new BigDecimal(0));
                                    epcOrderItemInfo.setRemainingCharge(new BigDecimal(0));
                                } else {
                                    epcOrderItemInfo.setTotalCharge(new BigDecimal(0));
                                    epcOrderItemInfo.setChargePaid(new BigDecimal(0));
                                    epcOrderItemInfo.setRemainingCharge(new BigDecimal(0));
                                }
	                            
	                            if(EpcItemCategory.SCREEN_REPLACE.equals(epcOrderItemInfo.getItemCat()) || EpcItemCategory.APPLECARE.equals(epcOrderItemInfo.getItemCat())) {
	                            	epcOrderItemInfo.setParentItemId(StringHelper.trim(rsetItem.getString("parent_item_id")));
	                            } else {
	                            	epcOrderItemInfo.setParentItemId("");
	                            }
	                            
	                            epcOrderItemInfo.setDeliveryId(epcOrderItemInfo.getDeliveryId());
	                            tmpEpcDeliveryDetail = itemDeliveryMap.get(epcOrderItemInfo.getItemId());
	                            if(tmpEpcDeliveryDetail != null) {
	                            	epcOrderItemInfo.setDeliveryMethod(tmpEpcDeliveryDetail.getDeliveryMethod());
	                            	epcOrderItemInfo.setPickupLocation(tmpEpcDeliveryDetail.getPickupStore());
	                            } else {
	                            	epcOrderItemInfo.setDeliveryMethod("");
	                            	epcOrderItemInfo.setPickupLocation("");
	                            }

	                            
	                            epcOrderItemInfoList.add(epcOrderItemInfo);
	                        } rsetItem.close();
	                        rsetItem = null;
	                        
	                        epcOrderCaseInfo.setEpcOrderItemList(epcOrderItemInfoList);
	                        // end of get item(s) under this case
	                    } rsetCase.close(); 
	                    rsetCase = null;
	                    // end of get(s) case under this quote
                    } rsetQuote.close();
                    rsetQuote = null;
                    // end of get quote(s) under this order
                }

                
                epcGetOrder.setStatus("OK");
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcGetOrder.setStatus("FAIL");
            epcGetOrder.setErrorCode("1001");
            epcGetOrder.setErrorMessage(e.getMessage());
        } finally {
        	try { if(rset != null) { rset.close(); rset = null; } } catch (Exception ee) {}
        	try { if(pstmt != null) { pstmt.close(); pstmt = null; } } catch (Exception ee) {}
        	try { if(rsetQuote != null) { rsetQuote.close(); rsetQuote = null; } } catch (Exception ee) {}
        	try { if(pstmtQuote != null) { pstmtQuote.close(); pstmtQuote = null; } } catch (Exception ee) {}
        	try { if(rsetCase != null) { rsetCase.close(); rsetCase = null; } } catch (Exception ee) {}
        	try { if(pstmtCase != null) { pstmtCase.close(); pstmtCase = null; } } catch (Exception ee) {}
        	try { if(rsetItem != null) { rsetItem.close(); rsetItem = null; } } catch (Exception ee) {}
        	try { if(pstmtItem != null) { pstmtItem.close(); pstmtItem = null; } } catch (Exception ee) {}
        	
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return epcGetOrder;
    }
}
