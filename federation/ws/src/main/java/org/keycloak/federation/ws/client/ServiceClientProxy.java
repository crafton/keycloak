package org.keycloak.federation.ws.client;


import org.keycloak.federation.ws.ServiceModel;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/person")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface ServiceClientProxy {

    @GET
    @Path("/{firstname}")
    ServiceModel getUser(@PathParam("firstname") String firstname);

    @GET
    List<ServiceModel> getAllUsers();

    @POST
    void createUser(ServiceModel user);

    @DELETE
    @Path("/delete/{firstname}")
    String deleteUser(@PathParam("firstname") String firstname);
}
