/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.helper;

//import java.io.*;
//import java.util.*;

/**
 *
 * @author KerryTsang
 */

public class EpcProperty {
//    private static String location;
//    private static Properties propertyMap;
//    
//    static {
//        try {
//            propertyMap = new Properties();
//            location = getAppEnv();
//// hardcoded as UAT for testing, kerrytsang, 20200525
//            location = "UAT";
//// end of hardcoded as UAT for testing, kerrytsang, 20200525
//
//            String file = "";
//            if("PROD".equals(location)) {
//                file = "epc.prod.properties";
//            } else if("STAGE".equals(location)) {
//                file = "epc.stage.properties";
//            } else {
//                file = "epc.uat.properties";
//            }
//
//            InputStream is = EpcProperty.class.getClassLoader().getResourceAsStream(file);
//            propertyMap.load(is);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    
    public static String getAppEnv() {
//        String appEnv = "";
//        try {
//            appEnv = (String) System.getProperty("appenv");
//        } catch (Exception e) {
//            e.printStackTrace(System.out);
//        }
//        return appEnv;
    	return StringHelper.trim(System.getenv("APPENV"));
    }
    
    public static String getValue(String name) {
//        return (String)propertyMap.get(name);
        return StringHelper.trim(System.getenv(name));
    }

    public static String getTlsVersion() {
        return "TLSv1.2";
    }
}
