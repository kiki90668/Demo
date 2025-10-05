package com.example.trading.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trading.wallet.entity.WalletReservation;

public interface WalletReservationRepository extends JpaRepository<WalletReservation, Long> {
    Optional<WalletReservation> findByUserIdAndClientOrderId(Long userId, String clientOrderId);
}
