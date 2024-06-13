package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcPrintTransferNotes {
    private String locationFrom;
    private String locationTo;
    private String printQueue;
    private ArrayList<String> transferNotes;
    private String result;
    private String errMsg;

    public EpcPrintTransferNotes() {}

    public String getLocationFrom() {
        return locationFrom;
    }

    public void setLocationFrom(String locationFrom) {
        this.locationFrom = locationFrom;
    }

    public String getLocationTo() {
        return locationTo;
    }

    public void setLocationTo(String locationTo) {
        this.locationTo = locationTo;
    }

    public String getPrintQueue() {
        return printQueue;
    }

    public void setPrintQueue(String printQueue) {
        this.printQueue = printQueue;
    }

    public ArrayList<String> getTransferNotes() {
        return transferNotes;
    }

    public void setTransferNotes(ArrayList<String> transferNotes) {
        this.transferNotes = transferNotes;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    
}
