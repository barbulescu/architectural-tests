package com.acme.web

import com.acme.domain.Money
import org.springframework.web.bind.annotation.RestController

@RestController
class MoneyController(val money: Money)
