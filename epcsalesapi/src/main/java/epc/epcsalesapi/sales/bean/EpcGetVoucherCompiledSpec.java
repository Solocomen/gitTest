package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcGetVoucherCompiledSpec {
    private String status;
    private ArrayList<EpcCompiledSpecEntity> data;
    public EpcGetVoucherCompiledSpec() {
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public ArrayList<EpcCompiledSpecEntity> getData() {
        return data;
    }
    public void setData(ArrayList<EpcCompiledSpecEntity> data) {
        this.data = data;
    }

    
}
