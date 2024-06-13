package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcExtensionCharge {
    
    private int orderId;
    private ArrayList<EpcOrderItemDetail> orderItemList;

    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public ArrayList<EpcOrderItemDetail> getOrderItemList() {
        return orderItemList;
    }
    public void setOrderItemList(ArrayList<EpcOrderItemDetail> orderItemList) {
        this.orderItemList = orderItemList;
    }
    
}
