package epc.epcsalesapi.sales.bean.bdayGift;

public class EpcGetGiftRedemptionList {
    
    private String requesterId;
    private String custNum;
    private String subrNum;
    private String membershipTier;
    private String redeemStatus;

    public String getRequesterId() {
        return requesterId;
    }
    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }
    public String getCustNum() {
        return custNum;
    }
    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }
    public String getSubrNum() {
        return subrNum;
    }
    public void setSubrNum(String subrNum) {
        this.subrNum = subrNum;
    }
    public String getMembershipTier() {
        return membershipTier;
    }
    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }
    public String getRedeemStatus() {
        return redeemStatus;
    }
    public void setRedeemStatus(String redeemStatus) {
        this.redeemStatus = redeemStatus;
    }

}
