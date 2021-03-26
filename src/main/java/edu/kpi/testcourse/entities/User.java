package edu.kpi.testcourse.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * User profile, sufficient for signing in. User-generated content is stored separately.
 */
public record User(
    @JsonProperty("email") String email,
    @JsonProperty("passwordHash") String passwordHash
) {}
