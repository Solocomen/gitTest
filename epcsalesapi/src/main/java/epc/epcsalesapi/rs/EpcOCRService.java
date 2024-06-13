package epc.epcsalesapi.rs;

import epc.epcsalesapi.sales.EpcOCRHandler;
import epc.epcsalesapi.sales.bean.ocr.OCRInputBean;
import epc.epcsalesapi.sales.bean.ocr.EpcOCRInputBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/ocrService")
public class EpcOCRService {

    @Value("${FES_OCR_HKID_LINK}")
    private String hkidLink;

    @Value("${FES_OCR_PASSPORT_LINK}")
    private String passportLink;

    @Value("${FES_OCR_BR_LINK}")
    private String brLink;

    @Value("${FES_OCR_ADDRPROOF_LINK}")
    private String addrproofLink;

    @Autowired
    private EpcOCRHandler epcOCRHandler;

    final static String REQUESTER = "EPC";

    @PostMapping(value = "/HKID")
    public String doHKIDOCR(@RequestBody EpcOCRInputBean epcOCRInputBean){

        OCRInputBean ocrInputBean = new OCRInputBean();
        ocrInputBean.setImage(epcOCRInputBean.getImage());
        ocrInputBean.setRequester(REQUESTER);
        ocrInputBean.setTxnId(String.valueOf(epcOCRInputBean.getOrderId()));

        return epcOCRHandler.remoteOCRTemplate(ocrInputBean,hkidLink,"FES_OCR_HKID_LINK");
    }

    @PostMapping(value = "/passport")
    public String doPassportOCR(@RequestBody EpcOCRInputBean epcOCRInputBean){

        OCRInputBean ocrInputBean = new OCRInputBean();
        ocrInputBean.setImage(epcOCRInputBean.getImage());
        ocrInputBean.setRequester(REQUESTER);
        ocrInputBean.setTxnId(String.valueOf(epcOCRInputBean.getOrderId()));

        return epcOCRHandler.remoteOCRTemplate(ocrInputBean,passportLink,"FES_OCR_PASSPORT_LINK");
    }

    @PostMapping(value = "/BR")
    public String doBR(@RequestBody EpcOCRInputBean epcOCRInputBean){

        OCRInputBean ocrInputBean = new OCRInputBean();
        ocrInputBean.setImage(epcOCRInputBean.getImage());
        ocrInputBean.setRequester(REQUESTER);
        ocrInputBean.setTxnId(String.valueOf(epcOCRInputBean.getOrderId()));

        return epcOCRHandler.remoteOCRTemplate(ocrInputBean,brLink,"FES_OCR_BR_LINK");
    }

    @PostMapping(value = "/addrProof")
    public String doAddrProof(@RequestBody EpcOCRInputBean epcOCRInputBean){

        OCRInputBean ocrInputBean = new OCRInputBean();
        ocrInputBean.setImage(epcOCRInputBean.getImage());

        return epcOCRHandler.remoteOCRTemplate(ocrInputBean,addrproofLink,"FES_OCR_ADDRPROOF_LINK");

    }
}
