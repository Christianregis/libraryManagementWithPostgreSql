package com.example.libraryManager.model;

import com.example.libraryManager.dto.BorrowDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entites Emprunts
 */
@Entity(name = "loans")
@Getter
@Setter
public class Borrow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dateEmprunt;
    private LocalDate dateRetourPrevue;
    private LocalDate dateRetourEffective;
    private Double penalites = 0.0;
    private String status;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public BorrowDto toDto(){
        return new BorrowDto(getDateEmprunt(), getDateRetourPrevue(), getDateRetourEffective(), getStatus(), book.getId(), user.getId(), penalites, getId());
    }

}
