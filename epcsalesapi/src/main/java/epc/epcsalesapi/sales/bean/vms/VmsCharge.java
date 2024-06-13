package epc.epcsalesapi.sales.bean.vms;

import java.math.BigDecimal;

public class VmsCharge {
    private String chargeInstanceId;
    private String chargeName;
    private String chargeTemplate;
    private BigDecimal chargeValue;
    private BigDecimal rrpPrice;

    public VmsCharge() {}

    public String getChargeInstanceId() {
        return chargeInstanceId;
    }

    public void setChargeInstanceId(String chargeInstanceId) {
        this.chargeInstanceId = chargeInstanceId;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public String getChargeTemplate() {
        return chargeTemplate;
    }

    public void setChargeTemplate(String chargeTemplate) {
        this.chargeTemplate = chargeTemplate;
    }

    public BigDecimal getChargeValue() {
        return chargeValue;
    }

    public void setChargeValue(BigDecimal chargeValue) {
        this.chargeValue = chargeValue;
    }

    public BigDecimal getRrpPrice() {
        return rrpPrice;
    }

    public void setRrpPrice(BigDecimal rrpPrice) {
        this.rrpPrice = rrpPrice;
    }

    
}
