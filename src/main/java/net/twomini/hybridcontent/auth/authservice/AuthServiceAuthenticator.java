package net.twomini.hybridcontent.auth.authservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import net.twomini.hybridcontent.auth.AuthCaller;
import net.twomini.hybridcontent.auth.HttpRequestDetails;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class AuthServiceAuthenticator implements Authenticator<HttpRequestDetails, AuthCaller> {

    private static final Logger L = LoggerFactory.getLogger(AuthServiceAuthenticator.class);

    private String authServiceBaseUrl;
    private String thisServiceId;
    private HttpClient httpClient;

    public AuthServiceAuthenticator(String authServiceBaseUrl, String thisServiceId, HttpClient httpClient) {
        this.authServiceBaseUrl = authServiceBaseUrl;
        this.thisServiceId = thisServiceId;
        this.httpClient = httpClient;
    }

    @Override
    public Optional<AuthCaller> authenticate(HttpRequestDetails req) throws AuthenticationException {
        final String userToken = req.cookies.get("authUser");
        final String serviceToken = req.headers.get("authService");

        if (userToken != null || serviceToken != null) {

            AuthCaller caller = null;
            if (userToken != null) {
                caller = authenticateUser(userToken);
            } else if (serviceToken != null) {
                caller = authenticateService(serviceToken);
            }

            if (caller != null) {
                return Optional.of(caller);
            } else {
                return Optional.absent();
            }
        }
        return Optional.absent();
    }

    private AuthCaller authenticateUser(String userToken) {
        HttpPost post;
        try {
            post = new HttpPost(authServiceBaseUrl + "getUserDetails");
            post.addHeader("serviceId", thisServiceId);
            post.addHeader("userSessionId", userToken);
            HttpResponse response = httpClient.execute(post);
            AuthServiceCallerDetails userDetails = new ObjectMapper().readValue(response.getEntity().getContent(), AuthServiceCallerDetails.class);
            return new AuthCaller(userDetails.getName(), userDetails.getDisplayName(), userDetails.getRoles(), null);
        } catch (IOException e) {
            L.error("Couldn't get User Details for userSessionId: " + userToken, e);
            return null;
        }
    }

    private AuthCaller authenticateService(String callingServiceId) {
        HttpPost post;
        try {
            post = new HttpPost(authServiceBaseUrl + "getServiceDetails");
            post.addHeader("serviceId", thisServiceId);
            post.addHeader("callingServiceId", callingServiceId);
            HttpResponse response = httpClient.execute(post);
            AuthServiceCallerDetails serviceDetails = new ObjectMapper().readValue(response.getEntity().getContent(), AuthServiceCallerDetails.class);
            return new AuthCaller(null, null, serviceDetails.getRoles(), serviceDetails.getName());
        } catch (IOException e) {
            L.error("Couldn't get Service Details for callingServiceId: " + callingServiceId, e);
            return null;
        }
    }

    private static class AuthServiceCallerDetails {

        private String name = null;

        private String displayName = null;

        private List<String> roles = null;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

    }

}
