package epc.epcsalesapi.rs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import epc.epcsalesapi.preorder.EpcPreorderHandler;
import epc.epcsalesapi.preorder.bean.EpcPreorder;


@RestController
@RequestMapping("/salesOrder/preorder")
public class EpcPreorderService {
    private final Logger logger = LoggerFactory.getLogger(EpcPreorderService.class);

    private EpcPreorderHandler epcPreorderHandler;

    public EpcPreorderService(EpcPreorderHandler epcPreorderHandler) {
        this.epcPreorderHandler = epcPreorderHandler;
    }

    @PostMapping(value = "/consume")
    public EpcPreorder consumePreorderRecord(@RequestBody EpcPreorder epcPreorder) {
        epcPreorderHandler.consumePreorderRecord(epcPreorder);
        return epcPreorder;
    }

    @PostMapping(value = "/resume")
    public EpcPreorder resumePreorderRecord(@RequestBody EpcPreorder epcPreorder) {
        epcPreorderHandler.resumePreorderRecord(epcPreorder);
        return epcPreorder;
    }
}
