package com.example.trading.shared.event;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
}
