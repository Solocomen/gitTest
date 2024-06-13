package epc.epcsalesapi.img.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import epc.epcsalesapi.helper.EpcCrypto;
import org.apache.commons.lang.StringUtils;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InsertSimpleImgRequest
{
    @JsonIgnore
    private transient int orderId;

    @JsonIgnore
    private String hkid;

    private String acctNo;
    private String mobileNo;
    private Integer docTypeId;
    private String acctImg;
    private String imageType;
    private String module = "EPC";
    private String referenceNumber;
    private String createdBy = "EPC";
    private String encrypted = "Y";

    @JsonIgnore
    private String idbrOcr;

    @JsonIgnore
    private String branchCode;

    @JsonIgnore
    private String businessEngName;

    @JsonIgnore
    private String businessChiName;

    @JsonIgnore
    private String branchEngName;

    @JsonIgnore
    private String branchChiName;

    @JsonIgnore
    private String brExpiryDate;

    @JsonIgnore
    private String engName;

    @JsonIgnore
    private String chiName;

    @JsonIgnore
    private String engFirstName;

    @JsonIgnore
    private String engLastName;

    @JsonIgnore
    private String chiFirstName;

    @JsonIgnore
    private String chiLastName;

    @JsonIgnore
    private String gender;

    @JsonIgnore
    private String dob;

    public InsertSimpleImgRequest(int orderId, String referenceNumber, String hkid, String acctNo, String mobileNo, int docTypeId, String imageType, String acctImg, boolean isRequireEncrypt)
    {
        this.orderId = orderId;
        this.referenceNumber = StringUtils.defaultIfBlank(referenceNumber, null);
        this.hkid = StringUtils.defaultIfBlank(hkid, null);
        this.acctNo = StringUtils.defaultIfBlank(acctNo, null);
        this.mobileNo = StringUtils.defaultIfBlank(mobileNo, null);
        this.docTypeId = docTypeId;
        this.imageType = StringUtils.defaultIfBlank(imageType, null);
        this.acctImg = StringUtils.defaultIfBlank(acctImg, null);

        if (isRequireEncrypt)
        {
            this.encrypted = "Y";

            try
            {
                if (this.hkid != null)
                {
                    this.hkid = EpcCrypto.eGet(this.hkid);
                }

                if (this.acctImg != null)
                {
                    this.acctImg = EpcCrypto.eGet(this.acctImg);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        else
        {
            this.encrypted = "N";
        }
    }

    public int getOrderId()
    {
        return orderId;
    }

    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
    }

    public String getHkid()
    {
        return hkid;
    }

    public String getAcctNo()
    {
        return acctNo;
    }

    public String getMobileNo()
    {
        return mobileNo;
    }

    public Integer getDocTypeId()
    {
        return docTypeId;
    }

    public String getAcctImg()
    {
        return acctImg;
    }

    public String getImageType()
    {
        return imageType;
    }

    public String getModule()
    {
        return module;
    }

    public String getReferenceNumber()
    {
        return referenceNumber;
    }

    public String getCreatedBy()
    {
        return createdBy;
    }

    public String getEncrypted()
    {
        return encrypted;
    }

    public String getIdbrOcr()
    {
        return idbrOcr;
    }

    public void setIdbrOcr(String idbrOcr)
    {
        this.idbrOcr = idbrOcr;
    }

    public String getBranchCode()
    {
        return branchCode;
    }

    public void setBranchCode(String branchCode)
    {
        this.branchCode = branchCode;
    }

    public String getBusinessEngName()
    {
        return businessEngName;
    }

    public void setBusinessEngName(String businessEngName)
    {
        this.businessEngName = businessEngName;
    }

    public String getBusinessChiName()
    {
        return businessChiName;
    }

    public void setBusinessChiName(String businessChiName)
    {
        this.businessChiName = businessChiName;
    }

    public String getBranchEngName()
    {
        return branchEngName;
    }

    public void setBranchEngName(String branchEngName)
    {
        this.branchEngName = branchEngName;
    }

    public String getBranchChiName()
    {
        return branchChiName;
    }

    public void setBranchChiName(String branchChiName)
    {
        this.branchChiName = branchChiName;
    }

    public String getBrExpiryDate()
    {
        return brExpiryDate;
    }

    public void setBrExpiryDate(String brExpiryDate)
    {
        this.brExpiryDate = brExpiryDate;
    }

    public String getEngName()
    {
        return engName;
    }

    public void setEngName(String engName)
    {
        this.engName = engName;
    }

    public String getChiName()
    {
        return chiName;
    }

    public void setChiName(String chiName)
    {
        this.chiName = chiName;
    }

    public String getEngFirstName()
    {
        return engFirstName;
    }

    public void setEngFirstName(String engFirstName)
    {
        this.engFirstName = engFirstName;
    }

    public String getEngLastName()
    {
        return engLastName;
    }

    public void setEngLastName(String engLastName)
    {
        this.engLastName = engLastName;
    }

    public String getChiFirstName()
    {
        return chiFirstName;
    }

    public void setChiFirstName(String chiFirstName)
    {
        this.chiFirstName = chiFirstName;
    }

    public String getChiLastName()
    {
        return chiLastName;
    }

    public void setChiLastName(String chiLastName)
    {
        this.chiLastName = chiLastName;
    }

    public String getGender()
    {
        return gender;
    }

    public void setGender(String gender)
    {
        this.gender = gender;
    }

    public String getDob()
    {
        return dob;
    }

    public void setDob(String dob)
    {
        this.dob = dob;
    }
}
