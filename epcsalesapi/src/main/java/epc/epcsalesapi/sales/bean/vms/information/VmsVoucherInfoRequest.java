package epc.epcsalesapi.sales.bean.vms.information;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VmsVoucherInfoRequest {
    private VoucherInformationReq voucherInformationReq;

    public VmsVoucherInfoRequest() {
    }

    public VoucherInformationReq getVoucherInformationReq() {
        return voucherInformationReq;
    }

    public void setVoucherInformationReq(VoucherInformationReq voucherInformationReq) {
        this.voucherInformationReq = voucherInformationReq;
    }

    
}
