/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.bean.productCandidate.characteristicUse.CharacteristicUseEntity
 * @author	TedKwan
 * @date	15-Aug-2022
 * Description:
 *
 * History:
 * 20220815-TedKwan: Created
 */
package epc.epcsalesapi.sales.bean.productCandidate.characteristicUse;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CharacteristicUseEntity {

	@JsonProperty("UseArea")
	private String useArea;
	
	@JsonProperty("CharacteristicID")
	private String characteristicID;
	
	@JsonProperty("Value")
	private ArrayList<CharacteristicUseEntityValue> value;
	
	@JsonProperty("ItemSource")
	private String itemSource;

	public String getUseArea() {
		return useArea;
	}

	public void setUseArea(String useArea) {
		this.useArea = useArea;
	}

	public String getCharacteristicID() {
		return characteristicID;
	}

	public void setCharacteristicID(String characteristicID) {
		this.characteristicID = characteristicID;
	}

	public ArrayList<CharacteristicUseEntityValue> getValue() {
		return value;
	}

	public void setValue(ArrayList<CharacteristicUseEntityValue> value) {
		this.value = value;
	}

	public String getItemSource() {
		return itemSource;
	}

	public void setItemSource(String itemSource) {
		this.itemSource = itemSource;
	}
	
}
