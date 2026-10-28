package fixtures.domain

data class Money(val amount: Int)

class Cart(val items: List<String>) {
    val size: Int get() = items.size

    fun total(): Int {
        var sum = 0 // local variables are not state
        for (item in items) sum += item.length
        return sum
    }
}

object Defaults {
    val currency = "EUR"
}

val TOP_LEVEL = Money(0)
