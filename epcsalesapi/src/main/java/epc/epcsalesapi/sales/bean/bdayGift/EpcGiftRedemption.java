package epc.epcsalesapi.sales.bean.bdayGift;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcGiftRedemption {
    
    private String entitleYear;
    private String membershipTier;
    private int birthdayMonth;
    private String redeemDate;
    private String subOfferId;
    private String birthdayGift;
    private String birthdayGiftChi;
    private String expiryDate;
    private String extensionRequestDate;
    private String redeemStatus;
    private String eAuthorization;

    public String getEntitleYear() {
        return entitleYear;
    }
    public void setEntitleYear(String entitleYear) {
        this.entitleYear = entitleYear;
    }
    public String getMembershipTier() {
        return membershipTier;
    }
    public void setMembershipTier(String membershipTier) {
        this.membershipTier = membershipTier;
    }
    public int getBirthdayMonth() {
        return birthdayMonth;
    }
    public void setBirthdayMonth(int birthdayMonth) {
        this.birthdayMonth = birthdayMonth;
    }
    public String getRedeemDate() {
        return redeemDate;
    }
    public void setRedeemDate(String redeemDate) {
        this.redeemDate = redeemDate;
    }
    public String getSubOfferId() {
        return subOfferId;
    }
    public void setSubOfferId(String subOfferId) {
        this.subOfferId = subOfferId;
    }
    public String getBirthdayGift() {
        return birthdayGift;
    }
    public void setBirthdayGift(String birthdayGift) {
        this.birthdayGift = birthdayGift;
    }
    public String getBirthdayGiftChi() {
        return birthdayGiftChi;
    }
    public void setBirthdayGiftChi(String birthdayGiftChi) {
        this.birthdayGiftChi = birthdayGiftChi;
    }
    public String getExpiryDate() {
        return expiryDate;
    }
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }
    public String getExtensionRequestDate() {
        return extensionRequestDate;
    }
    public void setExtensionRequestDate(String extensionRequestDate) {
        this.extensionRequestDate = extensionRequestDate;
    }
    public String getRedeemStatus() {
        return redeemStatus;
    }
    public void setRedeemStatus(String redeemStatus) {
        this.redeemStatus = redeemStatus;
    }
    public String geteAuthorization() {
        return eAuthorization;
    }
    public void seteAuthorization(String eAuthorization) {
        this.eAuthorization = eAuthorization;
    }

}
