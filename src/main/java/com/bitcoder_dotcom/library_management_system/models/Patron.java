package com.bitcoder_dotcom.library_management_system.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "patrons")
@AllArgsConstructor
@Getter
@Setter
public class Patron extends User{

    @OneToMany(mappedBy = "user")
    private List<Book> books;

    @OneToMany(mappedBy = "member")
    private List<Borrow> borrows;

    private boolean withBorrowedBook = false;

    public Patron() {
        super();
        this.setId(generateCustomUUID());
    }

    private String generateCustomUUID() {
        return "Pat"+ UUID.randomUUID().toString().substring(0, 5);
    }
}
