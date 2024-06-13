package epc.epcsalesapi.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.sales.EpcDoaHandler;
import epc.epcsalesapi.sales.bean.EpcDoa;

@RestController
@RequestMapping("/salesOrder/doa")
public class EpcDoaService {
    private final Logger logger = LoggerFactory.getLogger(EpcDoaService.class);

    private EpcDoaHandler epcDoaHandler;

    public EpcDoaService(EpcDoaHandler epcDoaHandler) {
        this.epcDoaHandler = epcDoaHandler;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcDoa> doa(@RequestBody EpcDoa epcDoa) {
        epcDoaHandler.doa(epcDoa);
        if ("SUCCESS".equals(epcDoa.getResult())) {
            return new ResponseEntity<>(epcDoa, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(epcDoa, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
