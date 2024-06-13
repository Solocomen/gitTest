/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

/**
 *
 * @author DannyChan
 */
public class EpcProductCombinationAttr {
    private String attrGuid;
    private String attrValue;
    private String attrValueGuid;

    /**
     * @return the attrGuid
     */
    public String getAttrGuid() {
        return attrGuid;
    }

    /**
     * @param attrGuid the attrGuid to set
     */
    public void setAttrGuid(String attrGuid) {
        this.attrGuid = attrGuid;
    }

    /**
     * @return the attrValue
     */
    public String getAttrValue() {
        return attrValue;
    }

    /**
     * @param attrValue the attrValue to set
     */
    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    /**
     * @return the attrValueGuid
     */
    public String getAttrValueGuid() {
        return attrValueGuid;
    }

    /**
     * @param attrValueGuid the attrValueGuid to set
     */
    public void setAttrValueGuid(String attrValueGuid) {
        this.attrValueGuid = attrValueGuid;
    }
}
