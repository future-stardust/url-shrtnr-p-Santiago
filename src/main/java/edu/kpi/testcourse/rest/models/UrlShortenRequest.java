package edu.kpi.testcourse.rest.models;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * URL shorten request with long and short URLs.
 *
 * @param url a full version of URL
 * @param alias an alias ({base URL shortener URL}/{alias})
 */
public record UrlShortenRequest(
    @JsonProperty("url") String url,
    @JsonProperty("alias") String alias) {
}
