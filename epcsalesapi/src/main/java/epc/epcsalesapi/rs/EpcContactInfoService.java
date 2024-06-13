package epc.epcsalesapi.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.sales.EpcContactInfoHandler;
import epc.epcsalesapi.sales.bean.EpcOrderContact;
import epc.epcsalesapi.sales.bean.EpcUpdateOrderContact;

@RestController
@RequestMapping("/salesOrder/contactInfo")
public class EpcContactInfoService {
    private final Logger logger = LoggerFactory.getLogger(EpcContactInfoService.class);

    private EpcContactInfoHandler epcContactInfoHandler;

    public EpcContactInfoService(EpcContactInfoHandler epcContactInfoHandler) {
        this.epcContactInfoHandler = epcContactInfoHandler;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcOrderContact> saveContactInfo(@RequestBody EpcOrderContact epcOrderContact) {
        return new ResponseEntity<EpcOrderContact>(epcContactInfoHandler.saveContactInfo(epcOrderContact), HttpStatus.OK);
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcOrderContact> getContactInfo(
        @RequestParam("custId") String custId,
        @RequestParam("orderId") int orderId,
        @RequestParam(name = "masked", required = false) String masked
    ) {
        return new ResponseEntity<EpcOrderContact>(epcContactInfoHandler.getContactInfo(custId, orderId, masked), HttpStatus.OK);
    }
    
    @PostMapping(value = "/update", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcUpdateOrderContact> updateContactInfo(@RequestBody EpcUpdateOrderContact updateOrderContact) {
    	EpcUpdateOrderContact epcUpdateResult = epcContactInfoHandler.updateContact(updateOrderContact);
        if ("SUCCESS".equals(epcUpdateResult.getResult())) {
            return new ResponseEntity<EpcUpdateOrderContact>(epcUpdateResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcUpdateOrderContact>(epcUpdateResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
