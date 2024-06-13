package epc.epcsalesapi.sales;


import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;

import epc.epcsalesapi.helper.EpcProperty;


@Service
public class EpcSmartAddressHandler {
	
	private String clientKeyId;
	{
		clientKeyId=EpcProperty.getValue("ADDRAPP_CLIENT_KEY_ID");
	}

	private Logger logger = LoggerFactory.getLogger(getClass());
	public List<String> getAddress(int n,boolean isChi, String query) throws Exception{
		RestTemplate restTemplate = new RestTemplate();
		List<String> list=new ArrayList<String>();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Client-Key", clientKeyId);
            headers.add("X-Client-Id", clientKeyId);
            HttpEntity<?> request = new HttpEntity<>(headers);
            String apiUrl = EpcProperty.getValue("SMART_ADDRESS_API");
            apiUrl =apiUrl+"?n="+n+"&q="+query;
            ResponseEntity<String> rsp = restTemplate.exchange(apiUrl,HttpMethod.GET,request, String.class);
            String rtn=rsp.getBody();
            logger.info("query:{}", rtn);
            JSONObject jsonObject= JSONObject.parseObject(rtn);
            JSONArray array=jsonObject.getJSONArray("result");
            for (int i = 0; i < array.size(); i++) {
				JSONObject element = array.getJSONObject(i);
				if(isChi) {
					list.add(element.getString("fullChiAddressInOneLine"));
				}else {
					list.add(element.getString("fullEngAddressInOneLine"));
				}
				
			}
        }catch (HttpServerErrorException e){
            e.printStackTrace();
            logger.error(query, e);
            String rtn = e.getResponseBodyAsString();
            throw new Exception(rtn);
        }

        return list;
	}
	
}
