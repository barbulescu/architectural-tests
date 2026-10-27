package com.example.shop.domain

import java.math.BigDecimal

data class Money(val amount: BigDecimal) {
    operator fun plus(other: Money) = Money(amount + other.amount)
    operator fun times(factor: Int) = Money(amount * factor.toBigDecimal())

    companion object {
        val ZERO = Money(BigDecimal.ZERO)
        fun of(amount: String) = Money(BigDecimal(amount))
    }
}
