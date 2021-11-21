package com.mirrors.mirrorsbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.DispatcherServlet;

@SpringBootApplication
public class MirrorsBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(MirrorsBackendApplication.class, args);
    }

}
