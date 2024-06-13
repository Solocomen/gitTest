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

import epc.epcsalesapi.fes.EpcIphonePushHandler;
import epc.epcsalesapi.fes.bean.EpcPushNotificationReq;
import epc.epcsalesapi.fes.bean.EpcPushNotificationResult;

@RestController
@RequestMapping("/salesOrder/iPhonePush")
public class EpcIphonePushService {
    private final Logger logger = LoggerFactory.getLogger(EpcIphonePushService.class);

    private EpcIphonePushHandler epcIphonePushHandler;


    public EpcIphonePushService(EpcIphonePushHandler epcIphonePushHandler) {
        this.epcIphonePushHandler = epcIphonePushHandler;
    }


    @PostMapping(value = "/push", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcPushNotificationResult> push(@RequestBody EpcPushNotificationReq req) {
    	EpcPushNotificationResult result = epcIphonePushHandler.push(req);
        if ("SUCCESS".equals(result.getResult())) {
            return new ResponseEntity<EpcPushNotificationResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcPushNotificationResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/getScanResult")
    @ResponseBody
    public ResponseEntity<EpcPushNotificationResult> getScanResult(@RequestParam("machineId") String machineId, @RequestParam("salesman") String salesman) {
    	EpcPushNotificationResult result = epcIphonePushHandler.getScanResult(machineId, salesman);
    	if ("SUCCESS".equals(result.getResult())) {
            return new ResponseEntity<EpcPushNotificationResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcPushNotificationResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
