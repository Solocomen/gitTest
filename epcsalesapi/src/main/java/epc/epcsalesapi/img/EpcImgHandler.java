package epc.epcsalesapi.img;

import com.fasterxml.jackson.databind.ObjectMapper;
import epc.epcsalesapi.crm.EpcCustomerHandler;
import epc.epcsalesapi.helper.EpcActionLogHandler;
import epc.epcsalesapi.helper.EpcCrypto;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.helper.bean.EpcActionLog;
import epc.epcsalesapi.img.bean.DocType;
import epc.epcsalesapi.img.bean.EpcDeleteDocFromImg;
import epc.epcsalesapi.img.bean.EpcUploadDoc;
import epc.epcsalesapi.img.bean.EpcUploadDocToPci;
import epc.epcsalesapi.img.bean.ImgDocType;
import epc.epcsalesapi.img.bean.ImgValid;
import epc.epcsalesapi.img.bean.InsertSimpleImgRequest;
import epc.epcsalesapi.img.bean.InsertSimpleImgResponse;
import epc.epcsalesapi.img.bean.SaDocSource;
import epc.epcsalesapi.img.bean.UpdateAcctImgDocValidRequest;
import epc.epcsalesapi.img.bean.UpdateAcctImgDocValidResponse;
import epc.epcsalesapi.sales.EpcDocumentHandler;
import epc.epcsalesapi.sales.EpcGenSAHandler2;
import epc.epcsalesapi.sales.EpcOrderHandler;
import epc.epcsalesapi.sales.EpcSignatureHandler;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import epc.epcsalesapi.sales.bean.EpcOrderInfo;
import epc.epcsalesapi.sales.bean.orderAttachment.EpcOrderAttach;
import epc.epcsalesapi.sales.bean.orderAttachment.EpcOrderAttachType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class EpcImgHandler
{

    private final Logger logger = LoggerFactory.getLogger(EpcImgHandler.class);

    private static final String IMG_CREATE_SIMPLE_API;
    private static final String IMG_UPDATE_DOC_VALID_API;
    private static final String UPLOAD_MIME_TYPE_REGEX = "(?i)^(image|application)/(.*)$";
    private static final String IMG_TYPE_REGEX = "$2";

    private static final String ACTION_LOG_ACTION_UPDATE_ACCT_IMG_DOC_VALID = "UPDATE_ACCT_IMG_DOC_VALID";

    private static final String ACTION_LOG_ACTION_INSERT_SIMPLE_IMG = "INSERT_SIMPLE_IMG";

    @Autowired
    private DataSource epcDataSource;

    @Autowired
    private DataSource fesDataSource;

    @Autowired
    private DataSource crmFesDataSource;

    @Autowired
    @Lazy
    private EpcDocumentHandler epcDocumentHandler;

    @Autowired
    @Lazy
    private EpcOrderHandler epcOrderHandler;

    @Autowired
    @Lazy
    private EpcCustomerHandler epcCustomerHandler;

    @Autowired
    private EpcActionLogHandler epcActionLogHandler;

    @Autowired
    @Lazy
    private EpcGenSAHandler2 epcGenSAHandler2;

    @Autowired
    @Lazy
    private EpcSignatureHandler epcSignatureHandler;

    static
    {
        IMG_CREATE_SIMPLE_API = EpcProperty.getValue("IMG_CREATE_SIMPLE_LINK") + "?client_id=" + EpcProperty.getValue("IMG_CREATE_SIMPLE_LINK_CLIENTID") + "&client_secret=" + EpcProperty.getValue("IMG_CREATE_SIMPLE_LINK_CLIENTSECRET");
        IMG_UPDATE_DOC_VALID_API = EpcProperty.getValue("IMG_UPDATE_DOC_VALID_LINK") + "?client_id=" + EpcProperty.getValue("IMG_UPDATE_DOC_VALID_LINK_CLIENTID") + "&client_secret=" + EpcProperty.getValue("IMG_UPDATE_DOC_VALID_LINK_CLIENTSECRET");
    }

    public void uploadAttachmentAsync(int orderId, boolean forceUploadToImg)
    {
        try
        {
            EpcUploadDoc epcUploadDoc = new EpcUploadDoc();
            epcUploadDoc.setOrderId(orderId);
            epcUploadDoc.setForceUploadToImg(forceUploadToImg);
            CompletableFuture.completedFuture(epcUploadDoc).thenApplyAsync(s -> uploadAttachment(s));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void uploadAttachmentAsync(EpcOrderAttach orderAttach, boolean forceUploadToImg)
    {
        try
        {
            CompletableFuture.completedFuture(orderAttach).thenApplyAsync(s -> uploadAttachment(s, forceUploadToImg));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void updateAttachmentAsync(UpdateAcctImgDocValidRequest updateAcctImgDocValidRequest)
    {
        try
        {
            CompletableFuture.completedFuture(updateAcctImgDocValidRequest).thenApplyAsync(s -> updateAttachmentValid(s));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void voidAttachmentToImgAsync(Integer orderId)
    {
        try
        {
            CompletableFuture.completedFuture(orderId).thenApplyAsync(s -> voidAttachmentToImg(s));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String uploadAttachment(EpcOrderAttach orderAttach, boolean forceUploadToImg)
    {
        String result = "OK";
        String errMsg = "";
        String uploadAttachmentToImgResult = null;
        String insertOrderAttachToPciResult = null;
        EpcOrderAttach newOrderAttach = null;

        try
        {
            //Upload only Sales Agreement and Delivery Note to Imaging
            switch (orderAttach.getATTACH_TYPE())
            {
                case EpcOrderAttachType.SALES_AGREEMENT ->
                {
                    uploadAttachmentToImgResult = uploadAttachmentToImg(orderAttach, forceUploadToImg);
                }
                case EpcOrderAttachType.DELIVERY_NOTE ->
                {
                    uploadAttachmentToImgResult = uploadAttachmentToImg(orderAttach, forceUploadToImg);
                }
                default ->
                {
                }
            }

            //If upload attachment to Imaging success and patched IMG_SEQ_NO under EPC attachment table, then insert into PCI database
            if ("OK".equals(uploadAttachmentToImgResult))
            {
                newOrderAttach = epcDocumentHandler.getOrderFileByRecId(orderAttach.getREC_ID());
                if (newOrderAttach.getIMG_SEQ_NO() != null)
                {
                    insertOrderAttachToPciResult = uploadAttachmentToPci(newOrderAttach);

                    if (!"OK".equals(insertOrderAttachToPciResult))
                    {
                        errMsg += insertOrderAttachToPciResult;
                    }
                }
                else
                {
                    errMsg += "Failed to upload order attachment to PCI because EPC_ORDER_ATTACH.SEQ_NO is null, REC_ID:" + String.valueOf(orderAttach.getREC_ID());
                }
            }
            else
            {
                errMsg += uploadAttachmentToImgResult;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            errMsg += "uploadAttachment exception error. ";
        }
        finally
        {
            if (!errMsg.isBlank())
            {
                result = errMsg;
            }
        }

        return result;
    }

    //Upload order attachment to Imaging, PCI per order
    public String uploadAttachment(EpcUploadDoc epcUploadDoc)
    {
        ArrayList<EpcOrderAttach> orderAttachList = null;
        String errMsg = "";
        String uploadAttachmentResult = null;
        boolean isValid = true;

        try
        {
            //Default return success
            epcUploadDoc.setResult(EpcApiStatusReturn.RETURN_SUCCESS);

            //Basic checking
            if (epcUploadDoc.getOrderId() == null)
            {
                isValid = false;
                errMsg += "Order ID is missing. ";
            }

            if (isValid)
            {
                //Loop all attachment under the order and upload Sales Agreement, Delivery Note to Imaging
                orderAttachList = epcDocumentHandler.getOrderFileListByOrderId(epcUploadDoc.getOrderId(), true);

                for (EpcOrderAttach orderAttach : orderAttachList)
                {
                    uploadAttachmentResult = uploadAttachment(orderAttach, epcUploadDoc.isForceUploadToImg());
                    if (!"OK".equals(uploadAttachmentResult))
                    {
                        errMsg += uploadAttachmentResult;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            errMsg += "uploadAttachment exception error. ";
        }
        finally
        {
            if (!errMsg.isBlank())
            {
                epcUploadDoc.setResult(EpcApiStatusReturn.RETURN_FAIL);
                epcUploadDoc.setErrMsg(errMsg);
            }
        }

        return "OK";
    }

    public String uploadAttachmentToPci(EpcUploadDocToPci epcUploadDocToPci)
    {
        ArrayList<EpcOrderAttach> orderAttachList = null;
        String errMsg = "";
        String uploadAttachmentToPciResult = null;
        boolean isValid = true;

        try
        {
            //Default return success
            epcUploadDocToPci.setResult(EpcApiStatusReturn.RETURN_SUCCESS);

            //Basic checking
            if (epcUploadDocToPci.getOrderId() == null)
            {
                isValid = false;
                errMsg += "Order ID is missing. ";
            }

            if (isValid)
            {
                //Loop all attachment under the order and upload Sales Agreement, Delivery Note to Imaging
                orderAttachList = epcDocumentHandler.getOrderFileListByOrderId(epcUploadDocToPci.getOrderId(), true);

                for (EpcOrderAttach orderAttach : orderAttachList)
                {
                    uploadAttachmentToPciResult = uploadAttachmentToPci(orderAttach);

                    if (!"OK".equals(uploadAttachmentToPciResult))
                    {
                        errMsg += uploadAttachmentToPciResult;
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            errMsg += "uploadAttachmentToPci exception error. ";
        }
        finally
        {
            if (!errMsg.isBlank())
            {
                epcUploadDocToPci.setResult(EpcApiStatusReturn.RETURN_FAIL);
                epcUploadDocToPci.setErrMsg(errMsg);
            }
        }

        return "OK";
    }

    private String uploadAttachmentToImg(EpcOrderAttach orderAttach, boolean forceUploadToImg)
    {
        InsertSimpleImgResponse insertSimpleImgResponse = null;
        InsertSimpleImgRequest insertSimpleImgRequest = null;
        int updatedRow = 0;
        String referenceNumber = null;
        String hkid = null;
        String acctNo = null;
        String mobileNo = null;
        String acctImg = null;
        EpcOrderInfo orderInfo = null;
        Connection epcConn = null;
        ResultSet rset = null;
        String sql = null;
        PreparedStatement pstmt = null;
        String imageType = null;
        ImgDocType imgDocType = null;
        String errMsg = "";
        String result = "OK";

        try
        {
            //If force upload, ignore and no matter original IMG_SEQ_NO exists or not
            if (forceUploadToImg || orderAttach.getIMG_SEQ_NO() == null)
            {
                //API reference no is Order reference
                orderInfo = epcOrderHandler.getOrderSlimInfo(orderAttach.getORDER_ID());
                referenceNumber = String.valueOf(orderInfo.getOrderReference());

                //Get customer HKID
                hkid = epcCustomerHandler.getHkidByCustIdSlim(orderInfo.getCustId());

                //Get customer related info
                //Choose 1 of cust num / subr num which is not empty
                epcConn = epcDataSource.getConnection();
                sql = "select cust_num, subr_num "
                        + "  from epc_order_case "
                        + " where order_id = ? "
                        + "   and cust_num is not null ";
                pstmt = epcConn.prepareStatement(sql);
                pstmt.setInt(1, orderAttach.getORDER_ID()); // order_id
                rset = pstmt.executeQuery();
                if (rset.next())
                {
                    acctNo = StringHelper.trim(rset.getString("cust_num"));
                    mobileNo = StringHelper.trim(rset.getString("subr_num"));
                }
                rset.close();
                pstmt.close();

                //Convert MIME type to API Image type
                imageType = orderAttach.getCONTENT_TYPE().replaceAll(UPLOAD_MIME_TYPE_REGEX, IMG_TYPE_REGEX).toUpperCase();

                //Convert Blob to Base64 encoded string
                if (orderAttach.getATTACH_CONTENT_BYTE() != null && orderAttach.getATTACH_CONTENT_BYTE().length > 0)
                {
                    acctImg = Base64.getEncoder().encodeToString(orderAttach.getATTACH_CONTENT_BYTE());
                }
                else
                {
                    acctImg = Base64.getEncoder().encodeToString(orderAttach.getATTACH_CONTENT().getBytes(1, (int) orderAttach.getATTACH_CONTENT().length()));
                }

                //Convert EPC order attach type to Imaging doc type
                imgDocType = ImgDocType.fromEpcDocType(orderAttach.getATTACH_TYPE());

                insertSimpleImgRequest = new InsertSimpleImgRequest(orderAttach.getORDER_ID(), referenceNumber, hkid, acctNo, mobileNo, imgDocType.value, imageType, acctImg, true);
                insertSimpleImgResponse = uploadAttachmentToImg(insertSimpleImgRequest);

                //Update Imaging Sequence Number in EPC_ORDER_ATTACH table
                if (insertSimpleImgResponse != null && InsertSimpleImgResponse.SUCCESS.equals(insertSimpleImgResponse.getStatus()) && insertSimpleImgResponse.getSeqNo() != null)
                {
                    updatedRow = epcDocumentHandler.updateOrderFile(orderAttach.getREC_ID(), insertSimpleImgResponse.getSeqNo());

                    if (updatedRow != 1)
                    {
                        errMsg += "EPC update order attachment error. REC_ID:" + String.valueOf(orderAttach.getREC_ID());

                        throw new Exception("EPC update order attachment error. REC_ID:" + String.valueOf(orderAttach.getREC_ID()));
                    }
                }
                else
                {
                    errMsg += "Upload order attachment file to Imaging error. Response may be failed or Sequence No is not returned. ";
                }
            }
            else
            {
                errMsg += "Ignore upload order attachment to Imaging because SEQ_NO exists in table EPC_ORDER_ATTACH. REC_ID:" + String.valueOf(orderAttach.getREC_ID());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            errMsg += "uploadAttachmentToImg exception error. ";
        }
        finally
        {
            if (!errMsg.isBlank())
            {
                result = errMsg;
            }

            try
            {
                if (rset != null)
                {
                    rset.close();
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
            try
            {
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
            try
            {
                if (epcConn != null)
                {
                    epcConn.close();
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
        }

        return result;
    }

    public String uploadAttachmentToPci(EpcOrderAttach orderAttach)
    {
        SaDocSource saDocSource = null;
        String referenceNumber = null;
        String hkid = null;
        EpcOrderInfo orderInfo = null;
        ImgDocType imgDocType = null;
        String errMsg = "";
        String result = "OK";
        String signId = null;
        String insertAttachmentToPciResult = null;
        boolean isValid = true;

        try
        {
            //API reference no is Order reference
            orderInfo = epcOrderHandler.getOrderSlimInfo(orderAttach.getORDER_ID());
            referenceNumber = String.valueOf(orderInfo.getOrderReference());

            //Get customer HKID
            hkid = epcCustomerHandler.getHkidByCustIdSlim(orderInfo.getCustId());

            //Convert EPC order attach type to Imaging doc type
            imgDocType = ImgDocType.fromEpcDocType(orderAttach.getATTACH_TYPE());

            saDocSource = new SaDocSource();
            //Set encrypted order attachment
            switch (orderAttach.getATTACH_TYPE())
            {
                case EpcOrderAttachType.SALES_AGREEMENT ->
                {
                    //Get sign ID for Sales Agreement
                    signId = epcSignatureHandler.getSaSignature(orderInfo.getOrderId(), false).getSignId();
                    if (StringUtils.isNotBlank(signId))
                    {
                        saDocSource.setDocSourceStr(EpcCrypto.eGet(epcGenSAHandler2.genSaHtml(orderInfo.getOrderId(), signId), "utf-8"));
                    }
                    else
                    {
                        isValid = false;
                        errMsg += "Sales Agreement Signature ID is blank. REC_ID:" + String.valueOf(orderAttach.getREC_ID());
                    }
                }
                case EpcOrderAttachType.DELIVERY_NOTE ->
                {
                    saDocSource.setDocSourceStr("");
                }
                default ->
                {
                }
            }

            saDocSource.setCaseId(referenceNumber);
            saDocSource.setSeqNo(orderAttach.getIMG_SEQ_NO());
            saDocSource.setDocTypeId(BigInteger.valueOf(imgDocType.value));
            saDocSource.setHkidBr(hkid);
            saDocSource.setCreateBy(orderInfo.getPlaceOrderSalesman());

            if (isValid)
            {
                insertAttachmentToPciResult = insertAttachmentToPci(saDocSource);
            }

            if (!"OK".equals(insertAttachmentToPciResult))
            {
                errMsg += insertAttachmentToPciResult;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            errMsg += "uploadAttachmentToPci exception error. ";
        }
        finally
        {
            if (!errMsg.isBlank())
            {
                result = errMsg;
            }
        }

        return result;
    }

    private String insertAttachmentToPci(SaDocSource saDocSource)
    {
        Connection crmFesConn = null;
        PreparedStatement crmFesPstmt = null;
        ResultSet crmFesRset = null;
        String sql = null;
        int updatedRow = 0;
        boolean isValid = true;
        String errMsg = "";
        String result = "OK";
        ArrayList<ImgDocType> docTypeList = null;

        try
        {
            if (saDocSource.getCaseId() == null)
            {
                isValid = false;
                errMsg += "SaDocSource.Case_ID is null. ";
            }
            if (saDocSource.getSeqNo() == null)
            {
                isValid = false;
                errMsg += "SaDocSource.Seq_No is null. ";
            }
            if (saDocSource.getDocTypeId() == null)
            {
                isValid = false;
                errMsg += "SaDocSource.Doc_Type_ID is null. ";
            }
            if (saDocSource.getCreateBy() == null)
            {
                isValid = false;
                errMsg += "SaDocSource.Create_By is null. ";
            }
            if (saDocSource.getDocSourceStr() == null)
            {
                isValid = false;
                errMsg += "SaDocSource.DOC_SOURCE(String) is null. ";
            }

            //Retrieve PCI Document Type list
            docTypeList = retrieveImgDocTypePci();
            if (docTypeList == null || docTypeList.isEmpty())
            {
                isValid = false;
                errMsg += "docTypeList is not defined, please check FES DB config table DOC_TYPE. ";
            }
            else
            {
                //Process only PCI document type ID list
                if (saDocSource.getDocTypeId() != null && docTypeList.stream().filter(docType -> docType.value.equals(saDocSource.getDocTypeId().intValue())).findAny().isEmpty())
                {
                    isValid = false;
                }
                else
                {
                    System.out.println("Skip insert order attachment record under table SA_DOC_SOURCE. SEQ_NO:" + saDocSource.getSeqNo());
                }
            }

            if (isValid)
            {
                crmFesConn = crmFesDataSource.getConnection();

                crmFesConn.setAutoCommit(false);

                sql = " insert into sa_doc_source (CASE_ID, SEQ_NO, DOC_TYPE_ID, CUST_NUM, SUBR_NUM, HKID_BR, IS_SMCAPPS, SOURCE_MODULE, DOC_SOURCE, PROCESS_IND, GEN_PDF_IND, CREATE_DATE, CREATE_BY, UPDATE_DATE, UPDATE_BY) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, sysdate, ?, ?, ?) ";

                crmFesPstmt = crmFesConn.prepareStatement(sql);
                crmFesPstmt.setString(1, saDocSource.getCaseId());  //CASE_ID
                crmFesPstmt.setLong(2, saDocSource.getSeqNo()); //SEQ_NO
                crmFesPstmt.setBigDecimal(3, new BigDecimal(saDocSource.getDocTypeId()));	//DOC_TYPE_ID
                crmFesPstmt.setString(4, saDocSource.getCustNum());	//CUST_NUM
                crmFesPstmt.setString(5, saDocSource.getSubrNum());	//SUBR_NUM
                crmFesPstmt.setString(6, saDocSource.getHkidBr());	//HKID_BR
                crmFesPstmt.setString(7, saDocSource.getIsSmcapps());	//IS_SMCAPPS
                crmFesPstmt.setString(8, saDocSource.getSourceModule());	//SOURCE_MODULE

                Blob docSourceBlob = crmFesConn.createBlob();
                docSourceBlob.setBytes(1, saDocSource.getDocSourceStr().getBytes());
                saDocSource.setDocSource(docSourceBlob);
                crmFesPstmt.setBlob(9, saDocSource.getDocSource()); //DOC_SOURCE

                crmFesPstmt.setString(10, saDocSource.getProcessInd());	//PROCESS_IND
                crmFesPstmt.setString(11, saDocSource.getGenPdfInd());	//GEN_PDF_IND
//                crmFesPstmt.setDate(12, saDocSource.getCreateDate());	//CREATE_DATE
                crmFesPstmt.setString(12, saDocSource.getCreateBy());	//CREATE_BY
                crmFesPstmt.setDate(13, saDocSource.getUpdateDate());	//UPDATE_DATE
                crmFesPstmt.setString(14, saDocSource.getUpdateBy());	//UPDATE_BY
//                crmFesPstmt.setClob(15, saDocSource.getDocContent());	//DOC_CONTENT, UAT only

                updatedRow = crmFesPstmt.executeUpdate();

                //Free DocSource file blob
                docSourceBlob.free();

                if (updatedRow == 1)
                {
                    System.out.println("Insert order attachment to table SA_DOC_SOURCE success. SEQ_NO:" + saDocSource.getSeqNo());
                }
                else
                {
                    errMsg += "Failed to insert order attachment record under table SA_DOC_SOURCE. SEQ_NO:" + saDocSource.getSeqNo();

                    throw new Exception("Failed to insert order attachment record under table SA_DOC_SOURCE. SEQ_NO:" + saDocSource.getSeqNo());
                }

                crmFesConn.commit();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            errMsg += "insertAttachmentToPci exception error. ";

            try
            {
                if (crmFesConn != null)
                {
                    crmFesConn.rollback();
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
        }
        finally
        {
            if (!errMsg.isBlank())
            {
                result = errMsg;
            }

            try
            {
                if (crmFesRset != null)
                {
                    crmFesRset.close();
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
            try
            {
                if (crmFesPstmt != null)
                {
                    crmFesPstmt.close();
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
            try
            {
                if (crmFesConn != null)
                {
                    crmFesConn.setAutoCommit(true);
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
            try
            {
                if (crmFesConn != null)
                {
                    crmFesConn.close();
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
        }

        return result;
    }

    private ArrayList<ImgDocType> retrieveImgDocTypePci()
    {
        ArrayList<ImgDocType> docTypeList = null;
        Connection fesConn = null;
        PreparedStatement fesPstmt = null;
        ResultSet fesRset = null;
        String sql = null;
        ImgDocType imgDocType = null;
        DocType docType = null;

        try
        {
            fesConn = fesDataSource.getConnection();

            sql = "SELECT "
                    + "    doc_type_id "
                    + "FROM "
                    + "    doc_type "
                    + "WHERE "
                    + "    pci_flag = ? ";

            fesPstmt = fesConn.prepareStatement(sql);
            fesPstmt.setString(1, "Y");  //PCI_FLAG

            fesRset = fesPstmt.executeQuery();

            docTypeList = new ArrayList<>();
            while (fesRset.next())
            {
                docType = new DocType();
                docType.setDocTypeId(BigInteger.ONE);

                imgDocType = ImgDocType.fromDocTypeId(docType.getDocTypeId().intValue());

                //Add non dummy PCI document type
                if (ImgDocType.DUMMY != imgDocType)
                {
                    docTypeList.add(imgDocType);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fesRset != null)
                {
                    fesRset.close();
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
            try
            {
                if (fesPstmt != null)
                {
                    fesPstmt.close();
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
            try
            {
                if (fesConn != null)
                {
                    fesConn.setAutoCommit(true);
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
            try
            {
                if (fesConn != null)
                {
                    fesConn.close();
                }
            }
            catch (Exception fe)
            {
                fe.printStackTrace();
            }
        }

        return docTypeList;
    }

    private InsertSimpleImgResponse uploadAttachmentToImg(InsertSimpleImgRequest insertSimpleImgRequest)
    {
        RestTemplate restTemplate = new RestTemplate();
        InsertSimpleImgResponse insertSimpleImgResponse = null;
        String logStr = "[uploadAttachment]";
        String tmpLogStr = "";
        ObjectMapper objectMapper = new ObjectMapper();
        EpcActionLog epcActionLog = new EpcActionLog();
        epcActionLog.setAction(ACTION_LOG_ACTION_INSERT_SIMPLE_IMG);
        epcActionLog.setUri("");
        String inString = "";
        String outString = "";

        try
        {
            tmpLogStr = "Order Reference:" + insertSimpleImgRequest.getReferenceNumber();
            logger.info("{}{}", logStr, tmpLogStr);

            inString = objectMapper.writeValueAsString(insertSimpleImgRequest);

            insertSimpleImgResponse = restTemplate.postForObject(IMG_CREATE_SIMPLE_API, new HttpEntity<>(insertSimpleImgRequest), InsertSimpleImgResponse.class);

            outString = objectMapper.writeValueAsString(insertSimpleImgResponse);

            tmpLogStr = "result json:" + objectMapper.writeValueAsString(insertSimpleImgResponse);
            logger.info("{}{}", logStr, tmpLogStr);

            if (insertSimpleImgResponse != null)
            {
                switch (insertSimpleImgResponse.getStatus())
                {
                    case InsertSimpleImgResponse.SUCCESS ->
                    {
                        System.out.println("Upload file to imaging success. SEQ_NO:" + insertSimpleImgResponse.getSeqNo());
                    }
                    case InsertSimpleImgResponse.FAILED ->
                    {
                        System.out.println("Upload file to imaging failed! Error Msg: " + StringUtils.defaultIfBlank(insertSimpleImgResponse.getErrMsg(), ""));
                    }
                    default ->
                    {
                    }
                }
            }
            else
            {
                System.out.println("Upload file to imaging failed! No response from Imaging system.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            outString = e.getMessage();
        }
        finally
        {
            epcActionLog.setInString(inString);
            epcActionLog.setOutString(outString);
            epcActionLogHandler.writeApiLogAsync(insertSimpleImgRequest.getOrderId(), epcActionLog);
        }

        return insertSimpleImgResponse;
    }

    public void voidAttachmentToImg(EpcDeleteDocFromImg epcDeleteDocFromImg)
    {
        UpdateAcctImgDocValidRequest updateAcctImgDocValidRequest = null;
        UpdateAcctImgDocValidResponse updateAcctImgDocValidResponse = null;
        EpcOrderAttach epcOrderAttach = null;
        ImgDocType imgDocType = null;
        String errMsg = "";

        try
        {
            //Default is success
            epcDeleteDocFromImg.setResult(EpcApiStatusReturn.RETURN_SUCCESS);

            //Retrieve order
            epcOrderAttach = epcDocumentHandler.getOrderFileByRecId(epcDeleteDocFromImg.getRecId());

            if (epcOrderAttach != null)
            {
                if (epcOrderAttach.getIMG_SEQ_NO() != null)
                {
                    //Convert EPC attachment type to Imaging document type
                    imgDocType = ImgDocType.fromEpcDocType(epcOrderAttach.getATTACH_TYPE());

                    //Mark void in Imaging
                    updateAcctImgDocValidRequest = new UpdateAcctImgDocValidRequest(epcOrderAttach.getORDER_ID(), epcOrderAttach.getIMG_SEQ_NO(), imgDocType, ImgValid.FALSE);

                    updateAcctImgDocValidResponse = updateAttachmentValid(updateAcctImgDocValidRequest);

                    if (!UpdateAcctImgDocValidResponse.SUCCESS.equals(updateAcctImgDocValidResponse.getStatus()))
                    {
                        errMsg += StringUtils.defaultIfBlank(updateAcctImgDocValidResponse.getErrMsg(), "");
                    }
                }
                else
                {
                    errMsg += "Order attachment Seq No is null. ";
                }
            }
            else
            {
                errMsg += "Order attachment cannot be found. ";
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            errMsg += "voidAttachmentToImg exception error. ";
        }
        finally
        {
            if (!errMsg.isBlank())
            {
                epcDeleteDocFromImg.setResult(EpcApiStatusReturn.RETURN_FAIL);
                epcDeleteDocFromImg.setErrMsg(errMsg);
            }
        }
    }

    public UpdateAcctImgDocValidResponse updateAttachmentValid(UpdateAcctImgDocValidRequest updateAcctImgDocValidRequest)
    {
        RestTemplate restTemplate = new RestTemplate();
        UpdateAcctImgDocValidResponse updateAcctImgDocValidResponse = null;
        String logStr = "[updateAttachmentValid]";
        String tmpLogStr = "";
        ObjectMapper objectMapper = new ObjectMapper();
        EpcActionLog epcActionLog = new EpcActionLog();
        epcActionLog.setAction(ACTION_LOG_ACTION_UPDATE_ACCT_IMG_DOC_VALID);
        epcActionLog.setUri("");
        String inString = "";
        String outString = "";

        try
        {
            tmpLogStr = "Order attachment sequence no in imaging: " + updateAcctImgDocValidRequest.getSeqNo();
            logger.info("{}{}", logStr, tmpLogStr);

            inString = objectMapper.writeValueAsString(updateAcctImgDocValidRequest);

            updateAcctImgDocValidResponse = restTemplate.postForObject(IMG_UPDATE_DOC_VALID_API, new HttpEntity<>(updateAcctImgDocValidRequest), UpdateAcctImgDocValidResponse.class);

            outString = objectMapper.writeValueAsString(updateAcctImgDocValidResponse);

            tmpLogStr = "result json:" + outString;
            logger.info("{}{}", logStr, tmpLogStr);

            if (updateAcctImgDocValidResponse != null)
            {
                if (UpdateAcctImgDocValidResponse.SUCCESS.equals(updateAcctImgDocValidResponse.getStatus()))
                {
                    System.out.println("Update document existence to imaging success. SEQ_NO:" + updateAcctImgDocValidResponse.getSeqNo());
                }
                else
                {
                    System.out.println("Update document existence to imaging error! Error Msg: " + StringUtils.defaultIfBlank(updateAcctImgDocValidResponse.getErrMsg(), ""));
                }
            }
            else
            {
                System.out.println("Update document existence to imaging error! Unable to get response from Imaging.");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            outString = e.getMessage();
        }
        finally
        {
            epcActionLog.setInString(inString);
            epcActionLog.setOutString(outString);
            epcActionLogHandler.writeApiLogAsync(updateAcctImgDocValidRequest.getOrderId(), epcActionLog);
        }

        return updateAcctImgDocValidResponse;
    }

    public String voidAttachmentToImg(Integer orderId)
    {
        String result = "OK";
        String errMsg = "";
        UpdateAcctImgDocValidRequest updateAcctImgDocValidRequest = null;
        UpdateAcctImgDocValidResponse updateAcctImgDocValidResponse = null;
        ImgDocType imgDocType = null;

        try
        {
            //Loop all attachment under the order and void Sales Agreement, Delivery Note to Imaging
            ArrayList<EpcOrderAttach> orderAttachList = epcDocumentHandler.getOrderFileListByOrderId(orderId, false);
            for (EpcOrderAttach epcOrderAttach : orderAttachList)
            {
                //Void only Sales Agreement and Delivery Note
                if (EpcOrderAttachType.SALES_AGREEMENT.equals(epcOrderAttach.getATTACH_TYPE()) || EpcOrderAttachType.DELIVERY_NOTE.equals(epcOrderAttach.getATTACH_TYPE()))
                {
                    if (epcOrderAttach.getIMG_SEQ_NO() != null)
                    {
                        //Convert EPC attachment type to Imaging document type
                        imgDocType = ImgDocType.fromEpcDocType(epcOrderAttach.getATTACH_TYPE());

                        //Mark void in Imaging
                        updateAcctImgDocValidRequest = new UpdateAcctImgDocValidRequest(epcOrderAttach.getORDER_ID(), epcOrderAttach.getIMG_SEQ_NO(), imgDocType, ImgValid.FALSE);

                        updateAcctImgDocValidResponse = updateAttachmentValid(updateAcctImgDocValidRequest);

                        if (UpdateAcctImgDocValidResponse.FAILED.equals(updateAcctImgDocValidResponse.getStatus()))
                        {
                            errMsg += updateAcctImgDocValidResponse.getErrMsg();
                        }
                    }
                    else
                    {
                        errMsg += "Failed to void order attachment to Imaging. REC_ID:" + String.valueOf(epcOrderAttach.getREC_ID());
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();

            errMsg += "voidAttachmentToImg exception error. ";
        }
        finally
        {
            if (!errMsg.isBlank())
            {
                result = errMsg;
            }
        }

        return result;
    }
}
