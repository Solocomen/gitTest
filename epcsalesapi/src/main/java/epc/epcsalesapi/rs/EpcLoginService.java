package epc.epcsalesapi.rs;

import epc.epcsalesapi.login.EpcLoginHandler;
import epc.epcsalesapi.login.bean.EpcLoginResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login")
public class EpcLoginService {
	
	@Autowired
	private EpcLoginHandler epcLoginHandler;
    

    public EpcLoginService() {
    }
    
//    @POST
//    @Produces(MediaType.APPLICATION_JSON)
    @PostMapping(
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
//    public Response createOrder(String samlXml) {
    public ResponseEntity<EpcLoginResult> createOrder(@RequestBody String samlXml) {
        EpcLoginResult epcLoginResult = epcLoginHandler.login(samlXml);
        
        if("OK".equals(epcLoginResult.getResult())) {
            epcLoginResult.setResult(null); // tmp
            
//            return Response.status(200).entity(epcLoginResult).build();
            return new ResponseEntity<EpcLoginResult>(epcLoginResult, HttpStatus.OK);
        } else {
            epcLoginResult.setResult(null); // tmp
            
//            return Response.status(500).entity(epcLoginResult).build();
            return new ResponseEntity<EpcLoginResult>(epcLoginResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping(
        value = "/channel"
    )
    @ResponseBody
    public String getSignature(@RequestParam("fesParentGroupId") String fesParentGroupId, @RequestParam("isStreetFighter") String isStreetFighter) {
        return epcLoginHandler.determineLoginChannel(fesParentGroupId, isStreetFighter);
    }
}
