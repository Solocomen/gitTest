/* EpcInvoicingEAppleCareBean.java (Apple Care enhancement): created by Danny Chan on 2021-6-25 */
package epc.epcsalesapi.sales.bean;

import java.io.*;
import java.lang.reflect.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.util.Date;
//import javax.servlet.*;
//import javax.servlet.http.*;

public class EpcInvoicingEAppleCareBean implements Serializable {
    String subrNum;
    String custNum;
    String custFirstName;
    String custLastName;
    String emailAddress;
    String emailAddressPrefix;
    String emailAddressSuffix;
    String pocDeliveryPreference;
    String pocLanguage;
// Added by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection
    List<EpcProductListBean> iDeviceProductList;
// End added by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection 
// Modified by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection 
//     String[] iphoneImei;
//     String[] iphoneImeiProductCode;
//     String[] iphoneImeiDescription;
// End modified by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection 
    String selectedIphoneImei;
    String selectedIphoneImeiProductCode;
    char purchaseMode;
    Date estimatedPurchaseDate;
    boolean isOutage;
    
    public EpcInvoicingEAppleCareBean() {
        subrNum = "";
        custNum = "";
        custFirstName = "";
        custLastName = "";
        emailAddress = "";
        emailAddressPrefix = "";
        emailAddressSuffix = "";
        pocDeliveryPreference = "";
        pocLanguage = "";        
// Added by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection
        iDeviceProductList = null;
// End added by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection
// Modified by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection 
//         iphoneImei = null;
//         iphoneImeiProductCode = null;
//         iphoneImeiDescription = null;
// End modified by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection 
        selectedIphoneImei = "";
        selectedIphoneImeiProductCode = "";
        purchaseMode = ' ';
        estimatedPurchaseDate = null;
        isOutage = false;
    }

     public void setSubrNum(String inputtedSubrNum) {
          subrNum = inputtedSubrNum;
     } 
  
     public String getSubrNum() {
          return subrNum;
     }   

     public void setCustNum(String inputtedCustNum) {
          custNum = inputtedCustNum;
     } 
  
     public String getCustNum() {
          return custNum;
     }    

     public void setCustFirstName(String inputtedCustFirstName) {
          custFirstName = inputtedCustFirstName;
     } 
  
     public String getCustFirstName() {
          return custFirstName;
     }   

     public void setCustLastName(String inputtedCustLastName) {
          custLastName = inputtedCustLastName;
     } 
  
     public String getCustLastName() {
          return custLastName;
     }   

     public void setEmailAddress(String inputtedEmailAddress) {
          emailAddress = inputtedEmailAddress;
     } 
  
     public String getEmailAddress() {
          return emailAddress;
     }

    public void setEmailAddressPrefix(String inputtedEmailAddressPrefix) {
          emailAddressPrefix = inputtedEmailAddressPrefix;
     } 
  
     public String getEmailAddressPrefix() {
          return emailAddressPrefix;
     }
    
    public void setEmailAddressSuffix(String inputtedEmailAddressSuffix) {
          emailAddressSuffix = inputtedEmailAddressSuffix;
     } 
  
     public String getEmailAddressSuffix() {
          return emailAddressSuffix;
     }  
    
    public void setPocDeliveryPreference(String inputtedPocDeliveryPreference) {
          pocDeliveryPreference = inputtedPocDeliveryPreference;
     } 
  
     public String getPocDeliveryPreference() {
          return pocDeliveryPreference;
     }  
        
    public void setPocLanguage(String inputtedPocLanguage) {
          pocLanguage = inputtedPocLanguage;
     } 
  
     public String getPocLanguage() {
          return pocLanguage;
     }  
    
// Added by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection
    public void setIDeviceProductList(List<EpcProductListBean> iDeviceProductList) {
        this.iDeviceProductList = iDeviceProductList;
    }

    public List<EpcProductListBean> getIDeviceProductList() {
        return iDeviceProductList;
    }
// End added by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection
// Modified by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection 
//      public void setIphoneImei(String[] inputtedIphoneImei) {
//           iphoneImei = inputtedIphoneImei;
//      } 
//   
//      public String[] getIphoneImei() {
//           return iphoneImei;
//      }
//     
//     public void setIphoneImeiProductCode(String[] inputtedIphoneImeiProductCode) {
//           iphoneImeiProductCode = inputtedIphoneImeiProductCode;
//      } 
//   
//      public String[] getIphoneImeiProductCode() {
//           return iphoneImeiProductCode;
//      }
//     
//     public void setIphoneImeiDescription(String[] inputtedIphoneImeiDescription) {
//           iphoneImeiDescription = inputtedIphoneImeiDescription;
//      } 
//   
//      public String[] getIphoneImeiDescription() {
//           return iphoneImeiDescription;
//      }
// End modified by Michael Wong on 07 Jun 2018 for fine tune the logic of multiple imei selection 
        
     public void setSelectedIphoneImei(String inputtedSelectedIphoneImei) {
          selectedIphoneImei = inputtedSelectedIphoneImei;
     } 
  
     public String getSelectedIphoneImei() {
          return selectedIphoneImei;
     }

    public void setSelectedIphoneImeiProductCode(String inputtedSelectedIphoneImeiProductCode) {
          selectedIphoneImeiProductCode = inputtedSelectedIphoneImeiProductCode;
     } 
  
     public String getSelectedIphoneImeiProductCode() {
          return selectedIphoneImeiProductCode;
     }

    public void setPurchaseMode(char inputtedPurchaseMode) {
          purchaseMode = inputtedPurchaseMode;
     } 
  
     public char getPurchaseMode() {
          return purchaseMode;
     }
    
    public void setEstimatedPurchaseDate(Date inputtedEstimatedPurchaseDate) {
          estimatedPurchaseDate = inputtedEstimatedPurchaseDate;
     } 
  
     public Date getEstimatedPurchaseDate() {
          return estimatedPurchaseDate;
     }
    
    public void setIsOutage(boolean inputtedIsOutage) {
          isOutage = inputtedIsOutage;
     } 
  
     public boolean getIsOutage() {
          return isOutage;
     }
}
