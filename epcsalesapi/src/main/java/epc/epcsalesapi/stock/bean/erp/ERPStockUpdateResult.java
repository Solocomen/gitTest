/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.stock.bean.erp.ERPStockUpdateResult
 * @author	TedKwan
 * @date	27-Sep-2022
 * Description:
 *
 * History:
 * 20220927-TedKwan: Created
 */
package epc.epcsalesapi.stock.bean.erp;

public class ERPStockUpdateResult {
	
	private Integer result;
	private String errorMessage;
	private String transferNote;
	/**
	 * @return the result
	 */
	public Integer getResult() {
		return result;
	}
	/**
	 * @param result the result to set
	 */
	public void setResult(Integer result) {
		this.result = result;
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
	 * @return the transferNote
	 */
	public String getTransferNote() {
		return transferNote;
	}
	/**
	 * @param transferNote the transferNote to set
	 */
	public void setTransferNote(String transferNote) {
		this.transferNote = transferNote;
	}
	
}
