package com.example.trading.wallet.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import com.example.trading.order.emus.OrderSide;
import com.example.trading.wallet.entity.WalletAccount;
import com.example.trading.wallet.entity.WalletReservation;
import com.example.trading.wallet.repository.WalletAccountRepository;
import com.example.trading.wallet.repository.WalletReservationRepository;
import com.example.trading.wallet.service.WalletAccountService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WalletAccountServiceImpl implements WalletAccountService {
    private final WalletAccountRepository accountRepo;
    private final WalletReservationRepository reservationRepo;
    private static final int MAX_RETRY = 3;

    @Override
    @Transactional
    public void prelock(Long userId, String symbol, OrderSide side, BigDecimal price, BigDecimal quantity, String clientOrderId) {
        if (clientOrderId == null || clientOrderId.isEmpty()) {
            throw new IllegalArgumentException("clientOrderId is required for idempotency");
        }
        //冪等 : 如果預扣存在直接return
        Optional<WalletReservation> existed = reservationRepo.findByUserIdAndClientOrderId(userId, clientOrderId);
        if (existed.isPresent()) {
            return;
        }

        boolean isBuy = OrderSide.BUY.equals(side);
        String base = parseBase(symbol);
        String quote = parseQuote(symbol);
        String lockCcy = isBuy ? quote : base; //BUY : 鎖定qutoe, SELL : 鎖定base
        BigDecimal amount = isBuy ? price.multiply(quantity) : quantity; //BUY : 價格*數量, SELL : 數量
        amount = amount.setScale(8, RoundingMode.HALF_UP);

        for (int i = 0; i <MAX_RETRY; i++) {
            try {
                WalletAccount acc = accountRepo.findByUserIdAndCcy(userId, lockCcy).orElseGet(() -> 
                    accountRepo.save(WalletAccount.builder()
                    .userId(userId).ccy(lockCcy).build()));
                
                //檢查餘額
                if (acc.getAvailable().compareTo(amount) < 0) {
                    throw new IllegalStateException("Insufficient " + lockCcy + " available: need=" + amount + ", have=" + acc.getAvailable());
                }

                acc.setAvailable(acc.getAvailable().subtract(amount));
                acc.setLocked(acc.getLocked().add(amount));

                accountRepo.saveAndFlush(acc);

                WalletReservation res = WalletReservation.builder()
                        .userId(userId).clientOrderId(clientOrderId).symbol(symbol).side(side).ccy(lockCcy).amount(amount)
                        .build();
                
                reservationRepo.saveAndFlush(res);
                return;
            } catch (OptimisticLockingFailureException e) {
                log.warn("prelock optimistic conflict, retry {}/{}", i + 1, MAX_RETRY);
                if (i == MAX_RETRY - 1) {
                    throw e;
                }
            }
        }
    }

    @Override
    @Transactional
    public void unlock(Long userId, String clientOrderId) {
        if (clientOrderId == null || clientOrderId.isEmpty()) {
            return;
        }
        //確認預扣是否存在，如果沒有表示已解鎖或未預扣
        Optional<WalletReservation> existed = reservationRepo.findByUserIdAndClientOrderId(userId, clientOrderId);
        if (existed.isEmpty()) {
            return;
        }
        WalletReservation res = existed.get();
        
        for (int i = 0; i < MAX_RETRY; i++) {
            try {
                WalletAccount acc = accountRepo.findByUserIdAndCcy(userId, res.getCcy())
                    .orElseThrow(() -> new IllegalStateException("Account not found for ccy=" + res.getCcy()));

                //解鎖
                BigDecimal locked = acc.getLocked();
                BigDecimal amount = res.getAmount();
                BigDecimal delta = locked.min(amount);
                
                //確認餘額
                if (locked.compareTo(amount) < 0) {
                    log.warn("Insufficient locked balance: need={}, have={}", amount, locked);
                }

                if (delta.signum() < 0) {
                    delta = BigDecimal.ZERO;
                }
                acc.setLocked(acc.getLocked().subtract(delta));
                acc.setAvailable(acc.getAvailable().add(delta));

                accountRepo.saveAndFlush(acc);

                //刪除預扣
                reservationRepo.delete(res);

                log.info("unlock success: userId={}, clientOrderId={}, ccy={}, released={}", userId, clientOrderId, res.getCcy(), delta);
                return;

            } catch (OptimisticLockingFailureException e) {
                log.warn("unlock optimistic conflict, retry {}/{}", i + 1, MAX_RETRY);
                if (i == MAX_RETRY - 1) {
                    throw e;
                }
            }
        }
    }

    private String parseBase(String symbol) {
        // 解析交易對的基礎貨幣
        return symbol.split("/")[0];
    }

    private String parseQuote(String symbol) {
        // 解析交易對的報價貨幣
        return symbol.split("/")[1];
    }

}
