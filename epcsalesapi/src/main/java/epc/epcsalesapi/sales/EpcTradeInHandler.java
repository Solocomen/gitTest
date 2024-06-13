package epc.epcsalesapi.sales;

import epc.epcsalesapi.sales.bean.*;
import java.sql.*;
import javax.sql.*;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;     // added by Danny Chan on 2023-12-8 (create a new api getting valid offer(s) under an order for later trade-in)


@Service
public class EpcTradeInHandler {

    @Autowired
    private DataSource fesDataSource;
    
    @Autowired
    private DataSource epcDataSource;
    
    private final Logger logger = LoggerFactory.getLogger(EpcTradeInHandler.class);

    // added by Danny Chan on 2023-12-8 (create a new api getting valid offer(s) under an order for later trade-in): start
    public EpcGetValidOfferResult getValidOffer(String orderReference) {
        EpcGetValidOfferResult result = new EpcGetValidOfferResult();

        Connection epcConn = null;

        PreparedStatement pstmt = null;

        ResultSet rset = null;

        try {
            epcConn = epcDataSource.getConnection();
			
            pstmt = epcConn.prepareStatement(
                        "select a.order_id, b.case_id, c.cpq_item_desc, c.cpq_item_desc_chi " +
                        "from epc_order a, epc_order_case b, epc_order_item c " + 
                        "where a.order_reference = ? " +
                        "and a.order_status in ('PF','CO') " +
                        "and trunc(sysdate) - trunc(a.place_order_date) <= 14 " +
                        "and b.order_id = a.order_id " +
                        "and b.cancel_receipt is null " +
                        "and c.order_id = b.order_id " +
                        "and c.case_id = b.case_id " +
                        "and c.parent_item_id is null " +
                        "and exists (" + 
                        "  select 1 from epc_order_item zz where zz.order_id = c.order_id and zz.case_id = c.case_id and zz.warehouse in ('AH', 'AA')" + 
                        ") " + 
                        "and not exists (" + 
                        "  select 1 from epc_order_item yy where yy.order_id = c.order_id and yy.case_id = c.case_id and yy.item_cat = 'TRADE_IN'" + 
                        ")"
                    );
			
            pstmt.setString(1, orderReference);
			
            rset = pstmt.executeQuery();
            
            ArrayList<EpcOffer> offers = new ArrayList();
			
            while (rset.next()) { 
                result.setOrderId(rset.getInt(1));

                EpcOffer item = new EpcOffer();
                item.setCaseId( rset.getString(2) );
                item.setOfferDesc( rset.getString(3));
                item.setOfferDescChi(rset.getString(3));

                offers.add(item);
            }

            result.setOffers(offers);
            result.setResult("SUCCESS");
        } catch (Exception e) {
            result.setResult("FAIL");
            result.setErrMsg("System Error");
            result.setOffers(null);
            result.setOrderId(-1);
            return result;
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
        }

        return result;
    }
    // added by Danny Chan on 2023-12-8 (create a new api getting valid offer(s) under an order for later trade-in): end
	
    public EpcGetTradeInResult getTradeIn(String referenceNo) {
        EpcGetTradeInResult result = new EpcGetTradeInResult();
        Connection fesConn = null;
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt2 = null;
        PreparedStatement pstmt3 = null;
        ResultSet rset = null;
        try {
            fesConn = fesDataSource.getConnection();
            epcConn = epcDataSource.getConnection();
            result.setReferenceNo(referenceNo);
            
            if (referenceNo == null) {
                result.setResult("FAILURE");
                result.setErrorCode("1001");
                result.setErrorMessage("Missing Trade-in reference number.");
                return result;
            }
            
            pstmt = epcConn.prepareStatement(
                        "SELECT 1 FROM epc_order a, epc_order_item b " +
                        "WHERE b.item_cat = ? AND b.item_code = ? " +
                        "AND a.order_id = b.order_id " +
                        "AND a.order_status IN ('PF' ,'CO', 'LOCK') "
                    );
            pstmt.setString(1, "TRADE_IN");
            pstmt.setString(2, referenceNo);
            rset = pstmt.executeQuery();
            if (rset.next()) {
                result.setResult("FAILURE");
                result.setErrorCode("1003");
                result.setErrorMessage("Trade-in reference number was used.");
                return result;
            }
            rset.close();
            
            pstmt2 = fesConn.prepareStatement("SELECT 1 FROM zz_pinv_ti WHERE ti_ref = ? AND stat = ? ");
            pstmt2.setString(1, referenceNo);
            pstmt2.setString(2, "N");
            rset = pstmt2.executeQuery();
            if (rset.next()) {
                result.setResult("FAILURE");
                result.setErrorCode("1003");
                result.setErrorMessage("Trade-in reference number was used.");
                return result;
            }
            rset.close();
            
            pstmt3 = fesConn.prepareStatement(
                        "SELECT b.prod_serv_code, b.price, b.tradein_level " + 
                        "FROM zz_pret_hdg a, zz_pret_dtl b " +
                        "WHERE a.return_ref = b.return_ref "+
                        "AND a.return_ref = ? " +
                        "AND a.stat = ? " +
                        "AND a.tx_type = ? "
                      );
            pstmt3.setString(1, referenceNo);
            pstmt3.setString(2, "N");
            pstmt3.setString(3, "T");
            rset = pstmt3.executeQuery();
            if (rset.next()) {
                result.setResult("SUCCESS");
                result.setProductCode(rset.getString("prod_serv_code"));
                result.setLevel(rset.getInt("tradein_level"));
                result.setTradeInValue(rset.getBigDecimal("price"));
            } else {
                result.setResult("FAILURE");
                result.setErrorCode("1002");
                result.setErrorMessage("Invalid Trade-in reference number.");
            }
            rset.close();
            
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            result.setResult("FAILURE");
            result.setErrorCode("1004");
            result.setErrorMessage("System Error");
            return result;
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if (pstmt2 != null) { pstmt2.close(); } } catch (Exception ignore) {}
            try { if (pstmt3 != null) { pstmt3.close(); } } catch (Exception ignore) {}
            try { if (fesConn != null) { fesConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (fesConn != null) { fesConn.close(); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
        }
        return result;
    }
    
    public EpcValidateTradeInResult validateInvoiceNo(String referenceNo) {
    	EpcValidateTradeInResult result = new EpcValidateTradeInResult();
        Connection epcConn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        try {
            epcConn = epcDataSource.getConnection();
            
            if (StringUtils.isBlank(referenceNo)) {
                result.setResult("FAILURE");
                result.setErrorCode("1001");
                result.setErrorMessage("Missing Trade-in reference number.");
                return result;
            }
            
            pstmt = epcConn.prepareStatement(
                        "select a.order_reference from epc_order a, epc_order_case b, epc_order_item c "
                        + "where a.order_status in ('PF','CO') "
                        + "and b.order_id = a.order_id "
                        + "and b.cancel_receipt is null "
                        + "and c.order_id = b.order_id "
                        + "and c.case_id = b.case_id  "
                        + "and c.item_cat = ? "
                        + "and c.cpq_item_value= ? "
                        + " "
                    );
            pstmt.setString(1, "TRADE_IN");
            pstmt.setString(2, referenceNo);
            rset = pstmt.executeQuery();
            if (rset.next()) {
                result.setResult("FAILURE");
                result.setErrorCode("1003");
                result.setErrorMessage("Trade-in reference number was used.");
                result.setOrderReference(rset.getString("order_reference"));
                return result;
            }
            rset.close();
            result.setResult("SUCCESS");
        } catch (Exception e) {
            logger.info(e.getMessage(), e);
            result.setResult("FAILURE");
            result.setErrorCode("1004");
            result.setErrorMessage("System Error");
            return result;
        } finally {
            try { if (rset != null) { rset.close(); } } catch (Exception ignore) {}
            try { if (pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.setAutoCommit(true); } } catch (Exception ignore) {}
            try { if (epcConn != null) { epcConn.close(); } } catch (Exception ignore) {}
        }
        return result;
    }

}
