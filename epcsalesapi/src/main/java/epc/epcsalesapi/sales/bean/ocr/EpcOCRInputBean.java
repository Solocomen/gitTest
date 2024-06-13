package epc.epcsalesapi.sales.bean.ocr;

import lombok.Data;

@Data
public class EpcOCRInputBean {

    private String image;
    private Integer orderId;
    private String loginUserName;
    private String loginSalesmanCode;
}
