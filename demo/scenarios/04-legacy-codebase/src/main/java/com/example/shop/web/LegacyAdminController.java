package com.example.shop.web;

import com.example.shop.domain.Order;
import com.example.shop.infrastructure.InMemoryOrderRepository;

import java.util.List;

/** Written in 2017, still in production, nobody wants to touch it. */
public class LegacyAdminController {

    private final InMemoryOrderRepository orders = new InMemoryOrderRepository();

    public List<Order> allOrders() {
        return orders.findAll();
    }
}
