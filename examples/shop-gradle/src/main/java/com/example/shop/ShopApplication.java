package com.example.shop;

import com.example.shop.application.PlaceOrderUseCase;
import com.example.shop.infrastructure.InMemoryOrderRepository;
import com.example.shop.web.OrderController;

/** Composition root: the one place that is allowed to know every layer. */
public final class ShopApplication {

    private ShopApplication() {
    }

    public static OrderController wire() {
        return new OrderController(new PlaceOrderUseCase(new InMemoryOrderRepository()));
    }
}
