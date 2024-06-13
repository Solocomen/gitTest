/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

/**
 *
 * @author DannyChan
 */
public class EpcProductCombination {
    private int combinationId;    
    private String productCode;
    private String rate;
    private HashMap<String, EpcProductCombinationAttr> attrs;

    /**
     * @return the combinationId
     */
    public int getCombinationId() {
        return combinationId;
    }

    /**
     * @param combinationId the combinationId to set
     */
    public void setCombinationId(int combinationId) {
        this.combinationId = combinationId;
    }

    /**
     * @return the productCode
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * @param productCode the productCode to set
     */
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    /**
     * @return the rate
     */
    public String getRate() {
        return rate;
    }

    /**
     * @param rate the rate to set
     */
    public void setRate(String rate) {
        this.rate = rate;
    }

    /**
     * @return the attrs
     */
    public HashMap<String, EpcProductCombinationAttr> getAttrs() {
        return attrs;
    }

    /**
     * @param attrs the attrs to set
     */
    public void setAttrs(HashMap<String, EpcProductCombinationAttr> attrs) {
        this.attrs = attrs;
    }
}
