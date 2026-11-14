package br.edu.atitus.productapi.dtos;

public record ProductRequest(
        String title,
        String description,
        double price,
        Long categoryId,
        String imageUrl
) {}
