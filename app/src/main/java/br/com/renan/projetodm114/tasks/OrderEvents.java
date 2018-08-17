package br.com.renan.projetodm114.tasks;

import java.util.List;

import br.com.renan.projetodm114.models.Order;
import br.com.renan.projetodm114.webservice.WebServiceResponse;

public interface OrderEvents {
    void getOrdersFinished(List<Order> orders);
    void getOrdersFailed(WebServiceResponse webServiceResponse);
    void getOrderByIdFinished(Order order);
    void getOrderByIdFailed(WebServiceResponse webServiceResponse);
}
