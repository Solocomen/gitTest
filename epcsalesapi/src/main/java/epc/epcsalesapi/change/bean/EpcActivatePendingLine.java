package epc.epcsalesapi.change.bean;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;

@Data
public class EpcActivatePendingLine {
    private String custId;
    private String crmOrderId;
    private String custNum;
    private String masterSubr;
    private String rootPortfolioId;
    private ArrayList<String> subrList;
    private String channel;
    private HashMap<String, Object> extraContextData;
    private String requesterId;
    private String effectiveDate;
    private String directSubmission;
}
