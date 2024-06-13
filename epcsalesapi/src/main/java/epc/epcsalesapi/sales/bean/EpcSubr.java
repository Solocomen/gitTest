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
public class EpcSubr {
    private int sigmaOrderId;
    private int smcOrderId;
    private String custNum;
    private String subrNum;
    private String subrKey;

    public EpcSubr() {
    }

    public int getSigmaOrderId() {
        return sigmaOrderId;
    }

    public void setSigmaOrderId(int sigmaOrderId) {
        this.sigmaOrderId = sigmaOrderId;
    }

    public int getSmcOrderId() {
        return smcOrderId;
    }

    public void setSmcOrderId(int smcOrderId) {
        this.smcOrderId = smcOrderId;
    }
    
    public String getCustNum() {
        return custNum;
    }

    public void setCustNum(String custNum) {
        this.custNum = custNum;
    }

    public String getSubrNum() {
        return subrNum;
    }

    public void setSubrNum(String subrNum) {
        this.subrNum = subrNum;
    }

    public String getSubrKey() {
        return subrKey;
    }

    public void setSubrKey(String subrKey) {
        this.subrKey = subrKey;
    }
    
    
}
