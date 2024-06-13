package epc.epcsalesapi.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.sales.EpcQuotaHandler;
import epc.epcsalesapi.sales.bean.EpcStaffOfferQuota;

@RestController
@RequestMapping("/salesOrder/quota")
public class EpcQuotaService {
    
    private final Logger logger = LoggerFactory.getLogger(EpcQuotaService.class);

    private final EpcQuotaHandler epcQuotaHandler;

    public EpcQuotaService(EpcQuotaHandler epcQuotaHandler) {
        this.epcQuotaHandler = epcQuotaHandler;
    }

    
    @PostMapping(value = "/staffOffer", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcStaffOfferQuota countStaffOfferQuota(@RequestBody EpcStaffOfferQuota epcStaffOfferQuota) {
        epcQuotaHandler.countStaffOfferQuota(epcStaffOfferQuota);
        return epcStaffOfferQuota;
    }


}
