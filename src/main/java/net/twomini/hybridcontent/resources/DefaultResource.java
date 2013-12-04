package net.twomini.hybridcontent.resources;

import com.yammer.dropwizard.jersey.caching.CacheControl;
import net.twomini.hybridcontent.auth.AuthCaller;
import net.twomini.hybridcontent.auth.AuthRequirements;

import net.twomini.hybridcontent.dto.TestResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class DefaultResource {

    private static final Logger L = LoggerFactory.getLogger(DefaultResource.class);

    public DefaultResource() {
    }

    @Path("testEndPoint")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @CacheControl(maxAge = 0, mustRevalidate = true, noCache = true, isPrivate = true, noStore = true, noTransform = true)
    public TestResponse testEndPoint(@AuthRequirements(requireLoggedIn = false) AuthCaller caller) {
        return new TestResponse((caller!=null)?caller.getUserName():null); // returns this object encoded as JSON
    }

    @Path("secretTestEndPoint")
    @Produces(MediaType.APPLICATION_JSON)
    @GET
    @CacheControl(maxAge = 0, mustRevalidate = true, noCache = true, isPrivate = true, noStore = true, noTransform = true)
    public TestResponse secureTestEndPoint(@AuthRequirements(requireRoles = {"secretRole"}) AuthCaller caller) {
        return new TestResponse((caller!=null)?caller.getUserName():null); // returns this object encoded as JSON
    }

}
