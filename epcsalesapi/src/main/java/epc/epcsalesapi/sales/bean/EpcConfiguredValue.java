package epc.epcsalesapi.sales.bean;

import java.io.Serializable;

public class EpcConfiguredValue implements Serializable {

    private String id;
    private String name;
    private String value;

    public EpcConfiguredValue() {
    }
    
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getValue() {
        return value;
    }
    public void setValue(String value) {
        this.value = value;
    }
}
