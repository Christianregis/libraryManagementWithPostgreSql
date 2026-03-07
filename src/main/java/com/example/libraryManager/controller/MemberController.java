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
import io.swagger.v3.oas.annotations.Parameter;
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
 * Controleur pour la gestion des membres
 */
@RestController
@RequestMapping("/api/members")
@Tag(name = "Membre", description = "API REST pour la gestion des memebres de la bibliothèque.")
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
     * Récupère les informations de l'utilisateur actuellement authentifié.
     *
     * Cette méthode utilise l'objet Authentication fourni par Spring Security
     * afin d'obtenir les informations de l'utilisateur connecté dans le système.
     *
     * @param authentication gestionnaire d'authentification contenant les informations
     *                       de l'utilisateur connecté
     * @return ResponseEntity contenant les informations de l'utilisateur connecté sous forme de UserDto
     */
    @Operation(
            summary = "Récupérer l'utilisateur connecté",
            description = "Retourne les informations de l'utilisateur actuellement authentifié dans le système"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Utilisateur récupéré avec succès"),
            @ApiResponse(responseCode = "401", description = "Utilisateur non authentifié")
    })
    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication){
        User user = (User) authentication.getPrincipal();
        assert user != null;
        return ResponseEntity.status(HttpStatus.OK).body(user.toDto());
    }

    // Affichage du catalogue de produits

    /**
     * Récupère l'ensemble des livres disponibles dans la bibliothèque.
     *
     * Cette méthode retourne la liste complète des livres enregistrés
     * dans le catalogue de la bibliothèque.
     *
     * @return ResponseEntity contenant la liste des livres sous forme de BookDto
     */
    @Operation(
            summary = "Lister tous les livres",
            description = "Retourne l'ensemble des livres présents dans le catalogue de la bibliothèque"
    )
    @ApiResponse(responseCode = "200", description = "Liste des livres récupérée avec succès")
    @GetMapping("/books")
    public ResponseEntity<List<BookDto>> getAllBooks(){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.getAllBooks().stream().map(Book::toDto).collect(Collectors.toList()));
    }

    /**
     * Recherche des livres à partir de leur titre.
     *
     * Cette méthode permet de filtrer les livres dont le titre correspond
     * au texte fourni en paramètre.
     *
     * @param title titre ou partie du titre du livre à rechercher
     * @return ResponseEntity contenant la liste des livres correspondants
     */
    @Operation(
            summary = "Rechercher un livre par titre",
            description = "Retourne la liste des livres correspondant au titre fourni"
    )
    @ApiResponse(responseCode = "200", description = "Résultats de la recherche récupérés avec succès")
    @GetMapping("/books/title/{title}")
    public ResponseEntity<List<BookDto>> getBooksByTitle(
            @Parameter(description = "Titre du livre à rechercher", required = true)
            @PathVariable String title){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.searchBooksByTitle(title).stream().map(Book::toDto).collect(Collectors.toList()));
    }

    /**
     * Recherche des livres à partir du nom de l'auteur.
     *
     * Cette méthode permet de récupérer tous les livres écrits
     * par un auteur spécifique.
     *
     * @param author nom de l'auteur
     * @return ResponseEntity contenant la liste des livres écrits par cet auteur
     */
    @Operation(
            summary = "Rechercher un livre par auteur",
            description = "Retourne les livres correspondant à l'auteur fourni"
    )
    @ApiResponse(responseCode = "200", description = "Liste des livres récupérée")
    @GetMapping("/books/author/{author}")
    public ResponseEntity<List<BookDto>> getBooksByAuthor(
            @Parameter(description = "Nom de l'auteur", required = true)
            @PathVariable String author){
        return ResponseEntity.status(HttpStatus.OK).body(bookService.searchBooksByAuthor(author).stream().map(Book::toDto).collect(Collectors.toList()));
    }

    /**
     * Récupère les livres appartenant à une catégorie spécifique.
     *
     * Cette méthode permet de filtrer les livres selon leur catégorie.
     *
     * @param categoryId identifiant de la catégorie
     * @return ResponseEntity contenant la liste des livres de la catégorie
     */
    @Operation(
            summary = "Rechercher les livres par catégorie",
            description = "Retourne les livres appartenant à une catégorie spécifique"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Livres récupérés avec succès"),
            @ApiResponse(responseCode = "404", description = "Catégorie non trouvée")
    })
    @GetMapping("/books/category/{categoryId}")
    public ResponseEntity<List<BookDto>> getBooksByCategory(
            @Parameter(description = "Identifiant de la catégorie", required = true)
            @PathVariable Long categoryId){
        Category category = categoryService.getCategoryById(categoryId).orElse(null);
        if(category != null){
            return ResponseEntity.status(HttpStatus.OK).body(bookService.searchBooksByCategory(category).stream().map(Book::toDto).collect(Collectors.toList()));
        }
        return ResponseEntity.notFound().build();
    }

    // Affichage de l'ensemble des categories

    /**
     * Récupère la liste complète des catégories de livres.
     *
     * Cette méthode permet d'afficher toutes les catégories
     * disponibles dans la bibliothèque.
     *
     * @return ResponseEntity contenant la liste des catégories
     */
    @Operation(
            summary = "Lister les catégories",
            description = "Retourne toutes les catégories de livres disponibles"
    )
    @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée")
    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getAllCategories(){
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategories().stream().map(
                Category::toDto
        ).collect(Collectors.toList()));
    }

    // Gestion des emprunts et retours

    /**
     * Permet à un utilisateur d'emprunter un livre.
     *
     * Cette méthode crée un nouvel emprunt en associant
     * un utilisateur et un livre spécifique.
     *
     * @param bookId identifiant du livre à emprunter
     * @param userId identifiant de l'utilisateur qui emprunte
     * @return ResponseEntity contenant les informations de l'emprunt créé
     */
    @Operation(
            summary = "Emprunter un livre",
            description = "Permet à un utilisateur d'emprunter un livre de la bibliothèque"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Emprunt créé avec succès"),
            @ApiResponse(responseCode = "404", description = "Utilisateur ou livre non trouvé")
    })
    @PostMapping("/books/borrowBook/{userId}/{bookId}")
    public ResponseEntity<BorrowDto> borrowBook(
            @Parameter(description = "Identifiant du livre", required = true)
            @PathVariable Long bookId,
            @Parameter(description = "Identifiant de l'utilisateur", required = true)
            @PathVariable Long userId){
        User user = userService.findUserById(userId);
        Book book = bookService.findBookById(bookId);
        if (user != null && book != null){
            return ResponseEntity.status(HttpStatus.CREATED).body(borrowService.borrowBook(book, user).toDto());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Permet de retourner un livre précédemment emprunté.
     *
     * Cette méthode enregistre le retour d'un livre
     * à partir de l'identifiant de l'emprunt.
     *
     * @param borrowId identifiant de l'emprunt
     * @return ResponseEntity contenant les informations du retour
     */
    @Operation(
            summary = "Retourner un livre",
            description = "Permet d'enregistrer le retour d'un livre emprunté"
    )
    @ApiResponse(responseCode = "200", description = "Livre retourné avec succès")
    @PostMapping("/books/return/{borrowId}")
    public ResponseEntity<BorrowDto>  returnBook(
            @Parameter(description = "Identifiant de l'emprunt", required = true)
            @PathVariable Long borrowId){
        Borrow borrow = returnService.returnBook(borrowId);
        return ResponseEntity.status(HttpStatus.OK).body(borrow.toDto());
    }

    /**
     * Permet de prolonger la durée d'un emprunt.
     *
     * Cette méthode ajoute un nombre de jours supplémentaires
     * à la durée d'un emprunt existant.
     *
     * @param borrowId identifiant de l'emprunt
     * @param days nombre de jours à ajouter
     * @return ResponseEntity contenant l'emprunt mis à jour
     */
    @Operation(
            summary = "Prolonger un emprunt",
            description = "Ajoute un nombre de jours supplémentaires à un emprunt existant"
    )
    @ApiResponse(responseCode = "201", description = "Durée de l'emprunt prolongée")
    @PostMapping("/books/borrow/book/addTime/{borrowId}/{days}")
    public ResponseEntity<BorrowDto> addTimeToBorrow(
            @Parameter(description = "Identifiant de l'emprunt", required = true)
            @PathVariable Long borrowId,
            @Parameter(description = "Nombre de jours à ajouter", required = true)
            @PathVariable Integer days){
        return ResponseEntity.status(HttpStatus.CREATED).body(returnService.addTimeToBorrow(borrowId, days).toDto());
    }
}
