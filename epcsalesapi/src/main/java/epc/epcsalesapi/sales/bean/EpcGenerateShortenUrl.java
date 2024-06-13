/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author DannyChan
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpcGenerateShortenUrl {
    private String result;
    private String errMsg;
	private String inputUrl;
	private int orderId;
	private String custId;
	private String shortenUrl;

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @return the errMsg
	 */
	public String getErrMsg() {
		return errMsg;
	}

	/**
	 * @param errMsg the errMsg to set
	 */
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	/**
	 * @return the inputUrl
	 */
	public String getInputUrl() {
		return inputUrl;
	}

	/**
	 * @param inputUrl the inputUrl to set
	 */
	public void setInputUrl(String inputUrl) {
		this.inputUrl = inputUrl;
	}

	/**
	 * @return the shortenUrl
	 */
	public String getShortenUrl() {
		return shortenUrl;
	}

	/**
	 * @param shortenUrl the shortenUrl to set
	 */
	public void setShortenUrl(String shortenUrl) {
		this.shortenUrl = shortenUrl;
	}

	/**
	 * @return the orderId
	 */
	public int getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the custId
	 */
	public String getCustId() {
		return custId;
	}

	/**
	 * @param custId the custId to set
	 */
	public void setCustId(String custId) {
		this.custId = custId;
	}
}
