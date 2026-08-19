package N18.haui.Pet_18.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        //cho phep nguon url nao
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000","http://localhost:5173","http://localhost:91"));
        // cho phep method nao
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // Allowed methods
        // cho phep header nao
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "x-no-retry"));

        // cho phep gui cookie/token hay khong
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        // How long the response from a pre-flight request can be cached by clients
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Đăng ký cấu hình cho toàn bộ các path trong project
        //Bạn chỉ cần cấu hình một lần duy nhất cho toàn bộ hệ thống
        // thay vì phải viết @CrossOrigin trên từng Controller.
        source.registerCorsConfiguration("/**", configuration);
        // Apply this configuration to all paths
        return source;
    }
}
