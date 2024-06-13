package epc.epcsalesapi.stock.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcConfirmReserveItemReturn {
    private String custName;
    private String channel;
    private String referKey;
    private String pickupDate;
    private String subrNum;
    private String pickupLoc;
    private String productType;
    private String productCode;
    private String commLang;
    private String priority;
    private String createUser;
    private String createSalesman;
    private String createChannel;
    private String notifyNeed;
    private String needDesposit;
    private String withStock;
    private String itemReferenceNo;
    private String epcItemId;
    private String tmpReserveId;
    private String isEpc;
    private String status;
    private String resultCode;
    private String errorMessage;

    public EpcConfirmReserveItemReturn() {}

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getReferKey() {
        return referKey;
    }

    public void setReferKey(String referKey) {
        this.referKey = referKey;
    }

    public String getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(String pickupDate) {
        this.pickupDate = pickupDate;
    }

    public String getSubrNum() {
        return subrNum;
    }

    public void setSubrNum(String subrNum) {
        this.subrNum = subrNum;
    }

    public String getPickupLoc() {
        return pickupLoc;
    }

    public void setPickupLoc(String pickupLoc) {
        this.pickupLoc = pickupLoc;
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

    public String getCommLang() {
        return commLang;
    }

    public void setCommLang(String commLang) {
        this.commLang = commLang;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateSalesman() {
        return createSalesman;
    }

    public void setCreateSalesman(String createSalesman) {
        this.createSalesman = createSalesman;
    }

    public String getCreateChannel() {
        return createChannel;
    }

    public void setCreateChannel(String createChannel) {
        this.createChannel = createChannel;
    }

    public String getNotifyNeed() {
        return notifyNeed;
    }

    public void setNotifyNeed(String notifyNeed) {
        this.notifyNeed = notifyNeed;
    }

    public String getNeedDesposit() {
        return needDesposit;
    }

    public void setNeedDesposit(String needDesposit) {
        this.needDesposit = needDesposit;
    }

    public String getWithStock() {
        return withStock;
    }

    public void setWithStock(String withStock) {
        this.withStock = withStock;
    }

    public String getItemReferenceNo() {
        return itemReferenceNo;
    }

    public void setItemReferenceNo(String itemReferenceNo) {
        this.itemReferenceNo = itemReferenceNo;
    }

    public String getEpcItemId() {
        return epcItemId;
    }

    public void setEpcItemId(String epcItemId) {
        this.epcItemId = epcItemId;
    }

    public String getTmpReserveId() {
        return tmpReserveId;
    }

    public void setTmpReserveId(String tmpReserveId) {
        this.tmpReserveId = tmpReserveId;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getIsEpc() {
        return isEpc;
    }

    public void setIsEpc(String isEpc) {
        this.isEpc = isEpc;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    
}
