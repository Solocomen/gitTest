package epc.epcsalesapi.fes.contract;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import org.apache.commons.lang.StringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


@Service
public class FesContractHandler {

    private final Logger logger = LoggerFactory.getLogger(FesContractHandler.class);

    @Autowired
    private DataSource fesDataSource;

    @Autowired
    private EpcSecurityHelper epcSecurityHelper;

    private static final String FESSQL = "SELECT invoice_no FROM zz_pinv_hdg a WHERE a.cellular = ? " +
            "AND a.subscriber = ? AND NOT EXISTS ( SELECT 1 FROM zz_pret_hdg b WHERE b.invoice_no = a.invoice_no ) " +
            "AND om_case_id = ?";


    public List<String> getFesDummyInvoice(String orderId, String smcCaseId, String custNum, String subrNum){
        String fesSQL = FESSQL;
        String iOmCaseId = "";
        String iCustNum = "";
        String iSubrNum = "";
        String logStr = "";
        List<String> resultList = new ArrayList<String>();


        if (orderId!=null&&StringUtils.isNotEmpty(smcCaseId) && !"null".equals(smcCaseId)){
            iOmCaseId = orderId + '@' + epcSecurityHelper.validateString(smcCaseId)  ;
        }
        if (StringUtils.isNotEmpty(custNum) && !"null".equals(custNum)){
            iCustNum = epcSecurityHelper.validateString(custNum);
        }
        if (StringUtils.isNotEmpty(subrNum) && !"null".equals(subrNum)){
            iSubrNum = epcSecurityHelper.validateString(subrNum);
        }

        try(Connection fesConn = fesDataSource.getConnection()){

            try(PreparedStatement pstmt = fesConn.prepareStatement(fesSQL)){
                pstmt.setString(1,iSubrNum);
                pstmt.setString(2,iCustNum);
                pstmt.setString(3,iOmCaseId);



                try(ResultSet rs = pstmt.executeQuery()){
                    while (rs.next()){
                        resultList.add(rs.getString("invoice_no"));
                    }
                }

            }

        }catch (Exception e){
            logger.error(String.valueOf(e));
            e.printStackTrace();
        }
        return resultList;
    }



    public void voidFesInvoice(List<String> invoiceNoList,String custNum,String subrNum,
                               String location,String createUser,int createUserId){

        final String fesSQL = "{call sa_ldmkt.void_invoice(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        String iInvoiceNo = "";
        String iCustNum = "";
        String iSubrNum = "";
        String  iLocation = "";
        String iCreateUser = "";
        Integer iCreateUserId = 0;
        Integer oStatus ;
        String oError = "";
        String logStr = "";
        Connection conn = null;

        if (StringUtils.isNotEmpty(custNum) && !"null".equals(custNum)){
            iCustNum = epcSecurityHelper.validateString(custNum);
        }
        if (StringUtils.isNotEmpty(subrNum) && !"null".equals(subrNum)){
            iSubrNum = epcSecurityHelper.validateString(subrNum);
        }
        if (StringUtils.isNotEmpty(location) && !"null".equals(location)){
            iLocation = epcSecurityHelper.validateString(location);
        }
        if (StringUtils.isNotEmpty(createUser) && !"null".equals(createUser)){
            iCreateUser = epcSecurityHelper.validateString(createUser);
        }
        if (createUserId != 0){
            iCreateUserId = createUserId ;
        }
        try{
            conn = fesDataSource.getConnection();
            conn.setAutoCommit(false);
            if (invoiceNoList.size() > 0){
                for (String s : invoiceNoList) {
                    logStr = "[voidFesInvoice]";
                    iInvoiceNo = epcSecurityHelper.validateString(s);

                    try(CallableStatement cstmt = conn.prepareCall(fesSQL)){

                        cstmt.setString(1,iCustNum);
                        cstmt.setString(2,iSubrNum);
                        cstmt.setString(3,iInvoiceNo);
                        cstmt.setString(4,iLocation);
                        cstmt.setString(5,iCreateUser);
                        cstmt.setNull(6, Types.VARCHAR);
                        cstmt.setInt(7,iCreateUserId);
                        cstmt.registerOutParameter(8, Types.VARCHAR);
                        cstmt.registerOutParameter(9, Types.INTEGER);
                        cstmt.registerOutParameter(10, Types.VARCHAR);
                        cstmt.registerOutParameter(11, Types.INTEGER);
                        cstmt.registerOutParameter(12, Types.INTEGER);
                        cstmt.registerOutParameter(13, Types.VARCHAR);

                        logStr += "[custNum: "+iCustNum+"]"+"[subrNum: "+iSubrNum+"]"+"[invoiceNo: "+iInvoiceNo+"]"+"[location: "+iLocation+"]"+"[createUser: "+iCreateUser+"]"+"[createUserId: "+iCreateUserId+"]";
                        cstmt.execute();

                        oStatus = cstmt.getInt(9);
                        oError = StringHelper.trim(cstmt.getString(10));

                        if (oStatus != 1){
                            logger.error("{}{}{}",logStr," CANNOT VOIDED ,","errMsg: "+oError);
                            throw new Exception("invoice failed");
                        }
                        logger.info("{}{}{}", logStr, " VOIDED", " oMsg: "+ oError);

                    }
                }
            }
            conn.commit();
        }catch (Exception e){
            logger.error(String.valueOf(e));
            e.printStackTrace();
            if(conn!=null)
                try {conn.rollback();} catch (Exception ig) {}
        }finally{
            if(conn!=null){
                try {conn.setAutoCommit(true);} catch (Exception ig) {}
                try {conn.close();} catch (Exception ig) {}
              }
        }



    }


}
