package com.task.e_commerce.repositories;

import com.task.e_commerce.entities.CartItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItemEntity, Long> {

    List<CartItemEntity> findAllByCartEntityId(Long id);

    Optional<CartItemEntity> findByCartEntityIdAndProductEntityId(Long cartId, Long productId);

}
