/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.fes.sa.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
//import javax.persistence.Basic;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.Id;
//import javax.persistence.NamedQueries;
//import javax.persistence.NamedQuery;
//import javax.persistence.Table;
//import javax.persistence.Temporal;
//import javax.persistence.TemporalType;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author KerryTsang
 */
//@Entity
//@Table(name = "ZZ_PSERVICE")
//@XmlRootElement
//@NamedQueries({
//    @NamedQuery(name = "ZzPservice.findAll", query = "SELECT z FROM ZzPservice z")
//    , @NamedQuery(name = "ZzPservice.findByServiceCode", query = "SELECT z FROM ZzPservice z WHERE z.serviceCode = :serviceCode")
//    , @NamedQuery(name = "ZzPservice.findByServiceCodes", query = "SELECT z FROM ZzPservice z WHERE z.serviceCode in :serviceCodes")
//})
public class ZzPservice implements Serializable {

    private static final long serialVersionUID = 1L;
//    @Id
//    @Basic(optional = false)
//    @NotNull
//    @Size(min = 1, max = 10)
//    @Column(name = "SERVICE_CODE")
    private String serviceCode;
//    @Size(max = 1)
//    @Column(name = "SERVICE_IND")
    private String serviceInd;
//    @Size(max = 40)
//    @Column(name = "SERVICE_DESC")
    private String serviceDesc;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
//    @Column(name = "PRICE")
    private BigDecimal price;
//    @Size(max = 1)
//    @Column(name = "AMD_PRICE")
    private String amdPrice;
//    @Size(max = 1)
//    @Column(name = "SUB_IND")
    private String subInd;
//    @Size(max = 1)
//    @Column(name = "PREPAY_IND")
    private String prepayInd;
//    @Size(max = 1)
//    @Column(name = "DEPOSIT_IND")
    private String depositInd;
//    @Size(max = 5)
//    @Column(name = "JUP_TX_TYPE")
    private String jupTxType;
//    @Size(max = 1)
//    @Column(name = "STATUS_IND")
    private String statusInd;
//    @Column(name = "LAST_MOD_DATE")
//    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModDate;
//    @Size(max = 8)
//    @Column(name = "LAST_MOD_TIME")
    private String lastModTime;
//    @Column(name = "FES_USERID")
    private Long fesUserid;
//    @Size(max = 1)
//    @Column(name = "JUPITER_IND")
    private String jupiterInd;
//    @Size(max = 1)
//    @Column(name = "PREORDER_IND")
    private String preorderInd;
//    @Size(max = 40)
//    @Column(name = "RECEIPT_SRV_DESC")
    private String receiptSrvDesc;
//    @Size(max = 40)
//    @Column(name = "RECEIPT_SRV_DESC_CHI")
    private String receiptSrvDescChi;
//    @Basic(optional = false)
//    @NotNull
//    @Size(min = 1, max = 1)
//    @Column(name = "GENEVA_IND")
    private String genevaInd;
//    @Size(max = 1)
//    @Column(name = "CLP_NEW_CUST")
    private String clpNewCust;
//    @Size(max = 1)
//    @Column(name = "CLP_EXIST_CUST")
    private String clpExistCust;
//    @Size(max = 80)
//    @Column(name = "REMARKS")
    private String remarks;
//    @Size(max = 1)
//    @Column(name = "KIOSK_IND")
    private String kioskInd;
    
//  @Size(max = 1)
//  @Column(name = "SETTLE_SHORT_CUT_KEY")
    private String settleShortCutKey;
    
//  @Size(max = 1)
//  @Column(name = "CREDIT_CARD_TYPE")
    private String creditCardType;

    public ZzPservice() {
    }

    public ZzPservice(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public ZzPservice(String serviceCode, String genevaInd) {
        this.serviceCode = serviceCode;
        this.genevaInd = genevaInd;
    }

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public String getServiceInd() {
        return serviceInd;
    }

    public void setServiceInd(String serviceInd) {
        this.serviceInd = serviceInd;
    }

    public String getServiceDesc() {
        return serviceDesc;
    }

    public void setServiceDesc(String serviceDesc) {
        this.serviceDesc = serviceDesc;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getAmdPrice() {
        return amdPrice;
    }

    public void setAmdPrice(String amdPrice) {
        this.amdPrice = amdPrice;
    }

    public String getSubInd() {
        return subInd;
    }

    public void setSubInd(String subInd) {
        this.subInd = subInd;
    }

    public String getPrepayInd() {
        return prepayInd;
    }

    public void setPrepayInd(String prepayInd) {
        this.prepayInd = prepayInd;
    }

    public String getDepositInd() {
        return depositInd;
    }

    public void setDepositInd(String depositInd) {
        this.depositInd = depositInd;
    }

    public String getJupTxType() {
        return jupTxType;
    }

    public void setJupTxType(String jupTxType) {
        this.jupTxType = jupTxType;
    }

    public String getStatusInd() {
        return statusInd;
    }

    public void setStatusInd(String statusInd) {
        this.statusInd = statusInd;
    }

    public Date getLastModDate() {
        return lastModDate;
    }

    public void setLastModDate(Date lastModDate) {
        this.lastModDate = lastModDate;
    }

    public String getLastModTime() {
        return lastModTime;
    }

    public void setLastModTime(String lastModTime) {
        this.lastModTime = lastModTime;
    }

    public Long getFesUserid() {
        return fesUserid;
    }

    public void setFesUserid(Long fesUserid) {
        this.fesUserid = fesUserid;
    }

    public String getJupiterInd() {
        return jupiterInd;
    }

    public void setJupiterInd(String jupiterInd) {
        this.jupiterInd = jupiterInd;
    }

    public String getPreorderInd() {
        return preorderInd;
    }

    public void setPreorderInd(String preorderInd) {
        this.preorderInd = preorderInd;
    }

    public String getReceiptSrvDesc() {
        return receiptSrvDesc;
    }

    public void setReceiptSrvDesc(String receiptSrvDesc) {
        this.receiptSrvDesc = receiptSrvDesc;
    }

    public String getReceiptSrvDescChi() {
        return receiptSrvDescChi;
    }

    public void setReceiptSrvDescChi(String receiptSrvDescChi) {
        this.receiptSrvDescChi = receiptSrvDescChi;
    }

    public String getGenevaInd() {
        return genevaInd;
    }

    public void setGenevaInd(String genevaInd) {
        this.genevaInd = genevaInd;
    }

    public String getClpNewCust() {
        return clpNewCust;
    }

    public void setClpNewCust(String clpNewCust) {
        this.clpNewCust = clpNewCust;
    }

    public String getClpExistCust() {
        return clpExistCust;
    }

    public void setClpExistCust(String clpExistCust) {
        this.clpExistCust = clpExistCust;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getKioskInd() {
        return kioskInd;
    }

    public void setKioskInd(String kioskInd) {
        this.kioskInd = kioskInd;
    }

    public String getSettleShortCutKey() {
        return settleShortCutKey;
    }

    public void setSettleShortCutKey(String settleShortCutKey) {
        this.settleShortCutKey = settleShortCutKey;
    }

    public String getCreditCardType() {
        return creditCardType;
    }

    public void setCreditCardType(String creditCardType) {
        this.creditCardType = creditCardType;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceCode != null ? serviceCode.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ZzPservice)) {
            return false;
        }
        ZzPservice other = (ZzPservice) object;
        if ((this.serviceCode == null && other.serviceCode != null) || (this.serviceCode != null && !this.serviceCode.equals(other.serviceCode))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "fes.jpa.ZzPservice[ serviceCode=" + serviceCode + " ]";
    }
    
}
