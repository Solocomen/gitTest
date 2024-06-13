/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

/**
 *
 * @author DannyChan
 */
public class EpcGetCreditCardPrefixResult {
    private String resultCode;
    private String errorMessage;
    private ArrayList<String> creditCardPrefix;

	/**
	 * @return the resultCode
	 */
	public String getResultCode() {
		return resultCode;
	}

	/**
	 * @param resultCode the resultCode to set
	 */
	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	/**
	 * @return the errorMessage
	 */
	public String getErrorMessage() {
		return errorMessage;
	}

	/**
	 * @param errorMessage the errorMessage to set
	 */
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	/**
	 * @return the creditCardPrefix
	 */
	public ArrayList<String> getCreditCardPrefix() {
		return creditCardPrefix;
	}

	/**
	 * @param creditCardPrefix the creditCardPrefix to set
	 */
	public void setCreditCardPrefix(ArrayList<String> creditCardPrefix) {
		this.creditCardPrefix = creditCardPrefix;
	}
    
}
