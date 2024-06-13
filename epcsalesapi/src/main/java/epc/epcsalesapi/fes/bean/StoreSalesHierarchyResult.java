package epc.epcsalesapi.fes.bean;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

@Data
public class StoreSalesHierarchyResult {

    private String loginLocation;
    private List<HashMap<String,String>> storeManagers;
    private List<HashMap<String,String>> storeInCharge;

}
