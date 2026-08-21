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

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthCredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher events;

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

    private UUID generateUserId() {
        return UUID.randomUUID();
    }
}