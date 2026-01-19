package com.razdeep.konsignapi.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RefreshRequest(
        @NotBlank(message = "Refresh token must not be blank") @Size(min = 20, message = "Invalid refresh token")
        String refreshToken) {}
