package epc.epcsalesapi.sales;

import java.util.ArrayList;
import java.util.HashMap;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.sales.bean.EpcCpqValidationError;
import epc.epcsalesapi.sales.bean.EpcGetOrder;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.EpcOrderItemInfo;
import epc.epcsalesapi.sales.bean.EpcOrderQuoteInfo;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.EpcQuoteProductCandidate;
import epc.epcsalesapi.sales.bean.EpcSmcQuote;
import epc.epcsalesapi.sales.bean.EpcValidateOrderError;
import epc.epcsalesapi.sales.bean.EpcValidateOrderResult;
// added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): start 
import epc.epcsalesapi.sales.bean.bdayGift.EpcGetBdayGift;
import epc.epcsalesapi.sales.bean.bdayGift.EpcGetBdayGiftResult;
// added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): end 

@Service
public class EpcOrderValidationHandler {
    
    private final Logger logger = LoggerFactory.getLogger(EpcOrderValidationHandler.class);
    
    @Autowired
    private EpcOrderHandler epcOrderHandler;

    @Autowired
    private EpcSecurityHelper epcSecurityHelper;

    @Autowired
    private DataSource epcDataSource;
    
    // added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): start 
    @Autowired
    private EpcBdayGiftHandler epcBdayGiftHandler;
	
	@Autowired
	private EpcQuoteHandler epcQuoteHandler;	
    // added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): end 

    @Autowired
    private EpcSearchOrderHandler epcSearchOrderHandler;

           
    public EpcValidateOrderResult validateOrder(int orderId) {
        EpcValidateOrderResult epcValidateOrderResult = new EpcValidateOrderResult();
        ArrayList<EpcValidateOrderError> epcValidateOrderErrorList = new ArrayList<EpcValidateOrderError>();
        epcValidateOrderResult.setEpcValidateOrderErrorList(epcValidateOrderErrorList);
        EpcValidateOrderError epcValidateOrderError = null;
        ArrayList<EpcSmcQuote> epcQuoteList = null;
        EpcQuote epcQuote = null;
        HashMap<String, Object> currentValidation = null;
        ArrayList<HashMap<String, Object>> errors = null;
        boolean valid = false;
        HashMap<String, Object> tmpItemCurrentValidationMap = null;
        String itemIdKey = "";
        boolean withValidDelivery = false;
        ArrayList<EpcOrderItemInfo> orderItemList = null;
        int tmpDeliveryId = 0;
        String tmpProductCode = "";
//        EpcOrderInfo epcOrderInfo = null;
        String orderReference = "";
        EpcGetOrder epcGetOrder = null;
        String logStr = "[validateOrder][orderId:" + orderId + "] ";
        String tmpLogStr = "";
        
        try {
logger.info("{}{}", logStr, "start");

            // no need to re-generate those charge !!!
            orderReference = epcOrderHandler.getOrderReferenceByOrderId(orderId);
            epcGetOrder = epcSearchOrderHandler.getOrders("", orderReference, "", 0, true, true, "", "", "", "", "", false, false, true,"","");
            for(EpcOrderInfo s: epcGetOrder.getOrders()) {
                epcValidateOrderResult.setEpcOrderInfo(s);
            }


            // get all quote under smc order id input
            epcQuoteList = epcOrderHandler.getQuoteByOrderId(orderId);
            epcQuoteList.forEach(
                s -> {
                    String ss = "quote included:" + s.getQuoteGuid() + ",quoteId:" + s.getQuoteId();
logger.info("{}{}", logStr, ss);

                    // invoke CPQ validation, kerrytsang, 20230130
                    boolean isValidate = epcQuoteHandler.validateAndPrice(s.getQuoteGuid());
                    ss = " validateAndPrice:" + isValidate;
logger.info("{}{}", logStr, ss);
                    // end of invoke CPQ validation, kerrytsang, 20230130

                    // co-operate with CPQ validation api - /api/quotes/{quoteId}/validateAndPrice, kerrytsang, 20230130
                    s.setEpcQuote(epcQuoteHandler.getQuoteInfo(s.getQuoteGuid(), "validation")); // get from CPQ
                }
            );
            // end of get all quote under smc order id input

            // remove quote metadata
	    // commented out by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): start 
            //epcOrderHandler.removeQuoteMetaData(epcQuoteList); // remove quote metadata
	    // commented out by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): end 
            // end of remove quote metadata
            
            
            // retrieve quote validation info
            for(EpcSmcQuote f : epcQuoteList) {
                epcQuote = f.getEpcQuote();
                if(epcQuote != null) {
                    currentValidation = epcQuote.getCurrentValidation();
                    if(currentValidation != null) {
                        valid = ((Boolean)currentValidation.get("valid")).booleanValue();

                        tmpLogStr = "quoteGuid:" + epcQuote.getId() + ",currentValidation.valid (quote level):" + valid;
logger.info("{}{}", logStr, tmpLogStr);
                        errors = (ArrayList<HashMap<String, Object>>)currentValidation.get("errors");
                        for (HashMap<String, Object> errorMap : errors) {
                            epcValidateOrderError = new EpcValidateOrderError();
                            epcValidateOrderError.setErrCode("10001");
                            epcValidateOrderError.setErrMsg("");
                            epcValidateOrderError.setErrMsg2("");
                            epcValidateOrderError.setErrMsg3(errorMap);
                            epcValidateOrderError.setSource("CPQ");
                            epcValidateOrderErrorList.add(epcValidateOrderError);
                        }
                    }
                    
                    if(!valid) {
                        // loop thru quote item
                        for(EpcQuoteItem epcQuoteItem: epcQuote.getItems()) {
                            tmpItemCurrentValidationMap = epcQuoteItem.getCurrentValidation();
                            errors = (ArrayList<HashMap<String, Object>>)tmpItemCurrentValidationMap.get("errors");
                            for (HashMap<String, Object> errorMap : errors) {
                                epcValidateOrderError = new EpcValidateOrderError();
                                epcValidateOrderError.setErrCode("10001");
                                epcValidateOrderError.setErrMsg("error in item " + epcQuoteItem.getId());
                                epcValidateOrderError.setErrMsg2("");
                                epcValidateOrderError.setErrMsg3(errorMap);
                                epcValidateOrderError.setSource("CPQ");
                                epcValidateOrderErrorList.add(epcValidateOrderError);
                            }
                        }
                        // end of loop thru quote item
                    }


                    // loop thru quote item
                    for(EpcQuoteItem epcQuoteItem: epcQuote.getItems()) {

			// added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): start 
			System.out.println("@@@ has_birthday_charge = " + epcBdayGiftHandler.hasBirthdayCharge(epcQuoteItem));

			if ( epcBdayGiftHandler.hasBirthdayCharge(epcQuoteItem) ) {
				String msisdn = null, billing_account_number = null, customer_number = null;
	
				msisdn = epcQuoteHandler.getConfiguredValue(epcQuoteItem, "Msisdn");
				billing_account_number = epcQuoteHandler.getConfiguredValue(epcQuoteItem, "Billing_Account_Number");
				customer_number = epcQuoteHandler.getConfiguredValue(epcQuoteItem, "Customer_Number");
	
				System.out.println("@@@ msisdn = " + msisdn);
				System.out.println("@@@ billing_account_number = " + billing_account_number);
				System.out.println("@@@ customer_number = " + customer_number);
				
				if ( msisdn==null ) {
					epcValidateOrderError = new EpcValidateOrderError();
					epcValidateOrderError.setErrCode("10008");
					epcValidateOrderError.setErrMsg("Msisdn is empty");
					epcValidateOrderError.setErrMsg2("");
					epcValidateOrderError.setSource("SMC");
					epcValidateOrderErrorList.add(epcValidateOrderError);
				}
				if ( customer_number==null ) {
					epcValidateOrderError = new EpcValidateOrderError();
					epcValidateOrderError.setErrCode("10008");
					epcValidateOrderError.setErrMsg("Customer number is empty");
					epcValidateOrderError.setErrMsg2("");
					epcValidateOrderError.setSource("SMC");
					epcValidateOrderErrorList.add(epcValidateOrderError);
				}				
				
				if ( msisdn!=null && customer_number!=null ) {
					EpcGetBdayGift epcGetBdayGift = new EpcGetBdayGift();
					
					epcGetBdayGift.setCustNum(customer_number);
					epcGetBdayGift.setSubrNum(msisdn);
					epcGetBdayGift.setGiftId("396");
					epcGetBdayGift.setUserName("SysAdmin");
					
					EpcGetBdayGiftResult result = epcBdayGiftHandler.enquire(epcGetBdayGift);
					
					System.out.println( "errorCode = " + result.getErrorCode() );
					System.out.println( "errorMessage = " + result.getErrorMessage());
					System.out.println( "result = " + result.getResult() );
					
					if (!result.getErrorCode().equals("0")) {
						epcValidateOrderError = new EpcValidateOrderError();
						epcValidateOrderError.setErrCode("10009");
						epcValidateOrderError.setErrMsg(result.getErrorMessage());
						epcValidateOrderError.setErrMsg2("");
						epcValidateOrderError.setSource("SMC");
						epcValidateOrderErrorList.add(epcValidateOrderError);						
					}
				}
			}
			// added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): end 

                        tmpItemCurrentValidationMap = epcQuoteItem.getCurrentValidation();
                        if(tmpItemCurrentValidationMap != null) {
                            errors = (ArrayList<HashMap<String, Object>>)tmpItemCurrentValidationMap.get("errors");
                            for (HashMap<String, Object> errorMap : errors) {
                                epcValidateOrderError = new EpcValidateOrderError();
                                epcValidateOrderError.setErrCode("10006");
                                epcValidateOrderError.setErrMsg("error in item " + epcQuoteItem.getId());
                                epcValidateOrderError.setErrMsg2("");
                                epcValidateOrderError.setErrMsg3(errorMap);
                                epcValidateOrderError.setSource("CPQ");
                                epcValidateOrderErrorList.add(epcValidateOrderError);
                            }
                        }
                    }
                    // end of loop thru quote item
                } else {
                    // cannot get quote info
                    epcValidateOrderError = new EpcValidateOrderError();
                    epcValidateOrderError.setErrCode("10002");
                    epcValidateOrderError.setErrMsg("cannot get quote info for " + epcQuote.getId());
                    epcValidateOrderError.setErrMsg2("");
                    epcValidateOrderError.setSource("SMC");
                    epcValidateOrderErrorList.add(epcValidateOrderError);
                }
            }
            // end of retrieve quote validation info
            
	    // added by Danny Chan on 2023-1-9 (Birthday Gift Enhancement): start 
	    epcOrderHandler.removeQuoteMetaData(epcQuoteList); // remove quote metadata
	    // added by Danny Chan on 2023-1-9 (Birthday Gift Enhancement): end 
            
            // check whether all DEVICE / SCREEN_REPLACE / APPLECARE items are mapped with delivery info
            //  and whether product code is missed
            orderItemList = epcOrderHandler.getOrderItemsForOrderValidation(orderId);
            for(EpcOrderItemInfo e: orderItemList) {
                itemIdKey = e.getItemId();
                tmpDeliveryId = e.getDeliveryId();
                tmpProductCode = e.getItemCode();
                
                withValidDelivery = false; // reset
                withValidDelivery = epcOrderHandler.withValidDelivery(orderId, itemIdKey);

                tmpLogStr = "itemId:" + itemIdKey + "deliveryId:" + tmpDeliveryId + ",withValidDelivery:" + withValidDelivery;
logger.info("{}{}", logStr, tmpLogStr);

                if(tmpDeliveryId == 0) {
                    // error
                    epcValidateOrderError = new EpcValidateOrderError();
                    epcValidateOrderError.setErrCode("10007");
                    epcValidateOrderError.setErrMsg("item " + itemIdKey + " is not mapped to any delivery info");
                    epcValidateOrderError.setErrMsg2("");
                    epcValidateOrderError.setSource("SMC");
                    epcValidateOrderErrorList.add(epcValidateOrderError);
                }
                
                if(!withValidDelivery) {
                    // error
                    epcValidateOrderError = new EpcValidateOrderError();
                    epcValidateOrderError.setErrCode("10005");
                    epcValidateOrderError.setErrMsg("delivery info of item " + itemIdKey + " is invalid");
                    epcValidateOrderError.setErrMsg2("");
                    epcValidateOrderError.setSource("SMC");
                    epcValidateOrderErrorList.add(epcValidateOrderError);
                }

                if("".equals(tmpProductCode)) {
                    // error
                    epcValidateOrderError = new EpcValidateOrderError();
                    epcValidateOrderError.setErrCode("10010");
                    epcValidateOrderError.setErrMsg("product code of item " + itemIdKey + " is missed");
                    epcValidateOrderError.setErrMsg2("");
                    epcValidateOrderError.setSource("SMC");
                    epcValidateOrderErrorList.add(epcValidateOrderError);
                }
            }
            // end of check whether all DEVICE items are mapped with delivery info
            
            
//            // convert quotes (wisespot cms validation is/are embedded in convert quote action
//            for(EpcSmcQuote q : epcQuoteList) {
//                if(q.getEpcQuote().getQuoteType() == 0) {
//                    // need to convert if quote type = 0
//                    convertFuture = CompletableFuture.completedFuture(q).thenApplyAsync(s -> epcQuoteHandler.convertQuote(s.getQuoteGuid()));
//                    convertFutureList.add(convertFuture);
//                }
//            }
//            
//            combinedFuture = CompletableFuture.allOf(convertFutureList.toArray(new CompletableFuture[convertFutureList.size()]));
//            combinedFuture.get();
//            
//            for(CompletableFuture<EpcConvertQuoteResult2> f : convertFutureList) {
//                epcConvertQuoteResult2 = f.get();
//                if(epcConvertQuoteResult2 != null) {
//                    if("SUCCESS".equals(epcConvertQuoteResult2.getResult())) {
//                        // update new quote (type 3) to epc table
////                        epcOrderHandler.updateConvertedQuoteToOrder(epcConvertQuoteResult2.getOriginQuoteGuid(), epcConvertQuoteResult2.getNewQuoteGuid());
//                        for(EpcSmcQuote q : epcQuoteList) {
//                            if(q.getQuoteGuid().equals(epcConvertQuoteResult2.getOriginQuoteGuid())) {
//                                epcOrderHandler.updateQuoteGuid(
//                                    orderId, q.getQuoteId(), 
//                                    epcConvertQuoteResult2.getOriginQuoteGuid(), epcConvertQuoteResult2.getNewQuoteGuid(), 
//                                    "convert " + epcConvertQuoteResult2.getOriginQuoteGuid() + " during order validation"
//                                );
//                            }
//                        }
//                    } else {
//                        // error
//                        epcValidateOrderError = new EpcValidateOrderError();
//                        epcValidateOrderError.setErrCode("10003");
//                        epcValidateOrderError.setErrMsg(epcConvertQuoteResult2.getErrMsg());
//                        epcValidateOrderError.setErrMsg2("");
//                        epcValidateOrderError.setSource("CPQ");
//                        epcValidateOrderErrorList.add(epcValidateOrderError);
//                    }
//                } else {
//                    // error 
//                    epcValidateOrderError = new EpcValidateOrderError();
//                    epcValidateOrderError.setErrCode("10004");
//                    epcValidateOrderError.setErrMsg("cannot convert quote info for " + epcQuote.getId());
//                    epcValidateOrderError.setErrMsg2("");
//                    epcValidateOrderError.setSource("SMC");
//                    epcValidateOrderErrorList.add(epcValidateOrderError);
//                }
//            }
//            // end of convert quotes (wisespot cms validation is/are embedded in convert quote action
            
            epcValidateOrderResult.setResult("SUCCESS");
        } catch(Exception e) {
            e.printStackTrace();
            
            epcValidateOrderResult.setResult("FAIL");
            
            epcValidateOrderError = new EpcValidateOrderError();
            epcValidateOrderError.setErrCode("10999");
            epcValidateOrderError.setErrMsg(e.getMessage());
            epcValidateOrderError.setErrMsg2("");
            epcValidateOrderError.setSource("SMC");
            epcValidateOrderErrorList.add(epcValidateOrderError);
        } finally {
        }
        return epcValidateOrderResult;
    }

}
