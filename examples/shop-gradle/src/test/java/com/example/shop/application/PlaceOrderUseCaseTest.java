package com.example.shop.application;

import com.example.shop.domain.Money;
import com.example.shop.domain.Order;
import com.example.shop.domain.OrderLine;
import com.example.shop.infrastructure.InMemoryOrderRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PlaceOrderUseCaseTest {

    private final InMemoryOrderRepository orders = new InMemoryOrderRepository();
    private final PlaceOrderUseCase placeOrder = new PlaceOrderUseCase(orders);

    @Test
    void places_an_order_and_computes_its_total() {
        Order order = placeOrder.place("alice", List.of(new OrderLine("book", 2, Money.Companion.of("12.50"))));

        assertEquals(Money.Companion.of("25.00"), order.getTotal());
        assertEquals(order, orders.findById(order.getId()));
    }

    @Test
    void rejects_an_empty_order() {
        assertThrows(IllegalArgumentException.class, () -> placeOrder.place("alice", List.of()));
    }
}
