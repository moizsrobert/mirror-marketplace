package com.mirrors.mirrorsbackend.util.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.nio.charset.StandardCharsets;

@Configuration
public class MultipartConfig {
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        resolver.setMaxUploadSize(-1);
        resolver.setMaxUploadSizePerFile(-1);
        resolver.setMaxInMemorySize(-1);
        return resolver;
    }
}
