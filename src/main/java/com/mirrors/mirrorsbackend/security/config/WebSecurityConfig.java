package com.mirrors.mirrorsbackend.security.config;

import com.mirrors.mirrorsbackend.marketplaceuser.MarketplaceUserService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@AllArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final MarketplaceUserService marketplaceUserService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final int rememberMeTokenValidity = 60 * 60 * 24;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                    .authorizeRequests()
                    .antMatchers("/api/**", "/css/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated()
                .and()
                    .formLogin()
                    .loginPage("/api/login")
                    .failureUrl("/api/login-failure")
                    .usernameParameter("email")
                    .passwordParameter("password")
                    .loginProcessingUrl("/api/login")
                    .defaultSuccessUrl("/home", true)
                    .failureUrl("/api/login?login_error")
                .and()
                    .logout()
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID")
                    .logoutUrl("/api/logout")
                    .permitAll()
                .and()
                .rememberMe()
                .rememberMeParameter("remember")
                .rememberMeCookieName("remember-me")
                .key("fe7a26f92564ccf64a7ff908765b7d819ee4f0bd0e3f7d7bd9c27b20ff776fb3")
                .tokenValiditySeconds(rememberMeTokenValidity);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder);
        provider.setUserDetailsService(marketplaceUserService);
        return provider;
    }
}