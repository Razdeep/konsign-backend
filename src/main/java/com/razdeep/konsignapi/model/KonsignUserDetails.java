package com.razdeep.konsignapi.model;

import com.razdeep.konsignapi.entity.KonsignUser;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class KonsignUserDetails implements UserDetails {
    private final String username;
    private final String password;

    private final String agencyId;

    private final boolean enabled;
    private final List<GrantedAuthority> authorities;

    public KonsignUserDetails(KonsignUser konsignUser) {
        username = konsignUser.getUsername();
        password = konsignUser.getPassword();
        enabled = konsignUser.isActive();
        agencyId = konsignUser.getAgencyId();
        authorities = Arrays.stream(konsignUser.getRoles().split(","))
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return enabled;
    }

    @Override
    public boolean isAccountNonLocked() {
        return enabled;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getAgencyId() {
        return agencyId;
    }
}
