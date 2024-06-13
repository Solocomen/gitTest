package epc.epcsalesapi.sales.bean.vms.information;

import java.util.List;

public class VoucherInformationListRes {
    private int statusCode;
    private String statusDesc;
    private List<VmsVoucherInformation> voucherInformation;
    
    public VoucherInformationListRes() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public List<VmsVoucherInformation> getVoucherInformation() {
        return voucherInformation;
    }

    public void setVoucherInformation(List<VmsVoucherInformation> voucherInformation) {
        this.voucherInformation = voucherInformation;
    }


}
