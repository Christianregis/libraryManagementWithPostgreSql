package com.example.libraryManager.service;

import com.example.libraryManager.dto.BookDto;
import com.example.libraryManager.model.Book;
import com.example.libraryManager.model.Category;
import com.example.libraryManager.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BookService {
    final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookDto saveBook(Book book){
        return bookRepository.save(book).toDto();
    }

    public Book updateBook(Long id, Book newBook){
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null){
            book.setTitle(newBook.getTitle() != null ? newBook.getTitle() : book.getTitle());
            book.setAuteur(newBook.getAuteur() != null ? newBook.getAuteur() : book.getAuteur());
            book.setIsbn(newBook.getIsbn() != null ? newBook.getIsbn() : book.getIsbn());
            book.setCategory(newBook.getCategory() != null ? newBook.getCategory() : book.getCategory());
            book.setStatus(newBook.getStatus() != null ? newBook.getStatus() : book.getStatus());
            return bookRepository.save(book);
        }
        return null;
    }

    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    public Book getBookInformation(Long id){
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null){
            return Objects.requireNonNull(bookRepository.findById(id).orElse(null));
        }
        return null;
    }

    public Book findBookById(Long id){
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null){
            return Objects.requireNonNull(bookRepository.findById(id).orElse(null));
        }
        return null;
    }

    public Boolean deleteBooK(Long id){
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null){
            bookRepository.delete(book);
            return true;
        }
        return false;
    }

    public long getBooksCount(){
        return bookRepository.count();
    }
    // Recherche de livres ici

    public List<Book> searchBooksByAuthor(String auteur){
        return bookRepository.findBooksByAuteurContainingOrderByCreatedAt(auteur);
    }

    public List<Book> searchBooksByTitle(String title){
        return bookRepository.findBooksByTitleContainsOrderByCreatedAtDesc(title);
    }

    public List<Book> searchBooksByCategory(Category category){
        return bookRepository.findBooksByCategory(category);
    }
}
