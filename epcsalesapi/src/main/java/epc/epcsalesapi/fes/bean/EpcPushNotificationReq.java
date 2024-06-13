package epc.epcsalesapi.fes.bean;

public class EpcPushNotificationReq {
	private String machineId;
	private String salesman;
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	public String getSalesman() {
		return salesman;
	}
	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}	
}