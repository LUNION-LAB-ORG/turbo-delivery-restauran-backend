package com.lunionlab.turbo_restaurant.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.lunionlab.turbo_restaurant.services.AuthEntryPointService;
import com.lunionlab.turbo_restaurant.services.AuthFilterService;

@Configuration
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)
@EnableWebSecurity
public class WebSecureConfig {
    @Autowired
    AuthFilterService authFilterService;
    @Autowired
    AuthEntryPointService authEntryPointService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity security) throws Exception {
        security.cors(cor -> {

        }).csrf(csrfProtect -> {
            csrfProtect.disable();
        });

        security.formLogin(form -> {
            form.disable();
        });

        security.authorizeHttpRequests(auth -> {
            auth.requestMatchers("/test/**", "/error", "/api/V1/turbo/resto/user/login",
                    "/api/V1/turbo/resto/user/register/stepfirst", "/api/V1/turbo/resto/user/register/stepsecond",
                    "/api/V1/turbo/resto/user/register/finalstep", "/api/V1/turbo/resto/user/change/password",
                    "/api/V1/turbo/resto/user/forget/password", "/api/V1/turbo/resto/user/new/password",
                    "/api/turbo/resto/collection/get", "/api/turbo/resto/collection/add",
                    "/api/turbo/resto/collection/detail/**", "/api/turbo/resto/collection/update/**")
                    .permitAll().anyRequest().authenticated();
        });

        security.exceptionHandling(exception -> {
            exception.authenticationEntryPoint(authEntryPointService);
        });

        security.sessionManagement(session -> {
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        });

        security.addFilterBefore(authFilterService, UsernamePasswordAuthenticationFilter.class);
        return security.build();
    }
}
