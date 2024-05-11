package com.bitcoder_dotcom.library_management_system.security.services;

import com.bitcoder_dotcom.library_management_system.constant.Roles;
import com.bitcoder_dotcom.library_management_system.models.Librarian;
import com.bitcoder_dotcom.library_management_system.models.Patron;
import com.bitcoder_dotcom.library_management_system.models.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
public class UserDetailsImpl implements UserDetails {

    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String email;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImpl(
            String id, String email, String password,
            List<GrantedAuthority> authorities) {
        this.id = id;
        this.name = email;
        this.email = email;
        this.password = password;
        this.authorities = authorities;

        logUserDetailsCreation();
    }

    public static UserDetailsImpl build(User user) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        if (user instanceof Librarian) {
            authorities.add(new SimpleGrantedAuthority(Roles.LIBRARIAN.name()));
        } else if (user instanceof Patron) {
            authorities.add(new SimpleGrantedAuthority(Roles.PATRON.name()));
        }

        UserDetailsImpl userDetails = new UserDetailsImpl(
                user.getId(),
                user.getEmail(),
                user.getPassword(),
                authorities
        );
        log.info("UserDetailsImpl built for user: {}", userDetails.getUsername());
        log.debug("UserDetailsImpl: {}", userDetails);

        return userDetails;
    }

    private void logUserDetailsCreation() {
        log.info("UserDetails created for user: {}", name);
        log.debug("UserDetails: {}", this);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        UserDetailsImpl user = (UserDetailsImpl) obj;
        return Objects.equals(id, user.id);
    }

    @Override
    public String toString() {
        return "UserDetailsImpl{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", authorities=" + authorities +
                '}';
    }
}
