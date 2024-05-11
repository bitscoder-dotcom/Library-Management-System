package com.bitcoder_dotcom.library_management_system.repository;


import com.bitcoder_dotcom.library_management_system.models.Librarian;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LibrarianRepository extends JpaRepository<Librarian, String> {

    Optional<Librarian> findByEmail(String email);
}
