package com.example.shop.application;

import com.example.shop.domain.Order;
import com.example.shop.domain.OrderLine;
import com.example.shop.domain.OrderRepository;
import com.example.shop.domain.OrderStatus;

import java.util.List;
import java.util.UUID;

public class PlaceOrderUseCase {

    private final OrderRepository orders;

    public PlaceOrderUseCase(OrderRepository orders) {
        this.orders = orders;
    }

    public Order place(String customer, List<OrderLine> lines) {
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("An order needs at least one line");
        }
        Order order = new Order(UUID.randomUUID().toString(), customer, lines, OrderStatus.PLACED);
        orders.save(order);
        return order;
    }
}
