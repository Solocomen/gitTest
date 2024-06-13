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
public class EpcCreateOrderResult {
    private String custId;
    private String cpqQuoteGUID;
    private String orderReference;
    private String result;
    private String errorCode;
    private String errorMessage;
    private int orderId;
    private int quoteId;

    public EpcCreateOrderResult() {
    }

    public String getCustId() {
        return custId;
    }

    public String getCpqQuoteGUID() {
        return cpqQuoteGUID;
    }

    public String getOrderReference() {
        return orderReference;
    }

    public String getResult() {
        return result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public void setCpqQuoteGUID(String cpqQuoteGUID) {
        this.cpqQuoteGUID = cpqQuoteGUID;
    }

    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

	public int getOrderId() {
		return orderId;
	}

	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	public int getQuoteId() {
		return quoteId;
	}

	public void setQuoteId(int quoteId) {
		this.quoteId = quoteId;
	}

    
}
