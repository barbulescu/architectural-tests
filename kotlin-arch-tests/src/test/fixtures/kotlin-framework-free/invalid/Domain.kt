package fixtures.domain

import jakarta.persistence.Entity
import java.math.BigDecimal
import org.springframework.stereotype.Component

@Entity
@Component
data class Money(val amount: BigDecimal)
