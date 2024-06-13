/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

/**
 *
 * @author DannyChan
 */
public class EpcGetStaffOfferCompanyResult {
	private String result;
    private String errMsg;
	private ArrayList<EpcStaffOfferCompany> staffOfferCompanies;

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @return the errMsg
	 */
	public String getErrMsg() {
		return errMsg;
	}

	/**
	 * @param errMsg the errMsg to set
	 */
	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}

	/**
	 * @return the staffOfferCompanies
	 */
	public ArrayList<EpcStaffOfferCompany> getStaffOfferCompanies() {
		return staffOfferCompanies;
	}

	/**
	 * @param staffOfferCompanies the staffOfferCompanies to set
	 */
	public void setStaffOfferCompanies(ArrayList<EpcStaffOfferCompany> staffOfferCompanies) {
		this.staffOfferCompanies = staffOfferCompanies;
	}
}
