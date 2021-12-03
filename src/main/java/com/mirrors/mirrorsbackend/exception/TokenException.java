package com.mirrors.mirrorsbackend.exception;

public class TokenException extends MarketplaceException {

    @XMLPresence
    private final String tokenAttempt;

    public TokenException(String message, String tokenAttempt) {
        super(message);
        this.tokenAttempt = tokenAttempt;
    }

    public String getTokenAttempt() {
        return this.tokenAttempt;
    }
}
