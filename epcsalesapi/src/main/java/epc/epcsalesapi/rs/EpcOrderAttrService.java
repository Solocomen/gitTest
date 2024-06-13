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

import epc.epcsalesapi.sales.EpcOrderHandler;
import epc.epcsalesapi.sales.bean.EpcAddOrderAttrs;

@RestController
@RequestMapping("/salesOrder/attributes")
public class EpcOrderAttrService {
    private final Logger logger = LoggerFactory.getLogger(EpcOrderAttrService.class);

    private final EpcOrderHandler epcOrderHandler;

    public EpcOrderAttrService(EpcOrderHandler epcOrderHandler) {
        this.epcOrderHandler = epcOrderHandler;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcAddOrderAttrs> addOrderAttrs(@RequestBody EpcAddOrderAttrs epcAddOrderAttrs) {
        epcOrderHandler.addOrderAttrs(epcAddOrderAttrs);

        if ("SUCCESS".equals(epcAddOrderAttrs.getResult())) {
            return new ResponseEntity<EpcAddOrderAttrs>(epcAddOrderAttrs, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcAddOrderAttrs>(epcAddOrderAttrs, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcAddOrderAttrs> getOrderAttrs(@RequestParam("orderId") int orderId,
            @RequestParam("custId") String custId) {
        EpcAddOrderAttrs epcAddOrderAttrs = new EpcAddOrderAttrs();
        epcAddOrderAttrs.setCustId(custId);
        epcAddOrderAttrs.setOrderId(orderId);
        epcOrderHandler.getOrderAttrs(epcAddOrderAttrs);

        if ("SUCCESS".equals(epcAddOrderAttrs.getResult())) {
            return new ResponseEntity<EpcAddOrderAttrs>(epcAddOrderAttrs, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcAddOrderAttrs>(epcAddOrderAttrs, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
