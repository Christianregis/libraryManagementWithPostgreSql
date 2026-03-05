package com.example.libraryManager.controller;

import com.example.libraryManager.dto.BookDto;
import com.example.libraryManager.dto.BorrowDto;
import com.example.libraryManager.dto.CategoryDto;
import com.example.libraryManager.dto.UserDto;
import com.example.libraryManager.model.Book;
import com.example.libraryManager.model.Borrow;
import com.example.libraryManager.model.Category;
import com.example.libraryManager.model.User;
import com.example.libraryManager.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controleur pour la gestion des membres
 */
@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final BookService bookService;
    private final CategoryService categoryService;
    private final UserService userService;
    private final BorrowService borrowService;
    private final ReturnService returnService;

    /**
     * Contructeur
     * @param bookService BookService
     * @param categoryService CategoryService
     * @param userService userService
     * @param borrowService BorrowService
     * @param returnService ReturnsService
     */
    public MemberController(BookService bookService, CategoryService categoryService, UserService userService, BorrowService borrowService, ReturnService returnService) {
        this.bookService = bookService;
        this.categoryService = categoryService;
        this.userService = userService;
        this.borrowService = borrowService;
        this.returnService = returnService;
    }

    // Affichage des informations du membre

    /**
     *
     * @param authentication Gestionnaire d'authentification
     * @return ResponseEntity
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        assert user != null;
        return ResponseEntity.status(HttpStatus.OK).body(user.toDto());
    }

    // Affichage du catalogue de produits

    /**
     * afficher tous les livres
     * @return ResponseEntity
     */
    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getAllBooks(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks().stream().map(Book::toDto).collect(Collectors.toList()));
    }

    /**
     * afficher un livre par son titre
     * @param title String
     * @return ResponseEntity
     */
    @GetMapping("/books/title/{title}")
    public ResponseEntity<List<BookDto>> getBooksByTitle(@PathVariable String title){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.searchBooksByTitle(title).stream().map(Book::toDto).collect(Collectors.toList()));
    }

    /**
     * Livres par nom de l'auteur
     * @param author String
     * @return ResponseEntity
     */
    @GetMapping("/books/author/{author}")
    public ResponseEntity<List<BookDto>> getBooksByAuthor(@PathVariable String author){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.searchBooksByAuthor(author).stream().map(Book::toDto).collect(Collectors.toList()));
    }

    /**
     * afficher les livres par categories
     * @param categoryId Long
     * @return ResponseEntity
     */
    @GetMapping("/books/category/{categoryId}")
    public ResponseEntity<List<BookDto>> getBooksByCategory(@PathVariable Long categoryId){
        Category category = categoryService.getCategoryById(categoryId).orElse(null);
        if(category != null){
            return ResponseEntity.status(HttpStatus.OK).body(bookService.searchBooksByCategory(category).stream().map(Book::toDto).collect(Collectors.toList()));
        }
        return ResponseEntity.notFound().build();
    }



    // Affichage de l'ensemble des categories

    /**
     * afficher les categories
     * @return ResponseEntity
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories().stream().map(
                Category::toDto
        ).collect(Collectors.toList()));
    }

    // Gestion des emprunts et retours

    /**
     * emprunter un livre par un utilisateur
     * @param bookId Long
     * @param userId Long
     * @return ResponseEntity
     */
    @PostMapping("/books/borrowBook/{userId}/{bookId}")
    public ResponseEntity<BorrowDto> borrowBook(@PathVariable Long bookId, @PathVariable Long userId){
        User user = userService.findUserById(userId);
        Book book = bookService.findBookById(bookId);
        if (user != null && book != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(borrowService.borrowBook(book, user).toDto());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * retourner un livre
     * @param borrowId Long
     * @return ResponseEntity
     */
    @PostMapping("/books/return/{borrowId}")
    public ResponseEntity<BorrowDto>  returnBook(@PathVariable Long borrowId){
        Borrow borrow = returnService.returnBook(borrowId);
        return ResponseEntity.status(HttpStatus.OK).body(borrow.toDto());
    }

    /**
     * prolonger un emprunts
     * @param borrowId Long
     * @param days Integer
     * @return ResponseEntity
     */
    @PostMapping("/books/borrow/book/addTime/{borrowId}/{days}")
    public ResponseEntity<BorrowDto> addTimeToBorrow(@PathVariable Long borrowId, @PathVariable Integer days){
        return ResponseEntity.status(HttpStatus.CREATED).body(returnService.addTimeToBorrow(borrowId, days).toDto());
    }
}
