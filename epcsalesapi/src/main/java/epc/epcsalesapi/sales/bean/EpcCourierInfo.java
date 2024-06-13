/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcCourierInfo {
    private ArrayList<EpcCourier> courierList;
    private String result;
    private String errMsg;

    public EpcCourierInfo() {
    }

	public ArrayList<EpcCourier> getCourierList() {
		return courierList;
	}

	public void setCourierList(ArrayList<EpcCourier> courierList) {
		this.courierList = courierList;
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
