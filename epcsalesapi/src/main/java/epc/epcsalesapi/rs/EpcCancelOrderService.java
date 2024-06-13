package epc.epcsalesapi.rs;


import epc.epcsalesapi.sales.EpcCancelHandler;
import epc.epcsalesapi.sales.EpcOrderHandler;
import epc.epcsalesapi.sales.EpcPaymentHandler;
import epc.epcsalesapi.sales.EpcVoucherHandlerNew;
import epc.epcsalesapi.sales.bean.AvailableCancelReceiptType;
import epc.epcsalesapi.sales.bean.EpcCancelReceipt;
import epc.epcsalesapi.sales.bean.EpcPayment;
import epc.epcsalesapi.sales.bean.cancelOrder.EpcCancelAmount;
import epc.epcsalesapi.sales.bean.cancelOrder.EpcCancelOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/salesOrder")
public class EpcCancelOrderService {
    @Autowired
    private EpcCancelHandler epcCancelHandler;

    @Autowired
    private EpcPaymentHandler epcPaymentHandler;
    
    @Autowired
    private EpcVoucherHandlerNew epcVoucherHandlerNew;
    
    @Autowired
    private EpcOrderHandler epcOrderHandler;
    
    @PostMapping(value = "/cancelOrder", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcCancelOrder cancelOrder(@RequestBody EpcCancelOrder epcCancelOrder) {
        epcCancelHandler.cancelOrder(epcCancelOrder);
        return epcCancelOrder;
    }

    @PostMapping(value = "/cancelOrder/cancelAmount", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcCancelAmount getCancelAmount(@RequestBody EpcCancelAmount epcCancelAmount) {
    	try {
    		BigDecimal cancelAmount = epcCancelHandler.getCancelAmount(
    				epcCancelAmount.getOrderId(),epcCancelAmount.getCaseIdList());
            epcCancelAmount.setVoucherAmount(
            		epcVoucherHandlerNew.getVoucherAmount(epcCancelAmount.getOrderId()));
            epcCancelAmount.setCourierFee(
            		epcOrderHandler.getCourierFee(epcCancelAmount.getOrderId()));
            epcCancelAmount.setCancelAmount(cancelAmount);
            epcCancelAmount.setResult("SUCCESS");
		} catch (Exception e) {
			epcCancelAmount.setResult("ERROR");
			epcCancelAmount.setErrMsg(e.getMessage());
		}
        
        return epcCancelAmount;
    }

	
    @PostMapping("/cancelReceipt/{custId}")
    @ResponseBody
    public ArrayList<EpcCancelReceipt> getAvailableCancelReceipt(
    		@PathVariable(value = "custId",required = true) String custId,
            @RequestBody(required = false) AvailableCancelReceiptType availableCancelReceiptType) throws Exception {
    	if(availableCancelReceiptType==null)
    		availableCancelReceiptType=AvailableCancelReceiptType.refund;
    	
        ArrayList<EpcCancelReceipt> list= epcCancelHandler.getAvailableCancelReceipt(custId, availableCancelReceiptType);

        if(list!=null)
        for (EpcCancelReceipt epcCancelReceipt : list) {
            List<EpcPayment> paymentList = epcPaymentHandler.getPaymentList(epcCancelReceipt.getOrderNo());
            epcCancelReceipt.setPaymentList(paymentList);
        }
        return list;
    }

    @GetMapping(value = "/cancelOrder/checkIfTradeInPaymentExistsNotVoid")
    public Boolean checkIfTradeInPaymentExistsNotVoid(String orderReference) {
        return epcCancelHandler.checkIfTradeInPaymentExistsNotVoid(orderReference);
    }


}
