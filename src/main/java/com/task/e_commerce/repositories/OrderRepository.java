package com.task.e_commerce.repositories;

import com.task.e_commerce.entities.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    List<OrderEntity> findAllByUserEntityIdOrderByOrderDateDesc(Long userId);
}
