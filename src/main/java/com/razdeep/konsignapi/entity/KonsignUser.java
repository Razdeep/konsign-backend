package com.razdeep.konsignapi.entity;

import com.razdeep.konsignapi.model.UserRegistration;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "User")
@Getter
@Setter
public class KonsignUser extends BaseTimestamp {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "username", unique = true)
    private String username;

    private String password;
    private String emailAddress;
    private String mobile;
    private String agencyId;
    private boolean active;
    private String roles;

    public KonsignUser() {}

    public KonsignUser(UserRegistration userRegistration) {
        username = userRegistration.getUsername();
        password = userRegistration.getPassword();
        emailAddress = userRegistration.getEmailAddress();
        mobile = userRegistration.getMobile();
        agencyId = userRegistration.getAgencyId();
        active = true;
        roles = "ROLE_USER";
    }
}
