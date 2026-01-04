package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.entity.KonsignUser;
import com.razdeep.konsignapi.exception.UsernameAlreadyExists;
import com.razdeep.konsignapi.model.UserRegistration;
import com.razdeep.konsignapi.repository.KonsignUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final KonsignUserRepository konsignUserRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthenticationService(
            KonsignUserRepository konsignUserRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.konsignUserRepository = konsignUserRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public void register(UserRegistration userRegistration) throws UsernameAlreadyExists {
        if (konsignUserRepository
                .findKonsignUserByUsername(userRegistration.getUsername())
                .isPresent()) {
            throw new UsernameAlreadyExists();
        }
        KonsignUser konsignUser = new KonsignUser(userRegistration);
        konsignUser.setPassword(bCryptPasswordEncoder.encode(konsignUser.getPassword()));
        konsignUserRepository.save(konsignUser);
    }
}
