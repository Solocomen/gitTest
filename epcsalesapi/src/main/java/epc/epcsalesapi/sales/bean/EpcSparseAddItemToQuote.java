package epc.epcsalesapi.sales.bean;

public class EpcSparseAddItemToQuote {
    
    private String custId;
	private int orderId;
	private int quoteId;
    private EpcSparseAdd sparseJson;
	private String productGuid;
    private String productCode;
	private String createUser;
	private String createSalesman;
	private String createChannel;
	private String createLocation;
	private String result;
	private String errMsg;
    
    public EpcSparseAddItemToQuote() {
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(int quoteId) {
        this.quoteId = quoteId;
    }

    public EpcSparseAdd getSparseJson() {
        return sparseJson;
    }

    public void setSparseJson(EpcSparseAdd sparseJson) {
        this.sparseJson = sparseJson;
    }

    public String getProductGuid() {
        return productGuid;
    }

    public void setProductGuid(String productGuid) {
        this.productGuid = productGuid;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getCreateSalesman() {
        return createSalesman;
    }

    public void setCreateSalesman(String createSalesman) {
        this.createSalesman = createSalesman;
    }

    public String getCreateChannel() {
        return createChannel;
    }

    public void setCreateChannel(String createChannel) {
        this.createChannel = createChannel;
    }

    public String getCreateLocation() {
        return createLocation;
    }

    public void setCreateLocation(String createLocation) {
        this.createLocation = createLocation;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    
}
