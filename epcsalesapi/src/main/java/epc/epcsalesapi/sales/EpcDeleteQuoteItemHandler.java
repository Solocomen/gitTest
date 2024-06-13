package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.preorder.EpcPreorderHandler;
import epc.epcsalesapi.preorder.bean.EpcPreorder;
import epc.epcsalesapi.sales.bean.EpcDeleteQuoteItemResult;
import epc.epcsalesapi.sales.bean.EpcDeleteSigmaItemFromQuote;
import epc.epcsalesapi.sales.bean.EpcItemCategory;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.stock.EpcStockHandler;

@Service
public class EpcDeleteQuoteItemHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcDeleteQuoteItemHandler.class);

    private DataSource epcDataSource;
    private EpcOrderHandler epcOrderHandler;
    private EpcQuoteHandler epcQuoteHandler;
    private EpcStockHandler epcStockHandler;
    private EpcOrderAttrHandler epcOrderAttrHandler;
    private EpcVoucherHandlerNew epcVoucherHandlerNew;
    private EpcSalesmanHandler epcSalesmanHandler;
    private EpcPreorderHandler epcPreorderHandler;


    public EpcDeleteQuoteItemHandler(DataSource epcDataSource, EpcOrderHandler epcOrderHandler,
            EpcQuoteHandler epcQuoteHandler, EpcStockHandler epcStockHandler, EpcOrderAttrHandler epcOrderAttrHandler,
            EpcVoucherHandlerNew epcVoucherHandlerNew, EpcSalesmanHandler epcSalesmanHandler,
            EpcPreorderHandler epcPreorderHandler) {
        this.epcDataSource = epcDataSource;
        this.epcOrderHandler = epcOrderHandler;
        this.epcQuoteHandler = epcQuoteHandler;
        this.epcStockHandler = epcStockHandler;
        this.epcOrderAttrHandler = epcOrderAttrHandler;
        this.epcVoucherHandlerNew = epcVoucherHandlerNew;
        this.epcSalesmanHandler = epcSalesmanHandler;
        this.epcPreorderHandler = epcPreorderHandler;
    }


    public void deleteSigmaItemFromQuote(EpcDeleteSigmaItemFromQuote epcDeleteSigmaItemFromQuote) {
        Connection conn = null;

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            deleteSigmaItemFromQuote(conn, epcDeleteSigmaItemFromQuote);
            if("SUCCESS".equals(epcDeleteSigmaItemFromQuote.getResult())) {
                conn.commit();
            } else {
                conn.rollback();
            }
        } catch (Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            e.printStackTrace();

            epcDeleteSigmaItemFromQuote.setResult("FAIL");
            epcDeleteSigmaItemFromQuote.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }


    public void deleteSigmaItemFromQuote(Connection conn, EpcDeleteSigmaItemFromQuote epcDeleteSigmaItemFromQuote) {
        String orderReference = "xxx";
        String custId = "";
        int orderId = 0;
        int quoteId = 0;
        String sigmaItemId = "";
        String deleteChannel = "";
        String deleteUser = "";
        String deleteSalesman = "";
        String deleteLocation = "";
        String quoteGuid = "";
        String errMsg = "";
        boolean isValid = true;
        EpcQuote epcQuote = null;
        EpcDeleteQuoteItemResult epcDeleteQuoteItemResult = null;
        String remarks = "";
        String caseId = "";
        boolean isDelete = false;
    	
        try {
            // get input param
            custId = StringHelper.trim(epcDeleteSigmaItemFromQuote.getCustId());
            deleteChannel = StringHelper.trim(epcDeleteSigmaItemFromQuote.getDeleteChannel());
            deleteUser = StringHelper.trim(epcDeleteSigmaItemFromQuote.getDeleteUser());
            deleteSalesman = StringHelper.trim(epcDeleteSigmaItemFromQuote.getDeleteSalesman());
            deleteLocation = StringHelper.trim(epcDeleteSigmaItemFromQuote.getDeleteLocation());
            orderId = epcDeleteSigmaItemFromQuote.getOrderId();
            quoteId = epcDeleteSigmaItemFromQuote.getQuoteId();
            sigmaItemId = StringHelper.trim(epcDeleteSigmaItemFromQuote.getSigmaItemId());
            // end of get input param
            
            
            // basic checking
            if("".equals(custId)) {
            	errMsg += "input cust id is empty. ";
            	isValid = false;
            }
            if(!"".equals(deleteUser)) {
                // check with FES ?
            	//  ...
            }
            if(!"".equals(deleteSalesman)) {
                // check with FES ?
            	//  ...
            }
            
            if(orderId == 0) {
            	errMsg += "input order id [" + orderId + "] is not valid. ";
            	isValid = false;
            } else {
            	// check whether this order id is belonged to input cust id (existingOrderId vs custId)
            	orderReference = epcOrderHandler.isOrderBelongCust(conn, custId, orderId);
            	if("NOT_BELONG".equals(orderReference)) {
            		errMsg += "input order id [" + orderId + "] is not belonged to input cust id [" + custId + "]. ";
                	isValid = false;
            	}

                if(epcOrderHandler.isOrderLocked(conn, custId, orderId)) {
                    errMsg += "input order [" + orderId + "] is locked. ";
                    isValid = false;
                }
            }
            
            if("".equals(sigmaItemId)) {
            	errMsg += "input sigma item id is empty. ";
            	isValid = false;
            }
            
            if(quoteId == 0) {
            	errMsg += "input quote id [" + quoteId + "] is not valid. ";
            	isValid = false;
            } else {
            	quoteGuid = epcOrderHandler.getCurrentQuoteGuid(conn, orderId, quoteId);
            	if("".equals(quoteGuid)) {
            		errMsg += "input order/quote id [" + orderId + "/" + quoteId + "] is not valid. ";
                	isValid = false;
            	} else {
                    caseId = epcOrderHandler.getCaseIdByQuoteItemGuid(conn, orderId, quoteId, sigmaItemId);
            		
            		if("".equals(caseId)) {
            			errMsg += "input sigma item id [" + sigmaItemId + "] is not found in epc table. ";
                    	isValid = false;
            		}
            	}
            }
            // end of basic checking
            
            
            if(isValid) {
                epcQuote = new EpcQuote();
                epcQuote.setId(quoteGuid);
                epcQuote.setUpdated("2018-12-04 07:11:59.411Z");

            	epcDeleteQuoteItemResult = epcQuoteHandler.deleteQuoteItem(epcQuote, sigmaItemId);
                if("SUCCESS".equals(epcDeleteQuoteItemResult.getResult())) {
                    // release tmp ticket
                    epcStockHandler.removeTmpTicketUnderQuoteItem(conn, orderId, quoteId, caseId);
                    // end of release tmp ticket

                    // resume preorder records if any
                    resumePreorderRecords(conn, orderId, quoteId, caseId, deleteUser, deleteSalesman, deleteChannel, deleteLocation);
                    // end of resume preorder records if any

                	// delete epc case, item table
                	isDelete = epcOrderHandler.deleteCaseItemRecord(conn, orderId, quoteId, caseId);
                	// end of delete epc case, item table

                	// delete attr under the case
                	epcOrderAttrHandler.obsoleteAttrByCaseId(conn, orderId, caseId);
                	// end of delete attr under the case

                    // delete voucher 
                    epcVoucherHandlerNew.removeAssignVoucher(conn, orderId, caseId);
                    // end of delete voucher 
                    
                    // create salesman log
                	remarks = "delete sigma item [" + sigmaItemId + "] from order";
                    epcSalesmanHandler.createSalesmanLog(conn, orderId, caseId, deleteUser, deleteSalesman, deleteLocation, deleteChannel, epcSalesmanHandler.actionDeleteQuoteItem, remarks);
                    // end of create salesman log

                    epcDeleteSigmaItemFromQuote.setResult("SUCCESS");
                } else {
                	epcDeleteSigmaItemFromQuote.setResult("FAIL");
                	epcDeleteSigmaItemFromQuote.setErrMsg(epcDeleteQuoteItemResult.getErrMsg());
                }
            } else {
            	epcDeleteSigmaItemFromQuote.setResult("FAIL");
            	epcDeleteSigmaItemFromQuote.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            e.printStackTrace();

            epcDeleteSigmaItemFromQuote.setResult("FAIL");
            epcDeleteSigmaItemFromQuote.setErrMsg(e.getMessage());
        } finally {
        }
    }


    public void resumePreorderRecords(Connection conn, int orderId, int quoteId, String caseId, String deleteUser, String deleteSalesman, String deleteChannel, String deleteLocation) {
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String itemId = "";
        String preorderCaseIdStr = "";
        EpcPreorder epcPreorder = null;
        String logStr = "[resumePreorderRecords][orderId:" + orderId + "][quoteId:" + quoteId + "][caseId:" + caseId + "] ";
        String tmpLogStr = "";

        try {
            sql = "select item_id " +
                  "  from epc_order_item " + 
                  " where order_id = ? " +
                  "   and quote_id = ? " +
                  "   and case_id = ? " +
                  "   and item_cat in (?) ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            pstmt.setInt(2, quoteId); // quote_id
            pstmt.setString(3, caseId); // case_id
            pstmt.setString(4, EpcItemCategory.DEVICE); // item_cat - DEVICE
            rset = pstmt.executeQuery();
            while(rset.next()) {
                itemId = StringHelper.trim(rset.getString("item_id"));
                preorderCaseIdStr = epcOrderAttrHandler.getAttrValue(orderId, caseId, itemId, epcOrderAttrHandler.ATTR_TYPE_PREORDER_CASE_ID); // preorder case id

                if(!"".equals(preorderCaseIdStr)) {
                    // proceed to resume
                    tmpLogStr = "itemId:" + itemId + ",preorderCaseId:" + preorderCaseIdStr + " resume preorder record";
logger.info("{}{}", logStr, tmpLogStr);

                    epcPreorder = new EpcPreorder();
                    epcPreorder.setOrderId(orderId);
                    epcPreorder.setCaseId(caseId);
                    epcPreorder.setItemId(itemId);
                    epcPreorder.setInvoiceNo("");
                    epcPreorder.setCreateUser(deleteUser);
                    epcPreorder.setCreateSalesman(deleteSalesman);
                    epcPreorder.setCreateChannel(deleteChannel);
                    epcPreorder.setCreateLocation(deleteLocation);

                    epcPreorderHandler.resumePreorderRecord(epcPreorder);
                    tmpLogStr = "result:" + epcPreorder.getResult() + ",errMsg:" + epcPreorder.getErrMsg();
logger.info("{}{}", logStr, tmpLogStr);
                }
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
