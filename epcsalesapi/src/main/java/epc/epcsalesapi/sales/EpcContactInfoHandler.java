package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcOrderContact;
import epc.epcsalesapi.sales.bean.EpcUpdateOrderContact;

@Service
public class EpcContactInfoHandler {
	
    private final Logger logger = LoggerFactory.getLogger(EpcContactInfoHandler.class);

    @Autowired
    private DataSource epcDataSource;

    @Autowired
    private EpcOrderHandler epcOrderHandler;

    @Autowired
    private EpcSecurityHelper epcSecurityHelper;
    
    @Autowired
    private EpcSalesmanHandler epcSalesmanHandler;


	public EpcOrderContact getContactInfo(String custId, int orderId, String masked) {
    	EpcOrderContact epcOrderContact = new EpcOrderContact();
    	Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        String iCustId = epcSecurityHelper.encodeForSQL(custId);
        String maskedStr = epcSecurityHelper.encodeForSQL(StringHelper.trim(masked));
        if("".equals(maskedStr)) {
//            maskedStr = "Y"; // default is masked
            maskedStr = "N"; // default is encrypted, requested by calvin / louis
        }
        String tmpStr = "";
        String decryptedStr = "";
        
        try {
            conn = epcDataSource.getConnection();
            
            epcOrderContact.setOrderId(orderId);
            
            sql = "select contact_email, contact_no, order_lang, contact_person_first_name, contact_person_last_name, " +
                  "       contact_person_title, is_existing_cust " +
                  "  from epc_order " +
                  " where order_id = ? " +
                  "   and cust_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, iCustId); // cust_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
            	epcOrderContact.setCustId(custId);
            	epcOrderContact.setOrderId(orderId);
            	epcOrderContact.setContactEmail(StringHelper.trim(rset.getString("contact_email")));
            	epcOrderContact.setContactNo(StringHelper.trim(rset.getString("contact_no")));
            	epcOrderContact.setOrderLang(StringHelper.trim(rset.getString("order_lang")));
            	epcOrderContact.setContactPersonFirstName(StringHelper.trim(rset.getString("contact_person_first_name")));
            	epcOrderContact.setContactPersonLastName(StringHelper.trim(rset.getString("contact_person_last_name")));
            	epcOrderContact.setContactPersonTitle(StringHelper.trim(rset.getString("contact_person_title")));
            	epcOrderContact.setIsExistingCust(StringHelper.trim(rset.getString("is_existing_cust")));
            } rset.close();
            pstmt.close();


            // masked data
            if("Y".equals(maskedStr)) {
                // contact email
                tmpStr = null; // reset
                try {
                    decryptedStr = StringHelper.trim(EpcCrypto.dGet(epcOrderContact.getContactEmail(), "utf-8"));
                    if(decryptedStr.length() > 1) {
                        tmpStr = ""; // assume with data, not return null
                        String[] decryptedStrArray = decryptedStr.split(" ");
                        for(String x : decryptedStrArray) {
                            if("".equals(tmpStr)) {
                                tmpStr = x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
                            } else {
                                tmpStr += " " + x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
                            }
                        }
                    } else {
                        tmpStr = decryptedStr;
                    }
                } catch(Exception e) {
                    tmpStr = null;
                }

                epcOrderContact.setContactEmail(tmpStr);
                // end of contact email

                // contact person first name
                tmpStr = null; // reset
                try {
                    decryptedStr = StringHelper.trim(EpcCrypto.dGet(epcOrderContact.getContactPersonFirstName(), "utf-8"));
                    if(decryptedStr.length() > 1) {
                        tmpStr = ""; // assume with data, not return null
                        String[] decryptedStrArray = decryptedStr.split(" ");
                        for(String x : decryptedStrArray) {
                            if("".equals(tmpStr)) {
                                tmpStr = x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
                            } else {
                                tmpStr += " " + x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
                            }
                        }
                    } else {
                        tmpStr = decryptedStr;
                    }
                } catch(Exception e) {
                    tmpStr = null;
                }

                epcOrderContact.setContactPersonFirstName(tmpStr);
                // end of contact person first name

                // contact person last name
                tmpStr = null; // reset
                try {
                    decryptedStr = StringHelper.trim(EpcCrypto.dGet(epcOrderContact.getContactPersonLastName(), "utf-8"));
                    if(decryptedStr.length() > 1) {
                        tmpStr = ""; // assume with data, not return null
                        String[] decryptedStrArray = decryptedStr.split(" ");
                        for(String x : decryptedStrArray) {
                            if("".equals(tmpStr)) {
                                tmpStr = x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
                            } else {
                                tmpStr += " " + x.substring(0, 1) + String.format("%" + (x.length() - 1) + "s", "").replace(' ', 'X');
                            }
                        }
                    } else {
                        tmpStr = decryptedStr;
                    }
                } catch(Exception e) {
                    tmpStr = null;
                }

                epcOrderContact.setContactPersonLastName(tmpStr);
                // end of contact person last name
            }
            // end of masked data
            
            epcOrderContact.setResult("SUCCESS");
        } catch(Exception e) {
            e.printStackTrace();

            epcOrderContact.setResult("FAIL");
            epcOrderContact.setErrMsg(e.getMessage());
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcOrderContact;
    }
	
	
	public EpcOrderContact saveContactInfo(EpcOrderContact epcOrderContact) {
    	Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        String orderReference = "";
        String custId = "";
        int orderId = 0;
        String contactEmail = "";
        String contactEmailUpper = "";
        String contactNo = "";
        String orderLang = "";
        String contactPersonFirstName = "";
        String contactPersonLastName = "";
        String contactPersonTitle = "";
        String isExistingCust = "";
        String decryptStr = "";
        
        boolean isValid = true;
        String errMsg = "";
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            
            custId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderContact.getCustId()));
            orderId = epcOrderContact.getOrderId();
            contactEmail = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderContact.getContactEmail()));
            contactNo = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderContact.getContactNo()));
            orderLang = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderContact.getOrderLang()));
            contactPersonFirstName = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderContact.getContactPersonFirstName()));
            contactPersonLastName = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderContact.getContactPersonLastName()));
            contactPersonTitle = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderContact.getContactPersonTitle()));
            isExistingCust = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcOrderContact.getIsExistingCust()));

            
            // basic checking
            orderReference = epcOrderHandler.isOrderBelongCust(conn, custId, orderId);
        	if("NOT_BELONG".equals(orderReference)) {
        		errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
            	isValid = false;
        	}

            if(!"Y".equals(isExistingCust) && !"N".equals(isExistingCust) && !"X".equals(isExistingCust)) {
                isValid = false;
                errMsg += "existing cust flag is invalid. ";
            }

            if(!"E".equals(orderLang) && !"C".equals(orderLang)) {
                isValid = false;
                errMsg += "order lang is empty. ";
            }
        	
            if(!"X".equals(isExistingCust)) {
                if(!"".equals(contactEmail)) {
                    // ecrypted contactemail is not empty
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(contactEmail, "utf-8"));

                        if("".equals(decryptStr) && "".equals(contactNo)) {
                            isValid = false;
                            errMsg += "contact email & contact no are empty. ";
                        } else {
                            contactEmailUpper = EpcCrypto.eGet(decryptStr.toUpperCase(), "utf-8");
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "contact email can't be decrypted. ";
                    }
                } else {
                    // ecrypted contactemail is empty
                    if("".equals(contactNo)) {
                        isValid = false;
                        errMsg += "contact email & contact no are empty. ";
                    }
                }
                
                if(!"".equals(contactPersonFirstName)) {
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(contactPersonFirstName, "utf-8"));

                        if("".equals(decryptStr)) {
                            isValid = false;
                            errMsg += "contact person first name is empty. ";
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "contact person first name can't be decrypted. ";
                    }
                } 
    //            else {
    //                isValid = false;
    //                errMsg += "contact person first name is empty. ";
    //            }
                
                if(!"".equals(contactPersonLastName)) {
                    try {
                        decryptStr = StringHelper.trim(EpcCrypto.dGet(contactPersonLastName, "utf-8"));

                        if("".equals(decryptStr)) {
                            isValid = false;
                            errMsg += "contact person last name is empty. ";
                        }
                    } catch (Exception e) {
                        isValid = false;
                        errMsg += "contact person last name can't be decrypted. ";
                    }
                } else {
                    isValid = false;
                    errMsg += "contact person last name is empty. ";
                }
            }
            // end of basic checking
        	
            
            if(isValid) {
	            sql = "update epc_order " +
	                  "   set contact_email = ?, " +
	            	  "       contact_email_upper = ?, " +
	            	  "       contact_no = ?, " +
	            	  "       order_lang = ?, " +
	            	  "       contact_person_first_name = ?, " +
	            	  "       contact_person_last_name = ?, " +
	            	  "       contact_person_title = ?, " +
	            	  "       is_existing_cust = ? " +
	                  " where order_id = ? " + 
	                  "   and cust_id = ? ";
	            pstmt = conn.prepareStatement(sql);
	            
	            pstmt.setString(1, contactEmail); // contact_email
	            pstmt.setString(2, contactEmailUpper); // contact_email_upper
	            pstmt.setString(3, contactNo); // contact_no
	            pstmt.setString(4, orderLang); // order_lang
	            pstmt.setString(5, contactPersonFirstName); // contact_person_first_name
	            pstmt.setString(6, contactPersonLastName); // contact_person_last_name
	            pstmt.setString(7, contactPersonTitle); // contact_person_title
	            pstmt.setString(8, isExistingCust); // is_existing_cust
            	pstmt.setInt(9, orderId); // order_id
	            pstmt.setString(10, custId); // cust_id
	            pstmt.executeUpdate();
                pstmt.close();
	            
	            conn.commit();
	            
	            epcOrderContact.setResult("SUCCESS");
            } else {
            	// error
            	epcOrderContact.setResult("FAIL");
            	epcOrderContact.setErrMsg(errMsg);
            }
        } catch(Exception e) {
            e.printStackTrace();
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
            
            epcOrderContact.setResult("FAIL");
            epcOrderContact.setErrMsg(e.getMessage());
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return epcOrderContact;
    }
	
	public EpcUpdateOrderContact updateContact(EpcUpdateOrderContact orderContact) {
		Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmtGetOld = null;
        String sql = "";       
        String orderChannel = "";
        String orderUser = "";
        String orderSalesman = "";
        String orderLocation = "";
        int orderId = 0;
        String contactNo = "";
        String contactEmail = "";
        String oldContactNo = "";
        String oldContactEmail = "";
        String contactEmailUpper = "";
        String decryptStr = "";
        String decryptStrOldEmail = "";
        String errMsg = "";
        String remarks = "";
        boolean isValid = true;
        ResultSet rset = null;

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            // get input param            
            orderChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(orderContact.getCreateChannel()));
            orderUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(orderContact.getCreateUser()));
            orderSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(orderContact.getCreateSalesman()));
            orderLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(orderContact.getCreateLocation()));
            orderId = orderContact.getOrderId();
            contactEmail = epcSecurityHelper.encodeForSQL(StringHelper.trim(orderContact.getContactEmail()));
            contactNo = epcSecurityHelper.encodeForSQL(StringHelper.trim(orderContact.getContactNo()));
            // end of get input param

            // basic checking            
            if(orderId == 0) {
                errMsg += "input order id [" + orderId + "] is not valid. ";
                isValid = false;
            }
            if(!"".equals(contactEmail)) {
                // ecrypted contactemail is not empty
                try {
                    decryptStr = StringHelper.trim(EpcCrypto.dGet(contactEmail, "utf-8"));

                    if("".equals(decryptStr) && "".equals(contactNo)) {
                        isValid = false;
                        errMsg += "contact email & contact no are empty. ";
                    } else {
                        contactEmailUpper = EpcCrypto.eGet(decryptStr.toUpperCase(), "utf-8");
                    }
                } catch (Exception e) {
                    isValid = false;
                    errMsg += "contact email can't be decrypted. ";
                }
            } else {
                // ecrypted contactemail is empty
                if("".equals(contactNo)) {
                    isValid = false;
                    errMsg += "contact email & contact no are empty. ";
                }
            }
            // end of basic checking
            
            // get old contact for log
        	sql = "select contact_no, contact_email from epc_order where order_id = ? ";
            pstmtGetOld = conn.prepareStatement(sql);
            pstmtGetOld.setInt(1, orderId); // order_id
            rset = pstmtGetOld.executeQuery();
            if(rset.next()) {
                oldContactNo = StringHelper.trim(rset.getString("contact_no"));
                oldContactEmail = StringHelper.trim(rset.getString("contact_email"));
            } rset.close();
            pstmtGetOld.close();
            
            try {
            	if (!"".equals(oldContactEmail)) {
            		decryptStrOldEmail = StringHelper.trim(EpcCrypto.dGet(oldContactEmail, "utf-8"));
            	}
            } catch (Exception e) {
                isValid = false;
                errMsg += "old contact email can't be decrypted. ";
            }
            // get old contact for log end

            if(isValid) {
                sql = "update epc_order " +
                      " set contact_no = ?, contact_email = ?, contact_email_upper = ? " +
                      " where order_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, contactNo);
                pstmt.setString(2, contactEmail);
                pstmt.setString(3, contactEmailUpper);
                pstmt.setInt(4, orderId);
                pstmt.executeUpdate();
                pstmt.close();

                // create salesman log
                remarks = "Change contact info, old contact no: "+ oldContactNo +", new contact no: " + contactNo 
                		+ ", old contact email:"+ decryptStrOldEmail +", new contact email: " + decryptStr + " ";
                epcSalesmanHandler.createSalesmanLog(conn, orderId, "", orderUser, orderSalesman, orderLocation, orderChannel, epcSalesmanHandler.actionModifyContactInfo, remarks);
                // end of create salesman log

                conn.commit();

                orderContact.setResult("SUCCESS");
            } else {
            	orderContact.setResult("FAIL");
            	orderContact.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            e.printStackTrace();

            orderContact.setResult("FAIL");
            orderContact.setErrMsg(e.getMessage());
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return orderContact;
	}
	
}
