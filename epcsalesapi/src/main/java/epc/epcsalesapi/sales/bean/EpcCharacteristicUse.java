package epc.epcsalesapi.sales.bean;

import java.io.Serializable;
import java.util.ArrayList;

public class EpcCharacteristicUse implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4991595999302350766L;
	private String id;
    private String name;
    private ArrayList<String> value;

    public EpcCharacteristicUse() {
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
    public ArrayList<String> getValue() {
        return value;
    }
    public void setValue(ArrayList<String> value) {
        this.value = value;
    }
    
}
