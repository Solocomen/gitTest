package epc.epcsalesapi.img.bean;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

public class AcctImage implements Serializable
{

    private static final long serialVersionUID = 1L;

    private String hkid;

    private String acctNo;

    private String mobileNo;

    private BigInteger docTypeId;

    private Serializable acctImg;

    private String imageType;

    private BigInteger systemId;

    private String module;

    private String referenceNumber;

    private Date creationDate;

    private String createdBy;

    private Long seqNo;

    private String esignature;

    private String saLogid;

    private String invoiceNo;

    private String processInd;

    private String emailAddress;

    private String authorizedHkidBr;
    private Long encvers;

    private String encrypted;

    private String branchCode;

    private Date brExpiryDate;

    private String ocrHkidBr;

    private String ocrIdType;

    private String ocrEngName;

    private String ocrEngFirstName;

    private String ocrEngLastName;

    private String ocrChiName;

    private String ocrChiFirstName;

    private String ocrChiLastName;

    private String ocrGender;

    private String ocrBirth;

    private String ocrBranchEngName;

    private String ocrBranchChiName;

    private String ocrLogid;

    public AcctImage()
    {
    }

    public AcctImage(Long seqNo)
    {
        this.seqNo = seqNo;
    }

    public AcctImage(Long seqNo, BigInteger docTypeId, String imageType, Date creationDate, String createdBy)
    {
        this.seqNo = seqNo;
        this.docTypeId = docTypeId;
        this.imageType = imageType;
        this.creationDate = creationDate;
        this.createdBy = createdBy;
    }

    public String getHkid()
    {
        return hkid;
    }

    public void setHkid(String hkid)
    {
        this.hkid = hkid;
    }

    public String getAcctNo()
    {
        return acctNo;
    }

    public void setAcctNo(String acctNo)
    {
        this.acctNo = acctNo;
    }

    public String getMobileNo()
    {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo)
    {
        this.mobileNo = mobileNo;
    }

    public BigInteger getDocTypeId()
    {
        return docTypeId;
    }

    public void setDocTypeId(BigInteger docTypeId)
    {
        this.docTypeId = docTypeId;
    }

    public Serializable getAcctImg()
    {
        return acctImg;
    }

    public void setAcctImg(Serializable acctImg)
    {
        this.acctImg = acctImg;
    }

    public String getImageType()
    {
        return imageType;
    }

    public void setImageType(String imageType)
    {
        this.imageType = imageType;
    }

    public BigInteger getSystemId()
    {
        return systemId;
    }

    public void setSystemId(BigInteger systemId)
    {
        this.systemId = systemId;
    }

    public String getModule()
    {
        return module;
    }

    public void setModule(String module)
    {
        this.module = module;
    }

    public String getReferenceNumber()
    {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber)
    {
        this.referenceNumber = referenceNumber;
    }

    public Date getCreationDate()
    {
        return creationDate;
    }

    public void setCreationDate(Date creationDate)
    {
        this.creationDate = creationDate;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public void setCreatedBy(String createdBy)
    {
        this.createdBy = createdBy;
    }

    public Long getSeqNo()
    {
        return seqNo;
    }

    public void setSeqNo(Long seqNo)
    {
        this.seqNo = seqNo;
    }

    public String getEsignature()
    {
        return esignature;
    }

    public void setEsignature(String esignature)
    {
        this.esignature = esignature;
    }

    public String getSaLogid()
    {
        return saLogid;
    }

    public void setSaLogid(String saLogid)
    {
        this.saLogid = saLogid;
    }

    public String getInvoiceNo()
    {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo)
    {
        this.invoiceNo = invoiceNo;
    }

    public String getProcessInd()
    {
        return processInd;
    }

    public void setProcessInd(String processInd)
    {
        this.processInd = processInd;
    }

    public void setProcessInd(boolean isProcessed)
    {
        if (isProcessed)
        {
            this.processInd = "Y";
        }
        else
        {
            this.processInd = "D";
        }
    }

    public String getEmailAddress()
    {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

    public String getAuthorizedHkidBr()
    {
        return authorizedHkidBr;
    }

    public void setAuthorizedHkidBr(String authorizedHkidBr)
    {
        this.authorizedHkidBr = authorizedHkidBr;
    }

    public Long getEncvers()
    {
        return encvers;
    }

    public void setEncvers(Long encvers)
    {
        this.encvers = encvers;
    }

    public String getEncrypted()
    {
        return encrypted;
    }

    public void setEncrypted(String encrypted)
    {
        this.encrypted = encrypted;
    }

    public String getBranchCode()
    {
        return branchCode;
    }

    public void setBranchCode(String branchCode)
    {
        this.branchCode = branchCode;
    }

    public Date getBrExpiryDate()
    {
        return brExpiryDate;
    }

    public void setBrExpiryDate(Date brExpiryDate)
    {
        this.brExpiryDate = brExpiryDate;
    }

    public String getOcrHkidBr()
    {
        return ocrHkidBr;
    }

    public void setOcrHkidBr(String ocrHkidBr)
    {
        this.ocrHkidBr = ocrHkidBr;
    }

    public String getOcrIdType()
    {
        return ocrIdType;
    }

    public void setOcrIdType(String ocrIdType)
    {
        this.ocrIdType = ocrIdType;
    }

    public String getOcrEngName()
    {
        return ocrEngName;
    }

    public void setOcrEngName(String ocrEngName)
    {
        this.ocrEngName = ocrEngName;
    }

    public String getOcrEngFirstName()
    {
        return ocrEngFirstName;
    }

    public void setOcrEngFirstName(String ocrEngFirstName)
    {
        this.ocrEngFirstName = ocrEngFirstName;
    }

    public String getOcrEngLastName()
    {
        return ocrEngLastName;
    }

    public void setOcrEngLastName(String ocrEngLastName)
    {
        this.ocrEngLastName = ocrEngLastName;
    }

    public String getOcrChiName()
    {
        return ocrChiName;
    }

    public void setOcrChiName(String ocrChiName)
    {
        this.ocrChiName = ocrChiName;
    }

    public String getOcrChiFirstName()
    {
        return ocrChiFirstName;
    }

    public void setOcrChiFirstName(String ocrChiFirstName)
    {
        this.ocrChiFirstName = ocrChiFirstName;
    }

    public String getOcrChiLastName()
    {
        return ocrChiLastName;
    }

    public void setOcrChiLastName(String ocrChiLastName)
    {
        this.ocrChiLastName = ocrChiLastName;
    }

    public String getOcrGender()
    {
        return ocrGender;
    }

    public void setOcrGender(String ocrGender)
    {
        this.ocrGender = ocrGender;
    }

    public String getOcrBirth()
    {
        return ocrBirth;
    }

    public void setOcrBirth(String ocrBirth)
    {
        this.ocrBirth = ocrBirth;
    }

    public String getOcrBranchEngName()
    {
        return ocrBranchEngName;
    }

    public void setOcrBranchEngName(String ocrBranchEngName)
    {
        this.ocrBranchEngName = ocrBranchEngName;
    }

    public String getOcrBranchChiName()
    {
        return ocrBranchChiName;
    }

    public void setOcrBranchChiName(String ocrBranchChiName)
    {
        this.ocrBranchChiName = ocrBranchChiName;
    }

    public String getOcrLogid()
    {
        return ocrLogid;
    }

    public void setOcrLogid(String ocrLogid)
    {
        this.ocrLogid = ocrLogid;
    }

    @Override
    public int hashCode()
    {
        int hash = 0;
        hash += (seqNo != null ? seqNo.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object)
    {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AcctImage))
        {
            return false;
        }
        AcctImage other = (AcctImage) object;
        if ((this.seqNo == null && other.seqNo != null) || (this.seqNo != null && !this.seqNo.equals(other.seqNo)))
        {
            return false;
        }
        return true;
    }

    @Override
    public String toString()
    {
        return "epc.epcsalesapi.img.bean.AcctImage[ seqNo=" + seqNo + " ]";
    }

}
