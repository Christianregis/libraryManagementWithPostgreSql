package com.example.libraryManager.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO pour les categories
 */
@Getter
@Setter
public class CategoryDto {
    private String name;
    private Long id;

    public CategoryDto(String name, Long id) {
        this.name = name;
        this.id = id;
    }
}

