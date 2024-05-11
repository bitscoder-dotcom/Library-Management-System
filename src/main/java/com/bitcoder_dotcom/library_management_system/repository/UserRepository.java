package com.bitcoder_dotcom.library_management_system.repository;

import com.bitcoder_dotcom.library_management_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByName(String username);

    boolean existsByEmail(String email);

    Optional<User> findByEmail(String name);
}
