package epc.epcsalesapi.sales.bean;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class EpcQuoteItemForUpdate {
	private String productId;
    private String linkedItemId;
    private String itemAction;
    private HashMap<String, Object> productCandidate;
    private HashMap<String, Object> metaTypeLookup;
    private String name;
    private HashMap<String, Object> prePricedCandidate;
    private HashMap<String, Object> metaDataLookup;
    private String id;
    private String itemNumber;
    private String created;
    private String description;
    private String supersededById;
    private String supersededFromId;
    private HashMap<String, Object> decorators;
    private boolean honourExistingPrice;
    private String portfolioItemId;
    private HashMap<String, Object> portfolioItem;
    private HashMap<String, Object> currentValidation;
    
    public EpcQuoteItemForUpdate() {}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getLinkedItemId() {
		return linkedItemId;
	}

	public void setLinkedItemId(String linkedItemId) {
		this.linkedItemId = linkedItemId;
	}

	public String getItemAction() {
		return itemAction;
	}

	public void setItemAction(String itemAction) {
		this.itemAction = itemAction;
	}

	public HashMap<String, Object> getProductCandidate() {
		return productCandidate;
	}

	public void setProductCandidate(HashMap<String, Object> productCandidate) {
		this.productCandidate = productCandidate;
	}

	public HashMap<String, Object> getMetaTypeLookup() {
		return metaTypeLookup;
	}

	public void setMetaTypeLookup(HashMap<String, Object> metaTypeLookup) {
		this.metaTypeLookup = metaTypeLookup;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<String, Object> getPrePricedCandidate() {
		return prePricedCandidate;
	}

	public void setPrePricedCandidate(HashMap<String, Object> prePricedCandidate) {
		this.prePricedCandidate = prePricedCandidate;
	}

	public HashMap<String, Object> getMetaDataLookup() {
		return metaDataLookup;
	}

	public void setMetaDataLookup(HashMap<String, Object> metaDataLookup) {
		this.metaDataLookup = metaDataLookup;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(String itemNumber) {
		this.itemNumber = itemNumber;
	}

	public String getCreated() {
		return created;
	}

	public void setCreated(String created) {
		this.created = created;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getSupersededById() {
		return supersededById;
	}

	public void setSupersededById(String supersededById) {
		this.supersededById = supersededById;
	}

	public String getSupersededFromId() {
		return supersededFromId;
	}

	public void setSupersededFromId(String supersededFromId) {
		this.supersededFromId = supersededFromId;
	}

	public HashMap<String, Object> getDecorators() {
		return decorators;
	}

	public void setDecorators(HashMap<String, Object> decorators) {
		this.decorators = decorators;
	}

	public boolean isHonourExistingPrice() {
		return honourExistingPrice;
	}

	public void setHonourExistingPrice(boolean honourExistingPrice) {
		this.honourExistingPrice = honourExistingPrice;
	}

	public String getPortfolioItemId() {
		return portfolioItemId;
	}

	public void setPortfolioItemId(String portfolioItemId) {
		this.portfolioItemId = portfolioItemId;
	}

	public HashMap<String, Object> getPortfolioItem() {
		return portfolioItem;
	}

	public void setPortfolioItem(HashMap<String, Object> portfolioItem) {
		this.portfolioItem = portfolioItem;
	}

	public HashMap<String, Object> getCurrentValidation() {
		return currentValidation;
	}

	public void setCurrentValidation(HashMap<String, Object> currentValidation) {
		this.currentValidation = currentValidation;
	}
    
}
