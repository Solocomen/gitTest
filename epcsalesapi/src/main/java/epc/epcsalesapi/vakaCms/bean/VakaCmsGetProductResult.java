package epc.epcsalesapi.vakaCms.bean;

import java.util.ArrayList;

public class VakaCmsGetProductResult {
    private String status;
    private ArrayList<VakaCmsProduct> data;

    public VakaCmsGetProductResult() {}

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<VakaCmsProduct> getData() {
        return data;
    }

    public void setData(ArrayList<VakaCmsProduct> data) {
        this.data = data;
    }

    
}
