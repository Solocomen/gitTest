package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcCompiledSpecEntity {
    private String parentHashPath;
    private String hashPath;
    private String entityId; // entity guid
    private String[] contextPath;
    private HashMap<String, Object> characteristics;
    private HashMap<String, Object> dates;

    public EpcCompiledSpecEntity() {
    }
    public String getParentHashPath() {
        return parentHashPath;
    }
    public void setParentHashPath(String parentHashPath) {
        this.parentHashPath = parentHashPath;
    }
    public String getHashPath() {
        return hashPath;
    }
    public void setHashPath(String hashPath) {
        this.hashPath = hashPath;
    }
    public String getEntityId() {
        return entityId;
    }
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }
    public HashMap<String, Object> getCharacteristics() {
        return characteristics;
    }
    public void setCharacteristics(HashMap<String, Object> characteristics) {
        this.characteristics = characteristics;
    }
    public String[] getContextPath() {
        return contextPath;
    }
    public void setContextPath(String[] contextPath) {
        this.contextPath = contextPath;
    }
    public HashMap<String, Object> getDates() {
        return dates;
    }
    public void setDates(HashMap<String, Object> dates) {
        this.dates = dates;
    }
    
    
}
