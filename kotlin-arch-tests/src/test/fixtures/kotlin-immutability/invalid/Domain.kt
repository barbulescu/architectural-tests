package fixtures.domain

data class Discount(val code: String, var percent: Int)

class Counter {
    var count = 0
}

class Basket(val items: MutableList<String>)

class Bag {
    val items = mutableListOf<String>()
}

object Registry {
    var size = 0
}

var counter = 0
