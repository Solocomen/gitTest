package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcSparseAddCharacteristic {
    private String characteristicId;
    private String useArea;
    private String itemAction;
    private ArrayList<EpcSparseAddCharacteristicValue> values;

    public EpcSparseAddCharacteristic() {}

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

    public ArrayList<EpcSparseAddCharacteristicValue> getValues() {
        return values;
    }

    public void setValues(ArrayList<EpcSparseAddCharacteristicValue> values) {
        this.values = values;
    }


}
