package epc.epcsalesapi.sales.bean.vms;

import java.math.BigDecimal;
import java.util.ArrayList;

public class VmsProductDetail {
    private String quoteId;
    private String itemId;
    private String rootInstanceId;
    private String rootOfferName;
    private String rootOfferTemplate;
    private ArrayList<VmsProduct> products;
    private ArrayList<VmsDiscount> v_discounts;
    private BigDecimal v_netPrice;

    public VmsProductDetail() {}

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getRootInstanceId() {
        return rootInstanceId;
    }

    public void setRootInstanceId(String rootInstanceId) {
        this.rootInstanceId = rootInstanceId;
    }

    public String getRootOfferName() {
        return rootOfferName;
    }

    public void setRootOfferName(String rootOfferName) {
        this.rootOfferName = rootOfferName;
    }

    public String getRootOfferTemplate() {
        return rootOfferTemplate;
    }

    public void setRootOfferTemplate(String rootOfferTemplate) {
        this.rootOfferTemplate = rootOfferTemplate;
    }

    public ArrayList<VmsProduct> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<VmsProduct> products) {
        this.products = products;
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
