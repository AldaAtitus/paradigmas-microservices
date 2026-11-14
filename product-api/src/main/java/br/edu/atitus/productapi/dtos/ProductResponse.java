package br.edu.atitus.productapi.dtos;

import br.edu.atitus.productapi.entities.ProductEntity;

public record ProductResponse(
        Long id, String title, String description, double price,
        Long categoryId, String imageUrl, Long sellerId,
        String environment, String promotionMessage, String targetCurrency, double convertedPrice
) {
    public static ProductResponse fromEntity(ProductEntity entity, String environment, 
            String promotionMessage, String targetCurrency, double convertedPrice) {
        return new ProductResponse(
                entity.getId(), entity.getTitle(), entity.getDescription(), entity.getPrice(),
                entity.getCategoryId(), entity.getImageUrl(), entity.getSellerId(),
                environment, promotionMessage, targetCurrency, convertedPrice
        );
    }
}