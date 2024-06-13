package epc.epcsalesapi.sales;


import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import javax.sql.DataSource;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.EpcActionLogHandler;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.helper.bean.EpcActionLog;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import epc.epcsalesapi.sales.bean.EpcOrderContact;
import epc.epcsalesapi.sales.bean.EpcRedeemVoucher;
import epc.epcsalesapi.sales.bean.EpcSendMsgRequest;
import epc.epcsalesapi.sales.bean.EpcVmsCaseInfoInCart;
import epc.epcsalesapi.sales.bean.EpcVmsEpcRecord;
import epc.epcsalesapi.sales.bean.EpcVmsGetCompiledSpec;
import epc.epcsalesapi.sales.bean.EpcVmsVoucherInfo;
import epc.epcsalesapi.sales.bean.EpcVmsVoucherListRequest;
import epc.epcsalesapi.sales.bean.EpcVmsVoucherListRequestDetail;
import epc.epcsalesapi.sales.bean.EpcVmsVoucherListResponse;
import epc.epcsalesapi.sales.bean.EpcVmsVoucherListResponseDetail;
import epc.epcsalesapi.sales.bean.EpcVmsVoucherListResponseDetailAssignTransaction;
import epc.epcsalesapi.sales.bean.EpcVmsVoucherListResponseDetailCoupon;
import epc.epcsalesapi.sales.bean.vms.EpcVmsVoucherCodeRequest;
import epc.epcsalesapi.sales.bean.vms.VmsCharge;
import epc.epcsalesapi.sales.bean.vms.VmsDiscount;
import epc.epcsalesapi.sales.bean.vms.VmsProduct;
import epc.epcsalesapi.sales.bean.vms.VmsProductDetail;
import epc.epcsalesapi.sales.bean.vms.VmsVoucher2;
import epc.epcsalesapi.sales.bean.vms.assign.AutoRemoveAssignedVoucher;
import epc.epcsalesapi.sales.bean.vms.assign.VmsAssign2;
import epc.epcsalesapi.sales.bean.vms.assign.VmsAssignVoucher;
import epc.epcsalesapi.sales.bean.vms.assign.VmsAutoAssign;
import epc.epcsalesapi.sales.bean.vms.cust.VmsCustVoucher;
import epc.epcsalesapi.sales.bean.vms.information.VmsVoucherInfoRequest;
import epc.epcsalesapi.sales.bean.vms.information.VmsVoucherInformation;
import epc.epcsalesapi.sales.bean.vms.information.VoucherInformationListRes;
import epc.epcsalesapi.sales.bean.vms.information.VoucherInformationReq;
import epc.epcsalesapi.sales.bean.vms.information.VoucherInformationReqCoupon;
import epc.epcsalesapi.sales.bean.vms.information.VoucherInformationResponse;
import epc.epcsalesapi.sales.bean.vms.order.VmsOrderVoucher;
import epc.epcsalesapi.sales.bean.vms.redeem.VmsRedeem2;


@Service
public class EpcVoucherHandlerNew {

    private final Logger logger = LoggerFactory.getLogger(EpcVoucherHandlerNew.class);

    private final String VOUCHER_SCOPE_QUOTE_LEVEL = "Shopping Cart";
    private final String VOUCHER_FACE_VALUE_TYPE_FIXED_DISCOUNT = "Fixed Discount";  
    private final String VOUCHER_FACE_VALUE_TYPE_PERCENTAGE_DISCOUNT = "Percentage Discount";
    private final String VOUCHER_FACE_VALUE_TYPE_FIXED_PRODUCT_PRICE = "Fixed Product Price";
    private final String VOUCHER_APPLY_LEVEL_ORDER = "ORDER";
    private final String VOUCHER_APPLY_LEVEL_QUOTE_ITEM = "QUOTE_ITEM";
    private final String VOUCHER_APPLY_LEVEL_PRODUCT = "PRODUCT";

    final String VOUCHER_ASSIGN = "ASSIGN";
    final String VOUCHER_REDEEM = "REDEEM";

    private final String REDEEM_ACTION_ADD = "ADD";
    private final String REDEEM_ACTION_DELETE = "DELETE";
    private final String REDEEM_ACTION_REFRESH = "REFRESH";
    private final String REDEEM_ACTION_REFRESH_DELETE = "REFRESH-DELETE";

    final String VOUCHER_CATEGORY_HANDSET_ACCESSORY_DISCOUNT = "Handset / Accessory discount";
    final String VOUCHER_CATEGORY_ENTITLEMENT = "Entitlement";
    final String VOUCHER_CATEGORY_E_COUPON = "e-Coupon";
    final String VOUCHER_CATEGORY_QUOTA_ITEM = "Quota item";
    final String VOUCHER_CATEGORY_PHYSICAL_COUPON = "Physical Coupon";

    private final DataSource epcDataSource;
    private final EpcOrderHandler epcOrderHandler;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcCompiledSpecHandler epcCompiledSpecHandler;
    private final EpcActionLogHandler epcActionLogHandler;
    private final EpcOrderAttrHandler epcOrderAttrHandler;

    @Autowired
    private EpcMsgHandler epcMsgHandler;
    @Autowired
    private EpcContactInfoHandler epcContactInfoHandler;

    public EpcVoucherHandlerNew(
            DataSource epcDataSource, EpcOrderHandler epcOrderHandler, EpcSecurityHelper epcSecurityHelper,
            EpcCompiledSpecHandler epcCompiledSpecHandler, EpcActionLogHandler epcActionLogHandler, EpcOrderAttrHandler epcOrderAttrHandler) {
        this.epcDataSource = epcDataSource;
        this.epcOrderHandler = epcOrderHandler;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcCompiledSpecHandler = epcCompiledSpecHandler;
        this.epcActionLogHandler = epcActionLogHandler;
        this.epcOrderAttrHandler = epcOrderAttrHandler;
    }

    /***
     * get order case list with upfront charge
     * sorted by upfront payment - larger amount, higher priority
     * 
     * @param orderId
     * @return
     */
    public ArrayList<EpcVmsCaseInfoInCart> getOrderCaseList2(int orderId, String voucherScope) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtQuoteLevel = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<EpcVmsCaseInfoInCart> orderCaseList = new ArrayList<>();
        EpcVmsCaseInfoInCart epcVmsCaseInfoInCart = null;
        BigDecimal tmpNetAmount = null;
        BigDecimal tmpVoucherAmount = null;
        BigDecimal tmpAmount = null;

        try {
            conn = epcDataSource.getConnection();

            sql = "select (select nvl(sum(charge_amount),0) " +
                    "          from epc_order_charge b " +
                    "         where b.order_id = a.order_id " +
                    "           and b.charge_code != '99' " +
                    "     ) as total_upfront_charge, " +
                    "     (select nvl(sum(voucher_amount),0) " +
                    "          from epc_order_voucher c " +
                    "         where c.order_id = a.order_id " +
                    "           and c.assign_redeem = ? " +
                    "           and c.status = ? " +
                    "     ) as voucher_amount " +
                    "  from epc_order a " +
                    " where a.order_id = ? ";
            pstmtQuoteLevel = conn.prepareStatement(sql);

            sql = "select a.case_id, a.cpq_offer_guid, c.cpq_quote_guid, a.quote_item_guid, c.quote_id, " +
                    "       (select nvl(sum(charge_amount),0) " +
                    "          from epc_order_charge b " +
                    "         where b.order_id = a.order_id " +
                    "           and b.case_id = a.case_id " +
                    "           and b.charge_code != '99' " +
                    "       ) as total_upfront_charge, " +
                    "       (select nvl(sum(voucher_amount),0) " +
                    "          from epc_order_voucher c " +
                    "         where c.order_id = a.order_id " +
                    "           and c.case_id = a.case_id " +
                    "           and c.assign_redeem = ? " +
                    "           and c.status = ? " +
                    "       ) as voucher_amount " +
                    "  from epc_order_case a, epc_order_quote c " +
                    " where a.order_id = ? " +
                    "   and c.order_id = a.order_id " +
                    "   and c.quote_id = a.quote_id " +
                    " order by 6 desc ";
            pstmt = conn.prepareStatement(sql);


            if (VOUCHER_SCOPE_QUOTE_LEVEL.equals(voucherScope)) {
                // quote level, with total amount (whole quote)
                pstmtQuoteLevel.setString(1, VOUCHER_REDEEM); // assign_redeem
                pstmtQuoteLevel.setString(2, "A"); // status
                pstmtQuoteLevel.setInt(3, orderId); // order_id
                rset = pstmtQuoteLevel.executeQuery();
                if (rset.next()) {
                    tmpNetAmount = rset.getBigDecimal("total_upfront_charge");
                    tmpVoucherAmount = rset.getBigDecimal("voucher_amount");
                    if(tmpNetAmount == null) {
                        tmpAmount = BigDecimal.ZERO;
                    } else {
                        tmpAmount = tmpNetAmount.subtract(tmpVoucherAmount);
                        if(tmpAmount.compareTo(BigDecimal.ZERO) == -1) {
                            tmpAmount = BigDecimal.ZERO;
                        }
                    }

                    epcVmsCaseInfoInCart = new EpcVmsCaseInfoInCart();
                    epcVmsCaseInfoInCart.setQuoteGuid("" + orderId);
                    epcVmsCaseInfoInCart.setQuoteId(0);
                    epcVmsCaseInfoInCart.setCaseId("");
                    epcVmsCaseInfoInCart.setOfferGuid("");
                    epcVmsCaseInfoInCart.setQuoteItemGuid("" + orderId); // use orderId instead of quote item guid
                    epcVmsCaseInfoInCart.setTotalCharge(tmpAmount);
                    if(tmpAmount.compareTo(BigDecimal.ZERO) == 1) {
                        // tmpAmount > 0
                        epcVmsCaseInfoInCart.setValidToRedeem(true);
                    } else {
                        // tmpAmount <= 0
                        epcVmsCaseInfoInCart.setValidToRedeem(false); // stop the amount allocation !!!
                    }
                    

                    orderCaseList.add(epcVmsCaseInfoInCart);
                }
                rset.close();
                pstmtQuoteLevel.close();
            } else {
                // quote item level, with quote item net amount
                pstmt.setString(1, VOUCHER_REDEEM); // assign_redeem
                pstmt.setString(2, "A"); // status
                pstmt.setInt(3, orderId); // order_id
                rset = pstmt.executeQuery();
                while (rset.next()) {
                    tmpNetAmount = rset.getBigDecimal("total_upfront_charge");
                    tmpVoucherAmount = rset.getBigDecimal("voucher_amount");
                    if(tmpNetAmount == null) {
                        tmpAmount = BigDecimal.ZERO;
                    } else {
                        tmpAmount = tmpNetAmount.subtract(tmpVoucherAmount);
                        if(tmpAmount.compareTo(BigDecimal.ZERO) == -1) {
                            tmpAmount = BigDecimal.ZERO;
                        }
                    }

                    epcVmsCaseInfoInCart = new EpcVmsCaseInfoInCart();
                    epcVmsCaseInfoInCart.setQuoteGuid(StringHelper.trim(rset.getString("cpq_quote_guid")));
                    epcVmsCaseInfoInCart.setQuoteId(rset.getInt("quote_id"));
                    epcVmsCaseInfoInCart.setCaseId(StringHelper.trim(rset.getString("case_id")));
                    epcVmsCaseInfoInCart.setOfferGuid(StringHelper.trim(rset.getString("cpq_offer_guid")));
                    epcVmsCaseInfoInCart.setQuoteItemGuid(StringHelper.trim(rset.getString("quote_item_guid")));
                    epcVmsCaseInfoInCart.setTotalCharge(tmpAmount);
                    epcVmsCaseInfoInCart.setValidToRedeem(false); // init

                    orderCaseList.add(epcVmsCaseInfoInCart);
                }
                rset.close();
                pstmt.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ee) {
            }
        }
        return orderCaseList;
    }

    public void checkValidOffers(int orderId, List<EpcVmsCaseInfoInCart> orderCaseList,
            EpcVmsVoucherInfo epcVmsVoucherInfo) {
        // check with compiled spec by master coupon id
        // https://epcuat-storefront.smartone.com/cpq/voucherInfo?epcOfferEntityId=696dac3f-1683-4eb0-b3e1-5cbfbdf11908&useArea=Master_Voucher_Id&masterVoucherId=Xmas_$200
        // http://epcstorefront.epcuat-storefront.svc.cluster.local:8080/cpq/voucherInfo?epcOfferEntityId=696dac3f-1683-4eb0-b3e1-5cbfbdf11908&useArea=Master_Voucher_Id&masterVoucherId=Xmas_$200
        String masterVoucherId = epcVmsVoucherInfo.getMasterVoucherId();
        String logStr = "[checkValidOffers][" + orderId + "] ";
        String offerGuid = "";
        EpcVmsGetCompiledSpec epcVmsGetCompiledSpec = null;
        HashMap<String, String> offerGuidMap = new HashMap<>();
        HashMap<String, EpcVmsGetCompiledSpec> resultMap = new HashMap<>(); // offer guid, EpcVmsGetCompiledSpec
        ArrayList<EpcVmsGetCompiledSpec> tmpOfferList = new ArrayList<>();
        ArrayList<CompletableFuture<EpcVmsGetCompiledSpec>> futureList = new ArrayList<CompletableFuture<EpcVmsGetCompiledSpec>>();
        CompletableFuture<EpcVmsGetCompiledSpec> future = null;
        CompletableFuture<Void> combinedFuture = null;

        try {
            for (EpcVmsCaseInfoInCart orderCase : orderCaseList) {
                offerGuid = orderCase.getOfferGuid();
                logger.info("{}{}{}", logStr, "check offer ", offerGuid);
                // need to distinct offerGuid b4 getting the compiledspec, kerrytsang, 20220913
                if (offerGuidMap.containsKey(offerGuid)) {
                    continue;
                } else {
                    offerGuidMap.put(offerGuid, "");
                }

                epcVmsGetCompiledSpec = new EpcVmsGetCompiledSpec();
                epcVmsGetCompiledSpec.setMasterVoucherId(masterVoucherId);
                epcVmsGetCompiledSpec.setPackageGuid(offerGuid);
                epcVmsGetCompiledSpec.setValidToRedeem("N");

                tmpOfferList.add(epcVmsGetCompiledSpec);
            }

            // get result
            for (EpcVmsGetCompiledSpec compiledSpec : tmpOfferList) {
                future = CompletableFuture.completedFuture(compiledSpec)
                        .thenApplyAsync(s -> epcCompiledSpecHandler.getCompiledSpecEntityForPackage(s));
                futureList.add(future);
            }

            combinedFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            combinedFuture.get();

            for (CompletableFuture<EpcVmsGetCompiledSpec> f : futureList) {
                epcVmsGetCompiledSpec = f.get();
                if ("Y".equals(epcVmsGetCompiledSpec.getValidToRedeem())) {
                    resultMap.put(epcVmsGetCompiledSpec.getPackageGuid(), epcVmsGetCompiledSpec);
                }
            }
            // end of result

            for (EpcVmsCaseInfoInCart orderCase : orderCaseList) {
                offerGuid = orderCase.getOfferGuid();
                if (resultMap.containsKey(offerGuid)) {
                    orderCase.setValidToRedeem(true);
                    orderCase.setEntityList(resultMap.get(offerGuid).getEntityList());
                } else {
                    orderCase.setValidToRedeem(false);
                }
                logger.info("{}{}{}{}{}", logStr, "offerGuid:", offerGuid, ",validToRedeem:",
                        orderCase.isValidToRedeem());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * call vms api to redeem voucher
     */
    public VmsRedeem2 redeemToVms2(int orderId, VmsRedeem2 vmsRedeem, StringBuilder logSB) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        // String apiUrl = EpcProperty.getValue("EPC_VMS_LINK") +
        // "/VoucherManagementController/rest/voucher/redeem";
        String apiUrl = EpcProperty.getValue("EPC_VMS_LINK") + "/VoucherManagementController/rest/voucher/redemption";
        VmsRedeem2 vmsRedeemResult = null;
        String logStr = "[redeemToVms2][orderId:" + orderId + "] ";
        String tmpLogStr = "";

        try {
//            logger.info("{}{}", logStr, "start");
//            logger.info("{}{}{}", logStr, " api url:", apiUrl);
logSB.append(logStr + " start" + "\n");
logSB.append(logStr + " api url:" + apiUrl + "\n");

            tmpLogStr = objectMapper.writeValueAsString(vmsRedeem);
//            logger.info("{}{}{}", logStr, "request json:", tmpLogStr);
logSB.append(logStr + "request json:" + tmpLogStr + "\n");

            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, new HttpEntity<VmsRedeem2>(vmsRedeem),
                    String.class);

            vmsRedeemResult = objectMapper.readValue(responseEntity.getBody(), VmsRedeem2.class);
//            logger.info("{}{}{}", logStr, "statusCode:", vmsRedeemResult.getStatusCode());
//            logger.info("{}{}{}", logStr, "statusDesc:",
//                    epcSecurityHelper.encodeForSQL(StringHelper.trim(vmsRedeemResult.getStatusDesc())));
logSB.append(logStr + "statusCode:" + vmsRedeemResult.getStatusCode() + "\n");
logSB.append(logStr + "statusDesc:" + epcSecurityHelper.encodeForSQL(StringHelper.trim(vmsRedeemResult.getStatusDesc())) + "\n");
        } catch (HttpStatusCodeException hsce) {
            try {
                vmsRedeemResult = objectMapper.readValue(hsce.getResponseBodyAsString(), VmsRedeem2.class);
            } catch (Exception eee) {
                eee.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

            vmsRedeemResult = new VmsRedeem2();
            vmsRedeemResult.setStatusCode(1); // 0 - success, others - error
            vmsRedeemResult.setStatusDesc(e.getMessage());
        } finally {
            try {
                tmpLogStr = "return json:" + objectMapper.writeValueAsString(vmsRedeemResult);
//                logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr);
            } catch (Exception eee) {
                eee.printStackTrace();
            }
        }
        return vmsRedeemResult;
    }

    /***
     * list customer assigned vouchers (from vms)
     */
	public EpcVmsVoucherInfo invokeVmsList(EpcVmsVoucherListRequest epcVmsVoucherListRequest) {
        EpcVmsVoucherListResponse epcVmsVoucherListResponse = null;
        EpcVmsVoucherListResponseDetail epcVmsVoucherListResponseDetail = null;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_VMS_LINK") + "/VoucherManagementController/rest/voucher/list";
        EpcVmsVoucherInfo epcVmsVoucherInfo = new EpcVmsVoucherInfo();
        epcVmsVoucherInfo.setIsFound("N");

        try {
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcVmsVoucherListRequest),
                    String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                epcVmsVoucherListResponse = objectMapper.readValue(responseEntity.getBody(),
                        EpcVmsVoucherListResponse.class);
                epcVmsVoucherListResponseDetail = epcVmsVoucherListResponse.getVoucherListRes();
                if (epcVmsVoucherListResponseDetail.getStatusCode() == 0) {
                    for (EpcVmsVoucherListResponseDetailCoupon c : epcVmsVoucherListResponseDetail.getCoupon()) {
                        if ("A".equals(StringHelper.trim(c.getStatus()))) {
                            epcVmsVoucherInfo.setMasterVoucherId(StringHelper.trim(c.getMasterCoupon().getMasterCouponId()));
                            epcVmsVoucherInfo.setSerialNo(StringHelper.trim(c.getSerialNo()));
                            epcVmsVoucherInfo.setCouponId(c.getCouponId());
                            epcVmsVoucherInfo.setScope(c.getMasterCoupon().getApplyScope());
                            epcVmsVoucherInfo.setMultipleUse(c.getMasterCoupon().isMultipleUse());
                            epcVmsVoucherInfo.setFaceValueType(c.getMasterCoupon().getFaceValueType());

                            epcVmsVoucherInfo.setIsFound("Y");
                            if (!"".equals(epcVmsVoucherListRequest.getVoucherListReq().getSerialNo())) {
                                epcVmsVoucherInfo.setIsFrom("SERIAL_NO");
                            } else if (!"".equals(epcVmsVoucherListRequest.getVoucherListReq().getMasterCouponId())) {
                                epcVmsVoucherInfo.setIsFrom("MASTER_VOUCHER_ID");
                            }

                            if (VOUCHER_FACE_VALUE_TYPE_FIXED_DISCOUNT.equals(epcVmsVoucherInfo.getFaceValueType())) {
                                if (c.getRemainingAmount().compareTo(new BigDecimal(0)) == 1) {
                                    epcVmsVoucherInfo.setRemainingAmount(c.getRemainingAmount());
                                } else {
                                    epcVmsVoucherInfo.setRemainingAmount(new BigDecimal(-1)); // not to pass amount to
                                                                                              // vms api
                                }
                            } else {
                                epcVmsVoucherInfo.setRemainingAmount(new BigDecimal(-1)); // not to pass amount to vms
                                                                                          // api
                            }

                            epcVmsVoucherInfo.setName(c.getMasterCoupon().getName());
                            epcVmsVoucherInfo.setNameZHHK(c.getMasterCoupon().getName_ZH_HK());
                            epcVmsVoucherInfo.setDescription(c.getMasterCoupon().getDescription());
                            epcVmsVoucherInfo.setDescriptionZHHK(c.getMasterCoupon().getDescript_ZH_HK());

                            epcVmsVoucherInfo.setChargeWaiver(StringHelper.trim(c.getMasterCoupon().getChargeWaiver()));

                            epcVmsVoucherInfo.setSerialNo(c.getSerialNo());
                            epcVmsVoucherInfo.setValidity(c.getMasterCoupon().getValidity());
                            epcVmsVoucherInfo.setStartDate(c.getStartDate());
                            epcVmsVoucherInfo.setEndDate(c.getEndDate());
                            epcVmsVoucherInfo.setCategory(c.getMasterCoupon().getCategory());
                            epcVmsVoucherInfo.setSendEmail(c.getMasterCoupon().getSendEmail() == Boolean.TRUE);
                            epcVmsVoucherInfo.setSendSMS(c.getMasterCoupon().getSendSMS() == Boolean.TRUE);
                            if (c.getMasterCoupon().isNotModelled()) {
                                epcVmsVoucherInfo.setNotModelled("Y");
                            } else {
                                epcVmsVoucherInfo.setNotModelled("N");
                            }

                            if (c.getMasterCoupon().isMobilePlanSubscriptionCoupon()) {
                                epcVmsVoucherInfo.setMobilePlanSubscriptionCoupon("Y");
                            } else {
                                epcVmsVoucherInfo.setMobilePlanSubscriptionCoupon("N");
                            }

                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return epcVmsVoucherInfo;
    }

    /**
     * get voucher list data
     * 
     * @author LincolnLiu
     * @param couponId
     * @return EpcVmsVoucherListResponse
     */
    public String invokeVmsList(String couponId) {
        EpcVmsVoucherListRequestDetail vvDetail = new EpcVmsVoucherListRequestDetail();
        vvDetail.setCouponId(couponId);
        EpcVmsVoucherListRequest epcVmsVoucherListRequest = new EpcVmsVoucherListRequest();
        epcVmsVoucherListRequest.setVoucherListReq(vvDetail);
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = EpcProperty.getValue("EPC_VMS_LINK") + "/VoucherManagementController/rest/voucher/list";

        try {
            ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl,
                    new HttpEntity<EpcVmsVoucherListRequest>(epcVmsVoucherListRequest),
                    String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {

                return responseEntity.getBody();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /***
     * create a thread to send voucher email / sms
     *  invoked by placeOrder()
     */
    public void sendVoucherEmailSmsAsync(int orderId, String customerId) {
        Object[] oArray = new Object[2];
        oArray[0] = Integer.valueOf(orderId);
        oArray[1] = customerId;

        try {
            CompletableFuture.completedFuture(oArray).thenApplyAsync(s -> sendVoucherEmailSms((Integer)s[0], (String)s[1]));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * save vouchers and send Email/Sms
     * 
     * @author LincolnLiu
     * @param orderId
     * @param customerId
     * @throws Exception
     */
    public String sendVoucherEmailSms(Integer orderId, String customerId) {
    	try {
    		//save Response from vms
            List<EpcVmsVoucherListResponseDetailCoupon> vvList = saveVoucherEmailSms(orderId);
            // Combine data to send
            sendVoucherEmailSms(orderId, customerId, vvList);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
        return "OK";
    }

    /**
     * save VmsVoucherListResponse
     * @author LincolnLiu
     * @param orderId
     * @return Coupon Detail by can use
     * @throws Exception
     */
    private List<EpcVmsVoucherListResponseDetailCoupon> saveVoucherEmailSms(Integer orderId) throws Exception {

        List<EpcVmsVoucherListResponseDetailCoupon> vvList = new ArrayList<EpcVmsVoucherListResponseDetailCoupon>();
        Connection conn = null;
        try {
            conn = epcDataSource.getConnection();
            Map<String, String> couponMap = new LinkedHashMap<String, String>();
            // select couponId(ASSIGN_ID) and key of EPC_ORDER_VOUCHER (ITEM_ID) put to map
            // by orderId
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT ASSIGN_ID,ITEM_ID  FROM EPC_ORDER_VOUCHER WHERE ASSIGN_REDEEM = 'ASSIGN' AND ORDER_ID = ?")) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String couponId = rs.getString(1);
                        String itemId = rs.getString(2);
                        couponMap.put(couponId, itemId);
                    }
                }
            }

            // call VmsList api to get VmsVoucherList ,loop for couponId
            // use CompletableFuture to call
            Set<String> couponList = couponMap.keySet();
            List<CompletableFuture<String>> futureList = new ArrayList<CompletableFuture<String>>();
            for (String couponId : couponList) {
                CompletableFuture<String> future = CompletableFuture.completedFuture(couponId)
                        .thenApplyAsync(s -> invokeVmsList(s));
                futureList.add(future);
            }
            CompletableFuture<Void> combinedFuture = CompletableFuture
                    .allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            combinedFuture.get();

            // update Voucher data to the record of corresponding itemId
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE EPC_ORDER_VOUCHER SET VMS_LIST_RESPONSE =? WHERE ORDER_ID =? AND ITEM_ID =?")) {
                for (CompletableFuture<String> completableFuture : futureList) {
                    String vvInfoString = completableFuture.get();
                    if (vvInfoString != null) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        EpcVmsVoucherListResponse vvInfo = objectMapper.readValue(vvInfoString,
                                EpcVmsVoucherListResponse.class);
                        ArrayList<EpcVmsVoucherListResponseDetailCoupon> Coupons = vvInfo.getVoucherListRes()
                                .getCoupon();
                        StringReader reader = new StringReader(vvInfoString);
                        ps.setCharacterStream(1, reader, vvInfoString.length());
                        ps.setInt(2, orderId);
                        String itemId = couponMap.get(Coupons.get(0).getCouponId());
                        ps.setString(3, itemId);
                        ps.addBatch();

                        for (EpcVmsVoucherListResponseDetailCoupon coupon : Coupons) {
                            if (isCoupon(coupon)) {
                                vvList.add(coupon);
                            }
                        }
                    }
                }
                ps.executeBatch();
                ps.clearBatch();
                conn.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null)
                conn.rollback();
            throw e;
        } finally {
            if (conn != null)
                try {
                    conn.setAutoCommit(true);
                } catch (Exception e) {
                }
//            DataSourceUtils.releaseConnection(conn, epcDataSource);
            if (conn != null)
                try {
                    conn.close();
                } catch (Exception e) {
            }
        }
        return vvList;
    }

    /**
     * send Email/Sms by Voucher record
     * 
     * @author LincolnLiu
     * @param orderId
     * @param customerId
     * @param vvList     (Voucher record)
     * @throws Exception
     */
    private void sendVoucherEmailSms(Integer orderId, String customerId,
            List<EpcVmsVoucherListResponseDetailCoupon> vvList) throws Exception {
        if (vvList == null || vvList.isEmpty()) {
            return;
        }

        // Get customer information
        final String chatCode = "utf-8";
        EpcOrderContact contact = epcContactInfoHandler.getContactInfo(customerId, orderId, "");
        String CUST_NAME = "";
        String LastName = EpcCrypto.dGet(contact.getContactPersonLastName(), chatCode);
        String FirstName = EpcCrypto.dGet(contact.getContactPersonFirstName(), chatCode);
        if ("E".equals(contact.getOrderLang())) {
            CUST_NAME = FirstName + " " + LastName;
        } else {
            CUST_NAME = LastName + FirstName;
        }

        // Loop Voucher data to send
        for (EpcVmsVoucherListResponseDetailCoupon vvInfo : vvList) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("CUST_NAME", CUST_NAME + "");
            params.put("MASTER_COUPON_ID", vvInfo.getMasterCoupon().getMasterCouponId() + "");
            params.put("COUPON_SERIAL_NO", vvInfo.getSerialNo() + "");
            if ("E".equals(contact.getOrderLang())) {
                params.put("COUPON_NAME", vvInfo.getMasterCoupon().getName() + "");
                params.put("COUPON_DESCRIPTION", vvInfo.getMasterCoupon().getDescription() + "");
            } else {
                params.put("COUPON_NAME", vvInfo.getMasterCoupon().getName_ZH_HK() + "");
                params.put("COUPON_DESCRIPTION", vvInfo.getMasterCoupon().getDescript_ZH_HK() + "");
            }
            //params.put("COUPON_NAME", vvInfo.getMasterCoupon().getName() + "");
            //params.put("COUPON_DESCRIPTION", vvInfo.getMasterCoupon().getDescription() + "");
            params.put("COUPON_AMOUNT", "$" + vvInfo.getRemainingAmount());
            params.put("COUPON_VALIDITY_DAY", vvInfo.getMasterCoupon().getValidity() + "");
            params.put("COUPON_START_DATE", vvInfo.getStartDate() + "");
            params.put("COUPON_EXPIRED_DATE", vvInfo.getEndDate() + "");

            Date date = DateUtils.parseDate(vvInfo.getEndDate(), new String[] { "yyyy-MM-dd'T'HH:mm:ss.SSS" });
            params.put("EXPIRED_YYYY", DateFormatUtils.format(date, "yyyy"));
            params.put("EXPIRED_MM", DateFormatUtils.format(date, "MM"));
            params.put("EXPIRED_DD", DateFormatUtils.format(date, "dd"));

            EpcSendMsgRequest msg = new EpcSendMsgRequest();
            msg.setLanguage(contact.getOrderLang());
            msg.setOrderId(orderId + "");
            msg.setRequestId(vvInfo.getCouponId());
            msg.setTemplateType("VOUCHER_ENQUIRY");
            msg.setParams(params);

            if (vvInfo.getMasterCoupon().getSendEmail()) {
                msg.setRecipient(EpcCrypto.dGet(contact.getContactEmail(), chatCode));
                msg.setSendType("EMAIL");
                epcMsgHandler.sendMsg(msg);
            }

            if (vvInfo.getMasterCoupon().getSendSMS()) {
                msg.setRecipient(contact.getContactNo());
                msg.setSendType("SMS");
                epcMsgHandler.sendMsg(msg);
            }
        }
    }

    /**
     * Get AssignedVoucher by orderId
     * @param orderId
     * @return
     * @throws Exception
     */
    public List<VmsAssignVoucher> getAssignedVoucher(Integer orderId) throws Exception {
        List<VmsAssignVoucher> list = new ArrayList<VmsAssignVoucher>();
        try (Connection conn = epcDataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM EPC_ORDER_VOUCHER WHERE ASSIGN_REDEEM = 'ASSIGN' AND STATUS ='A' AND ORDER_ID = ?")) {
                ps.setInt(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    ArrayList<EpcVmsVoucherListResponseDetailCoupon> api_coupons = null;
                    while (rs.next()) {
                        VmsAssignVoucher vav = ResultSetToVmsAssignVoucher(rs);
                        if (vav.getVmsListResponse() != null) {
                        	ArrayList<EpcVmsVoucherListResponseDetailCoupon> coupons = vav.getVmsListResponse().getVoucherListRes().getCoupon();
                            for (EpcVmsVoucherListResponseDetailCoupon coupon : coupons) {
                                if (isCoupon(coupon)) {
                                    list.add(vav);
                                    continue;
                                }
                            }
                        } else {
                            if (api_coupons == null) {
                            	api_coupons = new ArrayList<EpcVmsVoucherListResponseDetailCoupon>(
                                        saveVoucherEmailSms(orderId));
                            }
                            for (EpcVmsVoucherListResponseDetailCoupon coupon : api_coupons) {
                                if (coupon.getCouponId().equals(vav.getAssignId())) {
                                	EpcVmsVoucherListResponse vmsListResponse=new EpcVmsVoucherListResponse();
                            		EpcVmsVoucherListResponseDetail voucherListRes=new EpcVmsVoucherListResponseDetail();
                                    ArrayList<EpcVmsVoucherListResponseDetailCoupon> dlist=new ArrayList<EpcVmsVoucherListResponseDetailCoupon>();
                                    dlist.add(coupon);
                            		voucherListRes.setCoupon(dlist);
                            		vmsListResponse.setVoucherListRes(voucherListRes);
                            		vav.setVmsListResponse(vmsListResponse);
                                    list.add(vav);
                                    continue;
                                }
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return list;

    }

    /**
     * valid logic
     * @param coupon
     * @return
     */
    private boolean isCoupon(EpcVmsVoucherListResponseDetailCoupon coupon) {
        boolean before = false;
        try {
            if (coupon.getEndDate() != null) {
            	Date date = DateUtils.parseDate(coupon.getEndDate(), new String[] { "yyyy-MM-dd'T'HH:mm:ss.SSS" });
                before = new Date().before(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        return ("A".equals(StringHelper.trim(coupon.getStatus()))
//                && ("Handset / Accessory discount".equals(coupon.getMasterCoupon().getCategory())
//                        || "e-Coupon".equals(coupon.getMasterCoupon().getCategory())))
//                && before;
        return ("A".equals(StringHelper.trim(coupon.getStatus()))
                && (VOUCHER_CATEGORY_HANDSET_ACCESSORY_DISCOUNT.equals(coupon.getMasterCoupon().getCategory()) 
                        || VOUCHER_CATEGORY_E_COUPON.equals(coupon.getMasterCoupon().getCategory()))) 
                && before;
    }

    /**
     * send one Voucher
     * @param orderId
     * @param customerId
     * @param itemId
     * @throws Exception
     */
    public void sendVoucherEmailSms(Integer orderId, String customerId, String itemId) throws Exception {

        try (Connection conn = epcDataSource.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT * FROM EPC_ORDER_VOUCHER WHERE  ORDER_ID = ? AND ITEM_ID=?")) {
                ps.setInt(1, orderId);
                ps.setString(2, itemId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        VmsAssignVoucher vav = ResultSetToVmsAssignVoucher(rs);
                        sendVoucherEmailSms(orderId, customerId,
                                vav.getVmsListResponse().getVoucherListRes().getCoupon());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private VmsAssignVoucher ResultSetToVmsAssignVoucher(ResultSet rs) throws SQLException  {
        VmsAssignVoucher vav = new VmsAssignVoucher();
        try {
		vav.setOrderId(rs.getInt("ORDER_ID"));
        vav.setCaseId(rs.getString("CASE_ID"));
        vav.setItemId(rs.getString("ITEM_ID"));
        vav.setVoucherGuid(rs.getString("VOUCHER_GUID"));
        vav.setVoucherMasterId(rs.getString("VOUCHER_MASTER_ID"));
        vav.setVoucherCode(rs.getString("VOUCHER_CODE"));
        vav.setAssignId(rs.getString("ASSIGN_ID"));
        vav.setTransactionId(rs.getString("TRANSACTION_ID"));
        vav.setQuoteGuid(rs.getString("QUOTE_GUID"));

        String re = StringHelper.trim(rs.getString("VMS_LIST_RESPONSE"));
        if(!"".equals(re)) {
            vav.setVmsListResponse(new ObjectMapper().readValue(re, EpcVmsVoucherListResponse.class));
        }
//        Clob vlr = rs.getClob("VMS_LIST_RESPONSE");
//        
//        if(vlr!=null) {
//        Reader is = vlr.getCharacterStream();
//        BufferedReader br = new BufferedReader(is);
//        StringBuffer sb = new StringBuffer();
//
//        String s = br.readLine();
//        while (s != null) {
//            sb.append(s);
//            s = br.readLine();
//        }
//
//        if (!sb.isEmpty()) {
//            String re = sb.toString();
//            ObjectMapper mapper = new ObjectMapper();
//            EpcVmsVoucherListResponse vvlr = mapper.readValue(re, EpcVmsVoucherListResponse.class);
//            vav.setVmsListResponse(vvlr);
//        }
//        }
		} catch (IOException e) {
			e.printStackTrace();
		}
        return vav;
    }

    /*
    public ArrayList<EpcVmsVoucherListRequest> createRequestList(String custId, String voucherCode) {
        ArrayList<EpcVmsVoucherListRequest> aList = new ArrayList<>();
        EpcVmsVoucherListRequest epcVmsVoucherListRequest = null;
        EpcVmsVoucherListRequestDetail epcVmsVoucherListRequestDetail = null;

        // create 4 call input object
        // cust id + serial no
        epcVmsVoucherListRequest = new EpcVmsVoucherListRequest();
        epcVmsVoucherListRequestDetail = new EpcVmsVoucherListRequestDetail();
        epcVmsVoucherListRequest.setVoucherListReq(epcVmsVoucherListRequestDetail);

        epcVmsVoucherListRequestDetail.setCustomerId(custId);
        epcVmsVoucherListRequestDetail.setMasterCouponId("");
        epcVmsVoucherListRequestDetail.setSerialNo(voucherCode);

        aList.add(epcVmsVoucherListRequest);
        // cust id + serial no

        // cust id + master coupon id
        epcVmsVoucherListRequest = new EpcVmsVoucherListRequest();
        epcVmsVoucherListRequestDetail = new EpcVmsVoucherListRequestDetail();
        epcVmsVoucherListRequest.setVoucherListReq(epcVmsVoucherListRequestDetail);

        epcVmsVoucherListRequestDetail.setCustomerId(custId);
        epcVmsVoucherListRequestDetail.setMasterCouponId(voucherCode);
        epcVmsVoucherListRequestDetail.setSerialNo("");

        aList.add(epcVmsVoucherListRequest);
        // cust id + master coupon id

        // no cust id + serial no
        epcVmsVoucherListRequest = new EpcVmsVoucherListRequest();
        epcVmsVoucherListRequestDetail = new EpcVmsVoucherListRequestDetail();
        epcVmsVoucherListRequest.setVoucherListReq(epcVmsVoucherListRequestDetail);

        epcVmsVoucherListRequestDetail.setCustomerId("");
        epcVmsVoucherListRequestDetail.setMasterCouponId("");
        epcVmsVoucherListRequestDetail.setSerialNo(voucherCode);

        aList.add(epcVmsVoucherListRequest);
        // no cust id + serial no

        // // no cust id + master coupon id
        // epcVmsVoucherListRequest = new EpcVmsVoucherListRequest();
        // epcVmsVoucherListRequestDetail = new EpcVmsVoucherListRequestDetail();
        // epcVmsVoucherListRequest.setVoucherListReq(epcVmsVoucherListRequestDetail);
        //
        // epcVmsVoucherListRequestDetail.setCustomerId("");
        // epcVmsVoucherListRequestDetail.setMasterCouponId(voucherCode);
        // epcVmsVoucherListRequestDetail.setSerialNo("");
        //
        // aList.add(epcVmsVoucherListRequest);
        // // no cust id + master coupon id
        // end of create 4 call input object

        return aList;
    }
    */
    
    // alternative of createRequestList which can construct request list for both LIST and INFO API
    public ArrayList<EpcVmsVoucherCodeRequest> createRequestList(String custId, String voucherCode) {
        ArrayList<EpcVmsVoucherCodeRequest> resultList = new ArrayList<>();
        EpcVmsVoucherCodeRequest epcVmsVoucherCodeRequest = null;
        EpcVmsVoucherListRequestDetail epcVmsVoucherListRequestDetail = null;

        // create 4 call input object
        // cust id + serial no
        epcVmsVoucherCodeRequest = new EpcVmsVoucherCodeRequest();
        epcVmsVoucherCodeRequest.setQueryByApi(EpcVmsVoucherCodeRequest.LIST);
        epcVmsVoucherListRequestDetail = new EpcVmsVoucherListRequestDetail();
        epcVmsVoucherCodeRequest.setVoucherListReq(epcVmsVoucherListRequestDetail);

        epcVmsVoucherListRequestDetail.setCustomerId(custId);
        epcVmsVoucherListRequestDetail.setMasterCouponId("");
        epcVmsVoucherListRequestDetail.setSerialNo(voucherCode);

        resultList.add(epcVmsVoucherCodeRequest);
        // End of cust id + serial no

        // cust id + master coupon id
        epcVmsVoucherCodeRequest = new EpcVmsVoucherCodeRequest();
        epcVmsVoucherCodeRequest.setQueryByApi(EpcVmsVoucherCodeRequest.LIST);
        epcVmsVoucherListRequestDetail = new EpcVmsVoucherListRequestDetail();
        epcVmsVoucherCodeRequest.setVoucherListReq(epcVmsVoucherListRequestDetail);

        epcVmsVoucherListRequestDetail.setCustomerId(custId);
        epcVmsVoucherListRequestDetail.setMasterCouponId(voucherCode);
        epcVmsVoucherListRequestDetail.setSerialNo("");

        resultList.add(epcVmsVoucherCodeRequest);
        // End of cust id + master coupon id

        // no cust id + serial no
        epcVmsVoucherCodeRequest = new EpcVmsVoucherCodeRequest();
        epcVmsVoucherCodeRequest.setQueryByApi(EpcVmsVoucherCodeRequest.LIST);
        epcVmsVoucherListRequestDetail = new EpcVmsVoucherListRequestDetail();
        epcVmsVoucherCodeRequest.setVoucherListReq(epcVmsVoucherListRequestDetail);

        epcVmsVoucherListRequestDetail.setCustomerId("");
        epcVmsVoucherListRequestDetail.setMasterCouponId("");
        epcVmsVoucherListRequestDetail.setSerialNo(voucherCode);

        resultList.add(epcVmsVoucherCodeRequest);
        // End of no cust id + serial no

        // no cust id + master coupon id
        epcVmsVoucherCodeRequest = new EpcVmsVoucherCodeRequest();
        epcVmsVoucherCodeRequest.setQueryByApi(EpcVmsVoucherCodeRequest.INFO);
        epcVmsVoucherListRequestDetail = new EpcVmsVoucherListRequestDetail();
        epcVmsVoucherCodeRequest.setVoucherListReq(epcVmsVoucherListRequestDetail);
        
        epcVmsVoucherListRequestDetail.setCustomerId("");
        epcVmsVoucherListRequestDetail.setMasterCouponId(voucherCode);
        epcVmsVoucherListRequestDetail.setSerialNo("");
        
        resultList.add(epcVmsVoucherCodeRequest);
        // End of no cust id + master coupon id
        // end of create 4 call input object

        return resultList;
    }

    /***
     * check whether the input voucher is valid
     * - is master coupon id or serial no
     */
    public EpcVmsVoucherInfo getVoucherInfo(String custId, String voucherCode) {
        EpcVmsVoucherInfo epcVmsVoucherInfo = new EpcVmsVoucherInfo();
        epcVmsVoucherInfo.setIsFound("N");
        EpcVmsVoucherInfo tmpEpcVmsVoucherInfo = null;
        //ArrayList<EpcVmsVoucherListRequest> requestList = createRequestList(custId, voucherCode);
        ArrayList<EpcVmsVoucherCodeRequest> requestList = createRequestList(custId, voucherCode);
        ArrayList<CompletableFuture<EpcVmsVoucherInfo>> futureList = new ArrayList<CompletableFuture<EpcVmsVoucherInfo>>();
        CompletableFuture<EpcVmsVoucherInfo> future = null;
        CompletableFuture<Void> combinedFuture = null;

        try {
            
            //for (EpcVmsVoucherListRequest q : requestList) {
            //    future = CompletableFuture.completedFuture(q).thenApplyAsync(s -> invokeVmsList(s));
            //    futureList.add(future);
            //}
            for (EpcVmsVoucherCodeRequest q : requestList) {
                future = CompletableFuture.completedFuture(q).thenApplyAsync(s -> invokeVmsApi(s));
                futureList.add(future);
            }

            combinedFuture = CompletableFuture.allOf(futureList.toArray(new CompletableFuture[futureList.size()]));
            combinedFuture.get();

            for (CompletableFuture<EpcVmsVoucherInfo> f : futureList) {
                tmpEpcVmsVoucherInfo = f.get();
                if ("Y".equals(tmpEpcVmsVoucherInfo.getIsFound())) {
                    epcVmsVoucherInfo = tmpEpcVmsVoucherInfo;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return epcVmsVoucherInfo;
    }

    /***
     * called by controller
     */
    public void redeemVoucher(EpcRedeemVoucher epcRedeemVoucher) {
        int orderId = epcRedeemVoucher.getOrderId();
        String iCustId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getCustId()));
        String iAction = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getAction())); // ADD / DELETE / REFRESH
        String orderReference = "";
        boolean isValid = true;
        StringBuilder errMsgSB = new StringBuilder();
        StringBuilder logSB = new StringBuilder(2048); // 2048 bytes
        String tmpLogStr = "";
        EpcActionLog epcActionLog = null;
        String lockAction = "";

        logSB.append("[redeemVoucher][orderId:" + orderId + "][action:" + iAction + "]\n");

        // basic checking
        orderReference = epcOrderHandler.isOrderBelongCust(iCustId, orderId);
        if ("NOT_BELONG".equals(orderReference)) {
            isValid = false;
            errMsgSB.append("input order id [" + orderId + "] is not belonged to input cust id [" + iCustId + "]. ");
        } else {
            epcRedeemVoucher.setOrderReference(orderReference);
        }

        if(epcOrderHandler.isOrderLocked(iCustId, orderId)) {
            errMsgSB.append("input order [" + orderId + "] is locked. ");
            isValid = false;
        }

        if (!REDEEM_ACTION_ADD.equals(iAction)
                && !REDEEM_ACTION_DELETE.equals(iAction)
                && !REDEEM_ACTION_REFRESH.equals(iAction)) {
            isValid = false;
            errMsgSB.append("input action [" + iAction + "] is not valid. ");
        }
        
        // check action lock
        lockAction = epcOrderAttrHandler.getAttrValue(orderId, "", "", epcOrderAttrHandler.ATTR_TYPE_LOCK_VOUCHER);
        if("Y".equals(lockAction)) {
            // locked by other process, exit !
            //  to prevent concurrent calls
            errMsgSB.append("locked by other process / request");
            isValid = false;
        }

        // end of basic checking

        if (isValid) {
//            if (REDEEM_ACTION_ADD.equals(iAction)) {
//                redeemVoucherAdd2(epcRedeemVoucher);
//            } else if (REDEEM_ACTION_DELETE.equals(iAction)) {
//                redeemVoucherDelete2(epcRedeemVoucher);
//            } else {
//                redeemVoucherRefresh(epcRedeemVoucher);
//            }
            
            // make a action lock
            epcOrderAttrHandler.addAttr(orderId, "", "", epcOrderAttrHandler.ATTR_TYPE_LOCK_VOUCHER, "Y");
            
            if (REDEEM_ACTION_DELETE.equals(iAction)) {
                redeemVoucherDelete2(epcRedeemVoucher, logSB);
            } else {
                // ADD / REFRESH
                redeemVoucherRefresh(epcRedeemVoucher, logSB);
            }
            
            // free action lock
            epcOrderAttrHandler.obsoleteAttr(orderId, "", "", epcOrderAttrHandler.ATTR_TYPE_LOCK_VOUCHER);
            
        } else {
            epcRedeemVoucher.setResult("FAIL");
            epcRedeemVoucher.setErrMsg(errMsgSB.toString());
        }

        tmpLogStr = epcSecurityHelper.encodeForSQL(logSB.toString());
logger.info("{}", tmpLogStr);


        // create action log
        epcActionLog = new EpcActionLog();
        epcActionLog.setAction("REDEEM_VOUCHER");
        epcActionLog.setUri("");
        epcActionLog.setInString(tmpLogStr);
        epcActionLog.setOutString("");

        epcActionLogHandler.writeApiLogAsync(epcActionLog);
        // end of create action log
    }

    /**
     * revamp (separated from CPQ, epcSales <-> vms only), 20221219
     * 
     * @param epcRedeemVoucher
     */
    public void redeemVoucherAdd2(EpcRedeemVoucher epcRedeemVoucher, StringBuilder logSB) {
        int orderId = epcRedeemVoucher.getOrderId();
        String orderReference = epcRedeemVoucher.getOrderReference();
        String loginLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getLoginLocation()));
        String loginChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getLoginChannel()));
        String iCustId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getCustId()));
        String iVoucherCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getVoucherCode()));
        String masterVoucherId = "";
        BigDecimal voucherRemainingAmount = null;
        boolean isValid = true;
        StringBuilder errMsgSB = new StringBuilder();
        EpcVmsVoucherInfo epcVmsVoucherInfo = null;
        HashMap<String, EpcVmsVoucherInfo> voucherInfoMap = new HashMap<>();
        ArrayList<EpcVmsCaseInfoInCart> orderCaseList = null;
        boolean isOrderCaseValidToRedeem = false;
        VmsRedeem2 vmsRedeem = null;
        VmsRedeem2 vmsRedeemResult = null;
        boolean isCreateEpcRecord = false;
        ArrayList<VmsOrderVoucher> failVouchers = new ArrayList<>();
        epcRedeemVoucher.setFailVouchers(failVouchers);
        VmsOrderVoucher failVoucher = null;
        String logStr = "[redeemVoucherAdd2][orderId:" + orderId + "] ";
        String tmpLogStr = "";

        try {
            // basic checking

            // get voucher info
            epcVmsVoucherInfo = getVoucherInfo(iCustId, iVoucherCode);
            if ("N".equals(epcVmsVoucherInfo.getIsFound())) {
                tmpLogStr = "master voucher id is NOT found";
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");

                isValid = false;
                errMsgSB.append("master voucher id is not found / not valid. ");
            } else {
                masterVoucherId = epcVmsVoucherInfo.getMasterVoucherId();

                tmpLogStr = "master voucher id is found [" + masterVoucherId + "], scope:"
                        + epcVmsVoucherInfo.getScope();
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");
            }
            // end of get voucher info

            // check valid offers under order input (from compiled spec)
            orderCaseList = getOrderCaseList2(orderId, epcVmsVoucherInfo.getScope());

            if (VOUCHER_SCOPE_QUOTE_LEVEL.equals(epcVmsVoucherInfo.getScope())) {
                for (EpcVmsCaseInfoInCart orderCase : orderCaseList) {
                    if (orderCase.isValidToRedeem()) {
                        isOrderCaseValidToRedeem = true;
                    }

                    tmpLogStr = "masterVoucherId:" + epcVmsVoucherInfo.getMasterVoucherId() + 
                                ",couponId:" + epcVmsVoucherInfo.getCouponId() + 
                                ",scope:" + epcVmsVoucherInfo.getScope() + 
                                ",caseId:" + orderCase.getCaseId() +
                                ",offerGuid:" + orderCase.getOfferGuid() +
                                ",totalCaseAmount:" + orderCase.getTotalCharge() +
                                ",validToRedeem:" + orderCase.isValidToRedeem();
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");
                }
            } else {
                checkValidOffers(orderId, orderCaseList, epcVmsVoucherInfo);

                for (EpcVmsCaseInfoInCart orderCase : orderCaseList) {
                    if (orderCase.isValidToRedeem()) {
                        isOrderCaseValidToRedeem = true;
                    }

                    tmpLogStr = "masterVoucherId:" + epcVmsVoucherInfo.getMasterVoucherId() + 
                                ",couponId:" + epcVmsVoucherInfo.getCouponId() + 
                                ",scope:" + epcVmsVoucherInfo.getScope() + 
                                ",caseId:" + orderCase.getCaseId() +
                                ",offerGuid:" + orderCase.getOfferGuid() +
                                ",caseAmount:" + orderCase.getTotalCharge() +
                                ",validToRedeem:" + orderCase.isValidToRedeem();
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");
                }
            }
//logger.info("{}{}{}", logStr, "isOrderCaseValidToRedeem:", isOrderCaseValidToRedeem);
logSB.append(logStr + "isOrderCaseValidToRedeem:" + isOrderCaseValidToRedeem + "\n");

            if (!isOrderCaseValidToRedeem) {
                isValid = false;
                errMsgSB.append("this voucher is not applicable to offer(s) in cart. ");
            }
            // end of check valid offers under order input (from compiled spec)

            // end of basic checking


            if (isValid) {
                voucherRemainingAmount = epcVmsVoucherInfo.getRemainingAmount();
//logger.info("{}{}{}", logStr, " voucherRemainingAmount:", voucherRemainingAmount);
logSB.append(logStr + " voucherRemainingAmount:" + voucherRemainingAmount + "\n");

                // prepare input obj for vms call
                vmsRedeem = constructVmsInput(
                    iCustId, orderId, orderReference, orderCaseList, epcVmsVoucherInfo, iVoucherCode,
                    loginChannel, loginLocation, REDEEM_ACTION_ADD, logSB
                );
                if (vmsRedeem == null) {
                    throw new Exception("cannot construct input param to VMS");
                }
                // end of prepare input obj for vms call

                // prepare voucherInfoMap for voucher redeemed under this order
                voucherInfoMap = getRedeemedVoucherMap(iCustId, orderId, null);
                // end of prepare voucherInfoMap for voucher redeemed under this order

                // add incoming voucher
                voucherInfoMap.put(epcVmsVoucherInfo.getMasterVoucherId(), epcVmsVoucherInfo);
                // end of add incoming voucher

                // call vms api
//logger.info("{}{}", logStr, "call vms api");
logSB.append(logStr + "call vms api" + "\n");
                vmsRedeemResult = redeemToVms2(orderId, vmsRedeem, logSB);
                if (vmsRedeemResult.getStatusCode() == 0) {
                    // success
                    // update EPC record
                    isCreateEpcRecord = refreshEpcRecord(orderId, vmsRedeem, vmsRedeemResult, voucherInfoMap, logSB);
                    logger.info("{}{}{}", logStr, "refreshEpcRecord:", isCreateEpcRecord);
                    if (isCreateEpcRecord) {
                        epcRedeemVoucher.setResult("SUCCESS");
                    } else {
                        // rollback vms call ???

                        epcRedeemVoucher.setResult("FAIL");
                        epcRedeemVoucher.setErrMsg("cannot create voucher records");
                    }
                } else {
                    // error
                    errMsgSB = new StringBuilder();

                    if (vmsRedeemResult.getVouchers() != null) {
                        // error from vms
                        for (VmsVoucher2 v : vmsRedeemResult.getVouchers()) {
                            if (!v.isValid()) {
                                errMsgSB.append("master coupon id [" + v.getMasterCouponId()
                                        + "] is not valid, reject reason:" + v.getRejectReason());

                                failVoucher = new VmsOrderVoucher();
                                failVoucher.setVoucherMasterId(v.getMasterCouponId());
                                failVoucher.setRejectReason(v.getRejectReason());
                                failVouchers.add(failVoucher);
                            }
                        }
                    } else {
                        // error from generic exception
                        errMsgSB.append(vmsRedeemResult.getStatusDesc());
                    }

                    epcRedeemVoucher.setResult("FAIL");
                    epcRedeemVoucher.setErrMsg(errMsgSB.toString());
                }
                // end of call vms api
            } else {
                epcRedeemVoucher.setResult("FAIL");
                epcRedeemVoucher.setErrMsg(errMsgSB.toString());

                failVoucher = new VmsOrderVoucher();
                failVoucher.setVoucherMasterId(iVoucherCode);
                failVoucher.setRejectReason(errMsgSB.toString());
                failVouchers.add(failVoucher);
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcRedeemVoucher.setResult("FAIL");
            epcRedeemVoucher.setErrMsg(e.getMessage());
        } finally {
        }
logSB.append(logStr + "Done" + "\n");
    }

    public VmsRedeem2 constructVmsInput(
            String custId, int orderId, String orderReference,
            ArrayList<EpcVmsCaseInfoInCart> orderCaseList,
            EpcVmsVoucherInfo epcVmsVoucherInfo, String voucherCode,
            String loginChannel, String loginLocation, String actionType,
            StringBuilder logSB
    ) {
        VmsRedeem2 vmsRedeem = new VmsRedeem2();
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtItem = null;
        PreparedStatement pstmtCharge = null;
        PreparedStatement pstmtVoucherInOrderLevel = null;
        PreparedStatement pstmtVoucherInQuoteItemLevel = null;
        PreparedStatement pstmtVoucherInProductLevel = null;
        PreparedStatement pstmtDistrictRedeemedVoucher = null;
        PreparedStatement pstmtVoucherTransaction = null;
        ResultSet rset = null;
        ResultSet rsetItem = null;
        ResultSet rsetCharge = null;
        ResultSet rsetVoucherInOtherLevel = null;
        ResultSet rsetVoucherTransaction = null;
        String sql = "";
        ArrayList<VmsProductDetail> productDetailList = null;
        VmsProductDetail vmsProductDetail = null;
        ArrayList<VmsProduct> productList = null;
        VmsProduct vmsProduct = null;
        ArrayList<VmsCharge> chargeList = null;
        VmsCharge vmsCharge = null;
        ArrayList<VmsVoucher2> voucherList = null;
        ArrayList<VmsVoucher2> redeemedVoucherList = null;
        ArrayList<VmsOrderVoucher> redeemedVoucherInEPCDbList = null;
        VmsVoucher2 vmsVoucher = null;
        BigDecimal voucherRemainingAmount = null;
        BigDecimal caseAmount = null;
        BigDecimal tmpAmount = null;
        ArrayList<VmsDiscount> discountList = null;
        ArrayList<VmsDiscount> discountListInQuoteItemLevel = null;
        ArrayList<VmsDiscount> discountListInProductLevel = null;
        VmsDiscount vmsDiscount = null;
        boolean isCaseContainFaceValueTypeAlready = true;
        boolean isCaseContainCategoryAlready = true;
        boolean isBreakAllocationLoop = false;
        String logStr = "[constructVmsInput][orderId:" + orderId + "] ";
        String tmpLogStr = "";

        try {
            conn = epcDataSource.getConnection();

            vmsRedeem.setChannel(loginChannel);
            vmsRedeem.setLocation(loginLocation);
            if (REDEEM_ACTION_DELETE.equals(actionType) || REDEEM_ACTION_REFRESH_DELETE.equals(actionType)) {
                vmsRedeem.setForceRemove(true);
            }

            // prepare sql
            sql = "select a.cpq_quote_guid, a.quote_id, b.quote_item_guid, b.case_id, b.cpq_offer_desc, " +
                    "       (select template_name " +
                    "          from epc_order_item c " +
                    "         where c.order_id = a.order_id " +
                    "           and c.quote_id = b.quote_id " +
                    "           and c.case_id = b.case_id " +
                    "           and c.parent_item_id is null " +
                    "       ) as template_name " +
                    "  from epc_order_quote a, epc_order_case b " +
                    " where a.order_id = ? " +
                    "   and b.order_id = a.order_id " +
                    "   and b.quote_id = a.quote_id ";
            pstmt = conn.prepareStatement(sql);

            sql = "select item_id, item_code, template_name, " +
                    "       (select catalog_item_desc " +
                    "          from epc_order_item y " +
                    "         where y.order_id = z.order_id " +
                    "           and y.quote_id = z.quote_id " +
                    "           and y.case_id = z.case_id " +
                    "           and y.parent_item_id = z.item_id " +
                    "           and y.item_cat = ? " +
                    "           and catalog_item_desc is not null " +
                    "           and rownum = 1 " +
                    "       ) as catalog_desc " +
                    "  from epc_order_item z " +
                    " where order_id = ? " +
                    "   and quote_id = ? " +
                    "   and case_id = ? " +
                    "   and item_cat = ? ";
            pstmtItem = conn.prepareStatement(sql);

            sql = "select a.item_id, a.charge_amount, b.template_name, b.cpq_item_desc, b.catalog_rrp " +
                    "  from epc_order_charge a, epc_order_item b " +
                    " where a.order_id = ? " +
                    "   and a.quote_id = ? " +
                    "   and a.case_id = ? " +
                    "   and a.parent_item_id = ? " +
                    "   and a.charge_code != '99' " +
                    "   and b.order_id = a.order_id " +
                    "   and b.quote_id = a.quote_id " +
                    "   and b.case_id = a.case_id " +
                    "   and b.item_id = a.item_id ";
            pstmtCharge = conn.prepareStatement(sql);

            sql = "select transaction_id, assign_id, -1 * voucher_amount as v_amt " +
                    "  from epc_order_voucher " +
                    " where order_id = ? " +
                    "   and assign_redeem = ? " +
                    "   and apply_level = ? " +
                    "   and status = ? ";
            pstmtVoucherInOrderLevel = conn.prepareStatement(sql);

            sql = "select transaction_id, assign_id, -1 * voucher_amount as v_amt " +
                    "  from epc_order_voucher " +
                    " where order_id = ? " +
                    "   and assign_redeem = ? " +
                    "   and apply_level = ? " +
                    "   and status = ? " +
                    "   and case_id = ? ";
            pstmtVoucherInQuoteItemLevel = conn.prepareStatement(sql);

            sql = "select transaction_id, assign_id, -1 * voucher_amount as v_amt " +
                    "  from epc_order_voucher " +
                    " where order_id = ? " +
                    "   and assign_redeem = ? " +
                    "   and apply_level = ? " +
                    "   and status = ? " +
                    "   and item_id = ? ";
            pstmtVoucherInProductLevel = conn.prepareStatement(sql);

            sql = "select distinct assign_id " +
                    "  from epc_order_voucher " +
                    " where order_id = ? " +
                    "   and assign_redeem = ? ";
            pstmtDistrictRedeemedVoucher = conn.prepareStatement(sql);

            sql = "select transaction_id  " +
                  "  from epc_order_voucher " +
                  " where order_id = ? " +
                  "   and assign_id = ? " +
                  "   and assign_redeem = ? " +
                  "   and status = ? ";
            pstmtVoucherTransaction = conn.prepareStatement(sql);
            // end of prepare sql

            // get quote items
            productDetailList = new ArrayList<>();
            vmsRedeem.setProductDetails(productDetailList);

            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            while (rset.next()) {
                vmsProductDetail = new VmsProductDetail();
                vmsProductDetail.setQuoteId(StringHelper.trim(rset.getString("cpq_quote_guid")));
                vmsProductDetail.setItemId(StringHelper.trim(rset.getString("quote_item_guid")));
                vmsProductDetail.setRootInstanceId(StringHelper.trim(rset.getString("case_id")));
                vmsProductDetail.setRootOfferName(StringHelper.trim(rset.getString("cpq_offer_desc")));
                vmsProductDetail.setRootOfferTemplate(StringHelper.trim(rset.getString("template_name")));

                // v_discounts
                discountListInQuoteItemLevel = new ArrayList<>();
                vmsProductDetail.setV_discounts(discountListInQuoteItemLevel);

                pstmtVoucherInQuoteItemLevel.setInt(1, orderId); // order_id
                pstmtVoucherInQuoteItemLevel.setString(2, VOUCHER_REDEEM); // assign_redeem - REDEEM
                pstmtVoucherInQuoteItemLevel.setString(3, VOUCHER_APPLY_LEVEL_QUOTE_ITEM); // apply_level
                pstmtVoucherInQuoteItemLevel.setString(4, "A"); // status
                pstmtVoucherInQuoteItemLevel.setString(5, StringHelper.trim(rset.getString("case_id"))); // case_id
                rsetVoucherInOtherLevel = pstmtVoucherInQuoteItemLevel.executeQuery();
                while (rsetVoucherInOtherLevel.next()) {
                    vmsDiscount = new VmsDiscount();
                    vmsDiscount.setCouponId(StringHelper.trim(rsetVoucherInOtherLevel.getString("assign_id")));
                    vmsDiscount.setRedeemId(StringHelper.trim(rsetVoucherInOtherLevel.getString("transaction_id")));
                    vmsDiscount.setDiscount(rsetVoucherInOtherLevel.getBigDecimal("v_amt"));
                    vmsDiscount.setValid(true);

                    discountListInQuoteItemLevel.add(vmsDiscount);
                }
                rsetVoucherInOtherLevel.close();
                // end of v_discounts

                // get products
                productList = new ArrayList<>();
                vmsProductDetail.setProducts(productList);

                pstmtItem.setString(1, "CHARGE"); // item_cat
                pstmtItem.setInt(2, orderId); // order_id
                pstmtItem.setInt(3, rset.getInt("quote_id")); // quote_id
                pstmtItem.setString(4, StringHelper.trim(rset.getString("case_id"))); // case_id
                pstmtItem.setString(5, "DEVICE"); // item_cat
                rsetItem = pstmtItem.executeQuery();
                while (rsetItem.next()) {
                    vmsProduct = new VmsProduct();
                    vmsProduct.setInstanceId(StringHelper.trim(rsetItem.getString("item_id")));
                    vmsProduct.setProductCode(StringHelper.trim(rsetItem.getString("item_code")));
                    vmsProduct.setProductTemplate(StringHelper.trim(rsetItem.getString("template_name")));
                    vmsProduct.setModelName(StringHelper.trim(rsetItem.getString("catalog_desc")));

                    // get charges
                    chargeList = new ArrayList<>();
                    vmsProduct.setCharges(chargeList);

                    pstmtCharge.setInt(1, orderId); // order_id
                    pstmtCharge.setInt(2, rset.getInt("quote_id")); // quote_id
                    pstmtCharge.setString(3, StringHelper.trim(rset.getString("case_id"))); // case_id
                    pstmtCharge.setString(4, StringHelper.trim(rsetItem.getString("item_id"))); // parent_item_id
                    rsetCharge = pstmtCharge.executeQuery();
                    while (rsetCharge.next()) {
                        vmsCharge = new VmsCharge();
                        vmsCharge.setChargeInstanceId(StringHelper.trim(rsetCharge.getString("item_id")));
                        vmsCharge.setChargeName(StringHelper.trim(rsetCharge.getString("cpq_item_desc")));
                        vmsCharge.setChargeTemplate(StringHelper.trim(rsetCharge.getString("template_name")));
                        vmsCharge.setChargeValue(rsetCharge.getBigDecimal("charge_amount"));
                        vmsCharge.setRrpPrice(rsetCharge.getBigDecimal("catalog_rrp"));

                        chargeList.add(vmsCharge);
                    }
                    rsetCharge.close();
                    // end of get charges

                    // v_discounts
                    discountListInProductLevel = new ArrayList<>();
                    vmsProduct.setV_discounts(discountListInProductLevel);

                    pstmtVoucherInProductLevel.setInt(1, orderId); // order_id
                    pstmtVoucherInProductLevel.setString(2, VOUCHER_REDEEM); // assign_redeem - REDEEM
                    pstmtVoucherInProductLevel.setString(3, VOUCHER_APPLY_LEVEL_PRODUCT); // apply_level
                    pstmtVoucherInProductLevel.setString(4, "A"); // status
                    pstmtVoucherInProductLevel.setString(5, StringHelper.trim(rsetItem.getString("item_id"))); // item_id
                    rsetVoucherInOtherLevel = pstmtVoucherInProductLevel.executeQuery();
                    while (rsetVoucherInOtherLevel.next()) {
                        vmsDiscount = new VmsDiscount();
                        vmsDiscount.setCouponId(StringHelper.trim(rsetVoucherInOtherLevel.getString("assign_id")));
                        vmsDiscount.setRedeemId(StringHelper.trim(rsetVoucherInOtherLevel.getString("transaction_id")));
                        vmsDiscount.setDiscount(rsetVoucherInOtherLevel.getBigDecimal("v_amt"));
                        vmsDiscount.setValid(true);

                        discountListInProductLevel.add(vmsDiscount);
                    }
                    rsetVoucherInOtherLevel.close();
                    // end of v_discounts

                    productList.add(vmsProduct);
                }
                rsetItem.close();
                // end of get products

                productDetailList.add(vmsProductDetail);
            }
            rset.close();
            // end of get quote items

            // for voucher entity
            voucherList = new ArrayList<>();
            vmsRedeem.setVouchers(voucherList);

            if (REDEEM_ACTION_ADD.equals(actionType)) {
                // redeem action
                voucherRemainingAmount = epcVmsVoucherInfo.getRemainingAmount();

                // orderCaseList
                //  if the voucher is in order level, then there is one entry in orderCaseList (amount is summed in order level)
                //  else orderCaseList contains quote item, sorted by its total amount (amount is summed in quote item level and minus previous voucher amount)
                for (EpcVmsCaseInfoInCart orderCase : orderCaseList) {
                    if (orderCase.isValidToRedeem()) {
                        caseAmount = orderCase.getTotalCharge();
                        if(caseAmount == null) {
                            caseAmount = new BigDecimal(0);
                        }

                        if (VOUCHER_FACE_VALUE_TYPE_FIXED_DISCOUNT.equals(epcVmsVoucherInfo.getFaceValueType())
                            && voucherRemainingAmount.compareTo(new BigDecimal(0)) == 0) { // equal to zero
//logger.info("{}{}", logStr, " voucherRemainingAmount = 0, stop to allocate voucher to quote item !!!");
logSB.append(logStr + " voucherRemainingAmount = 0, stop to allocate voucher to quote item !!!" + "\n");
                            break; // stop allocate voucher to quote item !
                        }

                        if (VOUCHER_FACE_VALUE_TYPE_FIXED_DISCOUNT.equals(epcVmsVoucherInfo.getFaceValueType())
                            && caseAmount.compareTo(new BigDecimal(0)) == 0) { // equal to zero
//logger.info("{}{}", logStr, " caseAmount = 0, stop to allocate voucher to quote item !!!");
logSB.append(logStr + " caseAmount = 0, stop to allocate voucher to quote item !!!" + "\n");
                            break; // stop allocate voucher to quote item !
                        }
                        

                        if(VOUCHER_CATEGORY_ENTITLEMENT.equals(epcVmsVoucherInfo.getCategory())) {
                            // entitlement
                            // no value 
                            tmpAmount = new BigDecimal(0);

                            // kerrytsang, 20231114
                            // according to the OPM requirement, "entitlement" voucher is allocated to 1 offer
                            //  and if the offer is already enjoyed such type of voucher in previous stage, then skip it

                            if(!"".equals(orderCase.getCaseId())) {
                                // check whether other "percentage discount" voucher is already applied to this case
                                //  if so, then skip it
                                isCaseContainCategoryAlready = isCaseContainCategoryAlready(
                                    conn, orderId, orderCase.getCaseId(), epcVmsVoucherInfo.getMasterVoucherId(), VOUCHER_CATEGORY_ENTITLEMENT
                                );

                                tmpLogStr = "caseId:" + orderCase.getCaseId() + 
                                            ",category:" + VOUCHER_CATEGORY_ENTITLEMENT + 
                                            ",isCaseContainCategoryAlready:" + isCaseContainCategoryAlready;
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");

                                if(isCaseContainCategoryAlready) {
                                    tmpLogStr = " SKIP caseId:" + orderCase.getCaseId();
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");

                                    continue;
                                } else {
                                    tmpLogStr = " mark to caseId:" + orderCase.getCaseId();
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");

                                    isBreakAllocationLoop = true;
                                }
                            }
                        } else if (VOUCHER_FACE_VALUE_TYPE_FIXED_DISCOUNT.equals(epcVmsVoucherInfo.getFaceValueType())) {
                            // use discount amount

                            if (VOUCHER_SCOPE_QUOTE_LEVEL.equals(epcVmsVoucherInfo.getScope())) {
                                tmpAmount = voucherRemainingAmount; // use the voucher exact amount
                            } else {
                                if (caseAmount.compareTo(voucherRemainingAmount) > 0) {
                                    // case amount > voucher remaining amount, use voucher remaining amount
                                    tmpAmount = voucherRemainingAmount;
                                } else if (caseAmount.compareTo(epcVmsVoucherInfo.getRemainingAmount()) < 0) {
                                    // case amount < voucher remaining amount, use case amount
                                    tmpAmount = caseAmount;
                                } else {
                                    // case amount = voucher remaining amount
                                    tmpAmount = caseAmount;
                                }
                                
                                // only apply to quote item level
                                // if only voucher is NOT multiple use, only allocate it to a quote item of the order
                                if (!epcVmsVoucherInfo.isMultipleUse()) {
                                    isBreakAllocationLoop = true;
                                }
                                
                            }

                            // reduce amount from voucher remaining amount
                            voucherRemainingAmount = voucherRemainingAmount.subtract(tmpAmount);
                        } else if (VOUCHER_FACE_VALUE_TYPE_FIXED_PRODUCT_PRICE.equals(epcVmsVoucherInfo.getFaceValueType())) {
                            // no value in remaining amount for "fix amount", dynamcially calculated !
                            tmpAmount = null;

                            // only apply to quote item level
                            // if only voucher is NOT multiple use, only allocate it to a quote item of the order
                            if (VOUCHER_SCOPE_QUOTE_LEVEL.equals(epcVmsVoucherInfo.getScope())) {
                                if (epcVmsVoucherInfo.isMultipleUse()) {
                                    isBreakAllocationLoop = true;
                                }
                            }
                        } else if (VOUCHER_FACE_VALUE_TYPE_PERCENTAGE_DISCOUNT.equals(epcVmsVoucherInfo.getFaceValueType())) {
                            // no value in remaining amount for "discount percent", dynamcially calculated !
                            tmpAmount = null;

                            // kerrytsang, 20231013
                            // according to the OPM requirement, "percent discount" voucher is allocated to 1 offer
                            //  and if the offer is already enjoyed such type of voucher in previous stage, then skip it

                            if(!"".equals(orderCase.getCaseId())) {
                                // check whether other "percentage discount" voucher is already applied to this case
                                //  if so, then skip it
                                isCaseContainFaceValueTypeAlready = isCaseContainFaceValueTypeAlready(conn, orderId, orderCase.getCaseId(), VOUCHER_FACE_VALUE_TYPE_PERCENTAGE_DISCOUNT);

                                tmpLogStr = "caseId:" + orderCase.getCaseId() + 
                                            ",faseValueType:" + VOUCHER_FACE_VALUE_TYPE_PERCENTAGE_DISCOUNT + 
                                            ",isCaseContainFaceValueTypeAlready:" + isCaseContainFaceValueTypeAlready;
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");

                                if(isCaseContainFaceValueTypeAlready) {
                                    tmpLogStr = " SKIP caseId:" + orderCase.getCaseId();
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");

                                    continue;
                                } else {
                                    tmpLogStr = " mark to caseId:" + orderCase.getCaseId();
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");

                                    isBreakAllocationLoop = true;
                                }
                            }
                        } else {
                            // no value 
                            tmpAmount = null;
                            
                            // only apply to quote item level
                            // if only voucher is NOT multiple use, only allocate it to a quote item of the order
                            if (VOUCHER_SCOPE_QUOTE_LEVEL.equals(epcVmsVoucherInfo.getScope())) {
                                if (epcVmsVoucherInfo.isMultipleUse()) {
                                    isBreakAllocationLoop = true;
                                }
                            }
                        }

                        vmsVoucher = new VmsVoucher2();
                        vmsVoucher.setCustomerId(custId);
                        vmsVoucher.setQuoteId(orderCase.getQuoteGuid()); // when the voucher is quote/shopping bag
                                                                         // level, use smc orderId instead of quote guid
                        vmsVoucher.setItemId(orderCase.getQuoteItemGuid()); // when the voucher is quote/shopping bag
                                                                            // level, use smc orderId instead of quote
                                                                            // item guid
                        vmsVoucher.setMasterCouponId(epcVmsVoucherInfo.getMasterVoucherId());
                        vmsVoucher.setCouponId(epcVmsVoucherInfo.getCouponId());
                        if ("SERIAL_NO".equals(epcVmsVoucherInfo.getIsFrom())) {
                            vmsVoucher.setSerialNumber(voucherCode);
                        } else {
                            vmsVoucher.setSerialNumber("");
                        }
                        vmsVoucher.setRedeemAmount(tmpAmount);
                        vmsVoucher.setSmcOrderId(orderId + "");
                        vmsVoucher.setSmcOrderReference(orderReference);

                        voucherList.add(vmsVoucher);


                        if(isBreakAllocationLoop) {
                            break;
                        }
                    }
                }
            } else if (REDEEM_ACTION_DELETE.equals(actionType)) {
                // cancel redeem action
                pstmtVoucherTransaction.setInt(1, orderId); // order_id
                pstmtVoucherTransaction.setString(2, epcVmsVoucherInfo.getCouponId()); // assign_id
                pstmtVoucherTransaction.setString(3, VOUCHER_REDEEM); // assign_redeem - REDEEM
                pstmtVoucherTransaction.setString(4, "A"); // status
                rsetVoucherTransaction = pstmtVoucherTransaction.executeQuery();
                while(rsetVoucherTransaction.next()) {
                    vmsVoucher = new VmsVoucher2();
                    vmsVoucher.setTransactionId(StringHelper.trim(rsetVoucherTransaction.getString("transaction_id")));
    
                    voucherList.add(vmsVoucher);
                } rsetVoucherTransaction.close();
            } else if (REDEEM_ACTION_REFRESH_DELETE.equals(actionType)) {
                // refresh action - clear all
                pstmtDistrictRedeemedVoucher.setInt(1, orderId); // order_id
                pstmtDistrictRedeemedVoucher.setString(2, VOUCHER_REDEEM); // assign_redeem - REDEEM
                rset = pstmtDistrictRedeemedVoucher.executeQuery();
                while (rset.next()) {
                    pstmtVoucherTransaction.setInt(1, orderId); // order_id
                    pstmtVoucherTransaction.setString(2, StringHelper.trim(rset.getString("assign_id"))); // assign_id
                    pstmtVoucherTransaction.setString(3, VOUCHER_REDEEM); // assign_redeem - REDEEM
                    pstmtVoucherTransaction.setString(4, "A"); // status
                    rsetVoucherTransaction = pstmtVoucherTransaction.executeQuery();
                    while(rsetVoucherTransaction.next()) {
                        vmsVoucher = new VmsVoucher2();
                        vmsVoucher.setTransactionId(StringHelper.trim(rsetVoucherTransaction.getString("transaction_id")));
        
                        voucherList.add(vmsVoucher);
                    } rsetVoucherTransaction.close();
                }
                rset.close();
            } else {
                // ...
            }
            // end for voucher entity

            // for redeemed vouchers
            redeemedVoucherList = new ArrayList<>();
            vmsRedeem.setRedeemed(redeemedVoucherList);

            redeemedVoucherInEPCDbList = getRedeemedVouchersInOrder(conn, custId, orderId);
            if (redeemedVoucherInEPCDbList != null) {
                for (VmsOrderVoucher vv : redeemedVoucherInEPCDbList) {
                    vmsVoucher = new VmsVoucher2();
                    vmsVoucher.setCustomerId(custId);
                    vmsVoucher.setQuoteId(vv.getQuoteGuid());
                    vmsVoucher.setItemId(vv.getQuoteItemGuid());
                    vmsVoucher.setMasterCouponId(vv.getVoucherMasterId());
                    vmsVoucher.setCouponId(vv.getCouponId());
                    vmsVoucher.setSerialNumber(vv.getVoucherCode());
                    vmsVoucher.setRedeemAmount(vv.getVoucherAmount());
                    vmsVoucher.setTransactionId(vv.getTransactionId());

                    redeemedVoucherList.add(vmsVoucher);
                }
            }
            // end for redeemed vouchers

            // for v_discounts
            discountList = new ArrayList<>();
            vmsRedeem.setV_discounts(discountList);

            pstmtVoucherInOrderLevel.setInt(1, orderId); // order_id
            pstmtVoucherInOrderLevel.setString(2, VOUCHER_REDEEM); // assign_redeem - REDEEM
            pstmtVoucherInOrderLevel.setString(3, VOUCHER_APPLY_LEVEL_ORDER); // apply_level
            pstmtVoucherInOrderLevel.setString(4, "A"); // status
            rset = pstmtVoucherInOrderLevel.executeQuery();
            while (rset.next()) {
                vmsDiscount = new VmsDiscount();
                vmsDiscount.setCouponId(StringHelper.trim(rset.getString("assign_id")));
                vmsDiscount.setRedeemId(StringHelper.trim(rset.getString("transaction_id")));
                vmsDiscount.setDiscount(rset.getBigDecimal("v_amt"));
                vmsDiscount.setValid(true);

                discountList.add(vmsDiscount);
            }
            rset.close();
            // end of for v_discounts
        } catch (Exception e) {
            e.printStackTrace();

            vmsRedeem = null;
        } finally {
            try {
                if (pstmtCharge != null) {
                    pstmtCharge.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (pstmtItem != null) {
                    pstmtItem.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ee) {
            }
        }
        return vmsRedeem;
    }

    public HashMap<String, EpcVmsVoucherInfo> getRedeemedVoucherMap(String custId, int orderId,
            ArrayList<VmsOrderVoucher> existingList) {
        ArrayList<VmsOrderVoucher> vList = null;
        if (existingList == null) {
            vList = getRedeemedVouchersInOrder(custId, orderId);
        } else {
            vList = existingList;
        }

        // HashMap<String, EpcVmsVoucherInfo> vMap = (HashMap<String,
        // EpcVmsVoucherInfo>)vList
        // .stream()
        // .map(x -> extracted(x))
        // .distinct()
        // .collect(Collectors.toMap(x -> x.getMasterVoucherId(), x -> x));
        HashMap<String, EpcVmsVoucherInfo> vMap = new HashMap<>();
        for (VmsOrderVoucher x : vList) {
            EpcVmsVoucherInfo i = new EpcVmsVoucherInfo();
            i.setMasterVoucherId(x.getVoucherMasterId());
            i.setName(x.getName());
            i.setNameZHHK(x.getNameZHHK());
            i.setDescription(x.getDescription());
            i.setDescriptionZHHK(x.getDescriptionZHHK());
            i.setChargeWaiver(x.getChargeWaiver());
            i.setNotModelled(x.getNotModelled());
            if (x.isMobilePlanSubscriptionCoupon()) {
                i.setMobilePlanSubscriptionCoupon("Y");
            } else {
                i.setMobilePlanSubscriptionCoupon("N");
            }
            i.setFaceValueType(x.getFaceValueType());

            vMap.put(i.getMasterVoucherId(), i);
        }

        return vMap;
    }

    // private EpcVmsVoucherInfo extracted(VmsOrderVoucher x) {
    // EpcVmsVoucherInfo i = new EpcVmsVoucherInfo();
    // i.setMasterVoucherId(x.getVoucherMasterId());
    //// i.setCouponId(x.getCouponId());
    // i.setName(x.getName());
    // i.setNameZHHK(x.getNameZHHK());
    // i.setDescription(x.getDescription());
    // i.setDescriptionZHHK(x.getDescriptionZHHK());
    // return i;
    // }

    public ArrayList<VmsOrderVoucher> getRedeemedVouchersInOrder(String custId, int orderId) {
        Connection conn = null;
        ArrayList<VmsOrderVoucher> vList = new ArrayList<>();

        try {
            conn = epcDataSource.getConnection();
            vList = getRedeemedVouchersInOrder(conn, custId, orderId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ee) {
            }
        }
        return vList;
    }

    /***
     * get vouchers redeemed under an order
     * sort by apply_level
     *  PRODUCT -> handle 1st, QUOTE_ITEM -> handle 2nd, finally handle ORDER
     * 
     */
    public ArrayList<VmsOrderVoucher> getRedeemedVouchersInOrder(Connection conn, String custId, int orderId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<VmsOrderVoucher> vList = new ArrayList<>();
        VmsOrderVoucher vmsOrderVoucher = null;
        String iCustId = epcSecurityHelper.encodeForSQL(custId);

        try {
            sql = "select quote_item_guid, item_id, voucher_guid, voucher_master_id, voucher_code, " +
                  "       voucher_amount, assign_id, transaction_id, voucher_name, voucher_name_chi, " +
                  "       voucher_desc, voucher_desc_chi, quote_guid, charge_waiver, not_modelled, " +
                  "       plan_subscription, apply_level, face_value_type, category, " +
                  "       case " +
                  "         when apply_level = 'PRODUCT' then 1 " +
                  "         when apply_level = 'QUOTE_ITEM' then 2 " +
                  "         when apply_level = 'ORDER' then 3 " +
                  "         else 99 " +
                  "       end as order_col " +
                  "  from epc_order_voucher a, epc_order b " +
                  " where a.order_id = ? " +
                  "   and a.assign_redeem = ? " +
                  "   and a.status = ? " +
                  "   and b.order_id = a.order_id " +
                  "   and b.cust_id = ? " +
                  " order by 20 ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, VOUCHER_REDEEM); // assign_redeem - REDEEM
            pstmt.setString(3, "A"); // status
            pstmt.setString(4, iCustId); // cust_id
            rset = pstmt.executeQuery();
            while (rset.next()) {
                vmsOrderVoucher = new VmsOrderVoucher();
                vmsOrderVoucher.setOrderId(orderId);
                vmsOrderVoucher.setQuoteGuid(StringHelper.trim(rset.getString("quote_guid")));
                vmsOrderVoucher.setQuoteItemGuid(StringHelper.trim(rset.getString("quote_item_guid")));
                vmsOrderVoucher.setItemId(StringHelper.trim(rset.getString("item_id")));
                vmsOrderVoucher.setVoucherGuid(StringHelper.trim(rset.getString("voucher_guid")));
                vmsOrderVoucher.setVoucherMasterId(StringHelper.trim(rset.getString("voucher_master_id")));
                vmsOrderVoucher.setVoucherCode(StringHelper.trim(rset.getString("voucher_code")));
                vmsOrderVoucher.setVoucherAmount(rset.getBigDecimal("voucher_amount"));
                vmsOrderVoucher.setCouponId(StringHelper.trim(rset.getString("assign_id")));
                vmsOrderVoucher.setTransactionId(StringHelper.trim(rset.getString("transaction_id")));
                vmsOrderVoucher.setName(StringHelper.trim(rset.getString("voucher_name")));
                vmsOrderVoucher.setNameZHHK(StringHelper.trim(rset.getString("voucher_name_chi")));
                vmsOrderVoucher.setDescription(StringHelper.trim(rset.getString("voucher_desc")));
                vmsOrderVoucher.setDescriptionZHHK(StringHelper.trim(rset.getString("voucher_desc_chi")));
                vmsOrderVoucher.setChargeWaiver(StringHelper.trim(rset.getString("charge_waiver")));
                vmsOrderVoucher.setNotModelled(StringHelper.trim(rset.getString("not_modelled")));
                if ("Y".equals(StringHelper.trim(rset.getString("plan_subscription")))) {
                    vmsOrderVoucher.setMobilePlanSubscriptionCoupon(true);
                } else {
                    vmsOrderVoucher.setMobilePlanSubscriptionCoupon(false);
                }
                vmsOrderVoucher.setApplyLevel(StringHelper.trim(rset.getString("apply_level")));
                vmsOrderVoucher.setFaceValueType(StringHelper.trim(rset.getString("face_value_type")));
                vmsOrderVoucher.setCategory(StringHelper.trim(rset.getString("category")));

                vList.add(vmsOrderVoucher);
            }
            rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ee) {
            }
        }
        return vList;
    }

    /**
     * get all available vouchers per customer, include "NOT YET PAID" voucher (not
     * yet paid but in current order)
     * 
     * @param custId
     * @param orderId - current order id
     */
    public ArrayList<VmsCustVoucher> getCustAvailableVouchers(String custId, int orderId) {
        EpcVmsVoucherListRequest epcVmsVoucherListRequest = null;
        EpcVmsVoucherListRequestDetail epcVmsVoucherListRequestDetail = null;
        EpcVmsVoucherListResponse epcVmsVoucherListResponse = null;
        EpcVmsVoucherListResponseDetail epcVmsVoucherListResponseDetail = null;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_VMS_LINK") + "/VoucherManagementController/rest/voucher/list";
        ArrayList<VmsCustVoucher> voucherList = new ArrayList<>();
        VmsCustVoucher vmsCustVoucher = null;
        String tmpQuoteGuidInVoucher = ""; // voucher assigned by this quote
        String tmpMasterCouponId = "";
        String isVoucherValidToShow = "";
        String logStr = "[getCustVouchers][orderId:" + orderId + "][custId:" + custId + "] ";

        try {
            epcVmsVoucherListRequest = new EpcVmsVoucherListRequest();
            epcVmsVoucherListRequestDetail = new EpcVmsVoucherListRequestDetail();
            epcVmsVoucherListRequest.setVoucherListReq(epcVmsVoucherListRequestDetail);

            epcVmsVoucherListRequestDetail.setCustomerId(custId);
            epcVmsVoucherListRequestDetail.setMasterCouponId("");
            epcVmsVoucherListRequestDetail.setSerialNo("");

            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcVmsVoucherListRequest),
                    String.class);
            if (responseEntity.getStatusCode().is2xxSuccessful()) {
                epcVmsVoucherListResponse = objectMapper.readValue(responseEntity.getBody(),
                        EpcVmsVoucherListResponse.class);
                epcVmsVoucherListResponseDetail = epcVmsVoucherListResponse.getVoucherListRes();
                if (epcVmsVoucherListResponseDetail.getStatusCode() == 0) {
                    for (EpcVmsVoucherListResponseDetailCoupon c : epcVmsVoucherListResponseDetail.getCoupon()) {
                        isVoucherValidToShow = ""; // reset
                        tmpQuoteGuidInVoucher = ""; // reset
                        tmpMasterCouponId = c.getMasterCoupon().getMasterCouponId(); // reset

                        if ("A".equals(StringHelper.trim(c.getStatus()))) {
                            // get assign transaction info
                            for (EpcVmsVoucherListResponseDetailAssignTransaction a : c.getAssignmentTransactions()) {
                                tmpQuoteGuidInVoucher = StringHelper.trim(a.getQuoteId());
                                // check whether
                                // 0. from manual upload or BAU, allow to use
                                // 1. the quote is paid (not cancelled)
                                // 2. equal to current order
                                isVoucherValidToShow = isVoucherValidToShow(orderId, tmpQuoteGuidInVoucher);
                            }
                            // end of get assign transaction info
                            logger.info("{}{}{}{}{}{}{}", logStr, "check ", tmpMasterCouponId, ",quote:",
                                    tmpQuoteGuidInVoucher, " isVoucherValidToShow:", isVoucherValidToShow);

                            if (isVoucherValidToShow.startsWith("VALID")) {
                                vmsCustVoucher = new VmsCustVoucher();
                                vmsCustVoucher.setCustId(custId);
                                vmsCustVoucher.setCustNum(c.getCustomer().getCustomerNo());
                                vmsCustVoucher.setSubrNum(c.getCustomer().getSubscriberNo());
                                vmsCustVoucher.setAccountNum(c.getCustomer().getAccountNo());
                                vmsCustVoucher.setMasterCouponId(c.getMasterCoupon().getMasterCouponId());
                                vmsCustVoucher.setCouponId(c.getCouponId());
                                vmsCustVoucher.setSerialNo(c.getSerialNo());
                                vmsCustVoucher.setRemainingAmount(c.getRemainingAmount());
                                vmsCustVoucher.setStartDate(c.getStartDate());
                                vmsCustVoucher.setEndDate(c.getEndDate());
                                vmsCustVoucher.setName(c.getMasterCoupon().getName());
                                vmsCustVoucher.setNameZHHK(c.getMasterCoupon().getName_ZH_HK());
                                vmsCustVoucher.setDescription(c.getMasterCoupon().getDescription());
                                vmsCustVoucher.setDescriptionZHHK(c.getMasterCoupon().getDescript_ZH_HK());
                                vmsCustVoucher.setMobilePlanSubscriptionCoupon(c.getMasterCoupon().isMobilePlanSubscriptionCoupon());
                                vmsCustVoucher.setCategory(c.getMasterCoupon().getCategory());

                                voucherList.add(vmsCustVoucher);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return voucherList;
    }

    public String isVoucherValidToShow(int orderId, String quoteGuidFromAssignedVoucher) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        ResultSet rset = null;
        ResultSet rset2 = null;
        String sql = "";
        String str = "";

        try {
            conn = epcDataSource.getConnection();

            if ("".equals(quoteGuidFromAssignedVoucher) || orderId == 0) {
                // assigned from manual upload or bau
                str = "VALID";
            } else {
                sql = "select 1 " +
                        "  from epc_order_quote a " +
                        " where order_id = ? " +
                        "   and cpq_quote_guid = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId); // order_id
                pstmt.setString(2, quoteGuidFromAssignedVoucher); // cpq_quote_guid
                rset = pstmt.executeQuery();
                if (rset.next()) {
                    // current order
                    str = "VALID|CURRENT_ORDER";
                } else {
                    sql = "select order_status " +
                            "  from epc_order_quote a, epc_order b " +
                            " where a.cpq_quote_guid = ? " +
                            "   and b.order_id = a.order_id " +
                            "   and a.order_status not in (?,?) ";
                    pstmt2 = conn.prepareStatement(sql);
                    pstmt2.setString(1, quoteGuidFromAssignedVoucher); // cpq_quote_guid
                    pstmt2.setString(2, "I"); // order_status - I: initial, not yet paid
                    pstmt2.setString(3, "CA"); // order_status - CA: cancelled
                    rset2 = pstmt2.executeQuery();
                    if (rset2.next()) {
                        str = "VALID|OTHER_ORDER";
                    } else {
                        str = "INVALID|INVALID_ORDER";
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

            str = "INVALID";
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (rset2 != null) {
                    rset2.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (pstmt2 != null) {
                    pstmt2.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ee) {
            }
        }
        return str;
    }

    /***
     * refresh epc_order_voucher, epc_order_payment
     */
    public boolean refreshEpcRecord(int orderId, VmsRedeem2 vmsRedeemInput, VmsRedeem2 vmsRedeemResult,
            HashMap<String, EpcVmsVoucherInfo> voucherInfoMap, StringBuilder logSB) {
        boolean isCreate = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtGetCurrent = null;
        PreparedStatement pstmtDelete = null;
        ResultSet rset = null;
        String sql = "";
        String caseId = "";
        String itemId = "";
        String voucherGuid = "";
        String voucherMasterId = "";
        String voucherCode = "";
        BigDecimal voucherAmount = null;
        String assignId = "";
        String transactionId = "";
        String quoteGuid = "";
        String quoteItemGuid = "";
        String applyLevel = "";
        EpcVmsVoucherInfo voucherInfo = null;
        String name = "";
        String nameZHHK = "";
        String description = "";
        String descriptionZHHK = "";
        String chargeWaiver = "";
        String notModelled = "";
        String planSubscription = "";
        String faceValueType = "";
        String category = "";
        ArrayList<VmsVoucher2> voucherInputList = vmsRedeemInput.getVouchers();
        ArrayList<VmsVoucher2> voucherResultList = vmsRedeemResult.getVouchers();
        ArrayList<VmsVoucher2> voucherResultRedeemedList = vmsRedeemResult.getRedeemed();
        HashMap<String, String> currentvoucherCodeMap = new HashMap<>(); // used to map back voucher code
        String logStr = "[refreshEpcRecord][orderId:" + orderId + "] ";
        String tmpLogStr = "";

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            sql = "insert into epc_order_voucher ( " +
                    "  order_id, case_id, item_id, assign_redeem, voucher_guid,  " +
                    "  voucher_master_id, voucher_code, voucher_amount, assign_id, transaction_id, " +
                    "  status, remarks, quote_guid, quote_item_guid, create_date, " +
                    "  modify_date, voucher_name, voucher_name_chi, voucher_desc, voucher_desc_chi, " +
                    "  apply_level, charge_waiver, not_modelled, plan_subscription, face_value_type, " +
                    "  category " +
                    ") values ( " +
                    "  ?,?,?,?,?, " +
                    "  ?,?,?,?,?, " +
                    "  ?,?,?,?,sysdate, " +
                    "  sysdate,?,?,?,?, " +
                    "  ?,?,?,?,?, " +
                    "  ? " +
                    ") ";
            pstmt = conn.prepareStatement(sql);

            // use coupon id to search voucher code
            sql = "select assign_id, voucher_code " +
                    "  from epc_order_voucher " +
                    " where order_id = ? ";
            pstmtGetCurrent = conn.prepareStatement(sql);
            pstmtGetCurrent.setInt(1, orderId); // order_id
            rset = pstmtGetCurrent.executeQuery();
            while (rset.next()) {
                currentvoucherCodeMap.put(StringHelper.trim(rset.getString("assign_id")),
                        StringHelper.trim(rset.getString("voucher_code")));
            }
            rset.close();
            // end of use coupon id to search voucher code

//logger.info("{}{}", logStr, "delete all voucher records under this order");
logSB.append(logStr + "delete all voucher records under this order \n");
            sql = "delete from epc_order_voucher " +
                    " where order_id = ? " +
                    "   and assign_redeem = ? ";
            pstmtDelete = conn.prepareStatement(sql);
            pstmtDelete.setInt(1, orderId); // order_id
            pstmtDelete.setString(2, VOUCHER_REDEEM); // assign_redeem - REDEEM
            pstmtDelete.executeUpdate();

//logger.info("{}{}{}", logStr, " voucherInputList size:", voucherInputList.size());
logSB.append(logStr + " voucherInputList size:" + voucherInputList.size() + "\n");
//logger.info("{}{}{}", logStr, " voucherResultList size:", voucherResultList.size());
logSB.append(logStr + " voucherResultList size:" + voucherResultList.size() + "\n");
//logger.info("{}{}{}", logStr, " voucherResultRedeemedList size:", voucherResultRedeemedList.size());
logSB.append(logStr + " voucherResultRedeemedList size:" + voucherResultRedeemedList.size() + "\n");

            for (VmsVoucher2 voucher : voucherResultRedeemedList) {
                quoteGuid = StringHelper.trim(voucher.getQuoteId());
                quoteItemGuid = StringHelper.trim(voucher.getItemId());
                caseId = ""; // reset
                itemId = ""; // not existed due to no upload to CPQ
                voucherGuid = ""; // not existed due to no upload to CPQ
                voucherMasterId = StringHelper.trim(voucher.getMasterCouponId());
                voucherCode = ""; // reset
                voucherAmount = new BigDecimal(0);
                assignId = StringHelper.trim(voucher.getCouponId());
                transactionId = StringHelper.trim(voucher.getTransactionId());
                applyLevel = ""; // reset
                chargeWaiver = ""; // reset
                notModelled = ""; // reset
                planSubscription = ""; // reset
                faceValueType = ""; // reset
                category = ""; // reset

                // determine voucher code
                if (currentvoucherCodeMap.containsKey(assignId)) {
                    // voucher in bag
                    voucherCode = StringHelper.trim(currentvoucherCodeMap.get(assignId));
                } else {
                    // current input voucher
                    for (VmsVoucher2 currentVoucher : voucherResultList) {
                        if (assignId.equals(currentVoucher.getCouponId())) {
                            voucherCode = StringHelper.trim(currentVoucher.getSerialNumber());
                        }
                    }
                }
                // end of determine voucher code

                // determine voucher description, setting
                voucherInfo = voucherInfoMap.get(voucherMasterId);
                if (voucherInfo != null) {
                    name = voucherInfo.getName();
                    nameZHHK = voucherInfo.getNameZHHK();
                    description = voucherInfo.getDescription();
                    descriptionZHHK = voucherInfo.getDescriptionZHHK();
                    chargeWaiver = voucherInfo.getChargeWaiver();
                    notModelled = voucherInfo.getNotModelled();
                    planSubscription = voucherInfo.getMobilePlanSubscriptionCoupon();
                    faceValueType = voucherInfo.getFaceValueType();
                    category = voucherInfo.getCategory();
                } else {
                    name = "";
                    nameZHHK = "";
                    description = "";
                    descriptionZHHK = "";
                    chargeWaiver = "";
                    notModelled = "";
                    planSubscription = "";
                    faceValueType = "";
                    category = "";
                }
                // end of determine voucher description, setting

                // // find transactionId in result list
                // for(VmsVoucher2 voucherResult : voucherResultList) {
                // if(assignId.equals(StringHelper.trim(voucherResult.getCouponId()))
                // && quoteGuid.equals(StringHelper.trim(voucherResult.getQuoteId()))
                // && quoteItemGuid.equals(StringHelper.trim(voucherResult.getItemId()))
                // ) {
                // transactionId = StringHelper.trim(voucherResult.getTransactionId());
                // }
                // }
                // end of find transactionId in result list

                // find apply level
                // order / quote level
                if (vmsRedeemResult.getV_discounts() != null) {
                    for (VmsDiscount vmsDiscount : vmsRedeemResult.getV_discounts()) {
                        if (transactionId.equals(vmsDiscount.getRedeemId())) {
                            applyLevel = VOUCHER_APPLY_LEVEL_ORDER;

                            if (vmsDiscount.getDiscount() != null) {
                                voucherAmount = voucherAmount.add(vmsDiscount.getDiscount());
                            }
                        }
                    }
                }

                // quote item level
                if (vmsRedeemResult.getProductDetails() != null) {
                    for (VmsProductDetail vmsProductDetail : vmsRedeemResult.getProductDetails()) {
                        if (vmsProductDetail.getV_discounts() != null) {
                            for (VmsDiscount vmsDiscount : vmsProductDetail.getV_discounts()) {
                                if (transactionId.equals(vmsDiscount.getRedeemId())) {
                                    applyLevel = VOUCHER_APPLY_LEVEL_QUOTE_ITEM;
                                    caseId = vmsProductDetail.getRootInstanceId();

                                    if (vmsDiscount.getDiscount() != null) {
                                        voucherAmount = voucherAmount.add(vmsDiscount.getDiscount());
                                    }
                                }
                            }
                        }
                    }
                }

                // product level
                if (vmsRedeemResult.getProductDetails() != null) {
                    for (VmsProductDetail vmsProductDetail : vmsRedeemResult.getProductDetails()) {
                        if (vmsProductDetail.getProducts() != null) {
                            for (VmsProduct vmsProduct : vmsProductDetail.getProducts()) {
                                if (vmsProduct.getV_discounts() != null) {
                                    for (VmsDiscount vmsDiscount : vmsProduct.getV_discounts()) {
                                        if (transactionId.equals(vmsDiscount.getRedeemId())) {
                                            applyLevel = VOUCHER_APPLY_LEVEL_PRODUCT;
                                            itemId = vmsProduct.getInstanceId();

                                            if (vmsDiscount.getDiscount() != null) {
                                                voucherAmount = voucherAmount.add(vmsDiscount.getDiscount());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                voucherAmount = voucherAmount.multiply(new BigDecimal(-1));
                // end of find apply level

                tmpLogStr = "create voucherMasterId:" + voucherMasterId + ",voucherCode:" + voucherCode
                        + ",voucherAmount:" + voucherAmount +
                        ",assignId:" + assignId + ",transactionId:" + transactionId + ",applyLevel:" + applyLevel +
                        ",chargeWaiver:" + chargeWaiver + ",notModelled:" + notModelled + ",planSubscription:"
                        + planSubscription +
                        ",quoteGuid:" + quoteGuid + ",quoteItemGuid:" + quoteItemGuid + ",itemId:" + itemId;
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");

                // insert epc_order_voucher
                pstmt.setInt(1, orderId); // order_id
                pstmt.setString(2, caseId); // case_id
                pstmt.setString(3, itemId); // item_id
                pstmt.setString(4, VOUCHER_REDEEM); // assign_redeem - REDEEM
                pstmt.setString(5, voucherGuid); // voucher_guid
                pstmt.setString(6, voucherMasterId); // voucher_master_id
                pstmt.setString(7, voucherCode); // voucher_code
                if (voucherAmount != null) {
                    pstmt.setBigDecimal(8, voucherAmount); // voucher_amount
                } else {
                    pstmt.setNull(8, Types.INTEGER); // voucher_amount
                }
                pstmt.setString(9, assignId); // assign_id
                pstmt.setString(10, transactionId); // transaction_id
                pstmt.setString(11, "A"); // status
                pstmt.setString(12, ""); // remarks
                pstmt.setString(13, quoteGuid); // quote_guid
                pstmt.setString(14, quoteItemGuid); // quote_item_guid
                pstmt.setString(15, name); // voucher_name
                pstmt.setString(16, nameZHHK); // voucher_name_chi
                pstmt.setString(17, description); // voucher_desc
                pstmt.setString(18, descriptionZHHK); // voucher_desc_chi
                pstmt.setString(19, applyLevel); // apply_level
                pstmt.setString(20, chargeWaiver); // charge_waiver
                pstmt.setString(21, notModelled); // not_modelled
                pstmt.setString(22, planSubscription); // plan_subscription
                pstmt.setString(23, faceValueType); // face_value_type
                pstmt.setString(24, category); // category

                pstmt.executeUpdate();
                // end of insert epc_order_voucher
            }
            pstmt.close();

            // refresh epc_order_payment
            sql = "delete from epc_order_payment " +
                    " where order_id = ? " +
                    "   and payment_code = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, "COUP"); // payment_code
            pstmt.executeUpdate();

            sql = "insert into epc_order_payment ( " +
                    "  payment_id, order_id, case_id, payment_code, payment_amount, reference_1, " +
                    "  currency_code, currency_amount, exchange_rate " +
                    ")  " +
                    "  select epc_order_payment_seq.nextval, order_id, case_id, 'COUP', voucher_amount, voucher_master_id, "
                    +
                    "         'HKD', 0, 1 " +
                    "    from epc_order_voucher " +
                    "   where order_id = ? " +
                    "     and assign_redeem = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, VOUCHER_REDEEM); // assign_redeem - REDEEM
            pstmt.executeUpdate();
            // end of refresh epc_order_payment

            conn.commit();

            isCreate = true;
        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ee) {
            }
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (Exception ee) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ee) {
            }
        }
        return isCreate;
    }

    public void redeemVoucherDelete2(EpcRedeemVoucher epcRedeemVoucher, StringBuilder logSB) {
        int orderId = epcRedeemVoucher.getOrderId();
        String loginLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getLoginLocation()));
        String loginChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getLoginChannel()));
        String iCustId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getCustId()));
        String iVoucherCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getVoucherCode()));
        boolean isValid = true;
        StringBuilder errMsgSB = new StringBuilder();
        EpcVmsVoucherInfo epcVmsVoucherInfo = null;
        HashMap<String, EpcVmsVoucherInfo> voucherInfoMap = new HashMap<>();
        EpcVmsEpcRecord epcVmsEpcRecord = null;
        VmsRedeem2 vmsRedeem = null;
        VmsRedeem2 vmsRedeemResult = null;
        boolean isDelete = false;
        String logStr = "[redeemVoucherDelete2][orderId:" + orderId + "] ";
        String tmpLogStr = "";

        try {
            // basic checking

            // get voucher info in epc table
            epcVmsEpcRecord = getVoucherInfoInEpc(orderId, iVoucherCode);
            if (epcVmsEpcRecord.getVoucherList() == null || epcVmsEpcRecord.getVoucherList().size() == 0) {
                tmpLogStr = "voucher code is NOT found";
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");

                isValid = false;
                errMsgSB.append("voucher code is not found / not valid. ");
            } else {
//logger.info("{}{}{}", logStr, "voucher record:", epcVmsEpcRecord.getVoucherList().size());
logSB.append(logStr + "voucher record:" + epcVmsEpcRecord.getVoucherList().size() + "\n");
            }
            // end of get voucher info in epc table

            // end of basic checking

            if (isValid) {
                epcVmsVoucherInfo = epcVmsEpcRecord.getVoucherList().get(0); // only use assign_id

                // prepare input obj for vms call
                vmsRedeem = constructVmsInput(iCustId, orderId, "", null, epcVmsVoucherInfo, iVoucherCode, loginChannel,
                        loginLocation, REDEEM_ACTION_DELETE, logSB);
                if (vmsRedeem == null) {
                    throw new Exception("cannot construct input param to VMS");
                }
                // end of prepare input obj for vms call

                // prepare voucherInfoMap for voucher redeemed under this order
                voucherInfoMap = getRedeemedVoucherMap(iCustId, orderId, null);
                // end of prepare voucherInfoMap for voucher redeemed under this order

                // cancel redeem from VMS
//logger.info("{}{}", logStr, " cancel redeem from VMS");
logSB.append(logStr + " cancel redeem from VMS" + "\n");
                vmsRedeemResult = cancelVmsVoucher(orderId, vmsRedeem, logSB);
                if (vmsRedeemResult.getStatusCode() == 0) {
                    // success
                    // update EPC record
                    isDelete = refreshEpcRecord(orderId, vmsRedeem, vmsRedeemResult, voucherInfoMap, logSB);
//logger.info("{}{}{}", logStr, "isDelete:", isDelete);
logSB.append(logStr + "isDelete:" + isDelete + "\n");
                    if (isDelete) {
                        epcRedeemVoucher.setResult("SUCCESS");
                    } else {
                        // rollback vms call ???

                        epcRedeemVoucher.setResult("FAIL");
                        epcRedeemVoucher.setErrMsg("cannot delete voucher records");
                    }
                } else {
                    // error
                    errMsgSB = new StringBuilder();

                    if (vmsRedeemResult.getVouchers() != null) {
                        // error from vms
                        for (VmsVoucher2 v : vmsRedeemResult.getVouchers()) {
                            if (!v.isValid()) {
                                errMsgSB.append("master coupon id [" + v.getMasterCouponId()
                                        + "] is not valid, reject reason:" + v.getRejectReason());
                            }
                        }
                    } else {
                        // error from generic exception
                        errMsgSB.append(vmsRedeemResult.getStatusDesc());
                    }

                    epcRedeemVoucher.setResult("FAIL");
                    epcRedeemVoucher.setErrMsg(errMsgSB.toString());
                }
                // end of cancel redeem from VMS
            } else {
                epcRedeemVoucher.setResult("FAIL");
                epcRedeemVoucher.setErrMsg(errMsgSB.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcRedeemVoucher.setResult("FAIL");
            epcRedeemVoucher.setErrMsg(e.getMessage());
        }
logSB.append(logStr + "DONE" + "\n");
    }

    /***
     * used in cancel voucher action
     */
    public EpcVmsEpcRecord getVoucherInfoInEpc(int orderId, String voucherCode) {
        EpcVmsEpcRecord epcVmsEpcRecord = new EpcVmsEpcRecord();
        ArrayList<EpcVmsVoucherInfo> voucherList = new ArrayList<>();
        epcVmsEpcRecord.setVoucherList(voucherList);
        EpcVmsVoucherInfo epcVmsVoucherInfo = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            conn = epcDataSource.getConnection();

            sql = "select cust_id, order_reference from epc_order where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if (rset.next()) {
                epcVmsEpcRecord.setCustId(StringHelper.trim(rset.getString("cust_id")));
                epcVmsEpcRecord.setOrderReference(StringHelper.trim(rset.getString("order_reference")));
            }
            rset.close();
            pstmt.close();

            sql = "select cpq_quote_guid from epc_order_quote where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if (rset.next()) {
                epcVmsEpcRecord.setQuoteGuid(StringHelper.trim(rset.getString("cpq_quote_guid")));
            }
            rset.close();
            pstmt.close();

            // kerrytsang, 20230103
            // use assign_id (coupon_id) to perform cancellation
            // so, just need 1 record / obj with assign_id

            // as serial number
            sql = "select item_id, assign_id, transaction_id, quote_guid, quote_item_guid, 'SERIAL_NO' as is_from " +
                    "  from epc_order_voucher " +
                    " where order_id = ? " +
                    "   and voucher_code = ? " +
                    "   and assign_redeem = ? " +
                    "   and status = ? ";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, voucherCode); // voucher_master_id
            pstmt.setString(3, VOUCHER_REDEEM); // assign_redeem - REDEEM
            pstmt.setString(4, "A"); // status

            rset = pstmt.executeQuery();
            if (rset.next()) {
                epcVmsVoucherInfo = new EpcVmsVoucherInfo();
                epcVmsVoucherInfo.setIsFound("Y");

                epcVmsVoucherInfo.setVoucherItemId(StringHelper.trim(rset.getString("item_id")));
                epcVmsVoucherInfo.setVoucherTransactionId(StringHelper.trim(rset.getString("transaction_id")));
                epcVmsVoucherInfo.setIsFrom(StringHelper.trim(rset.getString("is_from")));
                epcVmsVoucherInfo.setCouponId(StringHelper.trim(rset.getString("assign_id")));
                epcVmsVoucherInfo.setQuoteGuid(StringHelper.trim(rset.getString("quote_guid")));
                epcVmsVoucherInfo.setQuoteItemGuid(StringHelper.trim(rset.getString("quote_item_guid")));

                voucherList.add(epcVmsVoucherInfo);
            }
            rset.close();
            pstmt.close();
            // end of as serial number

            // as master coupon id (remove the latest)
            sql = "select item_id, assign_id, transaction_id, quote_guid, quote_item_guid, 'MASTER_VOUCHER_ID' as is_from "
                    +
                    "  from epc_order_voucher a " +
                    " where a.order_id = ? " +
                    "   and a.voucher_master_id = ? " +
                    "   and a.voucher_code is null " +
                    "   and a.assign_redeem = ? " +
                    "   and a.status = ? " +
                    "order by create_date desc ";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, voucherCode); // voucher_master_id
            pstmt.setString(3, VOUCHER_REDEEM); // assign_redeem - REDEEM
            pstmt.setString(4, "A"); // status

            rset = pstmt.executeQuery();
            if (rset.next()) {
                epcVmsVoucherInfo = new EpcVmsVoucherInfo();
                epcVmsVoucherInfo.setIsFound("Y");

                epcVmsVoucherInfo.setVoucherItemId(StringHelper.trim(rset.getString("item_id")));
                epcVmsVoucherInfo.setVoucherTransactionId(StringHelper.trim(rset.getString("transaction_id")));
                epcVmsVoucherInfo.setIsFrom(StringHelper.trim(rset.getString("is_from")));
                epcVmsVoucherInfo.setCouponId(StringHelper.trim(rset.getString("assign_id")));
                epcVmsVoucherInfo.setQuoteGuid(StringHelper.trim(rset.getString("quote_guid")));
                epcVmsVoucherInfo.setQuoteItemGuid(StringHelper.trim(rset.getString("quote_item_guid")));

                voucherList.add(epcVmsVoucherInfo);
            }
            rset.close();
            pstmt.close();
            // end of as master coupon id (remove the latest)
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ee) {
            }
        }

        return epcVmsEpcRecord;
    }

    public VmsRedeem2 cancelVmsVoucher(int orderId, VmsRedeem2 vmsRedeem, StringBuilder logSB) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_VMS_LINK") + "/VoucherManagementController/rest/voucher/cancel";
        VmsRedeem2 vmsRedeemResult = null;
        String logStr = "[cancelVmsVoucher][orderId:" + orderId + "] ";
        String tmpLogStr = "";

        try {
//logger.info("{}{}", logStr, orderId);
logSB.append(logStr + orderId + "\n");
//logger.info("{}{}{}", logStr, "apiUrl:", apiUrl);
logSB.append(logStr + "apiUrl:" + apiUrl + "\n");

            tmpLogStr = "request json:" + objectMapper.writeValueAsString(vmsRedeem);
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");
            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, new HttpEntity<>(vmsRedeem), String.class);

            vmsRedeemResult = objectMapper.readValue(responseEntity.getBody(), VmsRedeem2.class);
//logger.info("{}{}{}", logStr, "statusCode:", vmsRedeemResult.getStatusCode());
logSB.append(logStr + "statusCode:" + vmsRedeemResult.getStatusCode() + "\n");
//logger.info("{}{}{}", logStr, "statusDesc:", epcSecurityHelper.encodeForSQL(StringHelper.trim(vmsRedeemResult.getStatusDesc())));
logSB.append(logStr + "statusDesc:" + epcSecurityHelper.encodeForSQL(StringHelper.trim(vmsRedeemResult.getStatusDesc())) + "\n");
        } catch (HttpStatusCodeException hsce) {
            try {
                vmsRedeemResult = objectMapper.readValue(hsce.getResponseBodyAsString(), VmsRedeem2.class);
            } catch (Exception eee) {
                eee.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();

            vmsRedeemResult = new VmsRedeem2();
            vmsRedeemResult.setStatusCode(1); // 0 - success, others - error
            vmsRedeemResult.setStatusDesc(e.getMessage());
        } finally {
            try {
                tmpLogStr = "return json:" + objectMapper.writeValueAsString(vmsRedeemResult);
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");
            } catch (Exception eee) {
                eee.printStackTrace();
            }
        }
        return vmsRedeemResult;
    }

    public boolean deleteEpcRecord(int orderId, EpcVmsEpcRecord epcVmsEpcRecord) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        boolean isDelete = false;

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            if (epcVmsEpcRecord.getVoucherList() != null) {
                sql = "delete from epc_order_voucher  " +
                        " where order_id = ? " +
                        "   and assign_redeem = ? " +
                        "   and assign_id = ? ";
                pstmt = conn.prepareStatement(sql);

                for (EpcVmsVoucherInfo vmsVoucherInfo : epcVmsEpcRecord.getVoucherList()) {
                    pstmt.setInt(1, orderId); // order_id
                    pstmt.setString(2, VOUCHER_REDEEM); // assign_redeem - REDEEM
                    pstmt.setString(3, vmsVoucherInfo.getCouponId()); // assign_id
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            } else {
                // clear all redeem record(s)
                sql = "delete from epc_order_voucher  " +
                        " where order_id = ? " +
                        "   and assign_redeem = ? ";
                pstmt = conn.prepareStatement(sql);

                pstmt.setInt(1, orderId); // order_id
                pstmt.setString(2, VOUCHER_REDEEM); // assign_redeem - REDEEM
                pstmt.executeUpdate();
            }
            pstmt.close();

            conn.commit();

            isDelete = true;
        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ee) {
            }
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (Exception ee) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ee) {
            }
        }
        return isDelete;
    }

    /***
     * use after delete action (delete quote item)
     */
    public void redeemVoucherRefresh(EpcRedeemVoucher epcRedeemVoucher, StringBuilder logSB) {
        int orderId = epcRedeemVoucher.getOrderId();
        String orderReference = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getOrderReference()));
        String iCustId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getCustId()));
        String loginLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getLoginLocation()));
        String loginChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getLoginChannel()));
        String action = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getAction()));
        String iVoucherCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcRedeemVoucher.getVoucherCode()));
        boolean isValid = true;
        StringBuilder errMsgSB = new StringBuilder();
        VmsRedeem2 vmsRedeem = null;
        VmsRedeem2 vmsRedeemResult = null;
        HashMap<String, EpcVmsVoucherInfo> voucherInfoMap = null;
        EpcVmsVoucherInfo epcVmsVoucherInfo = null;
        String masterVoucherId = "";
        boolean isDelete = false;
        EpcRedeemVoucher epcRedeemVoucherForReAdd = null;
        ArrayList<VmsOrderVoucher> existingList = null; // vouchers redeemed in this order
        ArrayList<VmsOrderVoucher> failVoucherList = new ArrayList<>();
        epcRedeemVoucher.setFailVouchers(failVoucherList);
        String logStr = "[redeemVoucherRefresh][orderId:" + orderId + "] ";
        String tmpLogStr = "";

        try {
            // basic checking
            if(REDEEM_ACTION_ADD.equals(action)) {
                // get voucher info
                epcVmsVoucherInfo = getVoucherInfo(iCustId, iVoucherCode);
                if ("N".equals(epcVmsVoucherInfo.getIsFound())) {
                    tmpLogStr = "master voucher id is NOT found";
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");

                    isValid = false;
                    errMsgSB.append("master voucher id is not found / not valid. ");
                } else {
                    masterVoucherId = epcVmsVoucherInfo.getMasterVoucherId();

                    tmpLogStr = "master voucher id is found [" + masterVoucherId + "], scope:"
                            + epcVmsVoucherInfo.getScope();
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");
                }
                // end of get voucher info
            }
            // end of basic checking


            if (isValid) {
                existingList = getRedeemedVouchersInOrder(iCustId, orderId); // sorted by apply_level
                if(REDEEM_ACTION_ADD.equals(action)) {
                    // add incoming voucher to existing list
                    sortVoucherByApplyLevel(existingList, epcVmsVoucherInfo);
                }

                // prepare voucherInfoMap for voucher redeemed under this order
                voucherInfoMap = getRedeemedVoucherMap(iCustId, orderId, existingList);
                // end of prepare voucherInfoMap for voucher redeemed under this order

                // un-redeem all

                // prepare input obj for vms call
                vmsRedeem = constructVmsInput(iCustId, orderId, "", null, null, "", loginChannel, loginLocation,
                        REDEEM_ACTION_REFRESH_DELETE, logSB);
                if (vmsRedeem == null) {
                    throw new Exception("cannot construct input param to VMS");
                } else {
//logger.info("{}{}", logStr, " cancel all redeemed from VMS");
logSB.append(logStr + " cancel all redeemed from VMS" + "\n");
                    vmsRedeemResult = cancelVmsVoucher(orderId, vmsRedeem, logSB);
                    if (vmsRedeemResult.getStatusCode() == 0) {
                        // success
                        // update EPC record
                        isDelete = refreshEpcRecord(orderId, vmsRedeem, vmsRedeemResult, voucherInfoMap, logSB);
//logger.info("{}{}{}", logStr, "isDelete (clear all redeem records):", isDelete);
logSB.append(logStr + "isDelete (clear all redeem records):" + isDelete + "\n");
                        if (isDelete) {
                            // ...
                        } else {
                            // rollback vms call ???
                            // ...
                        }
                    } else {
                        // error
                        throw new Exception("cannot delete all vouchers");
                    }
                }
                // end of un-redeem all

                // redeem all again according to current shopping bag
//logger.info("{}{}", logStr, " re-apply voucher(s)");
logSB.append(logStr + " re-apply voucher(s)" + "\n");
                for (VmsOrderVoucher v : existingList) {
                    tmpLogStr = "  re-apply masterCouponId:" + v.getVoucherMasterId() + ",voucherCode:" + v.getVoucherCode() + ",couponId:" + v.getCouponId();
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");
                    epcRedeemVoucherForReAdd = new EpcRedeemVoucher();
                    epcRedeemVoucherForReAdd.setCustId(iCustId);
                    epcRedeemVoucherForReAdd.setOrderId(orderId);
                    epcRedeemVoucherForReAdd.setOrderReference(orderReference);
                    if (!"".equals(v.getVoucherCode())) {
                        epcRedeemVoucherForReAdd.setVoucherCode(v.getVoucherCode());
                    } else {
                        epcRedeemVoucherForReAdd.setVoucherCode(v.getVoucherMasterId());
                    }
                    epcRedeemVoucherForReAdd.setLoginLocation(loginLocation);
                    epcRedeemVoucherForReAdd.setLoginChannel(loginChannel);

                    redeemVoucherAdd2(epcRedeemVoucherForReAdd, logSB);
                    tmpLogStr = "  masterCouponId:" + v.getVoucherMasterId() + ",couponId:" + v.getCouponId()
                            + ",result:" + epcRedeemVoucherForReAdd.getResult() + ",errMsg:"
                            + epcRedeemVoucherForReAdd.getErrMsg();
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");
                    if (!"SUCCESS".equals(epcRedeemVoucherForReAdd.getResult())) {
                        // return to caller !
                        if (epcRedeemVoucherForReAdd.getFailVouchers() != null) {
                            for (VmsOrderVoucher f : epcRedeemVoucherForReAdd.getFailVouchers()) {
                                failVoucherList.add(f);
                            }
                        }
                    }
                }
                // end of redeem all again according to current shopping bag

                epcRedeemVoucher.setResult("SUCCESS");
            } else {
                epcRedeemVoucher.setResult("FAIL");
                epcRedeemVoucher.setErrMsg(errMsgSB.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcRedeemVoucher.setResult("FAIL");
            epcRedeemVoucher.setErrMsg(e.getMessage());
        } finally {
        }
logSB.append(logStr + "Done" + "\n");
    }


    /**
     * 
     * @param existingVoucherList - vouchers in order
     * @param epcVmsVoucherInfo - incoming voucher
     */
    public void sortVoucherByApplyLevel(ArrayList<VmsOrderVoucher> existingVoucherList, EpcVmsVoucherInfo epcVmsVoucherInfo) {
        VmsOrderVoucher newVoucher = new VmsOrderVoucher();
        newVoucher.setVoucherMasterId(StringHelper.trim(epcVmsVoucherInfo.getMasterVoucherId()));
        newVoucher.setVoucherCode(StringHelper.trim(epcVmsVoucherInfo.getSerialNo()));
        newVoucher.setCouponId(epcVmsVoucherInfo.getCouponId());
        if(VOUCHER_SCOPE_QUOTE_LEVEL.equals(epcVmsVoucherInfo.getScope())) {
            newVoucher.setApplyLevel(VOUCHER_APPLY_LEVEL_ORDER);
        } else {
            newVoucher.setApplyLevel(VOUCHER_APPLY_LEVEL_QUOTE_ITEM);
        }
        existingVoucherList.add(newVoucher);

        existingVoucherList.sort(Comparator.comparing(VmsOrderVoucher::getApplyLevel).reversed());
    }


    // get voucher records which have not yet assigned (epc_order_voucher.assign_id
    // = null)
    public ArrayList<VmsAssignVoucher> getVoucherForAutoAssign(int orderId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        ArrayList<VmsAssignVoucher> voucherList = new ArrayList<>();
        VmsAssignVoucher vmsAssignVoucher = null;

        try {
            conn = epcDataSource.getConnection();

            sql = "select a.case_id, a.item_id, a.voucher_guid, a.voucher_master_id, a.assign_id, " +
                    "       a.voucher_code, a.transaction_id, c.cpq_quote_guid, d.cust_id " +
                    "  from epc_order_voucher a, epc_order_item b, epc_order_quote c, epc_order d " +
                    " where a.order_id = ? " +
                    "   and a.assign_redeem = ? " +
                    "   and a.assign_id is null " +
                    "   and b.order_id = a.order_id " +
                    "   and b.item_id = a.item_id " +
                    "   and c.order_id = b.order_id " +
                    "   and c.quote_id = b.quote_id " +
                    "   and d.order_id = a.order_id ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, VOUCHER_ASSIGN); // assign_redeem - ASSIGN
            rset = pstmt.executeQuery();
            while (rset.next()) {
                vmsAssignVoucher = new VmsAssignVoucher();
                vmsAssignVoucher.setOrderId(orderId);
                vmsAssignVoucher.setCaseId(StringHelper.trim(rset.getString("case_id")));
                vmsAssignVoucher.setItemId(StringHelper.trim(rset.getString("item_id")));
                vmsAssignVoucher.setVoucherGuid(StringHelper.trim(rset.getString("voucher_guid")));
                vmsAssignVoucher.setVoucherMasterId(StringHelper.trim(rset.getString("voucher_master_id")));
                vmsAssignVoucher.setAssignId(StringHelper.trim(rset.getString("assign_id")));
                vmsAssignVoucher.setVoucherCode(StringHelper.trim(rset.getString("voucher_code")));
                vmsAssignVoucher.setTransactionId(StringHelper.trim(rset.getString("transaction_id")));
                vmsAssignVoucher.setDone(false);
                vmsAssignVoucher.setQuoteGuid(StringHelper.trim(rset.getString("cpq_quote_guid")));
                vmsAssignVoucher.setCustId(StringHelper.trim(rset.getString("cust_id")));
                vmsAssignVoucher.setDesc(""); // init
                vmsAssignVoucher.setDescChi(""); // init

                voucherList.add(vmsAssignVoucher);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rset != null) {
                    rset.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ee) {
            }
        }
        return voucherList;
    }

    public VmsAutoAssign autoAssign(VmsAutoAssign vmsAutoAssign) {
        ArrayList<VmsVoucher2> failVouchers = new ArrayList<>();
        vmsAutoAssign.setFailVouchers(failVouchers);
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt1 = null;
        String sql = "";
        int orderId = vmsAutoAssign.getOrderId();
        String iCustId = epcSecurityHelper.encodeForSQL(StringHelper.trim(vmsAutoAssign.getCustId()));
        String instanceId = ""; // entity instance id
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_VMS_LINK")
                + "/VoucherManagementController/rest/voucher/assignCPQVoucher";
        VmsAssign2 vmsAssign = null;
        VmsAssign2 vmsAssignResult = null;
        ArrayList<VmsVoucher2> voucherList = null;
        VmsVoucher2 vmsVoucher = null;
        ArrayList<VmsAssignVoucher> assignList = null;
        String voucherDesc = "";
        String voucherDescChi = "";
        boolean isValid = true;
        StringBuilder errMsgSB = new StringBuilder();
        String logStr = "[autoAssign][orderId:" + orderId + "] ";
        String tmpLogStr = "";
        String lockAction = "";

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);


            // basic checking
            if(epcOrderHandler.isOrderLocked(iCustId, orderId)) {
                errMsgSB.append("input order [" + orderId + "] is locked. ");
                isValid = false;
            }
            // end of basic checking
            
            // check action lock
            lockAction = epcOrderAttrHandler.getAttrValue(orderId, "", "", epcOrderAttrHandler.ATTR_TYPE_LOCK_VOUCHER);
            if("Y".equals(lockAction)) {
                // locked by other process, exit !
                //  to prevent concurrent calls
                errMsgSB.append("locked by other process / request");
                isValid = false;
            } 


            if(isValid) {
                
                // make a action lock
                epcOrderAttrHandler.addAttr(orderId, "", "", epcOrderAttrHandler.ATTR_TYPE_LOCK_VOUCHER, "Y");
                
                // extend assigned voucher 
                sql = "update epc_order_voucher " +
                    "   set modify_date = sysdate " +
                    " where order_id = ? " +
                    "   and assign_redeem = ? " +
                    "   and assign_id is not null ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId); // order_id
                pstmt.setString(2, VOUCHER_ASSIGN); // assign_redeem
                pstmt.executeUpdate();
                // end of extend assigned voucher 


                sql = "update epc_order_voucher " +
                        "   set assign_id = ?, " +
                        "       transaction_id = ?, " +
                        "       voucher_code = ?, " +
                        "       modify_date = sysdate " +
                        " where order_id = ? " +
                        "   and item_id = ? " +
                        "   and voucher_master_id = ? ";
                pstmt1 = conn.prepareStatement(sql);

                // get voucher record(s) (epc_order_voucher), came from PUT quote item action
                // !!!
                assignList = getVoucherForAutoAssign(orderId);

                // get voucher desc / descChi
                if (assignList.size() > 0) {
logger.info("{}{}", logStr, "get voucher info");
                    getVoucherSetting(assignList);
                } else {
logger.info("{}{}", logStr, "NO need to get voucher info");
                }
                // end of get voucher desc / descChi


                vmsAssign = new VmsAssign2();

                voucherList = new ArrayList<>();
                vmsAssign.setVouchers(voucherList);

                for (VmsAssignVoucher voucher : assignList) {
                    logger.info("{}{}{}", logStr, "procceed ", voucher.getVoucherMasterId());
                    vmsVoucher = new VmsVoucher2();
                    vmsVoucher.setCustomerId(voucher.getCustId());
                    vmsVoucher.setMasterCouponId(voucher.getVoucherMasterId());
                    voucherList.add(vmsVoucher);
                }

                tmpLogStr = objectMapper.writeValueAsString(vmsAssign);
                logger.info("{}{}{}", logStr, "request json:", tmpLogStr);

                if (voucherList.size() > 0) { // new or not yet proceeded record(s)
                    logger.info("{}{}", logStr, "call vms assign api");
                    responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, new HttpEntity<>(vmsAssign),
                            String.class);
                    vmsAssignResult = objectMapper.readValue(responseEntity.getBody(), VmsAssign2.class);

                    tmpLogStr = objectMapper.writeValueAsString(vmsAssignResult);
                    logger.info("{}{}{}", logStr, "response json:", tmpLogStr);

                    if (vmsAssignResult.getStatusCode() == 0) {
                        for (VmsVoucher2 v : vmsAssignResult.getVouchers()) {
                            if (v.isValid()) {
                                instanceId = ""; // reset

                                // find the item id from assign voucher list
                                for (VmsAssignVoucher voucher : assignList) {
                                    if (v.getMasterCouponId().equals(voucher.getVoucherMasterId()) && !voucher.isDone()) {
                                        instanceId = voucher.getItemId();
                                        voucher.setDone(true);
                                        break;
                                    }
                                }
                                // end of find the item id from assign voucher list

                                // save coupon id / serial no / transaction id into epc voucher record
                                pstmt1.setString(1, v.getCouponId()); // assign_id
                                pstmt1.setString(2, v.getTransactionId()); // transaction_id
                                pstmt1.setString(3, v.getSerialNumber()); // voucher_code
                                pstmt1.setInt(4, orderId); // order_id
                                pstmt1.setString(5, instanceId); // item_id - voucher entity instance id
                                pstmt1.setString(6, v.getMasterCouponId()); // voucher_master_id
                                pstmt1.executeUpdate();
                            }
                        }

                        conn.commit();

                        vmsAutoAssign.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
                    } else {
                        // error
                        vmsAutoAssign.setResult(EpcApiStatusReturn.RETURN_FAIL);
                        vmsAutoAssign.setErrMsg(vmsAssignResult.getStatusDesc());
                    }
                } else {
                    logger.info("{}{}", logStr, "No voucher record to be assigned !!!");
                    vmsAutoAssign.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
                }
            } else {
                vmsAutoAssign.setResult(EpcApiStatusReturn.RETURN_FAIL);
                vmsAutoAssign.setErrMsg(errMsgSB.toString());
            }
        } catch (HttpStatusCodeException hsce) {
            try {
                vmsAssignResult = objectMapper.readValue(hsce.getResponseBodyAsString(), VmsAssign2.class);
                tmpLogStr = objectMapper.writeValueAsString(vmsAssignResult);
                logger.info("{}{}{}", logStr, "response json (error):", tmpLogStr);

                if (vmsAssignResult.getVouchers() != null) {
                    for (VmsVoucher2 v : vmsAssignResult.getVouchers()) {
                        instanceId = ""; // reset

                        if(v.isValid()) {
                            // not to include valid voucher
                            continue;
                        }

                        // find the item id from assign voucher list
                        if (assignList != null) {
                            for (VmsAssignVoucher voucher : assignList) {
                                if (v.getMasterCouponId().equals(voucher.getVoucherMasterId()) && !voucher.isDone()) {
                                    instanceId = voucher.getItemId();
                                    voucherDesc = voucher.getDesc();
                                    voucherDescChi = voucher.getDescChi();

                                    voucher.setDone(true);
                                    break;
                                }
                            }
                        }
                        // end of find the item id from assign voucher list

                        v.setInstanceId(instanceId);
                        v.setDesc(voucherDesc);
                        v.setDescChi(voucherDescChi);

                        failVouchers.add(v);
                    }
                }
            } catch (Exception eee) {
                eee.printStackTrace();
            }

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ee) {
            }

            vmsAutoAssign.setResult(EpcApiStatusReturn.RETURN_FAIL);
            vmsAutoAssign.setErrMsg("assign fail");
        } catch (Exception e) {
            e.printStackTrace();

            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (Exception ee) {
            }

            vmsAutoAssign.setResult(EpcApiStatusReturn.RETURN_FAIL);
            vmsAutoAssign.setErrMsg(e.getMessage());
        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (pstmt1 != null) {
                    pstmt1.close();
                }
            } catch (Exception ee) {
            }
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                }
            } catch (Exception ee) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (Exception ee) {
            }
            // free action lock
            epcOrderAttrHandler.obsoleteAttr(orderId, "", "", epcOrderAttrHandler.ATTR_TYPE_LOCK_VOUCHER);
        }
        return vmsAutoAssign;
    }


    public void getVoucherSetting(ArrayList<VmsAssignVoucher> assignList) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = EpcProperty.getValue("EPC_VMS_LINK") + "/VoucherManagementController/rest/voucher/information";
        VmsVoucherInfoRequest vmsVoucherInfoRequest = new VmsVoucherInfoRequest();
        VoucherInformationReq voucherInformationReq = new VoucherInformationReq();
        vmsVoucherInfoRequest.setVoucherInformationReq(voucherInformationReq);
        ArrayList<VoucherInformationReqCoupon> couponList = new ArrayList<>();
        voucherInformationReq.setCoupon(couponList);
        for(VmsAssignVoucher v : assignList) {
            couponList.add(new VoucherInformationReqCoupon(v.getVoucherMasterId()));
        }
        VoucherInformationResponse voucherInformationResponse = null;
        VoucherInformationListRes voucherInformationListRes = null;
        List<VmsVoucherInformation> returnVoucherList = null;

        try {
            voucherInformationResponse = restTemplate.postForObject(apiUrl, new HttpEntity<>(vmsVoucherInfoRequest), VoucherInformationResponse.class);
           if(voucherInformationResponse!=null)
            voucherInformationListRes = voucherInformationResponse.getVoucherInformationListRes();
           if(voucherInformationListRes!=null)
            returnVoucherList = voucherInformationListRes.getVoucherInformation();

            if(returnVoucherList != null) {
                for(VmsAssignVoucher v : assignList) {
                    for(VmsVoucherInformation rV : returnVoucherList) {
                        if(v.getVoucherMasterId().equals(rV.getMasterCouponId())) {
                            v.setDesc(rV.getNameEng());
                            v.setDescChi(rV.getNameChi());
                            break;
                        }
                    }
                }
            }
        } catch (HttpStatusCodeException hsce) {
            hsce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // another method to get Master Coupon information
    public List<VmsVoucherInformation> getVoucherSetting(HashMap<String, String>masterCouponIdMap) {
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = EpcProperty.getValue("EPC_VMS_LINK") + "/VoucherManagementController/rest/voucher/information";
        VmsVoucherInfoRequest vmsVoucherInfoRequest = new VmsVoucherInfoRequest();
        VoucherInformationReq voucherInformationReq = new VoucherInformationReq();
        vmsVoucherInfoRequest.setVoucherInformationReq(voucherInformationReq);
        ArrayList<VoucherInformationReqCoupon> masterCouponList = new ArrayList<VoucherInformationReqCoupon>();
        voucherInformationReq.setCoupon(masterCouponList);
        for (String masterCouponId : masterCouponIdMap.keySet()) {
            masterCouponList.add(new VoucherInformationReqCoupon(masterCouponId));
        }
        VoucherInformationResponse voucherInformationResponse = null;
        VoucherInformationListRes voucherInformationListRes = null;
        List<VmsVoucherInformation> returnMasterCouponInfoList = new ArrayList<VmsVoucherInformation>();

        try {
            voucherInformationResponse = restTemplate.postForObject(apiUrl, new HttpEntity<>(vmsVoucherInfoRequest), VoucherInformationResponse.class);
            if(voucherInformationResponse!=null)
                voucherInformationListRes = voucherInformationResponse.getVoucherInformationListRes();
            if(voucherInformationListRes!=null)
                returnMasterCouponInfoList = voucherInformationListRes.getVoucherInformation();

        } catch (HttpStatusCodeException hsce) {
            hsce.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnMasterCouponInfoList;
    }

    public BigDecimal getVoucherAmountInCaseLevel(Connection conn, int orderId, String caseId) {
        BigDecimal vAmount = new BigDecimal(0);
        PreparedStatement pstmtVoucherInCaseLevel = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select nvl(sum(voucher_amount),0) " +
                    "  from epc_order_voucher " +
                    " where order_id = ? " +
                    "   and case_id = ? " +
                    "   and apply_level = ? " +
                    "   and assign_redeem = ? ";
            pstmtVoucherInCaseLevel = conn.prepareStatement(sql);
            pstmtVoucherInCaseLevel.setInt(1, orderId); // order_id
            pstmtVoucherInCaseLevel.setString(2, caseId); // case_id
            pstmtVoucherInCaseLevel.setString(3, VOUCHER_APPLY_LEVEL_QUOTE_ITEM); // apply_level - QUOTE_ITEM
            pstmtVoucherInCaseLevel.setString(4, VOUCHER_REDEEM); // assign_redeem - REDEEM
            rset = pstmtVoucherInCaseLevel.executeQuery();
            if (rset.next()) {
                vAmount = rset.getBigDecimal(1);
            }
            rset.close();
            pstmtVoucherInCaseLevel.close();

            if (vAmount == null) {
                vAmount = new BigDecimal(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vAmount;
    }

    public BigDecimal getVoucherAmountInProductLevel(Connection conn, int orderId, String itemId) {
        BigDecimal vAmount = new BigDecimal(0);
        PreparedStatement pstmtVoucherInCaseLevel = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select nvl(sum(voucher_amount),0) " +
                    "  from epc_order_voucher " +
                    " where order_id = ? " +
                    "   and item_id = ? " +
                    "   and apply_level = ? " +
                    "   and assign_redeem = ? ";
            pstmtVoucherInCaseLevel = conn.prepareStatement(sql);
            pstmtVoucherInCaseLevel.setInt(1, orderId); // order_id
            pstmtVoucherInCaseLevel.setString(2, itemId); // item_id
            pstmtVoucherInCaseLevel.setString(3, VOUCHER_APPLY_LEVEL_PRODUCT); // apply_level - PRODUCT
            pstmtVoucherInCaseLevel.setString(4, VOUCHER_REDEEM); // assign_redeem - REDEEM
            rset = pstmtVoucherInCaseLevel.executeQuery();
            if (rset.next()) {
                vAmount = rset.getBigDecimal(1);
            }
            rset.close();
            pstmtVoucherInCaseLevel.close();

            if (vAmount == null) {
                vAmount = new BigDecimal(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vAmount;
    }

    public BigDecimal getVoucherAmountInOrderLevel(Connection conn, int orderId) {
        BigDecimal vAmount = new BigDecimal(0);
        PreparedStatement pstmtVoucherInCaseLevel = null;
        ResultSet rset = null;
        String sql = "";

        try {
            sql = "select nvl(sum(voucher_amount),0) " +
                    "  from epc_order_voucher " +
                    " where order_id = ? " +
                    "   and apply_level = ? " +
                    "   and assign_redeem = ? ";
            pstmtVoucherInCaseLevel = conn.prepareStatement(sql);
            pstmtVoucherInCaseLevel.setInt(1, orderId); // order_id
            pstmtVoucherInCaseLevel.setString(2, VOUCHER_APPLY_LEVEL_ORDER); // apply_level - ORDER
            pstmtVoucherInCaseLevel.setString(3, VOUCHER_REDEEM); // assign_redeem - REDEEM
            rset = pstmtVoucherInCaseLevel.executeQuery();
            if (rset.next()) {
                vAmount = rset.getBigDecimal(1);
            }
            rset.close();
            pstmtVoucherInCaseLevel.close();

            if (vAmount == null) {
                vAmount = new BigDecimal(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vAmount;
    }

    public BigDecimal getVoucherAmount(Integer orderId) {
        BigDecimal vAmount = BigDecimal.ZERO;
        String sql = "select nvl(sum(voucher_amount),0) " +
                "  from epc_order_voucher " +
                " where order_id = ? " +
                " and status='A' and assign_redeem = ? ";
        try(Connection conn=epcDataSource.getConnection()){
        try (PreparedStatement pstmtVoucher = conn.prepareStatement(sql)){
        	pstmtVoucher.setInt(1, orderId); // order_id
        	pstmtVoucher.setString(2, VOUCHER_REDEEM); // assign_redeem - REDEEM
            try(ResultSet rset = pstmtVoucher.executeQuery()){
            if (rset.next()) {
                vAmount = rset.getBigDecimal(1);
            }
            }
        }
            if (vAmount == null) {
                vAmount = BigDecimal.ZERO;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vAmount;
    }
    public boolean removeAssignVoucher(Connection conn, int orderId, String caseId) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String voucherMasterId = "";
        String transactionId = "";
        String iCaseId = epcSecurityHelper.encodeForSQL(StringHelper.trim(caseId));
        String logStr = "[removeAssignVoucher][orderId:" + orderId + "][caseId:" + iCaseId + "] ";
        String tmpLogStr = "";
        VmsRedeem2 vmsRedeem = null;
        ArrayList<VmsVoucher2> vouchers = null;
        VmsVoucher2 vmsVoucher = null;
        boolean isRemove = false;
        StringBuilder logSB = new StringBuilder(2048); // 2048 bytes
logSB.append(logStr + "start" + "\n");

        try {
            sql = "select * " +
                    "  from epc_order_voucher " +
                    " where order_id = ? " +
                    "   and case_id = ? " +
                    "   and assign_redeem = ? " +
                    "   and transaction_id is not null ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, iCaseId); // case_id
            pstmt.setString(3, VOUCHER_ASSIGN); // assign_redeem - ASSIGN
            rset = pstmt.executeQuery();
            while (rset.next()) {
                voucherMasterId = StringHelper.trim(rset.getString("voucher_master_id"));
                transactionId = StringHelper.trim(rset.getString("transaction_id"));

                tmpLogStr = "remove voucherMasterId:" + voucherMasterId + ",transactionId:" + transactionId
                        + " from VMS";
//logger.info("{}{}", logStr, tmpLogStr);
logSB.append(logStr + tmpLogStr + "\n");

                vmsRedeem = new VmsRedeem2();
                vmsRedeem.setForceRemove(true);
                vmsRedeem.setAssignCancel(true);
                vmsRedeem.setRemoveTransactionLog(true);

                vouchers = new ArrayList<>();
                vmsRedeem.setVouchers(vouchers);

                vmsVoucher = new VmsVoucher2();
                vmsVoucher.setTransactionId(transactionId);
                vouchers.add(vmsVoucher);

                cancelVmsVoucher(orderId, vmsRedeem, logSB);
            }
            rset.close();

            sql = "delete from epc_order_voucher " +
                    " where order_id = ? " +
                    "   and case_id = ? " +
                    "   and assign_redeem = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, iCaseId); // case_id
            pstmt.setString(3, VOUCHER_ASSIGN); // assign_redeem - ASSIGN
            pstmt.executeUpdate();

            isRemove = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
logSB.append(logStr + "Done" + "\n");

        tmpLogStr = epcSecurityHelper.encodeForSQL(logSB.toString());
logger.info("{}", tmpLogStr);

        return isRemove;
    }


    /***
     * remove non-paid assigned voucher (include all voucher types)
     *  triggered by cronjob
     * just revert the assignment, won't delete epc_order_voucher records
     */
    public ArrayList<AutoRemoveAssignedVoucher> removeNonPaidAssignedVoucher() {
        ArrayList<AutoRemoveAssignedVoucher> aList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtUpdate = null;
        ResultSet rset = null;
        String sql = "";
        AutoRemoveAssignedVoucher autoRemoveAssignedVoucher = null;
        VmsRedeem2 vmsRedeem = null;
        ArrayList<VmsVoucher2> vouchers = null;
        VmsVoucher2 vmsVoucher = null;
        StringBuilder logSB = null;

        try {
            conn = epcDataSource.getConnection();

            sql = "update epc_order_voucher " +
                  "   set assign_id = null, transaction_id = null, voucher_code = null " +
                  " where order_id = ? " +
                  "   and assign_id = ? " +
                  "   and transaction_id = ? ";
            pstmtUpdate = conn.prepareStatement(sql);

            sql = "select a.order_id, a.case_id, a.item_id, a.voucher_master_id, a.voucher_code, " +
                  "       a.assign_id, a.transaction_id, to_char(a.create_date, 'yyyymmddhh24mi') as vc_date, b.order_reference, b.order_status, " +
                  "       ((sysdate - a.modify_date)*1440) idle_mins " +
                  "  from epc_order_voucher a, epc_order b, epc_order_attr c, epc_order_attr d, epc_stock_reserve_ctrl e " +
                  " where a.assign_redeem = ? " +
                  "   and ((sysdate - a.modify_date)*1440) > e.reserve_mins " +
                  "   and assign_id is not null " +
                  "   and b.order_id = a.order_id " +
                  "   and b.order_status in (?) " +
                  "   and c.order_id = a.order_id " +
                  "   and c.attr_type = ? " +
                  "   and c.attr_value = ? " +
                  "   and c.status = ? " +
                  "   and d.order_id = a.order_id " +
                  "   and d.attr_type = ? " +
                  "   and d.status = ? " +
                  "   and e.channel = d.attr_value " + 
                  "   and e.product_code is null " +
                  "   and e.status = ? " +
                  " order by a.create_date ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, VOUCHER_ASSIGN); // assign_redeem - ASSIGN
            pstmt.setString(2, "I"); // order_status - I 
            pstmt.setString(3, "ORDER_TYPE"); // attr_type - ORDER_TYPE 
            pstmt.setString(4, "CHECKOUT"); // attr_value - CHECKOUT 
            pstmt.setString(5, "A"); // status - A

            pstmt.setString(6, "STOCK_RESERVE_CURRENT_CHANNEL"); // attr_type - A
            pstmt.setString(7, "A"); // status - A
            pstmt.setString(8, "A"); // status - A

            rset = pstmt.executeQuery();
            while(rset.next()) {
                // init
                logSB = new StringBuilder(2048); // 2048 bytes
                logSB.append(new java.util.Date() + " start");
                logSB.append(new java.util.Date() + "  idle time:" + rset.getBigDecimal("idle_mins"));

                autoRemoveAssignedVoucher = new AutoRemoveAssignedVoucher();
                autoRemoveAssignedVoucher.setOrderId(rset.getInt("order_id"));
                autoRemoveAssignedVoucher.setCaseId(StringHelper.trim(rset.getString("case_id")));
                autoRemoveAssignedVoucher.setItemId(StringHelper.trim(rset.getString("item_id")));
                autoRemoveAssignedVoucher.setVoucherMasterId(StringHelper.trim(rset.getString("voucher_master_id")));
                autoRemoveAssignedVoucher.setAssignId(StringHelper.trim(rset.getString("assign_id")));
                autoRemoveAssignedVoucher.setTransactionId(StringHelper.trim(rset.getString("transaction_id")));
                autoRemoveAssignedVoucher.setVoucherCreateDate(StringHelper.trim(rset.getString("vc_date")));
                autoRemoveAssignedVoucher.setOrderReference(StringHelper.trim(rset.getString("order_reference")));
                autoRemoveAssignedVoucher.setOrderStatus(StringHelper.trim(rset.getString("order_status")));

                // release assigned voucher
                vmsRedeem = new VmsRedeem2();
                vmsRedeem.setForceRemove(true);
                vmsRedeem.setAssignCancel(true);
                vmsRedeem.setRemoveTransactionLog(true);

                vouchers = new ArrayList<>();
                vmsRedeem.setVouchers(vouchers);

                vmsVoucher = new VmsVoucher2();
                vmsVoucher.setTransactionId(autoRemoveAssignedVoucher.getTransactionId());
                vouchers.add(vmsVoucher);

                cancelVmsVoucher(autoRemoveAssignedVoucher.getOrderId(), vmsRedeem, logSB);
                // end of release assigned voucher

                // update back to epc table epc_order_voucher (commit per case)
                pstmtUpdate.setInt(1, autoRemoveAssignedVoucher.getOrderId()); // order_id
                pstmtUpdate.setString(2, autoRemoveAssignedVoucher.getAssignId()); // assign_id
                pstmtUpdate.setString(3, autoRemoveAssignedVoucher.getTransactionId()); // transaction_id
                pstmtUpdate.executeUpdate();
                // end of update back to epc table epc_order_voucher

                logSB.append(new java.util.Date() + " end");

                autoRemoveAssignedVoucher.setLog(logSB.toString()); // log result


                aList.add(autoRemoveAssignedVoucher);
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return aList;
    }


    /***
     * remove assigned quota when cancel order
     *  at this moment (20230807), not applicable to real voucher
     */
    public ArrayList<AutoRemoveAssignedVoucher> removeAssignedVoucherWhenCancelOrder(int orderId, String caseId) {
        ArrayList<AutoRemoveAssignedVoucher> aList = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtUpdate = null;
        ResultSet rset = null;
        String sql = "";
        AutoRemoveAssignedVoucher autoRemoveAssignedVoucher = null;
        VmsRedeem2 vmsRedeem = null;
        ArrayList<VmsVoucher2> vouchers = null;
        VmsVoucher2 vmsVoucher = null;
        StringBuilder logSB = null;
        String iCaseId = epcSecurityHelper.encodeForSQL(caseId);
        String voucherCategory = "";
        String logStr = "[removeAssignedVoucherWhenCancelOrder][orderId:" + orderId + "][caseId:" + iCaseId + "] ";
        String tmpLogStr = "";

        try {
            conn = epcDataSource.getConnection();

            sql = "update epc_order_voucher " +
                  "   set order_id = -1 * order_id, status = ? " +
                  " where order_id = ? " +
                  "   and case_id = ? " +
                  "   and assign_id = ? " +
                  "   and transaction_id = ? ";
            pstmtUpdate = conn.prepareStatement(sql);

            sql = "select a.order_id, a.case_id, a.item_id, a.voucher_master_id, a.voucher_code, " +
                  "       a.assign_id, a.transaction_id, to_char(a.create_date, 'yyyymmddhh24mi') as vc_date, b.order_reference, b.order_status " +
                  "  from epc_order_voucher a, epc_order b, epc_order_attr c " +
                  " where a.order_id = ? " +
                  "   and a.case_id = ? " +
                  "   and a.assign_redeem = ? " +
                  "   and assign_id is not null " +
                  "   and b.order_id = a.order_id " +
                  "   and b.order_status not in (?,?) " +
                  "   and c.order_id = a.order_id " +
                  "   and c.attr_type = ? " +
                  "   and c.attr_value = ? " +
                  "   and c.status = ? " +
                  " order by a.create_date ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, iCaseId); // case_id
            pstmt.setString(3, VOUCHER_ASSIGN); // assign_redeem - ASSIGN
            pstmt.setString(4, "I"); // order_status - I 
            pstmt.setString(5, "LOCK"); // order_status - LOCK 
            pstmt.setString(6, "ORDER_TYPE"); // attr_type - ORDER_TYPE 
            pstmt.setString(7, "CHECKOUT"); // attr_value - CHECKOUT 
            pstmt.setString(8, "A"); // status - A
            rset = pstmt.executeQuery();
            while(rset.next()) {
                // init
                logSB = new StringBuilder(2048); // 2048 bytes
                logSB.append(new java.util.Date() + " start");

                autoRemoveAssignedVoucher = new AutoRemoveAssignedVoucher();
                autoRemoveAssignedVoucher.setOrderId(rset.getInt("order_id"));
                autoRemoveAssignedVoucher.setCaseId(StringHelper.trim(rset.getString("case_id")));
                autoRemoveAssignedVoucher.setItemId(StringHelper.trim(rset.getString("item_id")));
                autoRemoveAssignedVoucher.setVoucherMasterId(StringHelper.trim(rset.getString("voucher_master_id")));
                autoRemoveAssignedVoucher.setAssignId(StringHelper.trim(rset.getString("assign_id")));
                autoRemoveAssignedVoucher.setTransactionId(StringHelper.trim(rset.getString("transaction_id")));
                autoRemoveAssignedVoucher.setVoucherCreateDate(StringHelper.trim(rset.getString("vc_date")));
                autoRemoveAssignedVoucher.setOrderReference(StringHelper.trim(rset.getString("order_reference")));
                autoRemoveAssignedVoucher.setOrderStatus(StringHelper.trim(rset.getString("order_status")));

                tmpLogStr = "start voucherMasterId:" + autoRemoveAssignedVoucher.getVoucherMasterId() + 
                            ",assignId:" + autoRemoveAssignedVoucher.getAssignId() +
                            ",transactionId:" + autoRemoveAssignedVoucher.getTransactionId();
logger.info("{}{}", logStr, tmpLogStr);

                // get voucher type (category)
                voucherCategory = getVoucherCategory(autoRemoveAssignedVoucher.getAssignId());
                tmpLogStr = "voucherCategory:" + voucherCategory;
logger.info("{}{}", logStr, tmpLogStr);
                if(VOUCHER_CATEGORY_ENTITLEMENT.equals(voucherCategory) || VOUCHER_CATEGORY_QUOTA_ITEM.equals(voucherCategory)) {
                    // go ahead to proceed
                    tmpLogStr = " go ahead to proceed";
logger.info("{}{}", logStr, tmpLogStr);
                } else {
                    tmpLogStr = " NOT proceed";
logger.info("{}{}", logStr, tmpLogStr);

                    continue;
                }
                // end of get voucher type (category)

                // release assigned voucher
                vmsRedeem = new VmsRedeem2();
                vmsRedeem.setForceRemove(true);
                vmsRedeem.setAssignCancel(true);
                vmsRedeem.setRemoveTransactionLog(true);

                vouchers = new ArrayList<>();
                vmsRedeem.setVouchers(vouchers);

                vmsVoucher = new VmsVoucher2();
                vmsVoucher.setTransactionId(autoRemoveAssignedVoucher.getTransactionId());
                vouchers.add(vmsVoucher);

                cancelVmsVoucher(autoRemoveAssignedVoucher.getOrderId(), vmsRedeem, logSB);
                // end of release assigned voucher

                // update back to epc table epc_order_voucher (commit per case)
                pstmtUpdate.setString(1, "O"); // status - O, obsolete
                pstmtUpdate.setInt(2, autoRemoveAssignedVoucher.getOrderId()); // order_id
                pstmtUpdate.setString(3, autoRemoveAssignedVoucher.getCaseId()); // case_id
                pstmtUpdate.setString(4, autoRemoveAssignedVoucher.getAssignId()); // assign_id
                pstmtUpdate.setString(5, autoRemoveAssignedVoucher.getTransactionId()); // transaction_id
                pstmtUpdate.executeUpdate();
                // end of update back to epc table epc_order_voucher

                logSB.append(new java.util.Date() + " end");

                autoRemoveAssignedVoucher.setLog(logSB.toString()); // log result


                aList.add(autoRemoveAssignedVoucher);
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return aList;
    }


    /***
     * get voucher category / type
     *   "Hanset / Accessory discount" / "Entitlement" / "e-Coupon" / "Quota Item" / "Physical Coupon"
     * 
     * @param assignId
     * @return
     */
    public String getVoucherCategory(String assignId) {
        String category = "";
        String infoBody = "";
        ObjectMapper objectMapper = new ObjectMapper();
        EpcVmsVoucherListResponse epcVmsVoucherListResponse = null;
        ArrayList<EpcVmsVoucherListResponseDetailCoupon> couponList = null;

        try {
            infoBody = invokeVmsList(assignId);
            epcVmsVoucherListResponse = objectMapper.readValue(infoBody, EpcVmsVoucherListResponse.class);
            if(epcVmsVoucherListResponse != null) {
                couponList = epcVmsVoucherListResponse.getVoucherListRes().getCoupon();
                for (EpcVmsVoucherListResponseDetailCoupon coupon : couponList) {
                    category = coupon.getMasterCoupon().getCategory();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return category;
    }


    public boolean isCaseContainFaceValueTypeAlready(Connection epcConn, int smcOrderId, String smcCaseId, String faceValueType) {
        boolean isContain = true;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        int cnt = 0;

        try {
            sql = "select count(1) " +
                  "  from epc_order_item a, epc_order_voucher b " +
                  " where a.order_id = ? " +
                  "   and a.case_id = ? " +
                  "   and b.order_id = a.order_id " +
                  "   and b.item_id = a.item_id " +
                  "   and b.face_value_type = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, smcCaseId); // case_id
            pstmt.setString(3, faceValueType); // face_value_type
            rset = pstmt.executeQuery();
            if(rset.next()) {
                cnt = rset.getInt(1);
            } rset.close();
            pstmt.close();

            if(cnt == 0) {
                // no such type is found !
                isContain = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isContain;
    }


    public boolean isCaseContainCategoryAlready(Connection epcConn, int smcOrderId, String smcCaseId, String voucherMasterId, String category) {
        boolean isContain = true;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        int cnt = 0;

        try {
            sql = "select count(1) " +
                  "  from epc_order_case a, epc_order_voucher b " +
                  " where a.order_id = ? " +
                  "   and a.case_id = ? " +
                  "   and b.order_id = a.order_id " +
                  "   and b.case_id = a.case_id " +
                  "   and b.category = ? " + 
                  "   and b.voucher_master_id = ? ";
            pstmt = epcConn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, smcCaseId); // case_id
            pstmt.setString(3, category); // category
            pstmt.setString(4, voucherMasterId); // voucher_master_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                cnt = rset.getInt(1);
            } rset.close();
            pstmt.close();

            if(cnt == 0) {
                // no such category is found !
                isContain = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isContain;
    }
    
    public EpcVmsVoucherInfo invokeVmsApi(EpcVmsVoucherCodeRequest epcVmsVoucherCodeRequest) {
        EpcVmsVoucherInfo epcVmsVoucherInfo = new EpcVmsVoucherInfo();
        switch (epcVmsVoucherCodeRequest.getQueryByApi()) {
            case EpcVmsVoucherCodeRequest.LIST:
                epcVmsVoucherInfo = invokeVmsList(epcVmsVoucherCodeRequest);
                break;
            case EpcVmsVoucherCodeRequest.INFO:
                epcVmsVoucherInfo = invokeVmsInfo(epcVmsVoucherCodeRequest.getVoucherListReq().getMasterCouponId());
                break;
            default:
                break;
        }
        return epcVmsVoucherInfo;
    }

    public EpcVmsVoucherInfo invokeVmsInfo(String masterCouponId) {

        EpcVmsVoucherInfo epcVmsVoucherInfo = new EpcVmsVoucherInfo();
        epcVmsVoucherInfo.setIsFound("N");
        VmsAssignVoucher vmsAssignVoucher = null;
        HashMap<String, String> masterCouponIdMap = new HashMap<String, String>();
        List<VmsVoucherInformation> masterCouponInfoList = null;

        try {
            // initialize the map for query voucher information / setting
            vmsAssignVoucher = new VmsAssignVoucher();
            vmsAssignVoucher.setVoucherMasterId(masterCouponId);
            vmsAssignVoucher.setDone(false);
            
            masterCouponIdMap.put(masterCouponId, masterCouponId);
            
            masterCouponInfoList = getVoucherSetting(masterCouponIdMap);
            
            if (masterCouponInfoList.size() ==1 ) {
                VmsVoucherInformation masterCouponInfo = masterCouponInfoList.get(0);
                epcVmsVoucherInfo.setMasterVoucherId(masterCouponInfo.getMasterCouponId());
                epcVmsVoucherInfo.setScope(masterCouponInfo.getApplyScope());
                epcVmsVoucherInfo.setMultipleUse(masterCouponInfo.isMultipleUse());
                epcVmsVoucherInfo.setFaceValueType(masterCouponInfo.getFaceValueType());
                if (!masterCouponInfo.isCustomerEntitlementCoupon() && !masterCouponInfo.isAssignSerialNumber() && 
                    (VOUCHER_CATEGORY_PHYSICAL_COUPON.equals(masterCouponInfo.getCategory()) || VOUCHER_CATEGORY_HANDSET_ACCESSORY_DISCOUNT.equals(masterCouponInfo.getCategory()))) {
                    epcVmsVoucherInfo.setIsFound("Y");
                }
                epcVmsVoucherInfo.setIsFrom("MASTER_VOUCHER_ID");

                if (VOUCHER_FACE_VALUE_TYPE_FIXED_DISCOUNT.equals(epcVmsVoucherInfo.getFaceValueType())) {
                    epcVmsVoucherInfo.setRemainingAmount(masterCouponInfo.getFaceValue());
                } else {
                    epcVmsVoucherInfo.setRemainingAmount(new BigDecimal(-1)); // not to pass amount to vms api
                }
    
                epcVmsVoucherInfo.setName(masterCouponInfo.getNameEng());
                epcVmsVoucherInfo.setNameZHHK(masterCouponInfo.getNameChi());
                epcVmsVoucherInfo.setDescription(masterCouponInfo.getDescriptionEng());
                epcVmsVoucherInfo.setDescriptionZHHK(masterCouponInfo.getDescriptionChi());

                epcVmsVoucherInfo.setChargeWaiver(StringHelper.trim(masterCouponInfo.getChargeWaiver()));

                epcVmsVoucherInfo.setValidity(new BigDecimal(masterCouponInfo.getValidity()));
                epcVmsVoucherInfo.setCategory(masterCouponInfo.getCategory());
                epcVmsVoucherInfo.setSendEmail(masterCouponInfo.isSendEmail());
                epcVmsVoucherInfo.setSendSMS(masterCouponInfo.isSendSMS());
                if (masterCouponInfo.isNotModelled()) {
                    epcVmsVoucherInfo.setNotModelled("Y");
                } else {
                    epcVmsVoucherInfo.setNotModelled("N");
                }
    
                if (masterCouponInfo.isMobilePlanSubscriptionCoupon()) {
                    epcVmsVoucherInfo.setMobilePlanSubscriptionCoupon("Y");
                } else {
                    epcVmsVoucherInfo.setMobilePlanSubscriptionCoupon("N");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return epcVmsVoucherInfo;
    }
}
