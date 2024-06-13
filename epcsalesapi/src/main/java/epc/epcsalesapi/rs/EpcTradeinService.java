package epc.epcsalesapi.rs;

import epc.epcsalesapi.sales.EpcTradeInHandler;
import epc.epcsalesapi.sales.bean.EpcGetTradeInResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import epc.epcsalesapi.sales.bean.EpcGetValidOfferResult;     // added by Danny Chan on 2023-12-8 (create a new api getting valid offer(s) under an order for later trade-in)
import epc.epcsalesapi.sales.bean.EpcValidateTradeInResult;

@RestController
@RequestMapping("/tradein")
public class EpcTradeinService {
    
    @Autowired
    private EpcTradeInHandler epcTradeInHandler;
    
    @GetMapping(
        value = "/{referenceNo}", 
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcGetTradeInResult> getOrders(@PathVariable("referenceNo") String referenceNo) {
    EpcGetTradeInResult epcGetTradeIn = epcTradeInHandler.getTradeIn(referenceNo);
        if("SUCCESS".equals(epcGetTradeIn.getResult())) {
            return new ResponseEntity<EpcGetTradeInResult>(epcGetTradeIn, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGetTradeInResult>(epcGetTradeIn, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // added by Danny Chan on 2023-12-8 (create a new api getting valid offer(s) under an order for later trade-in): start
    @GetMapping(
        value = "/getValidOffers/{orderReference}", 
        produces = MediaType.APPLICATION_JSON_VALUE
    )	
    @ResponseBody
    public ResponseEntity<EpcGetValidOfferResult> getValidOffers(@PathVariable("orderReference") String orderReference) {
        EpcGetValidOfferResult result = epcTradeInHandler.getValidOffer(orderReference);
        if("SUCCESS".equals(result.getResult())) {
            return new ResponseEntity<EpcGetValidOfferResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGetValidOfferResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // added by Danny Chan on 2023-12-8 (create a new api getting valid offer(s) under an order for later trade-in): end
    
    @GetMapping(
        value = "/validate/{referenceNo}", 
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcValidateTradeInResult> validateEPC(@PathVariable("referenceNo") String referenceNo) {
    	EpcValidateTradeInResult epcGetTradeIn = epcTradeInHandler.validateInvoiceNo(referenceNo);
        if("SUCCESS".equals(epcGetTradeIn.getResult())) {
            return new ResponseEntity<EpcValidateTradeInResult>(epcGetTradeIn, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcValidateTradeInResult>(epcGetTradeIn, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
