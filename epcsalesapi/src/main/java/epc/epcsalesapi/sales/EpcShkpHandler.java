/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.catalogService.CatalogServiceHandler;
import epc.epcsalesapi.sales.bean.EpcCreateShkPointConfigResult;
import epc.epcsalesapi.sales.bean.EpcQuote;
import epc.epcsalesapi.sales.bean.EpcQuoteItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.sql.DataSource;
//import static org.springframework.data.redis.serializer.RedisSerializationContext.java;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

/**
 *
 * @author DannyChan
 */
@Service
public class EpcShkpHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcShkpHandler.class);	
       
    @Autowired						
    private EpcQuoteHandler epcQuoteHandler;

    @Autowired
    private DataSource epcDataSource;
	
	@Autowired
	private  CatalogServiceHandler catalogServiceHandler;
    
    // added by Danny Chan on 2022-10-11 (nehancement of payment page to support SHKP): start
    @Autowired
    private DataSource fesDataSource;
   
    public boolean isSHKShop(String locationCode) throws SQLException {
        Connection fesConn = null; 

        String sql = "SELECT rec_content_1 shop_id " + 
                     "FROM pos_param_ctrl " + 
                     "WHERE rec_type_1='SHK_CASH_REBATE_API' " + 
                     "AND rec_type_2='POINT_SHOP_ID' " + 
                     "AND rec_type_3=? " + 
                     "AND status='A'";
	
        ResultSet rset = null; 
        PreparedStatement pstmt = null; 
	
        try {
	    fesConn = fesDataSource.getConnection();
	    
	    pstmt = fesConn.prepareStatement(sql);
	    pstmt.setString(1, locationCode);
	    
	    rset = pstmt.executeQuery();
	    
	    if (rset.next()) {
                return true;
	    }
		    
	    return false;
        } catch (SQLException e) {
	    throw e;
        } finally {
            try {rset.close();} catch (Exception e) {}
            try {pstmt.close();} catch (Exception e) {}
	    try {fesConn.close();}
	    catch (Exception e) {}
        }
    }
    
    /*public String initSHKPPayment( ArrayList<HashMap> epcInitSHKPPaymentMap ) {
	String result = "{}";
	
	try {   
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<ArrayList> request = new HttpEntity<ArrayList>(epcInitSHKPPaymentMap, headers);
		
		ResponseEntity<String> responseEntity = restTemplate.exchange("https://fessales-uat.smartone.com/api/shk/initSHKPPayment", HttpMethod.POST, request, String.class);
	
		System.out.println( "response status code = " + responseEntity.getStatusCode() );

		if (responseEntity.getStatusCode().value()==200) {
			result = responseEntity.getBody();
		} else {
			result = "{\"error\": \"The status code returned is " + responseEntity.getStatusCode().value() + "\"}";
		}
	} catch (Exception e) {
		System.out.println(e.toString());
		result = "{\"error\": \"" + e.toString() + "\"}";
		e.printStackTrace();
	} finally {	
	}
	
	return result;
    }    
    
    public String updateShkpReceiptNo( HashMap epcUpdateShkpReceiptNoMap ) {
	String result = "{}";
	
	try {   
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<HashMap> request = new HttpEntity<HashMap>(epcUpdateShkpReceiptNoMap, headers);
		
		ResponseEntity<String> responseEntity = restTemplate.exchange("https://fessales-uat.smartone.com/api/shk/updateShkpReceiptNo", HttpMethod.POST, request, String.class);
	
		logger.info( "response status code = " + responseEntity.getStatusCode() );
		logger.info( "responseEntity.getBody() = " + responseEntity.getBody() );

		if (responseEntity.getStatusCode().value()==200) {
			result = responseEntity.getBody();
		} else {
			result = "{\"error\": \"The status code returned is " + responseEntity.getStatusCode().value() + "\"}";
		}
	} catch (Exception e) {
		System.out.println(e.toString());
		result = "{\"error\": \"" + e.toString() + "\"}";
		e.printStackTrace();
	} finally {	
	}
	
	return result;
    }
        
    public String refundSHKPDollars( HashMap epcRefundShkpPDollarsMap ) {
	String result = "{}";
	
	try {   
		RestTemplate restTemplate = new RestTemplate();

		HttpHeaders headers = new HttpHeaders();
		
		HttpEntity<HashMap> request = new HttpEntity<HashMap>(epcRefundShkpPDollarsMap, headers);
		
		ResponseEntity<String> responseEntity = restTemplate.exchange("https://fessales-uat.smartone.com/api/shk/refundSHKPDollars", HttpMethod.POST, request, String.class);
	
		logger.info( "response status code = " + responseEntity.getStatusCode() );
		logger.info( "responseEntity.getBody() = " + responseEntity.getBody() );

		if (responseEntity.getStatusCode().value()==200) {
			result = responseEntity.getBody();
		} else {
			result = "{\"error\": \"The status code returned is " + responseEntity.getStatusCode().value() + "\"}";
		}
	} catch (Exception e) {
		System.out.println(e.toString());
		result = "{\"error\": \"" + e.toString() + "\"}";
		e.printStackTrace();
	} finally {	
	}
	
	return result;
    }

    public String getSHKPPayment( String batchno ) {
	String rtn="{}";
	
	try {
		Map<String, String> params = new HashMap<String, String>();
		params.put("batchNo", batchno);
			
		RestTemplate restTemplate = new RestTemplate();
		
		rtn = restTemplate.getForObject("https://fessales-uat.smartone.com/api/shk/getSHKPPayment?batchNo={batchNo}", String.class, params);
        } catch (HttpServerErrorException e) {
		e.printStackTrace();
                rtn = e.getResponseBodyAsString();                        
	} catch (Exception e) {
		e.printStackTrace();
		logger.error(e.toString());
	}
	
	return rtn;
    }*/
    // added by Danny Chan on 2022-10-11 (nehancement of payment page to support SHKP): end
        
    public EpcCreateShkPointConfigResult createShkPointConfig(int orderId, EpcQuote epcQuote) {
        EpcCreateShkPointConfigResult result = new EpcCreateShkPointConfigResult();

	
    //String cached_import_id = epcQuoteHandler.getCachedImportId();
	//String import_id = epcQuoteHandler.getImportId();
	
	//logger.info("cached_import_id = " + cached_import_id);
	//logger.info("import_id = " + import_id);
	
	//if (!cached_import_id.equals(import_id)) {
	//   logger.info("The cache is cleared");
	//   epcQuoteHandler.clearCache();
	//   epcQuoteHandler.getCachedImportId();
	//}
	
	Connection epcConn = null;
	
        ResultSet rset = null;
        PreparedStatement pstmt = null;
	
        try {
	    epcConn = epcDataSource.getConnection();
	    
	    epcConn.setAutoCommit(false);
		
            EpcQuoteItem[] epcQuoteItems = epcQuote.getItems();
		
            for (int i=0; i<epcQuoteItems.length; i++) {		// loop for all items in a quote
                    
                // store a list of item id(s) of the point charges type
                Vector vec_point_charges = new Vector();
                    
                HashMap map = epcQuoteItems[i].getMetaDataLookup();
			
                String items[] = (String [])map.keySet().toArray(new String[0]);
			
                for (int j=0; j<items.length; j++) {
                    if ( map.get( items[j]) instanceof HashMap ) {
                       HashMap item_map = (HashMap)map.get( items[j] );

                       if ( item_map.get( "typePath" )!=null ) {
                          String typePath = (String)item_map.get("typePath");

                          if (typePath.startsWith("Point_Program_Specifications")) {
                             vec_point_charges.add( items[j] );
                          }
                       }
                    }
                }
			
                logger.info("vec_point_charge = " + vec_point_charges);
			
                HashMap product_candidate_map = epcQuoteItems[i].getProductCandidate();
		    
		String case_id = (String)epcQuoteItems[i].getProductCandidate().get("ID");
		
                try {
                    deletPointInfo(case_id, orderId, epcConn);
                    savePointInfo(product_candidate_map, vec_point_charges, case_id, orderId, epcConn);
                } catch (Exception e) {
                    throw e;
                }
            }     // end of loop for all items in a quote		
	    
	    epcConn.commit();

            result.setResultCode("0");
            result.setResultMsg("Success!");
        } catch (Exception e) {
	e.printStackTrace();
            try {epcConn.rollback();}
            catch (Exception e2) {}
		
            result.setResultCode("-1");
            result.setResultMsg(e.toString());
        } finally {
            try {rset.close();} catch (Exception e) {}
            try {pstmt.close();} catch (Exception e) {}
	    try {epcConn.close();}
	    catch (Exception e) {}
        }

        return result;
    }
    
    public void deletPointInfo(String id, int orderId, Connection conn) throws Exception {
        String sql = "DELETE FROM epc_order_earn_shk_point_config WHERE case_id = ? and order_id = ?";
	
        PreparedStatement pstmt = null;

	try {
            pstmt = conn.prepareStatement(sql);
		
            pstmt.setString(1, id);
            pstmt.setInt(2, orderId);
		
            pstmt.executeUpdate();
	} catch (Exception e) {
            throw e;
	} finally {
            try {pstmt.close();}
            catch (Exception e) {}
        }
    }
    
    public void savePointInfo(HashMap subtree, Vector vec_id, String case_id, int orderId, Connection conn) throws Exception {
        if ( vec_id.contains( (String)subtree.get("ID") ) ) { 
            String item_id = (String)subtree.get("ID");
	    
            java.util.ArrayList childEntity_arr = (java.util.ArrayList)subtree.get("ChildEntity");
	    
            for (int j=0; j<childEntity_arr.size(); j++) {
                HashMap child_map = (HashMap)childEntity_arr.get(j);
			
                java.util.ArrayList rate_attr_array = (java.util.ArrayList)child_map.get("RateAttribute");
                
                String sales_channel = null, location_code = null, shop_id = null, point_value = null;
                
                for (int k=0; k<rate_attr_array.size(); k++) {
                    HashMap rate_attr_map = (HashMap)rate_attr_array.get(k);
				
                    switch ((String)rate_attr_map.get("Name")) {
                           case "Sales_Channel": sales_channel = //epcQuoteHandler.getEntityName((String)rate_attr_map.get("Value"));
                                                                 catalogServiceHandler.getFactValueByFactGuid((String)rate_attr_map.get("Value"));
                                                 break;
                           case "Location_Code": location_code = //epcQuoteHandler.getEntityName((String)rate_attr_map.get("Value"));
						                                         catalogServiceHandler.getFactValueByFactGuid((String)rate_attr_map.get("Value"));
                                                 break;
                           case "Shop_ID":       shop_id = //epcQuoteHandler.getEntityName((String)rate_attr_map.get("Value"));
							                               catalogServiceHandler.getFactValueByFactGuid((String)rate_attr_map.get("Value"));
                                                 break;
                           case "Point_Value":   point_value = (String)rate_attr_map.get("Value");
                    }
                }
			
                logger.info("caseId = " + case_id);
                logger.info("orderId = " + orderId);
                logger.info("sales_channel = " + sales_channel);
                logger.info("location_code = " + location_code);
                logger.info("shop_id = " + shop_id);
                logger.info("point_value = " + point_value);			
                logger.info("item_id = " + item_id);
		 
                String sql = "INSERT INTO epc_order_earn_shk_point_config (order_id, case_id, item_id, order_channel, shop_id, point_value) VALUES (?, ?, ?, ?, ?, ?)";
			
                PreparedStatement pstmt = null;
			
                try {
                    pstmt = conn.prepareStatement(sql);
				
                    pstmt.setInt(1, orderId);
                    pstmt.setString(2, case_id);
                    pstmt.setString(3, item_id);
                    pstmt.setString(4, sales_channel);
				
                    if (shop_id.length()>20) {
                       shop_id = shop_id.substring(0,20);
                    }
				
                    pstmt.setString(5, shop_id);
                    pstmt.setString(6, point_value);
				
                    pstmt.executeUpdate();
                } catch (Exception e) {
                    throw e;
                } finally {
                    try {pstmt.close();}
                    catch (Exception e) {}
                }
	        }
        }

        java.util.ArrayList childEntity_arr = (java.util.ArrayList)subtree.get("ChildEntity");
	
        for (int j=0; j<childEntity_arr.size(); j++) {
            savePointInfo((HashMap)childEntity_arr.get(j), vec_id, case_id, orderId, conn);
        }
    }
}
