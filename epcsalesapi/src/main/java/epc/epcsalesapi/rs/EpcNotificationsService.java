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
import epc.epcsalesapi.sales.bean.EpcNotificationMessage;

@RestController
@RequestMapping("/salesOrder/notifications")
public class EpcNotificationsService {
    private final Logger logger = LoggerFactory.getLogger(EpcNotificationsService.class);

    private EpcNotificationHandler epcNotificationHandler;


    public EpcNotificationsService(EpcNotificationHandler epcNotificationHandler) {
        this.epcNotificationHandler = epcNotificationHandler;
    }

    @GetMapping(value = "/{orderId}")
    @ResponseBody
    public ArrayList<EpcNotificationMessage> getNotifications(@PathVariable("orderId") int orderId) {
        return epcNotificationHandler.getNotifications(orderId);
    }
    
    @PostMapping(value = "/resend", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcNotificationMessage> resendNotification(String msgId) {
        EpcNotificationMessage epcNotificationMessage = epcNotificationHandler.resendMsg(msgId);
        if ("SUCCESS".equals(epcNotificationMessage.getResult())) {
            return new ResponseEntity<EpcNotificationMessage>(epcNotificationMessage, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcNotificationMessage>(epcNotificationMessage, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
