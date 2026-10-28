package com.acme.domain

import com.acme.infrastructure.MoneyTable
import org.springframework.stereotype.Component

@Component
data class Money(val table: MoneyTable)
