package com.olacabs.shuttle;

/**
 * Puppeteer Exception wrapper
 * Created by harshit.pathak on 23/02/17.
 */
class PuppeteerException {

    static class InvalidPathException extends Exception {
        InvalidPathException(String message) {
            super(message);
        }
    }

    static class NoValueForKeyException extends Exception {
        NoValueForKeyException(String message) {
            super(message);
        }
    }
    static class UninitializedException extends Exception {
        UninitializedException(String message) {
            super(message);
        }
    }
}
