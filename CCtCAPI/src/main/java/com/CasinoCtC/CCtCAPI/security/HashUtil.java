package com.CasinoCtC.CCtCAPI.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.stereotype.Component;

@Component
public class HashUtil {

    public String sha256(String value) {

        try {

            MessageDigest md =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    md.digest(
                            value.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder sb = new StringBuilder();

            for (byte b : hash) {

                sb.append(
                        String.format("%02x", b)
                );
            }

            return sb.toString();

        } catch (Exception ex) {

            throw new RuntimeException(
                    "Hash generation failed",
                    ex
            );
        }
    }
}