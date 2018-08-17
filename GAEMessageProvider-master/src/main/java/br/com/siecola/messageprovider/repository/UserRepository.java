package br.com.siecola.messageprovider.repository;

import br.com.siecola.messageprovider.exception.UserAlreadyExistsException;
import br.com.siecola.messageprovider.exception.UserNotFoundException;
import br.com.siecola.messageprovider.model.User;
import com.google.appengine.api.datastore.*;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Repository
public class UserRepository {
    private static final Logger log = Logger.getLogger("UserRepository");

    private static final String USER_KIND = "Users";
    private static final String USER_KEY = "userKey";

    private static final String PROPERTY_ID = "UserId";
    private static final String PROPERTY_EMAIL = "email";
    private static final String PROPERTY_PASSWORD = "password";
    private static final String PROPERTY_GCM_REG_ID = "gcmRegId";
    private static final String PROPERTY_LAST_LOGIN = "lastLogin";
    private static final String PROPERTY_LAST_GCM_REGISTER = "lastGCMRegister";
    private static final String PROPERTY_ROLE = "role";
    private static final String PROPERTY_ENABLED = "enabled";

    @PostConstruct
    public void init(){
        User adminUser;
        Optional<User> optAdminUser = this.getByEmail("matilde@siecola.com.br");
        try {
            if (optAdminUser.isPresent()) {
                adminUser = optAdminUser.get();
                if (!adminUser.getRole().equals("ADMIN")) {
                    adminUser.setRole("ADMIN");
                    this.updateUser(adminUser, "matilde@siecola.com.br");
                }
            } else {
                adminUser = new User();
                adminUser.setRole("ADMIN");
                adminUser.setEnabled(true);
                adminUser.setPassword("matilde");
                adminUser.setEmail("matilde@siecola.com.br");
                this.saveUser(adminUser);
            }
        } catch (UserAlreadyExistsException | UserNotFoundException e) {
            log.severe("Failed while creating user ADMIN");
        }
    }

    public List<User> getUsers() {
        List<User> users = new ArrayList<>();
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query query;
        query = new Query(USER_KIND).addSort(PROPERTY_EMAIL,
                Query.SortDirection.ASCENDING);

        List<Entity> userEntities = datastore.prepare(query).asList(
                FetchOptions.Builder.withDefaults());

        for (Entity userEntity : userEntities) {
            User user = entityToUser(userEntity);

            users.add(user);
        }

        return users;
    }

    public Optional<User> getByEmail (String email) {
        log.info("User: " + email);

        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter filter = new Query.FilterPredicate(PROPERTY_EMAIL,
                Query.FilterOperator.EQUAL, email);

        Query query = new Query(USER_KIND).setFilter(filter);

        Entity userEntity = datastore.prepare(query).asSingleEntity();

        if (userEntity != null) {
            return Optional.ofNullable(entityToUser(userEntity));
        } else {
            return Optional.empty();
        }
    }

    public User saveUser (User user) throws UserAlreadyExistsException {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        if (!checkIfEmailExist (user)) {
            Key userKey = KeyFactory.createKey(USER_KIND, USER_KEY);
            Entity userEntity = new Entity(USER_KIND, userKey);

            userToEntity (user, userEntity);

            datastore.put(userEntity);

            user.setId(userEntity.getKey().getId());

            return user;
        } else {
            throw new UserAlreadyExistsException("Usuário " + user.getEmail()
                    + " já existe");
        }
    }

    public User updateUser (User user, String email)
            throws UserNotFoundException, UserAlreadyExistsException {

        if (!checkIfEmailExist (user)) {
            DatastoreService datastore = DatastoreServiceFactory
                    .getDatastoreService();

            Query.Filter emailFilter = new Query.FilterPredicate(PROPERTY_EMAIL,
                    Query.FilterOperator.EQUAL, email);

            Query query = new Query(USER_KIND).setFilter(emailFilter);

            Entity userEntity = datastore.prepare(query).asSingleEntity();

            if (userEntity != null) {
                userToEntity (user, userEntity);

                datastore.put(userEntity);

                user.setId(userEntity.getKey().getId());

                return user;
            } else {
                throw new UserNotFoundException("User " + email
                        + " not found");
            }
        } else {
            throw new UserAlreadyExistsException("User " + user.getEmail()
                    + " already exists");
        }
    }

    public User deleteUser (String email) throws UserNotFoundException {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter userFilter = new Query.FilterPredicate(PROPERTY_EMAIL,
                Query.FilterOperator.EQUAL, email);

        Query query = new Query(USER_KIND).setFilter(userFilter);

        Entity userEntity = datastore.prepare(query).asSingleEntity();

        if (userEntity != null) {
            datastore.delete(userEntity.getKey());

            return entityToUser(userEntity);
        } else {
            throw new UserNotFoundException("User " + email
                    + " not found");
        }
    }

    private boolean checkIfEmailExist (User user) {
        DatastoreService datastore = DatastoreServiceFactory
                .getDatastoreService();

        Query.Filter filter = new Query.FilterPredicate(PROPERTY_EMAIL,
                Query.FilterOperator.EQUAL, user.getEmail());

        Query query = new Query(USER_KIND).setFilter(filter);
        Entity userEntity = datastore.prepare(query).asSingleEntity();

        if (userEntity == null) {
            return false;
        } else {
            if (user.getId() == null) {
                return true;
            } else {
                return userEntity.getKey().getId() != user.getId();
            }
        }
    }

    private void userToEntity (User user, Entity userEntity) {
        userEntity.setProperty(PROPERTY_ID, user.getId());
        userEntity.setProperty(PROPERTY_EMAIL, user.getEmail());
        userEntity.setProperty(PROPERTY_PASSWORD, user.getPassword());
        userEntity.setProperty(PROPERTY_GCM_REG_ID, user.getGcmRegId());
        userEntity.setProperty(PROPERTY_LAST_LOGIN, user.getLastLogin());
        userEntity.setProperty(PROPERTY_LAST_GCM_REGISTER,
                user.getLastGCMRegister());
        userEntity.setProperty(PROPERTY_ROLE, user.getRole());
        userEntity.setProperty(PROPERTY_ENABLED, user.isEnabled());
    }


    private User entityToUser (Entity userEntity) {
        User user = new User();
        user.setId(userEntity.getKey().getId());
        user.setEmail((String) userEntity.getProperty(PROPERTY_EMAIL));
        user.setPassword((String) userEntity.getProperty(PROPERTY_PASSWORD));
        user.setGcmRegId((String) userEntity.getProperty(PROPERTY_GCM_REG_ID));
        user.setLastLogin((Date) userEntity.getProperty(PROPERTY_LAST_LOGIN));
        user.setLastGCMRegister((Date) userEntity.
                getProperty(PROPERTY_LAST_GCM_REGISTER));
        user.setRole((String) userEntity.getProperty(PROPERTY_ROLE));
        user.setEnabled((Boolean) userEntity.getProperty(PROPERTY_ENABLED));
        return user;
    }
}
