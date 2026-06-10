package com.CasinoCtC.CCtCAPI.dao;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.CasinoCtC.CCtCAPI.model.RefreshToken;

@Repository
public class RefreshTokenDAOImpl
        implements RefreshTokenDAO {

    private final JdbcTemplate jdbcTemplate;

    public RefreshTokenDAOImpl(
            JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(
            Integer userId,
            String tokenHash,
            Date expiryDate) {

        String sql = """
            INSERT INTO GSI.REFRESH_TOKENS
            (
                UserID,
                RefreshTokenHash,
                ExpiryDate
            )
            VALUES
            (
                ?,
                ?,
                ?
            )
        """;

        jdbcTemplate.update(
                sql,
                userId,
                tokenHash,
                expiryDate
        );
    }

    @Override
    public Optional<RefreshToken> findValidToken(
            String tokenHash) {

        String sql = """
            SELECT *
            FROM GSI.REFRESH_TOKENS
            WHERE RefreshTokenHash = ?
              AND Revoked = 0
              AND ExpiryDate > GETDATE()
        """;

        List<RefreshToken> tokens =
                jdbcTemplate.query(
                        sql,
                        (rs, rowNum) -> {

                            RefreshToken token =
                                    new RefreshToken();

                            token.setTokenId(
                                    rs.getInt("TokenID")
                            );

                            token.setUserId(
                                    rs.getInt("UserID")
                            );

                            token.setRefreshTokenHash(
                                    rs.getString(
                                            "RefreshTokenHash"
                                    )
                            );

                            token.setExpiryDate(
                                    rs.getTimestamp(
                                            "ExpiryDate"
                                    )
                            );

                            token.setRevoked(
                                    rs.getBoolean(
                                            "Revoked"
                                    )
                            );

                            return token;
                        },
                        tokenHash
                );

        return tokens.stream().findFirst();
    }

    @Override
    public void revokeToken(String tokenHash) {

        String sql = """
            UPDATE GSI.REFRESH_TOKENS
            SET Revoked = 1,
                RevokedDate = GETDATE()
            WHERE RefreshTokenHash = ?
        """;

        jdbcTemplate.update(sql, tokenHash);
    }
}