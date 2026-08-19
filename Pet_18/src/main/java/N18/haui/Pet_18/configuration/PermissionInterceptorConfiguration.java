package N18.haui.Pet_18.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import N18.haui.Pet_18.constant.UrlConstant;

@Configuration
public class PermissionInterceptorConfiguration implements WebMvcConfigurer {

    @Bean
    PermissionInterceptor getPermissionInterceptor() {
        return new PermissionInterceptor();
        // Lấy Bean đã được tạo ở Bước 1 ra sử dụng
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String prefix = "/api/v1";

        String[] white_list = {
                // 1. Tài nguyên tĩnh và hệ thống (Không bao giờ cần check quyền)
                "/", "/index.html", "/favicon.ico",
                "/storage/**", "/uploads/**", "/upload/**", "/images/**",
                "/actuator/health", "/actuator/info",
                "/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**",

                // 2. Auth (Cổng vào)
                prefix + UrlConstant.Auth.REGISTER,
                prefix + UrlConstant.Auth.LOGIN,
                prefix + UrlConstant.ForgetPassword.PREFIX + "/**", // Bỏ chặn toàn bộ luồng quên mật khẩu

                // 3. Các API public cố định
                prefix + UrlConstant.Payment.HANDLE_RETURN // Callback từ VNPAY thường gọi không token
        };
        registry.addInterceptor(getPermissionInterceptor())
                .excludePathPatterns(white_list); // ngoại trừ các API public không cần check quyền
    }
}
