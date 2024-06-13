/**
 * Copyright (c) 2022. All rights reserved. SmarTone Telecommunications Limited.
 * @project	epcsalesapi
 * @class	epc.epcsalesapi.sales.bean.AbstractEPCRequest
 * @author	TedKwan
 * @date	23-Sep-2022
 * Description:
 *
 * History:
 * 20220923-TedKwan: Created
 */
package epc.epcsalesapi.sales.bean;

public abstract class AbstractEPCAPIRequest {

	private String sysLoginUser;
    private String sysLoginSalesman;
    private String sysLoginLocation;
    private String sysLoginChannel;
    
    public boolean validateSysLoginRequest() throws Exception {
    	if( sysLoginUser == null 	 || sysLoginUser.trim().equals("") 		|| 
			sysLoginSalesman == null || sysLoginSalesman.trim().equals("")  ||
			sysLoginLocation == null || sysLoginLocation.trim().equals("")  ||
			sysLoginChannel == null  || sysLoginChannel.trim().equals("")   ) {
    		throw new Exception("Invalid Login user/salesman/location/channel");
    	}
    	
    	return true;
    }
    
	/**
	 * @return the sysLoginUser
	 */
	public String getSysLoginUser() {
		return sysLoginUser;
	}
	/**
	 * @param sysLoginUser the sysLoginUser to set
	 */
	public void setSysLoginUser(String sysLoginUser) {
		this.sysLoginUser = sysLoginUser;
	}
	/**
	 * @return the sysLoginSalesman
	 */
	public String getSysLoginSalesman() {
		return sysLoginSalesman;
	}
	/**
	 * @param sysLoginSalesman the sysLoginSalesman to set
	 */
	public void setSysLoginSalesman(String sysLoginSalesman) {
		this.sysLoginSalesman = sysLoginSalesman;
	}
	/**
	 * @return the sysLoginLocation
	 */
	public String getSysLoginLocation() {
		return sysLoginLocation;
	}
	/**
	 * @param sysLoginLocation the sysLoginLocation to set
	 */
	public void setSysLoginLocation(String sysLoginLocation) {
		this.sysLoginLocation = sysLoginLocation;
	}
	/**
	 * @return the sysLoginChannel
	 */
	public String getSysLoginChannel() {
		return sysLoginChannel;
	}
	/**
	 * @param sysLoginChannel the sysLoginChannel to set
	 */
	public void setSysLoginChannel(String sysLoginChannel) {
		this.sysLoginChannel = sysLoginChannel;
	}
    
}
