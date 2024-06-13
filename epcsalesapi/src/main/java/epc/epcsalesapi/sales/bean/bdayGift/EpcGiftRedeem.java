/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean.bdayGift;

/**
 *
 * @author DannyChan
 */
public class EpcGiftRedeem {
	private String requesterId;
	private String salesman;
	private String custNum;
	private String subrNum;
	private String rbdUnitCode;
	private String redemptionDate;

	/**
	 * @return the requesterId
	 */
	public String getRequesterId() {
		return requesterId;
	}

	/**
	 * @param requesterId the requesterId to set
	 */
	public void setRequesterId(String requesterId) {
		this.requesterId = requesterId;
	}

	/**
	 * @return the salesman
	 */
	public String getSalesman() {
		return salesman;
	}

	/**
	 * @param salesman the salesman to set
	 */
	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}

	/**
	 * @return the custNum
	 */
	public String getCustNum() {
		return custNum;
	}

	/**
	 * @param custNum the custNum to set
	 */
	public void setCustNum(String custNum) {
		this.custNum = custNum;
	}

	/**
	 * @return the subrNum
	 */
	public String getSubrNum() {
		return subrNum;
	}

	/**
	 * @param subrNum the subrNum to set
	 */
	public void setSubrNum(String subrNum) {
		this.subrNum = subrNum;
	}

	/**
	 * @return the rbdUnitCode
	 */
	public String getRbdUnitCode() {
		return rbdUnitCode;
	}

	/**
	 * @param rbdUnitCode the rbdUnitCode to set
	 */
	public void setRbdUnitCode(String rbdUnitCode) {
		this.rbdUnitCode = rbdUnitCode;
	}

	/**
	 * @return the redemptionDate
	 */
	public String getRedemptionDate() {
		return redemptionDate;
	}

	/**
	 * @param redemptionDate the redemptionDate to set
	 */
	public void setRedemptionDate(String redemptionDate) {
		this.redemptionDate = redemptionDate;
	}
}
