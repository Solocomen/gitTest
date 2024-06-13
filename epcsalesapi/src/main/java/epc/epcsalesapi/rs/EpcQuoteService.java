package epc.epcsalesapi.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.sales.EpcOrderHandler;
import epc.epcsalesapi.sales.EpcQuoteHandler;
import epc.epcsalesapi.sales.EpcTransferQuoteHandler;
import epc.epcsalesapi.sales.bean.EpcDeleteQuoteFromOrder;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcTransferOrder;

@RestController
@RequestMapping("/salesOrder/quote")
public class EpcQuoteService {
    private final Logger logger = LoggerFactory.getLogger(EpcQuoteService.class);

    private EpcOrderHandler epcOrderHandler;
    private EpcQuoteHandler epcQuoteHandler;
    private EpcTransferQuoteHandler epcTransferQuoteHandler;
    
    public EpcQuoteService(EpcOrderHandler epcOrderHandler, EpcQuoteHandler epcQuoteHandler,
            EpcTransferQuoteHandler epcTransferQuoteHandler) {
        this.epcOrderHandler = epcOrderHandler;
        this.epcQuoteHandler = epcQuoteHandler;
        this.epcTransferQuoteHandler = epcTransferQuoteHandler;
    }


    @GetMapping(value = "/{quoteId}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcQuote getQuoteInfoWithParam(@PathVariable("quoteId") String quoteId, @RequestParam(name = "param", required = false) String param) {
        return epcQuoteHandler.getQuoteInfo(quoteId, param);
    }


    @PostMapping(value = "/transfer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcTransferOrder transferOrder(@RequestBody EpcTransferOrder epcTransferOrder) {
        epcTransferQuoteHandler.transferQuoteToOrder(epcTransferOrder);
        return epcTransferOrder;
    }


    @DeleteMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcDeleteQuoteFromOrder> deleteQuote(@RequestBody EpcDeleteQuoteFromOrder epcDeleteQuoteFromOrder) {
        epcOrderHandler.deleteQuoteFromOrder(epcDeleteQuoteFromOrder);
        if ("SUCCESS".equals(epcDeleteQuoteFromOrder.getResult())) {
            return new ResponseEntity<EpcDeleteQuoteFromOrder>(epcDeleteQuoteFromOrder, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcDeleteQuoteFromOrder>(epcDeleteQuoteFromOrder, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
