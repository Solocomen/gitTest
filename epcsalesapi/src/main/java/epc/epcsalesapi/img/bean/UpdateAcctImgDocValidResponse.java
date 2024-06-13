package epc.epcsalesapi.img.bean;

public class UpdateAcctImgDocValidResponse
{
    public static final String SUCCESS = "OK";
    public static final String FAILED = "ERROR";

    private String status;
    private String errMsg;
    private Integer docTypeId;
    private Long seqNo;
    private String valid;

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getErrMsg()
    {
        return errMsg;
    }

    public void setErrMsg(String errMsg)
    {
        this.errMsg = errMsg;
    }

    public Integer getDocTypeId()
    {
        return docTypeId;
    }

    public void setDocTypeId(Integer docTypeId)
    {
        this.docTypeId = docTypeId;
    }

    public Long getSeqNo()
    {
        return seqNo;
    }

    public void setSeqNo(Long seqNo)
    {
        this.seqNo = seqNo;
    }

    public String getValid()
    {
        return valid;
    }

    public void setValid(String valid)
    {
        this.valid = valid;
    }

}
