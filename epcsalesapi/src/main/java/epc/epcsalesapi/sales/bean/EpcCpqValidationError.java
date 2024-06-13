package epc.epcsalesapi.sales.bean;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcCpqValidationError {
	private String entityID;
	private String entityUniqueCode;
	private String childID;
	private String errorCode;
	private String message;
	private String friendlyMessage;
	private String xsiType;
	
	private EpcCpqValidationError() {}

	public String getEntityID() {
		return entityID;
	}

	public void setEntityID(String entityID) {
		this.entityID = entityID;
	}

	public String getEntityUniqueCode() {
		return entityUniqueCode;
	}

	public void setEntityUniqueCode(String entityUniqueCode) {
		this.entityUniqueCode = entityUniqueCode;
	}

	public String getChildID() {
		return childID;
	}

	public void setChildID(String childID) {
		this.childID = childID;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getFriendlyMessage() {
		return friendlyMessage;
	}

	public void setFriendlyMessage(String friendlyMessage) {
		this.friendlyMessage = friendlyMessage;
	}

	public String getXsiType() {
		return xsiType;
	}

	public void setXsiType(String xsiType) {
		this.xsiType = xsiType;
	}

	@Override
	public String toString() {
		return "EpcCpqValidationError [entityID=" + entityID + ", entityUniqueCode=" + entityUniqueCode + ", childID="
				+ childID + ", errorCode=" + errorCode + ", message=" + message + ", friendlyMessage=" + friendlyMessage
				+ ", xsiType=" + xsiType + "]";
	}
	
	
}
