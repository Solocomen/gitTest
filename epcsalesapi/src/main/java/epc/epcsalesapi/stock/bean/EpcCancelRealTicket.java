package epc.epcsalesapi.stock.bean;

import java.util.List;

public class EpcCancelRealTicket {
    private List<EpcCancelRealTicketDetail> ordersList;
    private int modifyFesUserId;
    private int modifySalesmanId;

    public EpcCancelRealTicket() {}

    public List<EpcCancelRealTicketDetail> getOrdersList() {
        return ordersList;
    }

    public void setOrdersList(List<EpcCancelRealTicketDetail> ordersList) {
        this.ordersList = ordersList;
    }

    public int getModifyFesUserId() {
        return modifyFesUserId;
    }

    public void setModifyFesUserId(int modifyFesUserId) {
        this.modifyFesUserId = modifyFesUserId;
    }

    public int getModifySalesmanId() {
        return modifySalesmanId;
    }

    public void setModifySalesmanId(int modifySalesmanId) {
        this.modifySalesmanId = modifySalesmanId;
    }

    
}
