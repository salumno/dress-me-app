package ru.kpfu.itis.dressmeapp.security.providers;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kpfu.itis.dressmeapp.model.UserData;
import ru.kpfu.itis.dressmeapp.repositories.UserDataRepository;

import java.util.Collection;
import java.util.Optional;

@Component
public class AuthProvider implements AuthenticationProvider {

    private UserDataRepository userDataRepository;

    private UserDetailsService userDetailsService;

    private PasswordEncoder passwordEncoder;

    public AuthProvider(UserDataRepository userDataRepository, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDataRepository = userDataRepository;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        Optional<UserData> userOptional = userDataRepository.findOneByLogin(username);
		
        if (userOptional.isPresent()) {

            UserData user = userOptional.get();

            if (!passwordEncoder.matches(password, user.getHashPassword())) {

                throw new BadCredentialsException("Wrong password or login");
            }

        } else {
            throw new BadCredentialsException("Wrong password or login");
        }


        UserDetails details = userDetailsService.loadUserByUsername(username);
        Collection<? extends GrantedAuthority> authorities = details.getAuthorities();
        return new UsernamePasswordAuthenticationToken(details, password, authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}