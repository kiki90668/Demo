package com.example.trading.shared.event.impl;

import org.springframework.stereotype.Component;

import com.example.trading.shared.event.DomainEvent;
import com.example.trading.shared.event.DomainEventPublisher;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InMemoryEventPublisher implements DomainEventPublisher{
    @Override
    public void publish(DomainEvent event) {
        log.info("[DomainEvent] {}", event);
        // ToDo: 可改用 Spring ApplicationEventPublisher 或直接切換 Kafka 實作
    }
}
