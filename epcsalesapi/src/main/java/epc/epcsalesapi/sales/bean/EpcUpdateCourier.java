package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcUpdateCourier {
    private int orderId;
    private String deliveryDate;
    private String deliveryPeriod;
    private String courierCompany;
    private String courierFormNum;
    private String user;
    private String result;
    private String errMsg;

    public EpcUpdateCourier() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    
    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

	public String getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getDeliveryPeriod() {
		return deliveryPeriod;
	}

	public void setDeliveryPeriod(String deliveryPeriod) {
		this.deliveryPeriod = deliveryPeriod;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getCourierCompany() {
		return courierCompany;
	}

	public void setCourierCompany(String courierCompany) {
		this.courierCompany = courierCompany;
	}

	public String getCourierFormNum() {
		return courierFormNum;
	}

	public void setCourierFormNum(String courierFormNum) {
		this.courierFormNum = courierFormNum;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
    
    
}
