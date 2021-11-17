package com.mirrors.mirrorsbackend.utils.security;

import org.springframework.context.annotation.Bean;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import java.nio.charset.StandardCharsets;

public class MultipartConfiguration {
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        int mbInBytes = 1_048_576;
        resolver.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        resolver.setMaxUploadSize(mbInBytes * 4 * 8);
        resolver.setMaxUploadSizePerFile(mbInBytes * 4);
        return resolver;
    }
}
