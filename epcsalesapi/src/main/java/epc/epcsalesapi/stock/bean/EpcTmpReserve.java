/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.stock.bean;

/**
 *
 * @author KerryTsang
 */
public class EpcTmpReserve {
    private String custName;
    private String channel;
    private String referKey;
    private String pickupDate;
    private String subrNum;
    private String pickupLoc;
    private String productType;
    private String productCode;
    private String commLang;
    private String createUser;
    private String createSalesman;
    private String createChannel;
    private String notifyNeed;
    private String tmpReserveId;
    private String onlineReferenceNo;

    public EpcTmpReserve() {
    }

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

    public String getTmpReserveId() {
        return tmpReserveId;
    }

    public void setTmpReserveId(String tmpReserveId) {
        this.tmpReserveId = tmpReserveId;
    }

	public String getOnlineReferenceNo() {
		return onlineReferenceNo;
	}

	public void setOnlineReferenceNo(String onlineReferenceNo) {
		this.onlineReferenceNo = onlineReferenceNo;
	}
    
    
}
