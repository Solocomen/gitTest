package epc.epcsalesapi.fes.receipt;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;

@Service
public class FesReceiptHandler {
    private final Logger logger = LoggerFactory.getLogger(FesReceiptHandler.class);

    private final DataSource fesDataSource;
    private final EpcSecurityHelper epcSecurityHelper;

    public FesReceiptHandler(DataSource fesDataSource, EpcSecurityHelper epcSecurityHelper) {
        this.fesDataSource = fesDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
    }


    public void voidReceipt(FesVoidReceipt fesVoidReceipt) {
        Connection fesConn = null;
        CallableStatement cstmt = null;
        String sql = "";
        String receiptNo = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getReceiptNo()));
        String location = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getLocation()));
        String voidCode1 = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getVoidCode1()));
        String correctInfo1 = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getCorrectInfo1()));
        String voidCode2 = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getVoidCode2()));
        String correctInfo2 = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getCorrectInfo2()));
        String voidCode3 = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getVoidCode3()));
        String correctInfo3 = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getCorrectInfo3()));
        String reissued = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getReissued()));
        String refNo = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getRefNo()));
        String noriCode = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getNoriCode()));
        String noriDesc = epcSecurityHelper.encodeForSQL(StringHelper.trim(fesVoidReceipt.getNoriDesc()));
        int userId = fesVoidReceipt.getUserId();
        int salesmanId = fesVoidReceipt.getSalesmanId();
        int approverUserid = fesVoidReceipt.getApproverUserid();
        String voidRef = "";

        try {
            fesConn = fesDataSource.getConnection();
            fesConn.setAutoCommit(false);

            sql = " {? = call sa_settlement.void_receipt(?,?,?,?,?, ?,?,?,?,?, ?,?,?,?,?) } ";
            cstmt = fesConn.prepareCall(sql);

            cstmt.registerOutParameter(1, Types.VARCHAR); // output
            
            cstmt.setString(2, receiptNo); // i_receipt_no
            cstmt.setString(3, location);//i_bin_number
            cstmt.setString(4, voidCode1);//i_void_code1
            cstmt.setString(5, correctInfo1);//i_correct_info1
            cstmt.setString(6, voidCode2);//i_void_code2
            cstmt.setString(7, correctInfo2);//i_correct_info2
            cstmt.setString(8, voidCode3);//i_void_code3
            cstmt.setString(9, correctInfo3);//i_correct_info3
            cstmt.setString(10, reissued);//i_reissued
            cstmt.setString(11, refNo);//i_ref_no
            cstmt.setString(12, noriCode);//i_nori_code
            cstmt.setString(13, noriDesc);//i_nori_desc 
            cstmt.setInt(14, userId);//i_pos_userid
            cstmt.setInt(15, salesmanId);//i_fes_userid
            cstmt.setInt(16, approverUserid);//i_approve_userid

            cstmt.execute();

            voidRef = StringHelper.trim(cstmt.getString(1));

            fesConn.commit();

            fesVoidReceipt.setResult(EpcApiStatusReturn.RETURN_SUCCESS);
            fesVoidReceipt.setVoidRef(voidRef);
        } catch (Exception e) {
            e.printStackTrace();

            try { if(fesConn != null) { fesConn.rollback(); } } catch (Exception ee) {}

            fesVoidReceipt.setResult(EpcApiStatusReturn.RETURN_FAIL);
            fesVoidReceipt.setErrMsg(e.getMessage());
        } finally {
            try { if(fesConn != null) { fesConn.setAutoCommit(true); } } catch (Exception e) {}
            try { if(fesConn != null) { fesConn.close(); } } catch (Exception e) {}
        }
    }
}
