package epc.epcsalesapi.sales.bean;

public class EpcNotificationAttachment {
	private String cid; // inline cid
	private String contentType;
	private byte[] content;
	
	public EpcNotificationAttachment() {}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
	
	
}
