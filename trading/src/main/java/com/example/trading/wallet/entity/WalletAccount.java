package com.example.trading.wallet.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wallet_account", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_ccy", columnNames = {"user_id", "ccy"})})
public class WalletAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "ccy", nullable = false, length = 16)
    private String ccy;

    @Builder.Default
    @Column(name = "available", nullable = false, precision = 20, scale = 8)
    private BigDecimal available = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "locked", nullable = false, precision = 20, scale = 8)
    private BigDecimal locked = BigDecimal.ZERO;

    @Version
    private Long version;

    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;
}
