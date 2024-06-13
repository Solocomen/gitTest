package epc.epcsalesapi.sales.bean;

import java.util.List;

public class EpcCreateShortenUrl {
    private String appservice;
    private List<String> url;

    public EpcCreateShortenUrl() {}

    public String getAppservice() {
        return appservice;
    }

    public void setAppservice(String appservice) {
        this.appservice = appservice;
    }

    public List<String> getUrl() {
        return url;
    }

    public void setUrl(List<String> url) {
        this.url = url;
    }

    
}
