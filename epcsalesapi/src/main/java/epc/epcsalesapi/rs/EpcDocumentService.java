package epc.epcsalesapi.rs;

import epc.epcsalesapi.sales.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import epc.epcsalesapi.sales.bean.EpcTmpUpdOrder;

@RestController
@RequestMapping("/salesDocument")
public class EpcDocumentService {
    private final Logger logger = LoggerFactory.getLogger(EpcDocumentService.class);

    private final EpcGenSAHandler epcGenSAHandler;
    private final EpcGenSAHandler2 epcGenSAHandler2;
    private final EpcGenDeliveryNoteHandler epcGenDeliveryNoteHandler;
    private final EpcOrderHandler epcOrderHandler;
    private final EpcGenBuyBackHandler epcGenBuyBackHandler;

    public EpcDocumentService(EpcGenSAHandler epcGenSAHandler, EpcGenSAHandler2 epcGenSAHandler2, EpcGenDeliveryNoteHandler epcGenDeliveryNoteHandler, EpcOrderHandler epcOrderHandler, EpcGenBuyBackHandler epcGenBuyBackHandler) {
        this.epcGenSAHandler = epcGenSAHandler;
        this.epcGenSAHandler2 = epcGenSAHandler2;
        this.epcGenDeliveryNoteHandler = epcGenDeliveryNoteHandler;
        this.epcOrderHandler = epcOrderHandler;
        this.epcGenBuyBackHandler = epcGenBuyBackHandler;
    }

    @GetMapping(
            value = "/sa/{orderId}", produces = MediaType.TEXT_HTML_VALUE
    )
    @ResponseBody
    public String genSA(@PathVariable("orderId") int orderId, @RequestParam(value = "signId", required = false) String signId) {
        return epcGenSAHandler2.genSaHtml(orderId, signId);
    }


    @GetMapping(
            value = "/sa2/{orderId}", produces = MediaType.TEXT_HTML_VALUE
    )
    @ResponseBody
    public String genSA2(@PathVariable("orderId") int orderId, @RequestParam(value = "signId", required = false) String signId) {
        return epcGenSAHandler2.genSaHtml(orderId, signId);
    }


    @GetMapping(
            value = "/sa/pdf/{orderId}", produces = MediaType.APPLICATION_PDF_VALUE
    )
    @ResponseBody
    public byte[] genPdfSA(@PathVariable("orderId") int orderId, @RequestParam(value = "signId", required = false) String signId) {
        return epcGenSAHandler2.genSaPdf(orderId, signId);
    }


    @GetMapping(
            value = "/sa2/pdf/{orderId}", produces = MediaType.APPLICATION_PDF_VALUE
    )
    @ResponseBody
    public byte[] genPdfSA2(@PathVariable("orderId") int orderId, @RequestParam(value = "signId", required = false) String signId) {
        return epcGenSAHandler2.genSaPdf(orderId, signId);
    }

    @GetMapping(
            value = "/dn/{orderId}", produces = MediaType.TEXT_HTML_VALUE
    )
    @ResponseBody
    public String genDN(@PathVariable("orderId") int orderId) {
        return epcGenDeliveryNoteHandler.genDnHtml(orderId);
    }

    @GetMapping(
            value = "/dn/pdf/{orderId}", produces = MediaType.APPLICATION_PDF_VALUE
    )
    @ResponseBody
    public byte[] genPdfDN(@PathVariable("orderId") int orderId) {
        return epcGenDeliveryNoteHandler.genDnPdf(orderId);
    }

    @GetMapping(
            value = "/dnar/{orderId}", produces = MediaType.TEXT_HTML_VALUE
    )
    @ResponseBody
    public String genDNAR(@PathVariable("orderId") int orderId) {
        return epcGenDeliveryNoteHandler.genDnArHtml(orderId);
    }

    @GetMapping(
            value = "/dnar/pdf/{orderId}", produces = MediaType.APPLICATION_PDF_VALUE
    )
    @ResponseBody
    public byte[] genPdfDNAR(@PathVariable("orderId") int orderId) {
        return epcGenDeliveryNoteHandler.genDnArPdf(orderId);
    }

    @GetMapping(
            value = "/dnars/{orderId}", produces = MediaType.TEXT_HTML_VALUE
    )
    @ResponseBody
    public String genDNARStore(@PathVariable("orderId") int orderId) {
        return epcGenDeliveryNoteHandler.genDnArStoreHtml(orderId);
    }

    @GetMapping(
            value = "/dnars/pdf/{orderId}", produces = MediaType.APPLICATION_PDF_VALUE
    )
    @ResponseBody
    public byte[] genPdfDNARStore(@PathVariable("orderId") int orderId) {
        return epcGenDeliveryNoteHandler.genDnArStorePdf(orderId);
    }

    @PostMapping(value = "/tmpUpdateOrderInfoForPreviewSA", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcTmpUpdOrder> tmpUpdateOrderInfoForPreviewSA(@RequestBody EpcTmpUpdOrder epcTmpUpdOrder) {
        boolean result = epcOrderHandler.tmpUpdateOrderInfoForPreviewSA(epcTmpUpdOrder);
        if (result) {
            return new ResponseEntity<>(epcTmpUpdOrder, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(epcTmpUpdOrder, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/removeTmpUpdateOrderInfoForPreviewSA", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<EpcTmpUpdOrder> removeTmpUpdateOrderInfoForPreviewSA(@RequestBody EpcTmpUpdOrder epcTmpUpdOrder) {
        boolean result = epcOrderHandler.removeTmpUpdateOrderInfoForPreviewSA(epcTmpUpdOrder);
        if (result) {
            return new ResponseEntity<>(epcTmpUpdOrder, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(epcTmpUpdOrder, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping(
            value = "/genBuyBackForm", produces = MediaType.TEXT_HTML_VALUE
    )
    @ResponseBody
    public String genBuyBackFormHtml(@RequestParam("orderId") int orderId, @RequestParam("caseId") String caseId, @RequestParam(value = "signId", required = false) String signId) {
        return epcGenBuyBackHandler.genBuyBackFormHtml(orderId, caseId, signId);
    }


    @GetMapping(
            value = "/genPdfBuyBackForm/{orderId}", produces = MediaType.APPLICATION_PDF_VALUE
    )
    @ResponseBody
    public byte[] genBuyBackFormPdf(@PathVariable("orderId") int orderId, @PathVariable("caseId") String caseId, @RequestParam(value = "signId", required = false) String signId) {
        return epcGenBuyBackHandler.genBuyBackFormPdf(orderId, caseId, signId);
    }
}
