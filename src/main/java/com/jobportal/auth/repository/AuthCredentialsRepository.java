package com.jobportal.auth.repository;

import com.jobportal.auth.entity.AuthCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthCredentialsRepository extends JpaRepository<AuthCredentials, Long> {
    Optional<AuthCredentials> findByEmail(String email);
    boolean existsByEmail(String email);
}