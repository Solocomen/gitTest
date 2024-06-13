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

import epc.epcsalesapi.sales.EpcCustProfileHandler;
import epc.epcsalesapi.sales.bean.EpcCustProfile;
import epc.epcsalesapi.sales.bean.EpcGetCustProfileResult;


@RestController
@RequestMapping("/salesOrder/personalInfo")
public class EpcPersonalInfoService {
    private final Logger logger = LoggerFactory.getLogger(EpcPersonalInfoService.class);

    private EpcCustProfileHandler epcCustProfileHandler;

    public EpcPersonalInfoService(EpcCustProfileHandler epcCustProfileHandler) {
        this.epcCustProfileHandler = epcCustProfileHandler;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcCustProfile saveCustProfile(@RequestBody EpcCustProfile epcCustProfile) {
        epcCustProfileHandler.createCustProfile(epcCustProfile);
        return epcCustProfile;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcGetCustProfileResult> getCustProfile(
        @RequestParam("custId") String custId,
        @RequestParam("orderId") int orderId, 
        @RequestParam(name = "masked", required = false) String masked
    ) {
        return new ResponseEntity<EpcGetCustProfileResult>(epcCustProfileHandler.getCustProfile(orderId, custId, masked), HttpStatus.OK);
    }
}
