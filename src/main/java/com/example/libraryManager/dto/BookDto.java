package com.example.libraryManager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookDto {
    private String title;
    private String auteur;
    private String isbn;
    private Long categoryId;
    private String status;
    private Long id;

    public BookDto(String title, String auteur, String isbn, Long categoryId, String status, Long id) {
        this.title = title;
        this.auteur = auteur;
        this.isbn = isbn;
        this.status = status;
        this.categoryId = categoryId;
        this.id = id;
    }
}
