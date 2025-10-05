package com.example.trading.order.dto;

import java.math.BigDecimal;

import com.example.trading.order.emus.*;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class OrderPlaceReqDTO {

    @NotBlank
    private String symbol;

    @NotNull
    private OrderSide side;

    @NotNull
    private OrderType type;

    private TimeInForce timeInForce = TimeInForce.GTC; //default

    @Positive
    private BigDecimal quantity;

    //LIMIT 必填; MARKET勿傳
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal price;

    private String clientOrderId;
}
