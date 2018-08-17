package br.com.siecola.salesprovider.repository;

import br.com.siecola.salesprovider.exception.OrderItemNotFoundException;
import br.com.siecola.salesprovider.model.OrderItem;
import com.google.appengine.api.datastore.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Repository
public class OrderItemRepository {

    private static final Logger log = Logger.getLogger("OrderItemRepository");

    private static final String ORDER_ITEM_KIND = "OrderItems";
    private static final String ORDER_ITEM_KEY = "orderItemKey";

    private static final String PROPERTY_PRODUCT_ID = "productId";
    private static final String PROPERTY_ORDER_ID = "orderId";

    public List<OrderItem> getOrderItemsByOrderId (long orderid) {
        List<OrderItem> orderItems = new ArrayList<>();
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter filter = new Query.FilterPredicate(PROPERTY_ORDER_ID,
                Query.FilterOperator.EQUAL, orderid);

        Query query = new Query(ORDER_ITEM_KIND).setFilter(filter);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());

        for (Entity entity : entities) {
            OrderItem orderItem = entityToOrderItem(entity);

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    public OrderItem saveOrderItem (OrderItem orderItem) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Key key = KeyFactory.createKey(ORDER_ITEM_KIND, ORDER_ITEM_KEY);
        Entity entity = new Entity(ORDER_ITEM_KIND, key);

        orderItemToEntity (orderItem, entity);

        datastore.put(entity);

        orderItem.setId(entity.getKey().getId());

        return orderItem;
    }

    public List<OrderItem> deleteOrderItemsByOrderId (long orderid) {
        List<OrderItem> orderItems = new ArrayList<>();
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter filter = new Query.FilterPredicate(PROPERTY_ORDER_ID,
                Query.FilterOperator.EQUAL, orderid);

        Query query = new Query(ORDER_ITEM_KIND).setFilter(filter);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());

        for (Entity entity : entities) {
            OrderItem orderItem = entityToOrderItem(entity);

            datastore.delete(entity.getKey());

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    public OrderItem deleteOrderItem (long id) throws OrderItemNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Key parent = KeyFactory.createKey(ORDER_ITEM_KIND, ORDER_ITEM_KEY);
        Key key = KeyFactory.createKey(parent, ORDER_ITEM_KIND, id);
        try {
            Entity entity = datastore.get(key);
            datastore.delete(entity.getKey());

            return entityToOrderItem(entity);
        } catch (EntityNotFoundException e) {
            throw new OrderItemNotFoundException("OrderItem " + id
                    + " not found");
        }
    }

    private void orderItemToEntity (OrderItem orderItem, Entity orderItemEntity) {
        orderItemEntity.setProperty(PROPERTY_PRODUCT_ID, orderItem.getProductId());
        orderItemEntity.setProperty(PROPERTY_ORDER_ID, orderItem.getOrderId());
    }

    private OrderItem entityToOrderItem(Entity orderItemEntity) {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(orderItemEntity.getKey().getId());
        orderItem.setProductId((Long) orderItemEntity.getProperty(PROPERTY_PRODUCT_ID));
        orderItem.setOrderId((Long) orderItemEntity.getProperty(PROPERTY_ORDER_ID));

        return orderItem;
    }
}
