/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

/**
 *
 * @author DannyChan
 */
public class EpcGetProductCombinationResult {
    private String result;
    private int errorCode;
    private String errorMessage;
    private HashMap<String, EpcProductCombination[]> productCombinations;

    /**
     * @return the result
     */
    public String getResult() {
        return result;
    }

    /**
     * @param result the result to set
     */
    public void setResult(String result) {
        this.result = result;
    }

    /**
     * @return the errorCode
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * @param errorCode the errorCode to set
     */
    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    /**
     * @return the errorMessage
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * @param errorMessage the errorMessage to set
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }    

    /**
     * @return the productCombinations
     */
    public HashMap<String, EpcProductCombination[]> getProductCombinations() {
        return productCombinations;
    }

    /**
     * @param productCombinations the productCombinations to set
     */
    public void setProductCombinations(HashMap<String, EpcProductCombination[]> productCombinations) {
        this.productCombinations = productCombinations;
    }
}
