package epc.epcsalesapi.crm.bean;

import java.util.ArrayList;

public class EpcGetSubrInfoResult {
    private String resultCode;
    private String resultMsg;
    private ArrayList<EpcSubscriber> subscriberList;

    public EpcGetSubrInfoResult() {}

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public ArrayList<EpcSubscriber> getSubscriberList() {
        return subscriberList;
    }

    public void setSubscriberList(ArrayList<EpcSubscriber> subscriberList) {
        this.subscriberList = subscriberList;
    }

    
}
