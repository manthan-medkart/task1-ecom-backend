package com.task.e_commerce.repositories;

import com.task.e_commerce.entities.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    CartEntity findByUserEntityIdAndActive(Long userId, Boolean status);

}
