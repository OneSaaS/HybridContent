package net.twomini.hybridcontent.auth;

public class AuthUrlRule {

    private String urlStartsWith;
    private boolean mustBeLoggedIn;
    private String[] mustHaveRoles;

    public AuthUrlRule(String urlStartsWith, boolean mustBeLoggedIn, String... mustHaveRoles) {
        this.urlStartsWith = urlStartsWith;
        this.mustBeLoggedIn = mustBeLoggedIn;
        this.mustHaveRoles = mustHaveRoles;
    }

    public String getUrlStartsWith() {
        return urlStartsWith;
    }

    public boolean isMustBeLoggedIn() {
        return mustBeLoggedIn;
    }

    public String[] getMustHaveRoles() {
        return mustHaveRoles;
    }
}
