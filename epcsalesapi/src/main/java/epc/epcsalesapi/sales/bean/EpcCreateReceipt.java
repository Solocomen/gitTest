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
public class EpcCreateReceipt {
    private String orderId;
    private String custId;
    private String location;
    private String createUser;
    private String salesman;
    private String custNum;
    private String subrNum;
    private ArrayList<EpcCharge> charges;
    private ArrayList<EpcPayment> paymentList;
    private ArrayList<EpcOrderItemInfo> itemList;

    public EpcCreateReceipt() {
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
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

    public String getSalesman() {
        return salesman;
    }

    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    public ArrayList<EpcCharge> getCharges() {
        return charges;
    }

    public void setCharges(ArrayList<EpcCharge> charges) {
        this.charges = charges;
    }

    public ArrayList<EpcPayment> getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(ArrayList<EpcPayment> paymentList) {
        this.paymentList = paymentList;
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

    public ArrayList<EpcOrderItemInfo> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<EpcOrderItemInfo> itemList) {
        this.itemList = itemList;
    }


}


