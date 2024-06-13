/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

//import javax.xml.bind.annotation.*;
import com.fasterxml.jackson.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

/**
 *
 * @author KerryTsang
 */

//@XmlAccessorType(XmlAccessType.FIELD)
public class EpcQuoteProductCandidate implements Serializable  {
    
//    @XmlElement(name = "ID")
//    @JsonProperty("ID")
    private String id;
//    @XmlElement(name = "EntityID")
//    @JsonProperty("EntityID")
    private String entityID;
//    @XmlElement(name = "ItemSourceID")
//    @JsonProperty("ItemSourceID")
    private String itemSource;
//    @XmlElement(name = "CharacteristicUse")
//    @JsonProperty("CharacteristicUse")
//    private String characteristicUse;
//    @XmlElement(name = "RateAttribute")
//    @JsonProperty("RateAttribute")
    private String rateAttribute;
//    @XmlElement(name = "ConfiguredValue")
//    @JsonProperty("ConfiguredValue")
    private ArrayList<EpcConfiguredValue> configuredValue;
    private ArrayList<EpcCharacteristicUse> characteristicUse;
//    @XmlElement(name = "LinkedEntity")
//    @JsonProperty("LinkedEntity")
    private String linkedEntity;
//    @XmlElement(name = "ChildEntity")
//    @JsonProperty("ChildEntity")
    private ArrayList<EpcQuoteProductCandidate> childEntity;
    private String itemCat; // smc use only
    private String itemCode; // smc use only
    private String itemCode2; // smc use only
    private String cpqItemValue; // smc use only
    private String cpqItemDesc; // smc use only
    private String cpqItemDescChi; // smc use only
    private BigDecimal itemCharge;
    private String premium;
    private String templateName;
    private String catalogItemDesc;
    private BigDecimal catalogRrp;

    public EpcQuoteProductCandidate() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public String getItemSource() {
        return itemSource;
    }

    public void setItemSource(String itemSource) {
        this.itemSource = itemSource;
    }

//    public String getCharacteristicUse() {
//        return characteristicUse;
//    }

//    public void setCharacteristicUse(String characteristicUse) {
//        this.characteristicUse = characteristicUse;
//    }

    public String getRateAttribute() {
        return rateAttribute;
    }

    public void setRateAttribute(String rateAttribute) {
        this.rateAttribute = rateAttribute;
    }

    public ArrayList<EpcConfiguredValue> getConfiguredValue() {
        return configuredValue;
    }

    public void setConfiguredValue(ArrayList<EpcConfiguredValue> configuredValue) {
        this.configuredValue = configuredValue;
    }

    public ArrayList<EpcCharacteristicUse> getCharacteristicUse() {
        return characteristicUse;
    }

    public void setCharacteristicUse(ArrayList<EpcCharacteristicUse> characteristicUse) {
        this.characteristicUse = characteristicUse;
    }

    public String getLinkedEntity() {
        return linkedEntity;
    }

    public void setLinkedEntity(String linkedEntity) {
        this.linkedEntity = linkedEntity;
    }

    public ArrayList<EpcQuoteProductCandidate> getChildEntity() {
        return childEntity;
    }

    public void setChildEntity(ArrayList<EpcQuoteProductCandidate> childEntity) {
        this.childEntity = childEntity;
    }

    public String getItemCat() {
        return itemCat;
    }

    public void setItemCat(String itemCat) {
        this.itemCat = itemCat;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemCode2() {
        return itemCode2;
    }

    public void setItemCode2(String itemCode2) {
        this.itemCode2 = itemCode2;
    }

    public String getCpqItemValue() {
        return cpqItemValue;
    }

    public void setCpqItemValue(String cpqItemValue) {
        this.cpqItemValue = cpqItemValue;
    }

    public String getCpqItemDesc() {
        return cpqItemDesc;
    }

    public void setCpqItemDesc(String cpqItemDesc) {
        this.cpqItemDesc = cpqItemDesc;
    }

    public String getCpqItemDescChi() {
        return cpqItemDescChi;
    }

    public void setCpqItemDescChi(String cpqItemDescChi) {
        this.cpqItemDescChi = cpqItemDescChi;
    }

    public BigDecimal getItemCharge() {
        return itemCharge;
    }

    public void setItemCharge(BigDecimal itemCharge) {
        this.itemCharge = itemCharge;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getCatalogItemDesc() {
        return catalogItemDesc;
    }

    public void setCatalogItemDesc(String catalogItemDesc) {
        this.catalogItemDesc = catalogItemDesc;
    }

    public BigDecimal getCatalogRrp() {
        return catalogRrp;
    }

    public void setCatalogRrp(BigDecimal catalogRrp) {
        this.catalogRrp = catalogRrp;
    }

    
    
}
