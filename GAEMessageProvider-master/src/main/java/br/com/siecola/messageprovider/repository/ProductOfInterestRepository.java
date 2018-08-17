package br.com.siecola.messageprovider.repository;

import br.com.siecola.messageprovider.exception.ProductOfInterestNotFoundException;
import br.com.siecola.messageprovider.model.ProductOfInterest;
import com.google.appengine.api.datastore.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class ProductOfInterestRepository {
    private static final Logger log = Logger.getLogger("ProductOfInterestRepository");

    private static final String PRODUCT_KIND = "Products";
    private static final String PRODUCT_KEY = "productKey";

    private static final String PROPERTY_ID = "ProductId";
    private static final String PROPERTY_CODE = "Code";
    private static final String PROPERTY_PRICE = "Price";
    private static final String PROPERTY_EMAIL = "Email";


    public List<ProductOfInterest> getAll() {
        List<ProductOfInterest> products = new ArrayList<>();
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query query;
        query = new Query(PRODUCT_KIND).addSort(PROPERTY_EMAIL,
                Query.SortDirection.ASCENDING);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());

        for (Entity entity : entities) {
            ProductOfInterest product = entityToProduct(entity);

            products.add(product);
        }

        return products;
    }

    public ProductOfInterest save(ProductOfInterest productOfInterest) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Key key = KeyFactory.createKey(PRODUCT_KIND, PRODUCT_KEY);
        Entity entity;

        Optional<Entity> optEntity = this.findByCodeAndEmail(
                productOfInterest.getCode(), productOfInterest.getEmail());

        entity = optEntity.orElseGet(() -> new Entity(PRODUCT_KIND, key));
        productToEntity(productOfInterest, entity);

        datastore.put(entity);

        productOfInterest.setId(entity.getKey().getId());

        return productOfInterest;
    }

    public List<ProductOfInterest> findByEmail(String email) {
        List<ProductOfInterest> products = new ArrayList<>();

        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter filter = new Query.FilterPredicate(PROPERTY_EMAIL,
                Query.FilterOperator.EQUAL, email);

        Query query = new Query(PRODUCT_KIND).setFilter(filter);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());

        for (Entity entity : entities) {
            ProductOfInterest product = entityToProduct(entity);

            products.add(product);
        }

        return products;
    }

    public List<ProductOfInterest> findByCode(String code) {
        List<ProductOfInterest> products = new ArrayList<>();

        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter filter = new Query.FilterPredicate(PROPERTY_CODE,
                Query.FilterOperator.EQUAL, code);

        Query query = new Query(PRODUCT_KIND).setFilter(filter);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());

        for (Entity entity : entities) {
            ProductOfInterest product = entityToProduct(entity);

            products.add(product);
        }

        return products;
    }

    private Optional<Entity> findByCodeAndEmail(String code, String email) {
        List<ProductOfInterest> products = new ArrayList<>();

        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter codeFilter = new Query.FilterPredicate(PROPERTY_CODE,
                Query.FilterOperator.EQUAL, code);

        Query.Filter emailFilter = new Query.FilterPredicate(PROPERTY_EMAIL,
                Query.FilterOperator.EQUAL, email);

        Query.CompositeFilter compositeFilter = new Query.CompositeFilter(
                Query.CompositeFilterOperator.AND,
                Arrays.asList(codeFilter, emailFilter));

        Query query = new Query(PRODUCT_KIND).setFilter(compositeFilter);

        Entity entity = datastore.prepare(query).asSingleEntity();
        if (entity != null) {
            return Optional.of(entity);
        } else {
            return Optional.empty();
        }
    }

    public List<ProductOfInterest> findByCodeAndPriceGreaterThanOrEqual(String code,
                                                                        double price) {
        List<ProductOfInterest> products = new ArrayList<>();

        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter codeFilter = new Query.FilterPredicate(PROPERTY_CODE,
                Query.FilterOperator.EQUAL, code);

        Query.Filter priceFilter = new Query.FilterPredicate(PROPERTY_PRICE,
                Query.FilterOperator.GREATER_THAN_OR_EQUAL, price);

        Query.CompositeFilter compositeFilter = new Query.CompositeFilter(
                Query.CompositeFilterOperator.AND,
                Arrays.asList(codeFilter, priceFilter));

        Query query = new Query(PRODUCT_KIND).setFilter(compositeFilter);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());

        for (Entity entity : entities) {
            ProductOfInterest product = entityToProduct(entity);

            products.add(product);
        }

        return products;
    }

    public Optional<ProductOfInterest> findById(long id) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();
        try {
            Key parent = KeyFactory.createKey(PRODUCT_KIND, PRODUCT_KEY);
            Key key = KeyFactory.createKey(parent, PRODUCT_KIND, id);
            Entity entity = datastore.get(key);
            return Optional.ofNullable(entityToProduct(entity));
        } catch (EntityNotFoundException e) {
            return Optional.empty();
        }
    }

    public ProductOfInterest deleteById(long id) throws ProductOfInterestNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Key parent = KeyFactory.createKey(PRODUCT_KIND, PRODUCT_KEY);
        Key key = KeyFactory.createKey(parent, PRODUCT_KIND, id);
        Entity entity = null;

        try {
            entity = datastore.get(key);
            datastore.delete(entity.getKey());

            return entityToProduct(entity);
        } catch (EntityNotFoundException e) {
            throw new ProductOfInterestNotFoundException("Product of interest " + id
                    + " not found");
        }
    }

    private void productToEntity(ProductOfInterest product, Entity productEntity) {
        productEntity.setProperty(PROPERTY_CODE, product.getCode());
        productEntity.setProperty(PROPERTY_PRICE, product.getPrice());
        productEntity.setProperty(PROPERTY_EMAIL, product.getEmail());
    }

    private ProductOfInterest entityToProduct(Entity productEntity) {
        ProductOfInterest product = new ProductOfInterest();
        product.setId(productEntity.getKey().getId());
        product.setCode((String) productEntity.getProperty(PROPERTY_CODE));
        product.setPrice((Double) productEntity.getProperty(PROPERTY_PRICE));
        product.setEmail((String) productEntity.getProperty(PROPERTY_EMAIL));
        return product;
    }
}
