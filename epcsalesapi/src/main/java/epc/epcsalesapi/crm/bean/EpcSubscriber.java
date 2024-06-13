    /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.crm.bean;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author KenTKChung
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcSubscriber implements Serializable {

    private static final long serialVersionUID = 1L;
    private String recId;
    private String recStatus;
    private String custNum;
    private String accountNum;
    private String subrNum;
    private String portinSource;
    private String lineCategory;
    private String subrRole;
    private Integer productSeq;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date subrOnDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date subrActivateDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date subrOffDate;
    private String subrOffReason;
    private String subrStatus;
    private String subrCustId;
    private String commChannelId;
    private String prevRecId;
    private String prevRecReason;
    private String parentSubrId;
    private String parentType;
    private String mgmReferrerCode;
    private String mgmRefereeCode;
    private String loyaltyMarker;
    private String guarantorContactId;
    private String guarantorAddressId;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date guarantorStartDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date guarantorExpireDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastUpdatedDate;
    private String createdBy;
    private String lastUpdatedBy;
    private String userIdentity;
    private BigInteger loginnowId;
    private String portinInd;
    private String dno;
    private String buyoutNum;
    private String loginnowAuthorized;
    private String contactId;
    private String registerAddressId;
    private String commAddressId;
     
//    @Transient
    private String email;
    private EpcContact contact;
    private EpcContact userProfile;
    private EpcAddress registerAddress;
    private EpcAddress commAddress;
    private String crmLabel;
    private String planDesc;
    private String planEndDate;
    private String custId;
    private String subrName;
    
    
    public EpcSubscriber() {
    }

    public EpcSubscriber(String recId) {
        this.recId = recId;
    }

    public String getRecId() {
        return recId;
    }

    public void setRecId(String recId) {
        this.recId = recId;
    }

    public String getRecStatus() {
        return recStatus;
    }

    public void setRecStatus(String recStatus) {
        this.recStatus = recStatus;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getSubrNum() {
        return subrNum;
    }

    public void setSubrNum(String subrNum) {
        this.subrNum = subrNum;
    }

    public String getPortinSource() {
        return portinSource;
    }

    public void setPortinSource(String portinSource) {
        this.portinSource = portinSource;
    }

    public String getLineCategory() {
        return lineCategory;
    }

    public void setLineCategory(String lineCategory) {
        this.lineCategory = lineCategory;
    }

    public String getSubrRole() {
        return subrRole;
    }

    public void setSubrRole(String subrRole) {
        this.subrRole = subrRole;
    }

    public Integer getProductSeq() {
        return productSeq;
    }

    public void setProductSeq(Integer productSeq) {
        this.productSeq = productSeq;
    }

    public Date getSubrOnDate() {
        return subrOnDate;
    }

    public void setSubrOnDate(Date subrOnDate) {
        this.subrOnDate = subrOnDate;
    }

    public Date getSubrActivateDate() {
        return subrActivateDate;
    }

    public void setSubrActivateDate(Date subrActivateDate) {
        this.subrActivateDate = subrActivateDate;
    }

    public Date getSubrOffDate() {
        return subrOffDate;
    }

    public void setSubrOffDate(Date subrOffDate) {
        this.subrOffDate = subrOffDate;
    }

    public String getSubrOffReason() {
        return subrOffReason;
    }

    public void setSubrOffReason(String subrOffReason) {
        this.subrOffReason = subrOffReason;
    }

    public String getSubrStatus() {
        return subrStatus;
    }

    public void setSubrStatus(String subrStatus) {
        this.subrStatus = subrStatus;
    }

    public String getSubrCustId() {
        return subrCustId;
    }

    public void setSubrCustId(String subrCustId) {
        this.subrCustId = subrCustId;
    }

    public String getCommChannelId() {
        return commChannelId;
    }

    public void setCommChannelId(String commChannelId) {
        this.commChannelId = commChannelId;
    }

    public String getPrevRecId() {
        return prevRecId;
    }

    public void setPrevRecId(String prevRecId) {
        this.prevRecId = prevRecId;
    }

    public String getPrevRecReason() {
        return prevRecReason;
    }

    public void setPrevRecReason(String prevRecReason) {
        this.prevRecReason = prevRecReason;
    }

    public String getParentSubrId() {
        return parentSubrId;
    }

    public void setParentSubrId(String parentSubrId) {
        this.parentSubrId = parentSubrId;
    }

    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }

    public String getMgmReferrerCode() {
        return mgmReferrerCode;
    }

    public void setMgmReferrerCode(String mgmReferrerCode) {
        this.mgmReferrerCode = mgmReferrerCode;
    }

    public String getMgmRefereeCode() {
        return mgmRefereeCode;
    }

    public void setMgmRefereeCode(String mgmRefereeCode) {
        this.mgmRefereeCode = mgmRefereeCode;
    }

    public String getLoyaltyMarker() {
        return loyaltyMarker;
    }

    public void setLoyaltyMarker(String loyaltyMarker) {
        this.loyaltyMarker = loyaltyMarker;
    }

    public String getGuarantorContactId() {
        return guarantorContactId;
    }

    public void setGuarantorContactId(String guarantorContactId) {
        this.guarantorContactId = guarantorContactId;
    }

    public String getGuarantorAddressId() {
        return guarantorAddressId;
    }

    public void setGuarantorAddressId(String guarantorAddressId) {
        this.guarantorAddressId = guarantorAddressId;
    }

    public Date getGuarantorStartDate() {
        return guarantorStartDate;
    }

    public void setGuarantorStartDate(Date guarantorStartDate) {
        this.guarantorStartDate = guarantorStartDate;
    }

    public Date getGuarantorExpireDate() {
        return guarantorExpireDate;
    }

    public void setGuarantorExpireDate(Date guarantorExpireDate) {
        this.guarantorExpireDate = guarantorExpireDate;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getUserIdentity() {
        return userIdentity;
    }

    public void setUserIdentity(String userIdentity) {
        this.userIdentity = userIdentity;
    }

    public BigInteger getLoginnowId() {
        return loginnowId;
    }

    public void setLoginnowId(BigInteger loginnowId) {
        this.loginnowId = loginnowId;
    }

    public String getLoginnowAuthorized() {
        return loginnowAuthorized;
    }

    public void setLoginnowAuthorized(String loginnowAuthorized) {
        this.loginnowAuthorized = loginnowAuthorized;
    }
    
    public String getPortinInd() {
        return portinInd;
    }

    public void setPortinInd(String portinInd) {
        this.portinInd = portinInd;
    }
    
    public String getDno() {
        return dno;
    }

    public void setDno(String dno) {
        this.dno = dno;
    }
    
    public String getBuyoutNum() {
        return buyoutNum;
    }

    public void setBuyoutNum(String buyoutNum) {
        this.buyoutNum = buyoutNum;
    }
    
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public EpcContact getContact() {
        return contact;
    }

    public void setContact(EpcContact contact) {
        this.contact = contact;
    }
    
    public String getCrmLabel() {
        return crmLabel;
    }

    public void setCrmLabel(String crmLabel) {
        this.crmLabel = crmLabel;
    }
    
    public String getPlanDesc() {
        return planDesc;
    }

    public void setPlanDesc(String planDesc) {
        this.planDesc = planDesc;
    }
    
    public String getPlanEndDate() {
        return planEndDate;
    }

    public void setPlanEndDate(String planEndDate) {
        this.planEndDate = planEndDate;
    }
    
    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
    
    public String getRegisterAddressId() {
        return registerAddressId;
    }

    public void setRegisterAddressId(String registerAddressId) {
        this.registerAddressId = registerAddressId;
    }
    
    public String getCommAddressId() {
        return commAddressId;
    }

    public void setCommAddressId(String commAddressId) {
        this.commAddressId = commAddressId;
    }
    
    public EpcAddress getRegisterAddress() {
        return registerAddress;
    }

    public void setRegisterAddress(EpcAddress registerAddress) {
        this.registerAddress = registerAddress;
    }
    
    public EpcAddress getCommAddresss() {
        return commAddress;
    }

    public void setCommAddress(EpcAddress commAddress) {
        this.commAddress = commAddress;
    }
    
    public EpcContact getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(EpcContact userProfile) {
        this.userProfile = userProfile;
    }
    
    public String getSubrName() {
        return subrName;
    }

    public void setSubrName(String subrName) {
        this.subrName = subrName;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recId != null ? recId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EpcSubscriber)) {
            return false;
        }
        EpcSubscriber other = (EpcSubscriber) object;
        if ((this.recId == null && other.recId != null) || (this.recId != null && !this.recId.equals(other.recId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "epc.jpa.EpcSubscriber[ recId=" + recId + " ]";
    }
    
}
