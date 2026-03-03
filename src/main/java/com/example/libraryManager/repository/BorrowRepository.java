package com.example.libraryManager.repository;

import com.example.libraryManager.model.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> searchBorrowByDateRetourEffectiveIsNullOrStatusEquals(String status);

    List<Borrow> searchBorrowByStatusContains(String status);
}
