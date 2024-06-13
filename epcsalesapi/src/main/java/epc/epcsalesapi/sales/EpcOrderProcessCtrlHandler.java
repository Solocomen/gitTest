/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.EpcOrderQuoteInfo;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcSmcQuote;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author KerryTsang
 */

@Service
public class EpcOrderProcessCtrlHandler {
	
	@Autowired
	private DataSource epcDataSource;
	
//	@Autowired
//	private EpcOrderHandler epcOrderHandler;
	
	@Autowired
	private EpcQuoteHandler epcQuoteHandler;
	
	@Autowired
	private EpcCustProfileHandler epcCustProfileHandler;
	
	@Autowired
	private EpcSecurityHelper epcSecurityHelper;
    
    public final String processCreateEPCRecord = "CREATE_EPC_RECORD";
    public final String processCreateBillingAccountWithGup = "CREATE_BILLING_ACCOUNT_WITH_GUP";
    public final String processConfirmNewMobile = "CONFIRM_NEW_MOBILE";
    public final String processCreateMnp = "CREATE_MNP";
    public final String processSubmitQuoteToOrder = "SUBMIT_QUOTE_TO_ORDER";
    public final String processCreateEPCInvoice = "CREATE_EPC_INVOICE";
    public final String processCreateEPCReceipt = "CREATE_EPC_RECEIPT";
    public final String processUpdateEPCRecord = "UPDATE_EPC_RECORD";
    public final String processGenerateDummySim = "GENERATE_DUMMY_SIM";

    public final String processStatusInitial = "INITIAL";
    public final String processStatusDone = "DONE";
    public final String processStatusFail = "FAIL";


    public boolean createProcessCtrl(EpcOrderInfo epcOrderInfo) {
    	Connection conn = null;
    	boolean isCreate = true;
        int orderId = epcOrderInfo.getOrderId();
    	
    	try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            for (EpcOrderQuoteInfo e : epcOrderInfo.getEpcOrderQuoteInfoList()) {
            	isCreate = createProcessCtrl(conn, orderId, e.getQuoteId(), e.getEpcQuote());
            	if(!isCreate) {
            		throw new Exception("cannot create process ctrl for quote " + e.getQuoteGuid());
            	}
            }
            
            // create receipt ctrl
            createProcess(conn, orderId, 0, "", "", "", processCreateEPCReceipt);

            // update epc record ctrl
            createProcess(conn, orderId, 0, "", "", "", processUpdateEPCRecord);
            
            
            conn.commit();
    	} catch (Exception e) {
            e.printStackTrace();
            
            isCreate = false;
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    	
    	return isCreate;
    }
    
    
    public boolean createProcessCtrl(int orderId, ArrayList<EpcSmcQuote> quoteList) {
    	Connection conn = null;
    	boolean isCreate = true;
    	
    	try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            for (EpcSmcQuote e : quoteList) {
            	isCreate = createProcessCtrl(conn, orderId, e.getQuoteId(), e.getEpcQuote());
            	if(!isCreate) {
            		throw new Exception("cannot create process ctrl for quote " + e.getQuoteGuid());
            	}
            }
            
            // create receipt ctrl
            createProcess(conn, orderId, 0, "", "", "", processCreateEPCReceipt);

            // update epc record ctrl
            createProcess(conn, orderId, 0, "", "", "", processUpdateEPCRecord);
            
            
            conn.commit();
    	} catch (Exception e) {
            e.printStackTrace();
            
            isCreate = false;
            
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    	
    	return isCreate;
    }

    
    public boolean createProcessCtrl(Connection conn, int orderId, int quoteId, EpcQuote epcQuote) {
        boolean isCreate = false;
        String quoteGuid = "";
        ArrayList<HashMap<String, Object>> objList = null;
        HashMap<String, Object> objSMCCase = null;
        HashMap<String, Object> objSMCCustInfo = null;
        String smcCaseId = "";
        String smcItemId = "";
        String activationType = "";
        int smcOrderId = orderId;
        String custId = epcQuote.getCustomerRef();
        boolean isExist = false;
        
        try {
            quoteGuid = epcQuote.getId();
            
            
            // check duplication 
            isExist = isProcessExist(conn, quoteId);
            
            if(isExist) {
                isCreate = true;
            } else {
                // create epc record ctrl
                createProcess(conn, orderId, quoteId, quoteGuid, "", "", processCreateEPCRecord);

                // loop thru context data
//                objList = (ArrayList<HashMap<String, Object>>)contextData.get("SMCCases");
                objList = epcCustProfileHandler.getTmpCustProfile(smcOrderId, custId); // get cust info from epc table instead of quote context, kerrytsang, 20200908
                if(objList == null) {
                    // no customer info, no need to perform further action
                } else {
                    for (int i = 0; i < objList.size(); i++) {
                        objSMCCase = (HashMap<String, Object>)objList.get(i);
                        smcCaseId = StringHelper.trim((String)objSMCCase.get("SMCCaseId"));
                        smcItemId = StringHelper.trim((String)objSMCCase.get("SMCItemId"));
                        
                        if(!epcQuoteHandler.containsCase(epcQuote, smcCaseId)) {
                            // if record is existed in cust table (have not removed) but not in quote items
                            continue;
                        }
                        

                        objSMCCustInfo = (HashMap<String, Object>)objSMCCase.get("SMCCustInfo");
                        activationType = StringHelper.trim((String)objSMCCustInfo.get("SMCActivationType"));

                        if(EpcActivationTypeHandler.newActivate.equals(activationType)) {
                            createProcess(conn, orderId, quoteId, quoteGuid, smcCaseId, smcItemId, processGenerateDummySim);
                            createProcess(conn, orderId, quoteId, quoteGuid, smcCaseId, smcItemId, processCreateBillingAccountWithGup);
                            createProcess(conn, orderId, quoteId, quoteGuid, smcCaseId, smcItemId, processConfirmNewMobile);
                        } else if(EpcActivationTypeHandler.mnp.equals(activationType)) {
                            createProcess(conn, orderId, quoteId, quoteGuid, smcCaseId, smcItemId, processGenerateDummySim);
                            createProcess(conn, orderId, quoteId, quoteGuid, smcCaseId, smcItemId, processCreateBillingAccountWithGup);
                            createProcess(conn, orderId, quoteId, quoteGuid, smcCaseId, smcItemId, processCreateMnp);
                        } else if(EpcActivationTypeHandler.mvno.equals(activationType)) {
                            // ...
                        } else if(EpcActivationTypeHandler.inp.equals(activationType)) {
                            // ...
                        }
                    }
                }
                // end of loop thru context data

                // convert & submit quote ctrl
                createProcess(conn, orderId, quoteId, quoteGuid, "", "", processSubmitQuoteToOrder);

//                conn.commit();
                
                isCreate = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
//            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
//            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
//            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isCreate;
    }
    
    
    public boolean isProcessExist(Connection conn, int quoteId) {
        boolean isExist = false;
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        int cnt = 0;
        
        try {
            sql = "select count(1) from epc_order_process_ctrl where quote_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, quoteId); // quote_id
            rset = pstmt.executeQuery();
            if(rset.next()) {
                cnt = rset.getInt(1);
            } rset.close(); rset = null;
            
            if(cnt > 0) {
                isExist = true;
            } else {
                isExist = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return isExist;
    }

    
    public boolean createProcess(Connection conn, int orderId, int quoteId, String quoteGuid, String caseId, String itemId, String processName) {
        boolean isCreate = false;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            sql = "insert into epc_order_process_ctrl ( " + 
                    "  rec_id, order_id, quote_guid, case_id, process_name, process_status, " +
                    "  create_date, modify_date, remarks, quote_id, item_id " + 
                    ") values ( " +
                    "  epc_order_id_seq.nextval,?,?,?,?,?, " +
                    "  sysdate,null,null,?,? " +
                    ") ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id 
            pstmt.setString(2, quoteGuid); // quote_guid
            pstmt.setString(3, caseId); // case_id
            pstmt.setString(4, processName); // process_name
            pstmt.setString(5, processStatusInitial); // process_status
            if(quoteId != 0) {
            	pstmt.setInt(6, quoteId); // quote_id
            } else {
            	pstmt.setNull(6, Types.INTEGER);
            }
            pstmt.setString(7, itemId); // item_id
            pstmt.executeUpdate();
            
            isCreate = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
        return isCreate;
    }
    
    
    public boolean updateProcess(String quoteGuid, String caseId, String processName, String processStatus, String remarks) {
        boolean isUpdate = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_process_ctrl " + 
                    "  set process_status = ?, " +
                    "     remarks = substr(?,1,300), " +
                    "     modify_date = sysdate " +
                    "  where quote_guid = ? " +
                    "    and process_name = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, processStatus); // process_status
            pstmt.setString(2, remarks); // remarks
            pstmt.setString(3, quoteGuid); // quote_guid
            pstmt.setString(4, processName); // process_name
            pstmt.executeUpdate();
            
            isUpdate = true;
            
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
    
    
    public boolean updateProcess(int orderId, String processName, String processStatus, String remarks) {
        boolean isUpdate = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_process_ctrl " + 
                    "  set process_status = ?, " +
                    "     remarks = substr(?,1,300), " +
                    "     modify_date = sysdate " +
                    "  where order_id = ? " +
                    "    and process_name = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, processStatus); // process_status
            pstmt.setString(2, epcSecurityHelper.validateString(remarks)); // remarks
            pstmt.setInt(3, orderId); // order_id
            pstmt.setString(4, processName); // process_name
            pstmt.executeUpdate();
            
            isUpdate = true;
            
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
    
    
    public boolean updateProcess(int orderId, String caseId, String itemId, String processName, String processStatus, String remarks) {
        boolean isUpdate = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_process_ctrl " + 
                    "  set process_status = ?, " +
                    "     remarks = substr(?,1,300), " +
                    "     modify_date = sysdate " +
                    "  where order_id = ? " +
                    "    and case_id = ? " +
                    "    and item_id = ? " +
                    "    and process_name = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, processStatus); // process_status
            pstmt.setString(2, remarks); // remarks
            pstmt.setInt(3, orderId); // order_id
            pstmt.setString(4, caseId); // case_id
            pstmt.setString(5, itemId); // item_id
            pstmt.setString(6, processName); // process_name
            pstmt.executeUpdate();
            
            isUpdate = true;
            
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
    
    
    public boolean updateProcess(int orderId, int quoteId, String processName, String processStatus, String remarks) {
        boolean isUpdate = false;
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        
        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            sql = "update epc_order_process_ctrl " + 
                    "  set process_status = ?, " +
                    "     remarks = substr(?,1,300), " +
                    "     modify_date = sysdate " +
                    "  where order_id = ? " +
                    "    and quote_id = ? " +
                    "    and process_name = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, processStatus); // process_status
            pstmt.setString(2, remarks); // remarks
            pstmt.setInt(3, orderId); // order_id
            pstmt.setInt(4, quoteId); // quote_id
            pstmt.setString(5, processName); // process_name
            pstmt.executeUpdate();
            
            isUpdate = true;
            
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return isUpdate;
    }
    
    
    public String getProcessStatus(String quoteGuid, String caseId, String processName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        String status = "";
        
        try {
            conn = epcDataSource.getConnection();
            
            if("".equals(caseId)) {
                sql = "select process_status " +
                        "  from epc_order_process_ctrl " + 
                        " where quote_guid = ? " +
                        "   and process_name = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, quoteGuid); // quote_guid
                pstmt.setString(2, processName); // process_name
            } else {
                sql = "select process_status " +
                        "  from epc_order_process_ctrl " + 
                        " where quote_guid = ? " +
                        "   and case_id = ? " +
                        "   and process_name = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, quoteGuid); // quote_guid
                pstmt.setString(2, caseId); // case_id
                pstmt.setString(3, processName); // process_name
            }
            rset = pstmt.executeQuery();
            if(rset.next()) {
                status = StringHelper.trim(rset.getString("process_status"));
            } else {
                status = "NOT_EXIST"; // case should not be proceeded
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return status;
    }
    
    
    public String getProcessStatus(int orderId, String caseId, String itemId, String processName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        String status = "";
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "select process_status " +
                    "  from epc_order_process_ctrl " + 
                    " where order_id = ? " +
                    "   and case_id = ? " +
                    "   and item_id = ? " +
                    "   and process_name = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, caseId); // case_id
            pstmt.setString(3, itemId); // item_id
            pstmt.setString(4, processName); // process_name
            rset = pstmt.executeQuery();
            if(rset.next()) {
                status = StringHelper.trim(rset.getString("process_status"));
            } else {
                status = "NOT_EXIST"; // case should not be proceeded
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return status;
    }
    
    
    public String getProcessStatus(int orderId, int quoteId, String processName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        ResultSet rset = null;
        String status = "";
        
        try {
            conn = epcDataSource.getConnection();
            
            sql = "select process_status " +
                    "  from epc_order_process_ctrl " + 
                    " where order_id = ? " +
                    "   and quote_id = ? " +
                    "   and process_name = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setInt(2, quoteId); // quote_id
            pstmt.setString(3, processName); // process_name
            rset = pstmt.executeQuery();
            if(rset.next()) {
                status = StringHelper.trim(rset.getString("process_status"));
            } else {
                status = "NOT_EXIST"; // case should not be proceeded
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
        return status;
    }
}
