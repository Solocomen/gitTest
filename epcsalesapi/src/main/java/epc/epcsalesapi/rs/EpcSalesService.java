package epc.epcsalesapi.rs;

import epc.epcsalesapi.crm.EpcCustomerHandler;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.notification.EpcNotificationHandler;
import epc.epcsalesapi.notification.bean.EpcNotification;
import epc.epcsalesapi.sales.EpcCancelHandler;
import epc.epcsalesapi.sales.EpcInvoiceHandler;
import epc.epcsalesapi.sales.EpcMsgHandler;
import epc.epcsalesapi.sales.EpcOrderHandler;
import epc.epcsalesapi.sales.EpcOrderValidationHandler;
import epc.epcsalesapi.sales.EpcPaymentHandler;
import epc.epcsalesapi.sales.EpcPlaceOrderHandler;
import epc.epcsalesapi.sales.EpcPrintingHandler;
import epc.epcsalesapi.sales.EpcQuoteHandler;
import epc.epcsalesapi.sales.EpcReceiptHandler;
import epc.epcsalesapi.sales.EpcRefundHandler;
import epc.epcsalesapi.sales.EpcSearchOrderHandler;
import epc.epcsalesapi.sales.EpcTradeInHandler;
import epc.epcsalesapi.sales.EpcVoucherHandlerNew;
import epc.epcsalesapi.sales.bean.AvailableCancelReceiptType;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import epc.epcsalesapi.sales.bean.EpcCancelReceipt;
import epc.epcsalesapi.sales.bean.EpcCommonAPIResponse;
import epc.epcsalesapi.sales.bean.EpcConfirmOrderResult;
import epc.epcsalesapi.sales.bean.EpcCreateInvoice;
import epc.epcsalesapi.sales.bean.EpcCreateInvoiceResult;
import epc.epcsalesapi.sales.bean.EpcCreateOrder;
import epc.epcsalesapi.sales.bean.EpcCreateOrderResult;
import epc.epcsalesapi.sales.bean.EpcCreatePayment;
import epc.epcsalesapi.sales.bean.EpcCreateReceipt;
import epc.epcsalesapi.sales.bean.EpcCreateReceiptResult;
import epc.epcsalesapi.sales.bean.EpcDiscountCharge;
import epc.epcsalesapi.sales.bean.EpcDiscountChargeResult;
import epc.epcsalesapi.sales.bean.EpcExtensionCharge;
import epc.epcsalesapi.sales.bean.EpcExtensionChargeResult;
import epc.epcsalesapi.sales.bean.EpcFulfillOrder;
import epc.epcsalesapi.sales.bean.EpcFulfillResult;
import epc.epcsalesapi.sales.bean.EpcGeneralResponse;
import epc.epcsalesapi.sales.bean.EpcGetCharge;
import epc.epcsalesapi.sales.bean.EpcGetChargeResult;
import epc.epcsalesapi.sales.bean.EpcGetDealerCode;
import epc.epcsalesapi.sales.bean.EpcGetOrder;
import epc.epcsalesapi.sales.bean.EpcGetRemainingCharge;
import epc.epcsalesapi.sales.bean.EpcGetSpec;
import epc.epcsalesapi.sales.bean.EpcGetTradeInResult;
import epc.epcsalesapi.sales.bean.EpcInitPayment;
import epc.epcsalesapi.sales.bean.EpcLockOrderResult;
import epc.epcsalesapi.sales.bean.EpcNotificationMessage;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.EpcPayment;
import epc.epcsalesapi.sales.bean.EpcPaymentCodeResult;
import epc.epcsalesapi.sales.bean.EpcPlaceOrder;
import epc.epcsalesapi.sales.bean.EpcPlaceOrderResult;
import epc.epcsalesapi.sales.bean.EpcPrintTransferNotes;
import epc.epcsalesapi.sales.bean.EpcProceedOrder;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcRefundRecord;
import epc.epcsalesapi.sales.bean.EpcSendShoppingBag;
import epc.epcsalesapi.sales.bean.EpcSettleExtensionFee;
import epc.epcsalesapi.sales.bean.EpcSettlePayment;
import epc.epcsalesapi.sales.bean.EpcSubr;
import epc.epcsalesapi.sales.bean.EpcTransferOrder;
import epc.epcsalesapi.sales.bean.EpcUpdateOrderType;
import epc.epcsalesapi.sales.bean.EpcValidateOrderResult;
import epc.epcsalesapi.sales.bean.EpcVerifyExistingMobile;
import epc.epcsalesapi.sales.bean.EpcWaiveCharge;
import epc.epcsalesapi.sales.bean.EpcWaiveChargeResult;
import epc.epcsalesapi.sales.bean.RefundRequest;
import epc.epcsalesapi.sales.bean.cancelOrder.EpcCancelAmount;
import epc.epcsalesapi.sales.bean.cancelOrder.EpcCancelOrder;
import epc.epcsalesapi.sales.bean.orderAttachment.EpcOrderAttach;
import epc.epcsalesapi.sales.bean.orderAttachment.EpcOrderAttachResponse;
import epc.epcsalesapi.sales.bean.orderReservedItems.UpdateReservedItemsRequest;
import epc.epcsalesapi.sales.bean.vms.cust.VmsCustVoucher;
import epc.epcsalesapi.stock.EpcStockHandler;
import epc.epcsalesapi.stock.bean.EpcCheckProduct;
// added by Danny Chan on 2021-2-8: start
//import epc.epcsalesapi.sales.bean.EpcDailySalesFigures;
//import epc.epcsalesapi.sales.bean.EpcDailySalesFiguresSalesman;
//import epc.epcsalesapi.sales.bean.EpcDailySalesFiguresSummary;
import epc.epcsalesapi.sales.bean.EpcDeleteQuoteFromOrder;
import epc.epcsalesapi.sales.bean.EpcGetDailySalesFigures;
import epc.epcsalesapi.sales.bean.EpcGetDailySalesFiguresResult;

import java.util.ArrayList;
import epc.epcsalesapi.sales.EpcDailySalesFiguresHandler;
import epc.epcsalesapi.sales.EpcDocumentHandler;
import epc.epcsalesapi.sales.EpcFulfillHandler;
import epc.epcsalesapi.sales.EpcAppleCareHandler;
//import java.sql.Connection;
//import javax.sql.DataSource;
import epc.epcsalesapi.helper.AppleCareConnectInterface;
import epc.epcsalesapi.helper.AppleCareConnectInterface.VerifyResult;
import epc.epcsalesapi.sales.bean.EpcGetCreditCardPrefix;
import epc.epcsalesapi.sales.bean.EpcGetCreditCardPrefixResult;
import org.springframework.format.annotation.DateTimeFormat;
// added by Danny Chan on 2021-2-8: end

//import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import epc.epcsalesapi.sales.EpcShkpHandler;
import epc.epcsalesapi.sales.EpcStaffOfferCompanyHandler;
import epc.epcsalesapi.sales.bean.EpcGetReserveItemListByOrderIdResult;
import epc.epcsalesapi.sales.bean.EpcGetStaffOfferCompanyResult;
import epc.epcsalesapi.sales.bean.EpcSendMsgRequest;
import epc.epcsalesapi.sales.bean.vms.order.VmsOrderVoucher;
import javax.sql.DataSource;
import epc.epcsalesapi.sales.EpcShortenUrlHandler;
import epc.epcsalesapi.sales.bean.EpcGenerateShortenUrl;
import fes.smcApps.SmcAppsSMCSENS;


/**
 * REST Web Service
 *
 * @author KerryTsang
 */

@RestController
@RequestMapping("/salesOrder")
public class EpcSalesService {

    private final Logger logger = LoggerFactory.getLogger(EpcSalesService.class);

    @Autowired
    private EpcOrderHandler epcOrderHandler;

    @Autowired
    private EpcInvoiceHandler epcInvoiceHandler;

    @Autowired
    private EpcQuoteHandler epcQuoteHandler;

    @Autowired
    private EpcReceiptHandler epcReceiptHandler;

    @Autowired
    private EpcPaymentHandler epcPaymentHandler;

    @Autowired
    private EpcOrderValidationHandler epcOrderValidationHandler;

    // added by Danny Chan on 2021-2-9: start
    @Autowired
    private EpcDailySalesFiguresHandler epcDailySalesFiguresHandler;

    @Autowired
    private EpcAppleCareHandler epcAppleCareHandler; // for testing only

    @Autowired
    private AppleCareConnectInterface appleCareConnect; // for testing only
    // added by Danny Chan on 2021-2-9: end

    @Autowired
    private EpcCustomerHandler epcCustomerHandler;

    @Autowired
    private EpcStockHandler epcStockHandler;

    @Autowired
    private EpcNotificationHandler epcNotificationHandler;

    @Autowired
    private EpcMsgHandler epcMsgHandler;

    @Autowired
    private EpcVoucherHandlerNew epcVoucherHandlerNew;

    @Autowired
    private EpcTradeInHandler epcTradeInHandler;

    @Autowired
    private EpcDocumentHandler epcDocumentHandler;

    /* added by Danny Chan on 2022-9-29: start */
    @Autowired
    private EpcShkpHandler epcShkpHandler;

    @Autowired
    private DataSource epcDataSource;
    /* added by Danny Chan on 2022-9-29: end */

    @Autowired
    private EpcCancelHandler epcCancelHandler;

    @Autowired
    private EpcPrintingHandler epcPrintingHandler;

    @Autowired
    private EpcStaffOfferCompanyHandler epcStaffOfferCompanyHandler;

    @Autowired
    private EpcRefundHandler epcRefundHandler;


    /* added by Danny Chan on 2023-3-25: start */
    @Autowired
    private EpcShortenUrlHandler epcShortenUrlHandler;
    /* added by Danny Chan on 2023-3-25: end */

    @Autowired
    private EpcFulfillHandler epcFulfillHandler;

    @Autowired
    private EpcPlaceOrderHandler epcPlaceOrderHandler;

    @Autowired
    private EpcSearchOrderHandler epcSearchOrderHandler;
    

    /**
     * Creates a new instance of EpcSalesService
     */
    public EpcSalesService() {
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcCreateOrderResult> createOrder(@RequestBody EpcCreateOrder epcCreateOrder) {
        EpcCreateOrderResult epcCreateOrderResult = epcOrderHandler.createOrder(epcCreateOrder);

        if ("SUCCESS".equals(epcCreateOrderResult.getResult())) {
            return new ResponseEntity<EpcCreateOrderResult>(epcCreateOrderResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcCreateOrderResult>(epcCreateOrderResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // added by Danny Chan on 2022-9-26: start
    @GetMapping(value = "/getReserveItems", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetReserveItemListByOrderIdResult> getReserveItemListByOrderId(
            @RequestParam("orderId") int orderId) {
        return new ResponseEntity<EpcGetReserveItemListByOrderIdResult>(
                epcOrderHandler.getReserveItemListByOrderId(orderId), HttpStatus.OK);
    }


    @GetMapping(value = "/slim", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcOrderInfo> getSlimOrder(@RequestParam("orderId") int orderId) {
        EpcOrderInfo epcOrderInfo = epcOrderHandler.getOrderSlimInfo(orderId);
        return new ResponseEntity<EpcOrderInfo>(epcOrderInfo, HttpStatus.OK);
    }

    @GetMapping(value = "/orderReference", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> getOrderReferenceByOrderId(@RequestParam("orderId") int orderId) {
        String orderReference = epcOrderHandler.getOrderReferenceByOrderId(orderId);
        return new ResponseEntity<String>(orderReference, HttpStatus.OK);
    }

    @PostMapping(value = "/confirm", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcConfirmOrderResult> confirmOrder(@RequestBody HashMap<String, Object> epcConfirmOrderMap) {
        EpcConfirmOrderResult epcConfirmOrderResult = epcOrderHandler.confirmOrder(epcConfirmOrderMap);
        if ("SUCCESS".equals(epcConfirmOrderResult.getResult())) {
            return new ResponseEntity<EpcConfirmOrderResult>(epcConfirmOrderResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcConfirmOrderResult>(epcConfirmOrderResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // added by Danny Chan on 2021-2-8: start
    @PostMapping(value = "/getDailySalesFigures", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetDailySalesFiguresResult> getDailySalesFigures(
            @RequestBody EpcGetDailySalesFigures epcGetDailySalesFigures) {

        EpcGetDailySalesFiguresResult result = epcDailySalesFiguresHandler
                .getDailySalesFigures(epcGetDailySalesFigures);

        if ("OK".equals(result.getResult())) {
            return new ResponseEntity<EpcGetDailySalesFiguresResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGetDailySalesFiguresResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // added by Danny Chan on 2021-2-8: end


    @GetMapping(value = "/orders", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetOrder> getOrders(@RequestParam("searchValue") String searchValue
    		,@RequestParam(name = "orderDateFrom", required = false)String orderDateFrom
    		,@RequestParam(name = "orderDateTo", required = false)String orderDateTo
    		,@RequestParam(name = "orderStatus", required = false)String orderStatus ) {
        return new ResponseEntity<>(epcSearchOrderHandler.searchOrders(searchValue,orderDateFrom,orderDateTo,orderStatus), HttpStatus.OK);
    }

    @GetMapping(value = "/customer/{custId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetOrder> getOrdersByCustId(
            @PathVariable("custId") String custId,
            @RequestParam(name = "withQuoteDetail", required = false) String withQuoteDetail,
            @RequestParam(name = "withCharge", required = false) String withCharge,
            @RequestParam(name = "orderTypes", required = false) String orderTypes,
            @RequestParam(name = "orderStatus", required = false) String orderStatus,
            @RequestParam(name = "orderDateFrom", required = false) String orderDateFrom, 
            @RequestParam(name = "orderDateTo", required = false) String orderDateTo) {
        boolean withQuoteDetailBoolean = false;
        if ("Y".equals(StringHelper.trim(withQuoteDetail))) {
            withQuoteDetailBoolean = true;
        }
        boolean withChargeBoolean = true;
        if ("N".equals(StringHelper.trim(withCharge))) {
            withChargeBoolean = false;
        } else {
            withChargeBoolean = true;
        }

        EpcGetOrder epcGetOrder = epcSearchOrderHandler.getOrdersWithQuoteDetail(
                custId, "", "", 0, withQuoteDetailBoolean,
                withChargeBoolean, orderTypes, orderStatus, "", false, withChargeBoolean,orderDateFrom,orderDateTo);

        if ("OK".equals(epcGetOrder.getStatus())) {
            return new ResponseEntity<>(epcGetOrder, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(epcGetOrder, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/orderReference/{orderReference}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetOrder> getOrdersByOrderReference(
            @PathVariable("orderReference") String orderReference,
            @RequestParam(name = "withQuoteDetail", required = false) String withQuoteDetail,
            @RequestParam(name = "withCharge", required = false) String withCharge,
            @RequestParam(name = "withRequiredItem", required = false) String withRequiredItem,
            @RequestParam(name = "refreshCharge", required = false) String refreshCharge,
            @RequestParam(name = "orderDateFrom", required = false) String orderDateFrom,
            @RequestParam(name = "orderDateTo", required = false)  String orderDateTo) {
        boolean withQuoteDetailBoolean = false;
        if ("Y".equals(StringHelper.trim(withQuoteDetail))) {
            withQuoteDetailBoolean = true;
        }
        boolean withChargeBoolean = true;
        if ("N".equals(StringHelper.trim(withCharge))) {
            withChargeBoolean = false;
        } else {
            withChargeBoolean = true;
        }
        boolean withRequiredItemBooean = false;
        if ("Y".equals(StringHelper.trim(withRequiredItem))) {
            withRequiredItemBooean = true;
        }
        boolean refreshChargeBoolean = true;
        if ("N".equals(StringHelper.trim(refreshCharge))) {
            refreshChargeBoolean = false;
        } else {
            refreshChargeBoolean = true;
        }
        EpcGetOrder epcGetOrder = epcSearchOrderHandler.getOrdersWithQuoteDetail("", orderReference, "", 0,
                withQuoteDetailBoolean, withChargeBoolean, "", "", "", withRequiredItemBooean, refreshChargeBoolean,orderDateFrom,orderDateTo);

        if ("OK".equals(epcGetOrder.getStatus())) {
            return new ResponseEntity<EpcGetOrder>(epcGetOrder, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGetOrder>(epcGetOrder, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/serialNumber/{serialNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetOrder> getOrdersByItemSerialNo(@PathVariable("serialNumber") String serialNumber) {
        String orderReference = "";
        EpcGetOrder epcGetOrder = null;

        orderReference = epcOrderHandler.getOrderReferenceByItemSerialNo(serialNumber);
        if (!"".equals(orderReference)) {
            epcGetOrder = epcSearchOrderHandler.getOrders("", orderReference, "", 0, false, false, "", "", "", "", "", false, false,"","");
            if ("OK".equals(epcGetOrder.getStatus())) {
                epcGetOrder.setStatus(null); // tmp
                return new ResponseEntity<EpcGetOrder>(epcGetOrder, HttpStatus.OK);
            } else {
                epcGetOrder.setStatus(null); // tmp
                return new ResponseEntity<EpcGetOrder>(epcGetOrder, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            epcGetOrder = new EpcGetOrder();
            epcGetOrder.setStatus("FAIL");
            epcGetOrder.setErrorCode("1005");
            epcGetOrder.setErrorMessage("No order is found by " + serialNumber);
            return new ResponseEntity<EpcGetOrder>(epcGetOrder, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/warehouseOrders", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetOrder> getWarehouseOrders() {
        EpcGetOrder epcGetOrder = epcSearchOrderHandler.getWarehouseOrders("WHS");

        if ("OK".equals(epcGetOrder.getStatus())) {
            return new ResponseEntity<EpcGetOrder>(epcGetOrder, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGetOrder>(epcGetOrder, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/subrKeys", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ArrayList<EpcSubr>> getSubrKeyList(@RequestParam("sigmaOrderId") int sigmaOrderId) {
        return new ResponseEntity<ArrayList<EpcSubr>>(epcOrderHandler.getSubrKeyList(sigmaOrderId), HttpStatus.OK);
    }


    @PostMapping(value = "/lock", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcLockOrderResult> lockOrder(@RequestBody EpcLockOrderResult epcLockOrderResult) {
        epcOrderHandler.lockOrder(epcLockOrderResult);
        return new ResponseEntity<>(epcLockOrderResult, HttpStatus.OK);
    }


    @GetMapping(value = "/validate", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcValidateOrderResult> validateOrder(@RequestParam("orderId") int orderId) {
        return new ResponseEntity<EpcValidateOrderResult>(epcOrderValidationHandler.validateOrder(orderId),
                HttpStatus.OK);
    }

    @PostMapping(value = "/placeOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcPlaceOrderResult> placeOrder(@RequestBody EpcPlaceOrder epcPlaceOrder) {
        EpcPlaceOrderResult epcPlaceOrderResult = epcPlaceOrderHandler.placeOrder(epcPlaceOrder);
        if ("OK".equals(epcPlaceOrderResult.getSaveStatus())) {
            return new ResponseEntity<EpcPlaceOrderResult>(epcPlaceOrderResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcPlaceOrderResult>(epcPlaceOrderResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @PostMapping(value = "/orderType", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcUpdateOrderType> updateOrderType(@RequestBody EpcUpdateOrderType epcUpdateOrderType) {
        epcOrderHandler.updateOrderType(epcUpdateOrderType);
        if ("SUCCESS".equals(epcUpdateOrderType.getResult())) {
            return new ResponseEntity<EpcUpdateOrderType>(epcUpdateOrderType, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcUpdateOrderType>(epcUpdateOrderType, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/fulfill", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcFulfillResult> fulfill(@RequestBody EpcFulfillOrder epcFulfillOrder) {
        EpcFulfillResult epcFulfillResult = epcFulfillHandler.fulfill(epcFulfillOrder);
        if ("SUCCESS".equals(epcFulfillResult.getResult())) {
            return new ResponseEntity<EpcFulfillResult>(epcFulfillResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcFulfillResult>(epcFulfillResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/invoice", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcCreateInvoiceResult> createInvoice(@RequestBody EpcCreateInvoice epcCreateInvoice) {
        EpcCreateInvoiceResult epcCreateInvoiceResult = epcInvoiceHandler.createInvoice(epcCreateInvoice);

        if ("SUCCESS".equals(epcCreateInvoiceResult.getResult())) {
            epcCreateInvoiceResult.setResult(null); // tmp
            return new ResponseEntity<EpcCreateInvoiceResult>(epcCreateInvoiceResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcCreateInvoiceResult>(epcCreateInvoiceResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/receipt", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcCreateReceiptResult> createReceipt(@RequestBody EpcCreateReceipt epcCreateReceipt) {
        EpcCreateReceiptResult epcCreateReceiptResult = epcReceiptHandler.createReceipt(epcCreateReceipt);

        if ("SUCCESS".equals(epcCreateReceiptResult.getResult())) {
            return new ResponseEntity<EpcCreateReceiptResult>(epcCreateReceiptResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcCreateReceiptResult>(epcCreateReceiptResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // @POST
    // @Path("/paymentInfo")
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    @PostMapping(value = "/paymentInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcCreatePayment> savePaymentInfo(@RequestBody EpcCreatePayment epcCreatePayment) {
        EpcCreatePayment epcCreatePayment2 = epcPaymentHandler.savePaymentInfo(epcCreatePayment);

        if ("OK".equals(epcCreatePayment2.getSaveStatus())) {
            return new ResponseEntity<EpcCreatePayment>(epcCreatePayment2, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcCreatePayment>(epcCreatePayment2, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // added by Danny Chan on 2022-3-3: start
    @DeleteMapping(value = "/deletePaymentInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EpcCreatePayment> deletePaymentInfo(@RequestBody EpcCreatePayment epcCreatePayment) {
        EpcCreatePayment epcCreatePayment2 = epcPaymentHandler.deletePaymentInfo(epcCreatePayment);

        if ("OK".equals(epcCreatePayment2.getSaveStatus())) {
            return new ResponseEntity<EpcCreatePayment>(epcCreatePayment2, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcCreatePayment>(epcCreatePayment2, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // added by Danny Chan on 2022-3-3: end

    @PostMapping(value = "/paymentInfo/init", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EpcInitPayment> initPaymentInfo(@RequestBody EpcInitPayment epcInitPayment) {
        EpcInitPayment epcInitPaymentResult = epcPaymentHandler.initPaymentInfo(epcInitPayment);

        if ("SUCCESS".equals(epcInitPaymentResult.getResultCode())) {
            return new ResponseEntity<EpcInitPayment>(epcInitPaymentResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcInitPayment>(epcInitPaymentResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/paymentInfo/settleRemainingCharge", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcSettlePayment> settleRemainingCharge(@RequestBody EpcSettlePayment epcSettlePayment) {
        epcPaymentHandler.settleRemainingCharges(epcSettlePayment);

        if ("SUCCESS".equals(epcSettlePayment.getResult())) {
            return new ResponseEntity<>(epcSettlePayment, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(epcSettlePayment, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/paymentInfo/settleExtensionCharge", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcSettleExtensionFee> settleExtensionCharge(
            @RequestBody EpcSettleExtensionFee epcSettleExtensionFee) {
        epcPaymentHandler.settleExtensionCharges(epcSettleExtensionFee);

        if ("SUCCESS".equals(epcSettleExtensionFee.getResult())) {
            return new ResponseEntity<>(epcSettleExtensionFee, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(epcSettleExtensionFee, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    // @POST
    // @Path("/verifyExistingMobile")
    // @Consumes(MediaType.APPLICATION_JSON)
    // @Produces(MediaType.APPLICATION_JSON)
    @PostMapping(value = "/verifyExistingMobile", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcVerifyExistingMobile> verifyExistingMobile(
            @RequestBody EpcVerifyExistingMobile epcVerifyExistingMobile) {
        EpcVerifyExistingMobile epcVerifyExistingMobile2 = epcOrderHandler
                .verifyExistingMobile(epcVerifyExistingMobile);

        if ("SUCCESS".equals(epcVerifyExistingMobile2.getResult())) {
            return new ResponseEntity<>(epcVerifyExistingMobile2, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(epcVerifyExistingMobile2, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/custVouchers", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ArrayList<VmsCustVoucher>> getCustAvailableVouchers(@RequestParam("orderId") int orderId,
            @RequestParam("custId") String custId) {
        ArrayList<VmsCustVoucher> voucherList = epcVoucherHandlerNew.getCustAvailableVouchers(custId, orderId);

        return new ResponseEntity<>(voucherList, HttpStatus.OK);
    }

    @GetMapping(value = "/vouchersInOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<ArrayList<VmsOrderVoucher>> getRedeemedVouchersInOrder(@RequestParam("orderId") int orderId,
            @RequestParam("custId") String custId) {
        ArrayList<VmsOrderVoucher> voucherList = epcVoucherHandlerNew.getRedeemedVouchersInOrder(custId, orderId);

        return new ResponseEntity<>(voucherList, HttpStatus.OK);
    }


    @GetMapping(value = "/dealerCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    // public Response getDealerCode() {
    public ResponseEntity<EpcGetDealerCode> getDealerCode() {
        EpcGetDealerCode epcGetDealerCode = epcOrderHandler.getDealerCode();
        // return Response.status(200).entity(epcGetDealerCode).build();
        return new ResponseEntity<EpcGetDealerCode>(epcGetDealerCode, HttpStatus.OK);
    }


    @PostMapping(value = "/charge", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetChargeResult> getCharge(@RequestBody EpcGetCharge epcGetCharge) {
        EpcGetChargeResult epcGetChargeResult = epcPaymentHandler.getChargeResult(epcGetCharge);

        if ("SUCCESS".equals(epcGetChargeResult.getResult())) {
            return new ResponseEntity<EpcGetChargeResult>(epcGetChargeResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGetChargeResult>(epcGetChargeResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/waiveCharge", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcWaiveChargeResult> waiveCharge(@RequestBody EpcWaiveCharge epcWaiveCharge) {
        EpcWaiveChargeResult epcWaiveChargeResult = epcPaymentHandler.waiveCharge(epcWaiveCharge);

        if ("SUCCESS".equals(epcWaiveChargeResult.getResultCode())) {
            return new ResponseEntity<EpcWaiveChargeResult>(epcWaiveChargeResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcWaiveChargeResult>(epcWaiveChargeResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/discountCharge", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcDiscountChargeResult> discountCharge(@RequestBody EpcDiscountCharge epcDiscountCharge) {
        EpcDiscountChargeResult epcDiscountChargeResult = epcPaymentHandler.discountCharge(epcDiscountCharge);

        if ("SUCCESS".equals(epcDiscountChargeResult.getResultCode())) {
            return new ResponseEntity<EpcDiscountChargeResult>(epcDiscountChargeResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcDiscountChargeResult>(epcDiscountChargeResult,
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/remainingCharge", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetRemainingCharge> getRemainingCharge(
            @RequestBody EpcGetRemainingCharge epcGetRemainingCharge) {
        epcPaymentHandler.getRemainingCharges(epcGetRemainingCharge);

        if ("SUCCESS".equals(epcGetRemainingCharge.getResult())) {
            return new ResponseEntity<>(epcGetRemainingCharge, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(epcGetRemainingCharge, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/paymentCode", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcPaymentCodeResult> getPaymentCode(
            @RequestParam(value = "locationCode", required = false) String locationCode) { // modified by Danny Chan on
                                                                                           // 2022-10-11 (nehancement of
                                                                                           // payment page to support
                                                                                           // SHKP): add a new request
                                                                                           // parameter locationCode
        EpcPaymentCodeResult epcGetPaymentCodeResult = epcPaymentHandler.getPaymentCodeResult(locationCode); // modified
                                                                                                             // by Danny
                                                                                                             // Chan on
                                                                                                             // 2022-10-11
                                                                                                             // (nehancement
                                                                                                             // of
                                                                                                             // payment
                                                                                                             // page to
                                                                                                             // support
                                                                                                             // SHKP):
                                                                                                             // add a
                                                                                                             // new
                                                                                                             // request
                                                                                                             // parameter
                                                                                                             // locationCode
        return new ResponseEntity<EpcPaymentCodeResult>(epcGetPaymentCodeResult, HttpStatus.OK);
    }

    // added by Danny chan on 2022-6-10: start
    @PostMapping(value = "/creditCardPrefix", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetCreditCardPrefixResult> getCreditCardPrefix(
            @RequestBody EpcGetCreditCardPrefix getCreditCardPrefix) {
        EpcGetCreditCardPrefixResult EpcGetCreditCardPrefixResult = epcPaymentHandler
                .getCreditCardPrefix(getCreditCardPrefix);
        return new ResponseEntity<EpcGetCreditCardPrefixResult>(EpcGetCreditCardPrefixResult, HttpStatus.OK);
    }
    // added by Danny chan on 2022-6-10: end

    @PostMapping(value = "/extensionFee", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcExtensionChargeResult> getExtensionFee(
            @RequestBody EpcExtensionCharge epcExtensionCharge) {
        EpcExtensionChargeResult epcExtensionChargeResult = epcPaymentHandler.getExtensionCharge(epcExtensionCharge);
        return new ResponseEntity<EpcExtensionChargeResult>(epcExtensionChargeResult, HttpStatus.OK);
    }

    @GetMapping(value = "/loginIds")
    @ResponseBody
    public ResponseEntity<String> getLoginIdByCustId(@RequestParam("custId") String custId) {
        return new ResponseEntity<String>(epcCustomerHandler.getLoginIdByCustId(custId), HttpStatus.OK);
    }

    @GetMapping(value = "/checkProduct")
    @ResponseBody
    public ResponseEntity<EpcCheckProduct> checkProduct(@RequestParam("value") String value) {
        return new ResponseEntity<EpcCheckProduct>(epcStockHandler.checkProduct(value), HttpStatus.OK);
    }

    @PostMapping(value = "/notification", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<EpcNotification>> sendNotification(@RequestBody List<EpcNotification> epcNotificationList) {
        epcNotificationHandler.sendNotification(epcNotificationList);

        return new ResponseEntity<>(epcNotificationList, HttpStatus.OK);
    }

    @PostMapping(value = "/sendMessage", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcNotificationMessage> sendMessage(
            @RequestBody EpcNotificationMessage epcNotificationMessage) {
        epcMsgHandler.createMsg(epcNotificationMessage);

        if ("SUCCESS".equals(epcNotificationMessage.getResult())) {
            return new ResponseEntity<EpcNotificationMessage>(epcNotificationMessage, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcNotificationMessage>(epcNotificationMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/sendShoppingBag", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcSendShoppingBag> sendShoppingBag(@RequestBody EpcSendShoppingBag epcSendShoppingBag) {
        epcMsgHandler.sendShoppingBag(epcSendShoppingBag);

        if ("SUCCESS".equals(epcSendShoppingBag.getResult())) {
            return new ResponseEntity<EpcSendShoppingBag>(epcSendShoppingBag, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcSendShoppingBag>(epcSendShoppingBag, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(value = "/offerSpec", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<HashMap<String, Object>> getSpec(@RequestParam("offerGuid") String offerGuid,
            @RequestParam("deviceGuid") String deviceGuid) {
        return new ResponseEntity<HashMap<String, Object>>(epcOrderHandler.getOfferSpec(offerGuid, deviceGuid),
                HttpStatus.OK);
    }

    @GetMapping(value = "/compiledSpec", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetSpec> getCompiledSpec(@RequestParam("productGuid") String productGuid) {
        EpcGetSpec epcGetSpec = new EpcGetSpec();
        epcGetSpec.setProductGuid(productGuid);
        epcOrderHandler.getCompiledSpec2(epcGetSpec);
        return new ResponseEntity<>(epcGetSpec, HttpStatus.OK);
    }

//    // for testing only
//    @GetMapping(value = "/testTimeout")
//    public ResponseEntity<String> testTimeout(@RequestParam("time") String timeStr) {
//        int defaultTime = 80;
//
//        try {
//            logger.info("[testTimeout] start");
//            if ("".equals(timeStr) || timeStr == null) {
//                TimeUnit.SECONDS.sleep(defaultTime);
//            } else {
//                TimeUnit.SECONDS.sleep(Integer.parseInt(timeStr));
//            }
//            logger.info("[testTimeout] end");
//        } catch (InterruptedException ie) {
//            ie.printStackTrace();
//        }
//        return new ResponseEntity<String>("test timeout", HttpStatus.OK);
//    }

//    // added by Danny Chan on 2021-6-29 (for testing only): start
//    @GetMapping(value = "/testAppleCare", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public ResponseEntity<EpcCreateInvoiceResult> getTestAppleCarfe() {
//
//        // Connection fesConn = null;
//
//        EpcCreateInvoiceResult result = epcAppleCareHandler.getTestAppleCare();
//        return new ResponseEntity<EpcCreateInvoiceResult>(result, HttpStatus.OK);
//    }

    @PostMapping(value = "/verifyAppleCare", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<String> verifyAppleCare(@RequestParam("invoiceNo") String invoiceNo,
            @RequestParam("imei") String imei, @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName, @RequestParam("emailAddress") String emailAddress,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX") java.util.Date dateOfPurchase,
            @RequestParam("purchaseMode") char purchaseMode,
            @RequestParam("pocDeliveryPreference") char pocDeliveryPreference,
            @RequestParam("pocLanguage") char pocLanguage) {

        String result = "SUCCESS";

        VerifyResult verify_result = null;

        // System.out.println( "dateOfPurchase = " + dateOfPurchase );
        // System.out.println( "pocDeliveryPreference = " + pocDeliveryPreference );
        // System.out.println( "pocLanguage = " + pocLanguage );
        logger.info("dateOfPurchase = " + dateOfPurchase);
        logger.info("pocDeliveryPreference = " + pocDeliveryPreference);
        logger.info("pocLanguage = " + pocLanguage);

        try {
            verify_result = appleCareConnect.verifyOrder(purchaseMode, invoiceNo, imei, firstName, lastName,
                    emailAddress, dateOfPurchase, pocDeliveryPreference, pocLanguage);

            result = verify_result.getErrorCode() + " # " + verify_result.getErrorMessage();
        } catch (Exception e) {
            result = e.toString();
        }

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
    // added by Danny Chan on 2021-6-29 (for testing only): end

    // added by Danny Chan on 2022-10-11 (enhancement of payment page to support
    // SHKP): start
    /*
     * @PutMapping(
     * value = "/initSHKPPayment",
     * produces = MediaType.APPLICATION_JSON_VALUE
     * )
     * public ResponseEntity<String> initSHKPPayment( @RequestBody
     * ArrayList<HashMap> epcInitSHKPPaymentMap ) {
     * return new
     * ResponseEntity<String>(epcShkpHandler.initSHKPPayment(epcInitSHKPPaymentMap),
     * HttpStatus.OK);
     * }
     * 
     * @GetMapping(
     * value = "/getSHKPPayment",
     * produces = MediaType.APPLICATION_JSON_VALUE
     * )
     * public ResponseEntity<String> getSHKPPayment( @RequestParam String batchno )
     * {
     * logger.info("@@@batchno = " + batchno);
     * return new ResponseEntity<String>(epcShkpHandler.getSHKPPayment(batchno),
     * HttpStatus.OK);
     * }
     * 
     * @PostMapping(
     * value = "/refundSHKPDollars",
     * produces = MediaType.APPLICATION_JSON_VALUE
     * )
     * public ResponseEntity<String> refundSHKPDollars( @RequestBody HashMap
     * epcRefundShkpPDollarsMap ) {
     * return new ResponseEntity<String>(epcShkpHandler.refundSHKPDollars(
     * epcRefundShkpPDollarsMap), HttpStatus.OK);
     * }
     * 
     * @PostMapping(
     * value = "/updateShkpReceiptNo",
     * produces = MediaType.APPLICATION_JSON_VALUE
     * )
     * public ResponseEntity<String> updateSHKPPayment( @RequestBody HashMap
     * epcUpdateSHKPPaymentMap ) {
     * return new ResponseEntity<String>(epcShkpHandler.updateShkpReceiptNo(
     * epcUpdateSHKPPaymentMap), HttpStatus.OK);
     * }
     */

    @PostMapping(value = "/sendMsg", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> sendMessage(@RequestBody EpcSendMsgRequest request) {
        return new ResponseEntity<String>(epcMsgHandler.sendMsg(request), HttpStatus.OK);
    }
    // added by Danny Chan on 2022-10-11 (enhancement of payment page to support
    // SHKP): end

    // added by Danny Chan on 2022-8-31 (for testing only: SHK point dollar): start
    @PostMapping(value = "/createSHKDollarPoint", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createSHKDollarPoint(@RequestParam("numOfPoints") String numOfPoints) {
        String result = "{}";

        try {
            Proxy proxy = new Proxy(Type.HTTP, new InetSocketAddress("ispxyp01", 443));
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setProxy(proxy);

            RestTemplate restTemplate = new RestTemplate(requestFactory);

            String request_json = "{\n" +
                    "  \"amount\": " + numOfPoints + "\n" +
                    "}";

            System.out.println("request_json = " + request_json);

            HttpHeaders headers = new HttpHeaders();

            headers.add("x-api-key", "5450ed58-9313-4cff-acec-91d6d4e13ddb");
            headers.add("x-api-token", "CXKaQSWSxu4yk31GGCw6bkCQ40H66eKA");
            headers.add("accept", "application/json");
            headers.add("Content-Type", "application/json");

            HttpEntity<String> request = new HttpEntity<String>(request_json, headers);

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(
                    "https://uat-thepoint-api.uatgslb.shkp.com/coupon-api/dollar/create", request, String.class);

            System.out.println("response status code = " + responseEntity.getStatusCode());

            if (responseEntity.getStatusCode().value() == 200) {
                result = responseEntity.getBody();
            } else {
                result = "{\"error\": \"The status code returned is " + responseEntity.getStatusCode().value() + "\"}";
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            result = "{\"error\": \"" + e.toString() + "\"}";
            e.printStackTrace();
        } finally {
        }

        return new ResponseEntity<String>(result, HttpStatus.OK);
    }
    // added by Danny Chan on 2022-8-31 (for testing only: SHK point dollar): end

    @GetMapping(value = "/tradein/{referenceNo}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetTradeInResult> getTradein(@PathVariable("referenceNo") String referenceNo) {
        EpcGetTradeInResult epcGetTradeIn = epcTradeInHandler.getTradeIn(referenceNo);
        if ("SUCCESS".equals(epcGetTradeIn.getResult())) {
            return new ResponseEntity<EpcGetTradeInResult>(epcGetTradeIn, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGetTradeInResult>(epcGetTradeIn, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 20220721-Ted Kwan: API to save delivery note to EPC_ORDER_ATTACH
    /**
     * Method: saveDeliveryNote()
     * Return: ResponseEntity<EpcOrderAttachResponse>
     * Description: API to save delivery note
     * 
     * @param file    MultipartFile File from form submission
     * @param custId  String
     * @param orderId Integer
     * @return ResponseEntity<EpcOrderAttachResponse>
     */
    @PostMapping(value = "/saveDeliveryNote", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcOrderAttachResponse> saveDeliveryNote(@RequestParam("file") MultipartFile file,
            @RequestParam("custId") String custId, @RequestParam("orderId") Integer orderId) {

        EpcOrderAttachResponse result = new EpcOrderAttachResponse();
        result.setCustId(custId);
        result.setOrderId(orderId);

        if (custId == null || custId.trim().equals("") || orderId == null || orderId.intValue() <= 0) {
            result.setResult("Save file failed.");
            result.setErrMsg("Missing custId or orderId.");
            return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        if (file == null) {
            result.setResult("Save file failed.");
            result.setErrMsg("No file exist");
            return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            epcDocumentHandler.saveDeliverNote(custId, orderId, file.getInputStream().readAllBytes());
            result.setResult("Save file success.");

            return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.OK);
        } catch (Exception e) {
            result.setResult("Save file failed.");
            result.setErrMsg("" + e);
            return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PostMapping(value = "/saveDoc", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcOrderAttachResponse> saveDoc(@RequestParam("file") MultipartFile file,
            @RequestParam("custId") String custId, @RequestParam("orderId") Integer orderId,
            @RequestParam("attachType") String attachType) {

        EpcOrderAttachResponse result = new EpcOrderAttachResponse();
        result.setCustId(custId);
        result.setOrderId(orderId);

        if (custId == null || custId.trim().equals("") || orderId == null || orderId.intValue() <= 0) {
            result.setResult("Save file failed.");
            result.setErrMsg("Missing custId or orderId.");
            return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        if (file == null) {
            result.setResult("Save file failed.");
            result.setErrMsg("No file exist");
            return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            epcDocumentHandler.saveDoc(custId, orderId, attachType, file.getInputStream().readAllBytes());
            result.setResult("Save file success.");

            return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.OK);
        } catch (Exception e) {
            result.setResult("Save file failed.");
            result.setErrMsg("" + e);
            return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // 20220721-Ted Kwan: API to get saved document from EPC_ORDER_ATTACH
    /**
     * Method: getOrderAttachFile()
     * Return: ResponseEntity<byte[]>
     * Description: API to get saved document
     * 
     * @param recId Integer
     * @return ResponseEntity<byte[]>
     */
    @GetMapping(value = "/getOrderAttachFile/{recId}")
    @ResponseBody
    public ResponseEntity<byte[]> getOrderAttachFile(@PathVariable("recId") Integer recId,
            @RequestParam(value = "download", required = false, defaultValue = "false") boolean download) {

        if (recId == null || recId.intValue() <= 0) {
            String errResponse = "{'result':'Retrieve file failed.','errMsg':'Invalid recId'}";
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).contentType(MediaType.APPLICATION_JSON)
                    .body(errResponse.getBytes());
        }

        try {
            EpcOrderAttach epcOrderAttach = epcDocumentHandler.getOrderFileByRecId(recId);
            epcOrderAttach.getATTACH_CONTENT();
            byte[] fileBytes = epcOrderAttach.getATTACH_CONTENT().getBytes(1l,
                    (int) epcOrderAttach.getATTACH_CONTENT().length());
            epcOrderAttach.getATTACH_CONTENT().free();

            MediaType contentType = MediaType.parseMediaType(epcOrderAttach.getCONTENT_TYPE());
            if (contentType == null) {
                contentType = MediaType.APPLICATION_PDF;
            }

            String filename = epcOrderAttach.getCUST_ID() + "_" + epcOrderAttach.getORDER_ID() + "("
                    + epcOrderAttach.getREC_ID() + ").pdf";

            HttpHeaders headers = new HttpHeaders();

            // Download file header
            if (download) {
                contentType = MediaType.APPLICATION_OCTET_STREAM;
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename);
                headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
                headers.add(HttpHeaders.PRAGMA, "no-cache");
                headers.add(HttpHeaders.EXPIRES, "0");
            }
            // Open in tab header
            if (!download) {
                headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline;filename=" + filename);
                headers.add(HttpHeaders.CACHE_CONTROL, "must-revalidate, post-check=0, pre-check=0");
            }

            return ResponseEntity.ok()
                    .contentLength(fileBytes.length)
                    .contentType(contentType)
                    .headers(headers)
                    .body(fileBytes);
        } catch (Exception e) {
            logger.error("", e);
            String errResponse = "{'result':'Retrieve file failed.','errMsg':'" + e.getMessage() + "'}";
            return ResponseEntity.internalServerError().contentType(MediaType.APPLICATION_JSON)
                    .body(errResponse.getBytes());
        }

    }

    // 20220721-Ted Kwan: API to get saved document list from EPC_ORDER_ATTACH
    /**
     * Method: getOrderAttachList(String custId, Integer orderId)
     * Return: ResponseEntity<EpcOrderAttachResponse>
     * Description: API to get saved document list. If custId not empty, search by
     * custId. Else, search by orderId.
     * 
     * @param custId  String
     * @param orderId Integer
     * @return ResponseEntity<EpcOrderAttachResponse>
     */
    @GetMapping(value = "/getOrderAttachList", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcOrderAttachResponse> getOrderAttachList(
            @RequestParam(value = "custId", required = false) String custId,
            @RequestParam(value = "orderId", required = false) Integer orderId) {

        EpcOrderAttachResponse result = new EpcOrderAttachResponse();
        result.setCustId(custId);
        result.setOrderId(orderId);

        try {

            if (custId != null && !custId.trim().equals("")) {
                result.setEpcOrderAttach(epcDocumentHandler.getOrderFileListByCustId(custId));
                result.setResult("Search by custId success.");
                return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.OK);
            }

            if (orderId != null && orderId.intValue() > 0) {
                result.setEpcOrderAttach(epcDocumentHandler.getOrderFileListByOrderId(orderId, false));
                result.setResult("Search by orderId success.");
                return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.OK);
            }

        } catch (Exception e) {
            logger.error("", e);
            result.setErrMsg("Search failed. " + e.getMessage());
            return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }

        result.setResult("No record found.");
        return new ResponseEntity<EpcOrderAttachResponse>(result, HttpStatus.OK);
    }

    @GetMapping(value = "/getStaffOfferCompanyList", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetStaffOfferCompanyResult> getStaffOfferCompanyList() {

        EpcGetStaffOfferCompanyResult result = new EpcGetStaffOfferCompanyResult();

        result.setStaffOfferCompanies(epcStaffOfferCompanyHandler.getAllStaffOfferCompanies());
        result.setErrMsg("");
        result.setResult("SUCCESS");

        return new ResponseEntity<EpcGetStaffOfferCompanyResult>(result, HttpStatus.OK);
    }

//    @PostMapping(value = "/cancelOrder", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public EpcCancelOrder cancelOrder(@RequestBody EpcCancelOrder epcCancelOrder) {
//        epcCancelHandler.cancelOrder(epcCancelOrder);
//        return epcCancelOrder;
//    }

//    @PostMapping(value = "/cancelOrder/cancelAmount", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public EpcCancelAmount getCancelAmount(@RequestBody EpcCancelAmount epcCancelAmount) {
//        BigDecimal cancelAmount = epcCancelHandler.getCancelAmount(epcCancelAmount.getOrderId(),
//                epcCancelAmount.getCaseIdList());
//
//        epcCancelAmount.setCancelAmount(cancelAmount);
//        epcCancelAmount.setResult("SUCCESS");
//
//        return epcCancelAmount;
//    }

//    @GetMapping(value = "/cancelReceipt/{custId}")
//    @ResponseBody
//    public ArrayList<EpcCancelReceipt> getAvailableCancelReceipt(@PathVariable("custId") String custId) throws Exception {
//    	ArrayList<EpcCancelReceipt> list=epcCancelHandler.getAvailableCancelReceipt(custId,AvailableCancelReceiptType.refund);
//    	for (EpcCancelReceipt epcCancelReceipt : list) {
//    		List<EpcPayment> paymentList = epcPaymentHandler.getPaymentList(epcCancelReceipt.getOrderNo());
//    		epcCancelReceipt.setPaymentList(paymentList);
//		}
//        return list;
//    }


    /**
     * updateReservedItems
     * To update reserved items by order id and item id
     */
    @PostMapping(value = "/updateReservedItems", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGeneralResponse> updateReservedItems(@RequestBody UpdateReservedItemsRequest updateRequest) {
        String rtn = epcOrderHandler.updateReservedItemsReservedType(updateRequest);
        EpcGeneralResponse response = new EpcGeneralResponse();
        response.setResult(rtn);

        if (EpcApiStatusReturn.RETURN_SUCCESS.equals(rtn)) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/printQueue/{location}")
    @ResponseBody
    public ArrayList<String> getPrintQueue(@PathVariable("location") String location) {
        return epcPrintingHandler.getDefaultPrintQueue(location);
    }

    @PostMapping(value = "/printTransferNotes")
    @ResponseBody
    public EpcPrintTransferNotes printTransferNotes(@RequestBody EpcPrintTransferNotes epcPrintTransferNotes) {
        epcPrintingHandler.printTransferNotes(epcPrintTransferNotes);
        return epcPrintTransferNotes;
    }

    // added by Danny Chan on 2023-3-25: start
    @PostMapping(value = "/generateShortenUrl", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcGenerateShortenUrl generateShortenUrl(@RequestBody EpcGenerateShortenUrl epcGenerateShortenUrl) {
        String encrypted_orderId = null;

        try {
            encrypted_orderId = SmcAppsSMCSENS.eGet(String.valueOf(epcGenerateShortenUrl.getOrderId()), "utf-8");
        } catch (Exception e) {
            epcGenerateShortenUrl.setResult("FAIL");
            epcGenerateShortenUrl.setErrMsg("Error in encrypting order_id");
            return epcGenerateShortenUrl;
        }

        String encrypted_custId = null;

        try {
            encrypted_custId = SmcAppsSMCSENS.eGet(epcGenerateShortenUrl.getCustId(), "utf-8");
        } catch (Exception e) {
            epcGenerateShortenUrl.setResult("FAIL");
            epcGenerateShortenUrl.setErrMsg("Error in encrypting cust_id");
            return epcGenerateShortenUrl;
        }

        String encrypted_timestamp = null;

        try {
            encrypted_timestamp = SmcAppsSMCSENS.eGet(String.valueOf(System.currentTimeMillis()));
        } catch (Exception e) {
            epcGenerateShortenUrl.setResult("FAIL");
            epcGenerateShortenUrl.setErrMsg("Error in encrypting timestamp");
            return epcGenerateShortenUrl;
        }

        String shortenUrl = epcShortenUrlHandler.generateShortenUrl(epcGenerateShortenUrl.getInputUrl() +
                "?order_id=" + encrypted_orderId +
                "&cust_id=" + encrypted_custId +
                "&t=" + encrypted_timestamp);

        epcGenerateShortenUrl.setCustId(null);
        epcGenerateShortenUrl.setInputUrl(null);

        if (shortenUrl == null || shortenUrl.equals("N/A")) {
            epcGenerateShortenUrl.setResult("FAIL");
            epcGenerateShortenUrl.setErrMsg("Shorten URL is N/A");
        } else {
            epcGenerateShortenUrl.setResult("SUCCESS");
            epcGenerateShortenUrl.setShortenUrl(shortenUrl);
        }

        return epcGenerateShortenUrl;
    }
    // added by Danny Chan on 2023-3-25: end

    @PostMapping(value = "/warehouseOrders/proceed")
    @ResponseBody
    public EpcProceedOrder proceedOrder(@RequestBody EpcProceedOrder epcProceedOrder) {
        epcOrderHandler.proceedOrder(epcProceedOrder);
        return epcProceedOrder;
    }

    @PostMapping(value = "/refundCancelOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcCommonAPIResponse> refundCancelOrder(@RequestBody RefundRequest refundRequest) {
        EpcCommonAPIResponse result = new EpcCommonAPIResponse();
        try {
            epcRefundHandler.refundCancelOrder(refundRequest);
            result.setResult("SUCCESS");
            return new ResponseEntity<EpcCommonAPIResponse>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("", e);
            result.setResult("FAIL");
            result.setErrMsg("Save Refund Case failed. " + e.getMessage());
            return new ResponseEntity<EpcCommonAPIResponse>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @PutMapping(value = "/getRefundReferenceNo")
    public ResponseEntity<EpcCommonAPIResponse> getRefundReferenceNo() {
        EpcCommonAPIResponse result = new EpcCommonAPIResponse();
        try {
            result.setResult(epcRefundHandler.getRefundReferenceNo());
            return new ResponseEntity<EpcCommonAPIResponse>(result, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("", e);
            result.setErrMsg("Get RefundReferenceNo Case failed. " + e.getMessage());
            return new ResponseEntity<EpcCommonAPIResponse>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping(value = "/refundCase/{refundReference}")
    @ResponseBody
    public EpcRefundRecord getRefundCase(@PathVariable("refundReference") String refundReference) {
        return epcRefundHandler.getRefundRecord(refundReference);
    }

    @PostMapping(value = "/refundCase")
    @ResponseBody
    public EpcRefundRecord updateRefundCase(@RequestBody EpcRefundRecord epcRefundRecord) {
        epcRefundHandler.updateRefundRecord(epcRefundRecord);
        return epcRefundRecord;
    }

}
