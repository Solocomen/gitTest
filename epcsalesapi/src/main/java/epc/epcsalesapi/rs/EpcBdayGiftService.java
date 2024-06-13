package epc.epcsalesapi.rs;

import epc.epcsalesapi.sales.EpcBdayGiftHandler;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelRedeem;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelRedeemResult;
import epc.epcsalesapi.sales.bean.bdayGift.EpcGetBdayGift;
import epc.epcsalesapi.sales.bean.bdayGift.EpcGetBdayGiftResult;
import epc.epcsalesapi.sales.bean.bdayGift.EpcRedeem;
import epc.epcsalesapi.sales.bean.bdayGift.EpcRedeemResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/salesOrder/bdayGift/")
public class EpcBdayGiftService {
    
    @Autowired
    private EpcBdayGiftHandler epcBdayGiftHandler;
    
    @PostMapping(
        value = "/enquiry", 
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcGetBdayGiftResult> enquire(@RequestBody EpcGetBdayGift epcGetBdayGift) {
        EpcGetBdayGiftResult epcGetBdayGiftResult = epcBdayGiftHandler.enquire(epcGetBdayGift);
        if("SUCCESS".equals(epcGetBdayGiftResult.getResult())) {
            return new ResponseEntity<EpcGetBdayGiftResult>(epcGetBdayGiftResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcGetBdayGiftResult>(epcGetBdayGiftResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // added by Danny Chan on 2023-1-16 (Birthday Gift Enhancement): start 
    @PostMapping(
        value = "/redeem", 
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcRedeemResult> redeem(@RequestBody EpcRedeem epcRedeem) {
        EpcRedeemResult epcRedeemResult = epcBdayGiftHandler.redeem(epcRedeem);
        if("SUCCESS".equals(epcRedeemResult.getResult())) {
            return new ResponseEntity<EpcRedeemResult>(epcRedeemResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcRedeemResult>(epcRedeemResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }    
    
    @PostMapping(
        value = "/cancelRedeem", 
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<EpcCancelRedeemResult> cancelRedeem(@RequestBody EpcCancelRedeem epcCancelRedeem) {
        EpcCancelRedeemResult epcCancelRedeemResult = epcBdayGiftHandler.cancelRedeem(epcCancelRedeem);
        if("SUCCESS".equals(epcCancelRedeemResult.getResult())) {
            return new ResponseEntity<EpcCancelRedeemResult>(epcCancelRedeemResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcCancelRedeemResult>(epcCancelRedeemResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // added by Danny Chan on 2023-1-16 (Birthday Gift Enhancement): end 
}
