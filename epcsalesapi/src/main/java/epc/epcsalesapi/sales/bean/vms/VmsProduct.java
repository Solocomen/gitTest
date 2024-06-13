package epc.epcsalesapi.sales.bean.vms;

import java.math.BigDecimal;
import java.util.ArrayList;

public class VmsProduct {
    private String instanceId;
    private String modelName;
    private String productCode;
    private String productTemplate;
    private ArrayList<VmsCharge> charges;
    private ArrayList<VmsDiscount> v_discounts;
    private BigDecimal v_netPrice;

    public VmsProduct() {}

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductTemplate() {
        return productTemplate;
    }

    public void setProductTemplate(String productTemplate) {
        this.productTemplate = productTemplate;
    }

    public ArrayList<VmsCharge> getCharges() {
        return charges;
    }

    public void setCharges(ArrayList<VmsCharge> charges) {
        this.charges = charges;
    }

    public ArrayList<VmsDiscount> getV_discounts() {
        return v_discounts;
    }

    public void setV_discounts(ArrayList<VmsDiscount> v_discounts) {
        this.v_discounts = v_discounts;
    }

    public BigDecimal getV_netPrice() {
        return v_netPrice;
    }

    public void setV_netPrice(BigDecimal v_netPrice) {
        this.v_netPrice = v_netPrice;
    }

    
    
}
