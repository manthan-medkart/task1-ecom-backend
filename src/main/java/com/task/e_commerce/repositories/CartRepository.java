package com.task.e_commerce.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.task.e_commerce.entities.CartEntity;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, Long> {

    CartEntity findByUserEntityIdAndActive(Long userId, Boolean status);

}
