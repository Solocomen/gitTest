/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.bean.orderReservedItems.ReservedItem
 * @author	TedKwan
 * @date	08-Dec-2022
 * Description:
 *
 * History:
 * 20221208-TedKwan: Created
 */
package epc.epcsalesapi.sales.bean.orderReservedItems;

public class ReservedItem {

	private String itemId;
	
	private String itemDesc;
	
	private String productCode;
	
	private String reserveType;

	/**
	 * @return the itemId
	 */
	public String getItemId() {
		return itemId;
	}

	/**
	 * @param itemId the itemId to set
	 */
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the itemDesc
	 */
	public String getItemDesc() {
		return itemDesc;
	}

	/**
	 * @param itemDesc the itemDesc to set
	 */
	public void setItemDesc(String itemDesc) {
		this.itemDesc = itemDesc;
	}

	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}

	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/**
	 * @return the reserveType
	 */
	public String getReserveType() {
		return reserveType;
	}

	/**
	 * @param reserveType the reserveType to set
	 */
	public void setReserveType(String reserveType) {
		this.reserveType = reserveType;
	}
	
}
