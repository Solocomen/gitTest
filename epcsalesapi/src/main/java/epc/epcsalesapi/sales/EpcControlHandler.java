package epc.epcsalesapi.sales;

import java.sql.*;
import java.util.ArrayList;
import javax.sql.DataSource;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import epc.epcsalesapi.sales.bean.EpcControlTbl;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.helper.sql.PstmtInputParameters;
import epc.epcsalesapi.helper.sql.InputParameter;

@Service
public class EpcControlHandler {

    @Autowired
    private DataSource epcDataSource;


    public ArrayList<EpcControlTbl> getControl(EpcControlTbl control) throws Exception {

        ArrayList<EpcControlTbl> controlList = null;
        String sql = "";

        try {

            sql = "SELECT * FROM epc_control_tbl WHERE ";
            if (!"".equals(StringHelper.trim(control.getRecType()))) sql += "rec_type = ? ";
            if (!"".equals(StringHelper.trim(control.getKeyStr1()))) sql += "AND key_str1 = ? ";
            if (!"".equals(StringHelper.trim(control.getKeyStr2()))) sql += "AND key_str2 = ? ";
            if (!"".equals(StringHelper.trim(control.getKeyStr3()))) sql += "AND key_str3 = ? ";
            if (!"".equals(StringHelper.trim(control.getKeyStr4()))) sql += "AND key_str4 = ? ";
            if (!"".equals(StringHelper.trim(control.getKeyStr5()))) sql += "AND key_str5 = ? ";
            if (control.getKeyDate1() != null) sql += "AND key_date1 = ? ";
            if (control.getKeyDate2() != null) sql += "AND key_date2 = ? ";
            if (control.getKeyDate3() != null) sql += "AND key_date3 = ? ";
            if (control.getKeyNumber1() != null) sql += "AND key_number1 = ? ";
            if (control.getKeyNumber2() != null) sql += "AND key_number2 = ? ";
            if (control.getKeyNumber3() != null) sql += "AND key_number3 = ? ";

            controlList = getResultControl(sql, control);
            
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {}

        return controlList;
    }
    
    public ArrayList<EpcControlTbl> getControl(String sql, EpcControlTbl control) throws Exception {

        ArrayList<EpcControlTbl> controlList = null;

        try {
            controlList = getResultControl(sql, control);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        }
        return controlList;
    }
    
    public ArrayList<EpcControlTbl> getControl(String sql, PstmtInputParameters parameters) throws Exception {

        ArrayList<EpcControlTbl> controlList = null;

        try {
            controlList = getResultControl(sql, parameters);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        }
        return controlList;
    }
    
    private ArrayList<EpcControlTbl> getResultControl(String sql, PstmtInputParameters parameters) throws Exception {

        ArrayList<EpcControlTbl> controlList = new ArrayList<EpcControlTbl>();
        EpcControlTbl resultControl = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        int idx = 1;

        try {
            conn = epcDataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            ArrayList<InputParameter> parameterList = parameters.getParameterList();
            for (int i=0; i < parameterList.size(); i++) {
                pstmt.setObject(idx++, parameterList.get(i).getInputObject(), parameterList.get(i).getInputSqlType());
            }

            rset = pstmt.executeQuery();
            while(rset.next()) {
                resultControl = new EpcControlTbl();
                resultControl.setRecId(rset.getBigDecimal("rec_id"));
                resultControl.setRecDesc(StringHelper.trim(rset.getString("rec_desc")));
                resultControl.setKeyStr1(StringHelper.trim(rset.getString("key_str1")));
                resultControl.setKeyStr2(StringHelper.trim(rset.getString("key_str2")));
                resultControl.setKeyStr3(StringHelper.trim(rset.getString("key_str3")));
                resultControl.setKeyStr4(StringHelper.trim(rset.getString("key_str4")));
                resultControl.setKeyStr5(StringHelper.trim(rset.getString("key_str5")));
                resultControl.setKeyDate1(rset.getObject("key_date1", LocalDateTime.class));
                resultControl.setKeyDate2(rset.getObject("key_date2", LocalDateTime.class));
                resultControl.setKeyDate3(rset.getObject("key_date3", LocalDateTime.class));
                resultControl.setKeyNumber1(rset.getBigDecimal("key_number1"));
                resultControl.setKeyNumber2(rset.getBigDecimal("key_number2"));
                resultControl.setKeyNumber3(rset.getBigDecimal("key_number3"));
                resultControl.setValueStr1(StringHelper.trim(rset.getString("value_str1")));
                resultControl.setValueStr2(StringHelper.trim(rset.getString("value_str2")));
                resultControl.setValueStr3(StringHelper.trim(rset.getString("value_str3")));
                resultControl.setValueStr4(StringHelper.trim(rset.getString("value_str4")));
                resultControl.setValueStr5(StringHelper.trim(rset.getString("value_str5")));
                resultControl.setValueDate1(rset.getObject("value_date1", LocalDateTime.class));
                resultControl.setValueDate2(rset.getObject("value_date2", LocalDateTime.class));
                resultControl.setValueDate3(rset.getObject("value_date3", LocalDateTime.class));
                resultControl.setValueDate4(rset.getObject("value_date4", LocalDateTime.class));
                resultControl.setValueDate5(rset.getObject("value_date5", LocalDateTime.class));
                resultControl.setValueNumber1(rset.getBigDecimal("value_number1"));
                resultControl.setValueNumber2(rset.getBigDecimal("value_number2"));
                resultControl.setValueNumber3(rset.getBigDecimal("value_number3"));
                resultControl.setValueNumber4(rset.getBigDecimal("value_number4"));
                resultControl.setValueNumber5(rset.getBigDecimal("value_number5"));
                controlList.add(resultControl);
            } 

        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        } finally {
             try { if(rset != null) { rset.close(); } } catch (Exception ignore) {}
             try { if(pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
             try { if(conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        return controlList;
    }

    private ArrayList<EpcControlTbl> getResultControl(String sql, EpcControlTbl control) throws Exception {

        ArrayList<EpcControlTbl> controlList = new ArrayList<EpcControlTbl>();
        EpcControlTbl resultControl = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        int idx = 1;

        try {
            conn = epcDataSource.getConnection();
            pstmt = conn.prepareStatement(sql);
            if (!"".equals(StringHelper.trim(control.getRecType()))) pstmt.setString(idx++, control.getRecType());
            if (!"".equals(StringHelper.trim(control.getKeyStr1()))) { pstmt.setString(idx++, control.getKeyStr1()); System.out.println("control.getKeyStr1();");}
            if (!"".equals(StringHelper.trim(control.getKeyStr2()))) { pstmt.setString(idx++, control.getKeyStr2()); System.out.println("control.getKeyStr2();");}
            if (!"".equals(StringHelper.trim(control.getKeyStr3()))) { pstmt.setString(idx++, control.getKeyStr3()); System.out.println("control.getKeyStr3();");}
            if (!"".equals(StringHelper.trim(control.getKeyStr4()))) { pstmt.setString(idx++, control.getKeyStr4()); System.out.println("control.getKeyStr4();");}
            if (!"".equals(StringHelper.trim(control.getKeyStr5()))) { pstmt.setString(idx++, control.getKeyStr5()); System.out.println("control.getKeyStr5();");}
            if (control.getKeyDate1() != null) {pstmt.setObject(idx++, control.getKeyDate1()); System.out.println("control.getKeyDate1();");}
            if (control.getKeyDate2() != null) {pstmt.setObject(idx++, control.getKeyDate2()); System.out.println("control.getKeyDate2();");}
            if (control.getKeyDate3() != null) {pstmt.setObject(idx++, control.getKeyDate3()); System.out.println("control.getKeyDate3();");}
            if (control.getKeyNumber1() != null) {pstmt.setBigDecimal(idx++, control.getKeyNumber1()); System.out.println("control.getKeyNumber1();");}
            if (control.getKeyNumber2() != null) {pstmt.setBigDecimal(idx++, control.getKeyNumber2()); System.out.println("control.getKeyNumber2();");}
            if (control.getKeyNumber3() != null) {pstmt.setBigDecimal(idx++, control.getKeyNumber3()); System.out.println("control.getKeyNumber3();");}

            rset = pstmt.executeQuery();
            while(rset.next()) {
                resultControl = new EpcControlTbl();
                resultControl.setRecId(rset.getBigDecimal("rec_id"));
                resultControl.setRecDesc(StringHelper.trim(rset.getString("rec_desc")));
                resultControl.setKeyStr1(StringHelper.trim(rset.getString("key_str1")));
                resultControl.setKeyStr2(StringHelper.trim(rset.getString("key_str2")));
                resultControl.setKeyStr3(StringHelper.trim(rset.getString("key_str3")));
                resultControl.setKeyStr4(StringHelper.trim(rset.getString("key_str4")));
                resultControl.setKeyStr5(StringHelper.trim(rset.getString("key_str5")));
                resultControl.setKeyDate1(rset.getObject("key_date1", LocalDateTime.class));
                resultControl.setKeyDate2(rset.getObject("key_date2", LocalDateTime.class));
                resultControl.setKeyDate3(rset.getObject("key_date3", LocalDateTime.class));
                resultControl.setKeyNumber1(rset.getBigDecimal("key_number1"));
                resultControl.setKeyNumber2(rset.getBigDecimal("key_number2"));
                resultControl.setKeyNumber3(rset.getBigDecimal("key_number3"));
                resultControl.setValueStr1(StringHelper.trim(rset.getString("value_str1")));
                resultControl.setValueStr2(StringHelper.trim(rset.getString("value_str2")));
                resultControl.setValueStr3(StringHelper.trim(rset.getString("value_str3")));
                resultControl.setValueStr4(StringHelper.trim(rset.getString("value_str4")));
                resultControl.setValueStr5(StringHelper.trim(rset.getString("value_str5")));
                resultControl.setValueDate1(rset.getObject("value_date1", LocalDateTime.class));
                resultControl.setValueDate2(rset.getObject("value_date2", LocalDateTime.class));
                resultControl.setValueDate3(rset.getObject("value_date3", LocalDateTime.class));
                resultControl.setValueDate4(rset.getObject("value_date4", LocalDateTime.class));
                resultControl.setValueDate5(rset.getObject("value_date5", LocalDateTime.class));
                resultControl.setValueNumber1(rset.getBigDecimal("value_number1"));
                resultControl.setValueNumber2(rset.getBigDecimal("value_number2"));
                resultControl.setValueNumber3(rset.getBigDecimal("value_number3"));
                resultControl.setValueNumber4(rset.getBigDecimal("value_number4"));
                resultControl.setValueNumber5(rset.getBigDecimal("value_number5"));
                controlList.add(resultControl);
            } 

        } catch (Exception e) {
            e.printStackTrace(System.out);
            throw e;
        } finally {
        	 try { if(rset != null) { rset.close(); } } catch (Exception ignore) {}
             try { if(pstmt != null) { pstmt.close(); } } catch (Exception ignore) {}
             try { if(conn != null) { conn.close(); } } catch (Exception ignore) {}
        }
        return controlList;
    }
}
