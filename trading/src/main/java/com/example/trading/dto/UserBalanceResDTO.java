package com.example.trading.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class UserBalanceResDTO {
    private String username;
    private BigDecimal balance;
}
