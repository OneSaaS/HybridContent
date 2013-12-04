package net.twomini.hybridcontent.auth.dummy;

import com.google.common.base.Optional;
import com.yammer.dropwizard.auth.AuthenticationException;
import com.yammer.dropwizard.auth.Authenticator;
import net.twomini.hybridcontent.auth.AuthCaller;
import net.twomini.hybridcontent.auth.HttpRequestDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

public class DummyAuthenticator implements Authenticator<HttpRequestDetails, AuthCaller> {

    private static final Logger L = LoggerFactory.getLogger(DummyAuthenticator.class);

    public DummyAuthenticator() {
    }

    @Override
    public Optional<AuthCaller> authenticate(HttpRequestDetails c) throws AuthenticationException {
        return Optional.absent();
    }

}
