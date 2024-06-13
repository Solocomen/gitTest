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
public class EpcInvoice {
    private String orderId;
    private String orderLang;
    private String custId;
    private String hkidBr;
    private String custNum;
    private String subrNum;
    private String location;
    private String createUser;
    private String salesmanCode;
    private String offerId;
    private String offerDesc;
    private String offerDescChi;
    private ArrayList<EpcInvoiceItem> items;

    public EpcInvoice() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderLang() {
		return orderLang;
	}

	public void setOrderLang(String orderLang) {
		this.orderLang = orderLang;
	}

	public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getHkidBr() {
		return hkidBr;
	}

	public void setHkidBr(String hkidBr) {
		this.hkidBr = hkidBr;
	}

	public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getSalesmanCode() {
        return salesmanCode;
    }

    public void setSalesmanCode(String salesmanCode) {
        this.salesmanCode = salesmanCode;
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
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

    public ArrayList<EpcInvoiceItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<EpcInvoiceItem> items) {
        this.items = items;
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
    
    

}
