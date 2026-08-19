package N18.haui.Pet_18.configuration;


import N18.haui.Pet_18.security.UserPrincipal;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class AuditingConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return new AuditorAwareImpl();
    }


    public class AuditorAwareImpl implements AuditorAware<String> {

        @Override
        public Optional<String> getCurrentAuditor() {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Bỏ qua nếu null, không xác thực, hoặc là anonymous
            if (authentication == null
                    || authentication instanceof AnonymousAuthenticationToken
                    || !authentication.isAuthenticated()) {
                return Optional.empty();
            }

            try {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                return Optional.ofNullable(userPrincipal.getId());
            } catch (Exception e) {
                // Nếu lỗi bất kỳ, bỏ qua audit
                return Optional.empty();
            }
        }
    }
}
