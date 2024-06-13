package epc.epcsalesapi.stock.bean;

public class EpcCheckStockAvailableResultDetail {
    private int available;
    private int buffer;
    private String location;
    
    public EpcCheckStockAvailableResultDetail() {
    }

    public int getAvailable() {
        return available;
    }

    public void setAvailable(int available) {
        this.available = available;
    }

    public int getBuffer() {
        return buffer;
    }

    public void setBuffer(int buffer) {
        this.buffer = buffer;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    
}
