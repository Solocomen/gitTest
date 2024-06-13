package epc.epcsalesapi.img.bean;

import java.io.Serializable;
import java.math.BigInteger;

public class DocType implements Serializable
{

    private BigInteger docTypeId;
    private String docTypeDesc;
    private String abbreviation;
    private String displayFlag;
    private Integer displayOrd;
    private String missing_rptInd;
    private String myAccountDisp;
    private Integer myAccountDispOrd;
    private String myAccountEngDesc;
    private String myAccountChiDesc;
    private String myAccountJapDesc;
    private String myAccount_korDesc;
    private String docFilename;
    private String check_void;
    private String rescanDisplayFlag;
    private String printFlag;
    private String pciFlag;
    private BigInteger pageDispOrd;

    public BigInteger getDocTypeId()
    {
        return docTypeId;
    }

    public void setDocTypeId(BigInteger docTypeId)
    {
        this.docTypeId = docTypeId;
    }

    public String getDocTypeDesc()
    {
        return docTypeDesc;
    }

    public void setDocTypeDesc(String docTypeDesc)
    {
        this.docTypeDesc = docTypeDesc;
    }

    public String getAbbreviation()
    {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation)
    {
        this.abbreviation = abbreviation;
    }

    public String getDisplayFlag()
    {
        return displayFlag;
    }

    public void setDisplayFlag(String displayFlag)
    {
        this.displayFlag = displayFlag;
    }

    public Integer getDisplayOrd()
    {
        return displayOrd;
    }

    public void setDisplayOrd(Integer displayOrd)
    {
        this.displayOrd = displayOrd;
    }

    public String getMissing_rptInd()
    {
        return missing_rptInd;
    }

    public void setMissing_rptInd(String missing_rptInd)
    {
        this.missing_rptInd = missing_rptInd;
    }

    public String getMyAccountDisp()
    {
        return myAccountDisp;
    }

    public void setMyAccountDisp(String myAccountDisp)
    {
        this.myAccountDisp = myAccountDisp;
    }

    public Integer getMyAccountDispOrd()
    {
        return myAccountDispOrd;
    }

    public void setMyAccountDispOrd(Integer myAccountDispOrd)
    {
        this.myAccountDispOrd = myAccountDispOrd;
    }

    public String getMyAccountEngDesc()
    {
        return myAccountEngDesc;
    }

    public void setMyAccountEngDesc(String myAccountEngDesc)
    {
        this.myAccountEngDesc = myAccountEngDesc;
    }

    public String getMyAccountChiDesc()
    {
        return myAccountChiDesc;
    }

    public void setMyAccountChiDesc(String myAccountChiDesc)
    {
        this.myAccountChiDesc = myAccountChiDesc;
    }

    public String getMyAccountJapDesc()
    {
        return myAccountJapDesc;
    }

    public void setMyAccountJapDesc(String myAccountJapDesc)
    {
        this.myAccountJapDesc = myAccountJapDesc;
    }

    public String getMyAccount_korDesc()
    {
        return myAccount_korDesc;
    }

    public void setMyAccount_korDesc(String myAccount_korDesc)
    {
        this.myAccount_korDesc = myAccount_korDesc;
    }

    public String getDocFilename()
    {
        return docFilename;
    }

    public void setDocFilename(String docFilename)
    {
        this.docFilename = docFilename;
    }

    public String getCheck_void()
    {
        return check_void;
    }

    public void setCheck_void(String check_void)
    {
        this.check_void = check_void;
    }

    public String getRescanDisplayFlag()
    {
        return rescanDisplayFlag;
    }

    public void setRescanDisplayFlag(String rescanDisplayFlag)
    {
        this.rescanDisplayFlag = rescanDisplayFlag;
    }

    public String getPrintFlag()
    {
        return printFlag;
    }

    public void setPrintFlag(String printFlag)
    {
        this.printFlag = printFlag;
    }

    public String getPciFlag()
    {
        return pciFlag;
    }

    public void setPciFlag(String pciFlag)
    {
        this.pciFlag = pciFlag;
    }

    public BigInteger getPageDispOrd()
    {
        return pageDispOrd;
    }

    public void setPageDispOrd(BigInteger pageDispOrd)
    {
        this.pageDispOrd = pageDispOrd;
    }

}
