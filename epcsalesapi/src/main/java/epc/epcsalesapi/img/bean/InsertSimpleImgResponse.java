package epc.epcsalesapi.img.bean;

public class InsertSimpleImgResponse
{
    public static final String SUCCESS = "OK";
    public static final String FAILED = "ERROR";

    private String status;
    private String errMsg;
    private Long seqNo;

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

    public Long getSeqNo()
    {
        return seqNo;
    }

    public void setSeqNo(Long seqNo)
    {
        this.seqNo = seqNo;
    }

}
