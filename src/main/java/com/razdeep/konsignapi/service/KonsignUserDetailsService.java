package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.KonsignUser;
import com.razdeep.konsignapi.exception.UserNotFoundException;
import com.razdeep.konsignapi.model.KonsignUserDetails;
import com.razdeep.konsignapi.repository.KonsignUserRepository;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class KonsignUserDetailsService implements UserDetailsService {

    private final KonsignUserRepository konsignUserRepository;

    public KonsignUserDetailsService(KonsignUserRepository konsignUserRepository) {
        this.konsignUserRepository = konsignUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException {
        Optional<KonsignUser> konsignUser = konsignUserRepository.findKonsignUserByUsername(username);
        if (konsignUser.isEmpty()) {
            throw new UserNotFoundException("user name not found");
        }
        return new KonsignUserDetails(konsignUser.get());
    }

    public UserDetails loadUserByUserId(long userId) throws UserNotFoundException {
        Optional<KonsignUser> konsignUser = konsignUserRepository.findKonsignUserById(userId);
        if (konsignUser.isEmpty()) {
            throw new UserNotFoundException("user not found");
        }
        return new KonsignUserDetails(konsignUser.get());
    }
}
