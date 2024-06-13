package epc.epcsalesapi.change;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import epc.epcsalesapi.change.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import epc.epcsalesapi.helper.EpcActionLogHandler;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.bean.EpcActionLog;
import epc.epcsalesapi.sales.EpcOrderHandler;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;

@Service
public class EpcChangeHandlerNew {
    private final Logger logger = LoggerFactory.getLogger(EpcChangeHandlerNew.class);

    private EpcActionLogHandler epcActionLogHandler;
    private EpcOrderHandler epcOrderHandler;

    private final String ACTION_LOG_ACTION = "CHANGE_SIM";

    public EpcChangeHandlerNew(EpcActionLogHandler epcActionLogHandler, EpcOrderHandler epcOrderHandler) {
        this.epcActionLogHandler = epcActionLogHandler;
        this.epcOrderHandler = epcOrderHandler;
    }


    public void changeSim(EpcChangeSim epcChangeSim) {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<WsChangeSimResult> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("WS_APIGW_LINK") + "APIGW/changeSIM";
        WsChangeSim wsChangeSim = new WsChangeSim();
        java.util.Date createDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int changeOrderId = epcOrderHandler.genOrderId();
        EpcActionLog epcActionLog = new EpcActionLog();
        epcActionLog.setAction(ACTION_LOG_ACTION);
        epcActionLog.setUri("");
        String inString = "";
        String outString = "";
        WsChangeSimResult wsChangeSimResult = null;
        EpcActivatePendingLine activatePendingLine = new EpcActivatePendingLine();

        try {
            wsChangeSim.setCustId(epcChangeSim.getCustId());
            wsChangeSim.setCrmOrderId(changeOrderId + "");
            wsChangeSim.setCustNum(epcChangeSim.getCustNum());
            wsChangeSim.setMasterSubr("");
            wsChangeSim.setRootPortfolioId(epcChangeSim.getRootPortfolioId());
            wsChangeSim.setSubrNum(epcChangeSim.getSubrNum());
            wsChangeSim.setSim(epcChangeSim.getNewSimNo());
            wsChangeSim.setImsi(epcChangeSim.getNewImsi());
            wsChangeSim.seteSimFlag("N");
            wsChangeSim.setChannel(epcChangeSim.getModifyChannel());
            wsChangeSim.setExtraContextData(new HashMap<String, Object>());
            wsChangeSim.setRequesterId(epcChangeSim.getModifyUser());
            wsChangeSim.setDealerCode(epcChangeSim.getDealerCode());
            wsChangeSim.setEffectiveDate(sdf.format(createDate));
            wsChangeSim.setDirectSubmission("Y");

            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.PUT, new HttpEntity<>(wsChangeSim), WsChangeSimResult.class);
            wsChangeSimResult = responseEntity.getBody();

            if(wsChangeSimResult.getResultCode() == 0) {
                epcChangeSim.setResult(EpcApiStatusReturn.RETURN_SUCCESS);

                //call Wisespot api gateway to perform "activate now" action
                BeanUtils.copyProperties(wsChangeSim,activatePendingLine);
                ArrayList<String> subrList = new ArrayList<>();
                subrList.add(wsChangeSim.getSubrNum());
                activatePendingLine.setSubrList(subrList);

                activatePendingLine(activatePendingLine);
            } else {
                epcChangeSim.setResult(EpcApiStatusReturn.RETURN_FAIL);
                epcChangeSim.setErrMsg(wsChangeSimResult.getResultMsg());
            }

            inString = objectMapper.writeValueAsString(wsChangeSim);
            outString = objectMapper.writeValueAsString(wsChangeSimResult);
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();

            try {
                outString = hsce.getResponseBodyAsString();
            } catch (Exception eeeee) {
                eeeee.printStackTrace();
            }

            epcChangeSim.setResult(EpcApiStatusReturn.RETURN_FAIL);
        } catch(Exception e) {
            e.printStackTrace();

            outString = e.getMessage();

            epcChangeSim.setResult(EpcApiStatusReturn.RETURN_FAIL);
        } finally {
            epcActionLogHandler.writeApiLogAsync(epcActionLog);
        }
    }

    public EpcActivatePendingLineResult activatePendingLine(EpcActivatePendingLine activatePendingLine)throws Exception{
        RestTemplate restTemplate = new RestTemplate();
        String apiUrl = EpcProperty.getValue("WS_APIGW_LINK") + "APIGW/activatePendingSubrNo";
        HttpEntity<EpcActivatePendingLine> entity = new HttpEntity<>(activatePendingLine);
        EpcActivatePendingLineResult result = null;

        ResponseEntity<EpcActivatePendingLineResult> responseEntity = restTemplate.exchange(apiUrl, HttpMethod.PUT, entity, EpcActivatePendingLineResult.class);
        result = responseEntity.getBody();

        if (result.getResultCode() != 0){
            throw new Exception("Activate pending Subscription No. failed! "+result.getResultMsg());
        }

        return result;
    }

}
