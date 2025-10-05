package com.example.trading.order.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.trading.entity.BizException;
import com.example.trading.order.dto.OrderPlaceReqDTO;
import com.example.trading.order.dto.OrderRespDTO;
import com.example.trading.order.emus.OrderType;
import com.example.trading.order.entity.Order;
import com.example.trading.order.events.OrderCreatedEvent;
import com.example.trading.order.mapper.OrderMapper;
import com.example.trading.order.repository.OrderRepository;
import com.example.trading.order.service.OrderService;
import com.example.trading.shared.event.DomainEventPublisher;
import com.example.trading.wallet.service.WalletAccountService;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.StringRedisTemplate;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final StringRedisTemplate redis;
    private final WalletAccountService wallet;
    private final DomainEventPublisher events;

    private static final Duration ORDER_STATUS_TTL = Duration.ofDays(1);

    @Override
    @Transactional
    public OrderRespDTO placeOrder(Long userId, OrderPlaceReqDTO reqDTO) {

        //基礎規則和風控
        validateBusinessRules(reqDTO);

        //檢查重複訂單
        if (reqDTO.getClientOrderId() != null && !reqDTO.getClientOrderId().isEmpty()) {
            Optional<Order> exisited = orderRepository.findByUserIdAndClientOrderId(userId, reqDTO.getClientOrderId());
            if (exisited.isPresent()) {
                return OrderMapper.toResp(exisited.get());
            }
        }

        //先預扣款項 (餘額不足丟出例外 -> 回滾，wallet需要做冪等)
        BigDecimal lockPrice = reqDTO.getType() == OrderType.LIMIT ? reqDTO.getPrice() : estimateMarketPrice(reqDTO.getSymbol());
        wallet.prelock(userId, reqDTO.getSymbol(), reqDTO.getSide(), lockPrice, reqDTO.getQuantity(), reqDTO.getClientOrderId());

        //建立訂單Entity
        Order order = OrderMapper.toEntity(reqDTO, userId);

        try {
            order = orderRepository.saveAndFlush(order); //先存進資料庫
        } catch (DataIntegrityViolationException e) {
            //如果唯一鍵衝突，可能是重複訂單，再回查一次(idempotent)
            if (reqDTO.getClientOrderId() != null) {
                return orderRepository.findByUserIdAndClientOrderId(userId, reqDTO.getClientOrderId())
                        .map(OrderMapper::toResp)
                        .orElse(null);
            }
            throw e;
        }

        //寫入Redis快取
        cacheOrderStatus(order);

        // 發佈事件
        events.publish(new OrderCreatedEvent(
            order.getId(), order.getUserId(), order.getSymbol(), order.getSide(), order.getType()
        ));


        return OrderMapper.toResp(order);
    }

    private BigDecimal estimateMarketPrice(String symbol) {
        // ToDo : 連行情服務或從快取取價。範例先回 1。
        return new BigDecimal("1");
    }

    @Override
    @Transactional(readOnly = true)
    public OrderRespDTO getById(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .filter(o -> o.getUserId().equals(userId))
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));
        return OrderMapper.toResp(order);
    }

    public void cacheOrderStatus(Order order) {
        // 訂單狀態快取
        String key1 = "order:" + order.getId() + ":status";
        redis.opsForValue().set(key1, order.getStatus().name(), ORDER_STATUS_TTL);

        // 用戶訂單快取
        if (order.getClientOrderId() != null) {
            String key2 = "user:" + order.getUserId() + ":order:" + order.getClientOrderId();
            redis.opsForValue().set(key2, order.getId().toString(), ORDER_STATUS_TTL);
        }
    }


    //基本風控檢查
    public void validateBusinessRules(OrderPlaceReqDTO reqDTO) {
        if (reqDTO.getType() == OrderType.LIMIT && reqDTO.getPrice() == null) {
            throw new BizException(400, "Limit Order must specify a price");
        }

        if (reqDTO.getType() == OrderType.MARKET && reqDTO.getPrice() != null) {
            throw new BizException(400, "Market Order should not include price");
        }

        if (reqDTO.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BizException(400, "Order quantity must be positive");
        }
        //ToDo: 以後加上最小下單量、限價保護區間、黑名單等規則(待擴充)
    }

}
