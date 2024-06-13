package epc.epcsalesapi.sales;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.crm.CrmUpdateProfileHandler;
import epc.epcsalesapi.crm.bean.CrmUpdateProfile;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcConfirmNewMobileResult;
import epc.epcsalesapi.sales.bean.EpcCreateBillingAccountResult;
import epc.epcsalesapi.sales.bean.EpcCreateEPCReceiptResult;
import epc.epcsalesapi.sales.bean.EpcCreateEPCRecordResult;
import epc.epcsalesapi.sales.bean.EpcCreateMnpResult;
import epc.epcsalesapi.sales.bean.EpcCreateShkPointConfigResult;
import epc.epcsalesapi.sales.bean.EpcGetOrder;
import epc.epcsalesapi.sales.bean.EpcLoginChannel;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.EpcOrderQuoteInfo;
import epc.epcsalesapi.sales.bean.EpcPlaceOrder;
import epc.epcsalesapi.sales.bean.EpcPlaceOrderResult;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.EpcSmcQuote;
import epc.epcsalesapi.sales.bean.EpcSubmitQuoteToOrderResult2;
import epc.epcsalesapi.sales.bean.asiaMiles.CreateAsiaMiles;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelRedeem;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelRedeemResult;
import epc.epcsalesapi.sales.bean.bdayGift.EpcRedeem;
import epc.epcsalesapi.sales.bean.bdayGift.EpcRedeemResult;
import epc.epcsalesapi.stock.EpcStockHandler;
import epc.epcsalesapi.stock.bean.EpcConfirmStockReserveResult;

@Service
public class EpcPlaceOrderHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcPlaceOrderHandler.class);

    private final EpcOrderHandler epcOrderHandler;
    private final EpcQuoteHandler epcQuoteHandler;
    private final EpcPaymentHandler epcPaymentHandler;
    private final EpcSecurityHelper epcSecurityHelper;
    private final EpcBdayGiftHandler epcBdayGiftHandler;
    private final EpcOrderProcessCtrlHandler epcOrderProcessCtrlHandler;
    private final EpcStockHandler epcStockHandler;
    private final EpcMsgHandler epcMsgHandler;
    private final EpcVoucherHandlerNew epcVoucherHandlerNew;
    private final EpcWaivingReportHandler epcWaivingReportHandler;
    private final EpcShkpHandler epcShkpHandler;
    private final EpcSearchOrderHandler epcSearchOrderHandler;
    private final CrmUpdateProfileHandler crmUpdateProfileHandler;


    public EpcPlaceOrderHandler(EpcOrderHandler epcOrderHandler, EpcQuoteHandler epcQuoteHandler,
            EpcPaymentHandler epcPaymentHandler, EpcSecurityHelper epcSecurityHelper,
            EpcBdayGiftHandler epcBdayGiftHandler, EpcOrderProcessCtrlHandler epcOrderProcessCtrlHandler,
            EpcStockHandler epcStockHandler, EpcMsgHandler epcMsgHandler, EpcVoucherHandlerNew epcVoucherHandlerNew,
            EpcWaivingReportHandler epcWaivingReportHandler, EpcShkpHandler epcShkpHandler,
            EpcSearchOrderHandler epcSearchOrderHandler, CrmUpdateProfileHandler crmUpdateProfileHandler) {
        this.epcOrderHandler = epcOrderHandler;
        this.epcQuoteHandler = epcQuoteHandler;
        this.epcPaymentHandler = epcPaymentHandler;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcBdayGiftHandler = epcBdayGiftHandler;
        this.epcOrderProcessCtrlHandler = epcOrderProcessCtrlHandler;
        this.epcStockHandler = epcStockHandler;
        this.epcMsgHandler = epcMsgHandler;
        this.epcVoucherHandlerNew = epcVoucherHandlerNew;
        this.epcWaivingReportHandler = epcWaivingReportHandler;
        this.epcShkpHandler = epcShkpHandler;
        this.epcSearchOrderHandler = epcSearchOrderHandler;
        this.crmUpdateProfileHandler = crmUpdateProfileHandler;
    }


    public EpcPlaceOrderResult placeOrder(EpcPlaceOrder epcPlaceOrder) {
        EpcPlaceOrderResult epcPlaceOrderResult = new EpcPlaceOrderResult();
        String custId = StringHelper.trim(epcPlaceOrder.getCustId());
        int orderId = epcPlaceOrder.getOrderId();
        String orderReference = "";
        boolean isValid = true;
        String errMsg = "";
        ArrayList<EpcSmcQuote> epcQuoteList = null;
//        EpcQuote[] epcQuoteArray = null;
//        EpcQuote epcQuote = null;
        EpcCreateEPCRecordResult epcCreateEPCRecordResult = null;
//        EpcCreateBillingAccountResult epcCreateBillingAccountResult = null;
//        EpcSubmitQuoteToOrderResult epcSubmitQuoteToOrderResult = null;
//        EpcSubmitQuoteToOrderResult2 epcSubmitQuoteToOrderResult2 = null;
//        EpcConfirmNewMobileResult epcConfirmNewMobileResult = null;
        EpcCreateEPCReceiptResult epcCreateEPCReceiptResult = null;
//        EpcCreateMnpResult epcCreateMnpResult = null;
//        EpcGenerateDummySimResult epcGenerateDummySimResult = null;
        EpcConfirmStockReserveResult epcConfirmStockReserveResult = null;
//        String newQuoteId = "";
//        String sigmaOrderId = "";
        String receiptNo = "";
        String fulfillUser = StringHelper.trim(epcPlaceOrder.getFulfillUser());
        String fulfillSalesman = StringHelper.trim(epcPlaceOrder.getFulfillSalesman());
        String fulfillLocation = StringHelper.trim(epcPlaceOrder.getFulfillLocation());
        String fulfillChannel = StringHelper.trim(epcPlaceOrder.getFulfillChannel());
        BigDecimal totalOrderAmount = new BigDecimal(0);
        BigDecimal totalPaymentAmount = new BigDecimal(0);
//        BigDecimal totalCharge = new BigDecimal(0);
//        List<EpcOfferCharge> epcChargeList = null;
//        HashMap<String, String> convertedQuoteMap = new HashMap<>(); // quote guid, convert quote guid
        EpcGetOrder epcGetOrder = null;
        EpcOrderInfo epcOrderInfo = null;
        CreateAsiaMiles createAsiaMiles = null;
//        boolean isCreateWaivingRecord = false;
        CrmUpdateProfile crmUpdateProfile = null;
        String logStr = "[placeOrder()][orderId:" + orderId + "] ";
        String tmpLogStr = "";

        ArrayList<EpcRedeem> EpcRedeemList = new ArrayList<>();
	
        try {
logger.info("{}{}", logStr, "start");

            epcPlaceOrderResult.setCustId(custId);
            epcPlaceOrderResult.setOrderId(orderId);

            // basic checking
            orderReference = epcOrderHandler.isOrderBelongCust(custId, orderId);
            if("NOT_BELONG".equals(orderReference)) {
                errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
                isValid = false;
            } else {
//                epcGetOrder = getOrdersWithQuoteDetail("", orderReference, "", 0, true, true, "", "");
                epcGetOrder = epcSearchOrderHandler.getOrders("", orderReference, "", 0, true, true, "", "", "", "", "", false, false, true,"","");
                for(EpcOrderInfo s: epcGetOrder.getOrders()) {
                    epcOrderInfo = s;
                    epcPlaceOrderResult.setEpcOrderInfo(epcOrderInfo);


                    // prepare crm profile
                    crmUpdateProfile = new CrmUpdateProfile();
                    crmUpdateProfile.setCustId(epcOrderInfo.getCustId());
                    if("".equals(epcOrderInfo.getContactEmail())) {
                        crmUpdateProfile.setContactEmail("");
                    } else {
                        crmUpdateProfile.setContactEmail(EpcCrypto.dGet(epcOrderInfo.getContactEmail(), "utf-8"));
                    }
                    crmUpdateProfile.setContactNo(epcOrderInfo.getContactNo());
                    crmUpdateProfile.setOrderReference(epcOrderInfo.getOrderReference());
                    crmUpdateProfile.setOptIn("N"); // default
                    // end of prepare crm profile
                }
                if(epcOrderInfo == null) {
                    errMsg += "order detail cannot be retrieved by input order id [" + orderId + "]. ";
                    isValid = false;
                }
            }

            // check input channel
            if(!EpcLoginChannel.isChannelValid(fulfillChannel)) {
                errMsg += "input channel [" + fulfillChannel + "] is not valid. ";
                isValid = false;
            }
            
        	// check total payment vs total charge
        	totalPaymentAmount = epcPaymentHandler.getTotalPaymentAmount(orderId);
        	totalOrderAmount = epcPaymentHandler.getTotalCharge(orderId);
// tmp commented, kerrytsang, 20220221
//        	if(totalPaymentAmount.compareTo(totalOrderAmount) == -1) {
//        		errMsg += "total order payment is less than total order charge. ";
//            	isValid = false;
//        	}
        	
            // check stock reservation
            //  ...
            
            // end of basic checking
            
            
            if(isValid) {
                // get all quote under this order
                epcQuoteList = epcOrderHandler.getQuoteInEpcByOrderId(orderId);
                epcQuoteList.forEach(
                    s -> {
logger.info("{}{}{}", logStr, "quote included:", epcSecurityHelper.encode(s.getQuoteGuid()));

			// added by Danny Chan on 2023-1-9 (Birthday Gift Enhancement): start
			s.setEpcQuote(epcOrderHandler.getCPQQuoteInEpc(orderId, s.getQuoteId())); // get from EPC dumps
			// added by Danny Chan on 2023-1-9 (Birthday Gift Enhancement): end
                    }
                );
		
		// added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): start
		for (int i=0; i<epcQuoteList.size(); i++) {
			EpcQuoteItem quoteItems[] = epcQuoteList.get(i).getEpcQuote().getItems();
			
			for (int j=0; j<quoteItems.length; j++) {
				if ( epcBdayGiftHandler.hasBirthdayCharge(quoteItems[j]) ) {
					String msisdn = epcQuoteHandler.getConfiguredValue(quoteItems[j], "Msisdn");
					String customer_number = epcQuoteHandler.getConfiguredValue(quoteItems[j], "Customer_Number");
		
					EpcRedeem epcRedeem = new EpcRedeem();
		
					epcRedeem.setCustNum(customer_number);
					epcRedeem.setSubrNum(msisdn);
					epcRedeem.setUserName("SysAdmin");
					epcRedeem.setSalesman( epcPlaceOrder.getFulfillSalesman() );
					epcRedeem.setRbdUnitCode( epcPlaceOrder.getFulfillLocation() );
					epcRedeem.setRedemptionDate( new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()) );
		
					EpcRedeemResult epcRedeemResult = epcBdayGiftHandler.redeem(epcRedeem);
		
					if ( !epcRedeemResult.getErrorCode().equals("0") ) {
						throw new Exception(epcSecurityHelper.encode(epcRedeemResult.getErrorMessage()));
					} 
					
					EpcRedeemList.add(epcRedeem);					
				}
			}
		}
		// added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): end
		
                // end of get all quote under this order

                // added by Danny Chan on 2022-9-27: start
//                for (int i=0; i<epcQuoteList.size(); i++) {
//                    EpcQuote quote = epcQuoteList.get(i).getEpcQuote();
//                    
//                    EpcCreateShkPointConfigResult createShkPointConfig_result = epcShkpHandler.createShkPointConfig(orderId, quote);
//                    
//                    if (!createShkPointConfig_result.getResultCode().equals("0")) {
//                        throw new Exception(epcSecurityHelper.encode("Error in createShkPointConfig: " + createShkPointConfig_result.getResultMsg()));
//                    }
//                }
                for(EpcOrderQuoteInfo quoteInfo: epcOrderInfo.getEpcOrderQuoteInfoList()) {
                    EpcCreateShkPointConfigResult createShkPointConfig_result = epcShkpHandler.createShkPointConfig(orderId, quoteInfo.getEpcQuote());
                    if (!"0".equals(createShkPointConfig_result.getResultCode())) {
                        throw new Exception(epcSecurityHelper.encode("Error in createShkPointConfig: " + createShkPointConfig_result.getResultMsg()));
                    }
                }
                // added by Danny Chan on 2022-9-27: end
            	
                // create process control record
//                epcOrderProcessCtrlHandler.createProcessCtrl(orderId, epcQuoteList);
                epcOrderProcessCtrlHandler.createProcessCtrl(epcOrderInfo);
                // end of create process control record
                
                
                // handle dummy sim (new activation only)
// tmp commented, kerrytsang, 20210224
//                epcGenerateDummySimResult = epcSimHandler.generateDummySim(epcQuote);
//logger.info(logStr + "epcGenerateDummySimResult.getResult():" + epcGenerateDummySimResult.getResult());
//logger.info(logStr + "epcGenerateDummySimResult.getErrMsg():" + epcGenerateDummySimResult.getErrMsg());
//                if(!"SUCCESS".equals(epcGenerateDummySimResult.getResult())) {
//                    throw new Exception(epcGenerateDummySimResult.getErrMsg());
//                }
// end of tmp commented, kerrytsang, 20210224
                // end of handle dummy sim (new activation only)
                
                
                // create billing account (with gup), process ctrl are in createBillingAccountWithGup()
// tmp commented, kerrytsang, 20230614
//                epcCreateBillingAccountResult = epcOrderHandler.createBillingAccountWithGup(custId, orderId, fulfillUser);
//logger.info("{}{}{}", logStr, "epcCreateBillingAccountResult.getResult():", epcSecurityHelper.encodeForSQL(epcCreateBillingAccountResult.getResult()));
//logger.info("{}{}{}", logStr, "epcCreateBillingAccountResult.getErrMsg():", epcSecurityHelper.encodeForSQL(epcCreateBillingAccountResult.getErrMsg()));
//                if(!"SUCCESS".equals(epcCreateBillingAccountResult.getResult())) {
//                    throw new Exception(epcSecurityHelper.encodeForSQL(epcCreateBillingAccountResult.getErrMsg()));
//                }
// end of tmp commented, kerrytsang, 20230614
                // end of create billing account (with gup)
                
                
                // create mnp record (if any)
// tmp commented, kerrytsang, 20230614
//                epcCreateMnpResult = epcMnpHandler.createMnpRecord(custId, orderId, fulfillUser);
//logger.info("{}{}{}", logStr, "epcCreateMnpResult.getResult():", epcSecurityHelper.encodeForSQL(epcCreateMnpResult.getResult()));
//logger.info("{}{}{}", logStr, "epcCreateMnpResult.getErrMsg():", epcSecurityHelper.encodeForSQL(epcCreateMnpResult.getErrMsg()));
//                if(!"SUCCESS".equals(epcCreateMnpResult.getResult())) {
//                    throw new Exception(epcSecurityHelper.encodeForSQL(epcCreateMnpResult.getErrMsg()));
//                }
// end of tmp commented, kerrytsang, 20230614
                // end of create mnp record (if any)
                
                
                // confirm stock reservation (if any)
//                epcConfirmStockReserveResult = epcStockHandler.confirmTmpReserve(custId, orderId, fulfillUser, fulfillSalesman, null);
                epcConfirmStockReserveResult = epcStockHandler.confirmEpcTmpReserve(custId, orderId, fulfillUser, fulfillSalesman, fulfillChannel, fulfillLocation, null, false);
                tmpLogStr = "epcConfirmStockReserve result:" + epcSecurityHelper.encodeForSQL(epcConfirmStockReserveResult.getResult()) +
                            ",errMsg:" + epcSecurityHelper.encodeForSQL(epcConfirmStockReserveResult.getErrMsg());
logger.info("{}{}", logStr, tmpLogStr);
				if(!"SUCCESS".equals(epcConfirmStockReserveResult.getResult())) {
				    throw new Exception(epcSecurityHelper.encodeForSQL(epcConfirmStockReserveResult.getErrMsg()));
				}
                // end of confirm stock reservation (if any)


                // update stock status to unlimited items
                epcStockHandler.updateStockStatusForUnlimitedItems(orderId);
                // end of update stock status to unlimited items
                
                
                // confirm new mobile no. reservation (if any)
// tmp commented, kerrytsang, 20230614
//                epcConfirmNewMobileResult = confirmNewMobile(custId, orderId, fulfillUser, fulfillSalesman, fulfillLocation);
//logger.info("{}{}{}", logStr, "epcConfirmNewMobileResult.getResult():", epcSecurityHelper.encodeForSQL(epcConfirmNewMobileResult.getResult()));
//logger.info("{}{}{}", logStr, "epcConfirmNewMobileResult.getErrMsg():", epcSecurityHelper.encodeForSQL(epcConfirmNewMobileResult.getErrMsg()));
//                if(!"SUCCESS".equals(epcConfirmNewMobileResult.getResult())) {
//                    throw new Exception(epcSecurityHelper.encodeForSQL(epcConfirmNewMobileResult.getErrMsg()));
//                }
// end of tmp commented, kerrytsang, 20230614
                // end of confirm new mobile no. reservation (if any)


                // create asia miles record (if any)
                createAsiaMiles = epcOrderHandler.createAsiaMiles(orderId, fulfillUser, fulfillSalesman);
                tmpLogStr = "createAsiaMiles result:" + epcSecurityHelper.encodeForSQL(createAsiaMiles.getResult()) +
                            ",errMsg:" + epcSecurityHelper.encodeForSQL(createAsiaMiles.getErrMsg());
logger.info("{}{}", logStr, tmpLogStr);
                if(!"SUCCESS".equals(createAsiaMiles.getResult())) {
                    throw new Exception(epcSecurityHelper.encodeForSQL(createAsiaMiles.getErrMsg()));
                }
                // end of create asia miles record (if any)
                

                // convert quote and submit sigma order
                epcQuoteHandler.submitQuoteToOrderAsync2(epcQuoteList);
                // end of convert quote and submit sigma order
                
                
                // create 1st receipt
                epcCreateEPCReceiptResult = epcOrderHandler.createEPCReceipt(custId, orderId, epcOrderInfo, fulfillUser, fulfillSalesman, fulfillLocation, fulfillChannel);
                receiptNo = epcCreateEPCReceiptResult.getReceiptNo();
                
                epcPlaceOrderResult.setReceiptNo(receiptNo);		// added by Danny Chan on 2022-11-15 (SHK Point Payment Enhancement)
		
//                epcChargeList = epcCreateEPCReceiptResult.getEpcChargeList();
//                totalOrderAmount = epcCreateEPCReceiptResult.getTotalAmount();
                tmpLogStr = "epcCreateEPCReceiptResult result:" + epcSecurityHelper.encodeForSQL(epcCreateEPCReceiptResult.getResult()) +
                            ",errMsg:" + epcSecurityHelper.encodeForSQL(epcCreateEPCReceiptResult.getErrMsg()) +
                            ",receiptNo:" + epcSecurityHelper.encodeForSQL(receiptNo);
logger.info("{}{}", logStr, tmpLogStr);
                // end of create 1st receipt

                
                // update epc table
                epcCreateEPCRecordResult = epcOrderHandler.updateEPCRecords(custId, orderId, receiptNo, totalOrderAmount, fulfillUser, fulfillSalesman, fulfillLocation, fulfillChannel);
                tmpLogStr = "updateEPCRecords result (final update) result:" + epcSecurityHelper.encodeForSQL(epcCreateEPCRecordResult.getResult()) +
                            ",errMsg:" + epcSecurityHelper.encodeForSQL(epcCreateEPCRecordResult.getErrMsg());
logger.info("{}{}", logStr, tmpLogStr);
                // end of update epc table


                // remove quote metadata
                epcOrderHandler.removeQuoteMetaData(epcQuoteList);
                // end of remove quote metadata


                // send confirmation email
//                if(EpcLoginChannel.STORE.equals(fulfillChannel)) {
                if( !EpcLoginChannel.ONLINE.equals(fulfillChannel) ) {
                    epcMsgHandler.sendConfirmationEmailAsync(custId, orderId + "");
                }
                // end of send confirmation email

                
                // send voucher email / sms
                epcVoucherHandlerNew.sendVoucherEmailSmsAsync(orderId, custId);
                // end of send voucher email / sms


                // create waiving report record if any
                epcWaivingReportHandler.createWaivingRecordAsync(EpcWaivingReportHandler.WAIVE_TYPE_ONSPOT_DISCOUNT, orderId + "");
                // end of create waiving report record if any


                // update crm profile
                crmUpdateProfileHandler.updateProfileAsync(crmUpdateProfile);
                // end of update crm profile


                epcPlaceOrderResult.setSaveStatus("OK");
            } else {
                // error
                epcPlaceOrderResult.setSaveStatus("FAIL");
                epcPlaceOrderResult.setErrorCode("1000");
                epcPlaceOrderResult.setErrorMessage(errMsg);
            }
	    
        } catch (Exception e) {
            e.printStackTrace();
            
            for (int i=0; i<EpcRedeemList.size(); i++) {
                EpcCancelRedeem cancelRedeem = new EpcCancelRedeem();
                
                cancelRedeem.setCustNum(EpcRedeemList.get(i).getCustNum());
                cancelRedeem.setSubrNum(EpcRedeemList.get(i).getSubrNum());
                cancelRedeem.setSalesman(EpcRedeemList.get(i).getSalesman());
                cancelRedeem.setUserName(EpcRedeemList.get(i).getUserName());
                
                EpcCancelRedeemResult epcCancelRedeemResult = epcBdayGiftHandler.cancelRedeem(cancelRedeem);
                
                logger.info("cancelRedeem errorcode = " + epcCancelRedeemResult.getErrorCode() + 
                                                        ": custNum = " + EpcRedeemList.get(i).getCustNum() + ", " + 
                                                        "subrNum = " + EpcRedeemList.get(i).getSubrNum() + ", " + 
                                                        "salesman = " + EpcRedeemList.get(i).getSalesman() + ", " + 
                                                        "username = " + EpcRedeemList.get(i).getUserName() + ", ");
                
                if (!epcCancelRedeemResult.getErrorCode().equals("0")) {
                logger.info("Cannot cancel gift redeem: custNum = " + EpcRedeemList.get(i).getCustNum() + ", " + 
                                                        "subrNum = " + EpcRedeemList.get(i).getSubrNum() + ", " + 
                                                        "salesman = " + EpcRedeemList.get(i).getSalesman() + ", " + 
                                                        "username = " + EpcRedeemList.get(i).getUserName() + ", " );
                }
            }
	    
            epcPlaceOrderResult.setSaveStatus("FAIL");
            epcPlaceOrderResult.setErrorCode("1001");
            epcPlaceOrderResult.setErrorMessage(e.getMessage());
        } 
        return epcPlaceOrderResult;
    }
}
