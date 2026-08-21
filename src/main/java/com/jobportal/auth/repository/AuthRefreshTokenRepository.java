package com.jobportal.auth.repository;

import com.jobportal.auth.entity.AuthRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import java.util.List;
import java.util.Optional;


public interface AuthRefreshTokenRepository extends JpaRepository<AuthRefreshToken, Long> {
    Optional<AuthRefreshToken> findByTokenHash(String tokenHash);
    List<AuthRefreshToken> findByUserIdAndRevokedFalse(UUID userId);
}