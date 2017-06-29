package com.example;

/**
 * Puppeteer Exception wrapper
 * Created by harshit.pathak on 23/02/17.
 */
public class PuppeteerException {

    public static class InvalidPathException extends Exception {
        InvalidPathException(String message) {
            super(message);
        }
    }

    public static class NoValueForKeyException extends Exception {
        NoValueForKeyException(String message) {
            super(message);
        }
    }
    public static class UninitializedException extends Exception {
        UninitializedException(String message) {
            super(message);
        }
    }
}
