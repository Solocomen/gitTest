package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;
import java.util.HashMap;

import epc.epcsalesapi.sales.bean.vms.VmsVoucher;


public class EpcRedeemToVmsResult {
    private String result;
    private String errMsg;
    private HashMap<String, Object> errMsg2;
    ArrayList<VmsVoucher> voucherList;

    public EpcRedeemToVmsResult() {}

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

    public HashMap<String, Object> getErrMsg2() {
        return errMsg2;
    }

    public void setErrMsg2(HashMap<String, Object> errMsg2) {
        this.errMsg2 = errMsg2;
    }

    public ArrayList<VmsVoucher> getVoucherList() {
        return voucherList;
    }

    public void setVoucherList(ArrayList<VmsVoucher> voucherList) {
        this.voucherList = voucherList;
    }

    
}
