package com.example.shop.domain

data class Order(
    val id: String,
    val customer: String,
    val lines: List<OrderLine>,
    val status: OrderStatus = OrderStatus.PLACED,
) {
    val total: Money get() = lines.fold(Money.ZERO) { sum, line -> sum + line.subtotal }

    fun cancel(): Order {
        check(status == OrderStatus.PLACED) { "Only placed orders can be cancelled, order $id is $status" }
        return copy(status = OrderStatus.CANCELLED)
    }
}

data class OrderLine(val product: String, val quantity: Int, val unitPrice: Money) {
    init {
        require(quantity > 0) { "Quantity must be positive" }
    }

    val subtotal: Money get() = unitPrice * quantity
}

enum class OrderStatus { PLACED, CANCELLED }
