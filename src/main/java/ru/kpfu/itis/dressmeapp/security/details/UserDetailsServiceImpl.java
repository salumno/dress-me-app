package ru.kpfu.itis.dressmeapp.security.details;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kpfu.itis.dressmeapp.model.UserData;
import ru.kpfu.itis.dressmeapp.repositories.UserDataRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private UserDataRepository usersRepository;

    @Autowired
    public UserDetailsServiceImpl(UserDataRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        UserData user = usersRepository.findOneByLogin(login).orElseThrow(()
                -> new IllegalArgumentException("UserData not found by login <" + login + ">"));
        return new UserDetailsImpl(user);
    }
}