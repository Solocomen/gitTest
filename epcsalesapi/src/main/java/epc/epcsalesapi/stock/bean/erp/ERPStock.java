/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.stock.bean.erp.ERPStock
 * @author	TedKwan
 * @date	27-Sep-2022
 * Description:
 *
 * History:
 * 20220927-TedKwan: Created
 */
package epc.epcsalesapi.stock.bean.erp;

public enum ERPStock {

	ADD("ADD"),
	DEDUCT("DEDUCT"),
	TRANSFER("TRANSFER"),
	SOURCE_ADD_DOA("R"),
	SOURCE_ADD_TRADEIN("R"),
	SOURCE_ADD_VOID("R"),
	SOURCE_DEDUCT_SALES("I"),
	SOURCE_TRANSFER_INTER("T"),
	RESULT_SUCCESS(Integer.valueOf(0)),
	RESULT_INVALID(Integer.valueOf(1)),
	RESULT_UNKNOWN(Integer.valueOf(9))
	;
	
	public final String value;
	public final Integer intVal;
	private ERPStock(String value) {
		this.value = value;
		this.intVal = null;
	}
	private ERPStock(Integer intVal) {
		this.value = null;
		this.intVal = intVal;
	}
}
