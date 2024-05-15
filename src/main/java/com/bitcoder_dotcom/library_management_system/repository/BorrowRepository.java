package com.bitcoder_dotcom.library_management_system.repository;

import com.bitcoder_dotcom.library_management_system.models.Book;
import com.bitcoder_dotcom.library_management_system.models.Borrow;
import com.bitcoder_dotcom.library_management_system.models.Patron;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BorrowRepository extends JpaRepository<Borrow, String> {

    List<Borrow> findByPatron(Patron patron);
}
