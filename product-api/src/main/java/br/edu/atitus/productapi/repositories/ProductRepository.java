package br.edu.atitus.productapi.repositories;

import br.edu.atitus.productapi.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Page<ProductEntity> findByCategoryIdAndTitleContainingIgnoreCase(Long categoryId, String title, Pageable pageable);
    Page<ProductEntity> findByCategoryId(Long categoryId, Pageable pageable);
    Page<ProductEntity> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}