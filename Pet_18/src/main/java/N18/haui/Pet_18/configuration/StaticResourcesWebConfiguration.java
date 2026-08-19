package N18.haui.Pet_18.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class StaticResourcesWebConfiguration implements WebMvcConfigurer {

    // Đổi từ base-uri sang resource-location
    @Value("${hoang.upload-file.resource-location}")
    private String resourceLocation;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = resourceLocation.endsWith("/") ? resourceLocation : resourceLocation + "/";

        registry.addResourceHandler("/upload/**")
                .addResourceLocations(location);
    }
}