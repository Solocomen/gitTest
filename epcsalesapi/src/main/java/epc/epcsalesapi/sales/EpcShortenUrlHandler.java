package epc.epcsalesapi.sales;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcCreateShortenUrl;
import epc.epcsalesapi.sales.bean.EpcCreateShortenUrlResult;

@Service
public class EpcShortenUrlHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcShortenUrlHandler.class);

    private final String APP_NAME = "ISGPAPP"; // use gpapp's one at this moment

    public EpcShortenUrlHandler() {
    }

    
    public String generateShortenUrl(String inputUrl) {
        String apiUrl = EpcProperty.getValue("EPC_CREATE_SHORTEN_URL");
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        String shortenUrl = "";
        EpcCreateShortenUrl epcCreateShortenUrl = null;
        EpcCreateShortenUrlResult epcCreateShortenUrlResult = null;
        ArrayList<String> aList = new ArrayList<>();
        aList.add(inputUrl);
        String logStr = "[generateShortenUrl] ";
        String tmpLogStr = "";

        try {
            tmpLogStr = "apiUrl:" + apiUrl;
logger.info("{}{}", logStr, tmpLogStr);

            epcCreateShortenUrl = new EpcCreateShortenUrl();
            epcCreateShortenUrl.setAppservice(APP_NAME);
            epcCreateShortenUrl.setUrl(aList);

            epcCreateShortenUrlResult = restTemplate.postForObject(apiUrl, new HttpEntity<>(epcCreateShortenUrl), EpcCreateShortenUrlResult.class);
            if("OK".equals(epcCreateShortenUrlResult.getStatus())) {
                for(HashMap m : epcCreateShortenUrlResult.getUrl()) {
                    if(inputUrl.equals(StringHelper.trim((String)m.get("source_url")))) {
                        shortenUrl = StringHelper.trim((String)m.get("shorten_url"));
                    }
                }
            } else {
                // error 
                shortenUrl = "N/A";
            }

            tmpLogStr = "inputUrl:" + inputUrl + ",shortenUrl:" + shortenUrl;
logger.info("{}{}", logStr, tmpLogStr);
        } catch(HttpStatusCodeException hsce) {
            try {
                epcCreateShortenUrlResult = objectMapper.readValue(hsce.getResponseBodyAsString(), EpcCreateShortenUrlResult.class);
                tmpLogStr = "error json:" + objectMapper.writeValueAsString(epcCreateShortenUrlResult);
logger.info("{}{}", logStr, tmpLogStr);
            } catch (Exception eee) {
                eee.printStackTrace();    
            }

            shortenUrl = "N/A";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shortenUrl;
    }

    
}
