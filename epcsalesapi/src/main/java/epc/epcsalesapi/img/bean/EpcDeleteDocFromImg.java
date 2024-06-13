package epc.epcsalesapi.img.bean;

public class EpcDeleteDocFromImg
{

    private int recId;
    private String result;
    private String errMsg;

    public EpcDeleteDocFromImg(int recId)
    {
        this.recId = recId;
    }

    public int getRecId()
    {
        return recId;
    }

    public void setRecId(int recId)
    {
        this.recId = recId;
    }

    public String getResult()
    {
        return result;
    }

    public void setResult(String result)
    {
        this.result = result;
    }

    public String getErrMsg()
    {
        return errMsg;
    }

    public void setErrMsg(String errMsg)
    {
        this.errMsg = errMsg;
    }

}
