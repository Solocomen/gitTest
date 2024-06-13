package epc.epcsalesapi.helper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types;
import java.util.concurrent.CompletableFuture;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.jayway.jsonpath.JsonPath;

import epc.epcsalesapi.helper.bean.EpcActionLog;

@Service
public class EpcActionLogHandler {
    private final Logger logger = LoggerFactory.getLogger(EpcActionLogHandler.class);

    private final DataSource epcDataSource;

    public EpcActionLogHandler(DataSource epcDataSource) {
        this.epcDataSource = epcDataSource;
    }


    public void writeApiLogAsync(EpcActionLog epcActionLog) {
        try {
            CompletableFuture.completedFuture(epcActionLog).thenApplyAsync(s -> writeApiLog(s));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String writeApiLog(EpcActionLog epcActionLog) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        String action = StringHelper.trim(epcActionLog.getAction());
        String uri = StringHelper.trim(epcActionLog.getUri());
        String inString = StringHelper.trim(epcActionLog.getInString());
        String outString = StringHelper.trim(epcActionLog.getOutString());
        String jsonPath = "$.orderId";
        Object orderIdObj = null;
        Integer orderId = null;
        String createTimestamp = new java.util.Date().getTime() + "";
        String logStr = "[writeApiLog] ";
        String tmpLogStr = "";

        try {
            conn = epcDataSource.getConnection();

            sql = "insert into epc_action_log ( " +
                  "  order_id, action, uri, in_message, out_message, " +
                  "  create_date, create_timestamp, log_mth " +
                  ") values ( " +
                  "  ?,?,?,?,?, " +
                  "  sysdate,?,to_char(sysdate,'MM') " +
                  ") ";
            pstmt = conn.prepareStatement(sql);

            // get order id
            if(!"".equals(inString)) {
                try {
                    orderIdObj = JsonPath.read(inString, jsonPath);
                    if(orderIdObj instanceof Integer) {
                        orderId = (Integer)orderIdObj;
                    } else if(orderIdObj instanceof String) {
                        orderId = Integer.valueOf((String)orderIdObj);
                    }
                } catch(Exception ee) {
                    tmpLogStr = ee.getMessage() + ". " + inString;
logger.info("{}{}", logStr, tmpLogStr);
                }
            }
            // end of get order id

            if(orderId != null) {
                pstmt.setInt(1, orderId.intValue()); // order_id
            } else {
                pstmt.setNull(1, Types.NUMERIC); // order_id
            }
            pstmt.setString(2, action); // action
            pstmt.setString(3, uri); // uri
            pstmt.setString(4, inString); // in_message
            pstmt.setString(5, outString); // out_message
            pstmt.setString(6, createTimestamp); // create_timestamp

            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) { conn.close(); } } catch (Exception e) {}
        }

        return "OK";
    }


    public void writeApiLogAsync(int orderId, EpcActionLog epcActionLog) {
        try {
            CompletableFuture.completedFuture(epcActionLog).thenApplyAsync(s -> writeApiLog(orderId, s));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String writeApiLog(int orderId, EpcActionLog epcActionLog) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        String sql = "";
        String action = StringHelper.trim(epcActionLog.getAction());
        String uri = StringHelper.trim(epcActionLog.getUri());
        String inString = StringHelper.trim(epcActionLog.getInString());
        String outString = StringHelper.trim(epcActionLog.getOutString());
        String createTimestamp = new java.util.Date().getTime() + "";
        String logStr = "[writeApiLog] ";
        String tmpLogStr = "";

        try {
            conn = epcDataSource.getConnection();

            sql = "insert into epc_action_log ( " +
                  "  order_id, action, uri, in_message, out_message, " +
                  "  create_date, create_timestamp, log_mth " +
                  ") values ( " +
                  "  ?,?,?,?,?, " +
                  "  sysdate,?,to_char(sysdate,'MM') " +
                  ") ";
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, orderId); // order_id
            pstmt.setString(2, action); // action
            pstmt.setString(3, uri); // uri
            pstmt.setString(4, inString); // in_message
            pstmt.setString(5, outString); // out_message
            pstmt.setString(6, createTimestamp); // create_timestamp

            pstmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (conn != null) { conn.close(); } } catch (Exception e) {}
        }

        return "OK";
    }
}
