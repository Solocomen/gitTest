package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

public class EpcGetSpec {
    private String productGuid;
    private HashMap<String, Object> compiledSpecification;
    private String result;
    private String errMsg;
    public EpcGetSpec() {
    }
    public String getProductGuid() {
        return productGuid;
    }
    public void setProductGuid(String productGuid) {
        this.productGuid = productGuid;
    }
    public HashMap<String, Object> getCompiledSpecification() {
        return compiledSpecification;
    }
    public void setCompiledSpecification(HashMap<String, Object> compiledSpecification) {
        this.compiledSpecification = compiledSpecification;
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
