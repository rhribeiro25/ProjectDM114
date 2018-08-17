package br.com.renan.projetodm114.models;

public class OrderItem {
    private	long	id;
    private	long	productId;
    private	long	orderId;
    public Product Product;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public br.com.renan.projetodm114.models.Product getProduct() {
        return Product;
    }

    public void setProduct(br.com.renan.projetodm114.models.Product product) {
        Product = product;
    }
}
