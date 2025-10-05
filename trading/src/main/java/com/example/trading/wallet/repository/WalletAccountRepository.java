package com.example.trading.wallet.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.trading.wallet.entity.WalletAccount;

public interface WalletAccountRepository extends JpaRepository<WalletAccount, Long> {
    Optional<WalletAccount> findByUserIdAndCcy(Long userId, String ccy);
}
