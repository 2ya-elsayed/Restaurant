package com.spring.boot.restaurant.repository;

import com.spring.boot.restaurant.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepo extends JpaRepository<Product, Long> {
    Page<Product> findAllByOrderByIdAsc(Pageable pageable);

    // Find products by exact category ID
    Page<Product> findByCategoryIdOrderByIdAsc(Long categoryId, Pageable pageable);


    // Find products by exact category name (needs join on category)
    Page<Product> findByCategoryNameOrderByIdAsc(String categoryName, Pageable pageable);

    //    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByIdAsc(
            String nameKeyword,
            String descriptionKeyword,
            Pageable pageable
    );

    boolean existsByNameAndCategoryId(String name, Long categoryId);

}