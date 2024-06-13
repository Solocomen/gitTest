package epc.epcsalesapi.rs;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.sales.EpcVoidOrderHandlerNew;
import epc.epcsalesapi.sales.bean.EpcVoidOrder;

@RestController
@RequestMapping("/salesOrder/void")
public class EpcVoidOrderService {
    private final Logger logger = LoggerFactory.getLogger(EpcVoidOrderService.class);

    private final EpcVoidOrderHandlerNew epcVoidOrderHandlerNew;

    public EpcVoidOrderService(EpcVoidOrderHandlerNew epcVoidOrderHandlerNew) {
        this.epcVoidOrderHandlerNew = epcVoidOrderHandlerNew;
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcVoidOrder voidOrder(@RequestBody EpcVoidOrder epcVoidOrder) {
        epcVoidOrderHandlerNew.voidOrder(epcVoidOrder);
        return epcVoidOrder;
    }
    
}
