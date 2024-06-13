package epc.epcsalesapi.helper.bean;

public class EpcActionLog {
    private String action;
    private String uri;
    private String inString;
    private String outString;
    
    public EpcActionLog() {
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getInString() {
        return inString;
    }

    public void setInString(String inString) {
        this.inString = inString;
    }

    public String getOutString() {
        return outString;
    }

    public void setOutString(String outString) {
        this.outString = outString;
    }

    
}
