/* FESCryptoEncryption.java (Apple Care enhancement): created by Danny Chan on 2021-6-25 */
package epc.epcsalesapi.helper;

import fes.smcApps.SmcAppsSMCSENS;

import java.sql.*;

import java.util.Map;

public class FESCryptoEncryption 
{
  private Connection conn = null;
  ConnectionFactory factory=null;
  CallableStatement  cstmt;
  
  public FESCryptoEncryption(Connection fesCryptoConn)
  {
      conn = fesCryptoConn;
  }
  /*
   * @key : MAX 26-char long in HEX value
   */
  public String encrypt(String normal_string, String key)
  {
    String encryptedString="";
    String query="{?=call fes_vdep_crypto_encrypt_pkg.encrypt(?,?)}";
	String input_key = StringHelper.trim(key);
    try{
      //factory = FESCryptoConnectionFactory.getInstance();
      //conn = factory.getConnection();
      cstmt = conn.prepareCall(query);
      cstmt.registerOutParameter(1, Types.VARCHAR);      
      cstmt.setString(2, normal_string);
	  if ("".equals(input_key)){
		  cstmt.setNull(3, Types.VARCHAR);
	  }else{
          cstmt.setString(3, key);
	  }
      cstmt.execute();
      encryptedString=StringHelper.trim( cstmt.getString(1));
    } catch (Exception e) {
      System.out.println("FESCryptoEncryption : " + e.getMessage()); 
    }	finally {
      //factory.freeConnection(conn);
    }
    return encryptedString;
  }
  /*
   * @key : MAX 26-char long in HEX value
   */  
  public String decrypt(String encrypted_string, String key, String calling_apps, String calling_user, String calling_ref)
  {
    String decryptedString="";
    String query="{?=call fes_vdep_crypto_decrypt_pkg.decrypt(?,?,?,?,?)}";
    String input_key = StringHelper.trim(key);
    try{
      System.out.println("calling cecrypt");
      //factory = FESCryptoConnectionFactory.getInstance();
      //System.out.println("factory = " + factory);
      //conn = factory.getConnection();
      //System.out.println("conn = " + conn);
      cstmt = conn.prepareCall(query);
      cstmt.registerOutParameter(1, Types.VARCHAR);
      cstmt.setString(2, encrypted_string);
	  if ("".equals(input_key)){
		  cstmt.setNull(3, Types.VARCHAR);
	  }else{
          cstmt.setString(3, key);
	  }
      cstmt.setString(4, calling_apps);
      cstmt.setString(5, calling_user);
	  cstmt.setString(6, calling_ref);
      cstmt.execute();
      decryptedString=StringHelper.trim( cstmt.getString(1));
    } catch (Exception e) {
      System.out.println("FESCryptoEncryption : " + e.getMessage()); 
      e.printStackTrace();
    }	finally {
      //factory.freeConnection(conn);
    }    
    return decryptedString;
  }

  
  /**
     * for encryption of all sensitive data
     *
     * @param cipheredText
     * @return encrypted cipheredText
     * @throws Exception
     */
  public static String eGet(String cipheredText) throws Exception  {
      return SmcAppsSMCSENS.eGet(cipheredText);
  }

  
  /**
     * for encryption of all sensitive data
     *
     * @param cipheredText
     * @param encoding
     * @return encrypted cipheredText
     * @throws Exception
     */
  public static String eGet(String cipheredText, String encoding) throws Exception  {
      return SmcAppsSMCSENS.eGet(cipheredText, encoding);
  }
  
  
  /**
     * for encryption of all sensitive data (multiple values)
     *
     * @param map
     * @return encrypted map
     * @throws Exception
     */
  public static Map<String, String> eGetMap(Map<String, String> map) throws Exception  {
      return SmcAppsSMCSENS.eGetMap(map);
  }
  
  
  /**
     * for decryption of all sensitive data
     *
     * @param decrypted cipheredText
     * @return decrypted cipheredText
     * @throws Exception
     */
  public static String dGet(String cipheredText) throws Exception  {
      return SmcAppsSMCSENS.dGet(cipheredText);
  }
  
  
  /**
     * for decryption of all sensitive data
     *
     * @param encrypted cipheredText
     * @param encoding
     * @return decrypted cipheredText
     * @throws Exception
     */
  public static String dGet(String cipheredText, String encoding) throws Exception  {
      return SmcAppsSMCSENS.dGet(cipheredText, encoding);
  }
  
  
  /**
     * for decryption of all sensitive data (multiple values)
     *
     * @param map
     * @return decrypted map
     * @throws Exception
     */
  public static Map<String, String> dGetMap(Map<String, String> map) throws Exception  {
      return SmcAppsSMCSENS.dGetMap(map);
  }
  
  
}