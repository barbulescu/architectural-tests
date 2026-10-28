package com.acme.shop.domain

import com.acme.shop.infrastructure.MoneyTable
import org.springframework.stereotype.Component

@Component
data class Money(val table: MoneyTable)
