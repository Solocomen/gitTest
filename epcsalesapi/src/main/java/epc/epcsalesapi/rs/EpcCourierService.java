package epc.epcsalesapi.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.sales.EpcCourierHandler;
import epc.epcsalesapi.sales.bean.EpcCourierInfo;

@RestController
@RequestMapping("/salesOrder/courier")
public class EpcCourierService {
    private final Logger logger = LoggerFactory.getLogger(EpcCourierService.class);

    private EpcCourierHandler epcCourierHandler;


    public EpcCourierService(EpcCourierHandler epcCourierHandler) {
        this.epcCourierHandler = epcCourierHandler;
    }
    
    @GetMapping(value = "/getCourierList",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcCourierInfo> getCourierList() {
        EpcCourierInfo epcCourierInfo = epcCourierHandler.getCourierList();
        if ("SUCCESS".equals(epcCourierInfo.getResult())) {
            return new ResponseEntity<EpcCourierInfo>(epcCourierInfo, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcCourierInfo>(epcCourierInfo, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
