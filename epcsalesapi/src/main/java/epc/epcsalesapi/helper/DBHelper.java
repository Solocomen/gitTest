/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.helper.DBHelper
 * @author	TedKwan
 * @date	23-Sep-2022
 * Description:
 *
 * History:
 * 20220923-TedKwan: Created
 */
package epc.epcsalesapi.helper;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DBHelper {
	
	public static void closeAll(Object... input) {
		if(input !=null && input.length>0) {
			for(Object obj : input) {
				if(obj instanceof Connection) {
					try {((Connection) obj).close();} catch (SQLException ignore) {}
				}
				if(obj instanceof PreparedStatement) {
					try {((PreparedStatement) obj).close();} catch (SQLException ignore) {}
				}
				if(obj instanceof ResultSet) {
					try {((ResultSet) obj).close();} catch (SQLException ignore) {}
				}
			}
		}
	}
	
	public static String joinArrToSQLStr(ArrayList<String> input) {
		return input.stream().map(str -> "'"+str+"'").collect(Collectors.joining(","));
	}
	
    public static Long getLong(ResultSet rs, String strColName) throws SQLException
    {
        Long nValue = rs.getLong(strColName);
        return rs.wasNull() ? null : nValue;
    }
}
