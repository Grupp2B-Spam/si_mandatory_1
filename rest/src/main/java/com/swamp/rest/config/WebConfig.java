package com.swamp.rest.config;

import com.swamp.rest.security.ApiHeaderInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ApiHeaderInterceptor apiHeaderInterceptor;

    public WebConfig(ApiHeaderInterceptor apiHeaderInterceptor) {
        this.apiHeaderInterceptor = apiHeaderInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(apiHeaderInterceptor)
                .addPathPatterns("/api/**");
    }
}
