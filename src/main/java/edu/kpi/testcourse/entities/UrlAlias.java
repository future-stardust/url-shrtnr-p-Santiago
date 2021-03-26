package edu.kpi.testcourse.entities;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A single shortened URL.
 *
 * @param alias The unique short identifier of this URL. Prepend <tt>https://example.org/r/</tt> to get the short URL.
 * @param destinationUrl full version of URL
 * @param email an email of user that created this alias
 */
public record UrlAlias(
    @JsonProperty("alias") String alias,
    @JsonProperty("destinationUrl") String destinationUrl,
    @JsonProperty("email") String email
) {}
