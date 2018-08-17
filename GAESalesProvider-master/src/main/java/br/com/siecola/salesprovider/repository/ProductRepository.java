package br.com.siecola.salesprovider.repository;

import br.com.siecola.salesprovider.exception.ProductAlreadyExistsException;
import br.com.siecola.salesprovider.exception.ProductNotFoundException;
import br.com.siecola.salesprovider.model.Product;
import com.google.appengine.api.datastore.*;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class ProductRepository {

    private static final Logger log = Logger.getLogger("ProductRepository");

    private static final String PRODUCT_KIND = "Products";
    private static final String PRODUCT_KEY = "productKey";

    private static final String PROPERTY_NAME = "name";
    private static final String PROPERTY_DESCRIPTION = "description";
    private static final String PROPERTY_CODE = "code";
    private static final String PROPERTY_PRICE = "price";


    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query query;
        query = new Query(PRODUCT_KIND).addSort(PROPERTY_CODE,
                Query.SortDirection.ASCENDING);

        List<Entity> entities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());

        for (Entity entity : entities) {
            Product product = entityToProduct(entity);

            products.add(product);
        }

        return products;
    }

    public Optional<Product> getByCode (String code) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter filter = new Query.FilterPredicate(PROPERTY_CODE,
                Query.FilterOperator.EQUAL, code);

        Query query = new Query(PRODUCT_KIND).setFilter(filter);

        Entity entity = datastore.prepare(query).asSingleEntity();

        if (entity != null) {
            return Optional.ofNullable(entityToProduct(entity));
        } else {
            return Optional.empty();
        }
    }

    public Product saveProduct (Product product) throws ProductAlreadyExistsException {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        if (!checkIfCodeExist (product)) {
            Key key = KeyFactory.createKey(PRODUCT_KIND, PRODUCT_KEY);
            Entity entity = new Entity(PRODUCT_KIND, key);

            productToEntity (product, entity);

            datastore.put(entity);

            product.setId(entity.getKey().getId());

            return product;
        } else {
            throw new ProductAlreadyExistsException("Product " + product.getCode()
                    + " already exists");
        }
    }

    public Product updateProduct (Product product, String code)
            throws ProductNotFoundException, ProductAlreadyExistsException {

        if (!checkIfCodeExist (product)) {
            DatastoreService datastore = DatastoreServiceFactory
                    .getDatastoreService();

            Query.Filter filter = new Query.FilterPredicate(PROPERTY_CODE,
                    Query.FilterOperator.EQUAL, code);

            Query query = new Query(PRODUCT_KIND).setFilter(filter);

            Entity entity = datastore.prepare(query).asSingleEntity();

            if (entity != null) {
                productToEntity (product, entity);

                datastore.put(entity);

                product.setId(entity.getKey().getId());

                return product;
            } else {
                throw new ProductNotFoundException("Product " + code
                        + " not found");
            }
        } else {
            throw new ProductAlreadyExistsException("Product " + product.getCode()
                    + " already exists");
        }
    }

    public Product deleteProduct (String code) throws ProductNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter filter = new Query.FilterPredicate(PROPERTY_CODE,
                Query.FilterOperator.EQUAL, code);

        Query query = new Query(PRODUCT_KIND).setFilter(filter);

        Entity entity = datastore.prepare(query).asSingleEntity();

        if (entity != null) {
            datastore.delete(entity.getKey());

            return entityToProduct(entity);
        } else {
            throw new ProductNotFoundException("Product " + code
                    + " not found");
        }
    }

    private boolean checkIfCodeExist (Product product) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter codeFilter = new Query.FilterPredicate(PROPERTY_CODE,
                Query.FilterOperator.EQUAL, product.getCode());

        Query query = new Query(PRODUCT_KIND).setFilter(codeFilter);
        Entity productEntity = datastore.prepare(query).asSingleEntity();

        if (productEntity == null) {
            return false;
        } else {
            if (productEntity.getKey().getId() == product.getId()) {
                //it's the same product
                return false;
            } else {
                return true;
            }
        }
    }

    private void productToEntity (Product product, Entity productEntity) {
        productEntity.setProperty(PROPERTY_NAME, product.getName());
        productEntity.setProperty(PROPERTY_DESCRIPTION, product.getDescription());
        productEntity.setProperty(PROPERTY_CODE, product.getCode());
        productEntity.setProperty(PROPERTY_PRICE, product.getPrice());
    }

    private Product entityToProduct(Entity productEntity) {
        Product product = new Product();
        product.setId(productEntity.getKey().getId());
        product.setName((String) productEntity.getProperty(PROPERTY_NAME));
        product.setDescription((String) productEntity.getProperty(PROPERTY_DESCRIPTION));
        product.setCode((String) productEntity.getProperty(PROPERTY_CODE));
        product.setPrice((Double) productEntity.getProperty(PROPERTY_PRICE));

        return product;
    }
}
