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
public class EpcCreateSerialItem {
    private String itemId;
    private String imeiSim;

    public EpcCreateSerialItem() {
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getImeiSim() {
        return imeiSim;
    }

    public void setImeiSim(String imeiSim) {
        this.imeiSim = imeiSim;
    }
    
    
}
