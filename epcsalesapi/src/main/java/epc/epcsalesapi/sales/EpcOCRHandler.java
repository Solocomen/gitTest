package epc.epcsalesapi.sales;

import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.rs.EpcOCRService;
import epc.epcsalesapi.sales.bean.ocr.OCRInputBean;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class EpcOCRHandler {

    final static Logger logger = LoggerFactory.getLogger(EpcOCRService.class);

    public String remoteOCRTemplate(OCRInputBean ocrInputBean,String configMapKeyLink,String K8SConfigMapKeyRefLinkName){

        RestTemplate restTemplate = new RestTemplate();
        String rtn = "{}";
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<OCRInputBean> request = new HttpEntity<>(ocrInputBean,headers);
            String apiUrl = EpcProperty.getValue(K8SConfigMapKeyRefLinkName);
            if (StringUtils.isBlank(apiUrl)){
                apiUrl = configMapKeyLink;
            }
            rtn = restTemplate.postForObject(apiUrl, request, String.class);
        }catch (HttpServerErrorException e){
            e.printStackTrace();
            rtn = e.getResponseBodyAsString();
        }catch (Exception e){
            e.printStackTrace();
            logger.error("httpError:" + e.toString());
        }

        return rtn;
    }
}
