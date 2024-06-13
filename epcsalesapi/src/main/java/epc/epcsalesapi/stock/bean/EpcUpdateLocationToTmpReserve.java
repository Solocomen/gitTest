package epc.epcsalesapi.stock.bean;

public class EpcUpdateLocationToTmpReserve {
	private String result;
	private String errMsg;
	private String pickupDate;
	
	public EpcUpdateLocationToTmpReserve() {}

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

	public String getPickupDate() {
		return pickupDate;
	}

	public void setPickupDate(String pickupDate) {
		this.pickupDate = pickupDate;
	}
	
	
}
