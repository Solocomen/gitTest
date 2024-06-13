package epc.epcsalesapi.preorder.bean;

public class EpcPreorderUpdateResult {
    
    private int result;
    private String errorMsg;

    public EpcPreorderUpdateResult() {
    }
    public int getResult() {
        return result;
    }
    public void setResult(int result) {
        this.result = result;
    }
    public String getErrorMsg() {
        return errorMsg;
    }
    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    
}
