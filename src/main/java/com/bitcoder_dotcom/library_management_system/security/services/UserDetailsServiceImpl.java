package com.bitcoder_dotcom.library_management_system.security.services;

import com.bitcoder_dotcom.library_management_system.models.Librarian;
import com.bitcoder_dotcom.library_management_system.models.Patron;
import com.bitcoder_dotcom.library_management_system.models.User;
import com.bitcoder_dotcom.library_management_system.repository.LibrarianRepository;
import com.bitcoder_dotcom.library_management_system.repository.PatronRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class UserDetailsServiceImpl implements UserDetailsService {

    private final LibrarianRepository librarianRepository;
    private final PatronRepository patronRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("Trying to get user by email");
        Librarian librarian = librarianRepository.findByEmail(email)
                .orElse(null);

        if (librarian != null) {
            log.info("Found LIBRARIAN user: {}", email);
            return UserDetailsImpl.build(librarian);
        } else {
            log.info("LIBRARIAN not found. Checking for PATRON: {}", email);
        }

        Patron patron = patronRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("Patron not found with email: {}", email);
                    return new UsernameNotFoundException("Patron not found with email: " + email);
                });

        log.info("Found PATRON user: {}", email);
        return buildUserDetails(patron);
    }

    private UserDetails buildUserDetails(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user instanceof Librarian) {
            authorities.add(new SimpleGrantedAuthority("LIBRARIAN"));
        } else if (user instanceof Patron) {
            authorities.add(new SimpleGrantedAuthority("PATRON"));
        }
        log.info("Building UserDetails for user: {}", user.getName());
        return new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}
