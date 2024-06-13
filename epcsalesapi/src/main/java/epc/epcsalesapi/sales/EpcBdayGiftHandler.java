package epc.epcsalesapi.sales;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelGiftRedeem;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelGiftRedeemResult;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelRedeem;
import epc.epcsalesapi.sales.bean.bdayGift.EpcCancelRedeemResult;
import epc.epcsalesapi.sales.bean.bdayGift.EpcGetBdayGift;
import epc.epcsalesapi.sales.bean.bdayGift.EpcGetBdayGiftResult;
import epc.epcsalesapi.sales.bean.bdayGift.EpcGetGiftRedemptionList;
import epc.epcsalesapi.sales.bean.bdayGift.EpcGetGiftRedemptionListResult;
import epc.epcsalesapi.sales.bean.bdayGift.EpcGiftRedemption;
// added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): start 
import java.util.ArrayList;
import java.util.HashMap;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import epc.epcsalesapi.sales.bean.bdayGift.EpcGiftRedeem;
import epc.epcsalesapi.sales.bean.bdayGift.EpcGiftRedeemResult;
import epc.epcsalesapi.sales.bean.bdayGift.EpcRedeem;
import epc.epcsalesapi.sales.bean.bdayGift.EpcRedeemResult;
// added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): end 

@Service
public class EpcBdayGiftHandler {
    
    private final Logger logger = LoggerFactory.getLogger(EpcBdayGiftHandler.class);
    
    @Autowired
    private EpcSecurityHelper epcSecurityHelper;

    public EpcGetBdayGiftResult enquire(EpcGetBdayGift epcGetBdayGift) {
        
        EpcGetBdayGiftResult epcGetBdayGiftResult = new EpcGetBdayGiftResult();
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_BIRTHDAY_GIFT_LINK") + "/getGiftRedemptionList";
        String userName = epcSecurityHelper.validateString(epcGetBdayGift.getUserName());
        String subrNum = epcSecurityHelper.validateString(epcGetBdayGift.getSubrNum());
        String custNum = epcSecurityHelper.validateString(epcGetBdayGift.getCustNum());
        String giftId = epcSecurityHelper.validateString(epcGetBdayGift.getGiftId());
        
        try {
            
            if ("".equals(userName)) {
                epcGetBdayGiftResult.setResult("FAIL");
                epcGetBdayGiftResult.setErrorCode("-1");
                epcGetBdayGiftResult.setErrorMessage("Missing userName.");
                return epcGetBdayGiftResult;
            }
            if ("".equals(subrNum)) {
                epcGetBdayGiftResult.setResult("FAIL");
                epcGetBdayGiftResult.setErrorCode("-1");
                epcGetBdayGiftResult.setErrorMessage("Missing subrNum.");
                return epcGetBdayGiftResult;
            }
            if ("".equals(custNum)) {
                epcGetBdayGiftResult.setResult("FAIL");
                epcGetBdayGiftResult.setErrorCode("-1");
                epcGetBdayGiftResult.setErrorMessage("Missing custNum.");
                return epcGetBdayGiftResult;
            }
            if ("".equals(giftId)) {
                epcGetBdayGiftResult.setResult("FAIL");
                epcGetBdayGiftResult.setErrorCode("-1");
                epcGetBdayGiftResult.setErrorMessage("Missing giftId.");
                return epcGetBdayGiftResult;
            }

            EpcGetGiftRedemptionList epcGetGiftRedemptionList = new EpcGetGiftRedemptionList();
            epcGetGiftRedemptionList.setRequesterId(userName);
            epcGetGiftRedemptionList.setSubrNum(subrNum);
            epcGetGiftRedemptionList.setCustNum(custNum);
            epcGetGiftRedemptionList.setRedeemStatus("Pending for Redeem");
            epcGetGiftRedemptionList.setMembershipTier("Plus");

            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcGetGiftRedemptionList), String.class);
            EpcGetGiftRedemptionListResult epcGetGiftRedemptionListResult = objectMapper.readValue(responseEntity.getBody(), EpcGetGiftRedemptionListResult.class);
            if("0".equals(epcGetGiftRedemptionListResult.getResultCode())) {
                epcGetBdayGiftResult.setResult("FAIL");
                epcGetBdayGiftResult.setErrorCode("-3");
                epcGetBdayGiftResult.setErrorMessage("No available gift redemption.");				
                for(EpcGiftRedemption epcGiftRedemption:epcGetGiftRedemptionListResult.getGiftList()) {
                    if ( ((epcGiftRedemption.getSubOfferId()==null || epcGiftRedemption.getSubOfferId().equals("") || epcGiftRedemption.getSubOfferId().equals("396")) && giftId.equals("396")) || 
                        epcGiftRedemption.getSubOfferId().equals( giftId ) )  {
                        epcGetBdayGiftResult.setResult("SUCCESS");
                        epcGetBdayGiftResult.setErrorCode("0");
                        epcGetBdayGiftResult.setErrorMessage("");
                        break;
                    }
                }
            } else if (!"-3".equals(epcGetGiftRedemptionListResult.getResultCode())) {
                epcGetBdayGiftResult.setResult("FAIL");
                epcGetBdayGiftResult.setErrorCode("-2");
                epcGetBdayGiftResult.setErrorMessage(epcGetGiftRedemptionListResult.getResultMsg());
            } else {
                epcGetBdayGiftResult.setResult("FAIL");
                epcGetBdayGiftResult.setErrorCode("-3");
                epcGetBdayGiftResult.setErrorMessage(epcGetGiftRedemptionListResult.getResultMsg());
            }
            
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            epcGetBdayGiftResult.setResult("FAIL");
            epcGetBdayGiftResult.setErrorCode("-4");
            epcGetBdayGiftResult.setErrorMessage("System Error.");
        }
        return epcGetBdayGiftResult;
    }
    
    // added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): start 
    public EpcRedeemResult redeem(EpcRedeem epcRedeem) {
	EpcRedeemResult epcRedeemResult = new EpcRedeemResult();
	RestTemplate restTemplate = new RestTemplate();
	ResponseEntity<String> responseEntity = null;
	ObjectMapper objectMapper = new ObjectMapper();
	String apiUrl = EpcProperty.getValue("EPC_BIRTHDAY_GIFT_LINK") + "/giftRedeem";
	String subrNum = epcSecurityHelper.validateString(epcRedeem.getSubrNum());
	String custNum = epcSecurityHelper.validateString(epcRedeem.getCustNum());
	String salesman = epcSecurityHelper.validateString(epcRedeem.getSalesman());
	String redemptionDate = epcRedeem.getRedemptionDate();
	String userName = epcSecurityHelper.validateString(epcRedeem.getUserName());
	String rbdUnitCode = epcSecurityHelper.validateString(epcRedeem.getRbdUnitCode());
		
	try {
            
            if ("".equals(subrNum)) {
                epcRedeemResult.setResult("FAIL");
                epcRedeemResult.setErrorCode("-1");
                epcRedeemResult.setErrorMessage("Missing subrNum.");
                return epcRedeemResult;
            }
            if ("".equals(custNum)) {
                epcRedeemResult.setResult("FAIL");
                epcRedeemResult.setErrorCode("-1");
                epcRedeemResult.setErrorMessage("Missing custNum.");
                return epcRedeemResult;
            }
            if ("".equals(salesman)) {
                epcRedeemResult.setResult("FAIL");
                epcRedeemResult.setErrorCode("-1");
                epcRedeemResult.setErrorMessage("Missing salesman.");
                return epcRedeemResult;
            }
            if ("".equals(redemptionDate)) {
                epcRedeemResult.setResult("FAIL");
                epcRedeemResult.setErrorCode("-1");
                epcRedeemResult.setErrorMessage("Missing redemptionDate.");
                return epcRedeemResult;
            }
			if ("".equals(userName)) {
                epcRedeemResult.setResult("FAIL");
                epcRedeemResult.setErrorCode("-1");
                epcRedeemResult.setErrorMessage("Missing userName.");
                return epcRedeemResult;
            }
			if ("".equals(rbdUnitCode)) {
                epcRedeemResult.setResult("FAIL");
                epcRedeemResult.setErrorCode("-1");
                epcRedeemResult.setErrorMessage("Missing rbdUnitCode.");
                return epcRedeemResult;
            }
	    
            EpcGiftRedeem epcGiftRedeem = new EpcGiftRedeem();

			epcGiftRedeem.setCustNum(custNum);
			epcGiftRedeem.setSubrNum(subrNum);
			epcGiftRedeem.setSalesman(salesman);
			epcGiftRedeem.setRedemptionDate(redemptionDate);
			epcGiftRedeem.setRequesterId(userName);
			epcGiftRedeem.setRbdUnitCode(rbdUnitCode);

			responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcGiftRedeem), String.class);

			System.out.println("responseEntity.getBody() = " + responseEntity.getBody());	    
	    
            EpcGiftRedeemResult epcGiftRedeemResult = objectMapper.readValue(responseEntity.getBody(), EpcGiftRedeemResult.class);

			if (!epcGiftRedeemResult.getResultCode().equals("0")) {
			    epcRedeemResult.setErrorCode("-2");
				epcRedeemResult.setResult("FAIL");
				epcRedeemResult.setErrorMessage(epcGiftRedeemResult.getResultMsg());
				return epcRedeemResult;
			} 
	    
			epcRedeemResult.setResult("SUCCESS");
			epcRedeemResult.setErrorCode("0");
			epcRedeemResult.setErrorMessage("");
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            epcRedeemResult.setResult("FAIL");
            epcRedeemResult.setErrorCode("-4");
            epcRedeemResult.setErrorMessage("System Error.");
        }
        return epcRedeemResult;
    }
    
	public EpcCancelRedeemResult cancelRedeem(EpcCancelRedeem epcCancelRedeem) {
		EpcCancelRedeemResult epcCancelRedeemResult = new EpcCancelRedeemResult();
		RestTemplate restTemplate = new RestTemplate();
		ResponseEntity<String> responseEntity = null;
		ObjectMapper objectMapper = new ObjectMapper();
		String apiUrl = EpcProperty.getValue("EPC_BIRTHDAY_GIFT_LINK") + "/cancelGiftRedeem";
		String subrNum = epcSecurityHelper.validateString(epcCancelRedeem.getSubrNum());
		String custNum = epcSecurityHelper.validateString(epcCancelRedeem.getCustNum());
		String salesman = epcSecurityHelper.validateString(epcCancelRedeem.getSalesman());
		String userName = epcSecurityHelper.validateString(epcCancelRedeem.getUserName());
		
		try {            
			if ("".equals(subrNum)) {
				epcCancelRedeemResult.setResult("FAIL");
				epcCancelRedeemResult.setErrorCode("-1");
                epcCancelRedeemResult.setErrorMessage("Missing subrNum.");
                return epcCancelRedeemResult;
            }
            if ("".equals(custNum)) {
                epcCancelRedeemResult.setResult("FAIL");
                epcCancelRedeemResult.setErrorCode("-1");
                epcCancelRedeemResult.setErrorMessage("Missing custNum.");
                return epcCancelRedeemResult;
            }
            if ("".equals(salesman)) {
                epcCancelRedeemResult.setResult("FAIL");
                epcCancelRedeemResult.setErrorCode("-1");
                epcCancelRedeemResult.setErrorMessage("Missing salesman.");
                return epcCancelRedeemResult;
            }
			if ("".equals(userName)) {
                epcCancelRedeemResult.setResult("FAIL");
                epcCancelRedeemResult.setErrorCode("-1");
                epcCancelRedeemResult.setErrorMessage("Missing userName.");
                return epcCancelRedeemResult;
            }
	    
            EpcCancelGiftRedeem epcCancelGiftRedeem = new EpcCancelGiftRedeem();

			epcCancelGiftRedeem.setCustNum(custNum);
			epcCancelGiftRedeem.setSubrNum(subrNum);
			epcCancelGiftRedeem.setSalesman(salesman);
			epcCancelGiftRedeem.setRequesterId(userName);
	    
            System.out.println( "@@@ epcCancelGiftRedeem = " + new org.json.JSONObject(epcCancelGiftRedeem).toString() )   ;

            responseEntity = restTemplate.postForEntity(apiUrl, new HttpEntity<>(epcCancelGiftRedeem), String.class);

            System.out.println("responseEntity.getBody() = " + responseEntity.getBody());	    
	    
            EpcCancelGiftRedeemResult epcCancelGiftRedeemResult = objectMapper.readValue(responseEntity.getBody(), EpcCancelGiftRedeemResult.class);

			if (!epcCancelGiftRedeemResult.getResultCode().equals("0")) {
				epcCancelRedeemResult.setErrorCode("-2");
				epcCancelRedeemResult.setResult("FAIL");
				epcCancelRedeemResult.setErrorMessage(epcCancelGiftRedeemResult.getResultMsg());
				return epcCancelRedeemResult;
			} 
	    
			epcCancelRedeemResult.setResult("SUCCESS");
			epcCancelRedeemResult.setErrorCode("0");
			epcCancelRedeemResult.setErrorMessage("");
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            epcCancelRedeemResult.setResult("FAIL");
            epcCancelRedeemResult.setErrorCode("-4");
            epcCancelRedeemResult.setErrorMessage("System Error.");
        }
        return epcCancelRedeemResult;
    }    
		
	public boolean hasBirthdayCharge(String caseId, Connection conn) throws SQLException {
		String sql = "SELECT 1 FROM epc_order_item WHERE case_id = ? AND cpq_item_desc = 'Birthday Gift Discount Charge' and template_name = 'Non-Recurring_Charge_Template'";
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, caseId);
			
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				return true;
			}
			
			return false;
		} catch (SQLException  e) {
			throw e;
		} finally {
			try {rs.close();}
			catch (Exception e) {}
			try {pstmt.close();}
			catch (Exception e) {}
		}
	}	

	public ArrayList<String> getBirthDayChargeIdList(EpcQuoteItem epcQuoteItem) {
		ArrayList<String> bday_charge_id_list = new ArrayList();
			    
		String metaDataType_keys[] = epcQuoteItem.getMetaDataLookup().keySet().toArray(new String[0]);

		for (int i=0; i<metaDataType_keys.length; i++) {
			String k = metaDataType_keys[i];

			HashMap<String, Object> m = (HashMap<String, Object>)epcQuoteItem.getMetaDataLookup();
	
			if ( !(m.get(k) instanceof HashMap) ) continue;
	
			HashMap<String, Object> m2 = (HashMap<String, Object>)m.get(k);
	
			String name = (String)m2.get("name");
			String typePath = (String)m2.get("typePath");

			if (name==null || typePath==null) continue;
	
			if (name.equals("Birthday Gift Discount Charge") && typePath.startsWith("Non-Recurring_Charge_Template")) {
				bday_charge_id_list.add(k);
			}
		}
	
		return bday_charge_id_list;
    }
    
	public boolean hasBirthdayCharge(EpcQuoteItem epcQuoteItem) {

		ArrayList<String> bday_charge_id_list = getBirthDayChargeIdList(epcQuoteItem);
	
		//bday_charge_id_list  = new ArrayList();
		//bday_charge_id_list.add("CS_fd216c2c-1568-48e9-831d-f48fb939db4");
	
		return hasBirthdayCharge(epcQuoteItem.getProductCandidate(), bday_charge_id_list);
    }
           
    public boolean hasBirthdayCharge(HashMap<String, Object> json_obj, ArrayList<String> bday_charge_id_list) {
	    
		String id = (String)json_obj.get("ID");
		
		if (bday_charge_id_list.contains(id)) {
			return true;
		}
	
		if ( json_obj.get("ChildEntity")!=null ) {
			ArrayList<HashMap<String, Object>> childList = (ArrayList<HashMap<String, Object>>)json_obj.get("ChildEntity");
		
			for (int i=0; i<childList.size(); i++) {
				if ( hasBirthdayCharge( childList.get(i), bday_charge_id_list) ) {
					return true;
				}
			}
		
			return false;
		}
	  
	return false;
    }
    // added by Danny Chan on 2023-1-5 (Birthday Gift Enhancement): end 
}
