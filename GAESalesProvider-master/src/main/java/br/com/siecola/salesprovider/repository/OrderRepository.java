package br.com.siecola.salesprovider.repository;

import br.com.siecola.salesprovider.exception.OrderNotFoundException;
import br.com.siecola.salesprovider.model.Order;
import com.google.appengine.api.datastore.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class OrderRepository {

    private static final Logger log = Logger.getLogger("OrderRepository");

    private static final String ORDER_KIND = "Orders";
    private static final String ORDER_KEY = "orderKey";

    private static final String PROPERTY_EMAIL = "email";
    private static final String PROPERTY_FREIGH_PRICE = "freightPrice";


    public List<Order> getOrders() {
        List<Order> orders = new ArrayList<>();
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query query;
        query = new Query(ORDER_KIND).addSort(PROPERTY_EMAIL,
                Query.SortDirection.ASCENDING);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());

        for (Entity entity : entities) {
            Order order = entityToOrder(entity);

            orders.add(order);
        }

        return orders;
    }

    public Optional<Order> getOrderById (long id) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        try {
            Key parent = KeyFactory.createKey(ORDER_KIND, ORDER_KEY);
            Key key = KeyFactory.createKey(parent, ORDER_KIND, id);
            Entity entity = datastore.get(key);
            return Optional.ofNullable(entityToOrder(entity));
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    public List<Order> getOrdersByEmail (String email) {
        List<Order> orders = new ArrayList<>();
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter filter = new Query.FilterPredicate(PROPERTY_EMAIL,
                Query.FilterOperator.EQUAL, email);

        Query query = new Query(ORDER_KIND).setFilter(filter);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());

        for (Entity entity : entities) {
            Order order = entityToOrder(entity);

            orders.add(order);
        }

        return orders;
    }

    public Order saveOrder (Order order) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Key key = KeyFactory.createKey(ORDER_KIND, ORDER_KEY);
        Entity entity = new Entity(ORDER_KIND, key);

        orderToEntity (order, entity);

        datastore.put(entity);

        order.setId(entity.getKey().getId());

        return order;
    }

    public Order deleteOrder (long id) throws OrderNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Key parent = KeyFactory.createKey(ORDER_KIND, ORDER_KEY);
        Key key = KeyFactory.createKey(parent, ORDER_KIND, id);
        Entity entity = null;

        try {
            entity = datastore.get(key);
            datastore.delete(entity.getKey());

            return entityToOrder(entity);
        } catch (EntityNotFoundException e) {
            throw new OrderNotFoundException("Order " + id
                    + " not found");
        }
    }

    private void orderToEntity (Order order, Entity orderEntity) {
        orderEntity.setProperty(PROPERTY_EMAIL, order.getEmail());
        orderEntity.setProperty(PROPERTY_FREIGH_PRICE, order.getFreightPrice());
    }

    private Order entityToOrder(Entity orderEntity) {
        Order order = new Order();
        order.setId(orderEntity.getKey().getId());
        order.setEmail((String) orderEntity.getProperty(PROPERTY_EMAIL));
        order.setFreightPrice((Double) orderEntity.getProperty(PROPERTY_FREIGH_PRICE));

        return order;
    }
}
