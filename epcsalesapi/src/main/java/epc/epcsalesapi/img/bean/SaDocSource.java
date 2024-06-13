package epc.epcsalesapi.img.bean;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Date;

public class SaDocSource implements Serializable
{

    private String caseId;
    private Long seqNo;
    private BigInteger docTypeId;
    private String custNum;
    private String subrNum;
    private String hkidBr;
    private String isSmcapps = "N";
    private String sourceModule = "EPC";
    private Blob docSource;
    private String docSourceStr;
    private String processInd = "Y";
    private String genPdfInd = "N";
    private Date createDate;
    private String createBy;
    private Date updateDate;
    private String updateBy;

    public String getCaseId()
    {
        return caseId;
    }

    public void setCaseId(String caseId)
    {
        this.caseId = caseId;
    }

    public Long getSeqNo()
    {
        return seqNo;
    }

    public void setSeqNo(Long seqNo)
    {
        this.seqNo = seqNo;
    }

    public BigInteger getDocTypeId()
    {
        return docTypeId;
    }

    public void setDocTypeId(BigInteger docTypeId)
    {
        this.docTypeId = docTypeId;
    }

    public String getCustNum()
    {
        return custNum;
    }

    public void setCustNum(String custNum)
    {
        this.custNum = custNum;
    }

    public String getSubrNum()
    {
        return subrNum;
    }

    public void setSubrNum(String subrNum)
    {
        this.subrNum = subrNum;
    }

    public String getHkidBr()
    {
        return hkidBr;
    }

    public void setHkidBr(String hkidBr)
    {
        this.hkidBr = hkidBr;
    }

    public String getIsSmcapps()
    {
        return isSmcapps;
    }

    public void setIsSmcapps(String isSmcapps)
    {
        this.isSmcapps = isSmcapps;
    }

    public String getSourceModule()
    {
        return sourceModule;
    }

    public void setSourceModule(String sourceModule)
    {
        this.sourceModule = sourceModule;
    }

    public Blob getDocSource()
    {
        return docSource;
    }

    public void setDocSource(Blob docSource)
    {
        this.docSource = docSource;
    }

    public String getDocSourceStr()
    {
        return docSourceStr;
    }

    public void setDocSourceStr(String docSourceStr)
    {
        this.docSourceStr = docSourceStr;
    }

    public String getProcessInd()
    {
        return processInd;
    }

    public void setProcessInd(String processInd)
    {
        this.processInd = processInd;
    }

    public String getGenPdfInd()
    {
        return genPdfInd;
    }

    public void setGenPdfInd(String genPdfInd)
    {
        this.genPdfInd = genPdfInd;
    }

    public Date getCreateDate()
    {
        return createDate;
    }

    public void setCreateDate(Date createDate)
    {
        this.createDate = createDate;
    }

    public String getCreateBy()
    {
        return createBy;
    }

    public void setCreateBy(String createBy)
    {
        this.createBy = createBy;
    }

    public Date getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate)
    {
        this.updateDate = updateDate;
    }

    public String getUpdateBy()
    {
        return updateBy;
    }

    public void setUpdateBy(String updateBy)
    {
        this.updateBy = updateBy;
    }
}
