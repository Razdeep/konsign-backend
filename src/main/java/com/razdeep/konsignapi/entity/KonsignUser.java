package com.razdeep.konsignapi.entity;

import com.razdeep.konsignapi.model.UserRegistration;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "app_user")
@Getter
@Setter
public class KonsignUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "app_user_seq_gen")
    @SequenceGenerator(name = "app_user_seq_gen", sequenceName = "app_user_seq", allocationSize = 1)
    private long id;

    @Column(name = "username", unique = true)
    private String username;

    private String password;
    private String email;
    private String mobile;
    private String tenantId;
    private boolean active;
    private String roles;

    public KonsignUser() {}

    public KonsignUser(UserRegistration userRegistration) {
        username = userRegistration.getUsername();
        password = userRegistration.getPassword();
        email = userRegistration.getEmail();
        mobile = userRegistration.getMobile();
        tenantId = userRegistration.getTenantId();
        active = true;
        roles = "ROLE_USER";
    }
}
