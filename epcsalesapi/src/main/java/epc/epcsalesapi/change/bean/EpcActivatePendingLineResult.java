package epc.epcsalesapi.change.bean;

import lombok.Data;

import java.util.HashMap;

@Data
public class EpcActivatePendingLineResult {

    private String orderId; // hansen om id
    private int resultCode;
    private HashMap<String,Object> cpqErrorDetails;  //Validation that returns from CPQ
    private String resultMsg;
}
