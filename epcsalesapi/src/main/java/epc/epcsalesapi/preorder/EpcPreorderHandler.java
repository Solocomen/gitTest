package epc.epcsalesapi.preorder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import epc.epcsalesapi.fes.user.FesUser;
import epc.epcsalesapi.fes.user.FesUserHandler;
import epc.epcsalesapi.helper.EpcProperty;
import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.preorder.bean.EpcPreorder;
import epc.epcsalesapi.preorder.bean.EpcPreorderUpdate;
import epc.epcsalesapi.preorder.bean.EpcPreorderUpdateResult;
import epc.epcsalesapi.sales.EpcOrderAttrHandler;
import epc.epcsalesapi.sales.bean.EpcAddOrderAttrs;
import epc.epcsalesapi.sales.bean.EpcOrderAttr;


@Service
public class EpcPreorderHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcPreorderHandler.class);

    private final String PREORDER_UPDATE_ACTION_ISSUE_INVOICE = "ISSUE_INVOICE";
    private final String PREORDER_UPDATE_ACTION_VOID_INVOICE = "VOID_INVOICE";

    private EpcOrderAttrHandler epcOrderAttrHandler;
    private EpcSecurityHelper epcSecurityHelper;
    private FesUserHandler fesUserHandler;
    private DataSource fesDataSource;


    public EpcPreorderHandler(EpcOrderAttrHandler epcOrderAttrHandler, EpcSecurityHelper epcSecurityHelper,
            FesUserHandler fesUserHandler, DataSource fesDataSource) {
        this.epcOrderAttrHandler = epcOrderAttrHandler;
        this.epcSecurityHelper = epcSecurityHelper;
        this.fesUserHandler = fesUserHandler;
        this.fesDataSource = fesDataSource;
    }


    public void consumePreorderRecord(EpcPreorder epcPreorder) {
        int orderId = epcPreorder.getOrderId();
        int preorderCaseId = epcPreorder.getPreorderCaseId();
        String invoiceNo = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getInvoiceNo()));
        String caseId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getCaseId()));
        String itemId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getItemId()));
        String createUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getCreateUser()));
        String createSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getCreateSalesman()));
        String createChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getCreateChannel()));
        String createLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getCreateLocation()));
        String dummyInvoiceNo = "" + orderId;
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_PREORDER_UPDATE_LINK");
        EpcPreorderUpdate epcPreorderUpdate = null;
        EpcPreorderUpdateResult epcPreorderUpdateResult = null;
        FesUser fesUser = null;
        FesUser fesSalesman = null;
        EpcAddOrderAttrs epcAddOrderAttrs = null;
        EpcOrderAttr epcOrderAttr = null;
        ArrayList<EpcOrderAttr> attrList = null;
        boolean isValid = true;
        StringBuilder errMsgSB = new StringBuilder(1 * 1024); // default 1M
        String logStr = "[consumePreorderRecord][orderId:" + orderId + "][caseId:" + caseId + "][itemId:" + itemId + "] ";
        String tmpLogStr = "";

        try {
            // basic checking
            fesUser = fesUserHandler.getUserByUsername(createUser);
            if("".equals(fesUser.getUsername())) {
                isValid = false;
                errMsgSB.append(createUser + " is not found in FES. ");
            }

            fesSalesman = fesUserHandler.getUserByUsername(createSalesman);
            if("".equals(fesSalesman.getUsername())) {
                isValid = false;
                errMsgSB.append(fesSalesman + " is not found in FES. ");
            }
            // end of basic checking

            if(isValid) {
                if("".equals(invoiceNo)) {
logger.info("{}{}", logStr, "use dummy invoice no (from UI)");
                    invoiceNo = dummyInvoiceNo;
                }

                tmpLogStr = "invoiceNo:" + invoiceNo;
logger.info("{}{}", logStr, tmpLogStr);

                epcPreorderUpdate = new EpcPreorderUpdate();
                epcPreorderUpdate.setAction(PREORDER_UPDATE_ACTION_ISSUE_INVOICE);
                epcPreorderUpdate.setCaseId(preorderCaseId);
                epcPreorderUpdate.setInvoiceNo(invoiceNo);
                epcPreorderUpdate.setReceiptNo("");
                epcPreorderUpdate.setUpdateUser(fesUser.getUserid());
                epcPreorderUpdate.setUpdateSalesman(fesSalesman.getUserid());
                epcPreorderUpdate.setInvoiceHKID("");

                tmpLogStr = "request json:" + objectMapper.writeValueAsString(epcPreorderUpdate);
logger.info("{}{}", logStr, tmpLogStr);

                epcPreorderUpdateResult = restTemplate.postForObject(apiUrl, new HttpEntity<>(epcPreorderUpdate), EpcPreorderUpdateResult.class);

                tmpLogStr = "result json:" + objectMapper.writeValueAsString(epcPreorderUpdateResult);
logger.info("{}{}", logStr, tmpLogStr);

                if(epcPreorderUpdateResult.getResult() == 0) {
                    tmpLogStr = " update success ";
logger.info("{}{}", logStr, tmpLogStr);

                    // write order attr
                    epcAddOrderAttrs = new EpcAddOrderAttrs();
                    attrList = new ArrayList<>();
                    epcAddOrderAttrs.setAttrs(attrList);

                    epcAddOrderAttrs.setOrderId(orderId);

                    epcOrderAttr = new EpcOrderAttr();
                    epcOrderAttr.setOrderId(orderId);
                    epcOrderAttr.setCaseId(caseId);
                    epcOrderAttr.setItemId(itemId);
                    epcOrderAttr.setAttrType(epcOrderAttrHandler.ATTR_TYPE_PREORDER_CASE_ID); // preorder case id
                    epcOrderAttr.setAttrValue("" + preorderCaseId);
                    attrList.add(epcOrderAttr);

                    epcOrderAttr = new EpcOrderAttr();
                    epcOrderAttr.setOrderId(orderId);
                    epcOrderAttr.setCaseId(caseId);
                    epcOrderAttr.setItemId(itemId);
                    epcOrderAttr.setAttrType(epcOrderAttrHandler.ATTR_TYPE_PREORDER_INV_NO); // invoice no
                    epcOrderAttr.setAttrValue(invoiceNo);
                    attrList.add(epcOrderAttr);

                    epcOrderAttrHandler.addAttrs(epcAddOrderAttrs);
                    // end of write order attr

                    epcPreorder.setResult("SUCCESS");
                } else {
                    tmpLogStr = " update FAIL ";
logger.info("{}{}", logStr, tmpLogStr);

                    epcPreorder.setResult("FAIL");
                    epcPreorder.setErrMsg(epcPreorderUpdateResult.getErrorMsg());
                }
            } else {
                epcPreorder.setResult("FAIL");
                epcPreorder.setErrMsg(errMsgSB.toString());
            }
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();

            epcPreorder.setResult("FAIL");
            epcPreorder.setErrMsg(hsce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();

            epcPreorder.setResult("FAIL");
            epcPreorder.setErrMsg(e.getMessage());
        }
    }


    public void resumePreorderRecord(EpcPreorder epcPreorder) {
        int orderId = epcPreorder.getOrderId();
        String preorderCaseIdStr = "";
        int preorderCaseId = 0;
        String invoiceNo = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getInvoiceNo()));
        String caseId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getCaseId()));
        String itemId = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getItemId()));
        String createUser = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getCreateUser()));
        String createSalesman = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getCreateSalesman()));
        String createChannel = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getCreateChannel()));
        String createLocation = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPreorder.getCreateLocation()));
        String dummyInvoiceNo = "" + orderId;
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper();
        String apiUrl = EpcProperty.getValue("EPC_PREORDER_UPDATE_LINK");
        EpcPreorderUpdate epcPreorderUpdate = null;
        EpcPreorderUpdateResult epcPreorderUpdateResult = null;
        FesUser fesUser = null;
        FesUser fesSalesman = null;
        boolean isValid = true;
        StringBuilder errMsgSB = new StringBuilder(1 * 1024); // default 1M
        String logStr = "[resumePreorderRecord][orderId:" + orderId + "][caseId:" + caseId + "][itemId:" + itemId + "] ";
        String tmpLogStr = "";

        try {
            // basic checking
            fesUser = fesUserHandler.getUserByUsername(createUser);
            if("".equals(fesUser.getUsername())) {
                isValid = false;
                errMsgSB.append(createUser + " is not found in FES. ");
            }

            fesSalesman = fesUserHandler.getUserByUsername(createSalesman);
            if("".equals(fesSalesman.getUsername())) {
                isValid = false;
                errMsgSB.append(fesSalesman + " is not found in FES. ");
            }

            // get order attr
            preorderCaseIdStr = epcOrderAttrHandler.getAttrValue(orderId, caseId, itemId, epcOrderAttrHandler.ATTR_TYPE_PREORDER_CASE_ID); // preorder case id
            if("".equals(preorderCaseIdStr)) {
                isValid = false;
                errMsgSB.append("preorder case id is not found. ");
            } else {
                try {
                    preorderCaseId = Integer.parseInt(preorderCaseIdStr);
                } catch (Exception e) {
                }
            }
            // end of get order attr

            // end of basic checking


            if(isValid) {
                if("".equals(invoiceNo)) {
logger.info("{}{}", logStr, "use dummy invoice no (from UI)");
                    invoiceNo = dummyInvoiceNo;
                }

                tmpLogStr = "invoiceNo:" + invoiceNo;
logger.info("{}{}", logStr, tmpLogStr);

                epcPreorderUpdate = new EpcPreorderUpdate();
                epcPreorderUpdate.setAction(PREORDER_UPDATE_ACTION_VOID_INVOICE);
                epcPreorderUpdate.setCaseId(preorderCaseId);
                epcPreorderUpdate.setInvoiceNo(invoiceNo);
                epcPreorderUpdate.setReceiptNo("");
                epcPreorderUpdate.setUpdateUser(fesUser.getUserid());
                epcPreorderUpdate.setUpdateSalesman(fesSalesman.getUserid());
                epcPreorderUpdate.setInvoiceHKID("");

                tmpLogStr = "request json:" + objectMapper.writeValueAsString(epcPreorderUpdate);
logger.info("{}{}", logStr, tmpLogStr);

                epcPreorderUpdateResult = restTemplate.postForObject(apiUrl, new HttpEntity<>(epcPreorderUpdate), EpcPreorderUpdateResult.class);

                tmpLogStr = "result json:" + objectMapper.writeValueAsString(epcPreorderUpdateResult);
logger.info("{}{}", logStr, tmpLogStr);

                if(epcPreorderUpdateResult.getResult() == 0) {
                    tmpLogStr = " update success ";
logger.info("{}{}", logStr, tmpLogStr);

                    // obsolete order attr
                    epcOrderAttrHandler.obsoleteAttr(orderId, caseId, itemId, epcOrderAttrHandler.ATTR_TYPE_PREORDER_CASE_ID); // preorder case id
                    epcOrderAttrHandler.obsoleteAttr(orderId, caseId, itemId, epcOrderAttrHandler.ATTR_TYPE_PREORDER_INV_NO); // invoice no
                    // end of obsolete order attr

                    epcPreorder.setResult("SUCCESS");
                } else {
                    tmpLogStr = " update FAIL ";
logger.info("{}{}", logStr, tmpLogStr);

                    epcPreorder.setResult("FAIL");
                    epcPreorder.setErrMsg(epcPreorderUpdateResult.getErrorMsg());
                }
            } else {
                epcPreorder.setResult("FAIL");
                epcPreorder.setErrMsg(errMsgSB.toString());
            }
        } catch(HttpStatusCodeException hsce) {
            hsce.printStackTrace();

            epcPreorder.setResult("FAIL");
            epcPreorder.setErrMsg(hsce.getMessage());
        } catch (Exception e) {
            e.printStackTrace();

            epcPreorder.setResult("FAIL");
            epcPreorder.setErrMsg(e.getMessage());
        }
    }


    public String isPreOrdering(String productCode) {
        Connection fesConn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String isPreOrder = "N";

        try {
            fesConn = fesDataSource.getConnection();
            sql = "select porder_type from stockm a where warehouse like 'A%' and product = ? ";
            pstmt = fesConn.prepareStatement(sql);
            pstmt.setString(1, productCode); // product
            rset = pstmt.executeQuery();
            if(rset.next()) {
                isPreOrder = StringHelper.trim(rset.getString("porder_type"));
            } rset.close();
            pstmt.close();

            if("".equals(isPreOrder)) {
                isPreOrder = "N";
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception ee) {}
        }
        return isPreOrder;
    }
}
