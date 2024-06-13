package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcDoaInvoice {
	private ArrayList<String> invoiceList;
	private String user;
    private String salesman;
	private String location;
    private String doaLocation;
    private String symptom;
    private String result;
	private String errMsg;
	
	public EpcDoaInvoice() {}

	public ArrayList<String> getInvoiceList() {
		return invoiceList;
	}

	public void setInvoiceList(ArrayList<String> invoiceList) {
		this.invoiceList = invoiceList;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getSalesman() {
		return salesman;
	}

	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDoaLocation() {
		return doaLocation;
	}

	public void setDoaLocation(String doaLocation) {
		this.doaLocation = doaLocation;
	}

	/**
	 * @return the symptom
	 */
	public String getSymptom() {
		return symptom;
	}

	/**
	 * @param symptom the symptom to set
	 */
	public void setSymptom(String symptom) {
		this.symptom = symptom;
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

	
	
}
