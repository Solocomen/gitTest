package epc.epcsalesapi.rs;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.sales.EpcSignatureHandler;
import epc.epcsalesapi.sales.bean.EpcSignature;

@RestController
@RequestMapping("/salesOrder/sign")
public class EpcSignService {
    private final Logger logger = LoggerFactory.getLogger(EpcSignService.class);

    private EpcSignatureHandler epcSignatureHandler;


    public EpcSignService(EpcSignatureHandler epcSignatureHandler) {
        this.epcSignatureHandler = epcSignatureHandler;
    }


    @PostMapping
    @ResponseBody
    public EpcSignature saveSignature(@RequestBody EpcSignature epcSignature) {
        epcSignatureHandler.saveSignature(epcSignature);
        return epcSignature;
    }


    @PutMapping
    @ResponseBody
    public EpcSignature updateSignature(@RequestBody EpcSignature epcSignature) {
        epcSignatureHandler.updateSignature(epcSignature);
        return epcSignature;
    }


    @GetMapping(value = "/{orderId}")
    @ResponseBody
    public ArrayList<EpcSignature> getSignature(@PathVariable("orderId") int orderId) {
        return epcSignatureHandler.getSignature(orderId);
    }
}
