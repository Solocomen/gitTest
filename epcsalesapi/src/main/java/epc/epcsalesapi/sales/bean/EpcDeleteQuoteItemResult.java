package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

public class EpcDeleteQuoteItemResult {
	private String result;
    private String errMsg;
    private EpcCpqError errMsg2;

    public EpcDeleteQuoteItemResult() {
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

    public EpcCpqError getErrMsg2() {
        return errMsg2;
    }

    public void setErrMsg2(EpcCpqError errMsg2) {
        this.errMsg2 = errMsg2;
    }

}
