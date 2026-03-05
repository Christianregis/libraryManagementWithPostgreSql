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
 *
 * Controlleur pour la gestion de l'adminstration
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final CategoryService categoryService;
    private final BookService bookService;
    private final BorrowService borrowService;
    private final ReturnService returnService;

    /**
     *
     *
     * @param userService Injection pour le service des utilisateurs
     * @param categoryService Injection pour le service des categories
     * @param bookService Injection pour le service des livres
     * @param borrowService Injection pour le service des emprunts
     * @param returnService Injection pour le service des retours
     *
     */
    public AdminController(UserService userService, CategoryService categoryService, BookService bookService, BorrowService borrowService, ReturnService returnService) {
        this.userService = userService;
        this.categoryService = categoryService;
        this.bookService = bookService;
        this.borrowService = borrowService;
        this.returnService = returnService;
    }

    // Affichage des informations de l'adminstrateur

    /**
     * Retourne les informations de l'utilisateur connectee
     * @param authentication gestionnaire d'authentification
     * @return ResponseEntity
     */
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        assert user != null;
        return ResponseEntity.status(HttpStatus.OK).body(user.toDto());
    }

    // Gestion des utilisateurs

    /**
     * Affichage de l'ensemble des utilisateurs(admin+membre)
     * @return ResponseEntity
     */
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUser(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUser().stream().map(User::toDto).collect(Collectors.toList()));
    }

    /**
     * Supprimer un utilisateur par son ID
     * @param id Long
     * @return ResponseEntity
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<List<String>> deleteUser(@PathVariable Long id){
        if(userService.deleteUser(id)){
            return ResponseEntity.status(HttpStatus.OK).body(List.of("success","Utlisateur supprimee !"));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * afficher les infos d'un utilisateur par son ID
     * @param id Long
     * @return ResponseEntity
     */
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserInformation(@PathVariable Long id){
        if (userService.findUserById(id) != null){
            return ResponseEntity.status(HttpStatus.OK).body(userService.findUserById(id).toDto());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * affcihe le nombre d'utilisateurs
     * @return ResponseEntity
     */
    @GetMapping("/users/count")
    public ResponseEntity<Long> getUsersCount(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsersCount());
    }

    // Gestion des categories

    /**
     * afficher l'ensemble des categories
     * @return ResponseEntity
     */
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories().stream().map(
                Category::toDto
        ).collect(Collectors.toList()));
    }

    /**
     * enregistrer une categorie
     * @param categoryDto CategoryDto
     * @return ResponseEntity
     */
    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> saveCategory(@RequestBody CategoryDto categoryDto){
        Category category = new Category();
        category.setName(categoryDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(category).toDto());
    }

    /**
     * mettre a jour une categorie
     * @param id Long
     * @param categoryDto CategoryDto
     * @return ResponseEntity
     */
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto){
        Category newCategory = new Category();
        newCategory.setName(categoryDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.updateCategory(id, newCategory).toDto());
    }

    /**
     * Supprimer une categorie par son ID
     * @param id Long
     * @return ResponseEntity
     */
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<List<String>> deleteCategory(@PathVariable Long id){
        if (categoryService.deleteCategory(id)){
            return ResponseEntity.status(HttpStatus.OK).body(List.of("success","Categorie supprimee !"));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * afficher le nombre de categorie
     * @return ResponseEntity
     */
    @GetMapping("/categories/count")
    public ResponseEntity<Long> getCategoriesCount(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoriesCount());
    }

    // Gestion des Livres

    /**
     * recuperer tous les livres
     * @return ResponseEntity
     */
    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getAllBooks(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks().stream().map(Book::toDto).collect(Collectors.toList()));
    }

    /**
     * enregister un livre
     * @param bookDto BookDto
     * @return ResponseEntity
     */
    @PostMapping("/books")
    public ResponseEntity<BookDto> saveBook(@RequestBody BookDto bookDto){
        Category category = categoryService.getCategoryById(bookDto.getCategoryId()).orElse(null);
        if (category != null){
            Book book = new Book();
            book.setTitle(bookDto.getTitle());
            book.setAuteur(bookDto.getAuteur());
            book.setIsbn(bookDto.getIsbn());
            book.setStatus(bookDto.getStatus());

            book.setCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(bookService.saveBook(book).toDto());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * recuperer les informations d'un livre
     * @param id Long
     * @return ResponseEntity
     */
    @GetMapping("/books/{id}")
    public ResponseEntity<BookDto> getBookInformation(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookInformation(id).toDto());
    }

    /**
     * mettre a jour un livre
     * @param id Long
     * @param bookDto BookDto
     * @return ResponseEntity
     */
    @PutMapping("/books/{id}")
    public ResponseEntity<BookDto> updateBook(@PathVariable Long id, @RequestBody BookDto bookDto){
        Category category = categoryService.getCategoryById(bookDto.getCategoryId()).orElse(null);
        if (category != null){
            Book book = new Book();
            book.setTitle(bookDto.getTitle());
            book.setAuteur(bookDto.getAuteur());
            book.setIsbn(bookDto.getIsbn());
            book.setStatus(bookDto.getStatus());

            book.setCategory(category);
            return ResponseEntity.status(HttpStatus.CREATED).body(bookService.updateBook(id, book).toDto());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * recuperer le nombre de livre en base
     * @return ResponseEntity
     */
    @GetMapping("/books/count")
    public ResponseEntity<Long> getBooksCount(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksCount());
    }

    /**
     * supprimer un livre par son ID
     * @param id Long
     * @return ResponseEntity
     */
    @DeleteMapping("/books/{id}")
    public ResponseEntity<List<String>> deleteBook(@PathVariable Long id){
        if(bookService.deleteBooK(id)){
            return ResponseEntity.status(HttpStatus.OK).body(List.of("success","Livre supprimee !"));
        }
        return ResponseEntity.notFound().build();
    }

    // Gestion des emprunts

    /**
     * recuperer le total d'emprunt
     * @return ResponseEntity
     */
    @GetMapping("/emprunts/count")
    public ResponseEntity<Long> getBorrowCount(){
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getBorrowCount());
    }

    /**
     * retourne la liste des emprunts
     * @return ResponseEntity
     */
    @GetMapping("/emprunts/")
    public ResponseEntity<List<BorrowDto>> getAllBorrows(){
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getAllBorrows().stream().map(Borrow::toDto).collect(Collectors.toList()));
    }

    // Gestion des retours

    /**
     * retourne le total de retours
     * @return ResponseEntity
     */
    @GetMapping("/returns/count")
    public ResponseEntity<Long> getReturnsCount(){
        return ResponseEntity.status(HttpStatus.OK).body(returnService.getReturnsCount());
    }

    /**
     * affiche tous les retours
     * @return ResponseEntity
     */
    @GetMapping("/returns/")
    public ResponseEntity<List<BorrowDto>> getAllReturns(){
        return ResponseEntity.status(HttpStatus.OK).body(returnService.getAllReturns().stream().map(Borrow::toDto).collect(Collectors.toList()));
    }

}
