/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author KerryTsang
 */
public class EpcCpqError {
    private String responseCode;
    private int httpStatus;
    private String responseText;
    private String exceptionType;
    private String resolutionText;
    private ArrayList<HashMap<String, Object>> responseBody;

    public EpcCpqError() {
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getExceptionType() {
        return exceptionType;
    }

    public void setExceptionType(String exceptionType) {
        this.exceptionType = exceptionType;
    }

    public String getResolutionText() {
        return resolutionText;
    }

    public void setResolutionText(String resolutionText) {
        this.resolutionText = resolutionText;
    }

    public ArrayList<HashMap<String, Object>> getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(ArrayList<HashMap<String, Object>> responseBody) {
        this.responseBody = responseBody;
    }


    
}
