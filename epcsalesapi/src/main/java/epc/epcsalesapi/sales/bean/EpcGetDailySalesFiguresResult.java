/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author DannyChan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpcGetDailySalesFiguresResult {
    private String result;
    private String errMsg;
    private EpcGetDailySalesFigures searchCriteria;
    private EpcDailySalesFigures dailySalesFigures;

    /**
     * @return the searchCriteria
     */
    public EpcGetDailySalesFigures getSearchCriteria() {
        return searchCriteria;
    }

    /**
     * @param searchCriteria the searchCriteria to set
     */
    public void setSearchCriteria(EpcGetDailySalesFigures searchCriteria) {
        this.searchCriteria = searchCriteria;
    }

    /**
     * @return the dailySalesFigures
     */
    public EpcDailySalesFigures getDailySalesFigures() {
        return dailySalesFigures;
    }

    /**
     * @param dailySalesFigures the dailySalesFigures to set
     */
    public void setDailySalesFigures(EpcDailySalesFigures dailySalesFigures) {
        this.dailySalesFigures = dailySalesFigures;
    }

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
     * @return the errMsg
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * @param errMsg the errMsg to set
     */
    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }
}
