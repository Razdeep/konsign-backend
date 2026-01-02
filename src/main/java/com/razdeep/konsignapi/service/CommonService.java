package com.razdeep.konsignapi.service;

import com.razdeep.konsignapi.model.KonsignUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CommonService {

    private static final int MAX_INITIAL_SIZE = 4;

    public CommonService() {}

    private boolean isVowel(char c) {
        return c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'A' || c == 'E' || c == 'I' || c == 'O'
                || c == 'U';
    }

    private boolean isAlphabet(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
    }

    public String generateInitials(String name) {
        if (name == null || name.equals("")) {
            return null;
        }

        int n = name.length();

        StringBuilder sb = new StringBuilder();
        sb.append(Character.toUpperCase(name.charAt(0)));

        for (int i = 1; i < n; ++i) {
            if (isAlphabet(name.charAt((i))) && !isVowel(name.charAt(i))) {
                sb.append(Character.toUpperCase(name.charAt(i)));
            }
            if (sb.length() >= MAX_INITIAL_SIZE) {
                break;
            }
        }

        return sb.toString();
    }

    public String getAgencyId() {
        KonsignUserDetails konsignUserDetails = (KonsignUserDetails)
                SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (konsignUserDetails == null) {
            return null;
        }
        return konsignUserDetails.getAgencyId();
    }
}
