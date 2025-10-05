package com.example.trading.order.mapper;

import com.example.trading.order.dto.OrderPlaceReqDTO;
import com.example.trading.order.dto.OrderRespDTO;
import com.example.trading.order.emus.OrderStatus;
import com.example.trading.order.emus.OrderType;
import com.example.trading.order.entity.Order;

public class OrderMapper {
    public static Order toEntity(OrderPlaceReqDTO reqDTO, Long userId) {
        return Order.builder()
                .userId(userId)
                .clientOrderId(reqDTO.getClientOrderId())
                .symbol(reqDTO.getSymbol())
                .side(reqDTO.getSide())
                .type(reqDTO.getType())
                .timeInForce(reqDTO.getTimeInForce())
                .price(reqDTO.getType() == OrderType.LIMIT ? reqDTO.getPrice() : null)
                .quantity(reqDTO.getQuantity())
                .status(OrderStatus.NEW)
                .build();
    }

    public static OrderRespDTO toResp(Order order) {
        return OrderRespDTO.builder()
                .id(order.getId())
                .clientOrderId(order.getClientOrderId())
                .userId(order.getUserId())
                .symbol(order.getSymbol())
                .side(order.getSide())
                .type(order.getType())
                .timeInForce(order.getTimeInForce())
                .price(order.getPrice())
                .quantity(order.getQuantity())
                .filledQuantity(order.getFilledQuantity())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }

}
