package N18.haui.Pet_18.configuration;


import N18.haui.Pet_18.constant.UrlConstant;
import N18.haui.Pet_18.security.jwt.JwtCustomAuthenticationEntryPoint;
import N18.haui.Pet_18.security.jwt.JwtPreFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@RequiredArgsConstructor
@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
    private final JwtPreFilter jwtPreFilter;
    private final JwtCustomAuthenticationEntryPoint jwtCustomAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> {

                    // ========== AUTH & SECURITY MODULE (PUBLIC) ==========
                    auth.requestMatchers(
                            HttpMethod.POST, "/api/v1" + UrlConstant.Auth.REGISTER,
                            "/api/v1" + UrlConstant.Auth.LOGIN
                    ).permitAll();


                    auth.requestMatchers(HttpMethod.GET, "/api/v1/categories/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/services/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/menus/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/api/v1/product-images/**").permitAll();
                    auth.requestMatchers(HttpMethod.GET, "/upload/**").permitAll();

                    // Booking module
                    auth.requestMatchers(HttpMethod.GET, "/api/v1" + UrlConstant.Booking.GET_BOOKED_TIMES).permitAll();

                    //
                    auth.requestMatchers(HttpMethod.GET, "/api/v1" + UrlConstant.PetServiceImages.GET_SERVICE_IMAGES).permitAll();
                    // Public: Payment callback (handled by PermissionInterceptor whitelist)
                    auth.requestMatchers(
                            "/api/v1" + UrlConstant.Payment.HANDLE_RETURN
                    ).permitAll();


                    // Tất cả request còn lại cần đăng nhập
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(manager -> manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtCustomAuthenticationEntryPoint))
                .addFilterBefore(jwtPreFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
