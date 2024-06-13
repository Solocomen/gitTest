/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 *
 * @author KerryTsang
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcOrderItem {
    private String itemId;
    private String warehouse;
    private String productCode;
    private String imeiSim;
    private String invoiceNo;
    private String parentItemId;
    private String newProductCode;
    private String newImeiSim;
    private BigDecimal netAmount;
    private BigDecimal disAmount;
    

    public EpcOrderItem() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(String warehouse) {
		this.warehouse = warehouse;
	}

	public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getImeiSim() {
        return imeiSim;
    }

    public void setImeiSim(String imeiSim) {
        this.imeiSim = imeiSim;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

	public String getParentItemId() {
		return parentItemId;
	}

	public void setParentItemId(String parentItemId) {
		this.parentItemId = parentItemId;
	}

	public String getNewProductCode() {
		return newProductCode;
	}

	public void setNewProductCode(String newProductCode) {
		this.newProductCode = newProductCode;
	}

	public String getNewImeiSim() {
		return newImeiSim;
	}

	public void setNewImeiSim(String newImeiSim) {
		this.newImeiSim = newImeiSim;
	}

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
    }

    public BigDecimal getDisAmount() {
        return disAmount;
    }

    public void setDisAmount(BigDecimal disAmount) {
        this.disAmount = disAmount;
    }
    
    
}
