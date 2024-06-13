package epc.epcsalesapi.stock.bean;

public class EpcProductDetail {
    
    private String productCode;
    private String warehouse;
    private String productDesc;
    private String modelSeries;

    public String getProductCode() {
        return productCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    public String getWarehouse() {
        return warehouse;
    }
    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }
    public String getProductDesc() {
        return productDesc;
    }
    public void setProductDesc(String productDesc) {
        this.productDesc = productDesc;
    }
    public String getModelSeries() {
        return modelSeries;
    }
    public void setModelSeries(String modelSeries) {
        this.modelSeries = modelSeries;
    }

}
