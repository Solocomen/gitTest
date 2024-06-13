package epc.epcsalesapi.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import epc.epcsalesapi.sales.EpcCancelReceiptReportHandler;

@RestController
@RequestMapping("/salesOrder/report")
public class EpcReportService {
    
    private final Logger logger = LoggerFactory.getLogger(EpcSalesService.class);

    private final EpcCancelReceiptReportHandler epcCancelReceiptReportHandler;

    public EpcReportService(EpcCancelReceiptReportHandler epcCancelReceiptReportHandler) {
        this.epcCancelReceiptReportHandler = epcCancelReceiptReportHandler;
    }

    @GetMapping(value = "/cancelReceiptWithoutRefund")
    @ResponseBody
    public String getCancelReceiptWithoutRefund(@RequestParam("location") String location, @RequestParam("startDate") String startDate, @RequestParam("endDate") String endDate) {
        return epcCancelReceiptReportHandler.getCancelReceiptWithoutRefund(location, startDate, endDate);
    }
}
