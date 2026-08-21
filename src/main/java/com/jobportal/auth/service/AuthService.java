package com.jobportal.auth.service;

import com.jobportal.auth.dto.RegisterRequest;
import com.jobportal.auth.dto.RegisterResponse;
import com.jobportal.auth.entity.AuthCredentials;
import com.jobportal.auth.event.UserRegistered;
import com.jobportal.auth.repository.AuthCredentialsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.jobportal.auth.exception.EmailAlreadyRegisteredException;

import com.jobportal.auth.dto.LoginRequest;
import com.jobportal.auth.dto.LoginResponse;
import com.jobportal.auth.entity.AuthRefreshToken;
import com.jobportal.auth.exception.InvalidCredentialsException;
import com.jobportal.auth.jwt.JwtService;
import com.jobportal.auth.jwt.RefreshTokenHasher;
import com.jobportal.auth.repository.AuthRefreshTokenRepository;

import java.time.LocalDateTime;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthCredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher events;

    private final AuthRefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;
    private final RefreshTokenHasher tokenHasher;

    @org.springframework.beans.factory.annotation.Value("${jwt.refresh-token-expiration-days}")
    private long refreshTokenExpirationDays;

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (credentialsRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyRegisteredException(request.email());
        }

        var credentials = new AuthCredentials();
        credentials.setUserId(generateUserId());
        credentials.setEmail(request.email());
        credentials.setPasswordHash(passwordEncoder.encode(request.password()));

        try {
            credentialsRepository.save(credentials);
        } catch (DataIntegrityViolationException e) {
            // Race: two concurrent registrations for the same email both passed the
            // existsByEmail check above before either committed. The unique constraint
            // on auth_credentials.email is the real guard; this just turns the raw DB
            // exception into our own domain-meaningful one.
            throw new EmailAlreadyRegisteredException(request.email());
        }

        events.publishEvent(new UserRegistered(credentials.getUserId(), credentials.getEmail()));

        return new RegisterResponse(credentials.getUserId(), credentials.getEmail());
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        var credentials = credentialsRepository.findByEmail(request.email())
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(request.password(), credentials.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        String accessToken = jwtService.generateAccessToken(credentials.getUserId(), credentials.getEmail());

        String rawRefreshToken = tokenHasher.generateRawToken();
        var refreshToken = new AuthRefreshToken();
        refreshToken.setUserId(credentials.getUserId());
        refreshToken.setTokenHash(tokenHasher.hash(rawRefreshToken));
        refreshToken.setExpiresAt(LocalDateTime.now().plusDays(refreshTokenExpirationDays));
        refreshTokenRepository.save(refreshToken);

        return new LoginResponse(accessToken, rawRefreshToken);
    }

    private UUID generateUserId() {
        return UUID.randomUUID();
    }
}