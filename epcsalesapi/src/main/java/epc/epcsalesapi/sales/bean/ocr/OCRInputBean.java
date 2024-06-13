package epc.epcsalesapi.sales.bean.ocr;

import lombok.Data;

@Data
public class OCRInputBean {
    private String requester;
    private String txnId;
    private String image;
    private String idType;

}
