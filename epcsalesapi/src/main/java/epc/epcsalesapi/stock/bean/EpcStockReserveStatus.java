package epc.epcsalesapi.stock.bean;

public class EpcStockReserveStatus {
	private String caseid;
	private String status;
	private String description;
	
	public EpcStockReserveStatus() {}

	public String getCaseid() {
		return caseid;
	}

	public void setCaseid(String caseid) {
		this.caseid = caseid;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
