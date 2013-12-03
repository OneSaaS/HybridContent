package net.twomini.hybridcontent.auth.dummy;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import com.yammer.dropwizard.auth.AuthenticationException;
import net.twomini.hybridcontent.auth.AuthRequirements;
import net.twomini.hybridcontent.auth.Authorizer;
import net.twomini.hybridcontent.auth.exception.AuthLoginRequiredException;
import net.twomini.hybridcontent.auth.exception.AuthPermissionDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

public class DummyProvider<T> implements InjectableProvider<AuthRequirements, Parameter> {

    private static final Logger L = LoggerFactory.getLogger(DummyProvider.class);

    private static String serviceBaseURL;

    private static URI URI_LOGIN;
    private static URI URI_PERMISSION_DENIED;

    private static CacheControl NO_CACHE_CONTROL = null;
    static {
        NO_CACHE_CONTROL = new CacheControl();
        NO_CACHE_CONTROL.setMaxAge(0);
        NO_CACHE_CONTROL.setMustRevalidate(true);
        NO_CACHE_CONTROL.setNoCache(true);
        NO_CACHE_CONTROL.setPrivate(true);
        NO_CACHE_CONTROL.setNoStore(true);
        NO_CACHE_CONTROL.setNoTransform(true);
    }

    private static class DummyHttpContextInjectable<T> extends AbstractHttpContextInjectable<T> {

        private final Authorizer authorizer;
        private final AuthRequirements authRequirements;

        private DummyHttpContextInjectable(AuthRequirements authRequirements, Authorizer authorizer) {
            this.authRequirements = authRequirements;
            this.authorizer = authorizer;
        }

        @Override
        public T getValue(HttpContext c) {
            try {
                authorizer.authorize(null, authRequirements);
                return null;
            } catch(AuthLoginRequiredException e) {
                URI redirectTo = URI_LOGIN;
                try {
                    URI requestURI = c.getRequest().getRequestUri();
                    String destination = serviceBaseURL + requestURI.getPath().substring(1) + ((requestURI.getQuery()!=null && requestURI.getQuery().length()>0)?"?"+requestURI.getQuery():"");
                    redirectTo = new URI(URI_LOGIN.toString() + "?destination=" + URLEncoder.encode(destination, "utf-8"));
                } catch (Throwable t) {
                    t.printStackTrace();
                    //do nothing
                }
                throw new WebApplicationException(Response.seeOther(redirectTo)
                        .entity("You must be logged in to access this resource.")
                        .cacheControl(NO_CACHE_CONTROL)
                        .type(MediaType.TEXT_PLAIN_TYPE)
                        .build());
            } catch(AuthPermissionDeniedException e) {
                //Send Permission Denied
                throw new WebApplicationException(Response.seeOther(URI_PERMISSION_DENIED)
                        .entity("Permission Denied.")
                        .cacheControl(NO_CACHE_CONTROL)
                        .type(MediaType.TEXT_PLAIN_TYPE)
                        .build());
            } catch(Throwable t) {
                L.warn("Error authenticating/authorizing credentials", t);
                throw new WebApplicationException(Response.Status.INTERNAL_SERVER_ERROR);
            }
        }

    }

    private final Authorizer authorizer;

    public DummyProvider(Authorizer authorizer, String serviceBaseURL, String authServiceBaseURL) {
        this.authorizer = authorizer;
        this.serviceBaseURL = serviceBaseURL;
        try {
            URI_LOGIN = new URI(authServiceBaseURL + "login.html");
            URI_PERMISSION_DENIED = new URI(authServiceBaseURL + "permissionDenied.html");
        } catch (URISyntaxException e) {
            L.error("Unable to setup login or permission denied URI objects", e);
        }
    }

    @Override
    public ComponentScope getScope() {
        return ComponentScope.PerRequest;
    }

    @Override
    public Injectable<?> getInjectable(ComponentContext ic,
                                       AuthRequirements authRequirements,
                                       Parameter c) {
        return new DummyHttpContextInjectable<T>(authRequirements, authorizer);
    }

}
