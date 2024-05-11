package com.bitcoder_dotcom.library_management_system.repository;

import com.bitcoder_dotcom.library_management_system.models.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, String> {
    List<Borrow> findByBookId(String id);
}
