package com.example.shop.web;

import com.example.shop.application.PlaceOrderUseCase;
import com.example.shop.domain.Money;
import com.example.shop.domain.Order;
import com.example.shop.domain.OrderLine;

import java.util.List;

public class OrderController {

    private final PlaceOrderUseCase placeOrder;

    public OrderController(PlaceOrderUseCase placeOrder) {
        this.placeOrder = placeOrder;
    }

    public String post(String customer, String product, int quantity, String unitPrice) {
        Order order = placeOrder.place(customer, List.of(new OrderLine(product, quantity, Money.Companion.of(unitPrice))));
        return order.getId();
    }
}
