package ru.kpfu.itis.dressmeapp.services.impl;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.dressmeapp.model.UserData;
import ru.kpfu.itis.dressmeapp.repositories.UserDataRepository;
import ru.kpfu.itis.dressmeapp.security.details.UserDetailsImpl;
import ru.kpfu.itis.dressmeapp.services.AuthenticationService;

/**
 * Created by Melnikov Semen
 * 11-601 ITIS KPFU
 */

@Component
public class AuthenticationServiceImpl implements AuthenticationService {

    private UserDataRepository userDataRepository;

    public AuthenticationServiceImpl(UserDataRepository userDataRepository) {
        this.userDataRepository = userDataRepository;
    }

    @Override
    public UserData getUserByAuthentication(Authentication authentication) {
        UserDetailsImpl currentUserDetails = (UserDetailsImpl)authentication.getPrincipal();
        UserData currentUserModel = currentUserDetails.getUser();
        Long currentUserId = currentUserModel.getId();
        return userDataRepository.findOne(currentUserId);
    }
}
