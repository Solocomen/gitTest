package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

public class EpcMoveQuoteItemToOtherQuote {
	private String tmpCustId;
    private int tmpOrderId;
    private int tmpQuoteId;
    private String tmpQuoteItemGuid;
    private String targetCustId;
    private int targetOrderId;
    private int targetQuoteId;
    private String targetQuoteItemGuid;
    private String packageGuid;
    private EpcQuoteItem epcQuoteItem;
    private HashMap<String, Object> cmsItemMapping;
    private String createUser;
    private String createSalesman;
    private String createChannel;
    private String createLocation;
    private String result;
    private String errMsg;

	public EpcMoveQuoteItemToOtherQuote() {}

    public int getTmpOrderId() {
        return tmpOrderId;
    }

    public void setTmpOrderId(int tmpOrderId) {
        this.tmpOrderId = tmpOrderId;
    }

    public int getTmpQuoteId() {
        return tmpQuoteId;
    }

    public void setTmpQuoteId(int tmpQuoteId) {
        this.tmpQuoteId = tmpQuoteId;
    }

    public String getTmpQuoteItemGuid() {
        return tmpQuoteItemGuid;
    }

    public void setTmpQuoteItemGuid(String tmpQuoteItemGuid) {
        this.tmpQuoteItemGuid = tmpQuoteItemGuid;
    }

    public int getTargetOrderId() {
        return targetOrderId;
    }

    public void setTargetOrderId(int targetOrderId) {
        this.targetOrderId = targetOrderId;
    }

    public int getTargetQuoteId() {
        return targetQuoteId;
    }

    public void setTargetQuoteId(int targetQuoteId) {
        this.targetQuoteId = targetQuoteId;
    }

    public String getTargetQuoteItemGuid() {
        return targetQuoteItemGuid;
    }

    public void setTargetQuoteItemGuid(String targetQuoteItemGuid) {
        this.targetQuoteItemGuid = targetQuoteItemGuid;
    }

    public String getPackageGuid() {
        return packageGuid;
    }

    public void setPackageGuid(String packageGuid) {
        this.packageGuid = packageGuid;
    }

    public EpcQuoteItem getEpcQuoteItem() {
        return epcQuoteItem;
    }

    public void setEpcQuoteItem(EpcQuoteItem epcQuoteItem) {
        this.epcQuoteItem = epcQuoteItem;
    }

    public HashMap<String, Object> getCmsItemMapping() {
        return cmsItemMapping;
    }

    public void setCmsItemMapping(HashMap<String, Object> cmsItemMapping) {
        this.cmsItemMapping = cmsItemMapping;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateSalesman() {
        return createSalesman;
    }

    public void setCreateSalesman(String createSalesman) {
        this.createSalesman = createSalesman;
    }

    public String getCreateChannel() {
        return createChannel;
    }

    public void setCreateChannel(String createChannel) {
        this.createChannel = createChannel;
    }

    public String getCreateLocation() {
        return createLocation;
    }

    public void setCreateLocation(String createLocation) {
        this.createLocation = createLocation;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getTmpCustId() {
        return tmpCustId;
    }

    public void setTmpCustId(String tmpCustId) {
        this.tmpCustId = tmpCustId;
    }

    public String getTargetCustId() {
        return targetCustId;
    }

    public void setTargetCustId(String targetCustId) {
        this.targetCustId = targetCustId;
    }


	
}
