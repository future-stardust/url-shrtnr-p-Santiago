package edu.kpi.testcourse.logic;

/**
 * Runtime server-side configuration for the URL shortener application.
 *
 * @param storageRoot Full path to the DB root directory.
 */
public record UrlShortenerConfig(java.nio.file.Path storageRoot) {
}
