package epc.epcsalesapi.img.bean;

public class EpcUploadDoc
{

    private Integer orderId;
    private String result;
    private String errMsg;
    private boolean forceUploadToImg = false;

    public Integer getOrderId()
    {
        return orderId;
    }

    public void setOrderId(Integer orderId)
    {
        this.orderId = orderId;
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

    public boolean isForceUploadToImg()
    {
        return forceUploadToImg;
    }

    public void setForceUploadToImg(boolean forceUploadToImg)
    {
        this.forceUploadToImg = forceUploadToImg;
    }
}
