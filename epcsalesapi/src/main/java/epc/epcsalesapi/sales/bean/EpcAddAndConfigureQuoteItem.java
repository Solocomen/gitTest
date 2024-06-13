package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

public class EpcAddAndConfigureQuoteItem {
	private String custId;
	private int orderId;
	private int quoteId;
	private String productGuid;
	private HashMap<String, Object> productCandidate;
	private EpcQuoteItem epcQuoteItem;
	private HashMap<String, Object> cmsItemMapping;
	private String createUser;
	private String createSalesman;
	private String createChannel;
	private String createLocation;
	private String result;
	private String errMsg;
	
	public EpcAddAndConfigureQuoteItem() {}

	public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(int quoteId) {
		this.quoteId = quoteId;
	}

	public String getProductGuid() {
		return productGuid;
	}

	public void setProductGuid(String productGuid) {
		this.productGuid = productGuid;
	}

	public HashMap<String, Object> getProductCandidate() {
		return productCandidate;
	}

	public void setProductCandidate(HashMap<String, Object> productCandidate) {
		this.productCandidate = productCandidate;
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


	
	
}
