/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

/**
 *
 * @author KerryTsang
 */
public class EpcOrderItemDetail {
    private int orderId;
    private String itemId;
    private String itemCat;
    private String cpqOfferGuid;
    private String cpqOfferDesc;
    private String cpqOfferDescChi;
    private String cpqItemDesc;
    private String cpqItemDescChi;
    private String warehouse;
    private String reserveId;
    private String custId;
    private String custNum;
    private String subrNum;
    private String parentItemId;
    private String itemCode;
    

    public EpcOrderItemDetail() {
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemCat() {
		return itemCat;
	}

	public void setItemCat(String itemCat) {
		this.itemCat = itemCat;
	}

	public String getCpqOfferGuid() {
        return cpqOfferGuid;
    }

    public void setCpqOfferGuid(String cpqOfferGuid) {
        this.cpqOfferGuid = cpqOfferGuid;
    }

    public String getCpqOfferDesc() {
        return cpqOfferDesc;
    }

    public void setCpqOfferDesc(String cpqOfferDesc) {
        this.cpqOfferDesc = cpqOfferDesc;
    }

    public String getCpqOfferDescChi() {
        return cpqOfferDescChi;
    }

    public void setCpqOfferDescChi(String cpqOfferDescChi) {
        this.cpqOfferDescChi = cpqOfferDescChi;
    }

    public String getCpqItemDesc() {
        return cpqItemDesc;
    }

    public void setCpqItemDesc(String cpqItemDesc) {
        this.cpqItemDesc = cpqItemDesc;
    }

    public String getCpqItemDescChi() {
        return cpqItemDescChi;
    }

    public void setCpqItemDescChi(String cpqItemDescChi) {
        this.cpqItemDescChi = cpqItemDescChi;
    }

    public String getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(String warehouse) {
        this.warehouse = warehouse;
    }

    public String getReserveId() {
        return reserveId;
    }

    public void setReserveId(String reserveId) {
        this.reserveId = reserveId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
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

	public String getParentItemId() {
		return parentItemId;
	}

	public void setParentItemId(String parentItemId) {
		this.parentItemId = parentItemId;
	}

	public String getItemCode() {
		return itemCode;
	}

	public void setItemCode(String itemCode) {
		this.itemCode = itemCode;
	}
    
    
}
