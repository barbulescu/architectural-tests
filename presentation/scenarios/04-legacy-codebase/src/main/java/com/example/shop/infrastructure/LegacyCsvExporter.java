package com.example.shop.infrastructure;

import com.example.shop.application.PlaceOrderUseCase;
import com.example.shop.domain.Order;

import java.util.stream.Collectors;

/** Infrastructure reaching up into the application layer: a classic. */
public class LegacyCsvExporter {

    private final InMemoryOrderRepository orders = new InMemoryOrderRepository();
    private final PlaceOrderUseCase reimport = new PlaceOrderUseCase(orders);

    public String export() {
        return orders.findAll().stream().map(Order::getCustomer).collect(Collectors.joining("\n"));
    }

    public PlaceOrderUseCase reimporter() {
        return reimport;
    }
}
