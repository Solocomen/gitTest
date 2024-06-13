package epc.epcsalesapi.sales.bean;

import java.util.ArrayList;

public class EpcSparseAdd {

    private int quantity;
    private ArrayList<EpcSparseAddProduct> products;

    public EpcSparseAdd() {}

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ArrayList<EpcSparseAddProduct> getProducts() {
        return products;
    }

    public void setProducts(ArrayList<EpcSparseAddProduct> products) {
        this.products = products;
    }

    
}