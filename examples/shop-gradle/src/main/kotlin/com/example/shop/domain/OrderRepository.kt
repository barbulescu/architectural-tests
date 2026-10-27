package com.example.shop.domain

interface OrderRepository {
    fun save(order: Order)
    fun findById(id: String): Order?
    fun findAll(): List<Order>
}
