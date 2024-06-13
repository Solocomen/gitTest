package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcDeliveryDetail {
	private int deliveryId;
	private String deliveryMethod;
    private String pickupStore;
    private String deliveryAddress1;
    private String deliveryAddress2;
    private String deliveryAddress3;
    private String deliveryAddress4;
    private String deliveryContactPerson;
    private String deliveryContactNo;
    private String addrType;
    private String addrToProfile;
    private ArrayList<String> items;
    
    public EpcDeliveryDetail() {}

	public int getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(int deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public String getPickupStore() {
		return pickupStore;
	}

	public void setPickupStore(String pickupStore) {
		this.pickupStore = pickupStore;
	}

	public String getDeliveryAddress1() {
		return deliveryAddress1;
	}

	public void setDeliveryAddress1(String deliveryAddress1) {
		this.deliveryAddress1 = deliveryAddress1;
	}

	public String getDeliveryAddress2() {
		return deliveryAddress2;
	}

	public void setDeliveryAddress2(String deliveryAddress2) {
		this.deliveryAddress2 = deliveryAddress2;
	}

	public String getDeliveryAddress3() {
		return deliveryAddress3;
	}

	public void setDeliveryAddress3(String deliveryAddress3) {
		this.deliveryAddress3 = deliveryAddress3;
	}

	public String getDeliveryAddress4() {
		return deliveryAddress4;
	}

	public void setDeliveryAddress4(String deliveryAddress4) {
		this.deliveryAddress4 = deliveryAddress4;
	}

	public String getDeliveryContactPerson() {
		return deliveryContactPerson;
	}

	public void setDeliveryContactPerson(String deliveryContactPerson) {
		this.deliveryContactPerson = deliveryContactPerson;
	}

	public String getDeliveryContactNo() {
		return deliveryContactNo;
	}

	public void setDeliveryContactNo(String deliveryContactNo) {
		this.deliveryContactNo = deliveryContactNo;
	}

	public String getAddrType() {
		return addrType;
	}

	public void setAddrType(String addrType) {
		this.addrType = addrType;
	}

	public String getAddrToProfile() {
		return addrToProfile;
	}

	public void setAddrToProfile(String addrToProfile) {
		this.addrToProfile = addrToProfile;
	}

	public ArrayList<String> getItems() {
		return items;
	}

	public void setItems(ArrayList<String> items) {
		this.items = items;
	}
    
    
    
}
