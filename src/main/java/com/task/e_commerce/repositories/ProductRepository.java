package com.task.e_commerce.repositories;

import com.task.e_commerce.entities.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    @Query("SELECT p FROM ProductEntity p WHERE " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           " LOWER(p.composition) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<ProductEntity> searchProducts(@Param("search") String search, Pageable pageable);

    Boolean existsByProductCode(Long productCode);

    ProductEntity findByProductCode(ProductEntity productEntity);
}
