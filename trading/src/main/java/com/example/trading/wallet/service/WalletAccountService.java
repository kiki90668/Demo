package com.example.trading.wallet.service;

import java.math.BigDecimal;

import com.example.trading.order.emus.OrderSide;

public interface WalletAccountService {
    //預扣款，鎖定資金
    void prelock(Long userId, String symbol, OrderSide side, BigDecimal price, BigDecimal quantity, String clientOrderId);

    //解扣款，撤單或訂單失敗
    void unlock(Long userId, String clientOrderId);

}
