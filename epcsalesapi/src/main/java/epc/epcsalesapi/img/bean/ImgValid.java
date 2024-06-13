package epc.epcsalesapi.img.bean;

public enum ImgValid
{
    TRUE("Y"),
    FALSE("N");

    public final String value;

    private ImgValid(String value)
    {
        this.value = value;
    }
}
