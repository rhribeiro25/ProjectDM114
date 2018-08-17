package br.com.renan.projetodm114.tasks;

import java.util.List;

import br.com.renan.projetodm114.models.InterestProduct;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public interface InterestProductEvents {
    void getInterestProductsFinished(List<InterestProduct> interestProducts);
    void getInterestProductsFailed(WebServiceResponse webServiceResponse);
    void deleteInterestProductsFinished();
    void deleteInterestProductsFailed(WebServiceResponse webServiceResponse);
    void addInterestProductsFinished();
    void addInterestProductsFailed(WebServiceResponse webServiceResponse);
}
