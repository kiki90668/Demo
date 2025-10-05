package com.example.trading.wallet.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.trading.order.emus.OrderSide;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "wallet_reservation", uniqueConstraints = {
    @UniqueConstraint(name = "uk_user_client", columnNames = {"user_id", "client_order_id"})})
public class WalletReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "client_order_id", nullable = false, length = 64)
    private String clientOrderId;

    @Column(name = "symbol", nullable = false, length = 20)
    private String symbol;

    @Column(name = "side", nullable = false, length = 8)
    private OrderSide side; //BUY / SELL

    @Column(name = "ccy", nullable = false, length = 16)
    private String ccy; //鎖定的幣別

    @Column(name = "amount", nullable = false, precision = 20, scale = 8)
    private BigDecimal amount; //預扣金額

    @Column(name = "created_at", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
