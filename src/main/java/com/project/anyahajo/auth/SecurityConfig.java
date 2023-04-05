package com.project.anyahajo.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests()
                .requestMatchers("/",
                        "/home",
                        "/register",
                        "/books",
                        "/kolcsonzes/**",
                        "/item/**",
                        "/forgot-password/**",
                        "/enable-user/**",
                        "/all-items/img/{id}"
                )
                .permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")

                .anyRequest().authenticated()
                .and()
                .formLogin()
                    .loginPage("/login")
                    .defaultSuccessUrl("/#menu")
                    .permitAll()
                .and()
                .logout().logoutSuccessUrl("/#menu").permitAll()
                .and();
        return http.build();
    }
}
