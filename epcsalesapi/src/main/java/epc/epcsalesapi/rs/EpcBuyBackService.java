package epc.epcsalesapi.rs;

import epc.epcsalesapi.sales.EpcBuyBackHandler;
import epc.epcsalesapi.sales.bean.EpcBuyBackInfoBean;
import epc.epcsalesapi.sales.bean.EpcBuyBackUpdateImeiResult;
import epc.epcsalesapi.sales.bean.EpcBuyBackSaveInfoResult;

import epc.epcsalesapi.sales.bean.EpcBuyBackUpdateReceiptNoResult;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/epcBuyBackService")
public class EpcBuyBackService {

    @Autowired
    private EpcBuyBackHandler epcBuyBackHandler;

    @PostMapping(value = "/saveBuyBackInfo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcBuyBackSaveInfoResult saveBuyBackInfo(@RequestBody EpcBuyBackInfoBean epcBuyBackInfoBean) {
        return epcBuyBackHandler.saveBuyBackInfo(epcBuyBackInfoBean);
    }

    @GetMapping(value = "/isEpcBuyBackCase", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public boolean isEpcBuyBackCase(@RequestParam("orderId") String orderId, @RequestParam("caseId") String caseId) {
        return epcBuyBackHandler.isEpcBuyBackCase(orderId, caseId);
    }

    @GetMapping(value = "/getEpcBuyBackReceiptNo", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getEpcBuyBackReceiptNo(@RequestParam("orderId") String orderId, @RequestParam("imei") String imei) {
        return epcBuyBackHandler.getEpcBuyBackReceiptNo(orderId, imei);
    }

    @GetMapping(value = "/updateBuyBackImei", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcBuyBackUpdateImeiResult updateBuyBackImei(@RequestParam("imei") String imei , @RequestParam("orderId") String orderId, @RequestParam("caseId") String caseId) {
        return epcBuyBackHandler.updateBuyBackImei(imei, orderId, caseId);
    }

    @GetMapping(value = "/t", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public EpcBuyBackUpdateReceiptNoResult updateBuyBackReceiptNo(@RequestParam("receiptNo") String receiptNo , @RequestParam("orderId") String orderId, @RequestParam("caseId") String caseId) {
        return epcBuyBackHandler.updateBuyBackReceiptNo(receiptNo, orderId, caseId);
    }


}
