package net.twomini.hybridcontent.auth;

import java.util.List;

public class AuthCaller {

    private String userName;
    private String userDisplayName;
    private List<String> roles;
    private String serviceName;

    public AuthCaller(String userName, String userDisplayName, List<String> roles, String serviceName) {
        this.userName = userName;
        this.userDisplayName = userDisplayName;
        this.roles = roles;
        this.serviceName = serviceName;
    }

    public boolean isUser() {
        return userName != null;
    }

    public boolean isService() {
        return serviceName != null;
    }

    public boolean hasRoles(String... requiredRoles) {
        if (requiredRoles!=null && requiredRoles.length>0) {
            if (roles == null || roles.size()<1) {
                return false;
            }
            for (String requiredRole : requiredRoles) {
                boolean hasRequiredRole = false;
                for (String role : roles) {
                    if (role.equalsIgnoreCase(requiredRole)) {
                        hasRequiredRole = true;
                        break;
                    }
                }
                if (!hasRequiredRole) {
                    return false;
                }
            }
        }
        return true;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserDisplayName() {
        return userDisplayName;
    }

    public List<String> getRoles() {
        return roles;
    }

    public String getServiceName() {
        return serviceName;
    }
}
