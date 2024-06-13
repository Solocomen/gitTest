/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package epc.epcsalesapi.helper;

import fes.smcApps.SmcAppsSMCSENS;
import java.sql.Blob;
import java.util.*;
import org.apache.commons.codec.binary.Hex;

/**
 *
 * @author KerryTsang
 */
public class EpcCrypto {
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
     * for encryption of all sensitive data
     *
     * @param raw
     * @param encoding
     * @return encrypted cipheredText
     * @throws Exception
     */
    public static String eGet(Blob raw, String encoding) throws Exception
    {
        byte[] blobBytes = raw.getBytes(1, (int)raw.length());
        return eGet(blobBytes, encoding);
    }
    
    /**
     * for encryption of all sensitive data
     *
     * @param raw
     * @param encoding
     * @return encrypted cipheredText
     * @throws Exception
     */
    public static String eGet(byte[] raw, String encoding) throws Exception
    {
        return SmcAppsSMCSENS.eGet(Hex.encodeHexString(raw).toUpperCase(), encoding);
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
