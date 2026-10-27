package com.example.shop.web;

import com.example.shop.domain.Order;
import com.example.shop.infrastructure.InMemoryOrderRepository;

import java.util.List;

public class OrderReportController {

    private final InMemoryOrderRepository orders;

    public OrderReportController(InMemoryOrderRepository orders) {
        this.orders = orders;
    }

    public List<String> customersWithOrders() {
        return orders.findAll().stream().map(Order::getCustomer).distinct().toList();
    }
}
