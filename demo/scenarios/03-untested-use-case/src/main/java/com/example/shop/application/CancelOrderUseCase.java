package com.example.shop.application;

import com.example.shop.domain.Order;
import com.example.shop.domain.OrderRepository;

public class CancelOrderUseCase {

    private final OrderRepository orders;

    public CancelOrderUseCase(OrderRepository orders) {
        this.orders = orders;
    }

    public Order cancel(String orderId) {
        Order order = orders.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Unknown order " + orderId);
        }
        Order cancelled = order.cancel();
        orders.save(cancelled);
        return cancelled;
    }
}
