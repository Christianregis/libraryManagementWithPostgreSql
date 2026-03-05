package com.example.libraryManager.repository;

import com.example.libraryManager.model.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Gestion des emprunts en base de donnees
 */
@Repository
public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> searchBorrowByDateRetourEffectiveIsNullOrStatusEquals(String status);

    List<Borrow> searchBorrowByStatusContains(String status);
}
