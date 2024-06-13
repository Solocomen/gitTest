package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import javax.sql.DataSource;

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

import epc.epcsalesapi.billing.EpcBillingHandler;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcCpqError;
import epc.epcsalesapi.sales.bean.EpcCustNumSubrNum;
import epc.epcsalesapi.sales.bean.EpcCustProfile;
import epc.epcsalesapi.sales.bean.EpcGetCustProfileResult;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcQuoteContext;
import epc.epcsalesapi.sales.bean.EpcSubr;


@Service
public class EpcCustProfileHandler {

	private final Logger logger = LoggerFactory.getLogger(EpcCustProfileHandler.class);
	
	@Autowired
	private DataSource epcDataSource;
	
	@Autowired
	private EpcBillingHandler epcBillingHandler;
	
	@Autowired
	private EpcOrderHandler epcOrderHandler;
	
	@Autowired
	private EpcQuoteHandler epcQuoteHandler;
	
	@Autowired
	private EpcOrderTypeHandler epcOrderTypeHandler;
	
	@Autowired
	private EpcSecurityHelper epcSecurityHelper;
	
	
	public boolean saveTmpCustProfile(int smcOrderId, String custId, String caseId, String itemId, String custNum, String subrNum, String effectiveDate, String activationType, String content) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        boolean isInsert = false;
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_cust_profile " +
                  "  set status = ?, modify_date = sysdate " +
                  " where order_id = ? " +
                  "   and case_id = ? " +
                  "   and item_id = ? " + 
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "O"); // status - obsolete
            pstmt.setInt(2, smcOrderId); // order_id
            pstmt.setString(3, caseId); // case_id
            pstmt.setString(4, itemId); // item_id
            pstmt.setString(5, "A"); // status - active
            pstmt.executeUpdate();
            
            if(!"".contains(content)) {
                sql = "insert into epc_order_cust_profile ( " +
                        "  rec_id, order_id, cust_id, case_id, cust_info, " +
                        "  status, create_date, item_id " +
                        ") values ( " +
                        "  epc_order_id_seq.nextval,?,?,?,?, " + 
                        "  ?,sysdate,?  " +
                        ") ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, smcOrderId); // order_id
                pstmt.setString(2, custId); // cust_id
                pstmt.setString(3, caseId); // case_id
                pstmt.setString(4, content); // cust_info
                pstmt.setString(5, "A"); // status
                pstmt.setString(6, itemId); // item_id
                pstmt.executeUpdate();
            }
            
            conn.commit();
            
            isInsert = true;
        } catch(Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isInsert;
    }
	
	
	public EpcCustProfile createCustProfile(EpcCustProfile epcCustProfile) {
        EpcQuote epcQuote = null;
        EpcQuoteContext epcQuoteContext = null;
        EpcCpqError epcCpqError = null;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<String> responseEntity = null;
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_CPQ_LINK") + "quotes";
        String custId = "";
        int quoteId = 0;
        String quoteGuid = "";
        String caseId = "";
        String itemId = "";
        String custNum = "";
        String subrNum = "";
        String custFirstName = "";
        String custLastName = "";
        String custTitle = "";
        String hkidbr = "";
        String idType = "";
        String dob = "";
        String contactNo1 = "";
        String contactNo2 = "";
        String contactPerson = "";
        String email = "";
        String address1 = "";
        String address2 = "";
        String address3 = "";
        String address4 = "";
        String effectiveDate = "";
        String paymentMethod = "";
        String dno = "";
        String mnpPrepaidSim = "";
        String mnpHkidbr = "";
        String dm = "";
        String dmCompany = "";
        String lastUpdateDate = "";
        String activationType = ""; // NEW / MNP / MVNO
        String lang = "";
        String dealerCode = ""; // sim dealer code
        String decryptStr = "";
        String billDay = "";
        boolean isValid = true;
        String errMsg = "";
        java.util.Date tmpEffectiveDate = null;
        java.util.Date currentDate = null;
        SimpleDateFormat sdfYYYYMMDD = new SimpleDateFormat("yyyyMMdd");
        String orderType = "";
        String jsonString = "";
        boolean isInsert = false;
        String smcOrderIdStr = "";
        int smcOrderId = 0;
        String action = "";
        HashMap<String, Object> currContextDataMap = null;
        ArrayList<HashMap<String, Object>> smcCasesMap = null;
        HashMap<String, Object> smcCaseMap = null;
        HashMap<String, Object> smcCustInfoMap = null;
        String orderReference = "";
        HashMap<String, Object> orderMap = null;
        
        
        try {
        	smcOrderId = epcCustProfile.getOrderId();
            custId = StringHelper.trim(epcCustProfile.getCustId());
            quoteId = epcCustProfile.getQuoteId();
            caseId = StringHelper.trim(epcCustProfile.getCaseId());
            itemId = StringHelper.trim(epcCustProfile.getItemId());
            custNum = StringHelper.trim(epcCustProfile.getCustNum());
            subrNum = StringHelper.trim(epcCustProfile.getSubrNum());
            custFirstName = StringHelper.trim(epcCustProfile.getCustFirstName());
            custLastName = StringHelper.trim(epcCustProfile.getCustLastName());
            custTitle = StringHelper.trim(epcCustProfile.getCustTitle());
            hkidbr = StringHelper.trim(epcCustProfile.getHkidbr());
            idType = StringHelper.trim(epcCustProfile.getIdType());
            dob = StringHelper.trim(epcCustProfile.getDob());
            contactNo1 = StringHelper.trim(epcCustProfile.getContactNo1());
            contactNo2 = StringHelper.trim(epcCustProfile.getContactNo2());
            contactPerson = StringHelper.trim(epcCustProfile.getContactPerson());
            email = StringHelper.trim(epcCustProfile.getEmail());
            address1 = StringHelper.trim(epcCustProfile.getAddress1());
            address2 = StringHelper.trim(epcCustProfile.getAddress2());
            address3 = StringHelper.trim(epcCustProfile.getAddress3());
            address4 = StringHelper.trim(epcCustProfile.getAddress4());
            effectiveDate = StringHelper.trim(epcCustProfile.getEffectiveDate()); // yyyymmddhh24miss
            paymentMethod = StringHelper.trim(epcCustProfile.getPaymentMethod());
            dno = StringHelper.trim(epcCustProfile.getDno());
            mnpPrepaidSim = StringHelper.trim(epcCustProfile.getMnpPrepaidSim());
            mnpHkidbr = StringHelper.trim(epcCustProfile.getMnpHkidbr());
            lang = StringHelper.trim(epcCustProfile.getLang());
            dm = StringHelper.trim(epcCustProfile.getDm());
            dmCompany = StringHelper.trim(epcCustProfile.getDmCompany());
            dealerCode = StringHelper.trim(epcCustProfile.getDealerCode());
            action = StringHelper.trim(epcCustProfile.getAction());

            // basic checking
            if(smcOrderId <= 0) {
            	errMsg += "input order id [" + smcOrderId + "] is invalid. ";
            	isValid = false;
            }
            
            orderReference = epcOrderHandler.isOrderBelongCust(custId, smcOrderId);
        	if("NOT_BELONG".equals(orderReference)) {
        		errMsg += "input order id [" + smcOrderId + "] is not belonged to input cust id [" + custId + "]. ";
            	isValid = false;
        	}
        	
        	quoteGuid = epcOrderHandler.getCurrentQuoteGuid(smcOrderId, quoteId);
        	if("".equals(quoteGuid)) {
        		errMsg += "input order/quote id [" + smcOrderId + "/" + quoteId + "] is not valid. ";
            	isValid = false;
        	} else {
        		epcQuote = epcQuoteHandler.getQuoteInfo(quoteGuid);
        	}
            
            if(!"".equals(custId)) {
                // check with crm ?
                // ...
            } else {
                isValid = false;
                errMsg += "cust id is empty. ";
            }
            
            if(!"".equals(caseId)) {
                // ...
            } else {
                isValid = false;
                errMsg += "case id is empty. ";
            }
            
            if(!"".equals(itemId)) {
                // ...
            } else {
                isValid = false;
                errMsg += "item id is empty. ";
            }
            
            if("ADD".equals(action)) {
                if(!"".equals(custNum)) {
                    // ...
                }

                if(!"".equals(subrNum)) {
                    // check subr num
                    //  check existing ?
                    //  check dno ?
                } else {
                    isValid = false;
                    errMsg += "mobile no. is empty. ";
                }

                if(!"".equals(custFirstName)) {
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(custFirstName, "utf-8"));

                        if("".equals(decryptStr)) {
                            isValid = false;
                            errMsg += "customer first name is empty. ";
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "customer first name can't be decrypted. ";
                    }
                } else {
                    isValid = false;
                    errMsg += "customer first name is empty. ";
                }

                if(!"".equals(custLastName)) {
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(custLastName, "utf-8"));

                        if("".equals(decryptStr)) {
                            isValid = false;
                            errMsg += "customer last name is empty. ";
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "customer last name can't be decrypted. ";
                    }
                } else {
                    isValid = false;
                    errMsg += "customer last name is empty. ";
                }

                if("".equals(custTitle)) {
                    isValid = false;
                    errMsg += "cust title is empty. ";
                } else {
                    if(!"Mr".equals(custTitle) && !"Mrs".equals(custTitle) && !"Ms".equals(custTitle)) {
                        isValid = false;
                        errMsg += "invalid cust title. ";
                    }
                }

                if(!"".equals(hkidbr)) {
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(hkidbr, "utf-8"));

                        if("".equals(decryptStr)) {
                            isValid = false;
                            errMsg += "hkid is empty. ";
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "hkid can't be decrypted. ";
                    }
                } else {
                    isValid = false;
                    errMsg += "hkid is empty. ";
                }

                if(!"".equals(dob)) {
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(dob, "utf-8"));

                        if("".equals(decryptStr)) {
                            isValid = false;
                            errMsg += "date of birth is empty. ";
                        } else {
                            // check valid date format ?
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "date of birth can't be decrypted. ";
                    }
                } else {
                    isValid = false;
                    errMsg += "date of birth is empty. ";
                }

                if("".equals(contactNo1)) {
                    isValid = false;
                    errMsg += "contact no 1 is empty. ";
                }

                if("".equals(contactNo2)) {
                }

                if(!"".equals(email)) {
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(email, "utf-8"));

                        if("".equals(decryptStr)) {
                            isValid = false;
                            errMsg += "email is empty. ";
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "email can't be decrypted. ";
                    }
                } else {
                    isValid = false;
                    errMsg += "email is empty. ";
                }

                if(!"".equals(address1)) {
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(address1, "utf-8"));

                        if("".equals(decryptStr)) {
                            isValid = false;
                            errMsg += "address1 is empty. ";
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "address1 can't be decrypted. ";
                    }
                } else {
                    isValid = false;
                    errMsg += "address1 is empty. ";
                }

                if(!"".equals(address2)) {
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(address2, "utf-8"));

                        if("".equals(decryptStr)) {
                            isValid = false;
                            errMsg += "address2 is empty. ";
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "address2 can't be decrypted. ";
                    }
                } else {
                    isValid = false;
                    errMsg += "address2 is empty. ";
                }

                if(!"".equals(address3)) {
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(address3, "utf-8"));

                        if("".equals(decryptStr)) {
                            isValid = false;
                            errMsg += "address3 is empty. ";
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "address3 can't be decrypted. ";
                    }
                } else {
                    isValid = false;
                    errMsg += "address3 is empty. ";
                }
                if(!"".equals(address4)) {
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(address4, "utf-8"));

                        if("".equals(decryptStr)) {
                            isValid = false;
                            errMsg += "address4 is empty. ";
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "address4 can't be decrypted. ";
                    }
                } else {
                    isValid = false;
                    errMsg += "address4 is empty. ";
                }

                if("".equals(effectiveDate)) {
                    isValid = false;
                    errMsg += "effective date is empty. ";
                } else {
                    // check valid date format
                    try {
                        tmpEffectiveDate = new SimpleDateFormat("yyyyMMddHHmmss").parse(effectiveDate);
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "format of effective date " + effectiveDate + " is not correct. ";
                    }
                }

                if("".equals(paymentMethod)) {
                    isValid = false;
                    errMsg += "payment menthod is empty. ";
                }

                if(!"".equals(dno)) {
                    if("".equals(mnpPrepaidSim) && "".equals(mnpHkidbr)) {
                        isValid = false;
                        errMsg += "MNP Prepaid sim no / HKID is empty for MNP case. ";
                    }

                    if(!"".equals(mnpHkidbr)) {
                        try {
                            decryptStr = StringHelper.trim(EpcCrypto.dGet(mnpHkidbr, "utf-8"));

                            if("".equals(decryptStr)) {
                                isValid = false;
                                errMsg += "MNP HKID is empty. ";
                            }
                        } catch (Exception e) {
                            isValid = false;
                            errMsg += "MNP HKID can't be decrypted. ";
                        }
                    }
                }

                if(!"E".equals(lang) && !"C".equals(lang)) {
                    isValid = false;
                    errMsg += "Language is invalid. ";
                }

                if(!"Y".equals(dm) && !"N".equals(dm)) {
                    isValid = false;
                    errMsg += "DM opt-in flag is invalid. ";
                }

                if(!"Y".equals(dmCompany) && !"N".equals(dmCompany)) {
                    isValid = false;
                    errMsg += "DM (company) opt-in flag is invalid. ";
                }
            }
            
            if(!"ADD".equals(action) && !"DELETE".equals(action)) {
                isValid = false;
                errMsg += "action is invalid. ";
            }
            // end of basic checking
            
            
            if(isValid) {
                if("DELETE".equals(action)) {
                    // delete case
                    
                    // remove from quote context
                    currContextDataMap = epcQuote.getContextData();
                    smcCasesMap = (ArrayList<HashMap<String, Object>>)currContextDataMap.get("SMCCases");
                    if(smcCasesMap != null) {
                        for (int i = 0; i < smcCasesMap.size(); i++) {
                            smcCaseMap = smcCasesMap.get(i);
                            
                            if(caseId.equals(StringHelper.trim((String)smcCaseMap.get("SMCCaseId")))) {
                                smcCasesMap.remove(i);
                                break;
                            }
                        }
                    }
                    

                    apiUrl += "/" + quoteGuid + "/contextData";
                    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    responseEntity = restTemplate.exchange(apiUrl, HttpMethod.PUT, new HttpEntity<>(currContextDataMap, headers), String.class);
                    
                    
                    // remove from epc table
                    isInsert = saveTmpCustProfile(smcOrderId, custId, caseId, itemId, "", "", "", "", "");
                    if(isInsert) {
                        epcCustProfile.setResult("SUCCESS");
                    } else {
                    	epcCustProfile.setResult("FAIL");
                    	epcCustProfile.setErrMsg("cannot remove cust profile in tmp table");
                    }
                } else {
                    // add case
                
                    // determine activation type NEW / MNP / MVNO
                    if("".equals(dno)) {
                        activationType = "NEW";
                    } else if ("S3".equals(dno)) {
                        activationType = "MVNO";

                        // generate tmp no.
                        //  ...
                    } else {
                        activationType = "MNP";
                    }
                    // end of determine activation type NEW / MNP / MVNO
                
                    // determine order type
                    orderType = epcOrderTypeHandler.determineSigmaOrderType(epcOrderTypeHandler.actionTypeNewActivation, effectiveDate);
                    // end of determine order type
                
                
                    // generate new billing account (customer reference / cust num) & bill cycle (billday)
                    //  if input is empty, then generate a new cust num & bill day
                    if("".equals(custNum)) {
	                    custNum = epcBillingHandler.getNewCustomerReference();
	                    if("ERROR".equals(custNum)) {
	                        throw new Exception("cannot get new customer reference");
	                    }
	                    
	                    billDay = epcBillingHandler.getNextBillDay();	                    
                    }
                    // end of generate new billing account (customer reference / cust num) & bill cycle (billday)
                
                
                    // construct context object
                    // get current data
                    if(epcQuote != null) {
                    	currContextDataMap = epcQuote.getContextData();
                    	orderMap = (HashMap<String, Object>)currContextDataMap.get("order");
                    }
                    if(orderMap == null) {
                        throw new Exception("entity 'order' is not found in quote context");
                    } 
                    // end of get current data
                    

                    epcQuoteContext = new EpcQuoteContext();
                    if(epcQuote != null) {
                    	epcQuoteContext.setQuoteLastUpdated(epcQuote.getUpdated());
                    } else {
                    	epcQuoteContext.setQuoteLastUpdated("2018-12-04 07:11:59.411Z");
                    }

                    HashMap<String, Object> contextDataMap = new HashMap<String, Object>();
                    epcQuoteContext.setContextData(contextDataMap);
                
                    // as we will create PE line, such activation date will be added during quote submission (inside placeorder), kerrytsang, 20240104
                    orderMap.put("Requested_Activation_Date", "");

                    orderMap.put("Dealer_Code", dealerCode);
// dno is not needed for new act case, kerrytsang, 20240104
//                    orderMap.put("DNO", dno);
                    orderMap.put("Order_Type", orderType);
                    contextDataMap.put("order", orderMap);
                    // end of fill extra data to orderMap
                
                    HashMap<String, Object> customerMap = new HashMap<String, Object>();
                    customerMap.put("Payment_Method", paymentMethod);
                    customerMap.put("Cust_Num", custNum);
                    customerMap.put("New_Cust_Num", "");
                    contextDataMap.put("customer", customerMap);
                
                    // end of construct context object


                    apiUrl += "/" + quoteId + "/contextData";
                    headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    responseEntity = restTemplate.exchange(apiUrl, HttpMethod.PUT, new HttpEntity<>(epcQuoteContext, headers), String.class);

                    if(responseEntity.getStatusCodeValue() == 200) {
                        // good case
                        jsonString = objectMapper.writeValueAsString(epcCustProfile);

                        isInsert = saveTmpCustProfile(smcOrderId, custId, caseId, itemId, custNum, subrNum, effectiveDate, activationType, jsonString);
                        if(isInsert) {
                            epcCustProfile.setResult("SUCCESS");
                        } else {
                            epcCustProfile.setResult("FAIL");
                            epcCustProfile.setErrMsg("cannot save cust profile in tmp table");
                        }
                    } else {
                        // error
                        epcCpqError = objectMapper.readValue(responseEntity.getBody(), EpcCpqError.class);

                        epcCustProfile.setResult("FAIL");
                        epcCustProfile.setErrMsg(epcCpqError.getResponseText());
                    }
                }
            } else {
                epcCustProfile.setResult("FAIL");
                epcCustProfile.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();
            
            epcCustProfile.setResult("FAIL");
            epcCustProfile.setErrMsg(e.getMessage());
        } finally {
        }
        return epcCustProfile;
    }
	
	
	public EpcGetCustProfileResult getCustProfile(int orderId, String custId, String masked) {
    	EpcGetCustProfileResult epcGetCustProfileResult = new EpcGetCustProfileResult();
    	epcGetCustProfileResult.setCustId(StringHelper.trim(custId));
    	epcGetCustProfileResult.setOrderId(orderId);
    	String orderReference = "";
    	ArrayList<HashMap<String, Object>> aList = null;
    	String maskedStr = StringHelper.trim(masked);
    	if("".equals(masked)) {
    		maskedStr = "N";
    	}
    	boolean isValid = true;
    	String errMsg = "";
    	
    	try {
    		// basic checking
    		if(orderId <= 0) {
            	errMsg += "input order id [" + orderId + "] is invalid. ";
            	isValid = false;
            }
            
            orderReference = epcOrderHandler.isOrderBelongCust(custId, orderId);
        	if("NOT_BELONG".equals(orderReference)) {
        		errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
            	isValid = false;
        	}
            
            if(!"".equals(custId)) {
                // check with crm ?
                // ...
            } else {
                isValid = false;
                errMsg += "cust id is empty. ";
            }
    		// end of basic checking
            
    		
            if(isValid) {
	    		// get all cust profile input 
	    		aList = getTmpCustProfile(orderId, custId);
	    		// end of get all cust profile input
	    		
	    		if("Y".equalsIgnoreCase(maskedStr)) {
	    			aList.forEach(
	            		s -> {
	            			// s is HashMap<String, Object>
	            			HashMap<String, Object> tmpMap = (HashMap<String, Object>)s.get("SMCCustInfo");
	            			String encryptedStr = "";
	            			String decryptedStr = "";
	            			String mStr = "";
	            			
	            			// first name (show 1st character of each word)
	            			mStr = ""; // reset
	            			encryptedStr = StringHelper.trim((String)tmpMap.get("SMCCustFirstName"));
	            			try {
	            				decryptedStr = StringHelper.trim(EpcCrypto.dGet(encryptedStr, "utf-8"));
	            				if(decryptedStr.length() > 1) {
	            					String[] decryptedStrArray = decryptedStr.split(" ");
	            					for(String x : decryptedStrArray) {
	            						if("".equals(mStr)) {
	            							mStr = x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						} else {
	            							mStr += " " + x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						}
	            					}
	            				}
	            			} catch (Exception ee) {
	            				mStr = "XXXXX";
	            			}
	            			tmpMap.put("SMCCustFirstName", mStr);
	            			// end of first name
	            			
	            			// last name (show 1st character of each word)
	            			mStr = ""; // reset
	            			encryptedStr = StringHelper.trim((String)tmpMap.get("SMCCustLastName"));
	            			try {
	            				decryptedStr = StringHelper.trim(EpcCrypto.dGet(encryptedStr, "utf-8"));
	            				if(decryptedStr.length() > 1) {
	            					String[] decryptedStrArray = decryptedStr.split(" ");
	            					for(String x : decryptedStrArray) {
	            						if("".equals(mStr)) {
	            							mStr = x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						} else {
	            							mStr += " " + x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						}
	            					}
	            				}
	            			} catch (Exception ee) {
	            				mStr = "XXXXX";
	            			}
	            			tmpMap.put("SMCCustLastName", mStr);
	            			// end of last name
	            			
	            			// HKID (show first 4 char)
	            			mStr = ""; // reset
	            			encryptedStr = StringHelper.trim((String)tmpMap.get("SMCHKIDBR"));
	            			try {
	            				decryptedStr = StringHelper.trim(EpcCrypto.dGet(encryptedStr, "utf-8"));
	            				if(decryptedStr.length() > 1) {
	            					String[] decryptedStrArray = decryptedStr.split(" ");
	            					for(String x : decryptedStrArray) {
	            						if("".equals(mStr)) {
	            							mStr = x.substring(0, 4) + String.format("%" + (x.length() - 4) + "s", "").replace(' ', 'X');
	            						} else {
	            							mStr += " " + x.substring(0, 4) + String.format("%" + (x.length() - 4) + "s", "").replace(' ', 'X');
	            						}
	            					}
	            				}
	            			} catch (Exception ee) {
	            				mStr = "XXXXX";
	            			}
	            			tmpMap.put("SMCHKIDBR", mStr);
	            			// end of HKID
	            			
	            			// DOB (show first 4 char)
	            			mStr = ""; // reset
	            			encryptedStr = StringHelper.trim((String)tmpMap.get("SMCDOB"));
	            			try {
	            				decryptedStr = StringHelper.trim(EpcCrypto.dGet(encryptedStr, "utf-8"));
	            				if(decryptedStr.length() > 1) {
	            					String[] decryptedStrArray = decryptedStr.split(" ");
	            					for(String x : decryptedStrArray) {
	            						if("".equals(mStr)) {
	            							mStr = x.substring(0, 4) + String.format("%" + (x.length() - 4) + "s", "").replace(' ', 'X');
	            						} else {
	            							mStr += " " + x.substring(0, 4) + String.format("%" + (x.length() - 4) + "s", "").replace(' ', 'X');
	            						}
	            					}
	            				}
	            			} catch (Exception ee) {
	            				mStr = "XXXXX";
	            			}
	            			tmpMap.put("SMCDOB", mStr);
	            			// end of DOB
	            			
	            			// email
	            			mStr = ""; // reset
	            			encryptedStr = StringHelper.trim((String)tmpMap.get("SMCEmail"));
	            			try {
	            				decryptedStr = StringHelper.trim(EpcCrypto.dGet(encryptedStr, "utf-8"));
	            				if(decryptedStr.length() > 1) {
	            					String[] decryptedStrArray = decryptedStr.split("@");
	            					for(String x : decryptedStrArray) {
	            						if("".equals(mStr)) {
	            							mStr = x.substring(0, 4) + String.format("%" + (x.length() - 4) + "s", "").replace(' ', 'X');
	            						} else {
	            							mStr += "@" + x.substring(0, 4) + String.format("%" + (x.length() - 4) + "s", "").replace(' ', 'X');
	            						}
	            					}
	            				}
	            			} catch (Exception ee) {
	            				mStr = "XXXXX";
	            			}
	            			tmpMap.put("SMCEmail", mStr);
	            			// end of email
	            			
	            			// contact person
	            			mStr = ""; // reset
	            			encryptedStr = StringHelper.trim((String)tmpMap.get("SMCContactPerson"));
	            			try {
	            				decryptedStr = StringHelper.trim(EpcCrypto.dGet(encryptedStr, "utf-8"));
	            				if(decryptedStr.length() > 1) {
	            					String[] decryptedStrArray = decryptedStr.split(" ");
	            					for(String x : decryptedStrArray) {
	            						if("".equals(mStr)) {
	            							mStr = x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						} else {
	            							mStr += " " + x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						}
	            					}
	            				}
	            			} catch (Exception ee) {
	            				mStr = "XXXXX";
	            			}
	            			tmpMap.put("SMCContactPerson", mStr);
	            			// end of contact person
	            			
	            			// mnp hkid
	            			mStr = ""; // reset
	            			encryptedStr = StringHelper.trim((String)tmpMap.get("SMCMnpHKIDBR"));
	            			try {
	            				decryptedStr = StringHelper.trim(EpcCrypto.dGet(encryptedStr, "utf-8"));
	            				if(decryptedStr.length() > 1) {
	            					String[] decryptedStrArray = decryptedStr.split(" ");
	            					for(String x : decryptedStrArray) {
	            						if("".equals(mStr)) {
	            							mStr = x.substring(0, 4) + String.format("%" + (x.length() - 4) + "s", "").replace(' ', 'X');
	            						} else {
	            							mStr += " " + x.substring(0, 4) + String.format("%" + (x.length() - 4) + "s", "").replace(' ', 'X');
	            						}
	            					}
	            				}
	            			} catch (Exception ee) {
	            				mStr = "XXXXX";
	            			}
	            			tmpMap.put("SMCMnpHKIDBR", mStr);
	            			// end of mnp hkid
	            			
	            			// address 1
	            			mStr = ""; // reset
	            			encryptedStr = StringHelper.trim((String)tmpMap.get("SMCAddress1"));
	            			try {
	            				decryptedStr = StringHelper.trim(EpcCrypto.dGet(encryptedStr, "utf-8"));
	            				if(decryptedStr.length() > 1) {
	            					String[] decryptedStrArray = decryptedStr.split(" ");
	            					for(String x : decryptedStrArray) {
	            						if("".equals(mStr)) {
	            							mStr = x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						} else {
	            							mStr += " " + x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						}
	            					}
	            				}
	            			} catch (Exception ee) {
	            				mStr = "XXXXX";
	            			}
	            			tmpMap.put("SMCAddress1", mStr);
	            			// end of address 1
	            			
	            			// address 2
	            			mStr = ""; // reset
	            			encryptedStr = StringHelper.trim((String)tmpMap.get("SMCAddress2"));
	            			try {
	            				decryptedStr = StringHelper.trim(EpcCrypto.dGet(encryptedStr, "utf-8"));
	            				if(decryptedStr.length() > 1) {
	            					String[] decryptedStrArray = decryptedStr.split(" ");
	            					for(String x : decryptedStrArray) {
	            						if("".equals(mStr)) {
	            							mStr = x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						} else {
	            							mStr += " " + x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						}
	            					}
	            				}
	            			} catch (Exception ee) {
	            				mStr = "XXXXX";
	            			}
	            			tmpMap.put("SMCAddress2", mStr);
	            			// end of address 2
	            			
	            			// address 3
	            			mStr = ""; // reset
	            			encryptedStr = StringHelper.trim((String)tmpMap.get("SMCAddress3"));
	            			try {
	            				decryptedStr = StringHelper.trim(EpcCrypto.dGet(encryptedStr, "utf-8"));
	            				if(decryptedStr.length() > 1) {
	            					String[] decryptedStrArray = decryptedStr.split(" ");
	            					for(String x : decryptedStrArray) {
	            						if("".equals(mStr)) {
	            							mStr = x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						} else {
	            							mStr += " " + x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						}
	            					}
	            				}
	            			} catch (Exception ee) {
	            				mStr = "XXXXX";
	            			}
	            			tmpMap.put("SMCAddress3", mStr);
	            			// end of address 3
	            			
	            			// address 4
	            			mStr = ""; // reset
	            			encryptedStr = StringHelper.trim((String)tmpMap.get("SMCAddress4"));
	            			try {
	            				decryptedStr = StringHelper.trim(EpcCrypto.dGet(encryptedStr, "utf-8"));
	            				if(decryptedStr.length() > 1) {
	            					String[] decryptedStrArray = decryptedStr.split(" ");
	            					for(String x : decryptedStrArray) {
	            						if("".equals(mStr)) {
	            							mStr = x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						} else {
	            							mStr += " " + x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
	            						}
	            					}
	            				}
	            			} catch (Exception ee) {
	            				mStr = "XXXXX";
	            			}
	            			tmpMap.put("SMCAddress4", mStr);
	            			// end of address 4
	            		}
	            	);
	    		}
	    		
	    		epcGetCustProfileResult.setResult("SUCCESS");
	    		epcGetCustProfileResult.setCustList(aList);
            } else {
            	epcGetCustProfileResult.setResult("FAIL");
            	epcGetCustProfileResult.setErrMsg(errMsg);
            }
    	} catch(Exception e) {
    		e.printStackTrace();
    		
    		epcGetCustProfileResult.setResult("FAIL");
        	epcGetCustProfileResult.setErrMsg(e.getMessage());
    	}
    	return epcGetCustProfileResult;
    }
	
	
	public ArrayList<HashMap<String, Object>> getTmpCustProfile(String quoteGuid, String custId) {
        int smcOrderId = epcOrderHandler.getOrderIdByQuoteGuid(quoteGuid);
        ArrayList<HashMap<String, Object>> smcCasesList = getTmpCustProfile(smcOrderId, custId);
        return smcCasesList;
    }
	
	
    public ArrayList<HashMap<String, Object>> getTmpCustProfile(int smcOrderId, String custId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String tmpCaseId = "";
        String tmpStr = "";
        String cId = epcSecurityHelper.validateId(custId);
        HashMap<String, Object> tmpMap = null;
        ArrayList<HashMap<String, Object>> smcCasesList = new ArrayList<HashMap<String, Object>>();
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "select case_id, cust_info " +
                  " from epc_order_cust_profile " +
                  " where order_id = ? " +
                  "   and cust_id = ? " + 
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcOrderId); // order_id
            pstmt.setString(2, cId); // cust_id
            pstmt.setString(3, "A"); // status - active
            rset = pstmt.executeQuery();
            while(rset.next()) {
                tmpCaseId = StringHelper.trim(rset.getString("case_id"));
                tmpStr = StringHelper.trim(rset.getString("cust_info"));
                
                tmpMap = new ObjectMapper().readValue(tmpStr, HashMap.class);
                smcCasesList.add(tmpMap);
            } rset.close(); rset = null;
        } catch(Exception e) {
            e.printStackTrace();
            
            smcCasesList = null;
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return smcCasesList;
    }
    
    
    public boolean updateSubrKey(int orderId, String itemId, String subrKey) {
        boolean isUpdate = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_cust_profile " +
                  "  set subr_key = ? " +
                  " where order_id = ? " +
                  "   and item_id = ? " +
                  "   and status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, subrKey); // subr_key
            pstmt.setInt(2, orderId); // order_id
            pstmt.setString(3, itemId); // item_id
            pstmt.setString(4, "A"); // status
            pstmt.executeUpdate();
            
            conn.commit();
            
            isUpdate = true;
        } catch(Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
    
    
    /**
     * this method is invoked when Sigma OM completes an sales order
     * 
     * @param smcQuoteId - its value is equal to quote context order.CRM_Order_ID
     * @return
     */
    public ArrayList<EpcSubr> getSubrKeyListBySmcQuoteId(int smcQuoteId) {
        ArrayList<EpcSubr> subrList = new ArrayList<EpcSubr>();
        EpcSubr epcSubr = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        
        try {
            conn = epcDataSource.getConnection();
            sql = "select b.cust_num, b.subr_num, b.subr_key " +
                 "  from epc_order_cust_profile b, epc_order_quote a " +
            	 " where a.quote_id = ? " +
                 "   and b.order_id = a.order_id " +
            	 "   and b.status = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, smcQuoteId);
            pstmt.setString(2, "A"); // status
            rset = pstmt.executeQuery();
            while(rset.next()) {
                epcSubr = new EpcSubr();
                epcSubr.setSmcOrderId(smcQuoteId);
                epcSubr.setCustNum(StringHelper.trim(rset.getString("cust_num")));
                epcSubr.setSubrNum(StringHelper.trim(rset.getString("subr_num")));
                epcSubr.setSubrKey(StringHelper.trim(rset.getString("subr_key")));
                
                subrList.add(epcSubr);
            } rset.close();
        } catch (Exception e) {
            e.printStackTrace();
            
            subrList = null;
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return subrList;
    }
    
    
    public boolean isSubrExistInOrderTree(int orderId, String itemId) {
    	Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        int cnt = 0;
        boolean isExisted = false;
        
        try {
        	conn = epcDataSource.getConnection();
        	
        	sql = "select count(1) " +
          		  "  from epc_order_item " +
          		  " where order_id = ? " +
          		  "   and item_id = ? ";
        	pstmt = conn.prepareStatement(sql);
        	pstmt.setInt(1, orderId); // order_id
        	pstmt.setString(2, itemId); // item_id
        	rset = pstmt.executeQuery();
        	if(rset.next()) {
        		cnt = rset.getInt(1);
        	} rset.close(); rset = null;
        	pstmt.close(); pstmt = null;
        	
        	if(cnt > 0) {
        		isExisted = true;
        	}
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isExisted;
    }
	
}
