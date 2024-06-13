package epc.epcsalesapi.sales.bean;

public class EpcSignature {
    private String signId;
    private int orderId;
    private String content;
    private String contentType;
    private String createDate;
    private String withDn;
    private String result;
    private String errMsg;

    public EpcSignature() {}

    public String getSignId() {
        return signId;
    }

    public void setSignId(String signId) {
        this.signId = signId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public String getWithDn() {
        return withDn;
    }

    public void setWithDn(String withDn) {
        this.withDn = withDn;
    }

    
}
