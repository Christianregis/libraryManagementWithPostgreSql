package com.example.libraryManager.service;


import com.example.libraryManager.model.Borrow;
import com.example.libraryManager.repository.BorrowRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Service de gestion des retours
 */
@Service
public class ReturnService {

    private final BorrowRepository borrowRepository;

    public ReturnService(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    public Borrow returnBook(Long empruntId){
        Borrow borrow = borrowRepository.findById(empruntId).orElse(null);
        if (borrow != null){
            borrow.setDateRetourEffective(LocalDate.now());
            borrow.setStatus("TERMINE");
            double penalite = calculatePenality(borrow);
            borrow.setPenalites(penalite);
            borrow.getBook().setStatus("DISPONIBLE");
            return borrowRepository.save(borrow);
        }
        return null;
    }

    // Calculer les penalites en considerant le montant a payer par jour de retard est de 500 FCFA
    private double calculatePenality(Borrow borrow) {
        if(borrow.getDateRetourEffective() == null){
            return 0.0;
        }
        long retard = ChronoUnit.DAYS.between(borrow.getDateRetourPrevue(), borrow.getDateRetourEffective());
        if (retard > 0){
            return retard* 500;
        }
        return 0.0;
    }

    public Borrow addTimeToBorrow(Long empruntId, int jours) {

        Borrow borrow = borrowRepository.findById(empruntId)
                .orElseThrow(() -> new RuntimeException("Borrow introuvable"));

        if (borrow.getDateRetourEffective() != null) {
            throw new RuntimeException("Impossible de prolonger un livre déjà retourné");
        }

        borrow.setDateRetourPrevue(
                borrow.getDateRetourPrevue().plusDays(jours)
        );

        return borrowRepository.save(borrow);
    }

    public List<Borrow> getAllReturns(){
        return borrowRepository.searchBorrowByDateRetourEffectiveIsNullOrStatusEquals("TERMINE");
    }

    public Long getReturnsCount(){
        return (long) borrowRepository.searchBorrowByDateRetourEffectiveIsNullOrStatusEquals("TERMINE").size();
    }
}
