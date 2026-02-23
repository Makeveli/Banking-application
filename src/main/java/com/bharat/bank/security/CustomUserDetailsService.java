package com.bharat.bank.security;

import com.bharat.bank.auth_users.entity.User;
import com.bharat.bank.auth_users.repo.UserRepository;
import com.bharat.bank.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username)
                .orElseThrow(()-> new NotFoundException("Email Not found"));
        return AuthUser.builder()
                .user(user)
                .build();

     }
}
