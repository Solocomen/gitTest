package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcVmsEpcRecord {
    private String custId;
    private String orderReference;
    private String quoteGuid;
    private ArrayList<EpcVmsVoucherInfo> voucherList;

    public EpcVmsEpcRecord() {}

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public ArrayList<EpcVmsVoucherInfo> getVoucherList() {
        return voucherList;
    }

    public void setVoucherList(ArrayList<EpcVmsVoucherInfo> voucherList) {
        this.voucherList = voucherList;
    }

    public String getQuoteGuid() {
        return quoteGuid;
    }

    public void setQuoteGuid(String quoteGuid) {
        this.quoteGuid = quoteGuid;
    }

    
}
