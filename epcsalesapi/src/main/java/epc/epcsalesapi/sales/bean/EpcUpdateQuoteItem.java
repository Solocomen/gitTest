package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcUpdateQuoteItem {
	private String custId;
	private int orderId;
	private int quoteId;
	private String sigmaItemId;
	private HashMap<String, Object> productCandidate;
	private String updateUser;
	private String updateSalesman;
	private String updateChannel;
	private String updateLocation;
	private String result;
	private String errMsg;
	private EpcQuoteItem epcQuoteItem;
	private String itemAction;
	private HashMap<String, Object> cmsItemMapping;
	private HashMap<String, Object> errMsg2;

	public EpcUpdateQuoteItem() {}

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

	public String getSigmaItemId() {
		return sigmaItemId;
	}

	public void setSigmaItemId(String sigmaItemId) {
		this.sigmaItemId = sigmaItemId;
	}

	public HashMap<String, Object> getProductCandidate() {
		return productCandidate;
	}

	public void setProductCandidate(HashMap<String, Object> productCandidate) {
		this.productCandidate = productCandidate;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getUpdateSalesman() {
		return updateSalesman;
	}

	public void setUpdateSalesman(String updateSalesman) {
		this.updateSalesman = updateSalesman;
	}

	public String getUpdateChannel() {
		return updateChannel;
	}

	public void setUpdateChannel(String updateChannel) {
		this.updateChannel = updateChannel;
	}

	public String getUpdateLocation() {
		return updateLocation;
	}

	public void setUpdateLocation(String updateLocation) {
		this.updateLocation = updateLocation;
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

	public EpcQuoteItem getEpcQuoteItem() {
		return epcQuoteItem;
	}

	public void setEpcQuoteItem(EpcQuoteItem epcQuoteItem) {
		this.epcQuoteItem = epcQuoteItem;
	}

	public String getItemAction() {
		return itemAction;
	}

	public void setItemAction(String itemAction) {
		this.itemAction = itemAction;
	}

	public HashMap<String, Object> getErrMsg2() {
		return errMsg2;
	}

	public void setErrMsg2(HashMap<String, Object> errMsg2) {
		this.errMsg2 = errMsg2;
	}

	public HashMap<String, Object> getCmsItemMapping() {
		return cmsItemMapping;
	}

	public void setCmsItemMapping(HashMap<String, Object> cmsItemMapping) {
		this.cmsItemMapping = cmsItemMapping;
	}
	
	
}
