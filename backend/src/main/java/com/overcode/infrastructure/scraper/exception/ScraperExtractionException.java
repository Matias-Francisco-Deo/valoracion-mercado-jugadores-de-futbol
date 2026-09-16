package com.overcode.infrastructure.scraper.exception;

public class ScraperExtractionException extends RuntimeException {
    public ScraperExtractionException(String message) {
        super(message);
    }

    public ScraperExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}
