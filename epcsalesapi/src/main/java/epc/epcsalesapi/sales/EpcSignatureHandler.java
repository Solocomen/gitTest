package epc.epcsalesapi.sales;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.UUID;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcApiStatusReturn;
import epc.epcsalesapi.sales.bean.EpcSignature;
import epc.epcsalesapi.sales.bean.orderAttachment.EpcOrderAttachType;

@Service
public class EpcSignatureHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcSignatureHandler.class);

    private final DataSource epcDataSource;
    private final EpcSecurityHelper epcSecurityHelper;

    public EpcSignatureHandler(DataSource epcDataSource, EpcSecurityHelper epcSecurityHelper) {
        this.epcDataSource = epcDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
    }

    
    public ArrayList<EpcSignature> getSignature(int orderId) {
        ArrayList<EpcSignature> sList = new ArrayList<>();
        EpcSignature epcSignature = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            conn = epcDataSource.getConnection();
            sql = "select sign_id, order_id, content, content_type, to_char(create_date, 'yyyymmddhh24miss') as c_date, with_dn " +
                  "  from epc_order_sign " +
                  " where order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            while(rset.next()) {
                epcSignature = new EpcSignature();
                epcSignature.setSignId(StringHelper.trim(rset.getString("sign_id")));
                epcSignature.setOrderId(orderId);
                epcSignature.setContent(StringHelper.trim(rset.getString("content")));
                epcSignature.setContentType(StringHelper.trim(rset.getString("content_type")));
                epcSignature.setCreateDate(StringHelper.trim(rset.getString("c_date")));
                epcSignature.setWithDn(StringHelper.trim(rset.getString("with_dn")));

                sList.add(epcSignature);
            } rset.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(rset != null) { rset.close(); } } catch (Exception ee) {}
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return sList;
    }
    
    //Get Sales Agreement signature
    public EpcSignature getSaSignature(int orderId, boolean isRequiredContent)
    {
        EpcSignature epcSignature = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try
        {
            conn = epcDataSource.getConnection();
            sql = "select "
                    + "sign_id, "
                    + "order_id, "
                    + (isRequiredContent ? "content, " : "")
                    + "content_type, "
                    + "to_char(create_date, 'yyyymmddhh24miss') as c_date, "
                    + "with_dn "
                    + "  from epc_order_sign "
                    + " where "
                    + "order_id = ? "
                    + "order by create_date asc "
                    + "fetch first 1 rows only ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId); // order_id
            rset = pstmt.executeQuery();
            if (rset.next())
            {
                epcSignature = new EpcSignature();
                epcSignature.setSignId(StringHelper.trim(rset.getString("sign_id")));
                epcSignature.setOrderId(orderId);
                if (isRequiredContent)
                {
                    epcSignature.setContent(StringHelper.trim(rset.getString("content")));
                }
                epcSignature.setContentType(StringHelper.trim(rset.getString("content_type")));
                epcSignature.setCreateDate(StringHelper.trim(rset.getString("c_date")));
                epcSignature.setWithDn(StringHelper.trim(rset.getString("with_dn")));
            }
            rset.close();
            pstmt.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (rset != null)
                {
                    rset.close();
                }
            }
            catch (Exception ee)
            {
            }
            try
            {
                if (pstmt != null)
                {
                    pstmt.close();
                }
            }
            catch (Exception ee)
            {
            }
            try
            {
                if (conn != null)
                {
                    conn.close();
                }
            }
            catch (Exception ee)
            {
            }
        }

        return epcSignature;
    }

    public void saveSignature(EpcSignature epcSignature) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        int orderId = epcSignature.getOrderId();
        String content = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSignature.getContent()));
        String contentType = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcSignature.getContentType()));
        String signId = UUID.randomUUID().toString();

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            sql = "insert into epc_order_sign ( " +
                  "  sign_id, order_id, content, content_type, create_date " +
                  ") values ( " +
                  "  ?,?,?,?,sysdate " +
                  ") ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, signId); // sign_id
            pstmt.setInt(2, orderId); // order_id
            pstmt.setString(3, content); // content
            pstmt.setString(4, contentType); // content_type

            pstmt.executeUpdate();

            epcSignature.setSignId(signId);
            epcSignature.setResult(EpcApiStatusReturn.RETURN_SUCCESS);

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            epcSignature.setResult(EpcApiStatusReturn.RETURN_FAIL);
            epcSignature.setErrMsg(e.getMessage());
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }


    public void updateSignature(EpcSignature epcSignature) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        int orderId = epcSignature.getOrderId();
        String signId = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcSignature.getSignId()));
        String withDn = StringHelper.trim(epcSecurityHelper.encodeForSQL(epcSignature.getWithDn()));

        try {
            conn = epcDataSource.getConnection();
            conn.setAutoCommit(false);

            sql = "update epc_order_sign " +
                  "   set with_dn = ? " +
                  " where sign_id = ? " +
                  "   and order_id = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, withDn); // content
            pstmt.setString(2, signId); // sign_id
            pstmt.setInt(3, orderId); // order_id
            pstmt.executeUpdate();

            epcSignature.setSignId(signId);
            epcSignature.setResult(EpcApiStatusReturn.RETURN_SUCCESS);

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();

            try { if(conn != null) { conn.rollback(); } } catch (Exception ee) {}

            epcSignature.setResult(EpcApiStatusReturn.RETURN_FAIL);
            epcSignature.setErrMsg(e.getMessage());
        } finally {
            try { if(pstmt != null) { pstmt.close(); } } catch (Exception ee) {}
            try { if(conn != null) { conn.setAutoCommit(true); } } catch (Exception ee) {}
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }
}
