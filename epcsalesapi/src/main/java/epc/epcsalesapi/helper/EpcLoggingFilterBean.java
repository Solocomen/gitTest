package epc.epcsalesapi.helper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import epc.epcsalesapi.helper.bean.EpcActionLog;
import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/***
 * referred from https://vkuzel.com/log-requests-and-responses-including-body-in-spring-boot
 */

@Component
public class EpcLoggingFilterBean extends GenericFilterBean {
    
    private static final Logger logger = LoggerFactory.getLogger(EpcLoggingFilterBean.class);
    private final String[] pathToSkipArray = {"/api/actuator", "/api/salesDocument"};

    @Autowired
    private EpcActionLogHandler epcActionLogHandler;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ContentCachingRequestWrapper requestWrapper = requestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = responseWrapper(response);

        chain.doFilter(requestWrapper, responseWrapper);

//        logRequest(requestWrapper);
//        logResponse(responseWrapper);
        createLog(requestWrapper, responseWrapper);
    }

//    private void logRequest(ContentCachingRequestWrapper request) {
//        StringBuilder builder = new StringBuilder();
//        builder.append(headersToString(Collections.list(request.getHeaderNames()), request::getHeader));
//        builder.append(new String(request.getContentAsByteArray()));
//        logger.info("Request: {}", builder);
//    }

//    private void logResponse(ContentCachingResponseWrapper response) throws IOException {
//        StringBuilder builder = new StringBuilder();
//        builder.append(headersToString(response.getHeaderNames(), response::getHeader));
//        builder.append(new String(response.getContentAsByteArray()));
//        logger.info("Response: {}", builder);
//        response.copyBodyToResponse();
//    }

    private void createLogOld(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) throws IOException {
        String uri = StringHelper.trim(request.getRequestURI());
        String queryString = StringHelper.trim(request.getQueryString());
        String path = uri;
        if(!"".equals(queryString)) {
            path += "?" + queryString;
        }
        String method = request.getMethod();
        StringBuilder builder = new StringBuilder();
        boolean isPrint = true;

        for(String s : pathToSkipArray) {
            if(uri.contains(s)) {
                isPrint = false;
                break;
            }
        }

        if(isPrint) {
            // append request
            builder.append("request [" + method + " " + path + "]:");
            builder.append(new String(request.getContentAsByteArray()));

            // append response
            builder.append("|||response:");
            builder.append(new String(response.getContentAsByteArray()));

            logger.info("[API call]: {}", builder);
        }

        response.copyBodyToResponse();
    }


    private void createLog(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response) throws IOException {
        String uri = StringHelper.trim(request.getRequestURI());
        String queryString = StringHelper.trim(request.getQueryString());
        String path = uri;
        if(!"".equals(queryString)) {
            path += "?" + queryString;
        }
        String method = request.getMethod();
        StringBuilder builder = new StringBuilder();
        boolean isPrint = true;

        for(String s : pathToSkipArray) {
            if(uri.contains(s)) {
                isPrint = false;
                break;
            }
        }

        if(isPrint) {
            EpcActionLog epcActionLog = new EpcActionLog();
            epcActionLog.setAction("API");
            epcActionLog.setUri(method + " " + path);
            epcActionLog.setInString(new String(request.getContentAsByteArray()));
            epcActionLog.setOutString(new String(response.getContentAsByteArray()));

            epcActionLogHandler.writeApiLogAsync(epcActionLog);
        }

        response.copyBodyToResponse();
    }

//    private String headersToString(Collection<String> headerNames, Function<String, String> headerValueResolver) {
//        StringBuilder builder = new StringBuilder();
//        for (String headerName : headerNames) {
//            String header = headerValueResolver.apply(headerName);
//            builder.append("%s=%s".formatted(headerName, header)).append("\n");
//        }
//        return builder.toString();
//    }

    private ContentCachingRequestWrapper requestWrapper(ServletRequest request) {
        if (request instanceof ContentCachingRequestWrapper) {
            return (ContentCachingRequestWrapper)request;
        }
        return new ContentCachingRequestWrapper((HttpServletRequest) request);
    }

    private ContentCachingResponseWrapper responseWrapper(ServletResponse response) {
        if (response instanceof ContentCachingResponseWrapper) {
            return (ContentCachingResponseWrapper)response;
        }
        return new ContentCachingResponseWrapper((HttpServletResponse) response);
    }
}
