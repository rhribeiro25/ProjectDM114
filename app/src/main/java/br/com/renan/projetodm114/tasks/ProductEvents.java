package br.com.renan.projetodm114.tasks;

import java.util.List;

import br.com.renan.projetodm114.models.Order;
import br.com.renan.projetodm114.models.Product;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public interface ProductEvents {
    void getProductsFinished(List<Product> products);
    void getProductsFailed(WebServiceResponse webServiceResponse);
    void getProductByCodeFinished(Product product);
    void getProductByCodeFailed(WebServiceResponse webServiceResponse);
}
