package epc.epcsalesapi.sales.bean;

public class EpcConfirmOrder {

    private EpcConfirmOrderItem[] ConfirmOrderList;
    
    public EpcConfirmOrder() {}

    public void setConfirmOrderList(EpcConfirmOrderItem[] ConfirmOrderList) {
        this.ConfirmOrderList = ConfirmOrderList;
    }

    public EpcConfirmOrderItem[] getConfirmOrderList() {
        return ConfirmOrderList;
    }
}

