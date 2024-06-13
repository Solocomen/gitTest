/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.login;

import epc.epcsalesapi.fes.user.FesUser;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.login.bean.EpcGetUseridResult;
import epc.epcsalesapi.login.bean.EpcLoginResult;
import epc.epcsalesapi.login.bean.EpcSSOInfo;
import epc.epcsalesapi.login.bean.EpcValidateDwp;
import epc.epcsalesapi.login.bean.EpcValidateDwpResult;
import epc.epcsalesapi.sales.bean.EpcLoginChannel;
import epc.epcsalesapi.sales.bean.EpcQuoteContext;

import java.sql.*;
import java.util.*;
import javax.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 *
 * @author KerryTsang
 */

@Service
public class EpcLoginHandler {

    @Autowired
    private DataSource fesDataSource;

    @Autowired
    private EpcSecurityHelper epcSecurityHelper;
    
    private final Logger logger = LoggerFactory.getLogger(EpcLoginHandler.class);

    public EpcLoginResult login(String samlXml) {
        EpcLoginResult epcLoginResult = new EpcLoginResult();
//        javax.ws.rs.client.Client restClient = null;
//        javax.ws.rs.core.Response restResponse = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = "";
        String staffNo = "";
        EpcSSOInfo epcSSOInfo =  null;
        EpcGetUseridResult epcGetUseridResult = null;
        int[] useridArray = null;
        ArrayList<FesUser> userList = null;

        try {
//            restClient = ClientBuilder.newClient();
        	headers.setContentType(MediaType.APPLICATION_XML);
            
            // get staff no from saml xml
//            apiUrl = EpcProperty.getValue("FES.SSO.LINK");
            apiUrl = EpcProperty.getValue("FES_SSO_LINK");
//            restResponse = restClient.target(apiUrl).request().post(
//                Entity.entity(samlXml, MediaType.APPLICATION_XML), Response.class
//            );
            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(samlXml, headers), String.class);

//            if(restResponse.getStatus() == 200) {
            if(responseEntity.getStatusCodeValue() == 200) {
                // good case
//                epcSSOInfo = restResponse.readEntity(EpcSSOInfo.class);
                epcSSOInfo = objectMapper.readValue(responseEntity.getBody(), EpcSSOInfo.class);
                if("OK".equals(epcSSOInfo.getStatus())) {
                    staffNo = epcSSOInfo.getPrivatepersonalidentifier(); // SMC staff no
                } else {
                    // error
                    throw new Exception("error from fes sso service. " + epcSSOInfo.getErrorMessage());
                }
            } else {
                // error
//                throw new Exception("error from fes sso service. http status=" + restResponse.getStatus());
                throw new Exception("error from fes sso service. http status=" + responseEntity.getStatusCodeValue());
            }
            // end of get staff no from saml xml
            
            
            // get fes userid from staff no
//            apiUrl = EpcProperty.getValue("FES.GET.USERID.LINK") + "?staffID=" + staffNo + "&client_id=" + EpcProperty.getValue("FES.GET.USERID.LINK.CLIENTID") + "&client_secret=" + EpcProperty.getValue("FES.GET.USERID.LINK.CLIENTSECRET");
            apiUrl = EpcProperty.getValue("FES_GET_USERID_LINK") + "?staffID=" + staffNo + 
            		"&client_id=" + EpcProperty.getValue("FES_GET_USERID_LINK_CLIENTID") + 
            		"&client_secret=" + EpcProperty.getValue("FES_GET_USERID_LINK_CLIENTSECRET");
//            restResponse = restClient.target(apiUrl).request().header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON).get();
            responseEntity = restTemplate.getForEntity(apiUrl, String.class);
//            if(restResponse.getStatus() == 200) {
            if(responseEntity.getStatusCodeValue() == 200) {
//                epcGetUseridResult = restResponse.readEntity(EpcGetUseridResult.class);
                epcGetUseridResult = objectMapper.readValue(responseEntity.getBody(), EpcGetUseridResult.class);
                if(epcGetUseridResult.getResultCode() == 0) {
                    useridArray = epcGetUseridResult.getUserID();
                } else {
                    // error
                    throw new Exception("error from fes get userid service. resultCode:" + epcGetUseridResult.getResultCode());
                }
            } else {
                // error
//                throw new Exception("error from fes get userid service. http status=" + restResponse.getStatus());
                throw new Exception("error from fes get userid service. http status=" + responseEntity.getStatusCodeValue());
            }
            // end of get fes userid from staff no
            
            
            // prepare fes account
            userList = getFesAccount(useridArray);
            if(userList != null) {
                epcLoginResult.setUserList(userList);
                epcLoginResult.setStaffNo(staffNo);
                epcLoginResult.setResult("OK");
            } else {
                // error
                throw new Exception("cannot retrieve fes account by [" + useridArray.toString() + "]");
            }
            // end of prepare fes account
        } catch (Exception e) {
            e.printStackTrace();
            
            epcLoginResult.setResult("FAIL");
            epcLoginResult.setErrMsg(e.getMessage());
        } finally {
        }
        
        return epcLoginResult;
    }
    
    
    public ArrayList<FesUser> getFesAccount(int[] useridArray) {
        ArrayList<FesUser> userList = new ArrayList<FesUser>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        FesUser fesUser = null;
        
        try {
            conn = fesDataSource.getConnection();
            
            // sql come from fes.admin.login.setCallStmt() - stored pro login_common.get_user_info()
            //  get email_address too
            sql = "select userid, a.username, a.groupid, a.accessrights, status,loginlocation, releasedate, " +
                  "       fullname, salesman, acct_pos_loc, loginretry, street_fighter, dummy_account, language, " +
                  "       staff_id,secret_question,rbd_unit_id,c.groupname, b.subgroupid, card_sequence, " +
                  "       trunc(from_date) - trunc(sysdate),case when to_date is null then 1 else trunc(to_date) - trunc(sysdate) end,  " +
                  "       round(sysdate - lstchangedate),to_char(sysdate, 'ddmmyyyy'), email_address " +
                  "  from user_info a " +
                  "  left join group_info b on  a.groupid = b.groupid " +
                  "  left join  group_info c on b.subgroupid = c.groupid " +
                  " where userid = ? ";
            pstmt = conn.prepareStatement(sql);
            for (int i = 0; i < useridArray.length; i++) {
                pstmt.setInt(1, useridArray[i]); // userid
                rset = pstmt.executeQuery();
                if(rset.next()) {
                    fesUser = new FesUser();
                    fesUser.setUserid(rset.getInt("userid"));
                    fesUser.setUsername(StringHelper.trim(rset.getString("username")));
                    fesUser.setGroupid(rset.getInt("groupid"));
                    fesUser.setAccessrights(StringHelper.trim(rset.getString("accessrights")));
                    fesUser.setSalesman(StringHelper.trim(rset.getString("salesman")));
                    fesUser.setAcctPosLoc(StringHelper.trim(rset.getString("acct_pos_loc")));
                    fesUser.setStreetFighter(StringHelper.trim(rset.getString("street_fighter")));
                    fesUser.setDummyAccount(StringHelper.trim(rset.getString("dummy_account")));
                    fesUser.setLang(StringHelper.trim(rset.getString("language")));
                    fesUser.setStaffId(StringHelper.trim(rset.getString("staff_id")));
                    fesUser.setRbdUnitId(rset.getInt("rbd_unit_id"));
                    fesUser.setGroupName(StringHelper.trim(rset.getString("groupname")));
                    fesUser.setSubgroupId(rset.getInt("subgroupid"));
                    fesUser.setEmailAddress(StringHelper.trim(rset.getString("email_address")));

                    userList.add(fesUser);
                } rset.close(); rset = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            userList = null;
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        
        return userList;
    }
    
    public EpcValidateDwpResult validateDwp(String username, String dwp) throws Exception{

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("FES_APPROVER_LOGIN_LINK");
        String iUsername = username;
        String iDwp = dwp;
        EpcValidateDwp epcValidateDwp = null;
        EpcValidateDwpResult epcValidateDwpResult = new EpcValidateDwpResult();
        
        try {
            epcValidateDwp = new EpcValidateDwp();
            epcValidateDwp.setMnu(iUsername);
            epcValidateDwp.setDwp(iDwp);
            //epcValidateDwp.setDwp(EpcCrypto.eGet(iDwp));
            responseEntity = restTemplate.exchange(apiUrl, HttpMethod.POST, new HttpEntity<EpcValidateDwp>(epcValidateDwp), String.class);
            if(responseEntity.getStatusCodeValue() == 200) {
                epcValidateDwpResult = objectMapper.readValue(responseEntity.getBody(), EpcValidateDwpResult.class);
            } else {
                logger.info("httpStatusCode:" + responseEntity.getStatusCode());
                logger.info(responseEntity.getBody());
                epcValidateDwpResult.setValid(false);
                epcValidateDwpResult.setResultCode("-99");
                epcValidateDwpResult.setMessage("System Error");
            }
            
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            throw e;
        }
        return epcValidateDwpResult;
    }


    public String determineLoginChannel(String fesParentGroupId, String isStreetFighter) {
        String channel = "";

        if("1".equals(fesParentGroupId)) {
            channel = EpcLoginChannel.CS;
        } else if("11".equals(fesParentGroupId)) {
            channel = EpcLoginChannel.STORE;
        } else if("31".equals(fesParentGroupId)) {
            channel = EpcLoginChannel.PR;
        } else if("490".equals(fesParentGroupId)) {
            channel = EpcLoginChannel.TS;
        } else if("Y".equals(StringHelper.trim(isStreetFighter))) {
            channel = EpcLoginChannel.DS;
        } else {
            channel = "";
        }

        return channel;
    }
}
