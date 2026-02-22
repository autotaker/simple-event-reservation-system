package com.event.auth;

import java.security.Principal;

public record AdminAuthenticationPrincipal(String operatorId) implements Principal {

    @Override
    public String getName() {
        return operatorId;
    }
}
