package epc.epcsalesapi.sales.bean;

public class EpcWaiveCharge {

    private String approveUsername;
    private String waiveReason;
    private int orderId;
    private int seqId;
    private String chargeCode;
	private String handle_channel;
	private String handle_location;
	private String handle_salesman;
	private String handle_user;
	
    public String getApproveUsername() {
        return approveUsername;
    }
    public void setApproveUsername(String approveUsername) {
        this.approveUsername = approveUsername;
    }
    public String getWaiveReason() {
        return waiveReason;
    }
    public void setWaiveReason(String waiveReason) {
        this.waiveReason = waiveReason;
    }
    public int getOrderId() {
        return orderId;
    }
    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }
    public int getSeqId() {
        return seqId;
    }
    public void setSeqId(int seqId) {
        this.seqId = seqId;
    }
    public String getChargeCode() {
        return chargeCode;
    }
    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

	/**
	 * @return the handle_channel
	 */
	public String getHandle_channel() {
		return handle_channel;
	}

	/**
	 * @param handle_channel the handle_channel to set
	 */
	public void setHandle_channel(String handle_channel) {
		this.handle_channel = handle_channel;
	}

	/**
	 * @return the handle_location
	 */
	public String getHandle_location() {
		return handle_location;
	}

	/**
	 * @param handle_location the handle_location to set
	 */
	public void setHandle_location(String handle_location) {
		this.handle_location = handle_location;
	}

	/**
	 * @return the handle_salesman
	 */
	public String getHandle_salesman() {
		return handle_salesman;
	}

	/**
	 * @param handle_salesman the handle_salesman to set
	 */
	public void setHandle_salesman(String handle_salesman) {
		this.handle_salesman = handle_salesman;
	}

	/**
	 * @return the handle_user
	 */
	public String getHandle_user() {
		return handle_user;
	}

	/**
	 * @param handle_user the handle_user to set
	 */
	public void setHandle_user(String handle_user) {
		this.handle_user = handle_user;
	}
    
}
