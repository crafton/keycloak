package org.keycloak.federation.ws;

import org.jboss.logging.Logger;
import org.keycloak.Config;
import org.keycloak.models.*;
import org.keycloak.models.utils.KeycloakModelUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WsFederationProviderFactory implements UserFederationProviderFactory {

    private static final Logger logger = Logger.getLogger(WsFederationProviderFactory.class);

    @Override
    public WsFederationProvider getInstance(KeycloakSession session, UserFederationProviderModel model) {

        return new WsFederationProvider(session, model);
    }

    @Override
    public Set<String> getConfigurationOptions() {
        Set<String> configOptions = new HashSet<>();

        return configOptions;
    }

    @Override
    public String getId() {
        return "ws";
    }

    @Override
    public UserFederationSyncResult syncAllUsers(KeycloakSessionFactory sessionFactory, final String realmId, final UserFederationProviderModel model) {
        final UserFederationSyncResult syncResult = new UserFederationSyncResult();

        KeycloakModelUtils.runJobInTransaction(sessionFactory, new KeycloakSessionTask() {
            @Override
            public void run(KeycloakSession session) {
                RealmModel realmModel = session.realms().getRealm(realmId);
                WsFederationProvider wsFederationProvider = (WsFederationProvider) getInstance(session, model);
                List<ServiceModel> allUsers = wsFederationProvider.getAllUsers();
                UserProvider localProvider = session.userStorage();

                for(ServiceModel serviceModel : allUsers){
                    UserModel localUser = localProvider.getUserByUsername(serviceModel.getFirstName(), realmModel);

                    if(localUser == null){
                        UserModel imported = wsFederationProvider.getUserByUsername(realmModel, serviceModel.getFirstName());
                        if(imported != null){
                            syncResult.increaseAdded();
                        }
                    }
                }
            }
        });

        return syncResult;
    }

    @Override
    public UserFederationSyncResult syncChangedUsers(KeycloakSessionFactory sessionFactory, String realmId, UserFederationProviderModel model, Date lastSync) {
        return syncAllUsers(sessionFactory, realmId, model);
    }

    @Override
    public UserFederationProvider create(KeycloakSession session) {
        return null;
    }

    @Override
    public void init(Config.Scope config) {

    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }

    @Override
    public void close() {

    }
}
