/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.bean.productCandidate.characteristicUse.EntityValue
 * @author	TedKwan
 * @date	15-Aug-2022
 * Description:
 *
 * History:
 * 20220815-TedKwan: Created
 */
package epc.epcsalesapi.sales.bean.productCandidate.characteristicUse;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CharacteristicUseEntityValue {
	
	@JsonProperty("ValueID")
	private String valueID;
	
	@JsonProperty("ValueDetail")
	private String valueDetail;

	public String getValueID() {
		return valueID;
	}

	public void setValueID(String valueID) {
		this.valueID = valueID;
	}

	public String getValueDetail() {
		return valueDetail;
	}

	public void setValueDetail(String valueDetail) {
		this.valueDetail = valueDetail;
	}
	
}
