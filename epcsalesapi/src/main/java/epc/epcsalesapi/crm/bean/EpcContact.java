/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.crm.bean;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author KenTKChung
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcContact implements Serializable {

    private static final long serialVersionUID = 1L;
    private String contactId;
    private String custTitle;
    private String lastName;
    private String firstName;
    private String lastNameChi;
    private String firstNameChi;
    @JsonFormat(pattern="yyyy-MM-dd")
    private Date birth;
    private String idTypeCode;
    private String idbr;
    private String contactNum;
    private String contactNum2;
    private String contactPerson;
    private String faxNum;
    private String attention;
    private String commLangCode;
    private String nationality;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createdDate;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastUpdatedDate;
    private String createdBy;
    private String lastUpdatedBy;
    private String gender;
    private String lastNameChiCode;
    private String firstNameChiCode;
    private String fullName;
    private String fullNameChi;

    public EpcContact() {
    }

    public EpcContact(String contactId) {
        this.contactId = contactId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getCustTitle() {
        return custTitle;
    }

    public void setCustTitle(String custTitle) {
        this.custTitle = custTitle;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastNameChi() {
        return lastNameChi;
    }

    public void setLastNameChi(String lastNameChi) {
        this.lastNameChi = lastNameChi;
    }

    public String getFirstNameChi() {
        return firstNameChi;
    }

    public void setFirstNameChi(String firstNameChi) {
        this.firstNameChi = firstNameChi;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

    public String getIdTypeCode() {
        return idTypeCode;
    }

    public void setIdTypeCode(String idTypeCode) {
        this.idTypeCode = idTypeCode;
    }

    public String getIdbr() {
        return idbr;
    }

    public void setIdbr(String idbr) {
        this.idbr = idbr;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getContactNum2() {
        return contactNum2;
    }

    public void setContactNum2(String contactNum2) {
        this.contactNum2 = contactNum2;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getFaxNum() {
        return faxNum;
    }

    public void setFaxNum(String faxNum) {
        this.faxNum = faxNum;
    }

    public String getAttention() {
        return attention;
    }

    public void setAttention(String attention) {
        this.attention = attention;
    }

    public String getCommLangCode() {
        return commLangCode;
    }

    public void setCommLangCode(String commLangCode) {
        this.commLangCode = commLangCode;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getLastNameChiCode() {
        return lastNameChiCode;
    }

    public void setLastNameChiCode(String lastNameChiCode) {
        this.lastNameChiCode = lastNameChiCode;
    }

    public String getFirstNameChiCode() {
        return firstNameChiCode;
    }

    public void setFirstNameChiCode(String firstNameChiCode) {
        this.firstNameChiCode = firstNameChiCode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getFullNameChi() {
        return fullNameChi;
    }

    public void setFullNameChi(String fullNameChi) {
        this.fullNameChi = fullNameChi;
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (contactId != null ? contactId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EpcContact)) {
            return false;
        }
        EpcContact other = (EpcContact) object;
        if ((this.contactId == null && other.contactId != null) || (this.contactId != null && !this.contactId.equals(other.contactId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "epc.jpa.EpcContact[ contactId=" + contactId + " ]";
    }
    
}
