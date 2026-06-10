package com.ceylonechocolate.chocolate_factory_api.security;

import com.ceylonechocolate.chocolate_factory_api.entity.User;
import com.ceylonechocolate.chocolate_factory_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository
                .findByEmailAndIsDeletedFalse(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with email: " + email
                ));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPasswordHash(),
                getAuthorities(user)
        );
    }

    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // Add role as authority
        Set<GrantedAuthority> authorities = user.getRole()
                .getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(
                        permission.getModule() + "_" + permission.getAction()
                ))
                .collect(Collectors.toSet());

        // Add role name as authority too
        authorities.add(new SimpleGrantedAuthority(
                "ROLE_" + user.getRole().getName()
        ));

        return authorities;
    }
}