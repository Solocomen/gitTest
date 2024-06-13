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
public class EpcChangeBag {
    private String oldCustId;
    private String oldQuoteGuid;
    private String newCustId;
    private String createUser;
    private String createSalesman;

    public EpcChangeBag() {
    }

    public String getOldCustId() {
        return oldCustId;
    }

    public void setOldCustId(String oldCustId) {
        this.oldCustId = oldCustId;
    }

    public String getOldQuoteGuid() {
        return oldQuoteGuid;
    }

    public void setOldQuoteGuid(String oldQuoteGuid) {
        this.oldQuoteGuid = oldQuoteGuid;
    }

    public String getNewCustId() {
        return newCustId;
    }

    public void setNewCustId(String newCustId) {
        this.newCustId = newCustId;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateSalesman() {
        return createSalesman;
    }

    public void setCreateSalesman(String createSalesman) {
        this.createSalesman = createSalesman;
    }
    
    
}
