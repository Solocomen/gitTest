package epc.epcsalesapi.rs;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import epc.epcsalesapi.sales.EpcCourierChargeHandler;
import epc.epcsalesapi.sales.EpcDeliveryInfoHandler;
import epc.epcsalesapi.sales.EpcGetDeliveryInfoHandler;
import epc.epcsalesapi.sales.bean.EpcDeliveryInfo;
import epc.epcsalesapi.sales.bean.EpcSaveDeliveryInfoResult;
import epc.epcsalesapi.sales.bean.EpcUpdateCourier;

@RestController
@RequestMapping("/salesOrder/deliveryInfo")
public class EpcDeliveryInfoService {
    private final Logger logger = LoggerFactory.getLogger(EpcSignService.class);

    private EpcDeliveryInfoHandler epcDeliveryInfoHandler;
    private EpcGetDeliveryInfoHandler epcGetDeliveryInfoHandler;
    private EpcCourierChargeHandler epcCourierChargeHandler;

    public EpcDeliveryInfoService(
        EpcDeliveryInfoHandler epcDeliveryInfoHandler, EpcGetDeliveryInfoHandler epcGetDeliveryInfoHandler,
        EpcCourierChargeHandler epcCourierChargeHandler
    ) {
        this.epcDeliveryInfoHandler = epcDeliveryInfoHandler;
        this.epcGetDeliveryInfoHandler = epcGetDeliveryInfoHandler;
        this.epcCourierChargeHandler = epcCourierChargeHandler;
    }


    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcSaveDeliveryInfoResult> saveDeliveryInfo(@RequestBody EpcDeliveryInfo epcDeliveryInfo) {
        EpcSaveDeliveryInfoResult epcSaveDeliveryInfoResult = epcDeliveryInfoHandler.saveDeliveryInfo(epcDeliveryInfo);

        if ("SUCCESS".equals(epcSaveDeliveryInfoResult.getSaveStatus())) {
            return new ResponseEntity<EpcSaveDeliveryInfoResult>(epcSaveDeliveryInfoResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcSaveDeliveryInfoResult>(epcSaveDeliveryInfoResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcDeliveryInfo> getDeliveryInfo(@RequestParam("orderId") int orderId) {
        EpcDeliveryInfo epcDeliveryInfo = epcGetDeliveryInfoHandler.getPickupLocation(orderId);

        if ("SUCCESS".equals(epcDeliveryInfo.getResult())) {
            return new ResponseEntity<EpcDeliveryInfo>(epcDeliveryInfo, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcDeliveryInfo>(epcDeliveryInfo, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(value = "/courierFee")
    @ResponseBody
    public BigDecimal calculateCourierCharge(@RequestParam("orderId") int orderId, @RequestParam("channel") String channel) {
    	return epcCourierChargeHandler.calculateCourierCharge(orderId, channel);
    }
    
    @PostMapping(value = "/updateCourier", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcUpdateCourier> updateCourierInfo(@RequestBody EpcUpdateCourier epcUpdateCourier) {
    	EpcUpdateCourier epcUpdateResult = epcDeliveryInfoHandler.updateCourierInfo(epcUpdateCourier);

        if ("SUCCESS".equals(epcUpdateResult.getResult())) {
            return new ResponseEntity<EpcUpdateCourier>(epcUpdateResult, HttpStatus.OK);
        } else {
            return new ResponseEntity<EpcUpdateCourier>(epcUpdateResult, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
