package com.jobportal.auth.event;

import java.util.UUID;

/**
 * Published by the auth module after a new user's credentials are persisted.
 *
 * <p>Other modules (e.g. users) listen for this to build their own view of the user without auth
 * exposing its repository or entities directly.</p>
 */
public record UserRegistered(UUID userId, String email) {
}