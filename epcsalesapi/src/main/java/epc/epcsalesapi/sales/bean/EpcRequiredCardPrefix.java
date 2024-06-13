/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.List;

/**
 *
 * @author DannyChan
 */
public class EpcRequiredCardPrefix extends EpcValidateItem {
	private List<String> cardPrefixList;

	/**
	 * @return the cardPrefixList
	 */
	public List<String> getCardPrefixList() {
		return cardPrefixList;
	}

	/**
	 * @param cardPrefixList the cardPrefixList to set
	 */
	public void setCardPrefixList(List<String> cardPrefixList) {
		this.cardPrefixList = cardPrefixList;
	}
}
