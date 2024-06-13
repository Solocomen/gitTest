package epc.epcsalesapi.helper;

import java.util.Set;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.AbstractRequestLoggingFilter;

@Component
public class EpcRequestLoggingFilter extends AbstractRequestLoggingFilter {
    private Set<String> excludedUrls = Set.of("/api/actuator/prometheus");

    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        if(excludedUrls.contains(request.getRequestURI())) {
            return false;
        }
        return logger.isDebugEnabled();
    }

    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        logger.debug(message);
    }

    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        logger.debug(message);
    }
}
