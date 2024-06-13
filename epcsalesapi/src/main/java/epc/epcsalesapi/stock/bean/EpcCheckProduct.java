package epc.epcsalesapi.stock.bean;

public class EpcCheckProduct {
	private String productCode;
	private String imeiSim;
	
	public EpcCheckProduct() {}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getImeiSim() {
		return imeiSim;
	}

	public void setImeiSim(String imeiSim) {
		this.imeiSim = imeiSim;
	}
}
