package com.CasinoCtC.CCtCAPI.security;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    // ✅ Constructor Injection
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {

        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // ✅ Read Authorization Header
        String authHeader =
                request.getHeader("Authorization");

        String token = null;
        String username = null;

        // ✅ Check Bearer token
        if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);

            try {

                // ✅ Extract username from token
                username =
                        jwtUtil.extractUsername(token);

            } catch (Exception ex) {

                System.out.println(
                        "Invalid JWT Token"
                );
            }
        }

        // ✅ Authenticate only if not already authenticated
        if (username != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            // ✅ Validate Token
            if (jwtUtil.validateToken(token)) {

                // ✅ Extract roles
                List<String> roles =
                        jwtUtil.extractRoles(token);

                // ✅ Convert Roles to Authorities
                var authorities =
                        roles.stream()
                                .map(role ->
                                        new SimpleGrantedAuthority(
                                                "ROLE_" + role
                                        )
                                )
                                .collect(Collectors.toList());

                // ✅ Create Authentication Token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                authorities
                        );

                // ✅ Attach request details
                authToken.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                // ✅ Set authentication in Spring Security
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authToken);
            }
        }

        // ✅ Continue filter chain
        filterChain.doFilter(request, response);
    }
}