package com.example.trading.order.service;

import com.example.trading.order.dto.OrderPlaceReqDTO;
import com.example.trading.order.dto.OrderRespDTO;

public interface OrderService {
    OrderRespDTO placeOrder(Long userId, OrderPlaceReqDTO reqDTO);
    OrderRespDTO getById(Long userId, Long orderId);
}
