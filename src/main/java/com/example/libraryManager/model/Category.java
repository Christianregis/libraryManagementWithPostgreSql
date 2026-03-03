package com.example.libraryManager.model;

import com.example.libraryManager.dto.CategoryDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity(name = "category")
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @OneToMany(mappedBy = "category")
    private List<Book> books;

    public CategoryDto toDto(){
        return new CategoryDto(getName(), getId());
    }
}
