package epc.epcsalesapi.rs;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.notification.EpcNotificationHandler;
import epc.epcsalesapi.sales.EpcGenShippingNotificationHandler;
import epc.epcsalesapi.sales.bean.EpcNotificationMessage;

@RestController
@RequestMapping("/salesOrder/shippingNotification")
public class EpcShippingNotificationsService {
    private final Logger logger = LoggerFactory.getLogger(EpcShippingNotificationsService.class);

    private EpcGenShippingNotificationHandler epcGenShippingNotificationHandler;


    public EpcShippingNotificationsService(EpcGenShippingNotificationHandler epcGenShippingNotificationHandler) {
        this.epcGenShippingNotificationHandler = epcGenShippingNotificationHandler;
    }
    
    @PostMapping(value = "/sendEmail", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcNotificationMessage> sendShippingEmail(String orderId) {
        EpcNotificationMessage epcNotificationMessage = epcGenShippingNotificationHandler.sendEmail(orderId);
        if ("SUCCESS".equals(epcNotificationMessage.getResult())) {
            return new ResponseEntity<EpcNotificationMessage>(epcNotificationMessage, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcNotificationMessage>(epcNotificationMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(value = "/sendSms", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcNotificationMessage> sendShippingSms(String orderId) {
        EpcNotificationMessage epcNotificationMessage = epcGenShippingNotificationHandler.sendSms(orderId);
        if ("SUCCESS".equals(epcNotificationMessage.getResult())) {
            return new ResponseEntity<EpcNotificationMessage>(epcNotificationMessage, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcNotificationMessage>(epcNotificationMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
