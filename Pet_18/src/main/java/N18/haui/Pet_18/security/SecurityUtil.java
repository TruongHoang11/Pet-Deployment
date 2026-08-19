package N18.haui.Pet_18.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

public final class SecurityUtil {


    private SecurityUtil() {
        throw new IllegalStateException("Utility class");
    }


    public static Optional<String> getCurrentUserLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        return Optional.ofNullable(extractPrincipal(securityContext.getAuthentication()));
    }


    private static String extractPrincipal(Authentication authentication) {
        if (authentication == null) {
            return null;
        }
        else if (authentication.getPrincipal() instanceof UserPrincipal springSecurityUser) {
            return springSecurityUser.getId();
        }
        else if (authentication.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        else if (authentication.getPrincipal() instanceof String s) {
            return s;
        }

        return null;
    }
}
