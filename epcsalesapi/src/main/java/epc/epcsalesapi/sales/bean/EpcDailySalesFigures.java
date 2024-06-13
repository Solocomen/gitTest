/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.ArrayList;

/**
 *
 * @author DannyChan
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpcDailySalesFigures {
    private EpcDailySalesFiguresSummary total;
    private ArrayList<EpcDailySalesFiguresSalesman> salesmen;

    /**
     * @return the total
     */
    public EpcDailySalesFiguresSummary getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(EpcDailySalesFiguresSummary total) {
        this.total = total;
    }

    /**
     * @return the salesmen
     */
    public ArrayList<EpcDailySalesFiguresSalesman> getSalesmen() {
        return salesmen;
    }

    /**
     * @param salesmen the salesmen to set
     */
    public void setSalesmen(ArrayList<EpcDailySalesFiguresSalesman> salesmen) {
        this.salesmen = salesmen;
    }

}
 