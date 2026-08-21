package com.jobportal.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import com.jobportal.auth.jwt.JwtAuthenticationFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/**
 * Security configuration for the stateless JWT API.
 *
 * <p>The chain is secure-by-default: {@code anyRequest().authenticated()} means any endpoint not
 * explicitly listed below requires authentication. Opening a new endpoint to anonymous traffic is
 * therefore always a deliberate edit to this file.</p>
 *
 * <p>Publicly reachable paths:</p>
 * <ul>
 *   <li>{@code /api/auth/**} — the whole auth surface (register, login, and any future endpoint
 *       added under that prefix)</li>
 *   <li>{@code /actuator/health} — liveness probing</li>
 * </ul>
 *
 * <p>Spring Security's form-login and {@code AuthenticationManager} machinery is deliberately not
 * configured. Authorization is Spring's job; authentication will be handled by a custom JWT filter.
 * Until that filter exists, no request can establish a principal, so every path outside the two
 * matchers above is rejected unconditionally.</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtFilter) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}