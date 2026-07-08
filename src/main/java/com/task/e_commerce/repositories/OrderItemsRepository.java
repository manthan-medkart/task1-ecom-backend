package com.task.e_commerce.repositories;

import com.task.e_commerce.entities.OrderItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemsRepository extends JpaRepository<OrderItemsEntity, Long> {

    List<OrderItemsEntity> findAllByOrderEntityId(Long id);
}
