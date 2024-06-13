package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcEvaluateConfiguration {
    private String id;
    private String itemId;
    private EpcEvaluateTaskFilter taskFilter;

    public EpcEvaluateConfiguration() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public EpcEvaluateTaskFilter getTaskFilter() {
        return taskFilter;
    }

    public void setTaskFilter(EpcEvaluateTaskFilter taskFilter) {
        this.taskFilter = taskFilter;
    }

    
}
