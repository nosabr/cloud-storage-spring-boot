package org.example.cloudstorage1.service;

import org.example.cloudstorage1.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .map(user -> {
                    System.out.println("Loading user: " + user.getUsername());
                    System.out.println("Role: " + user.getRole());
                    System.out.println("Enabled: " + user.getEnabled());

                    return new org.springframework.security.core.userdetails.User(
                            user.getUsername(),
                            user.getPassword(),
                            user.getEnabled(),  // ← добавь enabled!
                            true,  // accountNonExpired
                            true,  // credentialsNonExpired
                            true,  // accountNonLocked
                            Collections.singletonList(user.getRole())
                    );
                })
                .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
