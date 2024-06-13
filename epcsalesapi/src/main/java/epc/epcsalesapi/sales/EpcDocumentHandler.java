/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.EpcDocumentHandler
 * @author	TedKwan
 * @date	20-Jul-2022
 * Description:
 * Save file to EPC_ORDER_ATTACH.
 * First draft to save delivery note only, expand this class with new save function similar to saveDeliverNote() for other file type.
 *
 * History:
 * 20220720-TedKwan: Created
 */
package epc.epcsalesapi.sales;

import epc.epcsalesapi.crm.EpcCustomerHandler;
import epc.epcsalesapi.helper.DBHelper;
import epc.epcsalesapi.img.EpcImgHandler;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.apache.hc.core5.http.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.DateHelper;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.orderAttachment.EpcOrderAttach;
import epc.epcsalesapi.sales.bean.orderAttachment.EpcOrderAttachType;
import java.math.BigDecimal;
import org.apache.commons.lang.StringUtils;

@Service
public class EpcDocumentHandler {
	private final Logger logger = LoggerFactory.getLogger(EpcDocumentHandler.class);

        private static final String ENC_ID_PDF_AUTHOR_FORMAT = "Z_%s";

        @Autowired
        private DataSource epcDataSource;

        @Autowired
        EpcOrderHandler epcOrderHandler;

        @Autowired
        EpcCustomerHandler epcCustomerHandler;
        
        @Autowired
        EpcImgHandler epcImgHandler;
	
	private static String SQL_GET_NEXT_ORDER_ID_SEQ="SELECT EPC_ORDER_ID_SEQ.NEXTVAL FROM DUAL";
	private static String SQL_INSERT_FILE_RECORD="INSERT INTO EPC_ORDER_ATTACH (REC_ID, CUST_ID, ORDER_ID, ATTACH_TYPE, ATTACH_CONTENT, CREATE_DATE, CREATE_TIMESTAMP, CREATE_MTH, CONTENT_TYPE, IMG_SEQ_NO) "
												+" VALUES (?,?,?,?,?, SYSDATE,?, TO_CHAR(SYSDATE, 'MM'),?,?)";
        private static String SQL_UPDATE_FILE_RECORD_IMG_SEQ_NO="UPDATE EPC_ORDER_ATTACH SET IMG_SEQ_NO = ? WHERE REC_ID=?";
	private static String SQL_GET_FILE_LIST="SELECT REC_ID, CUST_ID, ORDER_ID, ATTACH_TYPE, CONTENT_TYPE, CREATE_DATE, IMG_SEQ_NO FROM EPC_ORDER_ATTACH";
        private static String SQL_GET_FILE_CONTENT_LIST="SELECT REC_ID, CUST_ID, ORDER_ID, ATTACH_TYPE, CONTENT_TYPE, ATTACH_CONTENT, CREATE_DATE, IMG_SEQ_NO FROM EPC_ORDER_ATTACH";
	private static String SQL_GET_FILE_LIST_BY_CUST_ID=SQL_GET_FILE_LIST+" WHERE CUST_ID=?";
	private static String SQL_GET_FILE_LIST_BY_ORDER_ID=SQL_GET_FILE_LIST+" WHERE ORDER_ID=?";
        private static String SQL_GET_FILE_CONTENT_LIST_BY_ORDER_ID=SQL_GET_FILE_CONTENT_LIST+" WHERE ORDER_ID=?";
	private static String SQL_GET_FILE_BY_REC_ID="SELECT REC_ID, CUST_ID, ORDER_ID, ATTACH_TYPE, CONTENT_TYPE, ATTACH_CONTENT, IMG_SEQ_NO FROM EPC_ORDER_ATTACH WHERE REC_ID=?";
	
	/** Method: saveOrderFile(String custId, Integer orderId, Blob pdfFile, EpcOrderAttachType attachType, ContentType contentType)
	 *  Return: void
	 *  Description: Save file to table EPC_ORDER_ATTACH
	 * @param custId String
	 * @param orderId Integer
	 * @param fileByte Byte[]
	 * @param attachType EpcOrderAttachType
	 * @param contentType ContentType
	 * @throws Exception 
	 */
	public void saveOrderFile(String custId, Integer orderId, byte[] fileByte, String attachType, ContentType contentType) throws Exception {
		Connection epcConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try{
			int recId=0;
			epcConn = epcDataSource.getConnection();
			epcConn.setAutoCommit(false);
			
			//1. Get next order ID seq
			pstmt=epcConn.prepareStatement(SQL_GET_NEXT_ORDER_ID_SEQ);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				recId=rs.getInt("NEXTVAL");
			}
			pstmt.close();
			
			if(recId <= 0) throw new Exception("Get next rec ID failed.");
                        
			//2. Save file to DB
			pstmt=epcConn.prepareStatement(SQL_INSERT_FILE_RECORD);
			pstmt.setInt(1, recId);
			pstmt.setString(2, custId);
			pstmt.setInt(3, orderId);
			pstmt.setString(4, attachType);
			Blob fileBlob=epcConn.createBlob();
			fileBlob.setBytes(1l, fileByte);
			pstmt.setBlob(5, fileBlob);
			pstmt.setString(6, DateHelper.getCurrentDateTime(DateHelper.DT_FMT_FULL_TRIM));
			pstmt.setString(7, contentType.getMimeType());
                        pstmt.setBigDecimal(8, null);
			pstmt.executeUpdate();
			fileBlob.free();
			
			epcConn.commit();
                        
                        //3. Upload Sales Agreement, Delivery Note to Imaging and update Imaging Sequence No.
                        if (EpcOrderAttachType.SALES_AGREEMENT.equals(attachType) || EpcOrderAttachType.DELIVERY_NOTE.equals(attachType))
                        {
                            EpcOrderAttach orderAttach = new EpcOrderAttach();
                            orderAttach.setREC_ID(recId);
                            orderAttach.setORDER_ID(orderId);
                            orderAttach.setATTACH_TYPE(attachType);
                            orderAttach.setCONTENT_TYPE(contentType.getMimeType());
                            orderAttach.setATTACH_CONTENT_BYTE(fileByte);
                            epcImgHandler.uploadAttachmentAsync(orderAttach, true);
                        }
		} catch (SQLException e) {
			try { 
				epcConn.rollback(); 
				logger.error("",e);
			} catch (Exception ignore) {}
			throw new Exception(e);
		} catch (Exception e) {
			//For recId not get exception
			//Nothing to rollback, re-throw exception
			throw new Exception(e);
		} finally {
			try { rs.close(); rs=null; } catch (Exception ignore) {}
			try { pstmt.close(); pstmt=null; } catch (Exception ignore) {}
			try { epcConn.close(); } catch (Exception ignore) {}
		};
	}
        
        /**
         * Method: updateOrderFileImgSeq(String recId, Long seqNo) Return: void
         * Description: Update Imaging Sequence No. to table EPC_ORDER_ATTACH
         *
         * @param recId String
         * @param seqNo Long
         * @throws Exception
         */
        public int updateOrderFile(Integer recId, Long seqNo) throws Exception
        {
            Connection epcConn = null;
            PreparedStatement pstmt = null;
            ResultSet rs = null;
            int updatedRow = 0;

            try
            {
                if (recId != null)
                {
                    epcConn = epcDataSource.getConnection();
                    epcConn.setAutoCommit(false);

                    //Update Sequence No. of attachment in EPC_ORDER_ATTACH table
                    pstmt = epcConn.prepareStatement(SQL_UPDATE_FILE_RECORD_IMG_SEQ_NO);
                    pstmt.setBigDecimal(1, BigDecimal.valueOf(seqNo));
                    pstmt.setInt(2, recId);
                    updatedRow = pstmt.executeUpdate();

                    epcConn.commit();
                }
                else
                {
                    throw new Exception("Update order attachment error. Rec ID is null.");
                }
            }
            catch (SQLException e)
            {
                try
                {
                    epcConn.rollback();
                    logger.error("", e);
                }
                catch (Exception ignore)
                {
                }
                throw new Exception(e);
            }
            catch (Exception e)
            {
                throw new Exception(e);
            }
            finally
            {
                try
                {
                    rs.close();
                    rs = null;
                }
                catch (Exception ignore)
                {
                }
                try
                {
                    pstmt.close();
                    pstmt = null;
                }
                catch (Exception ignore)
                {
                }
                try
                {
                    epcConn.close();
                }
                catch (Exception ignore)
                {
                }
            };
            
            return updatedRow;
        }
        
	/** Method: parseDocList(ResultSet rs, ArrayList<EpcOrderAttach> fileList)
	 *  Return: void
	 *  Description: Retrieve result from result set and put into filelist (format of EpcOrderAttach)
	 * @param rs ResultSet
	 * @param fileList ArrayList<EpcOrderAttach>
         * @param isRequiredFileContent Boolean
	 */
	private static void parseDocList(ResultSet rs, ArrayList<EpcOrderAttach> fileList, boolean isRequiredFileContent) throws SQLException {
			while(rs.next()) {
				EpcOrderAttach epcOrderAttach=new EpcOrderAttach();
				epcOrderAttach.setREC_ID(rs.getInt("REC_ID"));
				epcOrderAttach.setCUST_ID(rs.getString("CUST_ID"));
				epcOrderAttach.setORDER_ID(rs.getInt("ORDER_ID"));
				epcOrderAttach.setATTACH_TYPE(rs.getString("ATTACH_TYPE"));
				epcOrderAttach.setCONTENT_TYPE(rs.getString("CONTENT_TYPE"));
				epcOrderAttach.setCREATE_DATE(rs.getDate("CREATE_DATE"));
                                epcOrderAttach.setIMG_SEQ_NO(DBHelper.getLong(rs, "IMG_SEQ_NO"));
                                if(isRequiredFileContent)
                                {
                                    epcOrderAttach.setATTACH_CONTENT(rs.getBlob("ATTACH_CONTENT"));
                                }
				fileList.add(epcOrderAttach);
			}
	}
	
	/** Method: getOrderFileListByCustId(String custId)
	 *  Return: ArrayList<EpcOrderAttach>
	 *  Description: Get file list by customer ID. Return ArrayList format of EpcOrderAttach without ATTACH_CONTENT
	 * @param custId String
	 * @return ArrayList<EpcOrderAttach>
	 * @throws Exception 
	 */
	public ArrayList<EpcOrderAttach> getOrderFileListByCustId(String custId) throws Exception{
		Connection epcConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		ArrayList<EpcOrderAttach> fileList=new ArrayList<>();
		
		try{
			epcConn = epcDataSource.getConnection();
			
			pstmt=epcConn.prepareStatement(SQL_GET_FILE_LIST_BY_CUST_ID);
			pstmt.setString(1, custId);
			rs = pstmt.executeQuery();
			parseDocList(rs, fileList, false);

		} catch (SQLException e) {
			try { 
				epcConn.rollback();
				logger.error("",e);
			} catch (Exception ignore) {}
			throw new Exception(e);
		} finally {
			try { rs.close(); rs=null; } catch (Exception ignore) {}
			try { pstmt.close(); pstmt=null; } catch (Exception ignore) {}
			try { epcConn.close(); } catch (Exception ignore) {}
		}
		
		return fileList;
	}
	
	/** *  Method: getOrderFileListByOrderId(Integer orderId)
	 *  Return: ArrayList<EpcOrderAttach>
	 *  Description: Get file list by order ID. Return ArrayList format of EpcOrderAttach without ATTACH_CONTENT
	 * @param orderId Integer
         * @param isRequiredFileContent Boolean
	 * @return ArrayList<EpcOrderAttach>
	 * @throws Exception 
	 */
	public ArrayList<EpcOrderAttach> getOrderFileListByOrderId(Integer orderId, boolean isRequiredFileContent) throws Exception{
		Connection epcConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		ArrayList<EpcOrderAttach> fileList=new ArrayList<>();
		
		try{
			epcConn = epcDataSource.getConnection();
			
                        if(isRequiredFileContent)
                        {
                            pstmt=epcConn.prepareStatement(SQL_GET_FILE_CONTENT_LIST_BY_ORDER_ID);
                        }
			else
                        {
                            pstmt=epcConn.prepareStatement(SQL_GET_FILE_LIST_BY_ORDER_ID);
                        }
			pstmt.setInt(1, orderId);
			rs = pstmt.executeQuery();
			parseDocList(rs, fileList, isRequiredFileContent);

		} catch (SQLException e) {
			try { epcConn.rollback(); logger.error("",e);} catch (Exception ignore) {}
			throw new Exception(e);
		} finally {
			try { rs.close(); rs=null; } catch (Exception ignore) {}
			try { pstmt.close(); pstmt=null; } catch (Exception ignore) {}
			try { epcConn.close(); } catch (Exception ignore) {}
		}
		
		return fileList;
	}
	
	/** Method: getOrderFileByRecId(Integer recId)
	 *  Return: EpcOrderAttach
	 *  Description: Get file rec ID. Return EpcOrderAttach that only contain ATTACH_CONTENT
	 * @param recId Integer
	 * @return EpcOrderAttach
	 * @throws Exception 
	 */
	public EpcOrderAttach getOrderFileByRecId(Integer recId) throws Exception{
		Connection epcConn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		EpcOrderAttach epcOrderAttach = new EpcOrderAttach();
		
		try{
			epcConn = epcDataSource.getConnection();
			
			pstmt=epcConn.prepareStatement(SQL_GET_FILE_BY_REC_ID);
			pstmt.setInt(1, recId);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				epcOrderAttach.setREC_ID(rs.getInt("REC_ID"));
				epcOrderAttach.setCUST_ID(rs.getString("CUST_ID"));
				epcOrderAttach.setORDER_ID(rs.getInt("ORDER_ID"));
				epcOrderAttach.setATTACH_CONTENT(rs.getBlob("ATTACH_CONTENT"));
				epcOrderAttach.setCONTENT_TYPE(rs.getString("CONTENT_TYPE"));
				epcOrderAttach.setATTACH_TYPE(rs.getString("ATTACH_TYPE"));
                                epcOrderAttach.setIMG_SEQ_NO(DBHelper.getLong(rs, "IMG_SEQ_NO"));
			}

		} catch (SQLException e) {
			try { 
				epcConn.rollback();
				logger.error("",e);
			} catch (Exception ignore) {}
			throw new Exception(e);
		} finally {
			try { rs.close(); rs=null; } catch (Exception ignore) {}
			try { pstmt.close(); pstmt=null; } catch (Exception ignore) {}
			try { epcConn.close(); } catch (Exception ignore) {}
		}
		
		return epcOrderAttach;
	}

	/** Method: saveDeliverNote(String custId, Integer orderId, Blob pdfFile)
	 *  Return: void
	 *  Description: Save delivery note to EPC_ORDER_ATTACH
	 * @param custId String
	 * @param orderId Integer
	 * @param fileByte byte[]
	 * @throws Exception 
	 */
	public void saveDeliverNote(String custId, Integer orderId, byte[] fileByte) throws Exception {
		saveOrderFile(custId, orderId, fileByte, EpcOrderAttachType.DELIVERY_NOTE, ContentType.APPLICATION_PDF);
	}

    public void saveDoc(String custId, Integer orderId, String attachType, byte[] fileByte) throws Exception {
        saveOrderFile(custId, orderId, fileByte, attachType, ContentType.APPLICATION_PDF);
    }
    
    public String genPdfAuthor(int orderId)
    {
        String id = null;
        String encId = null;
        EpcOrderInfo epcOrderInfo = null;
        String pdfAuthor = "";

        try
        {
            //Retrieve customer ID
            epcOrderInfo = epcOrderHandler.getOrderSlimInfo(orderId);

            //Construct encrypted ID as PDF author if has ID
            id = epcCustomerHandler.getHkidByCustIdSlim(epcOrderInfo.getCustId());
            if (StringUtils.isNotBlank(id))
            {
                encId = EpcCrypto.eGet(id, "UTF-8");
                pdfAuthor = String.format(ENC_ID_PDF_AUTHOR_FORMAT, encId);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return pdfAuthor;
    }
}
