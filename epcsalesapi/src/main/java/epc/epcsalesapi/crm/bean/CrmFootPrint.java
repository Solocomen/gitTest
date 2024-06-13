package epc.epcsalesapi.crm.bean;

import java.util.List;

public class CrmFootPrint {
    
    private String custId;
    private String custNum;
    private String subrNum;
    private String accountNum;
    private String ipAddress;
    private String sessionId;
    private String eventDate; // yyyy-mm-dd hh24:mi:ss
    private String eventDesc;
    private String eventType;
    private String eventCode;
    private String actionLevel;
    private String channel;
    private String location;
    private String systemName;
    private String serviceName;
    private List<CrmFootPrintEvent> eventList;
    
    public CrmFootPrint() {
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    public String getSubrNum() {
        return subrNum;
    }

    public void setSubrNum(String subrNum) {
        this.subrNum = subrNum;
    }

    public String getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(String accountNum) {
        this.accountNum = accountNum;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public void setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventCode() {
        return eventCode;
    }

    public void setEventCode(String eventCode) {
        this.eventCode = eventCode;
    }

    public String getActionLevel() {
        return actionLevel;
    }

    public void setActionLevel(String actionLevel) {
        this.actionLevel = actionLevel;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSystemName() {
        return systemName;
    }

    public void setSystemName(String systemName) {
        this.systemName = systemName;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public List<CrmFootPrintEvent> getEventList() {
        return eventList;
    }

    public void setEventList(List<CrmFootPrintEvent> eventList) {
        this.eventList = eventList;
    }

    
}
