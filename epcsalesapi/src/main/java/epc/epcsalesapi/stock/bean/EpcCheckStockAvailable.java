package epc.epcsalesapi.stock.bean;

public class EpcCheckStockAvailable {
    private String productType;
    private String productCode;
    private String[] locationList;
    
    public EpcCheckStockAvailable() {
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String[] getLocationList() {
        return locationList;
    }

    public void setLocationList(String[] locationList) {
        this.locationList = locationList;
    }

    
}
