package epc.epcsalesapi.reservation;

import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.reservation.bean.EpcNumReservation;
import epc.epcsalesapi.reservation.bean.EpcNumReservationResult;

@Service
public class EpcReservation {
	
	public EpcNumReservationResult numberReservation(EpcNumReservation epcNumReservation) {
		EpcNumReservationResult epcNumReservationResult = null;
		RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_BILLING_LINK") + "int/numberreservation";
        
        try {
        	responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<EpcNumReservation>(epcNumReservation), String.class);
      		epcNumReservationResult = objectMapper.readValue(responseEntity.getBody(), EpcNumReservationResult.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        
		return epcNumReservationResult;
	}
}
