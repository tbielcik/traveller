package com.bielcik.traveller.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
@Component
@Slf4j
public class TokenAuthProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
        String name = auth.getName();
        String password = auth.getCredentials().toString();
        log.info("Authenticating keystone user: {} ", name);

        if (password == null) {
            return null;
        }
        // TODO: logic to verify username, password
        if (password.contains("Admin")) {
            log.info("Admin user {} logged in successfully.", name);
            UserDetails user = User.withUsername(name).password(password).authorities("adminRole").build();
            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        } else if (password.contains("User")) {
            log.info("User {} logged in successfully.", name);
            UserDetails user = User.withUsername(name).password(password).authorities("userRole").build();
            return new UsernamePasswordAuthenticationToken(user, password, user.getAuthorities());
        }

        log.error("Login failed -> Invalid credentials provided.");
        return UsernamePasswordAuthenticationToken.unauthenticated(name,"");

    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
