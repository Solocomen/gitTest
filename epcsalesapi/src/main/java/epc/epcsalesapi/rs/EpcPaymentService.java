package epc.epcsalesapi.rs;

import epc.epcsalesapi.sales.EpcPaymentHandler;
import epc.epcsalesapi.sales.EpcValidatePaymentHandler;
import epc.epcsalesapi.sales.bean.EpcGetDefaultPayment;
import epc.epcsalesapi.sales.bean.EpcGetDefaultPaymentResult;
import epc.epcsalesapi.sales.bean.EpcValidatePayment;
import epc.epcsalesapi.sales.bean.EpcValidatePaymentResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payment")
public class EpcPaymentService {
    
    @Autowired
    private EpcPaymentHandler epcPaymentHandler;
	
	@Autowired
	private EpcValidatePaymentHandler epcValidatePaymentHandler;
    
    @PostMapping(
        value = "/defaultPayment", 
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcGetDefaultPaymentResult> getDefaultPayment(@RequestBody EpcGetDefaultPayment epcGetDefaultPayment) {
        EpcGetDefaultPaymentResult result = epcPaymentHandler.getDefaultPaymentResult(epcGetDefaultPayment);
        if("SUCCESS".equals(result.getResult())) {
            return new ResponseEntity<EpcGetDefaultPaymentResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGetDefaultPaymentResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(
            value = "/validatePayment", 
            produces = MediaType.APPLICATION_JSON_VALUE
        )
        @ResponseBody
        public ResponseEntity<EpcValidatePaymentResult> validatePayment(@RequestBody EpcValidatePayment epcValidatePayment) {
            EpcValidatePaymentResult result = epcValidatePaymentHandler.validatePayment(epcValidatePayment);
            if("SUCCESS".equals(result.getResult())) {
                return new ResponseEntity<EpcValidatePaymentResult>(result, HttpStatus.OK);
            } else {
                return new ResponseEntity<EpcValidatePaymentResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
}
