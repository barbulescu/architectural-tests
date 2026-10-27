package com.example.shop.architecture

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.api.ext.list.properties
import com.lemonappdev.konsist.api.ext.list.withPackage
import com.lemonappdev.konsist.api.verify.assertFalse
import org.junit.jupiter.api.Test

/**
 * The domain model is written in Kotlin. These rules are about how Kotlin is written, which only a
 * source-level tool can check. Everything else, Java and Kotlin alike, is covered by the ArchUnit tests.
 */
class DomainModelKonsistTest {

    private val domainFiles = Konsist.scopeFromProduction().files.withPackage("com.example.shop.domain..")

    @Test
    fun `domain objects are immutable`() {
        domainFiles.flatMap { it.classes() }.properties().assertFalse { it.hasVarModifier }
    }

    @Test
    fun `domain does not know about frameworks`() {
        domainFiles.assertFalse { file ->
            file.imports.any { it.name.startsWith("org.springframework") || it.name.startsWith("jakarta.") }
        }
    }
}
