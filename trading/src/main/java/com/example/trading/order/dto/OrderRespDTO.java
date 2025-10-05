package com.example.trading.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.trading.order.emus.OrderSide;
import com.example.trading.order.emus.OrderStatus;
import com.example.trading.order.emus.OrderType;
import com.example.trading.order.emus.TimeInForce;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderRespDTO {
    private Long id;
    private String clientOrderId;
    private Long userId;
    private String symbol;
    private OrderSide side;
    private OrderType type;
    private TimeInForce timeInForce;
    private BigDecimal price;
    private BigDecimal quantity;
    private BigDecimal filledQuantity;
    private OrderStatus status;
    private String rejectReason;
    private LocalDateTime createdAt;
}
