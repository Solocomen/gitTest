package epc.epcsalesapi.sales;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.catalogService.CatalogServiceHandler;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcCompiledSpecEntity;
import epc.epcsalesapi.sales.bean.EpcGetVoucherCompiledSpec;
import epc.epcsalesapi.sales.bean.EpcVmsGetCompiledSpec;

@Service
public class EpcCompiledSpecHandler {

    private CatalogServiceHandler catalogServiceHandler;

    public EpcCompiledSpecHandler(CatalogServiceHandler catalogServiceHandler) {
        this.catalogServiceHandler = catalogServiceHandler;
    }
    

    public EpcVmsGetCompiledSpec getCompiledSpecEntityForPackage_old(EpcVmsGetCompiledSpec epcVmsGetCompiledSpec) {
        String masterVoucherId = epcVmsGetCompiledSpec.getMasterVoucherId();
        String packageGuid = epcVmsGetCompiledSpec.getPackageGuid();
        String apiUrl = EpcProperty.getValue("EPC_COMPILED_SPEC_FOR_VOUCHER") + "?useArea=Master_Voucher_Id&masterVoucherId=" + masterVoucherId + "&epcOfferEntityId=" + packageGuid;
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        EpcGetVoucherCompiledSpec epcGetVoucherCompiledSpec = null;
        ArrayList<EpcCompiledSpecEntity> entityList = null;
        String tmpEntityGuid = "";
        HashMap<String, Object> tmpCharMap = null;
        HashMap<String, Object> tmpCharDetailMap = null;
        Set<String> tmpCharKeySet = null;
        Iterator<String> tmpKeyIterator = null;
        String tmpKey = "";
        HashMap<String, Object> entityMapFromCs = null;
        HashMap<String, Object> tmpComponentMap = null;
        HashMap<String, Object> tmpMetaMap = null;
        String tmpTemplate = "";

        try {
            responseEntity = restTemplate.getForEntity(apiUrl, String.class);
            if(responseEntity.getStatusCodeValue() == 200) {
                epcGetVoucherCompiledSpec = objectMapper.readValue(responseEntity.getBody(), EpcGetVoucherCompiledSpec.class);
                if("ok".equals(epcGetVoucherCompiledSpec.getStatus())) {
                    entityList = epcGetVoucherCompiledSpec.getData();
                    // set ValidToRedeem = Y only if contains Voucher_Redeem_Specification entity, kerrytsang, 20230403
                    if(entityList != null && !entityList.isEmpty()) {
                        for(EpcCompiledSpecEntity epcCompiledSpecEntity : entityList) {
                            tmpEntityGuid = epcCompiledSpecEntity.getEntityId();
                            tmpCharMap = epcCompiledSpecEntity.getCharacteristics();
                            if(tmpCharMap != null) {
                                tmpCharKeySet = tmpCharMap.keySet();
                                if(tmpCharKeySet != null) {
                                    tmpKeyIterator = tmpCharKeySet.iterator();
                                    while(tmpKeyIterator.hasNext()) {
                                        tmpKey = tmpKeyIterator.next();
                                        tmpCharDetailMap = (HashMap<String, Object>)tmpCharMap.get(tmpKey);
                                        if(tmpCharDetailMap.containsValue("Master_Voucher_Id")) {
                                            // get template from CS
                                            entityMapFromCs = catalogServiceHandler.getEntityByGuid(tmpEntityGuid);
                                            tmpComponentMap = (HashMap<String, Object>)entityMapFromCs.get("Component");
                                            if(tmpComponentMap != null) {
                                                tmpMetaMap = (HashMap<String, Object>)tmpComponentMap.get("_meta");
                                                if(tmpMetaMap != null) {
                                                    tmpTemplate = StringHelper.trim((String)tmpMetaMap.get("type"));
                                                    if("Voucher_Redeem_Specification".equals(tmpTemplate)) {
                                                        epcVmsGetCompiledSpec.setValidToRedeem("Y");
                                                        epcVmsGetCompiledSpec.setEntityList(entityList);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }                                
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return epcVmsGetCompiledSpec;
    }


    public EpcVmsGetCompiledSpec getCompiledSpecEntityForPackage(EpcVmsGetCompiledSpec epcVmsGetCompiledSpec) {
        String masterVoucherId = epcVmsGetCompiledSpec.getMasterVoucherId();
        String packageGuid = epcVmsGetCompiledSpec.getPackageGuid();
        String apiUrl = EpcProperty.getValue("EPC_COMPILED_SPEC_FOR_VOUCHER") + "?useArea=Master_Voucher_Id&masterVoucherId=" + masterVoucherId + "&epcOfferEntityId=" + packageGuid;
        RestTemplate restTemplate = new RestTemplate();
        EpcGetVoucherCompiledSpec epcGetVoucherCompiledSpec = null;
        ArrayList<EpcCompiledSpecEntity> entityList = null;
        String tmpEntityGuid = "";
        HashMap<String, Object> tmpCharMap = null;
        HashMap<String, Object> tmpCharDetailMap = null;
        Set<String> tmpCharKeySet = null;
        Iterator<String> tmpKeyIterator = null;
        String tmpKey = "";
        HashMap<String, Object> entityMapFromCs = null;
        HashMap<String, Object> tmpComponentMap = null;
        HashMap<String, Object> tmpMetaMap = null;
        HashMap<String, Object> tmpDates = null; // used to get availableStartDate / availableEndDate
        String tmpTemplate = "";
        SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
        String defaultStartDateStr = "1970-01-01";
        String defaultEndDateStr = "2999-12-31";
        java.util.Date currentDate = null;
        java.util.Date startDate = null;
        java.util.Date endDate = null;
        String tmpStr = "";
        boolean isWithinValidPeriod = false;

        try {
            currentDate = sdfYYYYMMDD.parse(sdfYYYYMMDD.format(new java.util.Date())); // yyyy-MM-dd

            epcGetVoucherCompiledSpec = restTemplate.getForObject(apiUrl, EpcGetVoucherCompiledSpec.class);
            if("ok".equals(epcGetVoucherCompiledSpec.getStatus())) {
                entityList = epcGetVoucherCompiledSpec.getData();
                // set ValidToRedeem = Y only if contains Voucher_Redeem_Specification entity, kerrytsang, 20230403
                if(entityList != null && !entityList.isEmpty()) {
                    for(EpcCompiledSpecEntity epcCompiledSpecEntity : entityList) {
                        tmpEntityGuid = epcCompiledSpecEntity.getEntityId();
                        tmpDates = epcCompiledSpecEntity.getDates();
                        tmpCharMap = epcCompiledSpecEntity.getCharacteristics();

                        // check start date / end date with sysdate
                        if(tmpDates != null && tmpDates.containsKey("availableStartDate")) {
                            tmpStr = StringHelper.trim((String) tmpDates.get("availableStartDate"));
                            if("".equals(tmpStr)) {
                                startDate = sdfYYYYMMDD.parse(defaultStartDateStr);
                            } else {
                                startDate = sdfYYYYMMDD.parse(tmpStr);
                            }
                        } else {
                            startDate = sdfYYYYMMDD.parse(defaultStartDateStr);
                        }

                        if(tmpDates != null && tmpDates.containsKey("availableEndDate")) {
                            tmpStr = StringHelper.trim((String) tmpDates.get("availableEndDate"));
                            if("".equals(tmpStr)) {
                                endDate = sdfYYYYMMDD.parse(defaultEndDateStr);
                            } else {
                                endDate = sdfYYYYMMDD.parse(tmpStr);
                            }
                        } else {
                            endDate = sdfYYYYMMDD.parse(defaultEndDateStr);
                        }

                        if(currentDate.getTime() >= startDate.getTime() && currentDate.getTime() <= endDate.getTime()) {
                            isWithinValidPeriod = true;
                        } else {
                            isWithinValidPeriod = false;
                        }
                        // end of check start date / end date with sysdate

                        if(tmpCharMap != null) {
                            tmpCharKeySet = tmpCharMap.keySet();
                            if(tmpCharKeySet != null) {
                                tmpKeyIterator = tmpCharKeySet.iterator();
                                while(tmpKeyIterator.hasNext()) {
                                    tmpKey = tmpKeyIterator.next();
                                    tmpCharDetailMap = (HashMap<String, Object>)tmpCharMap.get(tmpKey);
                                    if(tmpCharDetailMap.containsValue("Master_Voucher_Id")) {
                                        // get template from CS
                                        entityMapFromCs = catalogServiceHandler.getEntityByGuid(tmpEntityGuid);
                                        tmpComponentMap = (HashMap<String, Object>)entityMapFromCs.get("Component");
                                        if(tmpComponentMap != null) {
                                            tmpMetaMap = (HashMap<String, Object>)tmpComponentMap.get("_meta");
                                            if(tmpMetaMap != null) {
                                                tmpTemplate = StringHelper.trim((String)tmpMetaMap.get("type"));
                                                if("Voucher_Redeem_Specification".equals(tmpTemplate) && isWithinValidPeriod) {
                                                    epcVmsGetCompiledSpec.setValidToRedeem("Y");
                                                    epcVmsGetCompiledSpec.setEntityList(entityList);
                                                }
                                            }
                                        }
                                    }
                                }
                            }                                
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return epcVmsGetCompiledSpec;
    }
}
