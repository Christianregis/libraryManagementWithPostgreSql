package com.example.libraryManager.service;

import com.example.libraryManager.dto.BookDto;
import com.example.libraryManager.model.Book;
import com.example.libraryManager.model.Category;
import com.example.libraryManager.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * Service pour les livres
 */
@Service
public class BookService {
    final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    /**
     *
     * @param book Book
     * @return BookDto
     */
    public Book saveBook(Book book){
        return bookRepository.save(book);
    }

    /**
     *
     * @param id Long
     * @param newBook Book
     * @return Book
     */
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

    /**
     *
     * @return List<Book>
     */
    public List<Book> getAllBooks(){
        return bookRepository.findAll();
    }

    /**
     *
     * @param id Long
     * @return Book | null
     */
    public Book getBookInformation(Long id){
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null){
            return Objects.requireNonNull(bookRepository.findById(id).orElse(null));
        }
        return null;
    }

    /**
     *
     * @param id Long
     * @return Book | null
     */
    public Book findBookById(Long id){
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null){
            return Objects.requireNonNull(bookRepository.findById(id).orElse(null));
        }
        return null;
    }

    /**
     *
     * @param id Long
     * @return Boolean
     */
    public Boolean deleteBooK(Long id){
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null){
            bookRepository.delete(book);
            return true;
        }
        return false;
    }

    /**
     *
     * @return Long
     */
    public long getBooksCount(){
        return bookRepository.count();
    }
    // Recherche de livres ici

    /**
     *
     * @param auteur String
     * @return List
     */
    public List<Book> searchBooksByAuthor(String auteur){
        return bookRepository.findBooksByAuteurContainingOrderByCreatedAt(auteur);
    }

    /**
     *
     * @param title String
     * @return List
     */
    public List<Book> searchBooksByTitle(String title){
        return bookRepository.findBooksByTitleContainsOrderByCreatedAtDesc(title);
    }

    /**
     *
     * @param category Category
     * @return List
     */
    public List<Book> searchBooksByCategory(Category category){
        return bookRepository.findBooksByCategory(category);
    }
}
