package com.example.libraryManager.service;


import com.example.libraryManager.model.Book;
import com.example.libraryManager.model.Borrow;
import com.example.libraryManager.model.User;
import com.example.libraryManager.repository.BorrowRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BorrowService {

    private final BorrowRepository borrowRepository;

    public BorrowService(BorrowRepository borrowRepository) {
        this.borrowRepository = borrowRepository;
    }

    public Borrow borrowBook(Book book, User user){
        if(!book.isAvailable()){
            throw new RuntimeException("Livre non disponible");
        }
        Borrow borrow = new Borrow();
        borrow.setBook(book);
        borrow.setUser(user);
        borrow.setDateEmprunt(LocalDate.now());
        // On considere ici la la date limite d'borrow est toujours dans 14 Jours
        borrow.setDateRetourPrevue(LocalDate.now().plusDays(14));
        borrow.setStatus("EN_COURS");
        book.setStatus("EMPRUNTE");
        return borrowRepository.save(borrow);
    }

    public List<Borrow> getAllBorrows(){
        return borrowRepository.searchBorrowByStatusContains("EN_COURS");
    }

    public Long getBorrowCount(){
        return (long) borrowRepository.searchBorrowByStatusContains("EN_COURS").size();
    }
}
