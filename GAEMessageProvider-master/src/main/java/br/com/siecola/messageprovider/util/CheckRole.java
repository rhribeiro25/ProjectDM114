package br.com.siecola.messageprovider.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class CheckRole {
    public static final String ROLE_USER = "USER";
    public static final String ROLE_ADMIN = "ADMIN";

    private CheckRole(){}

    public static boolean hasRoleAdmin (Authentication authentication) {
        return hasRole(authentication, ROLE_ADMIN);
    }

    public static boolean hasRoleUser (Authentication authentication) {
        return hasRole(authentication, ROLE_USER);
    }

    private static boolean hasRole (Authentication authentication, String role) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        for (GrantedAuthority grantedAuthority : userDetails.getAuthorities()) {
            if (grantedAuthority.getAuthority().equals(role)) {
                return true;
            }
        }
        return false;
    }
}