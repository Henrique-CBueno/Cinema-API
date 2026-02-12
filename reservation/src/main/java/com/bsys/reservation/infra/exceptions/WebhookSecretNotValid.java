package com.bsys.reservation.infra.exceptions;

public class WebhookSecretNotValid extends RuntimeException {
    public WebhookSecretNotValid(String message) {
        super(message);
    }
}
