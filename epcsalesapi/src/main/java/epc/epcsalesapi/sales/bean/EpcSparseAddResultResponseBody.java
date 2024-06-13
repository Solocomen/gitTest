package epc.epcsalesapi.sales.bean;

public class EpcSparseAddResultResponseBody {
    private String quoteId;
    private String[] sparseQuoteItemIds;

    public EpcSparseAddResultResponseBody() {}

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String[] getSparseQuoteItemIds() {
        return sparseQuoteItemIds;
    }

    public void setSparseQuoteItemIds(String[] sparseQuoteItemIds) {
        this.sparseQuoteItemIds = sparseQuoteItemIds;
    }

    
}