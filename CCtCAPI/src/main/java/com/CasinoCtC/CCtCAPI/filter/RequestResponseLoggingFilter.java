package com.CasinoCtC.CCtCAPI.filter;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestResponseLoggingFilter
        extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        long startTime = System.currentTimeMillis();

        // ✅ Request Log
        System.out.println("\n========== REQUEST ==========");
        System.out.println("Method : " + request.getMethod());
        System.out.println("URI    : " + request.getRequestURI());
        System.out.println(
                "Auth   : "
                + request.getHeader("Authorization")
        );

        // ✅ Continue Request
        filterChain.doFilter(request, response);

        long duration =
                System.currentTimeMillis() - startTime;

        // ✅ Response Log
        System.out.println("========== RESPONSE ==========");
        System.out.println("Status : " + response.getStatus());
        System.out.println("Time   : " + duration + " ms");
        System.out.println("==============================\n");
    }
}