package com.task.e_commerce.repositories;

import com.task.e_commerce.entities.OrderItemsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemsRepository extends JpaRepository<OrderItemsEntity, Long> {
}
