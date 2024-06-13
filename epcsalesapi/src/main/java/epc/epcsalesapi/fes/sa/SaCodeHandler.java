package epc.epcsalesapi.fes.sa;

import java.sql.*;
import java.util.ArrayList;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import epc.epcsalesapi.fes.sa.bean.ZzPservice;
import epc.epcsalesapi.helper.StringHelper;


@Service
public class SaCodeHandler {

    @Autowired
    private DataSource fesDataSource;


    public ArrayList<ZzPservice> getCodes(ArrayList<String> codes) {
        ArrayList<ZzPservice> codeList = new ArrayList<ZzPservice>();
        ZzPservice zzPservice = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String str = "";
        int idx = 1;

        try {
            conn = fesDataSource.getConnection();

            for(String code : codes) {
                if("".equals(str)) {
                    str = StringHelper.trim(code);
                    str = "?";
                } else {
                    str += ",?";
                }
            }
            sql = "select * from zz_pservice where service_code in ( ";
            sql += str;
            sql += " ) ";

            pstmt = conn.prepareStatement(sql);
            for(String code : codes) {
                pstmt.setString(idx++, StringHelper.trim(code));
            }
            rset = pstmt.executeQuery();
            while(rset.next()) {
                zzPservice = new ZzPservice();
                zzPservice.setServiceCode(StringHelper.trim(rset.getString("service_code")));
                zzPservice.setServiceDesc(StringHelper.trim(rset.getString("service_desc")));
                zzPservice.setSubInd(StringHelper.trim(rset.getString("sub_ind")));
                zzPservice.setDepositInd(StringHelper.trim(rset.getString("deposit_ind")));
                zzPservice.setStatusInd(StringHelper.trim(rset.getString("status_ind")));
                zzPservice.setJupiterInd(StringHelper.trim(rset.getString("jupiter_ind")));
                zzPservice.setPrice(rset.getBigDecimal("price"));
                zzPservice.setAmdPrice(StringHelper.trim(rset.getString("amd_price")));
                zzPservice.setReceiptSrvDesc(StringHelper.trim(rset.getString("receipt_srv_desc")));
                //zzPservice.setReceiptSrvDescChi(StringHelper.convertEncodingFromISO88591(StringHelper.trim(rset.getString("receipt_srv_desc_chi")), "UTF-8"));
                zzPservice.setReceiptSrvDescChi(StringHelper.convertEncodingFromISO88591(StringHelper.trim(rset.getString("receipt_srv_desc_chi")), "Big5"));
                zzPservice.setKioskInd(rset.getString("kiosk_ind"));
                zzPservice.setSettleShortCutKey(rset.getString("settle_short_cut_key"));
                zzPservice.setCreditCardType(rset.getString("credit_card_type"));
                codeList.add(zzPservice);
            } 
            rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return codeList;
    }
    
    public ArrayList<ZzPservice> getCodes(String code) {
        ArrayList<ZzPservice> codeList = new ArrayList<ZzPservice>();
        ZzPservice zzPservice = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String str = "";

        try {
            conn = fesDataSource.getConnection();

            sql = "select * from zz_pservice where service_code = ? ";

            pstmt = conn.prepareStatement(sql);
           
            pstmt.setString(1, StringHelper.trim(code));
            rset = pstmt.executeQuery();
            if (rset.next()) {
                zzPservice = new ZzPservice();
                zzPservice.setServiceCode(StringHelper.trim(rset.getString("service_code")));
                zzPservice.setServiceDesc(StringHelper.trim(rset.getString("service_desc")));
                zzPservice.setSubInd(StringHelper.trim(rset.getString("sub_ind")));
                zzPservice.setDepositInd(StringHelper.trim(rset.getString("deposit_ind")));
                zzPservice.setStatusInd(StringHelper.trim(rset.getString("status_ind")));
                zzPservice.setJupiterInd(StringHelper.trim(rset.getString("jupiter_ind")));
                zzPservice.setPrice(rset.getBigDecimal("price"));
                zzPservice.setAmdPrice(StringHelper.trim(rset.getString("amd_price")));
                zzPservice.setReceiptSrvDesc(StringHelper.trim(rset.getString("receipt_srv_desc")));
                //zzPservice.setReceiptSrvDescChi(StringHelper.convertEncodingFromISO88591(StringHelper.trim(rset.getString("receipt_srv_desc_chi")), "UTF-8"));
                zzPservice.setReceiptSrvDescChi(StringHelper.convertEncodingFromISO88591(StringHelper.trim(rset.getString("receipt_srv_desc_chi")), "Big5"));
                zzPservice.setKioskInd(rset.getString("kiosk_ind"));
                zzPservice.setSettleShortCutKey(rset.getString("settle_short_cut_key"));
                zzPservice.setCreditCardType(rset.getString("credit_card_type"));
                codeList.add(zzPservice);
            } 
            rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return codeList;
    }
    
    public ArrayList<ZzPservice> getAllChargeCodes() throws Exception {
        return getAllCodes("Y");
    }
    
    public ArrayList<ZzPservice> getAllPaymentCodes() throws Exception {
        return getAllCodes("N");
    }
    
    public ArrayList<ZzPservice> getAllCodes(String serviceInd) throws Exception {
        ArrayList<ZzPservice> codeList = new ArrayList<ZzPservice>();
        ZzPservice zzPservice = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";

        try {
            conn = fesDataSource.getConnection();

            sql = "SELECT * FROM zz_pservice " +
                  "WHERE service_ind = ? ";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, serviceInd);
            rset = pstmt.executeQuery();
            while(rset.next()) {
                zzPservice = new ZzPservice();
                zzPservice.setServiceCode(StringHelper.trim(rset.getString("service_code")));
                zzPservice.setServiceDesc(StringHelper.trim(rset.getString("service_desc")));
                zzPservice.setSubInd(StringHelper.trim(rset.getString("sub_ind")));
                zzPservice.setDepositInd(StringHelper.trim(rset.getString("deposit_ind")));
                zzPservice.setStatusInd(StringHelper.trim(rset.getString("status_ind")));
                zzPservice.setJupiterInd(StringHelper.trim(rset.getString("jupiter_ind")));
                zzPservice.setPrice(rset.getBigDecimal("price"));
                zzPservice.setAmdPrice(StringHelper.trim(rset.getString("amd_price")));
                zzPservice.setReceiptSrvDesc(StringHelper.trim(rset.getString("receipt_srv_desc")));
                //zzPservice.setReceiptSrvDescChi(StringHelper.trim(rset.getString("receipt_srv_desc_chi")));
                zzPservice.setReceiptSrvDescChi(StringHelper.convertEncodingFromISO88591(StringHelper.trim(rset.getString("receipt_srv_desc_chi")), "Big5"));
                
                zzPservice.setKioskInd(rset.getString("kiosk_ind"));
                zzPservice.setSettleShortCutKey(rset.getString("settle_short_cut_key"));
                zzPservice.setCreditCardType(rset.getString("credit_card_type"));
                codeList.add(zzPservice);
            } 
            rset.close(); rset = null;
            pstmt.close(); pstmt = null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return codeList;
    }
}
