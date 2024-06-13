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
public class EpcCreateInvoiceResult {
    private String result;
    private String errorCode;
    private String errorMessage;
    private ArrayList<EpcInvoice> invoiceList;

    public EpcCreateInvoiceResult() {
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public ArrayList<EpcInvoice> getInvoiceList() {
        return invoiceList;
    }

    public void setInvoiceList(ArrayList<EpcInvoice> invoiceList) {
        this.invoiceList = invoiceList;
    }

    
    
}
