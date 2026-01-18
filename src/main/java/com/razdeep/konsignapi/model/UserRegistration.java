package com.razdeep.konsignapi.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class UserRegistration {
    @NonNull
    private String username;

    private String emailAddress;

    private String mobile;

    @NonNull
    private String password;

    @NonNull
    private String tenantId;
}
