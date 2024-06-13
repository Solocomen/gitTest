package epc.epcsalesapi.sales.bean.vms;

import epc.epcsalesapi.sales.bean.EpcVmsVoucherListRequest;

public class EpcVmsVoucherCodeRequest extends EpcVmsVoucherListRequest {

    public static final String LIST = "LIST";
    public static final String INFO = "INFO";
    
    private String queryByApi;

    public EpcVmsVoucherCodeRequest() {
        super();
        this.setQueryByApi(LIST);
    }

    public String getQueryByApi() {
        return queryByApi;
    }

    public void setQueryByApi(String queryByApi) {
        this.queryByApi = queryByApi;
    }

}
