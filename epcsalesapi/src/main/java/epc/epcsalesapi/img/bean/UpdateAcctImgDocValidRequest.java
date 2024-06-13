package epc.epcsalesapi.img.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UpdateAcctImgDocValidRequest
{

    @JsonIgnore
    private transient int orderId;

    private Integer docTypeId;

    private Long seqNo;

    private String valid;

    private String module = "EPC";

    public UpdateAcctImgDocValidRequest(int orderId, long seqNo, ImgDocType imgDocType, ImgValid imgValid)
    {
        this.orderId = orderId;
        this.docTypeId = imgDocType.value;
        this.seqNo = seqNo;
        this.valid = imgValid.value;
    }

    public int getOrderId()
    {
        return orderId;
    }

    public void setOrderId(int orderId)
    {
        this.orderId = orderId;
    }

    public Integer getDocTypeId()
    {
        return docTypeId;
    }

    public void setDocTypeId(ImgDocType imgDocType)
    {
        this.docTypeId = imgDocType.value;
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

    public void setValid(ImgValid imgValid)
    {
        this.valid = imgValid.value;
    }

    public String getModule()
    {
        return module;
    }

    public void setModule(String module)
    {
        this.module = module;
    }
}
