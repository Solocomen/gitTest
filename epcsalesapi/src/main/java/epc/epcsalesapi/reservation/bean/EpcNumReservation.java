package epc.epcsalesapi.reservation.bean;

public class EpcNumReservation {
    private String number;
    private String action;
    private String reserveKey;
    private String dealerCode;
    private String expiryTime;  // date-format YYYY-MM-DD HH24:MI:SS
    private String updateBy;
    

    public EpcNumReservation() {
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getReserveKey() {
        return reserveKey;
    }

    public void setReserveKey(String reserveKey) {
        this.reserveKey = reserveKey;
    }

    public String getDealerCode() {
        return dealerCode;
    }

    public void setDealerCode(String dealerCode) {
        this.dealerCode = dealerCode;
    }
    
    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }
   
    
}
