package net.twomini.hybridcontent.auth;

import net.twomini.hybridcontent.auth.exception.AuthLoginRequiredException;
import net.twomini.hybridcontent.auth.exception.AuthPermissionDeniedException;

import java.util.List;

public class Authorizer {

    private final List<AuthUrlRule> urlRequirements;

    public Authorizer(List<AuthUrlRule> urlRequirements) {
        this.urlRequirements = urlRequirements;
    }

    public void authorize(AuthCaller caller, AuthRequirements authAnnotation) throws AuthLoginRequiredException, AuthPermissionDeniedException {
        authorize(caller, authAnnotation.requireLoggedIn(), authAnnotation.requireRoles());
    }

    public void authorize(AuthCaller caller, String uri) throws AuthLoginRequiredException, AuthPermissionDeniedException {
        if (uri != null && urlRequirements != null) {
            for (AuthUrlRule requirement : urlRequirements) {
                if (uri.startsWith(requirement.getUrlStartsWith())) {
                    authorize(caller, requirement.isMustBeLoggedIn(), requirement.getMustHaveRoles());
                    break;
                }
            }
        }
    }

    private void authorize(AuthCaller caller, boolean requireLoggedIn, String[] requireRoles) throws AuthLoginRequiredException, AuthPermissionDeniedException {
        if (requireLoggedIn) {
            //Check Logged In
            if (caller == null) {
                throw new AuthLoginRequiredException();
            }

            if (requireRoles != null && requireRoles.length>0 && !caller.hasRoles(requireRoles)) {
                throw new AuthPermissionDeniedException();
            }

        }
    }

}
