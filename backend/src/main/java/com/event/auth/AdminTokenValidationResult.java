package com.event.auth;

public record AdminTokenValidationResult(AdminTokenState state, String operatorId) {

    public boolean isValid() {
        return state == AdminTokenState.VALID && operatorId != null && !operatorId.isBlank();
    }
}
