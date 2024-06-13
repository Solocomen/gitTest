package epc.epcsalesapi.rs;

import epc.epcsalesapi.fes.RbdHierarchyHandler;
import epc.epcsalesapi.fes.bean.StoreSalesHierarchyResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/salesOrder/rbdHierarchy")
public class EpcRbdHierarchyService {

    @Autowired
    private RbdHierarchyHandler rbdHierarchyHandler;

    @GetMapping("/nil")
    public StoreSalesHierarchyResult getStoreSalesHierarchy(@RequestParam(value = "loginLocation") String loginLocation){
        return rbdHierarchyHandler.getStoreSalesHierarchy(loginLocation);
    }
}
