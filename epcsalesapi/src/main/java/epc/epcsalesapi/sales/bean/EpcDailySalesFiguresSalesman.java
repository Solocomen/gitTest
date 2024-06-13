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
public class EpcDailySalesFiguresSalesman {
    private String salesmanName; 
    private EpcDailySalesFiguresSummary salesmanDailySalesFigures;

    /**
     * @return the salesmanName
     */
    public String getSalesmanName() {
        return salesmanName;
    }

    /**
     * @param salesmanName the salesmanName to set
     */
    public void setSalesmanName(String salesmanName) {
        this.salesmanName = salesmanName;
    }

    /**
     * @return the salesmanDailySalesFigures
     */
    public EpcDailySalesFiguresSummary getSalesmanDailySalesFigures() {
        return salesmanDailySalesFigures;
    }

    /**
     * @param salesmanDailySalesFigures the salesmanDailySalesFigures to set
     */
    public void setSalesmanDailySalesFigures(EpcDailySalesFiguresSummary salesmanDailySalesFigures) {
        this.salesmanDailySalesFigures = salesmanDailySalesFigures;
    }
    
}
