package epc.epcsalesapi.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import epc.epcsalesapi.crm.bean.CrmFootPrint;
import epc.epcsalesapi.helper.EpcCrypto;

@Configuration
public class KafkaConfig {

    @Value("${KAFKA_HOST}")
    private String kafkaHost;

    @Value("${KAFKA_SECURITY_PROTOCOL}")
    private String securityProtocol;

    @Value("${KAFKA_SASL_MECHANISM}")
    private String saslMechanism;

    @Value("${KAFKA_SASL_JAAS_CONFIG}")
    private String saslJaasConfigEncrypted;

    @Value("${KAFKA_ACKS}")
    private String acks;

    
    @Bean
    public ProducerFactory<String, CrmFootPrint> footPrintProducerFactory() {
        String saslJaasConfigDecrypted = "";
        try {
            saslJaasConfigDecrypted = EpcCrypto.dGet(saslJaasConfigEncrypted);
        } catch (Exception e) {
            saslJaasConfigDecrypted = "";
        }

        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaHost);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProps.put("security.protocol", securityProtocol);
        configProps.put("sasl.mechanism", saslMechanism);
        configProps.put("sasl.username", "XXXXXXX");
        configProps.put("sasl.password", "XXXXXXX");
        configProps.put("sasl.jaas.config", saslJaasConfigDecrypted);
        configProps.put("acks", acks);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, CrmFootPrint> footPrintKafkaTemplate() {
        return new KafkaTemplate<>(footPrintProducerFactory());
    }

}
