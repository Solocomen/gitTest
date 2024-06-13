/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.helper.DateHelper
 * @author	TedKwan
 * @date	20-Jul-2022
 * Description:
 *
 * History:
 * 20220720-TedKwan: Created
 */
package epc.epcsalesapi.helper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateHelper {
	public final static String DT_FMT_YYYYMMDD="yyyy-MM-dd"; //e.g. 2019-01-23
	public final static String DT_FMT_DDMMYYYY="dd-MM-yyyy"; //e.g. 23-01-2019
	public final static String DT_FMT_FULL="yyyy-MM-dd HH:mm:ss.SSS"; //e.g. 2019-01-23 14:23:02.123
	public final static String DT_FMT_FULL_TRIM="yyyyMMddHHmmssSSS"; //e.g. 20190123142302123
	public final static String DT_FMT_CORP_DATE="dd MMMM yyyy"; //e.g. 23 January 2019
	public final static String DT_FMT_DN_SIGN_DATE="dd/MM/yyyy HH:mm"; //e.g. 20/09/2023 15:17
	public final static String DT_FMT_AR_GEN_DATE="dd MMM yyyy"; //e.g. 20 Nov 2023
	
	/** Method: getCurrentDateTime([Optional] String dtFmt)
	 *  Return: String
	 *  Description: Return current date time. Input dtFmt string must be a SimpleDateFormat accepted string.
	 *               Default yyyy-MM-dd.
	 * @param dtFmt String format, default dd-MM-yyyy
	 * @return String
	 */
	static public String getCurrentDateTime(String... dtFmt) {
		Date currentTime=new Date();
		return formatDateTime(currentTime, dtFmt);
	}
	
	/** Method: formatDateTime(Date dt, [Optional] String dtFmt)
	 *  Return: String
	 *  Description: Format input date. Input dtFmt string must be a SimpleDateFormat accepted string.
	 *               Default yyyy-MM-dd.
	 * @param dtFmt String format, default dd-MM-yyyy
	 * @return String
	 */
	static public String formatDateTime(Date dt, String... dtFmt) {
		String parseFmt = DT_FMT_YYYYMMDD;
		
		if(dtFmt != null && dtFmt.length>0) {
			parseFmt=dtFmt[0];
		}
		
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(parseFmt);
			return sdf.format(dt);
		} catch (Exception e) {
			e.printStackTrace();
			throw(e);
		}
		
	}
}
