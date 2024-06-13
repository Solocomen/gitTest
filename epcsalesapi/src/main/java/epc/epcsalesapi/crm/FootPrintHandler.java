package epc.epcsalesapi.crm;

import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import epc.epcsalesapi.crm.bean.CrmFootPrint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@Service
public class FootPrintHandler {
    private final Logger logger = LoggerFactory.getLogger(FootPrintHandler.class);

    @Value("${KAFKA_TOPIC_FOOTPRINT}")
    private String TOPIC_NAME;
    
    private KafkaTemplate<String, CrmFootPrint> footPrintKafkaTemplate;

    public FootPrintHandler(KafkaTemplate<String, CrmFootPrint> footPrintKafkaTemplate) {
        this.footPrintKafkaTemplate = footPrintKafkaTemplate;
    }


//    public void sendAsync(CrmFootPrint crmFootPrint) {
//        try {
//            CompletableFuture.completedFuture(crmFootPrint).thenApplyAsync(s -> send(s));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public String send(CrmFootPrint crmFootPrint) {
        String logStr = "[send]";

        CompletableFuture<SendResult<String, CrmFootPrint>> future = footPrintKafkaTemplate.send(TOPIC_NAME, crmFootPrint);
        future.whenComplete((result, ex) -> {
            String tmpLogStr = "";

            if (ex == null) {
                tmpLogStr = "Sent message to kafka=[" + crmFootPrint + "] with offset=[" + result.getRecordMetadata().offset() + "]";
            } else {
                tmpLogStr = "Unable to send message to kafka=[" + crmFootPrint + "] due to : " + ex.getMessage();
            }
logger.info("{}{}", logStr, tmpLogStr);
        });
        return "OK";
    }


}
