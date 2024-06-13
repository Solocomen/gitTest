package epc.epcsalesapi.sales.bean;

public class EpcSparseAddProductToQuoteResult {
    private String result;
    private String errMsg;
    private String[] quoteItemGuids;

    public EpcSparseAddProductToQuoteResult() {}

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

    public String[] getQuoteItemGuids() {
        return quoteItemGuids;
    }

    public void setQuoteItemGuids(String[] quoteItemGuids) {
        this.quoteItemGuids = quoteItemGuids;
    }

    
}
