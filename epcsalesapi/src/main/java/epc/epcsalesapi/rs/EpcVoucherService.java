package epc.epcsalesapi.rs;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import epc.epcsalesapi.sales.EpcVoucherHandlerNew;
import epc.epcsalesapi.sales.bean.EpcRedeemVoucher;
import epc.epcsalesapi.sales.bean.vms.assign.AutoRemoveAssignedVoucher;
import epc.epcsalesapi.sales.bean.vms.assign.VmsAssignVoucher;
import epc.epcsalesapi.sales.bean.vms.assign.VmsAutoAssign;

@RestController
@RequestMapping("/salesOrder/vouchers")
public class EpcVoucherService {
    private final Logger logger = LoggerFactory.getLogger(EpcVoucherService.class);

    private final EpcVoucherHandlerNew epcVoucherHandlerNew;
   

    public EpcVoucherService(EpcVoucherHandlerNew epcVoucherHandlerNew) {
        this.epcVoucherHandlerNew = epcVoucherHandlerNew;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcRedeemVoucher> redeemVoucher(@RequestBody EpcRedeemVoucher epcRedeemVoucher) {
        epcVoucherHandlerNew.redeemVoucher(epcRedeemVoucher);

        if ("SUCCESS".equals(epcRedeemVoucher.getResult())) {
            return new ResponseEntity<>(epcRedeemVoucher, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(epcRedeemVoucher, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(
        value = "/assign",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<VmsAutoAssign> assignVoucher(@RequestBody VmsAutoAssign vmsAutoAssign) {
        epcVoucherHandlerNew.autoAssign(vmsAutoAssign);

        if ("SUCCESS".equals(vmsAutoAssign.getResult())) {
            return new ResponseEntity<>(vmsAutoAssign, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(vmsAutoAssign, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(
        value = "/nonPaidAssigned",
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ArrayList<AutoRemoveAssignedVoucher> removeNonPaidAssigned() {
        return epcVoucherHandlerNew.removeNonPaidAssignedVoucher();
    }


    @GetMapping(value = "/assignedVoucher", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> assignedVoucher(Integer orderId) {
        try {
            List<VmsAssignVoucher> list = epcVoucherHandlerNew.getAssignedVoucher(orderId);
            return new ResponseEntity<List<VmsAssignVoucher>>(list, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping(value = "/sendMailSms", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public ResponseEntity<?> sendMailSms(Integer orderId, String customerId, String itemId) {
        try {
            epcVoucherHandlerNew.sendVoucherEmailSms(orderId, customerId, itemId);
            return new ResponseEntity<>("OK", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
