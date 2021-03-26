package edu.kpi.testcourse.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Error descriptor for API responses.
 */
public record ErrorResponse(
    @JsonProperty("reason_code") int reasonCode,
    @JsonProperty("reason_text") String reasonText) {
}
