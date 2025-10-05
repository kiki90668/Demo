package com.example.trading.order.events;

import com.example.trading.order.emus.OrderType;
import com.example.trading.order.emus.OrderSide;
import com.example.trading.shared.event.DomainEvent;
import lombok.Value;

@Value
public class OrderCreatedEvent implements DomainEvent{
    Long orderId;
    Long userId;
    String symbol;
    OrderSide side;
    OrderType type;
}
