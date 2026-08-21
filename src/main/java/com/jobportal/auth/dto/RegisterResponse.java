package com.jobportal.auth.dto;

import java.util.UUID;

public record RegisterResponse(UUID userId, String email) {
}