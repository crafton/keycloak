package org.keycloak.federation.ws.client;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

public class ServiceClient {

    public static ServiceClientProxy getRestClient(String baseUrl) {
        ResteasyClient client = new ResteasyClientBuilder().build();
        ResteasyWebTarget target = client.target(baseUrl);

        ServiceClientProxy serviceClientProxy = target.proxyBuilder(ServiceClientProxy.class)
                .classloader(ServiceClientProxy.class.getClassLoader())
                .build();

        return serviceClientProxy;
    }
}
