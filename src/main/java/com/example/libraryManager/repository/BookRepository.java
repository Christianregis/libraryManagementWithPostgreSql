package com.example.libraryManager.repository;

import com.example.libraryManager.model.Book;
import com.example.libraryManager.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findBooksByAuteurContainingOrderByCreatedAt(String auteur);

    List<Book> findBooksByTitleContainsOrderByCreatedAtDesc(String title);

    List<Book> findBooksByCategory(Category category);
}
