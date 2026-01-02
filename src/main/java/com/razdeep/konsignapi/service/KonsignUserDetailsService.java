package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.KonsignUser;
import com.razdeep.konsignapi.model.KonsignUserDetails;
import com.razdeep.konsignapi.repository.KonsignUserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class KonsignUserDetailsService implements UserDetailsService {

    private final KonsignUserRepository konsignUserRepository;

    @Autowired
    public KonsignUserDetailsService(KonsignUserRepository konsignUserRepository) {
        this.konsignUserRepository = konsignUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<KonsignUser> konsignUser = konsignUserRepository.findKonsignUserByUsername(username);
        if (konsignUser.isEmpty()) {
            throw new UsernameNotFoundException("user name not found");
        }
        return new KonsignUserDetails(konsignUser.get());
    }
}
