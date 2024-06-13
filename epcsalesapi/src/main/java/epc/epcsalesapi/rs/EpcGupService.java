package epc.epcsalesapi.rs;

import javax.naming.directory.SearchResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.gup.GupHandler;
import epc.epcsalesapi.gup.bean.EpcCreateGup;
import epc.epcsalesapi.gup.bean.EpcCreateGupUserPin;
import epc.epcsalesapi.gup.bean.EpcDeleteGup;
import epc.epcsalesapi.gup.bean.EpcGupResult;
import epc.epcsalesapi.gup.bean.EpcUpdateGup;
import epc.epcsalesapi.gup.bean.EpcUpdateGupUserPin;
import epc.epcsalesapi.gup.bean.EpcUpdateGupUserPinUsername;

@RestController
@RequestMapping("/gup")
public class EpcGupService {
    
    @Autowired
    private GupHandler gupHandler;

    @PutMapping(
        value = "/create",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcGupResult> createGup(@RequestBody EpcCreateGup input) {

        EpcCreateGup encodedInput = gupHandler.getEncodedGupInput(input);
        EpcGupResult result = gupHandler.createGup(encodedInput);
        
        if(result.getResultCode() == 0) {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    

    @PutMapping(
        value = "/reset",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcGupResult> deleteThenCreateGup(@RequestBody EpcCreateGup input) {

        EpcCreateGup encodedInput = gupHandler.getEncodedGupInput(input);
        EpcGupResult result = gupHandler.deleteThenCreateGup(encodedInput);
        
        if(result.getResultCode() == 0) {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping(
        value = "/delete",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
   	public ResponseEntity<EpcGupResult> deleteGup(@RequestBody EpcDeleteGup input) {

        EpcDeleteGup encodedInput = gupHandler.getEncodedGupInput(input);
        EpcGupResult result = gupHandler.deleteGup(encodedInput);
        
        if(result.getResultCode() == 0) {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(
        value = "/update",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcGupResult> updateGup(@RequestBody EpcUpdateGup input) {

        EpcUpdateGup encodedInput = gupHandler.getEncodedGupInput(input);
        EpcGupResult result = gupHandler.updateGup(encodedInput);
        
        if(result.getResultCode() == 0) {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(
        value = "/createUserPin",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcGupResult> createGupUserPin(@RequestBody EpcCreateGupUserPin input) {

        EpcCreateGupUserPin encodedInput = gupHandler.getEncodedGupInput(input);
        EpcGupResult result = gupHandler.createGupUserPin(encodedInput);
        
        if(result.getResultCode() == 0) {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(
        value = "/updateUserPin",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcGupResult> updateGupUserPin(@RequestBody EpcUpdateGupUserPin input) {

        EpcUpdateGupUserPin encodedInput = gupHandler.getEncodedGupInput(input);
        EpcGupResult result = gupHandler.updateGupUserPin(encodedInput);
        
        if(result.getResultCode() == 0) {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    @PostMapping(
            value = "/changeUserPinUsername",
            produces = MediaType.APPLICATION_JSON_VALUE
        )
    @ResponseBody
    public ResponseEntity<EpcGupResult> updateGupUserPinUsername(@RequestBody EpcUpdateGupUserPinUsername input) {

        EpcUpdateGupUserPinUsername encodedInput = gupHandler.getEncodedGupInput(input);
        EpcGupResult result = gupHandler.updateGupUserPinUsername(encodedInput);
        
        if(result.getResultCode() == 0) {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGupResult>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getSprofile")
    public String getGupDetail(@RequestParam("subrNum") String subrNum) {
        
        return gupHandler.getGupDetail(subrNum);
    }

}
