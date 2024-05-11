package com.bitcoder_dotcom.library_management_system.repository;

import com.bitcoder_dotcom.library_management_system.models.Patron;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatronRepository extends JpaRepository<Patron, String> {
    Optional<Patron> findByEmail(String email);
}
