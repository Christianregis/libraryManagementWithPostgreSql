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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Adminstrateur", description = "API de gestion de la bibliotheque pour les administrateurs (ADMIN)")
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
     * Retourne les informations de l'utilisateur actuellement authentifié.
     *
     * Cette méthode récupère l'utilisateur connecté à partir du contexte de sécurité
     * de Spring Security et retourne ses informations sous forme de DTO.
     *
     * @param authentication gestionnaire d'authentification contenant les informations
     *                       de l'utilisateur connecté
     * @return ResponseEntity contenant les informations de l'utilisateur connecté
     */
    @Operation(
            summary = "Récupérer l'utilisateur connecté",
            description = "Retourne les informations de l'utilisateur actuellement authentifié dans le système"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Informations de l'utilisateur récupérées avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        assert user != null;
        return ResponseEntity.status(HttpStatus.OK).body(user.toDto());
    }

// Gestion des utilisateurs

    /**
     * Permet de récupérer la liste complète des utilisateurs du système.
     *
     * Cette liste inclut les administrateurs et les membres.
     *
     * @return ResponseEntity contenant la liste des utilisateurs sous forme de DTO
     */
    @Operation(
            summary = "Liste des utilisateurs",
            description = "Retourne la liste complète des utilisateurs (administrateurs et membres)"
    )
    @ApiResponse(responseCode = "200", description = "Liste des utilisateurs récupérée avec succès")
    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getAllUser(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getAllUser().stream().map(User::toDto).collect(Collectors.toList()));
    }

    /**
     * Supprime un utilisateur à partir de son identifiant.
     *
     * @param id identifiant de l'utilisateur à supprimer
     * @return ResponseEntity indiquant si la suppression a été effectuée
     */
    @Operation(
            summary = "Supprimer un utilisateur",
            description = "Supprime un utilisateur du système à partir de son identifiant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    @DeleteMapping("/users/{id}")
    public ResponseEntity<List<String>> deleteUser(@PathVariable Long id){
        if(userService.deleteUser(id)){
            return ResponseEntity.status(HttpStatus.OK).body(List.of("success","Utlisateur supprimee !"));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retourne les informations détaillées d'un utilisateur.
     *
     * @param id identifiant de l'utilisateur
     * @return ResponseEntity contenant les informations de l'utilisateur
     */
    @Operation(
            summary = "Informations d'un utilisateur",
            description = "Retourne les informations détaillées d'un utilisateur à partir de son identifiant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur trouvé"),
            @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    })
    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserInformation(@PathVariable Long id){
        if (userService.findUserById(id) != null){
            return ResponseEntity.status(HttpStatus.OK).body(userService.findUserById(id).toDto());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retourne le nombre total d'utilisateurs enregistrés dans le système.
     *
     * @return ResponseEntity contenant le nombre total d'utilisateurs
     */
    @Operation(
            summary = "Nombre d'utilisateurs",
            description = "Retourne le nombre total d'utilisateurs enregistrés dans la base de données"
    )
    @ApiResponse(responseCode = "200", description = "Nombre d'utilisateurs récupéré")
    @GetMapping("/users/count")
    public ResponseEntity<Long> getUsersCount(){
        return ResponseEntity.status(HttpStatus.OK).body(userService.getUsersCount());
    }

// Gestion des categories

    /**
     * Retourne la liste de toutes les catégories de livres.
     *
     * @return ResponseEntity contenant la liste des catégories
     */
    @Operation(
            summary = "Liste des catégories",
            description = "Retourne toutes les catégories disponibles dans la bibliothèque"
    )
    @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories().stream().map(
                Category::toDto
        ).collect(Collectors.toList()));
    }

    /**
     * Permet d'enregistrer une nouvelle catégorie.
     *
     * @param categoryDto informations de la catégorie à créer
     * @return ResponseEntity contenant la catégorie créée
     */
    @Operation(
            summary = "Créer une catégorie",
            description = "Permet d'ajouter une nouvelle catégorie de livres"
    )
    @ApiResponse(responseCode = "201", description = "Catégorie créée avec succès")
    @PostMapping("/categories")
    public ResponseEntity<CategoryDto> saveCategory(@RequestBody CategoryDto categoryDto){
        Category category = new Category();
        category.setName(categoryDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(category).toDto());
    }

    /**
     * Met à jour les informations d'une catégorie existante.
     *
     * @param id identifiant de la catégorie
     * @param categoryDto nouvelles informations de la catégorie
     * @return ResponseEntity contenant la catégorie mise à jour
     */
    @Operation(
            summary = "Modifier une catégorie",
            description = "Met à jour une catégorie existante"
    )
    @ApiResponse(responseCode = "201", description = "Catégorie mise à jour")
    @PutMapping("/categories/{id}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @RequestBody CategoryDto categoryDto){
        Category newCategory = new Category();
        newCategory.setName(categoryDto.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.updateCategory(id, newCategory).toDto());
    }

    /**
     * Supprime une catégorie à partir de son identifiant.
     *
     * @param id identifiant de la catégorie
     * @return ResponseEntity indiquant le résultat de la suppression
     */
    @Operation(
            summary = "Supprimer une catégorie",
            description = "Supprime une catégorie existante"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Catégorie supprimée"),
            @ApiResponse(responseCode = "404", description = "Catégorie introuvable")
    })
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<List<String>> deleteCategory(@PathVariable Long id){
        if (categoryService.deleteCategory(id)){
            return ResponseEntity.status(HttpStatus.OK).body(List.of("success","Categorie supprimee !"));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retourne le nombre total de catégories.
     *
     * @return ResponseEntity contenant le nombre de catégories
     */
    @Operation(
            summary = "Nombre de catégories",
            description = "Retourne le nombre total de catégories enregistrées"
    )
    @ApiResponse(responseCode = "200", description = "Nombre de catégories récupéré")
    @GetMapping("/categories/count")
    public ResponseEntity<Long> getCategoriesCount(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getCategoriesCount());
    }

// Gestion des Livres

    /**
     * Retourne la liste complète des livres disponibles.
     *
     * @return ResponseEntity contenant la liste des livres
     */
    @Operation(
            summary = "Liste des livres",
            description = "Retourne tous les livres enregistrés dans la bibliothèque"
    )
    @ApiResponse(responseCode = "200", description = "Liste des livres récupérée")
    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getAllBooks(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks().stream().map(Book::toDto).collect(Collectors.toList()));
    }

    /**
     * Enregistre un nouveau livre dans la bibliothèque.
     *
     * @param bookDto informations du livre à enregistrer
     * @return ResponseEntity contenant le livre enregistré
     */
    @Operation(
            summary = "Ajouter un livre",
            description = "Permet d'enregistrer un nouveau livre dans la bibliothèque"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livre ajouté avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie introuvable")
    })
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
     * Récupère les informations détaillées d'un livre à partir de son identifiant.
     *
     * @param id identifiant du livre
     * @return ResponseEntity contenant les informations du livre
     */
    @Operation(
            summary = "Informations d'un livre",
            description = "Retourne les informations détaillées d'un livre à partir de son identifiant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livre trouvé"),
            @ApiResponse(responseCode = "404", description = "Livre introuvable")
    })
    @GetMapping("/books/{id}")
    public ResponseEntity<BookDto> getBookInformation(@PathVariable Long id){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBookInformation(id).toDto());
    }

    /**
     * Met à jour les informations d'un livre existant.
     *
     * @param id identifiant du livre
     * @param bookDto nouvelles informations du livre
     * @return ResponseEntity contenant les informations du livre mis à jour
     */
    @Operation(
            summary = "Modifier un livre",
            description = "Met à jour les informations d'un livre existant"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Livre mis à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie introuvable")
    })
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
     * Retourne le nombre total de livres enregistrés dans la base de données.
     *
     * @return ResponseEntity contenant le nombre total de livres
     */
    @Operation(
            summary = "Nombre de livres",
            description = "Retourne le nombre total de livres enregistrés dans la bibliothèque"
    )
    @ApiResponse(responseCode = "200", description = "Nombre de livres récupéré")
    @GetMapping("/books/count")
    public ResponseEntity<Long> getBooksCount(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getBooksCount());
    }

    /**
     * Supprime un livre à partir de son identifiant.
     *
     * @param id identifiant du livre à supprimer
     * @return ResponseEntity indiquant si la suppression a réussi
     */
    @Operation(
            summary = "Supprimer un livre",
            description = "Supprime un livre existant dans la bibliothèque"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livre supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Livre introuvable")
    })
    @DeleteMapping("/books/{id}")
    public ResponseEntity<List<String>> deleteBook(@PathVariable Long id){
        if(bookService.deleteBooK(id)){
            return ResponseEntity.status(HttpStatus.OK).body(List.of("success","Livre supprimee !"));
        }
        return ResponseEntity.notFound().build();
    }

// Gestion des emprunts

    /**
     * Retourne le nombre total d'emprunts enregistrés.
     *
     * @return ResponseEntity contenant le nombre total d'emprunts
     */
    @Operation(
            summary = "Nombre d'emprunts",
            description = "Retourne le nombre total d'emprunts effectués dans la bibliothèque"
    )
    @ApiResponse(responseCode = "200", description = "Nombre d'emprunts récupéré")
    @GetMapping("/emprunts/count")
    public ResponseEntity<Long> getBorrowCount(){
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getBorrowCount());
    }

    /**
     * Retourne la liste complète des emprunts.
     *
     * @return ResponseEntity contenant la liste des emprunts
     */
    @Operation(
            summary = "Liste des emprunts",
            description = "Retourne la liste de tous les emprunts effectués par les utilisateurs"
    )
    @ApiResponse(responseCode = "200", description = "Liste des emprunts récupérée")
    @GetMapping("/emprunts/")
    public ResponseEntity<List<BorrowDto>> getAllBorrows(){
        return ResponseEntity.status(HttpStatus.OK).body(borrowService.getAllBorrows().stream().map(Borrow::toDto).collect(Collectors.toList()));
    }

// Gestion des retours

    /**
     * Retourne le nombre total de livres retournés.
     *
     * @return ResponseEntity contenant le nombre total de retours
     */
    @Operation(
            summary = "Nombre de retours",
            description = "Retourne le nombre total de livres retournés dans la bibliothèque"
    )
    @ApiResponse(responseCode = "200", description = "Nombre de retours récupéré")
    @GetMapping("/returns/count")
    public ResponseEntity<Long> getReturnsCount(){
        return ResponseEntity.status(HttpStatus.OK).body(returnService.getReturnsCount());
    }

    /**
     * Retourne la liste complète des retours de livres.
     *
     * @return ResponseEntity contenant la liste des retours
     */
    @Operation(
            summary = "Liste des retours",
            description = "Retourne la liste de tous les livres retournés par les utilisateurs"
    )
    @ApiResponse(responseCode = "200", description = "Liste des retours récupérée")
    @GetMapping("/returns/")
    public ResponseEntity<List<BorrowDto>> getAllReturns(){
        return ResponseEntity.status(HttpStatus.OK).body(returnService.getAllReturns().stream().map(Borrow::toDto).collect(Collectors.toList()));
    }

}
