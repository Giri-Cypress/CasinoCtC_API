package com.CasinoCtC.CCtCAPI.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.config.Customizer;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.CasinoCtC.CCtCAPI.security.JwtAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    // ✅ Constructor Injection
    public SecurityConfig(
            JwtAuthenticationFilter jwtFilter) {

        this.jwtFilter = jwtFilter;
    }

    /**
     * ✅ Security Filter Chain
     */
    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http

            // ✅ Enable CORS
            .cors(cors -> {})

            // ✅ Disable CSRF
            .csrf(csrf -> csrf.disable())

            // ✅ Stateless JWT Authentication
            .sessionManagement(session ->
                    session.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )

            // ✅ API Authorization Rules
            .authorizeHttpRequests(auth -> auth

                    // ✅ Public APIs
                    .requestMatchers(
                            "/api/auth/**",
                            "/encode/**"
                    ).permitAll()

                    // ✅ Secure everything else
                    .anyRequest()
                    .authenticated()
            )

            // ✅ JWT Filter
            .addFilterBefore(
                    jwtFilter,
                    UsernamePasswordAuthenticationFilter.class
            )

            // ✅ Optional Basic Auth
            .httpBasic(
                    Customizer.withDefaults()
            );

        return http.build();
    }

    /**
     * ✅ CORS Configuration
     */
    @Bean
    CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration =
                new CorsConfiguration();

        // ✅ Angular URL
        configuration.setAllowedOrigins(
                List.of(
                        "http://localhost:4200"
                )
        );

        // ✅ Allowed HTTP Methods
        configuration.setAllowedMethods(
                List.of(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS"
                )
        );

        // ✅ Allow All Headers
        configuration.setAllowedHeaders(
                List.of("*")
        );

        // ✅ Allow Credentials
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                configuration
        );

        return source;
    }
}