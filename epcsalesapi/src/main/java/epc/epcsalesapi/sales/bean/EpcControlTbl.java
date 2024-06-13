/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author williamtam
 */

public class EpcControlTbl implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal recId;

    private String recType;

    private String recDesc;

    private String keyStr1;

    private String keyStr2;

    private String keyStr3;

    private String keyStr4;

    private String keyStr5;

    private LocalDateTime keyDate1;

    private LocalDateTime keyDate2;

    private LocalDateTime keyDate3;

    private BigDecimal keyNumber1;

    private BigDecimal keyNumber2;

    private BigDecimal keyNumber3;

    private String valueStr1;

    private String valueStr2;

    private String valueStr3;

    private String valueStr4;

    private String valueStr5;

    private LocalDateTime valueDate1;

    private LocalDateTime valueDate2;

    private LocalDateTime valueDate3;

    private LocalDateTime valueDate4;

    private LocalDateTime valueDate5;

    private BigDecimal valueNumber1;

    private BigDecimal valueNumber2;

    private BigDecimal valueNumber3;

    private BigDecimal valueNumber4;

    private BigDecimal valueNumber5;

    public EpcControlTbl() {
    }

    public EpcControlTbl(BigDecimal recId) {
        this.recId = recId;
    }

    public BigDecimal getRecId() {
        return recId;
    }

    public void setRecId(BigDecimal recId) {
        this.recId = recId;
    }

    public String getRecType() {
        return recType;
    }

    public void setRecType(String recType) {
        this.recType = recType;
    }

    public String getRecDesc() {
        return recDesc;
    }

    public void setRecDesc(String recDesc) {
        this.recDesc = recDesc;
    }

    public String getKeyStr1() {
        return keyStr1;
    }

    public void setKeyStr1(String keyStr1) {
        this.keyStr1 = keyStr1;
    }

    public String getKeyStr2() {
        return keyStr2;
    }

    public void setKeyStr2(String keyStr2) {
        this.keyStr2 = keyStr2;
    }

    public String getKeyStr3() {
        return keyStr3;
    }

    public void setKeyStr3(String keyStr3) {
        this.keyStr3 = keyStr3;
    }

    public String getKeyStr4() {
        return keyStr4;
    }

    public void setKeyStr4(String keyStr4) {
        this.keyStr4 = keyStr4;
    }

    public String getKeyStr5() {
        return keyStr5;
    }

    public void setKeyStr5(String keyStr5) {
        this.keyStr5 = keyStr5;
    }

    public LocalDateTime getKeyDate1() {
        return keyDate1;
    }

    public void setKeyDate1(LocalDateTime keyDate1) {
        this.keyDate1 = keyDate1;
    }

    public LocalDateTime getKeyDate2() {
        return keyDate2;
    }

    public void setKeyDate2(LocalDateTime keyDate2) {
        this.keyDate2 = keyDate2;
    }

    public LocalDateTime getKeyDate3() {
        return keyDate3;
    }

    public void setKeyDate3(LocalDateTime keyDate3) {
        this.keyDate3 = keyDate3;
    }

    public BigDecimal getKeyNumber1() {
        return keyNumber1;
    }

    public void setKeyNumber1(BigDecimal keyNumber1) {
        this.keyNumber1 = keyNumber1;
    }

    public BigDecimal getKeyNumber2() {
        return keyNumber2;
    }

    public void setKeyNumber2(BigDecimal keyNumber2) {
        this.keyNumber2 = keyNumber2;
    }

    public BigDecimal getKeyNumber3() {
        return keyNumber3;
    }

    public void setKeyNumber3(BigDecimal keyNumber3) {
        this.keyNumber3 = keyNumber3;
    }

    public String getValueStr1() {
        return valueStr1;
    }

    public void setValueStr1(String valueStr1) {
        this.valueStr1 = valueStr1;
    }

    public String getValueStr2() {
        return valueStr2;
    }

    public void setValueStr2(String valueStr2) {
        this.valueStr2 = valueStr2;
    }

    public String getValueStr3() {
        return valueStr3;
    }

    public void setValueStr3(String valueStr3) {
        this.valueStr3 = valueStr3;
    }

    public String getValueStr4() {
        return valueStr4;
    }

    public void setValueStr4(String valueStr4) {
        this.valueStr4 = valueStr4;
    }

    public String getValueStr5() {
        return valueStr5;
    }

    public void setValueStr5(String valueStr5) {
        this.valueStr5 = valueStr5;
    }

    public LocalDateTime getValueDate1() {
        return valueDate1;
    }

    public void setValueDate1(LocalDateTime valueDate1) {
        this.valueDate1 = valueDate1;
    }

    public LocalDateTime getValueDate2() {
        return valueDate2;
    }

    public void setValueDate2(LocalDateTime valueDate2) {
        this.valueDate2 = valueDate2;
    }

    public LocalDateTime getValueDate3() {
        return valueDate3;
    }

    public void setValueDate3(LocalDateTime valueDate3) {
        this.valueDate3 = valueDate3;
    }

    public LocalDateTime getValueDate4() {
        return valueDate4;
    }

    public void setValueDate4(LocalDateTime valueDate4) {
        this.valueDate4 = valueDate4;
    }

    public LocalDateTime getValueDate5() {
        return valueDate5;
    }

    public void setValueDate5(LocalDateTime valueDate5) {
        this.valueDate5 = valueDate5;
    }

    public BigDecimal getValueNumber1() {
        return valueNumber1;
    }

    public void setValueNumber1(BigDecimal valueNumber1) {
        this.valueNumber1 = valueNumber1;
    }

    public BigDecimal getValueNumber2() {
        return valueNumber2;
    }

    public void setValueNumber2(BigDecimal valueNumber2) {
        this.valueNumber2 = valueNumber2;
    }

    public BigDecimal getValueNumber3() {
        return valueNumber3;
    }

    public void setValueNumber3(BigDecimal valueNumber3) {
        this.valueNumber3 = valueNumber3;
    }

    public BigDecimal getValueNumber4() {
        return valueNumber4;
    }

    public void setValueNumber4(BigDecimal valueNumber4) {
        this.valueNumber4 = valueNumber4;
    }

    public BigDecimal getValueNumber5() {
        return valueNumber5;
    }

    public void setValueNumber5(BigDecimal valueNumber5) {
        this.valueNumber5 = valueNumber5;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (recId != null ? recId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof EpcControlTbl)) {
            return false;
        }
        EpcControlTbl other = (EpcControlTbl) object;
        if ((this.recId == null && other.recId != null) || (this.recId != null && !this.recId.equals(other.recId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "epc.jpa.EpcControlTbl[ recId=" + recId + " ]";
    }
    
}
