package com.example.archtests

import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains
import kotlin.test.assertFalse

class FrameworkFreeRulesTest {

    @Test
    fun `plain JDK imports pass`() {
        FrameworkFreeRules.assertNoImports(Fixtures.scope("kotlin-framework-free", "valid"), "fixtures.domain..")
    }

    @Test
    fun `framework imports fail, outside the package they are fine`() {
        val failure = assertThrows<KoAssertionFailedException> {
            FrameworkFreeRules.assertNoImports(Fixtures.scope("kotlin-framework-free", "invalid"), "fixtures.domain..")
        }
        val message = failure.message.orEmpty()
        assertContains(message, "was violated (2 times)")
        assertContains(message, "jakarta.persistence.Entity")
        assertContains(message, "org.springframework.stereotype.Component")
        assertFalse("RestController" in message)
    }
}
