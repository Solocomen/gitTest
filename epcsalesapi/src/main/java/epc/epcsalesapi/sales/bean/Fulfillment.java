package epc.epcsalesapi.sales.bean;

public class Fulfillment {

	private String fulfill_reference;
	private String fulfill_date;
	private String imei_sim;
	private String fulfill_user;
	private String fulfill_salesman;
	private String fulfill_location;
	
	public String getFulfill_reference() {
		return fulfill_reference;
	}
	public String getFulfill_date() {
		return fulfill_date;
	}
	public String getImei_sim() {
		return imei_sim;
	}
	public String getFulfill_user() {
		return fulfill_user;
	}
	public String getFulfill_salesman() {
		return fulfill_salesman;
	}
	public String getFulfill_location() {
		return fulfill_location;
	}
	public void setFulfill_reference(String fulfill_reference) {
		this.fulfill_reference = fulfill_reference;
	}
	public void setFulfill_date(String fulfill_date) {
		this.fulfill_date = fulfill_date;
	}
	public void setImei_sim(String imei_sim) {
		this.imei_sim = imei_sim;
	}
	public void setFulfill_user(String fulfill_user) {
		this.fulfill_user = fulfill_user;
	}
	public void setFulfill_salesman(String fulfill_salesman) {
		this.fulfill_salesman = fulfill_salesman;
	}
	public void setFulfill_location(String fulfill_location) {
		this.fulfill_location = fulfill_location;
	}

	
}
