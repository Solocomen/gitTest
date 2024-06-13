package epc.epcsalesapi.sales.bean;

public class EpcFulfillLocation {
    private String invoiceLocation;
    private String doaLocation;
    private boolean found;
    
    public EpcFulfillLocation() {
    }

    public String getInvoiceLocation() {
        return invoiceLocation;
    }

    public void setInvoiceLocation(String invoiceLocation) {
        this.invoiceLocation = invoiceLocation;
    }

    public String getDoaLocation() {
        return doaLocation;
    }

    public void setDoaLocation(String doaLocation) {
        this.doaLocation = doaLocation;
    }

    public boolean isFound() {
        return found;
    }

    public void setFound(boolean found) {
        this.found = found;
    }

    
}
