package epc.epcsalesapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import epc.epcsalesapi.helper.EpcRequestLoggingFilter;

@Configuration
public class CommonConfig {
    @Bean
    public EpcRequestLoggingFilter requestLoggingFilter() {
        EpcRequestLoggingFilter loggingFilter = new EpcRequestLoggingFilter();
        loggingFilter.setIncludeClientInfo(false);
        loggingFilter.setIncludeQueryString(true);
        loggingFilter.setIncludePayload(true);
        loggingFilter.setIncludeHeaders(false);
        loggingFilter.setMaxPayloadLength(5000000);

        return loggingFilter;
    }
}
