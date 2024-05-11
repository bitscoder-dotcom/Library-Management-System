package com.bitcoder_dotcom.library_management_system.repository;

import com.bitcoder_dotcom.library_management_system.models.Book;
import com.bitcoder_dotcom.library_management_system.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, String>, JpaSpecificationExecutor<Book> {

    List<Book> findByUser(User user);
}
