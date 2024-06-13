package epc.epcsalesapi.sales;

import java.math.BigDecimal;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.catalogService.CatalogServiceHandler;
/**
 *
 * @author williamtam
 */
import epc.epcsalesapi.fes.sa.SaCodeHandler;
import epc.epcsalesapi.fes.sa.bean.ZzPservice;
import epc.epcsalesapi.helper.DBHelper;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.helper.sql.PstmtInputParameters;
import epc.epcsalesapi.sales.bean.EpcCharacteristicUse;
import epc.epcsalesapi.sales.bean.EpcCharge;
import epc.epcsalesapi.sales.bean.EpcChargeCtrl;
import epc.epcsalesapi.sales.bean.EpcConfiguredValue;
import epc.epcsalesapi.sales.bean.EpcControlTbl;
import epc.epcsalesapi.sales.bean.EpcCreatePayment;
import epc.epcsalesapi.sales.bean.EpcCreateReceipt;
import epc.epcsalesapi.sales.bean.EpcCreateReceiptResult;
import epc.epcsalesapi.sales.bean.EpcDefaultOfferPayment;
import epc.epcsalesapi.sales.bean.EpcDefaultPayment;
import epc.epcsalesapi.sales.bean.EpcDiscountCharge;
import epc.epcsalesapi.sales.bean.EpcDiscountChargeResult;
import epc.epcsalesapi.sales.bean.EpcExtensionCharge;
import epc.epcsalesapi.sales.bean.EpcExtensionChargeResult;
import epc.epcsalesapi.sales.bean.EpcGetCharge;
import epc.epcsalesapi.sales.bean.EpcGetChargeResult;
import epc.epcsalesapi.sales.bean.EpcGetCreditCardPrefix;
import epc.epcsalesapi.sales.bean.EpcGetCreditCardPrefixResult;
import epc.epcsalesapi.sales.bean.EpcGetDefaultPayment;
import epc.epcsalesapi.sales.bean.EpcGetDefaultPaymentResult;
import epc.epcsalesapi.sales.bean.EpcGetRemainingCharge;
import epc.epcsalesapi.sales.bean.EpcInstallmentCharge;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcLoginChannel;
import epc.epcsalesapi.sales.bean.EpcOfferCharge;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.EpcOrderItemDetail;
import epc.epcsalesapi.sales.bean.EpcOrderQuoteInfo;
import epc.epcsalesapi.sales.bean.EpcPayment;
import epc.epcsalesapi.sales.bean.EpcPaymentCode;
import epc.epcsalesapi.sales.bean.EpcPaymentCodeResult;
import epc.epcsalesapi.sales.bean.EpcPaymentCtrl;
import epc.epcsalesapi.sales.bean.EpcPaymentInfo;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.EpcQuoteProductCandidate;
import epc.epcsalesapi.sales.bean.EpcInitPayment;
import epc.epcsalesapi.sales.bean.EpcRequiredCardPrefix;
import epc.epcsalesapi.sales.bean.EpcRequiredPaymentCode;
import epc.epcsalesapi.sales.bean.EpcSalesActionType;
import epc.epcsalesapi.sales.bean.EpcSettleExtensionFee;
import epc.epcsalesapi.sales.bean.EpcSettlePayment;
import epc.epcsalesapi.sales.bean.EpcUpdatePayment;
import epc.epcsalesapi.sales.bean.EpcValidateItem;
import epc.epcsalesapi.sales.bean.EpcValidatePayment;
import epc.epcsalesapi.sales.bean.EpcValidatePaymentError;
import epc.epcsalesapi.sales.bean.EpcValidatePaymentResult;
import epc.epcsalesapi.sales.bean.EpcWaiveCharge;
import epc.epcsalesapi.sales.bean.EpcWaiveChargeResult;
import epc.epcsalesapi.stock.EpcStockHandler;
import epc.epcsalesapi.stock.bean.EpcProductDetail;
// added by Danny Chan on 2023-8-24 (filter payment code by location): start
import epc.epcsalesapi.fes.sa.SaControlHandler;
import epc.epcsalesapi.fes.sa.bean.ZzHardcodeCtrl;
// added by Danny Chan on 2023-8-24 (filter payment code by location): end
// added by Danny Chan on 2023-9-4: check salesman in validatePayment API: start
import epc.epcsalesapi.fes.user.FesUser;
import epc.epcsalesapi.fes.user.FesUserHandler;
// added by Danny Chan on 2023-9-4: check salesman in validatePayment API: end

@Service
public class EpcPaymentHandler {

    @Autowired
    private DataSource epcDataSource;

    @Autowired
    private DataSource fesDataSource;

    @Autowired
    private SaCodeHandler saCodeHandler;

    @Autowired
    private EpcControlHandler epcControlHandler;
	
    // added by Danny Chan on 2023-8-24 (filter payment code by location): start
    @Autowired
    private SaControlHandler saControlHandler;
    // added by Danny Chan on 2023-8-24 (filter payment code by location): end

    // added by Danny Chan on 2023-9-4: check salesman in validatePayment API - start
    @Autowired
    private FesUserHandler fesUserHandler;
    // added by Danny Chan on 2023-9-4: check salesman in validatePayment API - end

    @Autowired
    private EpcQuoteHandler epcQuoteHandler;

    @Autowired
    private EpcOrderHandler epcOrderHandler;

    @Autowired
    private EpcCustProfileHandler epcCustProfileHandler;

    @Autowired
    private EpcSecurityHelper epcSecurityHelper;

    @Autowired
    private EpcStockHandler epcStockHandler;

    @Autowired
    private EpcReceiptHandler epcReceiptHandler;

    @Autowired
    private EpcShkpHandler epcShkpHandler;

    @Autowired
    private EpcItemTemplateHandler epcItemTemplateHandler;

    @Autowired
    private EpcCourierChargeHandler epcCourierChargeHandler;
	
	
    private final Logger logger = LoggerFactory.getLogger(EpcPaymentHandler.class);

    // added by Danny Chan on 2022-11-10: start
    public EpcPaymentCodeResult getPaymentCodeResult(String locationCode) {     // modified by Danny Chan on 2022-10-11 (nehancement of payment page to support SHKP): add a new request parameter locationCode
    logger.info("locationCode = {}",locationCode);     // added by Danny Chan on 2022-10-11 (nehancement of payment page to support SHKP) 
        EpcPaymentCodeResult result = new EpcPaymentCodeResult();
        try {
            result.setPaymentCodeList(getPaymentCodeList(locationCode));     // modified by Danny Chan on 2022-10-11 (nehancement of payment page to support SHKP): add a new request parameter locationCode
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        }
        return result;
    }
    // added by Danny Chan on 2022-11-10: end
    
    public ArrayList<EpcPaymentCode> getPaymentCodeList(String locationCode) throws Exception {     // modified by Danny Chan on 2022-10-11 (enhancement of payment page to support SHKP): add a new request parameter locationCode

        // added by Danny Chan on 2022-10-11 (enhancement of payment page to support SHKP): start
        boolean showSHKPPaymentCode;
    
        if (locationCode==null) {
            showSHKPPaymentCode = true;
        } else {
            showSHKPPaymentCode = epcShkpHandler.isSHKShop(locationCode);
        }
    
        logger.info("locationCode = {}", locationCode);
        // added by Danny Chan on 2022-10-11 (enhancement of payment page to support SHKP): end
		
        // added by Danny Chan on 2024-1-22 (filter out readonly payment code): start
        ArrayList<EpcPaymentCtrl> epcPaymentCtrlList = null;
		
        Connection epcConn = null;

        try {
            epcConn = epcDataSource.getConnection();
            
            epcPaymentCtrlList = getPaymentCtrlList(epcConn);

        } catch (Exception e) {
            throw e;
        } finally {
            try { if (epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
        }
        // added by Danny Chan on 2024-1-22 (filter out readonly payment code): end
        
        ArrayList<EpcPaymentCode> paymentCodeList = new ArrayList<EpcPaymentCode>();
        EpcControlTbl enquiryControl;
        ArrayList<EpcControlTbl> resultControlList;
        try {
            List<ZzPservice> zzPserviceList = saCodeHandler.getAllPaymentCodes();
            for (int i=0; i < zzPserviceList.size(); i++) {
                if ("A".equals(zzPserviceList.get(i).getStatusInd())) {
                    EpcPaymentCode tmpPaymentCode = new EpcPaymentCode();
                    tmpPaymentCode.setPaymentCode(zzPserviceList.get(i).getServiceCode());
                    tmpPaymentCode.setPaymentCodeDesc(zzPserviceList.get(i).getServiceDesc());
                    tmpPaymentCode.setCreditCardType("Y".equals(zzPserviceList.get(i).getCreditCardType()));
                    tmpPaymentCode.setPaymentShortKey(zzPserviceList.get(i).getSettleShortCutKey());
					
                    // added by Danny Chan on 2023-8-24 (filter payment code by location): start
                    ZzHardcodeCtrl zzHardcodeCtrl = new ZzHardcodeCtrl();
                    zzHardcodeCtrl.setRecType("PYLOC");
                    zzHardcodeCtrl.setCode1( zzPserviceList.get(i).getServiceCode() );
                    
					ArrayList<ZzHardcodeCtrl> zzHardcodeControlList = saControlHandler.getControl(zzHardcodeCtrl);
					
					boolean isPaymentCodeAvailableForLocation = false;
					
                    if (zzHardcodeControlList.size()==0) {
                        isPaymentCodeAvailableForLocation = true;
                    } else {
                        isPaymentCodeAvailableForLocation = false;
						
                        for (int j=0; j<zzHardcodeControlList.size(); j++) {
                            ZzHardcodeCtrl ctrl = zzHardcodeControlList.get(j);
							
                            if (ctrl.getCode2().equals(locationCode)) {
                                isPaymentCodeAvailableForLocation = true;
                            }
                        }
                    }
					
                    // added by Danny Chan on 2023-8-24 (filter payment code by location): end
					
                    // added by Danny Chan on 2024-1-22 (filter out readonly payment code): start
                    boolean isReadOnlyPaymenyCode = false;
                    
                    for (int j=0; j<epcPaymentCtrlList.size(); j++) {
                        EpcPaymentCtrl paymentCtrl = epcPaymentCtrlList.get(j);
                        if ( paymentCtrl.getPaymentCode().equals(zzPserviceList.get(i).getServiceCode()) && 
                            paymentCtrl.isReadOnly() ) {
                            isReadOnlyPaymenyCode = true;
                        }
                    }
                    // added by Danny Chan on 2024-1-22 (filter out readonly payment code): end
                    
                    if ( (!zzPserviceList.get(i).getServiceCode().equals("SHKP") || showSHKPPaymentCode) && isPaymentCodeAvailableForLocation && !isReadOnlyPaymenyCode) {
                        paymentCodeList.add(tmpPaymentCode);
                    }
                }
            }

            enquiryControl = new EpcControlTbl();
            enquiryControl.setRecType("INSTALL_PAYMENT");
            resultControlList = epcControlHandler.getControl(enquiryControl);

            for (int i=0; i < paymentCodeList.size(); i++) {
                EpcPaymentCode tmpPaymentCode = paymentCodeList.get(i);
                for (int j=0; j < resultControlList.size(); j++) {
                    EpcControlTbl tmpEpcControlTbl = resultControlList.get(j);
                    if (tmpPaymentCode.getPaymentCode().equals(tmpEpcControlTbl.getKeyStr1())) {
                        tmpPaymentCode.setBankInstallment(true);
                        tmpPaymentCode.setCreditCardType(true);
                    }
                }
            }

            enquiryControl = new EpcControlTbl();
            enquiryControl.setRecType("MOBILE_PAYMENT");
            resultControlList = epcControlHandler.getControl(enquiryControl);

            for (int i=0; i < paymentCodeList.size(); i++) {
                EpcPaymentCode tmpPaymentCode = paymentCodeList.get(i);
                for (int j=0; j < resultControlList.size(); j++) {
                    EpcControlTbl tmpEpcControlTbl = resultControlList.get(j);
                    if (tmpPaymentCode.getPaymentCode().equals(tmpEpcControlTbl.getValueStr1())) {
                        tmpPaymentCode.setMobilePayment(true);
                    }
                }
            }
            
            enquiryControl = new EpcControlTbl();
            enquiryControl.setRecType("PAYMENT_DISABLE_REF");
            resultControlList = epcControlHandler.getControl(enquiryControl);

            for (int i=0; i < paymentCodeList.size(); i++) {
                EpcPaymentCode tmpPaymentCode = paymentCodeList.get(i);
                for (int j=0; j < resultControlList.size(); j++) {
                    EpcControlTbl tmpEpcControlTbl = resultControlList.get(j);
                    if (tmpPaymentCode.getPaymentCode().equals(tmpEpcControlTbl.getValueStr1())) {
                        tmpPaymentCode.setDisableReference(true);
                    }
                }
            }

        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw e;
        }
        return paymentCodeList;
    }


    public BigDecimal getItemDisCharge(int orderId, String itemId) {
        Connection epcConn = null;
        BigDecimal totalCharge = new BigDecimal(0);

        try {
            epcConn = epcDataSource.getConnection();
            totalCharge = getItemDisCharge(epcConn, orderId, itemId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
        }
        return totalCharge;
    }


    public BigDecimal getItemDisCharge(Connection epcConn, int orderId, String itemId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        BigDecimal disCharge = new BigDecimal(0);
        String iItemId = epcSecurityHelper.encode(StringHelper.trim(itemId));

        try {
            sql = "select sum(discount_amount) " +
                  "  from epc_order_charge " +
                  " where order_id = ? " +
                  "   and parent_item_id = ? " +
                  "   and charge_code not in (?) ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, iItemId); // parent_item_id
            pstmt.setString(3, "99"); // charge_code
            rset = pstmt.executeQuery();
            if(rset.next() && rset.getBigDecimal(1) != null) {
                disCharge = rset.getBigDecimal(1);
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
        }

        return disCharge;
    }


    public BigDecimal getItemTotalCharge(int orderId, String itemId) {
        Connection epcConn = null;
        BigDecimal totalCharge = new BigDecimal(0);

        try {
            epcConn = epcDataSource.getConnection();
            totalCharge = getItemTotalCharge(epcConn, orderId, itemId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
        }
        return totalCharge;
    }


    public BigDecimal getItemTotalCharge(Connection epcConn, int orderId, String itemId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        BigDecimal totalCharge = new BigDecimal(0);
        String iItemId = epcSecurityHelper.encode(StringHelper.trim(itemId));

        try {
            sql = "select sum(charge_amount) " +
                  "  from epc_order_charge " +
                  " where order_id = ? " +
                  "   and parent_item_id = ? " +
                  "   and charge_code not in (?) ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, iItemId); // parent_item_id
            pstmt.setString(3, "99"); // charge_code
            rset = pstmt.executeQuery();
            if(rset.next() && rset.getBigDecimal(1) != null) {
                totalCharge = rset.getBigDecimal(1);
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
        }

        return totalCharge;
    }


    public BigDecimal getItemChargePaid(Connection epcConn, int orderId, String itemId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        BigDecimal totalChargePaid = new BigDecimal(0);
        String iItemId = epcSecurityHelper.encode(StringHelper.trim(itemId));

        try {
            sql = "select sum(charge_amount) " +
                  "from epc_order_charge a " +
                  "where order_id = ? " +
                  "and parent_item_id = ? " +
                  "and paid = ? "+
                  "and ( " +
                  "        ( " +
                  "          charge_code IN (?) " +
                  "          and not exists ( " +
                  "              select 1 from epc_order_charge " +
                  "              where order_id = a.order_id " +
                  "              and parent_item_id = a.parent_item_id " +
                  "              and charge_code NOT IN (?) " +
                  "              and paid = ?)) " +
                  "         OR " +
                  "         charge_code NOT IN (?) " +
                  "    ) ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, iItemId); // parent_item_id
            pstmt.setString(3, "Y"); // paid
            pstmt.setString(4, "99"); // charge_code
            pstmt.setString(5, "99"); // charge_code
            pstmt.setString(6, "Y"); // paid
            pstmt.setString(7, "99"); // charge_code

            rset = pstmt.executeQuery();
            if(rset.next() && rset.getBigDecimal(1) != null) {
                totalChargePaid = rset.getBigDecimal(1);
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
        }

        return totalChargePaid;
    }

    public BigDecimal getNonItemTotalCharge(Connection epcConn, int orderId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        BigDecimal totalCharge = new BigDecimal(0);

        try {
            sql = "select sum(charge_amount) " +
                  "  from epc_order_charge " +
                  " where order_id = ? " +
                  "   and item_id is null";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if(rset.next() && rset.getBigDecimal(1) != null) {
                totalCharge = rset.getBigDecimal(1);
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
        }

        return totalCharge;
    }

    public BigDecimal getNonItemChargePaid(Connection epcConn, int orderId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        BigDecimal totalChargePaid = new BigDecimal(0);

        try {
            sql = "select sum(charge_amount) " +
                  "  from epc_order_charge " +
                  " where order_id = ? " +
                  "   and item_id is null " +
                  "   and paid = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, "Y"); // paid
            rset = pstmt.executeQuery();
            if(rset.next() && rset.getBigDecimal(1) != null) {
                totalChargePaid = rset.getBigDecimal(1);
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
        }

        return totalChargePaid;
    }

    public void getRemainingCharges(EpcGetRemainingCharge epcGetRemainingCharge) {
        Connection epcConn = null;

        try {
            epcConn = epcDataSource.getConnection();
            getRemainingCharges(epcConn, epcGetRemainingCharge);
        } catch (Exception e) {
            e.printStackTrace();

            epcGetRemainingCharge.setResult("FAIL");
            epcGetRemainingCharge.setErrMsg(e.getMessage());
        } finally {
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
        }
    }

    public void getRemainingCharges(Connection epcConn, EpcGetRemainingCharge epcGetRemainingCharge) {
        int iOrderId = epcGetRemainingCharge.getOrderId();
        ArrayList<EpcCharge> chargeList = epcGetRemainingCharge.getItems();
        String tmpItemId = "";
        BigDecimal totalCharge = new BigDecimal(0);
        epcGetRemainingCharge.setRemainingCharge(new BigDecimal(0));
        BigDecimal tmpTotalCharge = new BigDecimal(0);
        BigDecimal tmpChargePaid = new BigDecimal(0);
        BigDecimal tmpRemainingCharge = new BigDecimal(0);
        boolean isValid = true;
        String errMsg = "";
        String logStr = "[getRemainingCharges][orderId:" + iOrderId + "] ";

        try {
            // basic checking
            if(chargeList == null) {
                isValid = false;
                errMsg += "item list input is empty. ";
            } else if(chargeList.isEmpty()) {
                isValid = false;
                errMsg += "item list input is empty. ";
            }
            // end of basic checking

            if(isValid) {
                for(EpcCharge c : chargeList) {
                    tmpItemId = epcSecurityHelper.encode(StringHelper.trim(c.getItemId()));

                    tmpTotalCharge = getItemTotalCharge(epcConn, iOrderId, tmpItemId);
                    tmpChargePaid = getItemChargePaid(epcConn, iOrderId, tmpItemId);
                    tmpRemainingCharge = tmpTotalCharge.subtract(tmpChargePaid);

logger.info("{}{}:tmpTotalCharge:{},tmpChargePaid:{},tmpRemainingCharge:{}", logStr, tmpItemId, tmpTotalCharge, tmpChargePaid, tmpRemainingCharge);
                    c.setChargeAmount(tmpRemainingCharge);

                    if(tmpRemainingCharge.compareTo(new BigDecimal(0)) == 1) {
                        totalCharge = totalCharge.add(tmpRemainingCharge);
                    }
                }
logger.info("{}:totalCharge:{}", logStr, totalCharge);
                
                epcGetRemainingCharge.setResult("SUCCESS");
                epcGetRemainingCharge.setRemainingCharge(totalCharge);
            } else {
                epcGetRemainingCharge.setResult("FAIL");
                epcGetRemainingCharge.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcGetRemainingCharge.setResult("FAIL");
            epcGetRemainingCharge.setErrMsg(e.getMessage());
        } finally {
        }
    }


    public EpcGetChargeResult getChargeResult(EpcGetCharge epcGetCharge) {
        return getChargeResult(epcGetCharge, null);
    }


    public EpcGetChargeResult getChargeResult(EpcGetCharge epcGetCharge, EpcOrderInfo epcOrderInfo) {
        EpcGetChargeResult result = new EpcGetChargeResult();
        EpcQuote epcQuote = null;
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmtEpcOrder = null;
        PreparedStatement pstmtCharge = null;
        PreparedStatement pstmtCourier = null;
        PreparedStatement pstmtChargeAmount = null;
        PreparedStatement pstmtPayment = null;
        
        ResultSet rset = null;
        ResultSet rset2 = null;

        String sql = null;
        int orderId;
        String salesActionType;
        int quoteId;
        BigDecimal chargeAmount;
        String custId = "";
        String quoteGuid;
        String addressType;
        int[] seqId = {1};
        List<ZzPservice> zzPserviceList;
        HashMap<String, Object> contextData = null;
        ArrayList<HashMap<String, Object>> smcCasesList = null;
        EpcOfferCharge generalOfferCharge = new EpcOfferCharge();
        ArrayList<EpcCharge> generalChargeList = new ArrayList<EpcCharge>();
        ArrayList<EpcOfferCharge> offerChargeList = new ArrayList<EpcOfferCharge>();
        ArrayList<String> chargeCodeList = new ArrayList<String>();
        HashMap<String, ZzPservice> chargeCodeMap = new HashMap<String, ZzPservice>();
        HashMap<String, Object> smcCaseMap = null;
        HashMap<String, Object> smcCustInfoMap = null;
        boolean readOnly = false;
        ArrayList<EpcOrderItemDetail> epcItemList = null;
        ArrayList<EpcChargeCtrl> epcChargeCtrlList = null;
        ArrayList<EpcPaymentCtrl> epcPaymentCtrlList = null;
        EpcUpdatePayment epcUpdatePayment = new EpcUpdatePayment();
        String logStr = "[getChargeResult]";


        try {

            if (epcGetCharge.getOrderId() == null) {
                result.setResult("FAIL");
                result.setErrorCode("1001");
                result.setErrorMessage("Missing orderId");
                return result;
            }


            epcConn = epcDataSource.getConnection();
            epcConn.setAutoCommit(false);
            orderId = Integer.parseInt(epcGetCharge.getOrderId());
            salesActionType = StringHelper.trim(epcGetCharge.getSalesActionType());
            logStr += "[orderId:" + orderId + "] ";

            // get all reserved items
            epcItemList = epcOrderHandler.getAllDeviceItems(orderId, "FOR-DELIVERY");


            sql = "SELECT quote_id, cpq_quote_guid "+
                  "FROM epc_order_quote " +
                  "WHERE order_id = ? ";
            pstmt = epcConn.prepareStatement(
                sql,
                        ResultSet.TYPE_SCROLL_INSENSITIVE , 
                        ResultSet.CONCUR_UPDATABLE ,
                ResultSet.HOLD_CURSORS_OVER_COMMIT);

            sql = "DELETE FROM epc_order_charge " +
                  "WHERE order_id = ? ";
            pstmt2 = epcConn.prepareStatement(sql);

            sql = "INSERT INTO epc_order_charge (" +
                  "order_id, seq_id, quote_id, case_id, item_id, parent_item_id, item_guid, " +
                  "charge_desc, charge_desc_chi, charge_code, charge_amount, item_code,"+ 
                  "allow_installment, handling_fee_waive, msisdn, origin_charge_amount, " +
                  " need_to_pay, paid, waive_form_code, discount_form_code, catalog_item_desc, catalog_rrp ) " +
                  "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, " +
                  " ?, ?, ?, ?, ?, ? ) ";
            pstmtCharge = epcConn.prepareStatement(sql);

            sql = "SELECT cust_id, order_status FROM epc_order " +
                  "WHERE order_id = ? ";
            pstmtEpcOrder = epcConn.prepareStatement(sql);
            pstmtEpcOrder.setInt(1, orderId);
            rset = pstmtEpcOrder.executeQuery();
            if (rset.next()) {
                custId = rset.getString("cust_id");
                readOnly = !"I".equals(rset.getString("order_status"));
            }
            rset.close();

            // special for placeOrder, not to re-gen those charge, kerrytsang, 20220216
            if("Y".equals(StringHelper.trim(epcGetCharge.getFromPlaceOrder()))) {
                readOnly = true;
            }
            // end of special for placeOrder, not to re-gen those charge, kerrytsang, 20220216

            if (epcGetCharge.isSavePayment()) {
                readOnly = true;
            }
logger.info("{}{}{}", logStr, "readOnly:", readOnly);

            pstmt.setInt(1, orderId);
            rset = pstmt.executeQuery();
            if (!rset.next()) {
                result.setResult("FAIL");
                result.setErrorCode("1002");
                result.setErrorMessage("Invalid orderId");
                return result;
            } else {
                rset.beforeFirst();
            }

            // initialize charge type can be added in Sales flow
            // these charges are determined by sales flow
            EpcControlTbl chargeControl = new EpcControlTbl();
            chargeControl.setRecType("API_GEN_CHARGE");
            List<EpcControlTbl> epcChargeControlTblList = epcControlHandler.getControl(chargeControl);
            for (EpcControlTbl control:epcChargeControlTblList) {
                chargeCodeList.add(control.getValueStr1());
            }

            zzPserviceList = saCodeHandler.getCodes(chargeCodeList);
            for (ZzPservice tmpChargeCode: zzPserviceList) {
                chargeCodeMap.put(tmpChargeCode.getServiceCode(), tmpChargeCode);
            }
            
            // get charge control
            epcChargeCtrlList = getChargeCtrlList(epcConn);
            
            // get payment control
            epcPaymentCtrlList = getPaymentCtrlList(epcConn);
            
            // initialize epcUpdatePayment for payment to add
            epcUpdatePayment.setCustId(custId);
            epcUpdatePayment.setOrderId(orderId);
            epcUpdatePayment.setPaymentList(new ArrayList<EpcPayment>());

            if (!readOnly) {
                pstmt2.setInt(1, orderId);
                pstmt2.executeUpdate();
            }

            smcCasesList = epcCustProfileHandler.getTmpCustProfile(orderId, custId);

            while (rset.next()) {
                epcQuote = null; // reset

                quoteId = rset.getInt(1);
                quoteGuid = StringHelper.trim(rset.getString(2));
logger.info("{}{}{}{}{}", logStr, "quoteId:", quoteId, "quoteGuid:", quoteGuid);

                // add logic to get epcQuote from the caller instead of CPQ, kerrytsang, 20220812
                if(epcOrderInfo != null) {
                    for(EpcOrderQuoteInfo epcOrderQuoteInfo : epcOrderInfo.getEpcOrderQuoteInfoList()) {
                        if(epcOrderQuoteInfo != null && quoteGuid.equals(epcOrderQuoteInfo.getQuoteGuid())) {
                            epcQuote = epcOrderQuoteInfo.getEpcQuote();
                            break;
                        }
                    }
                }

                if(epcQuote == null) {
logger.info("{}{}", logStr, "get quote content from EPC db");
                    epcQuote = epcOrderHandler.getCPQQuoteInEpc(orderId, quoteId); // get from the dump in EPC db first
                }
                
                if(epcQuote == null) {
logger.info("{}{}", logStr, "get quote content from CPQ");
                    epcQuote = epcQuoteHandler.getQuoteInfo(quoteGuid); // if nothing is found, then get from CPQ
                }
                // end of add logic to get epcQuote from the caller instead of CPQ, kerrytsang, 20220812
                
                contextData = epcQuote.getContextData();
                EpcQuoteItem[] epcQuoteItems = epcQuote.getItems();

                for (int i=0; i < epcQuoteItems.length; i++) {
                    EpcOfferCharge offerCharge = new EpcOfferCharge();
                    ArrayList<EpcCharge> chargeList = new ArrayList<EpcCharge>();
                    offerCharge.setCaseId(epcQuoteItems[i].getProductCandidateObj().getId());
                    offerCharge.setOfferDescription(epcQuoteItems[i].getProductCandidateObj().getCpqItemDesc());
                    offerCharge.setChargeList(chargeList);
                    offerChargeList.add(offerCharge);
                    createQuoteChargeList(orderId, seqId, quoteId, epcQuoteItems[i].getProductCandidateObj().getId(), epcQuoteItems[i].getProductCandidateObj().getId(), null, null, null,
                        offerCharge, readOnly, epcQuoteItems[i].getProductCandidateObj(), epcQuoteItems[i].getMetaDataLookup(), contextData, pstmtCharge,
                        epcItemList, epcChargeCtrlList, epcPaymentCtrlList, chargeCodeMap, epcUpdatePayment);


                    // Add non-HKID deposit
                    if (smcCasesList != null && !readOnly) {
                        for (int m = 0; m < smcCasesList.size(); m++) {
                            smcCaseMap = smcCasesList.get(m);
                            smcCustInfoMap = (HashMap<String, Object>) smcCaseMap.get("SMCCustInfo");
                            if (smcCustInfoMap != null) {
                                if ("NEW".equals((String) smcCustInfoMap.get("SMCActivationType")) && "P".equals((String) smcCustInfoMap.get("SMCIdType"))) {
                                    //if ("NEW".equals((String) smcCustInfoMap.get("SMCActivationType"))) {
                                    if (epcQuoteItems[i].getProductCandidateObj().getId().equals(smcCaseMap.get("SMCCaseId"))) {
                                        addTableCharge(orderId, seqId, quoteId, (String)smcCaseMap.get("SMCCaseId"), null, null, null,
                                            chargeCodeMap.get("034").getReceiptSrvDesc(), chargeCodeMap.get("034").getReceiptSrvDescChi(), "034", chargeCodeMap.get("034").getPrice(),
                                            null, "N", "N", 
                                            offerCharge.getMsisdn(), "Y", "N", null, null, null,
                                            pstmtCharge
                                        );
                                    }
                                }
                            }
                        }
                    }

                    if (epcGetCharge.getPaymentList() != null && !readOnly) {
                        for (EpcPayment payment:epcGetCharge.getPaymentList()) {
                            if (payment.getPaymentTo() != null && payment.getCaseId() != null) {
                                if ("Ledger".equals(payment.getPaymentTo()) && payment.getCaseId().equals(offerCharge.getCaseId())) {
                                    addTableCharge(orderId, seqId, quoteId, payment.getCaseId(), null, null, null,
                                        chargeCodeMap.get("01").getReceiptSrvDesc(), chargeCodeMap.get("01").getReceiptSrvDescChi(), "01", payment.getPaymentAmount(),
                                        null, "N", "N",
                                        offerCharge.getMsisdn(), "Y", "N", null, null, null,
                                        pstmtCharge
                                    );
                                }
                            }
                        }
                    }
                }
            }
            rset.close();

            if (!readOnly) {
                updatePaymentInfo(epcConn, epcUpdatePayment);
            }

            // initialize general charge since it is not related to offer
            generalOfferCharge.setCaseId("");
            generalOfferCharge.setMsisdn("");
            generalOfferCharge.setOfferDescription("General Charge");
            generalOfferCharge.setChargeList(generalChargeList);
            offerChargeList.add(generalOfferCharge);

            // Add courier charge
            if (/*epcGetCharge.getPaymentList() != null &&*/ !readOnly) {   // modified by Danny Chan on 2023-1-20 (by-pass checking of existence of the array paymentList for creation of courier charge)

// modified to follow 2022 courier charge formula, kerrytsang, 20231025
//
//                sql = "SELECT delivery_id, addr_type FROM epc_order_delivery " +
//                      "WHERE status = ? " +
//                      "AND order_id = ? " +
//                      "AND delivery_method = ? ";
//                pstmtCourier = epcConn.prepareStatement(sql);
//                pstmtCourier.setString(1, "A");
//                pstmtCourier.setInt(2, orderId);
//                pstmtCourier.setString(3, "COURIER");
//                rset = pstmtCourier.executeQuery();
//
//                while (rset.next()) {
//                    addressType = rset.getString("addr_type");
//                    sql = "SELECT NVL(SUM(charge_amount), 0) total_amount FROM epc_order_charge " +
//                          "WHERE order_id = ? " +
//                          "AND item_id IS NOT NULL ";
//                    pstmtChargeAmount = epcConn.prepareStatement(sql);
//                    pstmtChargeAmount.setInt(1, orderId);
//                    rset2 = pstmtChargeAmount.executeQuery();
//
//                    if (rset2.next()) {
//                        chargeAmount = rset2.getBigDecimal("total_amount");
//                        sql = "SELECT * FROM epc_control_tbl " +
//                              "WHERE rec_type = ? " +
//                              "AND key_str1 = ? " +
//                              "AND ? BETWEEN key_number1 AND key_number2 ";
//                        PstmtInputParameters inputParameters2 = new PstmtInputParameters();
//                        inputParameters2.setParameter(1, "COURIER_CHARGE");
//                        inputParameters2.setParameter(2, addressType);
//                        inputParameters2.setParameter(3, chargeAmount);
//                        List<EpcControlTbl> epcControlTblList2 = epcControlHandler.getControl(sql, inputParameters2);
//
//                        for (EpcControlTbl control:epcControlTblList2) {
//                            addTableCharge(orderId, seqId, 0, null, null, null, null,
//                                chargeCodeMap.get("E96").getReceiptSrvDesc(), chargeCodeMap.get("E96").getReceiptSrvDescChi(), "E96", control.getValueNumber1(),
//                                null, "N", "N",
//                                null, "Y", "N", null, null, null,
//                                pstmtCharge
//                            );
//                        }
//                    }
//                    rset2.close();
//                }
//                rset.close();

                BigDecimal courierCharge = epcCourierChargeHandler.calculateCourierCharge(epcConn, orderId, "");
                if(courierCharge.compareTo(new BigDecimal(0)) == 1) {
                    // courier charge > 0
                    addTableCharge(orderId, seqId, 0, null, null, null, null,
                        chargeCodeMap.get("E96").getReceiptSrvDesc(), chargeCodeMap.get("E96").getReceiptSrvDescChi(), "E96", courierCharge,
                        null, "N", "N",
                        null, "Y", "N", null, null, null,
                        pstmtCharge
                    );
                }
// end of modified to follow 2022 courier charge formula, kerrytsang, 20231025
            }

            // Add installment handling charge
            /*
            if (epcGetCharge.getPaymentList() != null && !readOnly) {
                 sql = "SELECT * FROM epc_control_tbl " +
                       "WHERE rec_type = ? " +
                       "AND key_str1 = ? " +
                       "AND ? BETWEEN key_number1 AND key_number2 ";
                for (EpcPayment payment:epcGetCharge.getPaymentList()) {
                    if ("".equals(StringHelper.trim(payment.getCaseId()))) {
                        PstmtInputParameters inputParameters = new PstmtInputParameters();
                        inputParameters.setParameter(1, "INSTALL_HANDLING_FEE");
                        inputParameters.setParameter(2, payment.getPaymentCode());
                        inputParameters.setParameter(3, payment.getPaymentAmount());
                        List<EpcControlTbl> epcControlTblList = epcControlHandler.getControl(sql, inputParameters);

                        for (EpcControlTbl control:epcControlTblList) {
                            addTableCharge(orderId, seqId, 0, null, null, null, null,
                                          chargeCodeMap.get(control.getValueStr1()).getReceiptSrvDesc(), chargeCodeMap.get(control.getValueStr1()).getReceiptSrvDescChi(), control.getValueStr1(), chargeCodeMap.get(control.getValueStr1()).getPrice(), 
                                          null, "N", "N",
                                          null, "Y", "N", null,
                                          pstmtCharge
                                         );
                        }
                    }
                }
            }
             */
            
            // Modified by William Tam on 18 Oct 2023 that only payment page can trigger new instalment handling fee
            //if (epcGetCharge.getPaymentList() != null && epcGetCharge.isSavePayment()) {
            if (epcGetCharge.getPaymentList() != null && !"".equals(salesActionType)) {
            // End of Modified by William Tam that only payment page can trigger new instalment handling fee
                seqId[0] = getOrderMaxSeqId(orderId, epcConn) + 1;
                boolean isHandlingChargeCreated = false;

                sql = "SELECT * FROM epc_control_tbl " +
                        "WHERE rec_type = ? " +
                        "AND key_str1 = ? " +
                        "AND key_str2 = ? " +
                        "AND ? BETWEEN key_number1 AND key_number2 ";

                ArrayList<EpcCharge> installHandlingChargeList = getInstallHandlingChargeList(orderId, epcConn);
                for (EpcPayment payment:epcGetCharge.getPaymentList()) {
                    PstmtInputParameters inputParameters = new PstmtInputParameters();
                    inputParameters.setParameter(1, "INSTALL_HANDLING_FEE");
                    inputParameters.setParameter(2, payment.getPaymentCode());
                    inputParameters.setParameter(3, "".equals(StringHelper.trim(payment.getCaseId()))?"N":"Y");
                    inputParameters.setParameter(4, payment.getPaymentAmount());
                    List<EpcControlTbl> epcControlTblList = epcControlHandler.getControl(sql, inputParameters);
                    if (epcControlTblList.size() == 0) {
                        continue;
                    }
                    isHandlingChargeCreated = false;
                    for (EpcCharge installHandlingCharge:installHandlingChargeList) {
                        if(installHandlingCharge.getCaseId().equals(payment.getCaseId())) {
                            if (installHandlingCharge.getChargeCode().equals(epcControlTblList.get(0).getValueStr1())) {
                                installHandlingChargeList.remove(installHandlingCharge);
                                isHandlingChargeCreated = true;
                                break;
                            }
                        }
                    }
                    if (!isHandlingChargeCreated) {
                        addTableCharge(orderId, seqId, 0, null, null, null, null,
                            chargeCodeMap.get(epcControlTblList.get(0).getValueStr1()).getReceiptSrvDesc(), chargeCodeMap.get(epcControlTblList.get(0).getValueStr1()).getReceiptSrvDescChi(),
                            epcControlTblList.get(0).getValueStr1(), chargeCodeMap.get(epcControlTblList.get(0).getValueStr1()).getPrice(),
                            null, "N", "N",
                            null, "Y", "N", null, null, null,
                            pstmtCharge
                        );
                    }
                }

                for (EpcCharge installHandlingCharge:installHandlingChargeList) {
                    deleteTableCharge(orderId, installHandlingCharge.getSeqId(), epcConn);
                }
                refreshTableSeqId(orderId, epcConn);
            }


// migrate coupon to payment, kerrytsang, 20230117
//            // generate COUP charge from epc_order_voucher, kerrytsang, 20221230
//            if (!readOnly) {
//                generateCouponCharge(orderId, epcConn);
//            }
//            // end of generate COUP charge from epc_order_voucher, kerrytsang, 20221230
// end of migrate coupon to payment, kerrytsang, 20230117


            if (!readOnly) {
                updateTableCharge(orderId, offerChargeList, epcConn, pstmtCharge, seqId, chargeCodeMap);
            }

            offerChargeList = outputOfferChargeList(orderId, offerChargeList, epcConn, "Y".equals(epcGetCharge.getFromPlaceOrder()), salesActionType);

            epcConn.commit();
            result.setResult("SUCCESS");
            result.setErrorCode("");
            result.setErrorMessage("");
            result.setOfferChargeList(offerChargeList);
        
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            try { if(epcConn != null) { epcConn.rollback(); } } catch (Exception ignore) {}
            result.setResult("FAIL");
            result.setErrorCode("1003");
            result.setErrorMessage("System Error");
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if (pstmt2 != null) { pstmt2.close(); } } catch (Exception ignore) {}
            try { if (pstmtCourier != null) { pstmtCourier.close(); } } catch (Exception ignore) {}
            try { if (pstmtCharge != null) { pstmtCharge.close(); } } catch (Exception ignore) {}
            try { if (pstmtChargeAmount != null) { pstmtChargeAmount.close(); } } catch (Exception ignore) {}        
            try { if (epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
                }
        return result;

    }

    public void createQuoteChargeList(int smcOrderId, int[] seqId, int smcQuoteId, String smcCaseId, String parentItemId, String parentItemCode,  String parentItemDesc,
        String msisdn, EpcOfferCharge offerCharge, boolean readOnly, EpcQuoteProductCandidate productCandidateObj,
        HashMap<String, Object> metaDataLookup, HashMap<String, Object> contextData, PreparedStatement pstmtCharge,
        ArrayList<EpcOrderItemDetail> epcItemList, ArrayList<EpcChargeCtrl> epcChargeCtrlList, ArrayList<EpcPaymentCtrl> epcPaymentCtrlList, HashMap<String, ZzPservice> chargeCodeMap, EpcUpdatePayment epcUpdatePayment) throws Exception {

        String chargeCode = "";
        String allowInstallment = "N";
        String handlingFeeWaive = "N";
        BigDecimal depositChargeAmount = null;
        String paid = "N";
        String needToPay = "Y";
        String chargeDesc = null;
        String chargeDescChi = null;
        String discountFormCode = null;
        EpcProductDetail epcProductDetail = null;
        HashMap<String, Object> childMap = null;
        String typePath = null;
        String entityTemplate = null;
        String entityType = null;
        String catalogItemDesc = null;
        BigDecimal catalogRrp = null;
        String paymentCode = null;

        try {

            childMap = (HashMap<String, Object>)metaDataLookup.get(productCandidateObj.getId());
            typePath = StringHelper.trim((String)childMap.get("typePath"));
            if (!"".equals(typePath) && typePath.split("[/]").length >= 2) {
                entityTemplate = typePath.split("[/]")[0];
                entityType = typePath.split("[/]")[1];
            }

            for (int i=0; i < productCandidateObj.getCharacteristicUse().size(); i++) {
                EpcCharacteristicUse epcCharacteristicUse = productCandidateObj.getCharacteristicUse().get(i);
                if ("VAS_Label".equals(epcCharacteristicUse.getName())) {
                    /*
                    parentItemCode = "";
                    for (int j=0; j < epcCharacteristicUse.getValue().size(); j++) {
                        parentItemCode += epcCharacteristicUse.getValue().get(j) + "|";
                    }
                     */
                    parentItemCode = (String) epcCharacteristicUse.getValue().stream().collect(Collectors.joining("|"));
                    break;
                } else if ("rmsWithRequiredVASSubscribed".equals(epcCharacteristicUse.getName())) {
                    /*
                    parentItemCode = "";
                    for (int j=0; j < epcCharacteristicUse.getValue().size(); j++) {
                        parentItemCode += epcCharacteristicUse.getValue().get(j) + "|";
                    }
                     */
                    parentItemCode = (String) epcCharacteristicUse.getValue().stream().collect(Collectors.joining("|"));
                    break;
                } else if ("rmsNatureOfRebate".equals(epcCharacteristicUse.getName())) {
                    String rebateNature = (String) epcCharacteristicUse.getValue().get(0);
                    if ("Admin Fee Rebate (PnL Related)".equals(rebateNature)) {
                        parentItemCode = "ADMIN";
                    } else if ("Service Fee Rebate (PnL Related)".equals(rebateNature)) {
                        parentItemCode = "PLAN";
                    }
                    break;
                }
            }

            if ("Package".equals(entityType)) {
                ArrayList<EpcConfiguredValue> epcConfiguredValue = productCandidateObj.getConfiguredValue();
                for (int i=0; i < epcConfiguredValue.size(); i++) {
                    System.out.println("epcConfiguredValue Name:" + epcConfiguredValue.get(i).getName());
                    if("MSISDN1".equals(epcConfiguredValue.get(i).getName())) {
                        offerCharge.setMsisdn(epcConfiguredValue.get(i).getValue());
                        msisdn = epcConfiguredValue.get(i).getValue();
                    }
                    if("Net_Handset_Discount_Offer_Specification".equals(entityTemplate) && "Offer Case ID".equals(epcConfiguredValue.get(i).getName()) && epcConfiguredValue.get(i).getValue() != null) {
                        smcCaseId = epcConfiguredValue.get(i).getValue();
                    }
                }
            }

            if (msisdn == null) {
                if ("Customer Profile".equals(productCandidateObj.getCpqItemDesc())) {
                    ArrayList<EpcConfiguredValue> epcConfiguredValue = productCandidateObj.getConfiguredValue();
                    for (int i=0; i < epcConfiguredValue.size(); i++) {
                        if("MSISDN".equals(epcConfiguredValue.get(i).getName())) {
                            offerCharge.setMsisdn(epcConfiguredValue.get(i).getValue());
                        }
                    }
                }
            }

            if (msisdn == null) {
                if ("Mobile Product Spec".equals(productCandidateObj.getCpqItemDesc())) {
                    ArrayList<EpcConfiguredValue> epcConfiguredValue = productCandidateObj.getConfiguredValue();
                    for (int i=0; i < epcConfiguredValue.size(); i++) {
                        if("MSISDN".equals(epcConfiguredValue.get(i).getName())) {
                            offerCharge.setMsisdn(epcConfiguredValue.get(i).getValue());
                            msisdn = epcConfiguredValue.get(i).getValue();
                        }
                    }
                }
            }

            if (msisdn == null) {
                if ("Child Mobile Product Spec".equals(productCandidateObj.getCpqItemDesc())) {
                    ArrayList<EpcConfiguredValue> epcConfiguredValue = productCandidateObj.getConfiguredValue();
                    for (int i=0; i < epcConfiguredValue.size(); i++) {
                        if("MSISDN".equals(epcConfiguredValue.get(i).getName())) {
                            msisdn = epcConfiguredValue.get(i).getValue();
                        }
                    }
                }
            }
            
            if(EpcItemCategory.REQUIRED_PAYMENT.equals(productCandidateObj.getItemCat())) {
                if (productCandidateObj.getItemCode() != null) {
                    int index = epcPaymentCtrlList.indexOf(new EpcPaymentCtrl(productCandidateObj.getItemCode()));
                    if (index > 0) {
                        if (epcPaymentCtrlList.get(index).isDefaultPayment()) {
                        BigDecimal totalAmountByCaseId = getTotalAmountByCaseId(smcOrderId, smcCaseId);
                            if (totalAmountByCaseId.compareTo(BigDecimal.ZERO) > 0) {
                                EpcPayment epcPayment = new EpcPayment();
                                epcPayment.setPaymentCode(productCandidateObj.getItemCode());
                                epcPayment.setPaymentAmount(totalAmountByCaseId);
                                epcPayment.setExchangeRate(new BigDecimal(1));
                                epcPayment.setCurrencyCode("HKD");
                                epcPayment.setCurrencyAmount(new BigDecimal(0));
                                epcPayment.setCaseId(smcCaseId);
                                epcUpdatePayment.getPaymentList().add(epcPayment);
                            }
                        }
                    }
                }
            }

            if(EpcItemCategory.CHARGE.equals(productCandidateObj.getItemCat())) {

                if(productCandidateObj.getItemCharge() != null && productCandidateObj.getItemCharge().compareTo(BigDecimal.ZERO) != 0) {

                    if (typePath.matches(".*[/]Non_Recurring_Charge[/].*")) {
                        chargeCode = "";
                        discountFormCode = null;
                        chargeDesc = productCandidateObj.getCpqItemDesc();
                        chargeDescChi = productCandidateObj.getCpqItemDescChi();
                        catalogItemDesc = productCandidateObj.getCpqItemDesc();
                        EpcChargeCtrl epcChargeCtrl = null;
                        
                        // check the proceeding charge matches condition with charge control, if so, apply the control
                        for (EpcChargeCtrl tmpEpcChargeCtrl: epcChargeCtrlList) {
                            
                            if (tmpEpcChargeCtrl.getChargeCpqDesc().equals(productCandidateObj.getCpqItemDesc()) && tmpEpcChargeCtrl.getCheckParentItemDesc().equals(parentItemDesc) ) {
                                epcChargeCtrl = tmpEpcChargeCtrl;
                                break;
                            }
                        }

                        if (epcChargeCtrl == null) {
                            for (EpcChargeCtrl tmpEpcChargeCtrl: epcChargeCtrlList) {
                                if (tmpEpcChargeCtrl.getChargeCpqDesc().equals(productCandidateObj.getCpqItemDesc()) && tmpEpcChargeCtrl.getCheckParentItemDesc().equals("")) {
                                    epcChargeCtrl = tmpEpcChargeCtrl;
                                    break;
                                }
                            }
                        }

                        if (epcChargeCtrl != null) {
                            chargeCode = epcChargeCtrl.getChargeCode();
                            discountFormCode = epcChargeCtrl.getDiscountFormCode();
                            paymentCode = epcChargeCtrl.getPaymentCode();
                            
                            if ("PLAN".equals(epcChargeCtrl.getCheckParentItemCode())) {
                                parentItemCode = "PLAN";
                            } else if ("ADMIN".equals(epcChargeCtrl.getCheckParentItemCode())) {
                                parentItemCode = "ADMIN";
                            } else if ("PARENT".equals(epcChargeCtrl.getCheckParentItemCode())) {
                                // nothing to do
                            } else if ("SELF".equals(epcChargeCtrl.getCheckParentItemCode())) {
                                parentItemCode = productCandidateObj.getItemCode();
                            //} else if ("TRADEIN".equals(epcChargeCtrl.getCheckParentItemCode())) {
                            //    parentItemCode = "TRADEIN";
                            } else if ("DISCOUNT".equals(epcChargeCtrl.getCheckParentItemCode())) {
                                parentItemCode = "DISCOUNT";
                            }

                            if (epcChargeCtrl.isDisplayConfigDesc()) {
                                chargeDesc = epcChargeCtrl.getConfigDesc();
                                chargeDescChi = epcChargeCtrl.getConfigDesc();
                            } else if (epcChargeCtrl.isDisplayParentDesc()) {
                                for(EpcOrderItemDetail itemDetail : epcItemList) {
                                    if (itemDetail.getItemId().equals(parentItemId)) {
                                        chargeDesc = itemDetail.getCpqItemDesc();
                                        chargeDescChi = itemDetail.getCpqItemDescChi(); 
                                        break;
                                    }
                                }
                            } else if (epcChargeCtrl.isDisplayProductDesc()) {
                                epcProductDetail = epcStockHandler.getProductDetail(parentItemCode);
                                chargeDesc = epcProductDetail.getProductDesc();
                                chargeDescChi = epcProductDetail.getProductDesc();
                            } else if (epcChargeCtrl.isDisplayChargeDesc()) {
                                chargeDesc = chargeCodeMap.get(epcChargeCtrl.getChargeCode()).getReceiptSrvDesc();
                                chargeDescChi = chargeCodeMap.get(epcChargeCtrl.getChargeCode()).getReceiptSrvDescChi();
                            }
                            
                            if ("RRP".equals(epcChargeCtrl.getCatalogRrp())) {
                                catalogRrp = productCandidateObj.getCatalogRrp();
                            } else if ("RATE".equals(epcChargeCtrl.getCatalogRrp())) {
                                catalogRrp = productCandidateObj.getItemCharge();
                            }
                            
                        } else {
                            return;
                        }

                        if ("5f9b8a30-6832-4ed0-93c1-e772ce006e16".equals(productCandidateObj.getItemCode2())) {
                            allowInstallment = "Y";
                            handlingFeeWaive = "Y";
                        } else if ("f59771cc-0766-4cc8-b812-ed5581ab7c7f".equals(productCandidateObj.getItemCode2())) {
                            allowInstallment = "Y";
                            handlingFeeWaive = "N";
                        } else {
                            allowInstallment = "N";
                            handlingFeeWaive = "N";
                        }

                        if (!readOnly) {
                            if("02".equals(chargeCode)) {
                                // check whether need to create deposit charge (99)
                                //  search item list to get parent entity
                                for(EpcOrderItemDetail itemDetail : epcItemList) {
                                    if(itemDetail.getItemId().equals(parentItemId)) {
                                        if("NO_STOCK".equals(itemDetail.getReserveId()) && !"Accessory_Combo_Offer_Specification".equals(epcItemTemplateHandler.getPackageTemplate(smcOrderId, itemDetail.getItemId()))) {
                                            if("AA".equals(itemDetail.getWarehouse())) {
                                                if(new BigDecimal(100).compareTo(productCandidateObj.getItemCharge()) == 1) {
                                                    // if $100 > item charge, then charge item charge amount
                                                    depositChargeAmount = productCandidateObj.getItemCharge();
                                                } else {
                                                    // charge $100
                                                    depositChargeAmount = new BigDecimal(100);
                                                }
                                            } else if("AH".equals(itemDetail.getWarehouse())) {
                                                if(new BigDecimal(500).compareTo(productCandidateObj.getItemCharge()) == 1) {
                                                    // if $500 > item charge, then charge item charge amount
                                                    depositChargeAmount = productCandidateObj.getItemCharge();
                                                } else {
                                                    // charge $500
                                                    depositChargeAmount = new BigDecimal(500);
                                                }
                                            } else {
                                                depositChargeAmount = new BigDecimal(0);
                                            }

                                            // deposit
                                            needToPay = "Y";
                                            addTableCharge(
                                                smcOrderId, seqId, smcQuoteId, smcCaseId, productCandidateObj.getId(), parentItemId, productCandidateObj.getEntityID(),
                                                "Reservation Deposit", "Reservation Deposit", "99", depositChargeAmount,
                                                parentItemCode, allowInstallment, handlingFeeWaive,
                                                msisdn, needToPay, paid, null, null, null,
                                                pstmtCharge
                                            );
                                            // end of deposit

                                            // original charge
                                            needToPay = "N";
                                            addTableCharge(
                                                smcOrderId, seqId, smcQuoteId, smcCaseId, productCandidateObj.getId(), parentItemId, productCandidateObj.getEntityID(),
                                                chargeDesc, chargeDescChi, chargeCode, productCandidateObj.getItemCharge(),
                                                parentItemCode, allowInstallment, handlingFeeWaive,
                                                msisdn, needToPay, paid, discountFormCode, catalogItemDesc, catalogRrp,
                                                pstmtCharge
                                            );
                                            // end of original charge
                                        } else {
                                            needToPay = "Y";

                                            // original charge
                                            addTableCharge(
                                                smcOrderId, seqId, smcQuoteId, smcCaseId, productCandidateObj.getId(), parentItemId, productCandidateObj.getEntityID(),
                                                chargeDesc, chargeDescChi, chargeCode, productCandidateObj.getItemCharge(),
                                                parentItemCode, allowInstallment, handlingFeeWaive,
                                                msisdn, needToPay, paid, discountFormCode, catalogItemDesc, catalogRrp,
                                                pstmtCharge
                                            );
                                            // end of original charge
                                        }
                                        break;
                                    }
                                }
                            } else if (!"".equals(paymentCode)) {
                                EpcPayment epcPayment = new EpcPayment();
                                epcPayment.setPaymentCode(paymentCode);
                                epcPayment.setPaymentAmount(productCandidateObj.getItemCharge().abs());
                                epcPayment.setReference1(parentItemCode);
                                epcPayment.setExchangeRate(new BigDecimal(1));
                                epcPayment.setCurrencyCode("HKD");
                                epcPayment.setCurrencyAmount(new BigDecimal(0));
                                epcPayment.setCaseId(smcCaseId);
                                epcUpdatePayment.getPaymentList().add(epcPayment);
                            } else {
                                needToPay = "Y";

                                addTableCharge(
                                    smcOrderId, seqId, smcQuoteId, smcCaseId, productCandidateObj.getId(), parentItemId, productCandidateObj.getEntityID(),
                                    chargeDesc, chargeDescChi, chargeCode, productCandidateObj.getItemCharge(),
                                    parentItemCode, allowInstallment, handlingFeeWaive,
                                    msisdn, needToPay, paid, discountFormCode, catalogItemDesc, catalogRrp,
                                    pstmtCharge
                                );
                            }
                        }
                    }
                }
            }

            if (EpcItemCategory.DEVICE.equals(productCandidateObj.getItemCat()) || EpcItemCategory.APPLECARE.equals(productCandidateObj.getItemCat()) || 
                EpcItemCategory.SCREEN_REPLACE.equals(productCandidateObj.getItemCat()) || EpcItemCategory.SIM.equals(productCandidateObj.getItemCat()) ||
                EpcItemCategory.GIFT_WRAPPING.equals(productCandidateObj.getItemCat())
                || EpcItemCategory.PLASTIC_BAG.equals(productCandidateObj.getItemCat())
            ) {
                parentItemDesc = productCandidateObj.getCpqItemDesc();
            }
            
            if (EpcItemCategory.TRADE_IN.equals(productCandidateObj.getItemCat())) {
                parentItemCode = productCandidateObj.getCpqItemValue();
            }

            if(productCandidateObj.getChildEntity() != null) {
                for(int i = 0; i < productCandidateObj.getChildEntity().size(); i++) {
                    createQuoteChargeList(smcOrderId, seqId, smcQuoteId, smcCaseId, productCandidateObj.getId(), parentItemCode, parentItemDesc, msisdn, offerCharge, readOnly, 
                                          productCandidateObj.getChildEntity().get(i), metaDataLookup, contextData, pstmtCharge, epcItemList, epcChargeCtrlList, epcPaymentCtrlList, 
                                          chargeCodeMap, epcUpdatePayment);
                }
            } else {
                // nothing to do
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw e;
        }
        return;
    }

    public void addTableCharge(int smcOrderId, int[] seqId, int smcQuoteId, String smcCaseId, String itemId, String parentItemId, String itemGuid, 
                                 String chargeDesc, String chargeDescChi, String chargeCode, BigDecimal chargeAmount, String itemCode,
                                 String allowInstallment, String handlingFeeWaive,
                                 String msisdn, String needToPay, String paid, String discountFormCode, String catalogItemDesc, BigDecimal catalogRrp,
                                 PreparedStatement pstmtCharge) throws Exception {
        String waiveFormCode = null;
        try {

            waiveFormCode = getWaiveFormCode(chargeCode);

            pstmtCharge.clearParameters();
            pstmtCharge.setInt(1, smcOrderId);
            pstmtCharge.setInt(2, seqId[0]++);
            if (smcQuoteId==0) {
                pstmtCharge.setNull(3, java.sql.Types.INTEGER);
            } else {
                pstmtCharge.setInt(3, smcQuoteId);
            }
            pstmtCharge.setString(4, smcCaseId);
            pstmtCharge.setString(5, itemId);
            pstmtCharge.setString(6, parentItemId);
            pstmtCharge.setString(7, itemGuid);
            pstmtCharge.setString(8, chargeDesc);
            pstmtCharge.setString(9, chargeDescChi);
            pstmtCharge.setString(10, chargeCode);
            pstmtCharge.setBigDecimal(11, chargeAmount);
            pstmtCharge.setString(12, itemCode);
            pstmtCharge.setString(13, allowInstallment);
            pstmtCharge.setString(14, handlingFeeWaive);
            pstmtCharge.setString(15, msisdn);
            pstmtCharge.setBigDecimal(16, chargeAmount); // origin_charge_amount
            pstmtCharge.setString(17, needToPay); // need_to_pay
            pstmtCharge.setString(18, paid); // paid
            pstmtCharge.setString(19, waiveFormCode);
            pstmtCharge.setString(20, discountFormCode);
            pstmtCharge.setString(21, catalogItemDesc);
            pstmtCharge.setBigDecimal(22, catalogRrp);
            pstmtCharge.executeUpdate();
        } catch (Exception e) {
            throw e;
        }
    }

    public void deleteTableCharge(int smcOrderId, int seqId, Connection epcConn) throws Exception {

        PreparedStatement pstmt = null;

        try {
            pstmt = epcConn.prepareStatement("DELETE FROM epc_order_charge WHERE order_id = ? AND seq_id = ? ");
            pstmt.setInt(1, smcOrderId);
            pstmt.setInt(2, seqId);
            pstmt.executeUpdate();
        } catch (Exception e) {
            throw e;
        } finally {
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
        }
    }
    
    public void updateTableCharge(int smcOrderId, ArrayList<EpcOfferCharge> offerChargeList, Connection epcConn, PreparedStatement pstmtCharge, int[] seqId, HashMap<String, ZzPservice> chargeCodeMap) throws Exception {

        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rset = null;
        //BigDecimal tradeInSurplusByTradeInValue = null;
        //BigDecimal tradeInSurplusByTradeInBonus = null;
        BigDecimal handsetAndAccPrice = null;
        //BigDecimal discount = null;
        BigDecimal tradeInValue = null;
        //BigDecimal tradeInBonus = null;
        //BigDecimal couponAmount = null;
        BigDecimal otherPaymentAmount = null;
        BigDecimal netPaymentAmount = null;
        String surplusCharge = null;
        BigDecimal surplusAmount = null;
        BigDecimal tmpTotalRemainingChargeAmt = null;
        BigDecimal tmpDiscountChargeAmt = null;
        BigDecimal updateDiscountAmt = null;
        String logStr = "[updateTableCharge][" + smcOrderId + "] ";

        PreparedStatement pstmtEpcOrderVoucher = null;        // added by Danny Chan on 2022-10-27
        PreparedStatement pstmtReserve = null;
        PreparedStatement pstmtReserveParentCharge = null;
        PreparedStatement pstmtReserveUpdate = null;
        PreparedStatement pstmtChargeNeedToPay = null;
        PreparedStatement pstmtPayment = null;
        PreparedStatement pstmtExistingCust = null;
        PreparedStatement pstmtSumRemainingCharge = null;
        PreparedStatement pstmtGetDiscountCharge = null;
        PreparedStatement pstmtUpdateDiscountCharge = null;
        ResultSet rsetReserve = null;
        ResultSet rsetReserveParentCharge = null;
        ResultSet rsetChargeNeedToPay = null;
        ResultSet rsetPayment = null;
        ResultSet rsetExistingCust = null;
        ResultSet rsetSumRemainingCharge = null;
        ResultSet rsetGetDiscountCharge = null;
        String sql = "";
        String tmpItemId = "";
        String tmpParentItemId = "";
        String tmpParentWarehouse = "";
        BigDecimal tmpTotalParentCharge = null;
        
        try {

            pstmt = epcConn.prepareStatement(
                    "UPDATE epc_order_charge " +
                    "SET msisdn = ? " +
                    "WHERE order_id = ? "+
                    "AND msisdn IS NULL " +
                    "AND case_id = ? "
                    );
            for(EpcOfferCharge offerCharge:offerChargeList) {
                pstmt.setString(1, offerCharge.getMsisdn());
                pstmt.setInt(2, smcOrderId);
                pstmt.setString(3, offerCharge.getCaseId());
                pstmt.executeUpdate();
            }
            pstmt.close();

            pstmt = epcConn.prepareStatement(
                        "SELECT a.item_code, a.need_to_pay, a.parent_item_id, a.case_id, a.item_id " +
                        "FROM epc_order_charge a, epc_order_item b " +
                        "WHERE a.order_id = ? " +
                        "AND a.order_id = b.order_id " +
                        "AND a.item_id = b.item_id " +
                        "AND b.cpq_item_desc = ? " +
                        "AND a.charge_code = ? "
                    );
            pstmt2 = epcConn.prepareStatement(
                        "UPDATE epc_order_charge " +
                        "SET item_code = ?, need_to_pay = ? " +
                        "WHERE order_id = ? " +
                        "AND case_id = ? " +
                        "AND parent_item_id = ? " +
                        "AND item_id <> ? "
                    );

            pstmt.setInt(1, smcOrderId);
            pstmt.setString(2, "Accessory Charge");
            pstmt.setString(3, "02");
            rset = pstmt.executeQuery();
            while (rset.next()) {
                pstmt2.setString(1, rset.getString("item_code"));
                pstmt2.setString(2, rset.getString("need_to_pay"));
                pstmt2.setInt(3, smcOrderId);
                pstmt2.setString(4, rset.getString("case_id"));
                pstmt2.setString(5, rset.getString("parent_item_id"));
                pstmt2.setString(6, rset.getString("item_id"));
                pstmt2.addBatch();
            }
            rset.close();
            pstmt2.executeBatch();
            pstmt.close();
            pstmt2.close();

            pstmt = epcConn.prepareStatement(
                    "SELECT a.item_code, a.need_to_pay, a.case_id " +
                    "FROM epc_order_charge a, epc_order_item b " +
                    "WHERE a.order_id = ? " +
                    "AND a.order_id = b.order_id " +
                    "AND a.item_id = b.item_id " +
                    "AND b.cpq_item_desc = ? " +
                    "AND a.charge_code = ? "
                    );
            pstmt2 = epcConn.prepareStatement(
                     "UPDATE epc_order_charge " +
                     "SET item_code = ?, need_to_pay = ? " +
                     "WHERE order_id = ? " +
                     "AND case_id = ? " +
                     "AND charge_desc IN (?, ?) "
                    );
    
            pstmt.setInt(1, smcOrderId);
            pstmt.setString(2, "Handset One Time Charge");
            pstmt.setString(3, "02");
            rset = pstmt.executeQuery();
            while (rset.next()) {
                pstmt2.setString(1, rset.getString("item_code"));
                pstmt2.setString(2, rset.getString("need_to_pay"));
                pstmt2.setInt(3, smcOrderId);
                pstmt2.setString(4, rset.getString("case_id"));
                pstmt2.setString(5, "Mobile Payment Handset Discount");
                pstmt2.setString(6, "Net Handset Price Calculation Discount Charge");
                pstmt2.addBatch();
            }
            rset.close();
            pstmt2.executeBatch();
            pstmt.close();
            pstmt2.close();

            // calculate and add trade-in surplus
            /*
            tradeInSurplusByTradeInValue = null;
            tradeInSurplusByTradeInBonus = null;
            handsetAndAccPrice = new BigDecimal(0);
            discount = new BigDecimal(0);
            tradeInValue = new BigDecimal(0);
            tradeInBonus = new BigDecimal(0);
            couponAmount = new BigDecimal(0);
            pstmt = epcConn.prepareStatement(
                        "SELECT charge_code, charge_amount " +
                        "FROM epc_order_charge " +
                        "WHERE order_id = ? "
                    );
            pstmt.setInt(1, smcOrderId);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                if ("02".equals(rset.getString("charge_code"))) {
                    handsetAndAccPrice = handsetAndAccPrice.add(rset.getBigDecimal("charge_amount"));
                } else if ("DISCOUNT".equals(rset.getString("charge_code"))) {
                    discount = discount.add(rset.getBigDecimal("charge_amount"));
                } else if ("TRADEIN".equals(rset.getString("charge_code"))) {
                    tradeInValue = tradeInValue.add(rset.getBigDecimal("charge_amount"));
                } else if ("TDBONUS".equals(rset.getString("charge_code"))) {
                    tradeInBonus = tradeInBonus.add(rset.getBigDecimal("charge_amount"));
                } 
// migrate coupon as payment, kerrytsang, 20230117
//                else if ("COUP".equals(rset.getString("charge_code"))) {
//                    couponAmount = couponAmount.add(rset.getBigDecimal("charge_amount"));
//                }
// end of migrate coupon as payment, kerrytsang, 20230117
            }
            rset.close();
            pstmt.close();


            // get COUP amount in payment table, kerrytsang, 20230117
            pstmt = epcConn.prepareStatement(
                        "SELECT payment_amount " +
                        "FROM epc_order_payment " +
                        "WHERE order_id = ? " +
                        "AND payment_code = ? "
                    );
            pstmt.setInt(1, smcOrderId);
            pstmt.setString(2, "COUP");
            rset = pstmt.executeQuery();
            while (rset.next()) {
                couponAmount = couponAmount.add(rset.getBigDecimal("payment_amount"));
            }
            rset.close();
            pstmt.close();
            // end of get COUP amount in payment table, kerrytsang, 20230117

// migrate coupon as payment, kerrytsang, 20230117
//            tradeInSurplusByTradeInBonus = handsetAndAccPrice.add(discount).add(couponAmount);
            tradeInSurplusByTradeInBonus = handsetAndAccPrice.add(discount).subtract(couponAmount);
// end of migrate coupon as payment, kerrytsang, 20230117
            if (tradeInSurplusByTradeInBonus.compareTo(BigDecimal.ZERO) < 0) {
                tradeInSurplusByTradeInValue = tradeInValue;
                tradeInSurplusByTradeInBonus = tradeInBonus;
            } else if (tradeInSurplusByTradeInBonus.add(tradeInBonus).compareTo(BigDecimal.ZERO) < 0) {
                tradeInSurplusByTradeInValue = tradeInValue;
                tradeInSurplusByTradeInBonus = tradeInSurplusByTradeInBonus.add(tradeInBonus);
            } else if (tradeInSurplusByTradeInBonus.add(tradeInBonus).add(tradeInValue).compareTo(BigDecimal.ZERO) < 0) {
                tradeInSurplusByTradeInValue = tradeInSurplusByTradeInBonus.add(tradeInBonus).add(tradeInValue);
                tradeInSurplusByTradeInBonus = new BigDecimal(0);
            } else {
                tradeInSurplusByTradeInValue = new BigDecimal(0);
                tradeInSurplusByTradeInBonus = new BigDecimal(0);
            }

//            logger.info("tradeInSurplusByTradeInBonus:" + tradeInSurplusByTradeInBonus);
logger.info("{}{}{}", logStr, "tradeInSurplusByTradeInBonus:", tradeInSurplusByTradeInBonus);
//            logger.info("tradeInSurplusByTradeInValue:" + tradeInSurplusByTradeInValue);
logger.info("{}{}{}", logStr, "tradeInSurplusByTradeInValue:", tradeInSurplusByTradeInValue);

            if (tradeInSurplusByTradeInBonus.compareTo(BigDecimal.ZERO) < 0 || tradeInSurplusByTradeInValue.compareTo(BigDecimal.ZERO) < 0) {
                pstmt = epcConn.prepareStatement(
                            "SELECT quote_id, case_id, msisdn, charge_code, charge_amount " +
                            "FROM epc_order_charge " +
                            "WHERE charge_code IN ('TRADEIN', 'TDBONUS') " + 
                            "AND order_id = ? " +
                            "ORDER BY msisdn NULLS LAST, case_id NULLS LAST "
                        );
                pstmt.setInt(1, smcOrderId);
                rset = pstmt.executeQuery();
                while (rset.next()) {
                    surplusAmount = new BigDecimal(0);
                    if ("TRADEIN".equals(rset.getString("charge_code")) && tradeInSurplusByTradeInValue.compareTo(BigDecimal.ZERO) < 0) {
                        surplusAmount = tradeInSurplusByTradeInValue.subtract(rset.getBigDecimal("charge_amount"));
                        if (surplusAmount.compareTo(BigDecimal.ZERO) < 0) {
                            surplusAmount = rset.getBigDecimal("charge_amount");
                        } else {
                            surplusAmount = tradeInSurplusByTradeInValue;
                        }
                        tradeInSurplusByTradeInValue = tradeInSurplusByTradeInValue.subtract(rset.getBigDecimal("charge_amount"));
                    } else if ("TDBONUS".equals(rset.getString("charge_code")) && tradeInSurplusByTradeInBonus.compareTo(BigDecimal.ZERO) < 0) {
                        surplusAmount = tradeInSurplusByTradeInBonus.subtract(rset.getBigDecimal("charge_amount"));
                        if (surplusAmount.compareTo(BigDecimal.ZERO) < 0) {
                            surplusAmount = rset.getBigDecimal("charge_amount");
                        } else {
                            surplusAmount = tradeInSurplusByTradeInBonus;
                        }
                        tradeInSurplusByTradeInBonus = tradeInSurplusByTradeInBonus.subtract(rset.getBigDecimal("charge_amount"));
                    }

                    if (rset.getString("msisdn") != null) {
                        surplusCharge = "01";
                    } else {
                        surplusCharge = "FFTRADEIN";
                    }

                    if (surplusAmount.compareTo(BigDecimal.ZERO) < 0) {
                        addTableCharge(
                            smcOrderId, seqId, rset.getInt("quote_id"), rset.getString("case_id"), null, null, null,
                            chargeCodeMap.get(surplusCharge).getReceiptSrvDesc(), chargeCodeMap.get(surplusCharge).getReceiptSrvDescChi(), surplusCharge, surplusAmount.abs(),
                            null, "N", "N",
                            rset.getString("msisdn"), "Y", "N", null, null, null,
                            pstmtCharge
                        );
                    }
                }
                rset.close();
                pstmt.close();

            }
            */
            
            // added by Danny Chan on 2022-10-27: start
            pstmtEpcOrderVoucher = epcConn.prepareStatement( 
                                      "SELECT case_id, charge_waiver " + 
                                      "FROM epc_order_voucher " + 
                                      "WHERE order_id = ? " + 
                                      "AND assign_redeem = 'REDEEM' " + 
                                      "AND status = 'A' " + 
                                      "AND not_modelled = 'Y' " + 
                                      "AND charge_waiver is not NULL"
                                   );
        
            pstmt = epcConn.prepareStatement(
                       "DELETE FROM epc_order_charge " + 
                       "WHERE order_id = ? " + 
                       "AND charge_code = ? "
                    );
            
            pstmt2 = epcConn.prepareStatement(
                       "DELETE FROM epc_order_charge " + 
                       "WHERE order_id = ? " + 
                       "AND charge_code = ? " + 
                       "AND case_id = ?"
                    );
            
            pstmt.setInt(1, smcOrderId);
            pstmt2.setInt(1, smcOrderId);
            
            pstmtEpcOrderVoucher.setInt(1, smcOrderId);
        
            rset = pstmtEpcOrderVoucher.executeQuery();
            
            while (rset.next()) {
                String case_id = rset.getString(1);
                String charge_waiver = rset.getString(2);
                
                String chargeCodes[];
                
                if (charge_waiver.contains("|")) {
                    chargeCodes = charge_waiver.split("\\|");
                } else {
                    chargeCodes = new String[] {charge_waiver};
                }
                
                for (int t=0; t<chargeCodes.length; t++) {
                    if ( case_id==null || case_id.equals("") ) {
                        // coupon for all items in the shopping bag
                        pstmt.setString(2, chargeCodes[t]);
                        pstmt.executeUpdate();
                    } else {
                        // coupon for specific item 
                        pstmt2.setString(2, chargeCodes[t]);
                        pstmt2.setString(3, case_id);
                        pstmt2.executeUpdate();
                    }
                }
            }      
            
            pstmt.close();
            pstmt2.close();
            rset.close();
            pstmtEpcOrderVoucher.close();
            // added by Danny Chan on 2022-10-27: end
            
            // Added by William Tam on 12 Oct 2023, update discount amount if total discount amount (accessory discount is excluded) > sum of remaining charges other than discount
            // for each offer case, get sum of remaining charge amount
            pstmtSumRemainingCharge = epcConn.prepareStatement(
                                        "SELECT NVL(SUM(charge_amount), 0) total_remaining_amount " +
                                        "FROM epc_order_charge " +
                                        "WHERE order_id = ? " +
                                        "AND case_id = ? " +
                                        "AND item_guid IN ( " +
                                        "    SELECT charge_guid FROM epc_charge_ctrl " +
                                        "    WHERE item_code NOT IN (?, ?) " +
                                        ") " +
                                        "AND charge_code NOT IN (?) "
                                      );
            
            pstmtGetDiscountCharge = epcConn.prepareStatement(
                                        "SELECT seq_id, charge_amount " +
                                        "FROM epc_order_charge " +
                                        "WHERE order_id = ? " +
                                        "AND case_id = ? " +
                                        "AND item_guid IN ( " +
                                        "    SELECT charge_guid FROM epc_charge_ctrl " +
                                        "    WHERE item_code = ? " +
                                        ") " +
                                        "ORDER BY charge_amount DESC "
                                       );
            
            pstmtUpdateDiscountCharge = epcConn.prepareStatement(
                                        "UPDATE epc_order_charge " +
                                        "SET charge_amount = ?,  " +
                                        "    origin_charge_amount = ? " +
                                        "WHERE order_id = ? " +
                                        "AND case_id = ? " +
                                        "AND seq_id = ? "
                                       );

            for(EpcOfferCharge offerCharge:offerChargeList) {
                pstmtSumRemainingCharge.setInt(1, smcOrderId);
                pstmtSumRemainingCharge.setString(2, offerCharge.getCaseId());
                pstmtSumRemainingCharge.setString(3, "DISCOUNT"); // DISCOUNT other than Accessory Discount Charge
                pstmtSumRemainingCharge.setString(4, "TRADEIN"); // trade in related charges
                pstmtSumRemainingCharge.setString(5, "99");  // exclude charge_code 99
                rsetSumRemainingCharge = pstmtSumRemainingCharge.executeQuery();
                if (rsetSumRemainingCharge.next()) {
                    tmpTotalRemainingChargeAmt = rsetSumRemainingCharge.getBigDecimal("total_remaining_amount");
                } else {
                    tmpTotalRemainingChargeAmt = new BigDecimal(0);
                }
                rsetSumRemainingCharge.close();
                
                pstmtGetDiscountCharge.setInt(1, smcOrderId);
                pstmtGetDiscountCharge.setString(2, offerCharge.getCaseId());
                pstmtGetDiscountCharge.setString(3, "DISCOUNT"); // DISCOUNT other than Accessory Discount Charge
                rsetGetDiscountCharge = pstmtGetDiscountCharge.executeQuery();
                while(rsetGetDiscountCharge.next()) {
                    tmpDiscountChargeAmt = rsetGetDiscountCharge.getBigDecimal("charge_amount");
                    updateDiscountAmt = null;
                    if (tmpTotalRemainingChargeAmt.compareTo(BigDecimal.ZERO) < 0) {
                        updateDiscountAmt = new BigDecimal(0);
                    } else if ((tmpTotalRemainingChargeAmt.add(tmpDiscountChargeAmt)).compareTo(BigDecimal.ZERO) < 0) {
                        updateDiscountAmt = tmpTotalRemainingChargeAmt.negate();
                    }
                    tmpTotalRemainingChargeAmt = tmpTotalRemainingChargeAmt.add(tmpDiscountChargeAmt);
                    
                    if (updateDiscountAmt != null) {
                        pstmtUpdateDiscountCharge.setBigDecimal(1, updateDiscountAmt);
                        pstmtUpdateDiscountCharge.setBigDecimal(2, updateDiscountAmt);
                        pstmtUpdateDiscountCharge.setInt(3, smcOrderId);
                        pstmtUpdateDiscountCharge.setString(4, offerCharge.getCaseId());
                        pstmtUpdateDiscountCharge.setInt(5, rsetGetDiscountCharge.getInt("seq_id"));
                        pstmtUpdateDiscountCharge.executeUpdate();
                    }
                }
                rsetGetDiscountCharge.close();
            }
            pstmtSumRemainingCharge.close();
            pstmtGetDiscountCharge.close();
            pstmtUpdateDiscountCharge.close();
            // End of Added by William Tam on 12 Oct 2023, update discount amount if total discount amount > sum of charges other than discount

            // WARNING, below logic should be the 2nd last of the updateTableCharge, should NOT append any logic unless consider very carefully
            // fix 2 charges issue under 1 item, kerrytsang, 20230330
            sql = "update epc_order_charge " +
                  "   set charge_amount = ?, " +
                  "       origin_charge_amount = ? " + 
                  " where order_id = ? " +
                  "   and item_id = ? " +
                  "   and charge_code = ? ";
            pstmtReserveUpdate = epcConn.prepareStatement(sql);

            sql = "select nvl(sum(charge_amount),0) " +
                  "  from epc_order_charge " +
                  " where order_id = ? " +
                  "   and parent_item_id = ? " +
                  "   and charge_code != ? ";
            pstmtReserveParentCharge = epcConn.prepareStatement(sql);

            sql = "select a.item_id, a.parent_item_id, b.warehouse " +
                  "  from epc_order_charge a, epc_order_item b " +
                  " where a.order_id = ? " +
                  "   and a.charge_code = ? " +
                  "   and b.order_id = a.order_id " +
                  "   and b.item_id = a.parent_item_id ";
            pstmtReserve = epcConn.prepareStatement(sql);
            pstmtReserve.setInt(1, smcOrderId); // order_id
            pstmtReserve.setString(2, "99"); // charge_code
            rsetReserve = pstmtReserve.executeQuery();
            while(rsetReserve.next()) {
                tmpTotalParentCharge = new BigDecimal(0); // reset
                tmpItemId = StringHelper.trim(rsetReserve.getString("item_id"));
                tmpParentItemId = StringHelper.trim(rsetReserve.getString("parent_item_id"));
                tmpParentWarehouse = StringHelper.trim(rsetReserve.getString("warehouse"));

                // sum charges under this parent but not 99 charge
                pstmtReserveParentCharge.setInt(1, smcOrderId); // order_id
                pstmtReserveParentCharge.setString(2, tmpParentItemId); // parent_item_id
                pstmtReserveParentCharge.setString(3, "99"); // charge_code
                rsetReserveParentCharge = pstmtReserveParentCharge.executeQuery();
                if(rsetReserveParentCharge.next()) {
                    tmpTotalParentCharge = rsetReserveParentCharge.getBigDecimal(1);
                } rsetReserveParentCharge.close();

                if(tmpTotalParentCharge == null) {
                    tmpTotalParentCharge = new BigDecimal(0);
                }

                // update 99 charge
                if("AA".equals(tmpParentWarehouse)) {
                    if(new BigDecimal(100).compareTo(tmpTotalParentCharge) == 1) {
                        // if $100 > item charge, then charge item charge amount
                        pstmtReserveUpdate.setBigDecimal(1, tmpTotalParentCharge); // charge_amount
                        pstmtReserveUpdate.setBigDecimal(2, tmpTotalParentCharge); // origin_charge_amount
                        pstmtReserveUpdate.setInt(3, smcOrderId); // order_id
                        pstmtReserveUpdate.setString(4, tmpItemId); // item_id - 99 charge itself
                        pstmtReserveUpdate.setString(5, "99"); // chargre_code - 99
                        pstmtReserveUpdate.executeUpdate();
                    } else {
                        // charge $100
                        //  no need to update !
                    }
                } else if("AH".equals(tmpParentWarehouse)) {
                    if(new BigDecimal(500).compareTo(tmpTotalParentCharge) == 1) {
                        // if $500 > item charge, then charge item charge amount
                        pstmtReserveUpdate.setBigDecimal(1, tmpTotalParentCharge); // charge_amount
                        pstmtReserveUpdate.setBigDecimal(2, tmpTotalParentCharge); // origin_charge_amount
                        pstmtReserveUpdate.setInt(3, smcOrderId); // order_id
                        pstmtReserveUpdate.setString(4, tmpItemId); // item_id - 99 charge itself
                        pstmtReserveUpdate.setString(5, "99"); // chargre_code - 99
                        pstmtReserveUpdate.executeUpdate();
                    } else {
                        // charge $500
                        //  no need to update !
                    }
                } else {
                    // no action
                }
            } rsetReserve.close();
            pstmtReserve.close();
            // end of fix 2 charges issue under 1 item, kerrytsang, 20230330
            
            // WARNING, below logic should be the last of the updateTableCharge, should NOT append any logic unless consider very carefully
            // calculate and add trade-in surplus
            handsetAndAccPrice = new BigDecimal(0);
            pstmtChargeNeedToPay = epcConn.prepareStatement(
                                        "SELECT NVL(SUM(charge_amount), 0) total_charge_amount " +
                                        "FROM epc_order_charge " +
                                        "WHERE order_id = ? " +
                                        "AND paid = ? " +
                                        "AND need_to_pay = ? "
                                    );
            pstmtChargeNeedToPay.setInt(1, smcOrderId);
            pstmtChargeNeedToPay.setString(2, "N");
            pstmtChargeNeedToPay.setString(3, "Y");
            rsetChargeNeedToPay = pstmtChargeNeedToPay.executeQuery();
            if (rsetChargeNeedToPay.next()) {
                handsetAndAccPrice = rsetChargeNeedToPay.getBigDecimal("total_charge_amount");
            }
            rsetChargeNeedToPay.close();
            
            tradeInValue = new BigDecimal(0);
            otherPaymentAmount = new BigDecimal(0);
            pstmtPayment = epcConn.prepareStatement(
                                "SELECT payment_code, payment_amount " +
                                "FROM epc_order_payment " + 
                                "WHERE order_id = ? " +
                                "AND case_id IS NOT NULL "
                             );
            pstmtPayment.setInt(1, smcOrderId);
            rsetPayment = pstmtPayment.executeQuery();
            while (rsetPayment.next()) {
                if ("EPCTIBONUS".equals(rsetPayment.getString("payment_code")) || "EPCTRADIN".equals(rsetPayment.getString("payment_code"))) {
                    tradeInValue = tradeInValue.add(rsetPayment.getBigDecimal("payment_amount"));
                } else {
                    otherPaymentAmount = otherPaymentAmount.add(rsetPayment.getBigDecimal("payment_amount"));
                }
            }
            rsetPayment.close();
            
            surplusAmount = new BigDecimal(0);
            netPaymentAmount = handsetAndAccPrice.subtract(otherPaymentAmount);
            if (netPaymentAmount.compareTo(BigDecimal.ZERO) >= 0) {
                netPaymentAmount = netPaymentAmount.subtract(tradeInValue);
                if (netPaymentAmount.compareTo(BigDecimal.ZERO) < 0) {
                    surplusAmount = netPaymentAmount.abs();
                }
            } else {
                surplusAmount = tradeInValue.abs();
            }

            if (surplusAmount.compareTo(BigDecimal.ZERO) > 0) {
                pstmtExistingCust = epcConn.prepareStatement(
                                                "SELECT 1 FROM dual " +
                                                "WHERE EXISTS (" +
                                                "  SELECT 1 FROM epc_order_case " +
                                                "  WHERE order_id = ? " +
                                                "  AND subr_num IS NOT NULL " +
                                                "  AND cust_num IS NOT NULL " +
                                                ") "
                                            );
                pstmtExistingCust.setInt(1, smcOrderId);
                rsetExistingCust = pstmtExistingCust.executeQuery();
                if (rsetExistingCust.next()) {
                    surplusCharge = "01";
                } else {
                    surplusCharge = "EPCFORTI";
                }
                rsetExistingCust.close();
                
                addTableCharge(
                        smcOrderId, seqId, 0, null, null, null, null,
                        chargeCodeMap.get(surplusCharge).getReceiptSrvDesc(), chargeCodeMap.get(surplusCharge).getReceiptSrvDescChi(), surplusCharge, surplusAmount.abs(),
                        null, "N", "N",
                        null, "Y", "N", null, null, null,
                        pstmtCharge
                        );
            }
            
            
        } catch (Exception e) {
            throw e;
        } finally {
            DBHelper.closeAll(
                    rset,
                    rsetReserve,
                    rsetReserveParentCharge,
                    rsetChargeNeedToPay,
                    rsetPayment,
                    rsetExistingCust,
                    rsetSumRemainingCharge,
                    rsetGetDiscountCharge,
                    pstmt,
                    pstmt2,
                    pstmtEpcOrderVoucher,
                    pstmtReserve,
                    pstmtReserveParentCharge,
                    pstmtReserveUpdate,
                    pstmtChargeNeedToPay,
                    pstmtPayment,
                    pstmtExistingCust,
                    pstmtSumRemainingCharge,
                    pstmtGetDiscountCharge,
                    pstmtUpdateDiscountCharge
                    );

            //try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            //try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            //try { if (pstmt2 != null) { pstmt2.close(); } } catch (Exception ignore) {}
            //try { if (pstmtEpcOrderVoucher != null) { pstmtEpcOrderVoucher.close(); } } catch (Exception ignore) {}        // added by Danny Chan on 2022-10-27
            
        }
    }
    
    public void refreshTableSeqId(int smcOrderId, Connection epcConn) throws Exception {

        PreparedStatement pstmtGetCharge = null;
        PreparedStatement pstmtUpdateCharge = null;
        ResultSet rset = null;
        String sql = null;
        int currSeqId = 1;

        try {
            sql = "SELECT seq_id " +
                  "FROM epc_order_charge " +
                  "WHERE order_id = ? " +
                  "ORDER BY seq_id";
            pstmtGetCharge = epcConn.prepareStatement(sql);

            sql = "UPDATE epc_order_charge SET seq_id = ? " +
                  "WHERE order_id = ? " +
                  "AND seq_id = ? ";
            pstmtUpdateCharge = epcConn.prepareStatement(sql);

            pstmtGetCharge.setInt(1, smcOrderId);
            rset = pstmtGetCharge.executeQuery();
            while (rset.next()) {
                if (currSeqId != rset.getInt("seq_id")) {
                    pstmtUpdateCharge.setInt(1, currSeqId);
                    pstmtUpdateCharge.setInt(2, smcOrderId);
                    pstmtUpdateCharge.setInt(3, rset.getInt("seq_id"));
                    pstmtUpdateCharge.executeUpdate();
                }
                currSeqId++;
            }
            rset.close();
        } catch (Exception e) {
            throw e;
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmtGetCharge != null) { pstmtGetCharge.close(); } } catch (Exception ignore) {}
            try { if (pstmtUpdateCharge != null) { pstmtUpdateCharge.close(); } } catch (Exception ignore) {}
        }
    }
    
    public ArrayList<EpcOfferCharge> outputOfferChargeList(int smcOrderId, ArrayList<EpcOfferCharge> offerChargeList, Connection epcConn, boolean chargeForReceipt, String salesActionType) throws Exception {

        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        ResultSet rset = null;
        ResultSet rset2 = null;
        ArrayList<EpcCharge> epc02ChargeList = null;
        ArrayList<EpcCharge> epc99ChargeList = null;
        ArrayList<EpcCharge> discountChargeList = null;
        BigDecimal remainingDiscountAmount = new BigDecimal(0);

        try {
            pstmt = epcConn.prepareStatement(
                    "SELECT case_id, charge_code, charge_desc, charge_desc_chi, charge_amount, msisdn, " +
                    "       item_id, parent_item_id, need_to_pay, paid, waive_form_code, discount_form_code, seq_id, " +
                    "       origin_charge_amount, DECODE(discount_amount, NULL, 'N', 'Y') discounted, NVL(waive, 'N') waived, " +
                    "       approve_by, waive_reason, item_code, catalog_item_desc, catalog_rrp " +
                    "FROM epc_order_charge " +
                    "WHERE order_id = ? " +
                    "AND case_id = ? " +
                    "ORDER BY case_id ASC, charge_amount DESC, seq_id ASC"
                   );
            
            pstmt2 = epcConn.prepareStatement(
                    "SELECT case_id, voucher_master_id, voucher_amount * -1 payment_amount, voucher_desc, voucher_desc_chi "+
                    "FROM epc_order_voucher " +
                    "WHERE order_id  = ? " +
                    "AND case_id = ? " +
                    "AND assign_redeem = ? " +
                    "AND status = ? "
                   );
            
            pstmt3 = epcConn.prepareStatement(
                    "SELECT a.case_id, a.payment_code, a.payment_amount * -1 payment_amount "+
                    "FROM epc_order_payment a, epc_payment_ctrl b " +
                    "WHERE a.order_id  = ? " +
                    "AND a.case_id = ? " +
                    "AND a.payment_code NOT IN (?) " +
                    "AND a.payment_code = b.payment_code " +
                    "AND b.default_payment = ? "
                   );
            for (EpcOfferCharge offerCharge:offerChargeList) {
                epc02ChargeList = new ArrayList<EpcCharge>();
                
                discountChargeList = new ArrayList<EpcCharge>();
                epc99ChargeList = new ArrayList<EpcCharge>();
                pstmt.setInt(1, smcOrderId);
                pstmt.setString(2, offerCharge.getCaseId());
                rset = pstmt.executeQuery();
                while (rset.next()) {
                    EpcCharge epcCharge = new EpcCharge();
                    epcCharge.setChargeCode(rset.getString("charge_code"));
                    epcCharge.setChargeDesc(rset.getString("charge_desc"));
                    epcCharge.setChargeDescChi(rset.getString("charge_desc_chi"));
                    epcCharge.setChargeAmount(rset.getBigDecimal("charge_amount").setScale(2));
                    epcCharge.setCaseId(StringHelper.trim(rset.getString("case_id")));
                    epcCharge.setMsisdn(StringHelper.trim(rset.getString("msisdn")));
                    epcCharge.setItemId(StringHelper.trim(rset.getString("item_id")));
                    epcCharge.setParentItemId(StringHelper.trim(rset.getString("parent_item_id")));
                    epcCharge.setNeedToPay(StringHelper.trim(rset.getString("need_to_pay")));
                    epcCharge.setPaid(StringHelper.trim(rset.getString("paid")));
                    epcCharge.setWaiveFormCode(StringHelper.trim(rset.getString("waive_form_code")));
                    epcCharge.setDiscountFormCode(StringHelper.trim(rset.getString("discount_form_code")));
                    epcCharge.setSeqId(rset.getInt("seq_id"));
                    epcCharge.setOriginChargeAmount(rset.getBigDecimal("origin_charge_amount"));
                    epcCharge.setDiscounted(StringHelper.trim(rset.getString("discounted"), "N"));
                    epcCharge.setWaived(StringHelper.trim(rset.getString("waived"), "N"));
                    epcCharge.setApproveBy(StringHelper.trim(rset.getString("approve_by")));
                    epcCharge.setWaiveReason(StringHelper.trim(rset.getString("waive_reason")));
                    epcCharge.setItemCode(StringHelper.trim(rset.getString("item_code")));
                    epcCharge.setCatalogItemDesc(StringHelper.trim(rset.getString("catalog_item_desc")));
                    epcCharge.setCatalogRRP(rset.getBigDecimal("catalog_rrp"));
                    if (chargeForReceipt) {
                        if ("02".equals(epcCharge.getChargeCode())) {
                            epc02ChargeList.add(epcCharge);
                        } else if ("DISCOUNT".equals(epcCharge.getChargeCode())) {
                            discountChargeList.add(epcCharge);
                            remainingDiscountAmount = remainingDiscountAmount.add(epcCharge.getChargeAmount());
                        } else if ("99".equals(epcCharge.getChargeCode())) {
                            epc99ChargeList.add(epcCharge);
                        } else {
                            offerCharge.getChargeList().add(epcCharge);
                        }
                    } else {
                        offerCharge.getChargeList().add(epcCharge);
                    }

                }
                rset.close();

                // charge 02 amount = device charge amount - device discount amount
                // then if charge 02 amount > 0, charge 02 amount = charge 02 amount - remaining discount amount (for offer level but not for device)
                for (EpcCharge epc02Charge:epc02ChargeList) {
                    
                    for (Iterator<EpcCharge> discountItr = discountChargeList.iterator(); discountItr.hasNext(); ) {
                        EpcCharge discountcharge = discountItr.next();
                        if (epc02Charge.getParentItemId().equals(discountcharge.getParentItemId())) {
                            remainingDiscountAmount = remainingDiscountAmount.subtract(discountcharge.getChargeAmount());
                            epc02Charge.setChargeAmount(epc02Charge.getChargeAmount().add(discountcharge.getChargeAmount()));
                            if (epc02Charge.getChargeAmount().compareTo(BigDecimal.ZERO) < 0 ) {
                                epc02Charge.setChargeAmount(new BigDecimal(0));
                            }
                            discountItr.remove();
                        }
                    }
                }
                
                for (EpcCharge epc99Charge:epc99ChargeList) {
                    if (remainingDiscountAmount.compareTo(BigDecimal.ZERO) ==  0) {
                        // do nothing
                    } else if (epc99Charge.getChargeAmount().add(remainingDiscountAmount).compareTo(BigDecimal.ZERO) > 0) {
                        epc99Charge.setChargeAmount(epc99Charge.getChargeAmount().add(remainingDiscountAmount));
                        remainingDiscountAmount = new BigDecimal(0);
                    } else if (epc99Charge.getChargeAmount().add(remainingDiscountAmount).compareTo(BigDecimal.ZERO) < 0) {
                        remainingDiscountAmount = remainingDiscountAmount.add(epc99Charge.getChargeAmount());
                        epc99Charge.setChargeAmount(new BigDecimal(0));
                    } else {
                        remainingDiscountAmount = new BigDecimal(0);
                        epc99Charge.setChargeAmount(new BigDecimal(0));
                    }
                }
                
                for (EpcCharge epc99Charge:epc99ChargeList) {
                    offerCharge.getChargeList().add(epc99Charge);
                }

                for (EpcCharge epc02Charge:epc02ChargeList) {
                    if (remainingDiscountAmount.compareTo(BigDecimal.ZERO) ==  0) {
                        // do nothing
                    } else if (epc02Charge.getChargeAmount().add(remainingDiscountAmount).compareTo(BigDecimal.ZERO) > 0) {
                        epc02Charge.setChargeAmount(epc02Charge.getChargeAmount().add(remainingDiscountAmount));
                        remainingDiscountAmount = new BigDecimal(0);
                    } else if (epc02Charge.getChargeAmount().add(remainingDiscountAmount).compareTo(BigDecimal.ZERO) < 0) {
                        remainingDiscountAmount = remainingDiscountAmount.add(epc02Charge.getChargeAmount());
                        epc02Charge.setChargeAmount(new BigDecimal(0));
                    } else {
                        remainingDiscountAmount = new BigDecimal(0);
                        epc02Charge.setChargeAmount(new BigDecimal(0));
                    }
                }

                for (EpcCharge epc02Charge:epc02ChargeList) {
                    if (epc02Charge.getChargeAmount().compareTo(BigDecimal.ZERO) > 0) {
                        offerCharge.getChargeList().add(epc02Charge);
                    }
                }
                
                if (!chargeForReceipt && "".equals(salesActionType)) {
                    pstmt2.setInt(1, smcOrderId); // order_id
                    pstmt2.setString(2, offerCharge.getCaseId()); // case_id
                    pstmt2.setString(3, "REDEEM"); // assign_redeem
                    pstmt2.setString(4, "A"); // stat
                    rset2 = pstmt2.executeQuery();
                    while (rset2.next()) {
                        EpcCharge epcCharge = new EpcCharge();
                        epcCharge.setChargeCode("COUP");
                        epcCharge.setVoucherMasterId(rset2.getString("voucher_master_id"));
                        epcCharge.setChargeDesc(rset2.getString("voucher_desc"));
                        epcCharge.setChargeDescChi(rset2.getString("voucher_desc_chi"));
                        epcCharge.setChargeAmount(rset2.getBigDecimal("payment_amount").setScale(2));
                        epcCharge.setOriginChargeAmount(rset2.getBigDecimal("payment_amount").setScale(2));
                        epcCharge.setCaseId(rset2.getString("case_id"));
                        epcCharge.setNeedToPay("Y");
                        epcCharge.setPaid("N");
                        offerCharge.getChargeList().add(epcCharge);
                    }
                    rset2.close();
                    
                    pstmt3.setInt(1, smcOrderId); // order_id
                    pstmt3.setString(2, offerCharge.getCaseId()); // case_id
                    pstmt3.setString(3, "COUP"); // not COUP
                    pstmt3.setString(4, "Y"); // default payment
                    rset2 = pstmt3.executeQuery();
                    while(rset2.next()) {
                        
                        EpcCharge epcCharge = new EpcCharge();
                        epcCharge.setChargeCode(rset2.getString("payment_code"));
                        epcCharge.setChargeAmount(rset2.getBigDecimal("payment_amount").setScale(2));
                        epcCharge.setOriginChargeAmount(rset2.getBigDecimal("payment_amount").setScale(2));
                        epcCharge.setCaseId(rset2.getString("case_id"));
                        epcCharge.setNeedToPay("Y");
                        epcCharge.setPaid("N");
                        

                        List<ZzPservice> zzPserviceList = saCodeHandler.getCodes(rset2.getString("payment_code"));
                        for(ZzPservice paymentCode:zzPserviceList) {
                            epcCharge.setChargeDesc(paymentCode.getReceiptSrvDesc());
                            epcCharge.setChargeDescChi(paymentCode.getReceiptSrvDescChi());
                        }
                        
                        offerCharge.getChargeList().add(epcCharge);
                        
                    }
                    rset2.close();
                }
            }
            pstmt.close();

            pstmt = epcConn.prepareStatement(
                    "SELECT case_id, charge_code, charge_desc, charge_desc_chi, charge_amount, msisdn, " +
                    "       item_id, parent_item_id, need_to_pay, paid, waive_form_code, discount_form_code, seq_id, " +
                    "       origin_charge_amount, DECODE(discount_amount, NULL, 'N', 'Y') discounted, NVL(waive, 'N') waived, " +
                    "       approve_by, waive_reason, item_code, catalog_item_desc, catalog_rrp " +
                    "FROM epc_order_charge " +
                    "WHERE order_id = ? " +
                    "AND case_id IS NULL " +
                    "ORDER BY charge_code, seq_id"
                   );
            for (EpcOfferCharge offerCharge:offerChargeList) {
                if ("".equals(offerCharge.getCaseId())) {
                    pstmt.setInt(1, smcOrderId);
                    rset = pstmt.executeQuery();
                    while (rset.next()) {
                        EpcCharge epcCharge = new EpcCharge();
                        epcCharge.setChargeCode(rset.getString("charge_code"));
                        epcCharge.setChargeDesc(rset.getString("charge_desc"));
                        epcCharge.setChargeDescChi(rset.getString("charge_desc_chi"));
                        epcCharge.setChargeAmount(rset.getBigDecimal("charge_amount").setScale(2));
                        epcCharge.setCaseId(StringHelper.trim(rset.getString("case_id")));
                        epcCharge.setMsisdn(StringHelper.trim(rset.getString("msisdn")));
                        epcCharge.setItemId(StringHelper.trim(rset.getString("item_id")));
                        epcCharge.setParentItemId(StringHelper.trim(rset.getString("parent_item_id")));
                        epcCharge.setNeedToPay(StringHelper.trim(rset.getString("need_to_pay")));
                        epcCharge.setPaid(StringHelper.trim(rset.getString("paid")));
                        epcCharge.setWaiveFormCode(StringHelper.trim(rset.getString("waive_form_code")));
                        epcCharge.setDiscountFormCode(StringHelper.trim(rset.getString("discount_form_code")));
                        epcCharge.setSeqId(rset.getInt("seq_id"));
                        epcCharge.setOriginChargeAmount(rset.getBigDecimal("origin_charge_amount"));
                        epcCharge.setDiscounted(StringHelper.trim(rset.getString("discounted"), "N"));
                        epcCharge.setWaived(StringHelper.trim(rset.getString("waived"), "N"));
                        epcCharge.setApproveBy(StringHelper.trim(rset.getString("approve_by")));
                        epcCharge.setWaiveReason(StringHelper.trim(rset.getString("waive_reason")));
                        epcCharge.setItemCode(StringHelper.trim(rset.getString("item_code")));
                        epcCharge.setCatalogItemDesc(StringHelper.trim(rset.getString("catalog_item_desc")));
                        epcCharge.setCatalogRRP(rset.getBigDecimal("catalog_rrp"));
                        offerCharge.getChargeList().add(epcCharge);
                    }
                    rset.close();
                }
            }
            pstmt.close();
            
            if (!chargeForReceipt && "".equals(salesActionType)) {
                pstmt = epcConn.prepareStatement(
                                "SELECT case_id, voucher_master_id, voucher_amount * -1 payment_amount, voucher_desc, voucher_desc_chi "+
                                "FROM epc_order_voucher " +
                                "WHERE order_id  = ? " +
                                "AND case_id IS NULL " +
                                "AND assign_redeem = ? " +
                                "AND status = ? "
                       );
                pstmt.setInt(1, smcOrderId); // order_id
                pstmt.setString(2, "REDEEM"); // assign_redeem
                pstmt.setString(3, "A"); // stat
                rset = pstmt.executeQuery();
                for (EpcOfferCharge offerCharge:offerChargeList) {
                    if ("".equals(offerCharge.getCaseId())) {
                        while (rset.next()) {
                            EpcCharge epcCharge = new EpcCharge();
                            epcCharge.setChargeCode("COUP");
                            epcCharge.setVoucherMasterId(rset.getString("voucher_master_id"));
                            epcCharge.setChargeDesc(rset.getString("voucher_desc"));
                            epcCharge.setChargeDescChi(rset.getString("voucher_desc_chi"));
                            epcCharge.setChargeAmount(rset.getBigDecimal("payment_amount").setScale(2));
                            epcCharge.setOriginChargeAmount(rset.getBigDecimal("payment_amount").setScale(2));
                            epcCharge.setNeedToPay("Y");
                            epcCharge.setPaid("N");
                            offerCharge.getChargeList().add(epcCharge);
                        }
                        rset.close();
                        break;
                    }
                }
                pstmt.close();
            }

        } catch (Exception e) {
            throw e;
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (rset2 != null) { rset2.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if (pstmt2 != null) { pstmt2.close(); } } catch (Exception ignore) {}
            try { if (pstmt3 != null) { pstmt3.close(); } } catch (Exception ignore) {}
        }
        return offerChargeList;
    }

    public ArrayList<EpcCharge> getInstallHandlingChargeList(int smcOrderId, Connection epcConn) throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rset = null;
        ArrayList<EpcCharge> epcInstallHandlingChargeList = new ArrayList<EpcCharge>();

        try {
            pstmt = epcConn.prepareStatement(
                    "SELECT case_id, charge_code, charge_desc, charge_desc_chi, charge_amount, msisdn, " +
                    "        item_id, parent_item_id, need_to_pay, paid, waive_form_code, discount_form_code, seq_id " +
                    "FROM epc_order_charge " +
                    "WHERE order_id = ? " +
                    "AND charge_code IN ("+
                    "  SELECT value_str1 FROM epc_control_tbl " +
                    "  WHERE rec_type = ? " +
                    ") AND need_to_pay = ? AND paid = ? " +
                    "ORDER BY case_id NULLS LAST, seq_id"
                   );
            pstmt.setInt(1, smcOrderId);
            pstmt.setString(2, "INSTALL_HANDLING_FEE");
            pstmt.setString(3, "Y");
            pstmt.setString(4, "N");
            rset = pstmt.executeQuery();
            while (rset.next()) {
                EpcCharge epcCharge = new EpcCharge();
                epcCharge.setChargeCode(rset.getString("charge_code"));
                epcCharge.setChargeDesc(rset.getString("charge_desc"));
                epcCharge.setChargeDescChi(rset.getString("charge_desc_chi"));
                epcCharge.setChargeAmount(rset.getBigDecimal("charge_amount").setScale(2));
                epcCharge.setCaseId(StringHelper.trim(rset.getString("case_id")));
                epcCharge.setMsisdn(StringHelper.trim(rset.getString("msisdn")));
                epcCharge.setItemId(StringHelper.trim(rset.getString("item_id")));
                epcCharge.setParentItemId(StringHelper.trim(rset.getString("parent_item_id")));
                epcCharge.setNeedToPay(StringHelper.trim(rset.getString("need_to_pay")));
                epcCharge.setPaid(StringHelper.trim(rset.getString("paid")));
                epcCharge.setWaiveFormCode(StringHelper.trim(rset.getString("waive_form_code")));
                epcCharge.setDiscountFormCode(StringHelper.trim(rset.getString("discount_form_code")));
                epcCharge.setSeqId(rset.getInt("seq_id"));
                epcInstallHandlingChargeList.add(epcCharge);
            }
            rset.close();

        } catch (Exception e) {
            throw e;
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
        }
        return epcInstallHandlingChargeList;
    }

    public int getOrderMaxSeqId(int smcOrderId, Connection epcConn) throws Exception {

        PreparedStatement pstmt = null;
        ResultSet rset = null;
        int maxSeqId = 1;

        try {
            pstmt = epcConn.prepareStatement(
                    "SELECT MAX(seq_id) max_seq_id " +
                    "FROM epc_order_charge " +
                    "WHERE order_id = ? "
                   );
            pstmt.setInt(1, smcOrderId);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                maxSeqId = rset.getInt("max_seq_id");
            }
            rset.close();

        } catch (Exception e) {
            throw e;
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
        }
        return maxSeqId;
    }

    public EpcGetDefaultPaymentResult getDefaultPaymentResult(EpcGetDefaultPayment epcGetDefaultPayment) {
        EpcGetDefaultPaymentResult result = new EpcGetDefaultPaymentResult();
        EpcQuote epcQuote = null;
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtPayment = null;
        PreparedStatement pstmtDefaultInstalmentPaymentCode = null;
        PreparedStatement pstmtGetCharge = null;
        PreparedStatement pstmtGetDefaultPayment = null;
        ResultSet rset = null;
        ResultSet rset2 = null;
        int orderId;
        String caseId = StringHelper.trim(epcGetDefaultPayment.getCaseId());
        String quoteGuid;
        String sql;
        ArrayList<EpcValidateItem> validateItemList = new ArrayList<EpcValidateItem>();
        ArrayList<EpcDefaultPayment> epcDefaultPaymentList = new ArrayList<EpcDefaultPayment>();
        boolean skipDefaultPayment = false;
        int seqId = -1;

//pstmt.setInt(1, orderId);
//pstmt.setString(2, "Y");
//pstmt.setString(3, "N");
        
        try {

            if (epcGetDefaultPayment.getOrderId() == null) {
                result.setResult("FAIL");
                result.setErrorCode("1001");
                result.setErrorMessage("Missing orderId");
                return result;
            }
            if (EpcSalesActionType.EXTENSION.equals(epcGetDefaultPayment.getSalesActionType())) {
                result.setResult("FAIL");
                result.setErrorCode("1003");
                result.setErrorMessage("Invalid salesActionType");
                return result;
            }

            epcConn = epcDataSource.getConnection();
            epcConn.setAutoCommit(false);
            orderId = Integer.parseInt(epcGetDefaultPayment.getOrderId());

            sql = "SELECT payment_id, payment_code, payment_amount, reference_1, reference_2 "+
                  "FROM epc_order_payment " +
                  "WHERE order_id = ? " +
                  "AND case_id = ? ";
            pstmtPayment = epcConn.prepareStatement(sql);
            
            sql = "SELECT a.item_code payment_code " +
                  "FROM epc_order_item a, epc_control_tbl b " +
                  "WHERE a.order_id = ? " +
                  "AND a.case_id = ? " +
                  "AND a.item_cat = ? " +
                  "AND a.item_code = b.key_str1 " +
                  "AND b.rec_type = ? ";
            pstmtDefaultInstalmentPaymentCode = epcConn.prepareStatement(sql);
            
            sql = "SELECT　case_id, charge_desc, item_id, parent_item_id, item_code, charge_amount, allow_installment, handling_fee_waive, " +
                  "msisdn, charge_code, seq_id, need_to_pay, paid, catalog_item_desc " +
                  "FROM epc_order_charge " +
                  "WHERE order_id = ? " +
                  "AND case_id = ? " +
                  "AND need_to_pay = ? " +
                  "AND paid = ? ";
            pstmtGetCharge = epcConn.prepareStatement(sql);
                        
            // add those default payment like COUP, tradein and etc as charges for instalment calculation
            sql = "SELECT a.payment_code, a.case_id, a.payment_amount * -1 payment_amount " +
                  "FROM epc_order_payment a, epc_payment_ctrl c " +
                  "WHERE a.order_id = ? " +
                  "AND a.case_id = ? " +
                  "AND a.payment_code = c.payment_code " +
                  "AND c.default_payment = ? " +
                  "AND a.tx_no IS NULL ";
            pstmtGetDefaultPayment = epcConn.prepareStatement(sql);
            
            sql = "SELECT quote_id, cpq_quote_guid "+
                  "FROM epc_order_quote " +
                  "WHERE order_id = ? ";
            pstmt = epcConn.prepareStatement(
                        sql,
                        ResultSet.TYPE_SCROLL_INSENSITIVE , 
                        ResultSet.CONCUR_UPDATABLE ,
                        ResultSet.HOLD_CURSORS_OVER_COMMIT);
            pstmt.setInt(1, orderId);
            rset = pstmt.executeQuery();
            if (!rset.next()) {
                result.setResult("FAIL");
                result.setErrorCode("1002");
                result.setErrorMessage("Invalid orderId");
                return result;
            } else {
                rset.beforeFirst();
            }
            ArrayList<EpcPaymentCtrl> ecpPaymentCtrlList = getPaymentCtrlList(epcConn);
            while (rset.next()) {
                quoteGuid = StringHelper.trim(rset.getString(2));

                epcQuote = null; // reset
                epcQuote = epcOrderHandler.getCPQQuoteInEpc(orderId, rset.getInt("quote_id"));
                if(epcQuote == null) {
                    epcQuote = epcQuoteHandler.getQuoteInfo(quoteGuid);
                }

                EpcQuoteItem[] epcQuoteItems = epcQuote.getItems();
                // get validate items from quote
                /*
                for (int i=0; i < epcQuoteItems.length; i++) {
                    validateItemList.addAll(getValidateItemFromProductCandidate(epcQuoteItems[i].getProductCandidateObj().getId(), epcQuoteItems[i].getProductCandidateObj(), epcQuoteItems[i].getMetaDataLookup()));
                }
                 */
                for (int i=0; i < epcQuoteItems.length; i++) {
                    
                    // only allow default payments for specific caseId
                    //if (!EpcSalesActionType.CHECKOUT.equals(epcGetDefaultPayment.getSalesActionType()) && !"".equals(caseId) && !epcQuoteItems[i].getProductCandidateObj().getId().equals(caseId)) {
                    //    continue;
                    //}
                    
                    // skip to create default payment for each caseId
                    // if salesActionType == "CHECKOUT"
                    // and the main device unit is no stock
                    //skipDefaultPayment = false;
                    //if (EpcSalesActionType.CHECKOUT.equals(epcGetDefaultPayment.getSalesActionType())) {
                    //    ArrayList<TBL_Epc_Order_Item> epcOrderItemList = epcOrderHandler.getOrderItemsByCase(epcQuoteItems[i].getProductCandidateObj().getId());
                    //    for (TBL_Epc_Order_Item epcOrderItem:epcOrderItemList) {
                    //        if (EpcItemCategory.DEVICE.equals(epcOrderItem.getItem_cat()) && "N".equals(epcOrderItem.getPremium()) && "NO_STOCK".equals(epcOrderItem.getReserve_id())) {
                    //            skipDefaultPayment = true;
                    //            break;
                    //        }
                    //    }
                    //}
                    //if (skipDefaultPayment) {
                    //    continue;
                    //}

                    ArrayList<EpcInstallmentCharge> epcInstallmentChargeList = new ArrayList<EpcInstallmentCharge>();
                    //createQuoteDefaultPaymentList(defaultOfferPayment, epcQuoteItems[i].getProductCandidateObj(), epcQuoteItems[i].getMetaDataLookup(), epcInstallmentChargeList, null, null);
                    //createQuoteDefaultPaymentList(epcDefaultPaymentList, epcQuoteItems[i].getProductCandidateObj(), epcQuoteItems[i].getMetaDataLookup(), epcInstallmentChargeList, null, null);
                    // initialize msisdn to each installment charge if it is not set
                    //for(EpcInstallmentCharge installmentCharge:epcInstallmentChargeList) {
                    //    if (installmentCharge.getMsisdn() == null) {
                    //        installmentCharge.setMsisdn(defaultOfferPayment.getMsisdn());
                    //    }
                    //}
                    
                    pstmtGetCharge.setInt(1, orderId);
                    pstmtGetCharge.setString(2, epcQuoteItems[i].getProductCandidateObj().getId());
                    pstmtGetCharge.setString(3, "Y"); // need_to_pay
                    pstmtGetCharge.setString(4, "N"); // paid
                    rset2 = pstmtGetCharge.executeQuery();
                    while (rset2.next()) {
                        EpcInstallmentCharge installmentCharge = new EpcInstallmentCharge();
                        installmentCharge.setCaseId(StringHelper.trim(rset2.getString("case_id")));
                        installmentCharge.setMsisdn(StringHelper.trim(rset2.getString("msisdn")));
                        installmentCharge.setChargeCode(StringHelper.trim(rset2.getString("charge_code")));
                        installmentCharge.setCatalogItemDesc(StringHelper.trim(rset2.getString("catalog_item_desc")));
                        installmentCharge.setLabel(StringHelper.trim(rset2.getString("item_code")));
                        installmentCharge.setChargeAmount(rset2.getBigDecimal("charge_amount"));
                        installmentCharge.setAllowInstallment("Y".equals(rset2.getString("allow_installment")));
                        installmentCharge.setHandlingFeeWaive("Y".equals(rset2.getString("handling_fee_waive")));
                        installmentCharge.setSeqId(rset2.getInt("seq_id"));
                        installmentCharge.setItemId(StringHelper.trim(rset2.getString("item_id")));
                        installmentCharge.setParentItemId(StringHelper.trim(rset2.getString("parent_item_id")));
                        installmentCharge.setNeedToPay(rset2.getString("need_to_pay"));
                        installmentCharge.setPaid(rset2.getString("paid"));
                        
                        if("99".equals(installmentCharge.getChargeCode())) {
                            installmentCharge.setChargeAmount(installmentCharge.getChargeAmount().negate());
                        }
                        epcInstallmentChargeList.add(installmentCharge);
                    }
                    rset2.close();

                    pstmtGetDefaultPayment.setInt(1, orderId);
                    pstmtGetDefaultPayment.setString(2, epcQuoteItems[i].getProductCandidateObj().getId());
                    pstmtGetDefaultPayment.setString(3, "Y"); //default_payment
                    rset2 = pstmtGetDefaultPayment.executeQuery();
                    while (rset2.next()) {
                    
                        EpcInstallmentCharge installmentCharge = new EpcInstallmentCharge();
                        installmentCharge.setCaseId(StringHelper.trim(rset2.getString("case_id")));
                        installmentCharge.setMsisdn("");
                        installmentCharge.setChargeCode(StringHelper.trim(rset2.getString("payment_code")));
                        installmentCharge.setCatalogItemDesc("default_payment");
                        installmentCharge.setLabel("");
                        installmentCharge.setChargeAmount(rset2.getBigDecimal("payment_amount"));
                        installmentCharge.setAllowInstallment(false);
                        installmentCharge.setHandlingFeeWaive(false);
                        installmentCharge.setSeqId(seqId--);
                        installmentCharge.setItemId("");
                        installmentCharge.setParentItemId("");
                        installmentCharge.setNeedToPay("Y");
                        installmentCharge.setPaid("N");

                        epcInstallmentChargeList.add(installmentCharge);
                    }
                    rset2.close();
                    
                    BigDecimal installmentAmountWithoutHandlingFee = getEligibleInstallmentPaymentAmount(epcInstallmentChargeList, true, new ArrayList<EpcInstallmentCharge>());
                    logger.info("installmentAmountWithoutHandlingFee:" + installmentAmountWithoutHandlingFee.toString());
                    if (installmentAmountWithoutHandlingFee.compareTo(BigDecimal.ZERO) > 0) {
                        pstmtDefaultInstalmentPaymentCode.setInt(1, orderId);
                        pstmtDefaultInstalmentPaymentCode.setString(2, epcQuoteItems[i].getProductCandidateObj().getId());
                        pstmtDefaultInstalmentPaymentCode.setString(3, EpcItemCategory.REQUIRED_PAYMENT);
                        pstmtDefaultInstalmentPaymentCode.setString(4, "INSTALL_PAYMENT");
                        rset2 = pstmtDefaultInstalmentPaymentCode.executeQuery();
                        if (rset2.next()) {
                            EpcDefaultPayment defaultPayment = new EpcDefaultPayment();
                            defaultPayment.setPaymentType("NORMAL");
                            defaultPayment.setPaymentCode(rset2.getString("payment_code"));
                            defaultPayment.setPaymentAmount(installmentAmountWithoutHandlingFee);
                            defaultPayment.setReadOnly(false);
                            defaultPayment.setCaseId(epcQuoteItems[i].getProductCandidateObj().getId());
                            epcDefaultPaymentList.add(defaultPayment);
                        } else {
                            EpcDefaultPayment defaultPayment = new EpcDefaultPayment();
                            defaultPayment.setPaymentType("INSTALLMENT");
                            defaultPayment.setPaymentCode("");
                            defaultPayment.setPaymentAmount(installmentAmountWithoutHandlingFee);
                            defaultPayment.setReadOnly(false);
                            defaultPayment.setCaseId(epcQuoteItems[i].getProductCandidateObj().getId());
                            epcDefaultPaymentList.add(defaultPayment);
                        }
                        rset2.close();
                    }

                    // add presales payment code for testing
                    /*
                    if ("ID_vpuxf6nhdg".equals(epcQuoteItems[i].getProductCandidateObj().getId())) {
                        EpcDefaultPayment defaultPayment = new EpcDefaultPayment();
                        defaultPayment.setPaymentType("NORMAL");
                        defaultPayment.setPaymentCode("PRESALE");
                        defaultPayment.setReference1("BQ00445631");
                        defaultPayment.setPaymentAmount(new BigDecimal(500));
                        defaultOfferPayment.getPaymentList().add(defaultPayment);
                    }
                     */
                    validateItemList = getValidateItemFromProductCandidate(epcQuoteItems[i].getProductCandidateObj().getId(), epcQuoteItems[i].getProductCandidateObj(), epcQuoteItems[i].getMetaDataLookup());

                    //for (EpcValidateItem epcValidateItem:validateItemList) {
                    //    if (epcValidateItem instanceof EpcRequiredPaymentCode) {
                    //        EpcRequiredPaymentCode epcRequiredPaymentCode = (EpcRequiredPaymentCode) epcValidateItem;
                    //        if (epcRequiredPaymentCode.getPaymentCodeList().size() == 1) {
                    //            EpcDefaultPayment defaultPayment = new EpcDefaultPayment();
                    //            defaultPayment.setPaymentType("NORMAL");
                    //            defaultPayment.setPaymentCode(epcRequiredPaymentCode.getPaymentCodeList().get(0));
                    //            defaultPayment.setReadOnly(false);
                    //            //defaultPayment.setPaymentAmount();
                    //            //defaultOfferPayment.getPaymentList().add(defaultPayment);
                    //            epcDefaultPaymentList.add(defaultPayment);
                    //        }
                    //    }
                    //}

                    if (EpcSalesActionType.CHECKOUT.equals(epcGetDefaultPayment.getSalesActionType())) {
                        pstmtPayment.setInt(1, orderId);
                        pstmtPayment.setString(2, epcQuoteItems[i].getProductCandidateObj().getId());
                        rset2 = pstmtPayment.executeQuery();
                        while (rset2.next()) {
                            
                            EpcDefaultPayment defaultPayment = new EpcDefaultPayment();
                            defaultPayment.setPaymentId(rset2.getInt("payment_id"));
                            defaultPayment.setPaymentType("NORMAL");
                            defaultPayment.setPaymentCode(rset2.getString("payment_code"));
                            defaultPayment.setPaymentAmount(rset2.getBigDecimal("payment_amount").setScale(2));
                            defaultPayment.setReference1(rset2.getString("reference_1"));
                            defaultPayment.setReference2(rset2.getString("reference_2"));
                            defaultPayment.setReadOnly(false);
                            defaultPayment.setCaseId(epcQuoteItems[i].getProductCandidateObj().getId());
                            epcDefaultPaymentList.add(defaultPayment);
                            
                            for(EpcPaymentCtrl epcPaymentCtrl:ecpPaymentCtrlList) {
                                if (epcPaymentCtrl.getPaymentCode().equals(rset2.getString("payment_code")) && epcPaymentCtrl.isDefaultPayment()) {
                                    defaultPayment.setReadOnly(epcPaymentCtrl.isReadOnly());
    
                                    // set payment info message if any
                                    ArrayList<EpcPaymentInfo> epcPaymentInfoList = new ArrayList<EpcPaymentInfo>();
                                    if (!"".equals(epcPaymentCtrl.getDefaultRef1Message())) {
                                        EpcPaymentInfo epcPaymentInfo = new EpcPaymentInfo();
                                        epcPaymentInfo.setAssignTo("reference1");
                                        epcPaymentInfo.setInfoMessage(epcPaymentCtrl.getDefaultRef1Message());
                                        epcPaymentInfoList.add(epcPaymentInfo);
                                    }
                                    if (!"".equals(epcPaymentCtrl.getDefaultRef2Message())) {
                                        EpcPaymentInfo epcPaymentInfo = new EpcPaymentInfo();
                                        epcPaymentInfo.setAssignTo("reference2");
                                        epcPaymentInfo.setInfoMessage(epcPaymentCtrl.getDefaultRef2Message());
                                        epcPaymentInfoList.add(epcPaymentInfo);
                                    }
                                    if (!"".equals(epcPaymentCtrl.getDefaultPaymentAmountMessage())) {
                                        EpcPaymentInfo epcPaymentInfo = new EpcPaymentInfo();
                                        epcPaymentInfo.setAssignTo("paymentAmount");
                                        epcPaymentInfo.setInfoMessage(epcPaymentCtrl.getDefaultPaymentAmountMessage());
                                        epcPaymentInfoList.add(epcPaymentInfo);
                                    }
                                    if (epcPaymentInfoList.size() > 0) {
                                        defaultPayment.setPaymentInfoList(epcPaymentInfoList);
                                    }

                                    break;
                                }
                            }
                        }
                        rset2.close();
                    }
                }
            }
            rset.close();
            pstmtPayment.close();
            
            if (EpcSalesActionType.CHECKOUT.equals(epcGetDefaultPayment.getSalesActionType())) {

                // General Payment
                EpcDefaultOfferPayment defaultOfferPayment = new EpcDefaultOfferPayment();
                defaultOfferPayment.setCaseId("");
                defaultOfferPayment.setOfferDescription("General Payment");
                defaultOfferPayment.setPaymentList(new ArrayList<EpcDefaultPayment>());
                
                sql = "SELECT payment_id, payment_code, payment_amount, reference_1, reference_2 "+
                      "FROM epc_order_payment " +
                      "WHERE order_id = ? " +
                      "AND case_id IS NULL ";
    
                pstmtPayment = epcConn.prepareStatement(sql);
                pstmtPayment.setInt(1, orderId);
                rset = pstmtPayment.executeQuery();
                while(rset.next()) {

                    EpcDefaultPayment defaultPayment = new EpcDefaultPayment();
                    defaultPayment.setPaymentId(rset.getInt("payment_id"));
                    defaultPayment.setPaymentType("NORMAL");
                    defaultPayment.setPaymentCode(rset.getString("payment_code"));
                    defaultPayment.setPaymentAmount(rset.getBigDecimal("payment_amount").setScale(2));
                    defaultPayment.setReference1(rset.getString("reference_1"));
                    defaultPayment.setReference2(rset.getString("reference_2"));
                    defaultPayment.setReadOnly(false);
                    defaultPayment.setCaseId("");
                    epcDefaultPaymentList.add(defaultPayment);

                    for(EpcPaymentCtrl epcPaymentCtrl:ecpPaymentCtrlList) {
                        if (epcPaymentCtrl.getPaymentCode().equals(rset.getString("payment_code")) && epcPaymentCtrl.isDefaultPayment()) {

                            defaultPayment.setReadOnly(epcPaymentCtrl.isReadOnly());
                            
                            // set payment info message if any
                            ArrayList<EpcPaymentInfo> epcPaymentInfoList = new ArrayList<EpcPaymentInfo>();
                            if (!"".equals(epcPaymentCtrl.getDefaultRef1Message())) {
                                EpcPaymentInfo epcPaymentInfo = new EpcPaymentInfo();
                                epcPaymentInfo.setAssignTo("reference1");
                                epcPaymentInfo.setInfoMessage(epcPaymentCtrl.getDefaultRef1Message());
                                epcPaymentInfoList.add(epcPaymentInfo);
                            }
                            if (!"".equals(epcPaymentCtrl.getDefaultRef2Message())) {
                                EpcPaymentInfo epcPaymentInfo = new EpcPaymentInfo();
                                epcPaymentInfo.setAssignTo("reference2");
                                epcPaymentInfo.setInfoMessage(epcPaymentCtrl.getDefaultRef2Message());
                                epcPaymentInfoList.add(epcPaymentInfo);
                            }
                            if (!"".equals(epcPaymentCtrl.getDefaultPaymentAmountMessage())) {
                                EpcPaymentInfo epcPaymentInfo = new EpcPaymentInfo();
                                epcPaymentInfo.setAssignTo("paymentAmount");
                                epcPaymentInfo.setInfoMessage(epcPaymentCtrl.getDefaultPaymentAmountMessage());
                                epcPaymentInfoList.add(epcPaymentInfo);
                            }
                            if (epcPaymentInfoList.size() > 0) {
                                defaultPayment.setPaymentInfoList(epcPaymentInfoList);
                            }

                            break;
                        }
                    }
                }
                rset.close();
                pstmtPayment.close();
            }

            // sort output default payments by below order
            // 1) readOnly is true
            // 2) caseId
            // 3) paymentCode
            Comparator<EpcDefaultPayment> defaultPaymentComparator = Comparator.comparing(EpcDefaultPayment::isReadOnly).reversed().thenComparing(EpcDefaultPayment::getCaseId).thenComparing(EpcDefaultPayment::getPaymentCode);
            Collections.sort(epcDefaultPaymentList, defaultPaymentComparator);

            result.setResult("SUCCESS");
            result.setErrorCode("");
            result.setErrorMessage("");
            result.setPaymentList(epcDefaultPaymentList);
            epcConn.commit();

        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            result.setResult("FAIL");
            result.setErrorCode("1003");
            result.setErrorMessage("System Error");
        } finally {
            DBHelper.closeAll(
                rset,
                rset2,
                pstmt,
                pstmtPayment,
                pstmtDefaultInstalmentPaymentCode,
                pstmtGetCharge,
                pstmtGetDefaultPayment
            );
            try { if (epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
        }
        return result;
    }

    //public void createQuoteDefaultPaymentList(EpcDefaultOfferPayment epcDefaultOfferPayment, EpcQuoteProductCandidate productCandidateObj, HashMap<String, Object> metaDataLookup, ArrayList<EpcInstallmentCharge> epcInstallmentChargeList, String msisdn, String parentLabel) throws Exception {
    public void createQuoteDefaultPaymentList(ArrayList<EpcDefaultPayment> epcDefaultPaymentList, EpcQuoteProductCandidate productCandidateObj, HashMap<String, Object> metaDataLookup, ArrayList<EpcInstallmentCharge> epcInstallmentChargeList, String msisdn, String parentLabel) throws Exception {

        try {
            HashMap<String, Object> childMap = (HashMap<String, Object>)metaDataLookup.get(productCandidateObj.getId());
            String typePath = StringHelper.trim((String)childMap.get("typePath"));
            String entityTemplate = null;
            String entityType = null;
            if (!"".equals(typePath) && typePath.split("[/]").length >= 2) {
                entityTemplate = typePath.split("[/]")[0];
                entityType = typePath.split("[/]")[1];
            }

            for (int i=0; i < productCandidateObj.getCharacteristicUse().size(); i++) {
                EpcCharacteristicUse epcCharacteristicUse = productCandidateObj.getCharacteristicUse().get(i);
                if ("VAS_Label".equals(epcCharacteristicUse.getName())) {
                    /*
                    parentLabel = "";
                    for (int j=0; j < epcCharacteristicUse.getValue().size(); j++) {
                        parentLabel += epcCharacteristicUse.getValue().get(j) + "|";
                    }*/
                    parentLabel = (String) epcCharacteristicUse.getValue().stream().collect(Collectors.joining("|"));
                    break;
                } else if ("rmsWithRequiredVASSubscribed".equals(epcCharacteristicUse.getName())) {
                    /*
                    parentLabel = "";
                    for (int j=0; j < epcCharacteristicUse.getValue().size(); j++) {
                        parentLabel += epcCharacteristicUse.getValue().get(j) + "|";
                    }
                     */
                    parentLabel = (String) epcCharacteristicUse.getValue().stream().collect(Collectors.joining("|"));
                    break;
                } else if ("rmsNatureOfRebate".equals(epcCharacteristicUse.getName())) {
                    String rebateNature = (String) epcCharacteristicUse.getValue().get(0);
                    if ("Admin Fee Rebate (PnL Related)".equals(rebateNature)) {
                        parentLabel = "ADMIN";
                    } else if ("Service Fee Rebate (PnL Related)".equals(rebateNature)) {
                        parentLabel = "PLAN";
                    }
                    break;
                }
            }

            if (EpcItemCategory.CHARGE.equals(productCandidateObj.getItemCat())) {

                if(productCandidateObj.getItemCharge() != null && productCandidateObj.getItemCharge().compareTo(BigDecimal.ZERO) != 0 && typePath.matches(".*[/]Non_Recurring_Charge[/].*")) {
                    if ("Admin Fee Upfront Payment".equals(productCandidateObj.getCpqItemDesc())) {
                        parentLabel = "ADMIN";
                    } else if("Mobile Plan Upfront Payment".equals(productCandidateObj.getCpqItemDesc())) {
                        parentLabel = "PLAN";
                    }

                    EpcInstallmentCharge installmentCharge = new EpcInstallmentCharge();
                    installmentCharge.setCatalogItemDesc(productCandidateObj.getCpqItemDesc());
                    installmentCharge.setLabel(parentLabel);
                    installmentCharge.setMsisdn(msisdn);
                    installmentCharge.setChargeAmount(productCandidateObj.getItemCharge());
                    if ("5f9b8a30-6832-4ed0-93c1-e772ce006e16".equals(productCandidateObj.getItemCode2())) {
                        installmentCharge.setAllowInstallment(true);
                        installmentCharge.setHandlingFeeWaive(true);
                    } else if ("f59771cc-0766-4cc8-b812-ed5581ab7c7f".equals(productCandidateObj.getItemCode2())) {
                        installmentCharge.setAllowInstallment(true);
                        installmentCharge.setHandlingFeeWaive(false);
                    } else {
                        installmentCharge.setAllowInstallment(false);
                        installmentCharge.setHandlingFeeWaive(false);
                    }
                    epcInstallmentChargeList.add(installmentCharge);
                }
            }

            if ("Package".equals(entityType)) {
                ArrayList<EpcConfiguredValue> epcConfiguredValue = productCandidateObj.getConfiguredValue();
                for (int i=0; i < epcConfiguredValue.size(); i++) {
                    System.out.println("epcConfiguredValue Name:" + epcConfiguredValue.get(i).getName());
                    if("MSISDN1".equals(epcConfiguredValue.get(i).getName())) {
                        //epcDefaultOfferPayment.setMsisdn(epcConfiguredValue.get(i).getValue());
                        msisdn = epcConfiguredValue.get(i).getValue();
                    }
                }
            }

            if (msisdn == null) {
                if ("Customer Profile".equals(productCandidateObj.getCpqItemDesc())) {
                    ArrayList<EpcConfiguredValue> epcConfiguredValue = productCandidateObj.getConfiguredValue();
                    for (int i=0; i < epcConfiguredValue.size(); i++) {
                        if("MSISDN".equals(epcConfiguredValue.get(i).getName())) {
                            //epcDefaultOfferPayment.setMsisdn(epcConfiguredValue.get(i).getValue());
                            msisdn = epcConfiguredValue.get(i).getValue();
                        }
                    }
                }
            }

            if (msisdn == null) {
                if ("Mobile Product Spec".equals(productCandidateObj.getCpqItemDesc())) {
                    ArrayList<EpcConfiguredValue> epcConfiguredValue = productCandidateObj.getConfiguredValue();
                    for (int i=0; i < epcConfiguredValue.size(); i++) {
                        if("MSISDN".equals(epcConfiguredValue.get(i).getName())) {
                            //epcDefaultOfferPayment.setMsisdn(epcConfiguredValue.get(i).getValue());
                            msisdn = epcConfiguredValue.get(i).getValue();
                        }
                    }
                }
            }

            if (msisdn == null) {
                if ("Child Mobile Product Spec".equals(productCandidateObj.getCpqItemDesc())) {
                    ArrayList<EpcConfiguredValue> epcConfiguredValue = productCandidateObj.getConfiguredValue();
                    for (int i=0; i < epcConfiguredValue.size(); i++) {
                        if("MSISDN".equals(epcConfiguredValue.get(i).getName())) {
                            msisdn = epcConfiguredValue.get(i).getValue();
                        }
                    }
                }
            }

            if(productCandidateObj.getChildEntity() != null) {
                for(int i = 0; i < productCandidateObj.getChildEntity().size(); i++) {
                    //createQuoteDefaultPaymentList(epcDefaultOfferPayment, productCandidateObj.getChildEntity().get(i), metaDataLookup, epcInstallmentChargeList, msisdn, parentLabel);
                    createQuoteDefaultPaymentList(epcDefaultPaymentList, productCandidateObj.getChildEntity().get(i), metaDataLookup, epcInstallmentChargeList, msisdn, parentLabel);
                }
            } else {
                // nothing to do
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw e;
        }
        return;
    }

    public BigDecimal getEligibleInstallmentPaymentAmount(ArrayList<EpcInstallmentCharge> epcInstallmentChargeList, boolean isHandlingFeeWaive, ArrayList<EpcInstallmentCharge> generalInstallmentChargeList) throws Exception {
        BigDecimal resultPaymentAmount = new BigDecimal(0);
        BigDecimal handsetAmount = new BigDecimal(0);
        BigDecimal accessoryAmount = new BigDecimal(0);
        BigDecimal handsetAndAccessoryAmount;
        ArrayList<EpcInstallmentCharge> targetChargeList;
        ArrayList<EpcInstallmentCharge> remainingChargeList;
        String tmpLogStr = "";

        try {

            targetChargeList = new ArrayList<EpcInstallmentCharge>();
            remainingChargeList = new ArrayList<EpcInstallmentCharge>();
            for(EpcInstallmentCharge charge:epcInstallmentChargeList) {
                if (charge.isAllowInstallment() && charge.isHandlingFeeWaive() == isHandlingFeeWaive) {
                    targetChargeList.add(charge);
                }
                remainingChargeList.add(charge);
            }			
			
			
            for(EpcInstallmentCharge charge:targetChargeList) {
                if (
                    "Handset Prepayment Charge".equals(charge.getCatalogItemDesc()) ||
                    "Handset One Time Charge".equals(charge.getCatalogItemDesc())
                    ) 
                {
                    if (remainingChargeList.remove(charge)) {
                        handsetAmount = handsetAmount.add(charge.getChargeAmount());
                    }
                } else if (
                     "Accessory One Time Charges".equals(charge.getCatalogItemDesc()) ||
                     "Accessory Charge".equals(charge.getCatalogItemDesc())
                    )
                {
                    if (remainingChargeList.remove(charge)) {
                        accessoryAmount = accessoryAmount.add(charge.getChargeAmount());
                    }

                    for (EpcInstallmentCharge charge2:epcInstallmentChargeList) {
                        //if (charge2.getChargeType().equals("Accessory Discount Charge") && charge.getLabel().equals(charge2.getLabel()) && charge.getCaseId().equals(charge2.getCaseId()) && charge.getMsisdn().equals(charge2.getMsisdn())) {
                        if (charge2.getCatalogItemDesc().equals("Accessory Discount Charge") && charge.getLabel().equals(charge2.getLabel()) && charge.getCaseId().equals(charge2.getCaseId())) {
                            if (remainingChargeList.remove(charge2)) {
                                accessoryAmount = accessoryAmount.add(charge2.getChargeAmount());
                            }
                        }
                    }
                } else if (
                     "Admin Fee Upfront Payment".equals(charge.getCatalogItemDesc()) ||
                     "Mobile Plan Upfront Payment".equals(charge.getCatalogItemDesc()) ||
                     "VAS Upfront Payment".equals(charge.getCatalogItemDesc())
                    ) 
                {
                    resultPaymentAmount = resultPaymentAmount.add(charge.getChargeAmount());
                    remainingChargeList.remove(charge);
                    for (EpcInstallmentCharge charge2:epcInstallmentChargeList) {
                        //if (charge != charge2 && charge.getLabel().equals(charge2.getLabel()) && charge.getCaseId().equals(charge2.getCaseId()) && charge.getMsisdn().equals(charge2.getMsisdn())) {
                        if (charge != charge2 && compareVASLabel(charge2.getLabel(), charge.getLabel(), "") && charge.getCaseId().equals(charge2.getCaseId()) && charge.getMsisdn().equals(charge2.getMsisdn())) {
                            if (remainingChargeList.remove(charge2)) {
                                resultPaymentAmount = resultPaymentAmount.add(charge2.getChargeAmount());
                            }
                        }
                    }
                } else {
                    if (remainingChargeList.remove(charge)) {
                        resultPaymentAmount = resultPaymentAmount.add(charge.getChargeAmount());
                    }
                }
            }

            handsetAndAccessoryAmount = handsetAmount.add(accessoryAmount);

            if (handsetAndAccessoryAmount.compareTo(BigDecimal.ZERO) > 0) {
                resultPaymentAmount = resultPaymentAmount.add(handsetAndAccessoryAmount);
                for(EpcInstallmentCharge charge:epcInstallmentChargeList) {
                    if (
                        "Trade In Value Charge".equals(charge.getCatalogItemDesc()) ||
                        "Trade In Bonus Charge".equals(charge.getCatalogItemDesc()) ||
                        "Voucher Discount Charge".equals(charge.getCatalogItemDesc()) ||
                        "Mobile Payment Handset Discount".equals(charge.getCatalogItemDesc()) ||
                        "Net Handset Price Calculation Discount Charge".equals(charge.getCatalogItemDesc()) ||
                        "Birthday Gift Discount Charge".equals(charge.getCatalogItemDesc()) ||
                        "default_payment".equals(charge.getCatalogItemDesc()) ||
                        ("99".equals(charge.getChargeCode()) && "Y".equals(charge.getPaid()))
                    ) {
                        if (resultPaymentAmount.add(charge.getChargeAmount()).compareTo(BigDecimal.ZERO) >= 0) {
                            if (remainingChargeList.remove(charge)) {
                                resultPaymentAmount = resultPaymentAmount.add(charge.getChargeAmount());
                            }
                        } else {
                            charge.setChargeAmount(resultPaymentAmount.add(charge.getChargeAmount()));
                            resultPaymentAmount = BigDecimal.ZERO;
                        }
                    }
                }
            }

            for (EpcInstallmentCharge tmpCharge:remainingChargeList) {
                tmpLogStr = epcSecurityHelper.encodeForSQL(tmpCharge.getCatalogItemDesc() + "|" + tmpCharge.getLabel() + "|" + tmpCharge.getChargeAmount());
                logger.info(tmpLogStr);
            }
            generalInstallmentChargeList.addAll(remainingChargeList);

        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw e;
        }
        return resultPaymentAmount;
    }

    /*public EpcValidatePaymentResult validatePayment(EpcValidatePayment epcValidatePayment) {

        Connection epcConn = null;
        Connection fesConn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        PreparedStatement pstmt4 = null;
        ResultSet rset = null;
        ResultSet rset2 = null;
        int orderId;
        EpcValidatePaymentResult result = new EpcValidatePaymentResult();
        ArrayList<EpcValidatePaymentError> errorList = new ArrayList<EpcValidatePaymentError>();
        EpcControlTbl enquiryControl;
        ArrayList<EpcControlTbl> resultControlList;
        BigDecimal maxInstallmentAmount;
        HashMap <String, BigDecimal> grandChargeMap = new HashMap<String, BigDecimal>();
        HashMap <String, BigDecimal> grandPaymentMap = new HashMap<String, BigDecimal>();
        HashMap <String, BigDecimal> reqChargeMap = new HashMap<String, BigDecimal>();
        HashMap <String, BigDecimal> reqPaymentMap = new HashMap<String, BigDecimal>();
        HashMap <String, BigDecimal> offerChargeMap = new HashMap<String, BigDecimal>();
        ArrayList<EpcValidateItem> validateItemList;
        ArrayList<EpcCharge> inputChargeList;
        ArrayList<EpcPayment> inputPaymentList;
        boolean hasMobilePayment = false;
        boolean hasRequiredPaymentCode = false;
        boolean isInvalidInputCharge = false;
        result.setErrorList(errorList);
        int seqId = -1;

        try {

            epcConn = epcDataSource.getConnection();
            epcConn.setAutoCommit(false);
            fesConn = fesDataSource.getConnection();
            fesConn.setAutoCommit(false);

            inputChargeList = (ArrayList<EpcCharge>) getSanityList(epcValidatePayment.getChargeList());
            inputPaymentList = (ArrayList<EpcPayment>) getSanityList(epcValidatePayment.getPaymentList());

            // check input argument format
            if (epcValidatePayment.getOrderId() == null) {
                EpcValidatePaymentError error = new EpcValidatePaymentError();
                error.setErrorCode(EpcValidatePaymentError.E1001);
                error.setErrorMessage("Missing Order Id");
                errorList.add(error);
            }
			
            // added by Danny Chan on 2023-9-4: check salesman in validatePayment API - start
            if (epcValidatePayment.getSalesman()!=null) {
                FesUser fesUser = fesUserHandler.getUserByUsername( epcValidatePayment.getSalesman() );
            
                if (fesUser.getUsername()==null) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1001);
                    error.setErrorMessage("Salesman is invalid");
                    errorList.add(error);
                }
            }
            // added by Danny Chan on 2023-9-4: check salesman in validatePayment API - end
			
            pstmt = fesConn.prepareStatement("SELECT 1 FROM zz_pservice WHERE service_code = ? AND service_ind = ? ");

            ArrayList<EpcPaymentCode> availablePaymentCodeList = getPaymentCodeList(null);     // modified by Danny Chan on 2022-10-11 (nehancement of payment page to support SHKP)

            // added by Danny Chan on 2022-11-17 (SHK Point Payment Enhancement): start
            enquiryControl = new EpcControlTbl();
            enquiryControl.setRecType("MAX_ONE_PAYMENT_CODE");
            resultControlList = epcControlHandler.getControl(enquiryControl);        
        
            ArrayList<String> tooManyRecordsPaymentCodeList = new ArrayList();
        
            for (int i=0; i<resultControlList.size(); i++) {
                EpcControlTbl item = resultControlList.get(i);
                String payment_code = item.getKeyStr1(); 
        
                int count = 0;
    
                for (EpcPayment payment:inputPaymentList) {
                    if (payment.getPaymentCode().equals(payment_code)) {
                        count++;
                    }
                }

                if (count>1) {
                    tooManyRecordsPaymentCodeList.add(payment_code);
                }
            }
            // added by Danny Chan on 2022-11-17 (SHK Point Payment Enhancement): end
        
            //for (EpcPayment payment:epcValidatePayment.getPaymentList()) {
            for (EpcPayment payment:inputPaymentList) {
                if ("".equals(StringHelper.trim(payment.getPaymentCode()))) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1002);
                    error.setErrorMessage("Missing Payment Code");
                    error.setErrorPaymentId(payment.getPaymentId());
                    errorList.add(error);
                    continue;
                }

                // added by Danny Chan on 2022-11-16 (SHK Point Payment Enhancement): start 
                if (tooManyRecordsPaymentCodeList.contains(payment.getPaymentCode())) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1008);
                    error.setErrorMessage("There should be only 1 record for payment code " + payment.getPaymentCode());
                    error.setErrorPaymentId(payment.getPaymentId());
                    errorList.add(error);
                    continue;
                }
                // added by Danny Chan on 2022-11-16 (SHK Point Payment Enhancement): end
        
                pstmt.setString(1, payment.getPaymentCode());
                pstmt.setString(2, "N");
                rset = pstmt.executeQuery();
                if (!rset.next()) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1003);
                    error.setErrorMessage("Invalid Payment Code " + payment.getPaymentCode());
                    error.setErrorPaymentId(payment.getPaymentId());
                    errorList.add(error);
                    continue;
                }
                rset.close();

                if (payment.getPaymentAmount() == null) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1004);
                    error.setErrorMessage("Missing Payment Amount");
                    error.setErrorPaymentId(payment.getPaymentId());
                    errorList.add(error);
                    continue;
                }

                // added by Danny Chan on 2022-11-16 (SHK Point Payment Enhancement): start 
                if ( "SHKP".equals(StringHelper.trim(payment.getPaymentCode())) ) {
                    if ( payment.getPaymentAmount()==null || payment.getPaymentAmount().remainder(new BigDecimal(1)).compareTo(new BigDecimal(0))!=0  ) {
                        EpcValidatePaymentError error = new EpcValidatePaymentError();
                        error.setErrorCode(EpcValidatePaymentError.E1007);
                        error.setErrorMessage("Payment amount for SHKP should be a multiple of 1");
                        error.setErrorPaymentId(payment.getPaymentId());
                        errorList.add(error);
                        continue;
                    } 
					
					if (payment.getPaymentAmount().compareTo(new BigDecimal(10))<0) {
						EpcValidatePaymentError error = new EpcValidatePaymentError();
                        error.setErrorCode(EpcValidatePaymentError.E1007);
                        error.setErrorMessage("Payment amount for SHKP should be at least 10.");
                        error.setErrorPaymentId(payment.getPaymentId());
                        errorList.add(error);
                        continue;						
					}
                }
                // added by Danny Chan on 2022-11-16 (SHK Point Payment Enhancement): end
        
                // added by Danny Chan on 2022-6-7: start
                boolean isBankInstallment = false, isCreditCard = false;

                for (int i=0; i<availablePaymentCodeList.size(); i++) {
                    EpcPaymentCode availablePaymentCode = availablePaymentCodeList.get(i);

                    if (payment.getPaymentCode().equals(availablePaymentCode.getPaymentCode())) {
                        if ( availablePaymentCode.isBankInstallment() ) {
                            isBankInstallment = true;
                        }
                        
                        if ( availablePaymentCode.isCreditCardType() ) {
                            isCreditCard = true;
                        }
                    }
                }

                if (isBankInstallment) {
                    if (payment.getReference1()==null || payment.getReference1().equals("")) {
                        EpcValidatePaymentError error = new EpcValidatePaymentError();
                        error.setErrorCode(EpcValidatePaymentError.E1006);
                        error.setErrorMessage("Missing credit card number for installment payment");
                        error.setErrorPaymentId(payment.getPaymentId());
                        errorList.add(error);
                    }
                } else {
                    if (isCreditCard) {
                        if (payment.getReference1()==null || payment.getReference1().equals("")) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E1006);
                            error.setErrorMessage("Missing credit card number for credit card payment");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                        }
                    }
                }
                // added by Danny Chan on 2022-6-7: end
				
            }

            if (errorList.size() == 0) {

                orderId = Integer.parseInt(epcSecurityHelper.validateId(epcValidatePayment.getOrderId()));


                // check total payment amount = total charge amount
                BigDecimal totalPaymentAmount = new BigDecimal(0);
                BigDecimal totalChargeAmount = new BigDecimal(0);
                for (EpcPayment payment:epcValidatePayment.getPaymentList()) {
                    totalPaymentAmount = totalPaymentAmount.add(payment.getPaymentAmount());
                }

                //pstmt = epcConn.prepareStatement("SELECT NVL(SUM(charge_amount), 0) total_charge_amount FROM epc_order_charge WHERE order_id = ? AND need_to_pay = ? AND paid = ? ");
                //pstmt.setInt(1, orderId);
                //pstmt.setString(2,  "Y");
                //pstmt.setString(3,  "N");
                //rset = pstmt.executeQuery();
                //if (rset.next()) {
                //    totalChargeAmount = rset.getBigDecimal("total_charge_amount");
                //}
                //rset.close();
                //pstmt.close();

                //if (totalPaymentAmount.compareTo(totalChargeAmount) != 0) {
                //    EpcValidatePaymentError error = new EpcValidatePaymentError();
                //    error.setErrorCode(EpcValidatePaymentError.E2001);
                //    error.setErrorMessage("Total Charge Amount is not equals to total Payment Amount");
                //    errorList.add(error);
                //}

                // get validate items from quote
                validateItemList = getValidateItemFromQuote(orderId);

                HashMap<String, ArrayList<EpcInstallmentCharge>> installmentChargeMap = new HashMap<String, ArrayList<EpcInstallmentCharge>>();
                HashMap<String, ArrayList<EpcPayment>> installmentPaymentMap = new HashMap<String, ArrayList<EpcPayment>>();
                ArrayList<EpcInstallmentCharge> installmentChargeList = new ArrayList<EpcInstallmentCharge>();
                HashMap<String, BigDecimal> maxInstallmentAmountMap = new HashMap<String, BigDecimal>();
                HashMap<String, BigDecimal> installmentAmountMap = new HashMap<String, BigDecimal>();

                if (inputChargeList == null || inputChargeList.size() == 0) {
                    pstmt = epcConn.prepareStatement("SELECT case_id, charge_desc, item_id, parent_item_id, item_code, charge_amount, allow_installment, handling_fee_waive, " +
                                                     "msisdn, charge_code, seq_id, need_to_pay, paid, catalog_item_desc " +
                                                     "FROM epc_order_charge " +
                                                     "WHERE order_id = ? " +
                                                     "AND need_to_pay = ? " +
                                                     "AND paid = ? " +
                                                     "ORDER BY case_id");
                    pstmt.setInt(1, orderId);
                    pstmt.setString(2, "Y");
                    pstmt.setString(3, "N");
                } else {
                    pstmt = epcConn.prepareStatement("SELECT case_id, charge_desc, item_id, parent_item_id, item_code, charge_amount, allow_installment, handling_fee_waive, " +
                                                     "msisdn, charge_code, seq_id, need_to_pay, paid, catalog_item_desc " +
                                                     "FROM epc_order_charge " +
                                                     "WHERE order_id = ? " +
                                                     "AND need_to_pay = ? " +
                                                     "ORDER BY case_id");
                    pstmt.setInt(1, orderId);
                    pstmt.setString(2, "Y");
                }
                rset = pstmt.executeQuery();
                while (rset.next()) {

                    EpcInstallmentCharge installmentCharge = new EpcInstallmentCharge();
                    installmentCharge.setCaseId(StringHelper.trim(rset.getString("case_id")));
                    installmentCharge.setMsisdn(StringHelper.trim(rset.getString("msisdn")));
                    installmentCharge.setChargeCode(StringHelper.trim(rset.getString("charge_code")));
                    installmentCharge.setCatalogItemDesc(StringHelper.trim(rset.getString("catalog_item_desc")));
                    installmentCharge.setLabel(StringHelper.trim(rset.getString("item_code")));
                    installmentCharge.setChargeAmount(rset.getBigDecimal("charge_amount"));
                    installmentCharge.setAllowInstallment("Y".equals(rset.getString("allow_installment")));
                    installmentCharge.setHandlingFeeWaive("Y".equals(rset.getString("handling_fee_waive")));
                    installmentCharge.setSeqId(rset.getInt("seq_id"));
                    installmentCharge.setItemId(StringHelper.trim(rset.getString("item_id")));
                    installmentCharge.setParentItemId(StringHelper.trim(rset.getString("parent_item_id")));
                    installmentCharge.setNeedToPay(rset.getString("need_to_pay"));
                    installmentCharge.setPaid(rset.getString("paid"));

                    if (inputChargeList != null && inputChargeList.size() > 0) {
                        isInvalidInputCharge = true;
                        for(EpcCharge inputCharge:inputChargeList) {
                            if (inputCharge.equals(installmentCharge)) {
                                isInvalidInputCharge = false;
                                break;
                            }
                        }
                        if (isInvalidInputCharge) {
                            continue;
                        }
                    }

                    String caseId = StringHelper.trim(rset.getString("case_id"));
                    if (installmentChargeMap.get(caseId) == null) {
                        installmentChargeMap.put(caseId, new ArrayList<EpcInstallmentCharge>());
                    }
                    if (inputChargeList != null && inputChargeList.size() > 0 && "99".equals(installmentCharge.getChargeCode())) {
                        installmentCharge.setChargeAmount(installmentCharge.getChargeAmount().negate());
                    }
                    installmentChargeMap.get(caseId).add(installmentCharge);
                    installmentChargeList.add(installmentCharge);

                    if (offerChargeMap.get(caseId) == null) {
                        offerChargeMap.put(caseId, new BigDecimal(0));
                    }
                    offerChargeMap.put(caseId, offerChargeMap.get(caseId).add(installmentCharge.getChargeAmount()));


                    if (grandChargeMap.get(rset.getString("charge_code")) == null) {
                        grandChargeMap.put(rset.getString("charge_code"), new BigDecimal(0));
                    }
                    grandChargeMap.put(rset.getString("charge_code"), grandChargeMap.get(rset.getString("charge_code")).add(installmentCharge.getChargeAmount()));

                    totalChargeAmount = totalChargeAmount.add(installmentCharge.getChargeAmount());
                }
                rset.close();
                pstmt.close();
                
                // add those default payment like COUP, tradein and etc as charges for instalment calculation
                pstmt = epcConn.prepareStatement("SELECT a.payment_code, a.case_id, a.payment_amount * -1 payment_amount " +
                                                 "FROM epc_order_payment a, epc_payment_ctrl c " +
                                                 "WHERE a.order_id = ? " +
                                                 "AND a.payment_code = c.payment_code " +
                                                 "AND c.default_payment = ? " +
                                                 "AND a.tx_no IS NULL " +
                                                 "ORDER BY a.case_id");
                pstmt.setInt(1, orderId);
                pstmt.setString(2, "Y"); //default_payment
                rset = pstmt.executeQuery();
                while (rset.next()) {
                
                    String caseId = StringHelper.trim(rset.getString("case_id"));
                
                    EpcInstallmentCharge installmentCharge = new EpcInstallmentCharge();
                    installmentCharge.setCaseId(caseId);
                    installmentCharge.setMsisdn("");
                    installmentCharge.setChargeCode(StringHelper.trim(rset.getString("payment_code")));
                    installmentCharge.setCatalogItemDesc("default_payment");
                    installmentCharge.setLabel("");
                    installmentCharge.setChargeAmount(rset.getBigDecimal("payment_amount"));
                    installmentCharge.setAllowInstallment(false);
                    installmentCharge.setHandlingFeeWaive(false);
                    installmentCharge.setSeqId(seqId--);
                    installmentCharge.setItemId("");
                    installmentCharge.setParentItemId("");
                    installmentCharge.setNeedToPay("Y");
                    installmentCharge.setPaid("N");

                    if (installmentChargeMap.get(caseId) == null) {
                        installmentChargeMap.put(caseId, new ArrayList<EpcInstallmentCharge>());
                    }
                    installmentChargeMap.get(caseId).add(installmentCharge);
                    installmentChargeList.add(installmentCharge);
                }
                rset.close();
                pstmt.close();

                logger.info("installmentChargeList size:" + installmentChargeList.size());
                if (inputChargeList != null) {
                    logger.info("inputChargeList size:" + inputChargeList.size());
                } else {
                    logger.info("inputChargeList is null");
                }

                if (inputChargeList != null && inputChargeList.size() > 0 && installmentChargeList.size() != inputChargeList.size()) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E1005);
                    error.setErrorMessage("Invalid charges for validation");
                    errorList.add(error);
                    return result;
                }

                if (totalPaymentAmount.compareTo(totalChargeAmount) != 0) {
                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                    error.setErrorCode(EpcValidatePaymentError.E2001);
                    error.setErrorMessage("Total Charge Amount is not equals to total Payment Amount");
                    errorList.add(error);
                }

                ArrayList<EpcInstallmentCharge> generalInstallmentChargeList = new ArrayList<EpcInstallmentCharge>();
                for (Map.Entry<String, ArrayList<EpcInstallmentCharge>> entry : installmentChargeMap.entrySet()) {
                    if (!"".equals(entry.getKey())) {						
                        maxInstallmentAmount = getEligibleInstallmentPaymentAmount(entry.getValue(), true, generalInstallmentChargeList);
                        maxInstallmentAmountMap.put(entry.getKey(), maxInstallmentAmount);
                    } else {
                        generalInstallmentChargeList.addAll(entry.getValue());
                    }
                }

                maxInstallmentAmount = getEligibleInstallmentPaymentAmount(generalInstallmentChargeList, false, generalInstallmentChargeList);
                maxInstallmentAmountMap.put("", maxInstallmentAmount);

                pstmt = fesConn.prepareStatement("SELECT NVL(SUM(d.amount), 0) receipt_amount FROM zz_prec_hdr h, zz_prec_chg_dtl d " +
                                                 "WHERE h.receipt_no = d.receipt_no "+
                                                 "AND h.receipt_no = ? " +
                                                 "AND h.stat = ? " +
                                                 "AND d.charge_type = ? "
                                               );
                pstmt2 = fesConn.prepareStatement("SELECT NVL(SUM(amount), 0) redeemed_amount " +
                                                  "FROM zz_prec_pay_ref p " +
                                                  "WHERE payment_code = ? " +
                                                  "AND reference1 = ? " +
                                                  "AND NOT EXISTS (SELECT 1 FROM zz_pvoid_rec WHERE receipt_no = p.receipt_no)"
                                                  );
                pstmt3 = fesConn.prepareStatement("SELECT 1 from zz_pass_reg x, zz_prec_hdr y " +
                                                  "WHERE x.refund_receipt = ? " +
                                                  "AND x.invoice_no = y.receipt_no " +
                                                  "AND y.stat = ? "
                                                  );
                
                //for (EpcPayment payment:epcValidatePayment.getPaymentList()) {
                for (EpcPayment payment:inputPaymentList) {

                    // Control: check payment vs charge
                    // (1) check reference1 as prerequisite receipt number
                    // (2) prerequisite charge type in input receipt is correct
                    // (3) no duplicate input of prerequisite receipt
                    // (4) amount of prerequisite receipt is correct
                    // (5) amount of prerequisite receipt is not used up
                    enquiryControl = new EpcControlTbl();
                    enquiryControl.setRecType("PAYMENT_VS_CHARGE");
                    enquiryControl.setKeyStr1(payment.getPaymentCode());
                    resultControlList = epcControlHandler.getControl(enquiryControl);

                    if (resultControlList.size() > 0) {
                        BigDecimal redeemedAmount = BigDecimal.ZERO;
                        BigDecimal inputReceiptAmount = BigDecimal.ZERO;
                        int paymentCount = 0;

                        if ("".equals(StringHelper.trim(payment.getReference1()))) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2002);
                            error.setErrorMessage("Missing Receipt Number");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }

                        pstmt.setString(1, payment.getReference1());
                        pstmt.setString(2, "N");
                        pstmt.setString(3, resultControlList.get(0).getValueStr1());
                        rset = pstmt.executeQuery();
                        if (rset.next()) {
                            inputReceiptAmount = rset.getBigDecimal("receipt_amount");
                        }
                        rset.close();

                        if (inputReceiptAmount.compareTo(BigDecimal.ZERO) <= 0) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2003);
                            error.setErrorMessage("Invalid Receipt Number " + payment.getReference1());
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }

                        //for (EpcPayment payment2:epcValidatePayment.getPaymentList()) {
                        for (EpcPayment payment2:inputPaymentList) {
                            if (payment.getPaymentCode().equals(payment2.getPaymentCode()) && payment.getReference1().equals(payment2.getReference1())) {
                                paymentCount++;
                            }
                        }
                        if (paymentCount >= 2) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2004);
                            error.setErrorMessage("Duplicated Receipt Number " + payment.getReference1());
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }

                        pstmt2.setString(1, payment.getPaymentCode());
                        pstmt2.setString(2, payment.getReference1());
                        rset = pstmt2.executeQuery();
                        if (rset.next()) {
                            redeemedAmount = rset.getBigDecimal("redeemed_amount");
                        }
                        rset.close();

                        if (redeemedAmount.compareTo(inputReceiptAmount) >= 0) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2005);
                            error.setErrorMessage("Receipt "+ payment.getReference1() +" has already been fully used.");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }

                        pstmt3.setString(1, payment.getReference1());
                        pstmt3.setString(2, "N");
                        rset = pstmt3.executeQuery();
                        if (rset.next()) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2005);
                            error.setErrorMessage("Receipt "+ payment.getReference1() +" has already been fully used.");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }
                        rset.close();

                        if (redeemedAmount.add(payment.getPaymentAmount()).compareTo(inputReceiptAmount) > 0) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2006);
                            error.setErrorMessage("Input Payment Amount is too much");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                            continue;
                        }
                    }

                    enquiryControl = new EpcControlTbl();
                    enquiryControl.setRecType("INSTALL_PAYMENT");
                    enquiryControl.setKeyStr1(payment.getPaymentCode());
                    resultControlList = epcControlHandler.getControl(enquiryControl);

                    // added by Danny Chan on 2023-4-20: start
                    for (int y=0; y<resultControlList.size(); y++) {
                        EpcControlTbl tbl = resultControlList.get(y);
                        BigDecimal minAmount = tbl.getValueNumber1();

                        if (minAmount!=null && payment.getPaymentAmount().compareTo(minAmount)<0) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2014);
                            error.setErrorMessage("Installment is not allowed as installment payment amount is not enough.");
                            error.setErrorPaymentId(payment.getPaymentId());
                            errorList.add(error);
                        }
                    }
                    // added by Danny Chan on 2023-4-20: end

                    if (resultControlList.size() > 0) {
                        String caseId = StringHelper.trim(payment.getCaseId());
                        if (installmentAmountMap.get(caseId) == null) {
                            installmentAmountMap.put(caseId, payment.getPaymentAmount());
                        } else {
                            installmentAmountMap.put(caseId, installmentAmountMap.get(caseId).add(payment.getPaymentAmount()));
                        }
                        if (installmentPaymentMap.get(caseId) == null) {
                            installmentPaymentMap.put(caseId, new ArrayList<EpcPayment>());
                        }
                        installmentPaymentMap.get(caseId).add(payment);
                    }

                    // check exists mobile payment
                    //if (validateItem.isNeedMobilePayment()) {
                    //    enquiryControl = new EpcControlTbl();
                    //    enquiryControl.setRecType("MOBILE_PAYMENT");
                    //    enquiryControl.setKeyStr1(payment.getPaymentCode());
                    //    resultControlList = epcControlHandler.getControl(enquiryControl);
                    //    if (resultControlList.size() > 0) {
                    //        hasMobilePayment = true;
                    //    }
                    //}

                    if (grandPaymentMap.get(payment.getPaymentCode()) == null) {
                        grandPaymentMap.put(payment.getPaymentCode(), payment.getPaymentAmount());
                    } else {
                        grandPaymentMap.put(payment.getPaymentCode(), grandPaymentMap.get(payment.getPaymentCode()).add(payment.getPaymentAmount()));
                    }

                    if (reqPaymentMap.get(payment.getPaymentCode()) == null) {
                        reqPaymentMap.put(payment.getPaymentCode(), payment.getPaymentAmount());
                    } else {
                        reqPaymentMap.put(payment.getPaymentCode(), reqPaymentMap.get(payment.getPaymentCode()).add(payment.getPaymentAmount()));
                    }

                }

                // validate installment amount
                for (Map.Entry<String, BigDecimal> entry : installmentAmountMap.entrySet()) {					
                    maxInstallmentAmount = maxInstallmentAmountMap.get(entry.getKey());
                    if (maxInstallmentAmount == null) {
                        maxInstallmentAmount = BigDecimal.ZERO;
                    }
                    logger.info("case_id:" + entry.getKey());
                    logger.info("installmentAmount:" + entry.getValue());
                    logger.info("maxInstallmentAmount:" + maxInstallmentAmount);
                    if (entry.getValue().compareTo(maxInstallmentAmount) > 0 && maxInstallmentAmount.compareTo(BigDecimal.ZERO) == 0) {
                        if (installmentPaymentMap.get(entry.getKey()) != null) {
                            for(EpcPayment payment:installmentPaymentMap.get((entry.getKey()))) {
                                EpcValidatePaymentError error = new EpcValidatePaymentError();
                                error.setErrorCode(EpcValidatePaymentError.E2007);
                                error.setErrorMessage("No installment is allowed");
                                error.setErrorPaymentId(payment.getPaymentId());
                                errorList.add(error);
                            }
                        }
                    } else if (entry.getValue().compareTo(maxInstallmentAmount) > 0) {
                        if (installmentPaymentMap.get(entry.getKey()) != null) {
                            for(EpcPayment payment:installmentPaymentMap.get((entry.getKey()))) {
                                EpcValidatePaymentError error = new EpcValidatePaymentError();
                                error.setErrorCode(EpcValidatePaymentError.E2008);
                                error.setErrorMessage("Invalid Installment Amount (Max: $" + maxInstallmentAmount.setScale(2) + ")");
                                error.setErrorPaymentId(payment.getPaymentId());
                                errorList.add(error);
                            }
                        }
                    }
                }

                // validate mobile payment
                //if (validateItem.isNeedMobilePayment() && !hasMobilePayment) {
                //    EpcValidatePaymentError error = new EpcValidatePaymentError();
                //    error.setErrorCode(EpcValidatePaymentError.E2009);
                //    error.setErrorMessage("Missing mobile payment");
                //    errorList.add(error);
                //}

                // validate required payment by charge

                for (Map.Entry<String, BigDecimal> charge : grandChargeMap.entrySet()) {
                    enquiryControl = new EpcControlTbl();
                    enquiryControl.setRecType("PAYMENT_FOR_CHARGE");
                    enquiryControl.setKeyStr1(charge.getKey());
                    resultControlList = epcControlHandler.getControl(enquiryControl);
                    if (resultControlList.size() > 0 ) {
                        reqChargeMap.put(charge.getKey(), charge.getValue());
                        ArrayList<String> tmpPaymentCodeList = new ArrayList<String>();
                        for (int i=0; i < resultControlList.size(); i++) {
                            for (Map.Entry<String, BigDecimal> payment : reqPaymentMap.entrySet()) {
                                if (payment.getKey().equals(resultControlList.get(i).getValueStr1())) {
                                    tmpPaymentCodeList.add(payment.getKey());
                                    if (reqChargeMap.get(charge.getKey()).compareTo(BigDecimal.ZERO) > 0) {
                                        if (reqPaymentMap.get(payment.getKey()).subtract(reqChargeMap.get(charge.getKey())).compareTo(BigDecimal.ZERO) >= 0) {
                                            reqPaymentMap.put(payment.getKey(), reqPaymentMap.get(payment.getKey()).subtract(reqChargeMap.get(charge.getKey())));
                                            reqChargeMap.put(charge.getKey(), new BigDecimal(0));
                                        } else {
                                            reqPaymentMap.put(payment.getKey(), new BigDecimal(0));
                                            reqChargeMap.put(charge.getKey(), reqChargeMap.get(charge.getKey()).subtract(reqPaymentMap.get(payment.getKey())));
                                        }
                                    }
                                }
                            }
                        }
                        if (reqChargeMap.get(charge.getKey()).compareTo(BigDecimal.ZERO) > 0) {
                            EpcValidatePaymentError error = new EpcValidatePaymentError();
                            error.setErrorCode(EpcValidatePaymentError.E2009);
                            error.setErrorMessage("Missing or Invalid Payment Amount(required Payment: "+ tmpPaymentCodeList.stream().collect(Collectors.joining(",")) +")");
                            errorList.add(error);
                        }
                    }
                }

                // check exists mobile payment
                validateItemLoop:
                for (EpcValidateItem epcValidateItem:validateItemList) {

                    //DEBUG
                    logger.info("epcValidateItem:" + epcValidateItem.getCaseId());

                    // find any charge for the offer is required to pay. If no, skip the checking of that validation item
                    if (offerChargeMap.get(epcValidateItem.getCaseId()) == null) {
                        continue validateItemLoop;
                    }

                    if (offerChargeMap.get(epcValidateItem.getCaseId()).compareTo(BigDecimal.ZERO) <= 0) {
                        continue validateItemLoop;
                    }

                    // added by Danny Chan on 2022-6-2: start
                    if (epcValidateItem instanceof EpcRequiredCardPrefix) {
                        List cardPrefixList  = ((EpcRequiredCardPrefix)epcValidateItem).getCardPrefixList();
                        for (EpcPayment payment:inputPaymentList) {
                            boolean isCreditCard = false;
                            for (int i=0; i<availablePaymentCodeList.size(); i++) {
                                EpcPaymentCode availablePaymentCode = availablePaymentCodeList.get(i);
                                if (payment.getPaymentCode().equals(availablePaymentCode.getPaymentCode())) {
                                    if ( availablePaymentCode.isCreditCardType() ) {
                                        isCreditCard = true;
                                    }
                                }
                            }

                            if (!isCreditCard) {
                                continue;
                            }

                            boolean isValidCreditCardPrefix = false;

                            for (Object cardPrefix: cardPrefixList) {
                                if (payment.getReference1().startsWith((String)cardPrefix)) {
                                    isValidCreditCardPrefix = true;
                                    break;
                                }
                            }

                            if (!isValidCreditCardPrefix) {
                                EpcValidatePaymentError error = new EpcValidatePaymentError();
                                error.setErrorCode(EpcValidatePaymentError.E2013);
                                error.setErrorMessage("Invalid credit card prefix");
                                error.setErrorPaymentId(payment.getPaymentId());
                                errorList.add(error);
                            }
                        }
                    }
                    // added by Danny Chan on 2022-6-2: end


                    //if (installmentChargeMap.get(epcValidateItem.getCaseId()) != null) {
                    //    for(EpcCharge epcCharge:installmentChargeMap.get(epcValidateItem.getCaseId())) {
                    //        if ("99".equals(epcCharge.getChargeCode())) {
                    //            continue validateItemLoop;
                    //        }
                    //    }
                    //}

                    if (epcValidateItem instanceof EpcRequiredPaymentCode) {
                        hasMobilePayment = false;
                        hasRequiredPaymentCode = false;
                        for (String requiredPaymentCode:((EpcRequiredPaymentCode) epcValidateItem).getPaymentCodeList()) {
                            if ("Mobile Payment".equals(requiredPaymentCode)) {
                                //for (EpcPayment payment:epcValidatePayment.getPaymentList()) {
                                for (EpcPayment payment:inputPaymentList) {
                                    enquiryControl = new EpcControlTbl();
                                    enquiryControl.setRecType("MOBILE_PAYMENT");
                                    enquiryControl.setKeyStr1(payment.getPaymentCode());
                                    resultControlList = epcControlHandler.getControl(enquiryControl);
                                    if (resultControlList.size() > 0) {
                                        hasMobilePayment = true;
                                    }
                                }
                                if (!hasMobilePayment) {
                                    EpcValidatePaymentError error = new EpcValidatePaymentError();
                                    error.setErrorCode(EpcValidatePaymentError.E2010);
                                    error.setErrorMessage("Missing mobile payment");
                                    errorList.add(error);
                                }
                            }
                        }
                        // check required payment
                        if (!hasMobilePayment || (hasMobilePayment && ((EpcRequiredPaymentCode) epcValidateItem).getPaymentCodeList().size() > 1)) {
                            for (String requiredPaymentCode:((EpcRequiredPaymentCode) epcValidateItem).getPaymentCodeList()) {
                                if (!"Mobile Payment".equals(requiredPaymentCode)) {
                                    //for (EpcPayment payment:epcValidatePayment.getPaymentList()) {
                                    for (EpcPayment payment:inputPaymentList) {
                                        if (payment.getPaymentCode().equals(requiredPaymentCode) && ("".equals(payment.getCaseId()) || epcValidateItem.getCaseId().equals(payment.getCaseId()))) {
                                            hasRequiredPaymentCode = true;
                                            break;
                                        }
                                    }
                                }
                                if (hasRequiredPaymentCode) {
                                    break;
                                }
                            }

                            if (!hasRequiredPaymentCode) {
                                EpcValidatePaymentError error = new EpcValidatePaymentError();
                                error.setErrorCode(EpcValidatePaymentError.E2011);
                                error.setErrorMessage("Wrong payment code");
                                errorList.add(error);
                            }
                        }
                    }
                }

            }
            result.setResult("SUCCESS");

        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            result.setResult("FAIL");
            EpcValidatePaymentError error = new EpcValidatePaymentError();
            error.setErrorCode(EpcValidatePaymentError.E0001);
            error.setErrorMessage("System Error");
            errorList.add(error);
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (rset2 != null) { rset2.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if (pstmt2 != null) { pstmt2.close(); } } catch (Exception ignore) {}
            try { if (pstmt3 != null) { pstmt3.close(); } } catch (Exception ignore) {}
            try { if (pstmt4 != null) { pstmt4.close(); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
            try { if (fesConn != null) { fesConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (fesConn != null) { fesConn.close(); } } catch (Exception ignore) {}
                }

        return result;
    }*/


    /**
     * just sum in epc_order_charge, no re-generate
     *
     * @param orderId
     * @return
     */
    public BigDecimal getTotalCharge(int orderId) {
        BigDecimal totalCharge = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            conn = epcDataSource.getConnection();

            sql = "select sum(charge_amount) " +
                  "  from epc_order_charge " +
                  " where order_id = ? " +
                  "   and nvl(waive,?) = ? " +
                  "   and need_to_pay = ? " +
                  "   and paid = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, "N"); // waive value
            pstmt.setString(3, "N"); // waive value
            pstmt.setString(4, "Y"); // need_to_pay
            pstmt.setString(5, "N"); // paid
            rset = pstmt.executeQuery();
            if(rset.next()) {
                totalCharge = rset.getBigDecimal(1);
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;

            if(totalCharge == null) {
                totalCharge = new BigDecimal(0);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            try { if (conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        
        return totalCharge;
    }


    public BigDecimal getTotalPaymentAmount(int orderId) {
        BigDecimal totalPayment = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            conn = epcDataSource.getConnection();

            sql = "select sum(payment_amount) from epc_order_payment where order_id = ? and tx_no is null ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                totalPayment = rset.getBigDecimal(1);
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;

            if(totalPayment == null) {
                totalPayment = new BigDecimal(0);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            try { if (conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        
        return totalPayment;
    }

    public ArrayList<EpcValidateItem> getValidateItemFromQuote(int orderId) throws Exception {
        ArrayList <EpcValidateItem> validateItyemList = new ArrayList<EpcValidateItem>();
        EpcQuote epcQuote = null;
        Connection epcConn = null;
        String sql = null;
        String quoteGuid = null;
        ResultSet rset = null;
        PreparedStatement pstmt = null;
        try {
            epcConn = epcDataSource.getConnection();

            sql = "SELECT quote_id, cpq_quote_guid  "+
                  "FROM epc_order_quote " +
                  "WHERE order_id = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                quoteGuid = StringHelper.trim(rset.getString("cpq_quote_guid"));

                epcQuote = null; // reset
                epcQuote = epcOrderHandler.getCPQQuoteInEpc(orderId, rset.getInt("quote_id"));
                if(epcQuote == null) {
                    epcQuote = epcQuoteHandler.getQuoteInfo(quoteGuid);
                }


                EpcQuoteItem[] epcQuoteItems = epcQuote.getItems();
                for (int i=0; i < epcQuoteItems.length; i++) {
                    validateItyemList.addAll(getValidateItemFromProductCandidate(epcQuoteItems[i].getProductCandidateObj().getId(), epcQuoteItems[i].getProductCandidateObj(), epcQuoteItems[i].getMetaDataLookup()));
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
        }
        return validateItyemList;
    }

    public ArrayList<EpcValidateItem> getValidateItemFromProductCandidate(String caseId, EpcQuoteProductCandidate productCandidateObj, HashMap<String, Object> metaDataLookup) throws Exception {

        ArrayList <EpcValidateItem> validateItemList = new ArrayList<EpcValidateItem>();
        HashMap<String, Object> childMap = (HashMap<String, Object>)metaDataLookup.get(productCandidateObj.getId());
        String typePath = StringHelper.trim((String)childMap.get("typePath"));

        if (typePath.matches("Payment_Program[/].*")) {

            for (int i=0; i < productCandidateObj.getCharacteristicUse().size(); i++) {
                EpcCharacteristicUse epcCharacteristicUse = productCandidateObj.getCharacteristicUse().get(i);
                if ("Payment_Method".equals(epcCharacteristicUse.getName())) {
                    EpcRequiredPaymentCode epcRequiredPaymentCode = new EpcRequiredPaymentCode();
                    ArrayList<String> paymentCodeList = new ArrayList<String>();
                    epcRequiredPaymentCode.setCaseId(caseId);
                    epcRequiredPaymentCode.setPaymentCodeList(paymentCodeList);
                    for (int j=0; j < epcCharacteristicUse.getValue().size(); j++) {
                        paymentCodeList.add((String)epcCharacteristicUse.getValue().get(j));
                        /*
                        if ("Mobile Payment".equals(epcCharacteristicUse.getValue().get(j))) {
                            epcValidateItem.setNeedMobilePayment(true);
                            break;
                        }
                         */

                    }
                    //break;
                    validateItemList.add(epcRequiredPaymentCode);
                }
                // added by Danny Chan on 29220602: start
                if ("Credit_Card_Prefix".equals(epcCharacteristicUse.getName())) {
                    EpcRequiredCardPrefix epcRequiredCardPrefix = new EpcRequiredCardPrefix();
                    ArrayList<String> cardPrefixList = new ArrayList<String>();
                    epcRequiredCardPrefix.setCaseId(caseId);
                    epcRequiredCardPrefix.setCardPrefixList(cardPrefixList);
                    for (int j=0; j < epcCharacteristicUse.getValue().size(); j++) {
                        cardPrefixList.add((String)epcCharacteristicUse.getValue().get(j));
                    }
                    validateItemList.add(epcRequiredCardPrefix);
                }
                // added by Danny Chan on 29220602: end
            }
        }

        if(productCandidateObj.getChildEntity() != null) {
            for(int i = 0; i < productCandidateObj.getChildEntity().size(); i++) {
                validateItemList.addAll(getValidateItemFromProductCandidate(caseId, productCandidateObj.getChildEntity().get(i), metaDataLookup));
            }
        } else {
            // nothing to do
        }
        return validateItemList;
    }

    public boolean compareVASLabel(String vasSuperSetLabel, String vasSubSetLabel, String vasMinMaxOccur) throws Exception {

        boolean result = false;
        long minOccur = 0;
        long maxOccur = 0;
        try {
            if ("".equals(StringHelper.trim(vasMinMaxOccur))) {
                return vasSuperSetLabel.equals(vasSubSetLabel);
            } else if (!vasMinMaxOccur.matches("^[0-9]+:[0-9]+$")) {
                return vasSuperSetLabel.equals(vasSubSetLabel);
            }

            ArrayList<String> vasSuperSetList = new ArrayList<String> (Arrays.asList(vasSuperSetLabel.split("[|]")));
            ArrayList<String> vasSubSetList = new ArrayList<String> (Arrays.asList(vasSubSetLabel.split("[|]")));
            long matchCount = vasSubSetList.stream().filter(s -> vasSuperSetList.contains(s)).count();
            Matcher matcher = Pattern.compile("([0-9]+):([0-9]+)").matcher(vasMinMaxOccur);
            if (matcher.find())
            {
                minOccur = Long.parseLong(matcher.group(1));
                maxOccur = Long.parseLong(matcher.group(2));
            }
            if (matchCount >= minOccur && matchCount <= maxOccur) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            throw e;
        }
        return result;
    }

    public String getWaiveFormCode(String chargeCode) {
        String waiveFormCode = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            conn = fesDataSource.getConnection();

            sql = "SELECT wa_form_code FROM sa_service_charge WHERE charge_type = ? AND wa_form_code IS NOT NULL ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, chargeCode);
            rset = pstmt.executeQuery();
            if(rset.next()) {
                waiveFormCode = rset.getString("wa_form_code");
            } rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
        } finally {
            try { if (conn != null) { conn.close(); } } catch (Exception ignore) {}
                }

        return waiveFormCode;
    }


    public EpcCreatePayment savePaymentInfo(EpcCreatePayment epcCreatePayment) {
        Connection conn = null;
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            savePaymentInfo(conn, epcCreatePayment);
            if("OK".equals(epcCreatePayment.getSaveStatus())) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);

            epcCreatePayment.setSaveStatus("FAIL");
            epcCreatePayment.setErrMsg(e.getMessage());

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
                }
        return epcCreatePayment;
    }

    public EpcCreatePayment savePaymentInfo(Connection conn, EpcCreatePayment epcCreatePayment) {
        String custId = StringHelper.trim(epcCreatePayment.getCustId());
        int orderId = epcCreatePayment.getOrderId();
        String orderReference = "";
        ArrayList<EpcPayment> paymentList = epcCreatePayment.getPaymentList();
        boolean isValid = true;
        String errMsg = "";
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmtId = null;
        ResultSet rsetId = null;
        String sql = "";

		
        try {
            // basic checking
            if(orderId <= 0) {
                errMsg += "input order id [" + orderId + "] is invalid. ";
                isValid = false;
            }

            if(!"".equals(custId)) {
                // check with crm ?
                // ...
            } else {
                isValid = false;
                errMsg += "cust id is empty. ";
            }

            orderReference = epcOrderHandler.isOrderBelongCust(conn, custId, orderId);
            if("NOT_BELONG".equals(orderReference)) {
                errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
                isValid = false;
            }

            if(paymentList == null || paymentList.size() == 0) {
                isValid = false;
                errMsg += "payment list is empty. ";
            } else {
                // check valid payment code ?
                // ...
            }

            // end of basic checking


            if(isValid) {
                sql = "select epc_order_payment_seq.nextval from dual ";
                pstmtId = conn.prepareStatement(sql);

                sql = "insert into epc_order_payment ( " + 
                      "  payment_id, order_id, payment_code, payment_amount, reference_1, " + 
                      "  reference_2, currency_code, currency_amount, exchange_rate, cc_no_masked, " + 
                      "  cc_name_masked, cc_expiry_masked, ecr_no, approval_code, tx_no, " +
                      "  case_id " +
                      ") values ( " + 
                      "  ?,?,?,?,?, " +
                      "  ?,?,?,?,?, " + 
                      "  ?,?,?,?,?, " + 
                      "  ? " +
                      ") ";
                pstmt = conn.prepareStatement(sql);
                
                sql = "update epc_order_payment " +
                      "set  payment_amount = ?, " +
                      "     reference_1 = ?, " +
                      "     reference_2 = ? " +
                      "where payment_id = ? " +
                      "and order_id = ? ";
                pstmt2 = conn.prepareStatement(sql);

                for (EpcPayment p : paymentList) {
                    if (p.getPaymentId() <= 0) {
                        rsetId = pstmtId.executeQuery();
                        if(rsetId.next()) {
                            p.setPaymentId(rsetId.getInt(1));
                        }
                        rsetId.close();
    
                        pstmt.setInt(1, p.getPaymentId()); // payment_id
                        pstmt.setInt(2, orderId); // order_id
                        pstmt.setString(3, StringHelper.trim(p.getPaymentCode())); // payment_code
                        pstmt.setBigDecimal(4, p.getPaymentAmount()); // payment_amount
                        pstmt.setString(5, StringHelper.trim(p.getReference1())); // reference_1
                        pstmt.setString(6, StringHelper.trim(p.getReference2())); // reference_2
                        pstmt.setString(7, StringHelper.trim(p.getCurrencyCode())); // currency_code
                        pstmt.setBigDecimal(8, p.getCurrencyAmount()); // currency_amount
                        pstmt.setBigDecimal(9, p.getExchangeRate()); // exchange_rate
                        pstmt.setString(10, StringHelper.trim(p.getCcNoMasked())); // cc_no_masked
                        pstmt.setString(11, StringHelper.trim(p.getCcNameMasked())); // cc_name_masked
                        pstmt.setString(12, StringHelper.trim(p.getCcExpiryMasked())); // cc_expiry_masked
                        pstmt.setString(13, StringHelper.trim(p.getEcrNo())); // ecr_no
                        pstmt.setString(14, StringHelper.trim(p.getApprovalCode())); // approval_code
                        pstmt.setString(15, StringHelper.trim(p.getTxNo())); // approval_code
                        pstmt.setString(16, StringHelper.trim(p.getCaseId())); // case_id

                        pstmt.addBatch();
                    } else {
                        
                        pstmt2.setBigDecimal(1, p.getPaymentAmount()); // payment_amount
                        pstmt2.setString(2, StringHelper.trim(p.getReference1())); // reference_1
                        pstmt2.setString(3, StringHelper.trim(p.getReference2())); // reference_2
                        pstmt2.setInt(4, p.getPaymentId()); // payment_id
                        pstmt2.setInt(5, orderId); // order_id

                        pstmt2.addBatch();
                    }
                }
                pstmt.executeBatch();
                pstmt2.executeBatch();
                pstmt.close();
                pstmt2.close();
                pstmtId.close();

                //conn.commit();

                epcCreatePayment.setSaveStatus("OK");
            } else {
                epcCreatePayment.setSaveStatus("FAIL");
                epcCreatePayment.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);

            epcCreatePayment.setSaveStatus("FAIL");
            epcCreatePayment.setErrMsg(e.getMessage());

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(rsetId != null) { rsetId.close(); } } catch (Exception ee) {}
            try { if(pstmtId != null) { pstmtId.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(pstmt2 != null) { pstmt2.close(); } } catch (Exception ee) {}
        }
        return epcCreatePayment;
    }
    
    public EpcUpdatePayment updatePaymentInfo(EpcUpdatePayment epcUpdatePayment) {
        Connection conn = null;
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            updatePaymentInfo(conn, epcUpdatePayment);
            if("OK".equals(epcUpdatePayment.getSaveStatus())) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);

            epcUpdatePayment.setSaveStatus("FAIL");
            epcUpdatePayment.setErrMsg(e.getMessage());

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcUpdatePayment;
    }

    public EpcUpdatePayment updatePaymentInfo(Connection conn, EpcUpdatePayment epcUpdatePayment) {
        String custId = StringHelper.trim(epcUpdatePayment.getCustId());
        int orderId = epcUpdatePayment.getOrderId();
        String orderReference = "";
        ArrayList<EpcPayment> paymentList = epcUpdatePayment.getPaymentList();
        ArrayList<EpcPayment> addPaymentList = new ArrayList<EpcPayment>();
        ArrayList<EpcPayment> deletePaymentList = new ArrayList<EpcPayment>();
        ArrayList<EpcPayment> resultPaymentList = new ArrayList<EpcPayment>();
        boolean isValid = true;
        String errMsg = "";
        String sql = "";


        try {
            // basic checking
            if(orderId <= 0) {
                errMsg += "input order id [" + orderId + "] is invalid. ";
                isValid = false;
            }

            if(!"".equals(custId)) {
                // check with crm ?
                // ...
            } else {
                isValid = false;
                errMsg += "cust id is empty. ";
            }

            orderReference = epcOrderHandler.isOrderBelongCust(conn, custId, orderId);
            if("NOT_BELONG".equals(orderReference)) {
                errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
                isValid = false;
            }

            // end of basic checking


            if(isValid) {
                
                // except COUP in epc_order_payment, all other payment codes should be considered to be refreshed / updated

                // paymentList required to delete
                ArrayList<EpcPayment> existPaymentList = epcOrderHandler.getPaymentInfo(orderId);

                for(EpcPayment p:paymentList) {
                    logger.info("paymentCode in paymentList:" + p.getPaymentCode());
                }

                
                for (EpcPayment existPayment:existPaymentList) {
                    if (!paymentList.contains(existPayment) && !"COUP".equals(existPayment.getPaymentCode())) {
                        deletePaymentList.add(existPayment);
                    } else if (!"COUP".equals(existPayment.getPaymentCode())) {
                        resultPaymentList.add(existPayment);
                    }
                }

                EpcCreatePayment epcDeletePayment = new EpcCreatePayment();
                epcDeletePayment.setCustId(custId);
                epcDeletePayment.setOrderId(orderId);
                epcDeletePayment.setPaymentList(deletePaymentList);
                epcDeletePayment = deletePaymentInfo(conn, epcDeletePayment);
                
                // paymentList required to add
                for (EpcPayment payment:paymentList) {
                    if (!existPaymentList.contains(payment)) {
                        addPaymentList.add(payment);
                    }
                }
                EpcCreatePayment epcCreatePayment = new EpcCreatePayment();
                epcCreatePayment.setCustId(custId);
                epcCreatePayment.setOrderId(orderId);
                epcCreatePayment.setPaymentList(addPaymentList);
                epcCreatePayment = savePaymentInfo(conn, epcCreatePayment);
                resultPaymentList.addAll(epcCreatePayment.getPaymentList());

                //conn.commit();

                epcUpdatePayment.setSaveStatus("OK");
                epcUpdatePayment.setPaymentList(resultPaymentList);

            } else {
                epcUpdatePayment.setSaveStatus("FAIL");
                epcUpdatePayment.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);

            epcUpdatePayment.setSaveStatus("FAIL");
            epcUpdatePayment.setErrMsg(e.getMessage());

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {

        }
        return epcUpdatePayment;
    }
    
    public EpcCreatePayment deletePaymentInfo(EpcCreatePayment epcCreatePayment) {
        Connection conn = null;
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            deletePaymentInfo(conn, epcCreatePayment);
            if("OK".equals(epcCreatePayment.getSaveStatus())) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);

            epcCreatePayment.setSaveStatus("FAIL");
            epcCreatePayment.setErrMsg(e.getMessage());

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
                }
        return epcCreatePayment;
    }

    public EpcCreatePayment deletePaymentInfo(Connection conn, EpcCreatePayment epcCreatePayment) {
        String custId = StringHelper.trim(epcCreatePayment.getCustId());
        int orderId = epcCreatePayment.getOrderId();
        String orderReference = "";
        ArrayList<EpcPayment> paymentList = epcCreatePayment.getPaymentList();
        boolean isValid = true;
        String errMsg = "";
        PreparedStatement pstmt = null;
        String sql = "";


        try {

            // basic checking
            if(orderId <= 0) {
                errMsg += "input order id [" + orderId + "] is invalid. ";
                isValid = false;
            }

            if(!"".equals(custId)) {
                // check with crm ?
                // ...
            } else {
                isValid = false;
                errMsg += "cust id is empty. ";
            }

            orderReference = epcOrderHandler.isOrderBelongCust(conn, custId, orderId);
            if("NOT_BELONG".equals(orderReference)) {
                errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
                isValid = false;
            }

            if(paymentList == null || paymentList.size() == 0) {
                isValid = false;
                errMsg += "payment list is empty. ";
            } else {
                // check valid payment code ?
                // ...
            }

            // end of basic checking


            if(isValid) {
                sql = "delete from epc_order_payment where payment_id =? and order_id = ? and tx_no is null ";
                pstmt = conn.prepareStatement(sql);

                for (EpcPayment p : paymentList) {
                    pstmt.setInt(1, p.getPaymentId()); // payment_id
                    pstmt.setInt(2, orderId); // order_id

                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                pstmt.close();

                //conn.commit();

                epcCreatePayment.setSaveStatus("OK");
            } else {
                epcCreatePayment.setSaveStatus("FAIL");
                epcCreatePayment.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);

            epcCreatePayment.setSaveStatus("FAIL");
            epcCreatePayment.setErrMsg(e.getMessage());

        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
        }
        return epcCreatePayment;
    }
    
    public EpcInitPayment initPaymentInfo(EpcInitPayment epcInitPayment) {

        Connection conn = null;
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            initPaymentInfo(conn, epcInitPayment);
            if("SUCCESS".equals(epcInitPayment.getResultCode())) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);

            epcInitPayment.setResultCode("FAIL");
            epcInitPayment.setErrorMessage(e.getMessage());

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcInitPayment;
    }

    public EpcInitPayment initPaymentInfo(Connection conn, EpcInitPayment epcInitPayment) {

        int orderId = epcInitPayment.getOrderId();
        String sql = null;
        PreparedStatement deleteOrderPaymentPstmt = null;

        try {

            sql = "DELETE epc_order_payment p " +
                  "WHERE order_id = ? " +
                  "AND tx_no IS NULL " +
                  "AND NOT EXISTS ( " +
                  "    SELECT 1 FROM epc_payment_ctrl " +
                  "    WHERE payment_code = p.payment_code " +
                  "    AND default_payment = ? " +
                  ") ";
            deleteOrderPaymentPstmt = conn.prepareStatement(sql);
            deleteOrderPaymentPstmt.setInt(1, orderId); // order_id
            deleteOrderPaymentPstmt.setString(2, "Y"); // epc_control_ctrl.default_paymemt
            deleteOrderPaymentPstmt.executeUpdate();
            
            epcInitPayment.setResultCode("SUCCESS");

        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            epcInitPayment.setResultCode("FAIL");
            epcInitPayment.setErrorMessage(e.getMessage());
        } finally {
            try { if(deleteOrderPaymentPstmt != null) { deleteOrderPaymentPstmt.close(); } } catch (Exception ee) {}
        }
        return epcInitPayment;
    }

    public EpcWaiveChargeResult waiveCharge(EpcWaiveCharge epcWaiveCharge) {

        int orderId = epcWaiveCharge.getOrderId();
        int seqId = epcWaiveCharge.getSeqId();
        String approveUsername = epcSecurityHelper.validateString(epcWaiveCharge.getApproveUsername());
        String waiveReason = epcSecurityHelper.validateString(epcWaiveCharge.getWaiveReason());
        String chargeCode = epcSecurityHelper.validateString(epcWaiveCharge.getChargeCode());
        EpcWaiveChargeResult epcWaiveChargeResult = new EpcWaiveChargeResult();
        epcWaiveChargeResult.setResultCode("FAIL");

        boolean isValid = true;
        String errMsg = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        int numRecUpdated = 0;

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            // basic checking
            if(orderId <= 0) {
                errMsg += "input order id [" + orderId + "] is invalid. ";
                isValid = false;
            }

            if("".equals(approveUsername)) {
                errMsg += "Missing approveUsername.";
                isValid = false;
            }

            if("".equals(chargeCode)) {
                errMsg += "Missing chargeCode.";
                isValid = false;
            }

            if(isValid) {
                sql = "UPDATE epc_order_charge SET waive = ?, waive_reason = ?, charge_amount = ?, approve_by = ?, " +
					  "handle_user = ?, handle_salesman = ?, handle_channel = ?, handle_location = ? " + 
                      "WHERE order_id = ? AND seq_id = ? AND charge_code = ? AND paid = ?  AND waive IS NULL";

                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, "Y");
                pstmt.setString(2, waiveReason);
                pstmt.setBigDecimal(3, BigDecimal.ZERO);
                pstmt.setString(4, approveUsername);
				pstmt.setString(5, epcWaiveCharge.getHandle_user());
				pstmt.setString(6, epcWaiveCharge.getHandle_salesman());
				pstmt.setString(7, epcWaiveCharge.getHandle_channel());
				pstmt.setString(8, epcWaiveCharge.getHandle_location());				
                pstmt.setInt(9, orderId);
                pstmt.setInt(10, seqId);
                pstmt.setString(11, chargeCode);
                pstmt.setString(12, "N");
                numRecUpdated = pstmt.executeUpdate();
                pstmt.close();

                if (numRecUpdated == 1) {
                    epcWaiveChargeResult.setResultCode("SUCCESS");
                    conn.commit();
                } else {
                    epcWaiveChargeResult.setResultCode("FAIL");
                    errMsg += "Update failure.";
                    conn.rollback();
                }
                epcWaiveChargeResult.setErrorMessage(errMsg);
            } else {
                epcWaiveChargeResult.setResultCode("FAIL");
                epcWaiveChargeResult.setErrorMessage(errMsg);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            epcWaiveChargeResult.setResultCode("FAIL");
            epcWaiveChargeResult.setErrorMessage("System Error.");
            try { if(conn != null) { conn.rollback(); } } catch (Exception ignore) {}
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        return epcWaiveChargeResult;
    }

    public EpcDiscountChargeResult discountCharge(EpcDiscountCharge epcDiscountCharge) {

        int orderId = epcDiscountCharge.getOrderId();
        int seqId = epcDiscountCharge.getSeqId();
        String approveUsername = epcSecurityHelper.validateString(epcDiscountCharge.getApproveUsername());
        String chargeCode = epcSecurityHelper.validateString(epcDiscountCharge.getChargeCode());
        BigDecimal discountAmount = epcDiscountCharge.getDiscountAmount();
        BigDecimal discountPercent = epcDiscountCharge.getDiscountPercent();
        EpcDiscountChargeResult epcDiscountChargeResult = new EpcDiscountChargeResult();
        epcDiscountChargeResult.setResultCode("FAIL");

        boolean isValid = true;
        String errMsg = "";
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        int numRecUpdated = 0;
        BigDecimal originalChargeAmount = null;
        BigDecimal chargeAmount = null;

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            // basic checking
            if(orderId <= 0) {
                errMsg += "input order id [" + orderId + "] is invalid. ";
                isValid = false;
            }

            if("".equals(approveUsername)) {
                errMsg += "Missing approveUsername.";
                isValid = false;
            }

            if("".equals(chargeCode)) {
                errMsg += "Missing chargeCode.";
                isValid = false;
            }

            if(discountAmount.compareTo(BigDecimal.ZERO) <= 0) {
                errMsg += "Invalid Discount Amount.";
                isValid = false;
            }

            if(discountPercent.compareTo(BigDecimal.ZERO) <= 0) {
                errMsg += "Invalid Discount Percent.";
                isValid = false;
            }

            sql = "SELECT origin_charge_amount FROM epc_order_charge "+
                  "WHERE order_id = ? AND seq_id = ? AND charge_code = ? AND paid = ? AND discount_amount IS NULL ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            pstmt.setInt(2, seqId);
            pstmt.setString(3, chargeCode);
            pstmt.setString(4, "N");
            rset = pstmt.executeQuery();
            if (rset.next()) {
                originalChargeAmount = rset.getBigDecimal("origin_charge_amount");
            } else {
                errMsg += "Missing charge to update.";
                isValid = false;
            }
            rset.close();
            pstmt.close();
            pstmt = null;

            if (originalChargeAmount != null) {
                if (originalChargeAmount.subtract(discountAmount).compareTo(BigDecimal.ZERO) < 0) {
                    errMsg += "Invalid Discount Amount.";
                    isValid = false;
                } else {
                    chargeAmount = originalChargeAmount.subtract(discountAmount);
                }
            }

            if(isValid) {
                sql = "UPDATE epc_order_charge SET discount_amount = ?, discount_percent = ?, charge_amount = ?, approve_by = ?, " +
					  "handle_user = ?, handle_salesman = ?, handle_channel = ?, handle_location = ? " + 
                      "WHERE order_id = ? AND seq_id = ? AND charge_code = ? AND paid = ? ";

                pstmt = conn.prepareStatement(sql);
                pstmt.setBigDecimal(1, discountAmount);
                pstmt.setBigDecimal(2, discountPercent);
                pstmt.setBigDecimal(3, chargeAmount);
                pstmt.setString(4, approveUsername);
				pstmt.setString(5, epcDiscountCharge.getHandle_user());
				pstmt.setString(6, epcDiscountCharge.getHandle_salesman());
				pstmt.setString(7, epcDiscountCharge.getHandle_channel());
				pstmt.setString(8, epcDiscountCharge.getHandle_location());
                pstmt.setInt(9, orderId);
                pstmt.setInt(10, seqId);
                pstmt.setString(11, chargeCode);
                pstmt.setString(12, "N");
                numRecUpdated = pstmt.executeUpdate();
                pstmt.close();

                if (numRecUpdated == 1) {
                    epcDiscountChargeResult.setResultCode("SUCCESS");
                    conn.commit();
                } else {
                    epcDiscountChargeResult.setResultCode("FAIL");
                    errMsg += "Update failure.";
                    conn.rollback();
                }
                epcDiscountChargeResult.setErrorMessage(errMsg);
            } else {
                epcDiscountChargeResult.setResultCode("FAIL");
                epcDiscountChargeResult.setErrorMessage(errMsg);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            epcDiscountChargeResult.setResultCode("FAIL");
            epcDiscountChargeResult.setErrorMessage("System Error.");
            try { if(conn != null) { conn.rollback(); } } catch (Exception ignore) {}
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        return epcDiscountChargeResult;
    }


    public void settleRemainingCharges(EpcSettlePayment epcSettlePayment) {
        String iCustId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSettlePayment.getCustId()));
        int orderId = epcSettlePayment.getOrderId();
        String iCreateUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSettlePayment.getCreateUser()));
        String iCreateSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSettlePayment.getCreateSalesman()));
        String iCreateChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSettlePayment.getCreateChannel()));
        String iCreateLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSettlePayment.getCreateLocation()));
        ArrayList<String> itemIdList = epcSettlePayment.getItemIdList();
        ArrayList<EpcPayment> paymentList = epcSettlePayment.getPaymentList();
        String orderReference = "";
        boolean isValid = true;
        String errMsg = "";
        Connection conn = null;
        Connection fesConn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        BigDecimal totalChargeAmount = new BigDecimal(0);
        BigDecimal totalPaymentAmount = new BigDecimal(0);
        String tmpItemId = "";
        BigDecimal tmpItemTotalCharge = null;
        BigDecimal tmpItemChargePaid = null;
        BigDecimal tmpItemRemainingCharge = null;
        BigDecimal nonItemTotalCharge = null;
        BigDecimal nonItemChargePaid = null;
        BigDecimal nonItemRemainingCharge = null;
        String txNo = "" + new java.util.Date().getTime();
        EpcCreatePayment epcCreatePayment = null;
        ArrayList<EpcCharge> chargeList = null;
        EpcCreateReceipt epcCreateReceipt = null;
        EpcCreateReceiptResult epcCreateReceiptResult = null;
        EpcGetCharge epcGetCharge = null;
        EpcGetChargeResult epcGetChargeResult = null;
        List<EpcOfferCharge> offerChargeList = null;
        HashMap<String, BigDecimal> itemRemainingChargeMap = new HashMap<>(); // item_id, remaining charge amount

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            fesConn = fesDataSource.getConnection();
            fesConn.setAutoCommit(false);


            // basic checking
            if(orderId <= 0) {
                errMsg += "input order id [" + orderId + "] is invalid. ";
                isValid = false;
            }

            orderReference = epcOrderHandler.isOrderBelongCust(conn, iCustId, orderId);
            if("NOT_BELONG".equals(orderReference)) {
                errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + iCustId + "]. ";
                isValid = false;
            }

            // charge vs payment
            if(paymentList == null) {
                errMsg += "input payment list is empty. ";
                isValid = false;
            } else {
                for(EpcPayment p : paymentList) {
                    totalPaymentAmount = totalPaymentAmount.add(p.getPaymentAmount());
                }
            }

            if(itemIdList == null) {
                errMsg += "input item list is empty. ";
                isValid = false;
            } else {
                for(String itemId : itemIdList) {
                    tmpItemId = epcSecurityHelper.encodeForSQL(itemId);

                    tmpItemTotalCharge = getItemTotalCharge(conn, orderId, tmpItemId);
                    tmpItemChargePaid = getItemChargePaid(conn, orderId, tmpItemId);
                    tmpItemRemainingCharge = tmpItemTotalCharge.subtract(tmpItemChargePaid);

                    itemRemainingChargeMap.put(tmpItemId, tmpItemRemainingCharge); // item_id, remaining charge amount

                    totalChargeAmount = totalChargeAmount.add(tmpItemRemainingCharge);
                }
                // also handle sales flow generated charges (NOT in quote)
                nonItemTotalCharge = getNonItemTotalCharge(conn, orderId);
                nonItemChargePaid = getNonItemChargePaid(conn, orderId);
                nonItemRemainingCharge = nonItemTotalCharge.subtract(nonItemChargePaid);
                totalChargeAmount = totalChargeAmount.add(nonItemRemainingCharge);

            }

            if(totalPaymentAmount.compareTo(totalChargeAmount) != 0) {
                errMsg += "input payment amount [" + totalPaymentAmount + "] is not equal to input item charge [" + totalChargeAmount + "]. ";
                isValid = false;
            }
            // end of basic checking


            if(isValid) {
                // get charges (suppose no charge re-generation by order status)
                epcGetCharge = new EpcGetCharge();
                epcGetCharge.setOrderId(orderId + "");
                epcGetChargeResult = getChargeResult(epcGetCharge);
                if(!"SUCCESS".equals(epcGetChargeResult.getResult())) {
                    throw new Exception("get charge error, " + epcGetChargeResult.getErrorMessage());
                } else {
                    offerChargeList = epcGetChargeResult.getOfferChargeList();
                }

                // update charge from input item list
                sql = "update epc_order_charge " +
                      "   set paid = ?, " +
                      "       tx_no = ? " +
                      " where order_id = ? " +
                      "   and parent_item_id = ? " +
                      "   and need_to_pay = ? " +
                      "   and paid = ? ";
                pstmt = conn.prepareStatement(sql);
                for(String itemId : itemIdList) { // item id for main product, not the charge entity !!!
                    tmpItemId = epcSecurityHelper.encodeForSQL(itemId);

                    pstmt.setString(1, "Y"); // paid
                    pstmt.setString(2, txNo); // tx_no
                    pstmt.setInt(3, orderId); // order_id
                    pstmt.setString(4, tmpItemId); // item_id
                    pstmt.setString(5, "Y"); // need_to_pay
                    pstmt.setString(6, "N"); // paid
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                pstmt.close();
                // end of update charge from input item list

                // update charge for unpaid general charge
                sql = "update epc_order_charge " +
                      "   set paid = ?, " +
                      "       tx_no = ? " +
                      " where order_id = ? " +
                      "   and case_id is null " +
                      "   and need_to_pay = ? " +
                      "   and paid = ? ";
                pstmt = conn.prepareStatement(sql);
                for(String itemId : itemIdList) { // item id for main product, not the charge entity !!!
                    tmpItemId = epcSecurityHelper.encodeForSQL(itemId);

                    pstmt.setString(1, "Y"); // paid
                    pstmt.setString(2, txNo); // tx_no
                    pstmt.setInt(3, orderId); // order_id
                    pstmt.setString(4, "Y"); // need_to_pay
                    pstmt.setString(5, "N"); // paid
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                pstmt.close();
                // end of update charge for unpaid general charge

                // create payment record
                for(EpcPayment p : paymentList) {
                    p.setTxNo(txNo);
                }

                epcCreatePayment = new EpcCreatePayment();
                epcCreatePayment.setCustId(iCustId);
                epcCreatePayment.setOrderId(orderId);
                epcCreatePayment.setPaymentList(paymentList);

                epcCreatePayment = savePaymentInfo(conn, epcCreatePayment);
                if(!"OK".equals(epcCreatePayment.getSaveStatus())) {
                    throw new Exception(epcCreatePayment.getErrMsg());
                }
                // end of create payment record

                // create fes receipt
                chargeList = new ArrayList<>();
                for(String itemId : itemIdList) {
                    tmpItemId = epcSecurityHelper.encodeForSQL(itemId);
                    tmpItemRemainingCharge = itemRemainingChargeMap.get(tmpItemId); // item_id, remaining charge amount

                    for(EpcOfferCharge c : offerChargeList) {
                        for(EpcCharge cc : c.getChargeList()) {
                            if(tmpItemId.equals(cc.getParentItemId()) && "02".equals(cc.getChargeCode())) {
                                cc.setChargeAmount(tmpItemRemainingCharge);

                                chargeList.add(cc);
                            }
                        }
                    }
                }

                // also handle sales flow generated charges (NOT in quote)
                for(EpcOfferCharge c : offerChargeList) {
                    for(EpcCharge cc : c.getChargeList()) {
                        if ("".equals(cc.getItemId())) {
                            if ( cc.getPaid()==null || !cc.getPaid().equals("Y") ) {
                                chargeList.add(cc);
                            }
                        }
                    }

                }

                epcCreateReceipt = new EpcCreateReceipt();
                epcCreateReceipt.setCustId(iCustId);
                epcCreateReceipt.setOrderId(orderId + "");
                epcCreateReceipt.setCustNum("");
                epcCreateReceipt.setSubrNum("");
                if(EpcLoginChannel.ONLINE.equals(iCreateChannel)) {
                    epcCreateReceipt.setLocation("ECO");
                } else {
                    // rbd / ts / ds
                    epcCreateReceipt.setLocation(iCreateLocation);
                }
                if(EpcLoginChannel.ONLINE.equals(iCreateChannel)) {
                    epcCreateReceipt.setCreateUser("SysAdmin");
                    epcCreateReceipt.setSalesman("");
                } else {
                    epcCreateReceipt.setCreateUser(iCreateUser);
                    epcCreateReceipt.setSalesman(iCreateSalesman);
                }
                epcCreateReceipt.setPaymentList(paymentList);
                epcCreateReceipt.setCharges(chargeList);

                epcCreateReceiptResult = epcReceiptHandler.createReceipt(fesConn, epcCreateReceipt);
                if(!"SUCCESS".equals(epcCreateReceiptResult.getResult())) {
                    throw new Exception(epcCreateReceiptResult.getErrorMessage());
                }
                // end of create fes receipt

                // update receipt to epc_order_receipt
                epcOrderHandler.saveReceiptToOrder(orderId, epcCreateReceiptResult.getReceiptNo(), totalPaymentAmount, iCreateUser, iCreateSalesman, iCreateLocation, iCreateChannel);
                
                // update tx_no to epc_order_receipt
                sql = "update epc_order_receipt " +
                      "   set tx_no = ? " +
                      " where receipt_no = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txNo); // tx_no
                pstmt.setString(2, epcCreateReceiptResult.getReceiptNo()); // receipt_no
                pstmt.executeUpdate();
                pstmt.close();
                // end of update receipt to epc_order_receipt

                conn.commit();
                fesConn.commit();

                epcSettlePayment.setReceiptNo(epcCreateReceiptResult.getReceiptNo());     // added by Danny Chan on 2022-11-15 (SHK Point Payment Enhancement)
        
                epcSettlePayment.setResult("SUCCESS");
            } else {
                epcSettlePayment.setResult("FAIL");
                epcSettlePayment.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);

            epcSettlePayment.setResult("FAIL");
            epcSettlePayment.setErrMsg(e.getMessage());

            try { if(conn != null) { conn.rollback(); } } catch (Exception ignore) {}
            try { if(fesConn != null) { fesConn.rollback(); } } catch (Exception ignore) {}
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ignore) {}
            try { if(fesConn != null) { fesConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ignore) {}
        }
    }


    /***
     * input parentItemId (DEVICE item id) -> epcSettleExtensionFee
     */
    public void settleExtensionCharges(EpcSettleExtensionFee epcSettleExtensionFee) {
        String iCustId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSettleExtensionFee.getCustId()));
        int orderId = epcSettleExtensionFee.getOrderId();
        String iCreateUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSettleExtensionFee.getCreateUser()));
        String iCreateSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSettleExtensionFee.getCreateSalesman()));
        String iCreateChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSettleExtensionFee.getCreateChannel()));
        String iCreateLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSettleExtensionFee.getCreateLocation()));
        ArrayList<EpcCharge> extensionChargeList = epcSettleExtensionFee.getExtensionChargeList();
        ArrayList<EpcPayment> paymentList = epcSettleExtensionFee.getPaymentList();
        String orderReference = "";
        boolean isValid = true;
        String errMsg = "";
        Connection conn = null;
        Connection fesConn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        BigDecimal totalChargeAmount = new BigDecimal(0);
        BigDecimal totalPaymentAmount = new BigDecimal(0);
        String tmpParentItemId = "";
        String tmpWaived = "";
        String txNo = "" + new java.util.Date().getTime();
        EpcCreatePayment epcCreatePayment = null;
        EpcCreateReceipt epcCreateReceipt = null;
        EpcCreateReceiptResult epcCreateReceiptResult = null;
        EpcGetCharge epcGetCharge = null;
        EpcGetChargeResult epcGetChargeResult = null;
        List<EpcOfferCharge> offerChargeList = null;
        ArrayList<EpcCharge> chargeList = null;
        HashMap<String, BigDecimal> itemRemainingChargeMap = new HashMap<>(); // item_id, remaining charge amount
        boolean isPreSaleChargeFound = false;
        int seqNo;

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            fesConn = fesDataSource.getConnection();
            fesConn.setAutoCommit(false);


            // basic checking
            if(orderId <= 0) {
                errMsg += "input order id [" + orderId + "] is invalid. ";
                isValid = false;
            }

            orderReference = epcOrderHandler.isOrderBelongCust(conn, iCustId, orderId);
            if("NOT_BELONG".equals(orderReference)) {
                errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + iCustId + "]. ";
                isValid = false;
            }

            // charge vs payment
            if(paymentList == null) {
                errMsg += "input payment list is empty. ";
                isValid = false;
            } else {
                for(EpcPayment p : paymentList) {
                    totalPaymentAmount = totalPaymentAmount.add(p.getPaymentAmount());
                }
            }

            if(extensionChargeList == null) {
                errMsg += "input extension charge list is empty. ";
                isValid = false;
            } else {
                for(EpcCharge c : extensionChargeList) {
                    totalChargeAmount = totalChargeAmount.add(c.getChargeAmount());
                }
            }

            if(totalPaymentAmount.compareTo(totalChargeAmount) != 0) {
                errMsg += "input payment amount [" + totalPaymentAmount + "] is not equal to total extension charge [" + totalChargeAmount + "]. ";
                isValid = false;
            }

            // check whether input item has 99 charge
            // get charges (suppose no charge re-generation by order status)
            epcGetCharge = new EpcGetCharge();
            epcGetCharge.setOrderId(orderId + "");
            epcGetChargeResult = getChargeResult(epcGetCharge);
            if(!"SUCCESS".equals(epcGetChargeResult.getResult())) {
                throw new Exception("get charge error, " + epcGetChargeResult.getErrorMessage());
            } else {
                offerChargeList = epcGetChargeResult.getOfferChargeList();
                for(EpcCharge extensionCharge: extensionChargeList) {
                    isPreSaleChargeFound = false; // reset per input charge
                    tmpParentItemId = epcSecurityHelper.encodeForSQL(StringHelper.trim(extensionCharge.getParentItemId()));

                    for(EpcOfferCharge c : offerChargeList) {
                        for(EpcCharge cc : c.getChargeList()) {
                            if(tmpParentItemId.equals(cc.getParentItemId()) && "99".equals(cc.getChargeCode())) {
                                isPreSaleChargeFound = true;
                            }
                        }
                    }

                    if(!isPreSaleChargeFound) {
                        errMsg += "charge [" + tmpParentItemId + "] is not allowed to extend due to no reservation fee found. ";
                        isValid = false;
                    }
                }
            }
            // end of check whether input item has 99 charge

            // end of basic checking


            if(isValid) {
                // invoke ERP api to extend expiry date
                // ...
                // end of invoke ERP api to extend expiry date

                // Get maxinum seqId from epc_order_charge
                seqNo = getOrderMaxSeqId(orderId, conn);

                // create new charge - extension fee
                sql = "insert into epc_order_charge ( " +
                      "  order_id, seq_id, quote_id, case_id, item_id, " +
                      "  item_guid, charge_desc, charge_desc_chi, charge_code, charge_amount, " +
                      "  charge_periodicity, product, item_code, parent_item_id, allow_installment, " +
                      "  handling_fee_waive, msisdn, discount_amount, discount_percent, origin_charge_amount, " +
                      "  waive, waive_reason, approve_by, waive_form_code, discount_form_code, " +
                      "  tx_no, need_to_pay, paid " +
                      ") " +
                      "select order_id, ?, quote_id, case_id, item_id, " +
                      "       item_guid, ?, ?, ?, ?, " +
                      "       charge_periodicity, product, item_code, parent_item_id, allow_installment, " +
                      "       handling_fee_waive, msisdn, discount_amount, discount_percent, ?, " +
                      "       ?, ?, ?, ?, ?, " +
                      "       ?, ?, ? " +
                      "       from epc_order_charge " +
                      " where order_id = ? " +
                      "   and parent_item_id = ? " +
                      "   and charge_code = ? ";
                pstmt = conn.prepareStatement(sql);
                for(EpcCharge extensionCharge: extensionChargeList) {
                    tmpParentItemId = epcSecurityHelper.encodeForSQL(StringHelper.trim(extensionCharge.getParentItemId()));
                    tmpWaived = epcSecurityHelper.encodeForSQL(StringHelper.trim(extensionCharge.getWaived()));

                    pstmt.setInt(1, ++seqNo); // seq_id
                    pstmt.setString(2, "Reservation Extension Deposit"); // charge_desc
                    pstmt.setString(3, "Reservation Extension Deposit"); // charge_desc_chi
                    pstmt.setString(4, extensionCharge.getChargeCode()); // charge_code
                    pstmt.setBigDecimal(5, extensionCharge.getChargeAmount()); // charge_amount
                    pstmt.setBigDecimal(6, extensionCharge.getOriginChargeAmount()); // origin_charge_amount
                    if("Y".equals(tmpWaived)) {
                        pstmt.setString(7, tmpWaived); // waive
                        pstmt.setString(8, ""); // waive_reason
                        if(!"".equals(iCreateSalesman)) {
                            pstmt.setString(9, iCreateSalesman); // approve_by
                        } else {
                            pstmt.setString(9, iCreateUser); // approve_by
                        }
                        pstmt.setString(10, epcSecurityHelper.encodeForSQL(StringHelper.trim(extensionCharge.getWaiveFormCode()))); // waive_form_code
                        pstmt.setString(11, epcSecurityHelper.encodeForSQL(StringHelper.trim(extensionCharge.getDiscountFormCode()))); // discount_form_code
                    } else {
                        pstmt.setString(7, tmpWaived); // waive
                        pstmt.setString(8, ""); // waive_reason
                        pstmt.setString(9, ""); // approve_by
                        pstmt.setString(10, ""); // waive_form_code
                        pstmt.setString(11, ""); // discount_form_code
                    }
                    pstmt.setString(12, txNo); // tx_no
                    pstmt.setString(13, "Y"); // need_to_pay
                    pstmt.setString(14, "Y"); // paid
                    pstmt.setInt(15, orderId); // order_id
                    pstmt.setString(16, tmpParentItemId); // item_id
                    pstmt.setString(17, "99"); // charge_code

                    pstmt.addBatch();

                    // update info back to extensionCharge bean
                    extensionCharge.setChargeDesc("Reservation Extension Deposit");
                    extensionCharge.setChargeDescChi("Reservation Extension Deposit");
                    // end of update info back to extensionCharge bean
                }
                pstmt.executeBatch();
                // end of create new charge - extension fee

                // create payment record
                for(EpcPayment p : paymentList) {
                    p.setTxNo(txNo);
                }

                epcCreatePayment = new EpcCreatePayment();
                epcCreatePayment.setCustId(iCustId);
                epcCreatePayment.setOrderId(orderId);
                epcCreatePayment.setPaymentList(paymentList);

                epcCreatePayment = savePaymentInfo(conn, epcCreatePayment);
                if(!"OK".equals(epcCreatePayment.getSaveStatus())) {
                    throw new Exception(epcCreatePayment.getErrMsg());
                }
                // end of create payment record

                // create fes receipt
                chargeList = new ArrayList<>();
                for(EpcCharge extensionCharge: extensionChargeList) {
                    chargeList.add(extensionCharge);
                }

                epcCreateReceipt = new EpcCreateReceipt();
                epcCreateReceipt.setCustId(iCustId);
                epcCreateReceipt.setOrderId(orderId + "");
                epcCreateReceipt.setCustNum("");
                epcCreateReceipt.setSubrNum("");
                if(EpcLoginChannel.ONLINE.equals(iCreateChannel)) {
                    epcCreateReceipt.setLocation("ECO");
                } else {
                    // rbd / ts / ds
                    epcCreateReceipt.setLocation(iCreateLocation);
                }
                if(EpcLoginChannel.ONLINE.equals(iCreateChannel)) {
                    epcCreateReceipt.setCreateUser("SysAdmin");
                    epcCreateReceipt.setSalesman("");
                } else {
                    epcCreateReceipt.setCreateUser(iCreateUser);
                    epcCreateReceipt.setSalesman(iCreateSalesman);
                }
                epcCreateReceipt.setPaymentList(paymentList);
                epcCreateReceipt.setCharges(chargeList);

                epcCreateReceiptResult = epcReceiptHandler.createReceipt(fesConn, epcCreateReceipt);
                if(!"SUCCESS".equals(epcCreateReceiptResult.getResult())) {
                    throw new Exception(epcCreateReceiptResult.getErrorMessage());
                }
                // end of create fes receipt

                // update receipt to epc_order_receipt
                epcOrderHandler.saveReceiptToOrder(orderId, epcCreateReceiptResult.getReceiptNo(), totalPaymentAmount, iCreateUser, iCreateSalesman, iCreateLocation, iCreateChannel);

                // update tx_no to epc_order_receipt
                sql = "update epc_order_receipt " +
                      "   set tx_no = ? " +
                      " where receipt_no = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, txNo); // tx_no
                pstmt.setString(2, epcCreateReceiptResult.getReceiptNo()); // receipt_no
                pstmt.executeUpdate();
                pstmt.close();
                // end of update receipt to epc_order_receipt
                
                conn.commit();
                fesConn.commit();

                epcSettleExtensionFee.setResult("SUCCESS");
            } else {
                epcSettleExtensionFee.setResult("FAIL");
                epcSettleExtensionFee.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            logger.info(e.getMessage(), e);

            epcSettleExtensionFee.setResult("FAIL");
            epcSettleExtensionFee.setErrMsg(e.getMessage());

            try { if(conn != null) { conn.rollback(); } } catch (Exception ignore) {}
            try { if(fesConn != null) { fesConn.rollback(); } } catch (Exception ignore) {}
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ignore) {}
            try { if(fesConn != null) { fesConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ignore) {}
                }
            }

    public EpcExtensionChargeResult getExtensionCharge(EpcExtensionCharge epcExtensionCharge) {
        EpcExtensionChargeResult epcExtensionChargeResult = new EpcExtensionChargeResult();
        int orderId = epcExtensionCharge.getOrderId();
        ArrayList<EpcOrderItemDetail> inputOrderItemlist = null;
        Connection conn = null;
        Connection fesConn = null;
        EpcControlTbl enquiryControl;
        ArrayList<EpcControlTbl> resultControlList;
        ArrayList<EpcCharge> resultChargeList = new ArrayList<EpcCharge>();
        BigDecimal extensionFee = null;
        HashMap<String, BigDecimal> extensionFeeMap = new HashMap<String, BigDecimal>();

        boolean isValid = true;
        String errMsg = "";

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            fesConn = fesDataSource.getConnection();
            fesConn.setAutoCommit(false);

            inputOrderItemlist = (ArrayList<EpcOrderItemDetail>) getSanityList(epcExtensionCharge.getOrderItemList());

            // basic checking
            if(orderId <= 0) {
                errMsg += "input order id [" + orderId + "] is invalid. ";
                isValid = false;
            }

            if (isValid) {

                enquiryControl = new EpcControlTbl();
                enquiryControl.setRecType("EXTENSION_FEE");
                resultControlList = epcControlHandler.getControl(enquiryControl);

                for (int j=0; j < resultControlList.size(); j++) {
                    extensionFeeMap.put(resultControlList.get(j).getKeyStr1(), resultControlList.get(j).getValueNumber1());
                }


                for (EpcOrderItemDetail item:inputOrderItemlist) {

                    if (extensionFeeMap.get(item.getWarehouse()) != null) {
                        extensionFee = extensionFeeMap.get(item.getWarehouse());
                    } else {
                        extensionFee = new BigDecimal(0);
                    }
                    EpcCharge resultCharge = new EpcCharge();
                    resultCharge.setParentItemId(item.getParentItemId());
                    resultCharge.setItemId(item.getItemId());
                    resultCharge.setChargeCode("99");
                    resultCharge.setChargeAmount(extensionFee);
                    resultChargeList.add(resultCharge);
                }
                epcExtensionChargeResult.setExtensionChargeList(resultChargeList);

                epcExtensionChargeResult.setResultCode("SUCCESS");
            } else {
                epcExtensionChargeResult.setResultCode("FAIL");
                epcExtensionChargeResult.setErrorMessage(errMsg);
            }


        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            try { if(conn != null) { conn.rollback(); } } catch (Exception ignore) {}
            try { if(fesConn != null) { fesConn.rollback(); } } catch (Exception ignore) {}
            epcExtensionChargeResult.setResultCode("FAIL");
            epcExtensionChargeResult.setErrorMessage("System Error");
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ignore) {}
            try { if(fesConn != null) { fesConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ignore) {}
        }

        return epcExtensionChargeResult;
    }

    public <T>List<T> getSanityList(List<T> inputList) throws Exception {
         
        List<T> sanityObjectList = null;
        if (inputList == null) {
            return null;
        }
        sanityObjectList = inputList.stream().collect(Collectors.toList());
        Method getter;
        Method setter;
        for (T object:sanityObjectList) {
            Field[] fields = object.getClass().getDeclaredFields();
            for (int i=0; i < fields.length; i++) {

                switch (fields[i].getType().getSimpleName()) {
                    case "String":
                        getter = object.getClass().getMethod("get" + Character.toUpperCase(fields[i].getName().charAt(0)) + fields[i].getName().substring(1));
                        setter = object.getClass().getMethod("set" + Character.toUpperCase(fields[i].getName().charAt(0)) + fields[i].getName().substring(1), String.class);
                        setter.invoke(object, epcSecurityHelper.encodeForSQL((String)getter.invoke(object)));
                        break;
                    default:
                        break;
                }
            }
        }
        return sanityObjectList;
    }

    // added by Danny Chan on 2022-6-10: start
    // retrieve a list of credit card prefix allowed for a particular order
    public EpcGetCreditCardPrefixResult getCreditCardPrefix(EpcGetCreditCardPrefix getCreditCardPrefix) {
        EpcGetCreditCardPrefixResult epcGetCreditCardPrefixResult = new EpcGetCreditCardPrefixResult();

        int orderId = getCreditCardPrefix.getOrderId();

        logger.info("calling getCreditCardPrefix: orderId = " + orderId);

        try {
            ArrayList<EpcValidateItem> validateItemList = getValidateItemFromQuote(orderId);
            ArrayList<String>  result_card_prefix_list = null;

            for (EpcValidateItem epcValidateItem : validateItemList) {

                if (epcValidateItem instanceof EpcRequiredCardPrefix) {

                    result_card_prefix_list = new ArrayList();
                    List cardPrefixList = ((EpcRequiredCardPrefix) epcValidateItem).getCardPrefixList();

                    for (Object cardPrefix : cardPrefixList) {
                        result_card_prefix_list.add((String)cardPrefix);
                    }
                }
            }

            if (result_card_prefix_list==null) {
                epcGetCreditCardPrefixResult.setResultCode("FAIL");
                epcGetCreditCardPrefixResult.setErrorMessage("The card prefix list is not found.");
            } else {
                epcGetCreditCardPrefixResult.setResultCode("SUCCESS");
                epcGetCreditCardPrefixResult.setErrorMessage("");
                epcGetCreditCardPrefixResult.setCreditCardPrefix(result_card_prefix_list);
            }

        } catch (Exception e) {
            epcGetCreditCardPrefixResult.setResultCode("FAIL");
            epcGetCreditCardPrefixResult.setErrorMessage(e.toString());
        }

        return epcGetCreditCardPrefixResult;
    }
    // added by Danny Chan on 2022-6-10: end
    
    public ArrayList<EpcChargeCtrl> getChargeCtrlList(Connection epcConn) throws Exception {

        ResultSet rset = null;
        PreparedStatement pstmt = null;
        String sql;
        ArrayList<EpcChargeCtrl> epcChargeCtrlList = new ArrayList<EpcChargeCtrl>();
        
        try {

            sql = "SELECT rec_id, charge_guid, charge_cpq_desc, charge_code, config_desc, display_product_desc, " +
                  "display_config_desc, display_charge_desc, display_parent_desc, discount_form_code, item_code, " +
                  "check_parent_item_code, check_parent_item_desc, catalog_rrp, payment_code " +
                  "FROM epc_charge_ctrl ";
            
            pstmt = epcConn.prepareStatement(sql);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                EpcChargeCtrl epcChargeCtrl = new EpcChargeCtrl();
                epcChargeCtrl.setRecId(rset.getInt("rec_id"));
                epcChargeCtrl.setChargeGuid(StringHelper.trim(rset.getString("charge_guid")));
                epcChargeCtrl.setChargeCpqDesc(StringHelper.trim(rset.getString("charge_cpq_desc")));
                epcChargeCtrl.setChargeCode(StringHelper.trim(rset.getString("charge_code")));
                epcChargeCtrl.setConfigDesc(StringHelper.trim(rset.getString("config_desc")));
                epcChargeCtrl.setDisplayProductDesc("Y".equals(rset.getString("display_product_desc")));
                epcChargeCtrl.setDisplayConfigDesc("Y".equals(rset.getString("display_config_desc")));
                epcChargeCtrl.setDisplayChargeDesc("Y".equals(rset.getString("display_charge_desc")));
                epcChargeCtrl.setDisplayParentDesc("Y".equals(rset.getString("display_parent_desc")));
                epcChargeCtrl.setDiscountFormCode(StringHelper.trim(rset.getString("discount_form_code")));
                epcChargeCtrl.setItemCode(StringHelper.trim(rset.getString("item_code")));
                epcChargeCtrl.setCheckParentItemCode(StringHelper.trim(rset.getString("check_parent_item_code")));
                epcChargeCtrl.setCheckParentItemDesc(StringHelper.trim(rset.getString("check_parent_item_desc")));
                epcChargeCtrl.setCatalogRrp(StringHelper.trim(rset.getString("catalog_rrp")));
                epcChargeCtrl.setPaymentCode(StringHelper.trim(rset.getString("payment_code")));
                
                epcChargeCtrlList.add(epcChargeCtrl);
            }
            rset.close();
   
        } catch (Exception e) {
            throw e;
        } finally {
            if (rset != null) { try { rset.close(); } catch (Exception ignore) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception ignore) {} }
        }

        return epcChargeCtrlList;
    }

    public ArrayList<EpcPaymentCtrl> getPaymentCtrlList(Connection epcConn) throws Exception {

        ResultSet rset = null;
        PreparedStatement pstmt = null;
        String sql;
        ArrayList<EpcPaymentCtrl> epcPaymentCtrlList = new ArrayList<EpcPaymentCtrl>();
        
        try {

            sql = "SELECT rec_id, payment_code, disable_reference, default_payment, cal_default_payment_amount, " +
                  "default_ref1_mandatory, default_ref1_mandatory_message, default_ref2_mandatory, default_ref2_mandatory_message, " +
                  "default_payment_amount_message, maximum_one_payment, read_only " +
                  "FROM epc_payment_ctrl ";
            
            pstmt = epcConn.prepareStatement(sql);
            rset = pstmt.executeQuery();
            while (rset.next()) {
                EpcPaymentCtrl epcPaymentCtrl = new EpcPaymentCtrl();
                epcPaymentCtrl.setRecId(rset.getInt("rec_id"));
                epcPaymentCtrl.setPaymentCode(StringHelper.trim(rset.getString("payment_code")));
                epcPaymentCtrl.setDisableReference("Y".equals(rset.getString("disable_reference")));
                epcPaymentCtrl.setDefaultPayment("Y".equals(rset.getString("default_payment")));
                epcPaymentCtrl.setCalDefaultPaymentAmount("Y".equals(rset.getString("cal_default_payment_amount")));
                epcPaymentCtrl.setDefaultRef1Mandatory("Y".equals(rset.getString("default_ref1_mandatory")));
                epcPaymentCtrl.setDefaultRef1Message(StringHelper.trim(rset.getString("default_ref1_mandatory_message")));
                epcPaymentCtrl.setDefaultRef2Mandatory("Y".equals(rset.getString("default_ref2_mandatory")));
                epcPaymentCtrl.setDefaultRef2Message(StringHelper.trim(rset.getString("default_ref2_mandatory_message")));
                epcPaymentCtrl.setDefaultPaymentAmountMessage(StringHelper.trim(rset.getString("default_payment_amount_message")));
                epcPaymentCtrl.setMaximumOnePayment("Y".equals(rset.getString("maximum_one_payment")));
                epcPaymentCtrl.setReadOnly("Y".equals(rset.getString("read_only")));
                
                epcPaymentCtrlList.add(epcPaymentCtrl);
            }
            rset.close();
   
        } catch (Exception e) {
            throw e;
        } finally {
            if (rset != null) { try { rset.close(); } catch (Exception ignore) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception ignore) {} }
        }

        return epcPaymentCtrlList;
    }
    
    public BigDecimal getTotalAmountByCaseId(int orderId, String caseId) throws Exception {

        BigDecimal totalAmount = new BigDecimal(0);
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql;
        
        try {
            epcConn = epcDataSource.getConnection();
            sql = "SELECT SUM(item_charge) total_charge_amount " +
                  "FROM epc_order_item " +
                  "WHERE order_id = ? " +
                  "AND case_id = ? " +
                  "AND item_cat = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            pstmt.setString(2, caseId);
            pstmt.setString(3, "CHARGE");
            rset = pstmt.executeQuery();
            if (rset.next()) {
                totalAmount = rset.getBigDecimal("total_charge_amount");
            }
            rset.close();
            
        } catch (Exception e) {
            throw e;
        } finally {
            if (rset != null) { try { rset.close(); } catch (Exception ignore) {} }
            if (pstmt != null) { try { pstmt.close(); } catch (Exception ignore) {} }
            if (epcConn != null) { try { epcConn.close(); } catch (Exception ignore) {} } 
        }
        
        return totalAmount;
        
    }
    
    public List<EpcPayment> getPaymentList(String orderReference) throws Exception {
    	final String codesql ="SELECT SERVICE_CODE FROM zz_pservice WHERE SERVICE_IND = 'N' AND CREDIT_CARD_TYPE ='Y'";
    	Set<String> codeSet=new HashSet<String>();
    	try(Connection fesConn = fesDataSource.getConnection()){
    		try (PreparedStatement pstmt = fesConn.prepareStatement(codesql)) {
    			try (ResultSet rset = pstmt.executeQuery()) {
                    while (rset.next()) {
                    	codeSet.add(rset.getString(1));
                    }
    			}
    		}
    	}
        final String sql = "SELECT p.PAYMENT_ID ,p.PAYMENT_CODE ,p.PAYMENT_AMOUNT,p.REFERENCE_1 ,p.REFERENCE_2," +
                "p.CURRENCY_CODE,p.EXCHANGE_RATE ,p.CURRENCY_AMOUNT ,p.CASE_ID ,p.CC_NO_MASKED,p.CC_NAME_MASKED ," +
                "p.CC_EXPIRY_MASKED,p.ECR_NO ,p.APPROVAL_CODE ,p.TX_NO,"
                + "NVL((SELECT SUM(REFUND_AMOUNT) FROM EPC_ORDER_REFUND WHERE PAYMENT_ID=p.PAYMENT_ID AND IS_DONE<>'R'),0) AS REFUND_AMOUNT "
                + " FROM EPC_ORDER_PAYMENT p JOIN epc_order o "
                + "ON o.ORDER_ID =p.ORDER_ID WHERE o.ORDER_REFERENCE=? AND p.PAYMENT_CODE<>'COUP' "
                + "ORDER BY (p.PAYMENT_AMOUNT-(SELECT SUM(REFUND_AMOUNT) FROM EPC_ORDER_REFUND WHERE PAYMENT_ID=p.PAYMENT_ID AND IS_DONE<>'R')) DESC";
        List<EpcPayment> paymentList = new ArrayList<EpcPayment>();
        try (Connection epcConn = epcDataSource.getConnection()) {
            try (PreparedStatement pstmt = epcConn.prepareStatement(sql)) {
                pstmt.setString(1, orderReference);
                try (ResultSet rset = pstmt.executeQuery()) {
                    while (rset.next()) {
                        EpcPayment payment = genPayment(rset);
                        payment.setCreditCard(codeSet.contains(payment.getPaymentCode()));
                        paymentList.add(payment);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

        return paymentList;
    }
    
    public EpcPayment getPayment(Integer paymentId) throws Exception {

        final String sql = "SELECT p.PAYMENT_ID ,p.PAYMENT_CODE ,p.PAYMENT_AMOUNT,p.REFERENCE_1 ,p.REFERENCE_2," +
                "p.CURRENCY_CODE,p.EXCHANGE_RATE ,p.CURRENCY_AMOUNT ,p.CASE_ID ,p.CC_NO_MASKED,p.CC_NAME_MASKED ," +
                "p.CC_EXPIRY_MASKED,p.ECR_NO ,p.APPROVAL_CODE ,p.TX_NO,"
                + "NVL((SELECT SUM(REFUND_AMOUNT) FROM EPC_ORDER_REFUND WHERE PAYMENT_ID=p.PAYMENT_ID AND IS_DONE<>'R'),0) AS REFUND_AMOUNT "
                + " FROM EPC_ORDER_PAYMENT p WHERE p.PAYMENT_ID=? ";

        try (Connection epcConn = epcDataSource.getConnection()) {
            try (PreparedStatement pstmt = epcConn.prepareStatement(sql)) {
                pstmt.setInt(1, paymentId);;
                try (ResultSet rset = pstmt.executeQuery()) {
                   if(rset.next()) {
                        EpcPayment payment = genPayment(rset);
                        return payment;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
		return null;


    }
    
    private EpcPayment genPayment(ResultSet rset) throws SQLException {
    	 EpcPayment payment = new EpcPayment();
         payment.setPaymentId(rset.getInt(1));
         payment.setPaymentCode(rset.getString(2));
         payment.setPaymentAmount(rset.getBigDecimal(3));
         payment.setReference1(rset.getString(4));
         payment.setReference2(rset.getString(5));
         payment.setCurrencyCode(rset.getString(6));
         payment.setExchangeRate(rset.getBigDecimal(7));
         payment.setCurrencyAmount(rset.getBigDecimal(8));
         payment.setCaseId(rset.getString(9));
         payment.setCcNoMasked(rset.getString(10));
         payment.setCcNameMasked(rset.getString(11));
         payment.setCcExpiryMasked(rset.getString(12));
         payment.setEcrNo(rset.getString(13));
         payment.setApprovalCode(rset.getString(14));
         payment.setTxNo(rset.getString(15));
         payment.setBalance(payment.getPaymentAmount().subtract(rset.getBigDecimal(16)));
		return payment;
	}
}
