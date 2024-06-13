/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

/**
 *
 * @author KerryTsang
 */
public class EpcOrderCaseInfo {
    private String caseId;
    private String offerDesc;
    private String offerDescChi;
    private String salesAppOfferDesc;
    private String salesAppOfferDescChi;
    private String custNum;
    private String subrNum;
    private String subrKey;
    private String activationType;
    private String effectiveDate;
    private ArrayList<EpcOrderItemInfo> epcOrderItemList;
    private String cancelReceipt;
    private String cancelDate;
	private String createChannel;
	private String createLocation;
	private ArrayList<EpcCustProfile2> custProfileList;

    public EpcOrderCaseInfo() {
    }

	public String getCaseId() {
		return caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public String getOfferDesc() {
		return offerDesc;
	}

	public void setOfferDesc(String offerDesc) {
		this.offerDesc = offerDesc;
	}

	public String getOfferDescChi() {
		return offerDescChi;
	}

	public void setOfferDescChi(String offerDescChi) {
		this.offerDescChi = offerDescChi;
	}

	public String getSalesAppOfferDesc() {
		return salesAppOfferDesc;
	}

	public void setSalesAppOfferDesc(String salesAppOfferDesc) {
		this.salesAppOfferDesc = salesAppOfferDesc;
	}

	public String getSalesAppOfferDescChi() {
		return salesAppOfferDescChi;
	}

	public void setSalesAppOfferDescChi(String salesAppOfferDescChi) {
		this.salesAppOfferDescChi = salesAppOfferDescChi;
	}

	public String getCustNum() {
		return custNum;
	}

	public void setCustNum(String custNum) {
		this.custNum = custNum;
	}

	public String getSubrNum() {
		return subrNum;
	}

	public void setSubrNum(String subrNum) {
		this.subrNum = subrNum;
	}

	public String getSubrKey() {
		return subrKey;
	}

	public void setSubrKey(String subrKey) {
		this.subrKey = subrKey;
	}

	public String getActivationType() {
		return activationType;
	}

	public void setActivationType(String activationType) {
		this.activationType = activationType;
	}

	public String getEffectiveDate() {
		return effectiveDate;
	}

	public void setEffectiveDate(String effectiveDate) {
		this.effectiveDate = effectiveDate;
	}

	public ArrayList<EpcOrderItemInfo> getEpcOrderItemList() {
		return epcOrderItemList;
	}

	public void setEpcOrderItemList(ArrayList<EpcOrderItemInfo> epcOrderItemList) {
		this.epcOrderItemList = epcOrderItemList;
	}

	public String getCancelReceipt() {
		return cancelReceipt;
	}

	public void setCancelReceipt(String cancelReceipt) {
		this.cancelReceipt = cancelReceipt;
	}

	public String getCancelDate() {
		return cancelDate;
	}

	public void setCancelDate(String cancelDate) {
		this.cancelDate = cancelDate;
	}

	public String getCreateChannel() {
		return createChannel;
	}

	public void setCreateChannel(String createChannel) {
		this.createChannel = createChannel;
	}

	public String getCreateLocation() {
		return createLocation;
	}

	public void setCreateLocation(String createLocation) {
		this.createLocation = createLocation;
	}

	public ArrayList<EpcCustProfile2> getCustProfileList() {
		return custProfileList;
	}

	public void setCustProfileList(ArrayList<EpcCustProfile2> custProfileList) {
		this.custProfileList = custProfileList;
	}

	
}
