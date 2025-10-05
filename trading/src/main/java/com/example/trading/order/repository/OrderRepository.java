package com.example.trading.order.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trading.order.emus.OrderStatus;
import com.example.trading.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{
    Optional<Order> findByUserIdAndClientOrderId(Long userId, String clientOrderId);
    Page<Order> findByUserIdAndSymbolAndStatus(Long userId, String symbol, OrderStatus status);
    
}
