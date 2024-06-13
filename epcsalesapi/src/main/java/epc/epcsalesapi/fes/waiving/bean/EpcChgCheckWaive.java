package epc.epcsalesapi.fes.waiving.bean;

import java.math.BigDecimal;

public class EpcChgCheckWaive {
	private String formCode;
	private BigDecimal waiveAmt;
	private String waiveGrp;
	private int groupid;
	
	public EpcChgCheckWaive() {}

	public String getFormCode() {
		return formCode;
	}

	public void setFormCode(String formCode) {
		this.formCode = formCode;
	}

	public BigDecimal getWaiveAmt() {
		return waiveAmt;
	}

	public void setWaiveAmt(BigDecimal waiveAmt) {
		this.waiveAmt = waiveAmt;
	}

	public String getWaiveGrp() {
		return waiveGrp;
	}

	public void setWaiveGrp(String waiveGrp) {
		this.waiveGrp = waiveGrp;
	}

	public int getGroupid() {
		return groupid;
	}

	public void setGroupid(int groupid) {
		this.groupid = groupid;
	}
}
