package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcSparseAddUserDefinedCharacteristic {
    private String characteristicId;
    private String useArea;
    private String itemAction;
    private ArrayList<EpcSparseAddUserDefinedCharacteristicValue> values;

    public EpcSparseAddUserDefinedCharacteristic() {}

    public String getCharacteristicId() {
        return characteristicId;
    }

    public void setCharacteristicId(String characteristicId) {
        this.characteristicId = characteristicId;
    }

    public String getUseArea() {
        return useArea;
    }

    public void setUseArea(String useArea) {
        this.useArea = useArea;
    }

    public String getItemAction() {
        return itemAction;
    }

    public void setItemAction(String itemAction) {
        this.itemAction = itemAction;
    }

    public ArrayList<EpcSparseAddUserDefinedCharacteristicValue> getValues() {
        return values;
    }

    public void setValues(ArrayList<EpcSparseAddUserDefinedCharacteristicValue> values) {
        this.values = values;
    }

    
}
