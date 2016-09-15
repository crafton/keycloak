package org.keycloak.federation.ws;

import org.jboss.logging.Logger;
import org.keycloak.federation.ws.client.ServiceClient;
import org.keycloak.federation.ws.client.ServiceClientProxy;
import org.keycloak.models.*;

import java.util.*;

public class WsFederationProvider implements UserFederationProvider {

    protected KeycloakSession session;
    protected UserFederationProviderModel model;
    private final ServiceClientProxy serviceClientProxy;
    private static final String BASE_URL = "http://esoesp02-poc.qut.edu.au:9797";
    private static final Logger logger = Logger.getLogger(WsFederationProvider.class);

    public WsFederationProvider(KeycloakSession session, UserFederationProviderModel model) {
        this.session = session;
        this.model = model;
        this.serviceClientProxy = ServiceClient.getRestClient(BASE_URL);
    }

    public KeycloakSession getSession() {
        return session;
    }

    public UserFederationProviderModel getModel() {
        return model;
    }

    @Override
    public UserModel validateAndProxy(RealmModel realm, UserModel local) {

        /*if (local == null) {
            return null;
        }

        logger.info("validateAndProxy retrieving for: " + local.getUsername());

        ServiceModel serviceModel = this.serviceClientProxy.getUser(local.getUsername());
        if (serviceModel == null || serviceModel.getFirstName().isEmpty()) {
            return null;
        }*/
        return local;
    }

    @Override
    public boolean synchronizeRegistrations() {
        return true;
    }

    @Override
    public UserModel register(RealmModel realm, UserModel user) {
        ServiceModel serviceModel = new ServiceModel(user.getUsername(), user.getLastName(), 50);

        this.serviceClientProxy.createUser(serviceModel);

        return user;
    }

    @Override
    public boolean removeUser(RealmModel realm, UserModel user) {
        ServiceModel serviceModel = this.serviceClientProxy.getUser(user.getUsername());
        if (serviceModel != null) {
            this.serviceClientProxy.deleteUser(user.getUsername());

        } else {
            logger.info("User does not exist in remote database, deleting from keycloak alone.");
        }

        return true;
    }

    @Override
    public UserModel getUserByUsername(RealmModel realm, String username) {
        logger.info("Get by username: " + username);

        ServiceModel serviceModel = this.serviceClientProxy.getUser(username);

        if (serviceModel != null) {
            UserModel userModel = session.userStorage().addUser(realm, username);
            userModel.setEnabled(true);
            userModel.setFederationLink(model.getId());
            userModel.setEmail(serviceModel.getSurName() + "@" + serviceModel.getFirstName() + ".qut");
            userModel.setFirstName(serviceModel.getFirstName());
            userModel.setLastName(serviceModel.getSurName());

            List<String> age = new ArrayList<>();
            age.add(serviceModel.getAge().toString());
            userModel.setAttribute("age", age);

            return userModel;
        }

        return null;
    }

    @Override
    public UserModel getUserByEmail(RealmModel realm, String email) {
        return null;
    }

    @Override
    public List<UserModel> searchByAttributes(Map<String, String> attributes, RealmModel realm, int maxResults) {
        List<UserModel> searchResult = new ArrayList<>();

        if (attributes.containsKey(USERNAME)) {

            String username = attributes.get(USERNAME);

            if (session.userStorage().getUserByUsername(username, realm) == null) {
                UserModel userModel = getUserByUsername(realm, username);
                if (userModel != null) {
                    searchResult.add(userModel);
                    return searchResult;
                }
            }
        }

        return searchResult;
    }

    @Override
    public List<UserModel> getGroupMembers(RealmModel realm, GroupModel group, int firstResult, int maxResults) {
        return null;
    }

    @Override
    public void preRemove(RealmModel realm) {

    }

    @Override
    public void preRemove(RealmModel realm, RoleModel role) {

    }

    @Override
    public void preRemove(RealmModel realm, GroupModel group) {

    }

    @Override
    public boolean isValid(RealmModel realm, UserModel local) {

        logger.info("isValid called on: " + local.getUsername());
        if (local == null) {
            return false;
        }

        ServiceModel serviceModel = this.serviceClientProxy.getUser(local.getUsername());
        if (serviceModel == null || serviceModel.getFirstName().isEmpty()) {
            return false;
        }

        return true;
    }

    @Override
    public Set<String> getSupportedCredentialTypes(UserModel user) {
        return new HashSet<>();
    }

    @Override
    public Set<String> getSupportedCredentialTypes() {
        return new HashSet<>();
    }

    @Override
    public boolean validCredentials(RealmModel realm, UserModel user, List<UserCredentialModel> input) {
        return false;
    }

    @Override
    public boolean validCredentials(RealmModel realm, UserModel user, UserCredentialModel... input) {
        return false;
    }

    @Override
    public CredentialValidationOutput validCredentials(RealmModel realm, UserCredentialModel credential) {
        return null;
    }

    @Override
    public void close() {

    }

    public List<ServiceModel> getAllUsers() {
        List<ServiceModel> users = this.serviceClientProxy.getAllUsers();

        return users;
    }
}
