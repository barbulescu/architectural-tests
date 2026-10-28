package com.example.shop.architecture

import com.example.shop.architecture.rules.FrameworkFreeRules
import com.example.shop.architecture.rules.ImmutabilityRules
import com.lemonappdev.konsist.api.Konsist
import org.junit.jupiter.api.Test

/**
 * Rules about how the Kotlin code is written, which only a source-level tool can check. Dependencies between
 * packages, Java and Kotlin alike, are checked by the ArchUnit rules in ArchitectureTest.
 *
 * Existing offenders can be marked with @Suppress("konsist.<test method name>"), e.g.
 * @Suppress("konsist.domain objects are immutable"). Rename a test and its suppressions stop matching.
 */
class KotlinArchitectureTest {

    private val production = Konsist.scopeFromProduction()

    @Test
    fun `domain objects are immutable`() {
        ImmutabilityRules.assertImmutable(production, "com.example.shop.domain..")
    }

    @Test
    fun `domain does not know about frameworks`() {
        FrameworkFreeRules.assertNoImports(production, "com.example.shop.domain..")
    }
}
