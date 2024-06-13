package epc.epcsalesapi.sales;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import epc.epcsalesapi.helper.EpcSecurityHelper;
import epc.epcsalesapi.helper.StringHelper;
import epc.epcsalesapi.sales.bean.EpcPrintTransferNotes;

@Service
public class EpcPrintingHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcPrintingHandler.class);

    private final DataSource epcDataSource;
    private final DataSource fesDataSource;
    private final EpcSecurityHelper epcSecurityHelper;
    
    public EpcPrintingHandler(DataSource epcDataSource, DataSource fesDataSource, EpcSecurityHelper epcSecurityHelper) {
        this.epcDataSource = epcDataSource;
        this.fesDataSource = fesDataSource;
        this.epcSecurityHelper = epcSecurityHelper;
    }


    public ArrayList<String> getDefaultPrintQueue(String location) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String sql = "";
        String tmpPrintQueue = "";
        ArrayList<String> printQueues = new ArrayList<>();

        try {
            conn = fesDataSource.getConnection();

            sql = "select ptr01, ptr02, ptr03, ptr04, ptr05, " +
                  "       ptr06, ptr07, ptr08, ptr09, ptr10 " +
                  "  from zz_binmas " +
                  " where bin_number = ? ";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, location);
            rset = pstmt.executeQuery();
            if(rset.next()) {
                tmpPrintQueue = StringHelper.trim(rset.getString("ptr01"));
                if(!"".equals(tmpPrintQueue)) {
                    printQueues.add(tmpPrintQueue);
                }

                tmpPrintQueue = StringHelper.trim(rset.getString("ptr02"));
                if(!"".equals(tmpPrintQueue)) {
                    printQueues.add(tmpPrintQueue);
                }

                tmpPrintQueue = StringHelper.trim(rset.getString("ptr03"));
                if(!"".equals(tmpPrintQueue)) {
                    printQueues.add(tmpPrintQueue);
                }

                tmpPrintQueue = StringHelper.trim(rset.getString("ptr04"));
                if(!"".equals(tmpPrintQueue)) {
                    printQueues.add(tmpPrintQueue);
                }

                tmpPrintQueue = StringHelper.trim(rset.getString("ptr05"));
                if(!"".equals(tmpPrintQueue)) {
                    printQueues.add(tmpPrintQueue);
                }

                tmpPrintQueue = StringHelper.trim(rset.getString("ptr06"));
                if(!"".equals(tmpPrintQueue)) {
                    printQueues.add(tmpPrintQueue);
                }

                tmpPrintQueue = StringHelper.trim(rset.getString("ptr07"));
                if(!"".equals(tmpPrintQueue)) {
                    printQueues.add(tmpPrintQueue);
                }

                tmpPrintQueue = StringHelper.trim(rset.getString("ptr08"));
                if(!"".equals(tmpPrintQueue)) {
                    printQueues.add(tmpPrintQueue);
                }

                tmpPrintQueue = StringHelper.trim(rset.getString("ptr09"));
                if(!"".equals(tmpPrintQueue)) {
                    printQueues.add(tmpPrintQueue);
                }

                tmpPrintQueue = StringHelper.trim(rset.getString("ptr10"));
                if(!"".equals(tmpPrintQueue)) {
                    printQueues.add(tmpPrintQueue);
                }
            } rset.close();
            pstmt.close();
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }

        return printQueues;
    }

    
    public void printTransferNotes(EpcPrintTransferNotes epcPrintTransferNotes) {
        Connection conn = null;
        CallableStatement cstmt = null;
        String sql = "";
        ArrayList<String> transferNotesList = epcPrintTransferNotes.getTransferNotes();
        String refNoFrom = "";
        String refNoTo = "";
        String dateFrom = "20000101";
        String dateTo = "20991231";
        String locationFrom = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPrintTransferNotes.getLocationFrom()));
        String locationTo = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPrintTransferNotes.getLocationTo()));
        String printQueue = epcSecurityHelper.encodeForSQL(StringHelper.trim(epcPrintTransferNotes.getPrintQueue()));
        String reprint = "N";
        int rtn = 99;

        try {
            conn = fesDataSource.getConnection();

            // sort transfer notes
            Collections.sort(transferNotesList);
            refNoFrom = transferNotesList.get(0);
            refNoTo = transferNotesList.get(transferNotesList.size() - 1);
            // end of sort transfer notes
            
//            // RetVal := reprint_interloc.pos_rpnt_iloc ( i_ref_fm, i_ref_to, i_date_fm, i_date_to, i_bin_fm, i_bin_to, i_printer, i_reprint );
//            sql = " { ? = call reprint_interloc.pos_rpnt_iloc(?,?,to_date(?,'yyyymmdd'),to_date(?,'yyyymmdd'),?,?,?,?) } ";
//            cstmt = conn.prepareCall(sql);
//            cstmt.registerOutParameter(1, Types.INTEGER);
//            cstmt.setString(2, refNoFrom); // i_ref_fm
//            cstmt.setString(3, refNoTo); // i_ref_to
//            cstmt.setString(4, dateFrom); // i_date_fm
//            cstmt.setString(5, dateTo); // i_date_to
//            cstmt.setString(6, locationFrom); // i_bin_fm
//            cstmt.setString(7, locationTo); // i_bin_to
//            cstmt.setString(8, printQueue); // i_printer
//            cstmt.setString(9, reprint); // i_reprint

            // RetVal := reprint_intershop.pos_rpnt_xfr ( i_ref_fm, i_ref_to, i_date_fm, i_date_to, i_bin_fm, i_bin_to, i_printer, i_reprint, i_txf_outrp_ind );
            sql = " { ? = call reprint_intershop.pos_rpnt_xfr(?,?,to_date(?,'yyyymmdd'),to_date(?,'yyyymmdd'),?,?,?,?,?) } ";
            cstmt = conn.prepareCall(sql);
            cstmt.registerOutParameter(1, Types.INTEGER);
            cstmt.setString(2, refNoFrom); // i_ref_fm
            cstmt.setString(3, refNoTo); // i_ref_to
            cstmt.setString(4, dateFrom); // i_date_fm
            cstmt.setString(5, dateTo); // i_date_to
            cstmt.setString(6, locationFrom); // i_bin_fm
            cstmt.setString(7, locationTo); // i_bin_to
            cstmt.setString(8, printQueue); // i_printer
            cstmt.setString(9, reprint); // i_reprint
            cstmt.setString(10, "Y"); // i_txf_outrp_ind

            cstmt.execute();
            rtn = cstmt.getInt(1);
            cstmt.close();

            if(rtn == 0) {
                // success
                epcPrintTransferNotes.setResult("SUCCESS");
            } else {
                // fail
                epcPrintTransferNotes.setResult("FAIL");
                epcPrintTransferNotes.setErrMsg("Fail to print transfer note");
            }
        } catch(Exception e) {
            e.printStackTrace();

            epcPrintTransferNotes.setResult("FAIL");
            epcPrintTransferNotes.setErrMsg(e.getMessage());
        } finally {
            try { if(conn != null) { conn.close(); } } catch (Exception ee) {}
        }
    }
}
