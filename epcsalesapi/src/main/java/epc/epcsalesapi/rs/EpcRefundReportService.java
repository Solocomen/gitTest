package epc.epcsalesapi.rs;

import epc.epcsalesapi.sales.EpcRefundReportHandler;
import epc.epcsalesapi.sales.bean.refundReport.EpcRefundReportDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/salesOrder/refund")
public class EpcRefundReportService {

    @Autowired
    private EpcRefundReportHandler epcRefundReportHandler;

    @GetMapping(value = "/report")
    public List<EpcRefundReportDO> getRefundReport(@RequestParam(value = "startDate") String startDate,
                                                   @RequestParam(value = "endDate") String endDate,
                                                   @RequestParam(value = "state", required = false) String state,
                                                   @RequestParam(value = "method", required = false) String method,
                                                   @RequestParam(value = "location", required = false) String location) throws Exception {
        return epcRefundReportHandler.getRefundReport(startDate, endDate, state, method, location);
    }

}
