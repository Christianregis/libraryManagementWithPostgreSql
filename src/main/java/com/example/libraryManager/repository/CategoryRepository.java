package com.example.libraryManager.repository;

import com.example.libraryManager.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;

/**
 * Gestion des categories en base de donnees
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}
