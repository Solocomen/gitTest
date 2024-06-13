/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.math.BigDecimal;
import java.util.ArrayList;

import epc.epcsalesapi.sales.bean.receipt.EpcReceipt;
import epc.epcsalesapi.sales.bean.vms.order.VmsOrderVoucher;

/**
 *
 * @author KerryTsang
 */
public class EpcOrderInfo {
    private String custId;
    private int orderId;
    private String orderReference;
    private String orderDate;
    private String orderStatus;
    private String orderStatusDesc;
    private String orderStatusDescChi;
    private String deliveryMethod;
    private String pickupLocation;
    private BigDecimal totalAmount;
    private ArrayList<EpcOrderQuoteInfo> epcOrderQuoteInfoList;
    private ArrayList<EpcDeliveryDetail> epcDeliveryDetailList;
    private ArrayList<EpcOfferCharge> epcChargeList;
    private ArrayList<EpcPayment> epcPaymentList;
    private String receiptNo;
    private String orderType;
    private String contactNo;
    private String contactEmail;
    private String orderLang;
    private String placeOrderChannel;
    private String placeOrderSalesman;
    private ArrayList<VmsOrderVoucher> voucherList;
    private ArrayList<EpcOrderAttr> attrList;
	private ArrayList<EpcOrderRequiredItem> requiredItemList;
    private ArrayList<EpcReceipt> epcPaymentReceiptList;

    public EpcOrderInfo() {
    }

    public String getCustId() {
		return custId;
	}

	public void setCustId(String custId) {
		this.custId = custId;
	}

	public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatusDesc() {
        return orderStatusDesc;
    }

    public void setOrderStatusDesc(String orderStatusDesc) {
        this.orderStatusDesc = orderStatusDesc;
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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

	public ArrayList<EpcOrderQuoteInfo> getEpcOrderQuoteInfoList() {
		return epcOrderQuoteInfoList;
	}

	public void setEpcOrderQuoteInfoList(ArrayList<EpcOrderQuoteInfo> epcOrderQuoteInfoList) {
		this.epcOrderQuoteInfoList = epcOrderQuoteInfoList;
	}

	public ArrayList<EpcDeliveryDetail> getEpcDeliveryDetailList() {
		return epcDeliveryDetailList;
	}

	public void setEpcDeliveryDetailList(ArrayList<EpcDeliveryDetail> epcDeliveryDetailList) {
		this.epcDeliveryDetailList = epcDeliveryDetailList;
	}

	public ArrayList<EpcOfferCharge> getEpcChargeList() {
		return epcChargeList;
	}

	public void setEpcChargeList(ArrayList<EpcOfferCharge> epcChargeList) {
		this.epcChargeList = epcChargeList;
	}

	public ArrayList<EpcPayment> getEpcPaymentList() {
		return epcPaymentList;
	}

	public void setEpcPaymentList(ArrayList<EpcPayment> epcPaymentList) {
		this.epcPaymentList = epcPaymentList;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getOrderLang() {
        return orderLang;
    }

    public void setOrderLang(String orderLang) {
        this.orderLang = orderLang;
    }

    public String getPlaceOrderChannel() {
		return placeOrderChannel;
	}

	public void setPlaceOrderChannel(String placeOrderChannel) {
		this.placeOrderChannel = placeOrderChannel;
	}

	public ArrayList<VmsOrderVoucher> getVoucherList() {
        return voucherList;
    }

    public void setVoucherList(ArrayList<VmsOrderVoucher> voucherList) {
        this.voucherList = voucherList;
    }

    public ArrayList<EpcOrderAttr> getAttrList() {
        return attrList;
    }

    public void setAttrList(ArrayList<EpcOrderAttr> attrList) {
        this.attrList = attrList;
    }

    public ArrayList<EpcReceipt> getEpcPaymentReceiptList() {
        return epcPaymentReceiptList;
    }

    public void setEpcPaymentReceiptList(ArrayList<EpcReceipt> epcPaymentReceiptList) {
        this.epcPaymentReceiptList = epcPaymentReceiptList;
    }

    public String getOrderStatusDescChi() {
        return orderStatusDescChi;
    }

    public void setOrderStatusDescChi(String orderStatusDescChi) {
        this.orderStatusDescChi = orderStatusDescChi;
    }

    /**
     * @return the requiredItemList
     */
    public ArrayList<EpcOrderRequiredItem> getRequiredItemList() {
        return requiredItemList;
}

    /**
     * @param requiredItemList the requiredItemList to set
     */
    public void setRequiredItemList(ArrayList<EpcOrderRequiredItem> requiredItemList) {
        this.requiredItemList = requiredItemList;
    }

    public String getPlaceOrderSalesman()
    {
        return placeOrderSalesman;
    }

    public void setPlaceOrderSalesman(String placeOrderSalesman)
    {
        this.placeOrderSalesman = placeOrderSalesman;
    }
}
