package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;

public class EpcSparseAddProduct {

    private EpcSparseAddEntity entity;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ArrayList<EpcSparseAddCharacteristic> characteristics;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ArrayList<EpcSparseAddUserDefinedCharacteristic> userDefinedCharacteristics;
    
    public EpcSparseAddProduct() {}

    public EpcSparseAddEntity getEntity() {
        return entity;
    }

    public void setEntity(EpcSparseAddEntity entity) {
        this.entity = entity;
    }

    public ArrayList<EpcSparseAddCharacteristic> getCharacteristics() {
        return characteristics;
    }

    public void setCharacteristics(ArrayList<EpcSparseAddCharacteristic> characteristics) {
        this.characteristics = characteristics;
    }

    public ArrayList<EpcSparseAddUserDefinedCharacteristic> getUserDefinedCharacteristics() {
        return userDefinedCharacteristics;
    }

    public void setUserDefinedCharacteristics(ArrayList<EpcSparseAddUserDefinedCharacteristic> userDefinedCharacteristics) {
        this.userDefinedCharacteristics = userDefinedCharacteristics;
    }

}
