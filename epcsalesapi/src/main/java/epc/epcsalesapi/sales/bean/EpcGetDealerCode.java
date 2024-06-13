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
public class EpcGetDealerCode {
    private ArrayList<EpcDealerCode> dealerCodes;

    public EpcGetDealerCode() {
    }

    public ArrayList<EpcDealerCode> getDealerCodes() {
        return dealerCodes;
    }

    public void setDealerCodes(ArrayList<EpcDealerCode> dealerCodes) {
        this.dealerCodes = dealerCodes;
    }
    
}
