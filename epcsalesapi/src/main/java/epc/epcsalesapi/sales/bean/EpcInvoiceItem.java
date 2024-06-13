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
public class EpcInvoiceItem {
    private String itemId;
    private String itemCode;
    private String itemDesc;
    private String itemDescChi;
    private String itemValue;
    private String warehouse;
    private String reserveId;
    private String invoiceNo;
    private String itemCat;
    private String appleCareFirstName;
    private String appleCareLastName;
    private String appleCareEmail;
    private String parentItemId;
    private BigDecimal netAmt;
    private BigDecimal disAmt;

    public EpcInvoiceItem() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
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

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

	public String getItemCat() {
		return itemCat;
	}

	public void setItemCat(String itemCat) {
		this.itemCat = itemCat;
	}

	public String getAppleCareFirstName() {
		return appleCareFirstName;
	}

	public void setAppleCareFirstName(String appleCareFirstName) {
		this.appleCareFirstName = appleCareFirstName;
	}

	public String getAppleCareLastName() {
		return appleCareLastName;
	}

	public void setAppleCareLastName(String appleCareLastName) {
		this.appleCareLastName = appleCareLastName;
	}

	public String getAppleCareEmail() {
		return appleCareEmail;
	}

	public void setAppleCareEmail(String appleCareEmail) {
		this.appleCareEmail = appleCareEmail;
	}

	public String getParentItemId() {
		return parentItemId;
	}

	public void setParentItemId(String parentItemId) {
		this.parentItemId = parentItemId;
	}

    public BigDecimal getNetAmt() {
        return netAmt;
    }

    public void setNetAmt(BigDecimal netAmt) {
        this.netAmt = netAmt;
    }

    public BigDecimal getDisAmt() {
        return disAmt;
    }

    public void setDisAmt(BigDecimal disAmt) {
        this.disAmt = disAmt;
    }

    

}
