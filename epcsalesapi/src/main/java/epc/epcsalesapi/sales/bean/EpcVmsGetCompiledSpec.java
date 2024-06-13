package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcVmsGetCompiledSpec {
    private String masterVoucherId;
    private String packageGuid;
    private String validToRedeem;
    private ArrayList<EpcCompiledSpecEntity> entityList;

    public EpcVmsGetCompiledSpec() {}

    public String getMasterVoucherId() {
        return masterVoucherId;
    }

    public void setMasterVoucherId(String masterVoucherId) {
        this.masterVoucherId = masterVoucherId;
    }

    public String getPackageGuid() {
        return packageGuid;
    }

    public void setPackageGuid(String packageGuid) {
        this.packageGuid = packageGuid;
    }

    public String getValidToRedeem() {
        return validToRedeem;
    }

    public void setValidToRedeem(String validToRedeem) {
        this.validToRedeem = validToRedeem;
    }

    public ArrayList<EpcCompiledSpecEntity> getEntityList() {
        return entityList;
    }

    public void setEntityList(ArrayList<EpcCompiledSpecEntity> entityList) {
        this.entityList = entityList;
    }

    
}
