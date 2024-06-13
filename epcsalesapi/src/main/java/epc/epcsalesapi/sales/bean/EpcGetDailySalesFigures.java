/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 *
 * @author DannyChan
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EpcGetDailySalesFigures {
     private String salesman;
     private String location;
     private String type;
     
     @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyyMMdd", timezone="Asia/Hong_Kong")
     private java.util.Date startDate;
     
     @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyyMMdd", timezone="Asia/Hong_Kong")
     private java.util.Date endDate; 

    /**
     * @return the salesman
     */
    public String getSalesman() {
        return salesman;
    }

    /**
     * @param salesman the salesman to set
     */
    public void setSalesman(String salesman) {
        this.salesman = salesman;
    }

    /**
     * @return the location
     */
    public String getLocation() {
        return location;
    }

    /**
     * @param location the location to set
     */
    public void setLocation(String location) {
        this.location = location;
    }

    /**
     * @return the startDate
     */
    public java.util.Date getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(java.util.Date startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public java.util.Date getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(java.util.Date endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
}
