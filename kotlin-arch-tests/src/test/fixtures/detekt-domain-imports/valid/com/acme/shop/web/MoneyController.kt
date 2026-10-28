package com.acme.shop.web

import com.acme.shop.domain.Money
import org.springframework.web.bind.annotation.RestController

@RestController
class MoneyController(val money: Money)
