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
public class EpcCreateInvoice {
    private ArrayList<EpcInvoice> invoiceList;

    public EpcCreateInvoice() {
    }

    public ArrayList<EpcInvoice> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(ArrayList<EpcInvoice> invoiceList) {
        this.invoiceList = invoiceList;
    }
}
