package epc.epcsalesapi.img.bean;

import epc.epcsalesapi.sales.bean.orderAttachment.EpcOrderAttachType;

public enum ImgDocType
{
    DUMMY(-1),
    SA(1),
    DN(141);

    public final Integer value;

    private ImgDocType(Integer value)
    {
        this.value = value;
    }

    public static ImgDocType fromEpcDocType(String epcDocType)
    {
        ImgDocType imgDocType = null;

        imgDocType = switch (epcDocType)
        {
            case EpcOrderAttachType.SALES_AGREEMENT ->
                SA;
            case EpcOrderAttachType.DELIVERY_NOTE ->
                DN;
            default ->
                DUMMY;
        };

        return imgDocType;
    }

    public static ImgDocType fromDocTypeId(int value)
    {
        for (ImgDocType imgDocType : values())
        {
            if (imgDocType.value == value)
            {
                return imgDocType;
            }
        }

        return ImgDocType.DUMMY;
    }
}
