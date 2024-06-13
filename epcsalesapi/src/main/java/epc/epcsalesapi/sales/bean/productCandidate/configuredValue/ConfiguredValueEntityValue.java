/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.bean.productCandidate.configuredValue.EntityValue
 * @author	TedKwan
 * @date	15-Aug-2022
 * Description:
 *
 * History:
 * 20220815-TedKwan: Created
 */
package epc.epcsalesapi.sales.bean.productCandidate.configuredValue;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ConfiguredValueEntityValue {
	@JsonProperty("Value")
	private String value;
	
	@JsonProperty("ValueDetail")
	private String valueDetail;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getValueDetail() {
		return valueDetail;
	}

	public void setValueDetail(String valueDetail) {
		this.valueDetail = valueDetail;
	}
	
}
