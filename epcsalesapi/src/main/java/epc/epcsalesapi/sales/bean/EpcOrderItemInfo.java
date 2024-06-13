/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;

/**
 *
 * @author KerryTsang
 */
public class EpcOrderItemInfo {
    private String itemId;
    private String itemCat;
    private String itemCode;
    private String itemDesc;
    private String itemDescChi;
    private String itemValue;
    private String warehouse;
    private String reserveId;
    private String serial;
    private String isDummy;
    private String status;
    private String statusDesc;
    private String statusDescChi;
    private String invoiceNo;
    private String invoiceDate;
    private String parentItemId;
    private String deliveryMethod;
    private String pickupLocation;
    private int deliveryId;
    private String pickupDate;
    private BigDecimal totalCharge;
    private BigDecimal remainingCharge;
    private BigDecimal chargePaid;
    private String isReserveItem;
    private String reserveType;


    public EpcOrderItemInfo() {
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

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getItemDesc() {
        return itemDesc;
    }

    public void setItemDesc(String itemDesc) {
        this.itemDesc = itemDesc;
    }

    public String getItemDescChi() {
        return itemDescChi;
    }

    public void setItemDescChi(String itemDescChi) {
        this.itemDescChi = itemDescChi;
    }

    public String getItemValue() {
        return itemValue;
    }

    public void setItemValue(String itemValue) {
        this.itemValue = itemValue;
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

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getIsDummy() {
        return isDummy;
    }

    public void setIsDummy(String isDummy) {
        this.isDummy = isDummy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

	public String getInvoiceNo() {
		return invoiceNo;
	}

	public void setInvoiceNo(String invoiceNo) {
		this.invoiceNo = invoiceNo;
	}

	public String getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}

	public String getParentItemId() {
		return parentItemId;
	}

	public void setParentItemId(String parentItemId) {
		this.parentItemId = parentItemId;
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public String getPickupLocation() {
		return pickupLocation;
	}

	public void setPickupLocation(String pickupLocation) {
		this.pickupLocation = pickupLocation;
	}

	public int getDeliveryId() {
		return deliveryId;
	}

	public void setDeliveryId(int deliveryId) {
		this.deliveryId = deliveryId;
	}

	public String getPickupDate() {
		return pickupDate;
	}

	public void setPickupDate(String pickupDate) {
		this.pickupDate = pickupDate;
	}

    public BigDecimal getTotalCharge() {
        return totalCharge;
    }

    public void setTotalCharge(BigDecimal totalCharge) {
        this.totalCharge = totalCharge;
    }

    public BigDecimal getRemainingCharge() {
        return remainingCharge;
    }

    public void setRemainingCharge(BigDecimal remainingCharge) {
        this.remainingCharge = remainingCharge;
    }

    public BigDecimal getChargePaid() {
        return chargePaid;
    }

    public void setChargePaid(BigDecimal chargePaid) {
        this.chargePaid = chargePaid;
    }

    public String getIsReserveItem() {
        return isReserveItem;
    }

    public void setIsReserveItem(String isReserveItem) {
        this.isReserveItem = isReserveItem;
    }

    public String getReserveType() {
        return reserveType;
    }

    public void setReserveType(String reserveType) {
        this.reserveType = reserveType;
    }

    public String getStatusDescChi() {
        return statusDescChi;
    }

    public void setStatusDescChi(String statusDescChi) {
        this.statusDescChi = statusDescChi;
    }


}
