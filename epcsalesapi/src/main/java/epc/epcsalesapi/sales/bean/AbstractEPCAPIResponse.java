/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.bean.AbstractEPCResponse
 * @author	TedKwan
 * @date	23-Sep-2022
 * Description:
 *
 * History:
 * 20220923-TedKwan: Created
 */
package epc.epcsalesapi.sales.bean;

public abstract class AbstractEPCAPIResponse {
	
	private String result;
    private String errMsg;
    
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
