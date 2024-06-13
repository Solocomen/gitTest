package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

public class EpcValidateOrderError {
	private String errCode;
	private String errMsg;
	private String errMsg2;
	private HashMap<String, Object> errMsg3;
	private String source;
	
	public EpcValidateOrderError() {}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	public String getErrMsg2() {
		return errMsg2;
	}

	public void setErrMsg2(String errMsg2) {
		this.errMsg2 = errMsg2;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public HashMap<String, Object> getErrMsg3() {
		return errMsg3;
	}

	public void setErrMsg3(HashMap<String, Object> errMsg3) {
		this.errMsg3 = errMsg3;
	}
	
	
}
