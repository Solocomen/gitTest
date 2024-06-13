/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.bean.orderReservedItems.UpdateReservedItemsRequest
 * @author	TedKwan
 * @date	08-Dec-2022
 * Description:
 *
 * History:
 * 20221208-TedKwan: Created
 */
package epc.epcsalesapi.sales.bean.orderReservedItems;

import java.util.ArrayList;

public class UpdateReservedItemsRequest {
	
	private String orderId;
	
	private ArrayList<ReservedItem> items;

	/**
	 * @return the orderId
	 */
	public String getOrderId() {
		return orderId;
	}

	/**
	 * @param orderId the orderId to set
	 */
	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	/**
	 * @return the items
	 */
	public ArrayList<ReservedItem> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(ArrayList<ReservedItem> items) {
		this.items = items;
	}
	
}
