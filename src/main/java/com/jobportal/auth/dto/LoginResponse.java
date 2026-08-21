package com.jobportal.auth.dto;

public record LoginResponse(String accessToken, String refreshToken) {
}