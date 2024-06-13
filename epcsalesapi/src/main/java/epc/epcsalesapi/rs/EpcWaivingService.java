package epc.epcsalesapi.rs;

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

import epc.epcsalesapi.fes.waiving.WaivingHandler;
import epc.epcsalesapi.fes.waiving.bean.EpcCheckWaive;
import epc.epcsalesapi.fes.waiving.bean.EpcCheckWaiveResult;
import epc.epcsalesapi.fes.waiving.bean.EpcChgCheckWaive;
import epc.epcsalesapi.fes.waiving.bean.EpcChgCheckWaiveResult;
import epc.epcsalesapi.fes.waiving.bean.EpcWaiveAuthorityFormResult;

@RestController
@RequestMapping("/waiving")
public class EpcWaivingService {

    @Autowired
    private WaivingHandler waivingHandler;
    
    @PostMapping(
        value = "/check",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcCheckWaiveResult> checkWaive(@RequestBody EpcCheckWaive epcCheckWaive) {
        EpcCheckWaiveResult result = waivingHandler.checkWaive(epcCheckWaive);
        if("SUCCESS".equals(result.getResult())) {
            return new ResponseEntity<EpcCheckWaiveResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcCheckWaiveResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(
        value = "/checkForEPC",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcCheckWaiveResult> checkWaiveForEPC(@RequestBody EpcCheckWaive epcCheckWaive) {
        EpcCheckWaiveResult result = waivingHandler.checkWaiveForEPC(epcCheckWaive);
        if("SUCCESS".equals(result.getResult())) {
            return new ResponseEntity<EpcCheckWaiveResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcCheckWaiveResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(
        value = "/checkChg",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcChgCheckWaiveResult> checkWaive(@RequestBody EpcChgCheckWaive epcChgCheckWaive) {
    	EpcChgCheckWaiveResult result = waivingHandler.chgCheckWaive(epcChgCheckWaive);
        if("SUCCESS".equals(result.getResult())) {
            return new ResponseEntity<EpcChgCheckWaiveResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcChgCheckWaiveResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @GetMapping(
        value = "/waiveAuthorityForm",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcWaiveAuthorityFormResult> waiveAuthorityForm(@RequestParam(required = true) int groupid) {
        EpcWaiveAuthorityFormResult epcWaiveAuthorityFormResult = new EpcWaiveAuthorityFormResult();
        try {
            epcWaiveAuthorityFormResult.setWaiveAuthorityFormCode(waivingHandler.getFormType(groupid));
            epcWaiveAuthorityFormResult.setResult("SUCCESS");
            return new ResponseEntity<EpcWaiveAuthorityFormResult>(epcWaiveAuthorityFormResult, HttpStatus.OK);
        } catch (Exception e) {
            epcWaiveAuthorityFormResult.setResult("FAIL");
            epcWaiveAuthorityFormResult.setErrorMessage("System Error");
            return new ResponseEntity<EpcWaiveAuthorityFormResult>(epcWaiveAuthorityFormResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
