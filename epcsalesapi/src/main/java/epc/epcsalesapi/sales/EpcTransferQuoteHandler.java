package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcTransferOrder;

@Service
public class EpcTransferQuoteHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcTransferQuoteHandler.class);

    private DataSource epcDataSource;
    private EpcSecurityHelper epcSecurityHelper;
    private EpcOrderHandler epcOrderHandler;
    private EpcSalesmanHandler epcSalesmanHandler;


    public EpcTransferQuoteHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper,
            EpcOrderHandler epcOrderHandler, EpcSalesmanHandler epcSalesmanHandler) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
        this.epcOrderHandler = epcOrderHandler;
        this.epcSalesmanHandler = epcSalesmanHandler;
    }


    public void transferQuoteToOrder(EpcTransferOrder epcTransferOrder) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        String orderReference = "xxx";
        String custId = "";
        String orderChannel = "";
        String orderUser = "";
        String orderSalesman = "";
        String orderLocation = "";
        int newOrderId = 0;
        int tmpOrderId = 0;
        int tmpQuoteId = 0;
        String tmpQuoteGuid = "";
        boolean isValid = true;
        String errMsg = "";
        String remarks = "";


        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);
            
            // get input param
            custId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcTransferOrder.getCustId()));
            orderChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcTransferOrder.getOrderChannel()));
            orderUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcTransferOrder.getOrderUser()));
            orderSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcTransferOrder.getOrderSalesman()));
            orderLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcTransferOrder.getOrderLocation()));
            newOrderId = epcTransferOrder.getOrderId();
            tmpOrderId = epcTransferOrder.getTmpOrderId();
            tmpQuoteId = epcTransferOrder.getTmpQuoteId();
            // end of get input param


            // basic checking
            if("".equals(custId)) {
                errMsg += "input cust id is empty. ";
                isValid = false;
            }

            if(newOrderId != 0) {
                // check whether this order id is belonged to input cust id (existingOrderId vs custId)
                orderReference = epcOrderHandler.isOrderBelongCust(conn, custId, newOrderId);
                if("NOT_BELONG".equals(orderReference)) {
                    errMsg += "input order id [" + newOrderId + "] is not belonged to input cust id [" + custId + "]. ";
                    isValid = false;
                }
            } else {
                errMsg += "input order id [" + newOrderId + "] is not valid. ";
                isValid = false;
            }

            if(epcOrderHandler.isOrderLocked(conn, custId, newOrderId)) {
                errMsg += "input order [" + newOrderId + "] is locked. ";
                isValid = false;
            }

            if(tmpOrderId != 0) {
                // check whether this order id is belonged to input cust id (existingOrderId vs custId)
                orderReference = epcOrderHandler.isOrderBelongCust(conn, custId, tmpOrderId);
                if("NOT_BELONG".equals(orderReference)) {
                    errMsg += "input tmp order id [" + tmpOrderId + "] is not belonged to input cust id [" + custId + "]. ";
                    isValid = false;
                }
            } else {
                errMsg += "input tmp order id [" + tmpOrderId + "] is not valid. ";
                isValid = false;
            }

            if(epcOrderHandler.isOrderLocked(conn, custId, tmpOrderId)) {
                errMsg += "input order [" + tmpOrderId + "] is locked. ";
                isValid = false;
            }

            if(tmpQuoteId != 0) {
                tmpQuoteGuid = epcOrderHandler.getCurrentQuoteGuid(conn, tmpOrderId, tmpQuoteId);
                if("".equals(tmpQuoteGuid)) {
                    errMsg += "input tmp quote id [" + tmpQuoteId + "] is not belonged to input tmp order id [" + tmpOrderId + "]. ";
                    isValid = false;
                }
            } else {
                errMsg += "input tmp quote id [" + tmpQuoteId + "] is not valid. ";
                isValid = false;
            }
            // end of basic checking


            if(isValid) {
                // update all attr under this quote to new order (order_id + item_id)
                sql = "update epc_order_attr " +
                      "   set order_id = ? " +
                      " where (order_id, item_id) in ( " +
                      "   select order_id, item_id " +
                      "     from epc_order_item " +
                      "    where order_id = ? " +
                      "      and quote_id = ? " +
                      " ) ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, newOrderId); // order_id - new order id
                pstmt.setInt(2, tmpOrderId); // order_id - tmp order id
                pstmt.setInt(3, tmpQuoteId); // quote_id - tmp quote id
                pstmt.executeUpdate();
                pstmt.close();

                // update all attr under this quote to new order (order_id + case_id)
                sql = "update epc_order_attr " +
                      "   set order_id = ? " +
                      " where (order_id, case_id) in ( " +
                      "   select order_id, case_id " +
                      "     from epc_order_case " +
                      "    where order_id = ? " +
                      "      and quote_id = ? " +
                      " ) ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, newOrderId); // order_id - new order id
                pstmt.setInt(2, tmpOrderId); // order_id - tmp order id
                pstmt.setInt(3, tmpQuoteId); // quote_id - tmp quote id
                pstmt.executeUpdate();
                pstmt.close();

                // update all cust profile under this quote to new order (order_id + case_id)
                sql = "update epc_order_cust_profile " +
                      "   set order_id = ? " +
                      " where (order_id, case_id) in ( " +
                      "   select order_id, case_id " +
                      "     from epc_order_case " +
                      "    where order_id = ? " +
                      "      and quote_id = ? " +
                      " ) ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, newOrderId); // order_id - new order id
                pstmt.setInt(2, tmpOrderId); // order_id - tmp order id
                pstmt.setInt(3, tmpQuoteId); // quote_id - tmp quote id
                pstmt.executeUpdate();
                pstmt.close();

                // update all items under this quote to new order
                sql = "update epc_order_item " +
                      "   set order_id = ? " +
                      " where order_id = ? " +
                      "   and quote_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, newOrderId); // order_id - new order id
                pstmt.setInt(2, tmpOrderId); // order_id - tmp order id
                pstmt.setInt(3, tmpQuoteId); // quote_id - tmp quote id
                pstmt.executeUpdate();
                pstmt.close();

                // update all cases under this quote to new order
                sql = "update epc_order_case " +
                      "   set order_id = ? " +
                      " where order_id = ? " +
                      "   and quote_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, newOrderId); // order_id - new order id
                pstmt.setInt(2, tmpOrderId); // order_id - tmp order id
                pstmt.setInt(3, tmpQuoteId); // quote_id - tmp quote id
                pstmt.executeUpdate();
                pstmt.close();

                // update quote_id to new order
                sql = "update epc_order_quote " +
                      "   set order_id = ? " +
                      " where order_id = ? " +
                      "   and quote_id = ? ";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, newOrderId); // order_id - new order id
                pstmt.setInt(2, tmpOrderId); // order_id - tmp order id
                pstmt.setInt(3, tmpQuoteId); // quote_id - tmp quote id
                pstmt.executeUpdate();
                pstmt.close();


                // create salesman log
                remarks = "transfer tmp order [" + tmpOrderId + "] tmp quote [" + tmpQuoteId + "] to order [" + newOrderId + "]";
                epcSalesmanHandler.createSalesmanLog(conn, newOrderId, "", orderUser, orderSalesman, orderLocation, orderChannel, epcSalesmanHandler.actionTransferQuote, remarks);
                // end of create salesman log

                // update salesman log of tmp order
                epcSalesmanHandler.updateSalesmanLog(conn, tmpOrderId, newOrderId, remarks);
                // end of update salesman log of tmp order

                conn.commit();

                epcTransferOrder.setResult("SUCCESS");
            } else {
                epcTransferOrder.setResult("FAIL");
                epcTransferOrder.setErrMsg(errMsg);
            }
        } catch (Exception e) {
            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            e.printStackTrace();

            epcTransferOrder.setResult("FAIL");
            epcTransferOrder.setErrMsg(e.getMessage());
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }
}
