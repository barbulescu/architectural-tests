package com.example.archtests

import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertContains

class ImmutabilityRulesTest {

    @Test
    fun `val properties of immutable types pass`() {
        ImmutabilityRules.assertImmutable(Fixtures.scope("kotlin-immutability", "valid"), "fixtures.domain..")
    }

    @Test
    fun `var and mutable collections fail`() {
        val failure = assertThrows<KoAssertionFailedException> {
            ImmutabilityRules.assertImmutable(Fixtures.scope("kotlin-immutability", "invalid"), "fixtures.domain..")
        }
        val message = failure.message.orEmpty()
        assertContains(message, "Assert 'var and mutable collections fail' was violated (6 times)")
        for (name in listOf("percent", "count", "items", "size", "counter")) assertContains(message, "Property $name ")
    }

    @Test
    fun `existing offenders can be suppressed`() {
        ImmutabilityRules.assertImmutable(Fixtures.scope("kotlin-immutability", "suppressed"), "fixtures.domain..")
    }
}
